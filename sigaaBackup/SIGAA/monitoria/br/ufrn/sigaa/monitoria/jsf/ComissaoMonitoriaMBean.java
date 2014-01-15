/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/09/2009
 *
 */
package br.ufrn.sigaa.monitoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.monitoria.DiscenteMonitoriaDao;
import br.ufrn.sigaa.arq.dao.monitoria.ProjetoMonitoriaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.monitoria.dominio.HistoricoSituacaoDiscenteMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;

/**
 * Portal para acesso da comissão de monitoria.
 *
 * @author Gleydson
 *
 */
@Component("comissaoMonitoria") 
@Scope("session")
public class ComissaoMonitoriaMBean extends SigaaAbstractController<Object> {

	/** utilizado na tela de busca dos discentes */
	private Collection<ProjetoEnsino> projetos;
	/** Atributo utilizado para armazenar o histórico de Discentes */
	private Collection<HistoricoSituacaoDiscenteMonitoria> historicosDiscentes; 
	/** Atributo utilizado para armazenar uma lista de objetos */
	private Collection<Object> lista;
	
	/**
	 * usado em consultas
	 */
	private Unidade unidade = new Unidade();

	/** valida se dados obtidos serão exibidos em um relatório */ 
	private boolean checkGerarRelatorio;
	
	/** valida campo para busca pro centro */
	private boolean checkBuscaCentro;

	/** valida campo para busca por curso */
	private boolean checkBuscaCurso;	
	
	/** valida campo para busca por ano */
	private boolean checkBuscaAno;
	
	/** valida campo para busca por coordenador  */
	private boolean checkBuscaCoordenador;
	
	/** valida campo para seleção do tipo do Monitor */
	private boolean checkBuscaTipoMonitor;

	/** valida campo para seleção da situação do Discente Monitoria */
	private boolean checkBuscaTipoStatusDiscente;

	/** valida campo para seleção da situação do Discente Monitoria */
	private boolean checkBuscaTipoStatusProj;
	
	/** Atributo utilizado nas consultas */
	private boolean menu;
	/** Atributo utilizado nas consultas */
	private long total;

	/** Atributos utilizados na montagem do relatório de discentes do mês */
	private Integer mes, ano, idSituacao, idTipoMonitor, idSituacaoDiscMonit,idSituacaoProj;  
	/** Atributo utilizado para representar o curso */
	private Curso curso = new Curso();
	
	/**
	 * Redireciona para página de avaliar projeto de monitoria.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String avaliaProjeto() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.GESTOR_MONITORIA);
		return forward("/monitoria/AvaliacaoMonitoria/projetos.jsp");
	}

	
	/**
	 * Redireciona para página onde é possível fazer a alteração de uma avaliação de projeto.
	 * 
	 * Não é chamado por JSPs.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String alteraAvaliacaoProjeto() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.GESTOR_MONITORIA);
	    return forward("/monitoria/AvaliacaoMonitoria/lista.jsp");
	}

	
	/**
	 * Redireciona para página de avaliação do Resumo Sid.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String avaliaResumoSID() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA, SigaaPapeis.GESTOR_MONITORIA);
		return forward(ConstantesNavegacaoMonitoria.CADASTRARRESUMO_LISTA_AVALIAR);
	}
	
	/**
	 * Redireciona para página de busca do Resumo Sid.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarResumoSID() throws SegurancaException {
	    checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA, SigaaPapeis.MEMBRO_COMITE_CIENTIFICO_MONITORIA, SigaaPapeis.GESTOR_MONITORIA);
	    return forward(ConstantesNavegacaoMonitoria.BUSCAR_RESUMO_SID);

	}

	
	/**
	 * Redireciona para página onde é possível avaliar o relatório final de projeto.
	 * 
	 * Não é chamado por JSPs. 
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String avaliaRelatorioFinal() throws SegurancaException {
		checkRole(SigaaPapeis.MEMBRO_COMITE_MONITORIA);
		return forward("/monitoria/ResumoSid/avaliar.jsp");
	}

	/**
	 * Operação de cadastro de monitores realizado pela PROGRAD.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String cadastraMonitor() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		return forward("/monitoria/CadastrarMonitor/lista.jsp");
	}
	
	/**
	 * Operação de consulta de monitores realizado pela PROGRAD.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String consultarMonitor() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		return forward("/monitoria/ConsultarMonitor/lista.jsp");
	}

	
	/**
	 * Este método inicia o caso de uso de alterar o discente pela PROGRAD!
	 * Carrega o projeto selecionado e a lista de monitores existente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws ArqException 
	 */
	public String alterarMonitor() throws SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		return forward( ConstantesNavegacaoMonitoria.ALTERARDISCENTEMONITORIA_LISTA );
	}

	
	/**
	 * Este método inicia o caso de uso de cadastrar novo relatório de atividades do monitor (frequência) pela PROGRAD!
	 * Carrega o formulário de busca dos monitores
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws ArqException 
	 */
	public String novoRelatorioAtividades() throws SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA );
		return forward( ConstantesNavegacaoMonitoria.ATIVIDADEMONITOR_BUSCAR_MONITOR );
	}

	
	
	/**
	 * Inicia relatório de monitores por departamento.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>sigaa.war/monitoria/index.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioMonitoresPorCentro() throws SegurancaException{
		checkRole( SigaaPapeis.GESTOR_MONITORIA,SigaaPapeis.PORTAL_PLANEJAMENTO );
		return forward("/monitoria/Relatorios/seleciona_relatorio_monitores_centro.jsp");
	}
	
	/**
	 * Gera relatório de monitores por centro.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war\monitoria\Relatorios\seleciona_relatorio_monitores_centro.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String geraRelatorioMonitoresPorDepartamento() throws HibernateException, DAOException{
		Integer idCentro = null;
		Integer ano = null;
		Integer idTipoMonitor = null;
		Integer idTipoStatusDiscente = null;
		Integer idTipoStatusProj = null;
		ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);

		if( checkBuscaCentro ){
			idCentro = unidade.getId();
			if(idCentro > 0)
				unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);
			else
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}else
			unidade.setId(0);

		if(checkBuscaAno){
			ano = getAno();
			if(ano == null)
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		}
		else
			setAno(null);

		if(checkBuscaTipoMonitor){
			idTipoMonitor = getIdTipoMonitor();
			if(idTipoMonitor <= 0)
				addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
				
		}
		
		else
			setIdTipoMonitor(0);

		if ( checkBuscaTipoStatusDiscente ) {
			idTipoStatusDiscente = idSituacaoDiscMonit;
		}

		if ( checkBuscaTipoStatusProj ) {
			idTipoStatusProj = idSituacaoProj;
		}
		
		if (!checkBuscaCentro && !checkBuscaAno && !checkBuscaTipoMonitor){
			idCentro = null;
			idTipoMonitor = null;
			ano = null;
		}

		if(!hasErrors()){
			//Quantitativo de monitores
			projetos = dao.findMonitoresPorCentro(idCentro, ano, idTipoMonitor, idTipoStatusDiscente, idTipoStatusProj); 
			if(projetos.isEmpty()){
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward("/monitoria/Relatorios/rel_monitores_centro.jsp");
		}
		return null;
	}

	
	
	
	/**
	 * Retorna um relatório com o quantitativo de monitores por projeto.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/monitoria/Relatorios/quantitativo_monitores_form.jsp</li>
	 * <li>sigaa.war/monitoria/index.jsp</li>
	 * <li>sigaa.war/portais/rh_plan/abas/monitoria.jsp</li>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String relatorioQuantitativoMonitores() throws SegurancaException, HibernateException, DAOException{
		
			
						checkRole(SigaaPapeis.GESTOR_MONITORIA , SigaaPapeis.PORTAL_PLANEJAMENTO );		
						
						String menuString = getParameter("menu");
						boolean menu = Boolean.parseBoolean(menuString);
						
						total = 0;
						
						if( menu ){ //requisição vindo do menu, mostra form sem dados.
							checkBuscaCentro = false;
							checkBuscaCurso = false;
							checkBuscaAno = false;			
							checkGerarRelatorio = false;
							projetos = new ArrayList<ProjetoEnsino>();
							unidade = new Unidade();
							curso = new Curso();			
							ano = null;							
						}else{
						
										Integer idCentro = null;
										Integer idCurso = null;
										Integer ano = null;
										Integer idTipoMonitor = null;
										ProjetoMonitoriaDao dao =  getDAO(ProjetoMonitoriaDao.class);
										
										if( checkBuscaCentro ){
											idCentro = unidade.getId();
											unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);			
										}
											
										if( checkBuscaCurso){
											idCurso = curso.getId();
											curso =  dao.findByPrimaryKey(curso.getId(), Curso.class);
										}
										
										if(checkBuscaAno)
											ano = getAno();

										if(checkBuscaTipoMonitor)
											idTipoMonitor = getIdTipoMonitor();

										
										if (!checkBuscaCentro && !checkBuscaCurso && !checkBuscaAno && !checkBuscaTipoMonitor){
											idCentro = null;
											idCurso = null;
											idTipoMonitor = null;
											ano = null;
										}
										
										
										//Quantitativo de monitores
										projetos = dao.findQuantitativoMonitores(idCentro, idCurso, ano, idTipoMonitor); 
										
										DiscenteMonitoriaDao daoDiscente =  getDAO(DiscenteMonitoriaDao.class);
										total = daoDiscente.findQtdDiscentesMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, idCentro, idCurso, ano, true, idTipoMonitor );
										
						}
				
						
						if( checkGerarRelatorio )
							return forward( ConstantesNavegacaoMonitoria.RELATORIO_QUANTITATIVOMONITORES_REL );
				  	   else
							return forward( ConstantesNavegacaoMonitoria.RELATORIO_QUANTITATIVOMONITORES_FORM );
							
		
	}
	
	/**
	 * Retorna um relatório de monitores por projeto
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <li>sigaa.war/monitoria/Relatorios/projetos_monitores_form.jsp</li>
	 * <li>sigaa.war/monitoria/index.jsp</li>
	 * <li>sigaa.war/portais/rh_plan/abas/monitoria.jsp</li>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String relatorioMonitoresPorProjeto() throws SegurancaException, DAOException{
	    checkRole( SigaaPapeis.GESTOR_MONITORIA , SigaaPapeis.PORTAL_PLANEJAMENTO);

	    String menuString = getParameter("menu");
	    boolean menu = Boolean.parseBoolean(menuString);
	    total = 0;

	    if( menu ){ //requisição vindo do menu, mostra form sem dados.
		checkGerarRelatorio = false;
		checkBuscaCentro = false;
		checkBuscaCurso = false;			
		projetos = new ArrayList<ProjetoEnsino>();
		unidade = new Unidade();
		curso = new Curso();
	    }else{

		ProjetoMonitoriaDao dao = getDAO(ProjetoMonitoriaDao.class);
		Integer idCentro = null;
		Integer idCurso = null;

		if( checkBuscaCentro ){
		    idCentro = unidade.getId();
		    unidade = dao.findByPrimaryKey(unidade.getId(), Unidade.class);			
		}

		if( checkBuscaCurso ){
		    idCurso = curso.getId();
		    curso =  dao.findByPrimaryKey(curso.getId(), Curso.class);
		}

		if (!checkBuscaCentro && !checkBuscaCurso){
		    idCentro = null;
		    idCurso = null;
		}


		//Monitores por projeto
		projetos = dao.findMonitoresPorProjeto( idCentro, idCurso );
		total = getDAO(DiscenteMonitoriaDao.class).findQtdDiscentesMonitoria(SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA, idCentro, idCurso, null, true, null);

	    }

	    if( checkGerarRelatorio )
		return forward( ConstantesNavegacaoMonitoria.RELATORIO_PROJETOSMONITORES_REL );
	    else
		return forward( ConstantesNavegacaoMonitoria.RELATORIO_PROJETOSMONITORES_FORM );

	}
	
	
	
	
	/**
	 * Relatório baseado no histórico de situações do discente de monitoria.
     * Lista a situação do monitor no período informado.
     * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>sigaa.war/monitoria/Relatorios/monitores_mes_form.jsp</li>
     * <li>sigaa.war/monitoria/index.jsp<li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String relatorioMonitoresMes() throws SegurancaException, HibernateException, DAOException{
		checkRole(SigaaPapeis.GESTOR_MONITORIA);
		
		String menuString = getParameter("menu");
		boolean menu = Boolean.parseBoolean(menuString);
		
		total = 0;
		
		if( menu ){ //requisição vindo do menu, mostra form sem dados.
			historicosDiscentes = new ArrayList<HistoricoSituacaoDiscenteMonitoria>();
			checkGerarRelatorio = false;
		}else{
			
			if ((mes == null) || (ano == null) || (idSituacao == null)){
				addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
				return null;
			}
			
			DiscenteMonitoriaDao dao =  getDAO(DiscenteMonitoriaDao.class);
			historicosDiscentes = dao.findByHistoricoSituacaoDiscenteMes(mes, ano,  idSituacao);
		}
			
		if( checkGerarRelatorio )
			return forward( ConstantesNavegacaoMonitoria.RELATORIO_MONITORESMES_REL );
		else
			return forward( ConstantesNavegacaoMonitoria.RELATORIO_MONITORESMES_FORM );
			
	}
	

	public Collection<ProjetoEnsino> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoEnsino> projetos) {
		this.projetos = projetos;
	}

	public boolean isCheckGerarRelatorio() {
		return checkGerarRelatorio;
	}

	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
		this.checkGerarRelatorio = checkGerarRelatorio;
	}

	public boolean isCheckBuscaCentro() {
		return checkBuscaCentro;
	}

	public void setCheckBuscaCentro(boolean checkBuscaCentro) {
		this.checkBuscaCentro = checkBuscaCentro;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isMenu() {
		return menu;
	}

	public void setMenu(boolean menu) {
		this.menu = menu;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaCoordenador() {
		return checkBuscaCoordenador;
	}

	public void setCheckBuscaCoordenador(boolean checkBuscaCoordenador) {
		this.checkBuscaCoordenador = checkBuscaCoordenador;
	}

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Collection<HistoricoSituacaoDiscenteMonitoria> getHistoricosDiscentes() {
		return historicosDiscentes;
	}

	public void setHistoricosDiscentes(
			Collection<HistoricoSituacaoDiscenteMonitoria> historicosDiscentes) {
		this.historicosDiscentes = historicosDiscentes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getIdSituacao() {
		return idSituacao;
	}

	public void setIdSituacao(Integer idSituacao) {
		this.idSituacao = idSituacao;
	}

	public boolean isCheckBuscaCurso() {
		return checkBuscaCurso;
	}

	public void setCheckBuscaCurso(boolean checkBuscaCurso) {
		this.checkBuscaCurso = checkBuscaCurso;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isCheckBuscaTipoMonitor() {
		return checkBuscaTipoMonitor;
	}

	public void setCheckBuscaTipoMonitor(boolean checkBuscaTipoMonitor) {
		this.checkBuscaTipoMonitor = checkBuscaTipoMonitor;
	}

	public Integer getIdTipoMonitor() {
		return idTipoMonitor;
	}

	public void setIdTipoMonitor(Integer idTipoMonitor) {
		this.idTipoMonitor = idTipoMonitor;
	}


	public Collection<Object> getLista() {
		return lista;
	}

	public void setLista(Collection<Object> lista) {
		this.lista = lista;
	}

	public Integer getIdSituacaoDiscMonit() {
		return idSituacaoDiscMonit;
	}

	public void setIdSituacaoDiscMonit(Integer idSituacaoDiscMonit) {
		this.idSituacaoDiscMonit = idSituacaoDiscMonit;
	}


	public boolean isCheckBuscaTipoStatusDiscente() {
		return checkBuscaTipoStatusDiscente;
	}


	public void setCheckBuscaTipoStatusDiscente(boolean checkBuscaTipoStatusDiscente) {
		this.checkBuscaTipoStatusDiscente = checkBuscaTipoStatusDiscente;
	}


	public boolean isCheckBuscaTipoStatusProj() {
		return checkBuscaTipoStatusProj;
	}


	public void setCheckBuscaTipoStatusProj(boolean checkBuscaTipoStatusProj) {
		this.checkBuscaTipoStatusProj = checkBuscaTipoStatusProj;
	}


	public Integer getIdSituacaoProj() {
		return idSituacaoProj;
	}


	public void setIdSituacaoProj(Integer idSituacaoProj) {
		this.idSituacaoProj = idSituacaoProj;
	}
	
}