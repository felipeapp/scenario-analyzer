/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 23/03/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 * <p>MBean que controla a parte de pesquisas da biblioteca realizadas por usu�rios que n�o s�o bibliotec�rios.</p>
 *
 * <p>Extende o MBean {@link PesquisaPublicaBibliotecaMBean} para reutiliza��o de c�digo, j� que as buscas realizadas aqui tamb�m s�o p�blicas</p>
 *  
 * <p>Pode ser chamado de diversas partes do sistema como:
 * 	
 * <ul>
 * 	    <li> Portal Docente -> Ambientes Virtuais -> Turmas Virtuais -> Turmas Virtuais Abertas -> Gerenciar Plano de Curso -> Pesquisar no Acervo </li>
 *      <li> Portal Docente -> Biblioteca -> Pesquisar Materiais no Acervo </li> 
 *      <li> Portal Docente -> Biblioteca -> Solicitar Reserva de material </li>   
 *      <li> Portal Discente -> Biblioteca -> Pesquisar Materiais no Acervo </li>  
 *      <li> Portal Discente -> Biblioteca -> Solicitar Reserva de material </li>
 *      <li> Biblioteca -> M�dulo Servidor -> Pesquisar Materiais no Acervo </li>
 * </ul>
 * 
 * </p>
 * 
 * <p><strong>Qualquer parte do sistema que quiser utilizar essa pesquisa p�blica no acervo deve implentar a interface {@link PesquisarAcervoBiblioteca}<strong></p>
 *
 * @author Fred_Castro
 */
@Component(value="pesquisaInternaBibliotecaMBean")
@Scope(value="request")
public class PesquisaInternaBibliotecaMBean extends PesquisaPublicaBibliotecaMBean{
	
	
	/**
	 * Define a p�gina de busca interna para o acervo de t�tulos.
	 */
	public static final String PAGINA_BUSCA_INTERNA_ACERVO = "/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp";
	
	
	/** Se "false", indica que o usu�rio est� apenas realizando o caso de uso de busca, se "true" indica 
	 * que essa busca est� sendo chamada de outro caso de uso.*/
	private boolean buscaSelecionarTitulo = false;
	
	
	/** O Mbean que chamou esse cado de uso */
	private PesquisarAcervoBiblioteca mBeanChamador;
	
	/** Guarda o valor da aba utilizada na busca interna **/
	private String valorAbaPesquisa = "";
	
	/**
	 *  <p>Utilizado caso o caso de uso que chama a pesquisa no acervo deseje realizar a��es 
	 *        extras que n�o est�o definidas na p�gina de busca no acervo. <p>
	 *  <p>O caso de uso que chamar pode passar a p�gina que vai ser inclu�da. Essa p�gina vai conter bot�es com a��es extras que o caso de uso chamador deseja 
	 *  utilizar.</p>
	 *  
	 *  <p><strong>Ver exemplo em PlanoCursoMBean.java<strong></p>      
	 *        
	 */
	private String paginaAcoesExtras;
	
	public PesquisaInternaBibliotecaMBean(){
		
	}
	
	
	/**
	 *  Inicia a busca apenas para vistualizar os resultados do acervo, n�o precisa retorna o fluxo para nenhum outro caso de uso do sistema
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/menus/modulo_biblioteca_servidor.jsp</li>
	 *    <li>/portais/docente/menu_docente.jsp</li>
	 *    <li>/portais/discente/menu_discente.jsp</li>
	 *   </ul>
	 *
	 * @return
	 */
	public String iniciarBusca(){
		
		buscaSelecionarTitulo = false;
		
		return telaBuscaPublicaAcervo();
	}
	
	
	


	/**
	 *   Inicia a pesquisa interna no acervo, quando esse pesquisa � chamada de outro caso de uso
	 *
	 *   M�todo n�o chamado por nenhuma p�gina jsp.
	 *
	 * @param operacao
	 * @param paginaAcoesExtras  utilizado caso o caso de uso que chama a pesquisa no acervo deseje realizar a��es 
	 *        extras que n�o est�o definidas na p�gina de busca no acervo.
	 * @return
	 */
	public String iniciarBusca(PesquisarAcervoBiblioteca mBeanChamador, String paginaAcoesExtras){
		
		buscaSelecionarTitulo = true;
		
		this.mBeanChamador = mBeanChamador;
		
		this.paginaAcoesExtras = paginaAcoesExtras;
		
		return telaBuscaPublicaAcervo();
	}
	
	
	/**
	 * Chama a limpeza dos dado da busca da classe pai configurando as abas da busca interna
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#limparResultadosBuscaAcervo()
	 */
	@Override
	public String limparResultadosBuscaAcervo() {
		configuraAbasPesquisa();
		return super.limparResultadosBuscaAcervo();
	}

	
	/**
	 *  Chama a busca da classe pai configurando as abas da busca interna
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#pesquisaAvancadaAcervo()
	 */
	@Override
	public String pesquisaAvancadaAcervo() throws DAOException {
		configuraAbasPesquisa();
		return super.pesquisaAvancadaAcervo();
	}

	/**
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#pesquisarAcervo()
	 */
	@Override
	public String pesquisaMultiCampoAcervo() throws DAOException {
		configuraAbasPesquisa();
		return super.pesquisaMultiCampoAcervo();
	}

	
	/**
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#pesquisaSimplesAcervo()
	 */
	@Override
	public String pesquisaSimplesAcervo() throws DAOException {
		configuraAbasPesquisa();
		return super.pesquisaSimplesAcervo();
	}
	
	/**
	 * 
	 * <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *   
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#pesquisaSimplesAcervoAutoridades()
	 */
	@Override
	public String pesquisaSimplesAcervoAutoridades() throws DAOException {
		configuraAbasPesquisa();
		return super.pesquisaSimplesAcervoAutoridades();
	}
	
	/**
	 *    <p>Retorna o fluxo do caso de uso para o mBean que chamou a busca no acervo </p>
	 *  
	 *    <p> O Mbean que deseja realizar essa opera��o tem que implementar {@link PesquisarAcervoBiblioteca} </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 *
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String selecionouTitulo() throws ArqException{
		
		int idTitulo  = getParameterInt("idTitulo");
		CacheEntidadesMarc titulo = new CacheEntidadesMarc();
		titulo.setIdTituloCatalografico(idTitulo);
		mBeanChamador.setTitulo(titulo);
		
		return mBeanChamador.selecionaTitulo();
	}


	/**
	 *  <p> Realiza a fun��o do bot�o voltar quando a busca � chamada por outro caso de uso do sistema. </p>
	 *  <p> <i> ( implementa��o desse m�todo deve ser realizada por Mbean que chamar esse caso de uso, pois s� ele sabe para onde deve voltar ) </i> </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 */
	public String voltarBusca() throws ArqException {
		configuraAbasPesquisa();
		return mBeanChamador.voltarBuscaAcervo();
	}
	
	/**
	 *  <p> Verifica se o bot�o voltar deve ser mostrado ou n�o para o usu�rio </p>
	 *  <p> <i> ( implementa��o desse m�todo deve ser realizada por Mbean que chamar esse caso de uso, pois s� ele sabe se deve voltar ) </i> </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/processos_tecnicos/pesquisas_acervo/pesquisa_interna/buscaInternaAcervo.jsp</li>
	 *   </ul>
	 */
	public boolean isMostrarBotaoVoltarBusca(){
		return mBeanChamador.isUtilizaVoltarBuscaAcervo();
	}
	
	
	
	/**
	 *    M�todo que configura o valor das abas da tela de pesquisa de t�tulos.
	 *
	 */
	private void configuraAbasPesquisa(){
		if ( StringUtils.notEmpty(valorAbaPesquisa))
			getCurrentSession().setAttribute("abaPesquisaInterna", valorAbaPesquisa);
	}
	
	
	
	/**
	 * Sobre escre a p�gina p�blica com a p�gina interna de busca no acervo.
	 * 
	 * M�todo n�o chamado por jsp.
	 * 
	 * @see br.ufrn.sigaa.biblioteca.processos_tecnicos.pesquisa.jsf.PesquisaPublicaBibliotecaMBean#telaBuscaPublicaAcervo()
	 */
	@Override
	public String telaBuscaPublicaAcervo() {
		return forward(PAGINA_BUSCA_INTERNA_ACERVO);
	}
	

	
	
	public boolean isBuscaSelecionarTitulo() {
		return buscaSelecionarTitulo;
	}

	public String getValorAbaPesquisa() {
		return valorAbaPesquisa;
	}


	public void setValorAbaPesquisa(String valorAbaPesquisa) {
		this.valorAbaPesquisa = valorAbaPesquisa;
	}


	public String getPaginaAcoesExtras() {
		return paginaAcoesExtras;
	}


	public void setPaginaAcoesExtras(String paginaAcoesExtras) {
		this.paginaAcoesExtras = paginaAcoesExtras;
	}
	
}
