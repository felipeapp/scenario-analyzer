/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 09/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.FasciculoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.MaterialInformacionalDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;

/**
 *     Registra a chegada de um suplemento do fascículo.
 *
 * @author jadson
 * @since 09/10/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorRegistraChegadaSuplemento extends AbstractProcessador{

	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		MovimentoRegistraChegadaSuplemento movi = (MovimentoRegistraChegadaSuplemento) mov;
		
		GenericDAO dao = null;
		
		try{
		
			dao = getGenericDAO(movi);
			
			Assinatura a = movi.getAssinatura();
			
			a = dao.refresh(a);
			
			Fasciculo f = movi.getFasciculoPrincipal();
			Fasciculo suplemento = movi.getSuplemento();
			
			
			int proximoNumeroGerador = suplemento.criaCodigoBarrasSuplemento(f);
			suplemento.setSuplemento(true); // Importante, lembrar de setar o suplemento com "true"
			
			
			validate(mov);    // Tem que ser chamado depois de setado o código de barras
			
			
			a.addFasciculo(suplemento);
			suplemento.setAssinatura(a);
			suplemento.setFasciculoDeQuemSouSuplemento(f);
			
			f.setNumeroGeradorCodigoBarrasAnexos(proximoNumeroGerador); // IMPORTANTE ATUALIZA O NÚMERO GERADOR
			
			dao.create(suplemento);
			dao.update(f);
			//dao.update(a);  // não precisa atualizar a assinatura, já está atualizando quando salva o fascículo
			
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	
	
	
	/**
	 * Ver comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoRegistraChegadaSuplemento movi = (MovimentoRegistraChegadaSuplemento) mov;
		
		MaterialInformacionalDao dao = getDAO(MaterialInformacionalDao.class, movi);
		FasciculoDao daoFasciculo = getDAO(FasciculoDao.class, movi);
		
		Assinatura a = movi.getAssinatura();
		Fasciculo suplemento = movi.getSuplemento();
		
		try{
		
			if(a.isAssinaturaDeCompra()){
				if(! movi.getUsuarioLogado().isUserInRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
						, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO)){
					throw new NegocioException(" Usuário não tem permissão para registra a chegada de um fascículo de compra ");
				}
			}
						
			if(dao.countMateriaisByCodigosBarras(suplemento.getCodigoBarras()) > 0){
				throw new NegocioException(" Já existe um outro material com o mesmo código de barras no sistema: "
						+suplemento.getCodigoBarras()+", por isso o suplemento não pôde ser incluído. ");
			}
				
			Long qtdFasciculo = daoFasciculo.countFasciculoIguaisAssinaturaSuplemento(a.getId(),
					suplemento.getAnoCronologico(), suplemento.getAno(), suplemento.getVolume(), suplemento.getNumero(), suplemento.getEdicao(), suplemento.getDiaMes(), movi.getFasciculoPrincipal().getId());
					
			if(qtdFasciculo > 0 ){
				throw new NegocioException(" Já existe um fascículo com os mesmos ano cronológico, ano, dia/mês, volume, número e edição do suplemento para essa assinatura.");
			}
		
		}finally{
			dao.close();
			daoFasciculo.close();
		}
		
	}

}
