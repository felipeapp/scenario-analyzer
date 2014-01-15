/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Controller respons�vel pela exibi��o de informa��es/resultados no mini-portal
 * de resultados da Avalia��o Institucional.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("portalResultadoAvaliacao")
@Scope("session")
public class PortalResultadoAvaliacaoMBean extends SigaaAbstractController<ResultadoAvaliacaoDocente> {

	/**
	 * Constante que define o link para o portal de resultados da avalia��o
	 * institucional.
	 */
	private static final String PAGINA_PRINCIPAL = "/avaliacao/resultado_docente/resumo.jsp";
	/**
	 * Constante que define o link para o detalhe de resultados da avalia��o
	 * institucional de uma turma.
	 */
	private static final String DETALHE_TURMA = "/avaliacao/resultado_docente/detalhe_turma.jsp";
	/**
	 * Constante que define o link para relat�rio anal�tico da avalia��o
	 * institucional.
	 */
	private static final String RELATORIO_ANALITICO_DOCENTE = "/avaliacao/resultado_docente/relatorio_analitico_docente.jsp";
	/** Lista de ano/per�odo de resultados da avalia��o do docente. */
	private List<Map<String, Object>> listaResultados;
	/** Aba selecionada na visualiza��o de resultados. */
	private String abaSelecionada;
	/** Lista de resultados da avalia��o do docente. */
	private List<ResultadoAvaliacaoDocente> resultadosDocentes;
	/** Respostas por pergunta da avalia��o institucional. */
	private Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas;
	/** M�dia geral do docente no semestre. */
	private double mediaGeralSemestre;
	/** ID do resultado selecionado para detalhar as m�dias das notas */
	private int idResultado;
	/** Cole��o de m�dias das notas da avalia��o institucional do docente. */
	private List<MediaNotas> mediaNotas;
	/** Mapa das m�dias gerais da Avalia��o Institucional por ano-per�odo. */
	private Map<String, Double> mediasPorAnoPeriodo;
	/** Par�metro do processamento da Avalia��o Institucional. */
	private ParametroProcessamentoAvaliacaoInstitucional parametroProcessamento;
	
	/**
	 * Cole��o de percentual de respostas sim/n�o da avalia��o institucional do
	 * docente.
	 */
	private List<PercentualSimNao> percentuaisSimNao;
	/** Turma selecionada para detalhar as m�dias da avalia��o institucional. */
	private Turma turma;
	
	/** Grupo de perguntas utilizado para agrupar os gr�ficos no formul�rio. */
	private Collection<GrupoPerguntas> grupoPerguntas;
	
	/** Servidor o qual ser� gerado o relat�rio. */
	private Servidor servidor;
	
	/** Cole��o com as observa��es dadas pelos discentes ao docente da turma na Avalia��o Institucional. */
	private Collection<ObservacoesDocenteTurma> observacoesDocenteTurma;
	
	/** Cole��o com as observa��es dadas pelos discentes aos motivos de trancamento da turma na Avalia��o Institucional. */
	private Collection<ObservacoesTrancamento> observacoesTrancamento;
	
	/** Grupo de perguntas para o qual � calculado a m�dia geral. */
	private GrupoPerguntas dimensaoMediaGeral;

	/** Construtor padr�o. */
	public PortalResultadoAvaliacaoMBean() {
		super();
	}
	
	/**
	 * Inicializa a visualiza��o dos resultados da avalia��o institucional,
	 * setando a aba do �ltimo resultado processado.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	
	/** Redireciona o usu�rio � p�gina inicial do portal de resultados da Avalia��o Institucional.<br>M�todo n�o invocado por JSP�s.
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String paginaInicial() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		if( servidor != null ){
			listaResultados = dao.findResultadoByDocente(servidor.getId());
			// caso n�o seja usu�rio do Portal da Avalia��o, remove da lista as avalia��es n�o liberadas para consulta
			Collection<ParametroProcessamentoAvaliacaoInstitucional> processamentos = dao.findUltimoProcessamentos();
			if (!isPortalAvaliacaoInstitucional() && !ValidatorUtil.isEmpty(listaResultados)) {
				if (!ValidatorUtil.isEmpty(processamentos)) {
					for (ParametroProcessamentoAvaliacaoInstitucional processamento : processamentos) {
						// processamento n�o liberado
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
			addMensagemErro("N�o h� resultados dispon�veis para consulta.");
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
	 * Carrega as m�dias das notas da avalia��o institucional do docente.
	 * 
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaMedias() throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		resultadosDocentes = dao
				.findResultadoByDocenteCentroDepartamentoAnoPeriodo(servidor.getId(), 0, 0, parametroProcessamento.getAno(), parametroProcessamento.getPeriodo());
		if (resultadosDocentes == null || resultadosDocentes.isEmpty()) {
			addMensagemErro("N�o h� registro de m�dias de avalia��es para o ano-per�odo");
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
	
	/** Carrega as m�dias gerais por ano-per�odo.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaMediasPorAnoPeriodo() throws HibernateException, DAOException{
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		this.mediasPorAnoPeriodo =  dao.findEvolucaoMediaGeralAnoPeriodo(servidor.getId(), !isUserInRole(SigaaPapeis.COMISSAO_AVALIACAO, SigaaPapeis.BOLSISTA_AVALIACAO_INSTITUCIONAL));
	}

	/** Retorna um dataModel das m�dias da Avalia��o Institucional do docente.
	 * @return
	 */
	public DataModel getMediasDataModel() {
		return new ListDataModel(resultadosDocentes);
	}

	/**
	 * Carrega as notas da Avalia��o Institucional do docente.
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
	 * Redireciona o usu�rio para o portal de resultados da avalia��o
	 * institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Detalha as m�dias dadas na avalia��o institucional em uma determinada
	 * turma. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Exibe o relat�rio anal�tico da avalia��o institucional. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
	 * Exibe o relat�rio anal�tico da avalia��o institucional. <br>M�todo n�o invocado por JSP�s.
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
	
	/** Retorna uma cole��o com as observa��es dadas pelos discentes ao docente da turma na Avalia��o Institucional.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ObservacoesDocenteTurma> getObservacoesDocenteTurma() throws DAOException {
		if (observacoesDocenteTurma == null) {
			observacoesDocenteTurma = new ArrayList<ObservacoesDocenteTurma>();
			if (ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO)) {
				ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
				for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
					// incluir somente as observa��es moderadas.
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
	
	/** Retorna uma cole��o com as observa��es dadas pelos discentes aos motivos de trancamento da turma na Avalia��o Institucional.
	 * @return
	 * @throws DAOException 
	 */
	public Collection<ObservacoesTrancamento> getObservacoesTrancamento() throws DAOException {
		if (observacoesTrancamento == null) {
			observacoesTrancamento = new ArrayList<ObservacoesTrancamento>();
			if (ParametroHelper.getInstance().getParametroBoolean(ParametrosAvaliacaoInstitucional.INCLUIR_COMENTARIOS_RELATORIO_ANALITICO)) {
				ComentarioAvaliacaoModeradoDao dao = getDAO(ComentarioAvaliacaoModeradoDao.class);
				for (ResultadoAvaliacaoDocente resultado : resultadosDocentes) {
					// incluir somente as observa��es moderadas.
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

	/** Retorna a descri��o da Dimens�o cujo o qual � calculada a m�dia geral.
	 * <br>M�todo n�o invocado por JSP�s.
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
	
	/** Retorna a lista de ano/per�odo de resultados da avalia��o do docente. 
	 * @return
	 */
	public List<Map<String, Object>> getListaResultados() {
		return listaResultados;
	}

	/** Seta a lista de ano/per�odo de resultados da avalia��o do docente.
	 * @param listaResultados
	 */
	public void setListaResultados(List<Map<String, Object>> listaResultados) {
		this.listaResultados = listaResultados;
	}

	/** Retorna a aba selecionada na visualiza��o de resultados. 
	 * @return
	 */
	public String getAbaSelecionada() {
		return abaSelecionada;
	}

	/** Seta a aba selecionada na visualiza��o de resultados.
	 * @param abaSelecionada
	 */
	public void setAbaSelecionada(String abaSelecionada) {
		this.abaSelecionada = abaSelecionada;
	}

	/** Retorna a lista de resultados da avalia��o do docente. 
	 * @return
	 */
	public List<ResultadoAvaliacaoDocente> getResultadosDocentes() {
		return resultadosDocentes;
	}

	/** Seta a lista de resultados da avalia��o do docente.
	 * @param resultadosDocentes
	 */
	public void setResultadosDocentes(
			List<ResultadoAvaliacaoDocente> resultadosDocentes) {
		this.resultadosDocentes = resultadosDocentes;
	}

	/** Retorna as respostas por pergunta da avalia��o institucional. 
	 * @return
	 */
	public Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> getDetalheRespostas() {
		return detalheRespostas;
	}

	/** Seta as respostas por pergunta da avalia��o institucional.
	 * @param detalheRespostas
	 */
	public void setDetalheRespostas(
			Map<Turma, Map<GrupoPerguntas, TabelaRespostaResultadoAvaliacao>> detalheRespostas) {
		this.detalheRespostas = detalheRespostas;
	}

	/** Retorna a m�dia geral do docente no semestre. 
	 * @return
	 */
	public double getMediaGeralSemestre() {
		return mediaGeralSemestre;
	}

	/** Seta a m�dia geral do docente no semestre.
	 * @param mediaGeralSemestre
	 */
	public void setMediaGeralSemestre(double mediaGeralSemestre) {
		this.mediaGeralSemestre = mediaGeralSemestre;
	}

	/** Retorna a cole��o de m�dias das notas da avalia��o institucional do docente. 
	 * @return
	 */
	public List<MediaNotas> getMediaNotas() {
		return mediaNotas;
	}

	/** Seta a cole��o de m�dias das notas da avalia��o institucional do docente.
	 * @param mediaNotas
	 */
	public void setMediaNotas(List<MediaNotas> mediaNotas) {
		this.mediaNotas = mediaNotas;
	}

	/** Retorna o ID do resultado selecionado para detalhar as m�dias das notas 
	 * @return
	 */
	public int getIdResultado() {
		return idResultado;
	}

	/** Seta o ID do resultado selecionado para detalhar as m�dias das notas 
	 * @param idResultado
	 */
	public void setIdResultado(int idResultado) {
		this.idResultado = idResultado;
	}

	/** Retorna a turma selecionada para detalhar as m�dias da avalia��o institucional. 
	 * @return
	 */
	public Turma getTurma() {
		return turma;
	}

	/** Seta a turma selecionada para detalhar as m�dias da avalia��o institucional.
	 * @param turma
	 */
	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	/** Retorna a cole��o de percentual de respostas sim/n�o da avalia��o institucional do docente.
	 * @return
	 */
	public List<PercentualSimNao> getPercentuaisSimNao() {
		return percentuaisSimNao;
	}

	/** Seta a cole��o de percentual de respostas sim/n�o da avalia��o institucional do docente.
	 * @param percentuaisSimNao
	 */
	public void setPercentuaisSimNao(
			List<PercentualSimNao> percentuaisSimNao) {
		this.percentuaisSimNao = percentuaisSimNao;
	}

	/** Retorna uma cole��o de grupo de perguntas utilizado para agrupar os gr�ficos no formul�rio. 
	 * @return
	 */
	public Collection<GrupoPerguntas> getGrupoPerguntas() {
		return grupoPerguntas;
	}

	/** Retorna o mapa das m�dias gerais da Avalia��o Institucional por ano-per�odo.  
	 * @return
	 */
	public Map<String, Double> getMediasPorAnoPeriodo() {
		return mediasPorAnoPeriodo;
	}

	/** Retorna o Servidor o qual ser� gerado o relat�rio. 
	 * @return
	 */
	public Servidor getServidor() {
		return servidor;
	}

	/** Seta o Servidor o qual ser� gerado o relat�rio. 
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
