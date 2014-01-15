/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 16/11/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.dao.biblioteca.BibliotecaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.CampoOrdenacaoConsultaAcervo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.dominio.GeraPesquisaTextual;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.negocio.GeraPesquisaTextualFactory;
import br.ufrn.sigaa.biblioteca.util.PaginacaoBibliotecaUtil;

/**
 *
 * <p> MBean para realiza a busca no acervo de artigos na parte publica do sistema </p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
@Component(value="pesquisaPublicaArtigosBibliotecaMBean")
@Scope(value="request")
public class PesquisaPublicaArtigosBibliotecaMBean extends PesquisarAcervoPaginadoBiblioteca{

	
	/**
	 * A p�gina de busca p�blica no acervo de artigos
	 */
	public static final String PAGINA_BUSCA_PUBLICA_ARTIGOS  = "/public/biblioteca/buscaPublicaAcervoArtigos.jsp";
	
	/**
	 *  usado para gerar consultas nativas do banco
	 */
	private GeraPesquisaTextual geradorPesquisa;
	
	/** Os campos da busca simples de um artigo no acervo   */
	protected String titulo, palavrasChaves, autor;
	
	
	/** INDICA POR QUAIS CAMPOS DEVE-SE BUSCAR  */
	protected boolean buscarTitulo, buscarPalavrasChaves, buscarAutor;
	
	/**
	 * Construtor padr�o
	 */
	public PesquisaPublicaArtigosBibliotecaMBean(){
		geradorPesquisa = new GeraPesquisaTextualFactory().getGeradorPesquisaTextual();	
		valorCampoOrdenacao = CampoOrdenacaoConsultaAcervo.TITULO.getValor();
	}
	
	
	/** Os ids das bibliotecas que possuem acervo p�blico no sistema. O resultado da busca p�blica s� deve buscar nessas biblioteca. */
	protected List<Integer> idsBibliotecasAcervoPublico;
	
	
	/**
	 *    M�todo que realiza a busca no acervo. � o mesmo m�todo usado pelos bibliotec�rios dentro
	 * do sistema, s� que est� sendo chamado da  p�gina de pesquisa p�blica e o resultado mostrado
	 * � um pouquinho diferente.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublica.jsp
	 */
	public String pesquisarAcervoArtigos() throws DAOException{
		
		ArtigoDePeriodicoDao daoArtigo = null;
	
		limparResultadosPesquisa();
		
		String tituloBusca = titulo;
		String autorBusca = autor;
		String palavraChavesBusca = palavrasChaves;
		
		
		if(StringUtils.isEmpty(titulo) || titulo.length() <= 2) buscarTitulo = false;
		if(StringUtils.isEmpty(autor) || autor.length() <= 2) buscarAutor = false;
		if(StringUtils.isEmpty(palavrasChaves) || palavrasChaves.length() <= 2) buscarPalavrasChaves = false;
		
		if(! buscarTitulo) tituloBusca = null;
		if(! buscarAutor) autorBusca = null;
		if(! buscarPalavrasChaves) palavraChavesBusca = null;
		
		CampoOrdenacaoConsultaAcervo campoOrdenacao =  CampoOrdenacaoConsultaAcervo.getCampoOrdenacao(valorCampoOrdenacao);
		
		try{
			daoArtigo = getDAO(ArtigoDePeriodicoDao.class);
				
			if(StringUtils.notEmpty(tituloBusca) || StringUtils.notEmpty(palavraChavesBusca) || StringUtils.notEmpty(autorBusca)  ){
			
				resultadosBuscados = daoArtigo.findAllArtigosResumidosAtivosByTituloAutorPalarvaChave(geradorPesquisa, campoOrdenacao, tituloBusca, autorBusca, palavraChavesBusca, true, getIdsBibliotecasAcervoPublico());
		
				quantidadeTotalResultados = resultadosBuscados.size();
				quantidadePaginas = PaginacaoBibliotecaUtil.calculaQuantidadePaginas(quantidadeTotalResultados, quantideResultadosPorPagina);
				
				resultadosPaginadosEmMemoria = PaginacaoBibliotecaUtil.realizaPaginacaoEmMemoria(resultadosBuscados, paginaAtual,  quantideResultadosPorPagina,  quantidadeTotalResultados);
				
				verificaLimitesDaBusca();
			}else{
				addMensagemWarning("Por favor, informe um crit�rio de busca");
			}
	
		}finally{
			if(daoArtigo != null) daoArtigo.close();
		}
		
		return telaBuscaPublicaAcervoArtigos();
		
	}
	
	/** verifica se os limits das buscas foram alcan�ados para mostrar a mensagem ao usu�ria */
	private void verificaLimitesDaBusca(){
		
		if ( quantidadeTotalResultados == 0)
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		
		if (  ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS.compareTo(quantidadeTotalResultados ) <= 0) {
			addMensagemWarning("A busca de artigos resultou em um n�mero muito grande de resultados, somente os "+ArtigoDePeriodicoDao.LIMITE_BUSCA_ARTIGOS+" primeiros est�o sendo mostrados.");
		}
	}
	
	
	/**
	 *   Limpar os resultados da busca de no acervo de artigos.
	 * 
	 *  <br/><br/>
	 *  Chamado a partir da p�gina: /sigaa.war/public/biblioteca/buscaPublicaAcervoArtigos.jsp
	 */
	public String limparResultadosBuscaAcervoArtigos(){
		limparDadosFormularioBusca();
		limparResultadosPesquisa();
		return telaBuscaPublicaAcervoArtigos();
	}
	
	/**
	 *  Limpa a lista de os resultados da pesquisa
	 */
	private void limparDadosFormularioBusca(){
		titulo = null;
		palavrasChaves = null;
		autor = null;
		
		buscarTitulo = false;
		buscarAutor = false;
		buscarPalavrasChaves = false;
		
	}
	
	
	/**
	 *  Limpa a lista de os resultados da pesquisa
	 */
	private void limparResultadosPesquisa(){
		paginaAtual = 1;
		quantidadeTotalResultados = 0;
		quantidadePaginas = 1;
		
		resultadosBuscados = new ArrayList<CacheEntidadesMarc>();
		
	}
	
	/**
	 *   Retorna os ids das bibliotecas que possuem acervo p�blico. (liberado para consulta a usu�rios que n�o trabalham nas bibliotecas)
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 */
	public List<Integer>  getIdsBibliotecasAcervoPublico() throws DAOException{
		if(idsBibliotecasAcervoPublico == null){
			BibliotecaDao dao = null;
			try{
				dao =getDAO(BibliotecaDao.class);
				idsBibliotecasAcervoPublico = dao.findIdsBibliotecaAcervoPublico();
			}finally{
				if(dao != null) dao.close();
			}
		}
		return idsBibliotecasAcervoPublico;
	}
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoPaginadoBiblioteca#getCampoOrdenacaoResultadosComboBox()
	 */
	@Override
	public Collection<SelectItem> getCampoOrdenacaoResultadosComboBox() {
		Collection<SelectItem> lista = new ArrayList<SelectItem>();
		
		for(CampoOrdenacaoConsultaAcervo campo : CampoOrdenacaoConsultaAcervo.CAMPOS_ORDENACAO_ARTIGOS_PUBLICO){
			lista.add(new SelectItem(campo.getValor(), campo.getDescricao()));
		}
		
		return lista;
	}

	
	
	/**
	 *  M�todo n�o chamado por jsp.
	 */
	public String telaBuscaPublicaAcervoArtigos(){
		return forward(PAGINA_BUSCA_PUBLICA_ARTIGOS);
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getPalavrasChaves() {
		return palavrasChaves;
	}

	public void setPalavrasChaves(String palavrasChaves) {
		this.palavrasChaves = palavrasChaves;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public boolean isBuscarTitulo() {
		return buscarTitulo;
	}

	public void setBuscarTitulo(boolean buscarTitulo) {
		this.buscarTitulo = buscarTitulo;
	}

	public boolean isBuscarPalavrasChaves() {
		return buscarPalavrasChaves;
	}

	public void setBuscarPalavrasChaves(boolean buscarPalavrasChaves) {
		this.buscarPalavrasChaves = buscarPalavrasChaves;
	}

	public boolean isBuscarAutor() {
		return buscarAutor;
	}

	public void setBuscarAutor(boolean buscarAutor) {
		this.buscarAutor = buscarAutor;
	}

	
	
	
	
}
