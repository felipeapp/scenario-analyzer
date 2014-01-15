/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Controller respons�vel pelo preenchimento do formul�rio de Avalia��o
 * Institucional.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("formularioAvaliacaoInstitucionalBean")
@Scope("session")
public class FormularioAvaliacaoInstitucionalMBean extends SigaaAbstractController<FormularioAvaliacaoInstitucional> {

	/** Avalia��o Institucional a ser preenchida. */
	private AvaliacaoInstitucional avaliacao;
	/** Lista de trancamentos realizados pelo discente. */
	private List<Turma> trancamentosDiscente;
	/** Cole��o de turmas de doc�ncia assistida a serem avaliadas. */
	private Collection<TurmaDocenciaAssistida> turmasDocenciaAssistida;
	/** Lista de IDs de perguntas n�o respondidas. */
	private Map<Integer, List<Integer>> perguntasNaoRespondidas;
	/** Ano referente � avalia��o. */
	private int ano;
	/** Per�odo referente � avalia��o. */
	private int periodo;

	/** Inicia o preenchimento da avalia��o institucional.<br/>M�todo n�o invocado por JSP�s.
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
			addMensagemErro("Voc� n�o possui turmas com bolsista de Doc�ncia Assistida para poder realizar a Avalia��o.");
			return null;
		}

		// Verifica se existem avalia��es j� salvas
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

		if (avaliacao == null) { // N�o existiam avalia��es anteriores. Criar
									// uma nova
			avaliacao = new AvaliacaoInstitucional();
			avaliacao.setAno(ano);
			avaliacao.setPeriodo(periodo);
			avaliacao.setFormulario(obj);
			avaliacao.setRespostas(new ArrayList<RespostaPergunta>());
			avaliacao.setFinalizada(false);
		} else if (avaliacao.isFinalizada()) { // N�o deixar avaliar novamente
												// se a avalia��o estiver
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
	
	/** Inicia o preenchimento da avalia��o institucional.<br/>M�todo n�o invocado por JSP�s.
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

	/** Inicia os dados do MBean que controla o formul�rio de Avalia��o Institucional.
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
	
	/** Inicia o preenchimento da avalia��o institucional.<br/>M�todo n�o invocado por JSP�s.
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
	
	/** Inicia o preenchimento da avalia��o institucional.<br/>M�todo n�o invocado por JSP�s.
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
	 * Salvar os dados digitados na avalia��o sem finalizar a mesma.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
					"Avalia��o Institucional");
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}

		return null;
	}

	/**
	 * Salva os dados digitados e grava a avalia��o como finalizada.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
			// SupressWarning necess�rio de acordo com a especifica��o do wiki
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

		// S� continua se a avalia��o tiver sido respondida completamente
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
					"Avalia��o Institucional");
		}

		return cancelar();
	}

	/**
	 * Grava os dados da avalia��o em banco.
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
	 * Preenche a lista de IDs de todas perguntas. M�todo n�o chamado por JSP.
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
	// SupressWarning necess�rio de acordo com a especifica��o do wiki
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
	 * Carrega as observa��es da avalia��o.
	 * 
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	// SupressWarning necess�rio de acordo com a especifica��o do wiki
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
	 * Carrega o mapa de notas da avalia��o.
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	// SupressWarning necess�rio de acordo com a especifica��o do wiki
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

				// Se a pergunta for m�ltipla escolha, existem v�rias respostas
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
	 * Carrega uma cita��o, caso seja uma pergunta de m�ltipla escolha ou �nica
	 * escolha e o usu�rio tenha selecionado o item "outro".
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

	/** Retorna a cole��o de turmas com apenas um bolsista.
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

	/** Retorna a cole��o de turmas com mais de um bolsista.
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

	/** Retorna a cole��o de turmas com bolsistas de doc�ncia assistida.
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
	 * Retorna a lista de IDs de perguntas n�o respondidas.
	 * 
	 * @return Lista de IDs de perguntas n�o respondidas.
	 */
	public Map<Integer, List<Integer>> getPerguntasNaoRespondidas() {
		if (perguntasNaoRespondidas == null)
			perguntasNaoRespondidas = new HashMap<Integer, List<Integer>>();
		return perguntasNaoRespondidas;
	}

	/** Retorna o per�odo referente � avalia��o. 
	 * @return
	 */
	private int getPeriodo() {
		return periodo;
	}

	/** Retorna o ano referente � avalia��o.
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

	/** Retorna a avalia��o Institucional a ser preenchida. 
	 * @return
	 */
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	/** Seta a avalia��o Institucional a ser preenchida.
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