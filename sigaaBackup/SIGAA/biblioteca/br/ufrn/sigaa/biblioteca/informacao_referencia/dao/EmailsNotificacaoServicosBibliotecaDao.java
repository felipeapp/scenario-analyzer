/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 18/12/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.EmailsNotificacaoServicosBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * <p> Daos exclusivos para EmailsNotificacaoServicosBiblioteca </p>
 *
 * 
 * @author jadson
 *
 */
public class EmailsNotificacaoServicosBibliotecaDao extends GenericSigaaDAO {

	/**
	 * Retorna todos os EmailsNotificacaoServicosBiblioteca cadastrados
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<EmailsNotificacaoServicosBiblioteca> findAllEmailsNotificacaoServicosBiblioteca() throws  DAOException{
		
		String projecao = " id, tipoServico, emailsNotificacao, biblioteca.id, biblioteca.identificador, biblioteca.descricao, biblioteca.unidade.id ";
		
		StringBuilder hql =  new StringBuilder(" SELECT "+projecao+" FROM ");
		hql.append(" EmailsNotificacaoServicosBiblioteca e ");
		hql.append(" ORDER BY biblioteca.descricao ASC, tipoServico ");
		
		Query q = getSession().createQuery(hql.toString());
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		List<EmailsNotificacaoServicosBiblioteca> retorno = new ArrayList<EmailsNotificacaoServicosBiblioteca>();
		
		for (Object[] dado : dados) {
			EmailsNotificacaoServicosBiblioteca e = new EmailsNotificacaoServicosBiblioteca();
			
			e.setId( (Integer) dado[0] );
			e.setTipoServico( (TipoServicoInformacaoReferencia) dado[1] );
			e.setEmailsNotificacao( (String) dado[2] );
			e.setBiblioteca( new Biblioteca((Integer) dado[3] , (String) dado[4] , (String) dado[5] , new Unidade((Integer) dado[6])));
			
			retorno.add(e);
		}
		
		return retorno; 
		
	}
	
	
	/**
	 * Retorna todos os EmailsNotificacaoServicosBiblioteca cadastrados
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public List<EmailsNotificacaoServicosBiblioteca> findEmailsNotificacaoServicosBibliotecaByBiblioteca(int idBiblioteca) throws  DAOException{
		
		String projecao = " id, tipoServico, emailsNotificacao, biblioteca.id, biblioteca.identificador, biblioteca.descricao, biblioteca.unidade.id ";
		
		StringBuilder hql =  new StringBuilder(" SELECT "+projecao+" FROM ");
		hql.append(" EmailsNotificacaoServicosBiblioteca e ");
		hql.append(" WHERE e.biblioteca.id = :idBiblioteca ");
		hql.append(" ORDER BY biblioteca.descricao ASC, tipoServico ");
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idBiblioteca", idBiblioteca);
		
		@SuppressWarnings("unchecked")
		List<Object[]> dados = q.list();
		
		List<EmailsNotificacaoServicosBiblioteca> retorno = new ArrayList<EmailsNotificacaoServicosBiblioteca>();
		
		for (Object[] dado : dados) {
			EmailsNotificacaoServicosBiblioteca e = new EmailsNotificacaoServicosBiblioteca();
			
			e.setId( (Integer) dado[0] );
			e.setTipoServico( (TipoServicoInformacaoReferencia) dado[1] );
			e.setEmailsNotificacao( (String) dado[2] );
			e.setBiblioteca( new Biblioteca((Integer) dado[3] , (String) dado[4] , (String) dado[5] , new Unidade((Integer) dado[6])));
			
			retorno.add(e);
		}
		
		return retorno; 
		
	}
	
	
	
	/**
	 * Retorna todos os EmailsNotificacaoServicosBiblioteca cadastrados
	 *
	 * @param tipoServico
	 * @param idPessoa
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	public String[] findEmailsNotificacaoServicosBibliotecaByTipoServicoBiblioteca(TipoServicoInformacaoReferencia tipoServico, int idBiblioteca) throws  DAOException{
		
		StringBuilder hql =  new StringBuilder(" SELECT emailsNotificacao FROM ");
		hql.append(" EmailsNotificacaoServicosBiblioteca e ");
		hql.append(" WHERE e.biblioteca.id = :idBiblioteca " +
				   " AND tipoServico = "+tipoServico);
		
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idBiblioteca", idBiblioteca);
		
	
		String emails = (String) q.uniqueResult();
		if( StringUtils.notEmpty(emails) )
			return emails.split(";");
		else{
			
			// SE não tiver configurado retorna o email da biblioteca 
			
			StringBuilder hql2 =  new StringBuilder(" SELECT email FROM ");
			hql2.append(" Biblioteca b ");
			hql2.append(" WHERE id = :idBiblioteca ");
			Query q2 = getSession().createQuery(hql2.toString());
			q2.setInteger("idBiblioteca", idBiblioteca);
			return new String[]{ (String) q2.uniqueResult()};
		}
		
	}
	
}
