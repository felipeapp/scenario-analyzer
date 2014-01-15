/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/09/2006
 *
 */
package br.ufrn.sigaa.arq.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static org.hibernate.criterion.Restrictions.eq;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.HibernateTemplate;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Papel;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.sigaa.arq.dominio.ConstantesParametro;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Usuário do SIGAA, dados específicos do usuário no sistema acadêmico.
 * 
 * @author Andre M Dantas
 */
public class UsuarioDao extends GenericSigaaDAO {

	/** Usuários em cache. Otimização da caixa postal */
	private static Hashtable<Integer, UsuarioGeral> usuarios = new Hashtable<Integer,UsuarioGeral>();


	public UsuarioDao() {
	}

	/**
	 * Realiza uma busca pelo login passado como parâmetro. 
	 * 
	 * @param login
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByLogin(String login) throws DAOException {
		return findByLogin(login, false, true);
	}

	/** Busca usuário em cache, otimização da caixa postal */
	public  UsuarioGeral findUsuarioLeve(int id) throws DAOException {
        UsuarioGeral user = usuarios.get(id);
        if ( user == null ) {
	        Usuario userBD = findByPrimaryKey(id);
	        if (userBD != null) {
		        usuarios.put(id, userBD);
		        user = userBD;
	        }
        }
        return user;

	}

	
	/**
	 * Localiza o usuário do chefe de departamento
	 * 
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public List<UsuarioGeral> findUsuarioChefeDepartamento(int idUnidade) throws DAOException {
		return findUsuarioChefeByUnidade(idUnidade, AtividadeServidor.CHEFE_DEPARTAMENTO);
	}
	
	/**
	 * Localiza o usuário que possui designação na unidade especificada
	 * 
	 * @param idUnidade
	 * @param atividades
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuarioChefeByUnidade(int idUnidade, List<Integer> atividades) throws DAOException {
		
		String sql = "	select 	usu.id_usuario, usu.login, usu.email " +
				"		from 	rh.designacao d, rh.atividade atv, comum.usuario usu, comum.unidade uni " +
				"		where	usu.inativo = falseValue() and " +
				"				usu.autorizado = trueValue() and " +
				"				usu.email is not null and " +
				"				d.id_servidor = usu.id_servidor and " +
				"				d.gerencia = 'T' and " +
				"				d.id_atividade = atv.id_atividade and " +
				"				atv.codigo_rh in " + gerarStringIn(atividades) + " and " +
				"				(d.fim is null or d.fim >  ?) and " +
				"				uni.id_unidade = d.id_unidade and " +
				"				d.id_unidade = ? " +
				"		order by d.inicio";
		
		return getJdbcTemplate().query(sql, new Object[] { new Date(), idUnidade }, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				UsuarioGeral usu = new UsuarioGeral();
				usu.setId(rs.getInt("id_usuario"));
				usu.setLogin(rs.getString("login"));
				usu.setEmail(rs.getString("email"));
				
				return usu;
			}
			
		});
		
	}

	/**
	 * Retorna o responsável atual da unidade especificada que possua o nível de responsabilidade informado.
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuarioResponsavelAtualByUnidade(int idUnidade, char nivelResponsabilidade) throws DAOException {

				String sql = "	select 	usu.login, usu.id_usuario, usu.email " +
						"		from 	comum.responsavel_unidade ru, comum.unidade u, rh.servidor s, comum.usuario usu " +
						"		where 	usu.inativo = falseValue() and " +
						"				usu.autorizado = trueValue() and " +
						"				usu.email is not null and " +
						"				usu.id_servidor =  s.id_servidor and " +
						"				s.id_servidor = ru.id_servidor and " +
						"				ru.id_unidade = ? and " +
						"				u.id_unidade = ru.id_unidade and " +
						"				ru.nivel_responsabilidade = ? and " +
						"				ru.data_inicio <= current_date and " +
						"				(ru.data_fim is null or ru.data_fim >= current_date) " +
						"		order by ru.data_cadastro desc";


		Object[] lista = {idUnidade, String.valueOf(nivelResponsabilidade), };

		try{
			return getJdbcTemplate(Sistema.SIPAC).query(sql, lista, new RowMapper() {
				public Object mapRow(ResultSet rs, int row) throws SQLException {
					UsuarioGeral usu = new UsuarioGeral();
					usu.setId(rs.getInt("id_usuario"));
					usu.setLogin(rs.getString("login"));
					usu.setEmail(rs.getString("email"));
					return usu;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}	
	
	/**
	 * Esse método tem a função de retornar as principais informações do usuário, ele foi 
	 * implementado para ser mais rápido do que o método generic.
	 * 
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByPrimaryKey(int idUsuario) throws DAOException{

		try {

			String hql =  "select u.id, u.login, u.ramal, "
						+ "u.pessoa.id, u.pessoa.nome,  "
						+ "u.unidade.id, u.unidade.codigo, u.unidade.nome, u.unidade.sigla "
						+ "from Usuario u where u.id = :idUsuario";
			Query q = getSession().createQuery(hql);
			q.setInteger("idUsuario", idUsuario);

			Iterator<?> it = q.iterate();
			Usuario usuario = null;

			if (it.hasNext()) {
				usuario = new Usuario();
				Object[] obj = (Object[]) it.next();
				int i = 0;
				usuario.setId((Integer)obj[i++]);
				usuario.setLogin((String)obj[i++]);
				usuario.setRamal((String)obj[i++]);
				usuario.setPessoa(new PessoaGeral());
				usuario.getPessoa().setId((Integer) obj[i++]);
				usuario.getPessoa().setNome((String)obj[i++]);
				usuario.setUnidade(new Unidade());
				usuario.getUnidade().setId((Integer)obj[i++]);
				usuario.getUnidade().setCodigo((Long)obj[i++]);
				usuario.getUnidade().setNome((String)obj[i++]);
				usuario.getUnidade().setSigla((String)obj[i++]);
			} else {

				// Buscar por usuário com consultor
				hql =  "select u.id, u.login, u.ramal, "
					+ "u.consultor.id, u.consultor.nome  "
					+ "from Usuario u where u.id = :idUsuario";
				q = getSession().createQuery(hql);
				q.setInteger("idUsuario", idUsuario);
				it = q.iterate();
				if (it.hasNext()) {
					usuario = new Usuario();
					Object[] obj = (Object[]) it.next();
					int i = 0;
					usuario.setId((Integer)obj[i++]);
					usuario.setLogin((String)obj[i++]);
					usuario.setRamal((String)obj[i++]);

					usuario.setConsultor(new Consultor());
					usuario.getConsultor().setId((Integer) obj[i++]);
					usuario.getConsultor().setNome((String)obj[i++]);

					usuario.setPessoa(new PessoaGeral());
					usuario.getPessoa().setNome(usuario.getConsultor().getNome());
				}

			}

			return usuario;

		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}

	/**
	 * Realiza um busca pelo login passado como parâmetro, podendo ainda ser filtrado para retornar apenas os usuários ativos
	 * e/ou apenas os usuários autorizados. 
	 * 
	 * @param login
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByLogin(String login, boolean apenasAtivos, boolean apenasAutorizados) throws DAOException {

		try {
			Criteria c = getSession().createCriteria(Usuario.class);
			c.add(Expression.eq("login", login));

			if (apenasAtivos)
				c.add(Expression.eq("inativo", false));

			return  (Usuario) c.setMaxResults(1).uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}


	/**
	 * Realiza um busca do usuário com o nome passado no parâmetro e há a possibilidade de filtrar ou não apenas pelos
	 * servidores.
	 * 
	 * @param nome
	 * @param somenteServidores
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Usuario> findByNome(String nome, boolean somenteServidores, Integer categoria ) throws DAOException {
		nome += "%";
		String where = "";
		StringBuilder sql = new StringBuilder(" select u.id_usuario, u.login, p.nome" +
				" from comum.usuario u "+
				" inner join comum.pessoa p on (p.id_pessoa = u.id_pessoa) ");
				
		where = " where ("+ UFRNUtils.toAsciiUpperUTF8("p.nome") + " like "+ UFRNUtils.toAsciiUpperUTF8(":NOME")   
						 + " or " + UFRNUtils.toAsciiUpperUTF8("p.nome_ascii") + " like "+ UFRNUtils.toAsciiUpperUTF8(":NOME") + ")";
  
		//Somente usuário com vinculo de servidor
		if(somenteServidores){
			sql.append("inner join rh.servidor s on ( s.id_pessoa = p.id_pessoa and s.id_ativo = :ATIVO ");
			//Somente servidor com categoria docente
			if(categoria != null){
				sql.append(" and s.id_categoria = :CATEGORIA "); 
			}
			sql.append(" ) ");
		}
			
		sql.append(where);			
			
		Query q = getSession().createSQLQuery(sql.toString());
		q.setString("NOME", nome);
		if(somenteServidores){
			q.setInteger("ATIVO", Ativo.SERVIDOR_ATIVO);
			if(categoria != null){
				q.setInteger("CATEGORIA", categoria);
			}
		}
		List<Usuario> lista = q.list();

		Collection<Usuario> result = new ArrayList<Usuario>();

		for (Iterator<?> iter = lista.iterator(); iter.hasNext();) {
			Object[] colunas = (Object[]) iter.next();
			Usuario usuario = new Usuario();
			usuario.setId((Integer) colunas[0]);
			usuario.setLogin((String) colunas[1]);
			Pessoa pessoa = new Pessoa();
			pessoa.setNome((String) colunas[2]);
			usuario.setPessoa(pessoa);
			result.add(usuario);
		}
		return result;
	}
	
	/**
	 * Realiza um busca do usuário docente com o nome passado no parâmetro
	 * 
	 * @param nome
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Usuario> findByNomeDocente(String nome, boolean somenteAtivos) throws DAOException {
		nome += "%";
		String where = "";
		StringBuilder sql = new StringBuilder(" select u.id_usuario, u.login, p.nome, s.siape" +
				" from comum.usuario u "+
				" inner join comum.pessoa p on (p.id_pessoa = u.id_pessoa) " +
				" inner join rh.servidor s on (s.id_pessoa = u.id_pessoa) ");

		where = " where s.id_categoria = " + Categoria.DOCENTE + " and (" + UFRNUtils.toAsciiUpperUTF8("p.nome") + " like "+ UFRNUtils.toAsciiUpperUTF8(":NOME")   
						 + " or " + UFRNUtils.toAsciiUpperUTF8("p.nome_ascii") + " like "+ UFRNUtils.toAsciiUpperUTF8(":NOME") + ")";
  
		if ( somenteAtivos ){
			where += " and s.id_ativo =  " + Ativo.SERVIDOR_ATIVO;
		}
			
		sql.append(where);			
			
		Query q = getSession().createSQLQuery(sql.toString());
		q.setString("NOME", nome);
		List<Usuario> lista = q.list();

		Collection<Usuario> result = new ArrayList<Usuario>();

		for (Iterator<?> iter = lista.iterator(); iter.hasNext();) {
			Object[] colunas = (Object[]) iter.next();
			Usuario usuario = new Usuario();
			usuario.setId((Integer) colunas[0]);
			usuario.setLogin((String) colunas[1]);
			Pessoa pessoa = new Pessoa();
			pessoa.setNome((String) colunas[2]);
			usuario.setPessoa(pessoa);
			Servidor s = new Servidor();
			s.setSiape((Integer) colunas[3]);
			usuario.setServidor(s);
			result.add(usuario);
		}
		return result;
	}

	
	/**
	 * Realizar um busca pelo usuário com o parâmetro pessoa passado. 
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> findByPessoa(Pessoa pessoa) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(Usuario.class);
			c.add(eq("pessoa", pessoa));
			c.add(eq("inativo", false));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	
	/**
	 * Realizar um busca de um usuário cujo cpf ou cnpj seja igual ao passado no parâmetro.
	 * 
	 * @param cpf_cnpj
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByCpf(long cpf_cnpj)
			throws DAOException {

		try {

			Criteria c = getSession().createCriteria(Usuario.class);
			Criteria pessoaC = c.createCriteria("pessoa");
			pessoaC.add(Expression.eq("cpf_cnpj", new Long(cpf_cnpj)));
			c.add(Expression.eq("inativo", Boolean.FALSE));
			
			return c.list();

		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Tem como finalidade realizar uma consulta com o intuito de retornar todos os usuário de uma determinada unidade, com a 
	 * possibilidade de filtrar para retornar apenas os ativos e/ou para retornar apenas os autorizados.
	 * 
	 * @param unidade
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Usuario> findByUnidade(UnidadeGeral unidade, boolean apenasAtivos,
			boolean apenasAutorizados) throws DAOException {

		try {
			StringBuilder where = new StringBuilder(100);
			where.append("u.unidade = " + unidade.getId());

			if (apenasAtivos) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.inativo = falseValue() ");
			}

			if (apenasAutorizados) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.autorizado = trueValue()");
			}
			if (where.length() > 0)
				where.insert(0, " where ");

			String hql = "select u from Usuario u" + where
					+ " order by u.pessoa.nome";
			Query query = getSession().createQuery(hql);

			if (isPaginable()) {
				query.setMaxResults(getPageSize());
				query.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					Integer total = (Integer) getSession().createQuery(
							"select count(*) from Usuario u " + where)
							.uniqueResult();
					setCount(total.intValue());
				}
			}

			return query.list();

		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}
	
	/**
	 * Realizar uma consulta para retornar todos os usuário.
	 */
	@SuppressWarnings("unchecked")
	@Override
	public <T> Collection<T> findAll(Class<T> classe) throws DAOException {
	
		try {
			Criteria c = getCriteria(classe);

			if (isPaginable()) {
				c.setMaxResults(getPageSize());
				c.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					Integer total = (Integer) getSession().createQuery(
							"select count(*) from Usuario").uniqueResult();
					setCount(total.intValue());
				}
			}
			return c.list();

		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}


	/**
	 * Realiza a consulta das hierarquias dos usuários de uma unidade, tendo a possibilidade ainda de filtrar apenas pelos
	 * usuários ativos e/ou apenas pelos usuários autorizados. 
	 * 
	 * @param unidade
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findAllByHierarquia(UnidadeGeral unidade,
			boolean apenasAtivos, boolean apenasAutorizados)
			throws DAOException {

		try {
			String hql = "select new Usuario(u.id, u.pessoa.nome, u.pessoa.cpf_cnpj, u.login, u.unidade.sigla)"
					+ " from Usuario u ";

			StringBuilder where = new StringBuilder(100);
			if (unidade != null && unidade.getId() != 0) {
				where
						.append(" u.unidade in "
								+ "(select unidade.id from Unidade unidade where hierarquia like '%."
								+ unidade.getId() + ".%')");
			}

			if (apenasAtivos) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.inativo = falseValue() ");
			}

			if (apenasAutorizados) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.autorizado = trueValue()");
			}
			if (where.length() > 0)
				where.insert(0, " where ");

			hql += where.toString();
			hql += " order by u.pessoa.nome";
			Query q = getSession().createQuery(hql);
	
			if (isPaginable()) {
				q.setMaxResults(getPageSize());
				q.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					String countHql = "select count(*) from Usuario u";
					if (unidade != null && unidade.getId() != 0) {
						countHql += where;
					}

					Integer total = (Integer) getSession()
							.createQuery(countHql).uniqueResult();
					setCount(total.intValue());
				}
			}
			return q.list();

		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}


	/**
	 * Responsável pela consulta dos usuários com o nome passado como parâmetro, unidade, podendo ou não filtrar pelos
	 * ativos e/ou apenas pelos usuários autorizados.  
	 * 
	 * @param nome
	 * @param unidade
	 * @param hierarquia
	 * @param apenasAtivos
	 * @param apenasAutorizados
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByNome(String nome,
			UnidadeGeral unidade, int hierarquia, boolean apenasAtivos,
			boolean apenasAutorizados) throws DAOException {

		try {
			StringBuilder where = new StringBuilder(200);

			if (nome != null && nome.trim().length() > 0) {
				nome = nome + '%';
				where.append(UFRNUtils.toAsciiUpperUTF8("u.pessoa.nome")  + " like "
						+ UFRNUtils.toAsciiUpperUTF8(":NOME") );
			}

			if (unidade != null && unidade.getId() > 0) {
				if (where.length() != 0)
					where.append(" AND ");
				where.append("u.unidade.id = " + unidade.getId());
			}

			if (hierarquia > 0) {
				if (where.length() != 0)
					where.append(" AND ");
				where.append("u.unidade.hierarquia like '%." + hierarquia
						+ ".%'");
			}

			if (apenasAtivos) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.inativo = falseValue() ");
			}

			if (apenasAutorizados) {
				if (where.length() > 0)
					where.append(" AND ");
				where.append(" u.autorizado = trueValue()");
			}

			if (where.length() > 0)
				where.insert(0, " where ");

			String hql = " from Usuario u " + where;
			Query q = getSession().createQuery(hql);

			if (nome != null && nome.trim().length() > 0) 
				q.setString("NOME", nome.trim());
			
			if (isPaginable()) {
				q.setMaxResults(getPageSize());
				q.setFirstResult((getPageNum() - 1) * getPageSize());

				if (getCount() == 0) {
					String countHql = "select count(*) from Usuario u " + where;
					Query countQuery = getSession().createQuery(countHql);
					if (nome != null && nome.trim().length() > 0) 
						countQuery.setString("NOME", nome.trim());

					Integer total = (Integer) countQuery.uniqueResult();
					setCount(total.intValue());
				}
			}

			return q.list();

		} catch (Exception e) {
			throw new DAOException(e);
		} 
	}


	/**
	 * Responsável por retornar uma lista com os usuário não autorizados.
	 * 
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> findDiscentesNaoAutorizados() throws DAOException {
		try {
			Criteria c = getCriteria(Usuario.class);
			c.add(Expression.eq("autorizado", false));
			c.add(Expression.not(Expression.isNull("discente")));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Responsável por retornar o usuário do discente informado. 
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByDiscente(Discente discente) throws DAOException {
		try {
			Criteria c = getCriteria(Usuario.class);
			c.add(Expression.eq("pessoa.id", discente.getPessoa().getId()));
			c.addOrder( Order.desc("id"));
			c.setMaxResults(1);
			return (Usuario) c.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Responsável por retornar apenas algumas informações do usuário, do discente informado. 
	 * 
	 * @param discente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Usuario findLeveByDiscente(Discente discente) throws DAOException {
		try {
			String projecao = "id, login, pessoa.nome, email";
			Query q = getSession().createQuery("select " + projecao +
					" from Usuario where pessoa.id = " + discente.getPessoa().getId() +
					" and (inativo = falseValue() or inativo is null) " +
					" order by id desc ");
			
			Collection<Usuario> usuarios = HibernateUtils.parseTo(q.setMaxResults(1).list(), projecao, Usuario.class);
			if (!usuarios.isEmpty()) {
				return usuarios.iterator().next();
			}
			return null;
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Responsável pela consulta de um usuário, passando como parâmetro um servidor. 
	 * 
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByServidor(Servidor servidor) throws DAOException {
		try {
			Criteria c = getCriteria(Usuario.class);
			Criteria pessoaC = c.createCriteria("pessoa");
			c.add(Expression.eq("pessoa.id", servidor.getPessoa().getId()));
			c.addOrder( Order.desc("id") );
			c.setMaxResults(1);
			return (Usuario) c.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Consulta um usuário a partir de um servidor que é passado como parâmetro.  
	 * 
	 * @param servidor
	 * @return
	 */
	public Usuario findByServidorLeve(Servidor servidor) {
		try {
			return (Usuario) getJdbcTemplate().queryForObject("select id_usuario, login from comum.usuario where id_pessoa = ? " + BDUtils.limit(1), new Object[] { servidor.getPessoa().getId() }, new RowMapper(){
				public Object mapRow(ResultSet rs, int numRow) throws SQLException {
					
					Usuario u = new Usuario();
					u.setId(rs.getInt("id_usuario"));
					u.setLogin(rs.getString("login"));
					
					return u;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Consulta um usuário a partir de um servidor que é passado como parâmetro para envio de e-mails.  
	 * 
	 * @param servidor
	 * @return
	 */
	public Usuario findByPessoaParaEmail(Pessoa pessoa) {
		try {
			return (Usuario) getJdbcTemplate().queryForObject("select u.id_usuario, u.login, u.email,p.nome from comum.usuario u inner join comum.pessoa p using(id_pessoa) where id_pessoa = ? " + BDUtils.limit(1), new Object[] { pessoa.getId() }, new RowMapper(){
				public Object mapRow(ResultSet rs, int numRow) throws SQLException {
					
					Usuario u = new Usuario();
					Pessoa p = new Pessoa();
					p.setNome(rs.getString("nome"));
					u.setPessoa(p);
					u.setId(rs.getInt("id_usuario"));
					u.setLogin(rs.getString("login"));
					u.setEmail(rs.getString("email"));
					
					return u;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Realizar a consulta do usuário a partir das informações de um consultor. 
	 * 
	 * @param consultor
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByConsultor(Consultor consultor) throws DAOException {
		try {
			Criteria c = getCriteria(Usuario.class);
			c.add(Expression.eq("consultor.id", consultor.getId()));
			return (Usuario) c.uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Realizar um consulta com o intuito de retornar todos os usuário que possuem um papel desejado.
	 * 
	 * @param papel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Usuario> findUsuariosByPapel(Papel papel) throws DAOException {
		try {
			Query q = getSession().createQuery("select usr from Usuario usr, Permissao per where "
					+ "usr.id = per.usuario.id and per.papel.id = :papel order by usr.pessoa.nome");
			q.setInteger("papel", papel.getId());
			return q.list();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna o usuário ativo do docente externo informado
	 * @param idDocente
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByDocenteExterno(int idDocente) throws DAOException {
		try {
			return (Usuario) getSession().createQuery("select u from Usuario u, DocenteExterno de" +
					" where de.pessoa.id = u.pessoa.id" +
					" and de.id = " + idDocente).setMaxResults(1).uniqueResult();
		} catch(Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Tem como finalidade retornar todos os usuário que são tutores. 
	 * 
	 * @param tutor
	 * @return
	 * @throws DAOException
	 */
	public Usuario findByTuTor(TutorOrientador tutor) throws DAOException {
		String hql = "select u from Usuario u where pessoa.id in (select t.pessoa.id from TutorOrientador t where t.id = :idTutor)";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idTutor", tutor.getId());
		q.setMaxResults(1);
		
		return (Usuario) q.uniqueResult();
	}

	/**
	 * Tem como finalidade retornar o usuário que possui o idDiscente passado como parâmetro.
	 * 
	 * @param idDiscente
	 * @return
	 */
	public Usuario findByDiscente(int idDiscente) {
		HibernateTemplate template = getHibernateTemplate();
		template.setMaxResults(1);
		return (Usuario) template.uniqueResult("select u from Usuario u where u.pessoa.id in (select d.pessoa.id from Discente d where d.id = ?)", idDiscente);
	}

	/**
	 * Buscar um usuário de um discente a partir de sua matrícula
	 *
	 * @param matricula
	 * @return
	 */
	public Usuario findByMatriculaDiscente(long matricula) {
		HibernateTemplate template = getHibernateTemplate();
		template.setMaxResults(1);
		return (Usuario) template.uniqueResult("select u from Usuario u where u.pessoa.id in (select d.pessoa.id from Discente d where d.matricula = ?)", matricula);
	}

	/**
	 * Retornar o usuário que é coordenador de um determinado polo.
	 * 
	 * @param coordenador
	 * @return
	 */
	public Usuario findByCoordenadorPolo(CoordenacaoPolo coordenador) {
		return (Usuario) getHibernateTemplate().uniqueResult("select p.usuario from CoordenacaoPolo p where p.id = ?", coordenador.getId());
	}

	/**
	 * Retorna todos os usuários de um servidor
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException 
	 * @throws  
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findAllByServidor(int idServidor) throws DAOException {
		
		String sql = "select id_usuario, login, email from comum.usuario where id_servidor = ? and autorizado = trueValue() and inativo = 'f'";

		return getJdbcTemplate().query(sql, new Object[] { idServidor }, new RowMapper(){

			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsuarioGeral u = new UsuarioGeral();
				u.setId(rs.getInt("id_usuario"));
				u.setLogin(rs.getString("login"));
				u.setEmail(rs.getString("email"));
				
				return u;
			}
			
		});
	}

	/**
	 * Retorna o usuário que possui o id_pessoa passado como parâmetro.
	 * 
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public Usuario findPrimeiroUsuarioByPessoa(int id) throws DAOException {
		Criteria c = getSession().createCriteria(Usuario.class);
		c.add( eq("pessoa.id", id) );
		c.add( Expression.or( eq("inativo", false),eq("inativo", null) ) );
		c.setMaxResults(1);
		return (Usuario) c.uniqueResult();
	}
	
	/**
	 * Retorna uma coleção de usuários contendo seu id, nome, login e id da foto.
	 * Utilizado na turma virtual para mostrar as fotos dos docentes de um tópico de aula.
	 * 
	 * @param idsPessoas
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<Usuario> findUsuariosComFotoByPessoas(List<Integer> idsPessoas) throws HibernateException, DAOException {
		String hql = "select u.id, u.idFoto, u.pessoa.id, u.pessoa.nome, u.login from Usuario u where u.inativo = falseValue() " +
						"and u.pessoa.id in " + UFRNUtils.gerarStringIn(idsPessoas);
		
		Query q = getSession().createQuery(hql);

		Iterator<?> it = q.iterate();
		Collection<Usuario> usuarios = new ArrayList<Usuario>();
		
		while(it.hasNext()) {
			Object[] obj = (Object[]) it.next();
			int i = 0;
			
			Usuario u = new Usuario();
			u.setId((Integer)obj[i++]);
			u.setIdFoto((Integer)obj[i++]);
			u.setPessoa(new Pessoa((Integer)obj[i++]));
			u.getPessoa().setNome((String)obj[i++]);
			u.setLogin((String)obj[i++]);
			
			usuarios.add(u);
		}
		
		return usuarios;
	}

	/**
	 * Verifica se a pessoa do usuário possui algum vínculo no sistema, seja com discente,
	 * servidor, docente externo, tutor ou coordenador EAD.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean hasVinculoSistema(Integer idPessoa) throws HibernateException, DAOException {
		return !getSession().createSQLQuery("select id_pessoa " +
				" from comum.pessoa p" +
				" where id_pessoa = " + idPessoa +
				" and (exists (select id_discente from discente where id_pessoa = p.id_pessoa and status not in " + 
				UFRNUtils.gerarStringIn(new Integer[]{StatusDiscente.EXCLUIDO, StatusDiscente.PENDENTE_CADASTRO}) +
				") or exists (select id_servidor from rh.servidor where id_pessoa = p.id_pessoa and id_situacao in "+ 
				UFRNUtils.gerarStringIn(ParametroHelper.getInstance().getParametroIntegerArray(ConstantesParametro.VINCULOS_SITUACAO_SERVIDOR_PERMITIDO)) +
				") or exists (select id_docente_externo from ensino.docente_externo where id_pessoa = p.id_pessoa and ativo = trueValue()))")
				.list().isEmpty();
	}
}