/*
 * PesquisarArtigoMBean.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;

/**
 *     <p> Gerencia a p�gina de pesquisa de artigos utilizada pelos bibliotec�rios. </p>
 *
 *     <p> A diferen�a em rela��o aos outroas pesquisas � que essa permite altera remover, entre outras opera��es. </p>
 *    
 * @author jadson
 * @since 07/05/2009
 * @version 1.0 criacao da classe
 *
 */
@Component(value="pesquisarArtigoMBean")
@Scope(value="request")
public class PesquisarArtigoMBean extends PesquisarAcervoPaginadoBiblioteca{

	/** A p�gina de pesquisa de artigos utilizados pelos bibliotec�rios */
	public static final String PAGINA_PESQUISA_ARTIGO = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaArtigo.jsp";

	/**
	 *  Usado para gerar consultas nativas do banco
	 */
	private GeraPesquisaTextual geradorPesquisa;
	
	/** O t�tulo do artigo digitado pelo usu�rio */
	private String tituloArtigo;
	/** O autor do artigo digitado pelo usu�rio */
	private String autorArtigo;
	/** As palavras chaves do artigo digitado pelo usu�rio */
	private String palavraChave;
	/** O n�mero do sistema do artigo digitado pelo usu�rio */
	private Integer numeroDoSistema;
	
	/** o que deve ser buscado, marcados pelos checkbox na p�gina */
	private boolean buscarNumeroSistema;
	/** o que deve ser buscado, marcados pelos checkbox na p�gina */
	private boolean buscarTitulo;
	/** o que deve ser buscado, marcados pelos checkbox na p�gina */
	private boolean buscarAutor;
	/** o que deve ser buscado, marcados pelos checkbox na p�gina */
	private boolean buscarPalavraChave;
	
	
	
	/** opera��o que s�o feitas em cima de um artigo a partir da p�gina de pesquisa  */
	private static final int OPERACAO_PESQUISAR = 1;
	
	/** A opera��o atual sendo realizada*/
	private int operacao = -1;
	
	
	
	/**
	 * Construtor padr�o, inicia o gerador de pesquisa textual.
	 */
	public PesquisarArtigoMBean(){
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.TITULO.getValor();
	}
	
	
	/**
	 * Inicia a pesquisa geralmente chamada do menu principal da biblioteca
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/menus/processos_tecnicos.jsp
	 * @return
	 */
	public String iniciarPesquisa(){
		
		operacao = OPERACAO_PESQUISAR;
		
		return telaPesquisaArtigo();
	}

	
	
	
	/** 
	 *     M�todo que realiza a pesquisa dos artigos de acordo com o que foi 
	 * preenchido pelo usu�rio no formul�rio.
	 * <br/>
	 * Chamado na pagina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaArtigo.jsp
	 *
	 * @param e
	 * @throws DAOException 
	 * @throws  
	 * @throws DAOException 
	 */
	public String pesquisar() throws DAOException {
		
		limpaResultadoPesquisa();
		
		ArtigoDePeriodicoDao  artigoDao = null;
		
		artigoDao = getDAO(ArtigoDePeriodicoDao.class); 
		
		String tituloBusca = tituloArtigo;
		String autorBusca = autorArtigo;
		String palavraChaveBusca = palavraChave;
	
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		if( buscarNumeroSistema) {

			
			if(numeroDoSistema == null){
				addMensagemWarning("Informe o N�mero do Sistema da Autoridade");
			}else{
				
				CacheEntidadesMarc artigo = artigoDao.findByNumeroSistema(numeroDoSistema);
			
				if(artigo != null){
					resultadosBuscados.add(artigo);
					quantidadeTotalResultados = resultadosBuscados.size();
					quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
					resultadosPaginadosEmMemoria.addAll(PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados));
				}else{
					addMensagemWarning("N�o Foi encontrado nenhum Artigo com o N�mero do Sistema Informado.");
				}
			}
			
		}else{
			
			if(StringUtils.isEmpty(tituloArtigo) || tituloArtigo.length() <= 2) buscarTitulo = false;
			if(StringUtils.isEmpty(autorArtigo) || autorArtigo.length() <= 2) buscarAutor = false;
			if(StringUtils.isEmpty(palavraChave) || palavraChave.length() <= 2) buscarPalavraChave = false;
			
			if( ! buscarTitulo) tituloBusca = null;
			if( ! buscarAutor) autorBusca = null;
			if( ! buscarPalavraChave) palavraChaveBusca = null;
			
			if( ! buscarTitulo && ! buscarAutor && ! buscarPalavraChave ){
				addMensagemWarning("Por favor, informe um Crit�rio de Busca.");
			}else{
				
				resultadosBuscados = artigoDao.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, campoOrdenacao, tituloBusca, autorBusca, palavraChaveBusca, false, null);
				quantidadeTotalResultados = resultadosBuscados.size();
				quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
				
				resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
				
				if(quantidadeTotalResultados == 0){
					addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				}
			}
		
		}
		
		return telaPesquisaArtigo();
			
	}
	
	
	
	
	
	
	/**
	 *  Limpa os dados da pesquisa.
	 *  
	 * <br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaArtigo.jsp
	 * @return
	 */
	public String limparDadosPesquisa(){
		
		
		limpaResultadoPesquisa();
		limpaFormularioPesquisa();
		
		return telaPesquisaArtigo();
		
	}
	
	/**
	 * Limpa apenas o formul�rio da pesquisa
	 */
	private void limpaFormularioPesquisa(){
		tituloArtigo = null;
		autorArtigo = null;
		palavraChave = null;
		numeroDoSistema = null;
		buscarTitulo = false;
		buscarAutor = false;
		buscarPalavraChave = false;
		buscarNumeroSistema = false;
		quantideResultadosPorPagina = 1;
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.TITULO.getValor();
	}
	
	
	
	
	

	
	
	/**
	 * 
	 * Retorna os campos que o usu�rio vai pode escolher a ordena��o no resultado da pesquisa.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaArtigo.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox(){
		
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_ARTIGOS){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}
	
	
	///////////// telas de navega��o /////////////// 
	
	/**
	 * <p>M�todo n�o chamado por jsp. </p>
	 */
	public String telaPesquisaArtigo(){
		return forward(PAGINA_PESQUISA_ARTIGO);
	}


	///////////// opera��es /////////
	
	public boolean isOperacaoPesquisar(){
		return operacao == OPERACAO_PESQUISAR;
	}


	/// sets e gets
	
	
	public String getTituloArtigo() {
		return tituloArtigo;
	}


	public void setTituloArtigo(String tituloArtigo) {
		this.tituloArtigo = tituloArtigo;
	}


	public String getAutorArtigo() {
		return autorArtigo;
	}


	public void setAutorArtigo(String autorArtigo) {
		this.autorArtigo = autorArtigo;
	}


	public String getPalavraChave() {
		return palavraChave;
	}


	public void setPalavraChave(String palavraChave) {
		this.palavraChave = palavraChave;
	}


	public boolean isBuscarTitulo() {
		return buscarTitulo;
	}

	public void setBuscarTitulo(boolean buscarTitulo) {
		this.buscarTitulo = buscarTitulo;
	}

	public boolean isBuscarAutor() {
		return buscarAutor;
	}

	public void setBuscarAutor(boolean buscarAutor) {
		this.buscarAutor = buscarAutor;
	}

	public boolean isBuscarPalavraChave() {
		return buscarPalavraChave;
	}

	public void setBuscarPalavraChave(boolean buscarPalavraChave) {
		this.buscarPalavraChave = buscarPalavraChave;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public Integer getNumeroDoSistema() {
		return numeroDoSistema;
	}

	public void setNumeroDoSistema(Integer numeroDoSistema) {
		this.numeroDoSistema = numeroDoSistema;
	}

	public boolean isBuscarNumeroSistema() {
		return buscarNumeroSistema;
	}

	public void setBuscarNumeroSistema(boolean buscarNumeroSistema) {
		this.buscarNumeroSistema = buscarNumeroSistema;
	}

	public int getQuantideResultadosPorPagina() {
		return quantideResultadosPorPagina;
	}

	public void setQuantideResultadosPorPagina(int quantideResultadosPorPagina) {
		this.quantideResultadosPorPagina = quantideResultadosPorPagina;
	}

	public int getValorCampoOrdenacao() {
		return valorCampoOrdenacao;
	}

	public void setValorCampoOrdenacao(int valorCampoOrdenacao) {
		this.valorCampoOrdenacao = valorCampoOrdenacao;
	}

	public int getQuantidadePaginas() {
		return quantidadePaginas;
	}

	public int getPaginaAtual() {
		return paginaAtual;
	}
	
	
}
