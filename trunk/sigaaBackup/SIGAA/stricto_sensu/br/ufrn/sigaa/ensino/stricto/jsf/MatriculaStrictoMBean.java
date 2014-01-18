 /* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;


import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.MatriculaStrictoDao;
import br.ufrn.sigaa.arq.dao.graduacao.OrientacaoAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.util.VerificaSituacaoUsuarioBibliotecaUtil;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;
import br.ufrn.sigaa.ensino.graduacao.dominio.RestricoesMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.SugestaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.AlteracaoDadosDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.MatriculaGraduacaoMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoSolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.MatriculaStrictoValidator;

/**
 * Controlador responsável pela operação de matrícula online de discentes de pós-graduação
 * em turmas ou atividades (proficiência, qualificações e teses).
 *
 * @author Ricardo Wendell
 *
 */
@Component("matriculaStrictoBean") @Scope("session")
public class MatriculaStrictoMBean extends SigaaAbstractController<SolicitacaoMatricula> implements OperadorDiscente {

	/** Discente que irá realizar a matrícula */
	private DiscenteStricto discente;

	/** MBean de MatriculaGraduacao para auxiliar na matrícula em disciplinas */
	private MatriculaGraduacaoMBean matriculaGraduacaoBean;

	/** Calendário que será utilizado para a matrícula */
	private CalendarioAcademico calendarioMatricula;

	/** Sugestões de matrícula em DISCIPLINAS */
	private Collection<SugestaoMatricula> sugestoesMatricula;
	
	/** Para interagir nas páginas com as sugestões de matrícula */
	private DataModel sugestoesDataModel;

	/** ATIVIDADES disponíveis para matrícula */
	private Collection<SolicitacaoMatricula> atividades;

	/** Lista de solicitações de matrícula existentes para o período */
	private Collection<SolicitacaoMatricula> solicitacoesPeriodo;

	/** Lista de matrículas em componentes que o discente possui */
	private Collection<MatriculaComponente> matriculasDiscente;

	/** Tipo de atividade utilizado pelo fluxo corrente de matrícula (auxilia em alguns métodos) */
	private int tipoAtividade;

	/** Verifica se o discente pode se matricular em uma banca de defesa */
	private boolean passivelMatriculaDefesa;
	
	/** Verifica se o discente pode se matricular em uma banca de qualificação */
	private boolean passivelMatriculaQualificacao;
	
	/** Verifica se o discente pode se matricular em disciplinas */
	private boolean passivelMatriculaDisciplina;
	
	public MatriculaStrictoMBean() {
		clear();
	}

	/**
	 * Limpar dados do MBean para iniciar uma nova operação
	 */
	private void clear() {
		obj = new SolicitacaoMatricula();
		obj.setAtividade(new ComponenteCurricular());
		passivelMatriculaQualificacao = false;
		passivelMatriculaDefesa = false;
		passivelMatriculaDisciplina = false;
		calendarioMatricula = null;
	}

	/**
	 * Início da operação de matrícula para discentes de stricto sensu
	 *
	 * Chamado por:
	 * sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp
	 * sigaa.war/portais/discente/menu_discente.jsp
	 * sigaa.war/stricto/matricula/selecao_atividade.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		clear();

		// Buscar discente do usuário e validar seu nível
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		if (discente == null || !discente.isStricto()) {
			addMensagemErro("Esta operação é reservada a discentes de pós-graduação");
			return null;
		} else {
			this.discente = (DiscenteStricto) discente;
		}
		calendarioMatricula = CalendarioAcademicoHelper.getCalendario( this.discente );

		/* Regras de obrigatoriedade: <br/>
		 * RESOLUÇÃO No 028/2010-CONSAD, de 16 de setembro de 2010
		 * Art. 3o
		 * I - tratando-se de discente
		 * a) imposibilidade de se efetuar matricula em disciplina.
		 */
		try {
			VerificaSituacaoUsuarioBibliotecaUtil.verificaUsuarioPossuiInrregularidadeAdministrativa(discente.getPessoa().getId());
		} catch (NegocioException ne) {
			addMensagemErro(ne.getMessage());
			return null;
		}
		
		// Validar nível do discente
		if ( !MatriculaStrictoValidator.isPassivelSolicitacaoMatricula(discente) ) {
			addMensagemErro("Apenas alunos regulares ativos podem realizar solicitações de matrículas.");
			return null;
		}

		// Validar necessidade de atualização de dados pessoais
		if ( MatriculaStrictoValidator.isNecessariaAtualizacaoDadosDiscente(discente, getParametrosAcademicos(), getCalendarioMatricula()) ) {
			addMensagemWarning("ATENÇÃO: antes de realizar a matrícula é necessário atualizar seus dados pessoais.");
			AlteracaoDadosDiscenteMBean bean = getMBean("alteracaoDadosDiscente");
			return bean.iniciarAcessoDiscente();
		}
		
		prepareMovimento(SigaaListaComando.SOLICITACAO_MATRICULA);
		setOperacaoAtiva(SigaaListaComando.SOLICITACAO_MATRICULA.getId());
		
		return selecionaDiscente();
	}

	/**
	 * Não é chamado por JSPs
	 * 
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		// Popular orientador
		OrientacaoAcademicaDao orientacaoDao = getDAO(OrientacaoAcademicaDao.class);
		this.discente.setOrientacao( orientacaoDao.findOrientadorAtivoByDiscente(discente.getId()) );

		CalendarioAcademico calDiscente = CalendarioAcademicoHelper.getCalendario( this.discente );
		
		// Buscar exames de proficiência, qualificações e defesas já solicitados pelo discente
		popularSolicitacoesPeriodo(calDiscente);

		// Buscar as matrículas realizadas pelo discente
		popularMatriculasDiscente();

		passivelMatriculaDisciplina = MatriculaStrictoValidator.isPeriodoSolicitacaoMatricula(calDiscente);
		passivelMatriculaQualificacao = MatriculaStrictoValidator.isPassivelSolicitacaoQualificacao(discente);
		passivelMatriculaDefesa = MatriculaStrictoValidator.isPassivelSolicitacaoDefesa(discente);
		calendarioMatricula = calDiscente;
		return telaOpcoesMatricula();
	}

	/**
	 * Recupera as matrículos do discente
	 * 
	 * @throws DAOException
	 */
	private void popularMatriculasDiscente() throws DAOException {
		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);
		matriculasDiscente = matriculaDao.findByDiscente(discente, SituacaoMatricula.getSituacoesPagasEMatriculadas());
	}

	/**
	 * Recupera as solicitações ativos do discente
	 * 
	 * @throws DAOException
	 */
	private void popularSolicitacoesPeriodo() throws DAOException {
		popularSolicitacoesPeriodo(calendarioMatricula);
	}

	/**
	 * Recupera as solicitações ativos do discente
	 * 
	 * @throws DAOException
	 */
	private void popularSolicitacoesPeriodo(CalendarioAcademico cal) throws DAOException {
		SolicitacaoMatriculaDao solicitacaoDao = getDAO(SolicitacaoMatriculaDao.class);
		solicitacoesPeriodo = solicitacaoDao.findSolicitacoesAtividadeByDiscente(discente, cal.getAno(), cal.getPeriodo());
	}

	
	/**
	 * Redireciona para a tela com as opções de matrícula para o discente
	 *
	 * @return
	 */
	private String telaOpcoesMatricula() {
		return forward("/stricto/matricula/opcoes.jsf");
	}

	/**
	 * Inicia a matrícula do discente em turmas de disciplinas
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaDisciplinas() throws ArqException {
		
		// Validar período de matrícula
		calendarioMatricula = CalendarioAcademicoHelper.getCalendario( this.discente );
		if ( !MatriculaStrictoValidator.isPeriodoSolicitacaoMatricula(calendarioMatricula) ) {
			addMensagemErro("Não é permitido solicitar matrículas fora do período oficial definido " +
					"pelo calendário do seu programa de pós-graduação.");
			return null;
		}
		
		// Inicializar MBean de Matrículas de Graduação
		inicializarBeanMatriculaGraduacao();

		// Redirecionar para a tela adequada
		if ( matriculaGraduacaoBean.getTurmas().isEmpty() ) {
			return listarSugestoesMatricula();
		} else {
			return matriculaGraduacaoBean.telaSelecaoTurmas(false);
		}
	}

	/**
	 * Prepara {@link MatriculaGraduacaoMBean} para matrícula
	 * 
	 * @throws DAOException
	 * @throws ArqException
	 */
	public void inicializarBeanMatriculaGraduacao() throws DAOException, ArqException {
		matriculaGraduacaoBean = getMBean("matriculaGraduacao");
		matriculaGraduacaoBean.clear();
		matriculaGraduacaoBean.setDiscente(discente);
		matriculaGraduacaoBean.setCalendarioParaMatricula(calendarioMatricula);
		matriculaGraduacaoBean.setRematricula(isRematricula());
		
		// Popular sugestões de turma para matrícula
		MatriculaStrictoDao matriculaStrictoDao = getDAO(MatriculaStrictoDao.class);
		if (matriculaGraduacaoBean.getTurmasJaMatriculadas().isEmpty()){
			for (MatriculaComponente mc : matriculasDiscente) {
				if(ValidatorUtil.isNotEmpty(mc.getTurma())) matriculaGraduacaoBean.getTurmasJaMatriculadas().add(mc.getTurma());
			}
		}
			
		sugestoesMatricula = matriculaStrictoDao.findSugestoesMatricula(discente,
				matriculaGraduacaoBean.getTurmasJaMatriculadas(),
				calendarioMatricula.getAno(), calendarioMatricula.getPeriodo());

		// Popular turmas já matriculadas ou cumpridas através de MatriculaGraduacaoMBean
		matriculaGraduacaoBean.definirTipoSolicitacaoMatricula();
		matriculaGraduacaoBean.setRestricoes(RestricoesMatricula.getRestricoesRegular());
		matriculaGraduacaoBean.setOperacaoAtual("Turmas Abertas do Programa");
		matriculaGraduacaoBean.setTurmasCurriculo(sugestoesMatricula);
		matriculaGraduacaoBean.popularComponentesTurmas();
		matriculaGraduacaoBean.escolhaAlunoParaSolicitacao();
	}

	/**
	 * Redirecionar para a página com a lista das turmas abertas para os
	 * componentes pertencentes ao programa do discente
	 *
	 * Chamado por:
	 * /sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp
	 *
	 * @return
	 */
	public String listarSugestoesMatricula() {
		matriculaGraduacaoBean.setOperacaoAtual("Turmas Abertas do Programa");
		return redirect("/stricto/matricula/turmas_programa.jsf");
	}

	/**
	 * Iniciar a matrícula em atividades do tipo proficiência
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaProficiencia() throws ArqException {
		prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		popularSugestaoAtividades(TipoAtividade.PROFICIENCIA);
		return verificarExistenciaSugestoes("de proficiência");
	}
	
	/**
	 * Iniciar a matrícula em atividades complementares
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaComplementares() throws ArqException {
		prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		popularSugestaoAtividades(TipoAtividade.COMPLEMENTAR_STRICTO);
		return verificarExistenciaSugestoes("complementares");
	}

	/**
	 * Verifica se existe sugestões de matrícula para o tipo de atividad selecionado
	 * e define o redirecionamento da tela
	 *
	 * @return
	 */
	private String verificarExistenciaSugestoes(String descricaoTipo) {
		if (ValidatorUtil.isEmpty(sugestoesMatricula) ) {
			addMensagemErro("Não foram encontradas atividades " + descricaoTipo + " cadastradas para o seu currículo.");
			return telaOpcoesMatricula();
		} else {
			return telaSelecaoAtividade();
		}
	}

	/**
	 * Iniciar a solicitação de matrícula em atividade do tipo qualificação
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaQualificacao() throws ArqException {
		
		// Validar orientação
		MatriculaStrictoValidator.validarOrientacaoAtiva(discente, erros);
		
		if (hasErrors())
			return null;
		
		prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		popularSugestaoAtividades(TipoAtividade.QUALIFICACAO);
		return verificarExistenciaSugestoes("de qualificação");
	}

	/**
	 * Iniciar a solicitação de matrícula em atividade do tipo tese (defesa)
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/opcoes.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaDefesa() throws ArqException {
		erros = new ListaMensagens();
		
		MatriculaStrictoValidator.validarOrientacaoAtiva(discente, erros);
		MatriculaStrictoValidator.validarMatriculaDefesa(discente, erros);
		
		if (hasErrors()) {
			return null;
		}
		
		prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		popularSugestaoAtividades(TipoAtividade.TESE);
		return verificarExistenciaSugestoes("de defesa");
	}

	/**
	 * Iniciar a solicitação de matrícula em turmas de outros programas (apenas disciplinas)
	 * 
	 * Não é chamado por JSPs
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarMatriculaTurmasOutrosProgramas() throws ArqException {
		prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		popularSugestaoAtividades(TipoAtividade.TESE);
		return verificarExistenciaSugestoes("de defesa");
	}

	/**
	 * Prepara a lista de sugestões de atividades para matrícula
	 *
	 * @throws DAOException
	 */
	private void popularSugestaoAtividades(int tipoAtividade) throws DAOException {
		this.tipoAtividade = tipoAtividade;

		// Buscar as atividades disponibilizadas para o currículo do discente
		ComponenteCurricularDao dao = getDAO(ComponenteCurricularDao.class);
		Collection<ComponenteCurricular> componentes = dao.findAtividades(NivelEnsino.STRICTO, discente.getCurriculo(), discente.getGestoraAcademica(), null, tipoAtividade);

		// Verificar a situação de cada uma das atividades disponíveis para o discente
		setSugestoesMatricula(new ArrayList<SugestaoMatricula>());
		for (ComponenteCurricular cc : componentes) {
			SugestaoMatricula sugestao = new SugestaoMatricula();
			sugestao.setAtividade(cc);

			// Verificar se o discente já solicitou matrícula para o período atual
			SolicitacaoMatricula solicitacao = verificarSolicitacaoPeriodo(cc);
			if ( solicitacao != null && 
					!ValidatorUtil.isEmpty (solicitacao.getStatus()) && 
					solicitacao.getStatus() != SolicitacaoMatricula.EXCLUIDA) {
				
				sugestao.setTipoInvalido(SugestaoMatricula.JA_SELECIONADO);
				
			}

			// Verificar se o discente já se matriculou neste componente
			for ( MatriculaComponente matricula :  matriculasDiscente ) {
				if ( cc.getId() == matricula.getComponente().getId() ) {
					if (!MatriculaStrictoValidator.isPassivelSolicitacaoNovaMatricula(matricula)) {
						sugestao.setTipoInvalido(SugestaoMatricula.JA_MATRICULADO);
					}
				}
			}

			sugestoesMatricula.add(sugestao);
		}
	}

	/**
	 * Prepara a lista de sugestões de atividades para a matrícula de acordo com o tipo
	 * de atividade definido no momento
	 *
	 * @throws DAOException
	 */
	private void popularSugestaoAtividades() throws DAOException {
		popularSugestaoAtividades(tipoAtividade);
	}

	/**
	 * Verifica se existe uma solicitação cadastrada para o componente curricular informado
	 *
	 * @param cc
	 * @return
	 */
	private SolicitacaoMatricula verificarSolicitacaoPeriodo(ComponenteCurricular cc) {
		for (SolicitacaoMatricula solicitacao : solicitacoesPeriodo) {
			if (solicitacao.getAtividade().getId() == cc.getId()) {
				return solicitacao;
			}
		}
		return null;
	}

	/**
	 * Redireciona para a tela de seleção de atividades
	 *
	 * @return
	 */
	private String telaSelecaoAtividade() {
		return forward("/stricto/matricula/selecao_atividade.jsp");
	}
	
	/**
	 * Redireciona para tela de de seleção de turmas de outros programas
	 * 
	 * Chamado por:
	 * /sigaa.war/graduacao/matricula/cabecalho_botoes_superiores.jsp
	 */
	public String telaTurmasOutrosProgramas(){
		return forward("/stricto/matricula/turmas_outros_programas.jsp");
	}

	/**
	 * Selecionar a atividade, realizar as validações e cadastrar a solicitação
	 *
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/selecao_atividade.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String confirmarSolicitacao() throws ArqException {
		Collection<ComponenteCurricular> atividades = getAtividadesSelecionadas();

		// Validar seleção de atividades
		if (atividades.isEmpty()) {
			addMensagemErro("Não há atividade(s) habilitada(s) ou selecionada(s) para confirmar o cadastro de solicitação.");
			return telaSelecaoAtividade();
		}

		// Preparar movimento de solicitação de matrícula
		MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
		mov.setCalendarioAcademicoAtual(calendarioMatricula);
		mov.setDiscente(discente);
		mov.setAtividades(atividades);
		mov.setCodMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
		mov.setRematricula(isRematricula());

		// Chamar processador
		try {
			execute(mov, getCurrentRequest());
			addMensagemInformation("Solicitação de matrícula submetida com sucesso!");
			addMensagemWarning(" Lembre-se: Sua solicitação somente será efetivada após a análise por seu orientador(a) " +
					"ou pela coordenação de seu programa de pós-graduação!");
			return iniciar();
		} catch (NegocioException e) {
			e.printStackTrace();
			addMensagens(e.getListaMensagens());
			hasErrors();
			return null;
		}
	}

	/**
	 * Verifica se dentre as solicitações de matricula como sugestão
	 * existe alguma que já foi cadastrada, de acordo com o tipo de atividade
	 * selecionado atualmente
	 *
	 * @return
	 */
	public boolean isAlgumaSolicitacaoCadastrada() {
		for (SolicitacaoMatricula solicitacao : solicitacoesPeriodo) {
			if (solicitacao.getId() != 0 && solicitacao.getAtividade().getTipoAtividade().getId() == tipoAtividade && !solicitacao.isNegada()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Popular a lista de atividades selecionadas
	 *
	 * @return
	 */
	private Collection<ComponenteCurricular> getAtividadesSelecionadas() {
		Collection<ComponenteCurricular> selecao = new ArrayList<ComponenteCurricular>();

		// No caso de proficiência ou atividade complementar, é possível realizar uma seleção múltipla
		if ( isProficiencia() || isComplementar() ) {
			for ( SugestaoMatricula sugestao : sugestoesMatricula ) {
				if (sugestao != null && sugestao.isSelected() && ValidatorUtil.isEmpty(sugestao.getTipoInvalido())) {
					selecao.add(sugestao.getAtividade());
				}
			}
		// Para qualificações e defesas é possível selecionar apenas uma (a forma é diferente)
		} else {
			if (obj.getAtividade() != null && obj.getAtividade().getId() != 0 && isAtividadeValida(obj.getAtividade().getId())) {
				selecao.add( obj.getAtividade() );
			}
		}

		return selecao;
	}

	/**
	 * Método responsável por verificar se a atividade a ser solicitada é válida, para tal operação.
	 * @return
	 */
	private boolean isAtividadeValida(Integer idAtividade){
		boolean valida = true;
		for ( SugestaoMatricula sugestao : sugestoesMatricula ) {
			if (sugestao.getAtividade().getId() == idAtividade && ValidatorUtil.isNotEmpty(sugestao.getTipoInvalido()))
				valida = false;	
		}
		return valida;
	}
	
	/**
	 * Retorna uma descrição do status das solicitações para
	 * um determinado tipo de atividade
	 *
	 * @param qualificacao
	 * @return
	 */
	private String getStatusSolicitacao(int tipoAtividade) {
		Collection<SolicitacaoMatricula> solicitacoes = filtrarSolicitacoes(tipoAtividade);
		// Contar as solicitações atendidas ou negadas

		int[] totalSolicitacoesStatus = countStatus(solicitacoes);
		String status = "(";

		if ( solicitacoes.isEmpty() ) {
			status += "nenhuma solicitação cadastrada";
		} else {
			boolean virgula = false;
			if (totalSolicitacoesStatus[0] > 0) {
				status += totalSolicitacoesStatus[0] + " submetida" + (totalSolicitacoesStatus[0] > 1 ? "s" : "");
				virgula = true;
			}
			if (totalSolicitacoesStatus[1] > 0) {
				status += virgula ? ", " : "";
				status += totalSolicitacoesStatus[1] + " atendida" + (totalSolicitacoesStatus[1] > 1 ? "s" : "");
				virgula = true;
			}
			if (totalSolicitacoesStatus[2] > 0) {
				status += virgula ? ", " : "";
				status += totalSolicitacoesStatus[2] + " negada" + (totalSolicitacoesStatus[2] > 1 ? "s" : "");
			}
		}
		status += ")";
		return status;
	}

	/**
	 * Contar o número de solicitações atendidas
	 *
	 * @param solicitacoes
	 * @return
	 */
	private int[] countStatus(Collection<SolicitacaoMatricula> solicitacoes) {
		int[] count = new int[3];
		for (SolicitacaoMatricula solicitacao : solicitacoes) {
			count[0] += !solicitacao.foiAnalisada() ? 1 : 0;
			count[1] += solicitacao.isAtendida() ? 1 : 0;
			count[2] += solicitacao.isNegada() ? 1 : 0;
		}
		return count;
	}

	public String getStatusProficiencias() {
		return getStatusSolicitacao(TipoAtividade.PROFICIENCIA);
	}
	public String getStatusQualificacoes() {
		return getStatusSolicitacao(TipoAtividade.QUALIFICACAO);
	}
	public String getStatusDefesas() {
		return getStatusSolicitacao(TipoAtividade.TESE);
	}

	/**
	 * Filtrar as solicitações do período pelo tipo de atividade
	 *
	 * @param tipoAtividade
	 * @return
	 */
	private Collection<SolicitacaoMatricula> filtrarSolicitacoes(int tipoAtividade) {
		Collection<SolicitacaoMatricula> solicitacoesFiltradas = new ArrayList<SolicitacaoMatricula>();
		
		for (SolicitacaoMatricula solicitacao : solicitacoesPeriodo) {
			if (solicitacao.getAtividade().getTipoAtividade().getId() == tipoAtividade) {
				solicitacoesFiltradas.add(solicitacao);
			}
		}
		
		return solicitacoesFiltradas;
	}

	/**
	 * Cancela uma solicitação de matrícula para uma determinada atividade
	 * 
	 * Chamado por:
	 * /sigaa.war/stricto/matricula/selecao_atividade.jsp
	 *
	 * @return
	 * @throws ArqException
	 */
	public String removerSolicitacao() throws ArqException {
		SugestaoMatricula sugestao = (SugestaoMatricula) sugestoesDataModel.getRowData();
		obj = verificarSolicitacaoPeriodo(sugestao.getAtividade());

		Collection<SolicitacaoMatricula> solicitacoes = new ArrayList<SolicitacaoMatricula>();
		solicitacoes.add(obj);

		// Preparar movimento de cancelamento de solicitação de matrícula
		MovimentoSolicitacaoMatricula mov = new MovimentoSolicitacaoMatricula();
		mov.setCalendarioAcademicoAtual(calendarioMatricula);
		mov.setSolicitacoes(solicitacoes);
		mov.setCodMovimento(SigaaListaComando.CANCELAR_SOLICITAO_MATRICULA_STRICTO);

		// Chamar processador
		try {
			prepareMovimento(SigaaListaComando.CANCELAR_SOLICITAO_MATRICULA_STRICTO);
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage("Solicitação de matrícula cancelada com sucesso!", TipoMensagemUFRN.INFORMATION);

			// Preparar dados e voltar para a tela de seleção de atividades
			clear();
			popularSolicitacoesPeriodo();
			popularSugestaoAtividades();

			prepareMovimento(SigaaListaComando.SOLICITAR_MATRICULA_STRICTO);
			return telaSelecaoAtividade();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			hasErrors();
			return null;
		}
	}

	/**
	 * Verifica se está sendo realizado uma solicitação de matrícula em proficiência
	 *
	 * @return
	 */
	public boolean isProficiencia() {
		return tipoAtividade == TipoAtividade.PROFICIENCIA;
	}
	
	/**
	 * Verifica se está sendo realizado uma solicitação de matrícula em atividade complementar
	 *
	 * @return
	 */
	public boolean isComplementar() {
		return tipoAtividade == TipoAtividade.COMPLEMENTAR_STRICTO;
	}

	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = (DiscenteStricto) discente;
	}

	public DiscenteStricto getDiscente() {
		return this.discente;
	}

	public void setDiscente(DiscenteStricto discente) {
		this.discente = discente;
	}

	public MatriculaGraduacaoMBean getMatriculaGraduacaoBean() {
		return this.matriculaGraduacaoBean;
	}

	public void setMatriculaGraduacaoBean(
			MatriculaGraduacaoMBean matriculaGraduacaoBean) {
		this.matriculaGraduacaoBean = matriculaGraduacaoBean;
	}

	public CalendarioAcademico getCalendarioMatricula() {
		return this.calendarioMatricula;
	}

	public void setCalendarioMatricula(CalendarioAcademico calendarioMatricula) {
		this.calendarioMatricula = calendarioMatricula;
	}

	public Collection<SugestaoMatricula> getSugestoesMatricula() {
		return this.sugestoesMatricula;
	}

	public void setSugestoesMatricula(Collection<SugestaoMatricula> sugestoesMatricula) {
		this.sugestoesMatricula = sugestoesMatricula;
		if (sugestoesMatricula != null) {
			sugestoesDataModel = new ListDataModel();
			sugestoesDataModel.setWrappedData(sugestoesMatricula);
		}
	}

	public Collection<SolicitacaoMatricula> getAtividades() {
		return this.atividades;
	}

	public void setAtividades(Collection<SolicitacaoMatricula> atividades) {
		this.atividades = atividades;
	}

	public DataModel getSugestoesDataModel() {
		return this.sugestoesDataModel;
	}

	public void setSugestoesDataModel(DataModel atividadesDataModel) {
		this.sugestoesDataModel = atividadesDataModel;
	}

	public void setPassivelMatriculaDefesa(boolean passivelMatriculaDefesa) {
		this.passivelMatriculaDefesa = passivelMatriculaDefesa;
	}

	public void setPassivelMatriculaQualificacao(
			boolean passivelMatriculaQualificacao) {
		this.passivelMatriculaQualificacao = passivelMatriculaQualificacao;
	}

	public boolean isPassivelMatriculaDefesa() {
		return passivelMatriculaDefesa;
	}

	public boolean isPassivelMatriculaQualificacao() {
		return passivelMatriculaQualificacao;
	}

	public boolean isRematricula() {
		return calendarioMatricula.isPeriodoReMatricula();
	}

	public boolean isPassivelMatriculaDisciplina() {
		return passivelMatriculaDisciplina;
	}

	public void setPassivelMatriculaDisciplina(boolean passivelMatriculaDisciplina) {
		this.passivelMatriculaDisciplina = passivelMatriculaDisciplina;
	}
	
}
