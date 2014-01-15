/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
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
 *    Classe que cont�m as regras de neg�cio para a cria��o ou altera��o de uma assinatura.
 *
 * @author Jadson
 * @since 07/10/2009
 * @version 1.0 cria��o da classe
 *
 */
public class ProcessadorCadastraAlteraAssinaturaDePeriodico extends AbstractProcessador{
	
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoCadastraAlteraAssinaturaDePeriodico movimento
		   = (MovimentoCadastraAlteraAssinaturaDePeriodico) mov;
		
		GenericDAO dao = null;
		
		// checa as permiss�es do usu�rio
		try{
			
			dao = getGenericDAO(movimento);
		
			if(! movimento.isEditando() && ( movimento.getAssinatura().isAssinaturaDeDoacao() || movimento.isGerarCodigoAssinatuaCompra()) ){
				Calendar c = Calendar.getInstance();
				
				Biblioteca b = dao.refresh(movimento.getAssinatura().getUnidadeDestino());
				
				// o usu�rio n�o escolheu a unidade de destino na assinatura, n�o tem como gerar o c�digo de barras
				if(b == null){
					
					throw new NegocioException("Escolha a unidade de destino dos fasc�culos dessa assinatura");
				
				}else{
					int numeroGerador = b.getNumeroGeradorCodigoAssinatura();
					
					numeroGerador++;
					
					movimento.getAssinatura().setCodigo( ""+c.get(Calendar.YEAR)+b.getCodigoIdentificadorBiblioteca()+numeroGerador);
				
					b.incrementaCodigoGeradorBiblioteca();
					                                              
					dao.updateField(Biblioteca.class, b.getId(), "numeroGeradorCodigoAssinatura", b.getNumeroGeradorCodigoAssinatura());
				}
			}
		
			validate(movimento); // S� depois que gerar o c�digo da assinatura, para n�o dar erro na valida��o.
			
		
			if(movimento.isEditando()){
				dao.update(movimento.getAssinatura());
				// Por algum motivo n�o estava atualizando esse campo quando atualizava a assinatura //
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
			
			// S� aquisi��o pode criar assinaturas de compra
			if(assinatura.isAssinaturaDeCompra() && ! movimento.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				lista.addErro("Usu�rio: "+movimento.getUsuarioLogado().getNome()+" n�o tem permiss�o para criar uma assinatura com a modalidade de aquisi��o de compras ");
			}
			
			// S� pode criar assinatura para a biblioteca da permiss�o do usu�rio
			assinatura.setUnidadeDestino(  dao.refresh(assinatura.getUnidadeDestino()));
			
			// Sen�o � administrador geral, checa a unidade da cria��o da assinatura.
			if(! mov.getUsuarioLogado().isUserInRole( SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL)){
				checkRole(assinatura.getUnidadeDestino().getUnidade(), movimento, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO);
			}
			
			if(! movimento.isEditando() ){  // CRIA��O DA ASSINATURA
				
				if(assinatura.getCodigo() != null){
					Long qtdAssinatura = dao.countAssinaturaPorCodigo(assinatura.getCodigo());
			
					if(qtdAssinatura > 0){
						lista.addErro("J� existe uma outra assinatura cadastrada no sistema com o mesmo c�digo.");
					}
				}
			}
			
		}catch(SegurancaException se){
			lista.addErro("Usu�rio: "+movimento.getUsuarioLogado().getNome()+" n�o tem permiss�o para criar uma assinatura para a biblioteca: "+assinatura.getUnidadeDestino().getDescricao());
		}finally{
			if(dao != null ) dao.close();
		}
		
		checkValidation(lista);
		
	}

}
