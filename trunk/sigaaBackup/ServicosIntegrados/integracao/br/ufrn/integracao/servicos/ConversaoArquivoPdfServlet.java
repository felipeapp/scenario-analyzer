/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.integracao.servicos;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.web.UFRNServlet;
import br.ufrn.integracao.interfaces.ConversaoPdfRemoteService;

/**
 * @author David Pereira
 *
 */
public class ConversaoArquivoPdfServlet extends UFRNServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		int idArquivo = StringUtils.extractInteger(req.getParameter("idArquivo"));
		
		ConversaoPdfRemoteService service = getBean("conversaoPdfInvoker", req); 
		File tempFile = service.converter(idArquivo);
		
		if (tempFile != null) {
			ServletOutputStream os = res.getOutputStream();
			byte[] dados = FileUtils.readFileToByteArray(tempFile);
			os.write(dados);
		} else {
			try {
				EnvioArquivoHelper.recuperaArquivo(res, idArquivo, true);						
			} catch (Exception e) {
				e.printStackTrace();
			}
			return;
		}
	}
	
}
