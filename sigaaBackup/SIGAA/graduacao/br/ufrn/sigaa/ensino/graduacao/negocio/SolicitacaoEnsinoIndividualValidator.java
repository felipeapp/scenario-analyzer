/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Oct 8, 2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;

/**
 * Classe que cont�m as valida��es realizadas na solicita��o de ensino individual e de f�rias.
 * @author Victor Hugo
 */
public class SolicitacaoEnsinoIndividualValidator {

	/**
	 * Valida a solicita��o de turma realizada pelo aluno
	 * @param solicitacao
	 * @param lista
	 * @throws ArqException
	 */
	public static void validarSolicitacao( SolicitacaoEnsinoIndividual solicitacao, ListaMensagens lista ) throws ArqException{

		MatriculaComponenteDao dao = null;
		SolicitacaoEnsinoIndividualDao solicitacaoDao = null;

		SolicitacaoMatriculaDao solicitacaoMatriculaDao = null;
		ComponenteCurricularDao ccDao = null;
		
		try{

			//efetua as valida��es b�sicas
			lista.addAll( solicitacao.validate().getMensagens() );
			
			// Abrindo conex�es
			dao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class);
			solicitacaoMatriculaDao = DAOFactory.getInstance().getDAO(SolicitacaoMatriculaDao.class, null, dao.getSession());
			ccDao = DAOFactory.getInstance().getDAO(ComponenteCurricularDao.class, null, dao.getSession());
			solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoEnsinoIndividualDao.class, null, dao.getSession());
			
			DiscenteGraduacao discente = dao.findByPrimaryKey(solicitacao.getDiscente().getId(), DiscenteGraduacao.class);
			ComponenteCurricular disciplina = dao.findByPrimaryKey(solicitacao.getComponente().getId(), ComponenteCurricular.class);
			solicitacao.setComponente(disciplina);
			solicitacao.setDiscente( discente );

			if( solicitacao.isEnsinoIndividual() ) /** valida��es especificas de solicita��o de turma de ensino individualizado */
				validarSolicitacaoEnsinoIndividual(solicitacao, dao, lista);
			else if( solicitacao.isFerias() ) /** valida��es especificas de solicita��o de turma de ferias*/
				validarSolicitacaoTurmaFerias(solicitacao, dao, lista);
			else
				throw new ArqException("O tipo da solicita��o n�o p�de ser identificado.");

			Collection<MatriculaComponente> componentes = new ArrayList<MatriculaComponente>();

			/**
			 * valida��es comuns tanto a solicita��o de ensino individualizado como a turma de ferias
			 */
			if( disciplina.isBloco() )
				lista.addErro("N�o � poss�vel solicitar "+(solicitacao.isFerias() ? "turma de f�rias" : "ensino individual")+" para componente de bloco.");

			//valida se o aluno j� pagou a disciplina pretendida
			if( solicitacao.isEnsinoIndividual() ){
				componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.getSituacoesPagasEMatriculadasArray());

				if (solicitacao.getEquivalente() != null)
					componentes.addAll(dao.findByDiscenteEDisciplina(discente, solicitacao.getEquivalente(), SituacaoMatricula.getSituacoesPagasEMatriculadasArray()));
				
				if( componentes != null && !componentes.isEmpty() )
					lista.addErro("Voc� j� pagou ou est� pagando a disciplina pretendida.");
				

			} else if( solicitacao.isFerias() ){
				componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.getSituacoesPagasArray());
				if( componentes != null && !componentes.isEmpty() )
					lista.addErro("Voc� j� pagou a disciplina pretendida.");
			}
			
			if (solicitacao.getEquivalente() == null)
				validaOfertaComponenteEI(solicitacao.getAno(), solicitacao.getPeriodo(), solicitacao.getComponente(), lista, dao, solicitacaoMatriculaDao, ccDao, discente);
			else
				validaOfertaComponenteEI(solicitacao.getAno(), solicitacao.getPeriodo(), solicitacao.getEquivalente(), lista, dao, solicitacaoMatriculaDao, ccDao, discente);
				
			validaDuplicidadeSolicitacao(solicitacao, lista, solicitacaoDao, discente);

			Collection<SolicitacaoEnsinoIndividual> solicitacoesFerias = 
				solicitacaoDao.findByDiscenteComponenteAnoPeriodoSituacao(solicitacao.getDiscente().getId(), 
						Turma.FERIAS, null, solicitacao.getAno(), solicitacao.getPeriodo(), 
						SolicitacaoEnsinoIndividual.ATENDIDA, SolicitacaoEnsinoIndividual.SOLICITADA);
			if( !isEmpty( solicitacoesFerias ) ){
				lista.addErro("S� � permitido cursar uma turma de f�rias por per�odo.");
			}
			
		} finally{
			if (dao != null)
				dao.close();
			
			if (solicitacaoDao != null)
				solicitacaoDao.close();
			
			if (solicitacaoMatriculaDao != null)
				solicitacaoMatriculaDao.close();
			
			if (ccDao != null)
				ccDao.close();
			
		}
	}

	private static void validaDuplicidadeSolicitacao(SolicitacaoEnsinoIndividual solicitacao,	ListaMensagens lista, SolicitacaoEnsinoIndividualDao solicitacaoDao, DiscenteGraduacao discente) throws ArqException {
		Collection<SolicitacaoEnsinoIndividual> solicitacoes = solicitacaoDao.findByDiscenteComponenteAnoPeriodoSituacao(discente.getId(), solicitacao.getTipo(), null, solicitacao.getAno(), solicitacao.getPeriodo(), SolicitacaoEnsinoIndividual.ATENDIDA, SolicitacaoEnsinoIndividual.SOLICITADA, SolicitacaoEnsinoIndividual.NEGADA, SolicitacaoEnsinoIndividual.CANCELADA_POR_MATRICULA);
		if( !isEmpty( solicitacoes ) ){
			for( SolicitacaoEnsinoIndividual s : solicitacoes ){
				
				// Valida se o aluno esta tentando solicitar o mesmo componente
				if( s.getComponente().getId() == solicitacao.getComponente().getId() ){
					lista.addErro("Voc� j� solicitou turma de " + s.getTipoString() + " para este componente curricular neste per�odo.");
					return;
				}
				
				// A � obrigatoria
				// B � equivalente a A
				// C � equivalente a A
				// Valida quando o aluno j� solicitou B e agora tenta solicitar C
				if( s.getEquivalente() != null && solicitacao.getEquivalente() != null && s.getEquivalente().getId() == solicitacao.getEquivalente().getId() ){
					lista.addErro("Voc� j� solicitou turma de " + s.getTipoString() + " para componente curricular equivalente a " + solicitacao.getEquivalente().getCodigoNome() + " neste per�odo.");
					return;
				}				
				
			}
			
			if (solicitacao.isComponenteObrigatorio()) {
				
				String strEquivalencia = solicitacao.getComponente().getEquivalencia();
				
				if (strEquivalencia == null)
					return;

				// A � obrigatoria
				// B � equivalente a A
				// Valida o caso onde o aluno solcitou B (equivalente) e agora tenta solicitar A (abrigatoria)
				for( SolicitacaoEnsinoIndividual s : solicitacoes ){
					if( ExpressaoUtil.eval(strEquivalencia, s.getComponente()) ){
						lista.addErro("Voc� j� solicitou turma equivalente de " + s.getTipoString() + " para este componente curricular neste per�odo atrav�s de " + s.getComponente().getCodigoNome());
						return;
					}
				}			
			} else {
				// A � obrigatoria
				// B � equivalente a A
				// Valida o caso onde o aluno solcitou A (obrigatoria) e agora tenta solicitar B (equilvante a abrigatoria)
				for( SolicitacaoEnsinoIndividual s : solicitacoes ){
					if( s.getComponente().getId() == solicitacao.getEquivalente().getId() ){
						lista.addErro("Voc� j� solicitou turma equivalente de " + s.getTipoString() + " para este componente curricular neste per�odo atrav�s de " + s.getComponente().getCodigoNome());						
						return;
					}
				}
			}
		}
	}

	private static void validaOfertaComponenteEI(int ano, int periodo, ComponenteCurricular componente, ListaMensagens lista, MatriculaComponenteDao dao, SolicitacaoMatriculaDao solicitacaoMatriculaDao, ComponenteCurricularDao ccDao, DiscenteGraduacao discente)
			throws ArqException {
		Collection<Turma> turmasSolicitadas = new ArrayList<Turma>();
		turmasSolicitadas.addAll( solicitacaoMatriculaDao.findTurmasSolicitadasEmEspera(discente, ano, periodo) );
		turmasSolicitadas.addAll( solicitacaoMatriculaDao.findTurmasMatriculadasByDiscente(discente, ano, periodo, SituacaoMatricula.getSituacoesAtivas()) );
		
		String equivalencia = ccDao.findEquivalenciaPorDiscente(componente.getId(), discente.getId()).getEquivalencia();
		Collection<ComponenteCurricular> componentesEquivalentes = null;
		if(!isEmpty(equivalencia))
			componentesEquivalentes = ExpressaoUtil.expressaoToComponentes(equivalencia, discente.getId());
		
		//valida se o aluo tem solicita��o de turmas regulares iguais ou equivalentes a desejava para ensino individual
		for (Turma turma : turmasSolicitadas) {
			if(turma.getDisciplina().getId() == componente.getId()) {
				lista.addErro("N�o foi poss�vel solicitar ensino individualizado para este componente curricular pois existe, em seu plano de matr�culas, uma solicita��o em turma regular para ele.");
				break;
			}
			if (componentesEquivalentes != null) {
				for (ComponenteCurricular cc : componentesEquivalentes) {
					if(cc.getId() == turma.getDisciplina().getId()) {
						cc = dao.refresh(cc);
						lista.addErro("N�o foi poss�vel solicitar ensino individualizado para este componente curricular pois existe, em seu plano de matr�culas, uma solicita��o em turma regular para o componente "+cc.getCodigo()+", equivalente a ele.");
						break;
					}
				}
			}
		}
	}

	/**
	 * Valida a solicita��o de ensino individual realizada por um discente
	 * @param solicitacao
	 * @param erros lista de mensagens de erro de valida��o
	 * @throws ArqException
	 */
	public static void validarSolicitacaoEnsinoIndividual( SolicitacaoEnsinoIndividual solicitacao, MatriculaComponenteDao dao, ListaMensagens lista ) throws ArqException{

		DiscenteGraduacao discente = solicitacao.getDiscente();
		ComponenteCurricular disciplina = solicitacao.getComponente();
		int ano = solicitacao.getAno();
		int periodo = solicitacao.getPeriodo();
		
		validarSolicitacaoEnsinoIndividual(discente, disciplina, ano, periodo, dao, lista);
		
		
		
		if (solicitacao.getEquivalente() != null)
			validarSolicitacaoEnsinoIndividual(discente, solicitacao.getEquivalente(), ano, periodo, dao, lista);
	}
	
	/**
	 * Valida a solicita��o de ensino individual realizada por um discente
	 * @param solicitacao
	 * @param erros lista de mensagens de erro de valida��o
	 * @throws ArqException
	 */
	private static void validarSolicitacaoEnsinoIndividual(DiscenteGraduacao discente, ComponenteCurricular disciplina, int ano, int periodo, MatriculaComponenteDao dao, ListaMensagens lista) throws DAOException, ArqException {
		
		validaPreRequisitoEI(ano, periodo, dao, lista, discente, disciplina);
		
		validaReprovacaoPorFaltaEI(dao, lista, discente, disciplina);

		validaReprovacaoPorMediaEI(dao, lista, discente, disciplina);

		validaQuantidadeTrancamentoEI(dao, lista, discente, disciplina);

		validaQuantidadeSolicitacoesEI(ano,periodo, dao, lista, discente);

		validarPlanoMatriculaEI(discente, ano, periodo, disciplina, lista);
		
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> validando pr�-requisitos
	 * 
	 * 
	 * @param solicitacao
	 * @param dao
	 * @param lista
	 * @param discente
	 * @param disciplina
	 * @throws DAOException
	 * @throws ArqException
	 */
	private static void validaPreRequisitoEI(int ano, int periodo,	MatriculaComponenteDao dao, ListaMensagens lista, DiscenteGraduacao discente, ComponenteCurricular disciplina)
			throws DAOException, ArqException {
		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO(DiscenteDao.class, null, dao.getSession());
		Collection<ComponenteCurricular> jaPagou = discenteDao.findComponentesCurricularesConcluidos(discente);
		Turma turma = new Turma();
		turma.setDisciplina( disciplina );
		turma.setAno( ano );
		turma.setPeriodo( periodo );
		MatriculaGraduacaoValidator.validarPreRequisitos(discente.getId(), turma, jaPagou, lista);
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> verificando se o aluno j� solicitou ensino individual para outros dois (m�ximo de solicita��es por per�odo) componentes
	 * 
	 * @param solicitacao
	 * @param dao
	 * @param lista
	 * @param discente
	 * @throws DAOException
	 */
	private static void validaQuantidadeSolicitacoesEI(int ano, int periodo, MatriculaComponenteDao dao, ListaMensagens lista, DiscenteGraduacao discente) throws DAOException {
		SolicitacaoEnsinoIndividualDao solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoEnsinoIndividualDao.class, null, dao.getSession());
		Collection<Integer> situacoes = new ArrayList<Integer>();
		situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
		situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
		
		Collection<SolicitacaoEnsinoIndividual> solicitacoes = solicitacaoDao.findByDiscenteAnoPeriodo(discente.getId(), Turma.ENSINO_INDIVIDUAL, ano, periodo, situacoes);
		if( solicitacoes != null && solicitacoes.size() >= 2 )
			lista.addErro("Voc� realizou solicita��o de ensino individual para outros dois componentes neste per�odo.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> valida se o aluno possui no m�ximo um trancamento na disciplina pretendida
	 * 
	 * @param dao
	 * @param lista
	 * @param discente
	 * @param disciplina
	 * @throws DAOException
	 */
	private static void validaQuantidadeTrancamentoEI(MatriculaComponenteDao dao,
			ListaMensagens lista, DiscenteGraduacao discente,
			ComponenteCurricular disciplina) throws DAOException {
		Collection<MatriculaComponente> componentes;
		componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.TRANCADO);
		if( componentes != null && componentes.size() > 1 )
			lista.addErro("Voc� possui mais de um trancamento na disciplina pretendida.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> Valida se o aluno possui no m�ximo uma reprova��o por m�dia na disciplina pretendida
	 * 
	 * 
	 * @param dao
	 * @param lista
	 * @param discente
	 * @param disciplina
	 * @throws DAOException
	 */
	private static void validaReprovacaoPorMediaEI(MatriculaComponenteDao dao,
			ListaMensagens lista, DiscenteGraduacao discente,
			ComponenteCurricular disciplina) throws DAOException {
		Collection<MatriculaComponente> componentes;
		componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.REPROVADO);
		if( componentes != null && componentes.size() > 1 )
			lista.addErro("Voc� possui mais de uma reprova��o por m�dia na disciplina pretendida.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> valida se o aluno possui reprova��o por falta na disciplina pretendida
	 * @param dao
	 * @param lista
	 * @param discente
	 * @param disciplina
	 * @throws DAOException
	 */
	private static void validaReprovacaoPorFaltaEI(MatriculaComponenteDao dao,
			ListaMensagens lista, DiscenteGraduacao discente,
			ComponenteCurricular disciplina) throws DAOException {
		Collection<MatriculaComponente> componentes;
		componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.REPROVADO_FALTA, SituacaoMatricula.REPROVADO_MEDIA_FALTA);
		if( componentes != null && !componentes.isEmpty() )
			lista.addErro("Voc� possui reprova��o por falta na disciplina pretendida.");
	}

	/**
	 * Valida se existe uma turma regular aberta compat�vel com o plano de matricula do aluno, ou seja, 
	 * que n�o tenha choque de hor�rio com as outras disciplinas matriculadas
	 * @param solicitacao
	 * @param lista
	 * @throws DAOException
	 */
	private static void validarPlanoMatriculaEI(DiscenteGraduacao discente, int ano, int periodo, ComponenteCurricular componente, ListaMensagens lista) throws DAOException{

		DiscenteDao discenteDao = DAOFactory.getInstance().getDAO( DiscenteDao.class );
		TurmaDao turmaDao = DAOFactory.getInstance().getDAO(TurmaDao.class, null, discenteDao.getSession());
		SolicitacaoMatriculaDao solicitacaoMatriculaDao = DAOFactory.getInstance().getDAO(SolicitacaoMatriculaDao.class, null, discenteDao.getSession());

		try {
			discente = (DiscenteGraduacao) discenteDao.findByPK(discente.getId());
			Collection<Turma> turmas = turmaDao.findByDisciplinaAnoPeriodoSituacao(componente, ano, periodo, new SituacaoTurma(SituacaoTurma.ABERTA), new SituacaoTurma(SituacaoTurma.A_DEFINIR_DOCENTE));

			if( turmas != null && !turmas.isEmpty() ){
				// Turmas matriculadas
				Collection<Turma> turmasSemestre =  discenteDao.findTurmasMatriculadas(discente.getId());
				// Turmas solicitadas
				turmasSemestre.addAll( solicitacaoMatriculaDao.findTurmasSolicitacoesByDiscente(
						discente.getId(),
						ano,
						periodo,
						SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA));


				for( Turma t : turmas ){
					// S� validar se n�o for agrupadora
					if (!t.isAgrupadora()) {
						boolean choqueHorario = false;
						boolean diferencaTurno;
						
						// Verificar o choque de turnos
						diferencaTurno = !HorarioTurmaUtil.hasTurnoTodosHorarios(discente.getMatrizCurricular().getTurno(), t.getHorarios() );
						
						// Verificar choque de hor�rios com os componentes matriculados ou em espera
						Collection<Turma> turmasChoque = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(t, turmasSemestre);
						if( turmasChoque != null && !turmasChoque.isEmpty() ){
							choqueHorario = true;
						}
						
						if ( !choqueHorario && !diferencaTurno) {
							lista.addErro("N�o � poss�vel solicitar ensino individualizado para este componente curricular " +
									"pois existe turma regular aberta para ele, compat�vel com seu plano de matr�cula: Turma " + t.getCodigo() + " - " + t.getDescricaoHorario());
							break;
						}
					}
				}

			}

		} finally {
			discenteDao.close();
			turmaDao.close();
			solicitacaoMatriculaDao.close();
		}
	}

	/**
	 * Valida uma solicita��o de turma de f�rias realizado por um aluno
	 * @param solicitacao a solicita��o da turma de ferias
	 * @param lista
	 * @throws ArqException
	 */
	public static void validarSolicitacaoTurmaFerias( SolicitacaoEnsinoIndividual solicitacao, MatriculaComponenteDao dao, ListaMensagens lista ) throws ArqException{
		
		SolicitacaoEnsinoIndividualDao solicitacaoDao = null;
		
		try {
			if( solicitacao.getComponente().getChTotal() > 90 )
				lista.addErro("Art. 244. Somente os componentes curriculares com carga hor�ria total de at� 90 (noventa)" +
						" horas poder�o ser oferecidos em per�odo letivo especial de f�rias." +
				" (Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009)");
			
			/** verificando se o aluno j� solicitou turma de ferias para algum outro componente.. s� pode ter uma turma de ferias por per�odo de ferias */
			
			Collection<Integer> situacoes = new ArrayList<Integer>();
			situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
			situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
			
			
			solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoEnsinoIndividualDao.class);
			Collection<SolicitacaoEnsinoIndividual> solicitacoes = solicitacaoDao.findByDiscenteAnoPeriodo(solicitacao.getDiscente().getId(), Turma.FERIAS, solicitacao.getAno(), solicitacao.getPeriodo(), situacoes);
			if( !isEmpty( solicitacoes ) )
				lista.addErro("Art.  246.  Cada  aluno  poder�  obter  matr�cula  em  apenas  um  componente  curricular  por per�odo letivo especial de f�rias. " +
				"(Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009). ");
		} finally {
			if (solicitacaoDao != null)
				solicitacaoDao.close();
		}

	}

}
