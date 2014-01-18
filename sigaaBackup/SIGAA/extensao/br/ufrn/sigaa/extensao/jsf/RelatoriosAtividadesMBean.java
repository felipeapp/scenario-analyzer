/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 07/05/2008
 *
 */
package br.ufrn.sigaa.extensao.jsf; 

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;

import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.arq.web.jsf.AbstractController;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.dao.extensao.RelatorioTotalParticipanteExtensaoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.AreaTematica;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.DiscenteExtensao;
import br.ufrn.sigaa.extensao.dominio.EditalExtensao;
import br.ufrn.sigaa.extensao.dominio.LocalRealizacao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoParticipacaoAcaoExtensao;
import br.ufrn.sigaa.mensagens.MensagensExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * MBean utilizado para gerar relat�rios de extens�o simples via jsp ou jasper
 * reports.
 * 
 * @author Ilueny Santos
 * 
 */
@Scope(value = "request")
@Component(value = "relatoriosAtividades")
public class RelatoriosAtividadesMBean extends
		SigaaAbstractController<AtividadeExtensao> {

	/** Constante que armazena o endere�o de uma p�gina. */
	public static final String RELATORIO_TOTAL_ALUNOS_ENVOLVIDOS_BY_EDITAL = "Edital_Alunos_Envolvidos_em_acoes";
	/** Constante que armazena o endere�o de uma p�gina. */
	public static final String RELATORIO_TOTAL_BOLSAS_BY_EDITAL = "Edital_Bolsas_Solicitadas";
	/** Constante que armazena o endere�o de uma p�gina. */
	public static final String RELATORIO_TOTAL_ORCAMENTO_BY_EDITAL = "Edital_Orcamento_Solicitado";

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Integer ano;

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Date dataFim = CalendarUtils.descartarHoras(CalendarUtils.createDate(31, 11, CalendarUtils.getAnoAtual()));

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Date dataInicio = CalendarUtils.descartarHoras(CalendarUtils.createDate(01, 00, CalendarUtils.getAnoAtual()));

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Date dataFimConclusao = CalendarUtils.descartarHoras(CalendarUtils.createDate(31, 11, CalendarUtils.getAnoAtual()));

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Date dataInicioConclusao = CalendarUtils.descartarHoras(CalendarUtils.createDate(01, 00, CalendarUtils.getAnoAtual()));
	
	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private EditalExtensao edital = new EditalExtensao();

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private String nomeRelatorio = "";

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private String tituloAtividade;
	/** Atributo utilizado para representar o a situa��o da Atividade de Extens�o */
	private TipoSituacaoProjeto situacaoAtividade = new TipoSituacaoProjeto();
	/** Atributo utilizado para representar o tipo da Atividade de Extens�o */
	private TipoAtividadeExtensao tipoAtividadeExtensao = new TipoAtividadeExtensao();
	/** Atributo utilizado para representar o participante da A��o de Extens�o */
	private ParticipanteAcaoExtensao participante = new ParticipanteAcaoExtensao();

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Unidade unidade = new Unidade();

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private Unidade unidadeProponente = new Unidade();
	/** Atributo utilizado para representar o local de Realiza��o da A��o de Extens�o */
	private LocalRealizacao localRealizacao = new LocalRealizacao();

	/** Utilizado para armazenar informa��o inserida para gera��o de relat�rio. */
	private AreaTematica areaTematica = new AreaTematica();
	/** Atributo utilizado para representar se h� busca por A��es Associadas */
	private Boolean buscaAcoesAssociadas = false;

	/** Filtro ultilizado na busca por Unidade de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaUnidade = false;
	/** Filtro ultilizado na busca por Tipo de Participante de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaTipoParticipante = false;
	/** Filtro ultilizado na busca por Edital de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaEdital = false;
	/** Filtro ultilizado na busca por T�tulo da Atividade de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaTituloAtividade = false;
	/** Filtro ultilizado na busca por Situa��o da Atividade de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaSituacaoAtividade = false;
	/** Filtro ultilizado na busca por Tipo de Atividade de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaTipoAtividade = false;
	/** Filtro ultilizado na busca por Ano de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaAno = false;
	/** Filtro ultilizado na busca por Ano de in�cio de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaAnoInicio = false;
	/** Filtro ultilizado na busca por Per�odo de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaPeriodo = false;
	/** Filtro ultilizado na busca por Per�odo de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaPeriodoConclusao = false;
	/** Filtro ultilizado na busca por M�s de Conclus�o de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaMesConclusao = false;
	/** Filtro ultilizado na busca por Gerar Relat�rio de Relat�rios de Atividades de Extens�o. */
	private boolean checkGerarRelatorio = false;
	/** Filtro ultilizado na busca por A��o Associada de Relat�rios de Atividades de Extens�o. */
	private boolean checkBuscaAcaoAssociada = false;
	
	/** Utilizado para armazenar resultados de consultas ao banco */
	private Collection<AtividadeExtensao> atividadesLocalizadas;
	/** Utilizado para armazenar resultados de consultas ao banco */
	private Collection<Map<String,Object>> resultado; 
	/** Utilizado para armazenar resultados de consultas ao banco */
	private Collection<Object[]> resultadoQuantitativo;
	/** Atributo utilizado para representar o m�s de in�cio da A��o de Extens�o */
	private Integer mesInicio = null;
	/** Atributo utilizado para representar o m�s de Fim da A��o de Extens�o */
	private Integer mesFim = null;
	/** Atributo utilizado para representar os meses do ano */
	private String[] meses = { "Janeiro", "Fevereiro", "Mar�o", "Abril",
			"Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro",
			"Novembro", "Dezembro" };
	/** Atributo utilizado para representar o Nome da Situa��o na busca */
	private String buscaNomeSituacao;
	/** Atributo utilizado para representar o nome do Tipo da A��o na busca */
	private String buscaTipoAcao;
	/** Atributo utilizado para representar o nome do Tipo de Participante na busca */
	private String buscaTipoParticipante;
	/** Atributo utilizado para representar o nome da unidade na busca */
	private String buscaNomeUnidade;
	/** Atributo utilizado para representar as unidades respons�veis */
	private Collection<Unidade> unidadesResponsaveis = new ArrayList<Unidade>();
	/** Atributo utilizado para representar as unidades proponentes */
	private Collection<Unidade> unidadesProponentes = new ArrayList<Unidade>();
	/** 
	 * Atributo utilizado para selecionar o relat�rio de Total de Participantes atual
	 * 1 = RelatorioTotalParticipantesDocentesExtensao
	 * 2 = RelatorioTotalParticipantesDiscentesProjeto
	 * 3 = RelatorioTotalParticipantesDiscentesPlanoTrabalhoExtensao  
	 */
	private Integer selecionaRelatorio = 0;

	/**
	 * Respons�vel por emitir relat�rios de acordo com o edital e o nome do
	 * relat�rio.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_por_edital_form.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna pdf com o relat�rio selecionado.
	 */
	public String relatorioByEdital() {

	    if (edital.getId() == 0) {
		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital de Extens�o");
	    }
	    if (dataInicio == null) {
		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data de In�cio");
	    }		
	    if (dataFim == null) {
		addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Data Fim");
	    }


	    if (hasErrors()) {
		return null;
	    }
	    if (dataInicio.after(dataFim)) {
		addMensagem(MensagensArquitetura.DATA_INICIO_MENOR_FIM);
		return null;
		}
	    if (hasErrors()) {
			return null;
		    }
	    Connection con = null;
	    try {
		con = Database.getInstance().getSigaaConnection();
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		parametros.put("edital", edital.getId());
		parametros.put("ano", ano);
		parametros.put("inicio", dataInicio);
		parametros.put("fim", dataFim);
		parametros.put("subsistema", getSubSistema().getNome());
		parametros.put("subSistemaLink", getSubSistema().getLink());
		// nomeRelat�rio depende do m�todo iniciar chamado no passo anterior.
		JasperPrint prt = JasperFillManager.fillReport(getReportSIGAA(nomeRelatorio + ".jasper"), parametros, con);
		  if (prt.getPages().size() == 0) {
				addMensagemWarning("O relat�rio n�o possui dados.");
				return null;
			}
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
		getCurrentResponse().setContentType("application/pdf");
		getCurrentResponse().addHeader("Content-Disposition", "attachment; filename=" + nomeRelatorio + "_"
			+ sdf.format(new Date()) + ".pdf");
		JasperExportManager.exportReportToPdfStream(prt, getCurrentResponse().getOutputStream());
		FacesContext.getCurrentInstance().responseComplete();
	    } catch (Exception e) {
		notifyError(e);
	    } finally {
		Database.getInstance().close(con);
	    }

	    return null;
	}
	
	
	/**
	 * Realiza a busca de membros da atividade para auxiliar no form de busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_equipe_por_modalidade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String gerarRelatorioEquipePorModalidade() {
		
		erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(tipoAtividadeExtensao, "Tipo da A��o", erros);
		ValidatorUtil.validateRequired(situacaoAtividade, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividadesLocalizadas = dao.relatorioEquipePorModalidade(tipoAtividadeExtensao.getId(),situacaoAtividade.getId(), dataInicio, dataFim);
		
		Collection<Integer> listaInseridos = new ArrayList<Integer>();
		Collection<Integer> listaRepetidos = new ArrayList<Integer>();
		for(AtividadeExtensao at : atividadesLocalizadas) {
			for(MembroProjeto mp : at.getProjeto().getEquipe()) {
				if(listaInseridos.contains(mp.getPessoa().getId())) {
					listaRepetidos.add(mp.getPessoa().getId());
				}
				else
					listaInseridos.add(mp.getPessoa().getId());	
			}
		}
		
		for(AtividadeExtensao at : atividadesLocalizadas) {
			for(MembroProjeto mp : at.getProjeto().getEquipe()) {
				if(listaRepetidos.contains(mp.getPessoa().getId())) {
					mp.setSelecionado(true);
				}
			}
		}
		
		if(atividadesLocalizadas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}		
		
		return null;
	}
	
	/**
	 * Realiza a busca de discentes da atividade para auxiliar no form de busca.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_equipe_por_modalidade.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String gerarRelatorioDiscentePorModalidade() {
		
		erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(tipoAtividadeExtensao, "Tipo da A��o", erros);
		ValidatorUtil.validateRequired(situacaoAtividade, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividadesLocalizadas = dao.relatorioDiscentePorModalidade(tipoAtividadeExtensao.getId(),situacaoAtividade.getId(), dataInicio, dataFim);
		
		Collection<Integer> listaInseridos = new ArrayList<Integer>();
		Collection<Integer> listaRepetidos = new ArrayList<Integer>();
		for(AtividadeExtensao at : atividadesLocalizadas) {
			for(DiscenteExtensao de : at.getDiscentesSelecionados()) {
				if(listaInseridos.contains(de.getDiscente().getPessoa().getId())) {
					listaRepetidos.add(de.getDiscente().getPessoa().getId());
				}
				else
					listaInseridos.add(de.getDiscente().getPessoa().getId());	
			}
		}
		
		for(AtividadeExtensao at : atividadesLocalizadas) {
			for(DiscenteExtensao de : at.getDiscentesSelecionados()) {
				if(listaRepetidos.contains(de.getDiscente().getPessoa().getId())) {
					de.setSelecionado(true);
				}
			}
		}
		
		if(atividadesLocalizadas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}		
		
		return null;
	}
	
	/**
	 * Realiza a busca de A��es com financiamento interno(FAEX) ou externo.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_financiamento_interno_externo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String gerarRelatorioAcoesFinanciamentoInternoExterno() {
		
		erros = new ListaMensagens();
		
		ValidatorUtil.validateRequired(tipoAtividadeExtensao, "Tipo da A��o", erros);
		ValidatorUtil.validateRequired(situacaoAtividade, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividadesLocalizadas = dao.relatorioFinanciamentoInternoExterno(tipoAtividadeExtensao.getId(),situacaoAtividade.getId(), dataInicio, 
				dataFim, edital.getId(), areaTematica.getId());		
		
		if(atividadesLocalizadas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
		}		
		
		return null;
	}
	
	/**
	 * Utilizado para gerar o relat�rio de a��es por localidade.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_financiamento_interno_externo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorioAcoesPorLocalRealizacao() throws DAOException {
		
		erros = new ListaMensagens();		
		
		ValidatorUtil.validateRequired(situacaoAtividade, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		resultado = dao.relatorioAcoesPorLocalRealizacao(tipoAtividadeExtensao.getId(),situacaoAtividade.getId(), dataInicio, 
				dataFim);		
		
		if(resultado == null || (resultado != null && resultado.isEmpty())){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}		
		
		getGenericDAO().initialize(tipoAtividadeExtensao);
		getGenericDAO().initialize(situacaoAtividade);
		return forward(ConstantesNavegacao.RELATORIO_ACOES_POR_LOCALIDADE);
	}
	
	/**
	 * Utilizado para gerar o relat�rio de a��es por localidade.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_financiamento_interno_externo.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorioNominalAcoesPorLocalRealizacao() throws DAOException {
		
		erros = new ListaMensagens();
		
		
		ValidatorUtil.validateRequired(localRealizacao.getMunicipio(), "Local de Realiza��o", erros);
		ValidatorUtil.validateRequired(situacaoAtividade, "Situa��o da A��o", erros);
		ValidatorUtil.validateRequired(dataInicio, "Data in�cio do Per�odo de Execu��o da A��o", erros);
		ValidatorUtil.validateRequired(dataFim, "Data Fim do Per�odo de Execu��o da A��o", erros);
		
		if(!erros.isEmpty()) {
			addMensagens(erros);
			return null;
		}		
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividadesLocalizadas = dao.relatorioNominalAcoesPorLocalRealizacao(null,situacaoAtividade.getId(), dataInicio,dataFim, 
				localRealizacao.getMunicipio().getId());		
		
		if(atividadesLocalizadas.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}		
		
		getGenericDAO().initialize(localRealizacao.getMunicipio());
		getGenericDAO().initialize(situacaoAtividade);
		return forward(ConstantesNavegacao.RELATORIO_NOMINAL_ACOES_POR_LOCALIDADE);
	}
	
	/**
	 * Utilizado para gerar o relat�rio de Total de Docentes Participantes de A��es de Extens�o.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_total_participantes_acao_extensao_form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioTotalParticipantesAcaoExtensao() throws DAOException{
		erros = new ListaMensagens();
		ValidatorUtil.validateRequired(dataInicio, "Per�odo inicial", erros);
		ValidatorUtil.validateRequired(dataFim, "Per�odo final", erros);
		ValidatorUtil.validaOrdemTemporalDatas(dataInicio, dataFim, false, "Per�odo", erros);
		
		if( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}
		
		RelatorioTotalParticipanteExtensaoDao dao =  getDAO(RelatorioTotalParticipanteExtensaoDao.class);
		
		if(selecionaRelatorio == 1){
			resultado = dao.relatorioTotalParticipantesDocentesExtensao(unidade.getId(), dataInicio, dataFim);
		}
		
		if(selecionaRelatorio == 2){
			resultado = dao.relatorioTotalParticipantesDiscentesProjeto(unidade.getId(), dataInicio, dataFim);
		}
		
		if(selecionaRelatorio == 3) {
			resultado = dao.relatorioTotalParticipantesDiscentesPlanoTrabalhoExtensao(unidade.getId(), dataInicio, dataFim);
		}
		
		if(resultado != null){
			if(resultado.size() > 0){
				return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_REL);
			}else{
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
		}else{
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
	}
	
	/**
	 * Inicia relat�rio com os totais de alunos envolvidos em todas as a��es de
	 * extens�o cadastradas para o edital informado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * 		<li>/sigaa.war/portais/rh_plan/abas/extensao.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 */
	public String iniciarRelatorioEditalTotalAlunos() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO , SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = RELATORIO_TOTAL_ALUNOS_ENVOLVIDOS_BY_EDITAL;
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_ACOES_POR_EDITAL_FORM);
	}

	/**
	 * Inicia o relat�rio de bolsas solicitadas para o edital informado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 */
	public String iniciarRelatorioEditalBolsasSolicitadas() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO , SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = RELATORIO_TOTAL_BOLSAS_BY_EDITAL;
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_ACOES_POR_EDITAL_FORM);
	}

	/**
	 * Inicia o relat�rio de or�amento solicitado para o edital informado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 * 
	 */
	public String iniciarRelatorioOrcamentoSolicitado() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO , SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = RELATORIO_TOTAL_ORCAMENTO_BY_EDITAL;
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_ACOES_POR_EDITAL_FORM);
	}

	/**
	 * Leva para a tela de gerar relat�rio do p�blico participante da a��o de
	 * extens�o.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 */
	public String iniciarRelatorioPublicoEstimado() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,SigaaPapeis.PORTAL_PLANEJAMENTO);
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_PUBLICO_ESTIMADO_FORM);
	}
	
	
	/**
	 * Redireciona para a p�gina do Relat�rio Nominal do Total de Membros por Tipo
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioEquipePorModalidade() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_EQUIPE_POR_MODALIDADE);
	}
	
	/**
	 * Redireciona para a p�gina do Relat�rio Nominal do Total de Discentes de Extens�o por Tipo
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioDiscenteExtensaoPorModalidade() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.RELATORIO_DISCENTE_POR_MODALIDADE);
	}
	
	
	/**
	 * Redireciona para a p�gina do Relat�rio de A��es com Financiamento Interno Faex e
	 * Financiamento externo.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioAcoesFinanciamentoInternoFaexExterno() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.RELATORIO_FINANCIAMENTO_INTERNO_EXTERNO);
	}
	
	/**
	 * Redireciona para a p�gina do Relat�rio NOMINAL de A��es por Local de Realiza��o
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioNominalAcoesLocalidade() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.RELATORIO_NOMINAL_ACOES_LOCAL_REALIZACAO);
	}
	
	/**
	 * Redireciona para a p�gina do Relat�rio do total de A��es por Local de Realiza��o
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioAcoesLocalRealizacao() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);		
		return forward(ConstantesNavegacao.RELATORIO_ACOES_LOCAL_REALIZACAO);
	}
	

	/**
	 * Leva para a tela de gerar relat�rio do p�blico participante da a��o de
	 * extens�o. � um relat�rio mais detalhado em rela��o ao tipo de p�blico alvo.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 */
	public String iniciarRelatorioTotalParticipantes() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_FORM);
	}

	/**
	 * Leva para a tela de gerar relat�rio de descentraliza��o de recursos da
	 * Faex.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores de extens�o podem
	 *             executar esse m�todo.
	 */
	public String iniciarRelatorioDescentralizacaoRecursosFaex() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO);
		ano = CalendarUtils.getAnoAtual();
		return forward(ConstantesNavegacao.RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_FORM);
	}
	
	/**
	 * Leva para a tela de gerar o relat�rio de participantes de A��o de Extens�o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina com formul�rio para sele��o dos par�metros
	 * @throws SegurancaException
	 * 				Gerada por checkRole. Somente gestores de extens�o podem
	 *              executar esse m�todo.
	 */
	public String iniciarRelatorioTotalParticipantesDocentesExtensao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = "Total de Docentes Participantes de A��es de Extens�o";
		selecionaRelatorio = 1;
		return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_FORM);
	}
	
	/**
	 * Leva para a tela de gerar o relat�rio de participantes de A��o de Extens�o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioTotalParticipantesProjetoDiscentes() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = "Total de Discentes nas equipes de projetos participantes de A��es de Extens�o";
		selecionaRelatorio = 2;
		return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_FORM);
	}
	
	/**
	 * Leva para a tela de gerar o relat�rio de participantes de A��o de Extens�o
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciarRelatorioTotalDiscentesParticipantesPlanoTrabalhoExtensao() throws SegurancaException{
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,SigaaPapeis.PORTAL_PLANEJAMENTO);
		nomeRelatorio = "Total de Discentes com planos de trabalho participantes de A��es de Extens�o";
		selecionaRelatorio = 3;
		return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_ACAO_EXTENSAO_FORM);
	}

	/**
	 * Realiza a totaliza��o do p�blico alvo atingido e estimado nas a��es de
	 * extens�o.
	 * Utiliza o dado informado no cadastro do relat�rio.
	 * 
	 * @throws SegurancaException  Somente gestores e coordenadores de extens�o podem executar.
	 * @throws DAOException Gerado pelas buscas no banco.
	 */
	private void buscarPublicoEstimadoAtendido() throws SegurancaException, DAOException {
	    checkRole(SigaaPapeis.GESTOR_EXTENSAO,
		    SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO,
		    SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
		    SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO,
		    SigaaPapeis.PORTAL_PLANEJAMENTO);

	    ListaMensagens mensagens = new ListaMensagens();
	    erros.getMensagens().clear();
	    
	    if (resultadoQuantitativo != null) {
		resultadoQuantitativo.clear();
	    }

	    Integer[] idSituacao = new Integer[0];
	    Date buscaDataInicio = null;
	    Date buscaDataFim = null;	    
	    Date buscaDataInicioConclusao = null;
	    Date buscaDataFimConclusao = null;


	    // Defini��o dos filtros e valida��es
	    if (checkBuscaSituacaoAtividade) {
			getGenericDAO().initialize(situacaoAtividade);
			idSituacao = new Integer[1];
			idSituacao[0] = 0;
			if (situacaoAtividade != null) {
			    idSituacao[0] = situacaoAtividade.getId();
			}
	    }
	    if (checkBuscaPeriodo) {
			if (dataInicio == null || dataFim == null) {
				mensagens.addMensagem(MensagensExtensao.PERIODO_INVALIDO);
			}
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Data inicial deve ser maior que data final", mensagens);
			
			buscaDataInicio = dataInicio;
			buscaDataFim = dataFim;
	    }

	    if (checkBuscaPeriodoConclusao) {
			if (dataInicioConclusao == null || dataFimConclusao == null) {
				mensagens.addMensagem(MensagensExtensao.PERIODO_INVALIDO);
			}
			ValidatorUtil.validaInicioFim(dataInicioConclusao, dataFimConclusao, "Data inicial deve ser maior que data final", mensagens);
			buscaDataInicioConclusao = dataInicioConclusao;
			buscaDataFimConclusao = dataFimConclusao;
	    }

	    if ((!checkBuscaSituacaoAtividade) && (!checkBuscaPeriodo) && (!checkBuscaPeriodoConclusao)) {		
	    	addMensagem(MensagensArquitetura.SELECIONE_OPCAO_BUSCA);
		} else if (mensagens.isEmpty()) {
			resultadoQuantitativo = getDAO(AtividadeExtensaoDao.class).totaisPublicoEstimadoAtendido(idSituacao, buscaDataInicio, buscaDataFim, buscaDataInicioConclusao, buscaDataFimConclusao);
		} else {
			addMensagens(mensagens);
	    }
	}

	/**
	 * Busca os dados, exibe o relat�rio ou a lista de itens buscados em jsp.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/publico_estimado_form.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna relat�rio em jsp.
	 * @throws DAOException  Gerado pelas buscas no banco.
	 * @throws SegurancaExceptionSomente gestores e coordenadores de extens�o podem executar esse m�todo.
	 */
	public String relatorioPublicoEstimadoAtendido() throws SegurancaException, DAOException {
	    buscarPublicoEstimadoAtendido();
	    if (isCheckGerarRelatorio()) {
		return forward(ConstantesNavegacao.RELATORIO_PUBLICO_ESTIMADO_REL);
	    } else {
		return null;
	    }
	}

	/**
	 * Realiza a totaliza��o do p�blico atingido pelas a��es de extens�o
	 * detalhando as unidades das a��es dos projetos, o tipo de p�blico
	 * atingido, o tipo de a��es de extens�o, etc. Busca realizada com base nos
	 * Participantes cadastrados pelos coordenadores das a��es.
	 * 
	 * @throws DAOException Gerado pelas buscas no banco.
	 * @throws SegurancaException Somente gestores e coordenadores de extens�o podem executar esse m�todo.
	 */
	private void totalizarParticipantes() throws DAOException,
			SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);

		ListaMensagens mensagens = new ListaMensagens();
		erros.getMensagens().clear();
		if (resultadoQuantitativo != null) {
			resultadoQuantitativo.clear();
		}

		Integer anoAtividade = null;
		Integer tipoAtividade = null;
		Integer tipoParticipante = null;
		Integer unid = null;
		Date buscaDataInicio = null;
		Date buscaDataFim = null;

		if (checkBuscaAno) {
			ValidatorUtil.validaInt(ano, "Ano da A��o", mensagens);
			if (ano != null && ano > CalendarUtils.getAnoAtual()) {
				mensagens.addMensagem(MensagensExtensao.ANO_INVALIDO);
			}
			anoAtividade = ano;
		}
		if ((checkBuscaTipoAtividade) && (tipoAtividadeExtensao != null)) {
			getGenericDAO().initialize(tipoAtividadeExtensao);
			tipoAtividade = tipoAtividadeExtensao.getId();
		}
		if ((checkBuscaTipoParticipante) && (participante.getTipoParticipacao() != null)) {
			getGenericDAO().initialize(participante.getTipoParticipacao());
			tipoParticipante = participante.getTipoParticipacao().getId();
		}
		if ((checkBuscaUnidade) && (unidade != null)) {
			getGenericDAO().initialize(unidade);
			unid = unidade.getId();
		}
		if (checkBuscaPeriodo) {
			if (dataInicio == null || dataFim == null) {
				mensagens.addMensagem(MensagensExtensao.PERIODO_INVALIDO);
			}
			ValidatorUtil.validaInicioFim(dataInicio, dataFim, "Data inicial deve ser maior que data final", mensagens);
			buscaDataInicio = dataInicio;
			buscaDataFim = dataFim;
		}

		if (!checkBuscaAno && !checkBuscaTipoAtividade
				&& !checkBuscaTipoParticipante && !checkBuscaUnidade
				&& !checkBuscaAnoInicio && !checkBuscaPeriodo) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO);
		} else if (mensagens.isEmpty()) {
			resultadoQuantitativo = getDAO(AtividadeExtensaoDao.class).totaisParticipantes(anoAtividade, 
				tipoAtividade, tipoParticipante, unid, buscaDataInicio,	buscaDataFim);
		} else {
			addMensagens(mensagens);
		}
	}

	/**
	 * Exibe o relat�rio ou a lista de tipos de p�blico que participa das a��es
	 * de extens�o buscado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/Relatorios/relatorio_participantes_form.jsp</li>
	 * </ul>
	 * 
	 * @return P�gina jsp com relat�rio segundo os par�metros selecionados.
	 * @throws DAOException
	 *             Gerado pelas buscas no banco.
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores e coordenadores de
	 *             extens�o podem executar esse m�todo.
	 */
	public String relatorioTotaisParticipantes() throws DAOException, SegurancaException {
	    totalizarParticipantes();
	    if (isCheckGerarRelatorio()) {
		return forward(ConstantesNavegacao.RELATORIO_TOTAL_PARTICIPANTES_REL);
	    } else {
		return null;
	    }
	}

	/**
	 * Realiza a totaliza��o dos recursos recebidos por todas as a��es de
	 * extens�o aprovadas para o edital informado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/Relatorios/descentralizacao_recursos_faex_form.jsp</li>
	 * </ul>
	 * 
	 * @throws DAOException
	 *             Gerado pelas buscas no banco.
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores e coordenadores de
	 *             extens�o podem executar esse m�todo.
	 */
	public void buscarDescentralizacaoRecursosFaex() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_EXTENSAO,
				SigaaPapeis.APOIO_TECNICO_COORDENACAO_EXTENSAO,
				SigaaPapeis.COORDENADOR_CURSOS_EVENTOS_PRODUTOS_EXTENSAO,
				SigaaPapeis.COORDENADOR_PROGRAMAS_PROJETOS_EXTENSAO);

		erros.getMensagens().clear();
		if (atividadesLocalizadas != null) {
			atividadesLocalizadas.clear();
		}

		Integer idEdital = null;
		Integer idAreaTematica = null;
		Unidade uni = null;
		Boolean associadas = null;

		if ((edital != null) && (edital.getId() != 0)) {
			idEdital = edital.getId();
			getGenericDAO().initialize(edital);
		}
		if ((unidade != null) && (unidade.getId() != 0)) {
			uni = unidade;
			getGenericDAO().initialize(unidade);
		}
		if ((areaTematica != null) && (areaTematica.getId() != 0)) {
			idAreaTematica = areaTematica.getId();
			getGenericDAO().initialize(areaTematica);
		}
		if (buscaAcoesAssociadas != null) {
			associadas = buscaAcoesAssociadas;
		}

		
		atividadesLocalizadas = getDAO(AtividadeExtensaoDao.class)
				.findAcoesOrcamentosConsolidadosByCentro(idEdital, uni,
						idAreaTematica, associadas );
	}

	/**
	 * Exibe o relat�rio ou a lista de dados encontrados na busca dos recursos
	 * solicitados por todas as a��es aprovadas no edital informado. Relat�rio
	 * utilizado para descentraliza��o dos recursos da faex para os centros.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/Relatorios/descentralizacao_recursos_faex_form.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna o relat�rio ou a mesma p�gina com a lista dos relat�rios
	 *         selecionados.
	 * @throws DAOException
	 *             Gerado pelas buscas no banco.
	 * @throws SegurancaException
	 *             Gerada por checkRole. Somente gestores e coordenadores de
	 *             extens�o podem executar esse m�todo.
	 */
	public String relatorioDescentralizacaoRecursosFaex() throws DAOException,
			SegurancaException {
		if ((edital == null) || (edital.getId() == 0)) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Edital");
			return redirectMesmaPagina();
		}
		buscarDescentralizacaoRecursosFaex();
		return forward(ConstantesNavegacao.RELATORIO_DESCENTRALIZACAO_RECURSOS_FAEX_REL);
	}

	/**
	 * Lista todos os tipos de A��o de extens�o em um combo.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/Relatorios/publico_estimado_detalhado_form.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna um {@link SelectItem} com todos os tipos de a��o de
	 *         extens�o.
	 */
	public Collection<SelectItem> getTipoAtividadeCombo() {
		return getAll(TipoAtividadeExtensao.class, "id", "descricao");
	}

	/**
	 * Lista todos os tipos de A��o de participante em um combo.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/relatorios/publico_estimado_detalhado_form.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna Cole��o de {@link SelectItem} com todos os tipos de
	 *         participante de extens�o.
	 */
	public Collection<SelectItem> getTipoParticipanteCombo() {
		return getAll(TipoParticipacaoAcaoExtensao.class, "id", "descricao");
	}

	/**
	 * Retorna lista de unidades respons�veis que solicitaram recursos para o
	 * edital selecionado.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/extensao/relatorios/descentralizacao_recursos_faex.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna Cole��o de {@link SelectItem} com Unidades.
	 */
	@SuppressWarnings("unchecked")
	public List<SelectItem> getUnidadesResponsaveisEditalAtualCombo() {
		// carga autom�tica de todas as unidades do edital selecionado em outro
		// combo como o bean � de request a lista deve ser enviada para sess�o
		unidadesResponsaveis = (Collection<Unidade>) getCurrentSession()
				.getAttribute("relatoriosAtividadesMBean.unidadesResponsaveis");
		return toSelectItems(unidadesResponsaveis, "id", "nome");
		
		//((CancelarBolsaMonitoriaMBean)getMBean("cancelarBolsaMonitoria")).setMensagem("");
	}

	/**
	 * Retorna lista de unidades respons�veis que solicitaram recursos para o
	 * edital selecionado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/relatorios/descentralizacao_recursos_faex.jsp</li>
	 * </ul>
	 * 
	 * @return Retorna Cole��o de {@link SelectItem} com Unidades.
	 */	
	@SuppressWarnings("unchecked")
	public List<SelectItem> getUnidadesProponentesEditalAtualCombo() {
		// carga autom�tica de todas as unidades do edital selecionado em outro
		// combo como o bean � de request a lista deve ser enviada para sess�o
		
		unidadesProponentes = (Collection<Unidade>) getCurrentSession()
				.getAttribute("relatoriosAtividadesMBean.unidadesProponentes");
		
		return toSelectItems(unidadesProponentes, "id", "nome");
	}
	
	@Override
	public String cancelar() {
		// removendo da sessao
		if(dataInicio != null || dataFim != null){
			
			resetBean();
		}
		
		
		try {
			redirectJSF(getSubSistema().getLink());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * M�todo utilizado para atualizar a lista de todas as unidades que
	 * participaram enviando propostas para o edital selecionado.
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 		<li>/sigaa.war/extensao/relatorios/descentralizacao_recursos_faex.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 *            {@link ValueChangeEvent} com valor alterado na view.
	 */
	public void changeEdital(ValueChangeEvent evt) {
		AtividadeExtensaoDao uniDao = getDAO(AtividadeExtensaoDao.class);
		Integer idEdital = 0;
		try {

			unidadesResponsaveis.clear();

			idEdital = new Integer(evt.getNewValue().toString());
			if (idEdital != null && idEdital != 0) {
				unidadesResponsaveis = uniDao
						.getUnidadesResponsaveisByEditalExtensao(idEdital);
			}

			// carga autom�tica de todas as unidades do edital selecionado em
			// outro combo como o bean � de request a lista deve ser enviada para sess�o
			getCurrentSession().setAttribute(
					"relatoriosAtividadesMBean.unidadesResponsaveis",
					unidadesResponsaveis);

		} catch (DAOException e) {
			notifyError(e);
		}
	}

	// Gets e Sets

	
    
    
    /**
     * M�todo usado para redirecionar para uma p�gina de listagem de atividades sem plano de trabalho.
     * <br/>
     * Chamado por:
     * <ul>
     * 		<li>/sigaa.war/extensao/menu.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String preLocalizarAcoesSemPlano() {
    	atividadesLocalizadas = new ArrayList<AtividadeExtensao>();	
    	
    	ano = CalendarUtils.getAnoAtual();
    	tituloAtividade = null;
    	
    	checkBuscaAno = false;
    	checkBuscaTituloAtividade = false;
    	
    	return forward(ConstantesNavegacao.LISTA_ATIVIDADES_SEM_PLANO);
    }
    
    /**
     * M�todo que controla a busca de atividades sem plano de trabalho.
     * <br/>
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * 		<li>/sigaa.war/extensao/Atividade/lista_atividades_sem_plano.jsp</li>
     * </ul>
     * 
     * @return
     */
    public String localizarAtividadesSemPlanoTrabalho() {
    	
		ListaMensagens listaErros = new ListaMensagens();    	
		if(!checkBuscaAno && !checkBuscaTituloAtividade) {
		    listaErros.addErro("Selecione ao menos uma op��o para que seja realizada a busca.");
		}
		if(checkBuscaAno) {
		    ValidatorUtil.validaInt(ano, "Ano da A��o", listaErros);    		
		}
		if (checkBuscaTituloAtividade) {    	    
		    ValidatorUtil.validateRequired(tituloAtividade, "T�tulo da A��o", listaErros);
		}
		else {
		    tituloAtividade = null;
		}
		if(!listaErros.isEmpty()) {
		    addMensagens(listaErros);
		    return null;
		}
		
		AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class);
		atividadesLocalizadas = dao.atividadesSemPlano(tituloAtividade, ano);
	
		if(atividadesLocalizadas == null || atividadesLocalizadas.isEmpty()) {
		    addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);    	
		}
	
		return null;
    }
    
    // Getters and Setters
    
	public Collection<AtividadeExtensao> getAtividadesLocalizadas() {
	    return atividadesLocalizadas;
	}

	public void setAtividadesLocalizadas(
			Collection<AtividadeExtensao> atividadesLocalizadas) {
		this.atividadesLocalizadas = atividadesLocalizadas;
	}

	public EditalExtensao getEdital() {
		return edital;
	}

	public void setEdital(EditalExtensao edital) {
		this.edital = edital;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getTituloAtividade() {
		return tituloAtividade;
	}

	public void setTituloAtividade(String tituloAtividade) {
		this.tituloAtividade = tituloAtividade;
	}

	public boolean isCheckBuscaEdital() {
		return checkBuscaEdital;
	}

	public void setCheckBuscaEdital(boolean checkBuscaEdital) {
		this.checkBuscaEdital = checkBuscaEdital;
	}

	public boolean isCheckBuscaTituloAtividade() {
		return checkBuscaTituloAtividade;
	}

	public void setCheckBuscaTituloAtividade(boolean checkBuscaTituloAtividade) {
		this.checkBuscaTituloAtividade = checkBuscaTituloAtividade;
	}

	public boolean isCheckBuscaAno() {
		return checkBuscaAno;
	}

	public void setCheckBuscaAno(boolean checkBuscaAno) {
		this.checkBuscaAno = checkBuscaAno;
	}

	public boolean isCheckBuscaPeriodo() {
		return checkBuscaPeriodo;
	}

	public void setCheckBuscaPeriodo(boolean checkBuscaPeriodo) {
		this.checkBuscaPeriodo = checkBuscaPeriodo;
	}

	public boolean isCheckGerarRelatorio() {
		return checkGerarRelatorio;
	}

	public void setCheckGerarRelatorio(boolean checkGerarRelatorio) {
		this.checkGerarRelatorio = checkGerarRelatorio;
	}

	public boolean isCheckBuscaSituacaoAtividade() {
		return checkBuscaSituacaoAtividade;
	}

	public void setCheckBuscaSituacaoAtividade(
			boolean checkBuscaSituacaoAtividade) {
		this.checkBuscaSituacaoAtividade = checkBuscaSituacaoAtividade;
	}

	public Collection<Object[]> getResultadoQuantitativo() {
		return resultadoQuantitativo;
	}

	public void setResultadoQuantitativo(
			Collection<Object[]> resultadoQuantitativo) {
		this.resultadoQuantitativo = resultadoQuantitativo;
	}

	public Integer getMesInicio() {
		return mesInicio;
	}

	public void setMesInicio(Integer mesInicio) {
		this.mesInicio = mesInicio;
	}

	public Integer getMesFim() {
		return mesFim;
	}

	public void setMesFim(Integer mesFim) {
		this.mesFim = mesFim;
	}

	public void setSituacaoAtividade(TipoSituacaoProjeto situacaoAtividade) {
		this.situacaoAtividade = situacaoAtividade;
	}

	public TipoSituacaoProjeto getSituacaoAtividade() {
		return situacaoAtividade;
	}

	public boolean isCheckBuscaTipoAtividade() {
		return checkBuscaTipoAtividade;
	}

	public void setCheckBuscaTipoAtividade(boolean checkBuscaTipoAtividade) {
		this.checkBuscaTipoAtividade = checkBuscaTipoAtividade;
	}

	public TipoAtividadeExtensao getTipoAtividadeExtensao() {
		return tipoAtividadeExtensao;
	}

	public void setTipoAtividadeExtensao(
			TipoAtividadeExtensao tipoAtividadeExtensao) {
		this.tipoAtividadeExtensao = tipoAtividadeExtensao;
	}

	public boolean isCheckBuscaTipoParticipante() {
		return checkBuscaTipoParticipante;
	}

	public void setCheckBuscaTipoParticipante(boolean checkBuscaTipoParticipante) {
		this.checkBuscaTipoParticipante = checkBuscaTipoParticipante;
	}

	public ParticipanteAcaoExtensao getParticipante() {
		return participante;
	}

	public void setParticipante(ParticipanteAcaoExtensao participante) {
		this.participante = participante;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public boolean isCheckBuscaUnidade() {
		return checkBuscaUnidade;
	}

	public void setCheckBuscaUnidade(boolean checkBuscaUnidade) {
		this.checkBuscaUnidade = checkBuscaUnidade;
	}

	public Unidade getUnidadeProponente() {
		return unidadeProponente;
	}

	public void setUnidadeProponente(Unidade unidadeProponente) {
		this.unidadeProponente = unidadeProponente;
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
	}

	public Collection<Unidade> getUnidadesResponsaveis() {
		return unidadesResponsaveis;
	}

	public void setUnidadesResponsaveis(Collection<Unidade> unidadesResponsaveis) {
		this.unidadesResponsaveis = unidadesResponsaveis;
	}

	public Collection<Unidade> getUnidadesProponentes() {
		return unidadesProponentes;
	}

	public void setUnidadesProponentes(Collection<Unidade> unidadesProponentes) {
		this.unidadesProponentes = unidadesProponentes;
	}

	public boolean isCheckBuscaAnoInicio() {
		return checkBuscaAnoInicio;
	}

	public void setCheckBuscaAnoInicio(boolean checkBuscaAnoInicio) {
		this.checkBuscaAnoInicio = checkBuscaAnoInicio;
	}

	public boolean isCheckBuscaMesConclusao() {
		return checkBuscaMesConclusao;
	}

	public void setCheckBuscaMesConclusao(boolean checkBuscaMesConclusao) {
		this.checkBuscaMesConclusao = checkBuscaMesConclusao;
	}

	public String getBuscaMesInicio() {
		return meses[mesInicio - 1];
	}

	public String getBuscaMesFim() {
		return meses[mesFim - 1];
	}

	public String getBuscaNomeSituacao() {
		return buscaNomeSituacao;
	}

	public void setBuscaNomeSituacao(String buscaNomeSituacao) {
		this.buscaNomeSituacao = buscaNomeSituacao;
	}

	public String getBuscaTipoAcao() {
		return buscaTipoAcao;
	}

	public void setBuscaTipoAcao(String buscaTipoAcao) {
		this.buscaTipoAcao = buscaTipoAcao;
	}

	public String getBuscaTipoParticipante() {
		return buscaTipoParticipante;
	}

	public void setBuscaTipoParticipante(String buscaTipoParticipante) {
		this.buscaTipoParticipante = buscaTipoParticipante;
	}

	public String getBuscaNomeUnidade() {
		return buscaNomeUnidade;
	}

	public void setBuscaNomeUnidade(String buscaNomeUnidade) {
		this.buscaNomeUnidade = buscaNomeUnidade;
	}


	public Collection<Map<String, Object>> getResultado() {
		return resultado;
	}


	public void setResultado(Collection<Map<String, Object>> resultado) {
		this.resultado = resultado;
	}


	public LocalRealizacao getLocalRealizacao() {
		return localRealizacao;
	}


	public void setLocalRealizacao(LocalRealizacao localRealizacao) {
		this.localRealizacao = localRealizacao;
	}


	public Boolean getBuscaAcoesAssociadas() {
	    return buscaAcoesAssociadas;
	}


	public void setBuscaAcoesAssociadas(Boolean buscaAcoesAssociadas) {
	    this.buscaAcoesAssociadas = buscaAcoesAssociadas;
	}


	public boolean isCheckBuscaAcaoAssociada() {
	    return checkBuscaAcaoAssociada;
	}


	public void setCheckBuscaAcaoAssociada(boolean checkBuscaAcaoAssociada) {
	    this.checkBuscaAcaoAssociada = checkBuscaAcaoAssociada;
	}


	public Integer getSelecionaRelatorio() {
		return selecionaRelatorio;
	}


	public void setSelecionaRelatorio(Integer selecionaRelatorio) {
		this.selecionaRelatorio = selecionaRelatorio;
	}


	public Date getDataInicioConclusao() {
		return dataInicioConclusao;
	}


	public void setDataInicioConclusao(Date dataInicioConclusao) {
		this.dataInicioConclusao = dataInicioConclusao;
	}


	public boolean isCheckBuscaPeriodoConclusao() {
		return checkBuscaPeriodoConclusao;
	}


	public void setCheckBuscaPeriodoConclusao(boolean checkBuscaPeriodoConclusao) {
		this.checkBuscaPeriodoConclusao = checkBuscaPeriodoConclusao;
	}


	public Date getDataFimConclusao() {
		return dataFimConclusao;
	}


	public void setDataFimConclusao(Date dataFimConclusao) {
		this.dataFimConclusao = dataFimConclusao;
	}
	
	
}
