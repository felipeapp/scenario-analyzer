/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 22/12/2006
 */
package br.ufrn.arq.util;

import java.sql.BatchUpdateException;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Map;
import java.util.Set;

import javax.faces.FacesException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.DataException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.dominio.AlteracaoRegistroEntrada;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConstantesErro;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.ErrorProcessorDelegate;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.erros.UFRNException;
import br.ufrn.arq.erros.gerencia.MovimentoErro;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Método utilizados para notificar erros
 *
 * @author Gleydson Lima
 *
 */
public class ErrorUtils {

	/**
	 * Converte os parametros de uma requisição em uma string
	 *
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static String parametrosToString(HttpServletRequest req) {
		StringBuilder s = new StringBuilder(400);

		try {
			Set<Map.Entry<String, String[]>> parametersEntrySet = req.getParameterMap().entrySet();
			for (Map.Entry<String, String[]> entry : parametersEntrySet) {
				// Nao logar os parametros de view de JSF
				if (!entry.getKey().toString().startsWith("jsf_state") && !entry.getKey().toString().startsWith("jsf_tree") && entry.getKey().toString().indexOf("senha") == -1) {
					s.append("PARAM: ");
					s.append(entry.getKey());
					s.append(" ");
					s.append(" : ");
					s.append(Arrays.toString(entry.getValue()));
					s.append("\n");
				}
			}
		} catch (Exception e) {
			s.append("\n\n !!!! ERRO AO OBTER PARAMETROS!!!!! \n\n");
			s.append(e + "\n");
			s.append("STACK: " + Arrays.toString(e.getStackTrace()));
		}

		return s.toString();
	}

	public static String atributosToString(HttpServletRequest req) {
		StringBuilder s = new StringBuilder(1000);

		Enumeration<?> reqAttrbNames = req.getAttributeNames();
		while (reqAttrbNames.hasMoreElements()) {
			String attrName = (String) reqAttrbNames.nextElement();
			s.append(attrName);
			s.append(" [");
			try {
				s.append(String.valueOf(req.getAttribute(attrName)));
			} catch (Exception e) {
				s.append("Erro ao obter valor de atributo:" + e);
			}
			s.append("]\n");
		}

		return s.toString();
	}

	public static String atributosToString(HttpSession session) {
		StringBuilder s = new StringBuilder(1000);

		Enumeration<?> reqAttrbNames = session.getAttributeNames();
		while (reqAttrbNames.hasMoreElements()) {
			String attrName = (String) reqAttrbNames.nextElement();
			s.append(attrName);
			s.append(" [");
			try {
				s.append(String.valueOf(session.getAttribute(attrName)));
			} catch (Exception e) {
				s.append("Erro ao obter valor de atributo:" + e);
			}
			s.append("]\n");
		}

		return s.toString();
	}

	public static void enviaAlerta(Throwable e, String jndiName, String loginUsuario, String nomeUsuario, int sistema, int subsistema, String serverName) {
		enviaAlerta(e, null, jndiName, loginUsuario, nomeUsuario, sistema, subsistema, serverName);
	}
	
	public static void enviaAlerta(Throwable e, String jndiName, int sistema, int subsistema, String serverName) {
		enviaAlerta(e, null, jndiName, null, null, sistema, subsistema, serverName);
	}
	
	public static void enviaAlerta(Throwable e, HttpServletRequest req) {
		enviaAlerta(e, req, req.getSession().getServletContext().getAttribute("jndiName").toString(), null, null, null, null, null);
	}
	
	public static void enviaAlerta(Throwable e, HttpServletRequest req, String jndiName, String loginUsuario, String nomeUsuario, Integer sistema, Integer subsistema, String serverName) {
		
		boolean enviarEmail = br.ufrn.arq.parametrizacao.ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.EMAIL_DE_ALERTA);
		
		HttpSession session = null;
		UsuarioGeral usuarioLogado = null;
		if( req != null ){
			sistema = new Integer( req.getAttribute("sistema").toString() );
			subsistema = AbstractAction.getSubSistemaAtual(req);
			serverName = req.getServerName();
			
			session = req.getSession();
			usuarioLogado = (UsuarioGeral) session.getAttribute("usuario");
		}
		
		String email = br.ufrn.arq.parametrizacao.ParametroHelper.getInstance().getParametro(sistema+"_1_1");

		Throwable cause = null;
		if (e.getCause() != null)
			cause = e.getCause();
		else
			cause = e;

		if (e instanceof ServletException)
			cause = ((ServletException) e).getRootCause();

		if (cause instanceof ServletException)
			cause = ((ServletException) cause).getRootCause();

		if (cause instanceof UFRNException) {
			if (!((UFRNException) cause).isNotificavel()) {
				return;
			}
		}
		
		/**
		 * Exceções de erro do Hibernate.. vem encapsuladas em um DataException
		 */
		if ( cause instanceof DataException ) {
			cause = ((DataException) cause).getSQLException();
		}
		
		/**
		 * Exceções de violacão vem como ContraintViolationException
		 */
		if ( cause instanceof ConstraintViolationException ) {
			cause = ((ConstraintViolationException) cause).getSQLException();
		}
		
		/**
		 * Exceções de violacão vem como ContraintViolationException
		 */
		if ( cause instanceof BatchUpdateException ) {
			cause = ((BatchUpdateException) cause).getNextException();
		}

		if( req != null ){
			/* NeogocioException e SegurancaException não são notificados */
			if (cause instanceof NegocioException || e instanceof NegocioException
					|| e instanceof SegurancaException
					|| cause instanceof SegurancaException || session == null)
				return;
		}

		// Solicitação já processada não deve ser notificada
		if ((e instanceof ArqException && ((ArqException) e).getCodErro() == ConstantesErro.SOLICITACAO_JA_PROCESSADA)
				|| (cause != null && cause instanceof ArqException && ((ArqException) cause)
						.getCodErro() == ConstantesErro.SOLICITACAO_JA_PROCESSADA))
			return;

		//if (session.getAttribute("usuario") == null)
		//	return;

		

		String descSubsistema = "";
		String descSistema = "";
		if (req != null && req.getRequestURI().contains("/public/")) {
			descSubsistema = "Portal Público";
			if (sistema == Sistema.SIGAA)
				descSistema = "SIGAA";
			else
				descSistema = "SIPAC";
		} else {
			GenericDAO dao = new GenericDAOImpl(Sistema.COMUM);
			
			try {
				SubSistema ss = dao.findByPrimaryKey(subsistema, SubSistema.class);
				if (ss != null)
					descSubsistema = ss.getNome();
				
				if (sistema == Sistema.SIGAA) {
					descSistema = "SIGAA";
				} else {
					descSistema = "SIPAC";
				}
			} catch (DAOException ex) {

			} finally {
				dao.close();
			}
		}

		StringBuilder s = new StringBuilder(8000);

		s.append("Server name: ");
		s.append(serverName);
		s.append("\n\n");
		s.append("Servidor do cluster: ");
		s.append(NetworkUtils.getLocalName());
		s.append("\n\n");
		// Informações da host remoto
		if( req != null ){
			s.append("Endereço remoto:");
			s.append(req.getRemoteAddr());
			s.append("\n");
			s.append("Host remoto: ");
			s.append(req.getRemoteHost());
			s.append("\n");
			s.append("URL: ");
			s.append(req.getRequestURL() + "?" + req.getQueryString());
		}
		s.append("\n");
		s.append("Hora: ");
		s.append(new Date());

		s.append("\n\n");
		s.append("=== INFORMAÇÕES DO USUARIO LOGADO === ");
		s.append("\n");

		

		if (usuarioLogado != null && session != null) {
			s.append("USUARIO: ");
			AlteracaoRegistroEntrada alteracao = (AlteracaoRegistroEntrada) session
					.getAttribute("alteracaoRegistroEntrada");
			if (alteracao != null && alteracao.getRegistroEntrada() != null
					&& alteracao.getRegistroEntrada().getUsuario() != null) {
				s.append(alteracao.getRegistroEntrada().getUsuario().getLogin()
						+ " logado como ");
			}
			s.append(usuarioLogado.getId() + " - " + usuarioLogado.getLogin());
			s.append("\n");
			s.append("NOME: " + usuarioLogado.getPessoa().getNome());
			s.append("\n");

			if(!ValidatorUtil.isEmpty(usuarioLogado.getRegistroEntrada()))
			s.append("REGISTRO ENTRADA: "
					+ usuarioLogado.getRegistroEntrada().getId() + "\n");

			// Escreve comandos bloqueados
			if (usuarioLogado.getComandosBloqueados() != null) {
				s.append("COMANDOS BLOQUEADOS: ");
				s.append("\n");
				for (Comando cmd : usuarioLogado.getComandosBloqueados()) {
					s.append(cmd.getId());
					s.append(",");
				}
				s.append("\n");
			}

			if( req != null ){
				//comando que estava sendo executando na hora da excecao
				s.append("COMANDO LIBERADO: " + req.getSession().getAttribute("lastCommand") + "\n");
			}

			// Subsistema que o usuário estava utilizando na hora da exceção
			s.append("SUBSISTEMA: ");
			s.append(descSubsistema);
			s.append("\n\n");

			// Escreve dados do registro de entrada
			RegistroEntrada registroEntrada = usuarioLogado
					.getRegistroEntrada();
			if (registroEntrada != null) {
				s.append("===REGISTRO DE ENTRADA=== ");
				s.append("\n");
				s.append(registroEntrada.getId());
				s.append("\n");
				s.append("DATA: ");
				s.append(registroEntrada.getData());
				s.append("\n");
				s.append("IP: ");
				s.append(registroEntrada.getIP());
			}
		} else {
			if (loginUsuario == null && (req == null || req.getRequestURI().contains("/public/"))) {
				s.append("\n");
				s.append("Nenhum usuário logado. Requisição originada da área pública.");
				s.append("\n");
			} else if (loginUsuario != null) {
				s.append("USUARIO: " + loginUsuario);
				s.append("\n");
				s.append("NOME: " + nomeUsuario);
				s.append("\n");
			} else {				
				return;
			}
		}

		/*
		 * s.append("\n\n"); s.append("===HEADERS=== "); s.append("\n");
		 * Enumeration headersNames = req.getHeaderNames(); while
		 * (headersNames.hasMoreElements()) { String name = (String)
		 * headersNames.nextElement(); s.append(name); s.append(": ");
		 * s.append(req.getHeader(name)); s.append("\n"); }
		 *
		 *
		 * s.append("\n\n"); s.append("===ATRIBUTOS DA REQUISISIÇÃO=== ");
		 * s.append("\n"); s.append(atributosToString(req));
		 *
		 * s.append("\n\n"); s.append("===ATRIBUTOS DA SESSÃO=== ");
		 * s.append("\n"); s.append(atributosToString(session));
		 */

		// Detalhes da exceção
		s.append("\n\n");
		s.append("===DADOS DA EXCEÇÃO DISPARADA=== ");
		s.append("\n");
		s.append("Exceção: ");
		s.append(e.toString());
		s.append("\n\n");

		/*
		 * if (e.getCause() == null) { s.append("STACK TRACE:"); s.append("\n");
		 * s.append(Arrays.toString(e.getStackTrace())); s.append("\n\n"); }
		 */

		// Detalhes da causa da exceção
		// cause = e.getCause();
		while (cause != null) {
			s.append("Cause: ");
			s.append(String.valueOf(cause) + "\n");
			s.append("CAUSE STACK TRACE:");
			s.append("\n");
			String trace = Arrays.toString(cause.getStackTrace());
			trace = trace.replace(",", "\n");
			s.append(trace);
			s.append("\n\n");

			cause = cause.getCause();
		}

		// Se for um servlet exception, imprime detalhes do root cause
		if (e instanceof ServletException) {
			ServletException sE = (ServletException) e;
			if (sE.getRootCause() != null) {
				s.append("Root cause: ");
				s.append(String.valueOf(sE.getRootCause()));
				s.append("\n\n");
				s.append("ROOT CAUSE STACK TRACE:");
				s.append("\n");
				String trace = Arrays.toString(sE.getRootCause()
						.getStackTrace());
				trace = trace.replace(",", "\n");
				s.append(trace);
				s.append("\n\n");
			}
		} else if (e instanceof BatchUpdateException
				&& ((BatchUpdateException) e).getNextException() != null) {
			s.append("Cause BATCH UPDATE EXCEPTION: ");
			s.append(String.valueOf(((BatchUpdateException) e)
					.getNextException()));
			s.append("\n\n");
		}

		if( req != null ){
			// Escreve parâmatros da requisição
			s.append("\n\n");
			s.append("===PARAMETROS DA REQUISIÇÃO=== ");
			s.append("\n");
			s.append(parametrosToString(req));
		}

		if (enviarEmail) {
			String login = (usuarioLogado == null ? (req == null ? (loginUsuario == null ? "Timer" : loginUsuario) : "Portal Público") : usuarioLogado.getLogin());
			
			MailBody mail = new MailBody();
			mail.setNome("ERRO " + descSistema);
			mail.setEmail(email);
			mail.setAssunto(" Usuário: " + login + ", Subsistema: " + descSubsistema + ", " + e.getMessage() + ", ");
			mail.setMensagem(s.toString());
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setRegistrarEnvio(false);
			
			Mail.send(mail);
		}

		// chama o processador de erros
		MovimentoErro mov = new MovimentoErro();
		mov.setDetails(s.toString());
		mov.setErro(e);
		mov.setUsuarioLogado((req!=null)?getUsuarioLogado(req):null);
		mov.setSistema(sistema);
		
		mov.setSubsistema(subsistema);
		
		ErrorProcessorDelegate.getInstance().writeError(mov);
		
	}

	private static UsuarioGeral getUsuarioLogado(HttpServletRequest req) {
		return (UsuarioGeral) req.getSession(true).getAttribute("usuario");
	}

	/**
	 * retorna e exceção real encapsulada na exceção JSF
	 *
	 * @param e
	 * @return
	 */
	public static Throwable traceFacesException(ServletException e) {

		if (e.getRootCause() instanceof FacesException) {

			FacesException fe = (FacesException) e.getRootCause();
			Throwable cause = fe;
			Throwable real = fe;

			while ((cause = cause.getCause()) != null) {
				real = cause;
				if (cause.getClass().getName().indexOf("javax.faces") == -1)
					break;
			}

			return real;
		}
		return e;

	}

}
