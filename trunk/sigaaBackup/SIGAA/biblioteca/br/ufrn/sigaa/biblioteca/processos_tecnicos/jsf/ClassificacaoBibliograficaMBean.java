/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 10/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.event.ActionEvent;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dao.ClassificacaoBibliograficaDao;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CamposMarcClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio.MovimentoCadastraClassificacaoBibliografica;
import br.ufrn.sigaa.biblioteca.util.ClassificacoesBibliograficasUtil;


/**
 * <p>MBean para configurar exclusivamente os par�metros das classifica��es bibliogr�ficas do sistema.</p>
 *
 * @author felipe
 *
 */
@Component("classificacaoBibliograficaMBean")
@Scope("request")
public class ClassificacaoBibliograficaMBean  extends SigaaAbstractController<ClassificacaoBibliografica>{
	
	/** A p�gina que lista as classifica��es existentes */
	public static final String PAGINA_LISTA = "/biblioteca/ClassificacaoBibliografica/lista.jsp";
	
	/** A p�gina para informar os dados das novas classifica��es */
	public static final String PAGINA_FORM = "/biblioteca/ClassificacaoBibliografica/form.jsp";
	
	/** A quantidade m�xima de classifica��es suportadas pelo sistema, como os dados dessas classifica��es 
	 * precisam se mantidas em cache para os relat�rios,
	 * n�o d� para ter um n�mero infinito aqui, porque isso implica em criar novos campos no cache. */
	public static final int QUANTIDADE_MAXIMA_CLASSIFICACOES_SUPORTADA = 3; 
	
	/** Cont�m os dados da novas classifica��es a ser criada */
	private ClassificacaoBibliografica novaClassificacao = new ClassificacaoBibliografica(-1);
	
	/** lista das classifica��es ativas no sistema. */
	private List<ClassificacaoBibliografica> classificacoesAtivas;

	/** Informa��o da classe principal informada pelo usu�rio na tela de cadastro */
	private String classePrincipal;
	
	/** A lista de classes principais informadas pelo usu�rio */
	private List<String> classesPrincipais = new ArrayList<String>();
	
	/** O modelo para interar a lsta de  classes principais da p�gina de cadastro */
	private DataModel classesPrincipaisDataModel;

	/** O valor da ordem da classifica��es informada pelo usu�rio */
	private Integer ordem;
	
	/** O valor do campo MARC da classifica��es informada pelo usu�rio. Os campos s�o os campos 
	 * que podem ser utilizados para classifica��es "08x$y"  
	 * {@link http://www.loc.gov/marc/bibliographic/bd01x09x.html }
	 */
	private Integer campoMARC;
	
	public ClassificacaoBibliograficaMBean() {
		obj = new ClassificacaoBibliografica();
	}
	
	////// M�todos de Opera��es ///////
	
	/**
	 * Inicia o caso de uso redirecionado para listagem
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/cadastros.jsp</li>
	 *   </ul>
	 * 
	 */
	public String iniciar() {
		return telaListar();
	}
	
	
	
	/**
	 * 
	 * Inicia o caso de uso de cadastrar uma nova classifica��es, redirecionando o usu�rio para a tela de cadastro.<br/>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/lista.jsp</li>
	 *   </ul>
	 *
	 *  
	 * 
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	public String preCadastrar() throws ArqException {
		if (classificacoesAtivas.size() >= QUANTIDADE_MAXIMA_CLASSIFICACOES_SUPORTADA) {
			addMensagemErro("O sistema suporta at� "+QUANTIDADE_MAXIMA_CLASSIFICACOES_SUPORTADA+" classifica��es bibliogr�ficas.");
			
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CADASTRA_CLASSIFICACAO_BIBLIOGRAFICA);
		
		obj = new ClassificacaoBibliografica();
		
		ordem = null;
		campoMARC = null;
		classesPrincipais.clear();
		classesPrincipaisDataModel = new ListDataModel(classesPrincipais);
		
		return forward(PAGINA_FORM);
	}
	
	
	/**
	 * 
	 * Salva os dados da nova classifica��es bibliogr�fica no banco.<br/>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/form.jsp</li>
	 *   </ul>
	 *
	 *  
	 * 
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	public String cadastrar() throws ArqException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		try {
			montarObj();
			
			MovimentoCadastraClassificacaoBibliografica mov = new MovimentoCadastraClassificacaoBibliografica(obj);
			
			mov.setCodMovimento(SigaaListaComando.CADASTRA_CLASSIFICACAO_BIBLIOGRAFICA);
			
			execute(mov);
			
			atualizarClassificacoesAtivas();
			
			addMensagemInformation("Classifica��o bibliogr�fica cadastrada com sucesso.");
			
			return telaListar();			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());

			return null;
		}
	}

	/**
	 * 
	 * Remove a classifica��o bibliogr�fica do sistema.<br/>
	 *
	 * <br/>
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/lista.jsp</li>
	 *   </ul>
	 *
	 *
	 *
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	public String remover() throws SegurancaException, DAOException {
		checkRole(SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		ClassificacaoBibliografica c = classificacoesAtivas.get(classificacoesAtivas.indexOf(new ClassificacaoBibliografica(getParameterInt("id", 0))));
		
		GenericDAO dao = null;
		
		if (c == null || c.getId() == 0 || ! c.isAtiva())
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		else {
			try {
				dao = getGenericDAO();
				
				c.setAtiva(false);
				
				dao.update(c);
				
				atualizarClassificacoesAtivas();
				
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Classifica��o Bibliogr�fica");
			} finally {
				if (dao != null) dao.close();
			}
		
		}
		
		return null;
	}
	
	
	/** Inicia o objeto a ser cadastrado. */
	private void montarObj() {
		obj.setAtiva(true);
		obj.setOrdem(ClassificacaoBibliografica.OrdemClassificacao.getOrdem(ordem));
		obj.setCampoMARC(CamposMarcClassificacaoBibliografica.getCampoMarcClassificaoByValor(campoMARC));
		obj.setClassesPrincipaisClassificacaoBibliografica(classesPrincipais);
	}
	
	/**
	 * Redireciona para a tela de listagem das classifica��es
	 *  
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @return
	 */
	private String telaListar() {
		return forward(PAGINA_LISTA);
	}
	
	/**
	 *  Volta da tela de cadatro de uma nov
	 *  
	 *  <br/>
	 * 		 M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltar(){
		return forward(PAGINA_LISTA);
	}
	
	
	
	////// M�todos Auxiliares //////

	/** Retorna as classifica��es ativas do sistema para o usu�rio, tomando coidado para n�o buscar v�rias vezes no banco. */
	public Collection <ClassificacaoBibliografica> getClassificacoesAtivas () throws DAOException{
		if (classificacoesAtivas == null) {
			atualizarClassificacoesAtivas();
		}
		return classificacoesAtivas;
	}

	
	/** Atualiza as classifica��es ativas do sistema. */
	private void atualizarClassificacoesAtivas() throws DAOException {
		ClassificacaoBibliograficaDao dao = getDAO(ClassificacaoBibliograficaDao.class);
		try {
			classificacoesAtivas = dao.findAllAtivosSemProjecao();
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna os valores das ordem das classifica��es no combo box para o usu�rio. Todos menos os j� utilizados no sistema. 
	 * J� que cada classifica��es vai ter a sua ordem, a ordem � utilizada para saber qual a ordem em que a classifica��es 
	 * v�o ser mostradas para o usu�rio e se as informa��es que o usu�rio digitou no campo correspone a 1�, 2� ou 3� classifica��o configurada no sistema.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/form.jsp</li>
	 *  
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getOrdemCombo () throws DAOException {
		List<ClassificacaoBibliografica.OrdemClassificacao> ordemCombo = new ArrayList<ClassificacaoBibliografica.OrdemClassificacao>();
		
		for (ClassificacaoBibliografica.OrdemClassificacao o : ClassificacaoBibliografica.OrdemClassificacao.values()) {
			ordemCombo.add(o);
		}
		
		for (ClassificacaoBibliografica c : classificacoesAtivas) {
			if (ordemCombo.contains(c.getOrdem())) {
				ordemCombo.remove(c.getOrdem());
			}
		}
		
		return toSelectItems(ordemCombo, "valor", "descricao");
	}
	
	/**
	 * Retorna os campos MARC utilizados na classifica��es no combo box para o usu�rio. Todos menos os j� utilizados no sistema. 
	 * J� que cada campo s� pode conter 1 classifica��o.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *     <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/form.jsp</li>
	 *  
	 *   </ul>
	 *
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCampoMARCCombo () throws DAOException {
		List<CamposMarcClassificacaoBibliografica> campoMARCCombo = new ArrayList<CamposMarcClassificacaoBibliografica>();
		
		for (CamposMarcClassificacaoBibliografica m : CamposMarcClassificacaoBibliografica.values()) {
			campoMARCCombo.add(m);
		}
		
		for (ClassificacaoBibliografica c : classificacoesAtivas) {
			if (campoMARCCombo.contains(c.getCampoMARC())) {
				campoMARCCombo.remove(c.getCampoMARC());
			}
		}
		
		return toSelectItems(campoMARCCombo, "valor", "descricao");
	}

	
	/**
	 * AJAX - Adiciona uma nova pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 * </p>
	 */
	public void adicionarClassePrincipal(ActionEvent evt) {
		try {
			validarAdicionarClassePrincipal();		
			
			classesPrincipais.add(classePrincipal);
		} catch (IllegalArgumentException iaex) {
			addMensagemErro(iaex.getMessage());
		}
	}

	
	/**
	 * AJAX - Remove uma pessoa do filtro de v�rias pessoas.
	 * <p>Este m�todo � chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerClassePrincipal(ActionEvent evt) {
		classesPrincipais.remove(classesPrincipaisDataModel.getRowIndex());
	}
	
	/**
	 * Valida a classe principal rec�m-adicionada.
	 */
	private void validarAdicionarClassePrincipal() {
		if (StringUtils.isEmpty(classePrincipal)) {
			throw new IllegalArgumentException("Classe inv�lida.");
		} else if (classesPrincipais.contains(String.valueOf(classePrincipal))) {
			throw new IllegalArgumentException("Esta classe j� se encontra na lista.");
		}
	}
	
	///// gets e sets /////

	public ClassificacaoBibliografica getNovaClassificacao() {
		return novaClassificacao;
	}

	public void setNovaClassificacao(ClassificacaoBibliografica novaClassificacao) {
		this.novaClassificacao = novaClassificacao;
	}
	
	public String getClassePrincipal() {
		return classePrincipal;
	}

	public void setClassePrincipal(String classePrincipal) {
		this.classePrincipal = classePrincipal;
	}

	public List<String> getClassesPrincipais() {
		return classesPrincipais;
	}

	public void setClassesPrincipais(List<String> classesPrincipais) {
		this.classesPrincipais = classesPrincipais;
	}

	public DataModel getClassesPrincipaisDataModel() {
		return classesPrincipaisDataModel;
	}

	public void setClassesPrincipaisDataModel(DataModel classesPrincipaisDataModel) {
		this.classesPrincipaisDataModel = classesPrincipaisDataModel;
	}

	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public Integer getCampoMARC() {
		return campoMARC;
	}

	public void setCampoMARC(Integer campoMARC) {
		this.campoMARC = campoMARC;
	}

	
	
	
	
	
	
	/**
	 * <p>M�todo que verifica se o sistema est� utilizando a primeira classifica��o.</p>
	 *  
	 * <p>Utilizado quando se est� querendo acesar essa informa��o de alguma p�gina do sistema.</p> 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir o campo da classifica��o ou n�o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isSistemaUtilizandoClassificacao1() throws DAOException{
		Boolean isSistemaUtilizandoClassificacao1 = (Boolean) getCurrentSession().getAttribute("_isSistemaUtilizandoClassificacao1");
		
		if(isSistemaUtilizandoClassificacao1 == null){
			isSistemaUtilizandoClassificacao1 = ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao1();
			getCurrentSession().setAttribute("_isSistemaUtilizandoClassificacao1", isSistemaUtilizandoClassificacao1);
		}
		
		return isSistemaUtilizandoClassificacao1;
	}
	
	
	/**
	 *  <p>M�todo que verifica se o sistema est� utilizando a segunda classifica��o.</p>
	 *  
	 *  <p>Utilizado quando se est� querendo acesar essa informa��o de alguma p�gina do sistema.</p> 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir o campo da classifica��o ou n�o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isSistemaUtilizandoClassificacao2() throws DAOException{
		Boolean isSistemaUtilizandoClassificacao2 = (Boolean) getCurrentSession().getAttribute("_isSistemaUtilizandoClassificacao2");
		
		if(isSistemaUtilizandoClassificacao2 == null){
			isSistemaUtilizandoClassificacao2 = ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao2();
			getCurrentSession().setAttribute("_isSistemaUtilizandoClassificacao2", isSistemaUtilizandoClassificacao2);
		}
		
		return isSistemaUtilizandoClassificacao2;
	}
	
	
	/**
	 * <p>M�todo que verifica se o sistema est� utilizando a terceira classifica��o.</p>
	 *  
	 * <p>Utilizado quando se est� querendo acesar essa informa��o de alguma p�gina do sistema.</p>  
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir o campo da classifica��o ou n�o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public boolean isSistemaUtilizandoClassificacao3() throws DAOException{
		Boolean isSistemaUtilizandoClassificacao3 = (Boolean) getCurrentSession().getAttribute("_isSistemaUtilizandoClassificacao3");
		
		if(isSistemaUtilizandoClassificacao3 == null){
			isSistemaUtilizandoClassificacao3 = ClassificacoesBibliograficasUtil.isSistemaUtilizandoClassificacao3();
			getCurrentSession().setAttribute("_isSistemaUtilizandoClassificacao3", isSistemaUtilizandoClassificacao3);
		}
		
		return isSistemaUtilizandoClassificacao3;
	}
	
	
	
	
	/**
	 * <p>M�todo retorna a descri��o a primeira classifica��o configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas p�ginas para o usu�rio a descricao da classifica��o consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir a descri��o da da classifica��o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getDescricaoClassificacao1() throws DAOException{
		String descricaoClassificacao1 = (String) getCurrentSession().getAttribute("_descricaoClassificacao1");
		
		if( StringUtils.isEmpty(descricaoClassificacao1 ) ){
			descricaoClassificacao1 = ClassificacoesBibliograficasUtil.getDescricaoClassificacao1();
			getCurrentSession().setAttribute("_descricaoClassificacao1", descricaoClassificacao1);
		}
		
		return descricaoClassificacao1;
	}
	
	
	/**
	 * <p>M�todo retorna a descri��o da segudna classifica��o configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas p�ginas para o usu�rio a descricao da classifica��o consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir a descri��o da da classifica��o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getDescricaoClassificacao2() throws DAOException{
		String descricaoClassificacao2 = (String) getCurrentSession().getAttribute("_descricaoClassificacao2");
		
		if( StringUtils.isEmpty(descricaoClassificacao2 ) ){
			descricaoClassificacao2 = ClassificacoesBibliograficasUtil.getDescricaoClassificacao2();
			getCurrentSession().setAttribute("_descricaoClassificacao2", descricaoClassificacao2);
		}
		
		return descricaoClassificacao2;
	}
	
	
	/**
	 * <p>M�todo retorna a descri��o da terceira classifica��o configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas p�ginas para o usu�rio a descricao da classifica��o consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>M�todo chamado em todos as p�gina do sistema para exitir a descri��o da da classifica��o</p>
	 *
	 * @return
	 * @throws DAOException 
	 */
	public String getDescricaoClassificacao3() throws DAOException{
		String descricaoClassificacao3 = (String) getCurrentSession().getAttribute("_descricaoClassificacao3");
		
		if( StringUtils.isEmpty(descricaoClassificacao3 ) ){
			descricaoClassificacao3 = ClassificacoesBibliograficasUtil.getDescricaoClassificacao3();
			getCurrentSession().setAttribute("_descricaoClassificacao3", descricaoClassificacao3);
		}
		
		return descricaoClassificacao3;
	}
	
}
