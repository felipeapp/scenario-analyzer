/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/08/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.SimpleTimeZone;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoColacaoGrauColetiva;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;

/**
 * MBean responsável pelas operações de colação de grau coletiva.
 *
 * @author Gleydson
 *
 */
@Component("colacaoColetiva")
@Scope("session")
public class ColacaoGrauColetivaMBean extends SigaaAbstractController<Object> {
	// constantes que definem a operação atual do controller
	/** Constante que define a operação atual do controler como: Calculando totais dos discentes. */
	private static final int CALCULANDO_TOTAIS_DISCENTES = 1;
	/** Constante que define a operação atual do controler como: Concluindo discentes. */
	private static final int CONCLUINDO_DISCENTES = 2;
	
	/** Endereço do formulário de estorno coletivo. */
	private static final String JSP_BUSCA_ESTORNO_COLETIVO = "/graduacao/estorno_coletivo/busca.jsp";
	/** Endereço da lista de graduados do estorno coletivo. */
	private static final String JSP_LISTA_ESTORNO_COLETIVO = "/graduacao/estorno_coletivo/lista.jsp";	
	
	/** Data de colação de grau. */
	private Date dataColacao ;

	/** Coleção de discentes graduandos a colar grau. */
	private Collection<DiscenteGraduacao> graduandos = new ArrayList<DiscenteGraduacao>();
	
	/** Coleção de discentes concluintes. */
	private Collection<DiscenteGraduacao> concluintes = new ArrayList<DiscenteGraduacao>();	

	/** Curso a utilizado na busca dos discentes graduandos. */
	private Curso curso = new Curso();

	/** Ano de graduação dos discentes. */
	private Integer ano;

	/** Período de graduação dos discentes.*/
	private Integer periodo;
	
	/** Status dos alunos no estorno */
	private Integer statusRetorno;
	
	/** Percentual de alunos que tiveram o cálculo dos totais concluído. */
	private int percentualProcessado;
	
	/** Mensagem dada na view, do progresso de cálculo dos totais dos alunos. */
	private String mensagemProgresso;

	/** Operação atual do controler. */
	private int operacao;
	
	/** Total de discentes que tiveram o histórico recalculado. */
	private int totalDiscentesCalculados;
	
	/** Número total de discentes a calcular. */
	private int totalDiscentesCalcular;
	
	/** Hora inicial do cálculo dos históricos dos discentes. */
	private Date inicio;
	
	/** Tempo médio, em milisegundos, para calcular o total de cada discente. */
	private long tempoMedioCalculoDiscente;
	
	/** Formatador para a mensagem de estimativa de tempo no processo de conclusão coletiva. */
	private SimpleDateFormat dateFormatter;
	
	/** Coleção de discentes que foram confirmados para colar grau coletivamente. */
	private ArrayList<DiscenteGraduacao> confirmados;
	
	/** Indica que o controller deverá apenas alterar a data de colação dos concluintes. Caso contrário, deverá estornar a colação. */
	private boolean apenasAlterarDataColacao;
	
	/** Construtor padrão. */
	public ColacaoGrauColetivaMBean() {
		
	}

	/**
	 * Retorna o formulário de busca de cursos. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war//graduacao/colacao_coletiva/graduandos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String telaBuscaCurso() {
		return forward("/graduacao/colacao_coletiva/busca_curso.jsp");
	}

	/** Retorna o formulário de graduandos.
	 * Método não invocado por JSP
	 * @return
	 */
	public String telaGraduandos() {
		return forward("/graduacao/colacao_coletiva/graduandos.jsp");
	}

	/**
	 * Inicia a operação de colação de grau coletiva. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarColacaoColetiva() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		initObj();
		prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
		setOperacaoAtiva(SigaaListaComando.AFASTAR_ALUNO.getId());
		return telaBuscaCurso();
	}
	
	/**
	 * Inicia a operação de colação de grau coletiva. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/programa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciarAlteracaoDataColacao() throws ArqException {
		checkRole(SigaaPapeis.DAE);
		initObj();
		prepareMovimento(SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA);
		setOperacaoAtiva(SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId());
		this.apenasAlterarDataColacao = true;
		return telaBuscaEstornoColetivo();
	}

	/** Inicializa os atributos do controller.
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	private void initObj() throws SegurancaException, ArqException {
		int anoPeriodoAnterior = DiscenteHelper.somaSemestres(getCalendarioVigente().getAno(),
				getCalendarioVigente().getPeriodo(), -1);
		ano = anoPeriodoAnterior / 10;
		periodo = anoPeriodoAnterior - (ano * 10);
		operacao = 0;
		inicio = null;
		dateFormatter = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
		dateFormatter.setTimeZone(new SimpleTimeZone(0, "GMT"));
	}

	/**
	 * Busca por discentes graduandos. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/colacao_coletiva/busca_curso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarGraduandos() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.DAE);
		if (!checkOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId(), 
				SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId(),
				SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.getId()))
			return cancelar();

		validar();

		if (hasErrors())
			return null;

		int anoPeriodoNormal = new Integer(ano+""+periodo);
		int anoFerias, periodoFerias;
		if (periodo == 1) {
			anoFerias = ano - 1;
			periodoFerias = 4;
		} else {
			anoFerias = ano;
			periodoFerias = 3;
		}

		int anoPeriodoFerias = new Integer(anoFerias+""+periodoFerias);

		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		// busca por graduandos que possuam matrículas no semestre informado, e  no semestre de férias anterior a ele
		graduandos = dao.findGraduandosParaColacao(curso.getId(), anoPeriodoNormal, anoPeriodoFerias);
		if (graduandos == null || graduandos.isEmpty()) {
			addMensagemErro("Não existem discentes graduandos nesse curso para esse semestre informado");
			return null;
		}
		
		Iterator<DiscenteGraduacao> iterator = graduandos.iterator();
		
		while (iterator.hasNext()) {
			DiscenteGraduacao discente = iterator.next();
			if (discente.getParticipacaoEnadeConcluinte() != null &&
				!discente.getParticipacaoEnadeConcluinte().isParticipacaoPendente() &&
				discente.getParticipacaoEnadeIngressante() != null &&
				!discente.getParticipacaoEnadeIngressante().isParticipacaoPendente())
				discente.setMatricular(true);
		}

		curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
		operacao = 0;
		inicio = null;
		return telaGraduandos();
	}

	/**
	 * Valida os dados utilizados na busca de discentes.
	 */
	private void validar() {
		if (periodo == null || periodo > 2 || periodo <= 0) {
			addMensagemErro("Semestre inválido");
		}
		if (ano == null || ano < 1900 || ano > 2500) {
			addMensagemErro("Ano inválido");
		}
	}

	/**
	 * Confirma a colação de grau da lista de discentes.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/colacao_coletiva/graduandos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId(), 
				SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId(),
				SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.getId()))
			return cancelar();
		if (dataColacao == null) {
			validateRequired(dataColacao, "Data de Colação", erros);
		} else {
			validateRange(dataColacao, CalendarUtils.adicionaDias(new Date(), -365), CalendarUtils.adicionaDias(new Date(), 365), "Data de Colação", erros);
		}

		confirmados = new ArrayList<DiscenteGraduacao>(0);
		for (DiscenteGraduacao dg : graduandos) {
			if (dg.isMatricular())
				confirmados.add(dg);
		}
		
		if (confirmados.isEmpty()) {
			addMensagemErro("Deve ser escolhido no mínimo um discente");
		}

		if (hasErrors()) {
			redirectMesmaPagina();
			return null;
		}
		
		percentualProcessado = 1;
		mensagemProgresso = "Aguarde...";
		
		// recalcular os totais dos discentes antes de efetuar a colação de grau coletiva
		try {
			tempoMedioCalculoDiscente = 0;
			totalDiscentesCalculados = 1;
			totalDiscentesCalcular = confirmados.size();
			inicio = new Date();
			operacao = CALCULANDO_TOTAIS_DISCENTES;
			for (DiscenteGraduacao discente : confirmados) {
				percentualProcessado = 100 * totalDiscentesCalculados / totalDiscentesCalcular;
				prepareMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
				movCad.setObjMovimentado(discente);
				executeWithoutClosingSession(movCad, getCurrentRequest());
				atualizaTempoMedioCalculoDiscente(); 
				totalDiscentesCalculados++;
			}
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			redirectMesmaPagina();
			return null;
		} catch (Exception e) {
			notifyError(e);
			addMensagemErro("Erro inesperado");
			redirectMesmaPagina();
			return null;
		} finally {
			// mantem o progressbar na view
			percentualProcessado = -1;
		}
		
		// após os cálculos, verifica se todos estão com status Graduando (se não houve mudança de status)
		StringBuilder str = new StringBuilder();
		for (DiscenteGraduacao discente : confirmados) {
			if (discente.getStatus() != StatusDiscente.GRADUANDO)
				str.append((str.length() > 0 ? ", " : "") + discente.getMatriculaNome());
		}
		if (str.length() > 0) {
			addMensagemErro("Os seguintes discentes não são graduandos: " + str.toString());
			return redirectMesmaPagina();
		}
		
		return redirectJSF("/graduacao/colacao_coletiva/confirma.jsp");
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		if (!checkOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId(), 
				SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId()))
			return cancelar();
		if( !confirmaSenha() ) {
			addMensagens(erros);
			redirectMesmaPagina();
			return null;
		}
		// processar a colação de grau coletiva
		tempoMedioCalculoDiscente = 0;
		totalDiscentesCalculados = 1;
		totalDiscentesCalcular = confirmados.size();
		inicio = new Date();
		operacao = CONCLUINDO_DISCENTES;
		try {
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			TipoMovimentacaoAluno tipoConclusao = getGenericDAO().findByPrimaryKey(TipoMovimentacaoAluno.CONCLUSAO, TipoMovimentacaoAluno.class);
			for(DiscenteGraduacao dg : confirmados) {
				// caso já tenha sido processado, pula  para o próximo
				if (!dg.isMatricular()) continue;
				percentualProcessado = 100 * totalDiscentesCalculados / totalDiscentesCalcular;
				prepareMovimento(SigaaListaComando.AFASTAR_ALUNO);
				MovimentacaoAluno conclusao = new MovimentacaoAluno();
				conclusao.setDiscente(dg.getDiscente());
				conclusao.setAnoReferencia(getAno());
				conclusao.setPeriodoReferencia(getPeriodo());
				conclusao.setAnoOcorrencia(cal.getAno());
				conclusao.setPeriodoOcorrencia(cal.getPeriodo());
				conclusao.setTipoMovimentacaoAluno(tipoConclusao);
				conclusao.setDataColacaoGrau(getDataColacao());
				MovimentoCadastro movCad = new MovimentoCadastro();
				movCad.setCodMovimento(SigaaListaComando.AFASTAR_ALUNO);
				movCad.setUsuarioLogado(getUsuarioLogado());
				movCad.setObjMovimentado(conclusao);
				movCad.setObjAuxiliar(new Boolean(false));
				execute(movCad);
				atualizaTempoMedioCalculoDiscente();
				totalDiscentesCalculados++;
				// marca o discente como processado
				dg.setMatricular(false);
			}
			addMessage("Colação de grau coletiva efetivada com sucesso", TipoMensagemUFRN.INFORMATION);
		} catch (NegocioException e) {
			ListaMensagens lista = new ListaMensagens();
			lista.addErro(e.getMessage());
			lista.addErro("Foram concluídos " + totalDiscentesCalculados + " discente. Por favor, reinicie a conclusão coletiva buscando novamente por discentes.");
			addMensagensAjax(lista);
			return forward("/graduacao/colacao_coletiva/confirma.jsp");
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroAjax("Erro inesperado");
			return null;
		} finally {
			percentualProcessado = -1;
		}
		
		prepareMovimento(SigaaListaComando.COLACAO_GRAU_COLETIVA);
		setOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId());
		operacao = 0;
		inicio = null;
		return redirectJSF("/graduacao/colacao_coletiva/busca_curso.jsp");
	}
	
	/**
	 * Confirma o estorno de conclusão coletiva.
	 * Método chamado pela seguinte JSP: /graduacao/estorno_coletivo/lista.jsp
	 * @return
	 * @throws ArqException
	 */
	public String confirmarEstorno() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId(), 
				SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId(),
				SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.getId()))
			return cancelar();
		if (apenasAlterarDataColacao) {
			validateRequired(dataColacao, "Nova Data de Colação", erros);
		} else if (!apenasAlterarDataColacao && statusRetorno == 0) {
			addMensagemErro("Selecione um status!");
			return null;
		} else 
		
		if( !confirmaSenha() || hasErrors() )
			return null;

		Collection<DiscenteGraduacao> confirmados = new ArrayList<DiscenteGraduacao>(0);
		for (DiscenteGraduacao dg : concluintes) {
			if (dg.isSelecionado())
				confirmados.add(dg);
		}
		if (confirmados.isEmpty()) {
			addMensagemErro("Deve ser escolhido no mínimo um discente");
			return null;
		}

		MovimentoColacaoGrauColetiva mov = new MovimentoColacaoGrauColetiva();
		mov.setAno(getAno());
		mov.setPeriodo(getPeriodo());
		mov.setDiscentes(confirmados);
		if (apenasAlterarDataColacao) {
			for (DiscenteGraduacao dg : confirmados) {
				dg.setDataColacaoGrau(dataColacao);
			}
			mov.setCodMovimento(SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA);
		} else {
			mov.setStatusRetorno(statusRetorno);
			mov.setCodMovimento(SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO);
		}
		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			return cancelar();
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
		}			
		return null;
	}
	
	/**
	 * Redireciona para a tela de busca de estorno de conclusão coletiva.
	 * Método chamado pela seguinte JSP: /graduacao/estorno_coletivo/lista.jsp
	 * @return
	 */
	public String telaBuscaEstornoColetivo(){
		return forward(JSP_BUSCA_ESTORNO_COLETIVO);
	}
	
	/**
	 * Inicia o estorno de uma registro de afastamento para todos o alunos de um curso e período selecionado.
	 * Método chamado pela seguinte JSP: /graduacao/menus/programa.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciarEstornoColetivo() throws ArqException{		
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CHEFIA_DAE);
		initObj();		
		prepareMovimento(SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO);
		setOperacaoAtiva(SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.getId());
		this.apenasAlterarDataColacao = false;
		return telaBuscaEstornoColetivo();
	}

	/**
	 * Busca por discentes graduados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/estorno_coletivo/busca.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String buscarConcluintes() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.CHEFIA_DAE);
		if (!checkOperacaoAtiva(SigaaListaComando.COLACAO_GRAU_COLETIVA.getId(),
				SigaaListaComando.AFASTAR_ALUNO.getId(), 
				SigaaListaComando.ALTERAR_DATA_COLACAO_COLETIVA.getId(),
				SigaaListaComando.ESTORNAR_CONCLUSAO_ALUNO.getId()))
			return cancelar();
		
		validateRequired(curso, "Curso", erros);
		
		if (ano == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		
		if (periodo == null)
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");		

		if (hasErrors())
			return null;
		
		int anoPeriodoNormal = new Integer(ano+""+periodo);
		int anoFerias, periodoFerias;
		if (periodo == 1) {
			anoFerias = ano - 1;
			periodoFerias = 4;
		} else {
			anoFerias = ano;
			periodoFerias = 3;
		}

		int anoPeriodoFerias = new Integer(anoFerias+""+periodoFerias);

		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class);
		// busca por concluintes que concluiram no semestre informado, e  no semestre de férias anterior a ele
		concluintes = dao.findConcluintesbyCursoPeriodo(curso.getId(), anoPeriodoNormal, anoPeriodoFerias);
		if (concluintes == null || concluintes.isEmpty()) {
			addMensagemErro("Não existem discentes concluíntes nesse curso e período informados");
			return null;
		}		
	
		Iterator<DiscenteGraduacao> iterator = concluintes.iterator();
		
		while (iterator.hasNext()) {
			DiscenteGraduacao discente = iterator.next();
			// marca todos os alunos
			discente.setSelecionado(true);
		}		
			
		curso = dao.findByPrimaryKey(curso.getId(), Curso.class);
		return forward(JSP_LISTA_ESTORNO_COLETIVO);
	}		
	

	/** Retorna o curso a utilizado na busca dos discentes graduandos. 
	 * @return
	 */
	public Curso getCurso() {
		return curso;
	}

	/** Seta o curso a utilizado na busca dos discentes graduandos.
	 * @param curso
	 */
	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	/** Retorna a data de colação de grau. 
	 * @return
	 */
	public Date getDataColacao() {
		return dataColacao;
	}

	/** Seta a data de colação de grau.
	 * @param dataColacao
	 */
	public void setDataColacao(Date dataColacao) {
		this.dataColacao = dataColacao;
	}

	/** Retorna a coleção de discentes graduandos a colar grau. 
	 * @return
	 */
	public Collection<DiscenteGraduacao> getGraduandos() {
		return graduandos;
	}

	/** Seta a coleção de discentes graduandos a colar grau.
	 * @param graduandos
	 */
	public void setGraduandos(Collection<DiscenteGraduacao> graduandos) {
		this.graduandos = graduandos;
	}

	/** Retorna o ano de graduação dos discentes. 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano de graduação dos discentes.
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o período de graduação dos discentes.
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o período de graduação dos discentes.
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Collection<DiscenteGraduacao> getConcluintes() {
		return concluintes;
	}

	public void setConcluintes(Collection<DiscenteGraduacao> concluintes) {
		this.concluintes = concluintes;
	}

	public Integer getStatusRetorno() {
		return statusRetorno;
	}

	public void setStatusRetorno(Integer statusRetorno) {
		this.statusRetorno = statusRetorno;
	}

	public int getPercentualProcessado() {
		return percentualProcessado;
	}
	
	/** Atualiza o tempo médio gasto para o cálculo dos totais de um discente. */
	private void atualizaTempoMedioCalculoDiscente() {
		tempoMedioCalculoDiscente = 0;
		if (inicio != null && totalDiscentesCalculados > 0) {
			Date agora = new Date();
			long decorrido = agora.getTime() - inicio.getTime();
			tempoMedioCalculoDiscente = decorrido / totalDiscentesCalculados;
		}
	}

	/** Informa o processo atual e calcula o tempo estimado para conclusão da operação.
	 * @return
	 */
	public String getMensagemProgresso() {
		mensagemProgresso = "";
		if (inicio != null) {
			Date agora = new Date();
			long previsao = totalDiscentesCalcular * tempoMedioCalculoDiscente;
			long decorrido = agora.getTime() - inicio.getTime();
			Date r = new Date(previsao - decorrido);
			switch (operacao) {
			case CALCULANDO_TOTAIS_DISCENTES:
				mensagemProgresso = "Calculando os totais dos discentes ("
						+ totalDiscentesCalculados + " de "
						+ totalDiscentesCalcular + ")" 
						+ (tempoMedioCalculoDiscente > 0 ? ". Conclus&atilde;o estimada em " + dateFormatter.format(r) : "");
				break;
			case CONCLUINDO_DISCENTES:
				mensagemProgresso = "Processando a cola&ccedil;&atilde;o de grau coletiva ("
						+ totalDiscentesCalculados + " de "
						+ totalDiscentesCalcular + ")" 
						+ (tempoMedioCalculoDiscente > 0 ? ". Conclus&atilde;o estimada em " + dateFormatter.format(r) : "");
				break;
			default:
				mensagemProgresso = "Aguarde...";
				break;
			}
		}
		return mensagemProgresso;
	}

	public ArrayList<DiscenteGraduacao> getConfirmados() {
		return confirmados;
	}

	public boolean isApenasAlterarDataColacao() {
		return apenasAlterarDataColacao;
	}

	public void setApenasAlterarDataColacao(boolean apenasAlterarDataColacao) {
		this.apenasAlterarDataColacao = apenasAlterarDataColacao;
	}

	
}
