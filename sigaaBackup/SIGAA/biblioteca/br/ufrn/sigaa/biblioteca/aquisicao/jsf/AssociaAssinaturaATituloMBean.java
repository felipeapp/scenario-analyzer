/*
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/10/2009
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AssinaturaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.aquisicao.negocio.MovimentoAlteraAssociacaoAssinaturaTitulo;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaTituloCatalograficoMBean;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.util.BibliotecaUtil;

/**
 *
 *    <p>MBean que gerência a caso de uso de associar uma assinatura criada a um Título (Catalogação) e alterar essa associação.</p>
 *    
 *    <p><strong> Esse caso de uso é utilizado quando se quer transferir fascículos entre Títulos. </strong></p>
 *    
 *    <p><i>Essa associação também é feita quando se inclui o primeiro fascículo da assinatura na parte de catalogação,
 *    caso a assinatura não pertença a nenhum Título ainda. Mas isso pode ser necessário
 * antes de se catalogar o primeiro fascículo, por exemplo, criar uma assinatura em outra biblioteca para transferir os 
 * fascículos para ela. Neste último caso o usuário vai utilizar esse caso de uso. </i></p>
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 * @version 1.5 31/01/2012 alterando para utilizar a interface de busca de assinaturas em vez de chamar diretamente a busca.
 */
@Component("associaAssinaturaATituloMBean") 
@Scope("request")
public class AssociaAssinaturaATituloMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoBiblioteca, PesquisarAcervoAssinaturas{

	/** Página para confirmar a associação entre o título e assinatura selecionado */
	public static final String PAGINA_CONFIRMA_ASSOCIACAO_TITULO_ASSINATURA 
		= "/biblioteca/aquisicao/paginaConfirmaAssociacaoTituloAssinatura.jsp";
	
	/** Página para visualizar a associação entre o título e assinatura selecionado */
	public static final String PAGINA_VISUALIZA_ASSOCIACAO_TITULO_ASSINATURA 
		= "/biblioteca/aquisicao/paginaVisualizaAssociacaoTituloAssinatura.jsp";
	
	/** Página para alterar a associação entre o título e assinatura selecionado */
	public static final String PAGINA_CONFIRMA_ALTERACAO_ASSOCIACAO_TITULO_ASSINATURA 
	= "/biblioteca/aquisicao/paginaConfirmaAlteracaoAssociacaoTituloAssinatura.jsp";
	
	
	
	/** Guarda A assinatura selecionada para ser associada ao Título escolhido pelo usuário */
	private Assinatura assinaturaAssociacaoTitulo;
	
	
	/** Guarda o O Título que será associado à assinatura buscada (assinaturaAssociacaoTitulo) */
	private CacheEntidadesMarc tituloAssociacao;
	
	
	/**
	 *  O novo título que a assinatura vai possuir, usado no caso de uso de altera a associação 
	 * entre um título e a assinatura. 
	 * */
	private CacheEntidadesMarc novoTituloAssociacao; 
	
	
	/** Indica quando está realizando a associação de um Título com uma assinatura pela primeira vez ou alterando essa associação. 
	 * Porque esse Mbean vai controlar os dois fluxos */
	private boolean alterandoAssociacaoTituloAssinatura = false;
	
	
	/**
	 *    Inicia o caso de uso de associar uma assinatura a um Título (Catalogação)
	 * <br><br>
	 * Chamado a partir da página:<ul><li> /sigaa.war/biblioteca/menus/aquisicoes.jsp </li></ul>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaAssociacaoEntreAssinaturaETitulo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		alterandoAssociacaoTituloAssinatura = false;
		
		AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
		return bean.iniciarPesquisaSelecionarAssinaturaSemTitulos(this);
		
	}
	
	
	/**
	 *   Inicia o caso de uso em que o usuário pode verificar a qual Título(Catalogação) 
	 * uma assinatura de periódico está associada. A partir desse caso de uso, pode-se também 
	 * trocar esse associação.<br/><br/> 
	 *    <i>Um exemplo real, a assinatura da revista "cães e gatos" foi associada ou Título "onda jovem", 
	 * assim os fascículos dessa assinatura estavam sendo mostrados como se fossem fascículos do Título 
	 * "onda jovem". Nesse caso de uso o bibliotecário pode verificar o erro e trocar o Título da 
	 * assinatura para o Título correto. </i>
	 *
	 * <br/> <br/>
	 * Chamado a partir da página: 
	 * <ul><li>/sigaa.war/biblioteca/menus/aquisicoes.jsp </ul></li>
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciaAlteracaoAssociacaoEntreAssinaturaETitulo()throws ArqException{
		
		alterandoAssociacaoTituloAssinatura = true;
		
		AssinaturaPeriodicoMBean mbean = getMBean("assinaturaPeriodicoMBean");
		return mbean.iniciarPesquisaSelecionarAssinatura(this);
	
	}
	
	
	
	
	
	
	//////////////   Métodos da interface de busca de assinaturas  //////////////
	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.obj = assinatura;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#selecionaAssinatura()
	 */
	@Override
	public String selecionaAssinatura() throws ArqException {
		
		
		AssinaturaDao dao = null;
		
		tituloAssociacao = null;
		novoTituloAssociacao = null;
		
		try{
			dao = getDAO(AssinaturaDao.class);
			
			if(! alterandoAssociacaoTituloAssinatura){
				
				checkRole(SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
				
				assinaturaAssociacaoTitulo = dao.findInformacoesPrincipaisAssinaturaById(obj.getId());
				
				PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
				
				return mBean.iniciarPesquisaSelecionarTitulo(this);
			}else{
				
				assinaturaAssociacaoTitulo = dao.refresh(new Assinatura(obj.getId()));
				
				if(assinaturaAssociacaoTitulo.getRegistroCriacao() != null)
					assinaturaAssociacaoTitulo.setNomeCriador(assinaturaAssociacaoTitulo.getRegistroCriacao().getUsuario().getNome());
				
				TituloCatalografico tituloDaAssinatura = assinaturaAssociacaoTitulo.getTituloCatalografico();
				
				if(tituloDaAssinatura == null)
					addMensagemWarning("A assinatura selecionada não foi associada a nenhum Título");
				else
					tituloAssociacao = BibliotecaUtil.obtemDadosTituloCache(tituloDaAssinatura.getId());
				
				return telaVisualizaAssociacaoTituloAssinatura();
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * O usuário já escolheu a assinatura e o título, agora vai associar um ao outro.
	 * <br><br>
	 * Chamado a partir do bean: {@link AssinaturaPeriodicoMBean#selecionaTituloAssociacaoAssinatura()}
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @param assinaturaAssociacaoTitulo
	 * @param tituloAssociacao
	 * @return
	 * @throws ArqException 
	 */
	public String confirmaAssociacao(Assinatura assinaturaAssociacaoTitulo, CacheEntidadesMarc tituloAssociacao) throws ArqException{
		
		obj = assinaturaAssociacaoTitulo;
		
		obj.setNomeCriador(   getDAO(AssinaturaDao.class).findNomeCriadorAssinatura(obj.getId()) );
		
		this.tituloAssociacao = tituloAssociacao;
		
		prepareMovimento(ArqListaComando.ALTERAR);
		
		return telaConfirmaAssociacaoEntreTituloAssinatura();
	}
	
	
	
	/**
	 * Último passo do caso de uso, onde o usuário realmente faz a associação entre um título de 
	 * periódico e uma assinatura que não possua título.
	 * <br><br>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/paginaConfirmaAssociacaoTituloAssinatura.jsp
	 *
	 * @return
	 * @throws ArqException 
	 * @throws  
	 */
	public String realizaAssociacaoEntreAssinaturaETitulo() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		TituloCatalografico titulo = getGenericDAO().findByPrimaryKey
				(tituloAssociacao.getIdTituloCatalografico(), TituloCatalografico.class);

		
		// Única regra de negócio aqui no bean para não precisar criar um Processador só para isso.
		if(! titulo.getFormatoMaterial().isFormatoPeriodico()){
			addMensagemErro("O Título não pode ser associado com a Assinatura, pois não é um Título de Periódico");
			return telaConfirmaAssociacaoEntreTituloAssinatura();
		}
		
		GenericDAO dao = null;
		try{
			 // tem que ter os dados da assinatura antes de salvar, não há como alterar apenas o necessário porque não foi criado um processador só para isso.
			dao = getGenericDAO();
			obj = dao.findByPrimaryKey(obj.getId(), Assinatura.class); 
		}finally{   if(dao != null) dao.close();   }
		
		obj.setTituloCatalografico(titulo);  /// Se o título na assinatura

		// E chama o comando de atualizar //
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		
		
		try {
			execute(mov);
			
			addMensagemInformation("Associação realizada com sucesso.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return telaConfirmaAssociacaoEntreTituloAssinatura();
		}
		
		return cancelar();
		
	}
	
	
	
	/**
	 *  Inicia o caso de uso de alterar o Título de uma assinatura, redirecionando o usuário para a 
	 * pesquisa de Títulos para ele escolher o novo Título da Assinatura. 
	 * <br><br>
	 * Chamado a partir da página: /sigaa.war/biblioteca/aquisicao/paginaVisualizaAssociacaoTituloAssinatura.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String  iniciaAlteracaoAssociacao()throws ArqException{
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		
		return mBean.iniciarPesquisaSelecionarTitulo(this);
		
	}
	
	
	/**
	 * Método que realiza a operação de alterar a associação entre Assinatura e Título
	 * <br><br>
	 * Método não chamado por nenhuma página jsp.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String  alteraAssociacaoEntreAssinaturaETitulo() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO
				, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		MovimentoAlteraAssociacaoAssinaturaTitulo mov 
			= new MovimentoAlteraAssociacaoAssinaturaTitulo(obj, tituloAssociacao, novoTituloAssociacao);
		mov.setCodMovimento(SigaaListaComando.ALTERA_ASSOCIACAO_ASSINATURA_TITULO);
		
		
		try {
			execute(mov);
			
			addMensagemInformation("Associação alterada com sucesso, os fascículos da assinatura foram transferidos para o novo Título.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return telaConfirmaAlteracaoAssociacaoTituloAssinatura();
		}
		
		return cancelar();
		
	}
	
	
	
	
	//////////////////// telas de navegação ////////////////////////////////
	
	/**
	 * Este método redireciona para a página descrita pelo nome dele.
	 * 
	 * <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaConfirmaAssociacaoEntreTituloAssinatura(){
		return forward(PAGINA_CONFIRMA_ASSOCIACAO_TITULO_ASSINATURA);
	}

	/**
	 *  Este método redireciona para a página descrita pelo nome dele.
	 *  
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaVisualizaAssociacaoTituloAssinatura(){
		return forward(PAGINA_VISUALIZA_ASSOCIACAO_TITULO_ASSINATURA);
	}
	
	/**
	 *  Este método redireciona para a página descrita pelo nome dele.
	 *  
	 *  <p>Método não chamado por nenhuma página jsp.</p>
	 */
	public String telaConfirmaAlteracaoAssociacaoTituloAssinatura(){
		return forward(PAGINA_CONFIRMA_ALTERACAO_ASSOCIACAO_TITULO_ASSINATURA);
	}
	
	
	////////////////////////////////////////////////////////////////////
	
	
	
	// sets e gets

	public CacheEntidadesMarc getTituloAssociacao() {
		return tituloAssociacao;
	}


	public void setTituloAssociacao(CacheEntidadesMarc tituloAssociacao) {
		this.tituloAssociacao = tituloAssociacao;
	}

	public CacheEntidadesMarc getNovoTituloAssociacao() {
		return novoTituloAssociacao;
	}

	public void setNovoTituloAssociacao(CacheEntidadesMarc novoTituloAssociacao) {
		this.novoTituloAssociacao = novoTituloAssociacao;
	}

	public Assinatura getAssinaturaAssociacaoTitulo() {
		return assinaturaAssociacaoTitulo;
	}

	public void setAssinaturaAssociacaoTitulo(Assinatura assinaturaAssociacaoTitulo) {
		this.assinaturaAssociacaoTitulo = assinaturaAssociacaoTitulo;
	}

	
	
	
	//////////////////Métodos da interface de busca no acervo  ///////////////////////

	
	/**
	 * Configura o título selecionado pelo usuário na busca de títulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#setTitulo(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc)
	 */
	@Override
	public void setTitulo(CacheEntidadesMarc cache) throws ArqException {
		
		if(alterandoAssociacaoTituloAssinatura){
			novoTituloAssociacao = BibliotecaUtil.obtemDadosTituloCache(cache.getIdTituloCatalografico());
		}else{
			tituloAssociacao = BibliotecaUtil.obtemDadosTituloCache(cache.getIdTituloCatalografico());
		}
	}



	/**
	 * Ações realizadas quando o usuário selecionou o Título na busca de títulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 * <p>Método não chamado por página jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		if(alterandoAssociacaoTituloAssinatura){
			// O usuário selecionou o novo Título da assinatura, agora dever confirmar a alteração
			prepareMovimento(SigaaListaComando.ALTERA_ASSOCIACAO_ASSINATURA_TITULO);
			return telaConfirmaAlteracaoAssociacaoTituloAssinatura();
		}else{
			// O usuário já selecionou a assinatura e o Título, agora vai confirmar a associação
			return confirmaAssociacao(assinaturaAssociacaoTitulo, tituloAssociacao);
		}
	}



	/**
	 * Implementa o comportamento do botão "voltar" na busca de títulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 *    <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaPorListasTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaAvancadaTituloCatalografico.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#voltarBuscaAcervo()
	 */
	@Override
	public String voltarBuscaAcervo() throws ArqException {
		
		if(alterandoAssociacaoTituloAssinatura){
			return telaVisualizaAssociacaoTituloAssinatura();
		}else{
			AssinaturaPeriodicoMBean bean = getMBean("assinaturaPeriodicoMBean");
			return bean.telaBuscaAssinaturas();
		}
	}



	/**
	 * Indica que vai exitir um botão "voltar" na busca de títulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}

	
	////////////Fim dos métodos padrão da interface de busca no acervo //////////////////
	
	
	
	
}
