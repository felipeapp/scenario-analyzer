/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 24/05/2012
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

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dao.RelatorioInventarioAcervoDao;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosRelatorioListagemMaterial;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.InventarioAcervoBibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * <p> Relatório pelo qual o usuário pode consultas os registros realizados por ele para um determinado inventário do acervo.
 * mesmo esse inventário ainda estando aberto.
 *  </p>
 *
 * @author jadson
 *
 */
@Component("relatorioRegistrosInventarioAcervoUsuarioMBean")
@Scope("request")
public class RelatorioRegistrosInventarioAcervoUsuarioMBean extends SigaaAbstractController<Object>{

	/** Página com os filtros do relatório. */
	private static final String PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO = "/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp";
	
	/** Filtro para ordenação dos resultados do relatório por código de barras. */
	public static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordenação dos resultados do relatório por Título */
	public static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordenação dos resultados do relatório por localização */
	public static final int ORDENAR_POR_LOCALIZACAO = 3;
	
	/** Filtro de pessoa utilizando no rich:suggestionbox. */
	protected int idPessoa;
	
	/** Nome da pessoa utilizada no filtro de pessoa no rich:suggestionbox. */
	protected String nomePessoa;
	
	
	/** A biblioteca selecionada no cadastro de inventários */
	private Integer idBiblioteca;
	
	/** O inventário aberto da biblioteca selecionada */
	private InventarioAcervoBiblioteca inventariosAberto;
	
	/** Armazena o inventário selecionado pelo usuário */
	private Integer idInventario;
	
	/** Guarda a lista de bibliotecas que vão ser exibidas no combobox */
	private Collection<Biblioteca> bibliotecasCombo;
	
	/** Guarda os dados do arquivo pdf, antes dele ser enviado para o usuário */
	private ByteArrayOutputStream outputSteamDadosRelatorio;
	
	/** Filtro que indica como os registros serão ordenados. */
	private int ordenacao = ORDENAR_POR_CODIGO_BARRAS;
	
	
	/**
	 * Guarda os dados que serão usados para preencher o relatório. Reaproveita do relatóriode listagem de materiais porque a estrutura é a mesma.
	 * 
	 * IMPORTANTE: Também porque esse objeot contém a lógica correta de ordenação das localizações dos materiais.
	 * 
	 */
	private List<DadosRelatorioListagemMaterial> dadosRelatorio;
	
	/**
	 * 
	 * Ver comentários da classe pai.<br/>
	 *
	 * <p>Método não chamado por JSP.</>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	public String iniciar() {
		
		return forward(PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
		
	}

	
	
	/**
	 * <p>
	 * Gera o relatório o relatório com os registros realizados pelo usuário para o inventário aberto selecionado.
	 * </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 * </p>
	 * @throws JRException 
	 */
	public String gerarRelatorio() throws DAOException, SegurancaException, JRException {
		
		if ( idBiblioteca == null || idBiblioteca <= 0 ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Biblioteca");
		}
		
		if ( idInventario == null || idInventario <= 0  ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Inventário");
		}
		
		if ( idPessoa <= 0 ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Usuário");
			
		}
		
		if(hasErrors())
			return null;
		
		RelatorioInventarioAcervoDao dao = null;
		
		dadosRelatorio = new ArrayList<DadosRelatorioListagemMaterial>();
		
		// serve para contar a quantidade de títulos distintos
		Set<Integer> titulosDistintos = new HashSet<Integer>();
		
		try{
			dao = getDAO(RelatorioInventarioAcervoDao.class);
			
			List<Object[]> resultado = dao.findRegistrosRealizadosPeloUsuario(idInventario, idPessoa, ordenacao);
			
			for (Object[] array : resultado) {
				dadosRelatorio.add(new DadosRelatorioListagemMaterial(
						(Integer) array[0], (String) array[1], (BigInteger) array[2], (String)  array[3], (String)  array[4],
						(String)  array[5], (String)  array[6], (String)  array[7],
						ordenacao == ORDENAR_POR_CODIGO_BARRAS,
						ordenacao == ORDENAR_POR_TITULO,
						ordenacao == ORDENAR_POR_LOCALIZACAO ));
				
				titulosDistintos.add( ((Number) array[8]).intValue() );
			}
		
			int quantidadeTotalResultados = dadosRelatorio.size();
			
			/// Se a consulta não retornou resultados
			if ( quantidadeTotalResultados == 0 ) {
				addMensagemWarning("Não foram encontrados materiais registrados para este inventário a partir do filtros selecionados.");
				outputSteamDadosRelatorio = null;
				return  forward(PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
			}
			
			Collections.sort(dadosRelatorio); // Ordena corretamente.
			
			
			Map<String, String> props = configuraInformacoesArquivoGerado(titulosDistintos.size(), quantidadeTotalResultados);
			
			////////////////////Monta o arquivo PDF utilizando o JasperReport ////////////////////
			
			outputSteamDadosRelatorio = new ByteArrayOutputStream();
			
			JRDataSource ds = new JRBeanCollectionDataSource(dadosRelatorio);
			JasperPrint report = JasperFillManager.fillReport(getReportSIGAA("RelatorioInventarioAcervoBiblioteca.jasper"), props, ds);
			JasperExportManager.exportReportToPdfStream(report, outputSteamDadosRelatorio);
			
			addMensagemInformation("Relatório gerado com sucesso.");
			
			////////////////////////////////////////////////////////////////////////////////////////////
			
			return forward(PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * 
	 * Método que envia o arquivo pdf gerado no passo anterior para a saída do usuário.
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 *
	 *   Método não chamado por nenhuma página jsp.
	 */
	public void visualizarRelatorio(ActionEvent evt)throws DAOException, IOException {
		
		if(outputSteamDadosRelatorio != null){
		
			DataOutputStream dos  = new DataOutputStream(getCurrentResponse().getOutputStream());
			dos.write(outputSteamDadosRelatorio.toByteArray());
			
			getCurrentResponse().setContentType("application/pdf");
			getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + "RelatorioRegistrosInventarioAcervo.pdf");
			FacesContext.getCurrentInstance().responseComplete();
			
		}
	}
	
	
	/**
	 * Configurando os parâmetros do relatório
	 *
	 * @Map<String,String>
	 */
	private Map<String, String> configuraInformacoesArquivoGerado(int qtdTitulos, int qtdMateriais) throws DAOException{
		
		Map<String, String> props = new HashMap<String, String>();
		
		BibliotecaDao dao = null;
		
		try{
		
			dao = getDAO( BibliotecaDao.class );
			
			props.put("descricaoBiblioteca", dao.findDescricaoBibliotecaInternaAtiva(idBiblioteca));
			props.put("nomeRelatorio", " Registros Realizados pelo Usuario: " +dao.findByPrimaryKey(idPessoa, Pessoa.class, "nome").getNome());
		
		}finally{
			if(dao != null) dao.close();
		}
		
		
		props.put("descricaoInventario", inventariosAberto.getDescricaoCompleta());
		
		
		if(ordenacao == ORDENAR_POR_CODIGO_BARRAS)
			props.put("descricaoOrdenacao", "Ordenado por Código de Barras");
		else if(ordenacao == ORDENAR_POR_TITULO)
			props.put("descricaoOrdenacao", "Ordenado por Título");
		else if(ordenacao == ORDENAR_POR_LOCALIZACAO)
			props.put("descricaoOrdenacao", "Ordenado por Localização");
		
		// Caso 1 título possua exemplar e fasciculo ao mesmo tempo, o que não era para ocorrer!!!!
		// Deve ser contado 2 vezes para o relatário bater com o relatório de Total de Títulos e Materiais
		props.put("quantidadeTitulos", String.valueOf(qtdTitulos) ); 
		props.put("quantidadeMateriais", String.valueOf(qtdMateriais) );
		
		props.put("nomeInstituicao", RepositorioDadosInstitucionais.get("nomeInstituicao"));
		props.put("nomeSistema",  RepositorioDadosInstitucionais.get("nomeSigaa"));
		props.put("textoRodape", RepositorioDadosInstitucionais.get("siglaSigaa") +" | "+ RepositorioDadosInstitucionais.get("rodapeSistema"));
		    
		props.put("dataEmissao", new SimpleDateFormat("dd/MM/yyyy HH:mm").format(new Date()));
		
		props.put("caminhoLogoTipoInstituicao",   RepositorioDadosInstitucionais.get("logoInstituicao"));
		props.put("caminhoLogoTipoInformatica",  RepositorioDadosInstitucionais.get("logoInformatica"));
		
		return props;
	}

	
	
	
	
	/**
	 * <p>
	 * Busca o inventário aberto para a biblioteca selecionad na página de filtro.
	 * </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public void buscarInventarios(ActionEvent evt) throws DAOException {
		
		outputSteamDadosRelatorio = null;
		
		inventariosAberto = new InventarioAcervoBiblioteca();
		
		if (idBiblioteca == -1) {
			return;
		}
		
		InventarioAcervoBibliotecaDao dao = null;
		
		try {
			dao = getDAO(InventarioAcervoBibliotecaDao.class);
			
			inventariosAberto = dao.findAbertoOtimizadoByBiblioteca(idBiblioteca);
			
			if (inventariosAberto == null) {
				addMensagemErroAjax("Esta biblioteca não possui inventário aberto no momento.");
			}
			
			return;
		} finally {
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * <p>
	 * Método que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public Collection<SelectItem> getBibliotecasCombo () throws DAOException {
		if (bibliotecasCombo == null){
			BibliotecaDao dao = null;
			try{
				dao = getDAO(BibliotecaDao.class);
				bibliotecasCombo = dao.findAllBibliotecasInternasAtivas();
			}finally{
			  if (dao != null)  dao.close();
			}
		}
		
		return toSelectItems(bibliotecasCombo, "id", "descricaoCompleta");
	}
	
	
	
	/**
	 * Método que responde às requisições de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas físicas e jurídicas.
	 * 
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li></ul>
	 * </p>
	 *
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public List<Pessoa> autocompleteNomePessoa(Object event) throws DAOException {
		
		String nome = event.toString(); //Nome do item digitado no autocomplete
		
		Long cpfCnpj = StringUtils.getNumerosIniciais(nome);
		if (cpfCnpj != null){
			nome = null; //consulta pelo cpf/cnpj apenas
		}

		List<Pessoa> lista = new ArrayList<Pessoa>();
		
		GenericDAO genDAO = null;
		
		try{
			genDAO = getGenericDAO();
			
			if (nome == null){
				//Consulta pelo CPF
				Pessoa item = genDAO.findByExactField(Pessoa.class, "cpf_cnpj", cpfCnpj, true);
				if (item != null)
					lista.add(item);
			}else{
				lista = getDAO(RelatoriosBibliotecaDao.class).findPessoaByNome(nome);
			}
			
		}finally{
			if(genDAO != null) genDAO.close();
		}
		

		return lista;
	}


	
	/**
	 * <p>
	 * Retorna o inventário para o combo box na página. 
	 * </p>
	 * <p>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 * </p>
	 */
	public Collection <SelectItem> getInventariosCombo() throws DAOException{
		List<InventarioAcervoBiblioteca> inventarios = new ArrayList<InventarioAcervoBiblioteca>();
		
		if(inventariosAberto != null)
			inventarios.add(inventariosAberto);
		
		return toSelectItems(inventarios, "id", "descricao");
	}

	

	public int getIdPessoa() {
		return idPessoa;
	}


	public void setIdPessoa(int idPessoa) {
		this.idPessoa = idPessoa;
	}


	public String getNomePessoa() {
		return nomePessoa;
	}


	public void setNomePessoa(String nomePessoa) {
		this.nomePessoa = nomePessoa;
	}


	public Integer getIdBiblioteca() {
		return idBiblioteca;
	}


	public void setIdBiblioteca(Integer idBiblioteca) {
		this.idBiblioteca = idBiblioteca;
	}


	public Integer getIdInventario() {
		return idInventario;
	}


	public void setIdInventario(Integer idInventario) {
		this.idInventario = idInventario;
	}


	public ByteArrayOutputStream getOutputSteamDadosRelatorio() {
		return outputSteamDadosRelatorio;
	}


	public void setOutputSteamDadosRelatorio(
			ByteArrayOutputStream outputSteamDadosRelatorio) {
		this.outputSteamDadosRelatorio = outputSteamDadosRelatorio;
	}


	public void setBibliotecasCombo(Collection<Biblioteca> bibliotecasCombo) {
		this.bibliotecasCombo = bibliotecasCombo;
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

	
}
