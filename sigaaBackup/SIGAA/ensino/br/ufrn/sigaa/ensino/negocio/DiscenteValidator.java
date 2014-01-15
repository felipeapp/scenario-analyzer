/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/04/2008
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.UFRNUtils.getMensagem;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;

import java.util.Collection;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.complexohospitalar.dominio.DiscenteResidenciaMedica;
import br.ufrn.sigaa.ensino.form.DiscenteForm;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.latosensu.dominio.DiscenteLato;
import br.ufrn.sigaa.ensino.tecnico.dao.TurmaEntradaTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosLatoSensu;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Faz as valida��es referentes aos dados
 * de alunos.
 * Os m�todos de avalia��o devem ser referentes aos diferentes
 * tipos de discentes.
 * @author Andre M Dantas
 *
 */
public class DiscenteValidator {

	
	/**
	 * Sobrecarga devido a transi��o do cadastro discente t�cnico para STRUT's/JSF
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteTecnico(DiscenteTecnico discente, DiscenteForm dForm, ListaMensagens lista) throws DAOException {
		validarDadosDiscenteTecnico(discente, dForm, false, lista);
	}
	
	/**
	 * M�todo aceitando par�metro obj ou form devido a transi��o do cadastro discente t�cnico para STRUT's/JSF
	 * valida a submiss�o dos dados espec�ficos do aluno do t�cnico
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteTecnico(DiscenteTecnico discente, DiscenteForm dForm, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		DiscenteTecnico d = (discente != null) ? discente: dForm.getDiscenteTecnico();
		
		if(discenteAntigo){
			if(!ValidatorUtil.isEmpty( d.getMatricula()))
				ValidatorUtil.validaLong(d.getMatricula(), "Matr�cula", lista);
			else
				ValidatorUtil.validateRequired(d.getMatricula(), "Matr�cula", lista);
			
			ValidatorUtil.validaInt(d.getAnoIngresso(), "Ano de Ingresso", lista);
			
			if(!ValidatorUtil.isEmpty(d.getPeriodoIngresso())){
				ValidatorUtil.validateMinValue(d.getPeriodoIngresso(), 1, "Per�odo de Ingresso", lista);
				validateMaxValue(d.getPeriodoIngresso(), 2, "Per�odo de Ingresso", lista);
			}else
				ValidatorUtil.validateRequired(d.getPeriodoIngresso(), "Per�odo de Ingresso", lista);
			
			validateRequiredId(d.getStatus(), "Status", lista);
		}
		
		if (d.getCurso().getId() != ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO)){
			validateRequiredId(d.getTurmaEntradaTecnico().getId(), "Turma de Entrada", lista);
			validateRequiredId(d.getEstruturaCurricularTecnica().getCursoTecnico().getId(), "Curso", lista);
			validateRequiredId(d.getEstruturaCurricularTecnica().getId(), "Curr�culo", lista);
			validarCapacidadeTurmaEntradaTecnico(d, lista);
		}

	}

	/**
	 * Verifica se a turma de entrada do aluno j� est� com sua capacidade preenchida.
	 * @param d
	 * @param lista
	 * @throws DAOException
	 */
	private static void validarCapacidadeTurmaEntradaTecnico(DiscenteTecnico d,
			ListaMensagens lista) throws DAOException {
		TurmaEntradaTecnicoDao turmaEntradaDao = DAOFactory.getInstance().getDAO( TurmaEntradaTecnicoDao.class );
		try {
			if( d.getId() == 0 && turmaEntradaDao.isExcedeuCapacidade(d.getTurmaEntradaTecnico()))
				lista.addErro("A turma de entrada selecionada excedeu sua capacidade.");
		} finally {
			turmaEntradaDao.close();
		}
	}

	/**
	 * Valida a submiss�o dos dados espec�ficos do aluno lato sensu
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteLato(DiscenteLato discente, DiscenteForm form, ListaMensagens lista) throws DAOException {

		// campos obrigat�rios
		// (turma de entrada, ano, per�odo)
		validateRequiredId(discente.getTurmaEntrada().getId(), "Turma de Entrada", lista);
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Per�odo de Ingresso", lista);
		ValidatorUtil.validateRequiredId(discente.getProcessoSeletivo().getId(), "Processo Seletivo", lista);

		if (lista.size() > 0) {
			return;
		}
		// inteiros maiores ou igual a zero
		// (ano [no m�nimo ano anterior], per�odo)
		ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Per�odo de Ingresso", lista);
		validateMaxValue(discente.getPeriodoIngresso(), 2, "Per�odo de Ingresso", lista);
		
		/** Verifica se � permtido o discente ter mais de um v�nculo ativo em cursos de n�vel lato sensu. */
		if( !ParametroHelper.getInstance().getParametroBoolean( 
						ParametrosLatoSensu.PERMITE_CADASTRAR_DISCENTE_COM_MATRICULA_ATIVA ) ){
			
			boolean possuiVinculo = DAOFactory.getInstance().
				getDAO( DiscenteDao.class).possuiVinculo(
						discente.getNivel(), discente.getPessoa().getCpf_cnpj() );
			
			if( possuiVinculo  ){
				lista.addMensagem( getMensagem( MensagensGerais.DISCENTE_JA_POSSUI_VINCULO_ATIVO, discente.getNivelDesc() ) );
			}
		}	
		
	}

	/**
	 * Valida a submiss�o dos dados espec�ficos do aluno de gradua��o
	 * @throws DAOException
	 */
	public static void validarDadosDiscenteGraduacao(DiscenteGraduacao discente, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		if (discenteAntigo) {
			ValidatorUtil.validateRequired(discente.getMatricula(), "Matr�cula", lista);
		}

		// campos obrigatorios
		validateRequiredId(discente.getFormaIngresso().getId(), "Forma de Ingresso", lista);
		if (discente.getTipo() == Discente.REGULAR) {
			validateRequiredId(discente.getCurso().getId(), "Curso", lista);
			if(discente.getMatrizCurricular() != null) {
				validateRequiredId(discente.getMatrizCurricular().getId(), "Matriz Curricular", lista);
				if (discente.getCurso() != null && discente.getCurso().getId() != discente.getMatrizCurricular().getCurso().getId())
					lista.addErro("O curso do discente n�o � o mesmo curso da matriz curricular.");
			}
		}

		ValidatorUtil.validateRequired(discente.getPerfilInicial(), "Perfil Inicial", lista);
		validateRequiredId(discente.getTipo(), "Tipo", lista);
		validateRequiredId(discente.getStatus(), "Status", lista);
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Per�odo de Ingresso", lista);


		/**
		 * verificando se o aluno possui matr�cula ativa em algum curso de gradua��o
		 * s� � efetuado este teste caso esteja cadastrando o discente e a pessoa j� exista no banco
		 */
		if( discente.getId() == 0 ){

			if (discenteAntigo) {
				validateMaxValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual(), "Ano de Ingresso", lista);
				if (discente.isConcluido()){
						ValidatorUtil.validateRequired(discente.getDataColacaoGrau(), "Data de Cola��o de Grau", lista);
				}
			} else {
				ValidatorUtil.validateMinValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual() - 1, "Ano de Ingresso", lista);
			}
			ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Per�odo de Ingresso", lista);
			ValidatorUtil.validateMinValue(discente.getPerfilInicial(), 0, "Perfil Inicial", lista);

			verificarDuplicidadeDiscenteGraduacaoCadastrado(discente, lista);
		}

		if (lista.size() > 0) {
			return;
		}

	}

	/**
	 * Verifica se j� existe outro discente de gradua��o com o status CADASTRADO associado a esta pessoa
	 * s� verifica se o discente passado tiver o status CADASTRADO
	 * @param discente
	 * @param lista
	 * @throws DAOException
	 */
	public static void verificarDuplicidadeDiscenteGraduacaoCadastrado(DiscenteGraduacao discente, ListaMensagens lista) throws DAOException{
		if( discente.getStatus() == StatusDiscente.CADASTRADO ){
			DiscenteDao discenteDao = DAOFactory.getInstance().getDAO( DiscenteDao.class );
			try {
				Collection<DiscenteGraduacao> discentes = discenteDao.findByPessoaSituacao( discente.getPessoa().getId(), StatusDiscente.CADASTRADO);
				if( !isEmpty(discentes) ){
					lista.getMensagens().add( new MensagemAviso("Aten��o! N�o � poss�vel continuar o processo de cadastro " +
							" pois j� existe um discente com o status CADASTRADO associado a esta pessoa (mat. " + discentes.iterator().next().getMatricula() + ").",
							TipoMensagemUFRN.ERROR) );
				}
			} finally {
				discenteDao.close();
			}
		}
	}
	
	/**
	 * Valida a submiss�o dos dados espec�ficos do aluno de Resid�ncia M�dica 
	 */
	public static void validarDadosDiscenteResidenciaMedica(DiscenteResidenciaMedica discente, ListaMensagens lista) {
		
		// campos obrigat�rios
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano Inicial", lista);
		
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Per�odo Inicial", lista);
		ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Per�odo Inicial", lista);
		validateMaxValue(discente.getPeriodoIngresso(), 2, "Per�odo Inicial", lista);
		
		ValidatorUtil.validateRequired(discente.getMesEntrada(), "M�s de Entrada", lista);
		ValidatorUtil.validateRequired(discente.getLocalGraduacao(), "Local de Gradua��o", lista);
		ValidatorUtil.validateRequired(discente.getCrm(), "CRM", lista);
		ValidatorUtil.validateRequired(discente.getNivelEntradaResidente(), "N�vel de entrada", lista);
		
		validateRequiredId(discente.getGestoraAcademica().getId(), "Programa", lista);
		
	}

}
