/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Classe que possui os métodos de validação padrão de objetos da classe Turma.
 * 
 * @author Victor Hugo
 *
 */
public class TurmaValidator {
	
	/** Retorna uma instância de um DAO especificado.
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	
	/**
	 * Valida os dados básicos da turma, invocado após a submissão da primeira tela no cadastro de turmas lato sensu.
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
		
		validateRequired(turma.getDataInicio(), "Início", lista);
		validateMinValue(turma.getDataInicio(), CalendarUtils.createDate(1, 0, turma.getAno()), "Início", lista);
		validateRequired(turma.getDataFim(), "Fim", lista);
		validateMinValue(turma.getDataFim(), turma.getDataInicio(), "Fim", lista);
		
		if ( !turma.isDistancia() && turma.getId() != 0 )
			validateRequired(turma.getCodigo(), "Código da Turma", lista);
		
		
		if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
				turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
			lista.addErro("A capacidade da turma ("+turma.getCapacidadeAluno()+") não pode ser inferior à quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").");
		}
		
		if( !isEmpty(turma.getObservacao() ) )
			validateMaxLength(turma.getObservacao(), 100, "Observações", lista);
		
	}
	
	/**
	 * Valida os dados básicos da turma, invocado após a submissão da primeira tela no cadastro de turmas lato sensu.
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
			
			validateRequired(turma.getDataInicio(), "Início", lista);
			validateRequired(turma.getDataFim(), "Fim", lista);
			validateMinValue(turma.getDataFim(), turma.getDataInicio(),"Fim" , lista);
			
			CursoLato cursoLato = null;
			if(turma.getCurso() != null)
				cursoLato = cursoLatoDao.findById(turma.getCurso().getId());
			
			// valida se o período (início e fim) da turma está dentro do período do curso
			if (turma.getDataInicio() != null && cursoLato != null)
				validateMinValue(turma.getDataInicio(), cursoLato.getDataInicio(), "Início", lista);
			if (turma.getDataFim() != null && cursoLato != null)
				validateMaxValue(turma.getDataFim(), cursoLato.getDataFim(), "Fim", lista);
			
			if ( !turma.isDistancia() && turma.getId() != 0 )
				validateRequired(turma.getCodigo(), "Código da Turma", lista);
			
			if (!turma.isDistancia()) {
				validaInt(turma.getCapacidadeAluno(), "Capacidade de Alunos", lista);
				validateRequired(turma.getLocal(), "Local", lista);
			}
			
			ValidatorUtil.validateRange(turma.getPeriodo(), 1, 2, "Período", lista);
			
			if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
					turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
				lista.addErro("A capacidade da turma ("+turma.getCapacidadeAluno()+") não pode ser inferior à quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").");
			}
			
			if( !isEmpty(turma.getObservacao() ) )
				validateMaxLength(turma.getObservacao(), 100, "Observações", lista);
		} finally {
			if (cursoLatoDao != null) cursoLatoDao.close();
		}
	}
	
	/**
	 * Valida os dados básicos da turma, invocado após a submissão da primeira tela no cadastro de turmas.
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
						lista.addErro("O Período informado não é um período de férias válido.");
					else if (!turma.isTurmaFerias() && !PeriodoAcademicoHelper.getInstance().isPeriodoRegular(turma.getPeriodo()) && !(turma.isMedio()))
						lista.addErro("O período informado não é um período regular válido.");
				}
				
				if (turma.isGraduacao() && usuario.isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO)) {
					if (DiscenteHelper.somaSemestres( turma.getAno() ,  turma.getPeriodo(), 0) <  DiscenteHelper.somaSemestres(cal.getAno(), cal.getPeriodo(), 0))
						lista.addErro("Não é permitido criar turmas para ano-períodos anteriores ao ano-período atual.");
				}
			}
			
			// valida a carga horária máxima para o período de férias
			if (turma.getTipo() != null && turma.isTurmaFerias()) {
				ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametros(turma);
				if (parametros.getChMaximaTurmaFerias() != null
						&& turma.getDisciplina().getChTotal() > parametros.getChMaximaTurmaFerias())
					lista.addErro("A Carga Horária Total do Componente Curricular ("+turma.getDisciplina().getChTotal()+"h) não permite a criação de turmas de férias.");
			}
	
			if (turma.getAno() < ParametroHelper.getInstance().getParametroInt(ParametrosGerais.ANO_INICIAL_CADASTRO_TURMAS) || turma.getPeriodo() < 0 || turma.getPeriodo() > 4) {
				lista.getMensagens().add(new MensagemAviso("Ano-Período inválidos", TipoMensagemUFRN.ERROR));
				return;
			}
			
			// validações dependentes do Form Bean, caso não passado, ignoradas
			char nivelEnsino = turma.getDisciplina().getNivel();
	
			validateRequired(turma.getDataInicio(), "Início", lista);
			validateRequired(turma.getDataFim(), "Fim", lista);
			validateMinValue(turma.getDataFim(), turma.getDataInicio(),"Fim" , lista);
	
			// valida se as datas estão dentro do período letivo.
			// Validação de turmas de graduação feita no HorarioTurmaMBean
			if (!turma.isGraduacao() && turma.getDataInicio() != null && turma.getDataFim() != null && cal != null && turma.getTipo() != null && turma.getTipo() > 0) {
				if (turma.isTurmaFerias()) {
					validateRange(turma.getDataInicio(), cal.getInicioFerias(), cal.getFimFerias(), "Início", lista);
					validateRange(turma.getDataFim(), cal.getInicioFerias(), cal.getFimFerias(), "Fim", lista);
				} else {
					validateRange(turma.getDataInicio(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Início", lista);
					validateRange(turma.getDataFim(), cal.getInicioPeriodoLetivo(), cal.getFimPeriodoLetivo(), "Fim", lista);
				}
			}
			
			if ( !turma.isDistancia() && turma.getId() != 0 )
				validateRequired(turma.getCodigo(), "Código da Turma", lista);
	

			// na criação de turma se for de graduação o campus é obrigatório
			// só deve ter campus para turmas de graduação que NAO são EAD e que NAO são de convênio
			// Só valida para turmas abertas pois apenas turmas abertas podem ser alteradas, com exceção de ADMINISTRADOR_DAE e PPG 
			// que pode alterar turmas já consolidadas, porem só é possível alterar os docentes da turma, então não deve validar se a turma tem campus.
			if( turma.isCampusObrigatorio() && turma.isAberta() ){
				validateRequired(turma.getCampus(), "Campus", lista);
			}

			// a turma só deve possuir local se não for de bloco e possuir créditos de aula ou laboratório
			if (!turma.isDistancia()) {
				boolean validarLocal = (turma.getDisciplina().getDetalhes().getCrAula() + turma.getDisciplina().getDetalhes().getCrLaboratorio() > 0)
					|| (turma.getDisciplina().getDetalhes().getChAula() + turma.getDisciplina().getDetalhes().getChLaboratorio() > 0);
				if( !turma.getDisciplina().isBloco() && validarLocal )
					validateRequired(turma.getLocal(), "Local", lista);
			}
	

			if (!turma.isDistancia())
				validaInt(turma.getCapacidadeAluno(), "Capacidade de Alunos", lista);
	
			if (nivelEnsino != NivelEnsino.INFANTIL && nivelEnsino != NivelEnsino.MEDIO) {
				ValidatorUtil.validateRange(turma.getPeriodo(), 1, 4, "Período", lista);
			}
	
			if( !turma.isDistancia() && turma.isTurmaFerias() && turma.getId() == 0 && ( usuario == null || (usuario != null && !usuario.isUserInRole( SigaaPapeis.DAE ))) ){
				
				int total = 0;
				for( ReservaCurso rc : turma.getReservas() ){
					total += rc.getVagasReservadas() + rc.getVagasReservadasIngressantes();
				}
				int qtdMin = ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.QTD_MIN_DISCENTES_TURMA_FERIAS);
				if( total < qtdMin ){
					lista.addErro("É obrigatório ter no mínimo "+qtdMin+" alunos em turmas de férias.");
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
					lista.addErro("A quantidade de vagas total reservada para a turma (" + total + ") não pode ser superior a capacidade da turma(" + turma.getCapacidadeAluno() + ").");
			}
			
			if (!turma.isEad() && turma.getTotalMatriculados() > 0 &&
					turma.getCapacidadeAluno() != null && turma.getCapacidadeAluno() < turma.getTotalMatriculados()){
				String msgCapacidade = "A capacidade da turma ("+turma.getCapacidadeAluno()+") não pode ser inferior à quantidade de alunos matriculados ("+turma.getTotalMatriculados()+").";
				if (usuario != null && usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
					lista.addWarning(msgCapacidade);
				else
					lista.addErro(msgCapacidade);
			}
		
			if( !isEmpty(turma.getObservacao() ) )
				validateMaxLength(turma.getObservacao(), 100, "Observações", lista);
			
			if (turma.getId() == 0 && turma.isTurmaFerias()) {
				long count = turmaGradDao.countByDisciplinaAnoPeriodoTipoSituacao(turma.getDisciplina(), turma.getCampus(), turma.getAno(), turma.getPeriodo(), Turma.FERIAS, SituacaoTurma.getSituacoesAbertas());
				
				if (turma.getId() == 0 && count > 0 || turma.getId() > 0 && count > 1) {
					if (usuario != null && usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))
						lista.addWarning("Já existe uma turma de férias aberta para este componente curricular.");
					else
					lista.addErro("Já existe uma turma de férias aberta para este componente curricular.");
				}
			}
			
			/* Regras de obrigatoriedade: <br/>
			 * RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010
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
		ValidatorUtil.validateEmptyCollection("É necessário informar pelo menos um docente para a turma",docentes, lista);
	}

	/**
	 * Verifica se tem docentes inativos na turma, se tiver não permite criar a turma no ano-período atual, apenas em períodos anteriores.
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
					lista.getMensagens().add(new MensagemAviso("Turma com docentes inativos não pode ser definida para o ano-período atual.", TipoMensagemUFRN.ERROR));
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Valida o choque de horários entre uma coleção de turmas passada por parâmetro
	 * @param turmas
	 * @return
	 * @throws ArqException
	 */
	public static void validarChoqueHorarios(Collection<Turma> turmas, ListaMensagens lista){

		ArrayList<Turma> comChoques = new ArrayList<Turma>(0);
		StringBuilder msg = new StringBuilder();

		for( Turma turmaComparar : turmas ){
			if( !turmaComparar.getDisciplina().isExigeHorarioEmTurmas() )
				break; //Turma não tem aula presencial

			for( Turma turma : turmas ){
				if( !turmaComparar.getDisciplina().isExigeHorarioEmTurmas() )
					break; //Turma não tem aula presencial

				if( turmaComparar == turma	 )
					break; // NÃO DEVE COMPARAR HORÁRIOS DA MESMA TURMA

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
			retorno = "Ocorreram choque de horários com as seguintes turmas dos componentes: " + msg.toString();
			lista.addErro(retorno);
		}

	}

	/**
	 * Valida o choque de horários na mesma sala entre uma coleção de turmas passada por parâmetro
	 * utilizado no ensino técnico.
	 * @param turmas
	 * @return
	 */
	public static String validarChoqueHorariosSala(Collection<Turma> turmas){

		ArrayList<Turma> comChoques = new ArrayList<Turma>(0);
		StringBuilder msg = new StringBuilder();

		for( Turma turmaComparar : turmas ){

			for( Turma turma : turmas ){

				if( turmaComparar == turma	 )
					break; // NÃO DEVE COMPARAR HORÁRIOS DA MESMA TURMA

				for (HorarioTurma ht : turma.getHorarios()) {

					// se chocar o horário E o local for o mesmo
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
			retorno = "Ocorreu choque de horários com as seguintes turmas dos componentes: " + msg.toString();
			return retorno;
		}

		return null;
	}

	/**
	 * A turma DEVE possuir no MÍNIMO a quantidade de horários igual a soma dos créditos de aula + laboratório
	 * e NÃO PODE ultrapassar a soma de créditos total da disciplina (aula + laboratório + estagio).
	 * Ou seja, não precisa, necessariamente, possuir horários definidos para os créditos referentes a estagio
	 *
	 * turmas do tipo modulo deve possuir no mínimo 1 horário semanal E NAO TEM MAXIMO DE HORARIOS
	 * @param t
	 * @param lista
	 * @throws DAOException 
	 */
	public static void validaHorarios(Turma t, ListaMensagens lista, UsuarioGeral usuario) throws DAOException {
		
		if( t.isEad() || t.isTurmaEnsinoIndividual() || (t.isTurmaRegularOrigemEnsinoIndiv() && t.getId() == 0)) //não executa validação de horários para turmas EAD e de Ensino Individualizado.
			return;
		
		if( (t.getDisciplina().isModulo() || t.getDisciplina().isAtividade()) && t.getDisciplina().isExigeHorarioEmTurmas()){
			ValidatorUtil.validateEmptyCollection( "É obrigatório informar o horário da turma.", t.getHorarios(), lista);
			return;
		}

		int horariosMinimos = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
		
		if( horariosMinimos > 0 && t.getDisciplina().isExigeHorarioEmTurmas()){
			ValidatorUtil.validateEmptyCollection(
					"É obrigatório informar o horário da turma.",
					t.getHorarios(), lista);
		}
		
		// valida o início e fim do horário
		for (HorarioTurma ht : t.getHorarios()) {
			validateRange(ht.getDataInicio(), t.getDataInicio(), t.getDataFim(), "Data de início do horário", lista);
			validateRange(ht.getDataFim(), t.getDataInicio(), t.getDataFim(), "Data de início do horário", lista);
			validaOrdemTemporalDatas(ht.getDataInicio(), ht.getDataFim(), true, "Início e Fim do horário", lista);
		}
		
		if( t.isTurmaFerias() ){ // se for turma de férias não deve validar o máximo de horários. 
			validarHorarioTurmaFerias(t, lista, usuario);
			return;
		}		
		
		if (t.isGraduacao() && t.getDisciplina().isExigeHorarioEmTurmas()){
			int porcMin = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MIN_NUM_AULAS_EM_RELACAO_CH_TURMA );
			int porcMax = ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.PORCENTAGEM_MAX_NUM_AULAS_EM_RELACAO_CH_TURMA );
			if ( t.getPorcentagemAulas() < porcMin || t.getPorcentagemAulas() > porcMax )
				lista.addErro("A porcentagem do número de aulas do horário/período deve estar entre " +porcMin+ "% e "+porcMax+"% da porcentagem relativa ao número de aulas definido pela carga horária do componente. A porcentagem do número de aulas selecionado foi de "+t.getPorcentagemAulas()+"%" );
		}
		
		if (t.getDisciplina().getChTotal() != 0) {
			
			// se for turma de técnico e não tiver crédito não deve validar a quantidade de horários 
			if( t.getDisciplina().getNivel() == NivelEnsino.TECNICO || t.getDisciplina().getNivel() == NivelEnsino.FORMACAO_COMPLEMENTAR){
				return;
			}

			int numeroMinimo = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
			int numeroMaximo = t.getDisciplina().getChTotal()/fatorConversaoCargaHorariaEmCreditos(t.getDisciplina().getNivel());
			
			// se for turma de Médio e não tiver crédito não deve validar a quantidade de horários 
			if( t.getDisciplina().getNivel() == NivelEnsino.MEDIO && numeroMinimo == 0 ){
				return;
			}
	
			// Valida se o horário extrapolou os créditos
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
			lista.addErro("A soma dos dias letivos nos intervalos de cada período ("+diasPeriodo+") deve ser igual à quantidade de dias letivos da turma ("+diasTurma+").");
	}
	
	/**
	 * Verifica se os horários escolhidos são compatíveis com os créditos do componente curricular
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
		// turmas de lato sensu não tem crédito. neste caso, verifica-se se o horário definido é coerente com a carga horária.
		if ((turma.isLato() || !turma.getDisciplina().isDisciplina()) && !turma.getDisciplina().isPermiteHorarioFlexivel()) {
			if (HorarioTurmaUtil.calcularTotalHoras(turma.getHorarios()) != turma.getChTotalTurma()) {
			}
		} else 
			if ((qtdHorarios < numeroMinimo || qtdHorarios > numeroMaximo) &&  !turma.getDisciplina().isPermiteHorarioFlexivel() ){
			lista.addErro("Número de horários escolhidos é incompatível com os créditos do componente " +
					"da turma.<br>" +
					"O componente possui "+turma.getDisciplina().getCrTotal()+" créditos. A turma deve possuir a quantidade de horários igual a soma dos créditos de aula + laboratório do componente.");
		}
	}

	/**
	 * verifica se os dois docentes são a mesma pessoa.
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
	 * Validação invocada no momento que um docente é inserido numa turma 
	 * @param turma a turma a ser validada o docente inserido
	 * @param docenteTurma o docente inserido
	 * @param permiteCHCompartilhada se permite carga horário compartilhada
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
				lista.addErro("Docente: campo obrigatório não informado.");
			}
	
			// Verificando se o docente já está inserido na turma
			for( DocenteTurma dt : turma.getDocentesTurmas() ){
				// verifica se é o mesmo docente
				if ((!isEmpty(dt.getDocente())
							&& !isEmpty(docenteTurma.getDocente()) 
							&& dt.getDocente().getId() == docenteTurma.getDocente().getId())
						|| (!isEmpty(dt.getDocenteExterno())
							&& !isEmpty(docenteTurma.getDocenteExterno()) 
							&& dt.getDocenteExterno().getId() == docenteTurma.getDocenteExterno().getId())) {
					lista.addErro("Este docente já foi adicionado a esta turma.");
					break;
				} else {
					// verifica para docentes diferentes, se o horário conflita
					if (turma.getDisciplina().isPermiteHorarioDocenteFlexivel() && !permiteCHCompartilhada && dt.getGrupoDocente().equals(docenteTurma.getGrupoDocente())) {
						for (HorarioDocente hd : dt.getHorarios()) {
							for (HorarioDocente hdNovo : horariosDocente) {
								if (CalendarUtils.isIntervalosDeDatasConflitantes(hd.getDataInicio(), hd.getDataFim(), hdNovo.getDataInicio(), hdNovo.getDataFim())	&& hd.equals(hdNovo)){
									lista.addErro("Há choque de datas do horário deste docente com as datas informadas anteriormente.");
									break;
								}
							}
						}
					}
				}
			}
	
			// Valida a carga horária do docente
			if (!turma.isDistancia())
				validaCHDocenteTurma(turma, docenteTurma, permiteCHCompartilhada, lista, true, horariosDocente, docenteTurma.getGrupoDocente());

			dao = DAOFactory.getGeneric(Sistema.SIGAA);
			turma.setDisciplina( dao.refresh( turma.getDisciplina() ) );
			turma.getDisciplina().getUnidade().getId();
			if(turma.getDisciplina().getUnidade().getGestoraAcademica() != null)
				turma.getDisciplina().getUnidade().getGestoraAcademica().getId();
			
			// Só verifica se o docente está ativo no período se não for turma de lato sensu
			if(turma.getDisciplina().getNivel() != NivelEnsino.LATO){
				CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(turma);
				Servidor servidor = isEmpty(docenteTurma.getDocente()) ? docenteTurma.getDocenteExterno().getServidor() : docenteTurma.getDocente();
				boolean ativoNoPeriodo = validaStatusDocente(servidor, docenteTurma.getDocenteExterno(), turma.getAno(), turma.getPeriodo(), cal);
				if( !ativoNoPeriodo ){
					lista.addErro("Este docente não pode participar desta turma pois seu vínculo esta INATIVO no ano/período da turma.");
				}
			}
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Valida a carga horária do docente em relação a máxima permitida pela turma ou se a CH informada é maior que a da disciplina
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
	 * Valida a carga horária do docente em relação a máxima permitida pela turma ou se a CH informada é maior que a da disciplina.
	 * A validação é feita para docentes de diferentes níveis de ensino exceto graduação.
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
			ValidatorUtil.validaInt(docenteTurma.getChDedicadaPeriodo(), "Carga Horária", lista);
		
		if(grupo == null) grupo = docenteTurma.getGrupoDocente();
		// Só faz verificação de CH se não for EAD
		// ou se o componente curricular estiver setado para não ser verificar a carga horária
		if (!permiteCHCompartilhada && !turma.isDistancia()) {
			// valida se a adição da CH desse docente excede a CH máxima da turma
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
					lista.addErro("Docente com essa CH não pode ser adicionado. A CH total excede à CH da disciplina da turma.");
				}
			} else {
				if (totalAteAgora > turma.getChTotalTurma() ) {
					lista.addErro("Docente com essa CH não pode ser adicionado. A CH total excede à CH da disciplina da turma.");
				}
			}
		} else {
			// valida apenas se a CH informada é maior que a da disciplina
			if (docenteTurma.getChDedicadaPeriodo() > turma.getChTotalTurma()) {
				lista.addErro("Docente com essa CH não pode ser adicionado. A CH total excede à CH da disciplina da turma.");
			}
		}
	}
	
	/**
	 * Valida a porcentagem carga horária do docente em relação a máxima permitida pela turma ou se a CH informada é maior que a da disciplina.
	 * A validação é feita para docentes de diferentes níveis de ensino exceto graduação.
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
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Carga Horária");
		if ( docenteTurma.getPorcentagemChDedicada() != null && docenteTurma.getPorcentagemChDedicada() > 100 )
			lista.addMensagem(MensagensArquitetura.VALOR_MENOR_IGUAL_A, "Carga Horária", 100);
		if ( docenteTurma.getPorcentagemChDedicada() != null && docenteTurma.getPorcentagemChDedicada() <= 0)
			lista.addMensagem(MensagensArquitetura.VALOR_MAIOR_IGUAL_A, "Carga Horária", 1);
		
		boolean permiteGrupo = turma.getDisciplina().getNumMaxDocentes() > 1;
		
		// Só faz verificação de CH se não for EAD
		// ou se o componente curricular estiver setado para não ser verificar a carga horária
		if (!permiteCHCompartilhada && !turma.isDistancia()) {
			// valida se a adição da CH desse docente excede a CH máxima da turma
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
					lista.addErro("O docente "+ docenteTurma.getDocenteNome() +" não pode ser adicionado com essa CH. A porcentagem da CH total"+(permiteGrupo?"do Grupo "+grupo:"")+" excede 100%.");
				}
			} else {
				if (totalAteAgora > 100) {
					lista.addErro("Docente "+ docenteTurma.getDocenteNome() +" não pode ser adicionado com essa CH. A porcentagem da CH total"+(permiteGrupo?"do Grupo "+grupo:"")+" excede 100%.");
				}
			}
		} 
	}
	
	/**
	 * Valida se o docente está ativo no ano período informado, se ele não foi desligado antes daquele período.
	 * Se o ano-período informado for o ano-período atual, o servidor precisa estar ativo necessariamente.
	 * @param servidor
	 * @param ano
	 * @param periodo
	 * @param erros
	 * @return true caso esteja ativo
	 * @throws DAOException
	 */
	public static boolean validaStatusDocente( Servidor servidor,  DocenteExterno docenteExterno ,int ano, int periodo, CalendarioAcademico cal) throws DAOException{
		// define mês o de início do período letivo como janeiro
		int mesInicioPeriodo = 1; 

		if( cal == null ){
			cal = new CalendarioAcademico();
			cal.setAno(ano);
			cal.setPeriodo(periodo);
			// se o período for 1, o mês inicial será janeiro (1). Caso contrário, será julho (7)
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

			// se for colaborador voluntário pode ser associado a turma 
			ColaboradorVoluntarioDao dao = getDAO(ColaboradorVoluntarioDao.class);
			try {
				if( dao.isColaboradorVoluntario(servidor) )
					return true;
			} finally {
				dao.close();
			}

			// o docente pode ser inserido na turma se a data de desligamento dele for posterior posterior a data de Início
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
	 * Valida se os horários dos docentes da turma são compatíveis com as outras turmas do docente
	 * @param turma
	 * @param docenteTurma
	 * @param lista
	 * @throws DAOException
	 * @throws SQLException 
	 */
	public static void validaHorariosDocenteTurma(Turma turma,  DocenteTurma docenteTurma, ListaMensagens lista, UsuarioGeral usuario, HashMap<Integer, ParametrosGestoraAcademica> cacheParam) throws DAOException {
		
		if( turma.isDistancia() )
			return;
		
		// verifica se o horário do docente está definido para a turma
		if (!ValidatorUtil.isEmpty(turma.getHorarios()) && ValidatorUtil.isEmpty(docenteTurma.getHorarios()))
			lista.addErro("O docente " + docenteTurma.getDocenteNome() + " não está com o horário na turma definido.");
		
		// Verifica se o horário do docente é compatível com o horário da turma.
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
			lista.addErro("O(s) horário(s) "+StringUtils.transformaEmLista(CollectionUtils.toList(incompativel))+" do docente "+docenteTurma.getDocenteNome()+" não corresponde ao horário da turma. Como o horário da turma foi alterado, por favor adicione um novo docente de acordo com o novo horário (caso o docente já esteja na lista, remova-o e insira novamente).");
		
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
				// Verificando se o docente não está inserido em outra turma no mesmo horário
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
						StringBuffer msg = new StringBuffer("O docente " + docenteTurma.getDocenteNome() + " está vinculado a outra(s) turma(s) no mesmo horário: <br/>");
						for (Turma t : choque) {
							msg.append("&nbsp;&nbsp;&nbsp;-&nbsp;" + t.getDisciplina().getCodigo() + " - " + t.getCodigo() +" nos horários: ");
							
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
							lista.addWarning(msg.toString() + "Se concluir a operação o docente será inserido com choque de horário.");
						else
							lista.addErro(msg.toString());
					}
					
					// se o docente participar de mais de uma turma no exatamente MESMO horário então a carga horária conjunta dele em todas as turmas não pode ultrapassar
					// a quantidade de horários. 
					// Ex: se ele participa de duas turmas junto com outro professor no horário 24M34, então a carga horária de cada docente não pode ultrapassar 60 horas.
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
							String msg = "O docente " + docenteTurma.getDocenteNome() + " está vinculado a outra(s) turma(s) no mesmo horário (" 
									+ codTurmas.toString().substring(0, codTurmas.toString().length() - 2)  
									+ ") portanto, a soma da carga horária dedicada do docente em todas as turmas não pode ser superior a " 
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

	/** Carrega os parâmetros da gestora acadêmica
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
	 * Valida se é possível alterar o horario do plano de matrícula. Esse metodo varre todos os planos que a turma faz parte e verifica se existe choque de horario.
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
			StringBuilder msg = new StringBuilder("Choque de horário com outra(s) turma(s) no Plano de Matrícula: <br/>");
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
	 * Valida o horário de todos os discentes de uma turma
	 * utilizado por exemplo quando vai alterar o horário de uma turma que tem alunos matriculados, é necessário verificar o horário
	 * de todos os discente com as turmas que eles já estão matriculados
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
			
			// Validar também se há choque de horários com os alunos que solicitaram matrícula na turma, pois ao solicitar turma não é criado um registro em MatriculaComponente.
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
			
			StringBuilder sb = new StringBuilder( "Há choque de horário com turmas dos seguintes discentes: " );
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
	 * Valida o horário de todas as disciplinas de uma turma de ensino médio.
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
						"Há choque de horário com a(s) seguinte(s) disciplina(s) da Turma: " );
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
	 * Validar se a capacidade da turma comporta os alunos já matriculados
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
					lista.addErro("A capacidade informada é menor do que a quantidade de alunos ativamente matriculados no componente." +
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
				lista.addErro("A capacidade da turma é menor que a soma das reservas. Por favor, ajuste os valores informados.");
		}
	}

	/**
	 * Valida regras específicas das turmas de férias, deve ser chamado apenas para solicitação de turma de ferias ou seja, turmas para os semestre 3 e 4.
	 * Regras de validação:
	 * O numero de aulas não deverá exceder o limite de 3 horas por turno e 6 horas diárias.
	 * Só pode ser oferecido curso de ferias para disciplinas com carga horária inferior a 90 horas.
	 * @param t
	 *
	 */
	public static void validarHorarioTurmaFerias(Turma t, ListaMensagens lista, UsuarioGeral usuario) {

		// Administrador DAE não precisa validar  
		if ( !usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE )) {
			if ( t.getDisciplina().getDetalhes().getChTotal() >  ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.CARGA_HORARIA_DISCIPLINA_FERIAS ) )
				lista.addMensagem(MensagensGraduacao.ERRO_VALIDACAO_CARGA_HORARIA_DISCIPLINA_FERIAS, ParametroHelper.getInstance().getParametroInt( ParametrosGraduacao.CARGA_HORARIA_DISCIPLINA_FERIAS ));
		}

		int numeroMinimo = t.getDisciplina().getDetalhes().getCrAula() + t.getDisciplina().getDetalhes().getCrLaboratorio();
		int qtdHorarios = 0;
		if ( t.getHorarios() != null ) 
			qtdHorarios = t.getHorarios().size();
		
		if (  qtdHorarios < numeroMinimo ){
			lista.getMensagens().add(new MensagemAviso("Número de horários escolhidos incompatível com os créditos do componente " +
					"da turma.<br>" +
					"A turma deve possuir no mínimo a quantidade de horarios igual a soma dos creditos de aula + laboratorio (" + numeroMinimo + " créditos).", TipoMensagemUFRN.ERROR));
		}
		
		int horasMinimas = t.getDisciplina().getDetalhes().getChAula() + t.getDisciplina().getDetalhes().getChLaboratorio();
		int totalHoras = 0;
		if ( t.getHorarios() != null  )
			totalHoras = HorarioTurmaUtil.calcularTotalHoras(t.getHorarios());
		int horasFaltantes = horasMinimas - totalHoras;
		
		if (  totalHoras < horasMinimas && t.getDisciplina().isExigeHorarioEmTurmas() ){
			lista.getMensagens().add(new MensagemAviso("Número de horas escolhidas  é incompatível com as horas do componente " +
					"da turma.<br>" +
					"A turma deve possuir no mínimo a quantidade de horas aulas igual a carga horária da disciplina  " +
					"(" + t.getDisciplina().getDetalhes().getChAula() + " horas) " +
					" + carga horária de laboratorio (" + t.getDisciplina().getDetalhes().getChLaboratorio() + " horas). " +
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
	 * Verifica se o número de aulas, por disciplina, em um período letivo
	 * especial de férias, não excede o limite de horas por turno e horas
	 * diárias.
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
	 * Valida a adição de um discente em uma solicitação de turma de férias
	 * @param solicitacao a solicitação que esta sendo adicionada
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
	
			// verificando se o discente já foi adicionado a lista de interessados desta turma de férias
			for( DiscentesSolicitacao d : solicitacao.getDiscentes() ){
				if( d.getDiscenteGraduacao().getId() == discente.getId() ){
					lista.addErro("Este discente já foi adicionado na lista de interessados neste curso de férias.");
					return;
				}
			}
	
			// verificando se já existe uma turma / solicitação de turma de férias para este discente
			Integer [] situacoes = new Integer[] { SolicitacaoTurma.ABERTA, SolicitacaoTurma.ATENDIDA, SolicitacaoTurma.ATENDIDA_ALTERACAO, SolicitacaoTurma.ATENDIDA_PARCIALMENTE, SolicitacaoTurma.SOLICITADA_ALTERACAO };
			if( dao.findByDiscenteAnoPeriodo(discente.getId(), solicitacao.getPeriodo(), solicitacao.getAno(), situacoes ).size() > 0 ) {
				lista.addErro("Art.  246.  Cada  aluno  poderá  obter  matrícula  em  apenas  um  componente  curricular  por período letivo especial de férias. " +
						"(Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009). " +
						"Este aluno já possui interesse em outro curso de férias, nao podendo portanto obter neste também.");
				return;
			}
	
			// verificando se o discente já pagou a disciplina ou equivalentes da turma de férias em questão
			ddao = getDAO( DiscenteDao.class );
			matdao = getDAO( MatriculaComponenteDao.class );
			RestricoesMatricula restricoes = RestricoesMatricula.getRestricoesTurmaFerias();
	
			Collection<ComponenteCurricular> componentesConcluidos  = ddao.findComponentesCurricularesConcluidos(discente);
			Collection<ComponenteCurricular> componentesMatriculados = matdao.findComponentesMatriculadosByDiscente(discente);
	
			// criando uma coleção de todos os componentes (concluídos e das atividades do semestre)
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
	 * Valida a solicitação de ensino individual:
	 * verifica se ha choque de horário com as solicitações de matrícula dos alunos da solicitação de ensino. individual
	 * @param solicitacao
	 * @param lista
	 * @throws DAOException
	 */
	public static void validarSolicitacaoTurmaEnsinoIndividual(SolicitacaoTurma solicitacao, CalendarioAcademico cal, ListaMensagens lista) throws DAOException {
		HorarioDao daoH = null;
		try {
			//cria uma turma para passar para o método que verifica choque de horário
			Turma turma = new Turma();
			turma.setAno( solicitacao.getAno() );
			turma.setPeriodo( solicitacao.getPeriodo() );
			turma.setDisciplina(solicitacao.getComponenteCurricular());
			turma.setDataInicio( cal.getInicioPeriodoLetivo() );
			turma.setDataFim( cal.getFimPeriodoLetivo() );
	
			// seta os horários da solicitação
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
	 * Verificando se já não foi criado turma de férias com algum dos discentes da solicitação.
	 * Esta validação é necessária pois, por falha do sistema, alguns discentes conseguiram 
	 * solicitar turma de férias em mais de um componente curricular
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
				StringBuilder msg = new StringBuilder("Art.  246.  Cada  aluno  poderá  obter  matrícula  em  " +
						" apenas  um  componente  curricular  por período letivo especial de férias." +
						"(Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009)." +
						" Não é possível solicitar a criação desta turma de férias pois já existe " +
						" outra solicitação de turma de férias deste mesmo período com o(s) seguinte(s) aluno(s): ");
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
	
	/** Retorna o fator de conversão de crédito em carga horária de acordo com o nível de ensino.
	 * @param nivelEnsino
	 * @return
	 * @throws DAOException 
	 */
	private static int fatorConversaoCargaHorariaEmCreditos(char nivelEnsino) throws DAOException {
		ParametrosGestoraAcademica parametros = ParametrosGestoraAcademicaHelper.getParametrosNivelEnsino(nivelEnsino);
		return parametros.getHorasCreditosAula();		
	}	
	
}
