/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean para configurar exclusivamente os parâmetros das classificações bibliográficas do sistema.</p>
 *
 * @author felipe
 *
 */
@Component("classificacaoBibliograficaMBean")
@Scope("request")
public class ClassificacaoBibliograficaMBean  extends SigaaAbstractController<ClassificacaoBibliografica>{
	
	/** A página que lista as classificações existentes */
	public static final String PAGINA_LISTA = "/biblioteca/ClassificacaoBibliografica/lista.jsp";
	
	/** A página para informar os dados das novas classificações */
	public static final String PAGINA_FORM = "/biblioteca/ClassificacaoBibliografica/form.jsp";
	
	/** A quantidade máxima de classificações suportadas pelo sistema, como os dados dessas classificações 
	 * precisam se mantidas em cache para os relatórios,
	 * não dá para ter um número infinito aqui, porque isso implica em criar novos campos no cache. */
	public static final int QUANTIDADE_MAXIMA_CLASSIFICACOES_SUPORTADA = 3; 
	
	/** Contém os dados da novas classificações a ser criada */
	private ClassificacaoBibliografica novaClassificacao = new ClassificacaoBibliografica(-1);
	
	/** lista das classificações ativas no sistema. */
	private List<ClassificacaoBibliografica> classificacoesAtivas;

	/** Informação da classe principal informada pelo usuário na tela de cadastro */
	private String classePrincipal;
	
	/** A lista de classes principais informadas pelo usuário */
	private List<String> classesPrincipais = new ArrayList<String>();
	
	/** O modelo para interar a lsta de  classes principais da página de cadastro */
	private DataModel classesPrincipaisDataModel;

	/** O valor da ordem da classificações informada pelo usuário */
	private Integer ordem;
	
	/** O valor do campo MARC da classificações informada pelo usuário. Os campos são os campos 
	 * que podem ser utilizados para classificações "08x$y"  
	 * {@link http://www.loc.gov/marc/bibliographic/bd01x09x.html }
	 */
	private Integer campoMARC;
	
	public ClassificacaoBibliograficaMBean() {
		obj = new ClassificacaoBibliografica();
	}
	
	////// Métodos de Operações ///////
	
	/**
	 * Inicia o caso de uso redirecionado para listagem
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Inicia o caso de uso de cadastrar uma nova classificações, redirecionando o usuário para a tela de cadastro.<br/>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			addMensagemErro("O sistema suporta até "+QUANTIDADE_MAXIMA_CLASSIFICACOES_SUPORTADA+" classificações bibliográficas.");
			
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
	 * Salva os dados da nova classificações bibliográfica no banco.<br/>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			
			addMensagemInformation("Classificação bibliográfica cadastrada com sucesso.");
			
			return telaListar();			
		} catch (NegocioException ne) {
			ne.printStackTrace();
			addMensagens(ne.getListaMensagens());

			return null;
		}
	}

	/**
	 * 
	 * Remove a classificação bibliográfica do sistema.<br/>
	 *
	 * <br/>
	 * 
	 * <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
				
				addMensagem(MensagensArquitetura.REMOCAO_EFETUADA_COM_SUCESSO, "Classificação Bibliográfica");
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
	 * Redireciona para a tela de listagem das classificações
	 *  
	 *   <p>Método não chamado por nenhuma página jsp.</p>
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
	 * 		 Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/ClassificacaoBibliografica/form.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String voltar(){
		return forward(PAGINA_LISTA);
	}
	
	
	
	////// Métodos Auxiliares //////

	/** Retorna as classificações ativas do sistema para o usuário, tomando coidado para não buscar várias vezes no banco. */
	public Collection <ClassificacaoBibliografica> getClassificacoesAtivas () throws DAOException{
		if (classificacoesAtivas == null) {
			atualizarClassificacoesAtivas();
		}
		return classificacoesAtivas;
	}

	
	/** Atualiza as classificações ativas do sistema. */
	private void atualizarClassificacoesAtivas() throws DAOException {
		ClassificacaoBibliograficaDao dao = getDAO(ClassificacaoBibliograficaDao.class);
		try {
			classificacoesAtivas = dao.findAllAtivosSemProjecao();
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Retorna os valores das ordem das classificações no combo box para o usuário. Todos menos os já utilizados no sistema. 
	 * Já que cada classificações vai ter a sua ordem, a ordem é utilizada para saber qual a ordem em que a classificações 
	 * vão ser mostradas para o usuário e se as informações que o usuário digitou no campo correspone a 1ª, 2ª ou 3ª classificação configurada no sistema.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Retorna os campos MARC utilizados na classificações no combo box para o usuário. Todos menos os já utilizados no sistema. 
	 * Já que cada campo só pode conter 1 classificação.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
	 * AJAX - Adiciona uma nova pessoa do filtro de várias pessoas.
	 * <p>Este método é chamado pelas seguintes JSPs:
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
	 * AJAX - Remove uma pessoa do filtro de várias pessoas.
	 * <p>Este método é chamado pelas seguintes JSPs:
	 * <ul><li>/sigaa.war/biblioteca/controle_estatistico/paginaFiltrosPadraoRelatoriosBiblioteca.jsp</li></ul>
	 */
	public void removerClassePrincipal(ActionEvent evt) {
		classesPrincipais.remove(classesPrincipaisDataModel.getRowIndex());
	}
	
	/**
	 * Valida a classe principal recém-adicionada.
	 */
	private void validarAdicionarClassePrincipal() {
		if (StringUtils.isEmpty(classePrincipal)) {
			throw new IllegalArgumentException("Classe inválida.");
		} else if (classesPrincipais.contains(String.valueOf(classePrincipal))) {
			throw new IllegalArgumentException("Esta classe já se encontra na lista.");
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
	 * <p>Método que verifica se o sistema está utilizando a primeira classificação.</p>
	 *  
	 * <p>Utilizado quando se está querendo acesar essa informação de alguma página do sistema.</p> 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir o campo da classificação ou não</p>
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
	 *  <p>Método que verifica se o sistema está utilizando a segunda classificação.</p>
	 *  
	 *  <p>Utilizado quando se está querendo acesar essa informação de alguma página do sistema.</p> 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *     <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir o campo da classificação ou não</p>
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
	 * <p>Método que verifica se o sistema está utilizando a terceira classificação.</p>
	 *  
	 * <p>Utilizado quando se está querendo acesar essa informação de alguma página do sistema.</p>  
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir o campo da classificação ou não</p>
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
	 * <p>Método retorna a descrição a primeira classificação configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas páginas para o usuário a descricao da classificação consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir a descrição da da classificação</p>
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
	 * <p>Método retorna a descrição da segudna classificação configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas páginas para o usuário a descricao da classificação consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir a descrição da da classificação</p>
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
	 * <p>Método retorna a descrição da terceira classificação configurada no sistema, se existir.</p>
	 *  
	 * <p>Utilizado para mostrar mas páginas para o usuário a descricao da classificação consigurada no sistema, 
	 * em vez do nome fixo como era antes.</p> 
	 * 
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/catalogacao/painalLateralFormCatalogacao.jsp</li>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaMultiCampoTituloCatalografico.jsp</li>
	 *   </ul>
	 *
	 *   <p>Método chamado em todos as página do sistema para exitir a descrição da da classificação</p>
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
