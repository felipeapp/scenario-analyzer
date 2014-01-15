/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> Classe utilitária para a parte de emais de notificação da biblioteca. </p>
 * 
 * @author jadson - jadson@info.ufrn.br
 */
public class EmailsNotificacaoServicosBibliotecaUtil {

	
	/***
	 * Retorna os emails de notificação para os quais as notificações devem ser enviadas nos serviços de informação e referência.
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
