/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 22/04/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.ColecaoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoMaterialDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioListagemMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;

/**
 *
 * <p> MBean respons�vel por gerar o relat�rio de contagem dos materiais no acervo para as bibliotecas</p>
 * <p><i> Esse MBean n�o herda de AbstractRelatorioBibliotecaMbean porque ele deve gerar o resultado em PDF,
 * j� que a quantidade de resultados pode ser muito grande. </i></p>
 * 
 * <p> <i> <strong> � importante que o relat�rio possa ser organizado por CODIGO DE BARRAS, ou TITULO ou LOCALIZA��O. </strong>  </i> </p>
 * 
 * <p>
 * 	Filtro:<br/>
 *      <ul>
 * 		<li>Biblioteca Setorial</li>
 * 		<li>Cole��o</li>
 * 		<li>Classifica��o</li>
 * 		<li>Tipo de Material</li>
 *		</ul>
 * 	Campos da listagem anal�tica <br/>
 *		<ul>
 * 		<li>N�mero do Sistema</li>
 * 		<li>C�digo de Barras</li>
 * 		<li>N�mero de Patrim�nio</li>
 * 		<li>T�tulo</li>
 * 		<li>Autor</li>
 * 		<li>Edi��o</li>
 * 		<li>Ano</li>
 * 		<li>Chamada (Localiza��o)</li>
 *      </ul>
 * </p>
 * 
 * @author Jadson
 * @version 1.5 alterando o nome do relat�rio para "Contagem do Acervo".  J� que o Invent�rio � outra coisa. 
 *        O invent�rio deve ser um relat�rio que mostre os materiais faltando no acervo, que pressup�e um registro dos livros encontrados na biblioteca.
 */
@Component("relatorioListagemGeralAcervoMBean")
@Scope("request")
public class RelatorioListagemGeralAcervoMBean extends SigaaAbstractController<Object>{

	/** P�gina com os filtros do relat�rio. */
	private static final String PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO =
			"/biblioteca/controle_estatistico/filtrosRelatorioListagemGeralAcervo.jsp";
	
	// Categorias dos materiais a serem visualizados
	
	/** Categoria que engloba exemplares e fasc�culos. */
	public static final int CTGMAT_AMBOS      = 0;
	/** Categoria para mostrar somente exemplares. */
	public static final int CTGMAT_EXEMPLARES = 1;
	/** Categoria para mostrar somente fasc�culos. */
	public static final int CTGMAT_FASCICULOS = 2;
	
	/**
	 * Guarda os dados que ser�o usados para preencher o relat�rio.
	 */
	private List<DadosRelatorioListagemMaterial> dadosRelatorio;
	
	/** Filtro de bibliotecas. */
	private List<String> bibliotecas;
	
	/** Filtro que permite a escolha de mais de um tipo de material. */
	private List<String> tiposMaterial = new ArrayList<String>();

	/** Filtro que permite a escolha de mais de uma situa��o de material. */
	protected List<String> situacoesMaterial = new ArrayList<String>();
	
	/** Filtro de cole��o */
	private List<String> colecoes = new ArrayList<String>();
	
	/** A categoria de material retornada no relat�rio: exemplares, fasc�culos ou ambos. */
	protected int ctgMaterial = CTGMAT_AMBOS;
	
	/** Filtro quando o usu�rio escolhe a classifica��o CDU */
	private String numeroChamada = "";
	
	/** Filtro que indica como os registros ser�o ordenados. */
	private int ordenacao = ORDENAR_POR_CODIGO_BARRAS;
	
	/** Filtro para ordena��o dos resultados do relat�rio por c�digo de barras. */
	private static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordena��o dos resultados do relat�rio por T�tulo */
	private static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordena��o dos resultados do relat�rio por localiza��o */
	private static final int ORDENAR_POR_LOCALIZACAO = 3;
	
	/** Filtro que limita o relat�rio aos materiais que est�o emprestados no momento da gera��o. */
	private boolean somenteEmprestados = false;
	
	/** Guarda os dados do arquivo pdf, antes dele ser enviado para o usu�rio */
	private ByteArrayOutputStream outputSteamDadosRelatorio;
	
	
	
	public RelatorioListagemGeralAcervoMBean() {
		bibliotecas = new ArrayList<String>();
	}

	
	
	/**
	 * <p>Inicia o Relat�rio do invent�rio do sistema </p>
	 * <p>Indica qual o MBean que deve ser chamado para gerar os resultados e os filtros que
	 * devem ser mostrados para o usu�rio. </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public String iniciar(){
		return telaFiltrosListagemGeralAcervo();
	}
	
	
	/**
	 *   <p>M�todo chamado para consultar os dados no banco e gerar o arquivo .PDF</p>
	 *   <p> <i>Em alguns casos, principalmente para a biblioteca central, a quantidade de resultados �
	 *   muito grande e o sistema demora uns 2 min para criar o arquivo PDF, por isso a gera��o do PDF � feita
	 *   em dois passos, para possibilitar mostra uma mensagem de "espere" para o usu�rio.  </i></p>
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *   </ul>
	 */
	public String gerarRelatorio() throws DAOException, JRException {
		
		if ( bibliotecas.isEmpty() ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Bibliotecas");
			return null;
		}
		
		dadosRelatorio = new ArrayList<DadosRelatorioListagemMaterial>();
		
		// serve para contar a quantidade de t�tulos distintos
		Set<Integer> titulosDeExemplares = new HashSet<Integer>();
		Set<Integer> titulosDeFasciculos = new HashSet<Integer>();
		
		///// Faz as consultas a exemplares e a fasc�culos
		
		if ( ctgMaterial == CTGMAT_AMBOS || ctgMaterial == CTGMAT_EXEMPLARES ) {
			List<Object[]> lista1 =  getDAO(RelatoriosBibliotecaDao.class).findInventarioExemplares(
					UFRNUtils.toInteger(bibliotecas), UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), numeroChamada, somenteEmprestados);
			
			for (Object[] array1 : lista1) {
				dadosRelatorio.add(new DadosRelatorioListagemMaterial(
						(Integer) array1[0], (String) array1[1], (BigInteger)  array1[2], (String)  array1[3],
						(String)  array1[4], (String)  array1[5], (String)  array1[6], (String)  array1[7],
						ordenacao == ORDENAR_POR_CODIGO_BARRAS,
						ordenacao == ORDENAR_POR_TITULO,
						ordenacao == ORDENAR_POR_LOCALIZACAO ));
				
				titulosDeExemplares.add( ((Number) array1[8]).intValue() );
			}
		}
		
		if ( ctgMaterial == CTGMAT_AMBOS || ctgMaterial == CTGMAT_FASCICULOS ) {
			List<Object[]> lista2 =  getDAO(RelatoriosBibliotecaDao.class).findInventarioFasiculos(
					UFRNUtils.toInteger(bibliotecas), UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposMaterial), 
					UFRNUtils.toInteger(situacoesMaterial), numeroChamada, somenteEmprestados);
			
			for (Object[] array2 : lista2) {
				dadosRelatorio.add(new DadosRelatorioListagemMaterial(
						(Integer) array2[0], (String) array2[1], (String)  array2[2], (String)  array2[3],
						(String)  array2[4], (String)  array2[5], (String)  array2[6],
						ordenacao == ORDENAR_POR_CODIGO_BARRAS,
						ordenacao == ORDENAR_POR_TITULO,
						ordenacao == ORDENAR_POR_LOCALIZACAO ));
				
				titulosDeFasciculos.add( ((Number) array2[7]).intValue() );
			}
		}
		
		int quantidadeTotalResultados = dadosRelatorio.size();
		
		
		/// Se a consulta n�o retornou resultados
		if ( quantidadeTotalResultados == 0 ) {
			addMensagemWarning("N�o foram encontrados materiais com as caracter�sticas escolhidas");
			outputSteamDadosRelatorio = null;
			return telaFiltrosListagemGeralAcervo();
		}
		
		Collections.sort(dadosRelatorio);
		
		
		//////////////////// Configurando os par�metros do relat�rio /////////////////////
		
		Map<String, String> props = new HashMap<String, String>();
		
		props.put("nomeRelatorio", "Listagem Geral do Acervo");
		
		List<String> descBibs = new ArrayList<String>();
		BibliotecaDao dao = getDAO( BibliotecaDao.class );
		for ( Integer idBib : UFRNUtils.toInteger(bibliotecas) ) {
			descBibs.add( dao.findDescricaoBibliotecaInternaAtiva(idBib) );
		}
		props.put("descricaoBiblioteca", StringUtils.join(descBibs, ", "));

		if (colecoes == null || colecoes.size() == 0)
			props.put("descricaoColecoes", "Todas" );
		else {
			ColecaoDao colecoesDAO = getDAO(ColecaoDao.class);
			List<String> descColecoes = colecoesDAO.findDescricaoColecoesAtivas(UFRNUtils.toInteger(colecoes));
			
			props.put("descricaoColecoes", StringUtils.join(descColecoes, ", "));
		}

		if (tiposMaterial == null || tiposMaterial.size() == 0)
			props.put("descricaoTiposMaterial", "Todos" );
		else {
			TipoMaterialDAO tmDAO = getDAO(TipoMaterialDAO.class);
			List<String> descTipos = tmDAO.findDescricaoTiposAtivos(UFRNUtils.toInteger(tiposMaterial));
			
			props.put("descricaoTiposMaterial", StringUtils.join(descTipos, ", "));
		}
		
		if (situacoesMaterial == null || situacoesMaterial.size() == 0)
			props.put("descricaoSituacoesMaterial", "Todas" );
		else {
			SituacaoMaterialInformacionalDao smiDAO = getDAO(SituacaoMaterialInformacionalDao.class);
			List<String> descSituacoes = smiDAO.findDescricaoSituacoesAtivas(UFRNUtils.toInteger(situacoesMaterial));
			
			props.put("descricaoSituacoesMaterial", StringUtils.join(descSituacoes, ", "));
		}
		
		String ctg = null;
		if ( ctgMaterial == CTGMAT_EXEMPLARES )
			ctg = "Exemplares (Publica��es Avulsas)";
		else if ( ctgMaterial == CTGMAT_FASCICULOS )
			ctg = "Fasc�culos (Peri�dicos)";
		else
			ctg = "Exemplares (Publica��es Avulsas) e Fasc�culos (Peri�dicos)";
		props.put("ctgMaterial", ctg);
		
		if (StringUtils.notEmpty(numeroChamada)) {
			props.put("N�mero de Chamada", numeroChamada);
		}
		
		if ( somenteEmprestados )
			props.put("somenteEmprestados", "Sim");
		else
			props.put("somenteEmprestados", "N�o");
		
		if(ordenacao == ORDENAR_POR_CODIGO_BARRAS)
			props.put("descricaoOrdenacao", "Ordenado por C�digo de Barras");
		else if(ordenacao == ORDENAR_POR_TITULO)
			props.put("descricaoOrdenacao", "Ordenado por T�tulo");
		else if(ordenacao == ORDENAR_POR_LOCALIZACAO)
			props.put("descricaoOrdenacao", "Ordenado por Localiza��o");
		
		// Caso 1 t�tulo possua exemplar e fasciculo ao mesmo tempo, o que n�o era para ocorrer!!!!
		// Deve ser contado 2 vezes para o relat�rio bater com o relat�rio de Total de T�tulos e Materiais
		props.put("quantidadeTitulos", String.valueOf(titulosDeExemplares.size()+titulosDeFasciculos.size()) ); 
		props.put("quantidadeMateriais", String.valueOf(quantidadeTotalResultados) );
		
		props.put("nomeInstituicao", RepositorioDadosInstitucionais.get("nomeInstituicao"));
		props.put("nomeSistema",  RepositorioDadosInstitucionais.get("nomeSigaa"));
		props.put("textoRodape", RepositorioDadosInstitucionais.get("siglaSigaa") +" | "+ RepositorioDadosInstitucionais.get("rodapeSistema"));
		    
		props.put("dataEmissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		
		props.put("caminhoLogoTipoInstituicao",   RepositorioDadosInstitucionais.get("logoInstituicao"));
		props.put("caminhoLogoTipoInformatica",  RepositorioDadosInstitucionais.get("logoInformatica"));
		
		////////////////////////////////////////////////////////////////////////////////////////
		
		
		////////////////////  Monta o arquivo PDF utilizando o JasperReport ////////////////////
		
		outputSteamDadosRelatorio = new ByteArrayOutputStream();
		
		JRDataSource ds = new JRBeanCollectionDataSource(dadosRelatorio);
		JasperPrint report = JasperFillManager.fillReport(getReportSIGAA("RelatorioListagemGeralAcervoBiblioteca.jasper"), props, ds);
		JasperExportManager.exportReportToPdfStream(report, outputSteamDadosRelatorio);
		
		addMensagemInformation("Relat�rio gerado com sucesso.");
		
		////////////////////////////////////////////////////////////////////////////////////////////
		
		return telaFiltrosListagemGeralAcervo();
	}
	

	
	/**
	 * 
	 * M�todo que envia o arquivo pdf gerado no passo anterior para a sa�da do usu�rio.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public void visualizarRelatorio(ActionEvent evt)throws DAOException, IOException {
		
		if(outputSteamDadosRelatorio != null){
		
			DataOutputStream dos  = new DataOutputStream(getCurrentResponse().getOutputStream());
			dos.write(outputSteamDadosRelatorio.toByteArray());
			
			String nomeBiblioteca;
			if ( bibliotecas.size() == 1 )
				nomeBiblioteca = getGenericDAO().findByPrimaryKey(
						Integer.parseInt(bibliotecas.get(0)), Biblioteca.class, "identificador").
						getIdentificador();
			else
				nomeBiblioteca = "V�rias_Bibliotecas";
			
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + "Relat�rioContagem_" + nomeBiblioteca);
			FacesContext.getCurrentInstance().responseComplete();
			
		}
	}
	
	

	/**
	 * <p>
	 * M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioIventarioAcervo.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public Collection<SelectItem> getBibliotecasCombo () throws DAOException {
		BibliotecaDao dao = getDAO(BibliotecaDao.class);
		return toSelectItems(dao.findAllBibliotecasInternasAtivas(), "id", "descricao");
	}
	
	/**
	 * <p>M�todo que retorna as situa��es do sistema em forma de SelectItem's.
	 * <p>M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioIventarioAcervo.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesMateriaisCombo () throws DAOException {
		SituacaoMaterialInformacionalDao dao = getDAO(SituacaoMaterialInformacionalDao.class);
		return toSelectItems(dao.findAllSituacoesAtivasNaoBaixa(), "id", "descricao");
	}
	
	
	/**
	 *   <p> Redireciona para a tela de filtros do relat�rio. </p>
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	public String telaFiltrosListagemGeralAcervo(){
		return forward( PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
	}
	
	
	
	// sets e gets //
	
	
	public List<DadosRelatorioListagemMaterial> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(	List<DadosRelatorioListagemMaterial> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public List<String> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(List<String>bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public List<String> getTiposMaterial() {
		return tiposMaterial;
	}

	public void setTiposMaterial(List<String> tiposMaterial) {
		this.tiposMaterial = tiposMaterial;
	}

	public List<String> getColecoes() {
		return colecoes;
	}

	public void setColecoes(List<String> colecoes) {
		this.colecoes = colecoes;
	}

	public String getNumeroChamada() {
		return numeroChamada;
	}

	public void setNumeroChamada(String numeroChamada) {
		this.numeroChamada = numeroChamada;
	}

	public ByteArrayOutputStream getOutputSteamDadosRelatorio() {
		return outputSteamDadosRelatorio;
	}

	public void setOutputSteamDadosRelatorio(
			ByteArrayOutputStream outputSteamDadosRelatorio) {
		this.outputSteamDadosRelatorio = outputSteamDadosRelatorio;
	}
	
	public int getCtgMaterial() { return ctgMaterial; }
	public void setCtgMaterial(int ctgMaterial) { this.ctgMaterial = ctgMaterial; }

	public int getCtgMatAmbos() { return CTGMAT_AMBOS; }
	public int getCtgMatExemplares() { return CTGMAT_EXEMPLARES; }
	public int getCtgMatFasciculos() { return CTGMAT_FASCICULOS; }
	
	public int getOrdenarPorCodigoBarras() {
		return ORDENAR_POR_CODIGO_BARRAS;
	}
	
	public int getOrdenarPorTitulo() {
		return ORDENAR_POR_TITULO;
	}
	
	public int getOrdenarPorLocalizacao() {
		return ORDENAR_POR_LOCALIZACAO;
	}

	public boolean isSomenteEmprestados() {
		return somenteEmprestados;
	}

	public void setSomenteEmprestados(boolean somenteEmprestados) {
		this.somenteEmprestados = somenteEmprestados;
	}

	public int getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(int ordenacao) {
		this.ordenacao = ordenacao;
	}
	
	public List<String> getSituacoesMaterial() {
		return situacoesMaterial;
	}

	public void setSituacoesMaterial(List<String> situacoesMaterial) {
		this.situacoesMaterial = situacoesMaterial;
	}

}
