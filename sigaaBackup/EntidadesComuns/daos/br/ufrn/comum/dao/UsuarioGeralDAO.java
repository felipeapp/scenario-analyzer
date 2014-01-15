
package br.ufrn.comum.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.UsuarioUnidade;
import br.ufrn.comum.sincronizacao.SincronizadorPessoas;
import br.ufrn.comum.sincronizacao.SincronizadorUsuarios;

/**
 * DAO com as operações de Usuário Geral
 *
 * @author David Pereira
 *
 */
public class UsuarioGeralDAO {

	private DataSource administrativoDs = null;
	private DataSource sigaaDs = null;
	private DataSource comumDs = null;

	private JdbcTemplate administrativoTemplate = null;
	private JdbcTemplate sigaaTemplate = null;
	private JdbcTemplate comumTemplate = null;

	public UsuarioGeralDAO(DataSource administrativoDs, DataSource sigaaDs, DataSource comumDs) {
		this.administrativoDs = administrativoDs;
		this.sigaaDs = sigaaDs;
		this.comumDs = comumDs; 
	}

	public void cadastrarUsuario(UsuarioGeral usuario, UsuarioGeral usuarioLogado) throws NegocioException {
		cadastrarUsuario(usuario, usuarioLogado, null);
	}
	
	/**
	 * Recupera o cpf/cpnj do usuário, considerando o tipo do usuário.
	 * 
	 * @param usuario
	 * @return
	 * @throws NegocioException
	 */
	private long getCpfCnpjUsuario(UsuarioGeral usuario) throws NegocioException {
		
		long cpfCnpj = 0;
		
		// Cria pessoa, se nao existir
		switch (usuario.getTipo().getId()) {
			case TipoUsuario.TIPO_ALUNO:
				cpfCnpj = getCpfCnpjUsuarioAluno(usuario);
				break;
			case TipoUsuario.TIPO_SERVIDOR:
				cpfCnpj = getCpfCnpjUsuarioServidor(usuario);
				break;
			case TipoUsuario.TIPO_CONSULTOR:
				cpfCnpj = -1;
				break;
			case TipoUsuario.TIPO_PLANO_SAUDE:
			case TipoUsuario.TIPO_CONSIGNATARIA:
			case TipoUsuario.TIPO_COOPERACAO:
			case TipoUsuario.DOCENTE_EXTERNO:
			case TipoUsuario.TIPO_PRECEPTOR_ESTAGIO:
			case TipoUsuario.TIPO_CREDOR:
			case TipoUsuario.TIPO_OUTROS:
			case TipoUsuario.TIPO_FAMILIAR:
				if (usuario.getPessoa().getCpf_cnpj() == null)
					cpfCnpj = -1;
				else
					cpfCnpj = usuario.getPessoa().getCpf_cnpj();
				break;
	
			default:
				throw new NegocioException("Tipo de usuário inválido: " + usuario.getTipo().getId());
		}
		
		return cpfCnpj;
	}
	
	/**
	 * Retorna o cpf do usuário associado a um servidor existente
	 * @param usuario
	 * @return
	 * @throws NegocioException Caso nenhum servidor seja encontrado com o CPF informado.
	 */
	private long getCpfCnpjUsuarioServidor(UsuarioGeral usuario) throws NegocioException {
		
		try {
			return getCpfCnpjUsuarioByQuery("select p.cpf_cnpj from rh.servidor s, comum.pessoa p where s.id_pessoa = p.id_pessoa and s.id_servidor = ?", usuario.getIdServidor(), usuario);
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NegocioException("Nenhum servidor foi encontrado para o CPF informado: " + usuario.getCpf());
		}
	}
	
	/**
	 * Retorna o cpf do usuário associado a um aluno existente
	 * @param usuario
	 * @return
	 * @throws NegocioException Caso nenhum aluno seja encontrado com o CPF informado.
	 */
	private long getCpfCnpjUsuarioAluno(UsuarioGeral usuario) throws NegocioException {
		
		try {
			return getCpfCnpjUsuarioByQuery("select p.cpf_cnpj from academico.aluno a, comum.pessoa p where a.id_pessoa = p.id_pessoa and a.id_aluno = ?", usuario.getIdAluno(), usuario);
		} catch (IncorrectResultSizeDataAccessException e) {
			throw new NegocioException("Nenhum aluno foi encontrado para o CPF informado: " + usuario.getCpf());
		}
	}
	
	/**
	 * Realiza a consulta informada para recuperação do cpf/cnpj do usuário
	 * 
	 * @param query
	 * @param param
	 * @param usuario
	 * @return
	 * @throws NegocioException Caso a query não traga resultado.
	 */
	private long getCpfCnpjUsuarioByQuery(String query, Object param, UsuarioGeral usuario) throws NegocioException {
		
		long cpfCnpj = getAdministrativoTemplate().queryForLong(query, new Object[] {param});
		
		if (cpfCnpj <= 0) {
			cpfCnpj = usuario.getCpf();
		}
		
		return cpfCnpj;
	}

	public void cadastrarUsuario(UsuarioGeral usuario, UsuarioGeral usuarioLogado, Integer idPessoa) throws NegocioException {
		
		long cpfCnpj = getCpfCnpjUsuario(usuario);

		// cria usuário
		Calendar c = Calendar.getInstance();
		usuario.setAutorizado(true);
		usuario.setAnoOrcamentario(c.get(Calendar.YEAR));

		// Consultores (cpfCnpj == -1) não possuem pessoa
		if (cpfCnpj == -1) {
			if (idPessoa != null && idPessoa > 0) { // Cad. de usuário de docente externo
				int idUsuario = SincronizadorUsuarios.getNextIdUsuario();
				String sqlSipac = "insert into comum.usuario  (id_usuario, id_pessoa, id_unidade, login, tipo, autorizado, data_cadastro, email) values (?,?,?,?,?,?,?,?)";
				String sqlOutros = "insert into comum.usuario (id_usuario, id_pessoa, id_unidade, login, tipo, autorizado, data_cadastro, email) values (?,?,?,?,?,?,?,?)";
	
				if(Sistema.isAdministrativoAtivo()) {
					getAdministrativoTemplate().update(sqlSipac,  new Object[] { idUsuario, null, usuario.getUnidade().getId(), usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
				}
				getComumTemplate().update(sqlOutros, new Object[] { idUsuario, null, usuario.getUnidade().getId(), usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
				if (Sistema.isSigaaAtivo()) {
					getSigaaTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getPessoa().getId(), usuario.getUnidade().getId(), usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
				}
			} else {
				int idUsuario = SincronizadorUsuarios.getNextIdUsuario();
				String sqlSipac =  "insert into comum.usuario (id_usuario, login, tipo,  autorizado, data_cadastro, email) values (?,?,?,?,?,?)";
				String sqlOutros = "insert into comum.usuario (id_usuario, id_consultor, login, tipo, autorizado, data_cadastro, email) values (?,?,?,?,?,?,?)";

				getAdministrativoTemplate().update(sqlSipac, new Object[] { idUsuario, usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
				getComumTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdConsultor(), usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
				if (Sistema.isSigaaAtivo())
					getSigaaTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdConsultor(), usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), usuario.getEmail() });
			}
		} else {
			// Sincroniza as pessoas
			usuario.getPessoa().setCpf_cnpj(cpfCnpj);
			
			int idPessoaSipac = -1;
			if(Sistema.isAdministrativoAtivo()) {
				idPessoaSipac = cadastroPessoa(getAdministrativoTemplate(), usuario.getPessoa(), idPessoa);
			}
			int idPessoaSigaa = -1;
			if (Sistema.isSigaaAtivo()) {
				idPessoaSigaa = cadastroPessoa(getSigaaTemplate(), usuario.getPessoa(), idPessoa);
			}
			//int idPessoaSigrh = cadastroPessoa(getSigrhTemplate(), usuario.getPessoa());
			int idPessoaComum = cadastroPessoa(getComumTemplate(), usuario.getPessoa(), idPessoa);


			// Cria o usuário
			if (usuario.getTipo().getId() == TipoUsuario.TIPO_ALUNO && idPessoaSipac != -1 ) {
				// atualiza informação de aluno -> setIdPessoa()
				getAdministrativoTemplate().update("update aluno set id_pessoa = ? where id_aluno=?", new Object[] { idPessoaSipac, usuario.getIdAluno() });
			}

			String sqlSipac = "insert into comum.usuario (id_usuario, id_servidor, id_pessoa, login, tipo,  autorizado, data_cadastro, id_unidade, email) "
				+ "values (?,?,?,?,?,?,?,?,?)";
			String sqlOutros = "insert into comum.usuario (id_usuario, id_servidor, id_tutor, id_pessoa, login, tipo, autorizado, data_cadastro, id_docente_externo, id_unidade, email) "
				+ "values (?,?,?,?,?,?,?,?,?,?,?)";

			// Verificar pessoa
			int idUsuario = SincronizadorUsuarios.getNextIdUsuario();
			usuario.setId(idUsuario);

			if(Sistema.isAdministrativoAtivo()) {
				getAdministrativoTemplate().update(sqlSipac, new Object[] { idUsuario, usuario.getIdServidor(), idPessoaSipac, usuario.getLogin(), usuario.getTipo().getId(),  true, new Date(),usuario.getUnidade().getId(), usuario.getEmail() });
			}
			getComumTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdServidor(), null, idPessoaComum, usuario.getLogin(), usuario.getTipo().getId(), true, new Date(), null, usuario.getUnidade().getId(), usuario.getEmail() });
			if (Sistema.isSigaaAtivo()) {
				getSigaaTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdServidor(), usuario.getIdTutor(), idPessoaSigaa, usuario.getLogin(),  usuario.getTipo().getId(), true, new Date(), usuario.getIdDocenteExterno() , usuario.getUnidade().getId(), usuario.getEmail()});
			}


			// Associa usuário à unidades extras informadas
			String sqlInsertUnidades = "insert into comum.usuario_unidade (id_usuario, id_unidade, data, id_usuario_cadastro) values (?,?,?,?)";
			if( usuario.getUsuariosUnidades() != null && !usuario.getUsuariosUnidades().isEmpty() ){
				for (UsuarioUnidade uu : usuario.getUsuariosUnidades())
					executar(sqlInsertUnidades, new Object[] { usuario.getId(), uu.getUnidade().getId(), new Date(), usuarioLogado.getId() });
			}	
		}

		if(Sistema.isAdministrativoAtivo()) {
			int idUsuario = getAdministrativoTemplate().queryForInt("select id_usuario from comum.usuario where login=?", new Object[] { usuario.getLogin() });
			usuario.setId(idUsuario);
		}
		
		if (!ValidatorUtil.isEmpty(usuario.getSenha())) {
			try {
				UserAutenticacao.atualizaSenhaAtual(null, usuario.getId(), null, usuario.getSenha());
			} catch (ArqException e) {
				throw new NegocioException("Ocorreu um erro no cadastro do usuário: " + e.getMessage());
			}
		}
	}

	public void autoCadastroServidor(UsuarioGeral usuario) {
		String sqlSipac = "insert into comum.usuario (id_usuario, id_servidor, id_pessoa, login, tipo, autorizado, data_cadastro) "
			+ "values (?,?,?,?,?,?,?)";
		String sqlOutros = "insert into comum.usuario (id_usuario, id_servidor, id_pessoa, login, tipo, autorizado, data_cadastro) "
			+ "values (?,?,?,?,?,?,?)";

		// Verificar pessoa
		int idPessoa = cadastroPessoa(getAdministrativoTemplate(), usuario.getPessoa(), null);
		int idUsuario = SincronizadorUsuarios.getNextIdUsuario();
		
		getAdministrativoTemplate().update(sqlSipac, new Object[] { idUsuario, usuario.getIdServidor(), idPessoa, usuario.getLogin(), TipoUsuario.TIPO_SERVIDOR,  true, new Date() });

		if (Sistema.isSigaaAtivo()) { 
			cadastroPessoa(getSigaaTemplate(), usuario.getPessoa(), null);
			getSigaaTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdServidor(), idPessoa, usuario.getLogin(), TipoUsuario.TIPO_SERVIDOR, true, new Date() });
		}

		getComumTemplate().update(sqlOutros, new Object[] { idUsuario, usuario.getIdServidor(), idPessoa, usuario.getLogin(), TipoUsuario.TIPO_SERVIDOR, true, new Date() });

	}

	private int cadastroPessoa(JdbcTemplate template, PessoaGeral pessoa, Integer idPessoaSigaa) {
		int idPessoa = 0;

		if (Sistema.isSigaaAtivo() && template.getDataSource().equals(sigaaDs) && idPessoaSigaa != null) {
			idPessoa = idPessoaSigaa;
		} else {
			idPessoa = buscaPorCpfCnpj(template, pessoa.getCpf_cnpj());
		}

		if (idPessoa == 0) {
			if (pessoa.getCpf_cnpj() > 0) {
				idPessoa = SincronizadorPessoas.getNextIdPessoa();
	
				String sql = "insert into comum.pessoa (id_pessoa, nome, nome_ascii, data_nascimento, sexo, passaporte, cpf_cnpj, email, endereco, tipo, valido, funcionario) "
					+ "values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
	
				template.update(sql, new Object[] { idPessoa, pessoa.getNome(), pessoa.getNomeAscii(), pessoa.getDataNascimento(), String.valueOf(pessoa.getSexo()), pessoa.getPassaporte(), pessoa.getCpf_cnpj(),
						pessoa.getEmail(), pessoa.getEndereco(), String.valueOf(pessoa.getTipo()), true, pessoa.isFuncionario() == null ? false : pessoa.isFuncionario() });
			}
		}

		return idPessoa;
	}

	private int buscaPorCpfCnpj(JdbcTemplate template, long cpfCnpj) {
		try {
			Integer pessoa = (Integer) template.queryForObject("select id_pessoa from comum.pessoa where cpf_cnpj=?", new Object[] { cpfCnpj }, Integer.class);

			return pessoa;
		} catch (EmptyResultDataAccessException e) {
			return 0;
		}
	}

	public void alterarUsuario(UsuarioGeral usuario, UsuarioGeral usuarioLogado) {
		String sqlDeleteUnidades = "delete from comum.usuario_unidade where id_usuario=?";
		String sqlInsertUnidades = "insert into comum.usuario_unidade (id_usuario, id_unidade, data, id_usuario_cadastro) values (?,?,?,?)";
		String sqlPessoa = "update comum.pessoa set data_nascimento=?, sexo=?, nome=?, cpf_cnpj=? where id_pessoa=(select id_pessoa from comum.usuario where id_usuario=?)";
		String sqlUsuario = "update comum.usuario set login=?, email=?, id_unidade=?, ramal=?, inativo=? where id_usuario=?";

		executar(sqlDeleteUnidades, new Object[] { usuario.getId() });
		for (UsuarioUnidade uu : usuario.getUsuariosUnidades())
			executar(sqlInsertUnidades, new Object[] { usuario.getId(), uu.getUnidade().getId(), new Date(), usuarioLogado.getId() });

		executar(sqlPessoa, new Object[] { usuario.getDataNascimento(), String.valueOf(usuario.getSexo()), usuario.getNome(), usuario.getCpf(), usuario.getId() });
		executar(sqlUsuario, new Object[] { usuario.getLogin(), usuario.getEmail(), usuario.getUnidade().getId(), usuario.getRamal(), usuario.isInativo(), usuario.getId() });
	}

	public void autoCadastroDiscente(UsuarioGeral usuario) {

	}

	public void removerUsuario(UsuarioGeral usuario) {
		String sql = "delete from comum.usuario where id_usuario=?";
		executar(sql, new Object[] { usuario.getId() });
	}

	public void alterarSenha(UsuarioGeral usuario) throws ArqException {
		// Seta data de expirara a senha
		int diasExpirar = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.EXPIRA_SENHA);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_YEAR, diasExpirar);
		usuario.setExpiraSenha(c.getTime());

		String sql = "update comum.usuario set expira_senha=? where id_usuario=?";
		executar(sql, new Object[] { usuario.getExpiraSenha(), usuario.getId() });
		
		UserAutenticacao.atualizaSenhaAtual(null, usuario.getId(), null, usuario.getSenha());
	}

	public void alterarMeusDados(UsuarioGeral usuario) {
		String sqlPessoa = "update comum.pessoa set data_nascimento=?, sexo=? where id_pessoa=?";
		String sqlUsuario = "update comum.usuario set email=?, ramal=? where id_usuario=?";

		// Atualizar Pessoa
		executar(sqlPessoa, new Object[] { usuario.getPessoa().getDataNascimento(), String.valueOf(usuario.getPessoa().getSexo()), usuario.getPessoa().getId() });

		// Atualizar Usuario
		executar(sqlUsuario, new Object[] { usuario.getEmail(), usuario.getRamal(), usuario.getId() });
	}


	/**
	 * @return the comumTemplate
	 */
	public JdbcTemplate getComumTemplate() {
		if (comumTemplate == null)
			comumTemplate = new JdbcTemplate(comumDs);
		return comumTemplate;
	}

	/**
	 * @return the sigaaTemplate
	 */
	public JdbcTemplate getSigaaTemplate() {
		if (Sistema.isSigaaAtivo() && sigaaTemplate == null)
			sigaaTemplate = new JdbcTemplate(sigaaDs);
		return sigaaTemplate;
	}

	/**
	 * @return the sipacTemplate
	 */
	public JdbcTemplate getAdministrativoTemplate() {
		if (administrativoTemplate == null)
			administrativoTemplate = new JdbcTemplate(administrativoDs);
		return administrativoTemplate;
	}

	private void executar(String sql, Object[] params) {
		getAdministrativoTemplate().update(sql, params);
		getComumTemplate().update(sql, params);
		if (Sistema.isSigaaAtivo()) {
			getSigaaTemplate().update(sql, params);
		}
	}

	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuariosByServidor(Integer idServidor) {
		return getAdministrativoTemplate().queryForList("select u.id, u.inativo from comum.usuario where id_servidor="+idServidor, UsuarioGeral.class);
	}

	public void atribuirPapel(int papel, UsuarioGeral usuario, UsuarioGeral usuarioLogado) {
		getAdministrativoTemplate().update("insert into comum.permissao (id_usuario, id_papel, id_usuario_cadastro, data_cadastro) values (?,?,?,?)", new Object[] { usuario.getId(), papel, usuarioLogado.getId(), new Date() });
	}

	public boolean existeLogin(String login) {
		int count = getComumTemplate().queryForInt("select count(*) from comum.usuario where login=?", new Object[] { login });		
		return count > 0;
	}

	public void atualizarDadosPessoais(int idUsuario, String email, String ramal) {
		getComumTemplate().update("update comum.usuario set email=?, ramal=? where id_usuario=?", new Object[] { email, ramal, idUsuario });
	}

	public void atualizarDadosPessoaisTodosOsBancos(int idUsuario, Long cpf, String email, String ramal) {
		boolean sigaaAtivo = Sistema.isSigaaAtivo();
		
		// Atualizar dados em usuário
		getComumTemplate().update("update comum.usuario set email=?, ramal=? where id_usuario=?", new Object[] { email, ramal, idUsuario });
		getAdministrativoTemplate().update("update comum.usuario set email=?, ramal=? where id_usuario=?", new Object[] { email, ramal, idUsuario });
		if (sigaaAtivo) {
			getSigaaTemplate().update("update comum.usuario set email=?, ramal=? where id_usuario=?", new Object[] { email, ramal, idUsuario });
		}
		
		// Atualizar email em pessoa
		
		if (cpf !=  null) {
			String sqlPessoa = "update comum.pessoa set email = ? where cpf_cnpj = ?"; 
			getComumTemplate().update(sqlPessoa, new Object[] { email, cpf });
			getAdministrativoTemplate().update(sqlPessoa, new Object[] { email, cpf });
			if (sigaaAtivo) {
				getSigaaTemplate().update(sqlPessoa, new Object[] { email, cpf });
			}
		}
	}
	
	public void atualizarIdPessoa(int idUsuario, int idPessoa, Long cpf) {
		String sqlUsuario = "update comum.usuario set id_pessoa=? where id_usuario=?";
		if (Sistema.isSigaaAtivo()) {
			getSigaaTemplate().update(sqlUsuario, new Object[] { idPessoa, idUsuario });
		}

		if (cpf != null) {
			String sqlPessoa = "select id_pessoa from comum.pessoa where cpf_cnpj=? order by nome " + BDUtils.limit(1);
		
			try {
				int idPessoaSipac = getAdministrativoTemplate().queryForInt(sqlPessoa, cpf);
				getAdministrativoTemplate().update(sqlUsuario, new Object[] {  idPessoaSipac, idUsuario });
			} catch(EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
			
			try {
				int idPessoaComum = getComumTemplate().queryForInt(sqlPessoa, cpf);
				getComumTemplate().update(sqlUsuario, new Object[] { idPessoaComum, idUsuario });
			} catch(EmptyResultDataAccessException e) {
				e.printStackTrace();
			}
		}
	}

}
