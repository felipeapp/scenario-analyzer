/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/06/2007
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.arq.dao.graduacao.ReservaCursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dao.RegiaoMatriculaDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.RegiaoMatricula;
import br.ufrn.sigaa.dominio.RegiaoMatriculaCampus;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dao.ExpressaoComponenteCurriculoDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.ExpressaoComponenteCurriculo;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.RegistroAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatriculaEquivalentes;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;
import br.ufrn.sigaa.ensino.tecnico.negocio.MatriculaTecnicoValidator;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Responsável pelas validações de matrícula de graduação tais como: Para que a
 * capacidade da turma não seja excedida com mais matrícula, valida os
 * pré-requisitos para se matricular na turma, valida os componentes
 * equivalentes, valida o número máximo de matriculas por docente, valida para
 * evitar choque de horário, dentre outras validações realizadas.
 * 
 * @author Andre M Dantas
 */
public class MatriculaGraduacaoValidator {

	/**
	 * Verifica a capacidade da turma.
	 * 
	 * @param turma	
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarCapacidadeTurma(Turma turma, ListaMensagens erros) throws ArqException {
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			long matriculados = dao.findQtdAlunosPorTurma(turma.getId(), 
				SituacaoMatricula.getSituacoesOcupamVaga().toArray(
						new SituacaoMatricula[SituacaoMatricula.getSituacoesOcupamVaga().size()]));
			if (turma.getCapacidadeAluno() != null && matriculados + 1 > turma.getCapacidadeAluno() ) {
				String msgConteudo = "O discente não pode cursar a turma " + turma.getCodigo() + 
					" do componente  "	+ turma.getDisciplina().getDescricaoResumida() + 
					" pois a capacidade de alunos foi excedida " +
					" ("+turma.getCapacidadeAluno()+" vagas).<br/>";
				erros.addErro(msgConteudo);
			}
		} finally {
			dao.close();
		}
	}
	/**
	 * Apenas alerta sobre a capacidade da turma, incluindo as desistências
	 * realizadas que podem ser desfeitas.
	 * 
	 * @param discente
	 * @param turma
	 * @param erros
	 * @throws ArqException
	 */
	public static void alertarCapacidadeTurma(DiscenteAdapter discente, Turma turma, ListaMensagens erros) throws ArqException {
		TurmaDao dao = getDAO(TurmaDao.class);
		SolicitacaoMatriculaDao sDao = getDAO(SolicitacaoMatriculaDao.class);
		
		try {
			long matriculados = dao.findQtdAlunosPorTurma(turma.getId(), SituacaoMatricula.MATRICULADO.getId());
			long desistentes = dao.findQtdAlunosPorTurma(turma.getId(), SituacaoMatricula.DESISTENCIA.getId());
			SolicitacaoMatricula desistente = sDao.findDesistente(turma.getId(), discente.getId());
			
			Integer capacidadeTurma = turma.getCapacidadeAluno();
			if (capacidadeTurma != null) {
				if (  matriculados + 1 > capacidadeTurma  ) { 
					erros.addWarning("Atenção! A turma selecionada para o componente " + turma.getDisciplina().getCodigo() + 
							" já teve sua capacidade de " + capacidadeTurma +
							" alunos atingida. Apesar se ser permitido solicitar matrícula para ela é necessário lembrar " +
							" que a possibilidade da matrícula ser deferida depende da desistência de algum dos discente já matriculados.");
				} else if ( matriculados + desistentes + 1 > capacidadeTurma && desistente == null)  {
					erros.addWarning("Atenção! A turma selecionada para o componente " + turma.getDisciplina().getCodigo() + 
							" possui " + desistentes + " vaga(s) disponível(is) devido à desistência de alunos matriculados durante o processamento." +
							" A existência desta(s) vaga(s) pode ser alterada caso as desistências sejam canceladas pelos discentes." +
							" Neste caso a turma voltará a ter a ocupação original de " + (matriculados + desistentes) + 
							" alunos.");
				}
			}
		} finally {
			dao.close();
			sDao.close();
		}
	}

	/**
	 * Valida caso o componente da turma passado por parâmetro não possui seus
	 * pré-requisitos dentro da coleção de componentes também passado por
	 * parâmetro.
	 * 
	 * @param idDiscente
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarPreRequisitos(int idDiscente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros) throws ArqException {
		validarPreRequisitos(idDiscente, turma, componentes, erros, null);
	}
	
	
	/**
	 * Valida caso o componente da turma passado por parâmetro não possui seus
	 * pré-requisitos dentro da coleção de componentes também passado por
	 * parâmetro.
	 * 
	 * @param idDiscente
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarPreRequisitos(int idDiscente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros, Discente discente) throws ArqException {
		boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(turma.getDisciplina().getPreRequisito(), idDiscente, componentes);
		if (! possuiPreRequisito) {
				StringBuilder msgConteudo = new StringBuilder("O Discente");
				if (discente != null){
					msgConteudo.append(" ").append(discente.getMatriculaNome());
				}	
				msgConteudo.append(" não pode cursar o componente ");
				msgConteudo.append(turma.getDisciplina().getDescricaoResumida());
				msgConteudo.append(", pois não possui os pré-requisitos necessários.<br/>");
				erros.addErro(msgConteudo.toString());

		}
	}

	/**
	 * Valida se o discente informado possui os preRequisitos específicos cadastrados para a turma passada no parâmetro
	 * @param discente
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarPreRequisitoEspecifico( Discente discente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros ) throws ArqException{
		
		if( discente.getCurriculo() == null )
			return;
		
		ExpressaoComponenteCurriculoDao dao = getDAO(ExpressaoComponenteCurriculoDao.class);
		ExpressaoComponenteCurriculo exp = dao.findByComponenteCurriculo(discente.getCurriculo().getId(), turma.getDisciplina().getId());
		
		ComponenteCurricularDao daoComp = null;
		try {
			
			if( exp == null )
				return;
			
			if(exp.isAtivo() && !isEmpty( exp.getPrerequisito() ) ){
				daoComp = getDAO(ComponenteCurricularDao.class);
				boolean possuiPreRequisito = ExpressaoUtil.evalComTransitividade(exp.getPrerequisito(), discente.getId(), componentes);
				String preRequisitoFormatado = ExpressaoUtil.buildExpressaoFromDB(exp.getPrerequisito(), daoComp);
				if (!possuiPreRequisito) {
					String msgConteudo = "O Discente não pode cursar o componente "
						+ turma.getDisciplina().getDescricaoResumida() + " pois não possui os pré-requisitos necessários: " + preRequisitoFormatado;
					erros.addErro(msgConteudo);
				}
			}
			
		} finally {
			if (dao != null)
				dao.close();
			if (daoComp != null)
				daoComp.close();
		}
		
	}
	
	/**
	 * Valida se o discente informado possui os coRequisitos específicos
	 * cadastrados para a turma passada no parâmetro
	 * 
	 * @param discente
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarCoRequisitosEspecifico(Discente discente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros) throws ArqException {
		
		if( discente.getCurriculo() == null )
			return;
		
		ExpressaoComponenteCurriculoDao dao = getDAO(ExpressaoComponenteCurriculoDao.class);
		ExpressaoComponenteCurriculo exp = dao.findByComponenteCurriculo(discente.getCurriculo().getId(), turma.getDisciplina().getId());
		
		ComponenteCurricularDao daoComp = null;
		try {
			if( exp == null)
				return;
			
			String coRequisito = exp.getCorequisito();
			
			if(exp.isAtivo() && !isEmpty( exp.getCorequisito() ) ){
				boolean possuiCoRequisito = ExpressaoUtil.evalComTransitividade(coRequisito, discente.getId(), componentes);
				if (!possuiCoRequisito) {
					daoComp = getDAO(ComponenteCurricularDao.class);
					String coRequisitoFormatado = ExpressaoUtil.buildExpressaoFromDB(coRequisito, daoComp);
					String msgConteudo = "O discente não pode cursar turmas do componente " +
						turma.getDisciplina().getDescricaoResumida() + " pois não possui os co-requisitos necessários: " +
						coRequisitoFormatado;
					erros.addErro(msgConteudo);
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
			
	}

	/**
	 * Valida caso o componente da turma passado por parâmetro seja equivalente a
	 * qualquer componente dentro da coleção de componentes também passado por
	 * parâmetro.
	 * 
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarEquivalencia(DiscenteAdapter discente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros) throws ArqException {
		ComponenteCurricularDao ccdao = getDAO(ComponenteCurricularDao.class);
		
		try {
			ComponenteCurricular componenteSolicitacao = ccdao.findEquivalenciaPorDiscente(turma.getDisciplina().getId(), discente.getId());
			
			if (hasEquivalente(componenteSolicitacao, componentes)) {
				String msgConteudo =  "O Discente  não pode cursar turmas do componente "
						+ turma.getDisciplina().getDescricaoResumida() + " pois já cursou ou está matriculado em componentes equivalentes.<br/>";
				erros.addErro(msgConteudo);
			}
		} finally {
			ccdao.close();
		}
	}

	/**
	 * Verifica se o componente curricular informado possui alguma equivalência
	 * com o conjunto de componentes curriculares passado como segundo
	 * argumento.
	 * 
	 * @param componente
	 * @param componentes
	 * @return
	 * @throws ArqException
	 */
	public static boolean hasEquivalente(ComponenteCurricular componente,
			Collection<ComponenteCurricular> componentes) throws ArqException {
		return !isEmpty(componente.getEquivalencia())
				&& ExpressaoUtil.eval(componente.getEquivalencia(), componentes);
	}

	/**
	 * Erro caso o discente passado por parâmetro tenha excedido o número de
	 * vezes permitido se matricular em turmas de um componente, mesmo já tendo
	 * sido aprovado (ou aproveitado).
	 * 
	 * @param turma
	 * @param discente
	 * @param turmas
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarLimiteMaxMatriculas(Turma turma, DiscenteAdapter discente, Collection<Turma> turmas, RestricoesMatricula restricoes, ListaMensagens erros) throws ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		try {
			ComponenteCurricular componente = turma.getDisciplina();
			if (componente.isSubUnidade()) {
				componente = dao.refresh(componente).getBlocoSubUnidade();
			}

			//Collection<MatriculaComponente> turmasPagas = dao.findByDiscenteEDisciplina(discente, componente, SituacaoMatricula.getSituacoesPagasArray());
			long qtdTurmasPagas = dao.countMatriculadasByDiscenteComponente(discente, componente, SituacaoMatricula.getSituacoesPagasArray());
			
//			int qtdTurmasPagas = (turmasPagas == null ? 0 : turmasPagas.size());
			int qtdTurmasPretendidasEMatriculadas = getNumeroRepetidas(componente, turmas);
			int qtdMaximaMatriculas = componente.getQtdMaximaMatriculas();
			
			if (qtdTurmasPagas >= qtdMaximaMatriculas) {				
				erros.addErro("O Discente  já cursou o componente " + componente.getDescricaoResumida() + "<br/>");								
			} else if(qtdMaximaMatriculas > 1 && qtdTurmasPagas + qtdTurmasPretendidasEMatriculadas > qtdMaximaMatriculas){				
				erros.addErro("O Discente excedeu o limite de vezes que pode cursar a disciplina "+ componente.getDescricaoResumida() + "<br/>");				
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Retorna a quantidade de turmas da mesma disciplina
	 * 
	 * @param disciplina
	 * @param turmas
	 * @return
	 */
	private static int getNumeroRepetidas(ComponenteCurricular disciplina, Collection<Turma> turmas) {
		int qtd = 0;
		for(Turma t: turmas){
			if(t.getDisciplina().equals(disciplina))
				qtd++;
		}
		return qtd;
	}
	
	/**
	 * Valida caso o componente passado por parâmetro esteja presente na coleção
	 * de turmas também passada por parâmetro.
	 * 
	 * @param turma
	 * @param turmas
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarMatriculadasSemestre(Turma turma, Collection<Turma> turmas, RestricoesMatricula restricoes, ListaMensagens erros) throws ArqException {
		validarMatriculadasSemestre(turma, turmas, restricoes, erros, null);
	}
	
	/**
	 * Valida caso o componente passado por parâmetro esteja presente na coleção
	 * de turmas também passada por parâmetro.
	 * 
	 * @param turma
	 * @param turmas
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarMatriculadasSemestre(Turma turma, Collection<Turma> turmas, RestricoesMatricula restricoes, ListaMensagens erros, Discente discente) throws ArqException {
		
		for (Turma t : turmas) {
			if (t != turma && t.getDisciplina().getId() == turma.getDisciplina().getId()) {
				StringBuilder msgConteudo = new StringBuilder();
				if(NivelEnsino.isAlgumNivelStricto(turma.getDisciplina().getNivel())) {				
					msgConteudo.append("O discente");
					if (discente != null)
						msgConteudo.append(" ").append(discente.getMatriculaNome());
					msgConteudo.append(" já está matriculado, tentando se matricular, foi solicitada exclusão ou foi negada solicitação de matrícula em " +
					"turmas do componente: ")
					.append(turma.getDisciplina().getDescricaoResumida())
					.append(".<br/> Turma: ")
					.append(t.getCodigo());
				} else {
					msgConteudo.append("O discente");
					if (discente != null)
						msgConteudo.append(" ").append(discente.getMatriculaNome());
					msgConteudo.append(" já está matriculado (ou está tentando se matricular) " +
					" em mais de uma turma do componente: ")
					.append(turma.getDisciplina().getDescricaoResumida())
					.append("<br/>");					
				}				
				erros.addErro(msgConteudo.toString());
			}
		}
	}

	/**
	 * Valida caso o componente da turma passado por parâmetro não possuir seus
	 * co-requisitos dentro da coleção de componentes também passado por
	 * parâmetro.
	 * 
	 * @param idDiscente
	 * @param turma
	 * @param componentes
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarCoRequisitos(int idDiscente, Turma turma, Collection<ComponenteCurricular> componentes, ListaMensagens erros) throws ArqException {
		String coRequisito = turma.getDisciplina().getCoRequisito();
		boolean possuiCoRequisito = ExpressaoUtil.evalComTransitividade(coRequisito, idDiscente, componentes);

		if (!possuiCoRequisito) {
			ComponenteCurricularDao dao = null;
			try {
				dao = getDAO(ComponenteCurricularDao.class);
				String coRequisitoFormatado = ExpressaoUtil.buildExpressaoFromDB(coRequisito, dao);
				String msgConteudo = "O discente não pode cursar turmas do componente " +
					turma.getDisciplina().getDescricaoResumida() + " pois não possui os co-requisitos necessários: " +
					coRequisitoFormatado;
				erros.addErro(msgConteudo);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Verifica se há choque de horário entre a turma e alguma turma da coleção
	 * turmasDesseSemestre
	 * 
	 * @param turma
	 * @param turmasDesseSemestre
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarChoqueHorarios(Turma turma, Collection<Turma> turmasDesseSemestre, ListaMensagens erros) throws
			ArqException {
		Collection<Turma> horariosChocados = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(turma, turmasDesseSemestre);
		if (!horariosChocados.isEmpty()) {
			StringBuffer msg = new StringBuffer("Ocorreram choque de horários com as turmas: ");
			for (Turma t : horariosChocados) {
				msg.append("<br>" + turma.getDisciplina().getCodigo() + " - Turma "+turma.getCodigo()+" ("+turma.getDescricaoHorario()+") e "
								+ t.getDisciplina().getCodigo() + " - Turma "+t.getCodigo() +
								" (" + t.getDescricaoHorario() + ")");
			}
			erros.addErro(msg.toString()+ "<BR>");
		}
	}

	/**
	 * Verifica se há choque de horário entre a turma e as solicitações de
	 * matricula do discente inicialmente utilizado apenas na solicitação de
	 * turma de ensino individual, deve verificar se existe choque de horário
	 * entre a turma de ensino individual que esta sendo solicitada e as
	 * solicitações de matricula do discente
	 * 
	 * @param turma
	 * @param discente
	 * @param erros
	 * @throws DAOException
	 */
	public static void validarChoqueHorariosSolicitacoesMatricula(Turma turma, DiscenteAdapter discente, ListaMensagens erros) throws DAOException{

		SolicitacaoMatriculaDao dao = DAOFactory.getInstance().getDAO( SolicitacaoMatriculaDao.class );

		try {
			Collection<SolicitacaoMatricula> solicitacoes = dao.findByDiscenteAnoPeriodo(discente, turma.getAno(), turma.getPeriodo(), null, SolicitacaoMatricula.SUBMETIDA, SolicitacaoMatricula.ORIENTADO, SolicitacaoMatricula.VISTA);
	
			Collection<Turma> turmasSolicitacoes = new ArrayList<Turma>();
			for( SolicitacaoMatricula sm : solicitacoes ){
				turmasSolicitacoes.add( sm.getTurma() );
			}
	
			Collection<Turma> horariosChocados = HorarioTurmaUtil.verificarChoqueHorarioDiscentes(turma, turmasSolicitacoes);
			if (!horariosChocados.isEmpty()) {
				StringBuffer msg = new StringBuffer("Ocorreram choque de horários com as solicitações de turmas realizada pelo discente " + discente.toString() + ":");
				for (Turma t : horariosChocados) {
					msg.append("<br>" + turma.getDisciplina().getCodigo() + " - Turma "+turma.getCodigo()+" ("+turma.getDescricaoHorario()+") e "
									+ t.getDisciplina().getCodigo() + " - Turma "+t.getCodigo() +
									" (" + t.getDescricaoHorario() + ")");
				}
				erros.addErro(msg.toString()+ "<BR>");
			}
		} finally {
			dao.close();
		}

	}

	/**
	 * Verifica se o aluno de graduação informado excedeu o limite de créditos
	 * de componentes curriculares fora da sua estrutura curricular definido
	 * pela sua unidade gestora acadêmica.
	 * 
	 * @param discente
	 * @param erros
	 * @param turmas
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarLimiteCreditosExtra(DiscenteGraduacao discente, ListaMensagens erros, Collection<Turma> turmas) throws NegocioException,
			ArqException {
		if (discente.isRegular()) {
			
			EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
			try {
				Curriculo curriculo = null;
				
				if ( !isEmpty(discente.getCurriculo()))
					curriculo = dao.findDetalhesByCurriculo(discente.getCurriculo().getId());
				else
					curriculo = dao.findMaisRecenteByCurso(discente.getCurso().getId());
				
				Boolean validar = (turmas != null && !turmas.isEmpty()) ? false: true;				
				Collection<ComponenteCurricular> disciplinasCurriculo = curriculo.getComponentesCurriculares();
				//Evitar Erro de NP nos aproveitamentos, que não tem turma.
				if(turmas!=null){				
					for(Turma t : turmas) {					
						if(t.isMatricular() && !disciplinasCurriculo.contains(t.getDisciplina())) {						
							validar = true;					
						}
					}				
				}
				
				int max = 0;
				// Nunca deve vir nulo, colocado por precaução.
				if (curriculo != null && curriculo.getMaxEletivos() > 0){
					ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(curriculo.getCurso().getNivel());
					max =  curriculo.getMaxEletivos() / parametros.getHorasCreditosAula();
				} else {
					ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(discente);
					max = parametros.getMaxCreditosExtra();					
				}
				
				if (discente.getCrExtraIntegralizados() > max && validar) {
					String msgConteudo = "Foi excedido o limite máximo de " +
					max + " créditos de componentes curriculares fora da estrutura curricular do discente.";
					erros.addErro(msgConteudo);
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Verifica se o aluno informado excedeu o limite de créditos por semestre
	 * definido no seu currículo.
	 * 
	 * @param discente
	 * @param turmasDesseSemestre
	 * @param cal
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarLimiteMaximoCreditosSemestre(DiscenteAdapter discente, Collection<Turma> turmasDesseSemestre, CalendarioAcademico cal)
			throws NegocioException, ArqException {
		if ( discente.isRegular() ){
			validarLimiteMaximoCreditosSemestre(discente, turmasDesseSemestre, cal, discente.getCurriculo().getCrMaximoSemestre());
		}	
	}

	/**
	 * Verifica se o aluno informado excedeu o limite de créditos por semestre
	 * definido no seu currículo.
	 * 
	 * @param discente
	 * @param turmasDesseSemestre
	 * @param cal
	 * @param limiteMaximoCreditoSemestre
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarLimiteMaximoCreditosSemestre(DiscenteAdapter discente, Collection<Turma> turmasDesseSemestre, CalendarioAcademico cal, int limiteMaximoCreditoSemestre)
			throws NegocioException, ArqException {
		if (discente.isRegular()) {
			int crs = 0;
			for (Turma turma : turmasDesseSemestre) {
				if (discente.isStricto() || (turma.getAno() == cal.getAno() && turma.getPeriodo() == cal.getPeriodo()))
					crs += turma.getDisciplina().getCrTotal();
			}
			if (crs > limiteMaximoCreditoSemestre) {
				String msg = "O Discente excedeu o limite máximo de crédito por semestre " +
					"permitido pelo seu currículo.<br>" +
					"Está tentando se matricular no total de " + crs + " créditos, enquanto o máximo permitido " +
					" é de "+limiteMaximoCreditoSemestre+" créditos no semestre";
				throw new NegocioException(msg);
			}
		}
	}



	/**
	 * Valida o limite mínimo de créditos que um discente deve cumprir em um semestre
	 * 
	 * @param discente
	 * @param turmasDesseSemestre
	 * @param atividadesJaMatriculadas 
	 * @param cal
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarLimiteMinimoCreditosSemestre(DiscenteAdapter discente, Collection<Turma> turmasDesseSemestre, Collection<RegistroAtividade> atividadesJaMatriculadas, CalendarioAcademico cal)
			throws NegocioException, ArqException {
		if ( discente.isRegular() ){
			validarLimiteMinimoCreditosSemestre(discente, turmasDesseSemestre, atividadesJaMatriculadas, cal, discente.getCurriculo().getCrMinimoSemestre());
		}	
	}

	/**
	 * Valida o limite mínimo de créditos que um discente deve cumprir em um semestre
	 * 
	 * @param discente
	 * @param turmasDesseSemestre
	 * @param atividadesJaMatriculadas 
	 * @param cal
	 * @param limiteMinimoCreditoSemestre
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarLimiteMinimoCreditosSemestre(DiscenteAdapter discente, Collection<Turma> turmasDesseSemestre, Collection<RegistroAtividade> atividadesJaMatriculadas, CalendarioAcademico cal, Integer limiteMinimoCreditoSemestre)
			throws NegocioException, ArqException {
		if (discente.isRegular()) {
			
			int crs = 0;
			int ch = 0;
			
			for (Turma turma : turmasDesseSemestre) {
				if (discente.isStricto() || (turma.getAno() * 10 + turma.getPeriodo() >= cal.getAno() * 10 + cal.getPeriodo())) {
					crs += turma.getDisciplina().getCrTotal();
					ch += turma.getDisciplina().getChTotal();
				}
			}
			
			if (isNotEmpty(atividadesJaMatriculadas)) {
				for (RegistroAtividade ra : atividadesJaMatriculadas) {
					ch += ra.getAtividade().getChTotal();
					ch += ra.getAtividade().getCrTotal();
				}
			}
			
			int limiteMinimoCargaHorariaSemestre = discente.getCurriculo().getChMinimaSemestre() != null ? discente.getCurriculo().getChMinimaSemestre() : 0;
			int limiteMinimoCrSemestre = limiteMinimoCreditoSemestre != null ? limiteMinimoCreditoSemestre.intValue() : 0;
			
			if (crs < limiteMinimoCrSemestre && ch < limiteMinimoCargaHorariaSemestre) {
				String msg = "O Discente não atingiu o limite mínimo de créditos ou carga horária por semestre permitido pelo seu currículo.<br>"
						+ "Está tentando se matricular no total de " + crs + " créditos "
						+ "e " +  ch + " horas, enquanto o mínimo exigido "
						+ " é de " + limiteMinimoCreditoSemestre + " créditos "
						+ "ou " + limiteMinimoCargaHorariaSemestre + " horas, no semestre.";
				throw new NegocioException(msg);
			}
		}
	}


	/**
	 * Valida caso alguma das duas regras que envolvem a matrícula de aluno especial
	 * sejam quebradas:
	 * 1 - número máximo de matrículas
	 * 2 - número máximo de períodos cursados
	 *
	 * @param discente
	 * @param totalMatriculadosNoSemestre
	 * @param totalPeriodosCursados
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarAlunoEspecial(DiscenteAdapter discente, int totalMatriculadosNoSemestre, int totalPeriodosCursados, ListaMensagens erros) throws ArqException {
		// Validar regras de negócio do aluno especial
		if (discente.getTipo() == Discente.ESPECIAL) {
			
			DiscenteDao dao = getDAO(DiscenteDao.class);
			try {
				
				Discente discenteFormaIngresso = dao.findAndFetch(discente.getId(), Discente.class, "formaIngresso");
				
				Integer maxDisciplinas = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getMaxDisciplinas();
				Integer maxPeriodos = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getMaxPeriodos();
				String categoria = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getDescricao();
				
				if (maxDisciplinas != null && totalMatriculadosNoSemestre > maxDisciplinas) {
					String msg = "O Aluno Especial " + categoria  
							+ " só pode se matricular em, no máximo, " + maxDisciplinas
							+ " disciplinas por período.";
					erros.addErro(msg);
				}
	
				if (maxPeriodos != null && totalPeriodosCursados >= maxPeriodos) {
					String msg = "O Aluno Especial " + categoria 
						+ " só pode cursar no máximo " + maxPeriodos
						+ " períodos, consecutivos ou não.";
					erros.addErro(msg);
				}
			} finally {
				dao.close();
			}

		}
	}

	/**
	 * Verifica se é permitido fazer matrícula de aluno regular no momento.
	 * Caso não possa, é jogada uma exceção.
	 *
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarPeriodoRegular(MovimentoMatriculaGraduacao mov) throws NegocioException,
			ArqException {
		if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA ) )
			return; /* não valida período no calendário caso a matricula esteja sendo realizada por
			PPG, COORDENADOR_CURSO_STRICTO, SECRETARIA_POS, COORDENADOR_PROGRAMA_RESIDENCIA ou SECRETARIA_RESIDENCIA  */
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(mov.getDiscente());
		try {
			if (!cal.isPeriodoMatriculaRegular()) {
				throw new NegocioException();
			}
		} catch (NegocioException e) {
			e.addErro("Não é permitido realizar matrículas de alunos regulares fora do " +
						"período determinado no calendário universitário");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Ocorreu um erro na validação do período para matrícula");
		}

	}

	/**
	 * Verifica se é permitido fazer matrícula de aluno especial no momento.
	 * Caso não possa, é jogada uma exceção.
	 *
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarPeriodoEspecial(MovimentoMatriculaGraduacao mov) throws NegocioException,
			ArqException {

		if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) )
			return; /* não valida período no calendário caso a matricula esteja sendo realizada por
			PPG, COORDENADOR_CURSO_STRICTO, SECRETARIA_POS  */

		CalendarioAcademico cal = mov.getCalendarioAcademicoAtual();
		try {
			if (!cal.isPeriodoMatriculaAlunoEspecial()) {
				throw new NegocioException("Não é permitido realizar matrículas de alunos especiais fora do " +
						"período determinado no calendário universitário");
			}
		} catch (NegocioException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Ocorreu um erro na validação do período para matrícula");
		}
	}

	/**
	 * Checa permissões de acesso à operação de: Matrícula de alunos regulares.
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisRegular(MovimentoMatriculaGraduacao mov)  throws SegurancaException {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_CENTRO, 
				SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA,
				SigaaPapeis.CADASTRA_DISCENTE_GRADUACAO))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas");
	}

	/**
	 * Verifica se o usuário que está realizando o movimento possui permissão de
	 * efetuar matrícula regular dos alunos do ensino a distância.
	 * 
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisRegularEAD(MovimentoMatriculaGraduacao mov)  throws SegurancaException {
		checaPapeisRegularEAD(mov.getUsuarioLogado());
	}

	/**
	 * Verifica se o usuário que está realizando o movimento possui permissão de
	 * efetuar matrícula regular dos alunos do ensino a distância.
	 * 
	 * @param usuario
	 * @throws SegurancaException
	 */
	public static void checaPapeisRegularEAD(UsuarioGeral usuario)  throws SegurancaException {
		if (!usuario.isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.ADMINISTRADOR_DAE))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas");
	}

	/**
	 * Verifica se o usuário que está realizando o movimento possui permissão de
	 * efetuar matrícula regular dos alunos do convênio (PROBASICA ou PRONERA).
	 * 
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisConvenio(MovimentoMatriculaGraduacao mov)  throws SegurancaException {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_PROBASICA, SigaaPapeis.SECRETARIA_COORDENACAO))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas");
	}

	/**
	 * Checa permissões de acesso à operação de Matrícula de alunos especiais
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisEspecial(MovimentoMatriculaGraduacao mov) throws SegurancaException  {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas de aluno especial");
	}

	/**
	 * Checa permissões de acesso à operação de matrícula fora do prazo.
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisForaPrazo(MovimentoMatriculaGraduacao mov) throws SegurancaException {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas fora do prazo");
	}

	/**
	 * Checa permissões de acesso à operação de matrícula compulsórias.
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisCompulsoria(MovimentoMatriculaGraduacao mov) throws SegurancaException  {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP))
			throw new SegurancaException(
					"Usuário não possui permissão para realizar matrículas compulsória");
	}

	/**
	 * Com base nas restrições passadas, são feitas as validações se o discente
	 * passado por parâmetro pode se matricular.
	 * 
	 * @param discente
	 * @param turma
	 * @param turmasDoSemestre
	 * @param todosComponentes
	 * @param restricoes
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static ListaMensagens validarTurmaIndividualmente(DiscenteAdapter discente, Turma turma, Collection<Turma> turmasDoSemestre,
			Collection<ComponenteCurricular> todosComponentes, RestricoesMatricula restricoes) throws ArqException, NegocioException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			return validarTurmaIndividualmente(discente, turma, turmasDoSemestre, todosComponentes, restricoes, 
					dao.findComponentesCurricularesConcluidos(discente), CalendarioAcademicoHelper.getCalendario(discente));
		} finally {
			dao.close();
		}
	
	}

	/**
	 * Com base nas restrições passadas, são feitas as validações se o discente
	 * passado por parâmetro pode se matricular.
	 * 
	 * @param discente
	 * @param turma
	 *            - turma a ser verificada
	 * @param turmasDoSemestre
	 * @param todosComponentes
	 *            - todos os componentes já aprovados (ou aproveitados)
	 *            inclusive do semestre corrente
	 * @param restricoes
	 *            - o conjunto de restrições que devem ser analisados
	 * @param componentesPagos
	 * @param cal
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static ListaMensagens validarTurmaIndividualmente(DiscenteAdapter discente, Turma turma, Collection<Turma> turmasDoSemestre,
			Collection<ComponenteCurricular> todosComponentes, RestricoesMatricula restricoes, Collection<ComponenteCurricular> componentesPagos,
			CalendarioAcademico cal) throws ArqException, NegocioException {
		ListaMensagens erros = new ListaMensagens();

		Collection<ComponenteCurricular> componentesSemestre =  new ArrayList<ComponenteCurricular>(0);
		for (Turma t  : turmasDoSemestre) {
			componentesSemestre.add(t.getDisciplina());
		}

		// no modelo antigo de verificação de créditos
		if (restricoes.getValorMaximoCreditos() == null && restricoes.getValorMinimoCreditos() == null) {
			if (restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
				validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal);
			}
		} else {
			// no modelo novo de verificação de créditos
			if (restricoes.getValorMaximoCreditos() != null && restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
				validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal, restricoes.getValorMaximoCreditos());
			}
		}
			
			// verifica se deve ser testado a capacidade da turma
			if (restricoes.isCapacidadeTurma()) {
				validarCapacidadeTurma(turma, erros);
			}
			
			// Verifica se o usuário deve ser alertado sobre a capacidade
			if (restricoes.isAlertaCapacidadeTurma()) {
				alertarCapacidadeTurma(discente, turma, erros);
			}
			
			// verifica se deve ser testado se o discente já pagou esse componente ou se está matriculado nele.
			if (restricoes.isMesmoComponente() && !restricoes.isPermiteDuplicidadeCasoConteudoVariavel()) {
				/* verifica se o limite máximos de matrículas (aprovadas ou aproveitadas)
				 * do Componente foi excedido.
				 * ATENÇÂO: essa verificação é relacionada com o limite vinculado diretamente ao componente,
				 * existem componentes em que se é permitido ser aprovado mais de uma vez
				 */
				validarLimiteMaxMatriculas(turma, discente, turmasDoSemestre, restricoes, erros);
				if(discente.getNivel() != NivelEnsino.TECNICO && turma.getDisciplina().getQtdMaximaMatriculas() == 1) {						
					//  verifica se o aluno já está matriculado em uma turma desse componente
					validarMatriculadasSemestre(turma, turmasDoSemestre, restricoes,erros, discente.getDiscente());					
				}
				// verifica se o aluno já possui uma matricula aprovada ou aproveitada de um componente equivalente
				validarEquivalencia(discente, turma, todosComponentes, erros);
			}
			
			if (discente.getNivel() == NivelEnsino.TECNICO && turma.getEspecializacao() != null) {
				MatriculaTecnicoValidator.validarEspecializacaoTurma(discente, turma, erros);
			}
			
			// verifica se deve ser testado se o discente está se matriculando nos co-requisitos necessários
			if (restricoes.isCoRequisitos()) {
				validarCoRequisitos(discente.getId(), turma, todosComponentes, erros);
				validarCoRequisitosEspecifico(discente.getDiscente(), turma, todosComponentes, erros);
			}
			
			// verifica se deve ser testado se o aluno possui os pré-requisitos necessários para matricular-se na turma corrente
			if (restricoes.isPreRequisitos()) {
				validarPreRequisitos(discente.getId(), turma, componentesPagos, erros, discente.getDiscente());
				validarPreRequisitoEspecifico(discente.getDiscente(), turma, componentesPagos, erros);
			}
			
			// verifica se o choque de horário enter turmas a serem matriculadas deve ser considerado
			if (restricoes.isChoqueHorarios()) {
				validarChoqueHorarios(turma, turmasDoSemestre, erros);
			}
			
			if (restricoes.isAlunoOutroCampus() && discente.isGraduacao() && !discente.isDiscenteEad() && discente.getCurso() != null && !discente.getCurso().isProbasica() ) {
				validarAlunoOutroCampus(turma, discente, erros);
			}
			
			// verifica se a existência de trancamento de programa no ano-período da matrícula deve impedir a mesma de ser efetuada
			if(restricoes.isTrancamentoPrograma()){
				validarTrancamentoPrograma(turma, discente, erros);
			}
			
			validarAlunoCadastrado(discente, turma.getAno(), turma.getPeriodo(), erros);
			
			return erros;
	}

	/**
	 * Valida se o discente possui trancamento de programa no ano-período da matrícula.
	 * @param turma
	 * @param discente
	 * @param erros
	 */
	public static void validarTrancamentoPrograma(Turma turma, DiscenteAdapter discente, ListaMensagens erros) throws DAOException {
		MovimentacaoAlunoDao dao = getDAO(MovimentacaoAlunoDao.class);
		try{
			if(discente.isStricto()) {
				Collection<MovimentacaoAluno> trancamentos = dao.findByDiscenteOrTipoMovimentacao(discente.getId(), TipoMovimentacaoAluno.TRANCAMENTO, false, 0, discente.getNivel(), null);
				for(MovimentacaoAluno tranc: trancamentos){
					if(tranc.getAnoReferencia() != null && tranc.getPeriodoReferencia() != null 
							&& tranc.getAnoReferencia() == turma.getAno() && tranc.getPeriodoReferencia() == turma.getPeriodo()){
						erros.addErro("Não é possível matricular o discente, pois ele possui trancamento de vínculo no ano-período "+turma.getAnoPeriodo());
					} else if(turma.getDataInicio() != null  && turma.getDataFim() != null 
                            && CalendarUtils.compareTo(tranc.getInicioAfastamento(), turma.getDataInicio()) <= 0
                            && CalendarUtils.compareTo(CalendarUtils.adicionaMeses(tranc.getInicioAfastamento(), tranc.getValorMovimentacao()), turma.getDataFim()) >= 0){
						Formatador f = Formatador.getInstance();
						erros.addErro("Não é possível matricular o discente pois, ele possui trancamento de vínculo " +
								"no período de "+f.formatarData(tranc.getInicioAfastamento())+" a "
								+f.formatarData(CalendarUtils.adicionaMeses(tranc.getInicioAfastamento(), tranc.getValorMovimentacao())));
					}
						
				}
			} else {
				if(dao.findTracamentoByDiscente(discente, turma.getAno(), turma.getPeriodo(), false) != null)
					erros.addErro("Não é possível matricular o discente pois, ele possui trancamento de programa no ano-período "+turma.getAnoPeriodo());
			}
		}finally{
			dao.close();
		}
		
	}

	/**
	 * Valida se a turma selecionada é oferecida no campus cujo município
	 * corresponde ao município da sede do curso do discente.
	 *
	 * @param turma
	 * @param discente
	 * @param erros
	 * @throws DAOException
	 */
	public static void validarAlunoOutroCampus(Turma turma, DiscenteAdapter discente, ListaMensagens erros) throws DAOException {
		if( discente.getCurso() == null )
			return;

		EstruturaCurricularDao dao = getDAO(EstruturaCurricularDao.class);
		UnidadeDao unDao = getDAO(UnidadeDao.class);
		try {

			Curso cursoDiscente = dao.findByPrimaryKey(discente.getCurso().getId(), Curso.class, "id", "unidade.id", "unidade.municipio.id", "municipio.id", "municipio.nome");
			Unidade unidadeCursoDiscente = cursoDiscente.getUnidade();
			Municipio municipioDiscente = cursoDiscente.getMunicipio();

			Unidade unidadeComponenteTurma = null;
			if (turma.getId() != 0) {
				if (turma.getCampus() != null){
					// Matrícula em turma
					turma = dao.findByPrimaryKey(turma.getId(), Turma.class, "id", "disciplina.id", "disciplina.codigo", "disciplina.unidade.id", "campus.endereco.municipio.id", "campus.endereco.municipio.nome" );
				}
				unidadeComponenteTurma = turma.getDisciplina().getUnidade(); // Departamento ou Unidade Acadêmica Especializada
			} else {
				// Matrícula em atividades
				ComponenteCurricular componente = dao.findByPrimaryKey(turma.getDisciplina().getId(), ComponenteCurricular.class, "id", "codigo", "unidade.id");
				unidadeComponenteTurma = componente.getUnidade(); 
			}
			
			//Retorna null se a unidade não possuir município
			Unidade unidadeGestoraComponenteTurma = unDao.findGestoraByUnidade(unidadeComponenteTurma);
			Municipio municipioTurma = ( turma.getCampus() != null ? turma.getCampus().getEndereco().getMunicipio()
				: ( unidadeGestoraComponenteTurma != null ? unidadeGestoraComponenteTurma.getMunicipio() : null ) );
				
			if( isEmpty(municipioTurma) ){
				erros.addErro("Não é possível matricular-se pois a unidade gestora da turma selecionada " +
						"	não possui município definido. Entre em contato com suporte.");
			}else if ( unidadeCursoDiscente.getId() != unidadeComponenteTurma.getId()	
				&& ( unidadeGestoraComponenteTurma != null && unidadeCursoDiscente.getId() != unidadeGestoraComponenteTurma.getId() )
				&& !dao.containsComponenteByCurso(cursoDiscente.getId(), turma.getDisciplina().getId())
				&& !isMunicipioDiscenteInRegiaoMatricula(municipioDiscente, municipioTurma) ){
				erros.addErro("Não é possível matricular-se em turmas de " +  turma.getDisciplina().getCodigo()  +
						" (" + municipioTurma.getNome() +  "), pois ela não é oferecida no mesmo município ou região do campus" +
						" do seu curso (" + municipioDiscente.getNome() + ").");
			}
				
		} finally {
			dao.close();
			unDao.close();
		}
	}

	/**
	 * Verifica se o aluno informado pode selecionar o componente para matrícula
	 * de acordo com os componentes já selecionados na matrícula atual e com
	 * todos os componentes que este já cursou, além de um conjunto de
	 * restrições definido pelo tipo de matrícula que está sendo realizada
	 * (Regular, Fora de prazo, Compulsória, etc.)
	 * 
	 * @param discente
	 * @param componente
	 * @param componentes
	 * @param todosComponentes
	 * @param restricoes
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static ListaMensagens validarComponenteIndividualmente(DiscenteAdapter discente, ComponenteCurricular componente, int ano, int periodo, Collection<ComponenteCurricular> componentes,
			Collection<ComponenteCurricular> todosComponentes, RestricoesMatricula restricoes) throws ArqException, NegocioException {
		ArrayList<Turma> turmas = new ArrayList<Turma>(0);
		for (ComponenteCurricular c : componentes) {
			if (c != null) {
				Turma turma = new Turma();
				turma.setDisciplina(c);
				turmas.add(turma);
			}
		}
		
		Turma turma = new Turma();
		turma.setDisciplina(componente);
		turma.setAno(ano);
		turma.setPeriodo(periodo);
		ListaMensagens retorno = new ListaMensagens(validarTurmaIndividualmente(discente, turma, turmas, todosComponentes, restricoes).getMensagens());
		
		return retorno;
	}

	/**
	 * Retorna uma instância do DAO informado
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Verifica se a matrícula está sendo realizada antes do ano-período de ingresso do discente.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarAlunoCadastrado(DiscenteAdapter discente, int ano, int periodo, ListaMensagens erros) throws DAOException, NegocioException {
		int anoPeriodoAtual = new Integer(ano + "" + periodo);
		int anoPeriodoIngresso = new Integer(discente.getAnoIngresso() + "" + discente.getPeriodoIngresso());
		if (anoPeriodoAtual < anoPeriodoIngresso) {
			erros.addErro("Não é possível realizar matrícula para esse discente.<br/>" +
					"O semestre atual ainda não corresponde ao seu semestre de ingresso.");
		}

	}

	/**
	 * Validar se, caso uma turma de subunidade tenha sido selecionada, todas as
	 * outras subunidades do mesmo bloco também foram.
	 * 
	 * @param turmas
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarMatriculaTurmasBloco(Collection<Turma> turmas, ListaMensagens erros) throws DAOException, NegocioException {

		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);

		try {
			List<ComponenteCurricular> blocosMatriculados = new ArrayList<ComponenteCurricular>();
			List<ComponenteCurricular> componentesMatriculados = new ArrayList<ComponenteCurricular>();
			
			// Percorre as turmas matriculadas para pegar informações sobre os componentes curriculares
			for (Turma t : turmas) {
				componentesMatriculados.add(t.getDisciplina());
				
				// Verifica quais as turmas do discente que são turmas de bloco
				if (t.getDisciplina().isSubUnidade()) {
					ComponenteCurricular bloco = dao.findByPrimaryKey(t.getDisciplina().getId(), ComponenteCurricular.class).getBlocoSubUnidade();
					if (!blocosMatriculados.contains(bloco))
						blocosMatriculados.add(bloco);
				}
			}
			
			// Para cada bloco, verifica se o discente se matriculou em todas as suas sub-unidades
			for (ComponenteCurricular bloco : blocosMatriculados) {
				boolean erroNoBloco = false;
				StringBuilder mensagemErro = new StringBuilder("Não foi possível realizar a matrícula, pois foi realizada a matrícula de um componente do bloco "
						+ bloco.getCodigoNome() + " e por isso deve se realizar a matrícula no(s) componente(s) ");
				List<String> blocosNecessarios = new ArrayList<String>();
				
				for (ComponenteCurricular subUnidade : bloco.getSubUnidades()) {
					if (!componentesMatriculados.contains(subUnidade)) {
						erroNoBloco = true;
						blocosNecessarios.add(subUnidade.getCodigo());
					}
				}
				
				if (erroNoBloco) {
					erros.addErro(mensagemErro.toString() + StringUtils.transformaEmLista(blocosNecessarios) + ".");
				}
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Método responsável por verificar se o município do discente faz parte da região de matrícula do campus da turma.
	 * @param municipioDiscente
	 * @param campusTurma
	 * @return
	 * @throws DAOException 
	 */
	public static boolean isMunicipioDiscenteInRegiaoMatricula(Municipio municipioDiscente, Municipio municipioTurma) throws DAOException{
		RegiaoMatriculaDao rmDao = getDAO(RegiaoMatriculaDao.class);	
		
		try {
			Collection<RegiaoMatricula>	regioesMatricula = rmDao.findRegiaoMatriculaByCampus(null, municipioTurma);
			Collection<RegiaoMatriculaCampus> regioesCampus = new ArrayList<RegiaoMatriculaCampus>();
			Collection<Municipio> municipiosRegiao = new ArrayList<Municipio>();
			
			for (RegiaoMatricula regiaoMatricula : regioesMatricula) {
				regioesCampus.addAll(regiaoMatricula.getRegioesMatriculaCampus());
			}
			for (RegiaoMatriculaCampus rmc : regioesCampus) {
				municipiosRegiao.add(rmc.getCampusIes().getEndereco().getMunicipio());
			}
			return municipiosRegiao.contains(municipioDiscente);
		} finally {
			rmDao.close();
		}
	}
	
	/**
	 * Verifica a sugestão de componentes curriculares, para listar apenas as turmas com reserva 
	 * para o curso e matriz curricular do discente, quando houver..
	 * <br/>
	 * Método não chamado por JSP.
	 * @throws ArqException
	 */
	public static void verificarReservasCurso(Collection<SugestaoMatricula> turmasCurriculo, DiscenteAdapter discente) throws ArqException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			if (turmasCurriculo != null) {
				
				Collection<Turma> turmas = new ArrayList<Turma>();
				for (SugestaoMatricula sug : turmasCurriculo) {
					turmas.add(sug.getTurma());
				}
				popularReservasTurma(turmas);
				MatrizCurricular matrizDiscente = dao.findByPrimaryKey(discente.getId(),DiscenteGraduacao.class).getMatrizCurricular();
				
				for (Iterator<SugestaoMatricula> iterator = turmasCurriculo.iterator(); iterator.hasNext();) {
					SugestaoMatricula sugestao = iterator.next();
					if(isNotEmpty(sugestao.getTurma().getReservas()) &&
							naoPossuiReservasParaMatriz(matrizDiscente, sugestao.getTurma().getReservas())){
						iterator.remove();
					}
				}
			}	
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Verifica a sugestão de componentes curriculares, para listar apenas as turmas com reserva 
	 * para o curso e matriz curricular do discente, quando houver..
	 * <br/>
	 * Método não chamado por JSP.
	 * @param erros 
	 * @throws ArqException
	 */
	public static void verificarExtrasReservasCurso(Collection<Turma> resultadoTurmasBuscadas, DiscenteAdapter discente, ListaMensagens erros) throws ArqException {
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			if (resultadoTurmasBuscadas != null) {
				
				popularReservasTurma(resultadoTurmasBuscadas);
				MatrizCurricular matrizDiscente = dao.findByPrimaryKey(discente.getId(),DiscenteGraduacao.class).getMatrizCurricular();
				
				for (Iterator<Turma> iterator = resultadoTurmasBuscadas.iterator(); iterator.hasNext();) {
					Turma turma = iterator.next();
					if(isNotEmpty(turma.getReservas()) &&
							naoPossuiReservasParaMatriz(matrizDiscente,turma.getReservas())){
						iterator.remove();
					}
				}
				if(isEmpty(resultadoTurmasBuscadas))
					erros.addWarning("Atenção! Não foram encontradas turmas com reservas para o discente selecionado.");
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Verifica a sugestão de componentes curriculares equivalentes, para listar apenas as turmas com reserva 
	 * no curso e matriz curricular do discente, quando houver reserva.
	 * <br/>
	 * Método não chamado por JSP.
	 * @throws ArqException
	 */
	public static void verificarEquivalenciasReservasCurso(Collection<SugestaoMatriculaEquivalentes> turmasEquivalentesCurriculo, DiscenteAdapter discente) throws ArqException {
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		try {
			if (isNotEmpty(turmasEquivalentesCurriculo)) {
				
				Set<Turma> turmas = new HashSet<Turma>();
				for (SugestaoMatriculaEquivalentes sugEquiv : turmasEquivalentesCurriculo) {
					for (SugestaoMatricula sug : sugEquiv.getSugestoes()) {
						turmas.add(sug.getTurma());
					}
				}
				popularReservasTurma(turmas);
				MatrizCurricular matrizDiscente = dao.findByPrimaryKey(discente.getId(),DiscenteGraduacao.class).getMatrizCurricular();
				
				for (Iterator<SugestaoMatriculaEquivalentes> iterator = turmasEquivalentesCurriculo.iterator(); iterator.hasNext();) {
					SugestaoMatriculaEquivalentes sugestaoEquivalente = iterator.next();
					for (Iterator<SugestaoMatricula> itSugestao = sugestaoEquivalente.getSugestoes().iterator(); itSugestao.hasNext();) {
						SugestaoMatricula sugestao = itSugestao.next();
						if(isNotEmpty(sugestao.getTurma().getReservas()) &&
								naoPossuiReservasParaMatriz(matrizDiscente, sugestao.getTurma().getReservas())){
							itSugestao.remove();
						}
					}
				}	
			}	
		} finally {
			dao.close();
		}

	}
	
	/** 
	 * Método utilizado para verificar se a turma de sugestão possui reservas para a matriz curricular do discente.
	 * @param matriz
	 * @param reservas
	 * @return
	 */
	public static boolean naoPossuiReservasParaMatriz( MatrizCurricular matriz, Collection<ReservaCurso> reservas){
		boolean naoPossuiReserva = true;
		for (ReservaCurso reservaCurso : reservas) {
			if (reservaCurso.getMatrizCurricular().getId() == matriz.getId() )
				naoPossuiReserva = false;
		} 
		return naoPossuiReserva;
	}
	
	/** 
	 * Método utilizado para popular as reservas de curso das turmas de sugestão para matrícula. 
	 * @param turmasCurriculo
	 * @throws DAOException
	 */
	public static void popularReservasTurma(Collection<Turma> turmasCurriculo) throws DAOException{
		ReservaCursoDao rcDao = getDAO(ReservaCursoDao.class);
		try {
			if (turmasCurriculo != null) {
				List<Integer> idTurmas = new ArrayList<Integer>();
				for (Turma t : turmasCurriculo) {
					idTurmas.add(t.getId());
				}
				if (isEmpty(idTurmas) )
					return;
				Collection<ReservaCurso> reservas = rcDao.findByTurmas(idTurmas);
				for (Turma turma : turmasCurriculo) {
					for (ReservaCurso reserva : reservas) {
						if (turma.getId() == reserva.getTurma().getId())
							turma.getReservas().add(reserva);
					}
				}	
			}
		} finally {
			rcDao.close();
		}
	}
}
