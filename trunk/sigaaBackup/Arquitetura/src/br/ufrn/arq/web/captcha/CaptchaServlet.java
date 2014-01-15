/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criação em: 14/11/2007
 */
package br.ufrn.arq.web.captcha;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet para exibição de Captchas.
 *  
 * @author Ricardo Wendell
 * @author Gleydson Lima
 *
 */
public class CaptchaServlet extends javax.servlet.http.HttpServlet {

	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		ServletOutputStream out = response.getOutputStream();
		ByteArrayOutputStream jpegStream = new ByteArrayOutputStream();

		try {
			/* A imagem será gerada com base no ID da sessão */
			String jsid = request.getSession().getId();

			/* Gera a imagem */
			BufferedImage challenge = Captcha.getInstance().getService().getImageChallengeForID(jsid);
			/* Codifica a imagem no formato JPEG */
			ImageIO.write(challenge, "jpeg", jpegStream);
			
		} catch (Exception e) {
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
		}

		/* Transforma a imagem em um array de bytes */
		byte[] jpegBytes = jpegStream.toByteArray();

		/*
		 * Modifica os cabeçalhos http
		 * 
		 * para que a imagem não seja armazenada em cache
		 */

		response.setHeader("Cache-Control", "no-store");
		response.setHeader("Pragma", "no-cache");
		response.setDateHeader("Expires", 0);
		response.setContentType("image/jpeg");

		/* Envia a imagem para o cliente */

		out.write(jpegBytes);
		out.flush();
		out.close();

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}