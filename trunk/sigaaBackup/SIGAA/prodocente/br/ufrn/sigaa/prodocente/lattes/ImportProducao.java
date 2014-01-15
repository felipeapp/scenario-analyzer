/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.lattes;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.prodocente.ImportLattesDao;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pessoa.dominio.Pais;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoRegiao;

import com.sun.org.apache.xerces.internal.parsers.SAXParser;

/** Classe respons�vel pela importa��o de um tipo de produ��o.
 * 
 * @author davidpereira
 *
 * @param <T> Tipo de produ��o a ser importada.
 */
public abstract class ImportProducao<T> extends DefaultHandler {

	/** Item a ser importado. */
	protected T item;
	
	/** DAO para busca de informa��es para a importa��o do curr�culo Lattes. */
	protected ImportLattesDao dao;
	
	/** Dados a serem importados. */
	protected InputStream input;
	
	/** Indica se realiza o "parse". */
	protected boolean parse;
	
	/** Lista de produ��es. */
	@SuppressWarnings("unchecked")
	protected List producoes;
	
	/** Nome raiz (?). */
	protected String rootName;
	
	/** Mapa descritivo das grandes �reas de conhecimento. */
	private static Map<String, Integer> grandesAreas = new HashMap<String, Integer>();
	
	static {
		grandesAreas.put("CIENCIAS_EXATAS_E_DA_TERRA", 10000003);
		grandesAreas.put("CIENCIAS_BIOLOGICAS", 20000006);
		grandesAreas.put("ENGENHARIAS", 30000009);
		grandesAreas.put("CIENCIAS_DA_SAUDE", 40000001);
		grandesAreas.put("CIENCIAS_AGRARIAS", 50000004);
		grandesAreas.put("CIENCIAS_SOCIAIS_APLICADAS", 60000007);
		grandesAreas.put("CIENCIAS_HUMANAS", 70000000);
		grandesAreas.put("LINGUISTICA_LETRAS_E_ARTES", 80000002);
		grandesAreas.put("OUTROS", 90000005);
	}
	
	/** Importa a produ��o. 
	 * @param input
	 * @param dao
	 * @param producoes
	 * @param rootName
	 */
	public ImportProducao(InputStream input, ImportLattesDao dao, List<? extends Producao> producoes, String rootName) {
		this.input = input;
		this.dao = dao;
		this.producoes = producoes;
		this.rootName = rootName;
	}
	
	/**
	 * M�todo abstrato que recebe a notifica��o do in�cio de um elemento. Deve
	 * ser implementado nas subclasses para que sejam executadas a��es
	 * espec�ficas para o tipo de elemento informado.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 *      java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	public abstract void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException;
	
	/**
	 * M�todo abstrato que recebe a notifica��o do fim de um elemento. Deve ser
	 * implementado nas subclasses para que sejam executadas a��es espec�ficas
	 * para o tipo de elemento informado.
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	@SuppressWarnings("unchecked")
	public final void endElement(String uri, String localName, String qName) throws SAXException {
		if (rootName.equals(qName) && parse) {
			parse = false;
			producoes.add(item);
		}
	}
	
	/** L� o conte�do do arquivo a ser importado e realiza a an�lise (<i>parse</i>). */
	public void read() {
		try {
			SAXParser parser = new SAXParser();
			parser.setContentHandler(this);
			parser.parse(new InputSource(input));
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/** Interpreta uma string descritiva de uma regi�o e retorna um objeto da classe TipoRegiao correspondente.
	 * 
	 * @param regiao
	 * @return
	 */
	public TipoRegiao getTipoRegiao(String regiao) {
		if (regiao.equals("LOCAL"))
			return new TipoRegiao(TipoRegiao.LOCAL);
		else if (regiao.equals("REGIONAL"))
			return new TipoRegiao(TipoRegiao.REGIONAL);
		else if (regiao.equals("NACIONAL"))
			return new TipoRegiao(TipoRegiao.NACIONAL);
		else if (regiao.equals("INTERNACIONAL"))
			return new TipoRegiao(TipoRegiao.INTERNACIONAL);
		else
			return new TipoRegiao(TipoRegiao.NAO_INFORMADO);
	}
	
	/** Converte o atributo destaque para o tipo Boolean.
	 * 
	 * @param destaque "Sim" ou "N�o"
	 * @return True, caso destaque.equals("SIM"). False, caso contr�rio.
	 */
	public Boolean getDestaque(String destaque) {
		if ("SIM".equals(destaque))
			return true;
		else
			return false;
	}
	
	/** Retorna o valor da p�gina inicial da produ��o, convertendo o tipo String para Integer.
	 * 
	 * @param paginaInicial p�gina inicial da produ��o
	 * @return Objeto do tipo Integer com o valor da p�gina inicial.
	 */
	public Integer getPaginaInicial(String paginaInicial) {
		if (paginaInicial == null || "".equals(paginaInicial.trim()))
			return 1;
		else {
			if (paginaInicial.indexOf("/") != -1)
				return Integer.valueOf(StringUtils.extractInteger(paginaInicial.substring(0, paginaInicial.indexOf("/"))));
			else {
				Integer pagina = StringUtils.extractInteger(paginaInicial);
				if (pagina == null)
					return 1;
				else
					return pagina;
			}
		}
	}
	
	/** Retorna o valor da p�gina final da produ��o, convertendo o tipo String para Integer.
	 * 
	 * @param paginaFinal p�gina final da produ��o
	 * @return Objeto do tipo Integer com o valor da p�gina final.
	 */
	public Integer getPaginaFinal(String paginaFinal) {
		if (paginaFinal == null || "".equals(paginaFinal.trim())) {
			return 2;
		} else {
			return StringUtils.extractInteger(paginaFinal);
		}
	}
	
	/** Retorna a p�gina inicial no formato String, usado na importa��o de Artigo.
	 * 
	 * @param paginaInicial
	 * @return
	 */
	public String getPaginaInicialString(String paginaInicial) {
		if (paginaInicial == null || "".equals(paginaInicial.trim()))
			return "1";
		else {
			return paginaInicial.trim().toUpperCase();
		}
	}
	
	/** Retorna a p�gina final no formato String, usado usado na importa��o de Artigo.
	 * 
	 * @param paginaFinal
	 * @return
	 */
	public String getPaginaFinalString(String paginaFinal) {
		if (paginaFinal == null || "".equals(paginaFinal.trim())) {
			return "2";
		} else {
			return paginaFinal.trim().toUpperCase();
		}
	}
	
	/** Converte uma string descritiva da �rea de conhecimento para um objeto da classe {@link AreaConhecimentoCnpq}.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public AreaConhecimentoCnpq getGrandeAreaConhecimentoCnpq(String area) throws DAOException {
		return new AreaConhecimentoCnpq(grandesAreas.get(area));
	}
	
	/** Converte uma string descritiva da sub-�rea de conhecimento para um objeto da classe {@link AreaConhecimentoCnpq}.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws UnsupportedEncodingException 
	 */
	public AreaConhecimentoCnpq getSubAreaConhecimentoCnpq(String area) throws DAOException, UnsupportedEncodingException {
		return dao.findSubAreaConhecimento(area);		
	}
	
	/** Converte uma string de um nome de pa�s para um objeto da classe {@link Pais}.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public Pais getPais(String pais) throws DAOException {
		return dao.findPais(pais);
	}
	
	/** Converte uma string em um inteiro.
	 * 
	 * @param valor
	 * @return
	 */
	public int parseInt(String valor) {
		if (valor != null && !"".equals(valor.trim()))
			return Integer.parseInt(valor);
		return 0;
	}
		
}
