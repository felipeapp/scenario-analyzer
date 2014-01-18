/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '15/08/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ActionEvent;

import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.RegistroEntradaImpl;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.log.LogOperacao;
import br.ufrn.arq.usuarios.UserAutenticacao;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dominio.SigaaConstantes;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.struts.LoginActions;

/**
 * ManagedBean de Usuário. Permite logar como outro usuário.
 *
 * @author Gleydson
 *
 */
public class UsuarioMBean extends SigaaAbstractController<Usuario> {

	/** Constante que indica a quantidade máxima de resultados da busca por login. */
	private static final int QTD_RESULTADOS_BUSCA_LOGIN = 200;

	/** Indica se achou o login do usuário. */
	private boolean achou;

	/** Tempo usado nos usuários logados. */
	private int tempo;
	
	/** Tempo de atividade usado nos usuários logados. */
	private int tempoAtividade;

	/** Registros de entradas usado nos usuários logados. */
	private Collection<RegistroEntrada> entradas;

	/** Log de Operações para usuários logados. */
	private Collection<LogOperacao> operacoes;

	/** Senha atual do usuário. */
	private String senha;
	
	/** Nova senha do usuário. */
	private String novaSenha;

	/** Redigitação da nova senha do usuário. */
	private String reNovaSenha;

	/** Data/hora do login.*/
	private Date data;

	/** Motivo de uso do logar como */
	private String motivo;
	
	/** Construtor padrão. */
	public UsuarioMBean() {
		obj = new Usuario();
	}

	/**
	 * Busca os usuários.<br/>Método não invocado por JSP´s.
	 * @throws DAOException
	 */
	public void preencheUsuariosEntrada() throws DAOException {
		UsuarioDao userDao = getDAO(UsuarioDao.class);
		for (RegistroEntrada registro : entradas) {
			registro.setUsuario(userDao.findUsuarioLeve(registro.getUsuario()
					.getId()));
		}
	}

	/** Retorna os dados do usuário logado.
	 * @return
	 */
	public String getDadosUsuario() {
		obj = getUsuarioLogado();
		return "";
	}

	/**
	 * Visualiza as operações de um registro de entrada<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/logados.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public String verRegistroEntrada() throws SegurancaException, DAOException,
			SQLException {

		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);

		RegistroEntradaImpl regDao = new RegistroEntradaImpl(Sistema.SIGAA);
		operacoes = regDao.findLogByRegistro(getParameterInt("idRegistro"));

		getCurrentRequest().setAttribute("registro", regDao.findByPrimaryKey(getParameterInt("idRegistro"), RegistroEntrada.class));
		return forward("/administracao/usuario/log_operacao.jsp");

	}

	/**
	 * Busca por registro de entradas em um determinado tempo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/logados.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void buscaEntradasTempo(ActionEvent evt) throws SegurancaException,
			DAOException {

		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);

		RegistroEntradaImpl regDao = new RegistroEntradaImpl(Sistema.SIGAA);
		entradas = regDao.findByTempo(tempo);

		getCurrentRequest().setAttribute("total", entradas.size());

		preencheUsuariosEntrada();

	}

	/**
	 * Busca por registro de entradas em um determinado tempo
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/logados.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void buscaTempoAtividades(ActionEvent evt) throws SegurancaException,
			DAOException {

		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);

		RegistroEntradaImpl regDao = new RegistroEntradaImpl(Sistema.SIGAA);
		entradas = regDao.findByAtividade(tempoAtividade);

		getCurrentRequest().setAttribute("total", entradas.size());

		preencheUsuariosEntrada();

	}

	/**
	 * Busca por registro de entradas em um determinado usuário
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/logados.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public void buscaEntradasLogin(ActionEvent evt) throws SegurancaException,
			DAOException {

		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);

		RegistroEntradaImpl regDao = new RegistroEntradaImpl(Sistema.SIGAA);
		entradas = regDao.findByLogin(obj.getLogin(), QTD_RESULTADOS_BUSCA_LOGIN);

		getCurrentRequest().setAttribute("total", entradas.size());

		preencheUsuariosEntrada();

	}

	/** Verifica os papéis: ADMINSTRADOR_SIGAA
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getCheckRole()
	 */
	@Override
	public String getCheckRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		return "";
	}

	/**
	 * Busca o usuário por login
	 * <br/>Método não invocado por JSP´s.
	 * @param evt
	 * @throws DAOException
	 */
	public void buscaPorLogin(ActionEvent evt) throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		obj = (Usuario) dao.findByLogin(obj.getLogin());
		if (obj != null)
			achou = true;
	}

	/** Indica se achou o login do usuário. 
	 * @return
	 */
	public boolean isAchou() {
		return achou;
	}

	/** Seta se achou o login do usuário. 
	 * @param achou
	 */
	public void setAchou(boolean achou) {
		this.achou = achou;
	}

	/**
	 * Retornar ao usuário anterior
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/logar_como.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String retornarUsuario() throws SegurancaException {

		getCurrentSession().setAttribute("usuario",
				getCurrentSession().getAttribute("usuarioAnterior"));
		getCurrentSession().setAttribute("acesso",
				getCurrentSession().getAttribute("acessoAnterior"));

		getCurrentSession().removeAttribute("usuarioAnterior");
		getCurrentSession().removeAttribute("acessoAnterior");

		return "menuPrincipal";
	}

	/**
	 * Alterar os dados do usuário
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/dados_pessoais/dados.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 * @throws NegocioException
	 */
	public String alteraDados() throws ArqException, RemoteException,
			NegocioException {

		String email = obj.getEmail();
		obj = getUsuarioLogado();

		if (novaSenha != null && !novaSenha.trim().equals("")) {

			if (!novaSenha.equals(reNovaSenha)) {
				addMensagemErro("A senha digitada e a confirmação dela não conferem");
			}
		}

		if (senha != null && !senha.equals("") ) {
			if (!senha.equals((obj).getSenha())) {
				addMensagemErro("Senha do Usuário não confere");
			}
		}

		ValidatorUtil.validateEmail(email, "E-Mail do Usuário", erros);

		obj.setEmail(email);

		if (hasErrors()) {
			return null;
		} else {

			GenericDAO dao = getGenericDAO();
			Usuario newUser = dao.findByPrimaryKey(getUsuarioLogado().getId(),
					Usuario.class);
			newUser.setEmail(obj.getEmail());
			if ( novaSenha != null && !novaSenha.equals(""))
				newUser.setSenha(novaSenha);

			getUsuarioLogado().setEmail(obj.getEmail());

			prepareMovimento(SigaaListaComando.ALTERAR_DADOS_USUARIO);

			MovimentoCadastro movCad = new MovimentoCadastro();
			movCad.setObjMovimentado(newUser);
			movCad.setCodMovimento(SigaaListaComando.ALTERAR_DADOS_USUARIO);

			execute(movCad, getCurrentRequest());

			addMessage("Dados Alterados com Sucesso", TipoMensagemUFRN.INFORMATION);

			return "menuPrincipal";
		}

	}

	/** Inicia a operação de logar como outro usuário.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp </li>
	 * <li>/sigaa.war/stricto/menus/permissao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarLogarComo() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.ADMINISTRADOR_STRICTO } );
		return forward("/administracao/usuario/logar_como.jsp");
	}

	/** Inicia a operação de atribuição de permissões.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPermissao() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.ADMINISTRADOR_STRICTO  } );
		return forward("/administracao/papeis/buscar.jsp");
	}

	/**
	 * Loga como outro usuário
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/administracao/usuario/mudar_unidade.jsp</li>
	 * <li>/sigaa.war/administracao/usuario/logar_como.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws RemoteException
	 */
	public String logarComo() throws ArqException, RemoteException {

		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.ADMINISTRADOR_STRICTO, SigaaPapeis.GESTOR_LATO,SigaaPapeis.COORDENADOR_GERAL_REDE);
		
		ValidatorUtil.validateRequired(obj.getLogin(), "Login", erros);
		if (hasErrors()) return null;

		UsuarioMov mov = new UsuarioMov();

		UsuarioDao dao = getDAO(UsuarioDao.class);
		PermissaoDAO pDao = getDAO(PermissaoDAO.class);

		// Procurar por login
		String login = obj.getLogin();
		obj = (Usuario) dao.findByLogin(login);

		// Se não encontrar por login, buscar o usuário do discente com a matricula informada
		if (obj == null ) {
			try {
				long matricula = Long.parseLong(login);
				obj = dao.findByMatriculaDiscente(matricula);
			} catch (Exception e) {}
		}

		if (obj == null) {
			addMensagemErro("Usuário não encontrado");
			return null;
		} else {
			if (obj.getConsultor() != null) {
				obj.getConsultor().getAreaConhecimentoCnpq();
			}
			
			obj.setPermissoes(pDao.findPermissoesByUsuario(obj));
			//obj.getPermissoes().iterator();
			mov.setCodMovimento(SigaaListaComando.LOGAR_COMO);
			mov.setIP(getCurrentRequest().getRemoteAddr());
			mov.setUsuario(obj);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setRequest(getCurrentRequest());
			mov.setMotivo(motivo);

			try {

				SubSistema subsistemaAnterior = getSubSistema();
				
				prepareMovimento(mov.getCodMovimento());
				executeWithoutClosingSession(mov, getCurrentRequest());

				removerAtributosSessao();

				getCurrentSession().setAttribute("usuarioAnterior", getCurrentSession().getAttribute("usuario"));
				getCurrentSession().setAttribute("acessoAnterior",	getCurrentSession().getAttribute("acesso"));
				getCurrentSession().setAttribute("subsistemaAnterior",	subsistemaAnterior);
				
				obj.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());

				getCurrentSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, obj);

				Usuario usuario = obj;
				List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, getCurrentRequest());
				usuario.setVinculos(vinculos);
				
				boolean multiplosVinculos = usuario.getVinculosPrioritarios().size() > 1;
				
				if (!multiplosVinculos)
					if (!isEmpty(usuario.getVinculosPrioritarios()))
						usuario.setVinculoAtivo(usuario.getVinculosPrioritarios().get(0));
					else
						usuario.setVinculoAtivo(usuario.getVinculos().get(0));
				
				if (multiplosVinculos) {
					return redirect("/escolhaVinculo.do?dispatch=listar");
				} else {
					
					VinculoUsuario.popularVinculoAtivo(obj);
					
					LogonProgress progress = (LogonProgress) WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()).getBean("logonProgress");
					AcessoMenu acesso = new AcessoMenu(obj, progress);
					acesso.executar(getCurrentRequest());
					getCurrentSession().setAttribute("acesso", acesso.getDados());
					if (acesso.getDados().isDocenteUFRN() && !acesso.getDados().isUnidadeEspecializada()) {
						if (obj.getServidor().getUnidade() == null) {
							addMensagemErro(
									"Caro Usuário, não foi possível determinar o mapeamento entre a sua " +
									"lotação oficial de Recursos Humanos e a unidade do SIGAA");
							return null;
						}
						obj.setUnidade(obj.getServidor().getUnidade());
					}
	
					LoginActions loginAction = new LoginActions();
					loginAction.carregarParametrosCalendarioAtual( getCurrentRequest() );
				}

			} catch (NegocioException e) {
				addMensagemErro(e.getMessage());
				return null;
			} catch (Exception e) {
				notifyError(e);
				tratamentoErroPadrao(e);
			}

			return "menuPrincipal";

		}
	}


	/**
	 * Realiza o procedimento de Logon pelo celular
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws ArqException 
	 */
	public String logonMobile() throws ArqException {

		if ( obj.getLogin() == null || obj.getSenha() == null ) {
			addMensagemErro("Usuário ou senha vazios");
		} else {

			boolean autenticado = UserAutenticacao.autenticaUsuario(getCurrentRequest(), obj.getLogin(), obj.getSenha());
			if ( !autenticado ) {

				addMensagemErro("Usuário/Senha inválidos");

			} else {

				UsuarioDao dao = getDAO(UsuarioDao.class);
				Usuario user = (Usuario) dao.findByLogin(obj.getLogin());
				if ( user.getDiscente() == null ) {
					addMensagemErro("Somente alunos tem acesso ao SIGAA Mobile");
				} else {
					getCurrentSession().setAttribute("usuario", user);
					return "/mobile/menu.jsp";
				}
			}
		}
		return null;
	}

	/** Redireciona o usuário para o menu principal do sistema.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/docsEletronicos/docs_eletronicos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String redirecionarMenuPrincipal() {
		getCurrentRequest().getSession().removeAttribute("ajaxRequest");
		redirect("/verMenuPrincipal.do");
		return null;
	}
	
	
	/** Retorna os registros de entradas usado nos usuários logados. 
	 * @return
	 */
	public Collection<RegistroEntrada> getEntradas() {
		return entradas;
	}

	/** Seta os registros de entradas usado nos usuários logados.
	 * @param entradas
	 */
	public void setEntradas(Collection<RegistroEntrada> entradas) {
		this.entradas = entradas;
	}

	/** Retorna o tempo usado nos usuários logados. 
	 * @return
	 */
	public int getTempo() {
		return tempo;
	}

	/**Seta o tempo usado nos usuários logados. 
	 * @param tempo
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	/** Retorna o log de Operações para usuários logados. 
	 * @return
	 */
	public Collection<LogOperacao> getOperacoes() {
		return operacoes;
	}

	/** Seta o log de Operações para usuários logados.
	 * @param operacoes
	 */
	public void setOperacoes(Collection<LogOperacao> operacoes) {
		this.operacoes = operacoes;
	}

	/** Retorna a nova senha do usuário. 
	 * @return
	 */
	public String getNovaSenha() {
		return novaSenha;
	}

	/** Seta a nova senha do usuário.
	 * @param novaSenha
	 */
	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	/** Retorna a redigitação da nova senha do usuário.
	 * @return
	 */
	public String getReNovaSenha() {
		return reNovaSenha;
	}

	/** Seta a redigitação da nova senha do usuário. 
	 * @param reNovaSenha
	 */
	public void setReNovaSenha(String reNovaSenha) {
		this.reNovaSenha = reNovaSenha;
	}

	/** Retorna a senha atual do usuário.
	 * @return
	 */
	public String getSenha() {
		return senha;
	}

	/** Seta a senha atual do usuário. 
	 * @param senha
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/** Retorna o tempo de atividade usado nos usuários logados.
	 * @return
	 */
	public int getTempoAtividade() {
		return tempoAtividade;
	}

	/** Seta o tempo de atividade usado nos usuários logados. 
	 * @param tempoAtividade
	 */
	public void setTempoAtividade(int tempoAtividade) {
		this.tempoAtividade = tempoAtividade;
	}

	/** Retorna a data/hora do login.
	 * @return
	 */
	public Date getData() {
		return data;
	}

	/** Seta a data/hora do login.
	 * @param data
	 */
	public void setData(Date data) {
		this.data = data;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}
	
}