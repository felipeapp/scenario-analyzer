/*
 * BuscaCatalogacoesIncompletasMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 *      <p>MBean que controla a parte de buscas de Títulos ou Autoridades com catalogações incompletas 
 * (ou não finalizadas). Essas catalogações incompletas ocorrem quando o usuário não digita todos os 
 * campos obrigatórios ou quando são importadas várias entidades MARC em um único arquivo.</p>
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
	
	
	
	///// guarda os título e autoridades incompletas buscadas //////
	private List<CacheEntidadesMarc> titulosIncompletos;
	private List<CacheEntidadesMarc> autoridadesIncompletas;
	////////////////////////////////////////////////////////////////
	
	/** 
	 * Guarda o título selecionado pelo usuário, porque primeiro tem que ir para a tela
	 * em que o usuário escolhe o formato do material, para depois iniciar o processo de catalogação.
	 */
	private TituloCatalografico tituloSelecionado;

	
	/**
	 *  Indica se o usuário chegou à página quando clicou no botão de importar, nesse caso é mostrada
	 * a informação de importar novas entidades (títulos ou autoriades), senão é mostrada apenas
	 * a opção de se trabalhar as catalogações incompletas.
	 */
	private boolean pesquisaImportacao = false;
	
	
	/** por padrão traz apenas as catalogações incompletas realizadas pelo usuário */
	private boolean apenasMinhasCatalogacoesIncompletas = true;
	
	
	
	/**
	 *    Redireciona para a página na qual o usuário vai escolher a forma da importação.
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
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
	 *    Redireciona para a página na qual o usuário vai escolher a forma da importação.
	 *    
	 *    Por esse método é possível escolher se deve aparecer na página a opção de importar novos
	 *    título ou não.
	 *    
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
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
	 *    Vai para a tela que mostra os títulos com catalogações não finalizadas e mantém a operação
	 *  que tinha antes. Claro que só funciona se a página manter os dados do bean com
	 *  <code> <a4j:keepAlive> /code>
	 *    <br/><br/>
	 *    Método não chamado per nenhuma jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaTitulosIncompletosMantendoOperacao() throws ArqException{
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		return telaBuscaCatalogacoesIncompletasTitulo();             
	}
	
	
	/**
	 *    Redireciona para a página na qual o usuário vai escolher a forma da importação.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaTituloCatalografico.jsp
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
	 *    Redireciona para a página na qual o usuário vai escolher a forma da importação.
	 * 
	 *     Por esse método é possível escolher se deve aparecer na página a opção de importar novas
	 *    autoridades ou não.
	 * 
	 *    <br/><br/>
	 *    Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/pesquisa_acervo/pesquisaTituloCatalografico.jsp
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
	 *    Vai para a tela que mostra as autoridades com catalogações não finalizadas e mantém a operação
	 *  que tinha antes. Claro que só funciona se a página manter os dados do bean com
	 *  <code> <a4j:keepAlive> /code>
	 *    
	 *    <br/><br/>
	 *    Método não chamado per nenhuma jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarBuscaAutoridadesIncompletosMantendoOperacao() throws ArqException{
		
		checkRole(SigaaPapeis.BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO, SigaaPapeis.BIBLIOTECA_ADMINISTRADOR_GERAL);
		
		return telaBuscaCatalogacoesIncompletasAutoridades();             
	}
	
	
	
	
	/**
	 *   <strong>Chamado quando o usuário selecionou um título não finalizado.</strong>
	 *   Nesse caso o usuário vai precisar primeiro escolher o formato do material desse título.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
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
		
		// vai indicar na página que escolhe o formato do material quais ações tomar.
		if(! pesquisaImportacao)
			getCurrentRequest().setAttribute("escolhendoFormatoDeTituloNaoFinalizado", true);
		else
			if(pesquisaImportacao)
				getCurrentRequest().setAttribute("escolhendoFormatoDeTituloNaoFinalizadoImportacao", true);
		
		/* vai terminar a catalogação do título
		 * se não tiver formato do material o bean de catalogação vai se respossabilizar por 
		 * redirecionar o usuário para a página que escolher o formato.
		 */
		if(tituloSelecionado.isImportado())
			return bean.iniciarImportacao(tituloSelecionado);
		else
			return bean.iniciarDuplicacao(tituloSelecionado);
		
	}
	
	
	
	
	/**
	 *   <strong>Chamado quando o usuário importou apenas 1 título em um arquivo e não existem catalogações incompletas.</strong>
	 *   Nesse caso o usuário vai logo escolher o formato do material desse título e depois começar a catalogação.
	 *
	 *   <br/><br/>
	 *   Método não invocado por JSP.
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
	 *   Chamado quando o usuário selecionou uma autoridade não finalizado.
	 *   Vai para a página para finalizar a catalogação da autoridade.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
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
	 *       Atualiza os título incompletos mostrados ao usuário de acordo se o usuário deseja ver todos
	 *  os títulos ou apenas os incluídos por ele.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
	 *
	 * @param evt
	 */
	public void atualizaTitulosIncompletos(ValueChangeEvent evt){
	
		apenasMinhasCatalogacoesIncompletas = (Boolean )evt.getNewValue();
		
		titulosIncompletos = null; // para buscar novamento
	}
	
	
	
	
	
	/**
	 * 
	 *          Atualiza as autoridades incompletas mostradas ao usuário de acordo se o usuário deseja ver todas
	 *  as autoridades ou apenas as incluídas por ele.
	 *
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @param evt
	 */
	public void atualizaAutoridadeIncompletas(ValueChangeEvent evt){
		
		apenasMinhasCatalogacoesIncompletas = (Boolean )evt.getNewValue();
		
		autoridadesIncompletas = null; // para buscar novamento
	}
	
	
	
	
	
	/**
	 *   Retorna a quantidade de título incompletos no sistema.
	 * 
	 * <br/><br/>
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaTitulosCatalogacaoIncompleta.jsp
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
	 * Chamado a partir da página: /sigaa.war/biblioteca/processos_tecnicos/catalogacao/paginaVisualizaAutoridadesCatalogacaoIncompleta.jsp
	 *
	 * @return
	 * @throws DAOException 
	 */
	public int getQtdAutoridadesIncompletas() throws DAOException{
		return getAllAutoridadesIncompletas().size();
	}
	
	
	
	////////////////////////////  telas de navegação /////////////////////////
	
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
