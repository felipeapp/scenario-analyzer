/*
 * BuscaCatalogacoesIncompletasMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/08/2009
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.jsf;

import java.util.List;

import javax.faces.event.ValueChangeEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.biblioteca.AutoridadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.TituloCatalograficoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.biblioteca.util.CatalogacaoUtil;

/**
 *      <p>MBean que controla a parte de buscas de T�tulos ou Autoridades com cataloga��es incompletas 
 * (ou n�o finalizadas). Essas cataloga��es incompletas ocorrem quando o usu�rio n�o digita todos os 
 * campos obrigat�rios ou quando s�o importadas v�rias entidades MARC em um �nico arquivo.</p>
 *
 * @author jadson
 * @since 31/08/2009
 * @version 1.0 criacao da classe
 *
 */
@Component("buscaCatalogacoesIncompletasMBean")
@Scope("request")
public class BuscaCatalogacoesIncompletasMBean extends SigaaAbstractController<Object>{
	
	
	public static final String PAGINA_CATALOGACOES_INCOMPLETAS_TITULO = "/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp";
	public static final String PAGINA_CATALOGACOES_INCOMPLETAS_AUTORIDADES = "/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp";
	
	
	
	///// guarda os t�tulo e autoridades incompletas buscadas //////
	private List<CacheEntidadesMarc> titulosIncompletos;
	private List<CacheEntidadesMarc> autoridadesIncompletas;
	////////////////////////////////////////////////////////////////
	
	/** 
	 * Guarda o t�tulo selecionado pelo usu�rio, porque primeiro tem que ir para a tela
	 * em que o usu�rio escolhe o formato do material, para depois iniciar o processo de cataloga��o.
	 */
	private TituloCatalografico tituloSelecionado;

	
	/**
	 *  Indica se o usu�rio chegou � p�gina quando clicou no bot�o de importar, nesse caso � mostrada
	 * a informa��o de importar novas entidades (t�tulos ou autoriades), sen�o � mostrada apenas
	 * a op��o de se trabalhar as cataloga��es incompletas.
	 */
	private boolean pesquisaImportacao = false;
	
	
	/** por padr�o traz apenas as cataloga��es incompletas realizadas pelo usu�rio */
	private boolean apenasMinhasCatalogacoesIncompletas = true;
	
	
	
	/**
	 *    Redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o.
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaTitulosIncompletos() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		this.pesquisaImportacao = false;
		
		return telaBuscaCatalogacoesIncompletasTitulo();             
	}
	
	
	
	/**
	 *    Redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o.
	 *    
	 *    Por esse m�todo � poss�vel escolher se deve aparecer na p�gina a op��o de importar novos
	 *    t�tulo ou n�o.
	 *    
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaTitulosIncompletosImportacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		this.pesquisaImportacao = true;
		
		return telaBuscaCatalogacoesIncompletasTitulo();             
	}
	
	
	/**
	 *    Vai para a tela que mostra os t�tulos com cataloga��es n�o finalizadas e mant�m a opera��o
	 *  que tinha antes. Claro que s� funciona se a p�gina manter os dados do bean com
	 *  <code> <a4j:keepAlive> /code>
	 *    <br/><br/>
	 *    M�todo n�o chamado per nenhuma jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaTitulosIncompletosMantendoOperacao() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		return telaBuscaCatalogacoesIncompletasTitulo();             
	}
	
	
	/**
	 *    Redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaAutoridadesIncompletas() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
	
		this.pesquisaImportacao = false;
		
		return telaBuscaCatalogacoesIncompletasAutoridades();            
	}
	
	
	
	/**
	 *    Redireciona para a p�gina na qual o usu�rio vai escolher a forma da importa��o.
	 * 
	 *     Por esse m�todo � poss�vel escolher se deve aparecer na p�gina a op��o de importar novas
	 *    autoridades ou n�o.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaTituloCatalografico.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaAutoridadesIncompletasImportacao() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		this.pesquisaImportacao = true;	
		
		return telaBuscaCatalogacoesIncompletasAutoridades();            
	}
	
	
	/**
	 *    Vai para a tela que mostra as autoridades com cataloga��es n�o finalizadas e mant�m a opera��o
	 *  que tinha antes. Claro que s� funciona se a p�gina manter os dados do bean com
	 *  <code> <a4j:keepAlive> /code>
	 *    
	 *    <br/><br/>
	 *    M�todo n�o chamado per nenhuma jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaAutoridadesIncompletosMantendoOperacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		return telaBuscaCatalogacoesIncompletasAutoridades();             
	}
	
	
	
	
	/**
	 *   <strong>Chamado quando o usu�rio selecionou um t�tulo n�o finalizado.</strong>
	 *   Nesse caso o usu�rio vai precisar primeiro escolher o formato do material desse t�tulo.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCatalogacaoTitulos() throws ArqException{
		
		tituloSelecionado = new TituloCatalografico(getParameterInt("idTituloNaoFinalizado"));
		
		tituloSelecionado = getDAO(TituloCatalograficoDao.class)
					.findTituloByIdInicializandoDados(tituloSelecionado.getId());
		
		CatalogacaoMBean bean =  getMBean("catalogacaoMBean");
		
		if(titulosIncompletos.size() > 1)
			bean.setPossuiEntiadesNaoFinalizados(true);
		
		// vai indicar na p�gina que escolhe o formato do material quais a��es tomar.
		if(! pesquisaImportacao)
			getCurrentRequest().setAttribute("escolhendoFormatoDeTituloNaoFinalizado", true);
		else
			if(pesquisaImportacao)
				getCurrentRequest().setAttribute("escolhendoFormatoDeTituloNaoFinalizadoImportacao", true);
		
		/* vai terminar a cataloga��o do t�tulo
		 * se n�o tiver formato do material o bean de cataloga��o vai se respossabilizar por 
		 * redirecionar o usu�rio para a p�gina que escolher o formato.
		 */
		if(tituloSelecionado.isImportado())
			return bean.iniciarImportacao(tituloSelecionado);
		else
			return bean.iniciarDuplicacao(tituloSelecionado);
		
	}
	
	
	
	
	/**
	 *   <strong>Chamado quando o usu�rio importou apenas 1 t�tulo em um arquivo e n�o existem cataloga��es incompletas.</strong>
	 *   Nesse caso o usu�rio vai logo escolher o formato do material desse t�tulo e depois come�ar a cataloga��o.
	 *
	 *   <br/><br/>
	 *   M�todo n�o invocado por JSP.
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCatalogacaoComApenasUmTituloImportado() throws ArqException{
		
		CatalogacaoMBean bean =  getMBean("catalogacaoMBean");
		bean.setPossuiEntiadesNaoFinalizados(false);
		bean.setCatalogacaoImportacao(true);
		bean.setObj(tituloSelecionado);
		
		getCurrentRequest().setAttribute("escolhendoFormatoDeTituloNaoFinalizadoImportacao", true);
		
		return bean.telaEscolheFormatoMaterial();
		
	}
	
	
	
	/**
	 *   Chamado quando o usu�rio selecionou uma autoridade n�o finalizado.
	 *   Vai para a p�gina para finalizar a cataloga��o da autoridade.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarCatalogacaoAutoridades() throws ArqException{
		
		Autoridade autoridade = getDAO(AutoridadeDao.class)
			.findByPrimaryKey(getParameterInt("idAutoridadeNaoFinalizada"), Autoridade.class); 
			
		
		CatalogacaoMBean bean =  getMBean("catalogacaoMBean");
		
		if(autoridadesIncompletas.size() > 1)
			bean.setPossuiEntiadesNaoFinalizados(true);
		
		if(autoridade.isImportada())
			return bean.iniciarAutoridadesImportacao(CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade));
		else
			return bean.iniciarAutoridadesDuplicacao(CatalogacaoUtil.criaTituloAPartirAutoridade(autoridade));
		
	}
	
	
	
	/**
	 *       Atualiza os t�tulo incompletos mostrados ao usu�rio de acordo se o usu�rio deseja ver todos
	 *  os t�tulos ou apenas os inclu�dos por ele.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @param evt
	 */
	public void atualizaTitulosIncompletos(ValueChangeEvent evt){
	
		apenasMinhasCatalogacoesIncompletas = (Boolean )evt.getNewValue();
		
		titulosIncompletos = null; // para buscar novamento
	}
	
	
	
	
	
	/**
	 * 
	 *          Atualiza as autoridades incompletas mostradas ao usu�rio de acordo se o usu�rio deseja ver todas
	 *  as autoridades ou apenas as inclu�das por ele.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @param evt
	 */
	public void atualizaAutoridadeIncompletas(ValueChangeEvent evt){
		
		apenasMinhasCatalogacoesIncompletas = (Boolean )evt.getNewValue();
		
		autoridadesIncompletas = null; // para buscar novamento
	}
	
	
	
	
	
	/**
	 *   Retorna a quantidade de t�tulo incompletos no sistema.
	 * 
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws DAOException 
	 */
	public int getQtdTitulosIncompletos() throws DAOException{
		return getAllTitulosIncompletos().size();
	}
	
	/**
	 *   Retorna a quantidade de autoridades incompletas no sistema.
	 *
	 * <br/><br/>
	 * Chamado a partir da p�gina: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws DAOException 
	 */
	public int getQtdAutoridadesIncompletas() throws DAOException{
		return getAllAutoridadesIncompletas().size();
	}
	
	
	
	////////////////////////////  telas de navega��o /////////////////////////
	
	public String telaBuscaCatalogacoesIncompletasTitulo(){
		return forward(PAGINA_CATALOGACOES_INCOMPLETAS_TITULO);
	}
	
	public String telaBuscaCatalogacoesIncompletasAutoridades(){
		return forward(PAGINA_CATALOGACOES_INCOMPLETAS_AUTORIDADES);
	}
	
	
	//////////////////////////////////////////////////////////////////////////////
	
	
	
	
	// sets e gets
	
	
	public List<CacheEntidadesMarc> getAllTitulosIncompletos() throws DAOException {
		if(titulosIncompletos == null){
			
			if(apenasMinhasCatalogacoesIncompletas)
				titulosIncompletos = getDAO(TituloCatalograficoDao.class)
					.findAllTitulosComCatalogacaoIncompletaDoUsuario(getUsuarioLogado().getId());		
			else
				titulosIncompletos = getDAO(TituloCatalograficoDao.class).findAllTitulosComCatalogacaoIncompleta();		
		}
		return titulosIncompletos;
	}
	
	
	
	
	
	public List<CacheEntidadesMarc> getAllAutoridadesIncompletas() throws DAOException {
		if(autoridadesIncompletas == null){
			if(apenasMinhasCatalogacoesIncompletas)
				autoridadesIncompletas = getDAO(AutoridadeDao.class)
					.findAllAutoridadesComCatalogacaoIncompletaDoUsuario(getUsuarioLogado().getId());	
			else
				autoridadesIncompletas = getDAO(AutoridadeDao.class).findAllAutoridadesComCatalogacaoIncompleta();
		}
		return autoridadesIncompletas;
	}

	
	
	
	
	public void setTitulosIncompletos(List<CacheEntidadesMarc> titulosIncompletos) {
		this.titulosIncompletos = titulosIncompletos;
	}
	
	public void setAutoridadesIncompletas(List<CacheEntidadesMarc> autoridadesIncompletas) {
		this.autoridadesIncompletas = autoridadesIncompletas;
	}

	public TituloCatalografico getTituloSelecionado() {
		return tituloSelecionado;
	}

	public void setTituloSelecionado(TituloCatalografico tituloSelecionado) {
		this.tituloSelecionado = tituloSelecionado;
	}


	public boolean isPesquisaImportacao() {
		return pesquisaImportacao;
	}


	public void setPesquisaImportacao(boolean pesquisaImportacao) {
		this.pesquisaImportacao = pesquisaImportacao;
	}

	public boolean isApenasMinhasCatalogacoesIncompletas() {
		return apenasMinhasCatalogacoesIncompletas;
	}

	public void setApenasMinhasCatalogacoesIncompletas(boolean apenasMinhasCatalogacoesIncompletas) {
		this.apenasMinhasCatalogacoesIncompletas = apenasMinhasCatalogacoesIncompletas;
	}
	
	
	
}
