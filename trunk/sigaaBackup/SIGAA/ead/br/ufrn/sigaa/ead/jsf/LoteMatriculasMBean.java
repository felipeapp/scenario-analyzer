package br.ufrn.sigaa.ead.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.faces.event.ActionEvent;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.LoteMatriculas;
import br.ufrn.sigaa.ead.dominio.LoteMatriculasDiscente;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dao.TurmaEADDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Realizar a matricula de um conjunto de discentes em uma ou mais disciplinas
 * 
 * @author Henrique Andre
 *
 */
@Component("loteMatriculas") @Scope("session")
public class LoteMatriculasMBean extends SigaaAbstractController<LoteMatriculas> {

	// Atributos usado na busca por discentes
	/** Status do discente utilizado na busca por discente para efetuar matrícula. */
	private StatusDiscente status = new StatusDiscente();
	/** Ano de entrada do discente utilizado na busca por discente para efetuar matrícula. */
	private Integer anoEntrada = CalendarUtils.getAnoAtual();
	/** Período de entrada do discente utilizado na busca por discente para efetuar matrícula. */
	private Integer periodoEntrada = getPeriodoAtual();
	
	// Atributos usados na busca por componentes
	/** Código do componente para busca de turmas. */
	private String codigoComponente;
	/** Ano da turma para a busca de turmas*/
	private Integer anoComponente = CalendarUtils.getAnoAtual();
	/** Período da turma para a busca de turmas*/
	private Integer periodoComponente = getPeriodoAtual();
	/** Lista de turmas abertas para o componente no ano-período informados.*/
	private Collection<Turma> turmasAbertas = null;
	/** Lista de pólos da turma. */
	private List<Polo> polos = new ArrayList<Polo>();	
	/** Lista de informações dos pólos*/
	private List<InformacaoSobrePolo> infoPolos = null;
	/** Mensagens resultantes do processamento da matrícula em lote. */
	private Map<DiscenteGraduacao, List<String>> resultadoProcessamentoLote;

	/**
	 * Inicia o processo de matricular vários alunos em um ou vários componentes<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.ear/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculas() throws ArqException {
		MatriculaGraduacaoValidator.checaPapeisRegularEAD(getUsuarioLogado());
		prepareMovimento(SigaaListaComando.MATRICULAR_EM_LOTE);
		setOperacaoAtiva(SigaaListaComando.MATRICULAR_EM_LOTE.getId());
		obj = new LoteMatriculas();
		obj.setDiscentes(new ArrayList<DiscenteGraduacao>());
		obj.setTurmas(new ArrayList<Turma>());
		obj.setCurso(new Curso());
		
		infoPolos = null;
		
		return forward("/ead/matricula_lote/selecionar_discentes.jsp");
	}

	/**
	 * Busca os discentes que serão matriculados<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.ear/sigaa.war/ead/matricula_lote/selecionar_discentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarDiscentes() throws DAOException {
		if (!isOperacaoAtiva(SigaaListaComando.MATRICULAR_EM_LOTE.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		List<DiscenteGraduacao> listaDiscentes = dao.findDiscentesEADByStatusAnoPeriodoEntradaCurso(status, anoEntrada, periodoEntrada, obj.getCurso().getId());
		
		if (listaDiscentes != null && !listaDiscentes.isEmpty()) {
			obj.setDiscentes(listaDiscentes);
			// ordena por polo
			Collections.sort(obj.getDiscentes(), new Comparator<DiscenteGraduacao>() {

				public int compare(DiscenteGraduacao d1, DiscenteGraduacao d2) {
					if (d1.getPolo().getCidade().getId() == d2.getPolo().getCidade().getId()) {
						return 0;
					} else if (d1.getPolo().getCidade().getId() < d2.getPolo().getCidade().getId()) {
						return -1;
					} else if (d1.getPolo().getCidade().getId() > d2.getPolo().getCidade().getId()) {
						return 1;
					}
					return 0;
				}
				
			});
			addMensagemInformation("Foram achados " +  obj.getDiscentes().size() + " discentes.");
		} else {
			addMensagemErro("Nenhum discente foi localizado com estes criterios.");
			return null;
		}

		// so pra facilitar a vida do usuário;
		// geralmente buscam-se os componentes para o ano-período de entrada
		anoComponente = anoEntrada;
		periodoComponente = periodoEntrada;
		
		return forward("/ead/matricula_lote/selecionar_componentes.jsp");
	}

	/**
	 * Busca turmas abertas para o componentes desejado<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.ear/sigaa.war/ead/matricula_lote/selecionar_componentes.jsp</li>
	 * </ul>
	 * 
	 * @param event
	 * @throws LimiteResultadosException
	 * @throws DAOException
	 */
	public void buscarComponentes(ActionEvent event) throws LimiteResultadosException, DAOException {
		if (!isOperacaoAtiva(SigaaListaComando.MATRICULAR_EM_LOTE.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			cancelar();
			return;
		}
		ListaMensagens lista = new ListaMensagens();
		
		validarComponente(lista);
		
		if (!lista.isEmpty()) {
			addMensagens(lista);
			return ;
		}

		TurmaEADDao dao = getDAO(TurmaEADDao.class);
		turmasAbertas = dao.findAbertasByComponenteCurricular(
				null,
				codigoComponente,
				obj.getCurso(),
				null,
				anoComponente, 
				periodoComponente, 
				true, 
				NivelEnsino.GRADUACAO, 
				Turma.REGULAR);

		if (turmasAbertas == null || turmasAbertas.isEmpty()) {
			infoPolos = null;
			addMensagemErro("Não existe nenhuma turma para este componente: " + codigoComponente);
			return;
		}
		
		if (infoPolos == null)
			infoPolos = new ArrayList<InformacaoSobrePolo>();
		
		carregarPolos(dao);
		
		selecionarTurmasUteis();
		
		if (turmasAbertas == null || turmasAbertas.isEmpty()) {
			infoPolos = null;
			addMensagemErro("Não existe nenhuma turma para este componente (" + codigoComponente + ") nos polos desejados.");
			return;
		}		
		
 		extrairInformacaoPolo();
		
		for (Turma turma : turmasAbertas) {
			if (!obj.getTurmas().contains(turma)) {
				turma = dao.findByPrimaryKey(turma.getId(), Turma.class);
				// evita lazy exception
				turma.getDescricaoSemDocente();
				turma.setMatricular(true);
				obj.addTurma(turma);
			} else {
				addMensagemErro("Turma já adicionada.");
				return;
			}
		}
		
	}

	/**
	 * Popula o resto das informações sobre o polo.
	 * 1. numero de alunos por polo
	 * 2. se o polo possui o componente desejado
	 * 
	 */
	private void extrairInformacaoPolo() {
		for (InformacaoSobrePolo info : infoPolos) {
			calcularNumeroAlunos(info);
			checarSePoloContemComponente(info);
		}
	}

	/**
	 * Verifica se o polo contem o componente desejado
	 * 
	 * @param info
	 */
	private void checarSePoloContemComponente(InformacaoSobrePolo info) {
		for (Turma turma : turmasAbertas) {
			if (turma.getPolo() != null && turma.getPolo().getId() == info.getPolo().getId()
					|| turma.getCurso().equals(obj.getCurso())) {
				info.setPossuiComponente(true);
				break;
			}
		}
	}

	/**
	 * Numero de alunos que o polo possui
	 * 
	 * @param info
	 */
	private void calcularNumeroAlunos(InformacaoSobrePolo info) {
		int qtdDiscentesNoPolo = 0;
		for (DiscenteGraduacao d : obj.getDiscentes()) {
			if (d.getPolo().getId() == info.getPolo().getId()) {
				qtdDiscentesNoPolo++;
			}
		}
		info.setQtdAlunos(qtdDiscentesNoPolo);
	}

	/**
	 * Remove da colecao as turmas que nao tem discentes para matricula.
	 * Por exemplo: se so tem alunos de extremos e garanhus, nao tem utilidade
	 * uma turma de martins
	 * 
	 */
	private void selecionarTurmasUteis() {
		Collection<Turma> turmasUteis = new ArrayList<Turma>();
		for (InformacaoSobrePolo info : infoPolos) {
			for (Turma turma : turmasAbertas) {
				if (turma.getPolo() != null && info.getPolo().getId() == turma.getPolo().getId()
						|| turma.getCurso().equals(obj.getCurso())) {
					turmasUteis.add(turma);
				}
			}
		}
		
		turmasAbertas.retainAll(turmasUteis);
	}

	/**
	 * Entre os discentes selecionados, seleciona os pólos destes alunos
	 * e adiciona no objeto InformacaoSobrePolo
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void carregarPolos(TurmaEADDao dao) throws DAOException {
		for (DiscenteGraduacao dg : obj.getDiscentes()) {
			InformacaoSobrePolo info = new InformacaoSobrePolo();
			info.setPolo(dg.getPolo());
			
			if (!infoPolos.contains(info)) {
				infoPolos.add(info);
			}
		}
		// evitar lazy loading
		for (InformacaoSobrePolo info : infoPolos) {
			dao.initialize(info.getPolo());
		}
	}

	/**
	 * Valida a entrada do usuário
	 * 
	 * @param lista
	 */
	private void validarComponente(ListaMensagens lista) {
		ValidatorUtil.validateRequired(codigoComponente, "Código do Componente Curricular", lista );
		ValidatorUtil.validateRequired(anoComponente, "Ano", lista );
		ValidatorUtil.validateRequired(periodoComponente, "Periodo", lista );
	}

	/**
	 * Realiza a matricula<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.ear/sigaa.war/ead/matricula_lote/selecionar_componentes.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmarMatricula() throws ArqException {
		if (!isOperacaoAtiva(SigaaListaComando.MATRICULAR_EM_LOTE.getId())) {
			addMensagem(MensagensArquitetura.PROCEDIMENTO_PROCESSADO_ANTERIORMENTE);
			return cancelar();
		}
		List<LoteMatriculasDiscente> lotes = preValidacao();
		if (hasErrors())
			return null;
		MovimentoLoteMatricula mov = new MovimentoLoteMatricula();
		mov.setCodMovimento(SigaaListaComando.MATRICULAR_EM_LOTE);
		mov.setLoteMatriculas(lotes);
		
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			return cancelar();
		}
		
		addMensagemInformation("Matricula realizada com sucesso.");
		removeOperacaoAtiva();
		return forward("/ead/matricula_lote/resumo_processamento.jsp");
	}
	
	
	/**
	 * Faz algumas pre-validações e prepara as turmas que serão matriculadas
	 * @throws ArqException 
	 */
	private List<LoteMatriculasDiscente> preValidacao() throws ArqException {
		resultadoProcessamentoLote = new LinkedHashMap<DiscenteGraduacao, List<String>>();
		List<LoteMatriculasDiscente> loteFinal = new ArrayList<LoteMatriculasDiscente>();
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		
		// necessário para recuperar o calendário acadêmico
		dao.initialize(obj.getCurso());
		
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(obj.getCurso());		
		
		for (DiscenteGraduacao d : obj.getDiscentes()) {
			resultadoProcessamentoLote.put(d, new LinkedList<String>());
			
			List<Turma> turmas = new ArrayList<Turma>();

			// procura turma no mesmo polo do aluno
			for (Turma t : obj.getTurmas()) {
				if (t.getPolo() != null && t.getPolo().getId() == d.getPolo().getId()
						|| t.getCurso() != null && obj.getCurso().getId() == t.getCurso().getId()) {
					turmas.add(t);
				}
			}

			// se não tiver turma no polo deste aluno, passe pro próximo
			if (turmas == null || turmas.isEmpty()) {
				resultadoProcessamentoLote.get(d).add("Não há turmas para o pólo do discente.");
				continue;
			}			
			
			// Se o aluno já pagou a disciplina, deve ser removida a turma
			
			// Busca todos os componentes de turmas aprovadas (ou aproveitadas) antes
			Collection<ComponenteCurricular> componentesPagoseMatriculados =
				dao.findComponentesCurriculares(d, SituacaoMatricula.getSituacoesPagasEMatriculadas(), null);
			// criando uma coleção de todos os componentes (concluídos e os das turmas para matrícula)
			Collection<ComponenteCurricular> todosComponentes = new ArrayList<ComponenteCurricular>(componentesPagoseMatriculados);

			// coleção de turmas do semestre (as que já se matriculou e as que está tentando agora)
			Collection<Turma> turmasSemestre =  dao.findTurmasMatriculadas(d.getId(), true);

			for (Turma t : turmas) {
				todosComponentes.add(t.getDisciplina());
				turmasSemestre.add(t);
			}

			// Buscar solicitações de matrícula
			Collection<Turma> turmasSolicitadas = solicitacaoDao.findTurmasSolicitadasEmEspera(d,
					calendario.getAno(),
					calendario.getPeriodo(),
					true);
			
			for (Turma t : turmasSolicitadas) {
				todosComponentes.add(t.getDisciplina());
				turmasSemestre.add(t);
			}		
			
			List<Turma> turmasRemocao = new ArrayList<Turma>();
			
			ListaMensagens errosValidacao = new ListaMensagens(); 
			
			for (Turma turma : turmas) {
				MatriculaGraduacaoValidator.validarLimiteMaxMatriculas(turma, d, turmasSemestre,null, errosValidacao);
				
				if(d.getNivel() != NivelEnsino.TECNICO | turma.getDisciplina().getQtdMaximaMatriculas() == 1){
					//  verifica se o aluno já está matriculado em uma turma desse componente
					MatriculaGraduacaoValidator.validarMatriculadasSemestre(turma, turmasSemestre, null, errosValidacao, d.getDiscente());
				}
				
				MatriculaGraduacaoValidator.validarEquivalencia(d, turma, todosComponentes, errosValidacao);
				
				if (!errosValidacao.isEmpty()) {
					turmasRemocao.add(turma);
					for (MensagemAviso msg : errosValidacao.getMensagens()) {
						List<String> lista = resultadoProcessamentoLote.get(d);
						if (!lista.contains(msg.getMensagem()))
							lista.add(msg.getMensagem());
					}
					errosValidacao = new ListaMensagens();
				}
			}
			
			if (!turmasRemocao.isEmpty()) {
				turmas.removeAll(turmasRemocao);
			}
			
			// se não tiver turma no polo deste aluno, passe pro próximo
			if (turmas == null || turmas.isEmpty()) {
				resultadoProcessamentoLote.get(d).add("Não há turma para matricular o discente após as validações de matrícula.");
				continue;
			}
			
			Collection<ComponenteCurricular> componentesConcluidosDiscente = dao.findComponentesCurricularesConcluidos(d);
			
			LoteMatriculasDiscente lote = new LoteMatriculasDiscente();
			lote.setCurso(obj.getCurso());
			lote.setDiscente(d);
			lote.setTurmas(turmas);
			lote.setCalendario(calendario);
			lote.setTodosComponentes(todosComponentes);
			lote.setTurmasSemestre(turmasSemestre);
			lote.setComponentesConcluidosDiscente(componentesConcluidosDiscente);
			
			loteFinal.add(lote);
			resultadoProcessamentoLote.get(d).add("O discente foi matriculado na(s) turma(s) " + StringUtils.transformaEmLista(turmas));
			
		}
		return loteFinal;
	}

	public StatusDiscente getStatus() {
		return status;
	}

	public void setStatus(StatusDiscente status) {
		this.status = status;
	}

	public Integer getAnoEntrada() {
		return anoEntrada;
	}

	public void setAnoEntrada(Integer anoEntrada) {
		this.anoEntrada = anoEntrada;
	}

	public Integer getPeriodoEntrada() {
		return periodoEntrada;
	}

	public void setPeriodoEntrada(Integer periodoEntrada) {
		this.periodoEntrada = periodoEntrada;
	}

	public String getCodigoComponente() {
		return codigoComponente;
	}

	public void setCodigoComponente(String codigoComponente) {
		this.codigoComponente = codigoComponente;
	}

	public Integer getAnoComponente() {
		return anoComponente;
	}

	public void setAnoComponente(Integer anoComponente) {
		this.anoComponente = anoComponente;
	}

	public Integer getPeriodoComponente() {
		return periodoComponente;
	}

	public void setPeriodoComponente(Integer periodoComponente) {
		this.periodoComponente = periodoComponente;
	}

	public Collection<Turma> getTurmasAbertas() {
		return turmasAbertas;
	}

	public void setTurmasAbertas(Collection<Turma> turmasAbertas) {
		this.turmasAbertas = turmasAbertas;
	}

	public List<Polo> getPolos() {
		return polos;
	}

	public void setPolos(List<Polo> polos) {
		this.polos = polos;
	}

	/**
	 * Encapsula informações sobre a disciplina para ser mostrada na view
	 * 
	 * @author Henrique Andre
	 */
	public class InformacaoSobrePolo {
		/**Polo */
		private Polo polo;
		/** Quantidade de alunos do Polo*/
		private int qtdAlunos;
		/**Indica se o polo possui componente */
		private boolean isPossuiComponente;
		
		public Polo getPolo() {
			return polo;
		}
		public void setPolo(Polo polo) {
			this.polo = polo;
		}
		public int getQtdAlunos() {
			return qtdAlunos;
		}
		public void setQtdAlunos(int qtdAlunos) {
			this.qtdAlunos = qtdAlunos;
		}
		public boolean isPossuiComponente() {
			return isPossuiComponente;
		}
		public void setPossuiComponente(boolean isPossuiComponente) {
			this.isPossuiComponente = isPossuiComponente;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((polo == null) ? 0 : polo.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			final InformacaoSobrePolo other = (InformacaoSobrePolo) obj;
			if (polo == null) {
				if (other.polo != null)
					return false;
			} else if (!polo.equals(other.polo))
				return false;
			return true;
		}
		
	}

	public List<InformacaoSobrePolo> getInfoPolos() {
		return infoPolos;
	}

	public void setInfoPolos(List<InformacaoSobrePolo> infoPolos) {
		this.infoPolos = infoPolos;
	}

	public Map<DiscenteGraduacao, List<String>> getResultadoProcessamentoLote() {
		return resultadoProcessamentoLote;
	}
	
}
