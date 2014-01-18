/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/04/2008
 * 
 */
package br.ufrn.sigaa.avaliacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.avaliacao.AvaliacaoInstitucionalDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.avaliacao.dominio.CalendarioAvaliacao;
import br.ufrn.sigaa.avaliacao.dominio.FormularioAvaliacaoInstitucional;
import br.ufrn.sigaa.avaliacao.dominio.ParametroProcessamentoAvaliacaoInstitucional;
import br.ufrn.sigaa.parametros.dominio.ParametrosAvaliacaoInstitucional;

/**
 * Controller respons�vel por exportar os dados brutos de uma Avalia��o
 * Institucional. Os dados brutos s�o compostos por informa��es referentes �
 * turma, componente curricular, discente ou docente, e respostas dadas na
 * Avalia��o Institucional.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("exportaAvaliacaoInstitucional")
@Scope("request")
public class ExportaAvaliacaoInstitucionalMBean extends SigaaAbstractController<ParametroProcessamentoAvaliacaoInstitucional> {

	/** Constante que indica se a exporta��o de dados ser� de discentes. */
	private static final int DADOS_DISCENTES = 1;
	/** Constante que indica se a exporta��o de dados ser� de de docentes. */
	private static final int DADOS_DOCENTES = 2;
	/** Constante que indica se a exporta��o de dados ser� de tutor EAD. */
	private static final int DADOS_TUTOR = 3;

	/** Indica se deve exportar somente os dados das avalia��es correspondente aos filtros aplicados. */
	private boolean somenteDadosFiltrados;
	
	/** Indica se deve aplicar um filtro aos dados exportados. */
	private boolean usarFiltro;

	/** Indica se a exporta��o de dados ser� de discentes, docentes ou tutor EAD. */
	private int tipoDados;
	
	/** Calend�rios dispon�veis para preenchimento de Avalia��o. */
	private Collection<CalendarioAvaliacao> calendariosAvaliacao;
	
	/** Calend�rio usado para preenchimento de Avalia��o. */
	private CalendarioAvaliacao calendario;
	
	/** Construtor padr�o. */
	public ExportaAvaliacaoInstitucionalMBean() {
		init();
	}

	/**
	 * Inicializa os atributos do controller.
	 * 
	 */
	private void init() {
		obj = new ParametroProcessamentoAvaliacaoInstitucional();
		int ano = CalendarUtils.getAnoAtual();
		int periodo = getPeriodoAtual();
		// sugere o ano-per�odo anterior para processar.
		if (periodo == 1) {
			ano--;
			periodo = 2;
		} else {
			periodo = 1;
		}
		obj.setAno(ano);
		obj.setPeriodo(periodo);
		obj.setNumMinAvaliacoes(ParametroHelper.getInstance().getParametroInt(ParametrosAvaliacaoInstitucional.NUM_MINIMO_AVALIACOES_PROCESSAMENTO_DOCENTE));
		calendario = new CalendarioAvaliacao();
		calendariosAvaliacao = null;
	}

	/** Retorna uma cole��o de todos calend�rios de avalia��o ativos.
	 * <br/>M�todo n�o invocado por JSP�s
	 * @return
	 * @throws DAOException 
	 * @throws ArqException
	 */
	public void carregaCalendariosAvaliacao() throws DAOException  {
		GenericDAO dao = getGenericDAO();
		calendariosAvaliacao =  dao.findByExactField(CalendarioAvaliacao.class, "ativo", Boolean.TRUE);
		// filtra por perfil do entrevistado e se est� no prazo para preencher
		if (!isEmpty(calendariosAvaliacao)) {
			Iterator<CalendarioAvaliacao> iterator = calendariosAvaliacao.iterator();
			while (iterator.hasNext()) {
				CalendarioAvaliacao cal = iterator.next();
				FormularioAvaliacaoInstitucional form = cal.getFormulario();
				if (tipoDados == DADOS_DISCENTES && !form.isAvaliacaoDiscente()
					|| tipoDados == DADOS_DOCENTES && !form.isAvaliacaoDocente()
					|| tipoDados == DADOS_TUTOR && !form.isAvaliacaoTutorEad())
					iterator.remove();
			}
		}
		// ordena por ano-per�odo
		Collections.sort((List<CalendarioAvaliacao>) calendariosAvaliacao, new Comparator<CalendarioAvaliacao>() {
			@Override
			public int compare(CalendarioAvaliacao o1, CalendarioAvaliacao o2) {
				return o2.getAno() * 10 + o2.getPeriodo() - (o1.getAno() * 10 + o1.getPeriodo());
			}
		});
	}
	
	/** Recupera os par�metros utilizados no processamento da Avalia��o Institucional <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * @param evt
	 * @throws DAOException
	 */
	public void formularioListener(ValueChangeEvent evt) throws DAOException {
		GenericDAO dao = getGenericDAO();
		calendario = dao.findByPrimaryKey((Integer) evt.getNewValue(), CalendarioAvaliacao.class);
		if (calendario == null) calendario = new CalendarioAvaliacao();
		String fields[] = {"ano", "periodo", "formulario.id"};
		Object values[] = {calendario.getAno(), calendario.getPeriodo(), calendario.getFormulario().getId()};
		List<ParametroProcessamentoAvaliacaoInstitucional> lista = (List<ParametroProcessamentoAvaliacaoInstitucional>) dao.findByExactField(ParametroProcessamentoAvaliacaoInstitucional.class, fields, values);
		if (!isEmpty(lista)) {
			Collections.sort((List<CalendarioAvaliacao>) calendariosAvaliacao, new Comparator<CalendarioAvaliacao>() {
				@Override
				public int compare(CalendarioAvaliacao o1, CalendarioAvaliacao o2) {
					return o2.getFim().compareTo(o1.getInicio());
				}
			});
			this.obj = lista.get(0);
		} else {
			this.obj = new ParametroProcessamentoAvaliacaoInstitucional();
		}
	}
	
	/**
	 * Inicia a opera��o de exporta��o de dados das Avalia��es respondidas por discentes. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarDiscente() throws DAOException {
		init();
		tipoDados = DADOS_DISCENTES; 
		return forward("/avaliacao/relatorios/exportar_dados.jsp");
	}

	/** Inicia a opera��o de exporta��o de dados das Avalia��es respondidas por docentes. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarDocente() {
		init();
		tipoDados = DADOS_DOCENTES; 
		return forward("/avaliacao/relatorios/exportar_dados.jsp");
	}
	
	/** Inicia a opera��o de exporta��o de dados das Avalia��es respondidas por tutor EAD. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarTutorEad() {
		init();
		tipoDados = DADOS_TUTOR; 
		return forward("/avaliacao/relatorios/exportar_dados.jsp");
	}

	/** Exporta os dados das Avalia��es. <br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/relatorios/exporta_dados.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String exportarDados() throws ArqException {
		ValidatorUtil.validateRequired(calendario, "Formul�rio", erros);
		if (hasErrors()) return null;
		
		int ano, periodo;
		if (isEmpty(obj)) {
			ano = calendario.getAno();
			periodo = calendario.getPeriodo();
		} else {
			ano = obj.getAno();
			periodo = obj.getPeriodo();
		}
		int idFormulario = calendario.getFormulario().getId();

		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		String tabelaCSV = null;
		switch (tipoDados) {
			case DADOS_DISCENTES:
				tabelaCSV = dao.exportarAvaliacaoDiscentes(ano, periodo, idFormulario, this.somenteDadosFiltrados);
				break;
			case DADOS_DOCENTES :
				tabelaCSV = dao.exportarAvaliacaoDocentes(ano, periodo, idFormulario);
				break;
			case DADOS_TUTOR:
				tabelaCSV = dao.exportarAvaliacaoTutorEad(ano, periodo, idFormulario);
				break;
			default:
				break;
		}
		
		if (isEmpty(tabelaCSV)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		// cria o arquivo com os dados
		try {
			PrintWriter out = getCurrentResponse().getWriter();
			String perfil = "";
			switch (tipoDados) {
				case DADOS_DISCENTES: perfil = " _discente_"; break;
				case DADOS_DOCENTES: perfil = "_docente_"; break;
				case DADOS_TUTOR: perfil = "_tutor_ead_"; break;
			default:
				break;
			}
			String nomeArquivo =  "dados" +perfil + obj.getAno() + "_" + obj.getPeriodo() + (somenteDadosFiltrados ? "_filtrado" : "" ) +".csv";
			
			getCurrentResponse().setContentType("text/csv");
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=\""+nomeArquivo+"\"");
			out.print(StringUtils.toAscii(tabelaCSV).toUpperCase());
			FacesContext.getCurrentInstance().responseComplete();
		} catch (IOException e) {
			throw new ArqException(e);
		}
		return null;
	}

	/**
	 * Retorna a legenda de c�digos de perguntas respondidas por discentes
	 * utilizados na exporta��o dos dados da Avalia��o Institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String legendaPerguntasDiscentes() throws ArqException {
		return legendaPerguntas(true);
	}

	/**
	 * Retorna a legenda de c�digos de perguntas respondidas por docentes
	 * utilizados na exporta��o dos dados da Avalia��o Institucional.<br>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/avaliacao/menus/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String legendaPerguntasDocentes() throws ArqException {
		return legendaPerguntas(false);
	}

	/** Retorna a legenda de c�digos de perguntas respondidas utilizados na exporta��o dos dados da Avalia��o Institucional.
	 * @param discente caso true, retorna a legenda de perguntas respondidas por discente. Caso false, respondidas por docentes.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private String legendaPerguntas(boolean discente)
			throws HibernateException, DAOException {
		AvaliacaoInstitucionalDao dao = getDAO(AvaliacaoInstitucionalDao.class);
		Map<String, String> legendas = dao.legendaDescricaoPerguntas(true, obj.getFormulario().getId());
		if (legendas == null || legendas.size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

		getCurrentRequest().setAttribute("legendas", legendas);
		return forward("/avaliacao/relatorios/legenda_perguntas.jsp");
	}

	/** Indica se deve exportar somente os dados das avalia��es correspondente aos filtros aplicados. 
	 * @return
	 */
	public boolean isSomenteDadosFiltrados() {
		return somenteDadosFiltrados;
	}

	/** Seta se deve exportar somente os dados das avalia��es correspondente aos filtros aplicados. 
	 * @param somenteDadosFiltrados
	 */
	public void setSomenteDadosFiltrados(boolean somenteDadosFiltrados) {
		this.somenteDadosFiltrados = somenteDadosFiltrados;
	}

	/** Indica se deve aplicar um filtro aos dados exportados. 
	 * @return
	 */
	public boolean isUsarFiltro() {
		return usarFiltro;
	}

	/** Seta se deve aplicar um filtro aos dados exportados. 
	 * @param usarFiltro
	 */
	public void setUsarFiltro(boolean usarFiltro) {
		this.usarFiltro = usarFiltro;
	}

	/** Retorna uma cole��o de selectItens de par�metros de processamentos. 
	 * @return
	 * @throws DAOException 
	 */
	public Collection<SelectItem> getCalendariosAvaliacaoCombo() throws DAOException{
		if (calendariosAvaliacao == null)
			carregaCalendariosAvaliacao();
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		for (CalendarioAvaliacao calendario : calendariosAvaliacao) {
			itens.add(new SelectItem(calendario.getId(), calendario.getAno()+"."+calendario.getPeriodo() + " - " 
					+ calendario.getFormulario().getTitulo() + " ("
					+ (calendario.getFormulario().isEad() ? "EAD" :
						calendario.getFormulario().isAvaliacaoDocenciaAssistida() ? "DOC�NCIA ASSISTIDA" : "PRESENCIAL" )+ ")"));
		}
		return itens;
	}

	public int getTipoDados() {
		return tipoDados;
	}

	public void setTipoDados(int tipoDados) {
		this.tipoDados = tipoDados;
	}

	/** Indica que os dados a serem exprotados s�o da avalia��o preenchida por discentes.
	 * @return
	 */
	public boolean isExportarDiscente() {
		return tipoDados == DADOS_DISCENTES;
	}
	
	/** Indica que os dados a serem exprotados s�o da avalia��o preenchida por docentes.
	 * @return
	 */
	public boolean isExportarDocentes() {
		return tipoDados == DADOS_DOCENTES;
	}
	
	/** Indica que os dados a serem exprotados s�o da avalia��o preenchida por tutores EAD.
	 * @return
	 */
	public boolean isExportarTutorEAD() {
		return tipoDados == DADOS_TUTOR;
	}

	public CalendarioAvaliacao getCalendario() {
		return calendario;
	}

	public void setCalendario(CalendarioAvaliacao calendario) {
		this.calendario = calendario;
	}
}
