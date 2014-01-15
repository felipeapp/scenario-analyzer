/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 17/12/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.EmailsNotificacaoServicosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;

/**
 * <p> Classe utilit�ria para a parte de emais de notifica��o da biblioteca. </p>
 * 
 * @author jadson - jadson@info.ufrn.br
 */
public class EmailsNotificacaoServicosBibliotecaUtil {

	
	/***
	 * Retorna os emails de notifica��o para os quais as notifica��es devem ser enviadas nos servi�os de informa��o e refer�ncia.
	 * 
	 * @param tipoServico
	 * @param idBiblioteca
	 * @return
	 * @throws DAOException
	 */
	public static List<String> getEmailsNotificacao(TipoServicoInformacaoReferencia tipoServico, int idBiblioteca){

		List<String> emails = new ArrayList<String>();
		
		EmailsNotificacaoServicosBibliotecaDao dao = null;
		
		try{
			dao = DAOFactory.getInstance().getDAO(EmailsNotificacaoServicosBibliotecaDao.class);	
			
			emails = Arrays.asList(dao.findEmailsNotificacaoServicosBibliotecaByTipoServicoBiblioteca
					(tipoServico, idBiblioteca) );
		} catch (DAOException e) {
			return emails;
		}finally{
			if(dao != null) dao.close();
			
		}
		
		return emails;
	} 
}
