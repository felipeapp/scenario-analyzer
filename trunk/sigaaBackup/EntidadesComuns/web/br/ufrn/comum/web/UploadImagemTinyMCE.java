/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 10/05/2011
 */
package br.ufrn.comum.web;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.UFRNUtils;

/**
 * Servlet para upload de imagens pelo editor html
 * do Tiny MCE.
 * 
 * @author David Pereira
 *
 */
public class UploadImagemTinyMCE extends HttpServlet {

	@Override
	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {

		boolean isMultipart = ServletFileUpload.isMultipartContent(req);
		
		try {
			if (isMultipart) {
				ServletFileUpload upload = new ServletFileUpload(new DiskFileItemFactory());
				List<FileItem> items = upload.parseRequest(req);

				
				for (FileItem item : items) {
					if (!item.isFormField()) {
						if (item.getSize() <= ParametroHelper.getInstance().getParametroLong(ConstantesParametroGeral.TAMANHO_MAXIMO_UPLOAD_IMAGEM_TINYMCE)) {
							int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
							byte[] conteudo = item.get();
							String contentType = item.getContentType();
						    String fileName = item.getName();
							EnvioArquivoHelper.inserirArquivo(idArquivo, conteudo, contentType, fileName);
							
							res.sendRedirect("/shared/javascript/tiny_mce/plugins/advimage/image.htm?idArquivo=" + idArquivo + "&key=" + UFRNUtils.generateArquivoKey(idArquivo));
							break;
						} else {
							res.sendRedirect("/shared/javascript/tiny_mce/plugins/advimage/image.htm?erro=true&tamanho=" + (item.getSize()/1024) + "&maximo=" + (ParametroHelper.getInstance().getParametroLong(ConstantesParametroGeral.TAMANHO_MAXIMO_UPLOAD_IMAGEM_TINYMCE)/1024));
						}
					}
				}						
			}
			
			
		} catch (FileUploadException e) {
			e.printStackTrace();
		}
		
	}
	
}
