/*
 * ProcessadorSalvaEntidadesImportadas.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;

/**
 *
 * Processador Criado para salvar uma coleção de título ou autoridades que são importadas no sistema.
 *
 * @author jadson
 * @since 31/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class ProcessadorSalvaEntidadesImportadas extends AbstractProcessador{

	
	/**
	 * Ver o comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		MovimentoSalvaEntidadesImportadas movimento = (MovimentoSalvaEntidadesImportadas) mov;
		
		
		GenericDAO dao = null;
		
		
		try{
		
			dao = getGenericDAO(movimento);
			
			if(movimento.isSalvarTitulos()){
				
				for (TituloCatalografico titulo :  movimento.getListaTitulosImportados()) {
					
					titulo.setCatalogado(false);
					
					titulo.setNumeroDoSistema(   ((GenericDAOImpl) dao).getNextSeq("biblioteca", "numero_do_sistema_titulo_sequence") );
					
					CatalogacaoUtil.configuraPosicaoCamposMarc(titulo);
					
					ClassificacoesBibliograficasUtil.removeClassificacoesNaoInformadas(titulo, movimento.getClassificacoesUtilizadas());
					
					dao.create(titulo);
					
					// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
					CatalogacaoUtil.sincronizaTituloCatalograficoCache(dao, titulo, false, movimento.getClassificacoesUtilizadas());
					
					
				}
				
			}else{
				for (Autoridade autoridade : movimento.getListaAutoriadesImportadas()) {
					
					autoridade.setCatalogada(false);
					
					autoridade.setNumeroDoSistema(  ((GenericDAOImpl) dao).getNextSeq("biblioteca", "numero_do_sistema_autoridade_sequence") ); // importante não esquecer
					
					CatalogacaoUtil.configuraPosicaoCamposMarc(autoridade);
					
					
					dao.create(autoridade);
					
					// REALIZA A SINCRONIZACAO DAS BUSCAS DE TITULOS
					CatalogacaoUtil.sincronizaAutoridadeCache(dao, autoridade, false);
					
				}
			}
		
		}finally{
			if( dao != null) dao.close();
		}
		
		
		return null;
	}

	/**
	 * Ver o comentário na classe pai.
	 * 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Não tem
	}

}
