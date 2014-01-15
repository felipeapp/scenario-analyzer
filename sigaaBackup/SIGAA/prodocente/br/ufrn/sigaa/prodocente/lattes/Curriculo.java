/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.lattes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.myfaces.custom.fileupload.UploadedFile;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;

/**
 * Classe para interpretar um documento XML contento o currículo 
 * lattes de um docente.
 * 
 * @author David Pereira
 *
 */
public class Curriculo extends DefaultHandler {

	/** Ano de referência padrão */
	public static final int ANO_REFERENCIA = CalendarUtils.getAnoAtual();
	
	/** Componente para efetuar upload do arquivo do currículo */
	private UploadedFile input;
	
	/** Fluxo de bytes correspondente ao arquivo do currículo */
	private byte[] byteArray;
	
	/** Dao com consultas utilizadas no processo de importação */
	private ImportLattesDao dao;
	
	/** Lista de produções importadas */
	private List<? extends Producao> producoes;
	
	/**
	 * Construtor para upload de arquivo
	 * 
	 * @param input
	 * @param dao
	 */
	public Curriculo(UploadedFile input, ImportLattesDao dao) {
		this.input = input;
		this.dao = dao;
		producoes = new ArrayList<Producao>();
	}
	
	/**
	 * Construtor para arquivo obtido automaticamente.
	 * 
	 * @param byteArray
	 * @param dao2
	 */
	public Curriculo(byte[] byteArray, ImportLattesDao dao2) {
		this.byteArray = byteArray;
		this.dao = dao2;
		producoes = new ArrayList<Producao>();
	}

	/**
	 * Implementação do processamento realizado no início de cada nova marcação no XML.
	 */
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		
		try {
			
			ImportProducao<?> producao = null;
			
			if ("PRODUCAO-BIBLIOGRAFICA".equals(qName)) {
				producao = new ImportJornalRevista(getInputStream() , dao, producoes);
				producao.read();
				
				producao = new ImportArtigoPublicado(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportCapitulo(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportLivroPublicado(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportPartituraMusical(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportTrabalhoEvento(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportTraducao(getInputStream(), dao, producoes);
				producao.read();
			}
			
			if ("PRODUCAO-TECNICA".equals(qName)) {
				producao = new ImportApresentacaoTrabalho(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportCartaMapaSimilar(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportDesenvMaterialDidatico(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportMaquete(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportOrganizacaoEvento(getInputStream(), dao, producoes);
				producao.read();
				
//				producao = new ImportProdutoTecnologico(input.getInputStream(), dao, producoes);
//				producao.read();
				
				producao = new ImportSoftware(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportArtigoPublicado(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportApresentacaoObra(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportArranjoMusical(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportComposicaoMusical(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportObraArtesVisuais(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportSonoplastia(getInputStream(), dao, producoes);
				producao.read();
			}
			
			if ("DADOS-COMPLEMENTARES".equals(qName)) {
				producao = new ImportBancaDoutorado(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaEspecializacao(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaGraduacao(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaLivreDocencia(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaMestrado(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaProfessorTitular(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaQualificacao(getInputStream(), dao, producoes);
				producao.read();
				
				producao = new ImportBancaOutras(getInputStream(), dao, producoes);
				producao.read();
			}
			
		} catch(Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 * Obtém o fluxo de entrada do arquivo a partir da fonte correta utilizada.
	 * O arquivo pode ser obtido via upload ou automaticamente via web service.
	 * 
	 * @return
	 * @throws IOException
	 */
	private InputStream getInputStream() throws IOException {
		return input != null ? input.getInputStream() : new ByteArrayInputStream(byteArray);
	}
	
	/**
	 * Interpreta o XML do currículo e retorna uma lista de publicações encontradas 
	 * 
	 * @return
	 * @throws SAXException
	 * @throws IOException
	 * @throws NegocioException 
	 */
	public List<? extends Producao> interpretar() throws SAXException, IOException, NegocioException {
		SAXParser parser = new SAXParser();
		parser.setContentHandler(this);
		parser.setErrorHandler(this);
		
		if (input == null && byteArray == null)
			throw new NegocioException("Nenhum arquivo foi enviado. Por favor, selecione um arquivo.");
		
		parser.parse(new InputSource(getInputStream()));
		
		return producoes;
	}
	
}
