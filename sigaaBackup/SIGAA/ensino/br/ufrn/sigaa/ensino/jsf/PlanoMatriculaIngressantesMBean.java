/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 20/11/2012
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;

/** Controller responsável por cadastrar um plano de matrícula de turmas em lote.
 *  
 * @author Édipo Elder F. de Melo
 *
 */
@Component("planoMatriculaIngressantesMBean")
@Scope("session")
public class PlanoMatriculaIngressantesMBean extends SigaaAbstractController<PlanoMatriculaIngressantes> {
	/** Turmas para sugestão de matrícula */
	private List<SugestaoMatricula> turmasAbertas;
	/** Lista de matrizes curriculares de um curso, para os planos de graduação */
	private Collection<SelectItem> matrizCurricularCombo;
	/** Lista anos-períodos que tem planos de graduação cadastrados. */
	private Collection<SelectItem> anoPeriodoCombo;
	/** Lista de horários selecionadas. */
	private List<Horario> horarios;
	/** Lista de horários das turmas selecionadas. */
	private List<HorarioTurma> horariosTurma;
	/** Ano-período para filtrar os planos de matrículas cadastrados. */
	private int anoPeriodo;
	/** ID do curso para filtrar os planos de matrículas cadastrados. */
	private int idCurso;

	/** Construtor padrão. */
	public PlanoMatriculaIngressantesMBean() {
	}
	
	/**
	 * Inicializa os dados do controller 
	 */
	public void init() {
		obj = new PlanoMatriculaIngressantes();
		obj.setNivel(getNivelEnsino());
		CalendarioAcademico cal = getCalendarioVigente();
		obj.setAno(cal.getAnoNovasTurmas());
		obj.setPeriodo(cal.getPeriodoNovasTurmas());
		if (isPortalCoordenadorGraduacao())
			obj.setCurso(getCursoAtualCoordenacao());
		horarios = null;
		all = null;
		anoPeriodo = 0;
		idCurso = 0;
		anoPeriodoCombo = null;
		matrizCurricularCombo = null;
		horarios = null;
		horariosTurma = null;
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
	}
	
	@Override
	public void checkListRole() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
	}
	
	/** Atualiza um plano cadastrado.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#atualizar()
	 */
	@Override
	public String atualizar() throws ArqException {
		checkChangeRole();
		
		if (!isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP))
			verificarPeriodoPlano(erros);
		
		if (hasErrors())
			return null;
		
		init();
		selecionaPlanoMatricula();
		carregaTurmasAbertas(obj.getMatrizCurricular().getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE.getId());
		setConfirmButton("Alterar");
		return forward(getFormPage());
	}
	
	/** Inicia o cadastro de um plano de matrícula.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkListRole();

		if (!isUserInRole(SigaaPapeis.DAE, SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CDP))
			verificarPeriodoPlano(erros);
		
		if (hasErrors())
			return null;
		
		init();
		carregaTurmasAbertas(null);
		prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE);
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE.getId());
		return super.preCadastrar();
	}
	
	/**
	 * Verificar se operação esta dentro do prazo
	 * 
	 * @param lista
	 */
	private void verificarPeriodoPlano(ListaMensagens lista) {
		if (!getCalendarioVigente().isPeriodoPlanoMatriculas())
			lista.addErro("Fora do período para Cadastrar/Alterar Planos de Matrícula");
		
	}

	/** Remove um plano de matrícula cadastrado.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#remover()
	 */
	@Override
	public String remover() throws SegurancaException {
		checkChangeRole();
		obj = new PlanoMatriculaIngressantes();
		try {
			populateObj(true);
			if (isEmpty(obj)) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}
			// força o carregamento das turmas.
			obj.getTurmas().iterator();
			setOperacaoAtiva(SigaaListaComando.REMOVER_PLANO_MATRICULA_INGRESSANTE.getId());
			prepareMovimento(SigaaListaComando.REMOVER_PLANO_MATRICULA_INGRESSANTE);
			MovimentoCadastro mov = new MovimentoCadastro(obj, SigaaListaComando.REMOVER_PLANO_MATRICULA_INGRESSANTE);
			execute(mov);
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			all = null;
			return filtrar();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return cancelar();
		}
	}
	
	/** Cadastra um novo plano de matrícula.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/confirma.jsp</li>
	 * </ul>
	 * 
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE.getId()))
			return cancelar();
		prepareMovimento(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE);
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return redirectMesmaPagina();
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return redirectMesmaPagina();
		}
		idCurso = obj.getCurso().getId(); 
		anoPeriodo = obj.getAno()*10 + obj.getPeriodo();
		obj = new PlanoMatriculaIngressantes();
		return filtrar();
	}
	
	/** Lista os planos de matrículas existentes.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#listar()
	 */
	@Override
	public String listar() throws ArqException {
		checkListRole();
		init();
		if (!getAnoPeriodoCombo().isEmpty())
			anoPeriodo = (Integer) getAnoPeriodoCombo().iterator().next().getValue();
		else
			anoPeriodo = 0;
		if (isPortalCoordenadorGraduacao()) {
			idCurso = getCursoAtualCoordenacao().getId();
			return filtrar();
		} else {
			idCurso = 0;
			return forward(getListPage());				
		}
	}
	
	/** Submete e valida os dados gerais do plano de matrícula.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/form.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String submeterDadosGerais() throws ArqException {
		checkChangeRole();
		if (!checkOperacaoAtiva(SigaaListaComando.CADASTRAR_PLANO_MATRICULA_INGRESSANTE.getId()))
			return cancelar();
		
		GenericDAO dao = getGenericDAO();
		validateRequired(obj.getAno(), "Ano", erros);
		validateRange(obj.getPeriodo(), 1, 2, "Período", erros);
		validateRequired(obj.getCurso(), "Curso", erros);
		if (obj.isGraduacao())
			validateRequired(obj.getMatrizCurricular(), "Matriz Curricular", erros);
		validateMinValue(obj.getCapacidade(), 1, "Capacidade", erros);
		obj.getTurmas().clear();
		String[] selecaoTurmas = getCurrentRequest().getParameterValues("selecaoTurmas");
		if (selecaoTurmas != null) {
			for (String idTurma : selecaoTurmas) {
				Turma turma = dao.findByPrimaryKey(Integer.valueOf(idTurma), Turma.class);
				boolean insere = true;
				// verifica se uma turma do mesmo componente curricular já foi adicionada
				for (Turma turmaAdicionada : obj.getTurmas()) {
					if (turmaAdicionada.getDisciplina().equals(turma.getDisciplina())) {
						addMensagemErro("Não é possível inserir duas turmas ou mais turmas para o componente curricular " + turmaAdicionada.getDisciplina().getCodigoNome());
						insere = false;
						break;
					}
				}
				
				
				if (turma.getAno()*10 + turma.getPeriodo() != obj.getAno()*10 + obj.getPeriodo()) {
					addMensagemErro("Não é possível adicionar a turma porque seu ano/periodo é " + turma.getAno() + "." + turma.getPeriodo() + " e o plano de " + obj.getAno() + "." + obj.getPeriodo());
					insere = false;
					break;
				}
				
				if (insere) obj.addTurma(turma);
			}
		}
		
		if (obj.getTurmas().isEmpty())
			return null;
		
		
		for (SugestaoMatricula sugestao : turmasAbertas)
			sugestao.setSelected(obj.getTurmas().contains(sugestao.getTurma()));
		validateRequired(obj.getTurmas(), "Turmas", erros);
		if (!isEmpty(obj.getTurmas()))
			TurmaValidator.validarChoqueHorarios(obj.getTurmas(), erros);
		
		
		for (Iterator<Turma> iterator = obj.getTurmas().iterator(); iterator.hasNext();) {
			Turma t = (Turma) iterator.next();
			
			if (t.getDisciplina().getPreRequisito() != null) {
				boolean resAvaliacao = ExpressaoUtil.eval(t.getDisciplina().getPreRequisito(), obj.getDisciplinas());
				if (!resAvaliacao) {
					addMensagemErro("Plano não possui os pre-requisitos necessários para adicionar a turma: " + t.getDescricaoResumida() + ". <br>Pre-requisitos: " + ExpressaoUtil.buildExpressaoFromDB(t.getDisciplina().getPreRequisito(), getDAO(ComponenteCurricularDao.class)));
					iterator.remove();
				}
			}
			if (t.getDisciplina().getCoRequisito() != null) {
				boolean resAvaliacao = ExpressaoUtil.eval(t.getDisciplina().getCoRequisito(), obj.getDisciplinas());
				if (!resAvaliacao) {
					addMensagemErro("Plano não possui os co-requisitos necessários para adicionar a turma: " + t.getDescricaoResumida() + ". <br>Co-requisitos: " + ExpressaoUtil.buildExpressaoFromDB(t.getDisciplina().getCoRequisito(), getDAO(ComponenteCurricularDao.class)));
					iterator.remove();
				}
				
			}
		}
		
		
		if (hasErrors()) return null;
		if (!obj.isGraduacao()) {
			dao.initialize(obj.getCurso());
		} else
			dao.initialize(obj.getMatrizCurricular());
		
		if (isEmpty(turmasAbertas)) {
			addMensagemErro("Não há turmas abertas para o ano-período.");
			return null;
		}
		horarios = null;
		horariosTurma = null;
		return forward("/ensino/plano_matricula_ingressantes/confirma.jsp");
	}
	
	/** Redireciona o usuário ao formulário de dados gerais.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/seleciona_turmas.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formDadosGerais() {
		return forward(getFormPage());
	}
	
	/** Carrega uma lista de turmas abertas para o curso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/seleciona_turmas.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregaTurmasAbertas(ValueChangeEvent evt) throws DAOException {
		int id = evt != null ? (Integer) evt.getNewValue() : 0;
		obj.getMatrizCurricular().setId(id);
		carregaTurmasAbertas(id);
	}
	
	/** Carrega uma lista de turmas abertas para o curso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/seleciona_turmas.jsp</li>
	 * </ul>
	 * @throws DAOException
	 */
	public void carregaMatrizesCurriculares(ValueChangeEvent evt) throws DAOException {
		int id = evt != null ? (Integer) evt.getNewValue() : 0;
		obj.getCurso().setId(id);
		getGenericDAO().initialize(obj.getCurso());
		matrizCurricularCombo = null;
		all = null;
	}
	
	/** Carrega uma lista de turmas abertas para o curso.
	 * @param idMatrizCurricular
	 * @throws DAOException
	 */
	private void carregaTurmasAbertas(int idMatrizCurricular) throws DAOException {
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		dao.initialize(obj.getMatrizCurricular());
		turmasAbertas = new LinkedList<SugestaoMatricula>();
		if (isPortalCoordenadorGraduacao() || isPortalGraduacao()) {
			// caso alterando o plano, faz um merge com turmas que foram consolidadas, excluídas ou interrompidas
			if (obj.getId() > 0) {
				Collection<SugestaoMatricula> consolidadas = dao.findSugestoesMatriculaGraduacao(idMatrizCurricular, obj.getAno(), obj.getPeriodo(), 
						SituacaoTurma.CONSOLIDADA, SituacaoTurma.EXCLUIDA, SituacaoTurma.INTERROMPIDA);
				for (SugestaoMatricula sugestao : consolidadas) {
					if (obj.getTurmas().contains(sugestao.getTurma())) {
						sugestao.setNivel("TURMAS CONSOLIDADAS, EXCLUÍDAS OU INTERROMPIDAS");
						turmasAbertas.add(sugestao);
					}
				}
			}
			// turmas abertas
			turmasAbertas.addAll(dao.findSugestoesMatriculaGraduacao(idMatrizCurricular, obj.getAno(), obj.getPeriodo(),
					SituacaoTurma.ABERTA, SituacaoTurma.A_DEFINIR_DOCENTE));
		for (SugestaoMatricula sugestao : turmasAbertas)
			sugestao.setSelected(obj.getTurmas().contains(sugestao.getTurma()));
		}
	}

	/** Carrega uma lista de matrizes curriculares de um curso.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getMatrizCurricularCombo() throws DAOException{
		if (matrizCurricularCombo == null) {
			Collection<MatrizCurricular> matrizes = new ArrayList<MatrizCurricular>();
			MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class);
			if (isPortalCoordenadorGraduacao()) {
				matrizes = dao.findByCurso(getCursoAtualCoordenacao().getId(), true);
			} else if (obj.isGraduacao()){
				matrizes = dao.findByCurso(obj.getCurso().getId(), true);
			}
			matrizCurricularCombo = toSelectItems(matrizes, "id", "descricao");
		}
		return matrizCurricularCombo;
	}
	
	/** Retorna o endereço para o formulário de cadastro.
	 * <br/>Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getFormPage()
	 */
	@Override
	public String getFormPage() {
		return "/ensino/plano_matricula_ingressantes/form.jsp";
	}
	
	/** Retorna o endereço para a página de listagem.
	 * <br/>Método não invocado por JSP.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getListPage()
	 */
	@Override
	public String getListPage() {
		return "/ensino/plano_matricula_ingressantes/lista.jsp";
	}

	/** Filtra a lista de planos de matrícula cadastrados por ano-período e/ou curso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String filtrar() throws DAOException {
		int ano = 0, periodo = 0;
		if (anoPeriodo > 0) {
			ano = anoPeriodo / 10;
			periodo = anoPeriodo % 10;
		}
		validateRequiredId(anoPeriodo, "Ano-Período", erros);
		validateRequiredId(idCurso, "Curso", erros);
		if (hasErrors()) return null;
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		resultadosBusca = dao.findByCurso(idCurso, ano, periodo);
		return forward(getListPage());
	}
	
	/** Exibe um plano de matrícula cadastrado.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String view() throws SegurancaException, DAOException {
		checkListRole();
		obj = new PlanoMatriculaIngressantes();
		selecionaPlanoMatricula();
		if (hasErrors()) return null;
		horarios = null;
		horariosTurma = null;
		return forward("/ensino/plano_matricula_ingressantes/view.jsp");
	}
	
	/** Redireciona o usuário para a lista de planos de matrículas.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/view.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formLista() {
		return forward(getListPage());
	}

	/** Carrega os dados de um plano de matrícula.
	 * @throws DAOException
	 */
	private void selecionaPlanoMatricula() throws DAOException {
		populateObj(true);
		if (isEmpty(obj)) {
			obj = new PlanoMatriculaIngressantes();
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return;
		}
		// força o carregamento da lista de turmas
		obj.getTurmas().iterator();
	}
	
	/**
	 * Retorna todos os horários cadastrados para graduação
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *  <li>/sigaa.war/ensino/plano_matricula_ingressantes/confirma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public Collection<Horario> getHorarios() throws ArqException {
		if (isEmpty(horarios)) {
			HorarioDao dao = getDAO(HorarioDao.class);
			Unidade unidade = null;
			char nivel = getNivelEnsino();

			Collection<Integer> idUnidades = new ArrayList<Integer>();			
			List<Horario> horariosUnidades = new ArrayList<Horario>();
			
			Collection<Turma> todas = obj.getTurmas();
			
			for( Turma t : todas ) {
				if( !isEmpty(t.getHorarios()) && 
						!idUnidades.contains( t.getHorarios().iterator().next().getHorario().getUnidade().getId() ) ) {
					idUnidades.add(t.getHorarios().iterator().next().getHorario().getUnidade().getId());					
					horariosUnidades.addAll(dao.findAtivoByUnidade(t.getHorarios().iterator().next().getHorario().getUnidade(), nivel));
				}				
			}
			horarios = !horariosUnidades.isEmpty() ? horariosUnidades : (List<Horario>) dao.findAtivoByUnidade(unidade, nivel);
			Collections.sort(horarios, new Comparator<Horario>() {				
				public int compare(Horario o1, Horario o2) {
					return o1.getInicio().compareTo(o2.getInicio());
				}
			});
			
		}
		return horarios;
	}
	
	/**
	 * Retorna só os horários das turmas já selecionadas para o discente ser matriculado
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *  <li>/sigaa.war/ensino/plano_matricula_ingressantes/confirma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public List<HorarioTurma> getHorariosTurma() throws DAOException {
		if (horariosTurma == null) {
			TurmaDao dao = getDAO(TurmaDao.class);
			horariosTurma = dao.findHorariosByTurmas(obj.getTurmas());
		}
		if (horariosTurma == null) {
			horariosTurma = new ArrayList<HorarioTurma>();
		}
		return horariosTurma;
	}

	/** Retorna uma coleção de SelecItem de anos-períodos que possuem planos de matrículas cadastrados.<br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ensino/plano_matricula_ingressantes/lista.jsp</li>
	 * </ul>
	 * @return 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnoPeriodoCombo() throws HibernateException, DAOException {
		if (anoPeriodoCombo == null) {
			anoPeriodoCombo = new LinkedList<SelectItem>();
			PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
			Collection<Integer[]> anosPeriodos = dao.findAllAnoPeriodo(idCurso);
			if (!isEmpty(anosPeriodos)) {
				for (Integer[] anoPeriodo : anosPeriodos) {
					anoPeriodoCombo.add(new SelectItem(anoPeriodo[0] * 10 + anoPeriodo[1], anoPeriodo[0] + "." + anoPeriodo[1]));
				}
			}
		}
		return anoPeriodoCombo;
	}
	
	public Collection<SugestaoMatricula> getTurmasAbertas() {
		return turmasAbertas;
	}

	public void setTurmasAbertas(List<SugestaoMatricula> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}

	public int getIdCurso() {
		return idCurso;
	}

	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}
}
