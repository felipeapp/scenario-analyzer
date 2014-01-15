/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 01/11/2006
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validaInt;
import static br.ufrn.arq.util.ValidatorUtil.validaOrdemTemporalDatas;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoTurmaDao;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dao.GerenciarTurmaDao;
import br.ufrn.sigaa.ensino.dao.PlanoMatriculaIngressantesDao;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.GrupoHorarios;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.dominio.HorarioDocente;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.PlanoMatriculaIngressantes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dao.TurmaGraduacaoDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscentesSolicitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.ReservaCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoTurma;
import br.ufrn.sigaa.ensino.graduacao.negocio.MatriculaGraduacaoValidator;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerieAno;
import br.ufrn.sigaa.ensino.util.HorarioTurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.negocio.PeriodoAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que possui os m�todos de valida��o padr�o de objetos da classe Turma.
 * 
 * @author Victor Hugo
 *
 */
public class TurmaValidator {
	
	/** Retorna uma inst�ncia de um DAO especificado.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	
	/**
	 * Valida os dados b�sicos da turma, invocado ap�s a submiss�o da primeira tela no cadastro de turmas lato sensu.
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDadosBasicosResidenciaSaude(Turma turma, ListaMensagens lista) throws DAOException {
		
		if (turma.getDisciplina() == null || turma.getDisciplina().getId() <= 0) {
			turma.setDisciplina(new ComponenteCurricular());
			lista.addErro("Selecione um componente curricular.");
			return;
		}
		
		if( turma.getId() == 0 )
			validateRequiredId(turma.getTipo(), "Tipo da Turma", lista);
		
		validateRequired(turma.getAno(), "Ano", lista);
		
		if (!turma.isDistancia()) {
			validaInt(turma.getCapacidadeAluno(), "Capacidade de Alunos", lista);
			validateRequired(turma.getLocal(), "Local", lista);
		}
		
		validateRequired(turma.getDataInicio(), "In�cio", lista);
		validateMinValue(turma.getDataInicio(), CalendarUtils.createDate(1, 0, turma.getAno()), "In�cio", lista);
		validateRequired(turma.getDataFim(), "Fim", lista);
		validateMinValue(turma.getDataFim(), turma.getDataInicio(), "Fim", lista);
		
		if ( !turma.isDistancia() && turma.getId() != 0 )
			validateRequired(turma.getCodigo(), "C�digo da Turma", lista);
		
		
		if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
				turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
			lista.addErro("A capacidade da turma ("+turma.getCapacidadeAluno()+") n�o pode ser inferior � quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").");
		}
		
		if( !isEmpty(turma.getObservacao() ) )
			validateMaxLength(turma.getObservacao(), 100, "Observa��es", lista);
		
	}
	
	/**
	 * Valida os dados b�sicos da turma, invocado ap�s a submiss�o da primeira tela no cadastro de turmas lato sensu.
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDadosBasicosLatoSensu(Turma turma, ListaMensagens lista) throws DAOException {
		CursoLatoDao cursoLatoDao = null;
		try {
			cursoLatoDao = getDAO(CursoLatoDao.class);
			if (turma.getDisciplina() == null || turma.getDisciplina().getId() <= 0) {
				turma.setDisciplina(new ComponenteCurricular());
				lista.addErro("Selecione um componente curricular.");
				return;
			}
			
			if( turma.getId() == 0 )
				validateRequiredId(turma.getTipo(), "Tipo da Turma", lista);
			
			validateRequired(turma.getCurso(), "Curso", lista);
			
			validateRequired(turma.getDataInicio(), "In�cio", lista);
			validateRequired(turma.getDataFim(), "Fim", lista);
			validateMinValue(turma.getDataFim(), turma.getDataInicio(),"Fim" , lista);
			
			CursoLato cursoLato = null;
			if(turma.getCurso() != null)
				cursoLato = cursoLatoDao.findById(turma.getCurso().getId());
			
			// valida se o per�odo (in�cio e fim) da turma est� dentro do per�odo do curso
			if (turma.getDataInicio() != null && cursoLato != null)
				validateMinValue(turma.getDataInicio(), cursoLato.getDataInicio(), "In�cio", lista);
			if (turma.getDataFim() != null && cursoLato != null)
				validateMaxValue(turma.getDataFim(), cursoLato.getDataFim(), "Fim", lista);
			
			if ( !turma.isDistancia() && turma.getId() != 0 )
				validateRequired(turma.getCodigo(), "C�digo da Turma", lista);
			
			if (!turma.isDistancia()) {
				validaInt(turma.getCapacidadeAluno(), "Capacidade de Alunos", lista);
				validateRequired(turma.getLocal(), "Local", lista);
			}
			
			ValidatorUtil.validateRange(turma.getPeriodo(), 1, 2, "Per�odo", lista);
			
			if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
					turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
				lista.addErro("A capacidade da turma ("+turma.getCapacidadeAluno()+") n�o pode ser inferior � quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").");
			}
			
			if( !isEmpty(turma.getObservacao() ) )
				validateMaxLength(turma.getObservacao(), 100, "Observa��es", lista);
		} finally {
			if (cursoLatoDao != null) cursoLatoDao.close();
		}
	}
	
	/**
	 * Valida os dados b�sicos da turma, invocado ap�s a submiss�o da primeira tela no cadastro de turmas.
	 * @param turma
	 * @param tForm
	 * @param usuario
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDadosBasicos(Turma turma, Usuario usuario, ListaMensagens lista) throws DAOException {
		TurmaGraduacaoDao turmaGradDao = null;
		try {
			turmaGradDao = getDAO(TurmaGraduacaoDao.class);
			// evita lazy exception
			Unidade unidade = turmaGradDao.findByPrimaryKey(turma.getDisciplina().getUnidade().getId(), Unidade.class);
			turma.getDisciplina().setUnidade(unidade);
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(turma);
			
			CalendarioAcademico cal = null;
			try {
				cal = CalendarioAcademicoHelper.getCalendario(turma);
			} catch (ConfiguracaoAmbienteException e) {
				lista.addErro(e.getMessage());
				return;
			}
			
			if (turma.getDisciplina() == null || turma.getDisciplina().getId() == 0) {
				turma.setDisciplina(new ComponenteCurricular());
				lista.getMensagens().add(new MensagemAviso("Escolha uma disciplina.", TipoMensagemUFRN.ERROR));
				return;
			}
	
			if( turma.getId() == 0 ) {
				
				validateRequiredId(turma.getTipo(), "Tipo da Turma", lista);
				
				if (turma.getTipo() != null) {
					if (turma.isTurmaFerias() && !PeriodoAcademicoHelper.getInstance().isPeriodoIntervalar(turma.getPeriodo())) 
						lista.addErro("O Per�odo informado n�o � um per�odo de f�rias v�lido.");
					else if (!turma.isTurmaFerias() && !PeriodoAcademicoHelper.getInstance().isPeriodoRegular(turma.getPeriodo()) && !(turma.isMedio()))
						lista.addErro("O per�odo informado n�o � um per�odo regular v�lido.");
				}
				
				if (turma.isGraduacao() && usuario.isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
					if (DiscenteHelper.somaSemestres( turma.getAno() ,  turma.getPeriodo(), 0) <  DiscenteHelper.somaSemestres(cal.getAno(), cal.getPeriodo(), 0))
						lista.addErro("N�o � permitido criar turmas para ano-per�odos anteriores ao ano-per�odo atual.");
				}
			}
			
			// valida a carga hor�ria m�xima para o per�odo de f�rias
			if (turma.getTipo() != null && turma.isTurmaFerias()) {
				ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(turma);
				if (parametros.getChMaximaTurmaFerias() != null
						&& turma.getDisciplina().getChTotal() > parametros.getChMaximaTurmaFerias())
					lista.addErro("A Carga Hor�ria Total do Componente Curricular ("+turma.getDisciplina().getChTotal()+"h) n�o permite a cria��o de turmas de f�rias.");
			}
	
			if (turma.getAno() < ParametroHelper.getInstance().getParametroInt(ParametrosGerais.ANO_INICIAL_CADASTRO_TURMAS) || turma.getPeriodo() < 0 || turma.getPeriodo() > 4) {
				lista.getMensagens().add(new MensagemAviso("Ano-Per�odo inv�lidos", TipoMensagemUFRN.ERROR));
				return;
			}
			
			// valida��es dependentes do Form Bean, caso n�o passado, ignoradas
			char nivelEnsino = turma.getDisciplina().getNivel();
	
			validateRequired(turma.getDataInicio(), "In�cio", lista);
			validateRequired(turma.getDataFim(), "Fim", lista);
			validateMinValue(turma.getDataFim(), turma.getDataInicio(),"Fim" , lista);
	
			// valida se as datas est�o dentro do per�odo letivo.
			// Valida��o de turmas de gradua��o feita no HorarioTurmaMBean
			if (!turma.isGraduacao() && turma.getDataInicio() != null && turma.getDataFim() != null && cal != null && turma.getTipo() != null && turma.getTipo() > 0) {
				if (turma.isTurmaFerias()) {
					validateRange(turma.getDataInicio(), cal.getInicioFerias(), cal.getFimFerias(), "In�cio", lista);
					validateRange(turma.getDataFim(), cal.getInicioFerias(), cal.getFimFerias(), "Fim", lista);
				} else {
					validateRange(turma.getDataInicio(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "In�cio", lista);
					validateRange(turma.getDataFim(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Fim", lista);
				}
			}
			
			if ( !turma.isDistancia() && turma.getId() != 0 )
				validateRequired(turma.getCodigo(), "C�digo da Turma", lista);
	

			// na cria��o de turma se for de gradua��o o campus � obrigat�rio
			// s� deve ter campus para turmas de gradua��o que NAO s�o EAD e que NAO s�o de conv�nio
			// S� valida para turmas abertas pois apenas turmas abertas podem ser alteradas, com exce��o de ADMINISTRADOR_DAE e PPG 
			// que pode alterar turmas j� consolidadas, porem s� � poss�vel alterar os docentes da turma, ent�o n�o deve validar se a turma tem campus.
			if( turma.isCampusObrigatorio() && turma.isAberta() ){
				validateRequired(turma.getCampus(), "Campus", lista);
			}

			// a turma s� deve possuir local se n�o for de bloco e possuir cr�ditos de aula ou laborat�rio
			if (!turma.isDistancia()) {
				boolean validarLocal = (turma.getDisciplina().getDetalhes().getCrAula() + turma.getDisciplina().getDetalhes().getCrLaboratorio() > 0)
					|| (turma.getDisciplina().getDetalhes().getChAula() + turma.getDisciplina().getDetalhes().getChLaboratorio() > 0);
				if( !turma.getDisciplina().isBloco() && validarLocal )
					validateRequired(turma.getLocal(), "Local", lista);
			}
	

			if (!turma.isDistancia())
				validaInt(turma.getCapacidadeAluno(), "Capacidade de Alunos", lista);
	
			if (nivelEnsino != NivelEnsino.INFANTIL && nivelEnsino != NivelEnsino.MEDIO) {
				ValidatorUtil.validateRange(turma.getPeriodo(), 1, 4, "Per�odo", lista);
			}
	
			if( !turma.isDistancia() && turma.isTurmaFerias() && turma.getId() == 0 && ( usuario == null || (usuario != null && !usuario.isUserInRole( SigaaPapeis.DAE ))) ){
				
				int total = 0;
				for( ReservaCurso rc : turma.getReservas() ){
					total += rc.getVagasReservadas() + rc.getVagasReservadasIngressantes();
				}
				int qtdMin = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MIN_DISCENTES_TURMA_FERIAS);
				if( total < qtdMin ){
					lista.addErro("� obrigat�rio ter no m�nimo "+qtdMin+" alunos em turmas de f�rias.");
				}
				
			}
			
			if( !isEmpty( turma.getReservas() ) ){
				int total = 0;
				for( ReservaCurso rc: turma.getReservas() ){
					if( rc.getTotalVagasReservadas() <= 0 )
						lista.addErro("A quantidade de vagas da reserva deve ser superior a 0 (zero)");
					total += rc.getTotalVagasReservadas();
				}
				if( !turma.isEad() && turma.isTurmaRegular() && total > turma.getCapacidadeAluno() )
					lista.addErro("A quantidade de vagas total reservada para a turma (" + total + ") n�o pode ser superior a capacidade da turma(" + turma.getCapacidadeAluno() + ").");
			}
			
			if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
					turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
				String msgCapacidade = "A capacidade da turma ("+turma.getCapacidadeAluno()+") n�o pode ser inferior � quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").";
				if (usuario != null && usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
					lista.addWarning(msgCapacidade);
				else
					lista.addErro(msgCapacidade);
			}
		
			if( !isEmpty(turma.getObservacao() ) )
				validateMaxLength(turma.getObservacao(), 100, "Observa��es", lista);
			
			if (turma.getId() == 0 && turma.isTurmaFerias()) {
				long count = turmaGradDao.countByDisciplinaAnoPeriodoTipoSituacao(turma.getDisciplina(), turma.getCampus(), turma.getAno(), turma.getPeriodo(), Turma.FERIAS, SituacaoTurma.getSituacoesAbertas());
				
				if (turma.getId() == 0 && count > 0 || turma.getId() > 0 && count > 1) {
					if (usuario != null && usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
						lista.addWarning("J� existe uma turma de f�rias aberta para este componente curricular.");
					else
					lista.addErro("J� existe uma turma de f�rias aberta para este componente curricular.");
				}
			}
			
			/* Regras de obrigatoriedade: <br/>
			 * RESOLU��O No 028/2010-CONSAD, de 16 de setembro de 2010
			 * Art. 3o
			 * I - tratando-se de discente
			 * a) imposibilidade de se efetuar matricula em disciplina.
			 */
			if (turma.getId() == 0 && !isEmpty(turma.getDiscentes())) {
				try {
					for (Discente discente : turma.getDiscentes()) {
						VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(discente.getPessoa().getId());
					}
				} catch (NegocioException ne) {
					lista.addErro(ne.getMessage());
				}
			}
			
		} finally {
			if (turmaGradDao != null) turmaGradDao.close();
			
		}

	}

	/**
	 * Valida se a turma possui docentes
	 * 
	 * @param docentes
	 * @param lista
	 */
	public static void validaDocentes(Collection<DocenteTurma> docentes, ListaMensagens lista) {
		ValidatorUtil.validateEmptyCollection("� necess�rio informar pelo menos um docente para a turma",docentes, lista);
	}

	/**
	 * Verifica se tem docentes inativos na turma, se tiver n�o permite criar a turma no ano-per�odo atual, apenas em per�odos anteriores.
	 * @param turma
	 * @param docentes
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDocentesInativosTecnico(Turma turma, Collection<DocenteTurma> docentes, ListaMensagens lista) throws DAOException {
		boolean possuiInativo = false;
		TurmaDao dao = getDAO(TurmaDao.class);

		try {
			for (DocenteTurma dt: docentes){
				if(dt.getDocente() != null){
					Servidor d = dao.findByPrimaryKey(dt.getDocente().getId(), Servidor.class);
					if(d.getAtivo().getId() != Ativo.SERVIDOR_ATIVO)
						possuiInativo = true;
				}
			}
	
			if(possuiInativo){
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
				if(turma.getAno() == cal.getAno() && turma.getPeriodo() == cal.getPeriodo())
					lista.getMensagens().add(new MensagemAviso("Turma com docentes inativos n�o pode ser definida para o ano-per�odo atual.", TipoMensagemUFRN.ERROR));
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida o choque de hor�rios entre uma cole��o de turmas passada por par�metro
	 * @param turmas
	 * @return
	 * @throws ArqException
	 */
	public static void validarChoqueHorarios(Collection<Turma> turmas, ListaMensagens lista){

		ArrayList<Turma> comChoques = new ArrayList<Turma>(0);
		StringBuilder msg = new StringBuilder();

		for( Turma turmaComparar : turmas ){
			if( !turmaComparar.getDisciplina().isExigeHorarioEmTurmas() )
				break; //Turma n�o tem aula presencial

			for( Turma turma : turmas ){
				if( !turmaComparar.getDisciplina().isExigeHorarioEmTurmas() )
					break; //Turma n�o tem aula presencial

				if( turmaComparar == turma	 )
					break; // N�O DEVE COMPARAR HOR�RIOS DA MESMA TURMA

				for (HorarioTurma ht : turma.getHorarios()) {

					if (turmaComparar.temHorario(ht))  {
						comChoques.add(turma);
						msg.append("<br>" + turma.getDisciplina().getCodigo() + " E " + turmaComparar.getDisciplina().getCodigo());
						break;
					}
				}
			}
		}

		String retorno = "";
		if( msg.length() > 0 ){
			retorno = "Ocorreram choque de hor�rios com as seguintes turmas dos componentes: " + msg.toString();
			lista.addErro(retorno);
		}

	}

	/**
	 * Valida o choque de hor�rios na mesma sala entre uma cole��o de turmas passada por par�metro
	 * utilizado no ensino t�cnico.
	 * @param turmas
	 * @return
	 */
	public static String validarChoqueHorariosSala(Collection<Turma> turmas){

		ArrayList<Turma> comChoques = new ArrayList<Turma>(0);
		StringBuilder msg = new StringBuilder();

		for( Turma turmaComparar : turmas ){

			for( Turma turma : turmas ){

				if( turmaComparar == turma	 )
					break; // N�O DEVE COMPARAR HOR�RIOS DA MESMA TURMA

				for (HorarioTurma ht : turma.getHorarios()) {

					// se chocar o hor�rio E o local for o mesmo
					if (turmaComparar.temHorario(ht) && turmaComparar.getLocal().equals(turma.getLocal()))  {
						comChoques.add(turma);
						msg.append("<br>" + turma.getDisciplina().getCodigo() +" (Turma "+ turma.getCodigo() +") E " + turmaComparar.getDisciplina().getCodigo()+" (Turma "+ turmaComparar.getCodigo() +") no local: " + turma.getLocal());
						break;
					}
				}
			}
		}

		String retorno = "";
		if( msg.length() > 0 ){
			retorno = "Ocorreu choque de hor�rios com as seguintes turmas dos componentes: " + msg.toString();
			return retorno;
		}

		return null;
	}

	/**
	 * A turma DEVE possuir no M�NIMO a quantidade de hor�rios igual a soma dos cr�ditos de aula + laborat�rio
	 * e N�O PODE ultrapassar a soma de cr�ditos total da disciplina (aula + laborat�rio + estagio).
	 * Ou seja, n�o precisa, necessariamente, possuir hor�rios definidos para os cr�ditos referentes a estagio
	 *
	 * turmas do tipo modulo deve possuir no m�nimo 1 hor�rio semanal E NAO TEM MAXIMO DE HORARIOS
	 * @param t
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaHorarios(Turma t, ListaMensagens lista, UsuarioGeral usuario) throws DAOException {
		
		if( t.isEad() || t.isTurmaEnsinoIndividual() || (t.isTurmaRegularOrigemEnsinoIndiv() && t.getId() == 0)) //n�o executa valida��o de hor�rios para turmas EAD e de Ensino Individualizado.
			return;
		
		if( (t.getDisciplina().isModulo() || t.getDisciplina().isAtividade()) && t.getDisciplina().isExigeHorarioEmTurmas()){
			ValidatorUtil.validateEmptyCollection( "� obrigat�rio informar o hor�rio da turma.", t.getHorarios(), lista);
			return;
		}

		int horariosMinimos = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
		
		if( horariosMinimos > 0 && t.getDisciplina().isExigeHorarioEmTurmas()){
			ValidatorUtil.validateEmptyCollection(
					"� obrigat�rio informar o hor�rio da turma.",
					t.getHorarios(), lista);
		}
		
		// valida o in�cio e fim do hor�rio
		for (HorarioTurma ht : t.getHorarios()) {
			validateRange(ht.getDataInicio(), t.getDataInicio(), t.getDataFim(), "Data de in�cio do hor�rio", lista);
			validateRange(ht.getDataFim(), t.getDataInicio(), t.getDataFim(), "Data de in�cio do hor�rio", lista);
			validaOrdemTemporalDatas(ht.getDataInicio(), ht.getDataFim(), true, "In�cio e Fim do hor�rio", lista);
		}
		
		if( t.isTurmaFerias() ){ // se for turma de f�rias n�o deve validar o m�ximo de hor�rios. 
			validarHorarioTurmaFerias(t, lista, usuario);
			return;
		}		
		
		if (t.isGraduacao() && t.getDisciplina().isExigeHorarioEmTurmas()){
			int porcMin = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA );
			int porcMax = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA );
			if ( t.getPorcentagemAulas() < porcMin || t.getPorcentagemAulas() > porcMax )
				lista.addErro("A porcentagem do n�mero de aulas do hor�rio/per�odo deve estar entre " +porcMin+ "% e "+porcMax+"% da porcentagem relativa ao n�mero de aulas definido pela carga hor�ria do componente. A porcentagem do n�mero de aulas selecionado foi de "+t.getPorcentagemAulas()+"%" );
		}
		
		if (t.getDisciplina().getChTotal() != 0) {
			
			// se for turma de t�cnico e n�o tiver cr�dito n�o deve validar a quantidade de hor�rios 
			if( t.getDisciplina().getNivel() == NivelEnsino.TECNICO || t.getDisciplina().getNivel() == NivelEnsino.FORMACAO_COMPLEMENTAR){
				return;
			}

			int numeroMinimo = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
			int numeroMaximo = t.getDisciplina().getChTotal()/fatorConversaoCargaHorariaEmCreditos(t.getDisciplina().getNivel());
			
			// se for turma de M�dio e n�o tiver cr�dito n�o deve validar a quantidade de hor�rios 
			if( t.getDisciplina().getNivel() == NivelEnsino.MEDIO && numeroMinimo == 0 ){
				return;
			}
	
			// Valida se o hor�rio extrapolou os cr�ditos
			if ( t.getDisciplina().isPermiteHorarioFlexivel() ) {
				
				List<GrupoHorarios> grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(t.getHorarios());
				
				for (GrupoHorarios gh : grupoHorarios) {
					int qtdHorarios = 0;
					qtdHorarios = gh.getHorarios().size();
					verificarHorarioAndCredito(t, lista, numeroMinimo, numeroMaximo, qtdHorarios);	
				}
			} else {
				int qtdHorarios = 0;
				if ( t.getHorarios() != null ) {
					qtdHorarios = t.getHorarios().size();
				}
				
				verificarHorarioAndCredito(t, lista, numeroMinimo, numeroMaximo, qtdHorarios);			
			}
		}
	}	


	/**
	 * Verifica se existe algum dia que ficou descoberto sem aula
	 * 
	 * @param t
	 * @param lista
	 */
	public static void validaPeriodoHorario(Turma t, ListaMensagens lista) {
		
		if ((t.getDisciplina().isModuloOuAtividadeColetiva() && t.getDisciplina().isExigeHorarioEmTurmas()) 
				|| !t.getDisciplina().isExigeHorarioEmTurmas())
			return;
			
		List<GrupoHorarios> grupoHorarios = HorarioTurmaUtil.agruparHorarioPorPeriodo(t.getHorarios());
		
		int diasTurma = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(t.getDataInicio(), t.getDataFim());
		int diasPeriodo = 0;
		for (GrupoHorarios gh : grupoHorarios) {
			diasPeriodo += CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(gh.getPeriodo().getInicio(), gh.getPeriodo().getFim());
		}
		// se houver ch de ead, desconta-se dos dias da turma
		if (t.getDisciplina().getDetalhes().getChEad() > 0) {
			double percentualEad = (double) t.getDisciplina().getDetalhes().getChEad() / (double) t.getDisciplina().getDetalhes().getChTotal();
			diasTurma = (int) (diasTurma * (1 - percentualEad));
		}
		
		if (diasPeriodo > diasTurma || diasPeriodo < diasTurma)
			lista.addErro("A soma dos dias letivos nos intervalos de cada per�odo ("+diasPeriodo+") deve ser igual � quantidade de dias letivos da turma ("+diasTurma+").");
	}
	
	/**
	 * Verifica se os hor�rios escolhidos s�o compat�veis com os cr�ditos do componente curricular
	 * 
	 * @param turma
	 * @param lista
	 * @param numeroMinimo
	 * @param numeroMaximo
	 * @param qtdHorarios
	 */
	private static void verificarHorarioAndCredito(Turma turma, ListaMensagens lista, int numeroMinimo, int numeroMaximo, int qtdHorarios) {
		if (!turma.getDisciplina().isExigeHorarioEmTurmas())
			return;
		// turmas de lato sensu n�o tem cr�dito. neste caso, verifica-se se o hor�rio definido � coerente com a carga hor�ria.
		if ((turma.isLato() || !turma.getDisciplina().isDisciplina()) && !turma.getDisciplina().isPermiteHorarioFlexivel()) {
			if (HorarioTurmaUtil.calcularTotalHoras(turma.getHorarios()) != turma.getChTotalTurma()) {
			}
		} else 
			if ((qtdHorarios < numeroMinimo || qtdHorarios > numeroMaximo) &&  !turma.getDisciplina().isPermiteHorarioFlexivel() ){
			lista.addErro("N�mero de hor�rios escolhidos � incompat�vel com os cr�ditos do componente " +
					"da turma.<br>" +
					"O componente possui "+turma.getDisciplina().getCrTotal()+" cr�ditos. A turma deve possuir a quantidade de hor�rios igual a soma dos cr�ditos de aula + laborat�rio do componente.");
		}
	}

	/**
	 * verifica se os dois docentes s�o a mesma pessoa.
	 * @param dtA
	 * @param dtB
	 * @return
	 */
	public static boolean validaMesmaPessoaDocenteTurma(DocenteTurma dtA,DocenteTurma dtB){
		if (
			(!isEmpty(dtA.getDocente()) && 
				//(Servidor x Servidor)
			   (!isEmpty(dtB.getDocente()) && dtA.getDocente().getPessoa().equals(dtB.getDocente().getPessoa())) ||
			   //(Servidor x DocenteExterno)
				(!isEmpty(dtB.getDocenteExterno()) && dtA.getDocente().getPessoa().equals(dtB.getDocenteExterno().getPessoa())))
		|| 
		    (!isEmpty(dtA.getDocenteExterno()) &&
			   //(DocenteExterno x servidor)
				(!isEmpty(dtB.getDocente()) && dtA.getDocenteExterno().getPessoa().equals(dtB.getDocente().getPessoa())) ||
			   //(DocenteExterno x DocenteExterno)
				(!isEmpty(dtB.getDocenteExterno()) && dtA.getDocenteExterno().getPessoa().equals(dtB.getDocenteExterno().getPessoa())))
			){
			return true;
		}else
			return false;
	}
	
	/**
	 * Valida��o invocada no momento que um docente � inserido numa turma 
	 * @param turma a turma a ser validada o docente inserido
	 * @param docenteTurma o docente inserido
	 * @param permiteCHCompartilhada se permite carga hor�rio compartilhada
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaDocenteInserido(Turma turma,
			DocenteTurma docenteTurma, boolean permiteCHCompartilhada,
			ListaMensagens lista, Collection<HorarioDocente> horariosDocente) throws DAOException {
		GenericDAO dao = null;
		try {
			if( docenteTurma.getDocente() != null ){
				ValidatorUtil.validateRequiredId(docenteTurma.getDocente().getId(), "Docente",  lista);
			} else if( docenteTurma.getDocenteExterno() != null ){
				ValidatorUtil.validateRequiredId(docenteTurma.getDocenteExterno().getId(), "Docente",  lista);
			}else{
				lista.addErro("Docente: campo obrigat�rio n�o informado.");
			}
	
			// Verificando se o docente j� est� inserido na turma
			for( DocenteTurma dt : turma.getDocentesTurmas() ){
				// verifica se � o mesmo docente
				if ((!isEmpty(dt.getDocente())
							&& !isEmpty(docenteTurma.getDocente()) 
							&& dt.getDocente().getId() == docenteTurma.getDocente().getId())
						|| (!isEmpty(dt.getDocenteExterno())
							&& !isEmpty(docenteTurma.getDocenteExterno()) 
							&& dt.getDocenteExterno().getId() == docenteTurma.getDocenteExterno().getId())) {
					lista.addErro("Este docente j� foi adicionado a esta turma.");
					break;
				} else {
					// verifica para docentes diferentes, se o hor�rio conflita
					if (turma.getDisciplina().isPermiteHorarioDocenteFlexivel() && !permiteCHCompartilhada && dt.getGrupoDocente().equals(docenteTurma.getGrupoDocente())) {
						for (HorarioDocente hd : dt.getHorarios()) {
							for (HorarioDocente hdNovo : horariosDocente) {
								if (CalendarUtils.isIntervalosDeDatasConflitantes(hd.getDataInicio(), hd.getDataFim(), hdNovo.getDataInicio(), hdNovo.getDataFim())	&& hd.equals(hdNovo)){
									lista.addErro("H� choque de datas do hor�rio deste docente com as datas informadas anteriormente.");
									break;
								}
							}
						}
					}
				}
			}
	
			// Valida a carga hor�ria do docente
			if (!turma.isDistancia())
				validaCHDocenteTurma(turma, docenteTurma, permiteCHCompartilhada, lista, true, horariosDocente, docenteTurma.getGrupoDocente());

			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			turma.setDisciplina( dao.refresh( turma.getDisciplina() ) );
			turma.getDisciplina().getUnidade().getId();
			if(turma.getDisciplina().getUnidade().getGestoraAcademica() != null)
				turma.getDisciplina().getUnidade().getGestoraAcademica().getId();
			
			// S� verifica se o docente est� ativo no per�odo se n�o for turma de lato sensu
			if(turma.getDisciplina().getNivel() != NivelEnsino.LATO){
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
				Servidor servidor = isEmpty(docenteTurma.getDocente()) ? docenteTurma.getDocenteExterno().getServidor() : docenteTurma.getDocente();
				boolean ativoNoPeriodo = validaStatusDocente(servidor, docenteTurma.getDocenteExterno(), turma.getAno(), turma.getPeriodo(), cal);
				if( !ativoNoPeriodo ){
					lista.addErro("Este docente n�o pode participar desta turma pois seu v�nculo esta INATIVO no ano/per�odo da turma.");
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Valida a carga hor�ria do docente em rela��o a m�xima permitida pela turma ou se a CH informada � maior que a da disciplina
	 * 
	 * @param turma
	 * @param docenteTurma
	 * @param permiteCHCompartilhada
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaCHDocenteTurma(Turma turma, DocenteTurma docenteTurma, boolean permiteCHCompartilhada, ListaMensagens lista,
			boolean novoDocente, Collection<HorarioDocente> horariosDocente, Integer grupo) throws DAOException {
			validaCHDocente(turma, docenteTurma, permiteCHCompartilhada, lista, novoDocente, horariosDocente, grupo);
	}
	
	/**
	 * Valida a carga hor�ria do docente em rela��o a m�xima permitida pela turma ou se a CH informada � maior que a da disciplina.
	 * A valida��o � feita para docentes de diferentes n�veis de ensino exceto gradua��o.
	 * 
	 * @param turma
	 * @param docenteTurma
	 * @param permiteCHCompartilhada
	 * @param lista
	 * @param grupo 
	 * @throws DAOException 
	 */
	private static void validaCHDocente(Turma turma, DocenteTurma docenteTurma, boolean permiteCHCompartilhada, ListaMensagens lista,
			boolean novoDocente, Collection<HorarioDocente> horariosDocente, Integer grupo) throws DAOException {
		if (ValidatorUtil.isEmpty(horariosDocente))
			ValidatorUtil.validaInt(docenteTurma.getChDedicadaPeriodo(), "Carga Hor�ria", lista);
		
		if(grupo == null) grupo = docenteTurma.getGrupoDocente();
		// S� faz verifica��o de CH se n�o for EAD
		// ou se o componente curricular estiver setado para n�o ser verificar a carga hor�ria
		if (!permiteCHCompartilhada && !turma.isDistancia()) {
			// valida se a adi��o da CH desse docente excede a CH m�xima da turma
			int totalAteAgora = 0;
			for(DocenteTurma dt : turma.getDocentesTurmas()) {
				if (dt.getChDedicadaPeriodo() != null )
					if (grupo != null)
						totalAteAgora += dt.getGrupoDocente() == grupo ? dt.getChDedicadaPeriodo() : 0;
					else
						totalAteAgora += dt.getChDedicadaPeriodo();
			}
			if (novoDocente) {
				if (totalAteAgora + docenteTurma.getChDedicadaPeriodo() > turma.getChTotalTurma()) {
					lista.addErro("Docente com essa CH n�o pode ser adicionado. A CH total excede � CH da disciplina da turma.");
				}
			} else {
				if (totalAteAgora > turma.getChTotalTurma() ) {
					lista.addErro("Docente com essa CH n�o pode ser adicionado. A CH total excede � CH da disciplina da turma.");
				}
			}
		} else {
			// valida apenas se a CH informada � maior que a da disciplina
			if (docenteTurma.getChDedicadaPeriodo() > turma.getChTotalTurma()) {
				lista.addErro("Docente com essa CH n�o pode ser adicionado. A CH total excede � CH da disciplina da turma.");
			}
		}
	}
	
	/**
	 * Valida a porcentagem carga hor�ria do docente em rela��o a m�xima permitida pela turma ou se a CH informada � maior que a da disciplina.
	 * A valida��o � feita para docentes de diferentes n�veis de ensino exceto gradua��o.
	 * 
	 * @param turma
	 * @param docenteTurma
	 * @param permiteCHCompartilhada
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaPorcentagemCHDocente(Turma turma, DocenteTurma docenteTurma, boolean permiteCHCompartilhada, ListaMensagens lista,
			boolean novoDocente, Integer grupo) throws DAOException {
		
		if ( docenteTurma.getPorcentagemChDedicada() == null )
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Hor�ria");
		if ( docenteTurma.getPorcentagemChDedicada() != null && docenteTurma.getPorcentagemChDedicada() > 100 )
			lista.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Carga Hor�ria", 100);
		if ( docenteTurma.getPorcentagemChDedicada() != null && docenteTurma.getPorcentagemChDedicada() <= 0)
			lista.addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_A, "Carga Hor�ria", 1);
		
		boolean permiteGrupo = turma.getDisciplina().getNumMaxDocentes() > 1;
		
		// S� faz verifica��o de CH se n�o for EAD
		// ou se o componente curricular estiver setado para n�o ser verificar a carga hor�ria
		if (!permiteCHCompartilhada && !turma.isDistancia()) {
			// valida se a adi��o da CH desse docente excede a CH m�xima da turma
			int totalAteAgora = 0;
			for(DocenteTurma dt : turma.getDocentesTurmas()) {
				if (dt.getPorcentagemChDedicada() != null)
					if (grupo != null && grupo > 0)
						totalAteAgora += dt.getGrupoDocente().equals(grupo) ? dt.getPorcentagemChDedicada() : 0;
					else
						totalAteAgora += dt.getPorcentagemChDedicada();
			}
			if (docenteTurma.getPorcentagemChDedicada() != null && novoDocente) {
				if (totalAteAgora + docenteTurma.getPorcentagemChDedicada() > 100) {
					lista.addErro("O docente "+ docenteTurma.getDocenteNome() +" n�o pode ser adicionado com essa CH. A porcentagem da CH total"+(permiteGrupo?"do Grupo "+grupo:"")+" excede 100%.");
				}
			} else {
				if (totalAteAgora > 100) {
					lista.addErro("Docente "+ docenteTurma.getDocenteNome() +" n�o pode ser adicionado com essa CH. A porcentagem da CH total"+(permiteGrupo?"do Grupo "+grupo:"")+" excede 100%.");
				}
			}
		} 
	}
	
	/**
	 * Valida se o docente est� ativo no ano per�odo informado, se ele n�o foi desligado antes daquele per�odo.
	 * Se o ano-per�odo informado for o ano-per�odo atual, o servidor precisa estar ativo necessariamente.
	 * @param servidor
	 * @param ano
	 * @param periodo
	 * @param erros
	 * @return true caso esteja ativo
	 * @throws DAOException
	 */
	public static boolean validaStatusDocente( Servidor servidor,  DocenteExterno docenteExterno ,int ano, int periodo, CalendarioAcademico cal) throws DAOException{
		// define m�s o de in�cio do per�odo letivo como janeiro
		int mesInicioPeriodo = 1; 

		if( cal == null ){
			cal = new CalendarioAcademico();
			cal.setAno(ano);
			cal.setPeriodo(periodo);
			// se o per�odo for 1, o m�s inicial ser� janeiro (1). Caso contr�rio, ser� julho (7)
			mesInicioPeriodo = ( periodo == 1 ? 1 : 7 );
		}else{
			if( cal.getInicioPeriodoLetivo() != null ){
				Calendar calInicioPeriodo = Calendar.getInstance();
				calInicioPeriodo.setTime(cal.getInicioPeriodoLetivo());
				mesInicioPeriodo = calInicioPeriodo.get( Calendar.MONTH ) + 1;
			}else{
				mesInicioPeriodo = ( cal.getPeriodo() == 1 ? 1 : 7 );
			}
		}

		if( servidor != null ){
			if( servidor.getAtivo() != null && servidor.getAtivo().getId() == Ativo.SERVIDOR_ATIVO )
				return true;

			// se for colaborador volunt�rio pode ser associado a turma 
			ColaboradorVoluntarioDao dao = getDAO(ColaboradorVoluntarioDao.class);
			try {
				if( dao.isColaboradorVoluntario(servidor) )
					return true;
			} finally {
				dao.close();
			}

			// o docente pode ser inserido na turma se a data de desligamento dele for posterior posterior a data de In�cio
			if (servidor.getDataDesligamento() == null)
				return true;
			Calendar dataDesl = Calendar.getInstance();
			dataDesl.setTime(servidor.getDataDesligamento());
			int anoDesl = dataDesl.get( Calendar.YEAR );
			int mesDesl = dataDesl.get( Calendar.MONTH ) + 1;

			if( ano < anoDesl || ( ano == anoDesl && mesInicioPeriodo <= mesDesl ) ){
				return true;
			}

		} else if( docenteExterno != null ){
			return docenteExterno.isAtivo();
		}

		return false;

	}

	/**
	 * Valida se os hor�rios dos docentes da turma s�o compat�veis com as outras turmas do docente
	 * @param turma
	 * @param docenteTurma
	 * @param lista
	 * @throws DAOException
	 * @throws SQLException 
	 */
	public static void validaHorariosDocenteTurma(Turma turma,  DocenteTurma docenteTurma, ListaMensagens lista, UsuarioGeral usuario, HashMap<Integer, ParametrosGestoraAcademica> cacheParam) throws DAOException {
		
		if( turma.isDistancia() )
			return;
		
		// verifica se o hor�rio do docente est� definido para a turma
		if (!ValidatorUtil.isEmpty(turma.getHorarios()) && ValidatorUtil.isEmpty(docenteTurma.getHorarios()))
			lista.addErro("O docente " + docenteTurma.getDocenteNome() + " n�o est� com o hor�rio na turma definido.");
		
		// Verifica se o hor�rio do docente � compat�vel com o hor�rio da turma.
		Set<String> incompativel = new TreeSet<String>();
		for (HorarioDocente hd : docenteTurma.getHorarios()){
			boolean contem = false;
			for (HorarioTurma ht : turma.getHorarios()) {
				if (hd.getDia() == ht.getDia()
						&& hd.getTipo().equals(ht.getTipo())
						&& hd.getHorario().equals(ht.getHorario())
						&& CalendarUtils.isDentroPeriodo(ht.getDataInicio(), ht.getDataFim(), hd.getDataInicio())
						&& CalendarUtils.isDentroPeriodo(ht.getDataInicio(), ht.getDataFim(), hd.getDataFim())){
					contem = true;
					break;
				}
			}
			if (!contem)
				incompativel.add(hd.toString());
		}
		if (!incompativel.isEmpty())
			lista.addErro("O(s) hor�rio(s) "+StringUtils.transformaEmLista(CollectionUtils.toList(incompativel))+" do docente "+docenteTurma.getDocenteNome()+" n�o corresponde ao hor�rio da turma. Como o hor�rio da turma foi alterado, por favor adicione um novo docente de acordo com o novo hor�rio (caso o docente j� esteja na lista, remova-o e insira novamente).");
		
		TurmaDao dao = null;
		GerenciarTurmaDao gerDao = null;
		try {
			
			ParametrosGestoraAcademica params = ParametrosGestoraAcademicaHelper.getParametros(turma);
			if (ValidatorUtil.isNotEmpty(params))
				cacheParam.put(turma.getId(), params);
			
			boolean impedeChoqueHorarios = true;
			if (params != null)
				impedeChoqueHorarios = params.isImpedeChoqueHorarios();
			
			if(impedeChoqueHorarios) {
				// Verificando se o docente n�o est� inserido em outra turma no mesmo hor�rio
					dao = getDAO( TurmaDao.class );
					gerDao = getDAO( GerenciarTurmaDao.class );
					Collection<Turma> turmasDocente = null;
					if( docenteTurma.getDocente() != null ){
						turmasDocente = gerDao.findByDocenteExternoServidor(docenteTurma.getDocente().getPessoa(),false, turma.getAno(), turma.getPeriodo() );
					}
					else if( docenteTurma.getDocenteExterno() != null ){
						turmasDocente = gerDao.findByDocenteExternoServidor(docenteTurma.getDocenteExterno().getPessoa(),true, turma.getAno(), turma.getPeriodo() );
					}
		
					carregarParamGestororaAcademico(cacheParam, turmasDocente);
					
					Collection<Turma> choque = new ArrayList<Turma>();
					if( turmasDocente != null ){
						choque = HorarioTurmaUtil.verificarChoqueHorarioDocentes(turma, turmasDocente );
					}
		
					if (!turma.isSubTurma() && !choque.isEmpty()) {
						StringBuffer msg = new StringBuffer("O docente " + docenteTurma.getDocenteNome() + " est� vinculado a outra(s) turma(s) no mesmo hor�rio: <br/>");
						for (Turma t : choque) {
							msg.append("&nbsp;&nbsp;&nbsp;-&nbsp;" + t.getDisciplina().getCodigo() + " - " + t.getCodigo() +" nos hor�rios: ");
							
							for (Iterator<HorarioTurma> it = t.getHorarios().iterator(); it.hasNext();) {
								HorarioTurma ht = it.next();
								if(!ht.isConflitoHorario())
									it.remove();
							}
									
							StringBuilder sb = new StringBuilder(HorarioTurmaUtil.formatarCodigoHorarios(t));

							msg.append(sb);
							msg.append("<br/>");
						}
						
						if (usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
							lista.addWarning(msg.toString() + "Se concluir a opera��o o docente ser� inserido com choque de hor�rio.");
						else
							lista.addErro(msg.toString());
					}
					
					// se o docente participar de mais de uma turma no exatamente MESMO hor�rio ent�o a carga hor�ria conjunta dele em todas as turmas n�o pode ultrapassar
					// a quantidade de hor�rios. 
					// Ex: se ele participa de duas turmas junto com outro professor no hor�rio 24M34, ent�o a carga hor�ria de cada docente n�o pode ultrapassar 60 horas.
					int chTotalDocente = 0;
					StringBuilder codTurmas = new StringBuilder();
					chTotalDocente += turma.getChDedicadaDocente(docenteTurma.getDocente(), docenteTurma.getDocenteExterno());
					boolean possuiTurmasMesmoHorario = false;
					choque = HorarioTurmaUtil.verificarChoqueHorario(turma, turmasDocente, true, true, true );
					
					if (!isEmpty(choque)) {
						for( Turma t : choque ){
							possuiTurmasMesmoHorario = true;
							chTotalDocente += t.getChDedicadaDocente(docenteTurma.getDocente(), docenteTurma.getDocenteExterno());
							codTurmas.append(t.getDisciplina().getCodigo() + "-T" + t.getCodigo() + ", ");
						}
					}
					
					if( !turma.isSubTurma() && possuiTurmasMesmoHorario ){
						if( chTotalDocente > turma.getDisciplina().getChTotal() ){
							String msg = "O docente " + docenteTurma.getDocenteNome() + " est� vinculado a outra(s) turma(s) no mesmo hor�rio (" 
									+ codTurmas.toString().substring(0, codTurmas.toString().length() - 2)  
									+ ") portanto, a soma da carga hor�ria dedicada do docente em todas as turmas n�o pode ser superior a " 
									+ turma.getDisciplina().getChTotal() + " horas.";
							if (usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
								lista.addWarning(msg);
							else
								lista.addErro(msg);
						}
					}
					
			}
		} catch (DAOException e) {
			e.printStackTrace();
			throw e;
		}finally{
			if(dao != null)
				dao.close();
			if (gerDao != null)
				gerDao.close();
		}
	}

	/** Carrega os par�metros da gestora acad�mica
	 * @param cacheParam
	 * @param turmasDocente
	 * @throws DAOException
	 */
	private static void carregarParamGestororaAcademico(HashMap<Integer, ParametrosGestoraAcademica> cacheParam, Collection<Turma> turmasDocente) throws DAOException {
		for (Turma turmaLocal : turmasDocente) {

			if (turmaLocal.getParametros() == null) {
				if ( cacheParam.containsKey(turmaLocal.getId()) )
					turmaLocal.setParametros(cacheParam.get(turmaLocal.getId()));
				else {
					ParametrosGestoraAcademica paramLocal = ParametrosGestoraAcademicaHelper.getParametros(turmaLocal);
					if ( ValidatorUtil.isNotEmpty(paramLocal) ) {
						cacheParam.put(turmaLocal.getId(), paramLocal);
						turmaLocal.setParametros(paramLocal);
					}
				}
			}
		}
	}

	/**
	 * Valida se � poss�vel alterar o horario do plano de matr�cula. Esse metodo varre todos os planos que a turma faz parte e verifica se existe choque de horario.
	 * 
	 * @param turma
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaHorariosTurmaPlanoMatricula(Turma turma, ListaMensagens lista) throws DAOException {
		PlanoMatriculaIngressantesDao dao = getDAO(PlanoMatriculaIngressantesDao.class);
		
		List<PlanoMatriculaIngressantes> planos = dao.findByTurma(turma);
		
		if (planos.isEmpty())
			return;
		
		Set<Turma> turmas = new HashSet<Turma>();
		
		for (PlanoMatriculaIngressantes plano : planos) {
			turmas.addAll(plano.getTurmas());
		}
		
		Collection<Turma> choque = HorarioTurmaUtil.verificarChoqueHorario(turma, turmas, false);
		
		if (!choque.isEmpty()) {
			StringBuilder msg = new StringBuilder("Choque de hor�rio com outra(s) turma(s) no Plano de Matr�cula: <br/>");
			for (Turma t : choque) {
				msg.append("&nbsp;&nbsp;&nbsp;-&nbsp;" + t.getDescricaoSemDocente() + " - " + t.getDescricaoHorario());
				StringBuilder sb = new StringBuilder();
				msg.append(sb);
				msg.append("<br/>");
			}
			lista.addErro(msg.toString());
		}

		
	}
	
	/**
	 * Valida o hor�rio de todos os discentes de uma turma
	 * utilizado por exemplo quando vai alterar o hor�rio de uma turma que tem alunos matriculados, � necess�rio verificar o hor�rio
	 * de todos os discente com as turmas que eles j� est�o matriculados
	 * @param turma
	 * @param docenteTec
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaHorariosDiscentesTurma(Turma turma, UsuarioGeral usuario, ListaMensagens lista) throws DAOException{
		DiscenteDao dao = null;
		SolicitacaoMatriculaDao solicitacaoDao = null;
		try {
			dao = getDAO(DiscenteDao.class);
			solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
			
			Collection<Discente> discentes = dao.findByTurma( turma.getId() , true);
			
			// Validar tamb�m se h� choque de hor�rios com os alunos que solicitaram matr�cula na turma, pois ao solicitar turma n�o � criado um registro em MatriculaComponente.
			List<SituacaoMatricula> situacoes = new ArrayList<SituacaoMatricula>();
			situacoes.add(SituacaoMatricula.INDEFERIDA);
			situacoes.add(SituacaoMatricula.EXCLUIDA);
			

			if (isEmpty(discentes)) {
				Collection<SolicitacaoMatricula> solicitacoes = solicitacaoDao.findByTurma(turma.getId(), SolicitacaoMatricula.STATUS_ATIVOS_GRAD_PRESENCIAL, situacoes);
				for(SolicitacaoMatricula s : solicitacoes) {
					if(!discentes.contains(s.getDiscente()))				
						discentes.add(s.getDiscente());
				}
			}
			
			StringBuilder sb = new StringBuilder( "H� choque de hor�rio com turmas dos seguintes discentes: " );
			boolean hasChoque = false;
			
			for( Discente d : discentes ){
				if( HorarioTurmaUtil.hasChoqueHorarios(turma , d, null) ){
					sb.append( "\n" + d.toString() );
					hasChoque = true;
				}
			}
					
			if( hasChoque && usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
				lista.addWarning(sb.toString());
			else if (hasChoque)
				lista.addErro(sb.toString());
			
		} finally {
			if (dao != null)
				dao.close();
			if (solicitacaoDao != null)
				solicitacaoDao.close();
		}
	}

	/**
	 * Valida o hor�rio de todas as disciplinas de uma turma de ensino m�dio.
	 * @param turma
	 * @param docenteTec
	 * @param lista
	 * @throws DAOException
	 */
	public static void validaHorariosDisciplinasTurmaMedio(Turma disciplina, UsuarioGeral usuario, ListaMensagens lista) throws DAOException{
		TurmaSerieDao tsaDao = null;
		
		try {
			
			tsaDao = getDAO(TurmaSerieDao.class);
			Collection<Turma> disciplinasTurma = new ArrayList<Turma>();
			TurmaSerieAno turmaSerieAno = tsaDao.findByTurma(disciplina);
			TurmaSerie turmaSerie = turmaSerieAno.getTurmaSerie();

			for (TurmaSerieAno tsa : turmaSerie.getDisciplinas()) {
				disciplinasTurma.add(tsa.getTurma());
			}
			
			Collection<Turma> choque = new ArrayList<Turma>();
			if( disciplinasTurma != null ){
				choque = HorarioTurmaUtil.verificarChoqueHorarioTurmaMedio(disciplina, disciplinasTurma );
			}
			
			boolean hasChoque = false;
			
			if (!isEmpty(choque)) {
				StringBuilder sb = new StringBuilder( 
						"H� choque de hor�rio com a(s) seguinte(s) disciplina(s) da Turma: " );
				for( Turma t : choque ){
					sb.append( "\n" + t.getDisciplina().getNome() + " ("+ t.getDescricaoHorario() +"), ");
					hasChoque = true;
				}	
				if (hasChoque)
					lista.addErro(sb.toString().substring(0, sb.toString().length() - 2));
			}
			
			
		} finally {
			if (tsaDao != null)
				tsaDao.close();
		}
	}
	
	/**
	 * Validar se a capacidade da turma comporta os alunos j� matriculados
	 *
	 * @param obj
	 * @param object
	 * @param mensagens
	 * @throws DAOException
	 */
	public static void validaCapacidadeTurma(Turma obj, Object object, ListaMensagens lista) throws DAOException {
		if (!obj.isDistancia()) {
			MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
			try {
				long ativos = matriculaDao.findTotalMatriculasByTurma(obj, true);
				if (obj.getCapacidadeAluno() == null || obj.getCapacidadeAluno() < ativos) {
					lista.addErro("A capacidade informada � menor do que a quantidade de alunos ativamente matriculados no componente." +
							" Existem " + ativos + " alunos ativos associdos a esta turma." );
				}
			} finally {
				matriculaDao.close();
			}
		}
		if (obj.getReservas() != null) {
			int totalReservas = 0;
			for (ReservaCurso reserva : obj.getReservas()) {
				totalReservas += reserva.getVagasReservadas() + reserva.getVagasReservadasIngressantes();
			}
			if (obj.getCapacidadeAluno() != null && obj.getCapacidadeAluno() < totalReservas)
				lista.addErro("A capacidade da turma � menor que a soma das reservas. Por favor, ajuste os valores informados.");
		}
	}

	/**
	 * Valida regras espec�ficas das turmas de f�rias, deve ser chamado apenas para solicita��o de turma de ferias ou seja, turmas para os semestre 3 e 4.
	 * Regras de valida��o:
	 * O numero de aulas n�o dever� exceder o limite de 3 horas por turno e 6 horas di�rias.
	 * S� pode ser oferecido curso de ferias para disciplinas com carga hor�ria inferior a 90 horas.
	 * @param t
	 *
	 */
	public static void validarHorarioTurmaFerias(Turma t, ListaMensagens lista, UsuarioGeral usuario) {

		// Administrador DAE n�o precisa validar  
		if ( !usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE )) {
			if ( t.getDisciplina().getDetalhes().getChTotal() >  ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.CARGA_HORARIA_DISCIPLINA_FERIAS ) )
				lista.addMensagem(MensagensGraduacao.ERRO_VALIDACAO_CARGA_HORARIA_DISCIPLINA_FERIAS, ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.CARGA_HORARIA_DISCIPLINA_FERIAS ));
		}

		int numeroMinimo = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
		int qtdHorarios = 0;
		if ( t.getHorarios() != null ) 
			qtdHorarios = t.getHorarios().size();
		
		if (  qtdHorarios < numeroMinimo ){
			lista.getMensagens().add(new MensagemAviso("N�mero de hor�rios escolhidos incompat�vel com os cr�ditos do componente " +
					"da turma.<br>" +
					"A turma deve possuir no m�nimo a quantidade de horarios igual a soma dos creditos de aula + laboratorio (" + numeroMinimo + " cr�ditos).", TipoMensagemUFRN.ERROR));
		}
		
		int horasMinimas = t.getDisciplina().getDetalhes().getChAula() + t.getDisciplina().getDetalhes().getChLaboratorio();
		int totalHoras = 0;
		if ( t.getHorarios() != null  )
			totalHoras = HorarioTurmaUtil.calcularTotalHoras(t.getHorarios());
		int horasFaltantes = horasMinimas - totalHoras;
		
		if (  totalHoras < horasMinimas && t.getDisciplina().isExigeHorarioEmTurmas() ){
			lista.getMensagens().add(new MensagemAviso("N�mero de horas escolhidas  � incompat�vel com as horas do componente " +
					"da turma.<br>" +
					"A turma deve possuir no m�nimo a quantidade de horas aulas igual a carga hor�ria da disciplina  " +
					"(" + t.getDisciplina().getDetalhes().getChAula() + " horas) " +
					" + carga hor�ria de laboratorio (" + t.getDisciplina().getDetalhes().getChLaboratorio() + " horas). " +
					"No entanto, falta cadastrar um total de " + horasFaltantes + " horas. ", TipoMensagemUFRN.ERROR));
		}
		
		if (t.getDisciplina().isPermiteHorarioFlexivel()) {
			List<GrupoHorarios> horarioPorPeriodo = HorarioTurmaUtil.agruparHorarioPorPeriodo(t.getHorarios());
			
			for (GrupoHorarios grupoHorarios : horarioPorPeriodo) {
				for( int i = 1; i < 8; i++ ) {
					boolean isOk = validarQtdAulasPorTurnoTurmaFerias(new ArrayList<HorarioTurma>(grupoHorarios.getHorarios()), lista, i);
					if (!isOk)
						break;
				}
			}
		} else {
			for( int i = 1; i < 8; i++ ) {
				boolean isOk = validarQtdAulasPorTurnoTurmaFerias(t.getHorarios(), lista, i);
				if (!isOk)
					break;
			}
		}
	}

	/**
	 * Verifica se o n�mero de aulas, por disciplina, em um per�odo letivo
	 * especial de f�rias, n�o excede o limite de horas por turno e horas
	 * di�rias.
	 * 
	 * @param horarios
	 * @param lista
	 * @param i
	 */
	private static boolean validarQtdAulasPorTurnoTurmaFerias(List<HorarioTurma> horarios,
			ListaMensagens lista, int i) {
		
		int qtdHorariosManha;
		int qtdHorariosTarde;
		int qtdHorariosNoite;
		int qtdHorariosTotal;
		
		qtdHorariosManha = 0;
		qtdHorariosTarde = 0;
		qtdHorariosNoite = 0;
		qtdHorariosTotal = 0;

		qtdHorariosManha = HorarioTurmaUtil.getTotalHorariosDiaTurno( i, (short)Horario.MANHA, horarios);
		qtdHorariosTarde = HorarioTurmaUtil.getTotalHorariosDiaTurno( i, (short)Horario.TARDE, horarios);
		qtdHorariosNoite = HorarioTurmaUtil.getTotalHorariosDiaTurno( i, (short)Horario.NOITE, horarios);
		qtdHorariosTotal = qtdHorariosManha + qtdHorariosTarde + qtdHorariosNoite;

		int qtdMaxDia = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.CARGA_HORARIA_AULA_MAXIMA_POR_DIA);
		int qtdMaxTurno = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.CARGA_HORARIA_AULA_MAXIMA_POR_TURNO);
		if(qtdHorariosTotal > qtdMaxDia ||
				qtdHorariosManha > qtdMaxTurno || qtdHorariosTarde > qtdMaxTurno || qtdHorariosNoite > qtdMaxTurno){
			lista.addMensagem(MensagensGraduacao.CARGA_HORARIA_MAXIMA_TURMA_FERIAS, qtdMaxTurno, qtdMaxDia);
			return false;
		}
		return true;
	}

	/**
	 * Valida a adi��o de um discente em uma solicita��o de turma de f�rias
	 * @param solicitacao a solicita��o que esta sendo adicionada
	 * @param discente o discente que esta sendo adicionado
	 * @param lista
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public static void validarAdicaoDiscenteTurmaFerias(SolicitacaoTurma solicitacao, DiscenteGraduacao discente, ListaMensagens lista) throws ArqException, NegocioException {
		SolicitacaoTurmaDao dao = null;
		DiscenteDao ddao = null;
		MatriculaComponenteDao matdao = null;
		try {
			if( discente == null || discente.getId() == 0 ){
				lista.addErro("Selecione um discente.");
				return;
			}
	
			dao = getDAO(SolicitacaoTurmaDao.class);
			discente = dao.findByPrimaryKey( discente.getId() , DiscenteGraduacao.class );
	
			// verificando se o discente j� foi adicionado a lista de interessados desta turma de f�rias
			for( DiscentesSolicitacao d : solicitacao.getDiscentes() ){
				if( d.getDiscenteGraduacao().getId() == discente.getId() ){
					lista.addErro("Este discente j� foi adicionado na lista de interessados neste curso de f�rias.");
					return;
				}
			}
	
			// verificando se j� existe uma turma / solicita��o de turma de f�rias para este discente
			Integer [] situacoes = new Integer[] { SolicitacaoTurma.ABERTA, SolicitacaoTurma.ATENDIDA, SolicitacaoTurma.ATENDIDA_ALTERACAO, SolicitacaoTurma.ATENDIDA_PARCIALMENTE, SolicitacaoTurma.SOLICITADA_ALTERACAO };
			if( dao.findByDiscenteAnoPeriodo(discente.getId(), solicitacao.getPeriodo(), solicitacao.getAno(), situacoes ).size() > 0 ) {
				lista.addErro("Art.  246.  Cada  aluno  poder�  obter  matr�cula  em  apenas  um  componente  curricular  por per�odo letivo especial de f�rias. " +
						"(Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009). " +
						"Este aluno j� possui interesse em outro curso de f�rias, nao podendo portanto obter neste tamb�m.");
				return;
			}
	
			// verificando se o discente j� pagou a disciplina ou equivalentes da turma de f�rias em quest�o
			ddao = getDAO( DiscenteDao.class );
			matdao = getDAO( MatriculaComponenteDao.class );
			RestricoesMatricula restricoes = RestricoesMatricula.getRestricoesTurmaFerias();
	
			Collection<ComponenteCurricular> componentesConcluidos  = ddao.findComponentesCurricularesConcluidos(discente);
			Collection<ComponenteCurricular> componentesMatriculados = matdao.findComponentesMatriculadosByDiscente(discente);
	
			// criando uma cole��o de todos os componentes (conclu�dos e das atividades do semestre)
			Collection<ComponenteCurricular> todosComponentes = new ArrayList<ComponenteCurricular>(componentesConcluidos);
			todosComponentes.addAll(componentesMatriculados);
	
			// PARA TESTAR OS PRE-REQUISITOS DEVE CONSIDERAR TAMBEM OS COMPONENTES QUE ESTAO MATRICULADOS NO SEMESTRE 
		 	lista.addAll( MatriculaGraduacaoValidator.validarComponenteIndividualmente(discente, solicitacao.getComponenteCurricular(), solicitacao.getAno(), solicitacao.getPeriodo(), new ArrayList<ComponenteCurricular>(), todosComponentes, restricoes) );
		} finally {
		 	if (dao != null) dao.close();
		 	if (ddao != null) ddao.close();
		 	if (matdao != null) matdao.close();
		}
	}

	/**
	 * Valida a solicita��o de ensino individual:
	 * verifica se ha choque de hor�rio com as solicita��es de matr�cula dos alunos da solicita��o de ensino. individual
	 * @param solicitacao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validarSolicitacaoTurmaEnsinoIndividual(SolicitacaoTurma solicitacao, CalendarioAcademico cal, ListaMensagens lista) throws DAOException {
		HorarioDao daoH = null;
		try {
			//cria uma turma para passar para o m�todo que verifica choque de hor�rio
			Turma turma = new Turma();
			turma.setAno( solicitacao.getAno() );
			turma.setPeriodo( solicitacao.getPeriodo() );
			turma.setDisciplina(solicitacao.getComponenteCurricular());
			turma.setDataInicio( cal.getInicioPeriodoLetivo() );
			turma.setDataFim( cal.getFimPeriodoLetivo() );
	
			// seta os hor�rios da solicita��o
			daoH = getDAO(HorarioDao.class);
			List<HorarioTurma> horarios = HorarioTurmaUtil.parseCodigoHorarios(solicitacao.getHorario(), Unidade.UNIDADE_DIREITO_GLOBAL, 'G', daoH);
			for (HorarioTurma h : horarios) {
				h.setTurma(turma);
				h.setDataInicio(turma.getDataInicio());
				h.setDataFim(turma.getDataFim());
			}
			turma.setHorarios(horarios);
			turma.setDescricaoHorario( HorarioTurmaUtil.formatarCodigoHorarios(turma) );
	
			for( DiscentesSolicitacao ds : solicitacao.getDiscentes() ){
				MatriculaGraduacaoValidator.validarChoqueHorariosSolicitacoesMatricula(turma, ds.getDiscenteGraduacao(), lista);
			}
		} finally {
			if (daoH != null) daoH.close();
		}
	}
	
	/**
	 * Verificando se j� n�o foi criado turma de f�rias com algum dos discentes da solicita��o.
	 * Esta valida��o � necess�ria pois, por falha do sistema, alguns discentes conseguiram 
	 * solicitar turma de f�rias em mais de um componente curricular
	 * @param turma
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validarDiscentesTurmaFerias(SolicitacaoTurma solicitacao, ListaMensagens lista) throws DAOException  {
		
		SolicitacaoTurmaDao dao = null;
		try {
			dao = getDAO( SolicitacaoTurmaDao.class );
			List<Integer> ids = new ArrayList<Integer>();
			for( DiscentesSolicitacao ds : solicitacao.getDiscentes() ){
				ids.add( ds.getDiscenteGraduacao().getId() );
			}
			
			Collection<SolicitacaoTurma> solicitacoes = dao.findSolTurmaFeriasByDiscente( solicitacao, solicitacao.getAno(), solicitacao.getPeriodo(), ids, 
					SolicitacaoTurma.ABERTA, SolicitacaoTurma.ATENDIDA, SolicitacaoTurma.ATENDIDA_ALTERACAO, 
					SolicitacaoTurma.ATENDIDA_PARCIALMENTE, SolicitacaoTurma.SOLICITADA_ALTERACAO);
			
			if( !isEmpty( solicitacoes ) ){
				StringBuilder msg = new StringBuilder("Art.  246.  Cada  aluno  poder�  obter  matr�cula  em  " +
						" apenas  um  componente  curricular  por per�odo letivo especial de f�rias." +
						"(Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009)." +
						" N�o � poss�vel solicitar a cria��o desta turma de f�rias pois j� existe " +
						" outra solicita��o de turma de f�rias deste mesmo per�odo com o(s) seguinte(s) aluno(s): ");
				forExterno : for( DiscentesSolicitacao ds : solicitacao.getDiscentes() ){
					
					for( SolicitacaoTurma sol : solicitacoes ){
						
						for( DiscentesSolicitacao discSol : sol.getDiscentes() ){
							if( discSol.getDiscenteGraduacao().getId() == ds.getDiscenteGraduacao().getId() ){
								msg.append( "<br/> &nbsp;&nbsp;" + ds.getDiscenteGraduacao().getMatriculaNome() );
								continue forExterno;
							}
						}
						
					}
					
				}
				
				lista.addErro(msg.toString());
			}
		} finally {
			if (dao != null) dao.close();
		}
	}
	
	/** Retorna o fator de convers�o de cr�dito em carga hor�ria de acordo com o n�vel de ensino.
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException 
	 */
	private static int fatorConversaoCargaHorariaEmCreditos(char nivelEnsino) throws DAOException {
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(nivelEnsino);
		return parametros.getHorasCreditosAula();		
	}	
	
}
