/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/10/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.FormatoMaterialDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.FichaCatalografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Etiqueta;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.FormatoMaterial;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.SubCampo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 * <p> Classe auxiliar para manipular os formatos de refer�ncia e ficha catalogr�fica na biblioteca </p>
 *
 * 
 * @author jadson
 *
 */
public class FormatosBibliograficosUtil {

	/**
	 * A string que deve ser informada no campo autor caso tenha mais de 3.
	 */
	public static final String REFERENCIA_MUITOS_AUTORES = " et al";
	
	/**
	 * Inicia uma nova linha em html
	 */
	public static final String NOVA_LINHA_HTML = "<br/>";
	
	/**
	 * Inicia uma nova linha em Java
	 */
	public static final String NOVA_LINHA_JAVA = "\r\n";
	
	/**
	 * <p>Utilizado para delimitar o in�cio de uma �rea em negrito.</p> 
	 * 
	 * <p> <strong>IMPORTANTE:</strong> Tanto em Java como em HTML, se for 
	 * feito em java, deve ser tratado antes de mostrar para o usu�rio, dependendo do formato que vai ser mostrado (PDF, JTextArea, etc..)</p>
	 */
	public static final String INICIO_NEGRITO = "<strong>";
	
	/**
	 * <p>Utilizado para delimitar o fim de uma �rea em negrito.</p> 
	 * 
	 * <p> <strong>IMPORTANTE:</strong> Tanto em Java como em HTML, se for 
	 * feito em java, deve ser tratado antes de mostrar para o usu�rio, dependendo do formato que vai ser mostrado (PDF, JTextArea, etc..)</p>
	 * 
	 */
	public static final String FINAL_NEGRITO = "</strong>";
	
	
	/** A primeira margem da ficha, come�a do in�cio da linha (em HTML) */
	public static final String MARGEM_1_FICHA_EM_HTML = "";  
	
	/** A primeira margem da ficha, come�a do in�cio da linha (em Java) */
	public static final String MARGEM_1_FICHA_EM_JAVA = "";  
	
	/** Come�a a partir da 4a letra = 2 espa�os em branco + espaco obrigat�rio antes de come�ar os dados (em HTML) */
	public static final String MARGEM_2_FICHA_EM_HTML = "&nbsp;&nbsp; "; 
	
	/** Come�a a partir da 4a letra = 2 espa�os em branco + espaco obrigat�rio antes de come�ar os dados (em Java) */
	public static final String MARGEM_2_FICHA_EM_JAVA = "   ";  
	
	/**  Come�a a partir da 7a letra = 5 espa�os em branco + espaco obrigat�rio antes de come�ar os dados (em HTML) */
	public static final String MARGEM_3_FICHA_EM_HTML = "&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; ";  
	
	/**  Come�a a partir da 7a letra = 5 espa�os em branco + espaco obrigat�rio antes de come�ar os dados (em Java) */
	public static final String MARGEM_3_FICHA_EM_JAVA = "      ";
	
	/** A quantida de carateres impressos antes do texto, quando ele est� na segunda margem */
	public static final int QTD_CARACTERES_MARGEN_2 = 3; 
	
	/** A quantida de carateres impressos antes do texto, quando ele est� na terceira margem */
	public static final int QTD_CARACTERES_MARGEN_3 = 6; 
	
	/** A quantidade m�xima de caracteres que podem ser imprimidos por linha */
	public static final int QTD_MAXIMO_CARACTERES_POR_LINHA = 50; 
	
	/** separador ponto*/
	public static final String SEPARADOR_PONTO = ". ";
	
	/** separador traco*/
	public static final String SEPARADOR_TRACO = "- ";
	
	/** separador dois pontos*/
	public static final String SEPARADOR_DOIS_PONTOS = ": ";
	
	/** separador virgula*/
	public static final String SEPARADOR_VIRGULA = ", ";
	
	/** separador ponto e virgula*/
	public static final String SEPARADOR_PONTO_VIRGULA = "; ";
	
	/** separador abre par�nteses*/
	public static final String SEPARADOR_ABRE_PARENTESES = " (";
	
	/** separador fecha par�nteses*/
	public static final String SEPARADOR_FECHA_PARENTESES = ") ";
	
	/** separador abre cochete*/
	public static final String SEPARADOR_ABRE_COCHETE = " [";
	
	/** separador fecha cochete*/
	public static final String SEPARADOR_FECHA_COCHETE = "] ";
	
	/** separador barra*/
	public static final String SEPARADOR_BARRA = " / ";

	/** separador retic�ncias */
	public static final String SEPARADOR_RETICENCIAS = "... ";
	
	/** separador ponto espa�o tra�o */
	public static final String SEPARADOR_PONTO_ESPACO_TRACO = ". - ";
	
	/** texto impresso para isbn */
	public static final String TEXTO_ISBN = "ISBN: ";
	
	/** texto impresso para issn */
	public static final String TEXTO_ISSN = "ISSN: ";
	
	/** Texto que identifica que o que est� no campo de autor � o ilustrador da obra, nesse caso n�o entra nas refer�ncias */
	public static final String TEXTO_IDENTIFICA_ILUSTRADOR = "ilustr";
	
	/** Texto que identifica que o que est� no campo de autor � o tradutor da obra, nesse caso n�o entra nas refer�ncias */
	public static final String TEXTO_IDENTIFICA_TRADUTOR = "trad";
	
	
	
	////////////////////////////     GERA��O DO FORMATO DE REFER�NCIA    ///////////////////////
	
	
	/**
	 * Indica que se trata de uma autor pessoa 
	 */
	private boolean pessoa = false;
	
	/**
	 * Indica que se trata de uma autor corpora��o 
	 */
	private boolean corporativo = false;
	
	/**
	 * Indica que se trata de uma autor evento 
	 */
	private boolean evento = false;
	
	/**
	 * Indica se est� gerando a ficha para ser impresso nas p�ginas do sistem ou em algum arquivo texto.
	 */
	private boolean saidaEmHTML = true;
	
	/**
	 * Guarda as informa��es dos formatos de materiais de v�rios t�tulos ao mesmo tempo, para evitar v�rias consultas ao banco.
	 */
	private List<Object> infoFormatosMateriaisTitulos;
	
	/**
	 * Diz se se est� gerando o formato de refer�ncia ou a ficha catalogr�fica para v�rios t�tulo ao mesmo tempo. Assim
	 * precisa-se otimizar a consulta do formato de material.
	 */
	private boolean gerarParaVariosTitulos = false;
	
	/** Cont�m as siglas que indicam que um t�tulo � um trabalho acad�mico. */
	private String siglasTrabalhosAcademicos = "";
	
	/**
	 * Construtor padr�o
	 */
	public FormatosBibliograficosUtil(){
		gerarParaVariosTitulos = false;
		siglasTrabalhosAcademicos = ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.SIGLAS_IDENTIFICAO_TRABALHOS_ACADEMICOS);
	}
	
	
	
	
	/**
	 *    <p>M�todo que gera o formato de refer�ncia de um conjunto de t�tulos do acervo. </p> 
	 *    <p>RETORNANDO CADA FORMATO DE REFER�NCIA EM SEPARADO </p>
	 *    
	 *    <p>
	 *    <i>Utilizado quando se deseja gerar o formato de refer�ncia para v�rios t�tulo mas n�o se deseja o over head de
	 *    realizar v�rias consultas no banco para pegar o dados de cada t�tulo. 
	 *    </i>
	 *    </p>
	 * 
	 * @param titulos
	 * @param saidaEmHTML  indica que o resultado vai ser formatado utilizando tags html
	 * @return uma lista de mapas, onde cada MAP cont�m o id do T�tulo e o seu formata do refer�ncia.
	 * 
	 * Resultado 1:
	 *           Chave: idTitulo
	 *           Valor: Formato de Refer�ncia
	 *           
	 * Resultado 2:
	 *           Chave: idTitulo
	 *           Valor: Formato de Refer�ncia
	 *           
	 * Resultado N:
	 *           Chave: idTitulo
	 *           Valor: Formato de Refer�ncia                       
	 * 
	 * @throws DAOException 
	 */
	public List<Object[]> gerarFormatosReferenciaSeparados(List<TituloCatalografico> titulos, boolean saidaEmHTML) throws DAOException{
		
		TituloCatalograficoDao tituloDao = null;
		FormatoMaterialDao formatoDao = null;
		
		List<Object[]> restorno = new ArrayList<Object[]>();
		
		
		this.saidaEmHTML = saidaEmHTML;
		
		try{
			
			tituloDao =  DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
			formatoDao =  DAOFactory.getInstance().getDAO(FormatoMaterialDao.class);
			
			List<Integer> idTitulos = new ArrayList<Integer>();
			
			for (TituloCatalografico titulo : titulos) {
				idTitulos.add( titulo.getId() );
			}
			
			infoFormatosMateriaisTitulos = formatoDao.findFormatosMaterialDosTitulo(idTitulos);
			
			gerarParaVariosTitulos = true;
			
			/* ************************************************************************************
			 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
			 * Object[0] = id do titulo
			 * Object[1] = 245     (campo)
			 * Object[2] = a       (subcampo)
			 * Object[3] = "...."  (dados)
			 * Object[4] = "...."  (id campo dados)
			 * Object[5] = "...."  (posicao campo dados)
			 * Object[6] = "...."  (posicao sub campo)
			 * ************************************************************************************/
			List<Object[]> dadosCampoTitulos = tituloDao.findDadosTitulosCatalografico(idTitulos);
			
			
			for (TituloCatalografico titulo : titulos) {
				
				List<Object[]> dadosCampoTitulo = new ArrayList<Object[]>();
				
				for (Object[] dadosTitulos : dadosCampoTitulos) {
					
					if( ( (Integer) dadosTitulos[0] ).equals( titulo.getId())){
						
						/* ************************************************************************************
						 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
						 * Object[0] = 245     (campo)
						 * Object[1] = a       (subcampo)
						 * Object[2] = "...."  (dados)
						 * Object[3] = "...."  (id campo dados)
						 * Object[4] = "...."  (posicao campo dados)
						 * Object[5] = "...."  (posicao sub campo)
						 * ************************************************************************************/
						dadosCampoTitulo.add( new Object[]{dadosTitulos[1], dadosTitulos[2], dadosTitulos[3], dadosTitulos[4], dadosTitulos[5], dadosTitulos[6]});
					}
					
				}
				
				if(saidaEmHTML){
					Object[] temp = new Object[2];
					temp[0] = titulo.getId();
					temp[1] =  gerarFormatoReferencia(titulo, dadosCampoTitulo, saidaEmHTML).replace('\r', ' ').replace('\n', ' ')+NOVA_LINHA_HTML;
					restorno.add( temp );
					
					
				}else{
					// Retira os caracteres de quebra de linha em java para evitar que aparece no arquivo uma nova linha onde n�o deve
					Object[] temp = new Object[2];
					temp[0] = titulo.getId();
					temp[1] =  gerarFormatoReferencia(titulo, dadosCampoTitulo, saidaEmHTML).replace('\r', ' ').replace('\n', ' ')+NOVA_LINHA_JAVA;
					restorno.add( temp );
				}
			}
			
			
			return restorno;
			
		}finally{
			if(formatoDao != null) formatoDao.close();
			if(tituloDao != null) tituloDao.close();
		}
		
	}
	
	
	/**
	 *    <p>M�todo que gera o formato de refer�ncia de um conjunto de t�tulos do acervo. </p>
	 *    
	 *    <p> RETORNANDO UMA �NICA STRING COM TODOS OS FORMATO DE REFER�NCIA </p>
	 *    
	 *    <p>
	 *    <i>
	 *    Utilizado quando se deseja gerar o formato de refer�ncia para v�rios t�tulo mas n�o se deseja o over head de
	 *    realizar v�rias consultas no banco para pegar o dados de cada t�tulo 
	 *    </i>
	 *    </p>
	 * 
	 * @param titulos
	 * @param saidaEmHTML  indica que o resultado vai ser formatado utilizando tags html
	 * @return
	 * @throws DAOException 
	 */
	public String gerarFormatosReferencia (List<TituloCatalografico> titulos, boolean saidaEmHTML) throws DAOException{
		
		TituloCatalograficoDao tituloDao = null;
		FormatoMaterialDao formatoDao = null;
		
		StringBuilder formatoReferenciaBuffer = new StringBuilder();
		
		this.saidaEmHTML = saidaEmHTML;
		
		try{
			
			tituloDao =  DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
			formatoDao =  DAOFactory.getInstance().getDAO(FormatoMaterialDao.class);
			
			List<Integer> idTitulos = new ArrayList<Integer>();
			
			for (TituloCatalografico titulo : titulos) {
				idTitulos.add( titulo.getId() );
			}
			
			infoFormatosMateriaisTitulos = formatoDao.findFormatosMaterialDosTitulo(idTitulos);
			
			gerarParaVariosTitulos = true;
			
			/* ************************************************************************************
			 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
			 * Object[0] = id do titulo
			 * Object[1] = 245     (campo)
			 * Object[2] = a       (subcampo)
			 * Object[3] = "...."  (dados)
			 * Object[4] = "...."  (id campo dados)
			 * Object[5] = "...."  (posicao campo dados)
			 * Object[6] = "...."  (posicao sub campo)
			 * ************************************************************************************/
			List<Object[]> dadosCampoTitulos = tituloDao.findDadosTitulosCatalografico(idTitulos);
			
			
			for (TituloCatalografico titulo : titulos) {
				
				List<Object[]> dadosCampoTitulo = new ArrayList<Object[]>();
				
				for (Object[] dadosTitulos : dadosCampoTitulos) {
					
					if( ( (Integer) dadosTitulos[0] ).equals( titulo.getId())){
						
						/* ************************************************************************************
						 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
						 * Object[0] = 245     (campo)
						 * Object[1] = a       (subcampo)
						 * Object[2] = "...."  (dados)
						 * Object[3] = "...."  (id campo dados)
						 * Object[4] = "...."  (posicao campo dados)
						 * Object[5] = "...."  (posicao sub campo)
						 * ************************************************************************************/
						dadosCampoTitulo.add( new Object[]{dadosTitulos[1], dadosTitulos[2], dadosTitulos[3], dadosTitulos[4], dadosTitulos[5], dadosTitulos[6]});
					}
					
				}
				
				if(saidaEmHTML){
					formatoReferenciaBuffer.append( gerarFormatoReferencia(titulo, dadosCampoTitulo, saidaEmHTML).replace('\r', ' ').replace('\n', ' ')+NOVA_LINHA_HTML);
				}else{
					// Retira os caracteres de quebra de linha em java para evitar que aparece no arquivo uma nova linha onde n�o deve
					String resultado =  gerarFormatoReferencia(titulo, dadosCampoTitulo, saidaEmHTML).replace('\r', ' ').replace('\n', ' ')+NOVA_LINHA_JAVA;
					formatoReferenciaBuffer.append( resultado);
				}
			}
			
			
			return formatoReferenciaBuffer.toString();
			
		}finally{
			if(formatoDao != null) formatoDao.close();
			if(tituloDao != null) tituloDao.close();
		}
	}
	
	
	/**
	 *    <p>M�todo que gera o formato de refer�ncia de um T�tulo do acervo das bibliotecas, sem iniciar os dados 
	 *    do T�tulo previamente</p>.
	 *    <p>Chamado de v�rias partes do sistema onde se tenha uma refer�ncia do t�tulo e deseje v�-lo em formato de refer�ncia.</p>
	 * 
	 * @param objTitulo
	 * @param saidaEmHTML  indica que o resultado vai ser formatado utilizando tags html
	 * @return
	 * @throws DAOException 
	 */
	public String gerarFormatoReferencia (TituloCatalografico titulo, boolean saidaEmHTML) throws DAOException{
		
		TituloCatalograficoDao tituloDao = null;
		
		this.saidaEmHTML = saidaEmHTML;
		
		try{
			
			tituloDao =  DAOFactory.getInstance().getDAO(TituloCatalograficoDao.class);
			
			/* ************************************************************************************
			 * Obt�m os dados do T�tulos passado necess�rios para gerar o formato de refer�ncia.
			 * Object[0] = 245     (campo)
			 * Object[1] = a       (subcampo)
			 * Object[2] = "...."  (dados)
			 * Object[3] = "...."  (id campo dados)
			 * Object[4] = "...."  (posicao campo dados)
			 * Object[5] = "...."  (posicao sub campo)
			 * ************************************************************************************/
			List<Object[]> dadosCampo = tituloDao.findDadosTituloCatalografico(titulo.getId());
			return gerarFormatoReferencia(titulo, dadosCampo, saidaEmHTML);
			
			
		}finally{
			if(tituloDao != null) tituloDao.close();
		}
	}
	
	
	
	/**
	 *    M�todo que gera o formato de refer�ncia de um T�tulo do acervo das bibliotecas.
	 * 
	 * @param objTitulo
	 * @param dadosCampo  os dados do t�tulo que seram utilizados para gerar o formato de refer�ncia. J� devem ser consultados e passados para o m�todo.
	 * @param saidaEmHTML  indica que o resultado vai ser formatado utilizando tags html
	 * @return
	 * @throws DAOException 
	 */
	public String gerarFormatoReferencia (TituloCatalografico tituloCat, List<Object[]> dadosCampo, boolean saidaEmHTML) throws DAOException{

		pessoa = false;
		corporativo = false;
		evento = false;
		
		String autor = "";               // 100 a 
		String titulo = "";      	     // 245 a
		String subtitulo = ""; 	         // 245 b
		String edicao = ""; 	         // 250	a
		String local = ""; 	             // 260	a
		String editora = "";	         // 260	b
		String ano = "";	             // 260	c, g
		String descricaoFisica = "";     // 300 a, b, e
		String serie = "";	             // 440	a, n, p, v   ou 490 a, v
		String isbn = "";	             // 020	a
		String notaDissertacaoTese = ""; // 502 a 
		
		
		String periodicidade = "";      // 310 a
		String issn = "";               // 022 a
		
		FormatoMaterialDao formatoDao = null;
		
		boolean isPeriodico = false;
		boolean autoriaDesconhecida = false;
		
		this.saidaEmHTML = saidaEmHTML;
		
		try{
			
			if(! gerarParaVariosTitulos){  // Pode fazer a consulta j� otimizada do formato do material de t�tulo
				formatoDao =  DAOFactory.getInstance().getDAO(FormatoMaterialDao.class);
				FormatoMaterial f = formatoDao.findFormatoMaterialDoTitulo(tituloCat.getId());
				
				if(f == null)
					return "";  // N�o � poss�vel gerar o formato para t�tulos ainda n�o catalogados
				
				isPeriodico = f.isFormatoPeriodico();
				
			}else{  // mesmo sendo otimizada, n�o � poss�vel fazer centenas de consultas no banco, tem que pegar da mem�ria
				
				FormatoMaterial f = recuperaFormatoMaterialEmMemoria(tituloCat.getId());
				isPeriodico = f.isFormatoPeriodico();
			}
			
			
			/* ********************************************************
			 * Retira toda a pontua��o AACR2 colocada na cataloga��o
			 * ********************************************************/
			for (Object[] objects : dadosCampo) {
				objects[2] = CatalogacaoUtil.retiraPontuacaoAACR2( (String) objects[2],  campoPermaneceComPontuacaoEmPar(objects)); 
			}
			
			boolean trabalhosAcademicos = false;
			
			String _090_d = recuperaValorDoCampo(Etiqueta.NUMERO_CHAMADA.getTag(), new char[]{SubCampo.SUB_CAMPO_D}, dadosCampo);
			
			if(StringUtils.notEmpty(_090_d)){
				if( siglasTrabalhosAcademicos.replace(",", " ").contains(_090_d.trim()) ){
					trabalhosAcademicos = true;
				}
			}
			
			if(! isPeriodico ){
				try{
					autor = formataAutoresFormatoReferencia(dadosCampo, trabalhosAcademicos);
				} catch (AutoriaDesconhecidaException e) {
					autoriaDesconhecida = true;
				}
				
				if(autoriaDesconhecida)
					titulo = formataTituloAutoriaDesconhecidaFormatoReferencia( recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) );
				else
					titulo = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
			}else{
				// Para peri�dicos a entrada tamb�m � pelo titulo, todo em mai�culo
				
				titulo = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
				if(StringUtils.notEmpty(titulo)) titulo = titulo.toUpperCase();
			}
			
			
			
			subtitulo = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_B}, dadosCampo) ;
			
			if(! isPeriodico){
				edicao = recuperaValorDoCampo(Etiqueta.EDICAO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
			}
			
			local = recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
			editora = recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_B}, dadosCampo) ;
			ano = recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_C, SubCampo.SUB_CAMPO_G}, dadosCampo) ;
			
			if(! autoriaDesconhecida && ! isPeriodico){ // entrada por autor n�o tem descri��o F�sica
				descricaoFisica = recuperaValorDoCampo(Etiqueta.DESCRICAO_FISICA.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
				descricaoFisica = descricaoFisica.replace(".", "");
			}
			
			if( ! evento && ! isPeriodico){
				
				// Pega os dados do campo  490 //
				serie = recuperaValorDoCampo(Etiqueta.SERIE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
				
				String volume490 = recuperaValorDoCampo(Etiqueta.SERIE.getTag(), new char[]{SubCampo.SUB_CAMPO_V}, dadosCampo) ;
				
				if(StringUtils.isNotEmpty(volume490)){
					
					if(StringUtils.isNotEmpty(serie))
						serie = serie.trim() + ", "+volume490;
					else
						serie = volume490;
				}
				
				// Se n�o encontrou, utiliza o velho campo 440
				if(StringUtils.isEmpty(serie)){
					serie = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_N, SubCampo.SUB_CAMPO_P}, dadosCampo) ;
					
					String volumeSerie = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_V}, dadosCampo) ;
					
					if(StringUtils.isNotEmpty(volumeSerie)){
						
						if(StringUtils.isNotEmpty(serie))
							serie = serie.trim() + ", "+volumeSerie;
						else
							serie = volumeSerie;
					}
					
				}
				
				List<String> isbnsTemp = recuperaValorDoCampoSeparado(Etiqueta.ISBN.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
				
				int contadorIsbns = 0;
				
				for (String isbnTemp : isbnsTemp) {
					
					if(StringUtils.notEmpty(isbnTemp)){
						isbnTemp = formataISBN(isbnTemp);
						if(StringUtils.notEmpty(isbnTemp)){ // se ficou alguma coisa
							
							if(contadorIsbns == 0 ) // primeiro
								isbn = TEXTO_ISBN + isbnTemp;
							else
								isbn += SEPARADOR_VIRGULA+isbnTemp;
							
							contadorIsbns++;
						}
					}
					
				}
				
			}
			if(! evento && ! corporativo && ! autoriaDesconhecida && ! isPeriodico){
				notaDissertacaoTese = recuperaValorDoCampo(Etiqueta.NOTA_DISSETACAO_TESE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
			}
			
			
			if(isPeriodico){
				periodicidade = recuperaValorDoCampo(Etiqueta.PERIODICIDADE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
				
				String issnTemp = recuperaValorDoCampo(Etiqueta.ISSN.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
				
				if(StringUtils.notEmpty( issnTemp))
					issn = TEXTO_ISSN + issnTemp;
			}
			
		}finally{
			if(formatoDao != null) formatoDao.close();
		}
		
		StringBuilder retorno = new StringBuilder();
		retorno.append( getPontuacaoCampo(autor, '.') );
		retorno.append( getPontuacaoTitulo( getFormatacaoTituloReferencia(titulo, ( ! isPeriodico && !autoriaDesconhecida ) ), subtitulo )); // se ! isPeriodico && !autoriaDesconhecida fica em negrito 
		retorno.append( getPontuacaoCampo(edicao, '.') );
		retorno.append( getPontuacaoCampo(local, ':') );
		retorno.append( getPontuacaoCampo(editora, ',')  );
		retorno.append( getPontuacaoCampo(ano, '.')  );
		retorno.append( getPontuacaoCampo(descricaoFisica, '.')  );
		retorno.append( getPontuacaoSerie(serie) );
		retorno.append( getPontuacaoCampo(isbn, '.') );
		retorno.append( getPontuacaoCampo(notaDissertacaoTese, '.') );
		retorno.append( getPontuacaoCampo(periodicidade, '.') );
		retorno.append( getPontuacaoCampo(issn, '.') );
		
		return retorno.toString();
	}

	
	
	/**
	 * Retorna o formato de material do t�tulo que est� em mem�ria, para n�o realizar, em alguns casos, 
	 * centenas de consultas ao banco. 
	 *
	 * @param idTitulo
	 * @return
	 */
	private FormatoMaterial recuperaFormatoMaterialEmMemoria(int idTitulo){
		
		int indexPosicao = 0;
		
		FormatoMaterial retorno = new FormatoMaterial();
		
		if(infoFormatosMateriaisTitulos != null){
			for ( ; indexPosicao < infoFormatosMateriaisTitulos.size() ;  indexPosicao++) {
				
				Object[] infoFormatoTemp = (Object[]) infoFormatosMateriaisTitulos.get(indexPosicao);
				
				if(  ( (Integer)infoFormatoTemp[0]).equals(idTitulo)){
					retorno.setId( (Integer) infoFormatoTemp[1] ) ;
					break;
				}
				
			}
		}
		
		// Retira os t�tulos cujos formatos j� foram encontrados para diminuir o tamanho do array e a pesquisa ficar mais r�pida.
		if(indexPosicao >= 0 && indexPosicao < infoFormatosMateriaisTitulos.size())
			infoFormatosMateriaisTitulos.remove(indexPosicao);
		
		
		
		return retorno;
	}
	
	
	
	
	/**
	 *  <p>Formata os autores da obras de acordo com os regras do formato de refer�ncia.</p>
	 *
	 *	<p>
	 *		<h2> --- Um autor --- </h2>
	 *      SOBRENOME, Nome.
	 *		<h2> --- At� 3 autores --- </h2>
	 *      SOBRENOME, Nome; SOBRENOME, Nome; SOBRENOME, Nome. (O primeiro autor ser� extra�do do campo 1XX, os demais do campo 7XX)
	 *      
	 *      <h2> --- MAIS de 3 Autores  --- </h2>
	 *      SOBRENOME, Nome; et al. <br/>  
	 *      ( Se tiver autor no campo 100, usar esse autor seguido da express�o et al.  ) <br>
	 *      ( Caso n�o haka autor no campo 100, indicar o ***primeiro*** autor do campo 700 seguido da express�o et al.  ) <br>
	 *      
	 *     <h2> --- REGRA ENTRADA POR ORGANIZADOR, COORDENADOR OU EDITOR (Cataloga��o n�o possui o campo 100)  --- </h2>
	 *     Indicar o primeiro ***NOME*** do campo 700 seguido da abreviatura da designa��o (sub campo c), entre par�nteses.
	 *     Tamb�m segue a regra de mais do n�mero de autores: indicar o primeiro ***NOME*** seguido da express�o et al. e da 
	 *     abreviatura da designa��o (sub campo c), entre par�nteses.
	 *     
	 *     <h2> ---  AUTOR ENTIDADE  --- </h2>
	 *    NOME DA ENTIDADE. Nome da Unidade Subordinada.     <br/>
	 *    110$a ->  TUDO maiusculo                           <br/>
	 *    110$b -> apenas as primeiras letras em maiusculo;  <br/>
	 *    
	 *     <h2> ---  AUTOR EVENTO  --- </h2>
	 *    NOME DO EVENTO, N�mero do Evento.                       <br/>
	 *    111$a ->  TUDO maiusculo                                <br/>
	 *    110$n, d, c -> apenas as primeiras letras em maiusculo; <br/>
	 *    
	 *    <h2>---  ENTRADA POR T�TULO ----</h2>
	 *    PRIMEIRO nome do t�tulo em mai�scula                                                   <br/>
	 *    S� utilizado quando n�o existem na cataloga��o os campos 100, 110, 111, 700, 710, 711  <br/>
	 *  </p>
	 *
	 * @param dadosCampo
	 * @return
	 */
	private  String formataAutoresFormatoReferencia(List<Object[]> dadosCampo, boolean trabalhosAcademicos) throws AutoriaDesconhecidaException{
		
		int qtdAutores = 0;
		
		
		FormatosBibliograficosUtil formatador = new FormatosBibliograficosUtil();
		
		String autorPrincipalPessoa = "";       //100$a
		String autorPrincipalCooporativo = "";  //110$a
		String autorPrincipalEvento = "";       //111$a
		
		/*
		 * Dados auxiliares utilizados no caso de autor principal um entidade
		 */
		String autorCooporativoEntidadeSubordinada = "";  //110$b
		
		/*
		 * Dados auxiliares utilizados no caso de autor principal evento
		 */
		String numeroEvento = null; //111$n
		String anoRealizacao = null; //111$d
		String localRealizacao = null; //111$c
		
		/*
		 * Map<diCampoDados, Dados do campo>
		 */
		List<DadosMontaFormatoReferencia> autoresSegundariosPessoa = new ArrayList<DadosMontaFormatoReferencia>();                      // 700$a
		List<DadosMontaFormatoReferencia> autoresSegundariosCooporativo = new ArrayList<DadosMontaFormatoReferencia>();                 // 710$a 
		List<DadosMontaFormatoReferencia> autoresSegundariosEvento = new ArrayList<DadosMontaFormatoReferencia>();                      // 711$a
		
		/*
		 * Usado quando n�o se tem o autor principal
		 */
		List<DadosMontaFormatoReferencia> autoresSegundariosPessoaDesignacao = new ArrayList<DadosMontaFormatoReferencia>();                   // 700$c
		
		/*
		 * Usado para autores segund�rios cooporativos
		 */
		List<DadosMontaFormatoReferencia> autoresCooporativoEntidadesSubordinada = new ArrayList<DadosMontaFormatoReferencia>();                // 710$b
		
		
		/*
		 * Usado para autores segund�rios cooporativos
		 */
		List<DadosMontaFormatoReferencia> autorEventoNumerosEventos = new ArrayList<DadosMontaFormatoReferencia>();                  // 711$n
		List<DadosMontaFormatoReferencia> autorEventoAnosRealizacao = new ArrayList<DadosMontaFormatoReferencia>();                  // 711$d
		List<DadosMontaFormatoReferencia> autorEventoLocaisRealizacao = new ArrayList<DadosMontaFormatoReferencia>();                // 711$c
		
		
		/**
		 * Se a cataloga��o possui autor principal, isto � um dos campos 1XX foi usado.
		 * Caso contr�rio s� possui os campos 700, cuja refer�ncia deve ser gerada de uma forma 
		 * diferente
		 */
		boolean possuiAutorPrincipal = false;
		
		
		for (Object[] object : dadosCampo) {
		
			if( Etiqueta.AUTOR.getTag().equalsIgnoreCase((String)object[0]) && new Character(SubCampo.SUB_CAMPO_A).equals(object[1] )){
				autorPrincipalPessoa = (String)object[2];
				pessoa = true;
				possuiAutorPrincipal = true;
			}
			
			if( Etiqueta.AUTOR_COOPORATIVO.getTag().equalsIgnoreCase((String)object[0])){
				
				if(new Character(SubCampo.SUB_CAMPO_A).equals(object[1])){
					autorPrincipalCooporativo = (String)object[2] ;
					
					corporativo = true;
					possuiAutorPrincipal = true;
				}
				
				if(new Character(SubCampo.SUB_CAMPO_B).equals(object[1])){
					autorCooporativoEntidadeSubordinada = (String)object[2];
				}
			}
			
			if( Etiqueta.AUTOR_EVENTO.getTag().equalsIgnoreCase((String) object[0])){
				
				if( new Character(SubCampo.SUB_CAMPO_A).equals( object[1])){
				
					autorPrincipalEvento = (String)object[2];
					evento = true;
					possuiAutorPrincipal = true;
				}
				
				if( new Character(SubCampo.SUB_CAMPO_N).equals( object[1])){
					numeroEvento = (String)object[2];
				}
				
				if( new Character(SubCampo.SUB_CAMPO_D).equals( object[1])){
					anoRealizacao = (String)object[2] ;
				}

				if( new Character(SubCampo.SUB_CAMPO_C).equals( object[1])){
					localRealizacao =  (String)object[2];
				}
				
			}
			
			
			/*
			 *  Esses tipos de refer�ncias fossem elaborados  n�o levando em condidera��o de autoria os campos 700, ficando na refer�ncia apenas como autor o que se encontra no campo 100
			 */
			if(! trabalhosAcademicos){ 
			
				if( Etiqueta.AUTOR_SECUNDARIO.getTag().equalsIgnoreCase((String)object[0])){
					
					if(new Character(SubCampo.SUB_CAMPO_A).equals(object[1])){
						autoresSegundariosPessoa.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if(new Character(SubCampo.SUB_CAMPO_C).equals(object[1])){
						autoresSegundariosPessoaDesignacao.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if(new Character(SubCampo.SUB_CAMPO_E).equals(object[1])){
						/* 
						 * Se o autor secund�rio for um ilustrador ou tradutor, n�o deve ser 
						 * considerado na gera��o do formato de refer�ncia.
						 * Caso contr�rio, prossegue a gera��o normal.
						 */
						if (  object[2] != null &&
								(   ((String) object[2]).toLowerCase().contains(TEXTO_IDENTIFICA_ILUSTRADOR) || ((String) object[2]).toLowerCase().contains(TEXTO_IDENTIFICA_TRADUTOR)   )    ){
							
							// Itera e remove da listagem //
							for (Iterator<DadosMontaFormatoReferencia> it = autoresSegundariosPessoa.iterator(); it.hasNext(); ) {
								DadosMontaFormatoReferencia autorSecundario = it.next();
								
								if (autorSecundario.idCampo.equals(object[3])) {
									it.remove();
									
									break;
								}
							}
						}
						else {
							autoresSegundariosPessoaDesignacao.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
						}
					}
					
					if(pessoa == false && evento == false && corporativo == false) // se nada foi setado pela entra principal
					pessoa = true;
				}
				
				if(  Etiqueta.AUTOR_COOPORATIVO_SECUNDARIO.getTag().equalsIgnoreCase( (String) object[0] )){
					
					if(new Character(SubCampo.SUB_CAMPO_A).equals( object[1])){
						autoresSegundariosCooporativo.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if(new Character(SubCampo.SUB_CAMPO_B).equals(object[1])){
						autoresCooporativoEntidadesSubordinada.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if(pessoa == false && evento == false && corporativo == false) // se nada foi setado pela entra principal
						corporativo = true;
				}
				
				if(  Etiqueta.AUTOR_EVENTO_SECUNDARIO.getTag().equalsIgnoreCase( (String) object[0])){
					
					if(new Character(SubCampo.SUB_CAMPO_A).equals(object[1])){
						autoresSegundariosEvento.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if( new Character(SubCampo.SUB_CAMPO_N).equals( object[1])){
						autorEventoNumerosEventos.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
				
					if( new Character(SubCampo.SUB_CAMPO_D).equals( object[1])){
						autorEventoAnosRealizacao.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
	
					if( new Character(SubCampo.SUB_CAMPO_C).equals( object[1])){
						autorEventoLocaisRealizacao.add( formatador.new DadosMontaFormatoReferencia((String) object[0], (Character) object[1], (String)object[2], (Integer)object[3], (Integer)object[4], (Integer) object[5]));
					}
					
					if(pessoa == false && evento == false && corporativo == false) // se nada foi setado pela entra principal
					evento = true;
				}
			
			}
			
		}
		
		// Ordena as informa��es dos campos pela possi��o do campo e subcampo //
		
		Collections.sort( autoresSegundariosPessoa );
		Collections.sort( autoresSegundariosCooporativo );
		Collections.sort( autoresSegundariosEvento );
		Collections.sort( autoresSegundariosPessoaDesignacao );
		Collections.sort( autoresCooporativoEntidadesSubordinada );
		Collections.sort( autorEventoNumerosEventos );
		Collections.sort( autorEventoAnosRealizacao );
		Collections.sort( autorEventoLocaisRealizacao );
		
		
		///////////////////// Conta a quantidade de autores /////////////////////////
		
		qtdAutores  = contaQuantidadeAutoresCatalogacao(autorPrincipalPessoa, autorPrincipalCooporativo, autorPrincipalEvento
				, autoresSegundariosPessoa, autoresSegundariosCooporativo, autoresSegundariosEvento
				, pessoa, corporativo, evento);
		
		
		
		/////////////////////     REGRA ENTRADA POR T�TULO     ////////////////////
		if(qtdAutores == 0){ 
			throw new AutoriaDesconhecidaException();
		}
		
		
		
		if(qtdAutores <= 3 && qtdAutores > 0){  //REGRA DE AT� 3 AUTORES
			
			if(possuiAutorPrincipal){
			
				return formataAutorMenorQueTresComAutorPrincipal(qtdAutores, autorPrincipalPessoa, autorPrincipalCooporativo, autorPrincipalEvento
							, autoresSegundariosPessoa, autoresSegundariosCooporativo, autoresSegundariosEvento
							, autorCooporativoEntidadeSubordinada, numeroEvento,  anoRealizacao,  localRealizacao
							,  autoresCooporativoEntidadesSubordinada,  autorEventoNumerosEventos,  autorEventoAnosRealizacao,  autorEventoLocaisRealizacao
							, pessoa, corporativo, evento);
				
				
			}else{   //  
				
				// REGRA ENTRADA POR ORGANIZADOR, COORDENADOR OU EDITOR ( Regra s� se aplica a pessoas ) 
				if(pessoa){
					return formataNomeAutorMenorQueTresComDesignacao(autoresSegundariosPessoa, autoresSegundariosPessoaDesignacao);
				}else{
					
					// Para eventos e corpora��es onde N�O existe a regra da entra pelo organizador
					
					return formataAutorMenorQueTresSemAutorPrincipal(qtdAutores, autoresSegundariosCooporativo, autoresSegundariosEvento
							, autorCooporativoEntidadeSubordinada, numeroEvento,  anoRealizacao,  localRealizacao
							,  autoresCooporativoEntidadesSubordinada,  autorEventoNumerosEventos,  autorEventoAnosRealizacao,  autorEventoLocaisRealizacao
							, corporativo, evento);
					
				}
				
			}
				
		}else{ // REGRA MAIS DE 3 AUTORES  (pega o primeiro autor dos campos 700 e formata com a express�o "et al." no final)
			
			if(qtdAutores > 3){
			
				if(autoresSegundariosPessoaDesignacao.size() == 0){ // N�o possui designa��o, ent�o formato o 1� autor do campo 100 ou 700 normalmente.
					
					if(pessoa){
						if(possuiAutorPrincipal){  // se tiver autor no campo 100 usa ele
							return formataEstiloNomeAutorPessoa( autorPrincipalPessoa ) + REFERENCIA_MUITOS_AUTORES;
						}else{
							if(autoresSegundariosPessoa.size() >= 1)
								return formataEstiloNomeAutorPessoa(autoresSegundariosPessoa.get(0).dados) + REFERENCIA_MUITOS_AUTORES;
						}
					}
					
					if(corporativo){
						
						if(possuiAutorPrincipal){  // se tiver autor no campo 110 usa ele
							return formataEstiloNomeAutorCorporativo(autorPrincipalCooporativo
											, autorCooporativoEntidadeSubordinada) + REFERENCIA_MUITOS_AUTORES;
						}else{
							return formataEstiloNomeAutorCorporativo(autoresSegundariosCooporativo.get(0).dados
								, getDadosCorrelatos(autoresCooporativoEntidadesSubordinada, autoresSegundariosCooporativo.get(0).idCampo)) + REFERENCIA_MUITOS_AUTORES;
						}
					}
					
					if(evento){
						if(possuiAutorPrincipal){   // se tiver autor no campo 111 usa ele
							return formataEstiloNomeAutorEvento(autorPrincipalEvento, numeroEvento, anoRealizacao, localRealizacao) + REFERENCIA_MUITOS_AUTORES;
						}else{
						
							return formataEstiloNomeAutorEvento(autoresSegundariosEvento.get(0).dados
								, getDadosCorrelatos( autorEventoNumerosEventos, autorEventoNumerosEventos.get(0).idCampo)
								, getDadosCorrelatos( autorEventoNumerosEventos, autorEventoNumerosEventos.get(0).idCampo)
								, getDadosCorrelatos( autorEventoNumerosEventos, autorEventoNumerosEventos.get(0).idCampo)) + REFERENCIA_MUITOS_AUTORES;
						}
					}
					
				}else{ //  REGRA ENTRADA POR ORGANIZADOR, COORDENADOR OU EDITOR
					
					// Regra s� se aplica a pessoas
					if(pessoa){
						return formataNomeAutorMaiorQueTresComDesignacao(autoresSegundariosPessoa, autoresSegundariosPessoaDesignacao);
					}
				}
				
			}
			
			return "";
			
		}
	
	}
	

	
	
	/**
	 * 
	 * Primeira palavra do T�tulo mai�scula, exemplo:  JAVA como programar <br/>
	 * Se a primeira palavra for um pronome, a segunda palavra fica em mai�scula tamb�m. ex.: A MORENINHA <br/>
	 * @param titulo
	 * @return
	 */
	private  String formataTituloAutoriaDesconhecidaFormatoReferencia(String titulo){
		
		String[] tituloTemp = titulo.split("\\s");
		
		StringBuilder tituloFormatado = new StringBuilder();
		
		boolean primeiraPalavra = false;
		
		for (String palavra : tituloTemp) {
			
			if( primeiraPalavra == false ){
				tituloFormatado.append(palavra.toUpperCase()+" "); 
				 // Provavelmente n�o � um pronome: "de", "a" 
				if(palavra.length() > 2)
					primeiraPalavra = true;       
			}else
				tituloFormatado.append(palavra+" ");
		}
		
		return tituloFormatado.toString();
	}
	
	/**
	 * Coloca o T�tulo em negrito, caso seja sa�da em HTML
	 *
	 * @param titulo
	 * @param negrito
	 * @return
	 */
	private  String getFormatacaoTituloReferencia(String titulo, boolean negrito){
		if(StringUtils.notEmpty(titulo))
			if(negrito)
				return INICIO_NEGRITO+titulo.trim()+FINAL_NEGRITO;
			else
				return titulo.trim();
		else
			return "";
	}
	

	
	

	/**
	 * Formato os nomes dos autores para obras que possuiem 3 ou menos autores.
	 *
	 * @param qtdAutores
	 * @param autorPrincipal
	 * @param autoresSegundarios
	 * @return
	 */
	private  String formataAutorMenorQueTresComAutorPrincipal(int qtdAutores, String autorPrincipalPessoa, String autorPrincipalCorporativo, String autorPrincipalEvento
			, List<DadosMontaFormatoReferencia> autoresSegundariosPessoa, List<DadosMontaFormatoReferencia> autoresSegundariosCorporativo, List<DadosMontaFormatoReferencia> autoresSegundariosEvento
			, String autorCooporativoEntidadeSubordinada, String numeroEvento, String anoRealizacao, String localRealizacao
			,  List<DadosMontaFormatoReferencia> autoresCooporativoEntidadesSubordinada
			,  List<DadosMontaFormatoReferencia> autorEventoNumerosEventos,  List<DadosMontaFormatoReferencia> autorEventoAnosRealizacao, List<DadosMontaFormatoReferencia> autorEventoLocaisRealizacao
			, boolean pessoa,  boolean corporacao, boolean evento){
		
		if(qtdAutores == 1){ // REGRA DO UM AUTOR
			
			if(pessoa)
				return formataEstiloNomeAutorPessoa(autorPrincipalPessoa);
			
			if(corporacao)
				return formataEstiloNomeAutorCorporativo(autorPrincipalCorporativo, autorCooporativoEntidadeSubordinada);
			
			if(evento)
				return formataEstiloNomeAutorEvento(autorPrincipalEvento, numeroEvento, anoRealizacao, localRealizacao);
			
			return "";
			
		}else{              // REGRA At� tr�s autores
			
			if(pessoa){
				StringBuilder retornoTemp = new StringBuilder(  formataEstiloNomeAutorPessoa(autorPrincipalPessoa)+SEPARADOR_PONTO_VIRGULA );
				
				
				int quantidadesAutores = autoresSegundariosPessoa.size();
				
				for (int contador = 0; contador < autoresSegundariosPessoa.size() ; contador++) {
						
					if(contador == quantidadesAutores-1){                 // �ltimo autor
						retornoTemp.append( formataEstiloNomeAutorPessoa(autoresSegundariosPessoa.get(contador).dados) );
						
					}else{
						retornoTemp.append( formataEstiloNomeAutorPessoa(autoresSegundariosPessoa.get(contador).dados)+SEPARADOR_PONTO_VIRGULA);
					}
				}
				
				return retornoTemp.toString();
			}
			
			if(corporacao){
				StringBuilder retornoTemp = new StringBuilder(  formataEstiloNomeAutorCorporativo(autorPrincipalCorporativo, autorCooporativoEntidadeSubordinada) +SEPARADOR_PONTO_VIRGULA );
				
				// OBSERVA��O:  Pelos regras, diferentemente do que ocorre com nomes pessoais, quando possuir entra principal n�o considera os autores segund�rios
				
//				int quantidadesAutores = autoresSegundariosCorporativo.size();
//				
//				for (int contador = 0; contador < autoresSegundariosCorporativo.size() ; contador++) {
//						
//					if(contador == quantidadesAutores-1){                 // �ltimo autor
//						retornoTemp.append( formataEstiloNomeAutorCorporativo(autoresSegundariosCorporativo.get(contador).dados, 
//								getDadosCorrelatos(autoresCooporativoEntidadesSubordinada, autoresSegundariosCorporativo.get(contador).idCampo) ) );
//						
//					}else{
//						retornoTemp.append( formataEstiloNomeAutorCorporativo(autoresSegundariosCorporativo.get(contador).dados, 
//								getDadosCorrelatos(autoresCooporativoEntidadesSubordinada, autoresSegundariosCorporativo.get(contador).idCampo) ) + SEPARADOR_PONTO_VIRGULA);
//					}
//					
//				}
				
				return retornoTemp.toString();
			}
			
			if(evento){
				StringBuilder retornoTemp = new StringBuilder(  formataEstiloNomeAutorEvento(autorPrincipalEvento, numeroEvento, anoRealizacao, localRealizacao) + SEPARADOR_PONTO_VIRGULA );
				
				// OBSERVA��O: Pelos regras, diferentemente do que ocorre com nomes pessoais, quando possuir entra principal n�o considera os eventos segund�rios
				
//				int quantidadesAutores = autoresSecundariosEvento.size();
//				
//				for (int contador = 0; contador < autoresSecundariosEvento.size() ; contador++) {
//						
//					if(contador == quantidadesAutores-1){                 // �ltimo autor
//						retornoTemp.append( formataEstiloNomeAutorEvento(autoresSecundariosEvento.get(contador).dados
//								,getDadosCorrelatos( autorEventoNumerosEventos, autoresSecundariosEvento.get(contador).idCampo)
//								,getDadosCorrelatos( autorEventoAnosRealizacao, autoresSecundariosEvento.get(contador).idCampo)
//								,getDadosCorrelatos( autorEventoLocaisRealizacao, autoresSecundariosEvento.get(contador).idCampo) ) );
//						
//					}else{
//						retornoTemp.append( formataEstiloNomeAutorEvento(autoresSecundariosEvento.get(contador).dados
//								,getDadosCorrelatos( autorEventoNumerosEventos, autoresSecundariosEvento.get(contador).idCampo)
//								,getDadosCorrelatos( autorEventoAnosRealizacao, autoresSecundariosEvento.get(contador).idCampo)
//								,getDadosCorrelatos( autorEventoLocaisRealizacao, autoresSecundariosEvento.get(contador).idCampo) )+ SEPARADOR_PONTO_VIRGULA);
//					}
//					
//				}
				
				return retornoTemp.toString();
			}
			
			return "";
		}
	}
	
	
	/**
	 *     Formato os nomes dos autores para obras que possuiem 3 ou menos autores e n�o possui autor 
	 * principal <strong>(Caso s� v�lido para nomes corporativos e eventos) </strong>
	 *
	 * @param qtdAutores
	 * @param autorPrincipal
	 * @param autoresSecundarios
	 * @return
	 */
	private  String formataAutorMenorQueTresSemAutorPrincipal(int qtdAutores,
			List<DadosMontaFormatoReferencia> autoresSecundariosCorporativo, List<DadosMontaFormatoReferencia> autoresSecundariosEvento
			, String autorCooporativoEntidadeSubordinada, String numeroEvento, String anoRealizacao, String localRealizacao
			,  List<DadosMontaFormatoReferencia> autoresCooporativoEntidadesSubordinada
			,  List<DadosMontaFormatoReferencia> autorEventoNumerosEventos,  List<DadosMontaFormatoReferencia> autorEventoAnosRealizacao, List<DadosMontaFormatoReferencia> autorEventoLocaisRealizacao
			, boolean corporacao, boolean evento){
		
		if(qtdAutores == 1){ // REGRA DO UM AUTOR
			
			if(corporacao)
				return formataEstiloNomeAutorCorporativo(autoresSecundariosCorporativo.get(0).dados, autorCooporativoEntidadeSubordinada);
			
			if(evento)
				return formataEstiloNomeAutorEvento(autoresSecundariosEvento.get(0).dados, numeroEvento, anoRealizacao, localRealizacao);
			
			return "";
			
		}else{              // REGRA At� tr�s autores
			
			
			
			if(corporacao){
				StringBuilder retornoTemp = new StringBuilder();
				
				int quantidadesAutores = autoresSecundariosCorporativo.size();
				
				for (int contador = 0; contador < autoresSecundariosEvento.size() ; contador++) {
						
					if(contador == quantidadesAutores-1){                 // �ltimo autor
						retornoTemp.append( formataEstiloNomeAutorCorporativo(autoresSecundariosCorporativo.get(contador).dados
								, getDadosCorrelatos(autoresCooporativoEntidadesSubordinada, autoresSecundariosCorporativo.get(contador).idCampo) ) );
						
					}else{
						retornoTemp.append( formataEstiloNomeAutorCorporativo(autoresSecundariosCorporativo.get(contador).dados
								, getDadosCorrelatos(autoresCooporativoEntidadesSubordinada, autoresSecundariosCorporativo.get(contador).idCampo) ) + SEPARADOR_PONTO_VIRGULA);
					}
					
				}
				
				return retornoTemp.toString();
			}
			
			if(evento){
				StringBuilder retornoTemp = new StringBuilder();
				
				int quantidadesAutores = autoresSecundariosEvento.size();
				
				for (int contador = 0; contador < autoresSecundariosEvento.size() ; contador++) {
						
					if(contador == quantidadesAutores-1){                 // �ltimo autor
						retornoTemp.append( formataEstiloNomeAutorEvento(autoresSecundariosEvento.get(contador).dados
								,getDadosCorrelatos( autorEventoNumerosEventos, autoresSecundariosEvento.get(contador).idCampo)
								,getDadosCorrelatos( autorEventoAnosRealizacao, autoresSecundariosEvento.get(contador).idCampo)
								,getDadosCorrelatos( autorEventoLocaisRealizacao, autoresSecundariosEvento.get(contador).idCampo)) );
						
					}else{
						retornoTemp.append( formataEstiloNomeAutorEvento(autoresSecundariosEvento.get(contador).dados
								,getDadosCorrelatos( autorEventoNumerosEventos, autoresSecundariosEvento.get(contador).idCampo)
								,getDadosCorrelatos( autorEventoAnosRealizacao, autoresSecundariosEvento.get(contador).idCampo)
								,getDadosCorrelatos( autorEventoLocaisRealizacao, autoresSecundariosEvento.get(contador).idCampo) )+ SEPARADOR_PONTO_VIRGULA);
					}
					
				}
				
				return retornoTemp.toString();
			}
			
			return "";
		}
	}
	

	/**
	 * Formato os nomes dos autores para obras que possuiem 3 ou menos autores e n�o possuem autor principal
	 *
	 * @param qtdAutores
	 * @param autorPrincipal
	 * @param autoresSecundarios
	 * @return
	 */
	private  String formataNomeAutorMenorQueTresComDesignacao(List<DadosMontaFormatoReferencia> autoresSecundarios
			, List<DadosMontaFormatoReferencia> autoresSecundariosDesignacao){
			
		StringBuilder retornoTemp = new StringBuilder();
		
		int quantidadesAutores = autoresSecundarios.size();
		
		String designacao = "";
		
		for (int contador = 0; contador < autoresSecundarios.size() ; contador++) {
			
			if(contador == 0 || StringUtils.isEmpty(designacao)){ // Pega a primeira designa��o, caso exista mais de 1
				 designacao = getDadosCorrelatos( autoresSecundariosDesignacao, autoresSecundarios.get(contador).idCampo);
			}
			
			if(contador == quantidadesAutores-1){ // Se chegou no �ltimo autor
				
				retornoTemp.append( formataEstiloNomeAutorPessoa(autoresSecundarios.get(contador).dados) );  //Pega o primeiro nome do campo
				
				if(designacao != null){ // autor possui desgina��o
					retornoTemp.append( " ("+designacao+") " );  // IMPORTANTE: s� exibe a designa��o no final.
				}
				
			}else{
				if(designacao != null){ // autor possui desgina��o
					
					retornoTemp.append( formataEstiloNomeAutorPessoa(autoresSecundarios.get(contador).dados) + SEPARADOR_PONTO_VIRGULA);
				}else
					retornoTemp.append( formataEstiloNomeAutorPessoa(autoresSecundarios.get(contador).dados) + SEPARADOR_PONTO_VIRGULA);
			}
			
		}
		
		return retornoTemp.toString();
		
	}
	
	
	
	/**
	 * 
	 * Formato os autores quando a quantidade � maior que 3 e a cataloga��o n�o possui autor principal
	 *
	 * @param autoresSecundarios
	 * @param autoresSecundariosDesignacao
	 * @return
	 */
	private  String formataNomeAutorMaiorQueTresComDesignacao(List<DadosMontaFormatoReferencia> autoresSecundarios, List<DadosMontaFormatoReferencia> autoresSecundariosDesignacao){
		
		String formatoAutor = formataEstiloNomeAutorPessoa(autoresSecundarios.get(0).dados) + REFERENCIA_MUITOS_AUTORES;
		
		String designacao = getDadosCorrelatos(autoresSecundariosDesignacao, autoresSecundarios.get(0).idCampo);
		
		if(designacao != null){
			return formatoAutor +" ( "+designacao+" ) ";
		}else{
			return formatoAutor;
		}
	}
	
	
	
	
	
	/**
	 * Formato o nome o estilo do nome do autor acordo com a regra:   <strong> SOBRENOME, Nome. <strong>
	 *
	 * @param nomeautor
	 */
	private  String formataEstiloNomeAutorPessoa(String nomeCompletoAutor){
		
		nomeCompletoAutor = nomeCompletoAutor.trim();
		
		String sobreNome = "";
		String primeiroNome = "";
		
		if(nomeCompletoAutor.contains(",")){  // Ex.: Assis, Machado de
			String [] aux = nomeCompletoAutor.split(","); // separa os nomes que componhem o autor
			
			if(aux.length == 2){
				sobreNome = aux[0]; // A primeira posi��o � o sobre nome, j� que j� � catalogado ao contr�rio.
				primeiroNome = aux[1]; // O segunda posi��o � o primeiro nome, j� que j� � catalogado ao contr�rio.
			}
			
			if(aux.length == 1){
				sobreNome = aux[0]; // A primeira posi��o � o sobre nome, j� que j� � catalogado ao contr�rio.
			}
			
			// Caso tenha mais de uma vircula no nome do autor (Essa situ��o n�o era para ocorrer )
			if(aux.length > 2){
				sobreNome = aux[0]; // A primeira posi��o � o sobre nome, j� que j� � catalogado ao contr�rio.
				
				for (int a = 1; a < aux.length ; a++) {
					primeiroNome += aux[a]; // O segunda posi��o � o primeiro nome, j� que j� � catalogado ao contr�rio.
				}
				
			}
			
		}else{
			
			String [] aux = nomeCompletoAutor.split(" "); // ex.: Biblioteca Nacional 
			
			if(aux.length > 0){ //Se existem nomes separados por espa�o
				
				int ultimaPosicao = aux.length-1;
				
				sobreNome = aux[ultimaPosicao]; // A �ltima palavra vai ser o sobre nome
			
				while( (StringUtils.isEmpty(sobreNome) || sobreNome.length() <= 2) && ultimaPosicao > 0){      // O sobre nome tem que ter mais de 2 letras
					ultimaPosicao--; // paga a palavra anterior
					sobreNome = aux[ultimaPosicao];
				}
				
				if((StringUtils.isEmpty(sobreNome) || sobreNome.length() <= 2))
					sobreNome = "";
				
				for(int ptr = 0 ;  ptr < ultimaPosicao; ptr++){   // Primerio nome � o resto de senten�a
					primeiroNome += aux[ptr]+" ";
				}
			}
			
		}
		
		if(StringUtils.notEmpty(sobreNome) && StringUtils.notEmpty(primeiroNome))  return sobreNome.toUpperCase()+", "+primeiroNome;
		if(StringUtils.notEmpty(sobreNome))  return sobreNome.toUpperCase();
		if( StringUtils.notEmpty(primeiroNome))  return primeiroNome.toUpperCase();
			
		return ""; // Os dois s�o fazios
	}
	
	
	
	
	/**
	 * Formato o nome o estilo do nome do autor acordo com a regra:   <strong> NOME ENTIDADE, Nome Entidade Subornidada. <strong>
	 *
	 * @param nomeautor
	 */
	private  String formataEstiloNomeAutorCorporativo(String nomeCorporacao, String unidadeSubordinada){
		
		StringBuilder retorno = new StringBuilder(nomeCorporacao.toUpperCase());
		
		if(StringUtils.notEmpty(unidadeSubordinada)){
			
			for (String palavra : unidadeSubordinada.split("\\s")) {
				retorno.append( " "+StringUtils.primeriaMaiuscula(palavra) );
			}
			
			
		}
		
		return retorno.toString(); 
	}
	
	
	/**
	 * Formato o nome o estilo do nome do autor acordo com a regra:   <strong> NOME ENTIDADE, Numero Evento Ano Realizaaco Local Realizacao <strong>
	 *
	 * @param nomeautor
	 */
	private  String formataEstiloNomeAutorEvento(String nomeEvento, String numeroEvento, String anoRealizacao, String localRealizacao){
		
		StringBuilder retorno = new StringBuilder(nomeEvento.toUpperCase());
		
		if(StringUtils.notEmpty(numeroEvento)){
			
			//for (String palavra : numeroEvento.split("\\s")) {
				retorno.append( ", "+StringUtils.primeriaMaiuscula(numeroEvento) );
			//}
			
			
		}
		
		if(StringUtils.notEmpty(anoRealizacao)){
			
			//for (String palavra : anoRealizacao.split("\\s")) {
				retorno.append( ", "+StringUtils.primeriaMaiuscula(anoRealizacao) );
			//}
			
			
		}

		if(StringUtils.notEmpty(localRealizacao)){
			
			//for (String palavra : localRealizacao.split("\\s")) { // Na localiza��o, n�o coloca as primeiras letras em mai�scula, deixa do jeito que est�.
				retorno.append( ", "+localRealizacao );
			//}
			
			
		}
		
		return retorno.toString(); 
	}
	
	
	/**
	 * Conta a quantidade de autores presentes na cataloga��o
	 *
	 * @param autorPrincipalPessoa
	 * @param autorPrincipalCooporativo
	 * @param autorPrincipalEvento
	 * @param autoresSecundariosPessoa
	 * @param autoresSecundariosCooporativo
	 * @param autoresSecundariosEvento
	 * @param pessoa
	 * @param cooporativo
	 * @param evento
	 * @return
	 */
	private  int contaQuantidadeAutoresCatalogacao(final String autorPrincipalPessoa, final String autorPrincipalCooporativo, final String autorPrincipalEvento,
			final List<DadosMontaFormatoReferencia> autoresSecundariosPessoa, final List<DadosMontaFormatoReferencia> autoresSecundariosCooporativo, final List<DadosMontaFormatoReferencia> autoresSecundariosEvento
			, final boolean pessoa, final boolean cooporativo, final boolean evento) {
		
		int qtdAutores = 0;
		
		if(pessoa){
			if( StringUtils.notEmpty( autorPrincipalPessoa ) )qtdAutores+=1;
			qtdAutores+= autoresSecundariosPessoa.size();
		}
		
		if(cooporativo){
			if( StringUtils.notEmpty( autorPrincipalCooporativo ) )qtdAutores+=1;
			qtdAutores+= autoresSecundariosCooporativo.size();
		}
		
		if(evento){
			if( StringUtils.notEmpty( autorPrincipalEvento ) ) qtdAutores+=1;
			qtdAutores+= autoresSecundariosEvento.size();
		}
		return qtdAutores;
	}
	
	
	
	
	
	/**
	 * Retorna os dados da lista auxiliar passada que correpondente ao campo do autor (Se existirem)
	 */
	private  String getDadosCorrelatos(List<DadosMontaFormatoReferencia> lista, int idCampo){
		
		String temp = null;
		
		for (DadosMontaFormatoReferencia dadosMontaFormatoReferencia : lista) {
			
			if(dadosMontaFormatoReferencia.idCampo.equals( idCampo)){
			
				if(temp == null)
					temp = dadosMontaFormatoReferencia.dados;
				else
					temp += " "+dadosMontaFormatoReferencia.dados;
			
			}
			
		}
	
		return temp;
	}
	

	/**
	 *  Formata a pontua��o do T�tulo para o formato de refer�ncia
	 * @param titulo
	 * @param subTitulo
	 * @return
	 */
	private  String getPontuacaoTitulo(String titulo, String subTitulo){
		if(StringUtils.notEmpty(titulo) && StringUtils.notEmpty(subTitulo))
			return titulo.trim()+SEPARADOR_DOIS_PONTOS+subTitulo.trim()+SEPARADOR_PONTO;
		else{
			if(StringUtils.notEmpty(titulo) )
				return titulo.trim()+SEPARADOR_PONTO;
			if(StringUtils.notEmpty(subTitulo) )
				return subTitulo.trim()+SEPARADOR_PONTO;
			
			return "";
		}
	}
	
	
	/**
	 *  Formata a pontua��o do campo s�rie para o formato de refer�ncia
	 * @param serie
	 * @return
	 */
	private  String getPontuacaoSerie(String serie){
		if(StringUtils.notEmpty(serie))
			return SEPARADOR_ABRE_PARENTESES+serie.trim()+SEPARADOR_FECHA_PARENTESES;
		else
			return "";
	}
	
	
	

	/**
	 * <p>Classe interna auxiliar para gera��o do formato de refer�ncia   </p>
	 *
	 * 
	 * @author jadson
	 *
	 */
	private class DadosMontaFormatoReferencia implements Comparable<DadosMontaFormatoReferencia>{
		
//		/**
//		 * A tag do campo. ex: 250, 100, 020, etc... Serve para identificar o campo. Ver padr�o marc: http://www.loc.gov/marc/
//		 */
//		String tagCampo;
//		
//		/**
//		 * O c�digo do sub campo, serve para identificar o sub campo. a, b, c, ... z. Ver padr�o marc: http://www.loc.gov/marc/
//		 */
//		Character codigoSubcampo;
		
		/**
		 * o is do campo de dados
		 */
		Integer idCampo;
		
		/**
		 * A posic�o do campo detro da cataloga��o
		 */
		Integer posicaoCampo;
		
		/**
		 * A posic�o do sub campo detro da cataloga��o
		 */
		Integer posicaoSubCampo;
		
		/**
		 * Os dados que o campo. S�o os dado digitados pelo usu�rio na cataloga��o e que v�o servir para gerar a refer�ncia.
		 */
		String dados;

		
		/**
		 * Construtor padr�o
		 * @param tagCampo
		 * @param codigoSubcampo
		 * @param dados
		 * @param idCampo
		 * @param posicaoCampo
		 * @param posicaoSubCampo
		 */
		public DadosMontaFormatoReferencia(String tagCampo, Character codigoSubcampo, String dados, Integer idCampo,Integer posicaoCampo, Integer posicaoSubCampo) {
			this.idCampo = idCampo;
			this.posicaoCampo = posicaoCampo;
			this.posicaoSubCampo = posicaoSubCampo;
			this.dados = dados;
//			this.tagCampo = tagCampo;
//			this.codigoSubcampo = codigoSubcampo;
		}

		/**
		 * Compara se dois objetos DadosMontaFormatoReferencia s�o iguais.
		 * 
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DadosMontaFormatoReferencia o) {
			int resultado = posicaoCampo.compareTo(o.posicaoCampo);
			
			if(resultado == 0)
				return posicaoSubCampo.compareTo(o.posicaoSubCampo);
			else
				return resultado;
			
		}

		/**
		 * Calcula hashcode do objeto DadosMontaFormatoReferencia.
		 * 
		 * @see java.lang.Object#hashCode()
		 */
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((idCampo == null) ? 0 : idCampo.hashCode());
			result = prime * result
					+ ((posicaoCampo == null) ? 0 : posicaoCampo.hashCode());
			result = prime
					* result
					+ ((posicaoSubCampo == null) ? 0 : posicaoSubCampo
							.hashCode());
			return result;
		}

		/**
		 * Verifica se dois objetos DadosMontaFormatoReferencia s�o iguais
		 * 
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DadosMontaFormatoReferencia other = (DadosMontaFormatoReferencia) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (idCampo == null) {
				if (other.idCampo != null)
					return false;
			} else if (!idCampo.equals(other.idCampo))
				return false;
			if (posicaoCampo == null) {
				if (other.posicaoCampo != null)
					return false;
			} else if (!posicaoCampo.equals(other.posicaoCampo))
				return false;
			if (posicaoSubCampo == null) {
				if (other.posicaoSubCampo != null)
					return false;
			} else if (!posicaoSubCampo.equals(other.posicaoSubCampo))
				return false;
			return true;
		}

		private FormatosBibliograficosUtil getOuterType() {
			return FormatosBibliograficosUtil.this;
		}
		
	}
	
	
	
	////////////////////////////  FIM DA GERA��O DO FORMATO DE REFER�NCIA ///////////////////////
	
	
	
	
	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////// PARTE DA GERA��O DA FICHA CATALOGR�FICA ////////////////////////////
	
	

	/**
	 *     <p>M�todo que gera a ficha catalogr�fica utilizando na sess�o de informa��es e refer�ncia. � uma adapta��o para utilizar o 
	 * m�todo padr�o de gera��o de ficha catalogr�fica. </p>
	 *
	 *     <p>Os dados s�o digitados pelo usu�rio, n�o s�o obtidos de nenhuma cataloga��o </p>
	 */
	public String gerarFichaCatalografica (String titulo, String responsabilidade, String autor, String edicao
			, String localPublicacao, String editora, String ano
			, String descricaoFisica, String descricaoFisicaDetalhes, String descricaoFisicaDimensao, String descricaoFisicaMaterialAcompanha
			, String serie, List <String> notasGerais, List <String> notasTeses, List <String> notasBibiliograficas, List <String> notasConteudo
			, String isbn, String issn 
			, List<String> assuntosPessoais, List<String> assuntos
			, List<String> autoresSecundarios, String bibliotecaAtendimento, String descricaoClassificacao, String classificacao
			, boolean saidaEmHTML, int larguraFicha){
		
		
		// Object[0] = 245     (campo)
		// Object[1] = a       (subcampo)
		// Object[2] = "...."  (dados)
		// Object[3] = "...."  (id campo dados)
		// Object[4] = "...."  (posicao campo dados)
		// Object[5] = "...."  (posicao sub campo)
		
		List<Object[]> dadosCampo = new ArrayList<Object[]>();
		
		 // 245     (tag), a       (c�digo subcampo), dado, idCampoDados, posicaoCampoDados, posicaoSubCampo
		if(titulo != null) dadosCampo.add( new Object[]{Etiqueta.TITULO.getTag(), SubCampo.SUB_CAMPO_A, titulo, 0, 0, 0});
		if(responsabilidade != null) dadosCampo.add( new Object[]{Etiqueta.TITULO.getTag(), SubCampo.SUB_CAMPO_C, responsabilidade, 0, 0, 0});
		if(autor != null) dadosCampo.add( new Object[]{Etiqueta.AUTOR.getTag(), SubCampo.SUB_CAMPO_A, autor, 0, 0, 0});
		if(edicao != null)  dadosCampo.add( new Object[]{Etiqueta.EDICAO.getTag(), SubCampo.SUB_CAMPO_A, edicao, 0, 0, 0});
		if(localPublicacao != null)  dadosCampo.add( new Object[]{Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), SubCampo.SUB_CAMPO_A, localPublicacao, 0, 0, 0});
		if(editora != null)  dadosCampo.add( new Object[]{Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), SubCampo.SUB_CAMPO_B, editora, 0, 0, 0});
		if(ano != null) dadosCampo.add( new Object[]{Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), SubCampo.SUB_CAMPO_C, ano, 0, 0, 0});
		if(serie != null)  dadosCampo.add( new Object[]{Etiqueta.SERIE.getTag(), SubCampo.SUB_CAMPO_A, serie, 0, 0, 0});
		if(isbn != null)  dadosCampo.add( new Object[]{Etiqueta.ISBN.getTag(), SubCampo.SUB_CAMPO_A, isbn, 0, 0, 0});
		if(issn != null)  dadosCampo.add( new Object[]{Etiqueta.ISSN.getTag(), SubCampo.SUB_CAMPO_A, issn, 0, 0, 0});
		
		if(descricaoFisica != null)  dadosCampo.add( new Object[]{Etiqueta.DESCRICAO_FISICA.getTag(), SubCampo.SUB_CAMPO_A, descricaoFisica, 0, 0, 0});
		if(descricaoFisicaDetalhes != null)  dadosCampo.add( new Object[]{Etiqueta.DESCRICAO_FISICA.getTag(), SubCampo.SUB_CAMPO_B, descricaoFisicaDetalhes, 0, 0, 0});
		if(descricaoFisicaDimensao != null)  dadosCampo.add( new Object[]{Etiqueta.DESCRICAO_FISICA.getTag(), SubCampo.SUB_CAMPO_C, descricaoFisicaDimensao, 0, 0, 0});
		if(descricaoFisicaMaterialAcompanha != null) dadosCampo.add( new Object[]{Etiqueta.DESCRICAO_FISICA.getTag(), SubCampo.SUB_CAMPO_E, descricaoFisicaMaterialAcompanha, 0, 0, 0});
		
		for (String notaGeral : notasGerais) {
			dadosCampo.add( new Object[]{Etiqueta.NOTA_GERAL.getTag(), SubCampo.SUB_CAMPO_A, notaGeral, 0, 0, 0});
		}
		
		for (String notaTese : notasTeses) {
			dadosCampo.add( new Object[]{Etiqueta.NOTA_DISSETACAO_TESE.getTag(), SubCampo.SUB_CAMPO_A, notaTese, 0, 0, 0});
		}
		
		for (String notaBibliotegrafica : notasBibiliograficas) {
			dadosCampo.add( new Object[]{Etiqueta.NOTA_BIBLIOGRAFICA.getTag(), SubCampo.SUB_CAMPO_A, notaBibliotegrafica, 0, 0, 0});
		}
		
		for (String notaConteudo : notasConteudo) {
			dadosCampo.add( new Object[]{Etiqueta.NOTA_DE_CONTEUDO.getTag(), SubCampo.SUB_CAMPO_A, notaConteudo, 0, 0, 0});
		}
		
		for (String assuntoPessoais : assuntosPessoais) {
			dadosCampo.add( new Object[]{Etiqueta.ASSUNTO_PESSOAL.getTag(), SubCampo.SUB_CAMPO_A, assuntoPessoais, 0, 0, 0});
		}
		
		// Gera ids tempor�rios para os campos de dados gerados a partir da ficha de Inf. e Ref, porque 
		// informa��es de assuntos s�o gerados por ids
		int contAssunto = 1;
		
		for (String assunto : assuntos) {
			dadosCampo.add( new Object[]{Etiqueta.ASSUNTO.getTag(), SubCampo.SUB_CAMPO_A, assunto, contAssunto++, 0, 0});
		}
		
		for (String autoresSecudarios : autoresSecundarios) {
			dadosCampo.add( new Object[]{Etiqueta.AUTOR_SECUNDARIO.getTag(), SubCampo.SUB_CAMPO_A, autoresSecudarios, 0, 0, 0});
		}
		
		
		TituloCatalografico tituloCat = new TituloCatalografico();
		
		/**
		 * Gera a ficha comum e depois adiciona as informa��es exclusivas da ficha gerada em informar��o e refer�ncia.
		 */
		String[] fichaComum = gerarFichaCatalografica(tituloCat, dadosCampo, saidaEmHTML);
		
		String fichaInformacaoReferencia = fichaComum[1];
		
		fichaInformacaoReferencia += getSeparadorNovaLinha()+getSeparadorNovaLinha();
		
		// CDU, CDD ou BLACK
		String classficacao = descricaoClassificacao+" " + (classificacao != null ? classificacao : "");
		String identificacaoBiblioteca =  
			ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.UNIDADE_FEDERAL_IMPRESSO_FICHA_CATALOGRAFICA) 
			+ bibliotecaAtendimento;
		
		fichaInformacaoReferencia +=  identificacaoBiblioteca 
		+ geraEspacamento(identificacaoBiblioteca.length(), larguraFicha, classficacao.length() )
		+ classficacao; 
		
		fichaInformacaoReferencia += getSeparadorNovaLinha();//+getSeparadorNovaLinha();
		
		return fichaInformacaoReferencia;
	}

	
	
	/**
	 *     <p>M�todo que gera a ficha catalogr�fica utilizando na sess�o de informa��es e refer�ncia. � uma adapta��o para utilizar o 
	 * m�todo padr�o de gera��o de ficha catalogr�fica. </p>
	 *
	 *     <p>Os dados s�o digitados pelo usu�rio, n�o s�o obtidos de nenhuma cataloga��o </p>
	 */
	public String gerarFichaCatalografica (FichaCatalografica fichaCatalografica, boolean saidaEmHTML, int larguraFicha) {
		return gerarFichaCatalografica(fichaCatalografica.getTitulo(),
				fichaCatalografica.getResponsabilidade(),
				fichaCatalografica.getAutor(), 
				fichaCatalografica.getEdicao(), 
				fichaCatalografica.getLocalPublicacao(), 
				fichaCatalografica.getEditora(), 
				String.valueOf(fichaCatalografica.getAno()), 
				fichaCatalografica.getDescricaoFisica(), 
				fichaCatalografica.getDescricaoFisicaDetalhes(),  
				fichaCatalografica.getDescricaoFisicaDimensao(), 
				fichaCatalografica.getDescricaoFisicaMaterialAcompanha(), 
				fichaCatalografica.getSerie(), 
				fichaCatalografica.getNotasGerais(), 
				fichaCatalografica.getNotasTeses(), 
				fichaCatalografica.getNotasBibliograficas(), 
				fichaCatalografica.getNotasConteudo(), 
				fichaCatalografica.getIsbn(), 
				fichaCatalografica.getIssn(), 
				fichaCatalografica.getAssuntosPessoais(), 
				fichaCatalografica.getAssuntos(), 
				fichaCatalografica.getAutoresSecundarios(), 
				fichaCatalografica.getBiblioteca(), 
				fichaCatalografica.getDescricaoClassificacao(),
				fichaCatalografica.getClassificacao(), 
				saidaEmHTML, 
				larguraFicha);
	}
	
	
	/**
	 * M�todo que gera o espa�amento necess�rio para formatar as informa��es da ficha 
	 *
	 * @param tamanhoIdentificador
	 * @param larguraFicha
	 * @param tamanhoCDU
	 * @return
	 */
	private String geraEspacamento(int tamanhoIdentificador, int larguraFicha, int tamanhoCDU){
		
		StringBuilder espacamento = new StringBuilder();
		
		for (int i = 0; i < larguraFicha-(tamanhoIdentificador+tamanhoCDU); i++) {
			if(saidaEmHTML){
				espacamento.append(" &nbsp; ");
			}else
				espacamento.append(" ");
		}
		
		return espacamento.toString();
	}
	
	
	/**
	 *     M�todo que gera a ficha catalogr�fica de algum T�tulo j� salvo no acervo  das bibliotecas.
	 *
	 * @param objTitulo
	 * @param emHtml
	 * @return  [0] classfica��o da ficha catalogr�fica, [1] a ficha catalogr�fica em si. 
	 * @throws DAOException 
	 */
	public String[] gerarFichaCatalografica (TituloCatalografico tituloCat, List<Object[]> dadosCampo, boolean saidaEmHTML){

		this.saidaEmHTML = saidaEmHTML;
		
		////////////////////////////////////////////////////////////////////////////////////////////////////		
		//
		//		100a ou 110a 110b ou ainda 111a 111n 111d 111c.
		//        245a 245h 245b 245c 250a 250b 260a 260b 260c. (CONTINUA NA PRIMEIRA MARGEM, CONFORME ESSE MODELO).
		//        300a 300b 300c 300e 440a 440x 440v 440p ou 490a 490v
		//        
		//        500a
		//        502a
		//        504a
		//        505a (CADA NOTA DEVE FICAR EM UM PAR�GRAFO ESPEC�FICO)
		//        ISBN: 020
		//        
		//        1. 600a 650a 650v 650x 650y 650z. 2. 650a 650v 650x 650y 650z. (REPETIR QUANTAS VEZES APARECER O CAMPO 650). I. 700a. II. 700a.  
		//     III. 710� 710b IV. 711a 711b. V. 711n 711d 711c. VI. 245a. VI. 440a ou 490a.
		//		
		////////////////////////////////////////////////////////////////////////////////////////////////////

		
		String[] retorno = new String[2];
		
		String autor = "";                    // 100a ou 110a 110b ou ainda 111a 111n 111d 111c.
		
		String edicao = "";      	          // 250a 250b
		String local = "";                    // 260a
		String editora = "";                  // 260b
		String ano = "";                      // 260c.
		String descricaoFisica = "";                  // 300a
		String descricaoFisicaDetalhes = "";          // 300b
		String descricaoFisicaDimensao = "";          // 300c
		String descricaoFisicaMaterialAcompanha = ""; // 300e
		String serieTitle     = "";                   // 440a 
		String serieISSN      = "";                   // 440x 
		String serieVolume    = "";                   // 440v
		String serieParte     = "";                   // 440p 
		String serie          = "";                   // 490a 
		String serieSequencial= "";                   // 490v
		List <String> notasGerais = new ArrayList<String>();              // 500a 
		List <String> notasTeses = new ArrayList<String>();               // 502a 
		List <String> notasBibiliograficas = new ArrayList<String>();     // 504a
		List <String> notasConteudo = new ArrayList<String>();          // 505a
		//String isbn = "";                     // 020
		String issn = "";                     // 022   
		List<String> assuntosPessoais = new ArrayList<String>();          // 600a 
		List<String> assuntos = new ArrayList<String>();                 // 650a 650v 650x 650y 650z.
		
		List<String> autoresSecundariosPessoa = new ArrayList<String>();      // 700a 
		List<String> autoresSecundariosCorporacao = new ArrayList<String>();   // 710a 710b 
		List<String> autoresSecundariosEvento = new ArrayList<String>();       // 711a 711n 711d 711c
		String tituloRodape = "T�tulo";             //  Sempre aparece essa palavra fixa
		String serieRodape = "";              // 440a ou 490a.
		
			
			
			
		
		/* ********************************************************
		 * Retira toda a pontua��o AACR2 colocada na cataloga��o, apezar dele servir exatamente para gera��o da ficha catalogr�fica.
		 * O sistema vai gerar novamente para fica independente dela ter sido colocada na cataloga��o ou n�o.
		 * ********************************************************/
		for (Object[] objects : dadosCampo) {
			objects[2] = CatalogacaoUtil.retiraPontuacaoAACR2( (String) objects[2], campoPermaneceComPontuacaoEmPar(objects)); 
		}
		
		
		retorno[0]  = recuperaValorDoCampoClassificacaoFichaCatalografica(Etiqueta.NUMERO_CHAMADA.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_B}, dadosCampo) ;
		
		boolean autoriaDesconhecida = false;
		
		try {
			autor = recuperaAutorFichaCatalografica(dadosCampo);
		} catch (AutoriaDesconhecidaException e) {
			autoriaDesconhecida = true;
		}
		
		StringBuilder retorno1Temp = new StringBuilder();
		
		if(!autoriaDesconhecida){
			retorno1Temp.append(obtemAutorFichaCatalografica(autor));
		}
		
		retorno1Temp.append(obtemTituloFichaCatalografica(dadosCampo, autoriaDesconhecida));
		
		edicao = recuperaValorDoCampo(Etiqueta.EDICAO.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_B}, dadosCampo);
		
		if(StringUtils.notEmpty(edicao))
			retorno1Temp.append( SEPARADOR_TRACO+edicao.trim()+SEPARADOR_PONTO );
		
		local =  recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		editora = recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_B}, dadosCampo);
		ano = recuperaValorDoCampo(Etiqueta.LOCAL_EDITORA_ANO_PUBLICACAO.getTag(), new char[]{SubCampo.SUB_CAMPO_C}, dadosCampo);
		
		retorno1Temp.append(getPontuacaoLocalEdidoraAnoFicha(local, editora, ano));
		
		retorno1Temp.append(getSeparadorNovaLinha());  /// NOVA LINHA AS DEMAIS INFORMA��ES
		
		
		descricaoFisica = recuperaValorDoCampo(Etiqueta.DESCRICAO_FISICA.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		descricaoFisicaDetalhes = recuperaValorDoCampo(Etiqueta.DESCRICAO_FISICA.getTag(), new char[]{SubCampo.SUB_CAMPO_B}, dadosCampo);
		descricaoFisicaDimensao = recuperaValorDoCampo(Etiqueta.DESCRICAO_FISICA.getTag(), new char[]{SubCampo.SUB_CAMPO_C}, dadosCampo);
		descricaoFisicaMaterialAcompanha = recuperaValorDoCampo(Etiqueta.DESCRICAO_FISICA.getTag(), new char[]{SubCampo.SUB_CAMPO_E}, dadosCampo);
		
		
		String descricao = getPontuacaoDescricaoFisica(descricaoFisica, descricaoFisicaDetalhes, descricaoFisicaDimensao, descricaoFisicaMaterialAcompanha);
		
		// indica se a s�rie vai ser o primeiro valor da linha, para saber como gerar os separadores iniciais
		boolean possuiDescricao = StringUtils.notEmpty(descricao) ? true : false;
		
		retorno1Temp.append(descricao);
		
		
		serieTitle = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		serieISSN = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_X}, dadosCampo);
		serieVolume = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_V}, dadosCampo);
		serieParte = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_P}, dadosCampo);
		serie = recuperaValorDoCampo(Etiqueta.SERIE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		serieSequencial = recuperaValorDoCampo(Etiqueta.SERIE.getTag(), new char[]{SubCampo.SUB_CAMPO_V}, dadosCampo);
		
		String infoSerie = getPontuacaoSerieFicha(serieTitle, serieISSN, serieVolume, serieParte, serie, serieSequencial, possuiDescricao);
		
		// indica se a s�rie vai ser o primeiro valor da linha, para saber como gerar os separadores iniciais
		boolean possuiSerie = StringUtils.notEmpty(infoSerie) ? true : false;
		
		retorno1Temp.append(infoSerie);
		
		if(possuiDescricao || possuiSerie)
			retorno1Temp.append(getSeparadorNovaLinha()); 
		
		
		notasGerais.addAll(  recuperaValorDoCampoSeparado(Etiqueta.NOTA_GERAL.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) );
		
		notasTeses.addAll( recuperaValorDoCampoSeparado(Etiqueta.NOTA_DISSETACAO_TESE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo));
		
		notasBibiliograficas.addAll( recuperaValorDoCampoSeparado(Etiqueta.NOTA_BIBLIOGRAFICA.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) );
		
		notasConteudo.addAll( recuperaValorDoCampoSeparado(Etiqueta.NOTA_DE_CONTEUDO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo));
		
		if(notasGerais.size() > 0 || notasTeses.size() > 0 || notasBibiliograficas.size() > 0 || notasConteudo.size() > 0){
			retorno1Temp.append(getSeparadorNovaLinha());  ////////////// PULA UMA LINHA PARA COME�AR AS NOTAS /////////////////
		}
		
		for (String nota : notasGerais) {
			retorno1Temp.append(getMargem2Ficha(saidaEmHTML)+nota.trim()+SEPARADOR_PONTO+getSeparadorNovaLinha());
		}
		
		for (String nota : notasTeses) {
			retorno1Temp.append(getMargem2Ficha(saidaEmHTML)+nota.trim()+SEPARADOR_PONTO+getSeparadorNovaLinha());
		}
		
		for (String nota : notasBibiliograficas) {
			retorno1Temp.append(getMargem2Ficha(saidaEmHTML)+nota.trim()+SEPARADOR_PONTO+getSeparadorNovaLinha());
		}
		
		for (String nota : notasConteudo) {
			retorno1Temp.append(getMargem2Ficha(saidaEmHTML)+nota.trim()+SEPARADOR_PONTO+getSeparadorNovaLinha());
		}
		
		
		List<String> isbnsTemp = recuperaValorDoCampoSeparado(Etiqueta.ISBN.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		
		
		int contadorIsbns = 0;
		
		for (String isbnTemp : isbnsTemp) {
			if(StringUtils.notEmpty(isbnTemp)){
				isbnTemp = formataISBN(isbnTemp);
				
				if(StringUtils.notEmpty(isbnTemp)){
					
					if(contadorIsbns == 0 ) // primeiro
						retorno1Temp.append(  getMargem2Ficha(saidaEmHTML)+TEXTO_ISBN+isbnTemp.trim());
					else
						retorno1Temp.append(  SEPARADOR_PONTO_ESPACO_TRACO+isbnTemp.trim());
						
					contadorIsbns++;	
				}
			}
		}
		
		retorno1Temp.append( getSeparadorNovaLinha() ); // nova linha do ISBN
		
		issn  = recuperaValorDoCampo(Etiqueta.ISSN.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		
		if(StringUtils.notEmpty(issn))
			retorno1Temp.append(  getMargem2Ficha(saidaEmHTML)+TEXTO_ISSN+issn.trim()+getSeparadorNovaLinha());
		
		
		

		assuntosPessoais = recuperaValorDoCampoSeparado(Etiqueta.ASSUNTO_PESSOAL.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		assuntos = recuperaValorDoCampoSeparadoAssunto(Etiqueta.ASSUNTO.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_V, SubCampo.SUB_CAMPO_X, SubCampo.SUB_CAMPO_Y, SubCampo.SUB_CAMPO_Z}, dadosCampo);
		
		autoresSecundariosPessoa = recuperaValorDoCampoSeparado(Etiqueta.AUTOR_SECUNDARIO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);
		autoresSecundariosCorporacao = recuperaValorDoCampoSeparado(Etiqueta.AUTOR_COOPORATIVO_SECUNDARIO.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_B}, dadosCampo);
		autoresSecundariosEvento = recuperaValorDoCampoSeparado(Etiqueta.AUTOR_EVENTO_SECUNDARIO.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_N, SubCampo.SUB_CAMPO_D, SubCampo.SUB_CAMPO_C}, dadosCampo);
		
		boolean possuiDadosAssuntos = false;
		
		if(assuntosPessoais.size() > 0 || assuntos.size() > 0
				|| autoresSecundariosPessoa.size() > 0 || autoresSecundariosCorporacao.size() > 0 || autoresSecundariosEvento.size() > 0)
			possuiDadosAssuntos = true;
		
		if(possuiDadosAssuntos)
			retorno1Temp.append(getSeparadorNovaLinha());  ///////////// PULA UMA LINHA PARA COME�AR OS ASSUNTOS /////////////////
		
		
		int contadorArabicos = 0;   // Os assuntos devem est� numerados em algarismos ar�bicos
		
		retorno1Temp.append(getMargem2Ficha(saidaEmHTML));
		
		while(contadorArabicos < assuntosPessoais.size() || contadorArabicos < assuntos.size()){
			
			contadorArabicos++;
			
			if(contadorArabicos == 1)
				retorno1Temp.append(contadorArabicos+SEPARADOR_PONTO);
			else
				retorno1Temp.append(SEPARADOR_PONTO+contadorArabicos+SEPARADOR_PONTO);
			
			
			if(contadorArabicos-1 < assuntosPessoais.size())
				retorno1Temp.append( ( contadorArabicos != 0 ? " " : "") +  assuntosPessoais.get(contadorArabicos-1).trim() );
			
			if(contadorArabicos-1 < assuntos.size())
				retorno1Temp.append( ( contadorArabicos != 0 ? " " : "") + assuntos.get(contadorArabicos-1).trim() );
			
		}
		
		if(assuntosPessoais.size() > 0 || assuntos.size() > 0 )
			retorno1Temp.append(SEPARADOR_PONTO);
		
		
		
		try {
		
			int contadorRomanos = 0;  // Os autores secund�rios devem est� numerados em algarismos romanos
			
			while(contadorRomanos < autoresSecundariosPessoa.size() || contadorRomanos < autoresSecundariosCorporacao.size()
					|| contadorRomanos < autoresSecundariosEvento.size() ){
				
				contadorRomanos++;
				
				if(contadorRomanos == 1)
					retorno1Temp.append(Formatador.getInstance().converteParaRomano(contadorRomanos)+SEPARADOR_PONTO);
				else
					retorno1Temp.append(SEPARADOR_PONTO+Formatador.getInstance().converteParaRomano(contadorRomanos)+SEPARADOR_PONTO);
				
				if(contadorRomanos-1 < autoresSecundariosPessoa.size())
					retorno1Temp.append(( contadorRomanos != 0 ? " " : "") + autoresSecundariosPessoa.get(contadorRomanos-1).trim());
				
				if(contadorRomanos-1 < autoresSecundariosCorporacao.size())
					retorno1Temp.append( ( contadorRomanos != 0 ? " " : "") +autoresSecundariosCorporacao.get(contadorRomanos-1).trim());
				
				if(contadorRomanos-1 < autoresSecundariosEvento.size())
					retorno1Temp.append( ( contadorRomanos != 0 ? " " : "") +autoresSecundariosEvento.get(contadorRomanos-1).trim());
				
			}
		
			if(autoresSecundariosPessoa.size() > 0 || autoresSecundariosCorporacao.size() > 0 || autoresSecundariosEvento.size() >0)
				retorno1Temp.append(SEPARADOR_PONTO);	
			
			// Sempre aparece a palavara "T�tulo" nas notas de rodap� da fixa 
			contadorRomanos++;
			retorno1Temp.append(Formatador.getInstance().converteParaRomano(contadorRomanos)+SEPARADOR_PONTO+ tituloRodape );
			
			
			serieRodape  = recuperaValorDoCampo(Etiqueta.SERIE_OBSOLETA.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);	
			serieRodape += recuperaValorDoCampo(Etiqueta.SERIE.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);	
			
			if(StringUtils.notEmpty(serieRodape)){
				contadorRomanos++;
				retorno1Temp.append(SEPARADOR_PONTO+Formatador.getInstance().converteParaRomano(contadorRomanos)+SEPARADOR_PONTO+ serieRodape.trim() );
			}
			
			
			
		} catch (NumberFormatException e) {
			// N�o foi poss�vel gerar a sequ�ncia com os nomes dos autores segund�rios
			// Se isso ocorrer, n�o vai mostr�-los na ficha catalogr�fica
		}		
		
		
		if(autoresSecundariosPessoa.size() > 0 || autoresSecundariosCorporacao.size() > 0 || autoresSecundariosEvento.size() > 0)
			retorno1Temp.append(SEPARADOR_PONTO);
		
		retorno[1] = retorno1Temp.toString();
		
		return retorno;
		
	}


	/**
	 *  <p>Retirar aqui os campos cuja pontua��o em par n�o deve ser retirada. Potua��o em par s�o: () [] {}   </p>
	 *  
	 *  <p> Por padr�o o sistema tira de todos os campo, j� que a pontua��o � gerada novamente.  </p>
	 *
	 * @param objects
	 * @return
	 */
	private boolean campoPermaneceComPontuacaoEmPar(Object[] objects) {
		return ! Etiqueta.NOTA_DISSETACAO_TESE.getTag().equals( objects[0]) 
					&& ! Etiqueta.ASSUNTO.getTag().equals( objects[0])
					&& ! Etiqueta.ASSUNTO_PESSOAL.getTag().equals( objects[0])
					;
	}

	/**
	 * Retorna uma String com espa�os em branco do tamanho da margem tipo 1 no formato HTML ou no formato Java.
	 * 
	 * @param saidaEmHTML Se verdadeiro retorna margem compat�vel com HTML, caso contr�rio compat�vel com Java.
	 * @return
	 */
	private String getMargem1Ficha(boolean saidaEmHTML){
		if(saidaEmHTML) {
			return MARGEM_1_FICHA_EM_HTML;
		} else {
			return MARGEM_1_FICHA_EM_JAVA;
		}
	}

	/**
	 * Retorna uma String com espa�os em branco do tamanho da margem tipo 2 no formato HTML ou no formato Java.
	 * 
	 * @param saidaEmHTML Se verdadeiro retorna margem compat�vel com HTML, caso contr�rio compat�vel com Java.
	 * @return
	 */
	private String getMargem2Ficha(boolean saidaEmHTML){
		if(saidaEmHTML) {
			return MARGEM_2_FICHA_EM_HTML;
		} else {
			return MARGEM_2_FICHA_EM_JAVA;
		}
	}

	/**
	 * Retorna uma String com espa�os em branco do tamanho da margem tipo 3 no formato HTML ou no formato Java.
	 * 
	 * @param saidaEmHTML Se verdadeiro retorna margem compat�vel com HTML, caso contr�rio compat�vel com Java.
	 * @return
	 */
	private String getMargem3Ficha(boolean saidaEmHTML){
		if(saidaEmHTML) {
			return MARGEM_3_FICHA_EM_HTML;
		} else {
			return MARGEM_3_FICHA_EM_JAVA;
		}
	}



	/**
	 * <p>M�todo que forma o nome do autor para a ficha catalogr�fica.</p>
	 * <p><i>Aplicando todas as regras, inclusive a de nome de autores muito grandes, vai quebrar em v�rias 
	 * linhas e utilizar a <strong>3 margem</strong> </i></p>
	 *
	 * @param autor
	 * @return
	 */
	private String obtemAutorFichaCatalografica(String autor){
		
		StringBuilder retorno1Temp = new StringBuilder();
		
		///////////////////////////////////////////
		// O nome do autor cabe em 1 linha
		///////////////////////////////////////////
		if(autor.length()+QTD_CARACTERES_MARGEN_2 <= QTD_MAXIMO_CARACTERES_POR_LINHA){
			retorno1Temp.append(getPontuacaoCampo(autor, '.')+getSeparadorNovaLinha());
		}else{
			
			/////////////////////////////////////////////////////////////////////////////////////
			// Quando o tamanho do autor n�o cabe na primeira linha, quebra-se o nome de autor e 
			//as demais linhas come�ar�o da *** terceira *** margem
			/////////////////////////////////////////////////////////////////////////////////////
			
			retorno1Temp.append( quebraTextoVariasLinhasFicha(autor, getMargem1Ficha(saidaEmHTML), getMargem3Ficha(saidaEmHTML))+SEPARADOR_PONTO+getSeparadorNovaLinha());
			
		}
		
		return retorno1Temp.toString();
	}
	
	
	/**
	 * <p>Quando um texto n�o cabe em uma linha da ficha catalogr�fica, o texto deve ser quebrado obdecendo 
	 * uma regra das margens.  Geralmente o que passa da primeira linha fica em uma margem diferente. Esse m�todo 
	 * implementa essas regras. </p>
	 *
	 * @param texto
	 * @param margemPrimeiraLinha
	 * @param margemDemaisLinhas
	 * @return
	 */
	private String quebraTextoVariasLinhasFicha(String texto, String margemPrimeiraLinha, String margemDemaisLinhas){
		
		StringBuilder textoTemp = new StringBuilder();
		
		int qtdLinhasTexto = texto.length() / QTD_MAXIMO_CARACTERES_POR_LINHA;
		
		int posicaoInicial = 0;
		int posicaoFinal = 0;
		
		for (int qtdLinhas = 0;  qtdLinhas <= qtdLinhasTexto ; qtdLinhas++) {
		
			if(qtdLinhas == 0){
				posicaoInicial = 0;
				posicaoFinal = QTD_MAXIMO_CARACTERES_POR_LINHA-QTD_CARACTERES_MARGEN_2;
				
				if(posicaoFinal > texto.length())
					posicaoFinal =  texto.length();
				
				textoTemp.append( margemPrimeiraLinha+" "+texto.substring(posicaoInicial, posicaoFinal ) +getSeparadorNovaLinha()); // primeira linha do autor
			}else{
				
				// o restantes das linha ficham com a 3� margem
		
				posicaoInicial = posicaoFinal;
				posicaoFinal = posicaoFinal + QTD_MAXIMO_CARACTERES_POR_LINHA-QTD_CARACTERES_MARGEN_3;
				
				if(posicaoFinal > texto.length())
					posicaoFinal =  texto.length();
				
				textoTemp.append(margemDemaisLinhas+" "+texto.substring( posicaoInicial, posicaoFinal) );
				
				if(qtdLinhas != qtdLinhasTexto){ // se n�o for o �ltimo, quebra a pr�xima linha do autor
					textoTemp.append(getSeparadorNovaLinha());
				}
				
			}
		}
		
		return textoTemp.toString();
		
	}
	
	
	
	/**
	 * M�todo que cont�m as regras para formata��o de um t�tulo de uma obra.
	 *
	 * @param dadosCampo
	 * @return
	 */
	private Object obtemTituloFichaCatalografica(List<Object[]> dadosCampo, boolean autoriaDesconhecida) {
		
		StringBuilder retorno1Temp = new StringBuilder();
		
		String titulo = "";      	          // 245a   
		String tituloMedio = "";              // 245h
		String subTitulo = "";                // 245b
		String tituloResponsabilidade = "";   // 245c
		
		titulo = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo);	
		subTitulo = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_B}, dadosCampo);	
		tituloMedio = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_H}, dadosCampo);	
		tituloResponsabilidade = recuperaValorDoCampo(Etiqueta.TITULO.getTag(), new char[]{SubCampo.SUB_CAMPO_C}, dadosCampo);	

		
		if( StringUtils.notEmpty(titulo ) || StringUtils.notEmpty(subTitulo ) || StringUtils.notEmpty(tituloMedio ) || StringUtils.notEmpty( tituloResponsabilidade )){
			
			if(autoriaDesconhecida)
				retorno1Temp.append( quebraTextoVariasLinhasFicha(getPontuacaoTituloFicha(titulo, tituloMedio, subTitulo, tituloResponsabilidade), getMargem1Ficha(saidaEmHTML), getMargem2Ficha(saidaEmHTML)));
			else
				retorno1Temp.append( getMargem2Ficha(saidaEmHTML)+getPontuacaoTituloFicha(titulo, tituloMedio, subTitulo, tituloResponsabilidade) );
				
		}
		
		
		return retorno1Temp.toString();
	}
	
	
	/** 
	 * <p> Formata a potua��o das informa��es do campo 260 para a ficha. </p>
	 * 
	 * @param local
	 * @param editora
	 * @param ano
	 * @return
	 */
	private String getPontuacaoLocalEdidoraAnoFicha(String local, String editora, String ano) {
		
		StringBuilder retorno = new StringBuilder();
		
		if( StringUtils.notEmpty(local ) || StringUtils.notEmpty(editora ) || StringUtils.notEmpty(ano ) ){
			
			retorno.append(SEPARADOR_TRACO);
			
			if(StringUtils.notEmpty(local))
				retorno.append( local.trim());
			
			if(StringUtils.notEmpty(editora)){
				retorno.append(SEPARADOR_DOIS_PONTOS);
				retorno.append( editora.trim());
			}
			
			if(StringUtils.notEmpty(ano)){
				retorno.append(SEPARADOR_VIRGULA);
				retorno.append( ano.trim());
			}
			
			retorno.append(SEPARADOR_PONTO);
		}
		return retorno.toString();
	}
	
	
	/** 
	 * <p> Formata a potua��o das informa��es do campo 260 para a ficha. </p>
	 * 
	 * @param local
	 * @param editora
	 * @param ano
	 * @return
	 */
	private String getPontuacaoDescricaoFisica(String descricaoFisica, String descricaoFisicaDetalhes, String descricaoFisicaDimensao, String descricaoFisicaMaterialAcompanha) {
		
		StringBuilder retorno = new StringBuilder();
		
		if( StringUtils.notEmpty(descricaoFisica ) || StringUtils.notEmpty(descricaoFisicaDetalhes ) || StringUtils.notEmpty(descricaoFisicaDimensao ) || StringUtils.notEmpty(descricaoFisicaMaterialAcompanha)){
			
			retorno.append(getMargem2Ficha(saidaEmHTML));
			
			if(StringUtils.notEmpty(descricaoFisica))
				retorno.append( descricaoFisica.trim());
			
			if(StringUtils.notEmpty(descricaoFisicaDetalhes)){
				retorno.append(SEPARADOR_DOIS_PONTOS);
				retorno.append( descricaoFisicaDetalhes.trim());
			}
			
			if(StringUtils.notEmpty(descricaoFisicaDimensao)){
				retorno.append(SEPARADOR_VIRGULA);
				retorno.append( descricaoFisicaDimensao.trim());
			}
			
			if(StringUtils.notEmpty(descricaoFisicaMaterialAcompanha)){
				retorno.append(SEPARADOR_PONTO_VIRGULA);
				retorno.append( descricaoFisicaMaterialAcompanha.trim());
			}
			
			retorno.append(SEPARADOR_PONTO);
		}
		
		return retorno.toString();
	}
	
	
	/** 
	 * <p> Formata a potua��o das informa��es do campo 260 para a ficha. </p>
	 * 
	 * @param local
	 * @param editora
	 * @param ano
	 * @return
	 */
	private String getPontuacaoSerieFicha(String serieTitle, String serieISSN, String serieVolume, String serieParte, String serie, String serieSequencial, boolean seriePrimeiroCampoLinha) {
		
		StringBuilder retorno = new StringBuilder();
		
		if( StringUtils.notEmpty(serieTitle ) || StringUtils.notEmpty(serieISSN ) || StringUtils.notEmpty(serieVolume )
				|| StringUtils.notEmpty(serieParte ) || StringUtils.notEmpty(serie ) || StringUtils.notEmpty(serieSequencial )){
			
			if(seriePrimeiroCampoLinha){
				retorno.append(SEPARADOR_TRACO);
			}else{
				retorno.append(getMargem2Ficha(saidaEmHTML));
			}
			
			retorno.append(SEPARADOR_ABRE_PARENTESES);
			
			
			// Campo da s�rie que est� em desuso
			if(StringUtils.notEmpty(serieTitle))
				retorno.append( serieTitle.trim());
			
			if(StringUtils.notEmpty(serieISSN)){
				retorno.append(SEPARADOR_VIRGULA);
				retorno.append( serieISSN.trim());
			}
			
			if(StringUtils.notEmpty(serieVolume)){
				retorno.append(SEPARADOR_PONTO_VIRGULA);
				retorno.append( serieVolume.trim());
			}
			
			if(StringUtils.notEmpty(serieParte)){
				retorno.append(SEPARADOR_PONTO);
				retorno.append( serieParte.trim());
			}
			
			// Campo da s�rie que era para ser utilizada
			if(StringUtils.notEmpty(serie)){
				retorno.append( serie.trim());
			}
			
			if(StringUtils.notEmpty(serieSequencial)){
				retorno.append(SEPARADOR_PONTO_VIRGULA);
				retorno.append( serieSequencial.trim());
			}
			
			
			retorno.append(SEPARADOR_FECHA_PARENTESES);
		}
		return retorno.toString();
	}
	
	
	
	/**
	 * <p>M�todo diferente do M�todo <code> recuperaValorDoCampoSeparado() </code>. </p> 
	 * <p> Porque para assuntos o valor � separado por campo e n�o por sub campo. Os seja, todos os sub campos ficam juntos como se fossem 1. </p>
	 * 
	 * <p>
	 * Devem ficar: 1. Sa�de - Administracao e n�o : 1. Sa�de. 2. Administra��o.
	 * </p>
	 *
	 *
	 * @param tag  exemplo: 520
	 * @param codigoSubCampo exemplo: a
	 * @param dadosCampo  os dados de todos os campo do t�tulo
	 * @return  Todos os dados do Titulo para o campo passado
	 */
	private List<String> recuperaValorDoCampoSeparadoAssunto(String tag, char[] codigosSubCampo, List<Object[]> dadosCampo){

		/**
		 * Um mapa contento <<< id campos dados, os dados dos subcampos separados por '-' >>>
		 * 
		 * Ps.: Esse map n�o mant�m a ordem original das possi��es dos campos
		 */
		Map<Integer, String> dadosSeparadosPorCampo = new HashMap<Integer, String>();

		for (Object[] objects : dadosCampo) {
			
			if(  ( (String)objects[0] ).equals(tag)  ) {
				
				for(int indexSubCampo = 0 ;  indexSubCampo < codigosSubCampo.length ;  indexSubCampo++ ){
					if(  ( (Character) objects[1]).equals(codigosSubCampo[indexSubCampo])){
						if(dadosSeparadosPorCampo.containsKey( objects[3] ) ){
							String dadosAntigos = dadosSeparadosPorCampo.get( objects[3]);	
							dadosSeparadosPorCampo.put( (Integer)objects[3], dadosAntigos +" "+SEPARADOR_TRACO+(String) objects[2]);// o que tinha antes + "-"+ os novos dados
						}else{
							dadosSeparadosPorCampo.put( (Integer)objects[3], (String) objects[2]);
						}
					}
				}
			}
		}

		
		List<String> resultado = new ArrayList<String>();
		
		/* Aqui percorre novamente os dados dos campos para adicionar a lista de retorno.
		 * Como os dados dos campos est�o ordenados corretamente pela possi��o do campo de dados e sub campo, deve retornar na possi��o correta
		 * 
		 * objects[3] = id campo de dados
		 */
		
		for (Object[] objects : dadosCampo) {  // percorre os dados que est�o ordenados
			String dadosDoCampoFormatados = dadosSeparadosPorCampo.get( objects[3] ); // pega pelo id do campo
			
			if(StringUtils.notEmpty(dadosDoCampoFormatados)){ // Se o id do campo tem, � proque ele foi um dos campos formatados, ent�o adiciona a lista
				resultado.add(dadosDoCampoFormatados); // cada string cont�m os dados de v�rios sub campo do campo separedos por "-"
			}
		}
		
		return resultado;
	}
	

	/**
	 * M�todo que recupera os dados de todos os campos do t�tlo que possuem o label passado, o label � o campo + $ + codigo do sub campo.
	 *
	 * A diferen�a desse campo � que ele retorna os valores dos campos juntos em uma �nica string mais spera pelo separador de nova linha.
	 *
	 * @param tag  520
	 * @param codigoSubCampo a
	 * @param dadosCampo  os dados de todos os campo do t�tulo
	 * @return  Todos os dados do Titulo para o campo passado
	 */
	private String recuperaValorDoCampoClassificacaoFichaCatalografica(String tag, char[] codigosSubCampo, List<Object[]> dadosCampo){
		
		StringBuilder buffer = new StringBuilder();
		
		for (Object[] objects : dadosCampo) {
			
			if(  ( (String)objects[0] ).equals(tag)  ) {
				for(int indexSubCampo = 0 ;  indexSubCampo < codigosSubCampo.length ;  indexSubCampo++ ){
					if(  ( (Character) objects[1]).equals(codigosSubCampo[indexSubCampo])){
						buffer.append(objects[2]+getSeparadorNovaLinha());
					}
				}
			}	
		}
		
		return buffer.toString();
	}
	
	
	/**
	 * M�todo que recupera os dados de todos os campos passados.  Recupera cada valor de cada campo separado, 
	 * em uma lista, n�o tudo junto em uma string s� como o metodo <code> recuperaValorDoCampo() </code>. 
	 * Porque cada valor deve ser colocado em uma linha diferente ou ordenado de forma diferente.
	 *
	 * @param tag  exemplo: 520
	 * @param codigoSubCampo exemplo: a
	 * @param dadosCampo  os dados de todos os campo do t�tulo
	 * @return  Todos os dados do Titulo para o campo passado
	 */
	private List<String> recuperaValorDoCampoSeparado(String tag, char[] codigosSubCampo, List<Object[]> dadosCampo){
		
		 List<String> buffer = new ArrayList<String>();
		
		for (Object[] objects : dadosCampo) {
			
			if(  ( (String)objects[0] ).equals(tag)  ) {
				for(int indexSubCampo = 0 ;  indexSubCampo < codigosSubCampo.length ;  indexSubCampo++ ){
					if(  ( (Character) objects[1]).equals(codigosSubCampo[indexSubCampo])){
						buffer.add(objects[2]+" ");
					}
				}
			}	
		}
		
		return buffer;
	}
	
	
	/**
	 * M�todo que recupera os dados de todos os campos do t�tlo que possuem o label passado, o label � o campo + $ + codigo do sub campo.
	 *
	 * @param tag  520
	 * @param codigoSubCampo a
	 * @param dadosCampo  os dados de todos os campo do t�tulo
	 * @return  Todos os dados do Titulo para o campo passado
	 */
	private String recuperaValorDoCampo(String tag, char[] codigosSubCampo, List<Object[]> dadosCampo){
		
		StringBuilder buffer = new StringBuilder();
		
		for (Object[] objects : dadosCampo) {
			
			if(  ( (String)objects[0] ).equals(tag)  ) {
				for(int indexSubCampo = 0 ;  indexSubCampo < codigosSubCampo.length ;  indexSubCampo++ ){
					if(  ( (Character) objects[1]).equals(codigosSubCampo[indexSubCampo])){
						buffer.append(objects[2]+" ");
					}
				}
			}	
		}
		
		return buffer.toString();
	}
	
	
	
	
	/**
	 * Recupera o valor do campo 100$a, 110$a, ou 111$a.
	 *
	 * @param dadosCampo
	 * @return
	 * @throws AutoriaDesconhecidaException
	 */
	public String recuperaAutorFichaCatalografica(List<Object[]> dadosCampo) throws AutoriaDesconhecidaException{
		
		String autorTemp = recuperaValorDoCampo(Etiqueta.AUTOR.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
		
		String numeroEvento = "";  // N
		String anoRealizacao = "";   // D
		String localRealizacao =  "";   // C
		
		
		if(StringUtils.isEmpty(autorTemp))
			autorTemp = recuperaValorDoCampo(Etiqueta.AUTOR_COOPORATIVO.getTag(), new char[]{SubCampo.SUB_CAMPO_A, SubCampo.SUB_CAMPO_B}, dadosCampo) ;
		
		if(StringUtils.isEmpty(autorTemp)){
			autorTemp = recuperaValorDoCampo(Etiqueta.AUTOR_EVENTO.getTag(), new char[]{SubCampo.SUB_CAMPO_A}, dadosCampo) ;
			numeroEvento = recuperaValorDoCampo(Etiqueta.AUTOR_EVENTO.getTag(), new char[]{SubCampo.SUB_CAMPO_N}, dadosCampo) ;
			anoRealizacao = recuperaValorDoCampo(Etiqueta.AUTOR_EVENTO.getTag(), new char[]{SubCampo.SUB_CAMPO_D}, dadosCampo) ;
			localRealizacao = recuperaValorDoCampo(Etiqueta.AUTOR_EVENTO.getTag(), new char[]{SubCampo.SUB_CAMPO_C}, dadosCampo) ;
			
			if(StringUtils.notEmpty(numeroEvento) || StringUtils.notEmpty(anoRealizacao) || StringUtils.notEmpty(localRealizacao)){
				autorTemp += SEPARADOR_ABRE_PARENTESES+ numeroEvento;
			
				
				if( StringUtils.notEmpty(anoRealizacao))
					autorTemp += " "+SEPARADOR_DOIS_PONTOS+" "+anoRealizacao;
				
				if( StringUtils.notEmpty(localRealizacao))
					autorTemp += " "+SEPARADOR_DOIS_PONTOS+" "+localRealizacao;
				
				autorTemp = autorTemp.trim()+SEPARADOR_FECHA_PARENTESES;
			}
			
		}
		
		if(StringUtils.notEmpty(autorTemp))
			return autorTemp;
		else
			throw new AutoriaDesconhecidaException();
		
	}

	/**
	 * Pontua os dados do t�tulo da cataloga��o de acordo com as regras a AACR2 para ficha catalogr�fica. 
	 *
	 * @param titulo
	 * @param tituloMedio
	 * @param subTitulo
	 * @param tituloResponsabilidade
	 * @return
	 */
	private  String getPontuacaoTituloFicha(String titulo, String tituloMedio, String subTitulo, String tituloResponsabilidade){
		
		StringBuilder retorno1Temp = new StringBuilder();
		
		if(StringUtils.notEmpty(titulo))   titulo = titulo.trim();
		if(StringUtils.notEmpty(tituloMedio)) tituloMedio = tituloMedio.trim();
		if(StringUtils.notEmpty(subTitulo)) subTitulo = subTitulo.trim();
		if(StringUtils.notEmpty(tituloResponsabilidade)) tituloResponsabilidade = tituloResponsabilidade.trim();
		
		retorno1Temp.append(titulo); // Essa campo � obrigat�rio
		
		if(StringUtils.notEmpty(tituloMedio))
			retorno1Temp.append(SEPARADOR_ABRE_COCHETE+tituloMedio+SEPARADOR_FECHA_COCHETE);
		
		if(StringUtils.notEmpty(subTitulo))
			retorno1Temp.append(SEPARADOR_DOIS_PONTOS+subTitulo);
		
		if(StringUtils.notEmpty(tituloResponsabilidade )){
			retorno1Temp.append(SEPARADOR_BARRA+tituloResponsabilidade );
		}
		
		retorno1Temp.append(SEPARADOR_PONTO);
		
		return retorno1Temp.toString();
		
	}

	////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////////////////////// M�todos padr�es aos dois casos ///////////////////////////////////
	
	
	/**
	 * Retorna uma quebra de linha depedendo se a sa�da � para imprimir na tela do sistema ou em uma arquivo
	 */
	public String getSeparadorNovaLinha(){
		
		if(saidaEmHTML)
			return NOVA_LINHA_HTML;
		else
			return NOVA_LINHA_JAVA;
	}
	
	
	
	/**
	 * Aplica em um campo a pontua��o passada
	 * 
	 * @param autor
	 * @param pontuacao
	 * @return
	 */
	private  String getPontuacaoCampo(String autor, char pontuacao){
		if(StringUtils.notEmpty(autor))
			return autor.trim()+pontuacao+" ";
		else
			return "";
	}
	
	/**
	 * Formata o ISBN para deixar apenas n�meros
	 *
	 * @param isbn
	 * @return
	 */
	private static String formataISBN(String isbn){
		if(isbn != null){
			Pattern padrao = Pattern.compile("\\D");  // retira o que n�o � d�gito do ISBN
			Matcher pesquisa = padrao.matcher(isbn);  
			return pesquisa.replaceAll("");
		}else
			return isbn;
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////
	
	
}
