/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/12/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.espacofisico;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.espacofisico.dominio.EspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.GestorEspacoFisico;
import br.ufrn.sigaa.espacofisico.dominio.TipoGestorEspacoFisico;
import br.ufrn.sigaa.espacofisico.jsf.GestorEspacoFisicoMBean.Filtros;
import br.ufrn.sigaa.espacofisico.jsf.GestorEspacoFisicoMBean.ValoresBusca;

/**
 * DAO responsável por consultas referentes a gestores de espaços físicos
 * 
 * @author wendell
 * @author Henrique André
 *
 */
public class GestorEspacoFisicoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna as permissões de gestão de espaços físicos que o usuário possui
	 * 
	 * @param idUsario
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<GestorEspacoFisico> findByUsuario(int idUsuario) throws DAOException {
		String hql = "from GestorEspacoFisico where usuario.id = :idUsuario and ativo = trueValue()";
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuario", idUsuario);
		return q.list();
	}
	
	/**
	 * Verifica se um usuário possui permissão de gestor de espaço físico, de acordo com o tipo
	 * de gestão especificada
	 * 
	 * @param usuario
	 * @param tipoGestor
	 * @return
	 */
	public boolean isGestorEspacoFisico(Usuario usuario, TipoGestorEspacoFisico tipoGestor) {
		return (Long) getHibernateTemplate()
			.uniqueResult("select count(id) from GestorEspacoFisico " +
				" where usuario.id = ?" +
				" and ativo = trueValue() " +
				" and tipo = " + tipoGestor.getId(), 
				usuario.getId()) > 0;
	}
	
	/**
	 * Localizar todas as permissões de um usuário e unidade
	 * 
	 * @param usuario
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<GestorEspacoFisico> findAtivosByUsuarioUnidade(Usuario usuario, Unidade unidade) throws DAOException {
		
		Criteria c = getSession().createCriteria(GestorEspacoFisico.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		
		
		if ( !isEmpty(usuario) )
			c.add(Restrictions.eq("usuario.id", usuario.getId()));
		
		if ( !isEmpty(unidade) )
			c.add(Restrictions.eq("unidade.id", unidade.getId()));
		
		return c.list();
	}

	/**
	 * Localizar todas as permissões de um usuário e unidade
	 * 
	 * @param usuario
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<GestorEspacoFisico> findAtivosByUsuarioUnidade(Usuario usuario, Integer... ids) throws DAOException {
		
		Criteria c = getSession().createCriteria(GestorEspacoFisico.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		
		c.add(Restrictions.eq("usuario.id", usuario.getId()));
		
		Criteria espacoFisico = c.createCriteria("espacoFisico");
		
		espacoFisico.add(Restrictions.in("unidadeResponsavel.id", ids));
		
		return c.list();
	}	
	
	/**
	 * Localizar todas as permissões de um usuário e unidade
	 * 
	 * @param usuario
	 * @param unidade
	 * @return
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	public List<GestorEspacoFisico> findAtivosByUsuarioEspacoFisico(Usuario usuario, EspacoFisico espacoFisico) throws DAOException {

		Criteria c = getSession().createCriteria(GestorEspacoFisico.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		
		
		if ( !isEmpty(usuario) )
			c.add(Restrictions.eq("usuario.id", usuario.getId()));
		
		if ( !isEmpty(espacoFisico) )
			c.add(Restrictions.eq("espacoFisico.id", espacoFisico.getId()));
		
		return c.list();
	}

	/**
	 * Localiza gestores de acordo com os parâmetros fornecidos no formulário
	 * 
	 * @param filtros
	 * @param valores
	 * @return
	 * @throws DAOException
	 */
	public List<GestorEspacoFisico> findAtivosByUsuarioEspacoFisico(Filtros filtros, ValoresBusca valores) throws DAOException {
		
		Criteria c = getSession().createCriteria(GestorEspacoFisico.class);
		c.add(Restrictions.eq("ativo", Boolean.TRUE));
		
		
		if ( filtros.isUsuario() )
			c.add(Restrictions.eq("usuario.id", valores.getUsuario().getId()));
		
		if ( filtros.isEspacoFisico() )
			c.add(Restrictions.eq("espacoFisico.id", valores.getEspacoFisico().getId()));

		if ( filtros.isUnidade() )
			c.add(Restrictions.eq("unidade.id", valores.getUnidade().getId()));
		
		return c.list();

	}
}
