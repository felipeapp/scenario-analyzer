/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Validações específicas para o registro de atividades acadêmicas.
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
					"De acordo com o Art. 237, é vedada a validação de trabalhos de conclusão de curso." +
					"(Regulamento dos Cursos Regulares de Graduação, Resolução N° 227/2009-CONSEPE, de 3 de dezembro de 2009).");
		}
	}

	/**
	 * Verifica se o discente já possui uma matrícula na atividade selecionada
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
							"Não é possível registrar a atividade "
									+ atividade.getNome()
									+ ", pois o aluno já está matriculado ou o registro já foi integralizado.");
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
					"O componente curricular selecionado não é uma atividade acadêmcia específica");
		} else if (atividade.getTipoAtividade() == null) // {
			erros.addErro(
					"Não foi definido o tipo da atividade acadêmica específica selecionada. Por favor, contacte os administradores do sistema.");

	}

	/** Valida os dados da matrícula do registro em atividade acadêmica específica,
	 * verificando todos os critérios definidos no regulamento da graduação, bem como
	 * dos outros níveis de ensino que utilizam o registro de atividades, como a pós-graduação
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
					erros.addErro("É necessário definir um orientador para esta atividade.");
				}
			}
		}

		// Validações de estágio (Art. 62)
		if ( (matricula.getDiscente().isGraduacao() &&
				matricula.getComponente().getTipoAtividade().getId() == TipoAtividade.ESTAGIO) ||
				(matricula.getDiscente().isTecnico() &&
						matricula.getComponente().getTipoAtividade().getId() == TipoAtividade.ESTAGIO_TECNICO)) {

			// Validar coordenador de estágio
			if (validarDocentesEnvolvidos
					&& (matricula.getRegistroAtividade().getCoordenador() == null || matricula
							.getRegistroAtividade().getCoordenador().getId() <= 0)) {
				erros.addErro("É necessário definir um coordenador para este estágio.");
			}

		} else {
			matricula.getRegistroAtividade().setCoordenador(null);
			matricula.getRegistroAtividade().setSupervisor(null);
		}

		/** se tiver orientadores não podem ser servidores inativos. */
		if( !isEmpty( matricula.getRegistroAtividade().getOrientacoesAtividade() ) ){
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario( matricula.getDiscente() );

			for( OrientacaoAtividade oa :  matricula.getRegistroAtividade().getOrientacoesAtividade() ){
				int ano = matricula.getAno() != null ? matricula.getAno() : 0;
				int periodo = matricula.getPeriodo() != null ? matricula.getPeriodo() : 0;
				boolean ativoNoPeriodo = TurmaValidator.validaStatusDocente(oa.getOrientador(), oa.getOrientadorExterno(), ano, periodo, cal);
				if( !ativoNoPeriodo && !isEmpty(oa.getOrientador()) )
					erros.addErro(oa.getOrientador().getSiapeNome() + " não pode ser orientador desta atividade pois não possui vínculo ATIVO com a " + RepositorioDadosInstitucionais.getSiglaInstituicao() + ".");
			}
		}
	}

	/**
	 * Verifica se é necessário validar os docentes envolvidos no registro da atividade.
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
	 * Verifica se é necessário validar os docentes envolvidos no registro da atividade do nível técnico.
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
	 * Verifica se o ano.periodo informado é válido. Tem que ser um período
	 * posterior ao ingresso do discente e o discente não pode ter afastamento
	 * no período
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

			// caso graduação ou técnico
			if ((matricula.getDiscente().isGraduacao() || matricula.getDiscente().isTecnico() || matricula.getDiscente().isStricto()) && (matricula.getAno() == null || matricula.getPeriodo() == null)) {
				erros.addErro("É necessário informar o ano e o período do registro da atividade. ");
				return;
			}

			// Se for Lato
			if(matricula.getDiscente().isLato()) {
				int inicio = new Integer(matricula.getAno()*100 + matricula.getMes());
				int fim = new Integer(matricula.getAnoFim()*100 + matricula.getMesFim());
				if (inicio > fim)
					erros.addErro("A Data Final não pode ser anterior à Data de Início");
				if(matricula.getAno() == null || matricula.getPeriodo() == null)
					erros.addErro("É necessário informar o ano e o período do registro da atividade. ");

			// Se for Graduação ou Técnico
			} else if (matricula.getDiscente().isGraduacao() || matricula.getDiscente().isTecnico()) {
				int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() + "" + discente.getPeriodoIngresso());
				int anoPeriodo = new Integer(matricula.getRegistroAtividade().getAno() + "" + matricula.getRegistroAtividade().getPeriodo());
	
				if (!anoPeriodoCompulsorio) {
					// Validar ano-período da atividade com o ano-período de entrada do discente
					if (anoPeriodo < anoPeriodoDiscente)
						erros.addErro(
								"Ano e período inválidos: O discente ingressou em "
								+ discente.getAnoPeriodoIngresso()
								+ ". O ano-período informado deve ser posterior ao ingresso do aluno.");
				}

				RegistroAtividadeValidator.validarAfastamentos(matricula, discente, anoPeriodo);
				
			// Se for Stricto
			} else if (matricula.getDiscente().isStricto()) {
				if (operacao.equals(OperacaoRegistroAtividade.MATRICULA)) {
					DiscenteStricto disStricto = (DiscenteStricto) matricula.getDiscente();
					//int anoPeriodoDiscente = new Integer(discente.getAnoIngresso() + ""	+ discente.getPeriodoIngresso());
					//int anoPeriodo = new Integer(matricula.getRegistroAtividade().getAno() + "" + (matricula.getMes() / 7 + 1));
					//agora a validação do periodo da matrícula considera o mes de ingresso do discente
					int anoMesDiscente = new Integer( discente.getAnoIngresso() + "" + UFRNUtils.completaZeros( disStricto.getMesEntrada(), 2 ) );
					int anoMes = new Integer(matricula.getRegistroAtividade().getAno() + "" + UFRNUtils.completaZeros( matricula.getMes(), 2 ) );
					
					if (anoMes < anoMesDiscente)
						erros.addErro("Data de início inválida: O discente ingressou em "
								+ CalendarUtils.getNomeMes( disStricto.getMesEntrada() ) + " de " + discente.getAnoIngresso()
								//+ discente.getAnoPeriodoIngresso()
								+ ". O ano-mês informado devem ser posterior ao do ingresso do aluno.");
					}
			}

		} finally {
			pdao.close();
			movdao.close();
		}
	}

	/** Valida os dados da consolidação da matrícula.
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
				erros.addErro("Data Final inválida");
			}
		}

		// Validar o resultado da consolidação/validação
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
					erros.addErro("A situações válidas para a consolidação de atividades são APROVADO ou REPROVADO");
				} else if (!matricula.getSituacaoMatricula().equals(SituacaoMatricula.APROVEITADO_DISPENSADO)) {
					erros.addErro("A situações válidas para a consolidação de atividades são APROVADO, REPROVADO ou DISPENSADO");
				}
			}
		}

	}

	/**
	 * Validar a renovação de matrícula de atividades.
	 * (Válido somente para pós-graduação)
	 * 
	 * @param matricula
	 * @param erros
	 * @throws DAOException
	 */
	public static void validarRenovacao(MatriculaComponente matricula, ListaMensagens erros) throws DAOException {
		DiscenteAdapter discente = matricula.getDiscente();
		ComponenteCurricular atividade = matricula.getComponente();
		CalendarioAcademico calendario = CalendarioAcademicoHelper.getCalendario(discente); 

		// Validar nível do discente
		if ( !discente.isStricto() && !discente.isResidencia() ) {
			erros.addErro("Somente é permitida a renovação de matrícula em atividades para discentes de pós-graduação. ");
			return;
		}
		
		// Validar se a matrícula é do semestre corrente
		if (matricula.getAnoPeriodo().equals(calendario.getAnoPeriodo())) {
			erros.addErro("Somente é possível renovar atividades matriculadas em períodos anteriores ao atual.");
		}
		
		// Validar se já existe uma renovação para o ano/período corrente
		RenovacaoAtividadePosDao renovacaoDao = getDAO(RenovacaoAtividadePosDao.class);
		try {
			RenovacaoAtividadePos renovacao = renovacaoDao.findByMatricula( matricula, calendario.getAno(), calendario.getPeriodo() ) ;
			
			if (renovacao != null) {
				erros.addErro("Não é possível renovar a atividade selecionada pois a mesma já foi renovada no semestre corrente.");
				return;
			}
		} finally {
			renovacaoDao.close();
		}
		
		// Validar de acordo com o tipo de atividade
		if (matricula.getComponente().isProficiencia()) {
			erros.addErro("Não é possível renovar atividades de exame de proficiência.");
		}
		
		if ( matricula.getComponente().isQualificacao() ) {
			MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente, atividade, erros);
		}
		
		if ( matricula.getComponente().isTese() ) {
			MatriculaStrictoValidator.validarRenovacaoDefesa(discente, atividade, erros);
		}
		
	}

	/**
	 * Só valida a carga horária dedicada do docente na atividade na graduação
	 * 
	 * @param chDedicada
	 * @param matricula
	 * @throws NegocioException
	 */
	public static void validarCHDedicadaDocente(int chDedicada, MatriculaComponente matricula, ListaMensagens erros) throws NegocioException {
		if( matricula.getComponente().isGraduacao() || matricula.getComponente().isTecnico()){
			if (matricula.getComponente().isEstagio() || matricula.getComponente().isAtividadeColetiva()) {
				validateRequired(chDedicada, "Carga horária dedicada", erros);
				validateRange(chDedicada, 1, matricula.getComponente().getChTotal(), "Carga horária dedicada", erros);
			}
		}
	}
	
	/** Retorna uma instância de um DAO 
	 * @param <T>
	 * @param dao
	 * @return
	 * @throws DAOException
	 */
	private static <T extends GenericDAO> T getDAO(Class<T> dao) throws DAOException {
		return DAOFactory.getInstance().getDAO(dao, null, null);
	}

	/**
	 * Validar se a atividade pertence ou é equivalente a uma atividade do currículo do discente. 
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
			lista.addErro("A atividade selecionada não pertence e também não é equivalente a nenhum componente do currículo do aluno.");
		}
	}

	/**
	 * Valida se o discente possui movimentaçãoes de afastamento no periodo informado
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

			//Validação se o discente possui afastamento no período da matrícula
			Collection<MovimentacaoAluno> saidas = dao.findAfastamentosByDiscente(discente.getId());		 

			for (MovimentacaoAluno movim : saidas) {

				if (discente.isGraduacao()) {
					// Se o discente possuir tipoRetorno não deve validar o afastamento permanente
					// É possível que o aluno tenha um registro de saída permanente, mas fica ativo no sistema.
					// Isso acontece caso o aluno tenha retornado a instituição por meios judiciais, administrativos, etc.
					int anoPeriodoSaida = new Integer(movim.getAnoReferencia() + "" + movim.getPeriodoReferencia());
					
					if (anoPeriodoRegistro >= anoPeriodoSaida && movim.isAfastamentoPermanenteSemRetorno() )
						throw new NegocioException("O Aluno possui movimentação de afastamento permanente. Afastamento em " + movim.getAnoPeriodoReferencia() + " por " + movim.getTipoMovimentacaoAluno().getDescricao());
					
					if (anoPeriodoRegistro == anoPeriodoSaida && movim.getTipoMovimentacaoAluno().isTemporario())
						throw new NegocioException("Ano e período inválidos: o discente possui um afastamento no período " + movim.getAnoPeriodoReferencia() + " por " + movim.getTipoMovimentacaoAluno().getDescricao());
				}

				if(discente.isStricto()){
					Date inicioAfastamento = movim.getInicioAfastamento();
					if (inicioAfastamento != null){
						Date fimAfastamento = calculaFinalDoAfastamento(movim.getValorMovimentacao(), inicioAfastamento);
						Date inicioRegistro = popularData(matricula.getMes(), matricula.getAno().intValue(), true);
						Date fimRegistro = popularData(matricula.getMesFim(), matricula.getAnoFim(), false);
						if(CalendarUtils.isIntervalosDeDatasConflitantes(inicioAfastamento, fimAfastamento, inicioRegistro, fimRegistro)){
							Formatador f = Formatador.getInstance();
							throw new NegocioException("Não é possível realizar o registro em atividade pois o discente possui afastamento " +
									"no período de "+f.formatarData(inicioAfastamento)+" a "+f.formatarData(fimAfastamento));
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
	 * Verifica se é permitido ao discente passado se cadastrar em atividades acadêmicas específicas.
	 * Caso não seja permitido o cadastro, adiciona na lista de mensagens o erro correspondente.
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
			erros.addErro("Não é possível realizar matrículas em Atividades Acadêmicas Específicas " +
					"para esse discente<br>" +
					"Apenas discentes do tipo REGULAR e ESPECIAL (com forma de ingresso 'ALUNO ESPECIAL', 'MOBILIDADE ESTUDANTIL' ou 'ESTUDOS COMPLEMENTARES') " +
					"podem ser matriculados");
		
		return !naoPermite;
	}

	/**
	 * Faz o cálculo do retorno do aluno
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
	  * Retorna uma data a partir do mês e ano informados, no início ou final do mês, de acordo com o último parâmetro. 
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
	  * Verifica se a atividade é o única componente ativo do discente, se for não permite sua exclusão.
	  * @param matricula
	  * @param discente
	 * @throws DAOException 
	  */
	 public static void validaUnicaMatricula( MatriculaComponente matricula, DiscenteAdapter discente, boolean msgBloqueante, ListaMensagens erros ) throws DAOException{
		 
		// Contar o total de matrículas pendentes de consolidação para o período anterior
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		int totalMatriculas = matriculaDao.countMatriculasByDiscente(discente.getDiscente(),
				matricula.getAno(), matricula.getPeriodo(),
				SituacaoMatricula.MATRICULADO, SituacaoMatricula.EM_ESPERA, SituacaoMatricula.APROVADO);
		
		if (totalMatriculas <= 1){
			if (msgBloqueante)
				erros.addErro("Caro usuário, não será possível realizar a exclusão da matrícula selecionada, " +
					"pois deixará o discente sem nenhum vínculo com a instituição.");
			else
				erros.addErro("Caro usuário, a exclusão da matrícula selecionada " +
					" deixará o discente sem nenhum vínculo com a instituição.");
			return;									
		}
	}
	 
}
