/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/06/2010
 */
package br.ufrn.integracao.siged.service;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;

import br.ufrn.integracao.siged.dto.ArquivoDocumento;
import br.ufrn.integracao.siged.service.OcrService;

/**
 * Implementação do serviço de OCR utilizando o programa
 * tesseract (http://code.google.com/p/tesseract-ocr/).
 * 
 * @author David Pereira
 *
 */
@WebService
public class TesseractOcrService implements OcrService {

	/**
	 * Dado um arquivo, retorna a String identificada como sendo
	 * o conteúdo da imagem passada.
	 */
	@Override
	public String identificarConteudo(ArquivoDocumento arquivo) {
		
		List<File> recursosUtilizados = new LinkedList<File>();
		
		try {
			String extensao = FilenameUtils.getExtension(arquivo.getName());

			// Cria arquivo com a imagem original
			File file = File.createTempFile("ocr_" + System.currentTimeMillis(), extensao);
			FileUtils.writeByteArrayToFile(file, arquivo.getBytes());
			recursosUtilizados.add(file);
			
			// Converter imagem para tiff se não for desse tipo
			if (!"tif".equals(extensao)) {
				File convertido = converterImagem(file, extensao);
				recursosUtilizados.add(convertido);
				file = convertido;
			}
			
			// Executa o OCR na imagem
			String texto = executarOcr(file);
			
			return texto;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			limparRecursos(recursosUtilizados);
		}
		
		return null;
		
	}

	/*
	 * O tesseract-ocr só funciona com imagens do tipo TIFF. Se a imagem não
	 * for TIFF, deve ser feita uma conversão.
	 */
	private File converterImagem(File file, String extensao) throws IOException {
		if (!"gif".equals(extensao) && !"bmp".equals(extensao)) {
			throw new IOException("O tipo de imagem enviado (" + extensao + ") não é suportado.");
		}
		
		String comando = extensao + "2tiff " + file.getAbsolutePath() + file.getName() + " " + FilenameUtils.getBaseName(file.getName()) + ".tif -c none";
		Process exec = Runtime.getRuntime().exec(comando);
		IOUtils.toString(exec.getInputStream());
		return new File(file.getAbsolutePath() + FilenameUtils.getBaseName(file.getName()) + ".tif");
	}
	
	/*
	 * Executa o OCR e retorna a String com o conteúdo da imagem.
	 */
	private String executarOcr(File imagem) throws IOException {
		Process exec1 = Runtime.getRuntime().exec("tesseract " + imagem.getAbsolutePath() + " " + imagem.getPath() + FilenameUtils.getBaseName(imagem.getName()) + " -l por");
		IOUtils.toString(exec1.getInputStream());
		Process exec = Runtime.getRuntime().exec("cat " + imagem.getPath() + FilenameUtils.getBaseName(imagem.getName()) + ".txt");
		return IOUtils.toString(exec.getInputStream());
	}

	/*
	 * Remove os arquivos utilizados após a utilização do OCR.
	 */
	private void limparRecursos(List<File> recursosUtilizados) {
		for (File recurso : recursosUtilizados) {
			recurso.delete();
		}
	}
	
}
