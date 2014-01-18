/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/07/2009
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.SimpleTimeZone;

import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.ListaAvaliacaoInstitucionalProcessar;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import br.ufrn.sigaa.avaliacao.negocio.MovimentoCalculoAvaliacaoInstitucional;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;
import br.ufrn.sigaa.mensagens.MensagensAvaliacaoInstitucional;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;

/**
 * Controller responsável pela operacionalização do processamento das notas da
 * Avaliação Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("processamentoAvaliacaoInstitucional") 
@Scope("session")
public class ProcessamentoAvaliacaoInstitucionalMBean extends SigaaAbstractController<ParametroProcessamentoAvaliacaoInstitucional> {
	
	// constantes que definem as etapas do processamento.
	/** Define a operação atual do controller como: Consultando turmas a processar. */
	private static final int CONSULTANDO_TURMAS = 1;
	/** Define a operação atual do controller como: Determinando Avaliações Inválidas. */
	private static final int DETERMINANDO_AVALIACOES_INVALIDAS = 2;
	/** Define a operação atual do controller como: Calculando médias e desvios padrões. */
	private static final int CALCULANDO_MEDIAS = 3;
	/** Define a operação atual do controller como: Excluíndo do banco de dados o resultado anterior. */
	private static final int EXCLUINDO_RESULTADO_ANTERIOR = 4;
	/** Define a operação atual do controller como: Persistindo o processamento. */
	private static final int PERSISTINDO_RESULTADO = 5;
	
	/** Senha do usuário, a fim de autorizar o processamento da avaliação. */
	private String senha;
	
	/** Formatador de datas, utilizado na visualização do tempo de processamento.*/
	private DateFormat dateFormatter;
	
	/** Lista de perguntas selecionadas pelo usuário, que determinarão se a avaliação deverá ser computada ou não. */
	private List<String> perguntasSelecionadas;

	/** Coleção de perguntas que o usuário pode selecionar. */
	private Collection<SelectItem> perguntasCombo;
	
	/** Etapa atual do processamento. */
	private int etapaProcessamento;
	
	/** Indica se deve excluir do processamento as avaliações de discentes que foram reprovados por falta. */
	private boolean excluirRepovacoesFalta;
	/** Coleção de últimos processamentos realizados. */
	private Collection<ParametroProcessamentoAvaliacaoInstitucional> ultimosProcessamentos;
	
	/** Construtor padrão. */
	public ProcessamentoAvaliacaoInstitucionalMBean() {
	}
	
	/**
	 * Inicia a operação de Processamento. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menu_avaliacao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciar() {
		init();
		return forward("/avaliacao/processamento/seleciona_aplicacao.jsp");
	}
	
	/** Seleciona um formulário para preenchimento da Avaliação Institucional<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/seleciona_aplicacao.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarFormulario() throws DAOException {
		obj = new ParametroProcessamentoAvaliacaoInstitucional();
		int id = getParameterInt("id");
		CalendarioAvaliacao calendario = getGenericDAO().findByPrimaryKey(id, CalendarioAvaliacao.class);
		if (calendario == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		obj.setFormulario(calendario.getFormulario());
		obj.setNumMinAvaliacoes(ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE));
		obj.setAno(calendario.getAno());
		obj.setPeriodo(calendario.getPeriodo());
		this.perguntasCombo = null;
		this.perguntasSelecionadas = null;
		return forward("/avaliacao/processamento/form.jsp");
	}
	
	/**
	 * Inicia a operação de liberação da consulta pelo docente ao resultado de
	 * sua avaliação institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String liberaResultados() throws DAOException{
		init();
		if (isEmpty(getUltimosProcessamentos())) {
			addMensagemErro("Não há resultados de avaliações disponíveis");
			return null;
		}
		else		
			return forward("/avaliacao/processamento/libera_consulta.jsp");
	}
	
	/** 
	 * Inicializa os atributos do controller.
	 * 
	 */
	private void init() {
		obj = new ParametroProcessamentoAvaliacaoInstitucional();
		obj.setNumMinAvaliacoes(ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE));
		this.senha = null;
		this.dateFormatter = new SimpleDateFormat("HH:mm:ss", Locale.ROOT);
		this.dateFormatter.setTimeZone(new SimpleTimeZone(0, "GMT"));
		this.ultimosProcessamentos = null;
		ListaAvaliacaoInstitucionalProcessar.setErro(null);
		ListaAvaliacaoInstitucionalProcessar.setDocenteTurmas(null);
	}
	
	/**
	 * Processa as notas da Avaliação Institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException 
	 */
	public String processarAvaliacao() throws ArqException, NegocioException {
		if( !confirmaSenha() )
			return redirectMesmaPagina();
		validacaoDados(erros);
		if (hasErrors()) {
			return redirectMesmaPagina();
		}
		
		if (isProcessando()) {
			addMensagem(MensagensAvaliacaoInstitucional.PROCESSAMENTO_EM_ANDAMENTO);
			return redirectMesmaPagina();
		}
		
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		
		try {
			Date inicio = new Date();
			obj.setInicioProcessamento(inicio);
			obj.setExcluirRepovacoesFalta(excluirRepovacoesFalta);
			// preparando o mapa de avaliações consideradas inválidas
			obj.setPerguntaDeterminanteExclusaoAvaliacao(new ArrayList<Pergunta>());
			for (String idPergunta : perguntasSelecionadas) {
				Pergunta pergunta = dao.findByPrimaryKey(Integer.parseInt(idPergunta), Pergunta.class);
				obj.getPerguntaDeterminanteExclusaoAvaliacao().add(pergunta);
			}
	
			// consultado turmas a processar
			etapaProcessamento = CONSULTANDO_TURMAS;
			Collection<? extends PersistDB> docentesTurma = null;
			if (obj.getFormulario().isAvaliacaoDocenciaAssistida())
				docentesTurma = dao.findTurmaDocenciaAssistidaProcessamento(0, obj.getAno(), obj.getPeriodo(), obj.getFormulario().getId());
			else
				docentesTurma = dao.findDocenteTurmaProcessamento(0, obj.getAno(), obj.getPeriodo(), obj.getFormulario().getId());
			if (docentesTurma == null || docentesTurma.isEmpty()) {
				addMensagem(MensagensGraduacao.NENHUMA_TURMA_NO_ANO_PERIODO);
				etapaProcessamento = 0;
				return forward("/avaliacao/processamento/form.jsp");
			}
			ListaAvaliacaoInstitucionalProcessar.reset();
			ListaAvaliacaoInstitucionalProcessar.setDocenteTurmas(docentesTurma);
			MovimentoCalculoAvaliacaoInstitucional mov = new MovimentoCalculoAvaliacaoInstitucional();
			
			// determinando avaliações que não serão calculadas.
			etapaProcessamento = DETERMINANDO_AVALIACOES_INVALIDAS;
			mov.setCodMovimento(SigaaListaComando.DETERMINA_AVALIACOES_INVALIDAS);
			mov.setUsuarioLogado(getUsuarioLogado());
			mov.setParametroProcessamento(obj);
			prepareMovimento(SigaaListaComando.DETERMINA_AVALIACOES_INVALIDAS);
			execute(mov);
			
			// processando docentesTurmas
 			obj.setInicioProcessamento(new Date());
			etapaProcessamento = CALCULANDO_MEDIAS;
			mov.setCodMovimento(SigaaListaComando.CALCULAR_RESULTADO_AVALIACAO_INSTITUCIONAL);
			mov.setParametroProcessamento(obj);
			mov.setSalvarResultado(false);
			// Enquanto possuir turmas a processar.
			while(ListaAvaliacaoInstitucionalProcessar.possuiDocenteTurma()) {
				PersistDB item = ListaAvaliacaoInstitucionalProcessar.getProximo();
				if (!obj.isAvaliacaoInvalida(item.getId())) {
					mov.setDocenciaAssistida(obj.getFormulario().isAvaliacaoDocenciaAssistida());
					if (mov.isDocenciaAssistida())
						mov.setTurmaDocenciaAssistida((TurmaDocenciaAssistida) item);
					else
						mov.setDocenteTurma((DocenteTurma) item);
					prepareMovimento(SigaaListaComando.CALCULAR_RESULTADO_AVALIACAO_INSTITUCIONAL);
					ResultadoAvaliacaoDocente resultado = (ResultadoAvaliacaoDocente) execute(mov);
					if (resultado != null) 
						ListaAvaliacaoInstitucionalProcessar.addResultado(resultado);
				}
				ListaAvaliacaoInstitucionalProcessar.registraProcessada();
			}
			
			// remove o resultado anterior
			etapaProcessamento = EXCLUINDO_RESULTADO_ANTERIOR;
			mov.setCodMovimento(SigaaListaComando.REMOVER_RESULTADO_AVALIACAO_INSTITUCIONAL);
			prepareMovimento(SigaaListaComando.REMOVER_RESULTADO_AVALIACAO_INSTITUCIONAL);
			execute(mov);
			
			// persistindo o resultado do processamento
			obj.setInicioProcessamento(new Date());
			etapaProcessamento = PERSISTINDO_RESULTADO;
			mov.setCodMovimento(SigaaListaComando.CALCULAR_RESULTADO_AVALIACAO_INSTITUCIONAL);
			mov.setSalvarResultado(true);
			prepareMovimento(SigaaListaComando.CALCULAR_RESULTADO_AVALIACAO_INSTITUCIONAL);
			execute(mov);
		} catch(Exception e) {
			ListaAvaliacaoInstitucionalProcessar.setErro(e);
			etapaProcessamento = 0;
			tratamentoErroPadrao(e);
			return redirect(getSubSistema().getLink());
		} finally {
			ListaAvaliacaoInstitucionalProcessar.setDocenteTurmas(null);
			dao.close();
		}
		
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		etapaProcessamento = 0;
		return cancelar();
	}

	/**
	 * Verifica se o processamento possui mensagens de erro e retorna ao menu
	 * principal. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/progresso.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws NegocioException
	 */
	public String retornaMenuPrincipal() throws NegocioException {
		// verifica as threads de processamento
		if (ListaAvaliacaoInstitucionalProcessar.getErro() != null) {
			notifyError(ListaAvaliacaoInstitucionalProcessar.getErro());
			addMensagem(MensagensAvaliacaoInstitucional.OCORREU_ERRO_PROCESSAMENTO);
		}
		return cancelar();
	}
	
	/** Indica se houve erro no processamento.
	 * @return
	 */
	public boolean isErroProcessamento() {
		return ListaAvaliacaoInstitucionalProcessar.getErro() != null;
	}
	
	/** Retorna o percentual do processamento. 
	 * @return Percentual do processamento. Ao fim do processamento, retorna o valor 101. 
	 */
	public int getPercentualProcessado() {
		if (ListaAvaliacaoInstitucionalProcessar.getErro() != null)
			return 0;
		switch (etapaProcessamento) {
			case CONSULTANDO_TURMAS: 
				return 1;
			case DETERMINANDO_AVALIACOES_INVALIDAS :
				return 1;
			case EXCLUINDO_RESULTADO_ANTERIOR :
			case PERSISTINDO_RESULTADO :
				int andamento = ListaAvaliacaoInstitucionalProcessar.totalSalvar - ListaAvaliacaoInstitucionalProcessar.restanteSalvar;
				if (ListaAvaliacaoInstitucionalProcessar.totalSalvar == 0)
					return 1;
				else
					return 100 * andamento / ListaAvaliacaoInstitucionalProcessar.totalSalvar;					
			case CALCULANDO_MEDIAS :
				if (ListaAvaliacaoInstitucionalProcessar.totalAProcessar >= 0) {
					if (ListaAvaliacaoInstitucionalProcessar.totalProcessado == 0 || ListaAvaliacaoInstitucionalProcessar.totalAProcessar == 0) return 0;
					else return 100 * ListaAvaliacaoInstitucionalProcessar.totalProcessado / ListaAvaliacaoInstitucionalProcessar.totalAProcessar;
				} else {
					return 1;
				}
			default :
			return 0;
		}
	}
	
	/** Retorna uma mensagem textual infomando o progresso do processamento.
	 * @return
	 */
	public String getMensagemProgresso() {
		switch (etapaProcessamento) {
		case CONSULTANDO_TURMAS: return "Aguarde: consultando turmas a processar...";
		case DETERMINANDO_AVALIACOES_INVALIDAS: return "Aguarde: determinando avaliações que não serão processadas...";
		case CALCULANDO_MEDIAS: 
			return "Calculando médias. Tempo estimando para conclusão: " + getTempoRestante();
		case EXCLUINDO_RESULTADO_ANTERIOR: return "Aguarde: removendo as médias do processamento anterior...";
		case PERSISTINDO_RESULTADO: 
			if(ListaAvaliacaoInstitucionalProcessar.restanteSalvar > 0)
				return "Nº de médias a gravar: " + ListaAvaliacaoInstitucionalProcessar.restanteSalvar +". Tempo estimado para o término: " + getTempoRestante();
			else 
				return "Aguarde: Finalizando o processamento...";
		default:
			return "";
		}
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getTempoRestante() {
		switch (etapaProcessamento) {
			case CALCULANDO_MEDIAS: 
				if (obj.getInicioProcessamento() == null || ListaAvaliacaoInstitucionalProcessar.totalProcessado == 0)
					return "...";
				Date agora = new Date();
				long decorrido = agora.getTime() - obj.getInicioProcessamento().getTime();
				long previsao = decorrido * ListaAvaliacaoInstitucionalProcessar.totalAProcessar / ListaAvaliacaoInstitucionalProcessar.totalProcessado;
				long restante = previsao - decorrido;
				Date r = new Date(restante);
				return dateFormatter.format(r);
			case PERSISTINDO_RESULTADO:
				int andamento = ListaAvaliacaoInstitucionalProcessar.totalSalvar - ListaAvaliacaoInstitucionalProcessar.restanteSalvar;
				if (andamento <= 0)
					return "...";
				agora = new Date();
				decorrido = agora.getTime() - obj.getInicioProcessamento().getTime();
				previsao = decorrido * ListaAvaliacaoInstitucionalProcessar.totalSalvar / andamento;
				restante = previsao - decorrido;
				r = new Date(restante);
				return dateFormatter.format(r);
			default :
				return "";
		}
	}
	
	/** Valida os seguintes dados: ano, período, número de threads.<br/>
	 * Método não invocado por JSP´s
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(java.util.Collection)
	 */
	@Override
	public boolean validacaoDados(ListaMensagens lista) {
		validateRequired(obj.getFormulario(), "Formulário de Avaliação Institucional", lista);
		validateRequired(obj.getPeriodo(), "Período", lista);
		validateMinValue(obj.getNumMinAvaliacoes(), ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE), "Nº Mínimo de Avaliações por Docente", lista);
		validateRequired(obj.getFormulario(), "Formulário de Avaliação", lista);
		return lista.isEmpty();
	}

	/** Indica que o processamento está sendo realizado.
	 * @return
	 */
	public boolean isProcessando() {
		return ListaAvaliacaoInstitucionalProcessar.totalAProcessar > 0;
	}

	/** Retorna a senha do usuário, a fim de autorizar o processamento da avaliação. 
	 * @return Senha do usuário, a fim de autorizar o processamento da avaliação. 
	 */
	public String getSenha() {
		return senha;
	}

	/** Seta a senha do usuário, a fim de autorizar o processamento da avaliação.
	 * @param senha Senha do usuário, a fim de autorizar o processamento da avaliação. 
	 */
	public void setSenha(String senha) {
		this.senha = senha;
	}

	/**
	 * Retorna uma coleção de SelectItem de Perguntas utilizadas na Avaliação
	 * Institucional.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPerguntasComboBox()
			throws HibernateException, DAOException {
		if (perguntasCombo == null) {
			perguntasCombo = new ArrayList<SelectItem>();
			int ordemGrupo = 1;
			for (GrupoPerguntas grupo : obj.getFormulario().getGrupoPerguntas()) {
				int ordemPergunta = 1;
				// retirando a tag <br/> que está cadastrada no banco.
				for (Pergunta pergunta : grupo.getPerguntas()) {
					if (pergunta.isNota()) {
						pergunta.setDescricao(pergunta.getDescricao().replaceAll("<br/>", "").replaceAll("<strong>", "").replaceAll("</strong>", ""));
						perguntasCombo.add(new SelectItem(new Integer(pergunta.getId()), 
								ordemGrupo+"."+ordemPergunta + " - " + pergunta.getDescricao()));
						ordemPergunta++;
					}
				}
				ordemGrupo++;
			}
		}
		return perguntasCombo;
	}

	public List<String> getPerguntasSelecionadas() {
		return perguntasSelecionadas;
	}

	public void setPerguntasSelecionadas(List<String> perguntasSelecionadas) {
		this.perguntasSelecionadas = perguntasSelecionadas;
	}

	/** Retorna as informações utilizadas nos últimos processamentos de uma Avaliação Institucional.
	 * @return
	 * @throws DAOException
	 */
	public Collection<ParametroProcessamentoAvaliacaoInstitucional> getUltimosProcessamentos() throws DAOException {
		if (ultimosProcessamentos == null) {
			AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
			ultimosProcessamentos  = dao.findUltimoProcessamentos();
		} 
		return ultimosProcessamentos;
	}
	
	/** Suspende a consulta ao resultado da Avaliação Institucional<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/libera_consulta.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String suspendeConsulta() throws ArqException, NegocioException {
		liberaProcessamento(false);
		return redirectMesmaPagina();
	}
	
	/** Libera a consulta ao resultado da Avaliação Institucional<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/libera_consulta.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String liberaConsulta() throws ArqException, NegocioException {
		liberaProcessamento(true);
		return redirectMesmaPagina();
	}
	
	/** Libera a consulta pelo docente ao resultado da sua Avaliação Institucional.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/processamento/libera_consulta.jsp</li>
	 * </ul>
	 * @param liberado
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void liberaProcessamento(boolean liberado) throws ArqException, NegocioException {
		populateObj(true);
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return;
		}
		Boolean discente = getParameterBoolean("discente");
		if (discente != null && discente.booleanValue())
			obj.setConsultaDiscenteLiberada(liberado);
		else
			obj.setConsultaDocenteLiberada(liberado);
		prepareMovimento(SigaaListaComando.LIBERAR_CONSULTA_AO_RESULTADO_DA_AVALIACAO);
		setOperacaoAtiva(SigaaListaComando.LIBERAR_CONSULTA_AO_RESULTADO_DA_AVALIACAO.getId());
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(obj);
		mov.setCodMovimento(SigaaListaComando.LIBERAR_CONSULTA_AO_RESULTADO_DA_AVALIACAO);
		execute(mov);
		ultimosProcessamentos = null;
	}

	public boolean isExcluirRepovacoesFalta() {
		return excluirRepovacoesFalta;
	}

	public void setExcluirRepovacoesFalta(boolean excluirRepovacoesFalta) {
		this.excluirRepovacoesFalta = excluirRepovacoesFalta;
	}
}
