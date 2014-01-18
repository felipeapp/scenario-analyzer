/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/04/2009
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.avaliacao.ComentarioAvaliacaoModeradoDao;
import br.ufrn.sigaa.arq.dao.avaliacao.GrupoPerguntasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.EstatisticaAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.MediaNotas;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.Pergunta;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import br.ufrn.sigaa.avaliacao.dominio.TabelaRespostaResultadoAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.TipoPergunta;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.jsf.UnidadeMBean;
import br.ufrn.sigaa.mensagens.MensagensAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Managed bean para realização de relatórios sobre os discentes, tais como:
 * Trancamento por período, departamento/centro ou de Reprovações.
 * 
 * @author Jean Guerethes
 * 
 */

@Component
@Scope("request")
public class RelatorioAvaliacaoMBean extends SigaaAbstractController<ParametroProcessamentoAvaliacaoInstitucional> {

	/** Constante que define o relatório a ser gerado: relatório de trancamentos no período. */
	private static final int RELATORIO_TRANCAMENTO_PERIODO = 1;
	/** Constante que define o relatório a ser gerado: relatório quantitativo de trancamentos por centro / departamento. */
	private static final int RELATORIO_DEPARTAMENTO_CENTRO = 2;
	/** Constante que define o relatório a ser gerado: relatório quantitativo do total de alunos e total de trancamentos. */
	private static final int RELATORIO_REPROVACOES = 3;
	
	/** Link para o formulário de busca por docentes. */
	private static final String JSP_BUSCA_DOCENTE = "/avaliacao/relatorios/busca_docente.jsp";
	/** Link para o formulário com a lista de relatórios. */
	private static final String JSP_DIRECIONAR_LISTA_RELATORIOS = "/avaliacao/relatorios/busca_relatorio.jsp";
	/** Link para o formulário para geração do relatório do resultado da avaliação do docente. */
	private static final String JSP_CENTRO_ANO_PERIODO = "/avaliacao/relatorios/departamento.jsp";
	/** Link para o formulário para geração do relatório do resultado da avaliação do docente. */
	private static final String JSP_ANO_PERIODO = "/avaliacao/relatorios/ano_periodo.jsp";
	/** Link para o relatório de docentes com médias baixa em um grupo de pergunta. */
	private static final String JSP_FORM_MEDIA_BAIXA = "/avaliacao/relatorios/form_media_baixa.jsp";
	/** Link para o relatório de docentes com médias baixa em um grupo de pergunta. */
	private static final String JSP_FORM_NAO_COMPUTADO = "/avaliacao/relatorios/form_nao_computado.jsp";
	/**
	 * Link para o formulário da escolha de Resultados de Avaliações Disponíveis
	 * para a Consulta do Docente.
	 */
	private static final String JSP_ESCOLHA_RESULTADO = "/avaliacao/relatorios/resultados_docente.jsp";
	/** Link para o relatório de trancamentos por período. */
	private static final String JSP_RELATORIO_TRANCAMENTO_POR_PERIODO = "/avaliacao/relatorios/relatorio_trancamento.jsp";
	/** Link para o relatório de trancamentos. */
	private static final String JSP_RELATORIO_TRANCAMENTO = "/avaliacao/relatorios/relatorio_avaliacao.jsp";
	/** Link para o relatório de quantitativo de reprovações. */
	private static final String JSP_RELATORIO_REPROVADOS = "/avaliacao/relatorios/relatorio_quantitativo_reprovacoes.jsp";
	/** Link para o relatório do resultado da avaliação. */
	private static final String JSP_RELATORIO_MEDIA_DOCENTES = "/avaliacao/relatorios/media_docentes.jsp";
	/** Link para o Relatório de Docentes com Média Baixa por Pergunta. */
	private static final String JSP_RELATORIO_MEDIA_BAIXA = "/avaliacao/relatorios/media_baixa.jsp";
	/**
	 * Link para o relatório do resultado da avaliação com detalhamento das
	 * médias de perguntas.
	 */
	private static final String JSP_RELATORIO_MEDIA_DOCENTES_DETALHADO = "/avaliacao/relatorios/media_docentes_detalhado.jsp";
	/** Link para o formulário de consulta do resultado da avaliação institucional pelo discente. */
	private static final String JSP_CONSULTA_DISCENTE = "/avaliacao/relatorios/departamento.jsp";
	/** Link para o relatório quantitativo de avaliações institucionais não computadas. */
	private static final String JSP_RELATORIO_AI_NAO_COMPUTADA = "/avaliacao/relatorios/relatorio_nao_computada.jsp";
	/** Link para o relatório de observações dadas pelos docentes às turmas do discente. */
	private static final String JSP_OBSERVACOES_TURMA_DISCENTE = "/avaliacao/observacoes_turma_discente.jsp";
	/** Índice do relatório escolhido para geração. */
	private int escolhaRelatorio;
	/** Ano utilizado para geração do relatório. */
	private int ano;
	/** Período utilizado para geração do relatório. */
	private int periodo;
	/** ID do centro ao qual o relatório se restringe. */
	private int idUnidade;
	/** Centro ao qual se restringe o relatório. */
	private Unidade unidade;
	/** Lista com dados do relatório. */
	private List<Map<String, Object>> lista = new ArrayList<Map<String, Object>>();
	/** Mapa com dados do relatório. */
	private Map<String, Integer> result = new HashMap<String, Integer>();
	/** Legenda das perguntas selecionadas. */
	private Map<String, String> legendas;

	/** Coleção de perguntas a detalhar no relatório. */
	private Collection<Pergunta> perguntas;
	/**
	 * Lista de IDs de perguntas selecionadas para serem detalhadas no
	 * relatório.
	 */
	private List<String> perguntasSelecionadas;
	/**
	 * Indica se deverá listar docentes que obtiveram média geral abaixo do valor informado.
	 */
	private boolean mediaGeral;
	/** Coleção de resultados para listagem do relatório. */
	private List<ResultadoAvaliacaoDocente> resultadosDocentes;
	/** Coleção de resultados para listagem do Relatório de Docentes com Média Baixa por Pergunta. */
	private Collection<MediaNotas> resultadoMediaNotasBaixa;
	/** Coleção de resultados para listagem do Relatório de Docentes com Média Geral Baixa. */
	private Collection<ResultadoAvaliacaoDocente> resultadoMediaGeralBaixa;

	/** Indica se a busca por docentes deve ser filtrada por nome. */
	private boolean checkBuscaNome;
	/** Indica se a busca por docentes deve ser filtrada por departamento. */
	private boolean checkBuscaDepartamento;
	/** Indica se a busca por docentes deve ser filtrada por ano/período. */
	private boolean checkBuscaAnoPeriodo;

	/** Nome (ou parte) do docente que se deseja buscar. */
	private String nomeDocente;

	/** Docente a detalhar o resultado da Avaliação Institucional. */
	private Servidor docente;

	/** Lista de docentes encontrados na busca. */
	private List<Map<String, Object>> listaDocentes;

	/**
	 * Dados agrupados do relatório individual do docente na Avaliação
	 * Institucional.
	 */
	private Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas;

	/** Lista das unidades que o usuário pode consultar. */
	private ArrayList<SelectItem> unidades;
	/** Indica se o usuário retorna ao portal do docente. */ 
	private boolean viewPortalDocente;
	/** Coleção de selectItem de ano-períodos processados. */
	private Collection<SelectItem> anoPeriodoCombo;
	/** Ano-Período para geração do relatório. */
	private int anoPeriodo;
	/** Média mínima a ser avaliada no Relatório de Docentes com Média Baixa por Pergunta. */
	private double mediaMinima;
	
	/** Dados gerados para o relatório. */
	private Map<String, Map<String, Map<String, Integer>>> dadosRelatorioNaoComputado;
	/** Grupo de perguntas do relatório. */
	private Collection<SelectItem> grupoPerguntasCombo;
	/** Docentes resultantes da busca por relatório. */
	private Collection<DocenteTurma> docentesTurma;
	/** Coleção de observações do docente. */
	private Collection<ObservacoesDocenteTurma> observacoesTurmaDiscente;

	/** Indica se o relatório deve filtrar apenas os dados do Ensino à Distância. */
	private boolean ead;
	
	/** Dados estatísticos da avaliação institucional. */
	private Collection<EstatisticaAvaliacaoInstitucional> dadosEstatisticos;
	
	/** lista de formulários que o discente pode selecionar. */
	private Collection<SelectItem> formularioCombo;
	
	/**
	 * Inicia a geração do relatório, direcionando para um formulário para que o
	 * usuário escolha o relatório a ser gerado. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciar() throws DAOException {
		initController();
		return forward(JSP_DIRECIONAR_LISTA_RELATORIOS);
	}
	
	/**
	 * Inicia a visualização das observações dadas pelos docentes às turmas do discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarObservacoesTurmasDiscente() throws DAOException, SegurancaException {
		initController();
		if (getDiscenteUsuario() != null && !ValidatorUtil.isEmpty(getAnoPeriodoCombo())) {
			return forward(JSP_OBSERVACOES_TURMA_DISCENTE);
		} else throw new SegurancaException();
	}
	
	/**
	 * Inicia a visualização das observações dadas pelos docentes às turmas do discente.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/observacoes_turma_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String observacoesTurmasDiscente() throws DAOException, SegurancaException {
		ValidatorUtil.validateRequiredId(anoPeriodo, "Ano-Período", erros);
		if (hasErrors()) return null;
		ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
		observacoesTurmaDiscente = dao.findObservacoesModeradasTurmasDiscente(getDiscenteUsuario().getId(), anoPeriodo / 10, anoPeriodo % 10);
		if (ValidatorUtil.isEmpty(observacoesTurmaDiscente))
			addMensagemWarning("Não há observações dada por docente à suas turmas no ano-período selecionado.");
		return null;
	}
	
	/**
	 * Inicia a geração do Relatório de Docentes com Média Baixa por Pergunta.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarQuantitativoNaoComputado() throws DAOException, SegurancaException {
		initController();
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
			return forward(JSP_FORM_NAO_COMPUTADO);
		} else throw new SegurancaException();
	}
	
	/**
	 * Inicia a geração do Relatório de Docentes com Média Baixa por Pergunta.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarMediaBaixa() throws DAOException, SegurancaException {
		initController();
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
			return forward(JSP_FORM_MEDIA_BAIXA);
		} else throw new SegurancaException();
	}

	/**
	 * Inicia a geração do relatório de médias das notas da avaliação
	 * institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarConsultaPublica() throws DAOException, SegurancaException {
		initController();
		if (ValidatorUtil.isEmpty(getAnoPeriodoCombo())) {
			addMensagemErro("Não há resultados liberados para consulta.");
			return null;
		}
		return forward(JSP_CONSULTA_DISCENTE);
	}
	
	/**
	 * Inicia a geração do relatório de médias das notas da avaliação
	 * institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String iniciarMediaNotas() throws DAOException, SegurancaException {
		initController();
		if (ValidatorUtil.isEmpty(getAnoPeriodoCombo())) {
			addMensagemErro("Não há resultados liberados para consulta.");
			return null;
		}
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO)) {
			idUnidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			return forward(JSP_ANO_PERIODO);
		} else if (getUsuarioLogado().isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
			return forward(JSP_CENTRO_ANO_PERIODO);
		} else throw new SegurancaException();
	}

	/**
	 * Inicia a geração do Relatório Analítico da Avaliação Institucional por
	 * docente, redirecionando o usuário para um formulário de busca de
	 * docentes. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarResultadoAnalitico() throws DAOException {
		initController();
		if (ValidatorUtil.isEmpty(getAnoPeriodoCombo())) {
			addMensagemErro("Não há resultados liberados para consulta.");
			return null;
		}
		this.viewPortalDocente = false;
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			idUnidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			checkBuscaDepartamento = true;
		}
		return forward(JSP_BUSCA_DOCENTE);
	}

	/**
	 * Inicia a geração do Relatório Analítico da Avaliação Institucional por
	 * docente, redirecionando o usuário para um formulário de busca de
	 * docentes. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarResultadoDocente() throws DAOException {
		initController();
		this.viewPortalDocente = false;
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		listaDocentes = dao.findResultadoByDocente(getUsuarioLogado()
				.getServidorAtivo().getId());
		docente = dao.refresh(new Servidor(getUsuarioLogado()
				.getServidorAtivo().getId()));
		return forward(JSP_ESCOLHA_RESULTADO);
	}
	
	/**
	 * Inicia a geração do Relatório Analítico da Avaliação Institucional por
	 * docente, redirecionando o usuário para um formulário de busca de
	 * docentes. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarPortalDocente() throws DAOException {
		initController();
		this.viewPortalDocente = true;
		return forward(JSP_BUSCA_DOCENTE);
	}

	/**
	 * Busca por docentes de acordo com os parâmetros especificados no
	 * formulário. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/busca_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String buscarDocente() throws DAOException {
		int idCentro = 0;
		int idDepartamento = 0;
		String nomeDocente = null;
		int ano = 0, periodo = 0;
		// Para o chefe de departamento, a unidade é obrigatória
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			idUnidade = getUsuarioLogado().getVinculoAtivo().getUnidade().getId();
			unidade = getUsuarioLogado().getVinculoAtivo().getUnidade();
			checkBuscaDepartamento = true;
		} else if (getUsuarioLogado().isUserInRole(SigaaPapeis.DIRETOR_CENTRO)){
			idCentro = getUsuarioLogado().getVinculoAtivo().getUnidade().getGestora().getId();
		}
		if (isCheckBuscaDepartamento()) {
			idDepartamento = idUnidade;
		}
		if (isCheckBuscaNome())
			nomeDocente = this.docente.getNome();
		if (isCheckBuscaAnoPeriodo()) {
			 ano = anoPeriodo / 10;
			 periodo = anoPeriodo % 10;
		}
		if (nomeDocente == null && idUnidade == 0 && anoPeriodo == 0) {
			addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
			listaDocentes = null;
		} else {
			AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
			// se for diretor de centro ou chefe de departamento e não for limitado o ano, consulta os apenas os ano-períodos que estão liberados para consulta
			if (ano == 0 && periodo == 0 && getUsuarioLogado().isUserInRole(SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.DIRETOR_CENTRO)) {
				listaDocentes = new ArrayList<Map<String,Object>>();
				Collection<ParametroProcessamentoAvaliacaoInstitucional> ultimos = dao.findUltimoProcessamentos();
				for (ParametroProcessamentoAvaliacaoInstitucional parametro : ultimos) {
					if (isPortalAvaliacaoInstitucional() || !isPortalAvaliacaoInstitucional() && parametro.isConsultaDocenteLiberada()) {
						listaDocentes.addAll(dao.findDocenteAnoPeriodoByNomeCentroDepartamento(nomeDocente, idCentro, idDepartamento, parametro.getAno(), parametro.getPeriodo()));
					}
				}
			} else {
				listaDocentes = dao.findDocenteAnoPeriodoByNomeCentroDepartamento(nomeDocente, idCentro, idDepartamento, ano, periodo);
			}
			if (listaDocentes == null || listaDocentes.isEmpty()) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			}
		}
		return null;
	}

	/**
	 * Valida os dados: ano, período.
	 * <br/>Método não invocado por JSP´s.
	 * @throws DAOException
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#validacaoDados(br.ufrn.arq.negocio.validacao.ListaMensagens)
	 */
	public boolean validacaoAnoPeriodo(ListaMensagens mensagens)
			throws DAOException {
		if (mensagens == null)
			mensagens = new ListaMensagens();
		int anoInicial = ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.ANO_INICIO_AVALIACAO_INSTITUCIONAL);
		CalendarioAcademico cal = CalendarioAcademicoHelper
				.getCalendario(getUsuarioLogado());
		ValidatorUtil.validateMinValue(this.ano, anoInicial, "Ano", mensagens);
		if (ano * 10 + periodo > cal.getAno() * 10 + cal.getPeriodo())
			mensagens.addMensagem(MensagensAvaliacaoInstitucional.ANO_PERIODO_ALEM_DO_ATUAL);
		if (this.periodo != 1 && this.periodo != 2) {
			mensagens.addMensagem(MensagensAvaliacaoInstitucional.PERIODO_NAO_REGULAR);
		}
		return !(mensagens.size() > 0);
	}

	/**
	 * Visualiza o resultado individual do docente na Avaliação Institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/busca_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String viewResultadoDocente() throws HibernateException, DAOException {
		int idPessoa = Integer.parseInt(getCurrentRequest().getParameter("idServidor"));
		int ano = Integer.parseInt(getCurrentRequest().getParameter("ano"));
		int periodo = Integer.parseInt(getCurrentRequest().getParameter("periodo"));
		int idFormulario = Integer.parseInt(getCurrentRequest().getParameter("idFormulario"));
		PortalResultadoAvaliacaoMBean mBean = getMBean("portalResultadoAvaliacao");
		if (viewPortalDocente) {
			mBean.setPessoa(getGenericDAO().findByPrimaryKey(idPessoa, Pessoa.class));
			return mBean.paginaInicial();
		} else { 
			return mBean.viewResultadoDocente(idPessoa, ano, periodo, idFormulario);
		}
	}
	
	/**
	 * Visualiza o resultado individual do docente na Avaliação Institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/form_nao_computado.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String relatorioQuantitativoNaoComputado() throws HibernateException, DAOException {
		ValidatorUtil.validateRequiredId(anoPeriodo, "Ano-Período", erros);
		ValidatorUtil.validateRequired(obj.getFormulario(), "Formulário", erros);
		ano = anoPeriodo / 10;
		periodo = anoPeriodo % 10;
		if (hasErrors())
			return null;
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		dadosRelatorioNaoComputado = dao.findDadosRelatorioQuantitativoNaoComputado(ano, periodo, idUnidade, obj.getFormulario().getId());
		unidade = dao.refresh(new Unidade(idUnidade));
		return forward(JSP_RELATORIO_AI_NAO_COMPUTADA);
	}

	/**
	 * Gera o relatório de médias das notas da avaliação institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String relatorioSintetico() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		ValidatorUtil.validateRequiredId(idUnidade, "Departamento", erros);
		ValidatorUtil.validateRequired(anoPeriodo, "Ano-Período", erros);
		ParametroProcessamentoAvaliacaoInstitucional parametro = dao.findByPrimaryKey(anoPeriodo, ParametroProcessamentoAvaliacaoInstitucional.class); 
		if (parametro == null) return null;
		obj = parametro;
		ano = parametro.getAno();
		periodo = parametro.getPeriodo();
		validacaoAnoPeriodo(erros);
		if (hasErrors())
			return null;
		int idCentro = 0;
		int idDepartamento = idUnidade;
		if ( (getAcessoMenu().isDiscente() || getAcessoMenu().isDocente()) && !getAcessoMenu().isAvaliacao()) {
			String ids[] = ParametroHelper.getInstance().getParametroStringArray(ParametrosAvaliacaoInstitucional.ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO);
			perguntasSelecionadas = new ArrayList<String>(); 
			for (String id : ids) {
				if (!id.isEmpty()) perguntasSelecionadas.add(id.trim());
			}
		}
		if (getUsuarioLogado().isUserInRole(SigaaPapeis.DIRETOR_CENTRO)) {
			idDepartamento = idUnidade;
		} else if (getUsuarioLogado().isUserInRole(
				SigaaPapeis.CHEFE_DEPARTAMENTO)) {
			idDepartamento = idUnidade;
		} else if (getUsuarioLogado().isUserInRole(
				SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
			idDepartamento = idUnidade;
		}
		resultadosDocentes = dao.findResultadoByDocenteCentroDepartamentoAnoPeriodo(0, idCentro, idDepartamento, ano, periodo, obj.getFormulario().getId());
		if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		Collections.sort(resultadosDocentes);
		unidade = dao.refresh(new Unidade(idUnidade));
		carregaLegendaPerguntas();
		if (getPerguntasSelecionadas().isEmpty())
			return forward(JSP_RELATORIO_MEDIA_DOCENTES);
		else
			return forward(JSP_RELATORIO_MEDIA_DOCENTES_DETALHADO);
	}
	
	/**
	 * Gera o relatório de médias das notas da avaliação institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String relatorioPublico() throws HibernateException, DAOException {
		if (isPortalAvaliacaoInstitucional())
			return relatorioSintetico();
		ValidatorUtil.validateRequiredId(idUnidade, "Departamento", erros);
		ValidatorUtil.validateRequiredId(anoPeriodo, "Ano-Período", erros);
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		ParametroProcessamentoAvaliacaoInstitucional parametro = dao.findByPrimaryKey(anoPeriodo, ParametroProcessamentoAvaliacaoInstitucional.class); 
		if (parametro == null) return null;
		ano = parametro.getAno();
		periodo = parametro.getPeriodo();
		validacaoAnoPeriodo(erros);
		if (hasErrors())
			return null;
		int idCentro = 0;
		int idDepartamento = idUnidade;
		int idFormulario = parametro.getFormulario().getId();
		String ids[] = ParametroHelper.getInstance().getParametroStringArray(ParametrosAvaliacaoInstitucional.ID_PERGUNTAS_FIXAS_CONSULTA_PUBLICA_RESULTADO);
		perguntasSelecionadas = new ArrayList<String>();
		Collection<Integer> perguntasForm = new LinkedList<Integer>();
		// filta os IDs de perguntas para apenas as perguntas do formulário usado no ano/período.
		for (GrupoPerguntas gp : parametro.getFormulario().getGrupoPerguntas()) {
			for (Pergunta p : gp.getPerguntas()) {
				perguntasForm.add(p.getId());
			}
		}
		for (String id : ids) {
			if (!id.isEmpty() && perguntasForm.contains(Integer.valueOf(id))) {
				perguntasSelecionadas.add(id.trim());
			}
		}
		resultadosDocentes = dao.findResultadoByDocenteCentroDepartamentoAnoPeriodo(0, idCentro, idDepartamento, ano, periodo, idFormulario);
		if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		Collections.sort(resultadosDocentes);
		unidade = dao.refresh(new Unidade(idUnidade));
		carregaLegendaPerguntas();
		if (getPerguntasSelecionadas().isEmpty())
			return forward(JSP_RELATORIO_MEDIA_DOCENTES);
		else
			return forward(JSP_RELATORIO_MEDIA_DOCENTES_DETALHADO);
	}	
	
	/**
	 * Gera o Relatório de Docentes com Média Baixa por Pergunta. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/form_media_baixa.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException 
	 */
	public String relatorioMediaBaixa() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL);
		ValidatorUtil.validateRange(mediaMinima, 0.1, 10, "Média Mínima", erros);
		ano = anoPeriodo / 10;
		periodo = anoPeriodo % 10;
		validacaoAnoPeriodo(erros);
		if (hasErrors())
			return null;
		Collection<Integer> idPerguntas = new ArrayList<Integer>();
		for (String id : perguntasSelecionadas) {
			idPerguntas.add(Integer.parseInt(id.trim()));
		}
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		resultadoMediaNotasBaixa = new ArrayList<MediaNotas>();
		if (!ValidatorUtil.isEmpty(idPerguntas))
			resultadoMediaNotasBaixa = dao.findDocentesMediaNotasBaixa(idUnidade, ano, periodo, mediaMinima, idPerguntas);
		if (mediaGeral)
			resultadoMediaGeralBaixa = dao.findDocentesMediaGeralBaixa(idUnidade, ano, periodo, mediaMinima);
		if (ValidatorUtil.isEmpty(resultadoMediaNotasBaixa) && ValidatorUtil.isEmpty(resultadoMediaGeralBaixa)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		// agrupa os docentes para exibir na JSP
		docentesTurma = new ArrayList<DocenteTurma>();
		if (resultadoMediaGeralBaixa != null) {
			for (ResultadoAvaliacaoDocente resultado : resultadoMediaGeralBaixa) {
				docentesTurma.add(resultado.getDocenteTurma());
			}
		} else {
			resultadoMediaGeralBaixa = new ArrayList<ResultadoAvaliacaoDocente>();
		}
		if (resultadoMediaNotasBaixa!= null) {
			for (MediaNotas media : resultadoMediaNotasBaixa) {
				DocenteTurma dt = media.getResultadoAvaliacaoDocente().getDocenteTurma();
				if (!docentesTurma.contains(dt))
					docentesTurma.add(dt);
			}
		} else {
			resultadoMediaNotasBaixa = new ArrayList<MediaNotas>();
		}
		Collections.sort((List<DocenteTurma>) docentesTurma);
		unidade = dao.refresh(new Unidade(idUnidade));
		carregaLegendaPerguntas();
		return forward(JSP_RELATORIO_MEDIA_BAIXA);
	}

	/** Carrega as legendas das perguntas da Avaliação Institucional. 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaLegendaPerguntas() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		Map<Integer, String> todas = dao.legendaIdPerguntas(obj.getFormulario().getId());
		legendas = new TreeMap<String, String>();
		if (perguntasSelecionadas != null) {
			for (String id : perguntasSelecionadas) {
				if (todas.get(Integer.parseInt(id)) != null) {
					Pergunta p = dao.findByPrimaryKey(Integer.parseInt(id), Pergunta.class);
					legendas.put(todas.get(Integer.parseInt(id)), p.getDescricao().replaceAll("<br/>", "").replaceAll("<br>", ""));
				}
			}
		}		
	}
	
	/**
	 * Inicia os atributos deste controller.
	 * 
	 * @throws DAOException
	 */
	private void initController() throws DAOException {
		CalendarioAcademico c = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
		ano = c.getAno();
		periodo = c.getPeriodo();
		if (periodo == 2 || periodo == 4) {
			periodo = 1;
		} else {
			periodo = 2;
			ano--;
		}
		this.idUnidade = 0;
		this.perguntas = null;
		this.docente = new Servidor();
		this.checkBuscaAnoPeriodo = false;
		this.checkBuscaDepartamento = false;
		this.checkBuscaNome = false;
		this.lista = null;
		this.listaDocentes = null;
		this.viewPortalDocente = true;
		this.perguntasSelecionadas = new ArrayList<String>();
		this.obj = new ParametroProcessamentoAvaliacaoInstitucional();
		carregaLegendaPerguntas();
		formularioCombo = new LinkedList<SelectItem>();
	}

	/**
	 * Gera o relatório escolhido pelo usuário. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>
	 * /avaliacao/relatorios/busca_relatorio.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String relatorio() throws ArqException {
		validacaoAnoPeriodo(erros);
		if (hasErrors())
			return null;
		if (escolhaRelatorio == RELATORIO_TRANCAMENTO_PERIODO) {
			relatorioTrancamentoPeriodo();
		} else if (escolhaRelatorio == RELATORIO_DEPARTAMENTO_CENTRO) {
			relatorioDepartamentoCentro();
		} else if (escolhaRelatorio == RELATORIO_REPROVACOES) {
			relatorioReprovacoes();
		}
		return JSP_DIRECIONAR_LISTA_RELATORIOS;
	}

	/**
	 * Gera um Relatório quantitativo dos alunos que trancaram agrupando os
	 * mesmo por centro e departamento tendo com base os dados informados na
	 * busca_relatorio.jsp.<br>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioTrancamentoPeriodo() throws DAOException,
			ArqException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		result = dao.findCountTrancamentoPeriodo(ano, periodo);
		return forward(JSP_RELATORIO_TRANCAMENTO_POR_PERIODO);
	}

	/**
	 * Gera um Relatório quantitativo dos alunos que trancaram agrupando os
	 * mesmo por centro e departamento tendo com base os dados informados na
	 * busca_relatorio.jsp. <br>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioDepartamentoCentro() throws DAOException,
			ArqException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		lista = dao.findTrancamentoDepartamentoCentro(ano, periodo);
		return forward(JSP_RELATORIO_TRANCAMENTO);
	}

	/**
	 * Gera um Relatório quantitativo dos alunos reprovados por nota e falta,
	 * agrupando os mesmo por curso tendo com base os dados informados na
	 * busca_relatorio.jsp.<br>
	 * Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws ArqException
	 */
	public String relatorioReprovacoes() throws DAOException, ArqException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		lista = dao.findQuantitativoReprovados(ano, periodo);
		return forward(JSP_RELATORIO_REPROVADOS);
	}

	/**
	 * Retorna uma lista de SelectItem de departamentos do centro do usuário.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getUnidadesCombo() throws DAOException {
		if (unidades == null) {
			unidades = new ArrayList<SelectItem>();
			if (getUsuarioLogado().isUserInRole(SigaaPapeis.DIRETOR_CENTRO)) {
				UnidadeDao dao = getDAO(UnidadeDao.class);
				unidades.addAll(toSelectItems(dao.findBySubUnidades(
						getUsuarioLogado().getVinculoAtivo().getUnidade().getUnidadeGestora(),
						TipoUnidadeAcademica.DEPARTAMENTO), "id", "nome"));
			} else if (getUsuarioLogado().isUserInRole(
					SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL)) {
				UnidadeMBean unidadeMBean = new UnidadeMBean();
				unidades.addAll(unidadeMBean.getAllDepartamentoCombo());
			}
		}
		return unidades;
	}

	
	/** Carrega uma lista de formulários que foram aplicados no ano-período selecionado.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/form_nao_computado.jsp</li>
	 * </ul>
	 * @param evt
	 */
	public void carregaFormularios(ValueChangeEvent evt) {
		anoPeriodo = (Integer) evt.getNewValue();
		formularioCombo = new LinkedList<SelectItem>();
		GenericDAO dao = getGenericDAO();
		String[] fields = { "ano", "periodo"};
		Object[] values = {anoPeriodo / 10, anoPeriodo % 10} ;
		try {
			Collection<CalendarioAvaliacao> calendarios = dao.findByExactField(CalendarioAvaliacao.class, fields, values);
			if (!isEmpty(calendarios)) {
				for (CalendarioAvaliacao cal : calendarios)
					formularioCombo.add(new SelectItem(cal.getFormulario().getId(), cal.getFormulario().getTitulo()));
			}
		} catch (DAOException e) {
			notifyError(e);
		}
	}
	
	public Collection<SelectItem> getFormularioCombo() {
		return formularioCombo;
	}
	
	/**
	 * Retorna o ano utilizado para geração do relatório.
	 * 
	 * @return Ano utilizado para geração do relatório.
	 */
	public int getAno() {
		return ano;
	}

	/**
	 * Seta o período utilizado para geração do relatório.
	 * 
	 * @param periodo
	 *            Período utilizado para geração do relatório.
	 */
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	/**
	 * Retorna o índice do relatório escolhido para geração.
	 * 
	 * @return Índice do relatório escolhido para geração.
	 */
	public int getEscolhaRelatorio() {
		return escolhaRelatorio;
	}

	/**
	 * Seta o índice do relatório escolhido para geração.
	 * 
	 * @param escolhaRelatorio
	 *            Índice do relatório escolhido para geração.
	 */
	public void setEscolhaRelatorio(int escolhaRelatorio) {
		this.escolhaRelatorio = escolhaRelatorio;
	}

	/**
	 * Retorna o período utilizado para geração do relatório.
	 * 
	 * @return Período utilizado para geração do relatório.
	 */
	public int getPeriodo() {
		return periodo;
	}

	/**
	 * Seta o ano utilizado para geração do relatório.
	 * 
	 * @param ano
	 *            Ano utilizado para geração do relatório.
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/**
	 * Retorna o mapa com dados do relatório.
	 * 
	 * @return Mapa com dados do relatório.
	 */
	public Map<String, Integer> getResult() {
		return result;
	}

	/**
	 * Seta o mapa com dados do relatório.
	 * 
	 * @param result
	 *            Mapa com dados do relatório.
	 */
	public void setResult(Map<String, Integer> result) {
		this.result = result;
	}

	/**
	 * Retorna a lista com dados do relatório.
	 * 
	 * @return Lista com dados do relatório.
	 */
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	/**
	 * Seta a lista com dados do relatório.
	 * 
	 * @param lista
	 *            Lista com dados do relatório.
	 */
	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	/**
	 * Retorna o ID da unidade ao qual o relatório se restringe.
	 * 
	 * @return ID da unidade ao qual o relatório se restringe.
	 */
	public int getIdUnidade() {
		return idUnidade;
	}

	/**
	 * Seta o ID da unidade ao qual o relatório se restringe.
	 * 
	 * @param idUnidade
	 *            ID da unidade ao qual o relatório se restringe.
	 */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	/**
	 * Retorna uma coleção de SelectItem de Perguntas utilizadas na Avaliação
	 * Institucional.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getPerguntasComboBox() throws HibernateException, DAOException {
		if (perguntas == null) {
			perguntas = new ArrayList<Pergunta>();
		}
		Collection<SelectItem> perguntasCombo = new ArrayList<SelectItem>();
		int i = 0, k = 0;
		GrupoPerguntas grupoAnterior = new GrupoPerguntas();
		for (Pergunta p : perguntas) {
			if (p.getGrupo().getId() != grupoAnterior.getId()) {
				i++; k =1;
				grupoAnterior = p.getGrupo();
			}
			perguntasCombo.add(new SelectItem(p.getId(), i + "." + k +" - " + p.getDescricao()));
			k++;
		}
		return perguntasCombo;
	}

	/** Listenner responsável por carregar as perguntas utilizadas no formulário.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/relatorios/form_media_baixa.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException 
	 */
	public void carregaPerguntasAnoPeriodo(ValueChangeEvent evt) throws DAOException {
		int id = (Integer) evt.getNewValue();
		GenericDAO dao = getGenericDAO();
		ParametroProcessamentoAvaliacaoInstitucional parametro = dao.findByPrimaryKey(id, ParametroProcessamentoAvaliacaoInstitucional.class); 
		if (parametro == null) return;
		ano = parametro.getAno();
		periodo = parametro.getPeriodo();
		perguntas = new ArrayList<Pergunta>();
		Collection<GrupoPerguntas> grupos = parametro.getFormulario().getGrupoPerguntas();
		for (GrupoPerguntas grupo : grupos) {
			if (grupo.isAvaliaTurmas()) {
				for (Pergunta p : grupo.getPerguntas()) {
					// retirando a tag <br/> que está cadastrada no banco.
					if (p.getTipoPergunta().equals(TipoPergunta.PERGUNTA_NOTA)) {
						p.setDescricao(p.getDescricao().replaceAll("<br/>", ""));
						perguntas.add(p);
					}
				}
			}
		}
		Collections.sort((List<Pergunta>) perguntas, new Comparator<Pergunta>() {
			@Override
			public int compare(Pergunta o1, Pergunta o2) {
				int tmp = o1.getGrupo().getTitulo().compareTo(o2.getGrupo().getTitulo());
				if (tmp == 0) tmp = o1.getOrdem() - o2.getOrdem();
				return tmp;
			}
		});
	}
	
	/**
	 * Retorna a coleção de resultados para listagem do relatório.
	 * 
	 * @return Coleção de resultados para listagem do relatório.
	 */
	public List<ResultadoAvaliacaoDocente> getResultadosDocentes() {
		return resultadosDocentes;
	}

	/**
	 * Seta a coleção de resultados para listagem do relatório.
	 * 
	 * @param resultadosDocentes
	 *            Coleção de resultados para listagem do relatório.
	 */
	public void setResultadosDocentes(
			List<ResultadoAvaliacaoDocente> resultadosDocentes) {
		this.resultadosDocentes = resultadosDocentes;
	}

	/**
	 * Retorna a lista de IDs de perguntas selecionadas para serem detalhadas no
	 * relatório.
	 * 
	 * @return Lista de IDs de perguntas selecionadas para serem detalhadas no
	 *         relatório.
	 */
	public List<String> getPerguntasSelecionadas() {
		return perguntasSelecionadas;
	}

	/**
	 * Seta a lista de IDs de perguntas selecionadas para serem detalhadas no
	 * relatório.
	 * 
	 * @param perguntasSelecionadas
	 *            Lista de IDs de perguntas selecionadas para serem detalhadas
	 *            no relatório.
	 */
	public void setPerguntasSelecionadas(List<String> perguntasSelecionadas) {
		this.perguntasSelecionadas = perguntasSelecionadas;
	}

	/**
	 * Retorna o unidade ao qual se restringe o relatório.
	 * 
	 * @return Unidade ao qual se restringe o relatório.
	 */
	public Unidade getUnidade() {
		return unidade;
	}

	/**
	 * Seta o unidade ao qual se restringe o relatório.
	 * 
	 * @param unidade
	 *            Unidade ao qual se restringe o relatório.
	 */
	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	/**
	 * Indica se a busca por docentes deve ser filtrada por nome.
	 * 
	 * @return True, se a busca por docentes deve ser filtrada por nome.
	 */
	public boolean isCheckBuscaNome() {
		return checkBuscaNome;
	}

	/**
	 * Seta se a busca por docentes deve ser filtrada por nome.
	 * 
	 * @param checkBuscaNome
	 *            True, se a busca por docentes deve ser filtrada por nome.
	 */
	public void setCheckBuscaNome(boolean checkBuscaNome) {
		this.checkBuscaNome = checkBuscaNome;
	}

	/**
	 * Indica se a busca por docentes deve ser filtrada por departamento.
	 * 
	 * @return True, se a busca por docentes deve ser filtrada por departamento.
	 */
	public boolean isCheckBuscaDepartamento() {
		return checkBuscaDepartamento;
	}

	/**
	 * Seta se a busca por docentes deve ser filtrada por departamento.
	 * 
	 * @param checkBuscaDepartamento
	 *            True, se a busca por docentes deve ser filtrada por
	 *            departamento.
	 */
	public void setCheckBuscaDepartamento(boolean checkBuscaDepartamento) {
		this.checkBuscaDepartamento = checkBuscaDepartamento;
	}

	/**
	 * Retorna o nome (ou parte) do docente que se deseja buscar.
	 * 
	 * @return Nome (ou parte) do docente que se deseja buscar.
	 */
	public String getNomeDocente() {
		return nomeDocente;
	}

	/**
	 * Seta o nome (ou parte) do docente que se deseja buscar.
	 * 
	 * @param nomeDocente
	 *            Nome (ou parte) do docente que se deseja buscar.
	 */
	public void setNomeDocente(String nomeDocente) {
		this.nomeDocente = nomeDocente;
	}

	/**
	 * Retorna a lista de docentes encontrados na busca.
	 * 
	 * @return Lista de docentes encontrados na busca.
	 */
	public List<Map<String, Object>> getListaDocentes() {
		return listaDocentes;
	}

	/**
	 * Seta a lista de docentes encontrados na busca.
	 * 
	 * @param listaDocentes
	 *            Lista de docentes encontrados na busca.
	 */
	public void setListaDocentes(List<Map<String, Object>> listaDocentes) {
		this.listaDocentes = listaDocentes;
	}

	/**
	 * Indica se a busca por docentes deve ser filtrada por ano/período.
	 * 
	 * @return True, se a busca por docentes deve ser filtrada por ano/período.
	 */
	public boolean isCheckBuscaAnoPeriodo() {
		return checkBuscaAnoPeriodo;
	}

	/**
	 * Seta se a busca por docentes deve ser filtrada por ano/período.
	 * 
	 * @param checkBuscaAnoPeriodo
	 *            True, se a busca por docentes deve ser filtrada por
	 *            ano/período.
	 */
	public void setCheckBuscaAnoPeriodo(boolean checkBuscaAnoPeriodo) {
		this.checkBuscaAnoPeriodo = checkBuscaAnoPeriodo;
	}

	/**
	 * Retorna o docente a detalhar o resultado da Avaliação Institucional.
	 * 
	 * @return Docente a detalhar o resultado da Avaliação Institucional.
	 */
	public Servidor getDocente() {
		return docente;
	}

	/**
	 * Seta o docente a detalhar o resultado da Avaliação Institucional.
	 * 
	 * @param docente
	 *            Docente a detalhar o resultado da Avaliação Institucional.
	 */
	public void setDocente(Servidor docente) {
		this.docente = docente;
	}

	/**
	 * Retorna os dados agrupados do relatório individual do docente na
	 * Avaliação Institucional.
	 * 
	 * @return Dados agrupados do relatório individual do docente na Avaliação
	 *         Institucional.
	 */
	public Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> getDetalheRespostas() {
		return detalheRespostas;
	}

	/**
	 * Seta os dados agrupados do relatório individual do docente na Avaliação
	 * Institucional.
	 * 
	 * @param detalheRespostas
	 *            Dados agrupados do relatório individual do docente na
	 *            Avaliação Institucional.
	 */
	public void setDetalheRespostas(
			Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas) {
		this.detalheRespostas = detalheRespostas;
	}

	public Map<String, String> getLegendas() {
		return legendas;
	}

	public void setLegendas(Map<String, String> legendas) {
		this.legendas = legendas;
	}
	
	/** Retorna uma coleçaõ de ano-perídos processados.
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAnoPeriodoCombo() throws DAOException {
		if (anoPeriodoCombo == null) {
			AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
			Collection<ParametroProcessamentoAvaliacaoInstitucional> ultimos = dao.findUltimoProcessamentos();
			anoPeriodoCombo = new ArrayList<SelectItem>();
			if (ultimos != null)
				for (ParametroProcessamentoAvaliacaoInstitucional parametro : ultimos) {
					// monta o combo para usuários discentes ou docentes, de acordo com o perfil do usuário
					if (isPortalDiscente() && parametro.isConsultaDiscenteLiberada()
						|| isPortalDocente() && parametro.isConsultaDocenteLiberada()
						|| isPortalAvaliacaoInstitucional()
						|| isPortalReitoria() && parametro.isConsultaDiscenteLiberada()) {
						anoPeriodoCombo.add(new SelectItem(parametro.getId(), parametro.getAno() +"." +parametro.getPeriodo()
								+ (parametro.getFormulario().isEad() ? " - EAD" : "")));
					}
				}
		}
		return anoPeriodoCombo;
	}

	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}

	public double getMediaMinima() {
		return mediaMinima;
	}

	public void setMediaMinima(double mediaMinima) {
		this.mediaMinima = mediaMinima;
	}

	public Map<String, Map<String, Map<String, Integer>>> getDadosRelatorioNaoComputado() {
		return dadosRelatorioNaoComputado;
	}

	public void setDadosRelatorioNaoComputado(
			Map<String, Map<String, Map<String, Integer>>> dadosRelatorioNaoComputado) {
		this.dadosRelatorioNaoComputado = dadosRelatorioNaoComputado;
	}

	/**
	 * Retorna uma coleção de SelectItem de Perguntas utilizadas na Avaliação
	 * Institucional.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGrupoPerguntasComboBox()
			throws HibernateException, DAOException {
		if (grupoPerguntasCombo == null) {
			GrupoPerguntasDao grupoDao = getDAO(GrupoPerguntasDao.class);
			try {
				grupoPerguntasCombo = new ArrayList<SelectItem>();
				for (GrupoPerguntas grupo : grupoDao.findAll(GrupoPerguntas.class) )
					grupoPerguntasCombo.add(new SelectItem(new Integer(grupo.getId()), grupo.getTitulo() + " - " + grupo.getDescricao()));
				
			}finally {
				if (grupoDao != null)
					grupoDao.close();
			}
		}
		return grupoPerguntasCombo;
	}
	
	/**
	 * Retorna uma coleção de SelectItem de Perguntas utilizadas na Avaliação
	 * Institucional.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getGrupoPerguntasAvaliaDocenteComboBox() throws HibernateException, DAOException {
		if (grupoPerguntasCombo == null) {
			GrupoPerguntasDao grupoDao = getDAO(GrupoPerguntasDao.class);
			try {
				grupoPerguntasCombo = new ArrayList<SelectItem>();
				for (GrupoPerguntas grupo : grupoDao.findAll(GrupoPerguntas.class) )
					if (grupo.isAvaliaTurmas())
						grupoPerguntasCombo.add(new SelectItem(new Integer(grupo.getId()), grupo.getTitulo() + " - " + grupo.getDescricao()));
				
			}finally {
				if (grupoDao != null)
					grupoDao.close();
			}
		}
		return grupoPerguntasCombo;
	}
	
	/** Retorna uma coleção de observações dadas por docentes às turmas do discente.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ObservacoesDocenteTurma> getObservacoesTurmaDiscente() throws DAOException{
		return observacoesTurmaDiscente;
	}
	
	public boolean isMediaGeral() {
		return mediaGeral;
	}

	public void setMediaGeral(boolean mediaGeral) {
		this.mediaGeral = mediaGeral;
	}

	public Collection<MediaNotas> getResultadoMediaNotasBaixa() {
		return resultadoMediaNotasBaixa;
	}

	public void setResultadoMediaNotasBaixa(
			Collection<MediaNotas> resultadoMediaNotasBaixa) {
		this.resultadoMediaNotasBaixa = resultadoMediaNotasBaixa;
	}

	public Collection<ResultadoAvaliacaoDocente> getResultadoMediaGeralBaixa() {
		return resultadoMediaGeralBaixa;
	}

	public void setResultadoMediaGeralBaixa(
			Collection<ResultadoAvaliacaoDocente> resultadoMediaGeralBaixa) {
		this.resultadoMediaGeralBaixa = resultadoMediaGeralBaixa;
	}

	public Collection<DocenteTurma> getDocentesTurma() {
		return docentesTurma;
	}

	public void setDocentesTurma(Collection<DocenteTurma> docentesTurma) {
		this.docentesTurma = docentesTurma;
	}

	public boolean isEad() {
		return ead;
	}

	public void setEad(boolean ead) {
		this.ead = ead;
	}

	/** Totaliza as quantidade de alunos, disciplinas, turmas avaliadas e a quantidade de turmas não avaliadas.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/portal.jsp</li>
	 * </ul>
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public String atualizaEstatisticas() throws NegocioException, ArqException {
		try { 
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.ATUALIZAR_ESTATISTICAS_AVALIACAO_INSTITUCIONAL);
			prepareMovimento(SigaaListaComando.ATUALIZAR_ESTATISTICAS_AVALIACAO_INSTITUCIONAL);
			execute(mov);
			// carrega os parâmetros de ano e período
			GenericDAO dao = getGenericDAO();
			String order[] = {"formulario.tipoAvaliacao", "formulario.titulo", "ano", "periodo"};
			String ascDesc[] = {"asc", "asc", "asc", "asc"};
			dadosEstatisticos = dao.findAll(EstatisticaAvaliacaoInstitucional.class, order, ascDesc);
			if (isEmpty(dadosEstatisticos))
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		} catch (Exception e) {
			addMensagemErroPadrao();
			notifyError(e);
		}
		return forward("/avaliacao/relatorios/estatica_preenchimento.jsp");
	}
	
	/** Totaliza as quantidade de alunos, disciplinas, turmas avaliadas e a quantidade de turmas não avaliadas.
	 * @throws DAOException
	 */
	public Collection<EstatisticaAvaliacaoInstitucional> getEstatisticas() throws DAOException {
		return dadosEstatisticos;
	}

}