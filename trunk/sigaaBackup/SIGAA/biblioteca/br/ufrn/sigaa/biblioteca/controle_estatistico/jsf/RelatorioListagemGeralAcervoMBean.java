/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p> MBean responsável por gerar o relatório de contagem dos materiais no acervo para as bibliotecas</p>
 * <p><i> Esse MBean não herda de AbstractRelatorioBibliotecaMbean porque ele deve gerar o resultado em PDF,
 * já que a quantidade de resultados pode ser muito grande. </i></p>
 * 
 * <p> <i> <strong> É importante que o relatório possa ser organizado por CODIGO DE BARRAS, ou TITULO ou LOCALIZAÇÃO. </strong>  </i> </p>
 * 
 * <p>
 * 	Filtro:<br/>
 *      <ul>
 * 		<li>Biblioteca Setorial</li>
 * 		<li>Coleção</li>
 * 		<li>Classificação</li>
 * 		<li>Tipo de Material</li>
 *		</ul>
 * 	Campos da listagem analítica <br/>
 *		<ul>
 * 		<li>Número do Sistema</li>
 * 		<li>Código de Barras</li>
 * 		<li>Número de Patrimônio</li>
 * 		<li>Título</li>
 * 		<li>Autor</li>
 * 		<li>Edição</li>
 * 		<li>Ano</li>
 * 		<li>Chamada (Localização)</li>
 *      </ul>
 * </p>
 * 
 * @author Jadson
 * @version 1.5 alterando o nome do relatório para "Contagem do Acervo".  Já que o Inventário é outra coisa. 
 *        O inventário deve ser um relatório que mostre os materiais faltando no acervo, que pressupõe um registro dos livros encontrados na biblioteca.
 */
@Component("relatorioListagemGeralAcervoMBean")
@Scope("request")
public class RelatorioListagemGeralAcervoMBean extends SigaaAbstractController<Object>{

	/** Página com os filtros do relatório. */
	private static final String PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO =
			"/biblioteca/controle_estatistico/filtrosRelatorioListagemGeralAcervo.jsp";
	
	// Categorias dos materiais a serem visualizados
	
	/** Categoria que engloba exemplares e fascículos. */
	public static final int CTGMAT_AMBOS      = 0;
	/** Categoria para mostrar somente exemplares. */
	public static final int CTGMAT_EXEMPLARES = 1;
	/** Categoria para mostrar somente fascículos. */
	public static final int CTGMAT_FASCICULOS = 2;
	
	/**
	 * Guarda os dados que serão usados para preencher o relatório.
	 */
	private List<DadosRelatorioListagemMaterial> dadosRelatorio;
	
	/** Filtro de bibliotecas. */
	private List<String> bibliotecas;
	
	/** Filtro que permite a escolha de mais de um tipo de material. */
	private List<String> tiposMaterial = new ArrayList<String>();

	/** Filtro que permite a escolha de mais de uma situação de material. */
	protected List<String> situacoesMaterial = new ArrayList<String>();
	
	/** Filtro de coleção */
	private List<String> colecoes = new ArrayList<String>();
	
	/** A categoria de material retornada no relatório: exemplares, fascículos ou ambos. */
	protected int ctgMaterial = CTGMAT_AMBOS;
	
	/** Filtro quando o usuário escolhe a classificação CDU */
	private String numeroChamada = "";
	
	/** Filtro que indica como os registros serão ordenados. */
	private int ordenacao = ORDENAR_POR_CODIGO_BARRAS;
	
	/** Filtro para ordenação dos resultados do relatório por código de barras. */
	private static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordenação dos resultados do relatório por Título */
	private static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordenação dos resultados do relatório por localização */
	private static final int ORDENAR_POR_LOCALIZACAO = 3;
	
	/** Filtro que limita o relatório aos materiais que estão emprestados no momento da geração. */
	private boolean somenteEmprestados = false;
	
	/** Guarda os dados do arquivo pdf, antes dele ser enviado para o usuário */
	private ByteArrayOutputStream outputSteamDadosRelatorio;
	
	
	
	public RelatorioListagemGeralAcervoMBean() {
		bibliotecas = new ArrayList<String>();
	}

	
	
	/**
	 * <p>Inicia o Relatório do inventário do sistema </p>
	 * <p>Indica qual o MBean que deve ser chamado para gerar os resultados e os filtros que
	 * devem ser mostrados para o usuário. </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/menus/controle_estatistico.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public String iniciar(){
		return telaFiltrosListagemGeralAcervo();
	}
	
	
	/**
	 *   <p>Método chamado para consultar os dados no banco e gerar o arquivo .PDF</p>
	 *   <p> <i>Em alguns casos, principalmente para a biblioteca central, a quantidade de resultados é
	 *   muito grande e o sistema demora uns 2 min para criar o arquivo PDF, por isso a geração do PDF é feita
	 *   em dois passos, para possibilitar mostra uma mensagem de "espere" para o usuário.  </i></p>
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
		
		// serve para contar a quantidade de títulos distintos
		Set<Integer> titulosDeExemplares = new HashSet<Integer>();
		Set<Integer> titulosDeFasciculos = new HashSet<Integer>();
		
		///// Faz as consultas a exemplares e a fascículos
		
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
		
		
		/// Se a consulta não retornou resultados
		if ( quantidadeTotalResultados == 0 ) {
			addMensagemWarning("Não foram encontrados materiais com as características escolhidas");
			outputSteamDadosRelatorio = null;
			return telaFiltrosListagemGeralAcervo();
		}
		
		Collections.sort(dadosRelatorio);
		
		
		//////////////////// Configurando os parâmetros do relatório /////////////////////
		
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
			ctg = "Exemplares (Publicações Avulsas)";
		else if ( ctgMaterial == CTGMAT_FASCICULOS )
			ctg = "Fascículos (Periódicos)";
		else
			ctg = "Exemplares (Publicações Avulsas) e Fascículos (Periódicos)";
		props.put("ctgMaterial", ctg);
		
		if (StringUtils.notEmpty(numeroChamada)) {
			props.put("Número de Chamada", numeroChamada);
		}
		
		if ( somenteEmprestados )
			props.put("somenteEmprestados", "Sim");
		else
			props.put("somenteEmprestados", "Não");
		
		if(ordenacao == ORDENAR_POR_CODIGO_BARRAS)
			props.put("descricaoOrdenacao", "Ordenado por Código de Barras");
		else if(ordenacao == ORDENAR_POR_TITULO)
			props.put("descricaoOrdenacao", "Ordenado por Título");
		else if(ordenacao == ORDENAR_POR_LOCALIZACAO)
			props.put("descricaoOrdenacao", "Ordenado por Localização");
		
		// Caso 1 título possua exemplar e fasciculo ao mesmo tempo, o que não era para ocorrer!!!!
		// Deve ser contado 2 vezes para o relatário bater com o relatório de Total de Títulos e Materiais
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
		
		addMensagemInformation("Relatório gerado com sucesso.");
		
		////////////////////////////////////////////////////////////////////////////////////////////
		
		return telaFiltrosListagemGeralAcervo();
	}
	

	
	/**
	 * 
	 * Método que envia o arquivo pdf gerado no passo anterior para a saída do usuário.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
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
				nomeBiblioteca = "Várias_Bibliotecas";
			
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + "RelatórioContagem_" + nomeBiblioteca);
			FacesContext.getCurrentInstance().responseComplete();
			
		}
	}
	
	

	/**
	 * <p>
	 * Método que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * <p>Método que retorna as situações do sistema em forma de SelectItem's.
	 * <p>Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioIventarioAcervo.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesMateriaisCombo () throws DAOException {
		SituacaoMaterialInformacionalDao dao = getDAO(SituacaoMaterialInformacionalDao.class);
		return toSelectItems(dao.findAllSituacoesAtivasNaoBaixa(), "id", "descricao");
	}
	
	
	/**
	 *   <p> Redireciona para a tela de filtros do relatório. </p>
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
