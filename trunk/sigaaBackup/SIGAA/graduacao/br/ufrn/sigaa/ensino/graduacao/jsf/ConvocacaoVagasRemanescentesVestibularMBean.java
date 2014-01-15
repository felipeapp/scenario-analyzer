/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 16/12/2010
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMinValue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.TemplateDocumentoDao;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.graduacao.ConvocacaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.negocio.MovimentoConvocacaoVestibular;
import br.ufrn.sigaa.ensino.graduacao.negocio.vestibular.EstrategiaConvocacaoCandidatosVestibular;
import br.ufrn.sigaa.mensagens.TemplatesDocumentos;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoPessoaConvocacao;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;

/**
 * Controlador responsável por gerar as convocações dos candidatos aprovados no
 * vestibular para as vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
@Component("convocacaoVagasRemanescentesVestibularMBean") @Scope("session")
public class ConvocacaoVagasRemanescentesVestibularMBean extends SigaaAbstractController<ConvocacaoProcessoSeletivo> {

	/** Lista de novas convocações de candidatos. */
	private List<ConvocacaoProcessoSeletivoDiscente> convocacoes;
	/** Lista de cancelamentos de convocações anteriores gerados por reconvocações de candidatos. */
	private List<CancelamentoConvocacao> cancelamentos;
	/** Lista de processos seletivos para o usuário escolher em qual será importado os dados. */
	private List<SelectItem> processosCombo;
	/** Indica que não serão cancelados os discentes que não passarem de pré-cadastro para cadastrado.*/
	private boolean naoGerarCancelamento;
	/** Indica que as vagas remanescentes de cotistas serão preenchidas por candidatos PRÉ-CADASTRADOS de ampla concorrência. */
	private boolean preencherVagasCotistas;

	/**
	 * Mapa contendo a oferta de curso com a respectiva quantidade de vagas remanescente.
	 */
	private Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes;
	
	/**
	 * Mapa contendo a oferta de curso com vaga remanescente e a sua
	 * respectiva quantidade de vagas a convocar.
	 */
	private Map<OfertaVagasCurso, Integer> ofertaComVagasAConvocar;
	
	/**
	 * Mapa contendo o ID de cada {@link MatrizCurricular matriz} ofertada para
	 * entrada no primeiro semestre do {@link ProcessoSeletivoVestibular
	 * vestibular} selecionado e a sua respectiva quantidade de vagas
	 * remanescentes.
	 */
	
	/** Resumo dos erros de validações para as convocações de processo seletivo, não sendo convocados. */
	private Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> errosConvocacao;
	/** Mapa de cotas com vagas não preenchidas. */
	private Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes;
	/** Mapa de cotas com quantidade de vagas a convocar. */
	private Map<CotaOfertaVagaCurso, Integer> cotasComVagasAConvocar;
	/** Quantidade de matrizes curriculares processadas */
	private int quantidadeProcessado;
	/** Lista de matrizes curriculares a processar. */
	private ArrayList<MatrizCurricular> matrizes;
	/** Lista de ofertas de vagas por curso a operar. */
	private List<OfertaVagasCurso> listaOfertaVagasCurso;
	/** Grupos de cotas para o nível de ensino. */
	private Collection<GrupoCotaVagaCurso> gruposCotas;
	/** Indica se aplica o percentual de aumento de vagas aos cotistas. */
	private boolean aplicaPercentualCotistas;
	/** Indica se na contagem de vagas remanescentes ignora os discentes com status pré cadastrado. */
	private boolean incluirPreCadastrado;
	/** Indica se na contagem de vagas remanescentes ignora os discentes com status prendente de cadastrado. */
	private boolean incluirPendenteCadastro;
	/** Coleçao de discentes com status PENDENTE DE CADASTRO que serão excluídos. */
	private Collection<DiscenteGraduacao> discenteExcluidos;

	/**
	 * Construtor padrão.
	 */
	public ConvocacaoVagasRemanescentesVestibularMBean() {
		clear();
	}

	/**
	 * Inicializa as informações utilizadas em todo o caso de uso.
	 */
	private void clear() {
		obj = new ConvocacaoProcessoSeletivo();
		obj.setSemestreConvocacao(SemestreConvocacao.CONVOCA_TODOS_SEMESTRES);
		naoGerarCancelamento = false;
		incluirPendenteCadastro = true;
		incluirPreCadastrado = true;
		aplicaPercentualCotistas = false;
		convocacoes = null;
		cancelamentos = null;
		processosCombo = null;
		ofertaComVagasRemanescentes = null;
		ofertaComVagasAConvocar = null;
		errosConvocacao  = null;
		cotasComVagasRemanescentes  = null;
		cotasComVagasAConvocar  = null;
		quantidadeProcessado = 0;
		matrizes  = null;
		listaOfertaVagasCurso  = null;
		gruposCotas  = null;
	}

	/**
	 * Inicializa as informações utilizadas no processamento da convocação.
	 */
	private void clearProcessamento() {
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		cancelamentos = new ArrayList<CancelamentoConvocacao>();
		ofertaComVagasRemanescentes = new LinkedHashMap<OfertaVagasCurso, Integer>();
		quantidadeProcessado = 0;
	}

	/**
	 * Popula as informações necessárias e inicia o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES);
		setOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId());
		gruposCotas = getGenericDAO().findByExactField(GrupoCotaVagaCurso.class, "nivel", NivelEnsino.GRADUACAO, "asc", "descricao");
		return telaFormulario();
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String efetivarCadastramento() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		this.naoGerarCancelamento = true;
		// verifica se os templates de e-mail foram cadastrados
		TemplateDocumentoDao dao = getDAO(TemplateDocumentoDao.class, Sistema.COMUM);
		if (isEmpty(dao.findByCodigo(TemplatesDocumentos.EMAIL_CONFIRMACAO_DISCENTE_PRECADASTRADO)))
			addMensagemWarning("O modelo de e-mail a ser enviado para os discentes que terão seu cadastro confirmado não foi cadastrado.");
		if (isEmpty(dao.findByCodigo(TemplatesDocumentos.EMAIL_EXCLUSAO_DISCENTE_PRECADASTRADO)))
			addMensagemWarning("O modelo de e-mail a ser enviado para os discentes que terão seu cadastro excluído não foi cadastrado.");
		if (hasOnlyErrors()) return null;
		prepareMovimento(SigaaListaComando.EFETIVAR_CADASTRAMENTO_VESTIBULAR);
		setOperacaoAtiva(SigaaListaComando.EFETIVAR_CADASTRAMENTO_VESTIBULAR.getId());
		return formEfetivarCadastramento();
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException 
	 */
	public String encerrarCadastramento() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS);
		clear();
		this.naoGerarCancelamento = false;
		prepareMovimento(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR);
		setOperacaoAtiva(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR.getId());
		return formEncerrarCadastramento();
	}

	/** Redireciona o usuário para o formulário de encerramento do cadastramento.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formEfetivarCadastramento() {
		return forward("/graduacao/convocacao_vestibular/efetivar_cadastramento.jsp");
	}
	
	/** Redireciona o usuário para o formulário de encerramento do cadastramento.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String formEncerrarCadastramento() {
		return forward("/graduacao/convocacao_vestibular/encerrar_cadastramento.jsp");
	}
	
	/**
	 * Atualiza internamento o processo seletivo escolhido pelo usuário. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @throws ArqException
	 */
	public String processoSeletivoListener(ValueChangeEvent evt) throws DAOException {
		int id = (Integer) evt.getNewValue();
		if (id != 0) {
			obj.setProcessoSeletivo(getGenericDAO().findByPrimaryKey(id, ProcessoSeletivoVestibular.class));
			if (!obj.getProcessoSeletivo().isEntradaDoisPeriodos()) {
				if (obj.getProcessoSeletivo().getPeriodoEntrada() == 1)
					obj.setSemestreConvocacao(SemestreConvocacao.CONVOCA_APENAS_PRIMEIRO_SEMESTRE);
				else
					obj.setSemestreConvocacao(SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE);
			}
		} else
			obj.setProcessoSeletivo(new ProcessoSeletivoVestibular());
		return null;
	}
	
	/**
	 * Procura vagas ociosas e as informações dos próximos classificados para
	 * preenchê-las. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * 
	 * @throws ArqException
	 */
	public String buscarVagasRemanescentes() throws DAOException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId()))
			return telaFormulario();
		clearProcessamento();
		
		ConvocacaoVestibularDao dao = getDAO(ConvocacaoVestibularDao.class);
		
		ValidatorUtil.validateRequiredId(obj.getProcessoSeletivo().getId(), "Processo Seletivo Vestibular", erros);
		if (obj.getProcessoSeletivo().getEstrategiaConvocacao() == null)
			addMensagemErro("Não há estratégia de convocação definida para o Processo Seletivo selecionado.");
		else {
			ValidatorUtil.validateRequired(obj.getDescricao(), "Descrição", erros);
			ValidatorUtil.validateRequired(obj.getDataConvocacao(), "Data da Convocação", erros);
			ValidatorUtil.validateRequired(obj.getSemestreConvocacao(), "Semestre a Convocar", erros);
			validateMinValue(obj.getPercentualAdicionalVagas(), 0, "Percentual de Vagas Adicionais", erros);
			checarPreCondicoes(dao);
		}
		
		if(hasErrors())
			return telaFormulario();
		EstrategiaConvocacaoCandidatosVestibular estrategia = obj.getProcessoSeletivo().getEstrategiaConvocacao();
		
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoVestibular.class));
		ofertaComVagasRemanescentes = estrategia.findOfertaComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), incluirPreCadastrado, incluirPendenteCadastro);
		cotasComVagasRemanescentes = estrategia.findCotasComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), incluirPreCadastrado, incluirPendenteCadastro);
		if(hasVagasOciosas()){
			ofertaComVagasAConvocar = new LinkedHashMap<OfertaVagasCurso, Integer>();
			cotasComVagasAConvocar = new LinkedHashMap<CotaOfertaVagaCurso, Integer>();
			// aplica o percentual de aumento de vagas
			if (obj.getPercentualAdicionalVagas() > 0) {
				for(CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
					int vagas = cotasComVagasRemanescentes.get(cota);
					if (aplicaPercentualCotistas) 
						vagas = (int) Math.round(obj.getPercentualAdicionalVagas() * vagas / 100.0 + vagas);
					cotasComVagasAConvocar.put(cota, vagas);
				}
				for(OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
					Integer vagas = ofertaComVagasRemanescentes.get(oferta);
					vagas = (int) Math.round(obj.getPercentualAdicionalVagas() * vagas / 100.0 + vagas);
					ofertaComVagasAConvocar.put(oferta, vagas);
				}
			} else {
				ofertaComVagasAConvocar = ofertaComVagasRemanescentes;
				cotasComVagasAConvocar = cotasComVagasRemanescentes;
			}
			// caso haja mais discentes cadastrados que o número de vagas (quando, por exemplo, há pendente de cadastro)
			// o número de vagas fica negativo.
			for(OfertaVagasCurso oferta : ofertaComVagasAConvocar.keySet()) {
				Integer vagas = ofertaComVagasAConvocar.get(oferta);
				if (vagas < 0) vagas = 0;
				ofertaComVagasAConvocar.put(oferta, vagas);
			}
		} else {
			addMensagemWarning("Todas vagas ofertadas para o Processo Seletivo "+obj.getProcessoSeletivo().getNome()+" foram preenchidas.");
			return null;
		}
		// monta uma lista de oferta de vagas para que o usuário possa alterar os valores utilizados na convocação
		listaOfertaVagasCurso = new ArrayList<OfertaVagasCurso>();
		for(OfertaVagasCurso oferta : ofertaComVagasAConvocar.keySet()) {
			Integer vagas = ofertaComVagasAConvocar.get(oferta);
			oferta.setTotalVagasOciosas(vagas);
			for(CotaOfertaVagaCurso cota : oferta.getCotas()) {
				for(CotaOfertaVagaCurso cotaVaga : cotasComVagasAConvocar.keySet()) {
					if (cota.getOfertaVagasCurso().getId() == cotaVaga.getOfertaVagasCurso().getId() && cota.getGrupoCota().getId() == cotaVaga.getGrupoCota().getId()) {
						Integer vagasCota = cotasComVagasAConvocar.get(cotaVaga);
						cota.setTotalVagasOciosas(vagasCota);
					}
				}
			}
			listaOfertaVagasCurso.add(oferta);
		}
		// ordena a lista
		Collections.sort(listaOfertaVagasCurso, new Comparator<OfertaVagasCurso>() {
			@Override
			public int compare(OfertaVagasCurso o1, OfertaVagasCurso o2) {
				int cmp = o1.getMatrizCurricular().getCurso().getMunicipio().getNome().compareTo(o2.getMatrizCurricular().getCurso().getMunicipio().getNome());
				if (cmp == 0)
					cmp = o1.getMatrizCurricular().getDescricaoSemMunicipio().compareTo(o2.getMatrizCurricular().getDescricaoSemMunicipio());
				return cmp;
			}
		});
		return telaQuadroVagas();
	}
	
	/**
	 * Procura vagas ociosas e as informações dos próximos classificados para
	 * preenchê-las. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/form_convocacao.jsp</li>
	 * </ul>
	 * 
	 * @throws ArqException
	 */
	public String buscarConvocacoes() throws ArqException {
		convocacoes = new LinkedList<ConvocacaoProcessoSeletivoDiscente>();
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId()))
			return telaFormulario();
		if(!hasVagasOciosas()){
			addMensagemWarning("Todas vagas ofertadas para o Processo Seletivo "+obj.getProcessoSeletivo().getNome()+" foram preenchidas.");
			return null;
		}
		EstrategiaConvocacaoCandidatosVestibular estrategia = obj.getProcessoSeletivo().getEstrategiaConvocacao();
		ofertaComVagasRemanescentes = estrategia.findOfertaComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), incluirPreCadastrado, incluirPendenteCadastro);
		cotasComVagasRemanescentes = estrategia.findCotasComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), incluirPreCadastrado, incluirPendenteCadastro);
		// recupera as quantidade de convocações que o usuário pode ter alterado
		for (OfertaVagasCurso oferta : listaOfertaVagasCurso) {
			Integer vagasDigitado = oferta.getTotalVagasOciosas();
			ofertaComVagasAConvocar.put(oferta, vagasDigitado);
			Integer vagas = ofertaComVagasRemanescentes.get(oferta);
			if (vagasDigitado < vagas)
				ofertaComVagasRemanescentes.put(oferta, vagasDigitado);
			int totalCotas = 0;
			for (CotaOfertaVagaCurso cota : oferta.getCotas()) {
				for(CotaOfertaVagaCurso cotaVaga : cotasComVagasAConvocar.keySet()) {
					if (cota.getOfertaVagasCurso().getId() == cotaVaga.getOfertaVagasCurso().getId() && cota.getGrupoCota().getId() == cotaVaga.getGrupoCota().getId()) {
						Integer vagasCotaDigitado = cota.getTotalVagasOciosas();
						totalCotas += vagasCotaDigitado;
						cotasComVagasAConvocar.put(cotaVaga, vagasCotaDigitado);
						Integer vagasCota = cotasComVagasRemanescentes.get(cotaVaga);
						if (vagasCotaDigitado < vagasCota)
							cotasComVagasRemanescentes.put(cotaVaga, vagasCotaDigitado);
					}
				}
			}
			if (totalCotas > vagasDigitado) {
				addMensagemErro("O curso "
						+ oferta.getMatrizCurricular().getDescricao()
						+ " possui um número de cotistas (" + totalCotas
						+ ") maior que o número de candidatos a convocar ("
						+ vagasDigitado + ").");
			}
		}
		if (hasErrors()) return telaQuadroVagas();
		// convoca dentro do número de vagas
		estrategia.convocarCandidatos(convocacoes, ofertaComVagasRemanescentes, cotasComVagasRemanescentes, obj.getSemestreConvocacao(), obj.getProcessoSeletivo(), true);
		// se houver cadastro reserva
		if (obj.getPercentualAdicionalVagas() > 0) {
			// subtrai da reserva de vagas as quantidades já convocadas
			for (OfertaVagasCurso oferta : ofertaComVagasAConvocar.keySet()) {
				Integer vagas = ofertaComVagasAConvocar.get(oferta) - ofertaComVagasRemanescentes.get(oferta);
				if (vagas < 0) vagas = 0;
				ofertaComVagasAConvocar.put(oferta, vagas);
			}
			for (CotaOfertaVagaCurso cota : cotasComVagasAConvocar.keySet()) {
				Integer vagas = cotasComVagasAConvocar.get(cota) - cotasComVagasRemanescentes.get(cota);
				if (vagas < 0) vagas = 0;
				cotasComVagasAConvocar.put(cota, vagas);
			}
			// convoca para reserva de vagas
			estrategia.convocarCandidatos(convocacoes, ofertaComVagasAConvocar, cotasComVagasAConvocar, obj.getSemestreConvocacao(), obj.getProcessoSeletivo(), false);
		}
		if (isEmpty(convocacoes)) {
			addMensagemErro("Não há discentes a serem convocados.");
			return telaFormulario();
		}
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			convocacao.setConvocacaoProcessoSeletivo(obj);
		}
		cancelamentos = new LinkedList<CancelamentoConvocacao>();
		if (obj.getPercentualAdicionalVagas() > 0) {
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
				if (convocacao.getConvocacaoAnterior() != null && 
						convocacao.getConvocacaoAnterior().getCancelamento() != null) {
					cancelamentos.add(convocacao.getConvocacaoAnterior().getCancelamento());
				}
			}
		}
		// ordena a lista de convocados para apresentação final
		ordenaConvocacaoes();
		System.out.println("Renderizando...");
		return telaResumo();
	}

	/**
	 * Ordena as convocações por matriz curricular, cotas e classificação
	 */
	private void ordenaConvocacaoes() {
		Collections.sort(convocacoes, new Comparator<ConvocacaoProcessoSeletivoDiscente>() {
			@Override
			public int compare(ConvocacaoProcessoSeletivoDiscente o1, ConvocacaoProcessoSeletivoDiscente o2) {
				int cmp = o1.getDiscente().getMatrizCurricular().getCurso().getMunicipio().getNome()
						.compareTo(o2.getDiscente().getMatrizCurricular().getCurso().getMunicipio().getNome());
				if (cmp == 0)
					cmp = o1.getDiscente().getMatrizCurricular().getDescricaoSemMunicipio().compareTo(o2.getDiscente().getMatrizCurricular().getDescricaoSemMunicipio());
				if (cmp == 0) {
					if (!isEmpty(o1.getGrupoCotaConvocado()) && isEmpty(o2.getGrupoCotaConvocado())) 
						cmp = -1;
					else if (isEmpty(o1.getGrupoCotaConvocado()) && !isEmpty(o2.getGrupoCotaConvocado()))
						cmp = 1;
					else  if (!isEmpty(o1.getGrupoCotaConvocado()) && !isEmpty(o2.getGrupoCotaConvocado()))
						cmp = o1.getGrupoCotaConvocado().getDescricao().compareTo(o2.getGrupoCotaConvocado().getDescricao());
				}
				if (cmp == 0)
					cmp = o1.getResultado().getClassificacao() - o2.getResultado().getClassificacao();
				return cmp;
			}
		});
	}
	
	
	/**
	 * Procura vagas ociosas e as informações dos próximos classificados para
	 * preenchê-las. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/efetivar_cadastramento.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 * 
	 * @throws ArqException
	 */
	public String buscarEfetivacao() throws HibernateException, DAOException {
		if (!checkOperacaoAtiva(SigaaListaComando.EFETIVAR_CADASTRAMENTO_VESTIBULAR.getId()))
			return telaFormulario();
		clearProcessamento();
		ConvocacaoProcessoSeletivoDiscenteDao dao = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class);
		dao.initialize(obj.getProcessoSeletivo());
		ValidatorUtil.validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo Vestibular", erros);
		if (obj.getProcessoSeletivo().getEstrategiaConvocacao() == null)
			addMensagemErro("Não há estratégia de convocação definida para o Processo Seletivo selecionado.");
		if(hasErrors())
			return formEfetivarCadastramento();
		EstrategiaConvocacaoCandidatosVestibular estrategia = obj.getProcessoSeletivo().getEstrategiaConvocacao();
		obj.setProcessoSeletivo(dao.findByPrimaryKey(obj.getProcessoSeletivo().getId(), ProcessoSeletivoVestibular.class));
		ofertaComVagasRemanescentes = estrategia.findOfertaComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), false, false);
		cotasComVagasRemanescentes = estrategia.findCotasComVagasRemanescentes(obj.getProcessoSeletivo(), obj.getSemestreConvocacao(), false, false);
		// copia as vagas para executar o ponto de corte nos pré-cadastramentos
		ofertaComVagasAConvocar = new LinkedHashMap<OfertaVagasCurso, Integer>();
		cotasComVagasAConvocar = new LinkedHashMap<CotaOfertaVagaCurso, Integer>();
		// clona as vagas remanescentes para poder processar
		for(CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
			int vagas = cotasComVagasRemanescentes.get(cota);
			cotasComVagasAConvocar.put(cota, vagas);
		}
		for(OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
			Integer vagas = ofertaComVagasRemanescentes.get(oferta);
			ofertaComVagasAConvocar.put(oferta, vagas);
		}
		// processa 
		convocacoes = (List<ConvocacaoProcessoSeletivoDiscente>) dao.classificaPreCadastramento(obj.getProcessoSeletivo().getId(), ofertaComVagasAConvocar, cotasComVagasAConvocar, naoGerarCancelamento, preencherVagasCotistas);
		cancelamentos = new LinkedList<CancelamentoConvocacao>();
		if (!naoGerarCancelamento) {
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes)
				if (convocacao.getCancelamento() != null)
					cancelamentos.add(convocacao.getCancelamento());
		}
		if (isEmpty(convocacoes) && isEmpty(cancelamentos)) {
			addMensagemErro("Não há discentes pré-cadastrados para o Processo Seletivo selecionado");
			return forward("/graduacao/convocacao_vestibular/efetivar_cadastramento.jsp");
		} else if (isEmpty(convocacoes)) {
			addMensagemWarning("Não há discentes a serem cadastrados para o Processo Seletivo selecionado");
		}
		// reduz a lista para exibir somente os que serão cadastrados.
		// a lista será mostrada com CADASTROS e RESERVAS até o último que será cadastrado
		// mapa de última classificação por matriz curricular
		Map<Integer, Integer> mapaUltimoClassificado = new TreeMap<Integer, Integer>();
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			if (convocacao.getDiscente().getStatus() == StatusDiscente.CADASTRADO) {
				mapaUltimoClassificado.put(convocacao.getDiscente().getMatrizCurricular().getId(), convocacao.getResultado().getClassificacao());
			}
		}
		// remove da lista a ser exibida os discentes além do último classificado
		Iterator<ConvocacaoProcessoSeletivoDiscente> iterator = convocacoes.iterator();
		while (iterator.hasNext()) {
			ConvocacaoProcessoSeletivoDiscente convocacao = iterator.next();
			if (convocacao.getDiscente().getStatus() == StatusDiscente.CADASTRADO) continue;
			Integer ultimaClassificacao = mapaUltimoClassificado.get(convocacao.getDiscente().getMatrizCurricular().getId());
			if (ultimaClassificacao == null || convocacao.getResultado().getClassificacao() > ultimaClassificacao)
				iterator.remove();
		}
		discenteExcluidos = dao.findDiscentesPendentesCadastro(obj.getProcessoSeletivo());
		if (!isEmpty(discenteExcluidos))
			addMensagemWarning("Esta operação irá excluir " + discenteExcluidos.size() + " discentes com status PENDENTE DE CADASTRO");
		// ordena a lista de convocados para apresentação final
		ordenaConvocacaoes();
		System.out.println("Renderizando... " + convocacoes.size());
		return forward("/graduacao/convocacao_vestibular/resumo_efetivacao.jsp");
	}
	
	/**
	 * Procura por todos discentes com status PRÉ-CADASTRADO para excluir. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/efetivar_cadastramento.jsp</li>
	 * </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 * 
	 * @throws ArqException
	 */
	public String buscarEncerramento() throws HibernateException, DAOException {
		if (!checkOperacaoAtiva(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR.getId()))
			return telaFormulario();
		clearProcessamento();
		ConvocacaoProcessoSeletivoDiscenteDao dao = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class);
		dao.initialize(obj.getProcessoSeletivo());
		ValidatorUtil.validateRequired(obj.getProcessoSeletivo(), "Processo Seletivo Vestibular", erros);
		// processa 
		convocacoes = (List<ConvocacaoProcessoSeletivoDiscente>) dao.buscaEncerramentoCadastramento(obj.getProcessoSeletivo().getId());
		cancelamentos = new LinkedList<CancelamentoConvocacao>();
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes)
			if (convocacao.getCancelamento() != null)
				cancelamentos.add(convocacao.getCancelamento());
		if (isEmpty(cancelamentos)) {
			addMensagemErro("Não há discentes pré-cadastrados para o Processo Seletivo selecionado");
			return formEncerrarCadastramento();
		}
		// ordena a lista de convocados para apresentação final
		return forward("/graduacao/convocacao_vestibular/resumo_encerramento.jsp");
	}
	
	
	/**
	 * Invoca o processador para persistir as informações da convocação.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/resumo_convocacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String confirmar() throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES.getId())
				|| !confirmaSenha() )
			return redirectMesmaPagina();
		// separar o processamento por matriz curricular
		TreeSet<MatrizCurricular> matrizesOrdenadas = new TreeSet<MatrizCurricular>(new Comparator<MatrizCurricular>() {
			@Override
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			matrizesOrdenadas.add(convocacao.getDiscente().getMatrizCurricular());
		}
		for (CancelamentoConvocacao cancelamento : cancelamentos) {
			matrizesOrdenadas.add(cancelamento.getConvocacao().getDiscente().getMatrizCurricular());
		}
		matrizes = CollectionUtils.toList(matrizesOrdenadas);
		MatrizCurricular processando = null;
		try {
			// processa por matriz curricular
			quantidadeProcessado = 1;
			for (MatrizCurricular matriz : this.matrizes) {
				System.out.println("Cadastrando discentes para: " + matriz.getDescricao());
				processando = matriz;
				// separa as listas
				List<ConvocacaoProcessoSeletivoDiscente> subListaConvocacaoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
				List<CancelamentoConvocacao> subListaCancelamentos = new ArrayList<CancelamentoConvocacao>();
				for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
					if (convocacao.getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaConvocacaoes.add(convocacao);
					}
				}
				for (CancelamentoConvocacao cancelamento : cancelamentos) {
					if (cancelamento.getConvocacao().getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaCancelamentos.add(cancelamento);
					}
				}
				prepareMovimento(SigaaListaComando.CONVOCAR_CANDIDATOS_VESTIBULAR_PARA_VAGAS_REMANESCENTES);
				MovimentoConvocacaoVestibular mov = new MovimentoConvocacaoVestibular();
				mov.setCodMovimento(getUltimoComando());
				mov.setObjMovimentado(obj);
				mov.setConvocacoes(subListaConvocacaoes);
				mov.setCancelamentos(subListaCancelamentos);
				execute(mov);
				quantidadeProcessado++;
			}
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convocação para o curso "
					+ (processando != null ? " " + processando.getDescricaoSemEnfase():"")
					+ ": " + e.getMessage());
			notifyError(e);
			quantidadeProcessado = 101;
			return redirectMesmaPagina();
		} finally {
			quantidadeProcessado = matrizes.size();
			matrizes = null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}
	
	
	/**
	 * Invoca o processador para persistir as informações da convocação.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/resumo_convocacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String confirmarEfetivacaoCadastramento()  throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.EFETIVAR_CADASTRAMENTO_VESTIBULAR.getId()))
			return null;
		if( !confirmaSenha() ) return redirectMesmaPagina();
		// caso não processa os discentes pré-cadastrados, remove da lista os discentes que continuarão com status pré-cadastro
		if (naoGerarCancelamento && !isEmpty(convocacoes)) {
			Iterator<ConvocacaoProcessoSeletivoDiscente> iterator = convocacoes.iterator();
			while (iterator.hasNext()) {
				ConvocacaoProcessoSeletivoDiscente convocacao = iterator.next();
				if (convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO)
					iterator.remove();
			}
		}
		// separar o processamento por matriz curricular
		TreeSet<MatrizCurricular> matrizesOrdenadas = new TreeSet<MatrizCurricular>(new Comparator<MatrizCurricular>() {
			@Override
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			matrizesOrdenadas.add(convocacao.getDiscente().getMatrizCurricular());
		}
		for (CancelamentoConvocacao cancelamento : cancelamentos) {
			matrizesOrdenadas.add(cancelamento.getConvocacao().getDiscente().getMatrizCurricular());
		}
		matrizes = CollectionUtils.toList(matrizesOrdenadas);
		MatrizCurricular processando = null;
		try {
			quantidadeProcessado = 1;
			// excluir pendentes de cadastro
			MovimentoCadastro movExcluir = new MovimentoCadastro();
			movExcluir.setCodMovimento(getUltimoComando());
			movExcluir.setObjMovimentado(obj.getProcessoSeletivo());
			movExcluir.setObjAuxiliar(true);
			execute(movExcluir);
			// processa por matriz curricular
			for (MatrizCurricular matriz : this.matrizes) {
				processando = matriz;
				// separa as listas
				List<ConvocacaoProcessoSeletivoDiscente> subListaConvocacaoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
				List<CancelamentoConvocacao> subListaCancelamentos = new ArrayList<CancelamentoConvocacao>();
				for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
					if (convocacao.getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaConvocacaoes.add(convocacao);
					}
				}
				for (CancelamentoConvocacao cancelamento : cancelamentos) {
					if (cancelamento.getConvocacao().getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaCancelamentos.add(cancelamento);
					}
				}
				prepareMovimento(SigaaListaComando.EFETIVAR_CADASTRAMENTO_VESTIBULAR);
				MovimentoConvocacaoVestibular mov = new MovimentoConvocacaoVestibular();
				mov.setCodMovimento(getUltimoComando());
				mov.setObjMovimentado(obj);
				mov.setConvocacoes(subListaConvocacaoes);
				mov.setCancelamentos(subListaCancelamentos);
				execute(mov);
				quantidadeProcessado++;
			}
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convocação para o curso "
					+ (processando != null ? " " + processando.getDescricaoSemEnfase():"")
					+ ": " + e.getMessage());
			notifyError(e);
			quantidadeProcessado = 101;
			return redirectMesmaPagina();
		} finally {
			quantidadeProcessado = matrizes.size();
			matrizes = null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}

	
	/**
	 * Invoca o processador para persistir as informações da convocação.
	 * 
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/convocacao_vestibular/resumo_convocacao.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 */
	public String confirmarEncerramentoCadastramento()  throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR.getId()))
			return null;
		if( !confirmaSenha() ) return redirectMesmaPagina();
		// separar o processamento por matriz curricular
		TreeSet<MatrizCurricular> matrizesOrdenadas = new TreeSet<MatrizCurricular>(new Comparator<MatrizCurricular>() {
			@Override
			public int compare(MatrizCurricular o1, MatrizCurricular o2) {
				return o1.getId() - o2.getId();
			}
		});
		
		for (CancelamentoConvocacao cancelamento : cancelamentos) {
			matrizesOrdenadas.add(cancelamento.getConvocacao().getDiscente().getMatrizCurricular());
		}
		matrizes = CollectionUtils.toList(matrizesOrdenadas);
		MatrizCurricular processando = null;
		try {
			quantidadeProcessado = 1;
			// processa por matriz curricular
			for (MatrizCurricular matriz : this.matrizes) {
				processando = matriz;
				// separa as listas
				List<CancelamentoConvocacao> subListaCancelamentos = new ArrayList<CancelamentoConvocacao>();
				for (CancelamentoConvocacao cancelamento : cancelamentos) {
					if (cancelamento.getConvocacao().getDiscente().getMatrizCurricular().getId() == matriz.getId()){
						subListaCancelamentos.add(cancelamento);
					}
				}
				prepareMovimento(SigaaListaComando.ENCERRAR_CADASTRAMENTO_VESTIBULAR);
				MovimentoConvocacaoVestibular mov = new MovimentoConvocacaoVestibular();
				mov.setCodMovimento(getUltimoComando());
				mov.setObjMovimentado(obj);
				mov.setCancelamentos(subListaCancelamentos);
				execute(mov);
				quantidadeProcessado++;
			}
		} catch (Exception e) {
			addMensagemErro("Houve um erro durante o processamento da convocação para o curso "
					+ (processando != null ? " " + processando.getDescricaoSemEnfase():"")
					+ ": " + e.getMessage());
			notifyError(e);
			quantidadeProcessado = 101;
			return redirectMesmaPagina();
		} finally {
			quantidadeProcessado = matrizes.size();
			matrizes = null;
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		removeOperacaoAtiva();
		return cancelar();
	}

	/**
	 * Verifica se a operação pode ser realizada. Uma convocação só pode ser
	 * gerada se a etapa de cadastramento estiver concluída, ou seja, não pode
	 * haver alunos convocados para o processo seletivo em questão ainda com
	 * status {@link StatusDiscente.PENDENTE_CADASTRO}.
	 * 
	 * @param dao
	 * @throws DAOException
	 */
	private void checarPreCondicoes(ConvocacaoVestibularDao dao) throws DAOException {
		Collection<MatrizCurricular> matrizesComAlunosPendentesCadastro = dao.findMatrizesComAlunosPendentesCadastro(obj.getProcessoSeletivo());
		if(!isEmpty(matrizesComAlunosPendentesCadastro)) {
			addMensagemWarning("O processo seletivo ainda possui discente(s) com status PENDENTE DE CADASTRO em "
					+ matrizesComAlunosPendentesCadastro.size()
					+ " matriz(es) curricular(es).");
		}
	}

	/**
	 * Checa se há vagas disponíveis nas matrizes curriculares.
	 * 
	 * @return
	 */
	private boolean hasVagasOciosas() {
		return !isEmpty(ofertaComVagasRemanescentes);
	}

	/**
	 * Encaminha para a tela do formulário da convocação.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaFormulario() {
		return forward("/graduacao/convocacao_vestibular/form_convocacao.jsp");
	}
	
	/**
	 * Encaminha para a tela do formulário da convocação.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaQuadroVagas() {
		return forward("/graduacao/convocacao_vestibular/form_quadro_vagas.jsp");
	}
	
	/**
	 * Encaminha para a tela de resumo da convocação.
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 */
	public String telaResumo() {
		return forward("/graduacao/convocacao_vestibular/resumo_convocacao.jsp");
	}

	/** Lista de SelectItem de Processos Seletivos do Vestibular.
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getProcessoSeletivoVestibularCombo() throws DAOException{
		if (processosCombo == null){
			processosCombo = toSelectItems(getDAO(ConvocacaoVestibularDao.class).findProcessosSeletivos(), "id", "nome");
		}
		return processosCombo;
	}

	public List<ConvocacaoProcessoSeletivoDiscente> getConvocacoes() {
		return convocacoes;
	}

	public List<CancelamentoConvocacao> getCancelamentos() {
		return cancelamentos;
	}

	public Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> getErrosConvocacao() {
		return errosConvocacao;
	}
	
	/** Retorna o percentual do processamento. 
	 * @return Percentual do processamento. Ao fim do processamento, retorna o valor 101. 
	 */
	public int getPercentualProcessado() {
		if (quantidadeProcessado == 0) return 0;
		if (matrizes != null && matrizes.size() <= quantidadeProcessado)
			return 101;
		if (matrizes != null && matrizes.size() > quantidadeProcessado) {
			int percentual = 100 * (quantidadeProcessado) / matrizes.size();
			return percentual == 0 ? 1 : percentual;
		} else 
			return 0;
	}
	
	/** Retorna uma estimativa do tempo restante para o fim do processamento. 
	 * @return Estimativa do tempo restante para o fim do processamento.
	 */
	public String getMensagemProgresso() {
		if (convocacoes != null && matrizes != null) {
			StringBuilder msg = new StringBuilder();
			if (quantidadeProcessado <= 1) {
				msg.append("Verificando por discentes PENDENTE DE CADASTRO...");
			} else if (quantidadeProcessado < matrizes.size()) {
				msg.append("Processando ")
				.append(quantidadeProcessado + 1)
				.append(" de ")
				.append(matrizes.size())
				.append(" (")
				.append(matrizes.get(quantidadeProcessado).getDescricao())
				.append(")");
			}
			return msg.toString();
		} else {
			return null;
		}
	}

	public boolean isNaoGerarCancelamento() {
		return naoGerarCancelamento;
	}

	public void setNaoGerarCancelamento(boolean naoGerarCancelamento) {
		this.naoGerarCancelamento = naoGerarCancelamento;
	}

	public boolean isPreencherVagasCotistas() {
		return preencherVagasCotistas;
	}

	public void setPreencherVagasCotistas(boolean preencherVagasCotistas) {
		this.preencherVagasCotistas = preencherVagasCotistas;
	}
	
	public boolean isDistancia() {
		return false;
	}

	public List<OfertaVagasCurso> getListaOfertaVagasCurso() {
		return listaOfertaVagasCurso;
	}

	public void setListaOfertaVagasCurso(
			List<OfertaVagasCurso> listaOfertaVagasCurso) {
		this.listaOfertaVagasCurso = listaOfertaVagasCurso;
	}

	public Collection<GrupoCotaVagaCurso> getGruposCotas() {
		return gruposCotas;
	}

	public void setGruposCotas(Collection<GrupoCotaVagaCurso> gruposCotas) {
		this.gruposCotas = gruposCotas;
	}
	
	public boolean isConvocaPrimeiroPeriodo(){
		return obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_APENAS_PRIMEIRO_SEMESTRE ||
				obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_TODOS_SEMESTRES;
	}
	
	public boolean isConvocaSegundoPeriodo(){
		return obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_APENAS_SEGUNDO_SEMESTRE ||
				obj.getSemestreConvocacao() == SemestreConvocacao.CONVOCA_TODOS_SEMESTRES;
	}

	public boolean isIncluirPreCadastrado() {
		return incluirPreCadastrado;
	}

	public void setIncluirPreCadastrado(boolean incluirPreCadastrado) {
		this.incluirPreCadastrado = incluirPreCadastrado;
	}

	public boolean isIncluirPendenteCadastro() {
		return incluirPendenteCadastro;
	}

	public void setIncluirPendenteCadastro(boolean incluirPendenteCadastro) {
		this.incluirPendenteCadastro = incluirPendenteCadastro;
	}

	public boolean isAplicaPercentualCotistas() {
		return aplicaPercentualCotistas;
	}

	public void setAplicaPercentualCotistas(boolean aplicaPercentualCotistas) {
		this.aplicaPercentualCotistas = aplicaPercentualCotistas;
	}

	public Map<OfertaVagasCurso, Integer> getOfertaComVagasRemanescentes() {
		return ofertaComVagasRemanescentes;
	}

	public void setOfertaComVagasRemanescentes(
			Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes) {
		this.ofertaComVagasRemanescentes = ofertaComVagasRemanescentes;
	}

	public Collection<DiscenteGraduacao> getDiscenteExcluidos() {
		return discenteExcluidos;
	}

	public void setDiscenteExcluidos(Collection<DiscenteGraduacao> discenteExcluidos) {
		this.discenteExcluidos = discenteExcluidos;
	}

	public List<SelectItem> getProcessosCombo() {
		return processosCombo;
	}

	public void setProcessosCombo(List<SelectItem> processosCombo) {
		this.processosCombo = processosCombo;
	}

	public Map<OfertaVagasCurso, Integer> getOfertaComVagasAConvocar() {
		return ofertaComVagasAConvocar;
	}

	public void setOfertaComVagasAConvocar(
			Map<OfertaVagasCurso, Integer> ofertaComVagasAConvocar) {
		this.ofertaComVagasAConvocar = ofertaComVagasAConvocar;
	}

	public Map<CotaOfertaVagaCurso, Integer> getCotasComVagasRemanescentes() {
		return cotasComVagasRemanescentes;
	}

	public void setCotasComVagasRemanescentes(
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes) {
		this.cotasComVagasRemanescentes = cotasComVagasRemanescentes;
	}

	public Map<CotaOfertaVagaCurso, Integer> getCotasComVagasAConvocar() {
		return cotasComVagasAConvocar;
	}

	public void setCotasComVagasAConvocar(
			Map<CotaOfertaVagaCurso, Integer> cotasComVagasAConvocar) {
		this.cotasComVagasAConvocar = cotasComVagasAConvocar;
	}

	public int getQuantidadeProcessado() {
		return quantidadeProcessado;
	}

	public void setQuantidadeProcessado(int quantidadeProcessado) {
		this.quantidadeProcessado = quantidadeProcessado;
	}

	public ArrayList<MatrizCurricular> getMatrizes() {
		return matrizes;
	}

	public void setMatrizes(ArrayList<MatrizCurricular> matrizes) {
		this.matrizes = matrizes;
	}

	public void setConvocacoes(List<ConvocacaoProcessoSeletivoDiscente> convocacoes) {
		this.convocacoes = convocacoes;
	}

	public void setCancelamentos(List<CancelamentoConvocacao> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public void setErrosConvocacao(
			Map<MatrizCurricular, List<ResultadoPessoaConvocacao>> errosConvocacao) {
		this.errosConvocacao = errosConvocacao;
	}
}
