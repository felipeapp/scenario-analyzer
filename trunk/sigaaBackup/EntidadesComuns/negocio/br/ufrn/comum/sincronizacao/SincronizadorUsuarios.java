/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/11/2006
 */
package br.ufrn.comum.sincronizacao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Types;
import java.util.Date;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.object.BatchSqlUpdate;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.seguranca.log.LogJdbcUpdate;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe para sincronizar o cadastro de usuários nos três bancos de dados
 * (Comum, Administrativo e Acadêmico).
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class SincronizadorUsuarios {
	
	private static final SincronizadorUsuarios MOCK = new SincronizadorUsuarios((DataSource) null) {
		public void removerUsuario(UsuarioGeral u) { }
		public void sincronizarUsuario(UsuarioGeral u) { }
	};

	private JdbcTemplate jt;
	private Comando comando;
	private UsuarioGeral usuario;
	private int sistema;

	private SincronizadorUsuarios(DataSource ds) {
		if (ds != null)
			this.jt = new JdbcTemplate(ds);
	}

	/**
	 * Seta um usuário como inativo no banco de dados 
	 * @param u
	 */
	public void removerUsuario(UsuarioGeral u) {
		update("update comum.usuario set inativo = trueValue() where id_usuario = ?", new Object[] { u.getId() });
	}
	
	/**
	 * Sincroniza apenas o usuário utilizando um IDPESSOA já gerado. Não atualiza a pessoa associada.
	 * @param u
	 * @param idPessoa
	 */
	public void sincronizarApenasUsuario(UsuarioGeral u, int idPessoa) {
		
		if (u.getId() != 0 && usuarioExiste(u)) {
			String sql = "update comum.usuario set login=?, email=?, inativo=?, id_servidor=?, autorizado=?, expira_senha=?, id_unidade=?, "
				+ "funcionario=?, id_pessoa=?, tipo=?, ramal=?, data_cadastro=?, id_consignataria = ? "
				+ "where id_usuario=?";
			
			update(sql, new Object[] { u.getLogin(), u.getEmail(), u.isInativo(), u.getIdServidor(), u.isAutorizado(), u.getExpiraSenha(), isEmpty(u.getUnidade()) ? null : u.getUnidade().getId(), u.isFuncionario(), idPessoa,
					u.getTipo().getId(), u.getRamal(), u.getDataCadastro(), u.getIdConsignataria(), u.getId()});
		} else {
			String sql = "insert into comum.usuario (id_usuario, login, email, inativo, "
				+ "id_servidor, autorizado, expira_senha, id_unidade, funcionario, " 
				+ "id_pessoa, tipo, ramal, data_cadastro, id_consignataria) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			
			update(sql, new Object[] { u.getId(), u.getLogin(), u.getEmail(), u.isInativo(), u.getIdServidor(), u.isAutorizado(), u.getExpiraSenha(), isEmpty(u.getUnidade()) ? null : u.getUnidade().getId(), u.isFuncionario(), idPessoa,
					u.getTipo().getId(), u.getRamal(), u.getDataCadastro(), u.getIdConsignataria()});			
		}
	}

	/**
	 * Insere ou atualiza as informações de um usuário no banco de dados.
	 * @param u
	 */
	public void sincronizarUsuario(UsuarioGeral u) {
		int idPessoa = SincronizadorPessoas.usandoDataSource(jt.getDataSource()).sincronizarPessoa(u.getPessoa());
		
		sincronizarApenasUsuario(u, idPessoa);
	}
	
	/**
	 * Altera a unidade do usuário no banco de dados
	 * @param idUsuario
	 * @param idUnidade
	 */
	public void mudarUnidade(int idUsuario, int idUnidade) {
		update("update comum.usuario set id_unidade = ? where id_usuario = ?", new Object[] { idUnidade, idUsuario });
	}
	
	/*
	 * Realiza o update fazendo o log da operação JDBC
	 */
	private void update(String sql, Object... params) {
		jt.update(sql, params);
		
		Integer idUsuario = usuario == null ? null : usuario.getId();
		Integer idRegistro = (usuario == null || usuario.getRegistroEntrada() == null) ? null : usuario.getRegistroEntrada().getId();
		
		LogJdbcUpdate log = new LogJdbcUpdate();
		log.setCodMovimento(comando.getId());
		log.setData(new Date());
		log.setSql(sql);
		log.setParams(params);
		log.setSistema(sistema);
		log.setIdUsuario(idUsuario);
		log.setIdRegistroEntrada(idRegistro);

		LogProcessorDelegate.getInstance().writeJdbcUpdateLog(log);
	}

	/*
	 * Verifica se o usuário existe ou não no banco em questão
	 */
	private boolean usuarioExiste(UsuarioGeral u) {
		int count = jt.queryForInt("select count(id_usuario) from comum.usuario where id_usuario = ?", new Object[] { u.getId() });
		return count > 0;
	}

	public static SincronizadorUsuarios usandoDataSource(DataSource ds) {
		if (ds == null) {
			return MOCK;
		} else {
			return new SincronizadorUsuarios(ds);
		}
	}
	
	public static SincronizadorUsuarios usandoSistema(Movimento mov, int sistema) {
		
		 DataSource ds = null;
		 
		 if (Sistema.isSistemaAtivo(sistema)) {
			 ds = Database.getInstance().getDataSource(sistema);
		 }
		 
		 SincronizadorUsuarios sincronizadorUsuarios = usandoDataSource(ds);
		 sincronizadorUsuarios.comando = mov.getCodMovimento();
		 sincronizadorUsuarios.usuario = mov.getUsuarioLogado();
		 sincronizadorUsuarios.sistema = sistema;
		 return sincronizadorUsuarios;
	 }
	 
	 /**
	  * Retorna o próximo id possível para usuário.
	  * @return
	  */
	public static int getNextIdUsuario() {
		return new JdbcTemplate(Database.getInstance().getComumDs()).queryForInt("select nextval('comum.usuario_seq')");
	}

	/**
	 * Cria o objeto de batch de INSERT para a sincronização.
	 * @return
	 */
	public BatchSqlUpdate prepareBatchInsertDeSincronizacao() {
		
		BatchSqlUpdate insertBatch = new BatchSqlUpdate(jt.getDataSource(), "insert into comum.usuario (id_usuario, login, email, inativo, "
				+ "id_servidor, autorizado, expira_senha, id_unidade, funcionario, " 
				+ "id_pessoa, tipo, ramal, data_cadastro, id_consignataria) values "
				+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		insertBatch.declareParameter(new SqlParameter(Types.DATE));
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		insertBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		insertBatch.declareParameter(new SqlParameter(Types.DATE));
		insertBatch.declareParameter(new SqlParameter(Types.INTEGER));
		
		insertBatch.setBatchSize(500);
		
		insertBatch.compile();
		
		return insertBatch;
	}

	/**
	 * Cria o objeto de batch de UPDATE para a sincronização.
	 * @return
	 */
	public BatchSqlUpdate prepareBatchUpdateDeSincronizacao() {

		BatchSqlUpdate updateBatch = new BatchSqlUpdate(jt.getDataSource(), "update comum.usuario set login=?, email=?, inativo=?, " +
				" id_servidor=?, autorizado=?, expira_senha=?, id_unidade=?, "
				+ "funcionario=?, id_pessoa=?, tipo=?, ramal=?, data_cadastro=?, id_consignataria = ? "
				+ "where id_usuario=?");
		
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		updateBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		updateBatch.declareParameter(new SqlParameter(Types.DATE));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		updateBatch.declareParameter(new SqlParameter(Types.BOOLEAN));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		updateBatch.declareParameter(new SqlParameter(Types.VARCHAR));
		updateBatch.declareParameter(new SqlParameter(Types.DATE));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		updateBatch.declareParameter(new SqlParameter(Types.INTEGER));
		
		updateBatch.setBatchSize(5000);
		
		updateBatch.compile();
		
		return updateBatch;
	}
	
	/**
	 * Sincroniza apenas o usuário utilizando um IDPESSOA já gerado. Não atualiza a pessoa associada.
	 * @param u
	 * @param idPessoa
	 */
	public void sincronizarApenasUsuarioUsandoBatch(UsuarioGeral usuario, int idPessoa, BatchSqlUpdate insertBatch, BatchSqlUpdate updateBatch) {
		
		Integer idServidor = null;
		
		if (!ValidatorUtil.isEmpty(usuario.getIdServidor()) && existeServidor(usuario.getIdServidor())) {
			idServidor = usuario.getIdServidor();
		}
		
		int idUsuario = getIdUsuarioFromLogin(usuario.getLogin());
		if (idUsuario >= 0) {
			
			updateBatch.update(new Object[] {usuario.getLogin(), usuario.getEmail(), usuario.isInativo(), 
					idServidor, usuario.isAutorizado(), usuario.getExpiraSenha(), 
					isEmpty(usuario.getUnidade()) ? null : usuario.getUnidade().getId(), usuario.isFuncionario(), 
					(isEmpty(idPessoa) ? null : idPessoa), usuario.getTipo().getId(), usuario.getRamal(), usuario.getDataCadastro(), 
					(isEmpty(usuario.getIdConsignataria()) ? null : usuario.getIdConsignataria()), idUsuario});
			
		} else {

			if (usuarioExiste(usuario)) {
				idUsuario = getNextIdUsuario();
			} else {
				idUsuario = usuario.getId();
			}
			
			insertBatch.update(new Object[] {idUsuario, usuario.getLogin(), usuario.getEmail(), usuario.isInativo(), 
					idServidor, usuario.isAutorizado(), usuario.getExpiraSenha(), 
					isEmpty(usuario.getUnidade()) ? null : usuario.getUnidade().getId(), usuario.isFuncionario(), (isEmpty(idPessoa) ? null : idPessoa),
					usuario.getTipo().getId(), usuario.getRamal(), usuario.getDataCadastro(), 
					(isEmpty(usuario.getIdConsignataria()) ? null : usuario.getIdConsignataria())});
		}
	}

	/**
	 * Verifica se existe um usuário com o login informado no banco que está sendo atualizado.
	 * @param login
	 * @return
	 */
	private int getIdUsuarioFromLogin(String login) {
		try {
			return jt.queryForInt("SELECT id_usuario FROM comum.usuario WHERE upper(login) = ?", new Object[] {login.toUpperCase()});
		} catch (EmptyResultDataAccessException e) {
			return -1;
		}
	}

	/**
	 * Verifica se existe o servidor no banco que está sendo sincronizado através do ID.
	 * @param idServidor
	 * @return
	 */
	private boolean existeServidor(int idServidor) {
	
		int count = jt.queryForInt("SELECT count(id_servidor) FROM rh.servidor WHERE id_servidor = ?", new Object[] {idServidor});
		return count > 0;
	}
}