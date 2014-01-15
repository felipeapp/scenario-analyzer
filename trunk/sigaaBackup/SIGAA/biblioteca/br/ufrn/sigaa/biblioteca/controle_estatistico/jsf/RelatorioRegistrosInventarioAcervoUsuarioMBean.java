/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Relat�rio pelo qual o usu�rio pode consultas os registros realizados por ele para um determinado invent�rio do acervo.
 * mesmo esse invent�rio ainda estando aberto.
 *  </p>
 *
 * @author jadson
 *
 */
@Component("relatorioRegistrosInventarioAcervoUsuarioMBean")
@Scope("request")
public class RelatorioRegistrosInventarioAcervoUsuarioMBean extends SigaaAbstractController<Object>{

	/** P�gina com os filtros do relat�rio. */
	private static final String PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO = "/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp";
	
	/** Filtro para ordena��o dos resultados do relat�rio por c�digo de barras. */
	public static final int ORDENAR_POR_CODIGO_BARRAS = 1;

	/** Filtro para ordena��o dos resultados do relat�rio por T�tulo */
	public static final int ORDENAR_POR_TITULO = 2;

	/** Filtro para ordena��o dos resultados do relat�rio por localiza��o */
	public static final int ORDENAR_POR_LOCALIZACAO = 3;
	
	/** Filtro de pessoa utilizando no rich:suggestionbox. */
	protected int idPessoa;
	
	/** Nome da pessoa utilizada no filtro de pessoa no rich:suggestionbox. */
	protected String nomePessoa;
	
	
	/** A biblioteca selecionada no cadastro de invent�rios */
	private Integer idBiblioteca;
	
	/** O invent�rio aberto da biblioteca selecionada */
	private InventarioAcervoBiblioteca inventariosAberto;
	
	/** Armazena o invent�rio selecionado pelo usu�rio */
	private Integer idInventario;
	
	/** Guarda a lista de bibliotecas que v�o ser exibidas no combobox */
	private Collection<Biblioteca> bibliotecasCombo;
	
	/** Guarda os dados do arquivo pdf, antes dele ser enviado para o usu�rio */
	private ByteArrayOutputStream outputSteamDadosRelatorio;
	
	/** Filtro que indica como os registros ser�o ordenados. */
	private int ordenacao = ORDENAR_POR_CODIGO_BARRAS;
	
	
	/**
	 * Guarda os dados que ser�o usados para preencher o relat�rio. Reaproveita do relat�riode listagem de materiais porque a estrutura � a mesma.
	 * 
	 * IMPORTANTE: Tamb�m porque esse objeot cont�m a l�gica correta de ordena��o das localiza��es dos materiais.
	 * 
	 */
	private List<DadosRelatorioListagemMaterial> dadosRelatorio;
	
	/**
	 * 
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <p>M�todo n�o chamado por JSP.</>
	 *
	 * @see br.ufrn.sigaa.biblioteca.controle_estatistico.jsf.AbstractRelatorioBibliotecaMBean#configurar()
	 */
	public String iniciar() {
		
		return forward(PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
		
	}

	
	
	/**
	 * <p>
	 * Gera o relat�rio o relat�rio com os registros realizados pelo usu�rio para o invent�rio aberto selecionado.
	 * </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Invent�rio");
		}
		
		if ( idPessoa <= 0 ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Usu�rio");
			
		}
		
		if(hasErrors())
			return null;
		
		RelatorioInventarioAcervoDao dao = null;
		
		dadosRelatorio = new ArrayList<DadosRelatorioListagemMaterial>();
		
		// serve para contar a quantidade de t�tulos distintos
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
			
			/// Se a consulta n�o retornou resultados
			if ( quantidadeTotalResultados == 0 ) {
				addMensagemWarning("N�o foram encontrados materiais registrados para este invent�rio a partir do filtros selecionados.");
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
			
			addMensagemInformation("Relat�rio gerado com sucesso.");
			
			////////////////////////////////////////////////////////////////////////////////////////////
			
			return forward(PAGINA_FILTROS_RELATORIO_LISTAGEM_GERAL_ACERVO);
			
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * 
	 * M�todo que envia o arquivo pdf gerado no passo anterior para a sa�da do usu�rio.
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/controle_estatistico/filtrosRelatorioRegistrosInventarioAcervoUsuario.jsp</li>
	 *   </ul>
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
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
	 * Configurando os par�metros do relat�rio
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
			props.put("descricaoOrdenacao", "Ordenado por C�digo de Barras");
		else if(ordenacao == ORDENAR_POR_TITULO)
			props.put("descricaoOrdenacao", "Ordenado por T�tulo");
		else if(ordenacao == ORDENAR_POR_LOCALIZACAO)
			props.put("descricaoOrdenacao", "Ordenado por Localiza��o");
		
		// Caso 1 t�tulo possua exemplar e fasciculo ao mesmo tempo, o que n�o era para ocorrer!!!!
		// Deve ser contado 2 vezes para o relat�rio bater com o relat�rio de Total de T�tulos e Materiais
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
	 * Busca o invent�rio aberto para a biblioteca selecionad na p�gina de filtro.
	 * </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
				addMensagemErroAjax("Esta biblioteca n�o possui invent�rio aberto no momento.");
			}
			
			return;
		} finally {
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * <p>
	 * M�todo que retorna as bibliotecas do sistema em forma de SelectItem's.
	 * </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * M�todo que responde �s requisi��es de autocomplete com o componente
	 * rich:suggestionBox do RichFaces retornando uma lista de pessoas f�sicas e jur�dicas.
	 * 
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
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
	 * Retorna o invent�rio para o combo box na p�gina. 
	 * </p>
	 * <p>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
