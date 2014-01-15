/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 19/04/2010
 */
package br.ufrn.integracao.servicos;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.jws.WebMethod;
import javax.jws.WebService;

import org.apache.commons.io.FileUtils;
import org.springframework.util.StringUtils;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.util.Image2PdfUtils;
import br.ufrn.integracao.interfaces.ConversaoPdfRemoteService;

import com.artofsolving.jodconverter.DefaultDocumentFormatRegistry;
import com.artofsolving.jodconverter.DocumentConverter;
import com.artofsolving.jodconverter.DocumentFormat;
import com.artofsolving.jodconverter.openoffice.connection.OpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.connection.SocketOpenOfficeConnection;
import com.artofsolving.jodconverter.openoffice.converter.OpenOfficeDocumentConverter;

/**
 * Implementação padrão do serviço de conversão de
 * arquivos para o formato pdf.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 * @author Raphael Medeiros
 *
 */
@WebService
public class ConversaoPdfRemoteServiceImpl implements ConversaoPdfRemoteService {

	@Override
	@WebMethod(operationName="converterIdArquivo")
	public File converter(int idArquivo) {
		String nomeArquivo = EnvioArquivoHelper.recuperaNomeArquivo(idArquivo);
		String extensao = StringUtils.getFilenameExtension(nomeArquivo);
		if ("htm".equals(extensao)) extensao = "html";
		
		try {
			File file = File.createTempFile("converted" + System.currentTimeMillis(), "." + extensao);
			FileOutputStream fos = new FileOutputStream(file);
			
			EnvioArquivoHelper.recuperaArquivo(fos, idArquivo);
			
			return converter(file);
		} catch(Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	private boolean isSupported(String extensao) {
		return "doc".equalsIgnoreCase(extensao) || "xls".equalsIgnoreCase(extensao)
			|| "odt".equalsIgnoreCase(extensao) || "rtf".equalsIgnoreCase(extensao)
			|| "txt".equalsIgnoreCase(extensao) || "html".equalsIgnoreCase(extensao)
			|| "htm".equalsIgnoreCase(extensao) || "ods".equalsIgnoreCase(extensao)
			|| "odp".equalsIgnoreCase(extensao) || "ppt".equalsIgnoreCase(extensao);
	}
	
	private boolean isSupportedImage(String extensao) {
		return "bmp".equalsIgnoreCase(extensao) || "gif".equalsIgnoreCase(extensao)
			|| "jpg".equalsIgnoreCase(extensao) || "png".equalsIgnoreCase(extensao)
			|| "wmf".equalsIgnoreCase(extensao) || "tif".equalsIgnoreCase(extensao) || "tiff".equalsIgnoreCase(extensao);
	}

	@Override
	@WebMethod(operationName="converterFile")
	public File converter(File origem) {
		
		try {
			
			String extensao = StringUtils.getFilenameExtension(origem.getName());

			if (!"pdf".equals(extensao)) {
				
				if (isSupported(extensao)) {
					
					DocumentFormat formatoOrigem = new DefaultDocumentFormatRegistry().getFormatByFileExtension(extensao);
					DocumentFormat formatoDestino = new DefaultDocumentFormatRegistry().getFormatByFileExtension("pdf");
					
					// conectar a uma instância do OpenOffice.org na porta 8100
					OpenOfficeConnection connection = new SocketOpenOfficeConnection(8100);
					connection.connect();
			
					// converter
					DocumentConverter converter = new OpenOfficeDocumentConverter(connection);
					
					File tempFile = File.createTempFile(String.valueOf(System.currentTimeMillis()), ".pdf");
					converter.convert(new FileInputStream(origem), formatoOrigem, FileUtils.openOutputStream(tempFile), formatoDestino);
					
					// fechar a conexão
					connection.disconnect();
					return tempFile;
					
				}else if (isSupportedImage(extensao)) {
					
					File convertFile = null;
					
					if ("tif".equalsIgnoreCase(extensao) || "tiff".equalsIgnoreCase(extensao)) {
						convertFile = Image2PdfUtils.convertTiff(origem);
					}else{
						convertFile = Image2PdfUtils.convert(origem);
					}
					
					return convertFile;
					
				} else {
					return null;
				}
				
			} else {
				return origem;
			}
			
		} catch(Exception e) {
			return null;
		}
	}
}