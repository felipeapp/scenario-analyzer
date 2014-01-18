/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 30/04/2008
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dao.CalendarioAvaliacaoDao;
import br.ufrn.sigaa.avaliacao.dominio.AlternativaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.TipoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.negocio.AvaliacaoInstitucionalHelper;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ead.dominio.TutorOrientador;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.mensagens.MensagensAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
/**
 * Managed bean para realização da avaliação da docência
 * pelos discentes.
 * 
 * @author David Pereira
 *
 */
@Component("avaliacaoInstitucional") @Scope("session")
public class AvaliacaoInstitucionalMBean extends SigaaAbstractController<AvaliacaoInstitucional> {

	/** Lista de docentes das turmas em que o discente está matriculado. */
	private List<DocenteTurma> docenteTurmasDiscente;

	/** Lista de turmas avaliadas. */
	private List<Turma> turmasDiscenteComMaisDeUmDocente;

	/** Lista de turmas que o docente leciona. */
	private List<DocenteTurma> turmasDocente;

	/** Lista de turmas que o discente trancou. */
	private List<Turma> trancamentosDiscente;
	
	/** Lista de IDs de todas perguntas.*/
	private List<Integer> todasPerguntas;
	
	/** Lista de IDs de perguntas não respondidas. */
	private List<Integer> perguntasNaoRespondidas;
	
	/** Lista de IDs das turmas que não tiveram as perguntas respondidas. */
	private Set<String> perguntaTurmaNaoRespondidas;

	/** Indica se o usuário deve preencher também o questionário de satisfação. */
	private boolean preTeste = false;

	/** Ano da Avaliação Institucional */
	private int ano;
	
	/** Período da Avaliação Institucional. */
	private int periodo;

	/** Formulário ao qual esta avaliação está associada. */
	private FormularioAvaliacaoInstitucional formulario;

	/**
	 * Inicia o processo de avaliação pelo docente. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarDocente() throws ArqException, NegocioException {
		init();
		boolean avaliacaoDocenteAtiva = ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.AVALIACAO_DOCENTE_ATIVA);
		
		if (!avaliacaoDocenteAtiva)
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.INDISPONIVEL_NO_ANO_PERIODO).getMensagem());
		
		Servidor servidor = getUsuarioLogado().getServidorAtivo();
		DocenteExterno docenteExterno = getUsuarioLogado().getVinculoAtivo().getDocenteExterno();
		
		if ((servidor == null && docenteExterno == null) || (servidor != null && !servidor.isDocente())) {
			throw new SegurancaException();
		}
		
		if (!docentePodeAvaliar())
			return null;
		
		// Verificar se existem avaliações já salvas
		if (docenteExterno == null)
			obj = getDAO(AvaliacaoInstitucionalDao.class).findByDocente(servidor, getAno(), getPeriodo(), formulario);
		else
			obj = getDAO(AvaliacaoInstitucionalDao.class).findByDocenteExterno(docenteExterno, getAno(), getPeriodo(), formulario);
		
		if (obj == null) { // Não existiam avaliações anteriores. Criar uma nova
			obj = new AvaliacaoInstitucional();
			if (docenteExterno == null)
				obj.setServidor(servidor);
			else
				obj.setDocenteExterno(docenteExterno);
			obj.setAno(getAno());
			obj.setPeriodo(getPeriodo());
			obj.setRespostas(new ArrayList<RespostaPergunta>());
			obj.setFinalizada(false);
			obj.setFormulario(formulario);
		} else if ( (obj.getFormulario() == null && this.formulario == null
				|| obj.getFormulario() != null && formulario != null && obj.getFormulario().getId() == formulario.getId() )
				&& obj.isFinalizada()) { // Não deixar avaliar novamente se a avaliação estiver finalizada.
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA).getMensagem());
		} 
		
		if (obj.getObservacoesDocenteTurma() != null)
			obj.getObservacoesDocenteTurma().iterator();
		
		// remove respostas de turmas que foram excluídas
		Iterator<RespostaPergunta> iterator = obj.getRespostas().iterator();
		while (iterator.hasNext()) {
			RespostaPergunta resposta = iterator.next();
			if (resposta.getDocenteTurma() != null) {
				boolean turmaOk = false;
				for (DocenteTurma dt : getTurmasDocente()){
					if (dt.getId() == resposta.getDocenteTurma().getId())
						turmaOk = true;
				}
				if (!turmaOk)
					iterator.remove();
			}
		}
		
		// Lista todas as perguntas para depois verificar quais foram respondidas e quais não foram
		preencherListaPerguntas(getGruposDocente());
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_DOCENTE);
		prepareMovimento(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
		setOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId());
		return redirect("/avaliacao/introDocente.jsf");
	}
	
	/**
	 * Inicia o processo de avaliação pelo tutor de ensino EAD. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/tutor/menu_tutor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarTutor() throws ArqException, NegocioException {
		init();
		boolean avaliacaoAtiva = false;
		
		CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(getUsuarioLogado());
		getCurrentSession().setAttribute(CALENDARIO_SESSAO, cal);
		
//		if (!cal.isPeriodoAvaliacaoInstitucional()) {
////			if (cal.getInicioMatriculaOnline() == null || cal.getFimReMatricula() == null) {
////				addMensagem(MensagensAvaliacaoInstitucional.PERIODO_AVALIACAO_NAO_DEFINIDO);
////			} else {
////				addMensagem(MensagensAvaliacaoInstitucional.PERIODO_AVALIACAO_INSTITUCIONAL, cal.getInicioMatriculaOnline(), cal.getFimReMatricula());
////			}
//			return null;
//		} else {
//			avaliacaoAtiva = true;
//		}
//		
//		if (!avaliacaoAtiva)
//			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.INDISPONIVEL_NO_ANO_PERIODO).getMensagem());
		
		TutorOrientador tutor = getUsuarioLogado().getTutor();
		
		if (tutor == null)
			throw new NegocioException("Não foi possível determinar seu vínculo de tutor de ensino à distância.");
		
		// Verificar se existem avaliações já salvas
		obj = getDAO(AvaliacaoInstitucionalDao.class).findByTutor(tutor, getAno(), getPeriodo(), formulario);
		
		if (obj == null) { // Não existiam avaliações anteriores. Criar uma nova
			obj = new AvaliacaoInstitucional();
			obj.setTutorOrientador(tutor);
			obj.setAno(getAno());
			obj.setPeriodo(getPeriodo());
			obj.setRespostas(new ArrayList<RespostaPergunta>());
			obj.setFinalizada(false);
			obj.setFormulario(formulario);
		} else if (obj.isFinalizada()) { // Não deixar avaliar novamente se a avaliação estiver finalizada.
			addMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA);
			return null;
//			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA).getMensagem());
		} 
		
		obj.getRespostas().iterator();
		if (obj.getObservacoesDocenteTurma() != null)
			obj.getObservacoesDocenteTurma().iterator();
		
		// Lista todas as perguntas para depois verificar quais foram respondidas e quais não foram
		preencherListaPerguntas(getGruposTutor());
		prepareMovimento(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
		setOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId());
		return redirect("/avaliacao/introTutor.jsf");
	}

	/**
	 * Inicia o processo de avaliação pelo discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/discente/menu_discente.jsp</li>
	 * <li>/portais/discente/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarDiscente() throws ArqException, NegocioException {
		init();
		Discente discente = getUsuarioLogado().getDiscenteAtivo().getDiscente();

		if (discente == null) {
			throw new SegurancaException();
		} else if ( !discente.isAtivo() || !discente.isGraduacao()) {
			addMensagemErro("Somente discentes de graduação ativos podem preencher a Avaliação Institucional.");
			return null;
		}
		if (isEmpty(getSubSistema()))
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_DISCENTE);
		CalendarioAcademico cal = getCalendarioVigente();
		CalendarioAvaliacaoDao calDao;
		if (!cal.isPeriodoAvaliacaoInstitucional()) {
			if (cal.getInicioMatriculaOnline() == null || cal.getFimMatriculaExtraordinaria() == null) {
				addMensagem(MensagensAvaliacaoInstitucional.PERIODO_AVALIACAO_NAO_DEFINIDO);
			} else {
				addMensagem(MensagensAvaliacaoInstitucional.PERIODO_AVALIACAO_INSTITUCIONAL, cal.getInicioMatriculaOnline(), cal.getFimMatriculaExtraordinaria());
			}
			return cancelar();
		} else {
			// entrou aqui direto do login e o formulário não foi setado.
			if (formulario == null) {
				calDao = getDAO(CalendarioAvaliacaoDao.class);
				CalendarioAvaliacao calendario = calDao.findCalendarioAtivo(TipoAvaliacaoInstitucional.AVALIACAO_DISCENTE_GRADUACAO, discente.getCurso().isADistancia());
				if (calendario != null) {
					formulario = calendario.getFormulario();
					this.ano = calendario.getAno();
					this.periodo = calendario.getPeriodo();
				} else {
					addMensagemErro("Selecione um formulário da lista de calendários da Avaliação Institucional para o preenchimento.");
					return null;
				}
			}
			AvaliacaoInstitucional avaliacao = getDAO(AvaliacaoInstitucionalDao.class).findByDiscente(discente, getAno(), getPeriodo(), formulario);
			if (avaliacao != null && avaliacao.isFinalizada()) {  // Não deixar avaliar novamente se a avaliação estiver finalizada.
				addMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA);
				return null;
			}
			if (!AvaliacaoInstitucionalHelper.aptoPreencherAvaliacaoVigente(discente, formulario, getAno(), getPeriodo()))
				throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_INDISPONIVEL, "você não está matriculado em alguma turma de graduação em " + getAno() + "." + getPeriodo()).getMensagem());
		}

		//Adicionado a instância do subsSitema com objetivo de retirar o texto não definido da view
		if (ValidatorUtil.isEmpty(getSubSistema()))
			setSubSistemaAtual(new SubSistema(0,"",null,null));

		if (!getAcessoMenu().isDiscente()) {
			throw new SegurancaException("Somente discentes podem realizar esta operação.");
		}
		// Verificar se existem avaliações já salvas
		obj = getDAO(AvaliacaoInstitucionalDao.class).findByDiscente(discente, getAno(), getPeriodo(), formulario);
		
		if (obj == null) { // Não existiam avaliações anteriores. Criar uma nova
			obj = new AvaliacaoInstitucional();
			obj.setDiscente(discente);
			obj.setAno(getAno());
			obj.setPeriodo(getPeriodo());
			obj.setRespostas(new ArrayList<RespostaPergunta>());
			obj.setFinalizada(false);
			obj.setFormulario(formulario);
		} else if (obj.isFinalizada()) {  // Não deixar avaliar novamente se a avaliação estiver finalizada.
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA).getMensagem());
		} else {
			obj.setDiscente(discente); //evitar erro de Lazy no processador
		}

		if (obj.getTrancamentos() != null) {
			for (ObservacoesTrancamento obs : obj.getTrancamentos())
				obs.getTurma().getId();
		}
		if (obj.getObservacoesDocenteTurma() != null){
			for (ObservacoesDocenteTurma obs : obj.getObservacoesDocenteTurma())
				obs.getTurma().getId();
		}
		if (obj.getRespostas() != null) {
			for (RespostaPergunta resposta : obj.getRespostas()) {
				if (resposta.getDocenteTurma() != null) resposta.getDocenteTurma().getId();
				if (resposta.getTurmaDocenciaAssistida() != null) resposta.getTurmaDocenciaAssistida().getId();
			}
		}
		
		// Lista todas as perguntas para depois verificar quais foram respondidas e quais não foram
		preencherListaPerguntas(getGruposDiscente());
		
		prepareMovimento(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
		setOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId());
		return redirect("/avaliacao/introDiscente.jsf");
	}

	/** Preenche a lista de IDs de todas perguntas.
	 * Método não chamado por JSP.
	 * @param grupos
	 * @throws DAOException
	 */
	private void preencherListaPerguntas(List<GrupoPerguntas> grupos) throws DAOException {
		todasPerguntas = new ArrayList<Integer>();
		if(!isEmpty(grupos)){
			for (GrupoPerguntas grupo : grupos) {
				if(!isEmpty(grupo.getPerguntasAtivas())){
					for (Pergunta pergunta : grupo.getPerguntasAtivas()) {
						if (pergunta.getAlternativas() != null)
							pergunta.getAlternativas().iterator();
						todasPerguntas.add(pergunta.getId());				
					}
				}
			}
		}
	}
	
	/** Retorna o link para o formulário de avaliação pelo discente.
	 * Método não invocado por JSP´s.
	 * @return
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public String avaliacaoDiscente() throws NegocioException, ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId()))
			return null;
		if (!getAcessoMenu().isDiscente()) {
			throw new SegurancaException("Somente docentes podem realizar esta operação.");
		}
		return forward("/avaliacao/discente.jsp");
	}
	
	/** Retorna o link para o formulário de avaliação pelo docente.
	 * Método não invocado por JSP´s.
	 * @return
	 * @throws SegurancaException 
	 */
	public String avaliacaoDocente() throws SegurancaException {
		if (!checkOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId()))
			return null;
		if (!getAcessoMenu().isDocente()) {
			throw new SegurancaException("Somente docentes podem realizar esta operação.");
		}
		return forward("/avaliacao/docente.jsp");
	}

	/**
	 * Retorna o link para o formulário de avaliação pelo tutor de ensino EAD.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/introTutor.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String avaliacaoTutor() throws SegurancaException {
		if (!checkOperacaoAtiva(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE.getId()))
			return null;
		if (!getAcessoMenu().isTutorEad()) {
			throw new SegurancaException("Somente tutores de ensino à distância podem realizar esta operação.");
		}
		return forward("/avaliacao/docente.jsp");
	}

	/**
	 * Salvar os dados digitados na avaliação sem finalizar a mesma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/docente.jsp</li>
	 * <li>/avaliacao/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String salvar()  {
		try {
			obj.setFinalizada(false);
			
			carregarNotas();
			carregaTrancamentos();
			carregaObservacoes();
			
			prepareMovimento(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			gravarDados(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO, "Avaliação Institucional");
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		} 
		
		return null;
	}

	/**
	 * Salva os dados digitados e grava a avaliação como finalizada.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/docente.jsp</li>
	 * <li>/avaliacao/discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String finalizar() throws Exception {
		// Verificar se todas as perguntas foram respondidas
		boolean completa = false;
		carregarNotas();
		carregaTrancamentos();
		carregaObservacoes();
		
		completa = verificaPreenchimentoCompleto();
		
		// Só continua se a avaliação tiver sido respondida completamente
		if (!completa) {
			addMensagem(MensagensAvaliacaoInstitucional.PERGUNTAS_NAO_RESPONDIDAS);
			obj.setFinalizada(false);
			prepareMovimento(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			gravarDados(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			return null;
		} else {
			obj.setFinalizada(true);
			gravarDados(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,"Avaliação Institucional");
		}
			
		if (preTeste) {
			prepareMovimento(SigaaListaComando.QUESTIONARIO_SATISFACAO);
			resetBean();
			return redirect("/avaliacao/satisfacao.jsf");
		}
		
		removeOperacaoAtiva();
		return cancelar();
	}
	
	/** Verifica o preenchimento completo da Avaliação Institucional.
	 * @return
	 * @throws DAOException
	 */
	private boolean verificaPreenchimentoCompleto() throws DAOException {
		// perguntas preenchidas por discentes
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			return verificaPreenchimentoCompletoDiscente();
		} else {
			return verificaPreenchimentoCompletoDocente();
		}
		
	}
	
	/** Verifica o preenchimento completo da Avaliação Institucional preenchida pelo discente.
	 * @return
	 * @throws DAOException 
	 */
	private boolean verificaPreenchimentoCompletoDiscente() throws DAOException {
		// Mapa de perguntas respondidas
		Map<Integer, Map<Integer, Boolean>> mapaRespostasDocenteTurma = new TreeMap<Integer, Map<Integer, Boolean>>();
		Map<Integer, Boolean> mapaPerguntas = new TreeMap<Integer, Boolean>();
		
		List<GrupoPerguntas> grupos = obj.getDiscente() != null ? getGruposDiscente() : getGruposDocente();
		List<DocenteTurma> todosDocentesTurmas = new ArrayList<DocenteTurma>();
		
		for (Turma turma : getTurmasDiscenteComMaisDeUmDocente())
			todosDocentesTurmas.addAll(turma.getDocentesTurmas());
		for (GrupoPerguntas grupo : grupos) {
			for (Pergunta pergunta : grupo.getPerguntasAtivas()) {
				if (grupo.isAvaliaTurmas() && pergunta.isAvaliarTurmas()) {
					Map<Integer, Boolean> dtResposta = mapaRespostasDocenteTurma.get(pergunta.getId());
					if (dtResposta == null) {
						dtResposta = new TreeMap<Integer, Boolean>();
						mapaRespostasDocenteTurma.put(pergunta.getId(), dtResposta);
					}
					// perguntas para turmas com múltiplo docentes
					for (DocenteTurma dt : todosDocentesTurmas) {
						dtResposta.put(dt.getId(), isPerguntaRespondida(pergunta.getId(), dt.getId()));
					}
					// perguntas para turmas com único docente
					for (DocenteTurma dt : getDocenteTurmasDiscente()) {
						Boolean temResposta = mapaPerguntas.get(pergunta.getId());
						temResposta = temResposta == null ? isPerguntaRespondida(pergunta.getId(), dt.getId()) : temResposta && isPerguntaRespondida(pergunta.getId(), dt.getId()); 
						mapaPerguntas.put(pergunta.getId(), temResposta);
					}
				} else {
					mapaPerguntas.put(pergunta.getId(), isPerguntaRespondida(pergunta.getId())); 
				}
			}
		}
		
		// perguntas que não foram respondidas
		perguntasNaoRespondidas = new ArrayList<Integer>();
		for (Integer idPergunta : mapaPerguntas.keySet()) {
			if (mapaPerguntas.get(idPergunta) == false) {
				perguntasNaoRespondidas.add(idPergunta);
			}
		}
		
		perguntaTurmaNaoRespondidas = new TreeSet<String>();
		
		for (Integer idPergunta : mapaRespostasDocenteTurma.keySet()) {
			Map<Integer, Boolean> mapaRespostas = mapaRespostasDocenteTurma.get(idPergunta);
			for (Integer idDocenteTurma : mapaRespostas.keySet()) {
				if (!mapaRespostas.get(idDocenteTurma)) {
					DocenteTurma docenteTurma = selectDocenteTurma(todosDocentesTurmas, idDocenteTurma);
					perguntaTurmaNaoRespondidas.add("P"+idPergunta+"T"+docenteTurma.getTurma().getId());
				}
			}
		}
		
		if (isEmpty(perguntasNaoRespondidas) && isEmpty(perguntaTurmaNaoRespondidas)) 
			return true;
		else 
			return false;
	}

	/** Verifica o preenchimento completo da Avaliação Institucional preenchido pelo docente.
	 * @return
	 */
	private boolean verificaPreenchimentoCompletoDocente() {
		// perguntas preenchidas por discentes
		Map<Integer, Integer> mapaPerguntas = new HashMap<Integer, Integer>();
		
		for (RespostaPergunta resposta : obj.getRespostas()) {
			
			if (mapaPerguntas.get(resposta.getPergunta().getId()) == null)
				mapaPerguntas.put(resposta.getPergunta().getId(), 0);
			
			mapaPerguntas.put(resposta.getPergunta().getId(), mapaPerguntas.get(resposta.getPergunta().getId()) + 1);
			
			if (resposta.getResposta() != null)
				mapaPerguntas.put(resposta.getPergunta().getId(), mapaPerguntas.get(resposta.getPergunta().getId()) - 1);
			
		}

		// Retirar da lista de todas as perguntas aquelas que foram respondidas
		perguntasNaoRespondidas = new ArrayList<Integer>();
		if (todasPerguntas != null)
			perguntasNaoRespondidas.addAll(todasPerguntas);

		@SuppressWarnings("unchecked") //SupressWarning necessário de acordo com a especificação do wiki
		Collection<Entry<Integer, Integer>> respondidasCompletamente = CollectionUtils.select(mapaPerguntas.entrySet(), new Predicate() {
			public boolean evaluate(Object obj) {
				Entry<Integer, Integer> entry = (Entry<Integer, Integer>) obj;
				return entry.getValue() == 0;
			}
		});
		
		for (Entry<Integer, Integer> entry : respondidasCompletamente) {
			perguntasNaoRespondidas.remove(entry.getKey());
		}
		
		if (isEmpty(perguntasNaoRespondidas)) return true;
		else return false;
	}

	/** Retorna um DocenteTurma de uma coleção.
	 * @param docentesTurma
	 * @param idDocenteTurma
	 * @return
	 */
	private DocenteTurma selectDocenteTurma(Collection<DocenteTurma> docentesTurma, int idDocenteTurma) {
		if (isEmpty(docentesTurma)) return null;
		for (DocenteTurma docenteTurma : docentesTurma)
			if (docenteTurma.getId() == idDocenteTurma)
				return docenteTurma;
		return null;
	}
	
	/** Indica se uma pergunta foi respondida para um docente turma
	 * @param idPergunta
	 * @param idDocenteTurma
	 * @return
	 */
	private boolean isPerguntaRespondida(int idPergunta, int idDocenteTurma) {
		for (RespostaPergunta resposta : obj.getRespostas()) {
			if (resposta.getPergunta().getId() == idPergunta
					&& resposta.getDocenteTurma() != null
					&& resposta.getDocenteTurma().getId() == idDocenteTurma
					&& resposta.getResposta() != null)
				return true;
		}
		return false;
	}
	
	/** Indica se uma pergunta foi respondida
	 * @param idPergunta
	 * @param idDocenteTurma
	 * @return
	 */
	private boolean isPerguntaRespondida(int idPergunta) {
		for (RespostaPergunta resposta : obj.getRespostas()) {
			if (resposta.getPergunta().getId() == idPergunta
					&& resposta.getDocenteTurma() == null
					&& resposta.getResposta() != null)
				return true;
		}
		return false;
	}

	/** Grava os dados da avaliação em banco.
	 * @param comando
	 * @throws Exception
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void gravarDados(Comando comando) throws Exception, ArqException, NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(obj);

		execute(mov);
	}
	
	/** Retorna o grupo de perguntas para o discente.
	 * @return Grupo de perguntas para o discente. 
	 * @throws DAOException
	 */
	public List<GrupoPerguntas> getGruposDiscente() throws DAOException {
		return obj.getFormulario().getGrupoPerguntas();
	}
	
	/** Retorna o grupo de perguntas para o docente. 
	 * @return Grupo de perguntas para o docente. 
	 * @throws DAOException
	 */
	public List<GrupoPerguntas> getGruposDocente() throws DAOException {
		return obj.getFormulario().getGrupoPerguntas();
	}
	
	/** Retorna o grupo de perguntas para o tutor de pólo EAD. 
	 * @return Grupo de perguntas para o tutor. 
	 * @throws DAOException
	 */
	public List<GrupoPerguntas> getGruposTutor() throws DAOException {
		return obj.getFormulario().getGrupoPerguntas();
	}
	
	/** Retorna a lista de docentes das turmas em que o discente está matriculado. 
	 * @return Lista de docentes das turmas em que o discente está matriculado. 
	 * @throws DAOException
	 */
	public List<DocenteTurma> getDocenteTurmasDiscente() throws DAOException {
		if (docenteTurmasDiscente == null) {
			docenteTurmasDiscente = getDAO(AvaliacaoInstitucionalDao.class).buscarTurmasComApenasUmDocenteNoSemestrePorDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		}
		return docenteTurmasDiscente;
	}
	
	/** Retorna a lista de turmas avaliadas.  
	 * @return Lista de turmas avaliadas. 
	 * @throws DAOException
	 */
	public List<Turma> getTurmasDiscenteComMaisDeUmDocente() throws DAOException {
		if (turmasDiscenteComMaisDeUmDocente == null)
			turmasDiscenteComMaisDeUmDocente = getDAO(AvaliacaoInstitucionalDao.class).buscarTurmasComMaisDeUmDocenteNoSemestrePorDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		return turmasDiscenteComMaisDeUmDocente;
	}
	
	/** Retorna a lista de turmas que o docente leciona. 
	 * @return Lista de turmas que o docente leciona. 
	 * @throws DAOException
	 */
	public List<DocenteTurma> getTurmasDocente() throws DAOException {
		if (turmasDocente == null) {
			if (getUsuarioLogado().getVinculoAtivo().getDocenteExterno() == null && getUsuarioLogado().getServidorAtivo() != null)
				turmasDocente = getDAO(TurmaDao.class).findTurmasSemestreByDocente(getUsuarioLogado().getServidorAtivo(), getAno(), getPeriodo(), formulario.isEad());
			else if (getUsuarioLogado().getVinculoAtivo().getDocenteExterno() != null)
				turmasDocente = getDAO(TurmaDao.class).findTurmasSemestreByDocenteExterno(getUsuarioLogado().getVinculoAtivo().getDocenteExterno(), getAno(), getPeriodo(), formulario.isEad());
			else turmasDocente = new ArrayList<DocenteTurma>(); 
			for(DocenteTurma dt: turmasDocente)
				dt.getTurma().getId();
			// caso as turmas sejam de EAD, agrupar turmas do mesmo pólo, por disciplina
			if (formulario != null && formulario.isAgrupaTurmas()) {
				Map<Integer, DocenteTurma> mapa = new HashMap<Integer, DocenteTurma>();
				for(DocenteTurma dt: turmasDocente) {
					// define valor genéricos para o código da turma, uma vez que serão agrupadas.
					dt.getTurma().setCodigo("--");
					mapa.put(dt.getTurma().getDisciplina().getId(), dt);
				}
				turmasDocente = br.ufrn.arq.util.CollectionUtils.toList(mapa.values());
			}
			// ordena as turmas
			Collections.sort(turmasDocente, new Comparator<DocenteTurma>() {
				@Override
				public int compare(DocenteTurma o1, DocenteTurma o2) {
					int comp = o1.getTurma().getDisciplina().getCodigo().compareTo(o2.getTurma().getDisciplina().getCodigo());
					if (comp == 0)
						comp = o1.getTurma().getCodigo().compareTo(o2.getTurma().getCodigo());
					return comp;
				}
			});
		}
		return turmasDocente;
	}

	/** Retorna a lista de turmas que o discente trancou. 
	 * @return Lista de turmas que o discente trancou. 
	 * @throws DAOException
	 */
	public List<Turma> getTrancamentosDiscente() throws DAOException {
		if (trancamentosDiscente == null)
			trancamentosDiscente = getDAO(AvaliacaoInstitucionalDao.class).findTrancamentosSemestreByDiscente(getUsuarioLogado().getDiscenteAtivo().getDiscente(), getAno(), getPeriodo());
		return trancamentosDiscente;
	}
	
	/** Carrega o mapa de notas da avaliação.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked") //SupressWarning necessário de acordo com a especificação do wiki
	private void carregarNotas() throws Exception {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<RespostaPergunta> respostas = new ArrayList<RespostaPergunta>();
		
		for (String key : req.keySet()) {
			if (key.startsWith("p_")) {
				
				String[] notas = key.split("_");
				Pergunta pergunta = getPergunta(Integer.parseInt(notas[1]));
				DocenteTurma dt = null;
				if (notas.length > 2)
					dt = new DocenteTurma(Integer.parseInt(notas[2])); 
//					dt = getGenericDAO().findByPrimaryKey(Integer.parseInt(notas[2]), DocenteTurma.class, "id");
				
				RespostaPergunta resp = new RespostaPergunta();
				resp.setAvaliacao(obj);
				resp.setPergunta(pergunta);
				resp.setDocenteTurma(dt); 

				// Se a pergunta for múltipla escolha, existem várias respostas para a mesma pergunta
				if (pergunta.isMultiplaEscolha()) {

					String[] values = getCurrentRequest().getParameterValues(key);
					for (String r : values) {
						RespostaPergunta rp = new RespostaPergunta();
						rp.setAvaliacao(obj);
						rp.setPergunta(pergunta);
						rp.setDocenteTurma(dt); 
						rp.setAvaliacao(obj);
						rp.setResposta(Integer.parseInt(r));
						carregaCitacao(req, pergunta, rp);
						respostas.add(rp);
					}
					
				} else {
					
					resp.setResposta(getParameterInt(key));
					if (pergunta.isEscolhaUnica())
						carregaCitacao(req, pergunta, resp);
					respostas.add(resp);
					
				}
									
			}			
		}
		
		if (isEmpty(respostas)) {
			obj.setRespostas(null);
		} else {
			obj.setRespostas(respostas);
		}
	}
	
	/** Busca nos grupos de perguntas e retorna a pergunta com o ID especificado. 
	 * @param idPergunta
	 * @return
	 * @throws DAOException 
	 */
	private Pergunta getPergunta(int idPergunta) throws DAOException {
		List<GrupoPerguntas> grupos;
		if (!isEmpty(obj.getDiscente()))
			grupos = getGruposDiscente();
		else if (!isEmpty(obj.getTutorOrientador())) 
			grupos = getGruposTutor();
		else 
			grupos = getGruposDocente();
		for (GrupoPerguntas grupo : grupos) {
			for (Pergunta pergunta : grupo.getPerguntasAtivas()) { 
				if (pergunta.getId() == idPergunta)
					return pergunta;
			}
		}
		return null;
	}
	
	/** Busca nas alternativas de resposta pela alternativa do ID expecificado.
	 * @param resposta
	 * @return
	 * @throws DAOException 
	 */
	private AlternativaPergunta getAlternativaPergunta(Integer idAlternativa) throws DAOException {
		List<GrupoPerguntas> grupos;
		if (!isEmpty(obj.getDiscente()))
			grupos = getGruposDiscente();
		else if (!isEmpty(obj.getTutorOrientador())) 
			grupos = getGruposTutor();
		else 
			grupos = getGruposDocente();
		for (GrupoPerguntas grupo : grupos) {
			for (Pergunta pergunta : grupo.getPerguntasAtivas()) { 
				if (!isEmpty(pergunta.getAlternativas()))
					for (AlternativaPergunta alternativa : pergunta.getAlternativas()) {
							if (alternativa.getId() == idAlternativa)
								return alternativa;
					}
			}
		}
		return null;
	}

	/** Carrega as observações da avaliação.
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked") //SupressWarning necessário de acordo com a especificação do wiki
	private void carregaObservacoes() throws DAOException {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<ObservacoesDocenteTurma> observacoes = new ArrayList<ObservacoesDocenteTurma>();
		
		if (obj.getDiscente() != null) {
			
			for (DocenteTurma dt : getDocenteTurmasDiscente()) {
				String obss[] = req.get("obs_" + dt.getId());
				String obsStr = null;
				if (obss != null) obsStr = obss[0];
				if (!isEmpty(obsStr)) {
					ObservacoesDocenteTurma obs = new ObservacoesDocenteTurma();
					obs.setAvaliacao(obj);
					obs.setDocenteTurma(dt);
					obs.setTurma(dt.getTurma());
					obs.setObservacoes(obsStr);
					observacoes.add(obs);
				}
			}
			
			for (Turma t : getTurmasDiscenteComMaisDeUmDocente()) {
				String opcoes[] = req.get("opcao_tmd_" + t.getId());
				String opcaoStr = null;
				if (opcoes != null) opcaoStr = opcoes[0];
				if (!isEmpty(opcaoStr)) {
					Integer opcaoInt = Integer.parseInt(opcaoStr);
					if (opcaoInt == -1) {
						String obsStr = req.get("obs_tmd_" + t.getId())[0];
						ObservacoesDocenteTurma obs = new ObservacoesDocenteTurma();
						obs.setAvaliacao(obj);
						obs.setTurma(t);
						obs.setObservacoes(obsStr);
						observacoes.add(obs);
					} else {
						String obsStr = req.get("obs_tmd_" + t.getId())[0];
						ObservacoesDocenteTurma obs = new ObservacoesDocenteTurma();
						obs.setAvaliacao(obj);
						obs.setDocenteTurma(new DocenteTurma(opcaoInt));
						obs.setTurma(t);
						obs.setObservacoes(obsStr);
						observacoes.add(obs);
					}
				}
			}		
		} else {
			for (DocenteTurma dt : getTurmasDocente()) {
				String obss[] = req.get("obs_" + dt.getId());
				String obsStr = null;
				if (obss != null) obsStr = obss[0];
				if (!isEmpty(obsStr)) {
					ObservacoesDocenteTurma obs = new ObservacoesDocenteTurma();
					obs.setAvaliacao(obj);
					obs.setDocenteTurma(dt);
					obs.setTurma(dt.getTurma());
					obs.setObservacoes(obsStr);
					observacoes.add(obs);
				}
			}
		}
		if (isEmpty(observacoes)) {
			obj.setObservacoesDocenteTurma(null);
		} else {
			obj.setObservacoesDocenteTurma(observacoes);
		}
	}
	
	/** Carrega a lista de trancamentos do discente.
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked") //SupressWarning necessário de acordo com a especificação do wiki
	private void carregaTrancamentos() throws DAOException {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<ObservacoesTrancamento> trancamentos = new ArrayList<ObservacoesTrancamento>();
		
		for (String key : req.keySet()) {
			if (key.startsWith("tr_")) {
				
				String[] notas = key.split("_");
				Turma turma = new Turma(Integer.parseInt(notas[1]));
//				Turma turma = getGenericDAO().findByPrimaryKey(Integer.parseInt(notas[1]), Turma.class);
				
				ObservacoesTrancamento ot = new ObservacoesTrancamento();
				ot.setAvaliacao(obj);
				ot.setTurma(turma);
				ot.setObservacoes(getParameter(key));
				trancamentos.add(ot);
			}
		}
		
		if (isEmpty(trancamentos)) {
			obj.setTrancamentos(null);
		} else {
			obj.setTrancamentos(trancamentos);
		}
	}
	
	/**
	 * Carrega uma citação, caso seja uma pergunta de múltipla escolha ou única
	 * escolha e o usuário tenha selecionado o item "outro".
	 * 
	 * @param req
	 * @param pergunta
	 * @param resp
	 * @throws DAOException
	 */
	private void carregaCitacao(Map<String, String[]> req, Pergunta pergunta, RespostaPergunta resp) throws DAOException {
//		getGenericDAO().findByPrimaryKey(resp.getResposta(), AlternativaPergunta.class)
		AlternativaPergunta alternativa = getAlternativaPergunta(resp.getResposta());
		if (alternativa.isPermiteCitacao()) {
			resp.setCitacao((req.get("c_" + pergunta.getId()))[0]);
		}
	}

	/** Retorna a lista de IDs de perguntas não respondidas. 
	 * @return Lista de IDs de perguntas não respondidas. 
	 */
	public Integer[] getPerguntasNaoRespondidas() {
		if (perguntasNaoRespondidas == null) return null;
		return perguntasNaoRespondidas.toArray(new Integer[perguntasNaoRespondidas.size()]);
	}
	
	/** Indica se o discente está apto para avaliar.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws DAOException
	 */
	public boolean discentePodeAvaliar() throws DAOException {
		boolean pode = !isEmpty(getDocenteTurmasDiscente()) || !isEmpty(getTurmasDiscenteComMaisDeUmDocente());
		return pode;
	}
	
	/** Indica se o docente está apto para avaliar.
	 * @return
	 * @throws DAOException
	 */
	private boolean docentePodeAvaliar() throws DAOException {
		// verifica o calendário 
		if (isEmpty(getTurmasDocente())) {
			addMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_INDISPONIVEL, "você não está lecionado turmas "+ (formulario != null && formulario.isEad() ? "à distância":"presenciais") +" na graduação em " + getAno()+"."+getPeriodo());
			return false;
		} else {
				return true;
		}
	}
	
	/** Retorna o ano da Avaliação Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getAno() throws DAOException {
		return ano;
	}
	
	/** Retorna o período da Avaliação Institucional 
	 * @return
	 * @throws DAOException
	 */
	public int getPeriodo() throws DAOException {
		return periodo;
	}

	/** Verifica se o acesso se dá por usuário discente.
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoDiscente() throws NegocioException {
		if (obj == null) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.ACESSO_INDIRETO_AVALIACAO, "Por favor, utilize os links do Portal Discente.").getMensagem());
		}
		return null;
	}
	
	/** Verifica se o acesso se dá por usuário docente.
	 * @return
	 * @throws NegocioException
	 */
	public String getVerificaAcessoDocente() throws NegocioException {
		if (obj == null) {
			throw new NegocioException(UFRNUtils.getMensagem(MensagensAvaliacaoInstitucional.ACESSO_INDIRETO_AVALIACAO, "Por favor, utilize os links do Portal Docente.").getMensagem());
		}
		return null;
	}
	
	/** Inicializa os atributos deste controller. */
	private void init() {
		this.docenteTurmasDiscente = null;
		this.perguntasNaoRespondidas = null;
		this.perguntaTurmaNaoRespondidas = null;
		this.trancamentosDiscente = null;
		this.turmasDiscenteComMaisDeUmDocente = null;
		this.turmasDocente = null;
	}

	/** Retorna a lista de IDs de perguntas não respondidas. 
	 * @return Lista de IDs de perguntas não respondidas. 
	 */
	public String[] getPerguntaTurmaNaoRespondidas() {
		if (perguntaTurmaNaoRespondidas == null) return null;
		return perguntaTurmaNaoRespondidas.toArray(new String[perguntaTurmaNaoRespondidas.size()]);
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public void setFormulario(FormularioAvaliacaoInstitucional formulario) {
		this.formulario = formulario;
	}

	public FormularioAvaliacaoInstitucional getFormulario() {
		return formulario;
	}

	public String getResolucao() {
		return ParametroHelper.getInstance().getParametro(ParametrosAvaliacaoInstitucional.RESOLUCAO_REGULAMENTADORA);
	}
}
