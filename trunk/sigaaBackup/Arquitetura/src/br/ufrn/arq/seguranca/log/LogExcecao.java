package br.ufrn.arq.seguranca.log;

import java.util.Arrays;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class LogExcecao {

	
	public static void logarErroSessao(HttpServletRequest request) {
		logarErroSessao(request, null);
	}
	
	public static void logarErroSessao(HttpServletRequest request, Throwable e) {

		try {
			Logger logger = Logger.getLogger(LogExcecao.class);

			StringBuffer attributes = new StringBuffer();
			StringBuffer headers = new StringBuffer();
			StringBuffer parameters = new StringBuffer();

			
			StringBuffer requestURL = request.getRequestURL();
			String contentType = request.getContentType();
			String method = request.getMethod();
			String queryString = request.getQueryString();
			String server = request.getServerName()+":"+request.getServerPort();
			String remoteAddr = request.getRemoteAddr();
			String remoteHost = request.getRemoteHost();

			Enumeration enumHeaders = request.getHeaderNames();
			Enumeration enumAttributes = request.getAttributeNames();
			Enumeration enumParameters = request.getParameterNames();

			if (enumHeaders != null) {

				while (enumHeaders.hasMoreElements()) {
					String headerName = (String) enumHeaders.nextElement();
					String headerValue = request.getHeader(headerName);
					headers.append(headerName + "=" + headerValue + "\n\t");
				}
			}

			if (enumParameters != null) {

				while (enumParameters.hasMoreElements()) {
					String parameterName = (String) enumParameters
							.nextElement();
					String parameterValue = request.getParameter(parameterName);
					parameters.append(parameterName + "=" + parameterValue
							+ ";");
				}
			}

			if (enumAttributes != null) {

				while (enumAttributes.hasMoreElements()) {
					String attributeName = (String) enumAttributes
							.nextElement();
					String attributeValue = request.getParameter(attributeName);
					attributes.append(attributeName + "=" + attributeValue
							+ ";");
				}
			}

			StringBuffer logMessage = new StringBuffer();

			logMessage.append("\n");
			logMessage.append("RemoteAdress= " + remoteAddr +"\n");
			logMessage.append("RemoteHost=" + remoteHost +"\n");
			logMessage.append("------- \n");
			logMessage.append("server="+server+"\n");
			logMessage.append("URL=" + requestURL.toString() +"\n");
			logMessage.append("queryString="+queryString+"\n");
			logMessage.append("contentType="+contentType+"\n");
			logMessage.append("method="+method+"\n");
			logMessage.append("headers -> "+headers.toString()+"\n");
			logMessage.append("parameters -> "+parameters.toString()+"\n");
			logMessage.append("attributes -> "+attributes.toString()+"\n");

			registerCause(logMessage, e);
			
			logger.info(logMessage);

		} catch (Exception ex) {

			System.out.println("Erro do log de sessao");
			e.printStackTrace();
		}
	}

	private static void registerCause(StringBuffer logMessage, Throwable cause) {
		
		if (cause == null)
			return;
		
		// Detalhes da exceção
		logMessage.append("\n\n");
		logMessage.append("===DADOS DA EXCEÇÃO DISPARADA=== ");
		logMessage.append("\n");
		logMessage.append("Exceção: ");
		logMessage.append(cause.toString());
		logMessage.append("\n\n");
		
		while (cause != null) {
			logMessage.append("Cause: ");
			logMessage.append(String.valueOf(cause) + "\n");
			logMessage.append("CAUSE STACK TRACE:");
			logMessage.append("\n");
			String trace = Arrays.toString(cause.getStackTrace());
			trace = trace.replace(",", "\n");
			logMessage.append(trace);
			logMessage.append("\n\n");

			cause = cause.getCause();
		}
	}
}