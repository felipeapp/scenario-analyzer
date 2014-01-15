/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 15/03/2007
 */
package br.ufrn.arq.web;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.GZIPOutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet para compressão de javascript
 *
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class ScriptCompressorServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
		doGet(req, res);
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {

		// Extrair do request a ultima data do arquivo em cache
		// Se presente estará no formato RFC822 eg: "Fri, 08 Dec 2006 09:30:32 GMT"
		Date lastAccessDate = null;
		String lastAccessDateStr = request.getHeader("If-Modified-Since");
		if (lastAccessDateStr != null)
			lastAccessDate = parseRFC822Date(lastAccessDateStr);

		try {
			// Buscar parametro com o nome do arquivo
			String script = request.getParameter("src");
			URL u = Thread.currentThread().getContextClassLoader().getResource(script);
			if (u == null) {
				return;
			}

			URLConnection uc = u.openConnection();

			// Descartar milisegundos para evitar indicacao falsa de atualizacao
			Date modified = new Date( (uc.getLastModified()/1000) * 1000);

			// Se o parametro If-Modified-Since for enviado, verificar se pode salvar banda de envio
			if (lastAccessDate != null) {
				if (!modified.after(lastAccessDate)) {
					response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
					return;
				}
			}

			// Como o arquivo foi modificado, enviar novamente
			response.setContentType("text/javascript");
			response.setHeader("Last-Modified", rfc822Format[0].format(modified));
			response.setHeader("Content-Encoding", "gzip");

			try {
				// Copiar os bytes do arquivo para o GZIPOutputStream
				InputStream in = uc.getInputStream();
				GZIPOutputStream out = new GZIPOutputStream(response.getOutputStream());
				byte[] buffer = new byte[16384];
				int bufferFill = in.read(buffer);
				while (bufferFill != -1) {
					out.write(buffer, 0, bufferFill);
					bufferFill = in.read(buffer);
				}
				out.close();
				in.close();
			} catch (IOException e) {
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServletException(e.getMessage(), e);
		}
	}

	/**
	 * Formatos RFC822 para datas
	 */
	private static final SimpleDateFormat[] rfc822Format = new SimpleDateFormat[] {
		new SimpleDateFormat("EE, d MMM yyyy HH:mm:ss z", Locale.getDefault()),
		new SimpleDateFormat("EE d MMM yyyy HH:mm:ss z", Locale.getDefault()),
		new SimpleDateFormat("d MMM yyyy HH:mm:ss z", Locale.getDefault()),
		new SimpleDateFormat("EE, d MMM yyyy HH:mm:ss", Locale.getDefault())
	};

	private static Date parseRFC822Date(String rfcdate) {
		for (SimpleDateFormat d : rfc822Format) {
			try {
				synchronized (d) {
					return d.parse(rfcdate);
				}
			} catch(ParseException e) { }
		}
		return null;
	}
}