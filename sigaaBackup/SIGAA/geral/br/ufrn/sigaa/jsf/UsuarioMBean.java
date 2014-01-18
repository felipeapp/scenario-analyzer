/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
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
 * ManagedBean de Usu�rio. Permite logar como outro usu�rio.
 *
 * @author Gleydson
 *
 */
public class UsuarioMBean extends SigaaAbstractController<Usuario> {

	/** Constante que indica a quantidade m�xima de resultados da busca por login. */
	private static final int QTD_RESULTADOS_BUSCA_LOGIN = 200;

	/** Indica se achou o login do usu�rio. */
	private boolean achou;

	/** Tempo usado nos usu�rios logados. */
	private int tempo;
	
	/** Tempo de atividade usado nos usu�rios logados. */
	private int tempoAtividade;

	/** Registros de entradas usado nos usu�rios logados. */
	private Collection<RegistroEntrada> entradas;

	/** Log de Opera��es para usu�rios logados. */
	private Collection<LogOperacao> operacoes;

	/** Senha atual do usu�rio. */
	private String senha;
	
	/** Nova senha do usu�rio. */
	private String novaSenha;

	/** Redigita��o da nova senha do usu�rio. */
	private String reNovaSenha;

	/** Data/hora do login.*/
	private Date data;

	/** Motivo de uso do logar como */
	private String motivo;
	
	/** Construtor padr�o. */
	public UsuarioMBean() {
		obj = new Usuario();
	}

	/**
	 * Busca os usu�rios.<br/>M�todo n�o invocado por JSP�s.
	 * @throws DAOException
	 */
	public void preencheUsuariosEntrada() throws DAOException {
		UsuarioDao userDao = getDAO(UsuarioDao.class);
		for (RegistroEntrada registro : entradas) {
			registro.setUsuario(userDao.findUsuarioLeve(registro.getUsuario()
					.getId()));
		}
	}

	/** Retorna os dados do usu�rio logado.
	 * @return
	 */
	public String getDadosUsuario() {
		obj = getUsuarioLogado();
		return "";
	}

	/**
	 * Visualiza as opera��es de um registro de entrada<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Busca por registro de entradas em um determinado usu�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

	/** Verifica os pap�is: ADMINSTRADOR_SIGAA
	 * @see br.ufrn.sigaa.arq.jsf.SigaaAbstractController#getCheckRole()
	 */
	@Override
	public String getCheckRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_SIGAA);
		return "";
	}

	/**
	 * Busca o usu�rio por login
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @param evt
	 * @throws DAOException
	 */
	public void buscaPorLogin(ActionEvent evt) throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		obj = (Usuario) dao.findByLogin(obj.getLogin());
		if (obj != null)
			achou = true;
	}

	/** Indica se achou o login do usu�rio. 
	 * @return
	 */
	public boolean isAchou() {
		return achou;
	}

	/** Seta se achou o login do usu�rio. 
	 * @param achou
	 */
	public void setAchou(boolean achou) {
		this.achou = achou;
	}

	/**
	 * Retornar ao usu�rio anterior
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Alterar os dados do usu�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErro("A senha digitada e a confirma��o dela n�o conferem");
			}
		}

		if (senha != null && !senha.equals("") ) {
			if (!senha.equals((obj).getSenha())) {
				addMensagemErro("Senha do Usu�rio n�o confere");
			}
		}

		ValidatorUtil.validateEmail(email, "E-Mail do Usu�rio", erros);

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

	/** Inicia a opera��o de logar como outro usu�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

	/** Inicia a opera��o de atribui��o de permiss�es.
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarPermissao() throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.ADMINISTRADOR_SIGAA, SigaaPapeis.ADMINISTRADOR_DAE,
				SigaaPapeis.ADMINISTRADOR_STRICTO  } );
		return forward("/administracao/papeis/buscar.jsp");
	}

	/**
	 * Loga como outro usu�rio
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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

		// Se n�o encontrar por login, buscar o usu�rio do discente com a matricula informada
		if (obj == null ) {
			try {
				long matricula = Long.parseLong(login);
				obj = dao.findByMatriculaDiscente(matricula);
			} catch (Exception e) {}
		}

		if (obj == null) {
			addMensagemErro("Usu�rio n�o encontrado");
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
									"Caro Usu�rio, n�o foi poss�vel determinar o mapeamento entre a sua " +
									"lota��o oficial de Recursos Humanos e a unidade do SIGAA");
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
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 * @throws ArqException 
	 */
	public String logonMobile() throws ArqException {

		if ( obj.getLogin() == null || obj.getSenha() == null ) {
			addMensagemErro("Usu�rio ou senha vazios");
		} else {

			boolean autenticado = UserAutenticacao.autenticaUsuario(getCurrentRequest(), obj.getLogin(), obj.getSenha());
			if ( !autenticado ) {

				addMensagemErro("Usu�rio/Senha inv�lidos");

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

	/** Redireciona o usu�rio para o menu principal do sistema.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	
	/** Retorna os registros de entradas usado nos usu�rios logados. 
	 * @return
	 */
	public Collection<RegistroEntrada> getEntradas() {
		return entradas;
	}

	/** Seta os registros de entradas usado nos usu�rios logados.
	 * @param entradas
	 */
	public void setEntradas(Collection<RegistroEntrada> entradas) {
		this.entradas = entradas;
	}

	/** Retorna o tempo usado nos usu�rios logados. 
	 * @return
	 */
	public int getTempo() {
		return tempo;
	}

	/**Seta o tempo usado nos usu�rios logados. 
	 * @param tempo
	 */
	public void setTempo(int tempo) {
		this.tempo = tempo;
	}

	/** Retorna o log de Opera��es para usu�rios logados. 
	 * @return
	 */
	public Collection<LogOperacao> getOperacoes() {
		return operacoes;
	}

	/** Seta o log de Opera��es para usu�rios logados.
	 * @param operacoes
	 */
	public void setOperacoes(Collection<LogOperacao> operacoes) {
		this.operacoes = operacoes;
	}

	/** Retorna a nova senha do usu�rio. 
	 * @return
	 */
	public String getNovaSenha() {
		return novaSenha;
	}

	/** Seta a nova senha do usu�rio.
	 * @param novaSenha
	 */
	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}

	/** Retorna a redigita��o da nova senha do usu�rio.
	 * @return
	 */
	public String getReNovaSenha() {
		return reNovaSenha;
	}

	/** Seta a redigita��o da nova senha do usu�rio. 
	 * @param reNovaSenha
	 */
	public void setReNovaSenha(String reNovaSenha) {
		this.reNovaSenha = reNovaSenha;
	}

	/** Retorna a senha atual do usu�rio.
	 * @return
	 */
	public String getSenha() {
		return senha;
	}

	/** Seta a senha atual do usu�rio. 
	 * @param senha
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/** Retorna o tempo de atividade usado nos usu�rios logados.
	 * @return
	 */
	public int getTempoAtividade() {
		return tempoAtividade;
	}

	/** Seta o tempo de atividade usado nos usu�rios logados. 
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