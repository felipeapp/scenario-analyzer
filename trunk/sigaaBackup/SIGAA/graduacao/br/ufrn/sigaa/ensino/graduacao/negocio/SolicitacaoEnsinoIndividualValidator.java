/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
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
 * Classe que contém as validações realizadas na solicitação de ensino individual e de férias.
 * @author Victor Hugo
 */
public class SolicitacaoEnsinoIndividualValidator {

	/**
	 * Valida a solicitação de turma realizada pelo aluno
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

			//efetua as validações básicas
			lista.addAll( solicitacao.validate().getMensagens() );
			
			// Abrindo conexões
			dao = DAOFactory.getInstance().getDAO(MatriculaComponenteDao.class);
			solicitacaoMatriculaDao = DAOFactory.getInstance().getDAO(SolicitacaoMatriculaDao.class, null, dao.getSession());
			ccDao = DAOFactory.getInstance().getDAO(ComponenteCurricularDao.class, null, dao.getSession());
			solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoEnsinoIndividualDao.class, null, dao.getSession());
			
			DiscenteGraduacao discente = dao.findByPrimaryKey(solicitacao.getDiscente().getId(), DiscenteGraduacao.class);
			ComponenteCurricular disciplina = dao.findByPrimaryKey(solicitacao.getComponente().getId(), ComponenteCurricular.class);
			solicitacao.setComponente(disciplina);
			solicitacao.setDiscente( discente );

			if( solicitacao.isEnsinoIndividual() ) /** validações especificas de solicitação de turma de ensino individualizado */
				validarSolicitacaoEnsinoIndividual(solicitacao, dao, lista);
			else if( solicitacao.isFerias() ) /** validações especificas de solicitação de turma de ferias*/
				validarSolicitacaoTurmaFerias(solicitacao, dao, lista);
			else
				throw new ArqException("O tipo da solicitação não pôde ser identificado.");

			Collection<MatriculaComponente> componentes = new ArrayList<MatriculaComponente>();

			/**
			 * validações comuns tanto a solicitação de ensino individualizado como a turma de ferias
			 */
			if( disciplina.isBloco() )
				lista.addErro("Não é possível solicitar "+(solicitacao.isFerias() ? "turma de férias" : "ensino individual")+" para componente de bloco.");

			//valida se o aluno já pagou a disciplina pretendida
			if( solicitacao.isEnsinoIndividual() ){
				componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.getSituacoesPagasEMatriculadasArray());

				if (solicitacao.getEquivalente() != null)
					componentes.addAll(dao.findByDiscenteEDisciplina(discente, solicitacao.getEquivalente(), SituacaoMatricula.getSituacoesPagasEMatriculadasArray()));
				
				if( componentes != null && !componentes.isEmpty() )
					lista.addErro("Você já pagou ou está pagando a disciplina pretendida.");
				

			} else if( solicitacao.isFerias() ){
				componentes = dao.findByDiscenteEDisciplina(discente, disciplina, SituacaoMatricula.getSituacoesPagasArray());
				if( componentes != null && !componentes.isEmpty() )
					lista.addErro("Você já pagou a disciplina pretendida.");
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
				lista.addErro("Só é permitido cursar uma turma de férias por período.");
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
					lista.addErro("Você já solicitou turma de " + s.getTipoString() + " para este componente curricular neste período.");
					return;
				}
				
				// A é obrigatoria
				// B é equivalente a A
				// C é equivalente a A
				// Valida quando o aluno já solicitou B e agora tenta solicitar C
				if( s.getEquivalente() != null && solicitacao.getEquivalente() != null && s.getEquivalente().getId() == solicitacao.getEquivalente().getId() ){
					lista.addErro("Você já solicitou turma de " + s.getTipoString() + " para componente curricular equivalente a " + solicitacao.getEquivalente().getCodigoNome() + " neste período.");
					return;
				}				
				
			}
			
			if (solicitacao.isComponenteObrigatorio()) {
				
				String strEquivalencia = solicitacao.getComponente().getEquivalencia();
				
				if (strEquivalencia == null)
					return;

				// A é obrigatoria
				// B é equivalente a A
				// Valida o caso onde o aluno solcitou B (equivalente) e agora tenta solicitar A (abrigatoria)
				for( SolicitacaoEnsinoIndividual s : solicitacoes ){
					if( ExpressaoUtil.eval(strEquivalencia, s.getComponente()) ){
						lista.addErro("Você já solicitou turma equivalente de " + s.getTipoString() + " para este componente curricular neste período através de " + s.getComponente().getCodigoNome());
						return;
					}
				}			
			} else {
				// A é obrigatoria
				// B é equivalente a A
				// Valida o caso onde o aluno solcitou A (obrigatoria) e agora tenta solicitar B (equilvante a abrigatoria)
				for( SolicitacaoEnsinoIndividual s : solicitacoes ){
					if( s.getComponente().getId() == solicitacao.getEquivalente().getId() ){
						lista.addErro("Você já solicitou turma equivalente de " + s.getTipoString() + " para este componente curricular neste período através de " + s.getComponente().getCodigoNome());						
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
		
		//valida se o aluo tem solicitação de turmas regulares iguais ou equivalentes a desejava para ensino individual
		for (Turma turma : turmasSolicitadas) {
			if(turma.getDisciplina().getId() == componente.getId()) {
				lista.addErro("Não foi possível solicitar ensino individualizado para este componente curricular pois existe, em seu plano de matrículas, uma solicitação em turma regular para ele.");
				break;
			}
			if (componentesEquivalentes != null) {
				for (ComponenteCurricular cc : componentesEquivalentes) {
					if(cc.getId() == turma.getDisciplina().getId()) {
						cc = dao.refresh(cc);
						lista.addErro("Não foi possível solicitar ensino individualizado para este componente curricular pois existe, em seu plano de matrículas, uma solicitação em turma regular para o componente "+cc.getCodigo()+", equivalente a ele.");
						break;
					}
				}
			}
		}
	}

	/**
	 * Valida a solicitação de ensino individual realizada por um discente
	 * @param solicitacao
	 * @param erros lista de mensagens de erro de validação
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
	 * Valida a solicitação de ensino individual realizada por um discente
	 * @param solicitacao
	 * @param erros lista de mensagens de erro de validação
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
	 * <b>ENSINO INDIVIDUAL</b> validando pré-requisitos
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
	 * <b>ENSINO INDIVIDUAL</b> verificando se o aluno já solicitou ensino individual para outros dois (máximo de solicitações por período) componentes
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
			lista.addErro("Você realizou solicitação de ensino individual para outros dois componentes neste período.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> valida se o aluno possui no máximo um trancamento na disciplina pretendida
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
			lista.addErro("Você possui mais de um trancamento na disciplina pretendida.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> Valida se o aluno possui no máximo uma reprovação por média na disciplina pretendida
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
			lista.addErro("Você possui mais de uma reprovação por média na disciplina pretendida.");
	}

	/**
	 * <b>ENSINO INDIVIDUAL</b> valida se o aluno possui reprovação por falta na disciplina pretendida
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
			lista.addErro("Você possui reprovação por falta na disciplina pretendida.");
	}

	/**
	 * Valida se existe uma turma regular aberta compatível com o plano de matricula do aluno, ou seja, 
	 * que não tenha choque de horário com as outras disciplinas matriculadas
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
					// Só validar se não for agrupadora
					if (!t.isAgrupadora()) {
						boolean choqueHorario = false;
						boolean diferencaTurno;
						
						// Verificar o choque de turnos
						diferencaTurno = !HorarioTurmaUtil.hasTurnoTodosHorarios(discente.getMatrizCurricular().getTurno(), t.getHorarios() );
						
						// Verificar choque de horários com os componentes matriculados ou em espera
						Collection<Turma> turmasChoque = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(t, turmasSemestre);
						if( turmasChoque != null && !turmasChoque.isEmpty() ){
							choqueHorario = true;
						}
						
						if ( !choqueHorario && !diferencaTurno) {
							lista.addErro("Não é possível solicitar ensino individualizado para este componente curricular " +
									"pois existe turma regular aberta para ele, compatível com seu plano de matrícula: Turma " + t.getCodigo() + " - " + t.getDescricaoHorario());
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
	 * Valida uma solicitação de turma de férias realizado por um aluno
	 * @param solicitacao a solicitação da turma de ferias
	 * @param lista
	 * @throws ArqException
	 */
	public static void validarSolicitacaoTurmaFerias( SolicitacaoEnsinoIndividual solicitacao, MatriculaComponenteDao dao, ListaMensagens lista ) throws ArqException{
		
		SolicitacaoEnsinoIndividualDao solicitacaoDao = null;
		
		try {
			if( solicitacao.getComponente().getChTotal() > 90 )
				lista.addErro("Art. 244. Somente os componentes curriculares com carga horária total de até 90 (noventa)" +
						" horas poderão ser oferecidos em período letivo especial de férias." +
				" (Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009)");
			
			/** verificando se o aluno já solicitou turma de ferias para algum outro componente.. só pode ter uma turma de ferias por período de ferias */
			
			Collection<Integer> situacoes = new ArrayList<Integer>();
			situacoes.add(SolicitacaoEnsinoIndividual.ATENDIDA);
			situacoes.add(SolicitacaoEnsinoIndividual.SOLICITADA);
			
			
			solicitacaoDao = DAOFactory.getInstance().getDAO(SolicitacaoEnsinoIndividualDao.class);
			Collection<SolicitacaoEnsinoIndividual> solicitacoes = solicitacaoDao.findByDiscenteAnoPeriodo(solicitacao.getDiscente().getId(), Turma.FERIAS, solicitacao.getAno(), solicitacao.getPeriodo(), situacoes);
			if( !isEmpty( solicitacoes ) )
				lista.addErro("Art.  246.  Cada  aluno  poderá  obter  matrícula  em  apenas  um  componente  curricular  por período letivo especial de férias. " +
				"(Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009). ");
		} finally {
			if (solicitacaoDao != null)
				solicitacaoDao.close();
		}

	}

}
