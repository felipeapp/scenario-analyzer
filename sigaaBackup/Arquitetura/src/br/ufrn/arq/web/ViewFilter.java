/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 22/01/2008
 */
package br.ufrn.arq.web;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.StringTokenizer;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.struts.Globals;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.hibernate.Session;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.RegistroAcessoPublicoDao;
import br.ufrn.arq.dao.ThreadScopedResourceCache;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.RegistroAcessoPublico;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.email.MailThread;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ErrorConsumer;
import br.ufrn.arq.erros.ErrorProcessorDelegate;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.UFRNException;
import br.ufrn.arq.erros.gerencia.MovimentoErro;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.autenticacao.AutenticacaoUtil;
import br.ufrn.arq.seguranca.log.SessionLogger;
import br.ufrn.arq.seguranca.log.UserAgent;
import br.ufrn.arq.tasks.CancelamentoTarefasScheduler;
import br.ufrn.arq.tasks.TarefaScheduler;
import br.ufrn.arq.usuarios.UserOnlineMonitor;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.AvisoHelper;
import br.ufrn.arq.util.ErrorUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.web.exceptions.ExceptionHandler;
import br.ufrn.comum.dao.SistemaDao;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Filtro que verifica se os usuários estão logados para ter acesso aos recursos
 * da aplicação. Também aqui é feito o Log de visualização.
 * 
 * @author Gleydson Lima
 * @author David Pereira
 */
public class ViewFilter implements Filter {

	/**
	 * Sistema que está fazendo a request
	 */
	private int sistema;

	/**
	 * URLs permitidas sem necessitar de controle de acesso
	 */
	private List<String> urlPermitidas;
	
	/**
	 * Extensões que serão desconsideradas pelo view filter, de modo
	 * que quando uma URL com o final existente na lista passar pelo
	 * filtro, ele irá simplesmente ignorar a URL. Utilizado para imagens,
	 * CSS, Javascript, etc.
	 */
	private List<String> extensoesDescartadas;

	/**
	 * Classe para tratamento de exceções.
	 */
	private ExceptionHandler exHandler;

	/**
	 * Endereço para lookup do EJB no JNDI 
	 */
	private String jndiName;
	
	/**
	 * scheduler de tarefas da arquitetura
	 */
	static TarefaScheduler tarefaScheduler;
	
	/**
	 * scheduler de tarefas da arquitetura
	 */
	static MailThread mailThread;
	
	/** Thread que registra os erros que acontecem nos sistemas no banco de dados  */
	static ErrorConsumer errorThread;
	
	/** Número máquina de requisições concorrentes para se precaver contra ataques de DOS */
	private int numMaximoRequisicoesConcorrentes;
	
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;

		// Verifica se a URL deve ou não ser processada pelo view filter de acordo com sua extensão
		if (extensaoDesconsiderada(request)) {
			chain.doFilter(req, res);
			return;
		}
		
		long initOperacao = System.currentTimeMillis();
		
		String requestId = System.currentTimeMillis() + req.toString();

		@SuppressWarnings("unchecked")
		List<String> lista = (List<String>) request.getSession().getAttribute("responses_waiting");
		
		if (lista == null) {
			lista = new ArrayList<String>();
			request.getSession().setAttribute("responses_waiting", lista);
		}
		
		if (!isAjaxRequest(request, response)) {
			lista.add(requestId);
		}

		if ( lista.size() >  numMaximoRequisicoesConcorrentes ) {
			lista.remove(requestId);
			// redireciona para página dizendo que foi detectado um excesso de requisições e que isso pode representar um problema de segurança
			req.getRequestDispatcher("/WEB-INF/jsp/include/erros/excesso_requisicoes.jsp").forward(req, res);
			return;
		}
		
		String reqUrl = request.getRequestURI();
		
		//Indica se o acesso está sendo feito pela área pública.
		//Pelo portal público, o usuário não precisa estar autenticado no sistema
		boolean areaPublica = false;
		if (reqUrl.startsWith("/public/") || reqUrl.contains("/link/public/") || reqUrl.contains("/remoting/") || reqUrl.contains("/services/")) //Teste considerando sipac
			areaPublica = true;
		
		//Remove o contexto
		reqUrl = reqUrl.substring(request.getContextPath().length());

		UsuarioGeral user = (UsuarioGeral) request.getSession(true).getAttribute("usuario");

		if (user != null && user.getRegistroEntrada() != null) {
			if (RegistroEntrada.CANAL_WAP.equals(user.getRegistroEntrada().getCanal())) {
				if (reqUrl.indexOf("/mobile/") == -1) {
					lista.remove(requestId);
					response.sendRedirect(RepositorioDadosInstitucionais.get("enderecoWap"));
					return;
				}
			}
		}
		
		if (user == null) {
			if (reqUrl.startsWith("/public/") || reqUrl.startsWith("/link/public/") || reqUrl.startsWith("/javascript/") || reqUrl.startsWith("/mobile/touch/public/")) //Teste considerando sigrh, sigaa
				areaPublica = true;
			
			if (!areaPublica) {//Permite acesso a páginas públicas sem estar logado no sistema
				
				//Acesso não vem da área pública
				boolean permitido = false;
				
				// verifica se nao eh um JS do RichFaces
				// http://localhost/sigrh/a4j_3_2_0-SNAPSHOTorg/richfaces/renderkit/html/scripts/modalPanel.js.jsf
				if ( reqUrl.startsWith("/a4j") && reqUrl.endsWith(".jsf") ) {
					permitido = true;
				}
				
				if (urlPermitidas.contains(reqUrl) || request.getSession().getAttribute(AutenticacaoUtil.TOKEN_REDIRECT) != null) {
					//Verifica URLs permitdas de acesso sem estar logado, no caso de não estar no diretório /public.
					//Estar URLs estão mapeadas no web.xml
					request.getSession().removeAttribute(AutenticacaoUtil.TOKEN_REDIRECT);
					permitido = true;
				}
				
				if (!permitido) {
					lista.remove(requestId);
					
					if (reqUrl.startsWith("/rest/")) {
						req.setAttribute("sistema", sistema);
						
						try {
							chain.doFilter(req, res);
						} finally {
							closeResources(req);
						}
					}
					else if (reqUrl.startsWith("/mobile/touch")) {
						response.sendRedirect(request.getContextPath() + "/mobile/touch/login.jsf?expirada=true");
					} else if (reqUrl.startsWith("/mobile")) {
						request.setAttribute("mensagem", "Sua sessão foi expirada. É necessário  autenticar-se novamente!");
						response.sendRedirect(request.getContextPath() + "/mobile/login.jsp");
					} else {
						response.sendRedirect(request.getContextPath() + "/expirada.jsp");
					}
					
					return;
				} else if(reqUrl.startsWith("/documentoEletronico")) {
					lista.remove(requestId);
					return;
				}
			} else {
				if (!Sistema.isPortalPublicoAtivo(sistema) && !reqUrl.contains("/services/")) {
					if (sistema == Sistema.SIPAC) {
						response.sendRedirect("/sipac");
						return;
					} else if ( sistema == Sistema.SIGADMIN ){
						// O SIGAdmin não tem portal público
					} else {
						response.sendRedirect(request.getContextPath());
						return;
					}
				}
			}
		} else {
			UserOnlineMonitor.updateUser(user.getLogin(), user.getRegistroEntrada() == null ? 0 : user.getRegistroEntrada().getId(), sistema);
		}

		// seta a aba corrente do menu
		if (req.getParameter("aba") != null && !req.getParameter("aba").equals(""))
			request.getSession(true).setAttribute("aba", req.getParameter("aba"));
		// Setando subAba dentro de uma aba corrente do menu
		if (req.getParameter("subAba") != null && !req.getParameter("subAba").equals(""))
			request.getSession(true).setAttribute("subAba", req.getParameter("subAba"));

		//Seta o sistema em request
		req.setAttribute("sistema", sistema);

		AvisoHelper.exportaMensagensAvisoParaRequest(sistema, request);
		
		try {
			//Passa a responsabilidade para o item requisitado
			if (AmbienteUtils.isSistemaEmManutencao(sistema)) {
				res.setContentType("text/html");
				res.getWriter().write(AmbienteUtils.getTelaManutencao(sistema));
				return;
			} else if (!Sistema.isSistemaAtivo(sistema)) {
				res.setContentType("text/html");
				res.getWriter().write("Sistema desabilitado");
				return;
			} else {
				
				if (request.getParameter("download-excel") != null) {
					response.setContentType("application/ms-excel");
					String filename = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
					FilenameUtils.removeExtension(filename);
					response.setHeader("Content-disposition", "attachment; filename=\"" + filename + ".xls\"");
				} else if (request.getParameter("download-word") != null) {
					response.setContentType("application/ms-word");
					String filename = request.getRequestURI().substring(request.getRequestURI().lastIndexOf("/")+1);
					FilenameUtils.removeExtension(filename);
					response.setHeader("Content-disposition", "attachment; filename=\"" + filename + ".doc\"");
				}
				
				chain.doFilter(req, res);
			}
		} catch (Exception e) {
			exHandler.handleException(e, request, response, sistema, initOperacao);
		} finally {
			if ( lista != null )
				lista.remove(requestId);
			
			HttpSession httpSession = request.getSession(false);
			
			if (httpSession != null && user != null && user.getRegistroEntrada() != null) {
				try {
					if (req.getAttribute("NO_LOGGER") == null) {
						String mensagens = null;
						try {
							mensagens = carregaMensagens(request);
						} catch (Exception e ) {
							e.printStackTrace();
						}
						UserAgent.logaOperacao(request.getRequestURL().toString(), System.currentTimeMillis() - initOperacao, 
								user.getRegistroEntrada().getId(), ErrorUtils.parametrosToString(request), sistema, isEmpty(mensagens) ? null : mensagens);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			} else if  ( areaPublica ) {
				
				RegistroAcessoPublico registro = null;

				try {
					registro = createRegistroAcessoPublico(request);
					UserAgent.logaOperacaoPublica(request.getRequestURL().toString(), System.currentTimeMillis() - initOperacao, registro.getId(),
							ErrorUtils.parametrosToString(request), sistema);
				} catch (UFRNException e) {
					e.printStackTrace();
				}
								
			}
			
			closeResources(req);

		}

	}

	private void closeResources(ServletRequest req) {
		@SuppressWarnings("unchecked")
		Map<Integer, Session> sessions = (Map<Integer, Session>) req.getAttribute(Database.SESSION_ATRIBUTE);
		if (!isEmpty(sessions)) {
			for (Entry<Integer, Session> entry : sessions.entrySet()) {
				Session session = entry.getValue();
				if (session != null && session.isOpen()) {
					session.clear();
					session.close();
				
					SessionLogger.log(session, 'C');
				}
			}
			
			System.out.println("Sessão Fechada no ViewFilter");
		}
		

		ThreadScopedResourceCache.closeResources();
	}

	/*
	 * Verifica na URL acessada se ela possui extensão dentro da lista
	 * de extensões descartadas para que o View Filter a ignore.
	 */
	private boolean extensaoDesconsiderada(HttpServletRequest req) {
		String url = req.getRequestURI();
		for (String extensao : extensoesDescartadas) {
			if (url.endsWith(extensao))
				return true;
		}
		
		return false;
	}

	/*
	 * Carrega as mensagens armazenadas na lista de mensagens em sessão
	 * para passá-las ao LogOperacao.
	 */
	@SuppressWarnings("unchecked")
	private String carregaMensagens(HttpServletRequest request) {
		
		StringBuilder sb = new StringBuilder();
		
		if (request.getSession().getAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION) != null) {
			ListaMensagens lista = (ListaMensagens) request.getSession().getAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION);
			if (!isEmpty(lista.getMensagens())) {
				for (MensagemAviso ma : lista.getMensagens()) {
					sb.append(ma.getMensagem() + "<br />");
				}
			}
		}
		
		if (request.getAttribute(Globals.ERROR_KEY) != null) {
			ActionMessages erros = (ActionMessages) request.getAttribute(Globals.ERROR_KEY);
			if (!erros.isEmpty()) {
				for (Iterator<ActionMessage> it = erros.get(); it.hasNext(); ) {
					ActionMessage message = it.next();
					if (message.getValues() != null)
						sb.append(message.getValues()[0] + "<br />");
				}
			}
		}
		
		if (request.getAttribute(Globals.MESSAGE_KEY) != null) {
			ActionMessages erros = (ActionMessages) request.getAttribute(Globals.MESSAGE_KEY);
			if (!erros.isEmpty()) {
				for (Iterator<ActionMessage> it = erros.get(); it.hasNext(); ) {
					ActionMessage message = it.next();
					sb.append(message.getKey() + "<br />");
				}
			}
		}
		
		request.getSession().removeAttribute(ListaMensagens.LISTA_TEMPORARIA_SESSION);
		return sb.toString();
	}

	/*
	 * Verifica se a requisição é uma requisição do tipo Ajax, para evitar que certas
	 * operações (como a verificação do número de requests simultâneos) sejam executadas.
	 */
	private boolean isAjaxRequest(HttpServletRequest request, HttpServletResponse response) {
		String uri = request.getRequestURI();
		
		return "XmlHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) 
					|| request.getParameter("AJAXREQUEST") != null
					|| uri.indexOf("a4j_") != -1
					|| uri.indexOf("richfaces") != -1
					|| uri.indexOf("verArquivo") != -1
					|| uri.indexOf("verFoto") != -1;
	}

	/*
	 * Cria um registro de acesso público em sessão para o usuário que está
	 * acessando a área pública dos sistemas sem se logar.
	 */
	private RegistroAcessoPublico createRegistroAcessoPublico(HttpServletRequest request) throws NegocioException, ArqException {
		RegistroAcessoPublico regSession = (RegistroAcessoPublico) request.getSession().getAttribute("REGISTRO_ACESSO_PUBLICO");
		
		if (regSession == null) {
			regSession = new RegistroAcessoPublico();
			regSession.setIP(request.getRemoteAddr());
			regSession.setUserAgent(request.getHeader("User-Agent"));
			regSession.setServer(NetworkUtils.getLocalName());
			regSession.setCanal(RegistroEntrada.CANAL_WEB);
			regSession.setData(new Date());
			regSession.setSistema(sistema);
			
			RegistroAcessoPublicoDao dao = new RegistroAcessoPublicoDao();
			dao.inserir(regSession);
			
			request.getSession().setAttribute("REGISTRO_ACESSO_PUBLICO", regSession);
		}
		
		return regSession;
	}

	/**
	 * Realiza configuração do filtro, populando seus atributos
	 * com parâmetros de inicialização.
	 */
	public void init(FilterConfig config) throws ServletException {
		jndiName = config.getInitParameter("jndiName");
		config.getServletContext().setAttribute("jndiName", jndiName);
		sistema = Integer.parseInt(config.getInitParameter("sistema"));

		exHandler = new ExceptionHandler();
		exHandler.config();
		
		String paginasPermitidas = config.getInitParameter("paginasPermitidas");
		if (paginasPermitidas != null) {
			StringTokenizer st = new StringTokenizer(paginasPermitidas, ",");
			urlPermitidas = new ArrayList<String>(st.countTokens());
			while (st.hasMoreTokens()) {
				urlPermitidas.add(st.nextToken());
			}
		}
		
		urlPermitidas.add("/");

		String extensoesIgnoradas = config.getInitParameter("extensoesIgnoradas");
		if (extensoesIgnoradas != null) {
			StringTokenizer st = new StringTokenizer(extensoesIgnoradas, ",");
			extensoesDescartadas = new ArrayList<String>(st.countTokens());
			while (st.hasMoreTokens()) {
				extensoesDescartadas.add(st.nextToken());
			}
		}		
		
		Map<String, String> configs = RepositorioDadosInstitucionais.getAll();
		config.getServletContext().setAttribute("configSistema", configs);
		config.getServletContext().setAttribute("sistema", sistema);

		if ( tarefaScheduler == null ) {
			FacadeDelegate facadeScheduler = new FacadeDelegate(jndiName);
			ApplicationContext ac = WebApplicationContextUtils.getWebApplicationContext(config.getServletContext());
			
			try {
				tarefaScheduler = (TarefaScheduler) ac.getBean("tarefaScheduler");
				tarefaScheduler.setFacade(facadeScheduler);
				tarefaScheduler.start();
			
				new CancelamentoTarefasScheduler().start();
			} catch(NoSuchBeanDefinitionException e) { 
				// Para casos em que não é necessária a definição do TarefaScheduler, como a parte pública do SIPAC
			}
		}
		
		if ( mailThread == null ) {
			FacadeDelegate facadeMail = new FacadeDelegate(jndiName);
			mailThread = new MailThread(facadeMail);
			mailThread.start();
		}
		
		if ( errorThread == null ) {
			Queue<MovimentoErro> erros = new LinkedList<MovimentoErro>();
			FacadeDelegate facadeErrors = new FacadeDelegate(jndiName);
			
			ErrorProcessorDelegate.getInstance().setErros(erros);
			
			errorThread = new ErrorConsumer(facadeErrors, erros);
			errorThread.start();
		}
		
		numMaximoRequisicoesConcorrentes = ParametroHelper.getInstance().getParametroInt(ConstantesParametroGeral.NUMERO_MAXIMO_REQUISICOES_CONCORRENTES);

		SistemaDao dao = new SistemaDao();
		
		try {
			Sistema.setSistemasAtivos(dao.findMapaSistemasAtivos());
			Sistema.setCaixaPostalAtiva(dao.findMapaCaixaPostalAtiva());
			Sistema.setMemorandosAtivos(dao.findMapaMemorandosAtivos());
			Sistema.setQuestionariosAtivos(dao.findMapaQuestionariosAtivos());
			Sistema.setPortalPublicoAtivo(dao.findMapaPortalPublicoAtivo());
		} finally {
			dao.close();
		}
	}

	public void destroy() {
		DAOFactory.getInstance().closeSessionsFactory();
	}
}
