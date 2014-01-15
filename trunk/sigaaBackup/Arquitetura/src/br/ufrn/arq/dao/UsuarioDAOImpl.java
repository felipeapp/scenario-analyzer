/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 31/10/2007
 */
package br.ufrn.arq.dao;

import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.like;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.dominio.BloqueioUsuarioInativo;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * DAO para buscas de usuários.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
public class UsuarioDAOImpl extends GenericSharedDBDao {

	/** Usuáios em cache. Otimização da caixa postal */
	private static Hashtable<Integer, UsuarioGeral> usuarios = new Hashtable<Integer,UsuarioGeral>();
	
	/** Usuáios em cache. Otimização da caixa postal */
	private static Hashtable<String, UsuarioGeral> usuariosLogin = new Hashtable<String,UsuarioGeral>();
	
	public UsuarioGeral findByLogin(String login) throws DAOException {
		return findByLogin(login, true, true);
	}
	
	public UsuarioGeral findByLogin(String login, boolean somenteAtivos) throws DAOException {
		return findByLogin(login, somenteAtivos, true);
	}

	private UsuarioGeral findByLogin(String login, boolean apenasAtivos, boolean apenasAutorizados) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.add(Restrictions.eq("login", login));
		if (apenasAtivos) c.add(Restrictions.eq("inativo", false));

		// Não lista solicitações de cadastro
		//if (apenasAutorizados) c.add(Expression.eq("autorizado", true));
		//c.setMaxResults(1);

		return (UsuarioGeral) c.uniqueResult();

	}
	
	/**
	 * Método mais otimizado do que o genérico
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public UsuarioGeral findByPrimaryKey(int idUsuario) throws DAOException{
		String hql =  "select u.id, u.login, u.ramal, "
			+ "u.pessoa.id, u.pessoa.nome,  "
			+ "u.unidade.id, u.unidade.codigo, u.unidade.nome, u.unidade.sigla "
			+ "from UsuarioGeral u where u.id = :idUsuario";
		Query q = getSession().createQuery(hql);
		q.setInteger("idUsuario", idUsuario);

		Iterator<Object[]> it = q.iterate();
		UsuarioGeral usuario = null;

		if (it.hasNext()) {
			usuario = new UsuarioGeral();
			Object[] obj = it.next();
			int i = 0;
			usuario.setId((Integer)obj[i++]);
			usuario.setLogin((String)obj[i++]);
			usuario.setRamal((String)obj[i++]);
			usuario.setPessoa(new PessoaGeral());
			usuario.getPessoa().setId((Integer) obj[i++]);
			usuario.getPessoa().setNome((String)obj[i++]);
			usuario.setUnidade(new UnidadeGeral());
			usuario.getUnidade().setId((Integer)obj[i++]);
			usuario.getUnidade().setCodigo((Long)obj[i++]);
			usuario.getUnidade().setNome((String)obj[i++]);
			usuario.getUnidade().setSigla((String)obj[i++]);
		} 

		return usuario;
	}
	
	/**
	 * Método mais otimizado do que o genérico
	 * @param idUsuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public UsuarioGeral findByLoginLeve(String login) throws DAOException{
		String hql =  "select u.id, u.login, u.ramal, "
			+ "u.pessoa.id, u.pessoa.nome,  "
			+ "u.unidade.id, u.unidade.codigo, u.unidade.nome, u.unidade.sigla "
			+ "from UsuarioGeral u where u.login = :login";
		Query q = getSession().createQuery(hql);
		q.setString("login", login);

		Iterator<Object[]> it = q.iterate();
		UsuarioGeral usuario = null;

		if (it.hasNext()) {
			usuario = new UsuarioGeral();
			Object[] obj = it.next();
			int i = 0;
			usuario.setId((Integer)obj[i++]);
			usuario.setLogin((String)obj[i++]);
			usuario.setRamal((String)obj[i++]);
			usuario.setPessoa(new PessoaGeral());
			usuario.getPessoa().setId((Integer) obj[i++]);
			usuario.getPessoa().setNome((String)obj[i++]);
			usuario.setUnidade(new UnidadeGeral());
			usuario.getUnidade().setId((Integer)obj[i++]);
			usuario.getUnidade().setCodigo((Long)obj[i++]);
			usuario.getUnidade().setNome((String)obj[i++]);
			usuario.getUnidade().setSigla((String)obj[i++]);
		} 

		return usuario;
	}
	
	/** Busca os usuários da lista passada como parâmetro no banco de dados */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findListaUsuariosLeve(Collection<UsuarioGeral> lista) throws DAOException {
		StringBuilder hql =  new StringBuilder("select u.id, u.login, u.ramal, "
			+ "u.pessoa.id, u.pessoa.nome,  "
			+ "u.unidade.id, u.unidade.codigo, u.unidade.nome, u.unidade.sigla "
			+ "from UsuarioGeral u where u.login in (");
		
		for (Iterator<UsuarioGeral> it = lista.iterator(); it.hasNext(); ) {
			hql.append("'" + StringUtils.escapeBackSlash(it.next().getLogin()) + "'");
		}
		hql.append(")");
		
		Query q = getSession().createQuery(hql.toString());

		Iterator<Object[]> it = q.iterate();
		List<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();

		if (it.hasNext()) {
			UsuarioGeral usuario = new UsuarioGeral();
			Object[] obj = it.next();
			int i = 0;
			usuario.setId((Integer)obj[i++]);
			usuario.setLogin((String)obj[i++]);
			usuario.setRamal((String)obj[i++]);
			usuario.setPessoa(new PessoaGeral());
			usuario.getPessoa().setId((Integer) obj[i++]);
			usuario.getPessoa().setNome((String)obj[i++]);
			usuario.setUnidade(new UnidadeGeral());
			usuario.getUnidade().setId((Integer)obj[i++]);
			usuario.getUnidade().setCodigo((Long)obj[i++]);
			usuario.getUnidade().setNome((String)obj[i++]);
			usuario.getUnidade().setSigla((String)obj[i++]);
			usuarios.add(usuario);
		} 

		return usuarios;
	}

	
	/** Busca usuário em cache, otimização da caixa postal */
	public  UsuarioGeral findUsuarioLeve(int id) throws DAOException {
        UsuarioGeral user = usuarios.get(id);
        if ( user == null ) {
	        UsuarioGeral userBD = findByPrimaryKey(id);
	        if (userBD != null) {
		        usuarios.put(id, userBD);
		        user = userBD;
	        }
        }
        return user;

	}
	
	
	/** Busca usuário em cache, otimização da caixa postal */
	public  UsuarioGeral findUsuarioLeve(String login) throws DAOException {
        UsuarioGeral user = usuariosLogin.get(login);
        if ( user == null ) {
	        UsuarioGeral userBD = findByLoginLeve(login);
	        if (userBD != null) {
	        	usuariosLogin.put(login, userBD);
		        user = userBD;
	        }
        }
        return user;

	}

	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByNome(String nome, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.createCriteria("pessoa").add(like("nome", nome.toUpperCase() + "%")).addOrder(asc("nome"));
		if (somenteAtivos) c.add(Restrictions.eq("inativo", false));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByCpf(String cpf, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.add(eq("inativo", false)).createCriteria("pessoa").add(eq("cpf_cnpj", Long.valueOf(Formatador.getInstance().parseStringCPFCNPJ(cpf)))).addOrder(asc("nome"));
		if (somenteAtivos) c.add(Restrictions.eq("inativo", false));
		return c.list();
	}

	/**
	 * Busca todos os usuários cadastrados com determinado papel
	 * e com a informação de ativos ou nao no sistema
	 * @param idPapel
	 * @param apenasAtivos
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByPapel(int idPapel, boolean somenteAtivos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select pe.usuario.id, pe.usuario.idFoto, pe.usuario.login, pe.usuario.pessoa.nome, pe.usuario.unidade.nome, pe.usuario.unidade.sigla, pe.usuario.ramal, pe.usuario.email from Permissao pe ");
		hql.append("where pe.papel.id = :idPapel ");
		if (somenteAtivos) hql.append("and pe.usuario.inativo = falseValue() ");
		hql.append("order by pe.usuario.pessoa.nome");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPapel" , idPapel);

		List<Object[]> result = q.list();
		Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		for (int i = 0; i < result.size(); i++) {
			int j = 0;
			Object[] obj = result.get(i);
			UsuarioGeral user = new UsuarioGeral();
			user.setId((Integer) obj[j++]);
			user.setIdFoto((Integer) obj[j++]);
			user.setLogin((String) obj[j++]);
			user.setPessoa(new PessoaGeral());
			user.getPessoa().setNome((String) obj[j++]);
			user.setUnidade(new UnidadeGeral());
			user.getUnidade().setNome((String) obj[j++]);
			user.getUnidade().setSigla((String) obj[j++]);
			user.setRamal((String) obj[j++]);
			user.setEmail((String) obj[j++]);
			usuarios.add(user);
		}

		return usuarios;
	}

	@SuppressWarnings("unchecked")
	public Collection<? extends UsuarioGeral> findByUnidade(UnidadeGeral unidade) throws DAOException {
		Query q = getSession().createQuery("select distinct us.id from UsuarioGeral us join us.usuariosUnidades uu"
				+ " where ( us.unidade.id = :idUnid1 or uu.id = :idUnid2 ) ");
		q.setInteger("idUnid1", unidade.getId());
		q.setInteger("idUnid2", unidade.getId());

		return q.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<? extends UsuarioGeral> findBySipac() throws DAOException {
		String hql = "select distinct u from UsuarioGeral u, Permissao pe where u.id = pe.usuario.id and u.inativo= falseValue()";

		Query q = getSession().createQuery(hql);
		return q.list();
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findByEmail(String email) throws DAOException {
		return getSession().createQuery("select u from UsuarioGeral u where u.email = ? and inativo=falseValue() order by u.login asc").setString(0, email).list();
	}

	public boolean isGerenteSistema(int idUsuario) {
		return getJdbcTemplate().queryForInt("select count(*) from iproject.gerente_sistema where id_usuario = ?", new Object[] { idUsuario }) > 0;
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuariosServidores(String nome) {
		return getJdbcTemplate().query("select * from comum.usuario u, comum.pessoa p where u.id_pessoa = p.id_pessoa and upper(p.nome) like upper(?) " 
				+ " and u.id_servidor is not null order by p.nome asc", new Object[] { nome + "%" }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsuarioGeral u = new UsuarioGeral();
				u.setId(rs.getInt("id_usuario"));
				u.setNome(rs.getString("nome"));
				u.setLogin(rs.getString("login"));
				return u;
			}
		});
	}

	public UsuarioGeral findByLogin(String login, boolean buscarUnidade, UnidadeGeral obj, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.add(Restrictions.eq("login", login));
		if (somenteAtivos) c.add(eq("inativo", false));
		if (buscarUnidade) c.add(eq("unidade.id", obj.getId()));
		
		return (UsuarioGeral) c.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByCpf(String cpf, boolean buscarUnidade, UnidadeGeral unidade, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.add(eq("inativo", false)).createCriteria("pessoa").add(eq("cpf_cnpj", Formatador.getInstance().parseCPFCNPJ(cpf))).addOrder(asc("nome"));
		if (somenteAtivos) c.add(Restrictions.eq("inativo", false));
		if (buscarUnidade) c.add(eq("unidade.id", unidade.getId()));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByNome(String nome, boolean buscarUnidade, UnidadeGeral unidade, boolean somenteAtivos) throws DAOException {
		Criteria c = getSession().createCriteria(UsuarioGeral.class);
		c.createCriteria("pessoa").add(like("nome", nome.toUpperCase() + "%")).addOrder(asc("nome"));
		if (somenteAtivos) c.add(Restrictions.eq("inativo", false));
		if (buscarUnidade) c.add(eq("unidade.id", unidade.getId()));
		return c.list();
	}

	@SuppressWarnings("unchecked")
	public Collection<UsuarioGeral> findByPapel(int idPapel, boolean buscarUnidade, UnidadeGeral unidade, boolean somenteAtivos) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append("select pe.usuario.id, pe.usuario.idFoto, pe.usuario.login, pe.usuario.pessoa.nome, pe.usuario.unidade.nome, pe.usuario.unidade.sigla, pe.usuario.ramal from Permissao pe ");
		hql.append("where pe.papel.id = :idPapel and pe.autorizada = trueValue() ");
		if (somenteAtivos) hql.append("and pe.usuario.inativo = falseValue() ");
		if (buscarUnidade) hql.append("and pe.usuario.unidade.id = " + unidade.getId());
		hql.append(" order by pe.usuario.pessoa.nome");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idPapel" , idPapel);

		List<Object[]> result = q.list();
		Collection<UsuarioGeral> usuarios = new ArrayList<UsuarioGeral>();
		for (int i = 0; i < result.size(); i++) {
			int j = 0;
			Object[] obj = result.get(i);
			UsuarioGeral user = new UsuarioGeral();
			user.setId((Integer) obj[j++]);
			user.setIdFoto((Integer) obj[j++]);
			user.setLogin((String) obj[j++]);
			user.setPessoa(new PessoaGeral());
			user.getPessoa().setNome((String) obj[j++]);
			user.setUnidade(new UnidadeGeral());
			user.getUnidade().setNome((String) obj[j++]);
			user.getUnidade().setSigla((String) obj[j++]);
			user.setRamal((String) obj[j++]);
			usuarios.add(user);
		}

		return usuarios;

	}

	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findByNomeLoginOuCpf(String nome) throws DAOException {
		Long cpf = StringUtils.extractLong(nome);
		Query q = getSession().createQuery("select u from UsuarioGeral u where (upper(u.login) like upper(?) or upper(u.pessoa.nome) like upper(?) or "
				+ "u.pessoa.cpf_cnpj = ?) order by u.pessoa.nome");
		q.setString(0, nome + "%");
		q.setString(1, nome + "%");
		q.setLong(2, cpf == null ? 0 : cpf);
		return q.list();
	}

	public void associaUsuarioServidor(int idUsuario, int idServidor) {
		getJdbcTemplateComum().update("update comum.usuario set id_servidor = ? where id_usuario = ?", new Object[] { idServidor, idUsuario });
		getJdbcTemplateSipac().update("update comum.usuario set id_servidor = ? where id_usuario = ?", new Object[] { idServidor, idUsuario });
		if (Sistema.isSigaaAtivo())
			getJdbcTemplateSigaa().update("update comum.usuario set id_servidor = ? where id_usuario = ?", new Object[] { idServidor, idUsuario });
	}

	public void alterarDadosUsuario(UsuarioGeral usuario) {
		JdbcTemplate jtComum = getJdbcTemplateComum();
		JdbcTemplate jtSipac = getJdbcTemplateSipac();
		JdbcTemplate jtSigaa = null;
		if (Sistema.isSigaaAtivo()) jtSigaa = getJdbcTemplateSigaa();
		
		// Dados de usuário
		String updateUsuario = "update comum.usuario set login = ?, email = ?, ramal = ?, inativo = ?, tipo = ?, id_unidade = ? where id_usuario = ?";
		Object[] params = new Object[] { usuario.getLogin(), usuario.getEmail(), usuario.getRamal(), usuario.isInativo(), usuario.getTipo(), usuario.getUnidade().getId(), usuario.getId() };
		
		jtComum.update(updateUsuario, params);
		jtSipac.update(updateUsuario, params);
		if (jtSigaa != null) jtSigaa.update(updateUsuario, params);
		
		// Dados pessoais
		String selectIdPessoa = "select id_pessoa from comum.usuario where id_usuario = ?";
		String updatePessoa = "update comum.pessoa set nome = ?, sexo = ?, data_nascimento = ?, cpf_cnpj = ? where id_pessoa = ?";
		
		int idPessoaComum = jtComum.queryForInt(selectIdPessoa, new Object[] { usuario.getId() });
		int idPessoaSipac = jtSipac.queryForInt(selectIdPessoa, new Object[] { usuario.getId() });
		int idPessoaSigaa = -1;
		if (jtSigaa != null) {
			idPessoaSigaa = jtSigaa.queryForInt(selectIdPessoa, new Object[] { usuario.getId() });
		}
		
		jtComum.update(updatePessoa, new Object[] { usuario.getPessoa().getNome(), String.valueOf(usuario.getPessoa().getSexo()), usuario.getPessoa().getDataNascimento(), usuario.getPessoa().getCpf_cnpj(), idPessoaComum });
		jtSipac.update(updatePessoa, new Object[] { usuario.getPessoa().getNome(), String.valueOf(usuario.getPessoa().getSexo()), usuario.getPessoa().getDataNascimento(), usuario.getPessoa().getCpf_cnpj(), idPessoaSipac });
		if (jtSigaa != null) {
			jtSigaa.update(updatePessoa, new Object[] { usuario.getPessoa().getNome(), String.valueOf(usuario.getPessoa().getSexo()), usuario.getPessoa().getDataNascimento(), usuario.getPessoa().getCpf_cnpj(), idPessoaSigaa });
		}
		
	}

	private JdbcTemplate getJdbcTemplateSigaa() {
		return new JdbcTemplate(Database.getInstance().getSigaaDs());
	}

	private JdbcTemplate getJdbcTemplateSipac() {
		return new JdbcTemplate(Database.getInstance().getSipacDs());
	}

	private JdbcTemplate getJdbcTemplateComum() {
		return new JdbcTemplate(Database.getInstance().getComumDs());
	}

	public void alteraHashConfirmacaoCadastro(String login, String hash) {
		getJdbcTemplate(Database.getInstance().getComumDs()).update("update comum.usuario set autorizado = false, hash_confirmacao_cadastro = ? where login = ?", new Object[] { hash, login });
	}

	public boolean verificaHashConfirmacaoCadastro(String login, String hash) {
		try {
			String hashBanco = (String) getJdbcTemplate(Database.getInstance().getComumDs()).queryForObject("select hash_confirmacao_cadastro from comum.usuario where login = ?", new Object[] { login }, String.class);
			if (hash.equals(hashBanco))
				return true;
		} catch(EmptyResultDataAccessException e) { }
		return false;
	}

	public void autorizaCadastroUsuario(String login) {
		getJdbcTemplate(Database.getInstance().getComumDs()).update("update comum.usuario set autorizado = true where login = ?", new Object[] { login });
		getJdbcTemplate(Database.getInstance().getSipacDs()).update("update comum.usuario set autorizado = true where login = ?", new Object[] { login });
		if (Sistema.isSigaaAtivo())
			getJdbcTemplate(Database.getInstance().getSigaaDs()).update("update comum.usuario set autorizado = true where login = ?", new Object[] { login });
	}

	/**
	 * Busca usuários que já tiveram sua verificação de inatividade
	 * expirada há pelo menos 3 meses.
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuariosParaVerificacaoInatividade() throws DAOException {
		Query q = getSession().createQuery("select u from UsuarioGeral u where u.ultimaVerificacaoInatividade < ?");
		q.setDate(0, CalendarUtils.subtrairMeses(new Date(), 3));
		q.setMaxResults(20);
		return q.list();
	}
	
	/**
	 * Identifica se um usuário está bloqueado ou não devido à inatividade
	 * nos sistemas. 
	 * @param idUsuario
	 * @return
	 * @throws DAOException 
	 */
	public boolean isUsuarioBloqueado(int idUsuario) throws DAOException {
		Criteria c = getCriteria(BloqueioUsuarioInativo.class);
		c.add(eq("ativo", true)).add(eq("usuario.id", idUsuario));
		return c.uniqueResult() != null;
	}

}
