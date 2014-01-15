/*
 * Universidade Federal do Rio Grande do Norte

 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 21/05/2010
 */
package br.ufrn.sigaa.ava.cv.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.notificacoes.GrupoDestinatarios;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.cv.dominio.ComunidadeVirtual;
import br.ufrn.sigaa.cv.dominio.MembroComunidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pelo pelas operações de MembroComunidade 
 *  
 * @author agostinho campos
 */
public class MembroComunidadeDao extends GenericSigaaDAO {

	/**
	 * Registra quando um convite é enviado ao usuário
	 * 
	 * @param data
	 * @param idUsuario
	 * @param idComunidade
	 * @param idPessoa 
	 * @param permissaoUsuario 
	 * @param hashMD5
	 * @return
	 */
	public boolean registrarConviteEnviado(Date data, int idUsuario, int idComunidade, int idPessoa, int permissaoMembro, String hashMD5) {
		String sql = "INSERT INTO cv.convites_enviados (id_convite, data_hora, id_usuario, id_comunidade, id_pessoa, permissao_membro, ativo, hash_md5)" +
					 " VALUES ((select nextval('hibernate_sequence')), ?, ?, ?, ?, ?, ?, ?)";
		
		try { 
			update(sql, data, idUsuario, idComunidade, idPessoa, permissaoMembro, true, hashMD5);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Localiza o membro da comunidade de acordo com o hash passado 
	 * 
	 * @param hashMD5
	 * @return
	 */
	public MembroComunidade findConviteByHashAutenticacao(String hashMD5) {
	
		String sql = " select id_usuario, id_comunidade, cv.id_pessoa as id_pessoa, permissao_membro, p.nome as nome, p.cpf_cnpj as cpf " +
					 " from cv.convites_enviados cv " +
					 " inner join comum.pessoa p on p.id_pessoa = cv.id_pessoa " +
					 " where ativo = trueValue() and hash_md5 = '" + hashMD5 + "'";
		
		try {
				return (MembroComunidade) getJdbcTemplate().queryForObject(sql, new RowMapper() {
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					MembroComunidade mb = new MembroComunidade();
					mb.setComunidade(new ComunidadeVirtual(rs.getInt("id_comunidade")));
					mb.setPermissao(rs.getInt("permissao_membro"));
					mb.setPessoa(new Pessoa(rs.getInt("id_pessoa"), rs.getString("nome"), rs.getLong("cpf") ));
					mb.setUsuario(new Usuario(rs.getInt("id_usuario")));
					return mb;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Busca de usuários na Comunidade Virtual. Busca as pessoas de acordo com o nome e tipo.
	 * 
	 * @param nomePessoa
	 * @param tipo
	 * @return
	 */
	public List<Usuario> findUsuarios(String nomePessoa, char tipo) {

		StringBuilder sb = new StringBuilder();

		sb.append(
				" select u.id_usuario, u.login, u.email, u.id_foto, p.id_pessoa as id_pessoa, p.nome as nome_pessoa " +
				" from comum.usuario u " +
				" inner join comum.pessoa p on u.id_pessoa = p.id_pessoa " +
				" where " + UFRNUtils.toAsciiUpperUTF8("p.nome") + " like " + UFRNUtils.toAsciiUTF8("'" + nomePessoa.toUpperCase() + "%'") + 
				" and u.inativo=falseValue() and p.tipo = '" + tipo + "'");
	
		@SuppressWarnings("unchecked")
		List<Usuario> lista = getJdbcTemplate().query(sb.toString(), new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {

				Usuario usuario = new Usuario();
				Pessoa pessoa = new Pessoa( rs.getInt("id_pessoa"), rs.getString("nome_pessoa") );
				usuario.setId(rs.getInt("id_usuario"));
				usuario.setLogin(rs.getString("login"));
				usuario.setEmail(rs.getString("email"));
				usuario.setIdFoto(rs.getInt("id_foto"));
				usuario.setPessoa(pessoa);
				
				return usuario;
			}
		});
		return lista;
	}
	
	/**
	 * Retorna lista de Participantes da Comunidade Virtual
	 * 
	 * @param idPessoa
	 * @param idComunidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean findPartipanteComunidade(int idPessoa, int idComunidade) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append(" select m from MembroComunidade m " +
				  " where m.pessoa.id = :idPessoa and m.comunidade.id = :idComunidade and m.ativo = trueValue()");
		
		Query q = getSession().createQuery(sb.toString());
			  q.setParameter("idPessoa", idPessoa);
			  q.setParameter("idComunidade", idComunidade);
		
		if (q.list().size() >= 1)
			return true;
		else
			return false;
	}
	
	/**
	 * Retorna verdadeiro se o usuário for administrador da comunidade
	 * 
	 * @param idPessoa
	 * @param idComunidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean findAdministradorComunidade(int idPessoa, int idComunidade) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append(" SELECT m FROM MembroComunidade m WHERE m.pessoa.id = :idPessoa AND m.comunidade.id = :idComunidade AND m.permissao = :permissao and m.ativo = trueValue()");
		
		Query q = getSession().createQuery(sb.toString());
			  q.setParameter("idPessoa", idPessoa);
			  q.setParameter("idComunidade", idComunidade);
			  q.setParameter("permissao", MembroComunidade.ADMINISTRADOR);
		
		if (q.list().size() >= 1)
			return true;
		else
			return false;
	}
	
	
	
	/**
	 * Retorna true caso o usuário já tenha realizado solicitações de participação na comunidade
	 * 
	 * @param idUsuario
	 * @param idComunidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean findSolicitacoesByUsuario(int idUsuario, int idComunidade) throws HibernateException, DAOException {
		StringBuilder sb = new StringBuilder();
		sb.append(" select s from SolicitacaoParticipacaoComunidade s" +
				  " inner join s.comunidadeVirtual c " +
				  " inner join s.usuario u " +
				  " where u.id = :idUsuario and s.comunidadeVirtual.id = :idComunidade and s.pendenteDecisao = trueValue()");
		
		Query q = getSession().createQuery(sb.toString());
			  q.setParameter("idUsuario", idUsuario);
			  q.setParameter("idComunidade", idComunidade);
		
		if (q.list().size() >= 1)
			return true;
		else
			return false;
	}
	
	/**
	 * Retorna a SQL cadastrada em GrupoDestinatarios do banco COMUM de acordo com o idGrupo  
	 * 
	 * @param idGrupo
	 * @return
	 */
	public String findSQLByGrupoDestinatarios(int idGrupo) {
		String hql = "select g.sqlDestinatarios FROM GrupoDestinatarios g where id = ?";
		return (String) getHibernateTemplate(Sistema.COMUM).find(hql, idGrupo).get(0);
	}
	
	/**
	 * Executa a SQL passada no banco SIGAA. Localiza pessoas em determinado Grupo. 
	 * 
	 * @param sql
	 * @return
	 */
	public List<Usuario> findUsuariosFromGrupoDestinatarios(String sql) {
		@SuppressWarnings("unchecked")
		List<Usuario> lista = getJdbcTemplate(getDataSource(Sistema.SIGAA)).query(sql, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				Pessoa pessoa = new Pessoa();
				pessoa.setId(rs.getInt("id_pessoa"));
				
				Usuario u = new Usuario();
				u.setPessoa(pessoa);
				u.setId(rs.getInt("id_usuario"));
				
				return u;
			}
		});
		
		return lista; 
	}
	
	/**
	 * Busca todos os grupos de destinatários
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<GrupoDestinatarios> findAllGrupoDestinatarios() throws DAOException {
		String hql = "select g FROM GrupoDestinatarios g left join g.subSistema ss " +
					" where g.ativo = trueValue() and g.participaComunidadeVirtual = trueValue() " +
					" order by g.sistema.nome, ss.nome, g.descricao";
		
		@SuppressWarnings("unchecked")
		List<GrupoDestinatarios> lista = getHibernateTemplate(Sistema.COMUM).find(hql);
		return lista;
	}
}
