/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 07/02/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.TreeSet;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.RenovacaoAtividadePosDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.expressao.ExpressaoUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.OrientacaoAtividade;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoRegistroAtividade;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.TurmaValidator;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.RenovacaoAtividadePos;
import br.ufrn.sigaa.ensino.stricto.negocio.MatriculaStrictoValidator;
import br.ufrn.sigaa.ensino.tecnico.dao.DiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Valida��es espec�ficas para o registro de atividades acad�micas.
 * 
 * @author Ricardo Wendell
 *
 */
public class RegistroAtividadeValidator {

	/**
	 * Verifica se a atividade selecionada pode ser validada
	 *
	 * @param atividade
	 * @param erros
	 */
	public static void isPassivelValidacao(ComponenteCurricular atividade,
			ListaMensagens erros) {
		if (atividade == null || atividade.getTipoAtividade() == null) {
			return;
		}
		if (atividade.getTipoAtividade().getId() == TipoAtividade.TRABALHO_CONCLUSAO_CURSO) {
			erros.addErro(
					"De acordo com o Art. 237, � vedada a valida��o de trabalhos de conclus�o de curso." +
					"(Regulamento dos Cursos Regulares de Gradua��o, Resolu��o N� 227/2009-CONSEPE, de 3 de dezembro de 2009).");
		}
	}

	/**
	 * Verifica se o discente j� possui uma matr�cula na atividade selecionada
	 *
	 * @param discente
	 * @param atividade
	 * @param erros
	 * @throws DAOException
	 */
	public static void verificarDuplicidade(DiscenteAdapter discente,
			ComponenteCurricular atividade, ListaMensagens erros)
			throws DAOException {

		MatriculaComponenteDao matriculaDao = new MatriculaComponenteDao();
		try {
			Collection<MatriculaComponente> matriculas = matriculaDao
					.findByDiscenteEDisciplina(discente, atividade,
							SituacaoMatricula.getSituacoesPagasEMatriculadas());			
			
			for(MatriculaComponente m : matriculas) {
				if( ! atividade.isConteudoVariavel() || (atividade.isConteudoVariavel() && m.isMatriculado() ) ) {
					erros.addErro(
							"N�o � poss�vel registrar a atividade "
									+ atividade.getNome()
									+ ", pois o aluno j� est� matriculado ou o registro j� foi integralizado.");
				}
			}
		} finally {
			matriculaDao.close();
		}

	}

	/** Valida as atividades
	 * @param atividade
	 * @param erros
	 */
	public static void validarAtividade(ComponenteCurricular atividade,
			ListaMensagens erros) {
		if (atividade.getTipoComponente().getId() != TipoComponenteCurricular.ATIVIDADE) {
			erros.addErro(
					"O componente curricular selecionado n�o � uma atividade acad�mcia espec�fica");
		} else if (atividade.getTipoAtividade() == null) // {
			erros.addErro(
					"N�o foi definido o tipo da atividade acad�mica espec�fica selecionada. Por favor, contacte os administradores do sistema.");

	}

	/** Valida os dados da matr�cula do registro em atividade acad�mica espec�fica,
	 * verificando todos os crit�rios definidos no regulamento da gradua��o, bem como
	 * dos outros n�veis de ensino que utilizam o registro de atividades, como a p�s-gradua��o
	 * lato e stricto sensu.
	 * @param usuario
	 * @param matricula 
	 * @param validacao
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarDadosMatricula(Usuario usuario, MatriculaComponente matricula, boolean validacao, OperacaoRegistroAtividade operacao, ListaMensagens erros) throws DAOException, NegocioException {
		boolean validarDocentesEnvolvidos = true;
		if (matricula.getDiscente().isGraduacao()) {
			DiscenteDao discenteDao = getDAO(DiscenteDao.class);
			try {
				DiscenteGraduacao discente = (DiscenteGraduacao) discenteDao.findByPK(matricula.getDiscente().getId());
				validarDocentesEnvolvidos = validarDocentesEnvolvidos(usuario, validacao, discente);
			} finally {
				discenteDao.close();
			}
		}
		else if (matricula.getDiscente().isTecnico()) {
			DiscenteTecnicoDao tecDao = getDAO(DiscenteTecnicoDao.class);
			try {
				DiscenteTecnico discente = (DiscenteTecnico) tecDao.findByPK(matricula.getDiscente().getId());
				validarDocentesEnvolvidos = validarDocentesEnvolvidosTecnico(usuario, validacao, discente);
			}
			finally{
				tecDao.close();
			}
		}

		// validar ano.periodo
		boolean anoPeriodoCompulsorio = usuario.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE);
		validarPeriodo(matricula, anoPeriodoCompulsorio, operacao, erros);

		if (matricula.getDiscente().isGraduacao() || matricula.getDiscente().isTecnico()) {
			// Validar orientador
			if (!matricula.getComponente().isAtividadeComplementar()) {
				if (validarDocentesEnvolvidos
						&& (matricula.getRegistroAtividade()
								.getOrientacoesAtividade() == null || matricula
								.getRegistroAtividade().getOrientacoesAtividade()
								.isEmpty())) {
					erros.addErro("� necess�rio definir um orientador para esta atividade.");
				}
			}
		}

		// Valida��es de est�gio (Art. 62)
		if ( (matricula.getDiscente().isGraduacao() &&
				matricula.getComponente().getTipoAtividade().getId() == TipoAtividade.ESTAGIO) ||
				(matricula.getDiscente().isTecnico() &&
						matricula.getComponente().getTipoAtividade().getId() == TipoAtividade.ESTAGIO_TECNICO)) {

			// Validar coordenador de est�gio
			if (validarDocentesEnvolvidos
					&& (matricula.getRegistroAtividade().getCoordenador() == null || matricula
							.getRegistroAtividade().getCoordenador().getId() <= 0)) {
				erros.addErro("� necess�rio definir um coordenador para este est�gio.");
			}

		} else {
			matricula.getRegistroAtividade().setCoordenador(null);
			matricula.getRegistroAtividade().setSupervisor(null);
		}

		/** se tiver orientadores n�o podem ser servidores inativos. */
		if( !isEmpty( matricula.getRegistroAtividade().getOrientacoesAtividade() ) ){
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario( matricula.getDiscente() );

			for( OrientacaoAtividade oa :  matricula.getRegistroAtividade().getOrientacoesAtividade() ){
				int ano = matricula.getAno() != null ? matricula.getAno() : 0;
				int periodo = matricula.getPeriodo() != null ? matricula.getPeriodo() : 0;
				boolean ativoNoPeriodo = TurmaValidator.validaStatusDocente(oa.getOrientador(), oa.getOrientadorExterno(), ano, periodo, cal);
				if( !ativoNoPeriodo && !isEmpty(oa.getOrientador()) )
					erros.addErro(oa.getOrientador().getSiapeNome() + " n�o pode ser orientador desta atividade pois n�o possui v�nculo ATIVO com a " + RepositorioDadosInstitucionais.getSiglaInstituicao() + ".");
			}
		}
	}

	/**
	 * Verifica se � necess�rio validar os docentes envolvidos no registro da atividade.
	 * @param usuario
	 * @param validacao
	 * @param discente
	 * @return
	 */
	public static boolean validarDocentesEnvolvidos(Usuario usuario,
			boolean validacao, DiscenteGraduacao discente) {
		return !validacao
				|| (validacao
						&& !discente.getMatrizCurricular()
								.isLicenciaturaPlena() && !usuario
						.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE));
	}
	
	/**
	 * Verifica se � necess�rio validar os docentes envolvidos no registro da atividade do n�vel t�cnico.
	 * @param usuario
	 * @param validacao
	 * @param discente
	 * @return
	 */
	public static boolean validarDocentesEnvolvidosTecnico(Usuario usuario,
			boolean validacao, DiscenteTecnico discente) {
		return !validacao || (validacao && !usuario.isUserInRole(SigaaPapeis.GESTOR_TECNICO));
	}

	/**
	 * Verifica se o ano.periodo informado � v�lido. Tem que ser um per�odo
	 * posterior ao ingresso do discente e o discente n�o pode ter afastamento
	 * no per�odo
	 *
	 * @param registro
	 * @param erros
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	public static void validarPeriodo(MatriculaComponente matricula, boolean anoPeriodoCompulsorio, OperacaoRegistroAtividade operacao, ListaMensagens erros) throws DAOException, NegocioException {
		MovimentacaoAlunoDao movdao = DAOFactory.getInstance().getDAO(MovimentacaoAlunoDao.class);
		ParametrosGestoraAcademicaDao pdao = DAOFactory.getInstance().getDAO(ParametrosGestoraAcademicaDao.class);
		
		DiscenteAdapter discente = matricula.getDiscente();
		
		try {
			// caso lato
			if (matricula.getDiscente().isLato() && (matricula.getAnoFim() == null || matricula.getMesFim() == null)) {
				erros.addErro("Informe a data final");
				return;
			}

			// caso gradua��o ou t�cnico
			if ((matricula.getDiscente().isGraduacao() || matricula.getDiscente().isTecnico() || matricula.getDiscente().isStricto()) && (matricula.getAno() == null || matricula.getPeriodo() == null)) {
				erros.addErro("� necess�rio informar o ano e o per�odo do registro da atividade. ");
				return;
			}

			// Se for Lato
			if(matricula.getDiscente().isLato()) {
				int inicio = new Integer(matricula.getAno()*100 + matricula.getMes());
				int fim = new Integer(matricula.getAnoFim()*100 + matricula.getMesFim());
				if (inicio > fim)
					erros.addErro("A Data Final n�o pode ser anterior � Data de In�cio");
				if(matricula.getAno() == null || matricula.getPeriodo() == null)
					erros.addErro("� necess�rio informar o ano e o per�odo do registro da atividade. ");

			// Se for Gradua��o ou T�cnico
			} else if (matricula.getDiscente().isGraduacao() || matricula.getDiscente().isTecnico()) {
				int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() + "" + discente.getPeriodoIngresso());
				int anoPeriodo = new Integer(matricula.getRegistroAtividade().getAno() + "" + matricula.getRegistroAtividade().getPeriodo());
	
				if (!anoPeriodoCompulsorio) {
					// Validar ano-per�odo da atividade com o ano-per�odo de entrada do discente
					if (anoPeriodo < anoPeriodoDiscente)
						erros.addErro(
								"Ano e per�odo inv�lidos: O discente ingressou em "
								+ discente.getAnoPeriodoIngresso()
								+ ". O ano-per�odo informado deve ser posterior ao ingresso do aluno.");
				}

				RegistroAtividadeValidator.validarAfastamentos(matricula, discente, anoPeriodo);
				
			// Se for Stricto
			} else if (matricula.getDiscente().isStricto()) {
				if (operacao.equals(OperacaoRegistroAtividade.MATRICULA)) {
					DiscenteStricto disStricto = (DiscenteStricto) matricula.getDiscente();
					//int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() + ""	+ discente.getPeriodoIngresso());
					//int anoPeriodo = new Integer(matricula.getRegistroAtividade().getAno() + "" + (matricula.getMes() / 7 + 1));
					//agora a valida��o do periodo da matr�cula considera o mes de ingresso do discente
					int anoMesDiscente = new Integer( discente.getAnoIngresso() + "" + UFRNUtils.completaZeros( disStricto.getMesEntrada(), 2 ) );
					int anoMes = new Integer(matricula.getRegistroAtividade().getAno() + "" + UFRNUtils.completaZeros( matricula.getMes(), 2 ) );
					
					if (anoMes < anoMesDiscente)
						erros.addErro("Data de in�cio inv�lida: O discente ingressou em "
								+ CalendarUtils.getNomeMes( disStricto.getMesEntrada() ) + " de " + discente.getAnoIngresso()
								//+ discente.getAnoPeriodoIngresso()
								+ ". O ano-m�s informado devem ser posterior ao do ingresso do aluno.");
					}
			}

		} finally {
			pdao.close();
			movdao.close();
		}
	}

	/** Valida os dados da consolida��o da matr�cula.
	 * @param matricula
	 * @param usuario
	 * @param validacao
	 * @param erros
	 * @throws DAOException 
	 */
	public static void validarDadosConsolidacao(MatriculaComponente matricula, Usuario usuario, boolean validacao,
			ListaMensagens erros) throws DAOException {

		DiscenteAdapter discente = matricula.getDiscente();

		boolean validarDocentesEnvolvidos = false;
		if (discente.isGraduacao()) {
			DiscenteGraduacao grad = (DiscenteGraduacao) getDAO(DiscenteDao.class).findByPK(matricula.getDiscente().getId());
			validarDocentesEnvolvidos = validarDocentesEnvolvidos(usuario, validacao, grad);
		}
		
		if (discente.isTecnico()) {
			DiscenteTecnicoDao tecDao = getDAO(DiscenteTecnicoDao.class);
			try {
				DiscenteTecnico tec = (DiscenteTecnico) tecDao.findByPK(matricula.getDiscente().getId());
				validarDocentesEnvolvidos = validarDocentesEnvolvidosTecnico(usuario, validacao, tec);
			} catch (DAOException e) {
			}
			finally{
				tecDao.close();
			}
		}
		
		// validar datas
		if (discente.isStricto()) {
			if (matricula.getAnoFim() < matricula.getAno() ||
					(matricula.getAno().intValue() == matricula.getAnoFim() && matricula.getMesFim() < matricula.getMes())) {
				erros.addErro("Data Final inv�lida");
			}
		}

		// Validar o resultado da consolida��o/valida��o
		if (matricula.getComponente().isNecessitaMediaFinal() 
				&& !SituacaoMatricula.APROVEITADO_DISPENSADO.equals(matricula.getSituacaoMatricula())) {

			if( isEmpty(matricula.getConceito()) ){
				if( matricula.isMetodoConceito() ){
					erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Conceito");
				}else{
					validateRequired(matricula.getMediaFinal(), "Nota Final", erros);
					if( isEmpty(erros) )
						validateRange(matricula.getMediaFinal(), 0.0, 10.0, "Nota Final", erros);
				}	
			}

		} else if (!matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVADO)
				&& !matricula.getSituacaoMatricula().equals(SituacaoMatricula.REPROVADO)) {

			// se for igual a dispensado
			if (!(matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO) && usuario
					.isUserInRole(SigaaPapeis.ADMINISTRADOR_DAE))) {

				if (validarDocentesEnvolvidos) {
					erros.addErro("A situa��es v�lidas para a consolida��o de atividades s�o APROVADO ou REPROVADO");
				} else if (!matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO)) {
					erros.addErro("A situa��es v�lidas para a consolida��o de atividades s�o APROVADO, REPROVADO ou DISPENSADO");
				}
			}
		}

	}

	/**
	 * Validar a renova��o de matr�cula de atividades.
	 * (V�lido somente para p�s-gradua��o)
	 * 
	 * @param matricula
	 * @param erros
	 * @throws DAOException
	 */
	public static void validarRenovacao(MatriculaComponente matricula, ListaMensagens erros) throws DAOException {
		DiscenteAdapter discente = matricula.getDiscente();
		ComponenteCurricular atividade = matricula.getComponente();
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente); 

		// Validar n�vel do discente
		if ( !discente.isStricto() && !discente.isResidencia() ) {
			erros.addErro("Somente � permitida a renova��o de matr�cula em atividades para discentes de p�s-gradua��o. ");
			return;
		}
		
		// Validar se a matr�cula � do semestre corrente
		if (matricula.getAnoPeriodo().equals(calendario.getAnoPeriodo())) {
			erros.addErro("Somente � poss�vel renovar atividades matriculadas em per�odos anteriores ao atual.");
		}
		
		// Validar se j� existe uma renova��o para o ano/per�odo corrente
		RenovacaoAtividadePosDao renovacaoDao = getDAO(RenovacaoAtividadePosDao.class);
		try {
			RenovacaoAtividadePos renovacao = renovacaoDao.findByMatricula( matricula, calendario.getAno(), calendario.getPeriodo() ) ;
			
			if (renovacao != null) {
				erros.addErro("N�o � poss�vel renovar a atividade selecionada pois a mesma j� foi renovada no semestre corrente.");
				return;
			}
		} finally {
			renovacaoDao.close();
		}
		
		// Validar de acordo com o tipo de atividade
		if (matricula.getComponente().isProficiencia()) {
			erros.addErro("N�o � poss�vel renovar atividades de exame de profici�ncia.");
		}
		
		if ( matricula.getComponente().isQualificacao() ) {
			MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente, atividade, erros);
		}
		
		if ( matricula.getComponente().isTese() ) {
			MatriculaStrictoValidator.validarRenovacaoDefesa(discente, atividade, erros);
		}
		
	}

	/**
	 * S� valida a carga hor�ria dedicada do docente na atividade na gradua��o
	 * 
	 * @param chDedicada
	 * @param matricula
	 * @throws NegocioException
	 */
	public static void validarCHDedicadaDocente(int chDedicada, MatriculaComponente matricula, ListaMensagens erros) throws NegocioException {
		if( matricula.getComponente().isGraduacao() || matricula.getComponente().isTecnico()){
			if (matricula.getComponente().isEstagio() || matricula.getComponente().isAtividadeColetiva()) {
				validateRequired(chDedicada, "Carga hor�ria dedicada", erros);
				validateRange(chDedicada, 1, matricula.getComponente().getChTotal(), "Carga hor�ria dedicada", erros);
			}
		}
	}
	
	/** Retorna uma inst�ncia de um DAO 
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Validar se a atividade pertence ou � equivalente a uma atividade do curr�culo do discente. 
	 * 
	 * @param discente
	 * @param atividade
	 * @throws ArqException 
	 */
	public static void validarCurriculoEquivalencia(DiscenteAdapter discente,
			ComponenteCurricular atividade, ListaMensagens lista) throws ArqException {
		
		Map<Integer, String> mapa = getDAO(DiscenteGraduacaoDao.class).findMapaComponentesEquivalenciaDoCurriculo(discente.getCurriculo().getId());
		if(!mapa.containsKey(atividade.getId())){
			TreeSet<Integer> componentes = new TreeSet<Integer>();
			componentes.add(atividade.getId());
			for(String expressao: mapa.values()){ 
				if(ExpressaoUtil.eval(expressao, componentes))
					return;
			}
			lista.addErro("A atividade selecionada n�o pertence e tamb�m n�o � equivalente a nenhum componente do curr�culo do aluno.");
		}
	}

	/**
	 * Valida se o discente possui movimenta��oes de afastamento no periodo informado
	 * 
	 * @param registro
	 * @param discente
	 * @param anoPeriodoRegistro
	 * @throws NegocioException
	 * @throws DAOException
	 */
	public static void validarAfastamentos(MatriculaComponente matricula, DiscenteAdapter discente, int anoPeriodoRegistro) throws NegocioException, DAOException {

		MovimentacaoAlunoDao dao = null;

		try {

			dao = getDAO(MovimentacaoAlunoDao.class);

			//Valida��o se o discente possui afastamento no per�odo da matr�cula
			Collection<MovimentacaoAluno> saidas = dao.findAfastamentosByDiscente(discente.getId());		 

			for (MovimentacaoAluno movim : saidas) {

				if (discente.isGraduacao()) {
					// Se o discente possuir tipoRetorno n�o deve validar o afastamento permanente
					// � poss�vel que o aluno tenha um registro de sa�da permanente, mas fica ativo no sistema.
					// Isso acontece caso o aluno tenha retornado a institui��o por meios judiciais, administrativos, etc.
					int anoPeriodoSaida = new Integer(movim.getAnoReferencia() + "" + movim.getPeriodoReferencia());
					
					if (anoPeriodoRegistro >= anoPeriodoSaida && movim.isAfastamentoPermanenteSemRetorno() )
						throw new NegocioException("O Aluno possui movimenta��o de afastamento permanente. Afastamento em " + movim.getAnoPeriodoReferencia() + " por " + movim.getTipoMovimentacaoAluno().getDescricao());
					
					if (anoPeriodoRegistro == anoPeriodoSaida && movim.getTipoMovimentacaoAluno().isTemporario())
						throw new NegocioException("Ano e per�odo inv�lidos: o discente possui um afastamento no per�odo " + movim.getAnoPeriodoReferencia() + " por " + movim.getTipoMovimentacaoAluno().getDescricao());
				}

				if(discente.isStricto()){
					Date inicioAfastamento = movim.getInicioAfastamento();
					if (inicioAfastamento != null){
						Date fimAfastamento = calculaFinalDoAfastamento(movim.getValorMovimentacao(), inicioAfastamento);
						Date inicioRegistro = popularData(matricula.getMes(), matricula.getAno().intValue(), true);
						Date fimRegistro = popularData(matricula.getMesFim(), matricula.getAnoFim(), false);
						if(CalendarUtils.isIntervalosDeDatasConflitantes(inicioAfastamento, fimAfastamento, inicioRegistro, fimRegistro)){
							Formatador f = Formatador.getInstance();
							throw new NegocioException("N�o � poss�vel realizar o registro em atividade pois o discente possui afastamento " +
									"no per�odo de "+f.formatarData(inicioAfastamento)+" a "+f.formatarData(fimAfastamento));
						}						
					}
				}
			}
		} finally {
			if (dao != null) 
				dao.close();
		}
	}
	
	/**
	 * Verifica se � permitido ao discente passado se cadastrar em atividades acad�micas espec�ficas.
	 * Caso n�o seja permitido o cadastro, adiciona na lista de mensagens o erro correspondente.
	 * 
	 * @param discente
	 * @return
	 */
	public static boolean isPermiteRegistrarAtividade( DiscenteAdapter discente, ListaMensagens erros ) {
		boolean naoPermite =  discente.getTipo() == Discente.ESPECIAL && 
				!discente.getFormaIngresso().isAlunoEspecial() && 
				!discente.getFormaIngresso().isMobilidadeEstudantil() && 
				!discente.getFormaIngresso().isEstudosComplementares();
		
		if(naoPermite)
			erros.addErro("N�o � poss�vel realizar matr�culas em Atividades Acad�micas Espec�ficas " +
					"para esse discente<br>" +
					"Apenas discentes do tipo REGULAR e ESPECIAL (com forma de ingresso 'ALUNO ESPECIAL', 'MOBILIDADE ESTUDANTIL' ou 'ESTUDOS COMPLEMENTARES') " +
					"podem ser matriculados");
		
		return !naoPermite;
	}

	/**
	 * Faz o c�lculo do retorno do aluno
	 * 
	 * @param qtdMeses
	 * @param inicioAfastamento
	 * @return
	 */
	private static Date calculaFinalDoAfastamento(int qtdMeses, Date inicioAfastamento) {
		Date fimAfastamento = CalendarUtils.adicionaMeses(inicioAfastamento, qtdMeses-1);
		int mes = CalendarUtils.getMesByData(fimAfastamento);
		int ano = CalendarUtils.getAno(fimAfastamento);
		
		
		return CalendarUtils.getMaximoDia(mes+1, ano);
	}
	
	
	 /**
	  * Retorna uma data a partir do m�s e ano informados, no in�cio ou final do m�s, de acordo com o �ltimo par�metro. 
	  * @param mes
	  * @param ano
	  * @param inicio
	  * @return
	  */
	 private static Date popularData(Integer mes, Integer ano, boolean inicio) {
		 if(mes == null || ano == null)
			 return null;
		 return inicio ? CalendarUtils.createDate(1, mes-1, ano) : CalendarUtils.getMaximoDia(mes-1, ano);
	 }
	 
	 /**
	  * Verifica se a atividade � o �nica componente ativo do discente, se for n�o permite sua exclus�o.
	  * @param matricula
	  * @param discente
	 * @throws DAOException 
	  */
	 public static void validaUnicaMatricula( MatriculaComponente matricula, DiscenteAdapter discente, boolean msgBloqueante, ListaMensagens erros ) throws DAOException{
		 
		// Contar o total de matr�culas pendentes de consolida��o para o per�odo anterior
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		int totalMatriculas = matriculaDao.countMatriculasByDiscente(discente.getDiscente(),
				matricula.getAno(), matricula.getPeriodo(),
				SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, SituacaoMatricula.APROVADO);
		
		if (totalMatriculas <= 1){
			if (msgBloqueante)
				erros.addErro("Caro usu�rio, n�o ser� poss�vel realizar a exclus�o da matr�cula selecionada, " +
					"pois deixar� o discente sem nenhum v�nculo com a institui��o.");
			else
				erros.addErro("Caro usu�rio, a exclus�o da matr�cula selecionada " +
					" deixar� o discente sem nenhum v�nculo com a institui��o.");
			return;									
		}
	}
	 
}
