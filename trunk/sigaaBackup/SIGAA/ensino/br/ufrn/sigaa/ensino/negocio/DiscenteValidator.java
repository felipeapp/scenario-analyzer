/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Faz as validações referentes aos dados
 * de alunos.
 * Os métodos de avaliação devem ser referentes aos diferentes
 * tipos de discentes.
 * @author Andre M Dantas
 *
 */
public class DiscenteValidator {

	
	/**
	 * Sobrecarga devido a transição do cadastro discente técnico para STRUT's/JSF
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteTecnico(DiscenteTecnico discente, DiscenteForm dForm, ListaMensagens lista) throws DAOException {
		validarDadosDiscenteTecnico(discente, dForm, false, lista);
	}
	
	/**
	 * Método aceitando parâmetro obj ou form devido a transição do cadastro discente técnico para STRUT's/JSF
	 * valida a submissão dos dados específicos do aluno do técnico
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteTecnico(DiscenteTecnico discente, DiscenteForm dForm, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		DiscenteTecnico d = (discente != null) ? discente: dForm.getDiscenteTecnico();
		
		if(discenteAntigo){
			if(!ValidatorUtil.isEmpty( d.getMatricula()))
				ValidatorUtil.validaLong(d.getMatricula(), "Matrícula", lista);
			else
				ValidatorUtil.validateRequired(d.getMatricula(), "Matrícula", lista);
			
			ValidatorUtil.validaInt(d.getAnoIngresso(), "Ano de Ingresso", lista);
			
			if(!ValidatorUtil.isEmpty(d.getPeriodoIngresso())){
				ValidatorUtil.validateMinValue(d.getPeriodoIngresso(), 1, "Período de Ingresso", lista);
				validateMaxValue(d.getPeriodoIngresso(), 2, "Período de Ingresso", lista);
			}else
				ValidatorUtil.validateRequired(d.getPeriodoIngresso(), "Período de Ingresso", lista);
			
			validateRequiredId(d.getStatus(), "Status", lista);
		}
		
		if (d.getCurso().getId() != ParametroHelper.getInstance().getParametroInt(ParametrosTecnico.ID_CURSO_METROPOLE_DIGITAL_PARA_CONVOCACAO)){
			validateRequiredId(d.getTurmaEntradaTecnico().getId(), "Turma de Entrada", lista);
			validateRequiredId(d.getEstruturaCurricularTecnica().getCursoTecnico().getId(), "Curso", lista);
			validateRequiredId(d.getEstruturaCurricularTecnica().getId(), "Currículo", lista);
			validarCapacidadeTurmaEntradaTecnico(d, lista);
		}

	}

	/**
	 * Verifica se a turma de entrada do aluno já está com sua capacidade preenchida.
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
	 * Valida a submissão dos dados específicos do aluno lato sensu
	 * @throws DAOException 
	 */
	public static void validarDadosDiscenteLato(DiscenteLato discente, DiscenteForm form, ListaMensagens lista) throws DAOException {

		// campos obrigatórios
		// (turma de entrada, ano, período)
		validateRequiredId(discente.getTurmaEntrada().getId(), "Turma de Entrada", lista);
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Período de Ingresso", lista);
		ValidatorUtil.validateRequiredId(discente.getProcessoSeletivo().getId(), "Processo Seletivo", lista);

		if (lista.size() > 0) {
			return;
		}
		// inteiros maiores ou igual a zero
		// (ano [no mínimo ano anterior], período)
		ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Período de Ingresso", lista);
		validateMaxValue(discente.getPeriodoIngresso(), 2, "Período de Ingresso", lista);
		
		/** Verifica se é permtido o discente ter mais de um vínculo ativo em cursos de nível lato sensu. */
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
	 * Valida a submissão dos dados específicos do aluno de graduação
	 * @throws DAOException
	 */
	public static void validarDadosDiscenteGraduacao(DiscenteGraduacao discente, boolean discenteAntigo, ListaMensagens lista) throws DAOException {

		if (discenteAntigo) {
			ValidatorUtil.validateRequired(discente.getMatricula(), "Matrícula", lista);
		}

		// campos obrigatorios
		validateRequiredId(discente.getFormaIngresso().getId(), "Forma de Ingresso", lista);
		if (discente.getTipo() == Discente.REGULAR) {
			validateRequiredId(discente.getCurso().getId(), "Curso", lista);
			if(discente.getMatrizCurricular() != null) {
				validateRequiredId(discente.getMatrizCurricular().getId(), "Matriz Curricular", lista);
				if (discente.getCurso() != null && discente.getCurso().getId() != discente.getMatrizCurricular().getCurso().getId())
					lista.addErro("O curso do discente não é o mesmo curso da matriz curricular.");
			}
		}

		ValidatorUtil.validateRequired(discente.getPerfilInicial(), "Perfil Inicial", lista);
		validateRequiredId(discente.getTipo(), "Tipo", lista);
		validateRequiredId(discente.getStatus(), "Status", lista);
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano de Ingresso", lista);
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Período de Ingresso", lista);


		/**
		 * verificando se o aluno possui matrícula ativa em algum curso de graduação
		 * só é efetuado este teste caso esteja cadastrando o discente e a pessoa já exista no banco
		 */
		if( discente.getId() == 0 ){

			if (discenteAntigo) {
				validateMaxValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual(), "Ano de Ingresso", lista);
				if (discente.isConcluido()){
						ValidatorUtil.validateRequired(discente.getDataColacaoGrau(), "Data de Colação de Grau", lista);
				}
			} else {
				ValidatorUtil.validateMinValue(discente.getAnoIngresso(), CalendarUtils.getAnoAtual() - 1, "Ano de Ingresso", lista);
			}
			ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Período de Ingresso", lista);
			ValidatorUtil.validateMinValue(discente.getPerfilInicial(), 0, "Perfil Inicial", lista);

			verificarDuplicidadeDiscenteGraduacaoCadastrado(discente, lista);
		}

		if (lista.size() > 0) {
			return;
		}

	}

	/**
	 * Verifica se já existe outro discente de graduação com o status CADASTRADO associado a esta pessoa
	 * só verifica se o discente passado tiver o status CADASTRADO
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
					lista.getMensagens().add( new MensagemAviso("Atenção! Não é possível continuar o processo de cadastro " +
							" pois já existe um discente com o status CADASTRADO associado a esta pessoa (mat. " + discentes.iterator().next().getMatricula() + ").",
							TipoMensagemUFRN.ERROR) );
				}
			} finally {
				discenteDao.close();
			}
		}
	}
	
	/**
	 * Valida a submissão dos dados específicos do aluno de Residência Médica 
	 */
	public static void validarDadosDiscenteResidenciaMedica(DiscenteResidenciaMedica discente, ListaMensagens lista) {
		
		// campos obrigatórios
		ValidatorUtil.validateRequired(discente.getAnoIngresso(), "Ano Inicial", lista);
		
		ValidatorUtil.validateRequired(discente.getPeriodoIngresso(), "Período Inicial", lista);
		ValidatorUtil.validateMinValue(discente.getPeriodoIngresso(), 1, "Período Inicial", lista);
		validateMaxValue(discente.getPeriodoIngresso(), 2, "Período Inicial", lista);
		
		ValidatorUtil.validateRequired(discente.getMesEntrada(), "Mês de Entrada", lista);
		ValidatorUtil.validateRequired(discente.getLocalGraduacao(), "Local de Graduação", lista);
		ValidatorUtil.validateRequired(discente.getCrm(), "CRM", lista);
		ValidatorUtil.validateRequired(discente.getNivelEntradaResidente(), "Nível de entrada", lista);
		
		validateRequiredId(discente.getGestoraAcademica().getId(), "Programa", lista);
		
	}

}
