/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '26/01/2007'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisCompulsoria;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisConvenio;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisEspecial;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisForaPrazo;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisRegular;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.checaPapeisRegularEAD;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarAlunoEspecial;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarLimiteMaximoCreditosSemestre;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarLimiteMinimoCreditosSemestre;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarMatriculaTurmasBloco;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarPeriodoEspecial;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarPeriodoRegular;
import static br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator.validarTurmaIndividualmente;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.dao.graduacao.ExtrapolarCreditoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ead.dominio.LoteMatriculasDiscente;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ExtrapolarCredito;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalcularStatusDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.CalcularTiposIntegralizacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.ensino.negocio.MatriculaComponenteHelper;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador respons�vel por cadastrar as matr�culas dos alunos de gradua��o
 * nas componentes curriculares selecionados
 *
 * @author Andre Dantas
 */
public class ProcessadorMatriculaGraduacao extends AbstractProcessador {

	/* (non-Javadoc)
	 * @see br.ufrn.arq.ejb.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		MovimentoMatriculaGraduacao matriculaMov = (MovimentoMatriculaGraduacao) mov;
		
		RestricoesMatricula restricoesRegular = RestricoesMatricula.getRestricoesRegular();
		RestricoesMatricula restricoesEspecial = RestricoesMatricula.getRestricoesAlunoEspecial();

		if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULAR_GRADUACAO)) {
			if (matriculaMov.isMatriculaConvenio()) {
				checaPapeisConvenio(matriculaMov);
				matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
				matriculaMov.setRestricoes(restricoesRegular);
				
				permissaoExtrapolarCredito(matriculaMov.getCalendarioAcademicoAtual(), matriculaMov.getDiscente(), matriculaMov, matriculaMov.getRestricoes());
				
			} else if (matriculaMov.isMatriculaEAD()) {
				checaPapeisRegularEAD(matriculaMov);
				matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
				matriculaMov.setRestricoes( matriculaMov.isMatriculaFerias() ? RestricoesMatricula.getRestricoesEADFerias() : RestricoesMatricula.getRestricoesEAD());
			} else if (matriculaMov.getDiscente().getTipo() == Discente.REGULAR) {
				if(matriculaMov.getDiscente().isResidencia())
					matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
				else if (isEmpty(matriculaMov.getSituacao()))
					matriculaMov.setSituacao(SituacaoMatricula.EM_ESPERA);
				if (matriculaMov.isMatriculaFerias()) {
					matriculaMov.setRestricoes(RestricoesMatricula.getRestricoesTurmaFerias());
				} else {
					DiscenteHelper.setarPermissaoExtrapolarCredito(matriculaMov.getDiscente(), restricoesRegular, matriculaMov.getCalendarioAcademicoAtual());
					if (!matriculaMov.isMatriculaIngressante())
						validarPeriodoRegular(matriculaMov);
					matriculaMov.setRestricoes(restricoesRegular);
					validarTurmasBloco(matriculaMov);
				}
				checaPapeisRegular(matriculaMov);
				if( matriculaMov.getDiscente().isStricto() ) /**  SE FOR DISCENTE STRICTO VAI DIRETO PARA MATRICULADO */
					matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
			} else if (matriculaMov.getDiscente().getTipo() == Discente.ESPECIAL) {
				validarPeriodoEspecial(matriculaMov);
				checaPapeisEspecial(matriculaMov);
				
				if( matriculaMov.getDiscente().isStricto() ) {
					matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
				} else {
					matriculaMov.setSituacao(SituacaoMatricula.EM_ESPERA);
					restricoesEspecial.setCapacidadeTurma(false);
				}
				matriculaMov.setRestricoes(restricoesEspecial);
			}

		} else if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULA_FORA_PRAZO)) {

			checaPapeisForaPrazo(matriculaMov);
			if (matriculaMov.getDiscente().getTipo() == Discente.REGULAR) {
				matriculaMov.setRestricoes(restricoesRegular);
			} else if (matriculaMov.getDiscente().getTipo() == Discente.ESPECIAL) {
				matriculaMov.setRestricoes(restricoesEspecial);
			}
		} else if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULA_COMPULSORIA)) {

			// no caso de matricula compuls�ria as restri��es e a situa��o s�o escolhidas pelo usu�rio
			checaPapeisCompulsoria(matriculaMov);
			
			if( matriculaMov.getTurmas()!= null &&	matriculaMov.getTurmas().iterator().next().getPeriodo() > 2 ){
				matriculaMov.getRestricoes().setLimiteMinCreditosSemestre(false);
			}	
		
		} else if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULA_TURMA_FERIAS_ENSINO_INDIVIDUAL)) {

			//validarMatricula(matriculaMov);
			//checaPapeisEspecial(matriculaMov);
			matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
			//matriculaMov.setRestricoes(RestricoesMatricula.getRestricoesAlunoEspecial());

		} else if (mov.getCodMovimento().equals(SigaaListaComando.MATRICULAR_EXTRAORDINARIA)) {
			matriculaMov.setSituacao(SituacaoMatricula.MATRICULADO);
			DiscenteHelper.setarPermissaoExtrapolarCredito(matriculaMov.getDiscente(), restricoesRegular, matriculaMov.getCalendarioAcademicoAtual());
			restricoesRegular.setLimiteMinCreditosSemestre(false);
			matriculaMov.setRestricoes(restricoesRegular);
		}


		// validar restri��es
		matriculaMov.getRestricoes().setPermiteDuplicidadeCasoConteudoVariavel(matriculaMov.isPermiteDuplicidadeCasoConteudoVariavel());
		validate(matriculaMov);
		
		
		Collection<Turma> turmasDoPrograma = new ArrayList<Turma>();
		Collection<Turma> turmasDeOutroPrograma = new ArrayList<Turma>();
		if(matriculaMov.isMatriculandoTurmasOutrosProgramas()) {
			Discente discente = getGenericDAO(matriculaMov).findByPrimaryKey(matriculaMov.getDiscente().getId(), Discente.class);
			for(Turma t : matriculaMov.getTurmas()) {
				//O coordenador pode selecionar turmas de diferentes programas, da� important�ssimo ter a unidade preenchida corretamente.
				ComponenteCurricular disciplina = getGenericDAO(matriculaMov).findAndFetch(t.getDisciplina().getId(), ComponenteCurricular.class, "unidade");							
				if(disciplina.getUnidade().getGestoraAcademica().getId() == discente.getCurso().getUnidade().getGestoraAcademica().getId()) {
					turmasDoPrograma.add(t);
				} else {
					turmasDeOutroPrograma.add(t);
				}
			}
			matriculaMov.setTurmas(turmasDoPrograma);
			Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
			for(Turma t : turmasDeOutroPrograma) {
				SolicitacaoMatricula solicitacao = new SolicitacaoMatricula();
				solicitacao.setAno(t.getAno());
				solicitacao.setPeriodo(t.getPeriodo());
				solicitacao.setDataCadastro(new Date());
				solicitacao.setDataSolicitacao(new Date());			
				solicitacao.setDiscente((Discente)matriculaMov.getDiscente().getDiscente());			
				solicitacao.setRegistroCadastro(matriculaMov.getUsuarioLogado().getRegistroEntrada());
				solicitacao.setRegistroEntrada(matriculaMov.getUsuarioLogado().getRegistroEntrada());
				solicitacao.setRematricula(false);
				solicitacao.setAnulado(false);
				solicitacao.setStatus(SolicitacaoMatricula.AGUARDANDO_OUTRO_PROGRAMA);			
				solicitacao.setTurma(t);
				solicitacoes.add(solicitacao);
			}
			
			
			SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, matriculaMov);
			for(SolicitacaoMatricula s : solicitacoes) {
				solicitacaoDao.create(s);
			}
			
			matriculaMov.setTurmas(turmasDoPrograma);
			
		}		
		// efetivar registro das matr�culas
		matricular(matriculaMov);		

		/*
		 * Verificar se a turma j� possui notas cadastradas (poss�vel situa��o numa matricula compuls�ria).
		 * Caso j� existam avalia��es cadastradas para essa turma que o aluno t� 'entrando' diretamente como
		 * matriculado, devem ser criadas as mesmas avalia��es para esse aluno com as notas ZERO
		 */
		if (matriculaMov.getSituacao().equals(SituacaoMatricula.MATRICULADO) && (mov.getCodMovimento().equals(SigaaListaComando.MATRICULA_FORA_PRAZO) || mov.getCodMovimento().equals(SigaaListaComando.MATRICULA_COMPULSORIA))) {
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
			TurmaDao turmaDao = getDAO(TurmaDao.class, mov);
			AvaliacaoDao avaliacaoDao = getDAO(AvaliacaoDao.class, mov);
			try {
				for (Turma turma : matriculaMov.getTurmas()) {
					MatriculaComponente matricula = matriculaDao.findMatriculadoByDiscenteTurma(matriculaMov.getDiscente(), turma);
					MatriculaComponenteHelper.cadastrarAvaliacoesMatriculaCompulsoria(matricula, TurmaUtil.getNumUnidadesDisciplina(turma), turmaDao, avaliacaoDao);
				}
			} finally {
				matriculaDao.close();
				turmaDao.close();
				avaliacaoDao.close();
			}
		}

		
		int crExtraPreCalculo = 0;
		if (matriculaMov.getDiscente().isGraduacao() && matriculaMov.getDiscente().isRegular()) {
			DiscenteGraduacao dg = (DiscenteGraduacao)matriculaMov.getDiscente();
			
			if (isNotEmpty(dg.getCrExtraIntegralizados()))
				crExtraPreCalculo = dg.getCrExtraIntegralizados();
		}

		if (matriculaMov.getDiscente().getStatus() != StatusDiscente.CADASTRADO) {
			// realizar c�lculos do discente
			if (matriculaMov.getDiscente().isGraduacao() && matriculaMov.isAtualizarStatusDiscenteETiposIntegralizacao() 
					&& matriculaMov.getDiscente().isRegular() ){
				
				DiscenteGraduacao dg = (DiscenteGraduacao)matriculaMov.getDiscente();
				
				CalculosDiscenteChainNode<DiscenteGraduacao> calculos = new CalculosDiscenteChainNode<DiscenteGraduacao>();
				calculos.setNext(new CalcularTiposIntegralizacao())
					.setNext(new CalcularStatusDiscente());
				
				calculos.executar(dg, mov, false);
			} else {
				GenericDAO dao = getGenericDAO(matriculaMov);
				dao.updateField(DiscenteGraduacao.class, matriculaMov.getDiscente().getId(), "ultimaAtualizacaoTotais", null);
			}
		}

		RestricoesMatricula restricoes = matriculaMov.getRestricoes();
		// verifica se deve ser testado o limite m�ximo de cr�ditos de componentes de outros curr�culos
		// ser� excedido ao matricular-se nessas turmas
		// �nica restri��o a ser considerada depois dos c�lculos do discente
		
		if(matriculaMov.isMatriculandoTurmasOutrosProgramas()) {
			matriculaMov.getTurmas().addAll(turmasDeOutroPrograma);
		}		
		
		if (matriculaMov.getDiscente().isGraduacao() 
				&& matriculaMov.getDiscente().isRegular()
				&& restricoes.isLimiteCreditosExtra() ) {
			
			ListaMensagens erros = new ListaMensagens();
			validarLimiteCreditosExtra(matriculaMov, erros);
			
			checkValidation(erros);
		}
		return matriculaMov;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoMatriculaGraduacao matriculaMov = (MovimentoMatriculaGraduacao) mov;
		validaRestricoes(matriculaMov);
	}
	
	/** Valida o limite de cr�ditos extras para a solicita��o de matr�cula.
	 * @param mov
	 * @param erros
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws DAOException
	 */
	private void validarLimiteCreditosExtra(MovimentoMatriculaGraduacao mov, ListaMensagens erros) throws ArqException, NegocioException, DAOException {

		DiscenteGraduacao discente = (DiscenteGraduacao) mov.getDiscente();
		CalendarioAcademico cal = mov.getCalendarioAcademicoAtual();
		TurmaDao dao = getDAO(TurmaDao.class, mov);
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class, mov);
		DiscenteGraduacaoDao dgDao = getDAO(DiscenteGraduacaoDao.class, mov);
		
		try {
			// Preparar matr�culas a partir das turmas selecionadas
			Collection<MatriculaComponente> matriculas = new ArrayList<MatriculaComponente>();
			for (Turma turma : mov.getTurmas()) {
				
				MatriculaComponente matricula = new MatriculaComponente();
				matricula.setId( turma.getId() );
				matricula.setDiscente(discente.getDiscente());
				matricula.setTurma(turma);
				
				ComponenteCurricular componente = dao.refresh(turma.getDisciplina());
				if (!componente.isSubUnidade()) {
					matricula.setComponente(componente);
				} else {
					matricula.setComponente(componente.getBlocoSubUnidade());
				}
				
				if (!matriculas.contains(matricula)) {
					matriculas.add(matricula);
				}
			}
			
			// Calcular integraliza��es dos componentes solicitados e matriculados
			IntegralizacoesHelper.analisarTipoIntegralizacao(discente, matriculas, mov);
			
			// Validar total de cr�ditos extra-curriculares
			HashMap<Integer, Integer> mapa = new HashMap<Integer, Integer>();
			int creditosExtra = DiscenteHelper.getTotalCreditosExtra(matriculas, mapa);
			
			if (creditosExtra > 0) {
				dgDao.calcularIntegralizacaoExtras(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas());
				short totalExtraIntegralizados = discente.getCrExtraIntegralizados() != null ? discente.getCrExtraIntegralizados() : 0;
				
				discente.setCrExtraIntegralizados( totalExtraIntegralizados );
				MatriculaGraduacaoValidator.validarLimiteCreditosExtra(discente, erros, mov.getTurmas());
				
				discente.setCrExtraIntegralizados(totalExtraIntegralizados);
			}
		} finally {
			dao.close();
			matriculaDao.close();
			dgDao.close();
		}
	}

	/**
	 * Permite extrapolar os cr�ditos m�ximos
	 * 
	 * @param cal
	 * @param discente
	 * @param mov
	 * @param restricoes
	 * @return
	 * @throws DAOException
	 */
	protected void permissaoExtrapolarCredito(CalendarioAcademico cal, DiscenteAdapter discente, Movimento mov, RestricoesMatricula restricoes) throws DAOException {
		
		if (!discente.isGraduacao()){
			return ;
		}
		
		ExtrapolarCreditoDao ecDao = getDAO(ExtrapolarCreditoDao.class, mov);
		try {
			ExtrapolarCredito extrapolarCredito = ecDao.findPermissaoAtivo(discente.getDiscente(), cal.getAno(), cal.getPeriodo() );
			
			if (extrapolarCredito != null) {
				// caso a permiss�o seja no modelo antigo, sem valor m�ximo:
				if (extrapolarCredito.getCrMaximoExtrapolado() == null) {
					if (extrapolarCredito.isExtrapolarMaximo())
						restricoes.setLimiteMaxCreditosSemestre(false);
					else
						restricoes.setLimiteMinCreditosSemestre(false);
				} else {
					// modelo novo, com valor m�ximo e m�nimo
					restricoes.setValorMaximoCreditos(extrapolarCredito.getCrMaximoExtrapolado());
					restricoes.setValorMinimoCreditos(extrapolarCredito.getCrMinimoExtrapolado());
				}
			}
		} finally {
			ecDao.close();
		}
		
	}	
	
	/**
	 * Verifica quais restri��es devem ser analisadas e lan�ar� exce��es caso alguma restri��o n�o seja obedecida
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void validaRestricoes(MovimentoMatriculaGraduacao mov) throws NegocioException, ArqException {

		if (mov.getTurmas() == null || mov.getTurmas().isEmpty())
			throw new NegocioException("� necess�rio selecionar ao menos uma turma.");

		if (mov.getDiscente() == null)
			throw new NegocioException("� necess�rio definir o discente a matricular.");

		ListaMensagens erros = new ListaMensagens();

		// validar se o per�odo atual � no m�nimo o per�odo de ingresso do aluno
//		MatriculaGraduacaoValidator.validarAlunoCadastrado(mov.getDiscente(), mov.getCalendarioAcademicoAtual().getAno(), mov.getCalendarioAcademicoAtual().getPeriodo(), erros);

		RestricoesMatricula restricoes = mov.getRestricoes();

		// Discente em que se tentar� matricular nas turmas
		DiscenteAdapter discente = mov.getDiscente();

		// cria��o dos DAOs a serem usados nas valida��es abaixo
		DiscenteDao dao = getDAO(DiscenteDao.class, mov);
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class, mov);
		
		try {
			// Turmas em que se tentar� matricular o aluno
	
			Collection<ComponenteCurricular> componentesPagoseMatriculados = null;
			Collection<ComponenteCurricular> todosComponentes = new HashSet<ComponenteCurricular>();
			Collection<Turma> turmasSemestre = new HashSet<Turma>();
			Collection<Turma> turmasSolicitadas= null;
			LoteMatriculasDiscente lote = null;
			
			
			if( mov.getLoteMatriculasDiscente() == null ){
				// Busca todos os componentes de turmas aprovadas (ou aproveitadas) antes
				componentesPagoseMatriculados =
					dao.findComponentesCurriculares(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas(), null);
				// criando uma cole��o de todos os componentes (conclu�dos e os das turmas para matr�cula)
				todosComponentes.addAll(componentesPagoseMatriculados);
		
				// cole��o de turmas do semestre (as que j� se matriculou e as que est� tentando agora)
				Collection<Turma> findTurmasMatriculadas = dao.findTurmasMatriculadas(discente.getId());
				if(findTurmasMatriculadas != null && findTurmasMatriculadas.size() > 0)
					turmasSemestre.addAll(findTurmasMatriculadas);
		
				turmasSolicitadas = solicitacaoDao.findTurmasSolicitadasEmEspera(discente,
						mov.getCalendarioAcademicoAtual().getAno(),
						mov.getCalendarioAcademicoAtual().getPeriodo());
			
				for (Turma t : mov.getTurmas()) {
					todosComponentes.add(t.getDisciplina());
					turmasSemestre.add(t);
				}
	
				// Buscar solicita��es de matr�cula
				//Collection<Turma> turmasSolicitadas = solicitacaoDao.findTurmasSolicitadasEmEspera(discente,
				//		mov.getCalendarioAcademicoAtual().getAno(),
				//		mov.getCalendarioAcademicoAtual().getPeriodo());
				
				for (Turma t : turmasSolicitadas) {
					todosComponentes.add(t.getDisciplina());
					turmasSemestre.add(t);
				}
				
			}else{
				lote = mov.getLoteMatriculasDiscente();
				//componentesPagoseMatriculados =	lote.getComponentesPagoseMatriculado();
				// criando uma cole��o de todos os componentes (conclu�dos e os das turmas para matr�cula)
				//todosComponentes = new ArrayList<ComponenteCurricular>(componentesPagoseMatriculados);
				todosComponentes = lote.getTodosComponentes();
		
				// cole��o de turmas do semestre (as que j� se matriculou e as que est� tentando agora)
				turmasSemestre =  lote.getTurmasSemestre();
		
				//turmasSolicitadas = lote.getTurmasSolicitadas();
			}
			
			
	
			// restri��es a serem analisadas em cada turma que se est� tentando matricular
			for (Turma turma : mov.getTurmas()) {
				if (turma.isMatricular()) {
					ListaMensagens msgs = new ListaMensagens();
					if( mov.getLoteMatriculasDiscente() == null ){
						msgs = validarTurmaIndividualmente(discente, turma, turmasSemestre, todosComponentes, restricoes);
					}else{
						lote = mov.getLoteMatriculasDiscente();
						msgs = validarTurmaIndividualmente(discente, turma, turmasSemestre, todosComponentes, restricoes, lote.getComponentesConcluidosDiscente(), mov.getCalendarioAcademicoAtual());
					}
					// TODO: validar se j� existe solicita��es de ensino individual para essas turmas
					erros.addAll(msgs.getMensagens());
				}
			}
	
			// restri��es gerais que levam em considera��o o conjunto de turmas que se est� tentado matricular
	
			// verifica se devem ser testadas as restri��es para matr�cula de aluno especial
			if (restricoes.isAlunoEspecial() && !NivelEnsino.isAlgumNivelStricto( discente.getNivel() ) ) {
				long totalPeriodoCursados = dao.findQtdPeriodosCursados(discente.getId(), mov.getCalendarioAcademicoAtual());
				int totalMatriculadosNoSemestre = turmasSemestre.size();
				validarAlunoEspecial(discente, totalMatriculadosNoSemestre, (int)totalPeriodoCursados, erros);
			}
	
			// verifica se deve ser testado os limites m�ximo e m�nimo de cr�ditos no semestre de acordo com o curr�culo do aluno
			Collection<Turma> turmasDoSemestre = mov.getTurmas();
			CalendarioAcademico cal = mov.getCalendarioAcademicoAtual();
			// no modelo antigo de verifica��o de cr�ditos
			if (restricoes.getValorMaximoCreditos() == null && restricoes.getValorMinimoCreditos() == null) {
				if (restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
					validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal);
				}
				if ( restricoes.getLimiteMinCreditosSemestre() != null &&  restricoes.getLimiteMinCreditosSemestre()) {
					validarLimiteMinimoCreditosSemestre( discente, turmasSemestre, null, cal);
				}
			} else {
				// no modelo novo de verifica��o de cr�ditos
				if (restricoes.getValorMaximoCreditos() != null && restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
					validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal, restricoes.getValorMaximoCreditos());
				}
				if (restricoes.getValorMinimoCreditos() != null && restricoes.getLimiteMinCreditosSemestre() && discente.isGraduacao()) {
					validarLimiteMinimoCreditosSemestre(discente, turmasDoSemestre, null, cal, restricoes.getValorMinimoCreditos());
				}
			}
	
			checkValidation(erros);
		} finally {
			dao.close();
			solicitacaoDao.close();
		}
	}
	
	/**
	 * Persiste as matr�culas do discente nas turmas passados no movimento.
	 * tamb�m � passado a situa��o da matr�cula em que deve ser cadastrada a matr�cula
	 * @param matriculaMov
	 * @return
	 * @throws ArqException
	 */
	private Object matricular(MovimentoMatriculaGraduacao matriculaMov) throws ArqException  {
		DiscenteAdapter discente = matriculaMov.getDiscente();

		DiscenteDao dao = getDAO(DiscenteDao.class, matriculaMov);
		TurmaDao turmaDao = getDAO(TurmaDao.class, matriculaMov);

		try {
			// Buscar as turmas j� matriculadas neste semestre para o discente
			Collection<Turma> turmasMatriculadas = dao.findTurmasMatriculadas(discente.getId(), true);

			// Gravar as matriculas
			for (Turma turma : matriculaMov.getTurmas()) {
				if (turma.isMatricular() && !turmasMatriculadas.contains(turma)) {
					persistirMatricula(matriculaMov, turma);
					if (turma.getDisciplina().isBloco()) {
						Collection<Turma> turmasSubunidades = turmaDao.findTurmasSubUnidadesByBloco(turma);
						if (turmasSubunidades != null) {
							for (Turma turmaSubUnidade : turmasSubunidades) {
								persistirMatricula(matriculaMov, turmaSubUnidade);
							}
						}
					}
				}
			}

			// Atualizar as solicita��es de matricula que o aluno havia realizado
//			solicitacaoDao.atualizarSolicitacoes(discente.getId());
			return matriculaMov;
		} catch (Exception e) {
			throw new ArqException(e);
		} finally {
			turmaDao.close();
			dao.close();
		}
	}


	/**
	 * Persiste a matr�cula do discente na turma passada como argumento.
	 * @param matriculaMov
	 * @param turma
	 * @throws ArqException
	 */
	private void persistirMatricula(MovimentoMatriculaGraduacao matriculaMov, Turma turma) throws ArqException {
		GenericDAO dao = getGenericDAO(matriculaMov);
		try {
			DiscenteAdapter discente = matriculaMov.getDiscente();
			SituacaoMatricula situacao = matriculaMov.getSituacao();
	
			MatriculaComponente matricula = new MatriculaComponente();
			matricula.setDataCadastro(new Date());
			matricula.setRegistroEntrada(matriculaMov.getUsuarioLogado().getRegistroEntrada());
			matricula.setDiscente(discente.getDiscente());
			matricula.setTurma(turma);
			matricula.setAno((short)turma.getAno());
			matricula.setPeriodo((byte)turma.getPeriodo());
			matricula.setComponente(turma.getDisciplina());
			matricula.setDetalhesComponente(turma.getDisciplina().getDetalhes());
			matricula.setRecuperacao(null);
			matricula.setSituacaoMatricula(situacao);
			if( discente.isStricto() ){
				matricula.setAnoInicio( turma.getAno());
				matricula.setMes( CalendarUtils.getMesByData(turma.getDataInicio())+1 );
			}
	
			// no caso de matr�cula compuls�ria s�o identificadas as restri��es usadas
			if (matriculaMov.getCodMovimento().equals(SigaaListaComando.MATRICULA_COMPULSORIA)) {
				dao.create(matriculaMov.getRestricoes());
				matricula.setRestricoes(matriculaMov.getRestricoes());
			}
	
			if (situacao.equals(SituacaoMatricula.MATRICULADO) && discente.getStatus() == StatusDiscente.CADASTRADO) {
				// passar status do discente para ATIVO se for CADASTRADO
				ParametrosGestoraAcademicaDao pdao = getDAO(ParametrosGestoraAcademicaDao.class, matriculaMov);
				try {
					DiscenteHelper.alterarStatusDiscente(discente, StatusDiscente.ATIVO, matriculaMov, pdao);
				} finally {
					pdao.close();
				}
			}
	
			dao.create(matricula);
			matriculaMov.setMatriculaGerada(matricula);
		} finally {
			dao.close();
		}
	}

	/** Valida a solicita��o de matr�cula.
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void validarTurmasBloco(MovimentoMatriculaGraduacao mov) throws ArqException, NegocioException {
		ListaMensagens erros = new ListaMensagens();

		if (mov.getDiscente().isGraduacao()) {
			
			// verifica se o aluno est� matriculado todas as sub-unidades de algum bloco no qual tentou realizar matricula
			validarMatriculaTurmasBloco(mov.getTurmas(), erros);
		}

		checkValidation(erros);
	}

}

