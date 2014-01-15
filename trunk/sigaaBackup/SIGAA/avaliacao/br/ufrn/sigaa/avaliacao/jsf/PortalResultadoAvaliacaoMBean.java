/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 05/01/2010
 *
 */
package br.ufrn.sigaa.avaliacao.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.dao.avaliacao.ComentarioAvaliacaoModeradoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dominio.GrupoPerguntas;
import br.ufrn.sigaa.avaliacao.dominio.MediaNotas;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesDocenteTurma;
import br.ufrn.sigaa.avaliacao.dominio.ObservacoesTrancamento;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.PercentualSimNao;
import br.ufrn.sigaa.avaliacao.dominio.ResultadoAvaliacaoDocente;
import br.ufrn.sigaa.avaliacao.dominio.TabelaRespostaResultadoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Controller responsável pela exibição de informações/resultados no mini-portal
 * de resultados da Avaliação Institucional.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("portalResultadoAvaliacao")
@Scope("session")
public class PortalResultadoAvaliacaoMBean extends SigaaAbstractController<ResultadoAvaliacaoDocente> {

	/**
	 * Constante que define o link para o portal de resultados da avaliação
	 * institucional.
	 */
	private static final String PAGINA_PRINCIPAL = "/avaliacao/resultado_docente/resumo.jsp";
	/**
	 * Constante que define o link para o detalhe de resultados da avaliação
	 * institucional de uma turma.
	 */
	private static final String DETALHE_TURMA = "/avaliacao/resultado_docente/detalhe_turma.jsp";
	/**
	 * Constante que define o link para relatório analítico da avaliação
	 * institucional.
	 */
	private static final String RELATORIO_ANALITICO_DOCENTE = "/avaliacao/resultado_docente/relatorio_analitico_docente.jsp";
	/** Lista de ano/período de resultados da avaliação do docente. */
	private List<Map<String, Object>> listaResultados;
	/** Aba selecionada na visualização de resultados. */
	private String abaSelecionada;
	/** Lista de resultados da avaliação do docente. */
	private List<ResultadoAvaliacaoDocente> resultadosDocentes;
	/** Respostas por pergunta da avaliação institucional. */
	private Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas;
	/** Média geral do docente no semestre. */
	private double mediaGeralSemestre;
	/** ID do resultado selecionado para detalhar as médias das notas */
	private int idResultado;
	/** Coleção de médias das notas da avaliação institucional do docente. */
	private List<MediaNotas> mediaNotas;
	/** Mapa das médias gerais da Avaliação Institucional por ano-período. */
	private Map<String, Double> mediasPorAnoPeriodo;
	/** Parâmetro do processamento da Avaliação Institucional. */
	private ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento;
	
	/**
	 * Coleção de percentual de respostas sim/não da avaliação institucional do
	 * docente.
	 */
	private List<PercentualSimNao> percentuaisSimNao;
	/** Turma selecionada para detalhar as médias da avaliação institucional. */
	private Turma turma;
	
	/** Grupo de perguntas utilizado para agrupar os gráficos no formulário. */
	private Collection<GrupoPerguntas> grupoPerguntas;
	
	/** Servidor o qual será gerado o relatório. */
	private Servidor servidor;
	
	/** Coleção com as observações dadas pelos discentes ao docente da turma na Avaliação Institucional. */
	private Collection<ObservacoesDocenteTurma> observacoesDocenteTurma;
	
	/** Coleção com as observações dadas pelos discentes aos motivos de trancamento da turma na Avaliação Institucional. */
	private Collection<ObservacoesTrancamento> observacoesTrancamento;
	
	/** Grupo de perguntas para o qual é calculado a média geral. */
	private GrupoPerguntas dimensaoMediaGeral;

	/** Construtor padrão. */
	public PortalResultadoAvaliacaoMBean() {
		super();
	}
	
	/**
	 * Inicializa a visualização dos resultados da avaliação institucional,
	 * setando a aba do último resultado processado.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String inicializa() throws HibernateException, DAOException {
		mediaGeralSemestre = 0;
		idResultado = 0;
		servidor = getUsuarioLogado().getServidorAtivo();
		this.observacoesDocenteTurma = null;
		this.observacoesTrancamento = null;
		return paginaInicial();
	}
	
	/** Redireciona o usuário à página inicial do portal de resultados da Avaliação Institucional.<br>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String paginaInicial() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		if( servidor != null ){
			listaResultados = dao.findResultadoByDocente(servidor.getId());
			// caso não seja usuário do Portal da Avaliação, remove da lista as avaliações não liberadas para consulta
			Collection<ParametroProcessamentoAvaliacaoInstitucional> processamentos = dao.findUltimoProcessamentos();
			if (!isPortalAvaliacaoInstitucional() && !ValidatorUtil.isEmpty(listaResultados)) {
				if (!ValidatorUtil.isEmpty(processamentos)) {
					for (ParametroProcessamentoAvaliacaoInstitucional processamento : processamentos) {
						// processamento não liberado
						if (!processamento.isConsultaDocenteLiberada()) {
							Iterator<Map<String, Object>> iterator = listaResultados.iterator();
							while (iterator.hasNext()) {
								Map<String, Object> resultado = iterator.next();
								if (resultado.get("ano").equals(processamento.getAno()) && resultado.get("periodo").equals(processamento.getPeriodo())) {
									iterator.remove();
									break;
								}
							}
						}
					}
				}
			}
		}	
		if (ValidatorUtil.isEmpty(listaResultados)) {
			addMensagemErro("Não há resultados disponíveis para consulta.");
			return null;
		}
		Map<String, Object> ultimoResultado = listaResultados.get(listaResultados.size() - 1);
		int ano = (Integer) ultimoResultado.get("ano");
		int periodo = (Integer) ultimoResultado.get("periodo");
		parametroProcessamento = dao.findUltimoProcessamento(ano, periodo);
		abaSelecionada = (String) ultimoResultado.get("anoPeriodo");
		turma = new Turma();
		carregaMedias();
		carregaMediasPorAnoPeriodo();		
		return forward(PAGINA_PRINCIPAL);
	}

	/**
	 * Carrega as médias das notas da avaliação institucional do docente.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaMedias() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		resultadosDocentes = dao
				.findResultadoByDocenteCentroDepartamentoAnoPeriodo(servidor.getId(), 0, 0, parametroProcessamento.getAno(), parametroProcessamento.getPeriodo());
		if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
			addMensagemErro("Não há registro de médias de avaliações para o ano-período");
			return;
		}
		String anoPeriodo = parametroProcessamento.getAno()+"."+parametroProcessamento.getPeriodo();
		carregaMediasPorAnoPeriodo();
		mediaGeralSemestre = getMediasPorAnoPeriodo().get(anoPeriodo) != null ? getMediasPorAnoPeriodo().get(anoPeriodo) : 0.0;
		carregaGrupoPerguntas();
	}
	
	/** Carrega a lista de grupos de perguntas. 
	 * @throws DAOException
	 */
	private void carregaGrupoPerguntas() throws DAOException {
		this.grupoPerguntas = parametroProcessamento.getFormulario().getGrupoPerguntas();
	}
	
	/** Carrega as médias gerais por ano-período.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaMediasPorAnoPeriodo() throws HibernateException, DAOException{
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		this.mediasPorAnoPeriodo =  dao.findEvolucaoMediaGeralAnoPeriodo(servidor.getId(), !isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL));
	}

	/** Retorna um dataModel das médias da Avaliação Institucional do docente.
	 * @return
	 */
	public DataModel getMediasDataModel() {
		return new ListDataModel(resultadosDocentes);
	}

	/**
	 * Carrega as notas da Avaliação Institucional do docente.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaNotas() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
			resultadosDocentes = dao.findResultadoByDocenteCentroDepartamentoAnoPeriodo(servidor.getId(), 0, 0, parametroProcessamento.getAno(), parametroProcessamento.getPeriodo());
			if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return;
			}
		}
		detalheRespostas = new LinkedHashMap<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>>();
		for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
			detalheRespostas.put(resultado.getDocenteTurma().getTurma(), dao.findRespostasAvaliacaoDocenteTurma(resultado.getDocenteTurma().getId(), parametroProcessamento.getAno(), parametroProcessamento.getPeriodo(), parametroProcessamento.isExcluirRepovacoesFalta()));
		}
	}

	/**
	 * Redireciona o usuário para o portal de resultados da avaliação
	 * institucional.<br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/resultado_docente/detalhe_turma.jsp</li>
	 * <li>/avaliacao/resultado_docente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String exibeResumo() throws HibernateException, DAOException {
		int ano = getParameterInt("ano");
		int periodo = getParameterInt("periodo");
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		parametroProcessamento = dao.findUltimoProcessamento(ano, periodo);
		carregaMedias();
		return forward(PAGINA_PRINCIPAL);
	}

	/**
	 * Detalha as médias dadas na avaliação institucional em uma determinada
	 * turma. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/resultado_docente/detalhe_turma.jsp</li>
	 * <li>/avaliacao/resultado_docente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String detalhaTurma() throws DAOException {
		this.idResultado = getParameterInt("idResultado");
		for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
			if (resultado.getId() == idResultado) {
				mediaNotas = (List<MediaNotas>) resultado.getMediaNotas();
				percentuaisSimNao = (List<PercentualSimNao>) resultado.getPercentualRespostasSimNao();
				break;
			}
		}
		if (mediaNotas == null){
			mediaNotas = new ArrayList<MediaNotas>();
		}
		if (percentuaisSimNao == null)
			percentuaisSimNao = new ArrayList<PercentualSimNao>();
		Collections.sort(mediaNotas);
		Collections.sort(percentuaisSimNao);
		return forward(DETALHE_TURMA);
	}

	/**
	 * Exibe o relatório analítico da avaliação institucional. <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/avaliacao/resultado_docente/resumo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String viewResultadoDocente() throws HibernateException,
			DAOException {
		carregaNotas();
		this.observacoesDocenteTurma = null;
		this.observacoesTrancamento = null;
		return forward(RELATORIO_ANALITICO_DOCENTE);
	}
	
	/**
	 * Exibe o relatório analítico da avaliação institucional. <br>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String viewResultadoDocente(int idServidor, int ano, int periodo) throws HibernateException,
			DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		servidor = dao.findByPrimaryKey(idServidor, Servidor.class);
		this.observacoesDocenteTurma = null;
		this.observacoesTrancamento = null;
		parametroProcessamento = dao.findUltimoProcessamento(ano, periodo);
		carregaMedias();
		carregaNotas();
		if (hasErrors())
			return null;
		return forward(RELATORIO_ANALITICO_DOCENTE);
	}
	
	/** Retorna uma coleção com as observações dadas pelos discentes ao docente da turma na Avaliação Institucional.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ObservacoesDocenteTurma> getObservacoesDocenteTurma() throws DAOException {
		if (observacoesDocenteTurma == null) {
			observacoesDocenteTurma = new ArrayList<ObservacoesDocenteTurma>();
			if (ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO)) {
				ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
				for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
					// incluir somente as observações moderadas.
					Collection<ObservacoesDocenteTurma> observacoes = dao.findObservacoesDocenteTurmaByDocenteTurma(resultado.getDocenteTurma().getId(), true); 
					if (observacoes != null) {
						for (ObservacoesDocenteTurma obs : observacoes) {
							if (!ValidatorUtil.isEmpty(obs.getObservacoesModeradas()))
								observacoesDocenteTurma.add(obs);
						}
					}
				}
			} 
		}
		return observacoesDocenteTurma;
	}
	
	/** Retorna uma coleção com as observações dadas pelos discentes aos motivos de trancamento da turma na Avaliação Institucional.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ObservacoesTrancamento> getObservacoesTrancamento() throws DAOException {
		if (observacoesTrancamento == null) {
			observacoesTrancamento = new ArrayList<ObservacoesTrancamento>();
			if (ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO)) {
				ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
				for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
					// incluir somente as observações moderadas.
					Collection<ObservacoesTrancamento> observacoes = dao.findObservacoesTrancamentosByDocenteTurma(resultado.getDocenteTurma().getTurma().getId());
					if (observacoes != null) {
						for (ObservacoesTrancamento obs : observacoes) {
							if (!ValidatorUtil.isEmpty(obs.getObservacoesModeradas()))
								observacoesTrancamento.add(obs);
						}
					}
				}
			} 
		}
		return observacoesTrancamento;
	}

	/** Retorna a descrição da Dimensão cujo o qual é calculada a média geral.
	 * <br>Método não invocado por JSP´s.
	 * @return
	 * @throws DAOException
	 */
	public String getDimensaoMediaGeral() throws DAOException {
		if (dimensaoMediaGeral == null) {
			int id = ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.ID_GRUPO_PERGUNTAS_MEDIA_GERAL_RESULTADO_AVALIACAO);
			dimensaoMediaGeral = getGenericDAO().findByPrimaryKey(id, GrupoPerguntas.class);
		}
		return dimensaoMediaGeral.getTitulo();
	}
	
	/** Retorna a lista de ano/período de resultados da avaliação do docente. 
	 * @return
	 */
	public List<Map<String, Object>> getListaResultados() {
		return listaResultados;
	}

	/** Seta a lista de ano/período de resultados da avaliação do docente.
	 * @param listaResultados
	 */
	public void setListaResultados(List<Map<String, Object>> listaResultados) {
		this.listaResultados = listaResultados;
	}

	/** Retorna a aba selecionada na visualização de resultados. 
	 * @return
	 */
	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	/** Seta a aba selecionada na visualização de resultados.
	 * @param abaSelecionada
	 */
	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	/** Retorna a lista de resultados da avaliação do docente. 
	 * @return
	 */
	public List<ResultadoAvaliacaoDocente> getResultadosDocentes() {
		return resultadosDocentes;
	}

	/** Seta a lista de resultados da avaliação do docente.
	 * @param resultadosDocentes
	 */
	public void setResultadosDocentes(
			List<ResultadoAvaliacaoDocente> resultadosDocentes) {
		this.resultadosDocentes = resultadosDocentes;
	}

	/** Retorna as respostas por pergunta da avaliação institucional. 
	 * @return
	 */
	public Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> getDetalheRespostas() {
		return detalheRespostas;
	}

	/** Seta as respostas por pergunta da avaliação institucional.
	 * @param detalheRespostas
	 */
	public void setDetalheRespostas(
			Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas) {
		this.detalheRespostas = detalheRespostas;
	}

	/** Retorna a média geral do docente no semestre. 
	 * @return
	 */
	public double getMediaGeralSemestre() {
		return mediaGeralSemestre;
	}

	/** Seta a média geral do docente no semestre.
	 * @param mediaGeralSemestre
	 */
	public void setMediaGeralSemestre(double mediaGeralSemestre) {
		this.mediaGeralSemestre = mediaGeralSemestre;
	}

	/** Retorna a coleção de médias das notas da avaliação institucional do docente. 
	 * @return
	 */
	public List<MediaNotas> getMediaNotas() {
		return mediaNotas;
	}

	/** Seta a coleção de médias das notas da avaliação institucional do docente.
	 * @param mediaNotas
	 */
	public void setMediaNotas(List<MediaNotas> mediaNotas) {
		this.mediaNotas = mediaNotas;
	}

	/** Retorna o ID do resultado selecionado para detalhar as médias das notas 
	 * @return
	 */
	public int getIdResultado() {
		return idResultado;
	}

	/** Seta o ID do resultado selecionado para detalhar as médias das notas 
	 * @param idResultado
	 */
	public void setIdResultado(int idResultado) {
		this.idResultado = idResultado;
	}

	/** Retorna a turma selecionada para detalhar as médias da avaliação institucional. 
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma selecionada para detalhar as médias da avaliação institucional.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna a coleção de percentual de respostas sim/não da avaliação institucional do docente.
	 * @return
	 */
	public List<PercentualSimNao> getPercentuaisSimNao() {
		return percentuaisSimNao;
	}

	/** Seta a coleção de percentual de respostas sim/não da avaliação institucional do docente.
	 * @param percentuaisSimNao
	 */
	public void setPercentuaisSimNao(
			List<PercentualSimNao> percentuaisSimNao) {
		this.percentuaisSimNao = percentuaisSimNao;
	}

	/** Retorna uma coleção de grupo de perguntas utilizado para agrupar os gráficos no formulário. 
	 * @return
	 */
	public Collection<GrupoPerguntas> getGrupoPerguntas() {
		return grupoPerguntas;
	}

	/** Retorna o mapa das médias gerais da Avaliação Institucional por ano-período.  
	 * @return
	 */
	public Map<String, Double> getMediasPorAnoPeriodo() {
		return mediasPorAnoPeriodo;
	}

	/** Retorna o Servidor o qual será gerado o relatório. 
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Seta o Servidor o qual será gerado o relatório. 
	 * @param servidor
	 */
	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public ParametroProcessamentoAvaliacaoInstitucional getParametroProcessamento() {
		return parametroProcessamento;
	}

	public void setParametroProcessamento(
			ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento) {
		this.parametroProcessamento = parametroProcessamento;
	}

	

}
