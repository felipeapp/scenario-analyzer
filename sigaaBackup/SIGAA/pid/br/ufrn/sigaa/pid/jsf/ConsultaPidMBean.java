/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/04/2010
 *
 */
package br.ufrn.sigaa.pid.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.pid.dao.PlanoIndividualDocenteDao;
import br.ufrn.sigaa.pid.dominio.PlanoIndividualDocente;

/**
 * MBean utilizado para a consulta de Planos Individuais de Docentes. 
 *  
 * @author wendell
 *
 */
@Component("consultaPidMBean") @Scope("request")
public class ConsultaPidMBean extends SigaaAbstractController<PlanoIndividualDocente> {

	// TODO: Implementar a visualização em formato de relatório, na área pública
	
	/** Ano-Período ao qual o relatório se refere. */
	private Integer ano, periodo;
	/** Docente para o qual o relatório será referenciado. */
	private Servidor docente;
	/** Unidade para o qual o relatório será referenciado. */
	private Unidade unidade;

	/** Indica se o formulário deverá solicitar do usuário um ano-período. */
	private boolean opcaoAnoPeriodo;
	/** Indica se o formulário deverá solicitar do usuário uma unidade. */
	private boolean opcaoUnidade;
	/** Indica se o formulário deverá solicitar do usuário um docente. */
	private boolean opcaoDocente;
	
	/** Construtor padrão
	 * @throws DAOException
	 */
	public ConsultaPidMBean() throws DAOException {
		CalendarioAcademico calendario = null;
		// necessário caso esteja acessando do portal público, não existe usuário logado
		if (getUsuarioLogado() != null)
			calendario = getCalendarioVigente();
		else
			calendario = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		
		this.obj = new PlanoIndividualDocente();
		
		this.opcaoAnoPeriodo = true;
		this.ano = calendario.getAno(); 
		this.periodo = calendario.getPeriodo(); 
		
		this.docente = new Servidor();
		this.unidade = new Unidade();
	}

	/**
	 * Abre o form de busca do PID. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciar() {
		return forward("/pid/busca_pid/form_busca.jsp");
	}

	/**
	 * Abre o form de busca do PID. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws IOException 
	 */
	public String iniciarSelecaoSIGRH() throws IOException {
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_CPDI);
		getCurrentResponse().sendRedirect("/sigaa/pid/busca_pid/form_busca.jsf?id_unidade="+unidade.getId());
		return null;
	}

	/**
	 * Busca por plano de docências.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/pid/busca_pid/form_busca.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws DAOException {
		resultadosBusca  = new ArrayList<PlanoIndividualDocente>();
		if ( validarOpcoes() )
			resultadosBusca = getDAO(PlanoIndividualDocenteDao.class).find(ano, periodo, docente, unidade);
		
		if (isEmpty(resultadosBusca) && !hasErrors()) {
			addMensagemWarning("Não foram encontrados planos individuais de acordo com os critérios de busca informados.");
		}
		return null;	
	}

	/**
	 * Visualizar o PID do docente.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/pid/busca_pid/form_busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public String visualizar() throws ArqException, NegocioException {
		populateObj(false);
		
		CargaHorariaPIDMBean mBean = getMBean("cargaHorariaPIDMBean");
		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
		
		if ( obj != null) {
			if (obj.getServidor().isDedicacaoExclusiva())
				obj.getServidor().setRegimeTrabalho(CargaHorariaPIDMBean.CARGA_HORARIA_DEDICACAO_EXCLUSIVA);
		
			// exibe os PIDs com o dados que foram submetidos pelo docente, 
			// independente se houve alterações nas turmas do PID após a submissão 
			mBean.carregarDadosPID(pidDAO, obj, true);
		}
		return forward("/pid/busca_pid/relatorio.jsp");
	}

	/**
	 * Exibe o PID em formato de relatório. Usado no Portal Público da UFRN.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/public/docente/listagem_pids.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String visualizarPortalPublico() throws ArqException, NegocioException {
		Integer id = getParameterInt("id", 0);
		obj = getDAO(PlanoIndividualDocenteDao.class).findByPrimaryKey(id, PlanoIndividualDocente.class);
		setSubSistemaAtual(new SubSistema(0, "Portal Público", "", "", "/sigaa"));
		return visualizar();
	}
	
	/** 
	 * Método para verificação de campos.
	 * Se todos os campos forem validados retorna true.
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractController#hasErrors()
	 */
	private boolean validarOpcoes() {
		if (opcaoAnoPeriodo) {
			validateRequired(ano, "Ano de referência" , erros);
			validateRequired(periodo, "Período de referência" , erros);
		}
		else {
			ano = null;
			periodo = null;
		}
		
		if (opcaoUnidade)
			validateRequired(unidade, "Unidade de lotação" , erros);
		else
			unidade = new Unidade();
		
		if (opcaoDocente)
			validateRequired(docente, "Docente" , erros);
		else
			docente = new Servidor();
				
		if (erros.isEmpty())
			return true;
		else
			return false;
	}

	/**
	 * Seta o docente selecionado no autocomplete ao fazer a busca por docente. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/pid/busca_pid/form_busca.jsp</li>
	 * </ul>
	 * 
	 * @param e
	 * @throws DAOException
	 */
	public void selecionarDocente(ActionEvent e) throws DAOException {
		docente = (Servidor) e.getComponent().getAttributes().get("docente");
		if (!isEmpty(docente)) {
			docente = getGenericDAO().refresh(docente);
		}
	}

	/**
	 * Exporta os Planos Individuais de Docentes no formado CSV.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/pid/busca_pid/form_busca_anoPeriodo.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public void exportarPID() throws ArqException {
		PlanoIndividualDocenteDao pidDAO = getDAO(PlanoIndividualDocenteDao.class);
		try {
			String dados;
			dados = pidDAO.findByAnoPeriodo(this.ano, this.periodo);
			if (ValidatorUtil.isEmpty(dados)) {
				addMensagemErro("Não há Planos Individuais de Docentes para esse Ano/Período.");
				return;
			}
			
			String nomeArquivo =  "pid_"+ano+"_"+periodo+".csv";
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
			PrintWriter out = getCurrentResponse().getWriter();
			out.println(dados);
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			tratamentoErroPadrao(e);
		}
	}
	
	public String buscarPIDAnoPeriodo() {
	return forward("/pid/busca_pid/form_busca_anoPeriodo.jsf");
	}
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Servidor getDocente() {
		return docente;
	}

	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isOpcaoAnoPeriodo() {
		return opcaoAnoPeriodo;
	}

	public void setOpcaoAnoPeriodo(boolean opcaoAnoPeriodo) {
		this.opcaoAnoPeriodo = opcaoAnoPeriodo;
	}

	public boolean isOpcaoDocente() {
		return opcaoDocente;
	}

	public void setOpcaoDocente(boolean opcaoDocente) {
		this.opcaoDocente = opcaoDocente;
	}

	public boolean isOpcaoUnidade() {
		return opcaoUnidade;
	}

	public void setOpcaoUnidade(boolean opcaoUnidade) {
		this.opcaoUnidade = opcaoUnidade;
	}
	
	/**
	 * Utilizado na integração pelo SIGRH para consulta do PID.
	 * @return
	 */
	public String getUnidadeIntegracao() {
		String idUnidade = getParameter("id_unidade");
		if(!isEmpty(idUnidade)) {
			unidade.setId(Integer.parseInt(idUnidade));
			opcaoUnidade = true;
		}
		return null;
	}
	
}
