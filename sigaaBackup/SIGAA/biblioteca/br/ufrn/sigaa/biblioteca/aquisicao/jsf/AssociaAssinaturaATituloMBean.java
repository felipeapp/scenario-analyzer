/*
 * Universidade Federal do Rio Grande no Norte
 * Superintend�ncia de Inform�tica
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
 *    <p>MBean que ger�ncia a caso de uso de associar uma assinatura criada a um T�tulo (Cataloga��o) e alterar essa associa��o.</p>
 *    
 *    <p><strong> Esse caso de uso � utilizado quando se quer transferir fasc�culos entre T�tulos. </strong></p>
 *    
 *    <p><i>Essa associa��o tamb�m � feita quando se inclui o primeiro fasc�culo da assinatura na parte de cataloga��o,
 *    caso a assinatura n�o perten�a a nenhum T�tulo ainda. Mas isso pode ser necess�rio
 * antes de se catalogar o primeiro fasc�culo, por exemplo, criar uma assinatura em outra biblioteca para transferir os 
 * fasc�culos para ela. Neste �ltimo caso o usu�rio vai utilizar esse caso de uso. </i></p>
 *
 * @author jadson
 * @since 23/10/2009
 * @version 1.0 criacao da classe
 * @version 1.5 31/01/2012 alterando para utilizar a interface de busca de assinaturas em vez de chamar diretamente a busca.
 */
@Component("associaAssinaturaATituloMBean") 
@Scope("request")
public class AssociaAssinaturaATituloMBean extends SigaaAbstractController<Assinatura> implements PesquisarAcervoBiblioteca, PesquisarAcervoAssinaturas{

	/** P�gina para confirmar a associa��o entre o t�tulo e assinatura selecionado */
	public static final String PAGINA_CONFIRMA_ASSOCIACAO_TITULO_ASSINATURA 
		= "/biblioteca/aquisicao/paginaConfirmaAssociacaoTituloAssinatura.jsp";
	
	/** P�gina para visualizar a associa��o entre o t�tulo e assinatura selecionado */
	public static final String PAGINA_VISUALIZA_ASSOCIACAO_TITULO_ASSINATURA 
		= "/biblioteca/aquisicao/paginaVisualizaAssociacaoTituloAssinatura.jsp";
	
	/** P�gina para alterar a associa��o entre o t�tulo e assinatura selecionado */
	public static final String PAGINA_CONFIRMA_ALTERACAO_ASSOCIACAO_TITULO_ASSINATURA 
	= "/biblioteca/aquisicao/paginaConfirmaAlteracaoAssociacaoTituloAssinatura.jsp";
	
	
	
	/** Guarda A assinatura selecionada para ser associada ao T�tulo escolhido pelo usu�rio */
	private Assinatura assinaturaAssociacaoTitulo;
	
	
	/** Guarda o O T�tulo que ser� associado � assinatura buscada (assinaturaAssociacaoTitulo) */
	private CacheEntidadesMarc tituloAssociacao;
	
	
	/**
	 *  O novo t�tulo que a assinatura vai possuir, usado no caso de uso de altera a associa��o 
	 * entre um t�tulo e a assinatura. 
	 * */
	private CacheEntidadesMarc novoTituloAssociacao; 
	
	
	/** Indica quando est� realizando a associa��o de um T�tulo com uma assinatura pela primeira vez ou alterando essa associa��o. 
	 * Porque esse Mbean vai controlar os dois fluxos */
	private boolean alterandoAssociacaoTituloAssinatura = false;
	
	
	/**
	 *    Inicia o caso de uso de associar uma assinatura a um T�tulo (Cataloga��o)
	 * <br><br>
	 * Chamado a partir da p�gina:<ul><li> /sigaa.war/biblioteca/menus/aquisicoes.jsp </li></ul>
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
	 *   Inicia o caso de uso em que o usu�rio pode verificar a qual T�tulo(Cataloga��o) 
	 * uma assinatura de peri�dico est� associada. A partir desse caso de uso, pode-se tamb�m 
	 * trocar esse associa��o.<br/><br/> 
	 *    <i>Um exemplo real, a assinatura da revista "c�es e gatos" foi associada ou T�tulo "onda jovem", 
	 * assim os fasc�culos dessa assinatura estavam sendo mostrados como se fossem fasc�culos do T�tulo 
	 * "onda jovem". Nesse caso de uso o bibliotec�rio pode verificar o erro e trocar o T�tulo da 
	 * assinatura para o T�tulo correto. </i>
	 *
	 * <br/> <br/>
	 * Chamado a partir da p�gina: 
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
	
	
	
	
	
	
	//////////////   M�todos da interface de busca de assinaturas  //////////////
	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#setAssinatura(br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura)
	 */
	@Override
	public void setAssinatura(Assinatura assinatura) throws ArqException {
		this.obj = assinatura;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
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
					addMensagemWarning("A assinatura selecionada n�o foi associada a nenhum T�tulo");
				else
					tituloAssociacao = BibliotecaUtil.obtemDadosTituloCache(tituloDaAssinatura.getId());
				
				return telaVisualizaAssociacaoTituloAssinatura();
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
	}

	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#voltarBuscaAssinatura()
	 */
	@Override
	public String voltarBuscaAssinatura() throws ArqException {
		return null;
	}


	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.aquisicao.jsf.PesquisarAcervoAssinaturas#isUtilizaVoltarBuscaAssinatura()
	 */
	
	@Override
	public boolean isUtilizaVoltarBuscaAssinatura() {
		return false;
	}
	
	
	/////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	/**
	 * O usu�rio j� escolheu a assinatura e o t�tulo, agora vai associar um ao outro.
	 * <br><br>
	 * Chamado a partir do bean: {@link AssinaturaPeriodicoMBean#selecionaTituloAssociacaoAssinatura()}
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
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
	 * �ltimo passo do caso de uso, onde o usu�rio realmente faz a associa��o entre um t�tulo de 
	 * peri�dico e uma assinatura que n�o possua t�tulo.
	 * <br><br>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/paginaConfirmaAssociacaoTituloAssinatura.jsp
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

		
		// �nica regra de neg�cio aqui no bean para n�o precisar criar um Processador s� para isso.
		if(! titulo.getFormatoMaterial().isFormatoPeriodico()){
			addMensagemErro("O T�tulo n�o pode ser associado com a Assinatura, pois n�o � um T�tulo de Peri�dico");
			return telaConfirmaAssociacaoEntreTituloAssinatura();
		}
		
		GenericDAO dao = null;
		try{
			 // tem que ter os dados da assinatura antes de salvar, n�o h� como alterar apenas o necess�rio porque n�o foi criado um processador s� para isso.
			dao = getGenericDAO();
			obj = dao.findByPrimaryKey(obj.getId(), Assinatura.class); 
		}finally{   if(dao != null) dao.close();   }
		
		obj.setTituloCatalografico(titulo);  /// Se o t�tulo na assinatura

		// E chama o comando de atualizar //
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(ArqListaComando.ALTERAR);
		
		
		try {
			execute(mov);
			
			addMensagemInformation("Associa��o realizada com sucesso.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return telaConfirmaAssociacaoEntreTituloAssinatura();
		}
		
		return cancelar();
		
	}
	
	
	
	/**
	 *  Inicia o caso de uso de alterar o T�tulo de uma assinatura, redirecionando o usu�rio para a 
	 * pesquisa de T�tulos para ele escolher o novo T�tulo da Assinatura. 
	 * <br><br>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/aquisicao/paginaVisualizaAssociacaoTituloAssinatura.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String  iniciaAlteracaoAssociacao()throws ArqException{
		
		PesquisaTituloCatalograficoMBean mBean = getMBean("pesquisaTituloCatalograficoMBean");
		
		return mBean.iniciarPesquisaSelecionarTitulo(this);
		
	}
	
	
	/**
	 * M�todo que realiza a opera��o de alterar a associa��o entre Assinatura e T�tulo
	 * <br><br>
	 * M�todo n�o chamado por nenhuma p�gina jsp.
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
			
			addMensagemInformation("Associa��o alterada com sucesso, os fasc�culos da assinatura foram transferidos para o novo T�tulo.");
			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());
			return telaConfirmaAlteracaoAssociacaoTituloAssinatura();
		}
		
		return cancelar();
		
	}
	
	
	
	
	//////////////////// telas de navega��o ////////////////////////////////
	
	/**
	 * Este m�todo redireciona para a p�gina descrita pelo nome dele.
	 * 
	 * <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaConfirmaAssociacaoEntreTituloAssinatura(){
		return forward(PAGINA_CONFIRMA_ASSOCIACAO_TITULO_ASSINATURA);
	}

	/**
	 *  Este m�todo redireciona para a p�gina descrita pelo nome dele.
	 *  
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 */
	public String telaVisualizaAssociacaoTituloAssinatura(){
		return forward(PAGINA_VISUALIZA_ASSOCIACAO_TITULO_ASSINATURA);
	}
	
	/**
	 *  Este m�todo redireciona para a p�gina descrita pelo nome dele.
	 *  
	 *  <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
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

	
	
	
	//////////////////M�todos da interface de busca no acervo  ///////////////////////

	
	/**
	 * Configura o t�tulo selecionado pelo usu�rio na busca de t�tulos do acervo utilizado por esse caso de uso.<br/>
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
	 * A��es realizadas quando o usu�rio selecionou o T�tulo na busca de t�tulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 * <p>M�todo n�o chamado por p�gina jsp.</p>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#selecionaTitulo()
	 */
	@Override
	public String selecionaTitulo() throws ArqException {
		
		if(alterandoAssociacaoTituloAssinatura){
			// O usu�rio selecionou o novo T�tulo da assinatura, agora dever confirmar a altera��o
			prepareMovimento(SigaaListaComando.ALTERA_ASSOCIACAO_ASSINATURA_TITULO);
			return telaConfirmaAlteracaoAssociacaoTituloAssinatura();
		}else{
			// O usu�rio j� selecionou a assinatura e o T�tulo, agora vai confirmar a associa��o
			return confirmaAssociacao(assinaturaAssociacaoTitulo, tituloAssociacao);
		}
	}



	/**
	 * Implementa o comportamento do bot�o "voltar" na busca de t�tulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 *    <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Indica que vai exitir um bot�o "voltar" na busca de t�tulos do acervo utilizado por esse caso de uso.<br/>
	 *
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisarAcervoBiblioteca#isUtilizaVoltarBuscaAcervo()
	 */
	@Override
	public boolean isUtilizaVoltarBuscaAcervo() {
		return true;
	}

	
	////////////Fim dos m�todos padr�o da interface de busca no acervo //////////////////
	
	
	
	
}
