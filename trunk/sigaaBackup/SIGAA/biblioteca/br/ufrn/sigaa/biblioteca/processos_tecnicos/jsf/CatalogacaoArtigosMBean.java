/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 05/05/2009
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ArtigoDePeriodicoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ArtigoDePeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Fasciculo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCatalogaAtualizaArtigoPeriodico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.DetalhesMateriaisDeUmTituloMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarArtigoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarFasciculoMBean;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *     MBean respons�vel por catalogar artigos de peri�dicos. A cataloga��o de peri�dicos
 * vai ocorrer em uma tela mais simplificada do que aquela tela copiada do aleph e usada nos outros
 * casos. O sistema vai gerar o campos MARC e criar um novo t�tulo na base.
 *
 * @author jadson
 * @since 05/05/2009
 * @version 1.0 cria��o da classe
 *
 */
@Component("catalogacaoArtigosMBean")
@Scope("request")
public class CatalogacaoArtigosMBean extends SigaaAbstractController<ArtigoDePeriodico>{

	/**
	 * Armazena a p�gina principal do processo de cataloga��o de artigo.
	 */
	public static final String PAGINA_CATALOGA_ARTIGO = "/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaArtigo.jsp";
	
	
	//    OS CAMPOS MARC QUE O USU�RIO VAI DIGITAR NA PAGINA E O SISTEMA VAI TER QUE GERAR   //
	
	/** Campo 245$a bibliogr�fico */
	private String titulo;
	
	/** Campo 100$a bibliogr�fico */
	private String autor;
	
	/** Campo 700$a */
	private List<String> autoresSecundarios;
	
	/** Campo 773$g bibliogr�fico */
	private String intervaloPaginas;
	
	/** Cada palavra chave gera um campo 650$a bibliogr�fico */
	private List<String> palavrasChaves = new ArrayList<String>();
	
	/** Campo 260$a */
	private String localPublicacao;       // 260$a

	/** Campo 260$b */
	private String editora;               // 260$b

	/** Campo 260$c */
	private String ano;                   // 260$c

	/** Campo 520$a bibliogr�fico */
	private String resumo;                // campo 520$a bibliogr�fico

	/**
	 * Armazena temporariamente os dados dos autores secund�rios.
	 */
	private DataModel autoresSecundariosDataModel;
	
	/**
	 * Armazena temporariamente os dados das palavras chave.
	 */
	private DataModel palavrasChaveDataModel;
	
	/** o fasc�culo para onde o artigo vai */
	private Fasciculo fasciculoDoArtigo;
	
	/** os artigos que esse peri�dico j� possua catalogado */
	private List<CacheEntidadesMarc> artigosDoFasciculo;
	
	/**
	 * Constante que armazena o valor da opera��o 'Catalogar Artigo'
	 */
	public static final int OPERACAO_CATALOGAR_ARTIGO = 1;

	/**
	 * Constante que armazena o valor da opera��o 'Alterar Artigo'
	 */
	public static final int OPERACAO_ALTERAR_ARTIGO = 2;
	
	/**
	 * Armazena a opera��o atualmente em execu��o.
	 */
	private int operacao = -1;
	
	/** indica para onde o bot�o voltar deve ir */
	private boolean voltarTelaDetalhesMateriais = false;
	
	/**  Se true, volta para p�gina que chamou  a atualiza��o, sen�o fica na mesma p�gina. */
	private String finalizarCatalogacao = "false";
	
	/**   Se true, volta para a p�gina que chamou a atualiza��o, sen�o fica na mesma p�gina. */
	private String finalizarAtualizacao = "false";
	
	/** Se true, a opera��o do bot�o 'Voltar' redireciona o fluxo de navega��o para a p�gina que requisitou a action de edi��o de artigo. */
	private boolean voltarParaRequest;
	
	/** Armazena a p�gina que requisitou a action de edi��o de artigo. */
	private String requestURL;
	
	public CatalogacaoArtigosMBean(){
		palavrasChaves.add("");
	}
	
	
	
	/**
	 *     Depois que o usu�rio escolheu o fasc�culo na tela de pesquisa. Redireciona para a p�gina
	 * na qual o usu�rio deve digitar os dados do artigo daquele fasc�culo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processo_tecnicos_pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String iniciarCatalogacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		fasciculoDoArtigo = getGenericDAO().findByPrimaryKey(getParameterInt("idFasciculoDoArtigo", 0), Fasciculo.class);
		
		artigosDoFasciculo = new ArrayList<CacheEntidadesMarc>();
	
		List<Integer> idsArtigos = getDAO(ArtigoDePeriodicoDao.class)
				.findIdArtigosDoFasciculoNaoRemovidos(fasciculoDoArtigo.getId());

		for (Integer idArtigo : idsArtigos) {
			artigosDoFasciculo.add(BibliotecaUtil.obtemDadosArtigoCache(idArtigo));
		}
		 
		prepareMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO);
		
		this.operacao = OPERACAO_CATALOGAR_ARTIGO;
		
		iniciaColecaoAutoresSecundarios();
		iniciaColecaoPalavrasChave();
		
		return telaCatalogaArtigoPeriodico();
		
	}
	
	
	
	
	/**
	 *     Depois que o usu�rio escolheu o fasc�culo na tela de pesquisa. Redireciona para a p�gina
	 * na qual o usu�rio deve digitar os dados do artigo daquele fasc�culo.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processo_tecnicos_pesquisa_acervo/resultadoPesquisaFasciculo.jsp
	 */
	public String iniciarCatalogacaoApartirTelaDetalhesMateriais() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		fasciculoDoArtigo = getGenericDAO().findByPrimaryKey(getParameterInt("idFasciculoDoArtigo", 0), Fasciculo.class);
		
		artigosDoFasciculo = new ArrayList<CacheEntidadesMarc>();
		
		List<Integer> idsArtigos = getDAO(ArtigoDePeriodicoDao.class)
				.findIdArtigosDoFasciculoNaoRemovidos(fasciculoDoArtigo.getId());

		for (Integer idArtigo : idsArtigos) {
			artigosDoFasciculo.add(BibliotecaUtil.obtemDadosArtigoCache(idArtigo));
		}
		 
		prepareMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO);
		
		this.operacao = OPERACAO_CATALOGAR_ARTIGO;
		
		voltarTelaDetalhesMateriais = true;
		
		iniciaColecaoAutoresSecundarios();
		iniciaColecaoPalavrasChave();
		
		return telaCatalogaArtigoPeriodico();
		
	}
	
	
	
	/**
	 * Inicia o caso de uso de alterar um artigo
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/
	 */
	public String iniciarParaEdicaoArtigo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if (getParameterBoolean("voltarParaRequest")) {
			voltarParaRequest = true;
			requestURL = getCurrentRequest().getRequestURI().replaceFirst(getContextPath(), "");
		}
		else {
			voltarParaRequest = false;
		}
		
		GenericDAO dao = null;
		CacheEntidadesMarc artigoTemp = null;
		
		try {
		
			dao = getGenericDAO();
			obj = dao.refresh(new ArtigoDePeriodico(getParameterInt("idArtigoParaEdicao", 0)));
		
			if(! obj.isAtivo()){
				addMensagemErro("O artigo foi removido, n�o pode ser alterado.");
				return null;
			}
			
			fasciculoDoArtigo = obj.getFasciculo();
		
			if (fasciculoDoArtigo != null){
				
				artigosDoFasciculo = new ArrayList<CacheEntidadesMarc>();
				
				List<Integer> idsArtigos = getDAO(ArtigoDePeriodicoDao.class)
					.findIdArtigosDoFasciculoNaoRemovidos(fasciculoDoArtigo.getId());

				for (Integer idArtigo : idsArtigos) {
					artigosDoFasciculo.add(BibliotecaUtil.obtemDadosArtigoCache(idArtigo));
				}
			
			}
			
			artigoTemp = BibliotecaUtil.obtemDadosArtigoCache(obj.getId());
			
		}finally{
			// Fechar para n�o ficar duas sess�es do hibernate abertas sen�o vai dar erro no processador
			// CATALOGA_ARTIGO_DE_PERIODICO.
			if(dao != null)  dao.close();
		}
		
		
		this.titulo = artigoTemp.getTitulo();
		this.autor = artigoTemp.getAutor();
		
		if ( autoresSecundarios == null )
			autoresSecundarios = new ArrayList<String>();
		else
			autoresSecundarios.clear();
		
		autoresSecundarios.addAll( artigoTemp.getAutoresSecundariosFormatados() );
		
		if(palavrasChaves == null)
			palavrasChaves = new ArrayList<String>();
		else
			palavrasChaves.clear();
		
		palavrasChaves.addAll(artigoTemp.getAssuntosFormatados());
		
		Collections.sort(palavrasChaves);
		
		this.intervaloPaginas = artigoTemp.getIntervaloPaginas();
		
		/// para os artigos o usu�rio s� pode digitar um local, uma editora e um ano.
		// que criar� um campo 260. Apesar de no MARC esse campo pode ser repetido.
		if(artigoTemp.getLocaisPublicacaoFormatados().size() > 0)
			this.localPublicacao = artigoTemp.getLocaisPublicacaoFormatados().get(0);
		
		if(artigoTemp.getEditorasFormatadas().size() > 0)
			this.editora = artigoTemp.getEditorasFormatadas().get(0);
		
		if(artigoTemp.getAnosFormatados().size() > 0)
			this.ano = artigoTemp.getAnosFormatados().get(0);
		
		this.resumo = artigoTemp.getResumo();
		
		prepareMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO);
		
		this.operacao = OPERACAO_ALTERAR_ARTIGO;
		
		iniciaColecaoAutoresSecundarios();
		iniciaColecaoPalavrasChave();
		
		return telaCatalogaArtigoPeriodico();
	}
	
	/**
	 * M�todo que inicializa a lista tempor�ria de palavras chave
	 */
	private void iniciaColecaoPalavrasChave(){
		
		if(palavrasChaves == null)
			palavrasChaves = new ArrayList<String>();
		else
			if(palavrasChaves.size() == 0)
				palavrasChaves.add("");
		
		palavrasChaveDataModel = new ListDataModel(palavrasChaves);
		
	}

	/**
	 * M�todo que inicializa a lista tempor�ria de autores secund�rios
	 */
	private void iniciaColecaoAutoresSecundarios(){
		
		if (autoresSecundarios == null)
			autoresSecundarios = new ArrayList<String>();
		if ( autoresSecundarios.isEmpty() )
			autoresSecundarios.add("");
		
		autoresSecundariosDataModel = new ListDataModel(autoresSecundarios);
		
	}
	
	/**
	 * Remove da posi��o que o usu�rio selecionou na p�gina.
	 * 
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaArtigo.jsp
	 */
	public void removerPalavraChave(ActionEvent e){
		if(palavrasChaves.size() > 1)
			palavrasChaves.remove(palavrasChaveDataModel.getRowIndex());
	}
	
	/**
	 *     M�todo chamado para adicionar uma nova palavra chave na p�gina para o usu�rio.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp";
	 */
	public void adicionarPalavraChave(ActionEvent evt){
		if (palavrasChaves == null)
			palavrasChaves = new ArrayList<String>();
		
		palavrasChaves.add("");
	
		palavrasChaveDataModel = new ListDataModel(palavrasChaves);
	}
	
	/**
	 * AJAX - Adiciona um novo autor secund�rio.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp</li></ul>
	 */
	public void adicionarAutorSecundario( ActionEvent evt ) {
		autoresSecundarios.add("");
	}
	
	/**
	 * AJAX - Remove um autor secund�rio.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp</li></ul>
	 */
	public void removerAutorSecundario( ActionEvent evt ) {
		autoresSecundarios.remove( autoresSecundariosDataModel.getRowIndex() );
	}
	
	/**
	 *    M�todo que cria um t�tulo com os dados digitados pelo usu�rio. Atribui esse t�tulo ao artigo,
	 * o artigo ao fasc�culo e salva todo mundo no banco.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp
	 */
	public String salvarArtigo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(usuarioDigitouDadosCorretos()){
			
			GenericDAO dao = getGenericDAO();
			
			// tem que est� com a sess�o aberta para pegar os artigos e o t�tulo desse fasc�culo
			fasciculoDoArtigo = dao.refresh(fasciculoDoArtigo);
			
			ArtigoDePeriodico artigo =  CatalogacaoUtil.montaArtigoPeriodico(fasciculoDoArtigo,
					titulo, autor, autoresSecundarios, intervaloPaginas, palavrasChaves,
					localPublicacao, editora, ano, resumo);
			artigo.setFasciculo(fasciculoDoArtigo);
			
			if(obj != null){
				artigo.setId(obj.getId()); // mant�m o id  e n�mero do sistema do artigo para o usu�rio poder permanecer na mesma p�gina e ficar salvando.
				artigo.setNumeroDoSistema(obj.getNumeroDoSistema());
			}
			
			MovimentoCatalogaAtualizaArtigoPeriodico movimento = new MovimentoCatalogaAtualizaArtigoPeriodico(artigo, false);
			movimento.setCodMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO);
			
			try {
				obj = execute(movimento);
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("Artigo Salvo com Sucesso com o N� do Sistema: "+artigo.getNumeroDoSistema());
			
			if("true".equals(finalizarCatalogacao)){
				return voltarPagina(); // volta para a p�gina que chamou a altera��o
			}else{
				fasciculoDoArtigo.setAssinatura( getGenericDAO().refresh(fasciculoDoArtigo.getAssinatura()));
				prepareMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO); // usu�rio apenas salvou, ent�o fica na mesma p�gina e libera para salvar novamente.
				return null;
			}
			
		}else{
			return null;
		}
	}
	
	
	
	
	
	/**
	 *    M�todo que atualiza o t�tulo do artigo de peri�dico.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp
	 */
	public String atualizarArtigo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		if(usuarioDigitouDadosCorretos()){

			fasciculoDoArtigo = getGenericDAO().refresh(fasciculoDoArtigo);
			
			if(fasciculoDoArtigo == null){
				addMensagemErro("N�o foi poss�vel atualizar o artigo porque ele n�o pertence a nenhum fasc�culo.");
				return null;
			}
			
			ArtigoDePeriodico artigo =  CatalogacaoUtil.montaArtigoPeriodico(fasciculoDoArtigo,
					titulo, autor, autoresSecundarios, intervaloPaginas, palavrasChaves,
					localPublicacao, editora, ano, resumo);
			artigo.setFasciculo(fasciculoDoArtigo);
			artigo.setNumeroDoSistema(obj.getNumeroDoSistema());
			artigo.setId(obj.getId());
			
			
			MovimentoCatalogaAtualizaArtigoPeriodico movimento = new MovimentoCatalogaAtualizaArtigoPeriodico(artigo, true);
			movimento.setCodMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO);
			
			try {
				execute(movimento);
			} catch (NegocioException ne) {
				ne.printStackTrace();
				addMensagens(ne.getListaMensagens());
				return null;
			}
			
			addMensagemInformation("Artigo atualizado com Sucesso");
			
			if("true".equals(finalizarAtualizacao)){
				return voltarPagina(); // volta para a p�gina que chamou a altera��o
			}else{
				fasciculoDoArtigo.setAssinatura( getGenericDAO().refresh(fasciculoDoArtigo.getAssinatura()));
				prepareMovimento(SigaaListaComando.CATALOGA_ARTIGO_DE_PERIODICO); // usu�rio apenas salvou, ent�o fica na mesma p�gina e libera para salvar novamente.
				return null;
			}
			
		}else{
			return null;
		}
	}
	
	
	
	
	
	/**
	 *     M�todo utilizando para decidir para onde se deve ir quando o usu�rio clicar no bot�o voltar
	 * da p�gina
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaCatalogaoArtigo.jsp";
	 */
	public String voltarPagina() throws DAOException{
		
		if (voltarParaRequest) {
			return forward(requestURL);
		}
		
		if(voltarTelaDetalhesMateriais){
			DetalhesMateriaisDeUmTituloMBean  mBean = getMBean("detalhesMateriaisDeUmTituloMBean");
			return mBean.telaInformacoesMateriais();
		}
		
		if(isOperacaoAtualizaArtigo()){
			PesquisarArtigoMBean mBean = getMBean("pesquisarArtigoMBean");
			return mBean.pesquisar();
		}
			
		PesquisarFasciculoMBean  mBean = getMBean("pesquisarFasciculoMBean");
		return mBean.voltaCatalogacaoArtigo();
				
	}
	
	
	
	
	
	
	
	
	/**
	 * Valida os dados que o usu�rio digitou
	 */
	private boolean usuarioDigitouDadosCorretos(){
		
		boolean contemErro = false;
		
		if(palavrasChaves != null || palavrasChaves.size() == 0){
			
			boolean possuiPalavraCache = false;
			
			for (String palavra : palavrasChaves) {
				if(br.ufrn.arq.util.StringUtils.notEmpty(palavra))
					possuiPalavraCache = true;
			}
			
			if(! possuiPalavraCache){
				addMensagemErro("Informe pelo menos uma palavra chaves para o artigo.");
				contemErro = true;
			}
		}else{
			addMensagemErro("Informe pelo menos uma palavra chaves para o artigo.");
			contemErro = true;
		}
		
		if(StringUtils.isEmpty(intervaloPaginas)){
			addMensagemErro("Informe o intervalo de p�ginas do artigo.");
			contemErro = true;
		}
		
		if(StringUtils.isEmpty(autor)){
			addMensagemErro("Informe o autor do artigo.");
			contemErro = true;
		}
		
		
		if(StringUtils.isEmpty(titulo)){
			addMensagemErro("Informe o t�tulo do artigo.");
			contemErro = true;
		}
		
		
		return ! contemErro;
		
	}
	
	
	
	
	/**
	 *        M�todo que Busca as informa��es de altera��o de um T�tulo ou Autoridade para mostra ao
	 * usu�rio na hora da cataloga��o. Serve para ter uma id�ia de quem mexeu no registro da entidade.
	 *
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaHistoricoAlteracaoCatalogacao.jsp
	 */
	public List <Object []> getHistoricoAlteracaoArtigo()throws DAOException{
		
		List <Object []> listaHistoricoAlteracoes = new ArrayList<Object[]>();
		
		int idArtigo = getParameterInt("idArtigoVisualizarHistorico", 0);
		
		if(idArtigo != 0)
			listaHistoricoAlteracoes = getDAO(ArtigoDePeriodicoDao.class).findAlteracoesByArtigoDePeriodicoPeriodo(idArtigo, null, null);
		
		for (Object[] object : listaHistoricoAlteracoes)
			object[1] = object[1].toString().replaceAll("\n", "<br/>");
		
		return listaHistoricoAlteracoes;
	}



	
	//////////////// tela de navega��o  ///////////////
	
	/**
	 * M�todo que retorna a p�gina principal do processo de cataloga��o de arquivo.
	 * 
	 * N�o � chamado por nenhuma JSP.
	 */
	public String telaCatalogaArtigoPeriodico(){
		return forward(PAGINA_CATALOGA_ARTIGO);
	}
	
	
	
	////////// opera��es /////////////
	
	
	public boolean isOperacaoCatalogaArtigo(){
		return operacao == OPERACAO_CATALOGAR_ARTIGO;
	}
	
	public boolean isOperacaoAtualizaArtigo(){
		return operacao == OPERACAO_ALTERAR_ARTIGO;
	}
	
	
	
	// sets e gets
	
	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAutor() {
		return autor;
	}

	public void setAutor(String autor) {
		this.autor = autor;
	}

	public String getIntervaloPaginas() {
		return intervaloPaginas;
	}

	public void setIntervaloPaginas(String intervaloPaginas) {
		this.intervaloPaginas = intervaloPaginas;
	}

	public List<String> getPalavrasChaves() {
		return palavrasChaves;
	}

	public void setPalavrasChaves(List<String> palavrasChaves) {
		this.palavrasChaves = palavrasChaves;
	}

	public String getResumo() {
		return resumo;
	}

	public void setResumo(String resumo) {
		this.resumo = resumo;
	}
	
	public Fasciculo getFasciculoDoArtigo() {
		return fasciculoDoArtigo;
	}

	public void setFasciculoDoArtigo(Fasciculo fasciculoDoArtigo) {
		this.fasciculoDoArtigo = fasciculoDoArtigo;
	}

	public List<CacheEntidadesMarc> getArtigosDoFasciculo() {
		return artigosDoFasciculo;
	}

	public void setArtigosDoFasciculo( List<CacheEntidadesMarc> artigosDoFasciculo) {
		this.artigosDoFasciculo = artigosDoFasciculo;
	}

	public int getOperacao() {
		return operacao;
	}

	public void setOperacao(int operacao) {
		this.operacao = operacao;
	}

	public boolean isVoltarTelaDetalhesMateriais() {
		return voltarTelaDetalhesMateriais;
	}

	public void setVoltarTelaDetalhesMateriais(boolean voltarTelaDetalhesMateriais) {
		this.voltarTelaDetalhesMateriais = voltarTelaDetalhesMateriais;
	}

	public String getLocalPublicacao() {
		return localPublicacao;
	}

	public void setLocalPublicacao(String localPublicacao) {
		this.localPublicacao = localPublicacao;
	}

	public String getEditora() {
		return editora;
	}

	public void setEditora(String editora) {
		this.editora = editora;
	}

	public String getAno() {
		return ano;
	}

	public void setAno(String ano) {
		this.ano = ano;
	}


	public DataModel getPalavrasChaveDataModel() {
		return palavrasChaveDataModel;
	}



	public void setPalavrasChaveDataModel(DataModel palavrasChaveDataModel) {
		this.palavrasChaveDataModel = palavrasChaveDataModel;
	}

	public String getFinalizarAtualizacao() {
		return finalizarAtualizacao;
	}

	public void setFinalizarAtualizacao(String finalizarAtualizacao) {
		this.finalizarAtualizacao = finalizarAtualizacao;
	}

	public String getFinalizarCatalogacao() {
		return finalizarCatalogacao;
	}
	
	public void setFinalizarCatalogacao(String finalizarCatalogacao) {
		this.finalizarCatalogacao = finalizarCatalogacao;
	}

	public List<String> getAutoresSecundarios() {
		return autoresSecundarios;
	}
	
	public void setAutoresSecundarios( List<String> autoresSecundarios ) {
		this.autoresSecundarios = autoresSecundarios;
	}

	public DataModel getAutoresSecundariosDataModel() {
		return autoresSecundariosDataModel;
	}
	
	public void setAutoresSecundariosDataModel(
			DataModel autoresSecundariosDataModel ) {
		this.autoresSecundariosDataModel = autoresSecundariosDataModel;
	}
	
}
