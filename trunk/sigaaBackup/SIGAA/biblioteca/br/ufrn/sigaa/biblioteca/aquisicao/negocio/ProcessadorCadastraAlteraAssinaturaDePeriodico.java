/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;
import java.util.Calendar;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;

/**
 *
 *    Classe que contém as regras de negócio para a criação ou alteração de uma assinatura.
 *
 * @author Jadson
 * @since 07/10/2009
 * @version 1.0 criação da classe
 *
 */
public class ProcessadorCadastraAlteraAssinaturaDePeriodico extends AbstractProcessador{
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastraAlteraAssinaturaDePeriodico movimento
		   = (MovimentoCadastraAlteraAssinaturaDePeriodico) mov;
		
		GenericDAO dao = null;
		
		// checa as permissões do usuário
		try{
			
			dao = getGenericDAO(movimento);
		
			if(! movimento.isEditando() && ( movimento.getAssinatura().isAssinaturaDeDoacao() || movimento.isGerarCodigoAssinatuaCompra()) ){
				Calendar c = Calendar.getInstance();
				
				Biblioteca b = dao.refresh(movimento.getAssinatura().getUnidadeDestino());
				
				// o usuário não escolheu a unidade de destino na assinatura, não tem como gerar o código de barras
				if(b == null){
					
					throw new NegocioException("Escolha a unidade de destino dos fascículos dessa assinatura");
				
				}else{
					int numeroGerador = b.getNumeroGeradorCodigoAssinatura();
					
					numeroGerador++;
					
					movimento.getAssinatura().setCodigo( ""+c.get(Calendar.YEAR)+b.getCodigoIdentificadorBiblioteca()+numeroGerador);
				
					b.incrementaCodigoGeradorBiblioteca();
					                                              
					dao.updateField(Biblioteca.class, b.getId(), "numeroGeradorCodigoAssinatura", b.getNumeroGeradorCodigoAssinatura());
				}
			}
		
			validate(movimento); // Só depois que gerar o código da assinatura, para não dar erro na validação.
			
		
			if(movimento.isEditando()){
				dao.update(movimento.getAssinatura());
				// Por algum motivo não estava atualizando esse campo quando atualizava a assinatura //
				dao.updateField(Assinatura.class, movimento.getAssinatura().getId(), "frequenciaPeriodicos.id", movimento.getAssinatura().getFrequenciaPeriodicos().getId());
			}else
				dao.create(movimento.getAssinatura());
			
		}finally{
			
			if(dao != null) dao.close();
			
		}
		return movimento.getAssinatura().getCodigo();
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

		MovimentoCadastraAlteraAssinaturaDePeriodico movimento
			= (MovimentoCadastraAlteraAssinaturaDePeriodico) mov;
		
		Assinatura  assinatura = movimento.getAssinatura();
		
		ListaMensagens lista = assinatura.validate(); // valida o preenchimento dos dados da assinatura
		checkValidation(lista);
		
		AssinaturaDao dao = null;
		
		try{
		
			dao = getDAO(AssinaturaDao.class, movimento);
			
			// Só aquisição pode criar assinaturas de compra
			if(assinatura.isAssinaturaDeCompra() && ! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				lista.addErro("Usuário: "+movimento.getUsuarioLogado().getNome()+" não tem permissão para criar uma assinatura com a modalidade de aquisição de compras ");
			}
			
			// Só pode criar assinatura para a biblioteca da permissão do usuário
			assinatura.setUnidadeDestino(  dao.refresh(assinatura.getUnidadeDestino()));
			
			// Senão é administrador geral, checa a unidade da criação da assinatura.
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				checkRole(assinatura.getUnidadeDestino().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
			}
			
			if(! movimento.isEditando() ){  // CRIAÇÃO DA ASSINATURA
				
				if(assinatura.getCodigo() != null){
					Long qtdAssinatura = dao.countAssinaturaPorCodigo(assinatura.getCodigo());
			
					if(qtdAssinatura > 0){
						lista.addErro("Já existe uma outra assinatura cadastrada no sistema com o mesmo código.");
					}
				}
			}
			
		}catch(SegurancaException se){
			lista.addErro("Usuário: "+movimento.getUsuarioLogado().getNome()+" não tem permissão para criar uma assinatura para a biblioteca: "+assinatura.getUnidadeDestino().getDescricao());
		}finally{
			if(dao != null ) dao.close();
		}
		
		checkValidation(lista);
		
	}

}
