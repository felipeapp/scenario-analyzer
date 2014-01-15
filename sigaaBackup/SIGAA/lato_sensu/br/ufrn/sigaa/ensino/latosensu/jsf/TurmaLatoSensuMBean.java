/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 27/05/2010
 *
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.EquipeLatoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.jsf.ComponenteCurricularMBean;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.jsf.TurmaMBean;
import br.ufrn.sigaa.ensino.latosensu.dominio.CorpoDocenteDisciplinaLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.OperacaoTurmaValidator;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.TurmaMov;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed Bean responsável por operações em turmas Lato Sensu.
 * 
 * @author Édipo
 *
 */
@Component("turmaLatoSensuBean") @Scope("session")
public class TurmaLatoSensuMBean extends TurmaMBean {
	/** Define o link para o formulário de dados gerais. */
	public static final String JSP_DADOS_GERAIS = "/lato/turma/dados_gerais.jsp";
	/** Define o link para o resumo dos dados da turma. */
	public static final String JSP_RESUMO = "/lato/turma/resumo.jsp";

	/** Registro de alterações efetuadas na turma em relação às pré-aprovadas na proposta do curso de lato sensu. */
	private RegistroAlteracaoLato registroAlteracao;
	/** Indica que o controller deve setar o docente da turma com os definidos na proposta do curso. */
	private boolean docenteOnce;
	/** Indica se o usuário deverá selecionar ou não o curso. */
	private boolean selecionaCurso;
	/** Indica se a operação é de remoção da turma. */
	private boolean remover;
	
	@Override
	public String cadastrar() throws ArqException {
		if (getConfirmButton().equalsIgnoreCase("remover")) {
			return remover();
		} else {
			
			Comando cmd = SigaaListaComando.CADASTRAR_TURMA;
			TurmaMov mov = new TurmaMov();
			if (getConfirmButton().equalsIgnoreCase("alterar")) {
				cmd = SigaaListaComando.ALTERAR_TURMA;
				mov.setAlteracaoTurma(getAlteracaoTurma());
			}
			if (obj.getCurso() != null && obj.getCurso().getId() == 0)
				obj.setCurso(null);
			
			mov.setCodMovimento(cmd);
			mov.setTurma(obj);
			mov.setSolicitacaoEnsinoIndividualOuFerias(null);
			mov.setSolicitacoes( new HashSet<SolicitacaoTurma>() );
			mov.setRegistroAlteracaoLato(registroAlteracao);

			for( ReservaCurso r : obj.getReservas() ){
				mov.getSolicitacoes().add( r.getSolicitacao() );
			}
			try {
				executeWithoutClosingSession(mov, getCurrentRequest());
			} catch (NegocioException e) {
				addMensagens(e.getListaMensagens());
				e.printStackTrace();
				return null;
			}
			
			if (cmd.equals(SigaaListaComando.CADASTRAR_TURMA)) {
				if( obj.getDisciplina().isSubUnidade() )
					addMessage("ATENÇÃO! A turma criada foi de uma subunidade de um bloco e só estará disponível para matrícula após todas as subunidades do bloco terem sido criadas.", TipoMensagemUFRN.INFORMATION);
				addMessage("Turma " + obj.getDescricaoSemDocente() + " cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			} else {
				addMessage("Turma " + obj.getDescricaoSemDocente() + " alterada com sucesso!", TipoMensagemUFRN.INFORMATION);
			}			
			return ((BuscaTurmaMBean)getMBean("buscaTurmaBean")).popularBuscaGeral();
		}
	}

	@Override
	public String formConfirmacao() {
		return forward(JSP_RESUMO);
	}

	@Override
	public String formDadosGerais() {
		return forward(JSP_DADOS_GERAIS);
	}

	@Override
	public String formSelecaoComponente() {
		try {
			ComponenteCurricularMBean mBean = getMBean("componenteCurricular");
			mBean.setSelecionaUnidade(false);
			Unidade unidade = null;
			if (isPortalCoordenadorLato()) {
				if (getCursoAtualCoordenacao() != null) {
					unidade = getCursoAtualCoordenacao().getUnidade();
				} else {
					addMensagemErro("Não foi possível determinar o curso atual da coordenação.");
					return null;
				}
			}
			return mBean.buscarComponente(this, "Cadastro de Turmas", unidade, false,false, TipoComponenteCurricular.DISCIPLINA, TipoComponenteCurricular.MODULO);
		} catch (ArqException e) {
			tratamentoErroPadrao(e);
			return null;
		}
	}

	/**
	 * Recarrega o curso selecionado na tela de cadastro de turma.<br>
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/lato/turma/dados_gerais.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 */
	public void reCarregarCurso(ValueChangeEvent evt) throws DAOException {
		
		Integer id = (Integer) evt.getNewValue();
		Curso curso = getGenericDAO().findByPrimaryKey(id, CursoLato.class);
		obj.setCurso(curso);
		obj.setDistancia(curso.isADistancia());
		
	}
	
	@Override
	public char getNivelEnsino() {
		return NivelEnsino.LATO;
	}

	@Override
	public boolean isDefineDocentes() {
		return true;
	}

	@Override
	public boolean isDefineHorario() {
		return !obj.isDistancia() && obj.getDisciplina().isExigeHorarioEmTurmas();
	}

	@Override
	public boolean isPodeAlterarHorarios() {
		return obj.isAberta() || (obj.getId() == 0 || !isMatriculada());
	}

	/**
	 * Indica se a turma de Lato Sensu é EAD. EAD não tem polo!
	 */
	@Override
	public boolean isTurmaEad() {
		if (obj.getCurso() != null)
			return obj.getCurso().isADistancia();
		return false;
	}

	@Override
	public String remover() throws ArqException {
		TurmaMov mov = new TurmaMov();
		mov.setCodMovimento(SigaaListaComando.REMOVER_TURMA);
		mov.setTurma(obj);

		try {
			Turma removida = (Turma) executeWithoutClosingSession(mov, getCurrentRequest());
			if( removida.getPolo() != null )
				removida.setPolo( getGenericDAO().findByPrimaryKey( removida.getPolo().getId() , Polo.class) );
			addMessage("Turma " + removida.getDescricaoSemDocente() + " removida com sucesso!", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return getFormPage();
		} catch (SegurancaException e) {
			throw e;
		}

		clear();
		return cancelar();
	}

	@Override
	public void validaDadosGerais(ListaMensagens erros) throws DAOException {
		if( obj.getDisciplina() == null || obj.getDisciplina().getId() <= 0 ){
			addMensagemErro("Selecione um componente curricular.");
		} else 	if (!obj.getDisciplina().isAtivo()) {
			addMensagemErro("O componente curricular " + obj.getDisciplina().getDescricaoResumida() + " não está ativo.");
		}
		if(hasErrors()) return;
		
		TurmaDao dao = getDAO(TurmaDao.class);
		obj.setDisciplina( dao.findByPrimaryKey(obj.getDisciplina().getId(), ComponenteCurricular.class) );

		if (obj.getCurso() != null && obj.getCurso().getId() > 0)
			obj.setCurso(dao.findByPrimaryKey(obj.getCurso().getId(), Curso.class));
		
		// Atribuir ano-período com base nas datas informadas no formulário
		if (obj.getDataInicio() != null) {
			Calendar ini = Calendar.getInstance();
			ini.setTime(obj.getDataInicio());
			obj.setAno(ini.get(Calendar.YEAR));
			if (ini.get(Calendar.MONTH) <= Calendar.JUNE)
				obj.setPeriodo(1);
			else
				obj.setPeriodo(2);
		}
		
		TurmaValidator.validaDadosBasicosLatoSensu(obj, erros);
		
		if (hasOnlyErrors())
			return;

	}
	
	@Override
	public void validaDocentesTurma(ListaMensagens erros) throws DAOException {
		// cria registro de alteração e informa ao usuário
		EquipeLatoDao daoE = getDAO(EquipeLatoDao.class);
		int numDocentes = 0;
		// docentes definidos para a disciplina na proposta do curso.
		Collection<CorpoDocenteDisciplinaLato> docentes = daoE.findByDisciplina(obj.getDisciplina().getId(), obj.getCurso().getId());
		if (docentes.isEmpty())
			return;
		// verifica se os docentes informados pelo usuário são os mesmos dos definidos na proposta do curso
		for (CorpoDocenteDisciplinaLato docenteLato : docentes) {
			for (Docente docente : obj.getDocentes()){
				if (docente instanceof Servidor) {
					if (docenteLato.getDocente() != null && docenteLato.getDocente().equals(docente)) {
						numDocentes++;
						break;
					}
				}
				if (docente instanceof DocenteExterno) {
					if (docenteLato.getDocenteExterno() != null && docenteLato.getDocenteExterno().equals(docente)) {
						numDocentes++;
						break;
					}
				}				
			}
		}
		// caso não seja, registra a alteração
		if (numDocentes != docentes.size() || docentes.size() != obj.getDocentes().size()) {
			registroAlteracao = new RegistroAlteracaoLato();
			registroAlteracao.setDisciplina(obj.getDisciplina());
			registroAlteracao.setTurma(obj);
			addMensagemWarning("Os docentes informados são diferentes dos informados na proposta do curso.<br>"
					+ "Esta alteração será registrada para posterior averiguação da Pró-Reitoria de Pós-Graduação");
		} else {
			registroAlteracao = null;
		}
	}

	@Override
	public void checkRoleCadastroTurmaSemSolicitacao(ListaMensagens erros) throws SegurancaException {
		checkRole(new int[] { SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.GESTOR_LATO});
		try {
			if( isUserInRole( SigaaPapeis.PPG ) ){
				obj.setAno(CalendarUtils.getAnoAtual());
				obj.setPeriodo(getPeriodoAtual());
			}else{
					obj.setAno(getCalendario().getAno());
				obj.setPeriodo(getCalendario().getPeriodo());
			}
			this.remover = false;
		} catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
	}

	@Override
	protected CalendarioAcademico getCalendario() throws DAOException {
		CalendarioAcademico cal = getCalendarioVigente();
		if( isUserInRole( SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) 
				&& getCursoAtualCoordenacao() != null) {
			cal = CalendarioAcademicoHelper.getCalendario(getCursoAtualCoordenacao());
		}
		return cal;
	}

	@Override
	public void beforeSelecionarComponente() throws ArqException {
		this.selecionaCurso = false;
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO) && getCursoAtualCoordenacao() != null) {
			obj.setCurso(getCursoAtualCoordenacao());
		} else if (isLatoSensu()) {
			if (obj.getCurso() == null) obj.setCurso(new CursoLato());
			this.selecionaCurso = true;
		} else {
			addMensagemErro("Não foi possível determinar o curso atual da coordenação.");
			return;
		}
		obj.setDistancia(obj.getCurso().isADistancia());
		obj.setAno(getCalendario().getAno());
		obj.setPeriodo(getCalendario().getPeriodo());
		obj.setDataInicio( getCalendario().getInicioPeriodoLetivo() );
		obj.setDataFim( getCalendario().getFimPeriodoLetivo() );
		obj.setSituacaoTurma( new SituacaoTurma( SituacaoTurma.A_DEFINIR_DOCENTE ) );
		obj.setTipo( Turma.REGULAR );
		registroAlteracao = null;
		docenteOnce = false;
	}
	
	@Override
	public void beforeDefinirDocentesTurma() throws DAOException {
		if (!docenteOnce && !obj.getDisciplina().isPermiteHorarioDocenteFlexivel()) {
			GenericDAO dao = getGenericDAO();
			obj.setDisciplina(dao.refresh(obj.getDisciplina()));
			if (obj.getCurso().getId() > 0 && obj.getId() == 0) {
				EquipeLatoDao daoE = getDAO(EquipeLatoDao.class);
				if (obj.getDisciplina().getId() > 0) {
					CursoLato cursoLato = daoE.findByPrimaryKey(obj.getCurso().getId(), CursoLato.class);
					Collection<CorpoDocenteDisciplinaLato> equipes = daoE.findByDisciplina(obj.getDisciplina().getId(), cursoLato.getPropostaCurso().getId());
					for (CorpoDocenteDisciplinaLato el : equipes) {
						DocenteTurma d = new DocenteTurma();
						if (el.getDocenteExterno() != null) {
							d.setDocenteExterno(el.getDocenteExterno());
							d.setDocente(null);
						} else if (el.getDocente() != null) {
							d.setDocente(el.getDocente());
							d.setDocenteExterno(null);
						}
						d.setChDedicadaPeriodo(el.getCargaHoraria());
						d.setTurma(obj);
						// horários do docente
						for (HorarioTurma ht : obj.getHorarios()) {
							HorarioDocente hd = new HorarioDocente();
							hd.setDataFim(obj.getDataFim());
							hd.setDataInicio(obj.getDataInicio());
							hd.setDia(ht.getDia());
							hd.setHorario(ht.getHorario());
							hd.setTipo(ht.getTipo());
							d.getHorarios().add(hd);
						}
						obj.getDocentesTurmas().add(d);
					}
				}
			}
			docenteOnce = true;
		}
	}
	
	@Override
	public void beforeDadosGerais() throws DAOException {
		if (selecionaCurso) {
			if (obj.getCurso() == null) obj.setCurso(new Curso());
			Collection<SelectItem> cursosCombo = new ArrayList<SelectItem>();
			CursoLatoDao dao = getDAO(CursoLatoDao.class);
			Collection<CursoLato> cursos = dao.findCursosByDisciplina(obj.getDisciplina().getId());
			for (Curso curso : cursos) {
				cursosCombo.add(new SelectItem(curso.getId(), curso.getDescricao()));
			}
			setCursosCombo(cursosCombo);
		}
	}

	public boolean isSelecionaCurso() {
		return selecionaCurso;
	}

	@Override
	public void beforeConfirmacao() throws ArqException {
		
	}

	@Override
	public void validaHorariosTurma(ListaMensagens erros) throws DAOException {
	}

	@Override
	public void beforeAtualizarTurma() {
	}

	@Override
	public void checkRoleAtualizarTurma() throws ArqException {
		setApenasVisualizacao(false);
		remover = false;
		
		if ( !OperacaoTurmaValidator.isPermiteAlterar(obj) ) {
			addMensagemErro("Caro usuário, com seu nível de permissão não é possível alterar esta turma.");
			return;
		}

	}

	@Override
	public void checkRolePreRemover() throws ArqException {
		
		if ( !OperacaoTurmaValidator.isPermiteRemoverTurma(obj) ) {
			addMensagemErro("Caro usuário, com seu nível de permissão não é possível remover esta turma.");
			return;
		}
		
		// Validar nível do componente selecionado
		if( !isEmpty(obj.getMatriculasDisciplina()) && !isUserInRole(SigaaPapeis.GESTOR_LATO)){
			addMensagemErro("Não é possível remover a turma selecionada pois existem matrículas associadas a ela.");
			return;
		} else if( !isEmpty(obj.getMatriculasDisciplina()) && isUserInRole(SigaaPapeis.GESTOR_LATO)){
			addMensagemWarning("Existem matrículas ativas ou em espera de processamento associadas à turma selecionada.");
		}
		remover = true;
	}

	@Override
	public String formConfirmacaoRemover() {
		this.remover = true;
		return formConfirmacao();
	}

	public boolean isRemover() {
		return remover;
	}
	
	/** Indica se a operação corrente é de edição do código da turma.
	 * @return
	 */
	public boolean isEditarCodigoTurma() {
		return obj.getId() != 0;
	}

	@Override
	public ListaMensagens validarSelecaoComponenteCurricular(ComponenteCurricular componente) throws ArqException {
		return new ListaMensagens();
	}

	@Override
	public String retornarSelecaoComponente() {
		// TODO redirecionar o usuário para o formulário que invocou a seleção de componentes.
		return cancelar();
	}

	@Override
	public String definePeriodosTurma(Date inicio, Date fim)
			throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}
}
