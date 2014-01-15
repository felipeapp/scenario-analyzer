/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Respons�vel pelas valida��es de matr�cula de gradua��o tais como: Para que a
 * capacidade da turma n�o seja excedida com mais matr�cula, valida os
 * pr�-requisitos para se matricular na turma, valida os componentes
 * equivalentes, valida o n�mero m�ximo de matriculas por docente, valida para
 * evitar choque de hor�rio, dentre outras valida��es realizadas.
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
				String msgConteudo = "O discente n�o pode cursar a turma " + turma.getCodigo() + 
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
	 * Apenas alerta sobre a capacidade da turma, incluindo as desist�ncias
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
					erros.addWarning("Aten��o! A turma selecionada para o componente " + turma.getDisciplina().getCodigo() + 
							" j� teve sua capacidade de " + capacidadeTurma +
							" alunos atingida. Apesar se ser permitido solicitar matr�cula para ela � necess�rio lembrar " +
							" que a possibilidade da matr�cula ser deferida depende da desist�ncia de algum dos discente j� matriculados.");
				} else if ( matriculados + desistentes + 1 > capacidadeTurma && desistente == null)  {
					erros.addWarning("Aten��o! A turma selecionada para o componente " + turma.getDisciplina().getCodigo() + 
							" possui " + desistentes + " vaga(s) dispon�vel(is) devido � desist�ncia de alunos matriculados durante o processamento." +
							" A exist�ncia desta(s) vaga(s) pode ser alterada caso as desist�ncias sejam canceladas pelos discentes." +
							" Neste caso a turma voltar� a ter a ocupa��o original de " + (matriculados + desistentes) + 
							" alunos.");
				}
			}
		} finally {
			dao.close();
			sDao.close();
		}
	}

	/**
	 * Valida caso o componente da turma passado por par�metro n�o possui seus
	 * pr�-requisitos dentro da cole��o de componentes tamb�m passado por
	 * par�metro.
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
	 * Valida caso o componente da turma passado por par�metro n�o possui seus
	 * pr�-requisitos dentro da cole��o de componentes tamb�m passado por
	 * par�metro.
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
				msgConteudo.append(" n�o pode cursar o componente ");
				msgConteudo.append(turma.getDisciplina().getDescricaoResumida());
				msgConteudo.append(", pois n�o possui os pr�-requisitos necess�rios.<br/>");
				erros.addErro(msgConteudo.toString());

		}
	}

	/**
	 * Valida se o discente informado possui os preRequisitos espec�ficos cadastrados para a turma passada no par�metro
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
					String msgConteudo = "O Discente n�o pode cursar o componente "
						+ turma.getDisciplina().getDescricaoResumida() + " pois n�o possui os pr�-requisitos necess�rios: " + preRequisitoFormatado;
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
	 * Valida se o discente informado possui os coRequisitos espec�ficos
	 * cadastrados para a turma passada no par�metro
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
					String msgConteudo = "O discente n�o pode cursar turmas do componente " +
						turma.getDisciplina().getDescricaoResumida() + " pois n�o possui os co-requisitos necess�rios: " +
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
	 * Valida caso o componente da turma passado por par�metro seja equivalente a
	 * qualquer componente dentro da cole��o de componentes tamb�m passado por
	 * par�metro.
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
				String msgConteudo =  "O Discente  n�o pode cursar turmas do componente "
						+ turma.getDisciplina().getDescricaoResumida() + " pois j� cursou ou est� matriculado em componentes equivalentes.<br/>";
				erros.addErro(msgConteudo);
			}
		} finally {
			ccdao.close();
		}
	}

	/**
	 * Verifica se o componente curricular informado possui alguma equival�ncia
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
	 * Erro caso o discente passado por par�metro tenha excedido o n�mero de
	 * vezes permitido se matricular em turmas de um componente, mesmo j� tendo
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
				erros.addErro("O Discente  j� cursou o componente " + componente.getDescricaoResumida() + "<br/>");								
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
	 * Valida caso o componente passado por par�metro esteja presente na cole��o
	 * de turmas tamb�m passada por par�metro.
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
	 * Valida caso o componente passado por par�metro esteja presente na cole��o
	 * de turmas tamb�m passada por par�metro.
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
					msgConteudo.append(" j� est� matriculado, tentando se matricular, foi solicitada exclus�o ou foi negada solicita��o de matr�cula em " +
					"turmas do componente: ")
					.append(turma.getDisciplina().getDescricaoResumida())
					.append(".<br/> Turma: ")
					.append(t.getCodigo());
				} else {
					msgConteudo.append("O discente");
					if (discente != null)
						msgConteudo.append(" ").append(discente.getMatriculaNome());
					msgConteudo.append(" j� est� matriculado (ou est� tentando se matricular) " +
					" em mais de uma turma do componente: ")
					.append(turma.getDisciplina().getDescricaoResumida())
					.append("<br/>");					
				}				
				erros.addErro(msgConteudo.toString());
			}
		}
	}

	/**
	 * Valida caso o componente da turma passado por par�metro n�o possuir seus
	 * co-requisitos dentro da cole��o de componentes tamb�m passado por
	 * par�metro.
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
				String msgConteudo = "O discente n�o pode cursar turmas do componente " +
					turma.getDisciplina().getDescricaoResumida() + " pois n�o possui os co-requisitos necess�rios: " +
					coRequisitoFormatado;
				erros.addErro(msgConteudo);
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Verifica se h� choque de hor�rio entre a turma e alguma turma da cole��o
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
			StringBuffer msg = new StringBuffer("Ocorreram choque de hor�rios com as turmas: ");
			for (Turma t : horariosChocados) {
				msg.append("<br>" + turma.getDisciplina().getCodigo() + " - Turma "+turma.getCodigo()+" ("+turma.getDescricaoHorario()+") e "
								+ t.getDisciplina().getCodigo() + " - Turma "+t.getCodigo() +
								" (" + t.getDescricaoHorario() + ")");
			}
			erros.addErro(msg.toString()+ "<BR>");
		}
	}

	/**
	 * Verifica se h� choque de hor�rio entre a turma e as solicita��es de
	 * matricula do discente inicialmente utilizado apenas na solicita��o de
	 * turma de ensino individual, deve verificar se existe choque de hor�rio
	 * entre a turma de ensino individual que esta sendo solicitada e as
	 * solicita��es de matricula do discente
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
				StringBuffer msg = new StringBuffer("Ocorreram choque de hor�rios com as solicita��es de turmas realizada pelo discente " + discente.toString() + ":");
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
	 * Verifica se o aluno de gradua��o informado excedeu o limite de cr�ditos
	 * de componentes curriculares fora da sua estrutura curricular definido
	 * pela sua unidade gestora acad�mica.
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
				//Evitar Erro de NP nos aproveitamentos, que n�o tem turma.
				if(turmas!=null){				
					for(Turma t : turmas) {					
						if(t.isMatricular() && !disciplinasCurriculo.contains(t.getDisciplina())) {						
							validar = true;					
						}
					}				
				}
				
				int max = 0;
				// Nunca deve vir nulo, colocado por precau��o.
				if (curriculo != null && curriculo.getMaxEletivos() > 0){
					ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(curriculo.getCurso().getNivel());
					max =  curriculo.getMaxEletivos() / parametros.getHorasCreditosAula();
				} else {
					ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(discente);
					max = parametros.getMaxCreditosExtra();					
				}
				
				if (discente.getCrExtraIntegralizados() > max && validar) {
					String msgConteudo = "Foi excedido o limite m�ximo de " +
					max + " cr�ditos de componentes curriculares fora da estrutura curricular do discente.";
					erros.addErro(msgConteudo);
				}
			} finally {
				if (dao != null)
					dao.close();
			}
		}
	}

	/**
	 * Verifica se o aluno informado excedeu o limite de cr�ditos por semestre
	 * definido no seu curr�culo.
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
	 * Verifica se o aluno informado excedeu o limite de cr�ditos por semestre
	 * definido no seu curr�culo.
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
				String msg = "O Discente excedeu o limite m�ximo de cr�dito por semestre " +
					"permitido pelo seu curr�culo.<br>" +
					"Est� tentando se matricular no total de " + crs + " cr�ditos, enquanto o m�ximo permitido " +
					" � de "+limiteMaximoCreditoSemestre+" cr�ditos no semestre";
				throw new NegocioException(msg);
			}
		}
	}



	/**
	 * Valida o limite m�nimo de cr�ditos que um discente deve cumprir em um semestre
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
	 * Valida o limite m�nimo de cr�ditos que um discente deve cumprir em um semestre
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
				String msg = "O Discente n�o atingiu o limite m�nimo de cr�ditos ou carga hor�ria por semestre permitido pelo seu curr�culo.<br>"
						+ "Est� tentando se matricular no total de " + crs + " cr�ditos "
						+ "e " +  ch + " horas, enquanto o m�nimo exigido "
						+ " � de " + limiteMinimoCreditoSemestre + " cr�ditos "
						+ "ou " + limiteMinimoCargaHorariaSemestre + " horas, no semestre.";
				throw new NegocioException(msg);
			}
		}
	}


	/**
	 * Valida caso alguma das duas regras que envolvem a matr�cula de aluno especial
	 * sejam quebradas:
	 * 1 - n�mero m�ximo de matr�culas
	 * 2 - n�mero m�ximo de per�odos cursados
	 *
	 * @param discente
	 * @param totalMatriculadosNoSemestre
	 * @param totalPeriodosCursados
	 * @param erros
	 * @throws ArqException
	 */
	public static void validarAlunoEspecial(DiscenteAdapter discente, int totalMatriculadosNoSemestre, int totalPeriodosCursados, ListaMensagens erros) throws ArqException {
		// Validar regras de neg�cio do aluno especial
		if (discente.getTipo() == Discente.ESPECIAL) {
			
			DiscenteDao dao = getDAO(DiscenteDao.class);
			try {
				
				Discente discenteFormaIngresso = dao.findAndFetch(discente.getId(), Discente.class, "formaIngresso");
				
				Integer maxDisciplinas = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getMaxDisciplinas();
				Integer maxPeriodos = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getMaxPeriodos();
				String categoria = discenteFormaIngresso.getFormaIngresso().getCategoriaDiscenteEspecial().getDescricao();
				
				if (maxDisciplinas != null && totalMatriculadosNoSemestre > maxDisciplinas) {
					String msg = "O Aluno Especial " + categoria  
							+ " s� pode se matricular em, no m�ximo, " + maxDisciplinas
							+ " disciplinas por per�odo.";
					erros.addErro(msg);
				}
	
				if (maxPeriodos != null && totalPeriodosCursados >= maxPeriodos) {
					String msg = "O Aluno Especial " + categoria 
						+ " s� pode cursar no m�ximo " + maxPeriodos
						+ " per�odos, consecutivos ou n�o.";
					erros.addErro(msg);
				}
			} finally {
				dao.close();
			}

		}
	}

	/**
	 * Verifica se � permitido fazer matr�cula de aluno regular no momento.
	 * Caso n�o possa, � jogada uma exce��o.
	 *
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarPeriodoRegular(MovimentoMatriculaGraduacao mov) throws NegocioException,
			ArqException {
		if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_PROGRAMA_RESIDENCIA, SigaaPapeis.SECRETARIA_RESIDENCIA ) )
			return; /* n�o valida per�odo no calend�rio caso a matricula esteja sendo realizada por
			PPG, COORDENADOR_CURSO_STRICTO, SECRETARIA_POS, COORDENADOR_PROGRAMA_RESIDENCIA ou SECRETARIA_RESIDENCIA  */
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(mov.getDiscente());
		try {
			if (!cal.isPeriodoMatriculaRegular()) {
				throw new NegocioException();
			}
		} catch (NegocioException e) {
			e.addErro("N�o � permitido realizar matr�culas de alunos regulares fora do " +
						"per�odo determinado no calend�rio universit�rio");
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Ocorreu um erro na valida��o do per�odo para matr�cula");
		}

	}

	/**
	 * Verifica se � permitido fazer matr�cula de aluno especial no momento.
	 * Caso n�o possa, � jogada uma exce��o.
	 *
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public static void validarPeriodoEspecial(MovimentoMatriculaGraduacao mov) throws NegocioException,
			ArqException {

		if( mov.getUsuarioLogado().isUserInRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS ) )
			return; /* n�o valida per�odo no calend�rio caso a matricula esteja sendo realizada por
			PPG, COORDENADOR_CURSO_STRICTO, SECRETARIA_POS  */

		CalendarioAcademico cal = mov.getCalendarioAcademicoAtual();
		try {
			if (!cal.isPeriodoMatriculaAlunoEspecial()) {
				throw new NegocioException("N�o � permitido realizar matr�culas de alunos especiais fora do " +
						"per�odo determinado no calend�rio universit�rio");
			}
		} catch (NegocioException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new ArqException("Ocorreu um erro na valida��o do per�odo para matr�cula");
		}
	}

	/**
	 * Checa permiss�es de acesso � opera��o de: Matr�cula de alunos regulares.
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
					"Usu�rio n�o possui permiss�o para realizar matr�culas");
	}

	/**
	 * Verifica se o usu�rio que est� realizando o movimento possui permiss�o de
	 * efetuar matr�cula regular dos alunos do ensino a dist�ncia.
	 * 
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisRegularEAD(MovimentoMatriculaGraduacao mov)  throws SegurancaException {
		checaPapeisRegularEAD(mov.getUsuarioLogado());
	}

	/**
	 * Verifica se o usu�rio que est� realizando o movimento possui permiss�o de
	 * efetuar matr�cula regular dos alunos do ensino a dist�ncia.
	 * 
	 * @param usuario
	 * @throws SegurancaException
	 */
	public static void checaPapeisRegularEAD(UsuarioGeral usuario)  throws SegurancaException {
		if (!usuario.isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.ADMINISTRADOR_DAE))
			throw new SegurancaException(
					"Usu�rio n�o possui permiss�o para realizar matr�culas");
	}

	/**
	 * Verifica se o usu�rio que est� realizando o movimento possui permiss�o de
	 * efetuar matr�cula regular dos alunos do conv�nio (PROBASICA ou PRONERA).
	 * 
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisConvenio(MovimentoMatriculaGraduacao mov)  throws SegurancaException {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.GESTOR_PROBASICA, SigaaPapeis.SECRETARIA_COORDENACAO))
			throw new SegurancaException(
					"Usu�rio n�o possui permiss�o para realizar matr�culas");
	}

	/**
	 * Checa permiss�es de acesso � opera��o de Matr�cula de alunos especiais
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisEspecial(MovimentoMatriculaGraduacao mov) throws SegurancaException  {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG))
			throw new SegurancaException(
					"Usu�rio n�o possui permiss�o para realizar matr�culas de aluno especial");
	}

	/**
	 * Checa permiss�es de acesso � opera��o de matr�cula fora do prazo.
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisForaPrazo(MovimentoMatriculaGraduacao mov) throws SegurancaException {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE))
			throw new SegurancaException(
					"Usu�rio n�o possui permiss�o para realizar matr�culas fora do prazo");
	}

	/**
	 * Checa permiss�es de acesso � opera��o de matr�cula compuls�rias.
	 *
	 * @param mov
	 * @throws SegurancaException
	 */
	public static void checaPapeisCompulsoria(MovimentoMatriculaGraduacao mov) throws SegurancaException  {
		if (!mov.getUsuarioLogado().isUserInRole(SigaaPapeis.DAE, SigaaPapeis.CDP))
			throw new SegurancaException(
					"Usu�rio n�o possui permiss�o para realizar matr�culas compuls�ria");
	}

	/**
	 * Com base nas restri��es passadas, s�o feitas as valida��es se o discente
	 * passado por par�metro pode se matricular.
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
	 * Com base nas restri��es passadas, s�o feitas as valida��es se o discente
	 * passado por par�metro pode se matricular.
	 * 
	 * @param discente
	 * @param turma
	 *            - turma a ser verificada
	 * @param turmasDoSemestre
	 * @param todosComponentes
	 *            - todos os componentes j� aprovados (ou aproveitados)
	 *            inclusive do semestre corrente
	 * @param restricoes
	 *            - o conjunto de restri��es que devem ser analisados
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

		// no modelo antigo de verifica��o de cr�ditos
		if (restricoes.getValorMaximoCreditos() == null && restricoes.getValorMinimoCreditos() == null) {
			if (restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
				validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal);
			}
		} else {
			// no modelo novo de verifica��o de cr�ditos
			if (restricoes.getValorMaximoCreditos() != null && restricoes.isLimiteMaxCreditosSemestre() && discente.isGraduacao()) {
				validarLimiteMaximoCreditosSemestre(discente, turmasDoSemestre, cal, restricoes.getValorMaximoCreditos());
			}
		}
			
			// verifica se deve ser testado a capacidade da turma
			if (restricoes.isCapacidadeTurma()) {
				validarCapacidadeTurma(turma, erros);
			}
			
			// Verifica se o usu�rio deve ser alertado sobre a capacidade
			if (restricoes.isAlertaCapacidadeTurma()) {
				alertarCapacidadeTurma(discente, turma, erros);
			}
			
			// verifica se deve ser testado se o discente j� pagou esse componente ou se est� matriculado nele.
			if (restricoes.isMesmoComponente() && !restricoes.isPermiteDuplicidadeCasoConteudoVariavel()) {
				/* verifica se o limite m�ximos de matr�culas (aprovadas ou aproveitadas)
				 * do Componente foi excedido.
				 * ATEN��O: essa verifica��o � relacionada com o limite vinculado diretamente ao componente,
				 * existem componentes em que se � permitido ser aprovado mais de uma vez
				 */
				validarLimiteMaxMatriculas(turma, discente, turmasDoSemestre, restricoes, erros);
				if(discente.getNivel() != NivelEnsino.TECNICO && turma.getDisciplina().getQtdMaximaMatriculas() == 1) {						
					//  verifica se o aluno j� est� matriculado em uma turma desse componente
					validarMatriculadasSemestre(turma, turmasDoSemestre, restricoes,erros, discente.getDiscente());					
				}
				// verifica se o aluno j� possui uma matricula aprovada ou aproveitada de um componente equivalente
				validarEquivalencia(discente, turma, todosComponentes, erros);
			}
			
			if (discente.getNivel() == NivelEnsino.TECNICO && turma.getEspecializacao() != null) {
				MatriculaTecnicoValidator.validarEspecializacaoTurma(discente, turma, erros);
			}
			
			// verifica se deve ser testado se o discente est� se matriculando nos co-requisitos necess�rios
			if (restricoes.isCoRequisitos()) {
				validarCoRequisitos(discente.getId(), turma, todosComponentes, erros);
				validarCoRequisitosEspecifico(discente.getDiscente(), turma, todosComponentes, erros);
			}
			
			// verifica se deve ser testado se o aluno possui os pr�-requisitos necess�rios para matricular-se na turma corrente
			if (restricoes.isPreRequisitos()) {
				validarPreRequisitos(discente.getId(), turma, componentesPagos, erros, discente.getDiscente());
				validarPreRequisitoEspecifico(discente.getDiscente(), turma, componentesPagos, erros);
			}
			
			// verifica se o choque de hor�rio enter turmas a serem matriculadas deve ser considerado
			if (restricoes.isChoqueHorarios()) {
				validarChoqueHorarios(turma, turmasDoSemestre, erros);
			}
			
			if (restricoes.isAlunoOutroCampus() && discente.isGraduacao() && !discente.isDiscenteEad() && discente.getCurso() != null && !discente.getCurso().isProbasica() ) {
				validarAlunoOutroCampus(turma, discente, erros);
			}
			
			// verifica se a exist�ncia de trancamento de programa no ano-per�odo da matr�cula deve impedir a mesma de ser efetuada
			if(restricoes.isTrancamentoPrograma()){
				validarTrancamentoPrograma(turma, discente, erros);
			}
			
			validarAlunoCadastrado(discente, turma.getAno(), turma.getPeriodo(), erros);
			
			return erros;
	}

	/**
	 * Valida se o discente possui trancamento de programa no ano-per�odo da matr�cula.
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
						erros.addErro("N�o � poss�vel matricular o discente, pois ele possui trancamento de v�nculo no ano-per�odo "+turma.getAnoPeriodo());
					} else if(turma.getDataInicio() != null  && turma.getDataFim() != null 
                            && CalendarUtils.compareTo(tranc.getInicioAfastamento(), turma.getDataInicio()) <= 0
                            && CalendarUtils.compareTo(CalendarUtils.adicionaMeses(tranc.getInicioAfastamento(), tranc.getValorMovimentacao()), turma.getDataFim()) >= 0){
						Formatador f = Formatador.getInstance();
						erros.addErro("N�o � poss�vel matricular o discente pois, ele possui trancamento de v�nculo " +
								"no per�odo de "+f.formatarData(tranc.getInicioAfastamento())+" a "
								+f.formatarData(CalendarUtils.adicionaMeses(tranc.getInicioAfastamento(), tranc.getValorMovimentacao())));
					}
						
				}
			} else {
				if(dao.findTracamentoByDiscente(discente, turma.getAno(), turma.getPeriodo(), false) != null)
					erros.addErro("N�o � poss�vel matricular o discente pois, ele possui trancamento de programa no ano-per�odo "+turma.getAnoPeriodo());
			}
		}finally{
			dao.close();
		}
		
	}

	/**
	 * Valida se a turma selecionada � oferecida no campus cujo munic�pio
	 * corresponde ao munic�pio da sede do curso do discente.
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
					// Matr�cula em turma
					turma = dao.findByPrimaryKey(turma.getId(), Turma.class, "id", "disciplina.id", "disciplina.codigo", "disciplina.unidade.id", "campus.endereco.municipio.id", "campus.endereco.municipio.nome" );
				}
				unidadeComponenteTurma = turma.getDisciplina().getUnidade(); // Departamento ou Unidade Acad�mica Especializada
			} else {
				// Matr�cula em atividades
				ComponenteCurricular componente = dao.findByPrimaryKey(turma.getDisciplina().getId(), ComponenteCurricular.class, "id", "codigo", "unidade.id");
				unidadeComponenteTurma = componente.getUnidade(); 
			}
			
			//Retorna null se a unidade n�o possuir munic�pio
			Unidade unidadeGestoraComponenteTurma = unDao.findGestoraByUnidade(unidadeComponenteTurma);
			Municipio municipioTurma = ( turma.getCampus() != null ? turma.getCampus().getEndereco().getMunicipio()
				: ( unidadeGestoraComponenteTurma != null ? unidadeGestoraComponenteTurma.getMunicipio() : null ) );
				
			if( isEmpty(municipioTurma) ){
				erros.addErro("N�o � poss�vel matricular-se pois a unidade gestora da turma selecionada " +
						"	n�o possui munic�pio definido. Entre em contato com suporte.");
			}else if ( unidadeCursoDiscente.getId() != unidadeComponenteTurma.getId()	
				&& ( unidadeGestoraComponenteTurma != null && unidadeCursoDiscente.getId() != unidadeGestoraComponenteTurma.getId() )
				&& !dao.containsComponenteByCurso(cursoDiscente.getId(), turma.getDisciplina().getId())
				&& !isMunicipioDiscenteInRegiaoMatricula(municipioDiscente, municipioTurma) ){
				erros.addErro("N�o � poss�vel matricular-se em turmas de " +  turma.getDisciplina().getCodigo()  +
						" (" + municipioTurma.getNome() +  "), pois ela n�o � oferecida no mesmo munic�pio ou regi�o do campus" +
						" do seu curso (" + municipioDiscente.getNome() + ").");
			}
				
		} finally {
			dao.close();
			unDao.close();
		}
	}

	/**
	 * Verifica se o aluno informado pode selecionar o componente para matr�cula
	 * de acordo com os componentes j� selecionados na matr�cula atual e com
	 * todos os componentes que este j� cursou, al�m de um conjunto de
	 * restri��es definido pelo tipo de matr�cula que est� sendo realizada
	 * (Regular, Fora de prazo, Compuls�ria, etc.)
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
	 * Retorna uma inst�ncia do DAO informado
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Verifica se a matr�cula est� sendo realizada antes do ano-per�odo de ingresso do discente.
	 * 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public static void validarAlunoCadastrado(DiscenteAdapter discente, int ano, int periodo, ListaMensagens erros) throws DAOException, NegocioException {
		int anoPeriodoAtual = new Integer(ano + "" + periodo);
		int anoPeriodoIngresso = new Integer(discente.getAnoIngresso() + "" + discente.getPeriodoIngresso());
		if (anoPeriodoAtual < anoPeriodoIngresso) {
			erros.addErro("N�o � poss�vel realizar matr�cula para esse discente.<br/>" +
					"O semestre atual ainda n�o corresponde ao seu semestre de ingresso.");
		}

	}

	/**
	 * Validar se, caso uma turma de subunidade tenha sido selecionada, todas as
	 * outras subunidades do mesmo bloco tamb�m foram.
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
			
			// Percorre as turmas matriculadas para pegar informa��es sobre os componentes curriculares
			for (Turma t : turmas) {
				componentesMatriculados.add(t.getDisciplina());
				
				// Verifica quais as turmas do discente que s�o turmas de bloco
				if (t.getDisciplina().isSubUnidade()) {
					ComponenteCurricular bloco = dao.findByPrimaryKey(t.getDisciplina().getId(), ComponenteCurricular.class).getBlocoSubUnidade();
					if (!blocosMatriculados.contains(bloco))
						blocosMatriculados.add(bloco);
				}
			}
			
			// Para cada bloco, verifica se o discente se matriculou em todas as suas sub-unidades
			for (ComponenteCurricular bloco : blocosMatriculados) {
				boolean erroNoBloco = false;
				StringBuilder mensagemErro = new StringBuilder("N�o foi poss�vel realizar a matr�cula, pois foi realizada a matr�cula de um componente do bloco "
						+ bloco.getCodigoNome() + " e por isso deve se realizar a matr�cula no(s) componente(s) ");
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
	 * M�todo respons�vel por verificar se o munic�pio do discente faz parte da regi�o de matr�cula do campus da turma.
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
	 * Verifica a sugest�o de componentes curriculares, para listar apenas as turmas com reserva 
	 * para o curso e matriz curricular do discente, quando houver..
	 * <br/>
	 * M�todo n�o chamado por JSP.
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
	 * Verifica a sugest�o de componentes curriculares, para listar apenas as turmas com reserva 
	 * para o curso e matriz curricular do discente, quando houver..
	 * <br/>
	 * M�todo n�o chamado por JSP.
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
					erros.addWarning("Aten��o! N�o foram encontradas turmas com reservas para o discente selecionado.");
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Verifica a sugest�o de componentes curriculares equivalentes, para listar apenas as turmas com reserva 
	 * no curso e matriz curricular do discente, quando houver reserva.
	 * <br/>
	 * M�todo n�o chamado por JSP.
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
	 * M�todo utilizado para verificar se a turma de sugest�o possui reservas para a matriz curricular do discente.
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
	 * M�todo utilizado para popular as reservas de curso das turmas de sugest�o para matr�cula. 
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
