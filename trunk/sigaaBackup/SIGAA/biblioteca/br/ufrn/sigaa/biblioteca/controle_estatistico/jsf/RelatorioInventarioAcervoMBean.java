/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/04/2012
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
import br.ufrn.sigaa.arq.dao.biblioteca.SituacaoMaterialInformacionalDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TipoMaterialDAO;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioInventarioAcervoDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioListagemMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;

/**
 *
 * <p> MBean responsável por gerar o relatório de inventário do acervo das bibliotecas</p>
 * <p><i> Esse MBean não herda de AbstractRelatorioBibliotecaMbean porque ele deve gerar o resultado em PDF,
 * já que a quantidade de resultados pode ser muito grande. </i></p>
 * 
 * <p> <i> <strong> É importante que o relatório possa ser organizado por CODIGO DE BARRAS, ou TITULO ou LOCALIZAÇÃO. </strong>  </i> </p>
 * 
 * <p>
 * 	Filtro:<br/>
 *      <ul>
 * 		<li>Biblioteca Setorial</li>
 * 		<li>Inventário</li>
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
 * @author Felipe
 * 
 */
@Component("relatorioInventarioAcervoMBean")
@Scope("request")
public class RelatorioInventarioAcervoMBean extends SigaaAbstractController<Object> {

	/** Página com os filtros do relatório. */
	private static final String PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO = "/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp";
	
	// Categorias dos materiais a serem visualizados
	

	/** A biblioteca selecionada no cadastro de inventários */
	private Integer idBiblioteca;
	/** Lista dos inventários da biblioteca selecionada */
	private List<InventarioAcervoBiblioteca> inventarios;

	
	
	/** Armazena o inventário selecionado pelo usuário */
	private Integer idInventario;
	/** Guarda a lista de bibliotecas que vão ser exibidas no combobox */
	private Collection<Biblioteca> bibliotecas;
	
	/**
	 * Guarda os dados que serão usados para preencher o relatório.
	 */
	private List<DadosRelatorioListagemMaterial> dadosRelatorio;
	
	/** Filtro que permite a escolha de mais de um tipo de material. */
	private List<String> tiposMaterial = new ArrayList<String>();

	/** Filtro que permite a escolha de mais de uma situação de material. */
	protected List<String> situacoesMaterial = new ArrayList<String>();
	
	/** Filtro de coleção */
	private List<String> colecoes = new ArrayList<String>();
	
	
	/** Filtro que indica como os registros serão ordenados. */
	private int ordenacao = ORDENAR_POR_CODIGO_BARRAS;
	
	/** Filtro para ordenação dos resultados do relatório por código de barras. */
	private static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordenação dos resultados do relatório por Título */
	private static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordenação dos resultados do relatório por localização */
	private static final int ORDENAR_POR_LOCALIZACAO = 3;
	
	/** Guarda os dados do arquivo pdf, antes dele ser enviado para o usuário */
	private ByteArrayOutputStream outputSteamDadosRelatorio;
	
	/** Diz se o inventário do relatório é de uma coleção específica. 
	 *  Nesse caso o usuário não vai poder seleciona nenhuma coleção. tem que ser a coleção do inventário.
	 */
	private boolean inventarioEspecificoColecao = false;
	
	public RelatorioInventarioAcervoMBean() {
		inventarios = new ArrayList<InventarioAcervoBiblioteca>();
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
		
		if ( idBiblioteca == null || idBiblioteca <= 0 ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		}
		
		if ( idInventario == null || idInventario <= 0  ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Inventário");
		}
		
		if(hasErrors())
			return null;
		
		
		RelatorioInventarioAcervoDao dao = null;
		BibliotecaDao bibliotecaDao = null;
		ColecaoDao colecoesDAO = null;
		TipoMaterialDAO tmDAO = null;
		SituacaoMaterialInformacionalDao smiDAO = null;
		
		try {
			dao = getDAO(RelatorioInventarioAcervoDao.class);
			
			dadosRelatorio = new ArrayList<DadosRelatorioListagemMaterial>();
			
			// serve para contar a quantidade de títulos distintos
			Set<Integer> titulosDeMateriais = new HashSet<Integer>();
			
			InventarioAcervoBiblioteca objInventario = dao.findByPrimaryKey(idInventario, InventarioAcervoBiblioteca.class, "id", "descricao", "dataFechamento");
			
			///// Faz as consultas a materiais
			
			List<Object[]> lista = dao.findInventarioMateriais(
					idBiblioteca, objInventario, UFRNUtils.toInteger(colecoes), UFRNUtils.toInteger(tiposMaterial), 
					UFRNUtils.toInteger(situacoesMaterial));
			
			for (Object[] array : lista) {
				dadosRelatorio.add(new DadosRelatorioListagemMaterial(
						(Integer) array[0], (String) array[1], (BigInteger)  array[2], (String)  array[3],
						(String)  array[4], (String)  array[5], (String)  array[6], (String)  array[7],
						ordenacao == ORDENAR_POR_CODIGO_BARRAS,
						ordenacao == ORDENAR_POR_TITULO,
						ordenacao == ORDENAR_POR_LOCALIZACAO ));
				
				titulosDeMateriais.add( ((Number) array[8]).intValue() );
			}
			
			int quantidadeTotalResultados = dadosRelatorio.size();
			
			/// Se a consulta não retornou resultados
			if ( quantidadeTotalResultados == 0 ) {
				addMensagemWarning("Não foram encontrados materiais não registrados para este inventário a partir do filtros selecionados.");
				outputSteamDadosRelatorio = null;
				return telaFiltrosListagemGeralAcervo();
			}
			
			Collections.sort(dadosRelatorio);
	
			//////////////////// Configurando os parâmetros do relatório /////////////////////
			
			Map<String, String> props = new HashMap<String, String>();
			
			props.put("nomeRelatorio", "Inventário do Acervo (materiais não registrados)");
			
			bibliotecaDao = getDAO( BibliotecaDao.class );
			
			props.put("descricaoBiblioteca", bibliotecaDao.findDescricaoBibliotecaInternaAtiva(idBiblioteca));
			props.put("descricaoInventario", objInventario.getDescricao());
	
			if (colecoes == null || colecoes.size() == 0)
				props.put("descricaoColecoes", "Todas" );
			else {
				colecoesDAO = getDAO(ColecaoDao.class);
				List<String> descColecoes = colecoesDAO.findDescricaoColecoesAtivas(UFRNUtils.toInteger(colecoes));
				
				props.put("descricaoColecoes", StringUtils.join(descColecoes, ", "));
			}
			
			if (tiposMaterial == null || tiposMaterial.size() == 0)
				props.put("descricaoTiposMaterial", "Todos" );
			else {
				tmDAO = getDAO(TipoMaterialDAO.class);
				List<String> descTipos = tmDAO.findDescricaoTiposAtivos(UFRNUtils.toInteger(tiposMaterial));
				
				props.put("descricaoTiposMaterial", StringUtils.join(descTipos, ", "));
			}
			
			if (situacoesMaterial == null || situacoesMaterial.size() == 0)
				props.put("descricaoSituacoesMaterial", "Todas" );
			else {
				smiDAO = getDAO(SituacaoMaterialInformacionalDao.class);
				List<String> descSituacoes = smiDAO.findDescricaoSituacoesAtivas(UFRNUtils.toInteger(situacoesMaterial));
				
				props.put("descricaoSituacoesMaterial", StringUtils.join(descSituacoes, ", "));
			}
			
			if(ordenacao == ORDENAR_POR_CODIGO_BARRAS)
				props.put("descricaoOrdenacao", "Ordenado por Código de Barras");
			else if(ordenacao == ORDENAR_POR_TITULO)
				props.put("descricaoOrdenacao", "Ordenado por Título");
			else if(ordenacao == ORDENAR_POR_LOCALIZACAO)
				props.put("descricaoOrdenacao", "Ordenado por Localização");
			
			// Caso 1 título possua exemplar e fasciculo ao mesmo tempo, o que não era para ocorrer!!!!
			// Deve ser contado 2 vezes para o relatário bater com o relatório de Total de Títulos e Materiais
			props.put("quantidadeTitulos", String.valueOf(titulosDeMateriais.size())); 
			props.put("quantidadeMateriais", String.valueOf(quantidadeTotalResultados));
			
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
			JasperPrint report = JasperFillManager.fillReport(getReportSIGAA("RelatorioInventarioAcervoBiblioteca.jasper"), props, ds);
			JasperExportManager.exportReportToPdfStream(report, outputSteamDadosRelatorio);
			
			addMensagemInformation("Relatório gerado com sucesso.");
			
			////////////////////////////////////////////////////////////////////////////////////////////
			
			return telaFiltrosListagemGeralAcervo();
		} finally {
			if (dao != null) dao.close();
			if (bibliotecaDao != null) bibliotecaDao.close();
			if (colecoesDAO != null) colecoesDAO.close();
			if (tmDAO != null) tmDAO.close();
			if (smiDAO != null) smiDAO.close();
		}
	}
	

	
	/**
	 * 
	 * Método que envia o arquivo pdf gerado no passo anterior para a saída do usuário.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
	 */
	public void visualizarRelatorio(ActionEvent evt)throws DAOException, IOException {
		
		if(outputSteamDadosRelatorio != null){
		
			DataOutputStream dos  = new DataOutputStream(getCurrentResponse().getOutputStream());
			dos.write(outputSteamDadosRelatorio.toByteArray());
			
			String nomeBiblioteca = getGenericDAO().findByPrimaryKey(idBiblioteca, Biblioteca.class, "identificador").getIdentificador();
			
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + "RelatórioInventárioAcervo_" + nomeBiblioteca);
			FacesContext.getCurrentInstance().responseComplete();
			
		}
	}
	
	/**
	 * Método chamado quando o usuário seleciona uma biblioteca, então o sistema deve buscar os inventários abertos.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 * @String
	 */
	public String buscarInventarios() throws DAOException {
		outputSteamDadosRelatorio = null;
		
		inventarioEspecificoColecao = false;

		inventarios.clear();
		
		if (idBiblioteca == -1) {
			return null;
		}
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class);
			
			inventarios = dao.findAllFechadoByBiblioteca(idBiblioteca);
			
			if (inventarios == null || inventarios.size() == 0) {
				addMensagemErroAjax("Esta biblioteca não possui inventários concluídos (fechados).");
			}
			
			return null;
		} finally {
			if(dao != null) dao.close();
		}
	}
	
	/**
	 * Método chamado quando o usuário seleciona um inventário, então o sistema deve verificar se é um inventário de coleção especícica ou não.
	 *
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 * @String
	 */
	public void selecionouInventario(ActionEvent env) throws DAOException {
		outputSteamDadosRelatorio = null;
		
		if (idInventario == -1) {
			inventarioEspecificoColecao = false;
			return;
		}
		
		InventarioAcervoBiblioteca inventarioSelecionado = new InventarioAcervoBiblioteca();
		
		for (InventarioAcervoBiblioteca inventario : inventarios) {
			if(inventario.getId() == idInventario)
				inventarioSelecionado = inventario;
		}
		
		
		// O inventário é de uma coleção específica //
		if( inventarioSelecionado.getColecao() != null ){
			colecoes = new ArrayList<String>();
			colecoes.add(""+inventarioSelecionado.getColecao().getId());
			inventarioEspecificoColecao = true;
		}else{
			inventarioEspecificoColecao = false;
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
		
		if (bibliotecas == null){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bibliotecas = dao.findAllBibliotecasInternasAtivas();
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		
		return toSelectItems(bibliotecas, "id", "descricaoCompleta");
	}

	/**
	 *  Retorna todos os inventário fechados (concluídos) da biblioteca selecionada.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioInventarioAcervo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection <SelectItem> getInventariosCombo() throws DAOException{
		return toSelectItems(inventarios, "id", "descricaoCompleta");
	}
	
	/**
	 * <p>Método que retorna as situações do sistema em forma de SelectItem's.
	 * <p>Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioIventarioAcervo.jsp</li>
	 *   </ul>
	 */
	public Collection<SelectItem> getSituacoesMateriaisCombo () throws DAOException {
		SituacaoMaterialInformacionalDao dao = null;
		
		try {
			dao = getDAO(SituacaoMaterialInformacionalDao.class);
			return toSelectItems(dao.findAllSituacoesAtivasNaoBaixa(), "id", "descricao");
		} finally {
			if (dao != null) dao.close();
		}
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

	public ByteArrayOutputStream getOutputSteamDadosRelatorio() {
		return outputSteamDadosRelatorio;
	}

	public void setOutputSteamDadosRelatorio(
			ByteArrayOutputStream outputSteamDadosRelatorio) {
		this.outputSteamDadosRelatorio = outputSteamDadosRelatorio;
	}
	
	public int getOrdenarPorCodigoBarras() {
		return ORDENAR_POR_CODIGO_BARRAS;
	}
	
	public int getOrdenarPorTitulo() {
		return ORDENAR_POR_TITULO;
	}
	
	public int getOrdenarPorLocalizacao() {
		return ORDENAR_POR_LOCALIZACAO;
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

	public Integer getIdBiblioteca() {
		return idBiblioteca;
	}

	public void setIdBiblioteca(Integer idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}

	public List<InventarioAcervoBiblioteca> getInventarios() {
		return inventarios;
	}

	public void setInventarios(List<InventarioAcervoBiblioteca> inventarios) {
		this.inventarios = inventarios;
	}

	public Integer getIdInventario() {
		return idInventario;
	}

	public void setIdInventario(Integer idInventario) {
		this.idInventario = idInventario;
	}

	public Collection<Biblioteca> getBibliotecas() {
		return bibliotecas;
	}

	public void setBibliotecas(Collection<Biblioteca> bibliotecas) {
		this.bibliotecas = bibliotecas;
	}

	public boolean isInventarioEspecificoColecao() {
		return inventarioEspecificoColecao;
	}

	public void setInventarioEspecificoColecao(boolean inventarioEspecificoColecao) {
		this.inventarioEspecificoColecao = inventarioEspecificoColecao;
	}

}
