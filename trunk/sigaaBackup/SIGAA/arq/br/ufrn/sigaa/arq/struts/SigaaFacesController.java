package br.ufrn.sigaa.arq.struts;


/**
 * Controlador JSF do SIGAA
 *
 * @author Gleydson
 *
 */
public class SigaaFacesController {
/*
	// extends MyFacesServlet 
	@Override
	public void service(ServletRequest req, ServletResponse res)
			throws IOException, ServletException {

		HttpServletRequest request = (HttpServletRequest) req;
		String reqUrl = request.getRequestURL().toString();

		int pos = 7; // posição do início da URL (http://)
		if (req.isSecure()) {
			pos = 8;
		}

		if (request.getSession().getAttribute("usuario") == null
				&& (reqUrl
						.indexOf(request.getHeader("Host") + "/sigaa/public/") != pos)
				&& !reqUrl
						.endsWith("/portais/docente/menu_docente_externo.jsf")
				&& !reqUrl
						.endsWith("/portais/discente/menu_discente_externo.jsf")) {
			HttpServletResponse response = (HttpServletResponse) res;
			response.sendRedirect("/sigaa/");
			return;
		}

		long initOperacao = System.currentTimeMillis();

		Usuario user = (Usuario) request.getSession(true).getAttribute(
				"usuario");

		if (req.getParameter("aba") != null) {
			request.getSession().setAttribute("aba", req.getParameter("aba"));
		}

		// usado no DAO para decidir qual SessionFactory utilizar
		req.setAttribute("sistema", Sistema.SIGAA);
		try {

			// Verifica padrões de URL protegidas
			URLCheckAccess.processRequest(request);

			super.service(req, res);

		} catch (Exception e) {
			e.printStackTrace();
			if (e.getCause() != null) {
				e.getCause().printStackTrace();
			}

			Exception realException = null;

			try {
				realException = ErrorUtils
						.traceFacesException((ServletException) e);

				realException.printStackTrace();

				if (realException instanceof SegurancaException) {

					req.getRequestDispatcher(
							"/WEB-INF/jsp/include/erros/autorizacao.jsp")
							.forward(req, res);
					return;
				} else if (realException instanceof NegocioException) {
					req.setAttribute("erro", realException);
					req.getRequestDispatcher(
							"/WEB-INF/jsp/include/erros/negocio.jsp").forward(
							req, res);
					return;
				}

			} catch (Exception er) {
				er.printStackTrace();
			}

			if (user != null && user.getRegistroEntrada() != null) {
				try {
					if (req.getAttribute("NO_LOGGER") == null) {
						UserAgent.logaOperacaoErro(request.getRequestURL()
								.toString(), System.currentTimeMillis()
								- initOperacao, user.getRegistroEntrada()
								.getId(), ErrorUtils
								.parametrosToString(request), e, Sistema.SIGAA);
					}
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}

			try {
				boolean enviarEmail = new Boolean(ParametroHelper.getInstance()
						.getParametro(ConstantesParametroGeral.EMAIL_DE_ALERTA)
						.trim());
				if (enviarEmail) {
					String email = ParametroHelper.getInstance().getParametro(
							ConstantesParametroGeral.EMAIL_ALERTA_ERRO).trim();
					if (realException != null) {
						e = realException;
					}
					ErrorUtils.enviaAlerta(e, request, email, "Erro SIGAA", e
							.getMessage(), Sistema.SIGAA);

				}
			} catch (Exception e2) {
				e2.printStackTrace();
			}

			MensagemAviso msg = new MensagemAviso(
					"Ocorreu um erro inesperado. Este erro foi encaminhado automaticamente para Diretoria de Sistemas para solucioná-lo. Pedimos desculpa pelo transtorno.",
					TipoMensagemUFRN.ERROR);
			ListaMensagens erros = new ListaMensagens();
			erros.addMensagem(msg);
			req.setAttribute(ListaMensagens.LISTA_MENSAGEM_SESSION, erros);
			req.setAttribute("erro", realException);
			req.getRequestDispatcher("/WEB-INF/jsp/include/erros/geral.jsp")
					.forward(req, res);

		} finally {

			Session session = (Session) req
					.getAttribute(Database.SESSION_ATRIBUTE);
			if (session != null && session.isOpen()) {
				session.clear();
				session.close();
			}
		}
	}
*/
}
