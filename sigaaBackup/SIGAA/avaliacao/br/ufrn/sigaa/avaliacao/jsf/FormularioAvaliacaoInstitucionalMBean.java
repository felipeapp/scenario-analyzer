/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 13/09/2010
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;
import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.AlternativaPergunta;
import br.ufrn.sigaa.avaliacao.dominio.AvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenciaAssistida;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.RespostaPergunta;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.TurmaDocenciaAssistidaDao;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.PlanoDocenciaAssistida;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;
import br.ufrn.sigaa.mensagens.MensagensAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller responsável pelo preenchimento do formulário de Avaliação
 * Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("formularioAvaliacaoInstitucionalBean")
@Scope("session")
public class FormularioAvaliacaoInstitucionalMBean extends SigaaAbstractController<FormularioAvaliacaoInstitucional> {

	/** Avaliação Institucional a ser preenchida. */
	private AvaliacaoInstitucional avaliacao;
	/** Lista de trancamentos realizados pelo discente. */
	private List<Turma> trancamentosDiscente;
	/** Coleção de turmas de docência assistida a serem avaliadas. */
	private Collection<TurmaDocenciaAssistida> turmasDocenciaAssistida;
	/** Lista de IDs de perguntas não respondidas. */
	private Map<Integer, List<Integer>> perguntasNaoRespondidas;
	/** Ano referente à avaliação. */
	private int ano;
	/** Período referente à avaliação. */
	private int periodo;

	/** Inicia o preenchimento da avaliação institucional.<br/>Método não invocado por JSP´s.
	 * @param formulario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarDocenciaAssistida(FormularioAvaliacaoInstitucional formulario, int ano, int periodo)
			throws ArqException, NegocioException {
		init();
		this.ano = ano;
		this.periodo = periodo;
		obj = formulario;
		if (obj == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}

		if (isEmpty(getTurmasDocenciaAssistida())) {
			addMensagemErro("Você não possui turmas com bolsista de Docência Assistida para poder realizar a Avaliação.");
			return null;
		}

		// Verifica se existem avaliações já salvas
		Discente discente = getUsuarioLogado().getDiscenteAtivo() != null ? getUsuarioLogado()
				.getDiscenteAtivo().getDiscente()
				: null;
		Servidor servidor = getUsuarioLogado().getServidorAtivo();
		DocenteExterno docenteExterno = getUsuarioLogado().getVinculoAtivo()
				.getDocenteExterno();
		if (getAcessoMenu().isDiscente())
			avaliacao = getDAO(AvaliacaoInstitucionalDao.class).findByDiscente(
					discente, getAno(), getPeriodo(), obj);
		else if (docenteExterno == null)
			avaliacao = getDAO(AvaliacaoInstitucionalDao.class).findByDocente(
					servidor, getAno(), getPeriodo(), obj);
		else
			avaliacao = getDAO(AvaliacaoInstitucionalDao.class)
					.findByDocenteExterno(docenteExterno, getAno(),
							getPeriodo(), obj);

		if (avaliacao == null) { // Não existiam avaliações anteriores. Criar
									// uma nova
			avaliacao = new AvaliacaoInstitucional();
			avaliacao.setAno(ano);
			avaliacao.setPeriodo(periodo);
			avaliacao.setFormulario(obj);
			avaliacao.setRespostas(new ArrayList<RespostaPergunta>());
			avaliacao.setFinalizada(false);
		} else if (avaliacao.isFinalizada()) { // Não deixar avaliar novamente
												// se a avaliação estiver
												// finalizada.
			addMensagem(MensagensAvaliacaoInstitucional.AVALIACAO_REALIZADA);
			return null;
		}

		if (getAcessoMenu().isDiscente())
			avaliacao.setDiscente(discente);
		else if (docenteExterno == null)
			avaliacao.setServidor(servidor);
		else
			avaliacao.setDocenteExterno(docenteExterno);
		
		// Filtra o grupo de perguntas por perfil (discente/docente)
		Iterator<GrupoPerguntas> iterator = obj.getGrupoPerguntas().iterator();
		while (iterator.hasNext()) {
			GrupoPerguntas grupo = iterator.next();
			if (isPortalDiscente() && !grupo.isDiscente() || isPortalDocente()
					&& grupo.isDiscente())
				iterator.remove();
		}

		// evitando lazy exception
		if (avaliacao.getTrancamentos() != null)
			avaliacao.getTrancamentos().iterator();
		if (avaliacao.getObservacoesDocenteTurma() != null)
			avaliacao.getObservacoesDocenteTurma().iterator();
		if (avaliacao.getObservacoesDocenciaAssistida() != null)
			avaliacao.getObservacoesDocenciaAssistida().iterator();
		for (GrupoPerguntas grupo : obj.getGrupoPerguntas()) {
			for (Pergunta pergunta : grupo.getPerguntasAtivas()) {
				if (pergunta.getAlternativas() != null)
					pergunta.getAlternativas().iterator();
			}
		}

		prepareMovimento(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
		return forward("/avaliacao/docencia_assistida.jsp");
	}
	
	/** Inicia o preenchimento da avaliação institucional.<br/>Método não invocado por JSP´s.
	 * @param formulario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarAvaliacaoDocente(FormularioAvaliacaoInstitucional formulario, int ano, int periodo) throws ArqException, NegocioException {
		AvaliacaoInstitucionalMBean mBean = iniciarAvaliacao(formulario, ano, periodo);
		return mBean.iniciarDocente();
	}

	/** Inicia os dados do MBean que controla o formulário de Avaliação Institucional.
	 * @param formulario
	 * @param ano
	 * @param periodo
	 * @return
	 */
	private AvaliacaoInstitucionalMBean iniciarAvaliacao(FormularioAvaliacaoInstitucional formulario, int ano, int periodo) {
		init();
		this.ano = ano;
		this.periodo = periodo;
		if (formulario == null) {
			addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			return null;
		}
		obj = formulario;

		AvaliacaoInstitucionalMBean mBean = getMBean("avaliacaoInstitucional");
		mBean.setAno(ano);
		mBean.setPeriodo(periodo);
		mBean.setFormulario(obj);
		return mBean;
	}
	
	/** Inicia o preenchimento da avaliação institucional.<br/>Método não invocado por JSP´s.
	 * @param formulario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarAvaliacaoDiscente(FormularioAvaliacaoInstitucional formulario, int ano, int periodo) throws ArqException, NegocioException {
		AvaliacaoInstitucionalMBean mBean = iniciarAvaliacao(formulario, ano, periodo);
		return mBean.iniciarDiscente();
	}
	
	/** Inicia o preenchimento da avaliação institucional.<br/>Método não invocado por JSP´s.
	 * @param formulario
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarAvaliacaoTutor(FormularioAvaliacaoInstitucional formulario, int ano, int periodo) throws ArqException, NegocioException {
		AvaliacaoInstitucionalMBean mBean = iniciarAvaliacao(formulario, ano, periodo);
		return mBean.iniciarTutor();
	}

	/**
	 * Salvar os dados digitados na avaliação sem finalizar a mesma.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/docencia_assistida.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String salvar() {
		try {
			avaliacao.setFinalizada(false);

			carregarNotas();
			carregaTrancamentos();
			carregaObservacoes();

			prepareMovimento(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			gravarDados(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,
					"Avaliação Institucional");
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
	 * <li>/avaliacao/docencia_assistida.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception
	 */
	public String finalizar() throws Exception {
		boolean completa = false;
		carregarNotas();
		carregaTrancamentos();
		carregaObservacoes();

		// Verificar se todas as perguntas foram respondidas
		perguntasNaoRespondidas = new HashMap<Integer, List<Integer>>();
		for (TurmaDocenciaAssistida turma : getTurmasDocenciaAssistida()) {
			Map<Integer, Integer> mapaPerguntas = new HashMap<Integer, Integer>();
			List<Integer> subPerguntasNaoRespondidas = new ArrayList<Integer>();
			for (Integer idPergunta : getTodasPerguntas(obj.getGrupoPerguntas())) {
				if (mapaPerguntas.get(idPergunta) == null)
					mapaPerguntas.put(idPergunta, 0);
				
				mapaPerguntas.put(idPergunta, mapaPerguntas.get(idPergunta) + 1);

				for (RespostaPergunta resposta : avaliacao.getRespostas()) {
					if (resposta.getPergunta().getId() == idPergunta
							&& resposta.getTurmaDocenciaAssistida().getId() == turma.getId()
							&& resposta.getResposta() != null)
						mapaPerguntas.put(idPergunta, mapaPerguntas.get(idPergunta) - 1);
				}
			}
			// Retirar da lista de todas as perguntas aquelas que foram respondidas
			List<Integer> todasPerguntas = getTodasPerguntas(obj
					.getGrupoPerguntas());
			if (todasPerguntas != null)
				subPerguntasNaoRespondidas.addAll(todasPerguntas);
			
			@SuppressWarnings("unchecked")
			// SupressWarning necessário de acordo com a especificação do wiki
			Collection<Entry<Integer, Integer>> respondidasCompletamente = CollectionUtils
			.select(mapaPerguntas.entrySet(), new Predicate() {
				public boolean evaluate(Object obj) {
					Entry<Integer, Integer> entry = (Entry<Integer, Integer>) obj;
					return entry.getValue() == 0;
				}
			});
			
			for (Entry<Integer, Integer> entry : respondidasCompletamente) {
				subPerguntasNaoRespondidas.remove(entry.getKey());
			}
			if (!isEmpty(subPerguntasNaoRespondidas)) {
				perguntasNaoRespondidas.put(turma.getId(), subPerguntasNaoRespondidas);
			}
		}

		if (isEmpty(perguntasNaoRespondidas))
			completa = true;

		// Só continua se a avaliação tiver sido respondida completamente
		if (!completa) {
			addMensagem(MensagensAvaliacaoInstitucional.PERGUNTAS_NAO_RESPONDIDAS);
			avaliacao.setFinalizada(false);
			prepareMovimento(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			gravarDados(SigaaListaComando.SALVAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			return null;
		} else {
			avaliacao.setFinalizada(true);
			gravarDados(SigaaListaComando.FINALIZAR_AVALIACAO_INSTITUCIONAL_DISCENTE);
			addMensagem(MensagensArquitetura.CADASTRADO_COM_SUCESSO,
					"Avaliação Institucional");
		}

		return cancelar();
	}

	/**
	 * Grava os dados da avaliação em banco.
	 * 
	 * @param comando
	 * @throws Exception
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void gravarDados(Comando comando) throws Exception, ArqException,
			NegocioException {
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(comando);
		mov.setObjMovimentado(avaliacao);
		execute(mov);
	}

	/**
	 * Preenche a lista de IDs de todas perguntas. Método não chamado por JSP.
	 * 
	 * @param grupos
	 * @return
	 * @throws DAOException
	 */
	private List<Integer> getTodasPerguntas(Collection<GrupoPerguntas> grupos)
			throws DAOException {
		List<Integer> todasPerguntas = new ArrayList<Integer>();
		for (GrupoPerguntas grupo : grupos) {
			for (Pergunta pergunta : grupo.getPerguntasAtivas()) {
				if (pergunta.getAlternativas() != null)
					pergunta.getAlternativas().iterator();
				todasPerguntas.add(pergunta.getId());
			}
		}
		return todasPerguntas;
	}

	/**
	 * Carrega a lista de trancamentos do discente.
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	// SupressWarning necessário de acordo com a especificação do wiki
	private void carregaTrancamentos() throws DAOException {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<ObservacoesTrancamento> trancamentos = new ArrayList<ObservacoesTrancamento>();

		for (String key : req.keySet()) {
			if (key.startsWith("tr_")) {

				String[] notas = key.split("_");
				Turma turma = getGenericDAO().findByPrimaryKey(
						Integer.parseInt(notas[1]), Turma.class);

				ObservacoesTrancamento ot = new ObservacoesTrancamento();
				ot.setAvaliacao(avaliacao);
				ot.setTurma(turma);
				ot.setObservacoes(getParameter(key));
				trancamentos.add(ot);
			}
		}

		if (isEmpty(trancamentos)) {
			avaliacao.setTrancamentos(null);
		} else {
			avaliacao.setTrancamentos(trancamentos);
		}
	}

	/**
	 * Carrega as observações da avaliação.
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	// SupressWarning necessário de acordo com a especificação do wiki
	private void carregaObservacoes() throws DAOException {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<ObservacoesDocenciaAssistida> observacoes = new ArrayList<ObservacoesDocenciaAssistida>();

		for (TurmaDocenciaAssistida t : getTurmasDocenciaAssistida()) {
			String obss[] = req.get("obs_" + t.getId());
			String obsStr = null;
			if (obss != null)
				obsStr = obss[0];
			if (!isEmpty(obsStr)) {
				ObservacoesDocenciaAssistida obs = new ObservacoesDocenciaAssistida();
				obs.setAvaliacao(avaliacao);
				obs.setTurmaDocenciaAssistida(t);
				obs.setObservacoes(obsStr);
				observacoes.add(obs);
			}
		}
		if (isEmpty(observacoes)) {
			avaliacao.setObservacoesDocenciaAssistida(null);
		} else {
			avaliacao.setObservacoesDocenciaAssistida(observacoes);
		}
	}

	/**
	 * Carrega o mapa de notas da avaliação.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	// SupressWarning necessário de acordo com a especificação do wiki
	private void carregarNotas() throws Exception {
		Map<String, String[]> req = getCurrentRequest().getParameterMap();
		List<RespostaPergunta> respostas = new ArrayList<RespostaPergunta>();

		for (String key : req.keySet()) {
			if (key.startsWith("p_")) {

				String[] notas = key.split("_");
				Pergunta pergunta = getGenericDAO().findByPrimaryKey(
						Integer.parseInt(notas[1]), Pergunta.class);
				DocenteTurma dt = null;
				TurmaDocenciaAssistida tda = null;
				if (notas.length > 2) {
					if (obj.isAvaliacaoDocenciaAssistida()) {
						tda = getGenericDAO().findByPrimaryKey(
								Integer.parseInt(notas[2]),
								TurmaDocenciaAssistida.class);
					} else {
						dt = getGenericDAO().findByPrimaryKey(Integer.parseInt(notas[2]), DocenteTurma.class, "id");
					}
				}

				RespostaPergunta resp = new RespostaPergunta();
				resp.setAvaliacao(avaliacao);
				resp.setPergunta(pergunta);
				resp.setDocenteTurma(dt);
				resp.setTurmaDocenciaAssistida(tda);

				// Se a pergunta for múltipla escolha, existem várias respostas
				// para a mesma pergunta
				if (pergunta.isMultiplaEscolha()) {

					String[] values = getCurrentRequest().getParameterValues(
							key);
					for (String r : values) {
						RespostaPergunta rp = new RespostaPergunta();
						rp.setAvaliacao(avaliacao);
						rp.setPergunta(pergunta);
						rp.setDocenteTurma(dt);
						resp.setTurmaDocenciaAssistida(tda);
						rp.setAvaliacao(avaliacao);
						rp.setResposta(Integer.parseInt(r));
						carregaCitacao(req, pergunta, rp, tda);
						respostas.add(rp);
					}

				} else {

					resp.setResposta(getParameterInt(key));
					if (pergunta.isEscolhaUnica())
						carregaCitacao(req, pergunta, resp, tda);
					respostas.add(resp);

				}

			}
		}

		if (isEmpty(respostas)) {
			avaliacao.setRespostas(null);
		} else {
			avaliacao.setRespostas(respostas);
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
	private void carregaCitacao(Map<String, String[]> req, Pergunta pergunta,
			RespostaPergunta resp, TurmaDocenciaAssistida tda)
			throws DAOException {
		AlternativaPergunta alternativa = getGenericDAO().findByPrimaryKey(
				resp.getResposta(), AlternativaPergunta.class);
		if (alternativa.isPermiteCitacao()) {
			String[] resposta;
			if (tda == null)
				resposta = req.get("c_" + pergunta.getId());
			else
				resposta = req.get("c_" + pergunta.getId() + "_"
						+ tda.getId());
			resp.setCitacao(resposta[0]);
		}
	}

	/** Retorna a coleção de turmas com apenas um bolsista.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<TurmaDocenciaAssistida> getTurmasComUmDocente()
			throws HibernateException, DAOException {
		Collection<TurmaDocenciaAssistida> subGrupo = new ArrayList<TurmaDocenciaAssistida>();
		if (getTurmasDocenciaAssistida() != null) {
			for (TurmaDocenciaAssistida tda : getTurmasDocenciaAssistida()) {
				boolean duplicado = false;
				for (TurmaDocenciaAssistida outraTda : getTurmasDocenciaAssistida()) {
					if (tda.getId() != outraTda.getId()
							&& tda.getTurma().getId() == outraTda.getTurma()
									.getId()) {
						duplicado = true;
						break;
					}
				}
				if (!duplicado) {
					subGrupo.add(tda);
				}
			}
		}
		return subGrupo;
	}

	/** Retorna a coleção de turmas com mais de um bolsista.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Turma, List<TurmaDocenciaAssistida>> getTurmasComMaisDeUmDocente()
			throws HibernateException, DAOException {
		Map<Turma, List<TurmaDocenciaAssistida>> subGrupo = new HashMap<Turma, List<TurmaDocenciaAssistida>>();
		if (getTurmasDocenciaAssistida() != null) {
			for (TurmaDocenciaAssistida tda : getTurmasDocenciaAssistida()) {
				boolean duplicado = false;
				for (TurmaDocenciaAssistida outraTda : getTurmasDocenciaAssistida()) {
					if (tda.getId() != outraTda.getId()
							&& tda.getTurma().getId() == outraTda.getTurma()
									.getId()) {
						duplicado = true;
						break;
					}
				}
				if (duplicado) {
					List<TurmaDocenciaAssistida> lista = subGrupo.get(tda
							.getTurma());
					if (lista == null)
						lista = new ArrayList<TurmaDocenciaAssistida>();
					lista.add(tda);
					subGrupo.put(tda.getTurma(), lista);
				}
			}
		}
		return subGrupo;
	}

	/** Retorna a coleção de turmas com bolsistas de docência assistida.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<TurmaDocenciaAssistida> getTurmasDocenciaAssistida()
			throws HibernateException, DAOException {
		int status[] = {PlanoDocenciaAssistida.APROVADO,
				PlanoDocenciaAssistida.CONCLUIDO,
				PlanoDocenciaAssistida.ANALISE_RELATORIO,
				PlanoDocenciaAssistida.SOLICITADO_ALTERACAO_RELATORIO};
		if (turmasDocenciaAssistida == null) {
			if (getAcessoMenu().isDiscente())
				turmasDocenciaAssistida = getDAO(TurmaDocenciaAssistidaDao.class)
						.findTurmasDocenciaAssistidaByDiscenteMatriculado(
								getDiscenteUsuario().getId(),
								getAno(), getPeriodo(),
								status);
			else if (getUsuarioLogado().getVinculoAtivo().isVinculoDocenteExterno())
				turmasDocenciaAssistida = getDAO(TurmaDocenciaAssistidaDao.class)
						.findTurmasDocenciaAssistidaByDocenteExterno(
								getUsuarioLogado().getDocenteExternoAtivo().getId(),
								getAno(), getPeriodo(),
								status);
			else
				turmasDocenciaAssistida = getDAO(TurmaDocenciaAssistidaDao.class)
						.findTurmasDocenciaAssistidaByDocente(
								getServidorUsuario().getId(),
								getAno(), getPeriodo(),
								status);
		}
		return turmasDocenciaAssistida;
	}

	/**
	 * Retorna a lista de turmas que o discente trancou.
	 * 
	 * @return Lista de turmas que o discente trancou.
	 * @throws DAOException
	 */
	public List<Turma> getTrancamentosDiscente() throws DAOException {
		if (trancamentosDiscente == null && isPortalDiscente())
			trancamentosDiscente = getDAO(AvaliacaoInstitucionalDao.class)
					.findTrancamentosSemestreByDiscente(
							getUsuarioLogado().getDiscenteAtivo().getDiscente(),
							getAno(), getPeriodo());
		if (trancamentosDiscente == null)
			trancamentosDiscente = new ArrayList<Turma>();
		return trancamentosDiscente;
	}

	/**
	 * Retorna o grupo de perguntas para o discente.
	 * 
	 * @return Grupo de perguntas para o discente.
	 * @throws DAOException
	 */
	public Collection<GrupoPerguntas> getGrupos() throws DAOException {
		return obj.getGrupoPerguntas();
	}

	/**
	 * Retorna a lista de IDs de perguntas não respondidas.
	 * 
	 * @return Lista de IDs de perguntas não respondidas.
	 */
	public Map<Integer, List<Integer>> getPerguntasNaoRespondidas() {
		if (perguntasNaoRespondidas == null)
			perguntasNaoRespondidas = new HashMap<Integer, List<Integer>>();
		return perguntasNaoRespondidas;
	}

	/** Retorna o período referente à avaliação. 
	 * @return
	 */
	private int getPeriodo() {
		return periodo;
	}

	/** Retorna o ano referente à avaliação.
	 * @return
	 */
	private int getAno() {
		return ano;
	}

	/** Inicializa os atributos do objeto. */
	private void init() {
		this.turmasDocenciaAssistida = null;
		this.avaliacao = null;
		this.trancamentosDiscente = null;
		this.perguntasNaoRespondidas = null;
	}

	/** Retorna a avaliação Institucional a ser preenchida. 
	 * @return
	 */
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	/** Seta a avaliação Institucional a ser preenchida.
	 * @param avaliacao
	 */
	public void setAvaliacao(AvaliacaoInstitucional avaliacao) {
		this.avaliacao = avaliacao;
	}

	/** Seta a lista de trancamentos realizados pelo discente. 
	 * @param trancamentosDiscente
	 */
	public void setTrancamentosDiscente(List<Turma> trancamentosDiscente) {
		this.trancamentosDiscente = trancamentosDiscente;
	}
}