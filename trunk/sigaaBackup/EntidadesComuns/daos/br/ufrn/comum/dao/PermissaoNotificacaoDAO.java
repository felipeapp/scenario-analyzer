/**
 * 
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Query;

import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;
import br.ufrn.comum.dominio.notificacoes.UsuarioGrupoDestinatarios;

/**
 * DAO para consultas relacionadas �s permiss�es
 * para o envio de notifica��es
 * 
 * @author Ricardo Wendell
 *
 */
public class PermissaoNotificacaoDAO extends GenericSharedDBDao {

	/**
	 * Buscar os papeis permitidos para notifica��o pelo usu�rio
	 * 
	 * @param usuario
	 * @param sistema
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Papel> findPapeisPermitidosByUsuario(UsuarioGeral usuario, Sistema sistema) throws DAOException {
		String hql = "FROM Papel" +
			" WHERE 1 = 1 " +
			(sistema != null ? " AND sistema.id = "+ sistema.getId() : "") +
			" order by sistema.nome, subSistema.nome, descricao";
		return getSession().createQuery(hql).list();
	}
	
	/**
	 * Retorna os grupos de destinat�rios para os quais o usu�rio informado tem permiss�o
	 * de enviar notifica��es.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<GrupoDestinatarios> findGruposPermitidosByUsuario(UsuarioGeral usuario) throws DAOException {
		Set<Papel> papeis = new HashSet<Papel>();
		papeis.addAll(usuario.getPapeis());
		papeis.addAll(usuario.getPapeisTemporarios());
		
		StringBuffer hql = new StringBuffer(); 
		hql.append(" SELECT distinct g.grupoDestinatarios FROM UsuarioGrupoDestinatarios g");
		hql.append(" WHERE g.ativo = trueValue() ");
		hql.append(" AND ((g.usuario is not null and g.usuario = :usuario) ");
		
		if (!isEmpty(papeis)) {
			hql.append("or (g.usuario is null and g.papel.id in " + UFRNUtils.gerarStringIn(papeis) + ") ");
		}
		
		hql.append(")");
		
		Query q = getSession().createQuery(hql.toString());
		q.setEntity("usuario", usuario);
		return q.list();
	}
	
	/**
	 * Verifica se o usu�rio informado possui alguma permiss�o de envio de notifica��o ativa.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public boolean hasPermissaoEnvioNotificacao(UsuarioGeral usuario) throws DAOException {
		return !ValidatorUtil.isEmpty(findPermissoesEnvioNotificacao(usuario)); 
	}

	/**
	 * Lista as permiss�es de envio de notifica��es para o usu�rio.
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGrupoDestinatarios> findPermissoesEnvioNotificacao(UsuarioGeral usuario)throws DAOException {
		StringBuffer hql = new StringBuffer(); 
		hql.append(" FROM UsuarioGrupoDestinatarios ");
		hql.append(" WHERE ativo = trueValue() ");
		hql.append(" AND usuario.id = " + usuario.getId());
		Query q = getSession().createQuery(hql.toString());
		return q.list();
	}
	
	/**
	 * Busca todas as permiss�es de envio de notifica��o
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGrupoDestinatarios> findAllPermissoesNotificacao() throws DAOException {
		String hql = "select ugd FROM UsuarioGrupoDestinatarios ugd left join fetch ugd.usuario u left join fetch u.pessoa p left join fetch ugd.papel pa " +
			" where ugd.ativo = trueValue() " +
			" order by p.nome, pa.nome";
		return getSession().createQuery(hql).list();
	}
	
}
