/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/11/2010
 */
package br.ufrn.sigaa.estagio.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.estagio.RelatorioEstagioDao;
import br.ufrn.sigaa.arq.dao.estagio.RenovacaoEstagioDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.estagio.dominio.Estagiario;
import br.ufrn.sigaa.estagio.dominio.RelatorioEstagio;
import br.ufrn.sigaa.estagio.dominio.RelatorioEstagioRespostas;
import br.ufrn.sigaa.estagio.dominio.RenovacaoEstagio;
import br.ufrn.sigaa.estagio.dominio.TipoRelatorioEstagio;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.TipoQuestionario;
import br.ufrn.sigaa.questionario.jsf.QuestionarioRespostasMBean;

/**
 * Este MBean tem como finalidade de auxiliar nas operações relacionadas ao 
 * Preenchimento do Relatório de Estágio
 * 
 * @author Arlindo Rodrigues
 *
 */
@Component("relatorioEstagioMBean") @Scope("request")
public class RelatorioEstagioMBean extends SigaaAbstractController<RelatorioEstagio>  {
	
	/** Estágio Selecionado */
	private Estagiario estagio;
	
	/** MBean injetado para auxilio nas operações de respostas de Questionário */
	@Autowired
	private QuestionarioRespostasMBean questionarioRespostasMBean;
	/** MBean injetado para auxiliar na busca de estágio */
	@Autowired
	private BuscaEstagioMBean buscaEstagioMBean;
	
	/** Relatórios de Estágios do Estágio selecionado */
	private Collection<RelatorioEstagio> relatoriosEstagio = new ArrayList<RelatorioEstagio>();
	
	/** Lista de Questionários Encontrados */
	private Collection<Questionario> questionarios = new ArrayList<Questionario>();
	
	/** Questionário Selecionado */
	private Questionario questionario;
	
	/** Construtor padrão */
	public RelatorioEstagioMBean() {
		initObj();
	}
	
	/**
	 * Inicializa os Objetos
	 */
	private void initObj(){
		obj = new RelatorioEstagio();
		obj.setQuestionario(new Questionario());
		obj.setEstagio(new Estagiario());
		obj.setRelatorioRespostas(new RelatorioEstagioRespostas());
	}
	
	/**
	 * Inicia o Cadastro do Relatório Periódico
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public String iniciarRelatorioPeriodico() throws HibernateException, ArqException{
		return iniciar(new TipoRelatorioEstagio(TipoRelatorioEstagio.RELATORIO_PERIODICO));
	}
	
	/**
	 * Inicia o Cadastro do Relatório de Renovação
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public String iniciarRelatorioRenovacao() throws HibernateException, ArqException{
		return iniciar(new TipoRelatorioEstagio(TipoRelatorioEstagio.RELATORIO_RENOVACAO));
	}	
	
	/**
	 * Inicia o Cadastro do Relatório Final
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws HibernateException
	 * @throws ArqException
	 */
	public String iniciarRelatorioFinal() throws HibernateException, ArqException{
		return iniciar(new TipoRelatorioEstagio(TipoRelatorioEstagio.RELATORIO_FINAL));
	}	
	
	/**
	 * Inicia o Questionário para Preenchimento do Relatório do Estágio
	 * @return
	 * @throws HibernateException
	 * @throws ArqException 
	 */
	private String iniciar(TipoRelatorioEstagio tipoRelatorio) throws HibernateException, ArqException{		

		if (ValidatorUtil.isEmpty(estagio)){
			addMensagemErro("Estágio não selecionado!");
			return null;
		}
		
		initObj();
		
		obj.setTipo(tipoRelatorio);
		
		RelatorioEstagioDao relatoriodao = getDAO(RelatorioEstagioDao.class);
		QuestionarioDao dao = getDAO(QuestionarioDao.class);
		try {			
			validaRelatorio(relatoriodao);
			
			if (hasErrors())
				return null;
			
			obj.setEstagio(estagio);
			
			TipoQuestionario tipo = null;
			if (isPortalDiscente())
				tipo = new TipoQuestionario(TipoQuestionario.RELATORIO_DE_ESTAGIO_DISCENTE);
			else if (isPortalConcedenteEstagio())
				tipo = new TipoQuestionario(TipoQuestionario.RELATORIO_DE_ESTAGIO_SUPERVISOR);
			else if (isPortalDocente())
				tipo = new TipoQuestionario(TipoQuestionario.RELATORIO_DE_ORIENTADOR_DE_ESTAGIO);
			else {
				addMensagemErro("Não foi possível definir o tipo de relatório a ser preenchido.");
				return null;
			}
			if (!ValidatorUtil.isEmpty(tipo)){
				
				questionarios = dao.findByTipo(tipo.getId());				
				if (ValidatorUtil.isEmpty(questionarios)){
					addMensagemErro("Não existe nenhum questionário cadastrado.");
					return null;
				}
										
				if (questionarios.size() == 1)							
					return iniciarQuestionario(questionarios.iterator().next());
				else
					return forward(getListPage());
				
			} 
		} finally {
			if (relatoriodao != null)
				relatoriodao.close();
			if (dao != null)
				dao.close();			
		}
		
		return null;
	}

	/**
	 * Valida Relatórios Pendentes
	 * @param dao
	 * @throws DAOException
	 */
	private void validaRelatorio(RelatorioEstagioDao dao) throws DAOException {
		if (obj.getTipo().getId() == TipoRelatorioEstagio.RELATORIO_RENOVACAO){			
			RenovacaoEstagioDao daoRenovacao = getDAO(RenovacaoEstagioDao.class);
			try {
				// Verifica se existe alguma renovação pendente de relatório. 
				RenovacaoEstagio renovacao = daoRenovacao.findRenovacaoAbertaByEstagio(estagio);
				if (ValidatorUtil.isEmpty(renovacao)){
					addMensagemErro("Não existem nenhuma Renovação para o Estágio Selecionado.");
				} else {
					Collection<RelatorioEstagio> relatorios = dao.findRelatorioByEstagio(estagio, getUsuarioLogado().getPessoa(), renovacao, obj.getTipo().getId(), true);
					if (!ValidatorUtil.isEmpty(relatorios)){
						addMensagemErro("Os Relatórios referente a Renovação do Estágio, já foram preenchidos.");
					}
					obj.setRenovacaoEstagio(renovacao);												
				}													
			} finally {
				if (daoRenovacao != null)
					daoRenovacao.close();
			}
			
		} else {
			Collection<RelatorioEstagio> relatorios = dao.findRelatorioByEstagio(estagio, getUsuarioLogado().getPessoa(), null, obj.getTipo().getId(), true);		
			if (!ValidatorUtil.isEmpty(relatorios)){
				
				if (obj.getTipo().getId() == TipoRelatorioEstagio.RELATORIO_FINAL){
					addMensagemErro("O Relatório Final já foi preenchido e Aprovado.");
				} else if (obj.getTipo().getId() == TipoRelatorioEstagio.RELATORIO_PERIODICO){
					if (relatorios.size() >= estagio.getQuantRelatoriosNecessarios())
						addMensagemErro("Já foram preenchidos todos os Relatórios Periódicos.");				
				}
				
			}			
		}
	}
	
	/**
	 * Inicia o Questionário
	 * @param q
	 * @return
	 * @throws ArqException
	 */
	private String iniciarQuestionario(Questionario q) throws ArqException{
		prepareMovimento(SigaaListaComando.CADASTRAR_RELATORIO_ESTAGIO);		
		// recupera o último relatório não preenchido
		RelatorioEstagioDao dao = getDAO(RelatorioEstagioDao.class);
		RelatorioEstagio relatorio = dao.findPendenteByEstagiarioTipoQuestionario(obj.getEstagio().getId(), obj.getTipo().getId(), q.getId());
		if (relatorio != null) {
			obj = relatorio;
			questionarioRespostasMBean.popularVizualicacaoRespostas(obj.getRelatorioRespostas().getQuestionarioRespostas());
		} else {
			q = getGenericDAO().refresh(q);		
			obj.setQuestionario(q);
			// Evitar Lazy
			obj.getQuestionario().getPerguntas().iterator();		
			obj.setEstagio( getGenericDAO().refresh(obj.getEstagio()) );
			questionarioRespostasMBean.inicializar(obj.getQuestionario());
		}
		return forward(getFormPage());		
	}
	
	/**
	 * Seleciona o Questionário para responde-lo
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/relatorio_estagio/lista_questionarios.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String selecionarQuestionario() throws ArqException{
		if (ValidatorUtil.isEmpty(questionario)){
			addMensagemErro("Nenhum Questionário selecionado!");
			return null;
		}
		
		return iniciarQuestionario(questionario);
	}
	
	/**
	 * Valida as respostas.<br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/relatorio_estagio/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 * @throws SegurancaException 
	 */
	public String submeterRelatorio() throws SegurancaException, ArqException, NegocioException {
		questionarioRespostasMBean.validarRepostas();
		if (hasErrors()) {
			return null;
		}
		
		obj.getRelatorioRespostas().setQuestionarioRespostas(
				questionarioRespostasMBean.getObj());
		
		return cadastrar();
	}	
	
	/**
	 * Cadastra o Relatório do Estágio
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/relatorio_estagio/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		if (hasErrors()) 
			return null;
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(getUltimoComando());
		try {
			execute(mov);
			addMensagemInformation("Relatório Cadastrado com sucesso.");
			
			if (getUltimoComando().equals(SigaaListaComando.APROVAR_RELATORIO_ESTAGIO))
				return view();
			
			return buscaEstagioMBean.filtrar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
	}
	
	/**
	 * Valida o Relatório de Estágio
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/relatorio_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String validarRelatorio() throws ArqException, NegocioException {
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Nenhum Relatório selecionado");
			return null;			
		}
			
		RelatorioEstagioDao dao = getDAO(RelatorioEstagioDao.class);
		try {
			obj = dao.findRelatorioById(obj.getId());
			if (obj.isAprovado()){
				addMensagemErro("O Relatório selecionado já está Aprovado");
				return null;				
			}
		} finally{
			if (dao != null)
				dao.close();
		}
		
		prepareMovimento(SigaaListaComando.APROVAR_RELATORIO_ESTAGIO);
		
		return cadastrar();
	}
	
	/**
	 * Visualiza os Relatórios de Estágios cadastrados
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/estagio/include/_lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String view() throws DAOException {
		estagio = getGenericDAO().refresh(estagio);
		
		RelatorioEstagioDao dao = getDAO(RelatorioEstagioDao.class);
		try {			
			relatoriosEstagio = dao.findRelatorioByEstagio(estagio, (isPortalCoordenadorGraduacao() ? null : getUsuarioLogado().getPessoa()), null, null, false);
			if (ValidatorUtil.isEmpty(relatoriosEstagio)){
				addMensagemErro("Nenhum Relatório cadastrado");
				return null;
			}			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward("/estagio/relatorio_estagio/view.jsp");
	}
	
	/**
	 * Visualiza as Respostas do Relatório Selecionado
     * <br><br>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/estagio/relatorio_estagio/view.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String viewRespostas() throws DAOException{
		if (ValidatorUtil.isEmpty(obj)){
			addMensagemErro("Nenhum Relatório selecionado");
			return null;			
		}
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), RelatorioEstagio.class);
		questionarioRespostasMBean.popularVizualicacaoRespostas(obj.getRelatorioRespostas().getQuestionarioRespostas());
		
		return forward("/estagio/relatorio_estagio/respostas.jsp");
	}
	
	@Override
	public String getFormPage() {
		return "/estagio/relatorio_estagio/form.jsp";
	}
	
	@Override
	public String getListPage() {
		return "/estagio/relatorio_estagio/lista_questionarios.jsp";
	}

	public Estagiario getEstagio() {
		return estagio;
	}

	public void setEstagio(Estagiario estagio) {
		this.estagio = estagio;
	}

	public Collection<RelatorioEstagio> getRelatoriosEstagio() {
		return relatoriosEstagio;
	}

	public void setRelatoriosEstagio(Collection<RelatorioEstagio> relatoriosEstagio) {
		this.relatoriosEstagio = relatoriosEstagio;
	}

	public Questionario getQuestionario() {
		return questionario;
	}

	public void setQuestionario(Questionario questionario) {
		this.questionario = questionario;
	}

	public Collection<Questionario> getQuestionarios() {
		return questionarios;
	}

	public void setQuestionarios(Collection<Questionario> questionarios) {
		this.questionarios = questionarios;
	}
}
