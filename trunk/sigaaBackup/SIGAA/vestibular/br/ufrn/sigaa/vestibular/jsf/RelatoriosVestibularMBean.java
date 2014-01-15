/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/10/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.gru.dominio.GuiaRecolhimentoUniao;
import br.ufrn.comum.gru.negocio.GuiaRecolhimentoUniaoHelper;
import br.ufrn.sigaa.arq.dao.questionario.PerguntaQuestionarioDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.arq.dao.vestibular.FiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoFiscalDao;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.dao.vestibular.IsentoTaxaInscricaoDao;
import br.ufrn.sigaa.arq.dao.vestibular.LocalAplicacaoProvaDao;
import br.ufrn.sigaa.arq.dao.vestibular.PessoaVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademico;
import br.ufrn.sigaa.mensagens.MensagensGerais;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivo;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.EstatisticaInscritosPorDia;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.LinhaQuestionarioRespostas;
import br.ufrn.sigaa.vestibular.dominio.LocalAplicacaoProva;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResumoProcessamentoSelecao;

/**
 * Controller responsável por geração de relatórios relacionados ao Vestibular.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Component("relatoriosVestibular")
@Scope("session")
public class RelatoriosVestibularMBean extends SigaaAbstractController<ProcessoSeletivoVestibular> {

	/** ID do processo seletivo do relatório. */
	private int idProcessoSeletivo;
	
	/** ID do local de aplicação de prova do relatório. */
	private int idLocalAplicacaoProva;
	
	/** Formato do relatório. */
	private String formato;
	
	/** Título do relatório. */
	private String nomeRelatorio;
	
	/** Curso do relatório. */
	private int idCurso;
	
	/** ID da unidade do relatório. */
	private int idUnidade;
	
	/** Perfil de usuário do relatório. */
	private int perfil;
	
	/** Data de aplicação da prova. */
	private Date dataAplicacao;
	
	/** Data de vencimento da GRU . */
	private Date dataVencimentoGRU;
	
	/** Indica se capta a data de aplicação da prova para geração do relatório. */
	private boolean coletaDataAplicacao = false;
	
	/** Indica se deve abrir o relatório em uma nova janela no navegador. */
	private boolean novaJanela = false;
	
	/** Indica o tipo de demanda solicitada pelo usuário */
	private boolean coletaTipoDemanda = false;
	
	/** Indica de o relatório deve ser o relatório final da demanda ou o parcial */
	private boolean demandaFinal = false;

	/** Indica se o usuário pode ou não informar o formato do relatório. */
	private boolean selecionaFormato = false;
	
	/** Locais de aplicação de prova do relatório. */
	private List<LocalAplicacaoProva> locaisAplicacao;

	/** Candidato do Processo Seletivo */
	private PessoaVestibular pessoa;
	
	/** Constante que define o perfil Discente. */
	public static final int PERFIL_DISCENTE = 1;
	/** Constante que define o perfil Servidor. */
	public static final int PERFIL_SERVIDOR = 2;

	/** Constante que define o conjunto de fiscais reservas. */
	public static final int FISCAIS_RESERVAS = 1;
	/** Constante que define o conjunto de fiscais titulares. */
	public static final int FISCAIS_TITULARES = 2;

	/** Constante que indica a página para seleção do processo seletivo. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO = "/vestibular/relatorios/seleciona_ps.jsp";
	/** Constante que indica a página para seleção do processo seletivo e local de aplicação. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_LOCAL_APLICACAO = "/vestibular/relatorios/seleciona_ps_local.jsp";
	/** Constante que indica a página para seleção do processo seletivo e perfil. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_PERFIL = "/vestibular/relatorios/seleciona_ps_perfil.jsp";
	/** Constante que indica a página para seleção do processo seletivo. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_CONVOCACAO = "/vestibular/relatorios/seleciona_ps_convocacao.jsp";
	/** Constante que indica a página para a escolha dos parametros para a exibição da inscrição do candidato. */
	private final static String FORWARD_SELECIONA_INSCRICAO_CANDIDATO = "/vestibular/relatorios/seleciona_inscricao_candidato.jsp";
	/** Constante que indica a página para a escolha dos parametros para a exibição da inscrição do candidato. */
	private final static String FORWARD_DETALHE_INSCRICAO_CANDIDATO = "/vestibular/relatorios/detalhe_inscricao_candidato.jsp";
	/** Constante que indica a página para a escolha dos parametros para a exibição da inscrição do candidato, para imopressão */
	private final static String FORWARD_DETALHE_INSCRICAO_CANDIDATO_IMPRESSAO = "/vestibular/relatorios/detalhe_inscricao_candidato_print.jsp";
	
	// relatórios de fiscais
	/** Relatório Lista Frequência Durante Aplicação de Provas. */
	private final static String LISTA_FREQUENCIA_APLICACAO_FISCAIS = "Lista Frequência Durante Aplicação de Provas";
	/** Relatório Lista de Frequência da Reunião de Fiscais. */
	private final static String LISTA_FREQUENCIA_REUNIAO_FISCAIS = "Lista de Frequência da Reunião de Fiscais";
	/** Relatório Lista de Distribuição dos Fiscais no Prédio. */
	private final static String LISTA_DISTRIBUICAO_FISCAL = "Lista de Distribuição dos Fiscais no Prédio";
	/** Relatório Lista de Locais de Aplicação de Prova. */
	private final static String LISTA_LOCAIS_PROVA = "Lista de Locais de Aplicação de Prova";
	/** Relatório Lista de fiscais Selecionados. */
	private final static String LISTA_FISCAIS_SELECIONADOS = "Lista de Fiscais Selecionados";
	/** Relatório Lista de Contato de Fiscais Reservas. */
	private final static String LISTA_CONTATO_FISCAIS_RESERVAS = "Lista de Contato de Fiscais Reservas";
	/** Relatório Ficha de Avaliação Individual de Fiscal. */
	private final static String FICHA_AVALIACAO_FISCAL = "Ficha de Avaliação Individual de Fiscal";
	/** Relatório Lista de Fiscais não Alocados. */
	private final static String LISTA_FISCAIS_NAO_ALOCADOS = "Lista de Fiscais não Alocados";
	/** Relatório de IRA Máximos e Mínimos, por curso, dos Fiscais Selecionados. */
	private final static String RESUMO_SELECAO_FISCAIS = "Resumo do Processamento da Seleção de Fiscais";
	/** Relatório Frequencia de fiscais para conferência e solicitação de pagamento. */
	private final static String FREQUENCIA_FISCAIS = "Relatório de Frequência de Fiscais para Conferência";
	//relatórios de inscrição do vestibular
	/** Relatório Estatítico de Inscritos no Vestibular por dia de inscrição. */
	private final static String ESTATISTICA_DE_INSCRITOS_POR_DIA = "Estatísticas dos Candidatos Inscritos por Dia";
	/** Relatório Estatítico de Inscritos no Vestibular por dia de inscrição. */
	private final static String ESTATISTICA_DE_INSCRITOS_POR_SEMANA = "Estatística de Candidatos Inscritos por Semana";
	/** Relatório Estatístico dos Beneficiários com a isenção na inscrição do Vestibular */
	private final static String ESTATISTICA_DE_BENEFICIARIO_ISENCAO_INSCRICAO = "Estatísticas dos Beneficiados com a Isenção na Inscrição";
	/** Relatório da Demanda Parcial de Candidatos Inscritos por Curso */
	private final static String DEMANDA_PARCIAL_CANDIDATO_INSCRITO_POR_CURSO = "Demanda de Candidatos Inscritos por Curso";
	/** Relatório Estatístico dos pagantes x Isentos no vestibular */
	private final static String ESTATISTICAS_DOS_PAGANTES_X_ISENTOS_INSCRITOS_NO_VESTIBULAR = "Estatística dos Pagantes X Isentos Inscritos no Vestibular";
	/** Relatório quantitativo das situações das fotos */
	private final static String QUANTITATITVO_STATUS_FOTO = "Quantitativo de Situações de Fotos";
	/** Relatório do Questionário sócio Econômico */
	private final static String RELATORIO_QUESTIONARIO_SOCIO_ECONOMICO = "Relatório Estatístico das Respostas ao Questionário Socioeconômico";
	/** Visualização dos dados do candidato do vestibular */
	private final static String VISUALIZACAO_DADOS_CANDIDATO_VESTIBULAR = "Visualização da Ficha de Inscrição";
	/** Relatório do Questionário sócio Econômico */
	private final static String CONTATO_CANDIDATOS_CONVOCADOS = "Lista de Contato dos Candidatos Aprovados no Vestibular";
	/** Relatório de GRUs pagas. */
	private final static String LISTA_GRU_PAGAS = "Lista de GRUs pagas";
	
	
	/** Indica se o formulário deve exibir a opção de fiscais titulares / reservas. */
	private boolean requerTitularReserva;
	
	/** Indica se o relatório deve restringir por fiscais titulares / reservas. */
	private int titularReserva;

	/** Lista dos beneficiarios da isenção da taxa de inscrição do vestibular */
	private List<HashMap<String, Object>> demanda = new ArrayList<HashMap<String, Object>>();
	
	/** Coleção que armazena os status das fotos. */
	private Collection<PessoaVestibular> statusFoto = new ArrayList<PessoaVestibular>();
	
	/** Map que armazena os isentos do vestibular */
	private Map<String, Object> isencaoVestibular = new TreeMap<String, Object>();

	/** Coleção utilizada na geração do relatório estatístico do questionário sócio econômico. */
	private Collection<LinhaQuestionarioRespostas> linhaQuestionarioRespostas = new ArrayList<LinhaQuestionarioRespostas>();

	/** Coleção de convocações de um determinador processo seletivo. */
	private Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo;

	/** ID da convocação dos candidatos aprovados no Vestibular. */
	private int idConvocacaoProcessoSeletivo;
	
	/** Coleção de convocações de discentes de um determinador processo seletivo. */
	private Collection<ConvocacaoProcessoSeletivoDiscente> convocacoesDiscente;

	/** Coleção de convocações de discentes de um determinador processo seletivo. */
	private Collection<InscricaoVestibular> inscricoes;

	/** Coleção de convocações de discentes de um determinador processo seletivo. */
	private InscricaoVestibular inscricao;

	/** Define que o controler irá exportar os dados dos candidatos no formato PDF. */
	private final int ARQUIVO_PDF = 1;
	/** Define que o controler irá exportar os dados dos candidatos no formato CSV. */
	private final int ARQUIVO_CSV = 2;
	/** Formato do arquivo de dados dos candidatos a ser exportadas (SQL ou CSV). */
	private int formatoArquivo;

	
	/** Realiza uma busca de inscrições de fiscais no processo seletivo e seta como atributo "inscrições".
	 * @param idProcessoSeletivo
	 * @throws DAOException
	 */
	private void buscarInscricoesProcessoSeletivo(int idProcessoSeletivo)
			throws DAOException {
		ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();
		processoSeletivo.setId(idProcessoSeletivo);
		
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class);
		Collection<InscricaoFiscal> inscricoes = inscricaoFiscalDao
				.findByProcessoSeletivo(processoSeletivo.getId());
		getCurrentRequest().setAttribute("inscricoes", inscricoes);
	}

	
	/**
	 * Busca fiscais do processo seletivo e seta o resultado como atributo de
	 * sessão "fiscais".
	 * 
	 * @param idProcessoSeletivo
	 * @throws DAOException
	 */
	private void buscarFiscaisProcessoSeletivo(int idProcessoSeletivo)
			throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(idProcessoSeletivo,
				ProcessoSeletivoVestibular.class);
		FiscalDao fiscalDao = getDAO(FiscalDao.class);
		Collection<Fiscal> fiscais = fiscalDao
				.findByProcessoSeletivo(idProcessoSeletivo);
		getCurrentRequest().setAttribute("fiscais", fiscais);
		getCurrentRequest().setAttribute("processoSeletivo", obj);
	}
	
	/**
	 * Busca fiscais do processo seletivo em um determinado local de aplicação e
	 * seta o resultado como atributo de sessão "fiscais".
	 * 
	 * @param idProcessoSeletivo
	 * @throws DAOException
	 */
	private void buscarFiscaisProcessoSeletivoLocalAplicacao(int idProcessoSeletivo, int idLocalAplicacaoProva)
			throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(idProcessoSeletivo,
				ProcessoSeletivoVestibular.class);
		FiscalDao fiscalDao = getDAO(FiscalDao.class);
		Collection<Fiscal> fiscais = fiscalDao
				.findByProcessoSeletivoLocalAplicacao(idProcessoSeletivo, idLocalAplicacaoProva, false, true);
		getCurrentRequest().setAttribute("fiscais", fiscais);
		getCurrentRequest().setAttribute("processoSeletivo", obj);
	}

	/** Busca inscrições de fiscais de um curso e processo seletivo, e seta como atributo "fiscais".
	 * @param idProcessoSeletivo
	 * @param idCurso
	 * @throws DAOException
	 */
	private void buscarInscricoesProcessoSeletivoCurso(
			Integer idProcessoSeletivo, int idCurso) throws DAOException {
		ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();
		processoSeletivo.setId(idProcessoSeletivo);
		Curso curso = new Curso();
		curso.setId(idCurso);
		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class);
		Collection<InscricaoFiscal> inscricoes = inscricaoFiscalDao
				.findDiscenteByProcessoSeletivoCurso(processoSeletivo.getId(), curso.getId());
		getCurrentRequest().setAttribute("inscricoes", inscricoes);

	}

	/** Busca inscrições de fiscais por processo seletivo e unidade, setando como atributo "inscrições".
	 * @param idProcessoSeletivo
	 * @param idUnidade
	 * @throws DAOException
	 */
	private void buscarInscricoesProcessoSeletivoUnidade(
			Integer idProcessoSeletivo, int idUnidade) throws DAOException {
		ProcessoSeletivoVestibular processoSeletivo = new ProcessoSeletivoVestibular();
		processoSeletivo.setId(idProcessoSeletivo);
		Unidade unidade = new Unidade();
		unidade.setId(idUnidade);

		InscricaoFiscalDao inscricaoFiscalDao = getDAO(InscricaoFiscalDao.class);
		Collection<InscricaoFiscal> inscricoes = inscricaoFiscalDao
				.findServidorByProcessoSeletivoUnidade(processoSeletivo.getId(),
						unidade.getId());
		getCurrentRequest().setAttribute("inscricoes", inscricoes);
	}

	/** Gera o relatório correspondente do tipo desejado.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_local.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_perfil.jsp</li>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public String gerarRelatorio() throws DAOException, SegurancaException, HibernateException, SQLException {
		if (nomeRelatorio.equals(LISTA_FREQUENCIA_APLICACAO_FISCAIS)
				|| nomeRelatorio.equals(LISTA_DISTRIBUICAO_FISCAL)
				|| nomeRelatorio.equals(LISTA_FREQUENCIA_REUNIAO_FISCAIS)
				|| nomeRelatorio.equals(LISTA_FISCAIS_SELECIONADOS)
				|| nomeRelatorio.equals(FREQUENCIA_FISCAIS))
			return gerarRelatorioPDF();
		else if (nomeRelatorio.equals(LISTA_LOCAIS_PROVA))
			return gerarRelatorioLocaisProva();
		else if (nomeRelatorio.equals(FICHA_AVALIACAO_FISCAL))
			return gerarRelatorioFichaAvaliacao();
		else if (nomeRelatorio.equals(LISTA_FISCAIS_NAO_ALOCADOS))
			return gerarListaFiscaisNaoAlocados();
		else if (nomeRelatorio.equals(LISTA_CONTATO_FISCAIS_RESERVAS))
			return gerarListaContatoFiscaisReservas();
		else if (nomeRelatorio.equals(RESUMO_SELECAO_FISCAIS))
			return gerarResumoSelecaoFiscais();
		else if (nomeRelatorio.equals(ESTATISTICA_DE_INSCRITOS_POR_DIA))
			return gerarEstatisticaInscritosPorDia();
		else if (nomeRelatorio.equals(ESTATISTICA_DE_INSCRITOS_POR_SEMANA)) 			
			return gerarEstatisticaInscricoesSemanal();
		else if (nomeRelatorio.equals(ESTATISTICA_DE_BENEFICIARIO_ISENCAO_INSCRICAO)) 
			return gerarEstatisticaIsercaoInscricaoVestibular();
		else if (nomeRelatorio.equals(DEMANDA_PARCIAL_CANDIDATO_INSCRITO_POR_CURSO)) 
			return gerarDemandaCandidatosInscritoCurso();
		else if (nomeRelatorio.equals(ESTATISTICAS_DOS_PAGANTES_X_ISENTOS_INSCRITOS_NO_VESTIBULAR))
			return gerarEstatisticaPagantesIsentosInscritos();
		else if (nomeRelatorio.equals(QUANTITATITVO_STATUS_FOTO))
			return gerarQuantitativoSituacaoFoto();
		else if (nomeRelatorio.equals(RELATORIO_QUESTIONARIO_SOCIO_ECONOMICO))
			return gerarRelatorioSocioEconomico();
		else if (nomeRelatorio.equals(CONTATO_CANDIDATOS_CONVOCADOS))
			return gerarRelatorioContatoCandidatosConvocados();
		else if (nomeRelatorio.equals(LISTA_GRU_PAGAS))
			return gerarRelatorioGRUPagas();
		else return null;
	}

	/** Gera os relatórios em formado PDF
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioPDF() throws DAOException {

		validaDados();
		if (hasErrors()) 
			return null;

		// Gerar relatório
		Connection con = null;
		try {
			formato = "pdf";
			// Popular parâmetros do relatório
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA);
			parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
			parametros.put("concurso", idProcessoSeletivo);
			parametros.put("indiceSelecao", ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO));
			InputStream relatorio = null;
			if (nomeRelatorio.equals(LISTA_FREQUENCIA_APLICACAO_FISCAIS)) {
				if (dataAplicacao == null) {
					addMensagemErro("Insira uma data válida");
					return null;
				}
				relatorio = JasperReportsUtil
						.getReportSIGAA("ListaFrequenciaAplicacaoFiscais.jasper");
				parametros.put("DATA_APLICACAO", dataAplicacao);
			} else if (nomeRelatorio.equals(LISTA_DISTRIBUICAO_FISCAL)) {
				relatorio = JasperReportsUtil
						.getReportSIGAA("ListaDistribuicaoFiscais.jasper");
			} else if (nomeRelatorio
					.equals(LISTA_FREQUENCIA_REUNIAO_FISCAIS)) {
				relatorio = JasperReportsUtil
						.getReportSIGAA("ListaAssinaturasFiscais.jasper");
			} else if (nomeRelatorio
					.equals(LISTA_FREQUENCIA_REUNIAO_FISCAIS)) {
				relatorio = JasperReportsUtil
						.getReportSIGAA("ListaContatoFiscaisReservas.jasper");
			} else if (nomeRelatorio
					.equals(LISTA_FISCAIS_SELECIONADOS)) {
				relatorio = JasperReportsUtil
						.getReportSIGAA("ListaFiscaisSelecionados.jasper");
			} else if (nomeRelatorio
					.equals(FREQUENCIA_FISCAIS) && formatoArquivo == ARQUIVO_PDF) {
				relatorio = JasperReportsUtil
						.getReportSIGAA("FrequenciaFiscaisConferencia.jasper");
			} else if (nomeRelatorio
					.equals(FREQUENCIA_FISCAIS) && formatoArquivo == ARQUIVO_CSV) {
				return gerarRelatorioFrequenciaFiscais();
			}

			// Preencher relatório
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(relatorio,
					parametros, con);
			if (prt.getPages().size() == 0) {
				addMensagemWarning("Não foi possível gerar o relatório, pois não foram encontrados fiscais associados ao processo seletivo escolhido.");
				return null;
			}
			// Exportar relatório de acordo com o formato escolhido
			String nomeArquivo = nomeRelatorio + "." + formato;
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(),
					getCurrentResponse(), "pdf");
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			e.printStackTrace();
			notifyError(e);
			addMensagem(MensagensGerais.ERRO_GERACAO_RELATORIO);
			return null;
		} finally {
			Database.getInstance().close(con);
		}

		return null;
	}

	/**  Validar campos do formulário */
	private void validaDados() {
		ValidatorUtil.validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		if (selecionaFormato && formatoArquivo == 0)
			erros.addErro("Formato do Relatório: Selecione um Formato para a geração do relatório.");
	}
	
	/**
	 * Geração do relatório de Frequência dos fiscais do vestibular, 
	 * ainda realiza a sua exportação para o formato cvs. 
	 * @return
	 * @throws ArqException
	 * @throws IOException
	 */
	private String gerarRelatorioFrequenciaFiscais() throws ArqException, IOException {
		FiscalDao dao = getDAO(FiscalDao.class);
		String dados = null;
		try {
			obj = new ProcessoSeletivoVestibular();
			setObj(dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class));
			dados = dao.exportaRelatorioFrequenciaFiscais(obj.getId());
			if ( isEmpty(dados) ) {
				addMensagemWarning("Não foi possível gerar o relatório, pois não foram encontrados fiscais associados ao processo seletivo escolhido.");
				return null;
			}
			getCurrentResponse().setContentType("text/csv");
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=Relatório.csv");
			PrintWriter out = getCurrentResponse().getWriter();
			out.println(dados);
			FacesContext.getCurrentInstance().responseComplete();
			return null;	
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Gerar o relatório com as Inscrições Agrupadas semanalmente.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String gerarEstatisticaInscricoesSemanal() throws DAOException, SegurancaException{
		checkRole(SigaaPapeis.VESTIBULAR);
		validaDados();
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		Collection<EstatisticaInscritosPorDia> estat = dao.estatiscaInscritosPorDia(idProcessoSeletivo);
		
		Collection<EstatisticaInscritosPorDia> estatisticas = new ArrayList<EstatisticaInscritosPorDia>();
		int semanaAtual = 0;
		int mesAtual = 0;
		Calendar c = Calendar.getInstance();
		for (EstatisticaInscritosPorDia est: estat) {
			c.setTime(est.getData());
			if ( (semanaAtual == 0 && mesAtual == 0) || 
					mesAtual == c.get(Calendar.MONTH) && semanaAtual == c.get(Calendar.DAY_OF_WEEK_IN_MONTH)) {
				semanaAtual = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
				mesAtual = c.get(Calendar.MONTH);
				estatisticas.add(inserirEstatistica(est, semanaAtual, mesAtual));
			}else{
				semanaAtual = c.get(Calendar.DAY_OF_WEEK_IN_MONTH);
				mesAtual = c.get(Calendar.MONTH);
				estatisticas.add(inserirEstatistica(est, semanaAtual, mesAtual));
			}
		}
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		getCurrentRequest().setAttribute("estatisticas", estatisticas);
		return forward("/vestibular/relatorios/acompanhamento_inscricao.jsp");
	}

	/**
	 * Cria uma instância de uma Estatística dos Inscritos por dia.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @param est
	 * @param semana
	 * @return
	 */
	private EstatisticaInscritosPorDia inserirEstatistica(EstatisticaInscritosPorDia est, int semana, int mes) {
		EstatisticaInscritosPorDia estatistica = new EstatisticaInscritosPorDia();
		estatistica.setNumeroSemana(semana + "º Semana de " + CalendarUtils.getNomeMes(mes + 1));
		estatistica.setPrimeiroQuartoDia(est.getPrimeiroQuartoDia());
		estatistica.setQuartoQuartoDia(est.getQuartoQuartoDia());
		estatistica.setSegundoQuartoDia(est.getSegundoQuartoDia());
		estatistica.setTerceiroQuartoDia(est.getTerceiroQuartoDia());
		estatistica.setTotalAte18Horas(est.getTotalAte18Horas());
		estatistica.setTotalCandidatos(est.getTotalCandidatos());
		estatistica.setTotalCandidatosIsentos(est.getTotalCandidatosIsentos());
		estatistica.setTotalInscricoes(est.getTotalInscricoes());
		estatistica.setData(est.getData());
		return estatistica;
	}
	
	/**
	 * Gerar o relatório com as Inscrições Diárias. 
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return /vestibular/relatorios/lista_fiscais_foto.jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarEstatisticaInscritosPorDia() throws DAOException, SegurancaException {
		InscricaoVestibularDao daoIns = getDAO(InscricaoVestibularDao.class);
		try {
			checkRole(SigaaPapeis.VESTIBULAR);
			validaDados();
			isencaoVestibular.clear();
			isencaoVestibular.put("Total de candidatos inscritos:", daoIns.quantTotalInscricoes(idProcessoSeletivo, demandaFinal));
			isencaoVestibular.put("Total de CPF's distintos:", daoIns.quantTotalInscricoesDistintas(idProcessoSeletivo, demandaFinal));
			Collection<EstatisticaInscritosPorDia> estatisticas = daoIns.estatiscaInscritosPorDia(idProcessoSeletivo);
			obj = daoIns.findByPrimaryKey(idProcessoSeletivo,ProcessoSeletivoVestibular.class);
			getCurrentRequest().setAttribute("estatisticas", estatisticas);
			return forward("/vestibular/relatorios/inscritos_por_dia.jsp");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} finally {
			daoIns.close();
		}
		return null;
	}
	
	/** Gera o relatório de inscritos por perfil.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_perfil.jsp</li>
	 * </ul>
	 * 
	 * @return /vestibular/relatorios/lista_inscritos_por_tipo.jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioListaInscritos() throws DAOException,
			SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		validaDados();
		
		if (perfil == PERFIL_DISCENTE) {
			buscarInscricoesProcessoSeletivoCurso(idProcessoSeletivo, idCurso);
		} else if (perfil == PERFIL_SERVIDOR) {
			buscarInscricoesProcessoSeletivoUnidade(idProcessoSeletivo,
					idUnidade);
		} else {
			buscarInscricoesProcessoSeletivo(idProcessoSeletivo);
		}
		obj = getGenericDAO().findByPrimaryKey(idProcessoSeletivo,
				ProcessoSeletivoVestibular.class);
		getCurrentRequest().setAttribute("perfil", perfil);
		return forward("/vestibular/relatorios/lista_inscritos_por_tipo.jsp");
	}

	/** Gera o relatório de Ficha de Avaliação do Fiscal.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return /vestibular/relatorios/lista_fiscais_foto.jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioFichaAvaliacao() throws DAOException,
			SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		validaDados();
		
		buscarFiscaisProcessoSeletivoLocalAplicacao(idProcessoSeletivo, idLocalAplicacaoProva);
		LocalAplicacaoProva local = getGenericDAO().findByPrimaryKey(
				idLocalAplicacaoProva, LocalAplicacaoProva.class);
		getCurrentRequest().setAttribute("nomeLocalAplicacao", local.getNome());
		return forward("/vestibular/relatorios/lista_fiscais_foto.jsp");
	}

	/** Gera o relatório do resultado do processamento da seleção de fiscais.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return /vestibular/relatorios/lista_fiscais_por_tipo.jsp
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioResultadoSelecaoFiscal() throws DAOException,
			SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		validaDados();
		buscarFiscaisProcessoSeletivo(idProcessoSeletivo);
		return forward("/vestibular/relatorios/lista_fiscais_por_tipo.jsp");
	}

	/** Gera o relatório de locais de prova.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioLocaisProva() throws DAOException {
		validaDados();
		LocalAplicacaoProvaDao localDao = getDAO(LocalAplicacaoProvaDao.class);
		Collection<LocalAplicacaoProva> listaLocaisAplicacaoProva = localDao
				.findByProcessoSeletivo(idProcessoSeletivo);
		if (isEmpty(listaLocaisAplicacaoProva)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		getCurrentRequest().setAttribute("lista", listaLocaisAplicacaoProva);
		return forward("/vestibular/relatorios/lista_locais_aplicacao.jsp");
	}
	
	/** Gera o relatório de fiscais não alocados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarListaFiscaisNaoAlocados() throws DAOException {
		validaDados();
		FiscalDao dao = getDAO(FiscalDao.class);
		Collection<Fiscal> lista = dao.findNaoAlocadosByProcessoSeletivo(idProcessoSeletivo);
		getCurrentRequest().setAttribute("lista", lista);
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		return forward("/vestibular/relatorios/lista_fiscais_nao_alocados.jsp");
	}
	
	/**
	 * Gera o relatório de contato de fiscais reservas (nome, bairro, telefone,
	 * celular), utilizado para convocação de fiscais que preencherão a lacuna
	 * deixada por fiscais ausentes no dia da prova. 
	 *  
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarListaContatoFiscaisReservas() throws DAOException {
		Boolean reserva;
		// Validar campos do formulário
		ValidatorUtil.validateRequiredId(idLocalAplicacaoProva, "Local de Aplicação", erros);
		validaDados();
		if (!erros.isEmpty()) 
			return null;
		switch (titularReserva) {
			case FISCAIS_RESERVAS: reserva = true; break;
			case FISCAIS_TITULARES: reserva = false; break;
			default: reserva = null; break;
		}
		FiscalDao dao = getDAO(FiscalDao.class);
		List<Map<String,Object>> lista = dao.findContatosFiscais(idProcessoSeletivo, idLocalAplicacaoProva, reserva);
		// Ordena a lista por bairro, nome
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		LocalAplicacaoProva localAplicacao = dao.findByPrimaryKey(idLocalAplicacaoProva, LocalAplicacaoProva.class);
		getCurrentRequest().setAttribute("localAplicacao", localAplicacao);
		getCurrentRequest().setAttribute("lista", lista);
		return forward("/vestibular/relatorios/lista_contato_fiscais.jsp");
	}
	
	/**
	 * Gera o relatório de IRA mínimo e máximo, por curso, dos fiscais selecionados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarResumoSelecaoFiscais() throws DAOException {
		validaDados();
		FiscalDao dao = getDAO(FiscalDao.class);
		List<ResumoProcessamentoSelecao> lista = dao.findIraMinimoMaximoFiscaisSelecionados(idProcessoSeletivo);
		if (lista == null || lista.isEmpty()) {
			addMensagemWarning("Não há dados para exibir no relatório.");
			return null;
		}
		int indiceSelecao = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO);
		IndiceAcademico indiceAcademico = dao.findByPrimaryKey(indiceSelecao, IndiceAcademico.class);
		getCurrentRequest().setAttribute("lista", lista);
		getCurrentRequest().setAttribute("indiceAcademico", indiceAcademico);
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		return forward("/vestibular/relatorios/resumo_selecao_fiscal.jsp");
	}

	/**
	 * Gera o relatório Estatístico dos Beneficiários com a Isenção na Inscrição do Vestibular
	 * 
 	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>Não é invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws SQLException 
	 * @throws HibernateException 
	 */
	public String gerarEstatisticaIsercaoInscricaoVestibular() throws DAOException, SegurancaException, HibernateException, SQLException {
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			checkRole(SigaaPapeis.VESTIBULAR);
			validaDados();
			isencaoVestibular = dao.findTotalInscritos(idProcessoSeletivo);
			obj = dao.findByPrimaryKey(idProcessoSeletivo,ProcessoSeletivoVestibular.class);
			return forward("/vestibular/relatorios/beneficiarios_isencao_inscricao.jsp");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		return null;
	}

	/**
	 * Gerar um relatório atual com a demanda parcial de candidatos inscritos por curso.
	 * 
 	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws HibernateException
	 * @throws SQLException
	 */
	public String gerarDemandaCandidatosInscritoCurso() throws DAOException, SegurancaException, HibernateException, SQLException {
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			checkRole(SigaaPapeis.VESTIBULAR);
			validaDados();
			
			isencaoVestibular.clear();
			isencaoVestibular.put("Total de candidatos inscritos:", dao.quantTotalInscricoes(idProcessoSeletivo, demandaFinal));
			isencaoVestibular.put("Total de CPF's distintos:", dao.quantTotalInscricoesDistintas(idProcessoSeletivo, demandaFinal));
			demanda = dao.demandaParcialCandidatoCurso(idProcessoSeletivo, demandaFinal);
			obj = dao.findByPrimaryKey(idProcessoSeletivo,ProcessoSeletivoVestibular.class);
			if (demanda.size() == 0) {
				addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
				return null;
			}
			return forward("/vestibular/relatorios/demanda_curso.jsp");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		return null;
	}
	
	/**
	 * Gera um relatórios com os candidatos beneficiados com a isenção, candidatos pagantes, dos candidatos pagantes com CPF's distintos
	 * o número de candidatos isentos inscritos como pagante, total de inscrições e o total de inscrições com cpf distinto.
	 * 
 	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps.jsp</li>
	 * </ul>
	 * 
	 */
	public String gerarEstatisticaPagantesIsentosInscritos() throws SegurancaException, DAOException, HibernateException, SQLException{
		IsentoTaxaInscricaoDao daoIsento = getDAO(IsentoTaxaInscricaoDao.class);
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			checkRole(SigaaPapeis.VESTIBULAR);
			validaDados();
			isencaoVestibular = new TreeMap<String, Object>();
			isencaoVestibular.put("Candidatos beneficiados com a isenção", daoIsento.quantCandidatosBeneficiadoIsencao(idProcessoSeletivo));
			isencaoVestibular.put("Candidatos pagantes", dao.quantCandidatosPagantes(idProcessoSeletivo));
			isencaoVestibular.put("Candidatos pagantes (CPF's distintos)", dao.quantCandidatosPagantesDistintos(idProcessoSeletivo));
			isencaoVestibular.put("Inscrições com CPF's repetidos (candidatos pagantes)", 
					((Integer) isencaoVestibular.get("Candidatos pagantes")) - (Integer)isencaoVestibular.get("Candidatos pagantes (CPF's distintos)"));
			isencaoVestibular.put("Número de candidatos isentos inscritos como pagante", daoIsento.quatIsentoCadPagante(idProcessoSeletivo));
			isencaoVestibular.put("Total de inscrições no banco", dao.quantTotalInscricoes(idProcessoSeletivo, demandaFinal));
			isencaoVestibular.put("Total de inscrições no banco (CPF's distintos)", dao.quantTotalInscricoesDistintas(idProcessoSeletivo, demandaFinal));
			obj = dao.findByPrimaryKey(idProcessoSeletivo,ProcessoSeletivoVestibular.class);
			return forward("/vestibular/relatorios/pagantes_isentos.jsp");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
			daoIsento.close();
		}
		return null;
	}
	
	/**
	 * Gerar um relatório quantitativo dos status das fotos.
	 * 
 	 * <br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String gerarQuantitativoSituacaoFoto() throws SegurancaException, DAOException{
		PessoaVestibularDao dao = getDAO(PessoaVestibularDao.class);
		try {
			checkRole(SigaaPapeis.VESTIBULAR);
			validaDados();
			statusFoto = dao.findByStatusFotoProcessoSeletivo(idProcessoSeletivo);
			obj = dao.findByPrimaryKey(idProcessoSeletivo,ProcessoSeletivoVestibular.class);
			return forward("/vestibular/relatorios/quantitativo_situacao_foto.jsp");
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		} finally {
			dao.close();
		}
		return null;
	}

	/**
	 * Gerar o relatório sócio econômico.
	 * 
 	 * <br/>Método não invocado por JSP´s.
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String gerarRelatorioSocioEconomico() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.VESTIBULAR);
		validaDados();
		QuestionarioRespostasDao dao = getDAO(QuestionarioRespostasDao.class);
		PerguntaQuestionarioDao perguntaDao = getDAO(PerguntaQuestionarioDao.class);
		try {
			obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
			if (obj.getQuestionario() == null) {
				addMensagemWarning("O processo seletivo não possui questionário Socioeconômico.");
				return null;
			}
	        
			Collection<Integer> tipoQuestao = new ArrayList<Integer>();  
			Collection<PerguntaQuestionario> perguntaQuestionario = perguntaDao.findAllPerguntasQuestionario(obj.getQuestionario()); 
			linhaQuestionarioRespostas.clear();
			for (PerguntaQuestionario pq : perguntaQuestionario) {
				if (!tipoQuestao.contains(pq.getTipo())) {
					linhaQuestionarioRespostas.addAll(dao.findByEstatisticaQuestionarioSocioEconomico(obj.getQuestionario(), pq.getTipo(), idProcessoSeletivo));
					if (pq.getTipo() == PerguntaQuestionario.UNICA_ESCOLHA 
							||pq.getTipo() == PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO) {
						
						tipoQuestao.add(PerguntaQuestionario.UNICA_ESCOLHA);
						tipoQuestao.add(PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO);
					}else{
						tipoQuestao.add(pq.getTipo());
					}
				}
			}
			if (linhaQuestionarioRespostas.size() == 0) {
				addMensagemErro("O Questionário Socioeconômico ainda não foi respondido.");
				return null;
			}else{
				Collections.sort( (List<LinhaQuestionarioRespostas>) linhaQuestionarioRespostas, new Comparator<LinhaQuestionarioRespostas>(){
					public int compare(LinhaQuestionarioRespostas lQR1,	LinhaQuestionarioRespostas lQR2) {						
						return new Integer(lQR1.getOrdem()).compareTo(new Integer(lQR2.getOrdem())); 					
					}
				});		
			}
		} catch (Exception e) {
			tratamentoErroPadrao(e);
		}finally{
			dao.close();
			perguntaDao.close();
		}
		return forward("/vestibular/relatorios/rel_estat_quest_socio_economico.jsp");		
	}
	
	/**
	 * Gera o relatório com dados de contato dos candidatos convocados.
	 * 
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioGRUPagas() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		// Validar campos do formulário
		ValidatorUtil.validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		if (hasErrors()) return null;
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		inscricoes = dao.findAllInscricaoGRUPaga(idProcessoSeletivo);
		if (isEmpty(inscricoes)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		return forward("/vestibular/relatorios/grus_pagas.jsp");
	}
	
	/**
	 * Gera o relatório com dados de contato dos candidatos convocados.
	 * 
	 * <br/>Método não invocado por JSP´s.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioContatoCandidatosConvocados() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		// Validar campos do formulário
		ValidatorUtil.validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		ValidatorUtil.validateRequiredId(idConvocacaoProcessoSeletivo, "Convocação", erros);
		if (hasErrors()) return null;
		ConvocacaoProcessoSeletivoDiscenteDao dao = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class);
		convocacoesDiscente = dao.dadosContatoConvocados(idProcessoSeletivo, idConvocacaoProcessoSeletivo);
		if (isEmpty(convocacoesDiscente)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		obj = dao.findByPrimaryKey(idProcessoSeletivo, ProcessoSeletivoVestibular.class);
		return forward("/vestibular/relatorios/rel_contato_aprovados_vestibular.jsp");
	}

	/**
	 * Responsável pela realização de uma busca, para retornar todos os inscritos. Baseando-se nos filtros.
	 * 
 	 * <br /> Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public String verInscricao() throws SegurancaException, DAOException, LimiteResultadosException{
		
		if ( pessoa.getCpf_cnpj() != null && pessoa.getCpf_cnpj() != 0 )
			ValidatorUtil.validateCPF_CNPJ(pessoa.getCpf_cnpj(), "CPF", erros);
		
		if (hasErrors()){
			pessoa.setCpf_cnpj((long) 0);
			return null;
		}
		
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		try {
			inscricoes = dao.findByNomeCpfInscricao(idProcessoSeletivo, pessoa.getNome(), pessoa.getCpf_cnpj(), 0, true);
		} catch (LimiteResultadosException e) {
			erros.addErro(e.getMessage());
		} finally {
			dao.close();
		}
		
		if (inscricoes.isEmpty() && !hasErrors()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward(FORWARD_SELECIONA_INSCRICAO_CANDIDATO);
	}
	
	/** Selecionar uma Inscrição do Vestibular para reimpressão da GRU
	 * <br /> Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String selecionarGRU() throws DAOException{
		int idInscricao = getParameterInt("id", 0);
		inscricao = getGenericDAO().findByPrimaryKey(idInscricao, InscricaoVestibular.class);
		if (idInscricao == 0 ||inscricao == null ) {
			addMensagemErro("Erro na identificação da inscrição.");
			return null;
		}
		dataVencimentoGRU = inscricao.getProcessoSeletivo().getDataVencimentoBoleto();
		return forward("/vestibular/relatorios/seleciona_vencimento_gru.jsp");
	}
	
	/** Imprime a GRU para pagamento da taxa de inscrição do vestibular.
	 * <br /> Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_vencimento_gru.jsp</li>
	 * </ul>
	 * @param idInscricao
	 * @param numeroInscricao
	 * @return
	 * @throws ArqException
	 * @throws IOException 
	 * @throws NegocioException 
	 */
	public  String imprimirGRU() throws ArqException, IOException, NegocioException {
		validateRequired(inscricao, "Erro na identificação da inscrição.", erros);
		validateRequired(dataVencimentoGRU, "Data de Vencimento da GRU", erros);
		if (hasErrors())
			return null;
		inscricao = getGenericDAO().refresh(inscricao);
		try {
			if (inscricao.getIdGRU() != null) {
	 			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(inscricao.getIdGRU());
	 			if (gru != null) {
	 				// altera a data de vencimento, caso o usuário informe outra
	 				gru.setVencimento(dataVencimentoGRU);
	 				GuiaRecolhimentoUniaoHelper.geraCodigoBarras(gru);
		 			// parâmetros do relatório
		 			Map<String, Object> map = new HashMap<String, Object>();
		 			
		 			ArrayList<GuiaRecolhimentoUniao> lista =  new ArrayList<GuiaRecolhimentoUniao>();
		 			lista.add(gru);
		 			JRBeanCollectionDataSource jrds = new JRBeanCollectionDataSource(lista);
		 			InputStream report = null;
		 			report = JasperReportsUtil.getReport("/br/ufrn/sigaa/relatorios/fontes/", gru.getTipo().getNomeArquivoJasper());
		 			JasperPrint prt = JasperFillManager.fillReport(report, map, jrds);
		 			JasperReportsUtil.exportar(prt, "GRU", getCurrentRequest(),
							getCurrentResponse(), "pdf");
					FacesContext.getCurrentInstance().responseComplete();
	 			}
 			} else {
 				throw new NegocioException("Não foi possível encontrar os dados para gerar a GRU.");
 			}
 		} catch (JRException e) {
 			throw new ArqException(e);
 		}
		return null;
	}
	
	/**
	 * Responsável pelo detalhamento das informações da inscrição do candidato.
	 * 
 	 * <br /> Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public String detalhar() throws SegurancaException, DAOException{
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		inscricao = new InscricaoVestibular();
		inscricao.setId(getParameterInt("id", 0));
		setInscricao(getGenericDAO().refresh(inscricao));
		if (!inscricao.getProcessoSeletivo().isProcessoExterno())
			inscricao.getProcessoSeletivo().setFimInscricaoCandidato(
				CalendarUtils.adicionaUmDia(inscricao.getProcessoSeletivo().getFimInscricaoCandidato()));
		return forward(FORWARD_DETALHE_INSCRICAO_CANDIDATO);
	}

	/**
	 * Responsável pelo detalhamento das informações da inscrição do candidato no formato para impressão.
	 * 
 	 * <br /> Chamado por 
	 * <ul>
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_inscricao_candidato.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public String detalharImpressao() throws SegurancaException, DAOException{
		detalhar();
		return forward(FORWARD_DETALHE_INSCRICAO_CANDIDATO_IMPRESSAO);
	}
	
	/** Retorna o formato do relatório. */
	public String getFormato() {
		return formato;
	}

	/** Retorna o ID do curso do relatório. */
	public int getIdCurso() {
		return idCurso;
	}

	/** Seta o ID do processo seletivo do relatório. */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Retorna a ID da unidade do relatório. */
	public int getIdUnidade() {
		return idUnidade;
	}

	/** Retorna o título do relatório. */
	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public int getPerfil() {
		return perfil;
	}
	
	/** Inicia a geração do Relatório Lista Frequência Durante Aplicação de Provas. 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarRelatorioGRUPagas() {
		resetaPropriedades();
		nomeRelatorio = LISTA_GRU_PAGAS;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/** Inicia a geração do Relatório Lista Frequência Durante Aplicação de Provas. 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarEstatisticaInscritosDiario() {
		resetaPropriedades();
		nomeRelatorio = ESTATISTICA_DE_INSCRITOS_POR_DIA;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** Inicia a geração do Relatório Lista Frequência Durante Aplicação de Provas. 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarEstatisticaInscritosPorSemana() {
		resetaPropriedades();
		nomeRelatorio = ESTATISTICA_DE_INSCRITOS_POR_SEMANA;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/**
	 * Inicia a geração do Relatório dos Beneficiários com a iserção na Inscrição
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarEstatisticaBeneficiariosInsencaoInscricao(){
		resetaPropriedades();
		nomeRelatorio = ESTATISTICA_DE_BENEFICIARIO_ISENCAO_INSCRICAO; 
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/**
	 * Inicia a demanda de cadidato por curso.
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarDemandaPorCurso(){
		resetaPropriedades();
		coletaTipoDemanda = true;
		nomeRelatorio = DEMANDA_PARCIAL_CANDIDATO_INSCRITO_POR_CURSO; 
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/**
	 * Inicia o Relatório dos Pagantes X Isentos Inscritos no Vestibular.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/candidatos.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarEstatisticaPaganteIsentoInscricao(){
		resetaPropriedades();
		nomeRelatorio = ESTATISTICAS_DOS_PAGANTES_X_ISENTOS_INSCRITOS_NO_VESTIBULAR;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/** Inicia a geração do Relatório Lista Frequência Durante Aplicação de Provas. 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaFrequenciaAplicacao() {
		resetaPropriedades();
		nomeRelatorio = LISTA_FREQUENCIA_APLICACAO_FISCAIS;
		coletaDataAplicacao = true;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** Inicia a geração do Relatório Ficha de Avaliação Individual de Fiscal.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarFichaAvaliacaoFiscal() {
		resetaPropriedades();
		nomeRelatorio = FICHA_AVALIACAO_FISCAL;
		coletaDataAplicacao = false;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO_LOCAL_APLICACAO);
	}

	/** Inicia a geração do Relatório Lista de Frequência da Reunião de Fiscais.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaFrequenciaReuniao() {
		resetaPropriedades();
		nomeRelatorio = LISTA_FREQUENCIA_REUNIAO_FISCAIS;
		coletaDataAplicacao = false;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** Inicia a geração do Relatório Lista de fiscais Selecionados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaFiscaisSelecionados() {
		resetaPropriedades();
		nomeRelatorio = LISTA_FISCAIS_SELECIONADOS;
		coletaDataAplicacao = false;
		novaJanela = true;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** Inicia a geração do relatório de inscritos.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaInscritos() {
		resetaPropriedades();
		coletaDataAplicacao = false;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO_PERFIL);
	}

	/** Inicia a geração do Relatório Lista de Locais de Aplicação de Prova.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menu_servidor.jsp</li>
	 *  <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaLocaisProva() {
		resetaPropriedades();
		nomeRelatorio = LISTA_LOCAIS_PROVA;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** Inicia a geração do Relatório Lista de Distribuição dos Fiscais no Prédio.
	 * 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaDistribuicaoFiscais() {
		resetaPropriedades();
		nomeRelatorio = LISTA_DISTRIBUICAO_FISCAL;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/** Inicia a geração do Relatório Lista de Fiscais não alocados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaFiscaisNaoAlocados() {
		resetaPropriedades();
		nomeRelatorio = LISTA_FISCAIS_NAO_ALOCADOS;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/** Inicia a geração do Relatório Lista de Contatos de Fiscais Reservas.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarListaContatoFiscaisReservas() {
		resetaPropriedades();
		this.requerTitularReserva = true;
		nomeRelatorio = LISTA_CONTATO_FISCAIS_RESERVAS;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO_LOCAL_APLICACAO);
	}
	
	/** Inicia a geração do Relatório IRA Máximos e Mínimos dos Fiscais Selecionados.
	 * 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarFrequenciaFiscais() {
		resetaPropriedades();
		nomeRelatorio = FREQUENCIA_FISCAIS;
		selecionaFormato = true;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}
	
	/** Inicia a geração do Relatório IRA Máximos e Mínimos dos Fiscais Selecionados.
	 * 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/fiscal.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarResumoSelecaoFiscais() {
		resetaPropriedades();
		nomeRelatorio = RESUMO_SELECAO_FISCAIS;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** 
	 * Quantitativo das descrições e dos quantitativos dos status das fotos.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/canidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarQuantitativoStatusFotos() {
		resetaPropriedades();
		nomeRelatorio = QUANTITATITVO_STATUS_FOTO;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** 
	 * Quantitativo das descrições e dos quantitativos dos status das fotos.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/canidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarRelatorioSocioEconomico() {
		resetaPropriedades();
		nomeRelatorio = RELATORIO_QUESTIONARIO_SOCIO_ECONOMICO;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO);
	}

	/** 
	 * Serve para direcionar para o formulário de busca dos dados do candidato do Vestibular
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/menus/canidatos.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarVisualizacaoDadosCandidato() {
		resetaPropriedades();
		nomeRelatorio = VISUALIZACAO_DADOS_CANDIDATO_VESTIBULAR;
		pessoa = new PessoaVestibular();
		inscricoes = new ArrayList<InscricaoVestibular>();
		return forward(FORWARD_SELECIONA_INSCRICAO_CANDIDATO);
	}
	
	/** 
	 * Relatório de Contato dos Candidatos Convocados no Vestibular. 
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/sigaa.war/vestibular/menus/canidatos.jsp</li>
	 * <li>/sigaa.war/graduacao/menus/relatorios_dae.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarRelatorioContatoCandidatosConvocados() {
		resetaPropriedades();
		nomeRelatorio = CONTATO_CANDIDATOS_CONVOCADOS;
		return forward(FORWARD_SELECIONA_PROCESSO_SELETIVO_CONVOCACAO);
	}
	
	/** Seta o formato do relatório. */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Seta o ID do curso do relatório. */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	/** Seta o ID do processo seletivo do relatório. */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Seta a ID da unidade do relatório. */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	/** Seta o título do relatório. */
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}

	/** Retorna a data de aplicação da prova. 
	 * @return
	 */
	public Date getDataAplicacao() {
		return dataAplicacao;
	}

	/** Seta a data de aplicação da prova.
	 * @param dataAplicacao
	 */
	public void setDataAplicacao(Date dataAplicacao) {
		this.dataAplicacao = dataAplicacao;
	}

	/** Indica se capta a data de aplicação da prova para geração do relatório. 
	 * @return
	 */
	public boolean isColetaDataAplicacao() {
		return coletaDataAplicacao;
	}

	/** Seta se capta a data de aplicação da prova para geração do relatório. 
	 * @param coletaDataAplicacao
	 */
	public void setColetaDataAplicacao(boolean coletaDataAplicacao) {
		this.coletaDataAplicacao = coletaDataAplicacao;
	}

	/** Retorna o ID do local de aplicação de prova do relatório. 
	 * @return
	 */
	public int getIdLocalAplicacaoProva() {
		return idLocalAplicacaoProva;
	}

	/** Seta o ID do local de aplicação de prova do relatório.
	 * @param idLocalAplicacaoProva
	 */
	public void setIdLocalAplicacaoProva(int idLocalAplicacaoProva) {
		this.idLocalAplicacaoProva = idLocalAplicacaoProva;
	}

	/** Carrega os locais de aplicação de prova.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_local.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String carregaLocalAplicacao() throws DAOException {
		LocalAplicacaoProvaDao dao = getDAO(LocalAplicacaoProvaDao.class);
		locaisAplicacao = dao.findByProcessoSeletivo(idProcessoSeletivo);
		idLocalAplicacaoProva = 0;
		return null;
	}
	
	/** Carrega os locais de aplicação de prova.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_local.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String carregaLocalAplicacao(ValueChangeEvent evt) throws DAOException {
		idProcessoSeletivo = (Integer) evt.getNewValue();
		return carregaLocalAplicacao();
	}

	/** Carrega as convocações relativas ao processo seletivo.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * <li>/SIGAA/app/sigaa.ear/sigaa.war/vestibular/relatorios/seleciona_ps_local.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String carregaConvocacaoProcessoSeletivo(ValueChangeEvent evt) throws DAOException {
		idProcessoSeletivo = (Integer) evt.getNewValue();
		ConvocacaoProcessoSeletivoDao dao = getDAO(ConvocacaoProcessoSeletivoDao.class);
		convocacoesProcessoSeletivo = dao.findByProcessoSeletivo(idProcessoSeletivo);
		idLocalAplicacaoProva = 0;
		return null;
	}
	
	/** Retorna uma lista de SelectItem de locais de aplicação de prova.
	 * @return
	 */
	public List<SelectItem> getLocaisAplicacao() {
		return toSelectItems(locaisAplicacao, "id", "nome");
	}
	
	/** Retorna uma lista de SelectItem de locais de aplicação de prova.
	 * @return
	 */
	public List<SelectItem> getConvocacoesProcessoSeletivo() {
		return toSelectItems(convocacoesProcessoSeletivo, "id", "descricao");
	}

	/** Seta a lista de locais de aplicação de prova do relatório. 
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<LocalAplicacaoProva> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/**
	 * "zera" todas variáveis utilizadas para a geração dos relatórios
	 * 
	 */
	private void resetaPropriedades() {
		idProcessoSeletivo = 0;
		idLocalAplicacaoProva = 0;
		idConvocacaoProcessoSeletivo = 0;
		idCurso = 0;
		idUnidade = 0;
		perfil = 0;
		coletaDataAplicacao = false;
		novaJanela = false;
		formato = null;
		nomeRelatorio = null;
		dataAplicacao = null;
		locaisAplicacao = null;
		convocacoesDiscente = null;
		convocacoesProcessoSeletivo = null;
		requerTitularReserva = false;
		coletaTipoDemanda = false;
		demandaFinal = false;
	}

	/** Retorna os possíveis formatados para a geração do relatório. */
	public Collection<SelectItem> getFormatosRelatorio(){
		Collection<SelectItem> opcoes = new ArrayList<SelectItem>();
		opcoes.add(new SelectItem(ARQUIVO_PDF, "Exportar formato PDF"));
		opcoes.add(new SelectItem(ARQUIVO_CSV, "Exportar formato CSV"));
		return opcoes;
	}

	/** Indica se deve abrir o relatório em uma nova janela no navegador. 
	 * @return
	 */
	public boolean isNovaJanela() {
		return novaJanela;
	}


	/** Seta se deve abrir o relatório em uma nova janela no navegador. 
	 * @param novaJanela
	 */
	public void setNovaJanela(boolean novaJanela) {
		this.novaJanela = novaJanela;
	}


	/** Indica se o formulário deve exibir a opção de fiscais titulares / reservas. 
	 * @return
	 */
	public boolean isRequerTitularReserva() {
		return requerTitularReserva;
	}


	/** Seta se o formulário deve exibir a opção de fiscais titulares / reservas. 
	 * @param requerTitularReserva
	 */
	public void setRequerTitularReserva(boolean requerTitularReserva) {
		this.requerTitularReserva = requerTitularReserva;
	}


	/** Indica se o relatório deve restringir por fiscais titulares / reservas. 
	 * @return
	 */
	public int getTitularReserva() {
		return titularReserva;
	}


	/** Seta se o relatório deve restringir por fiscais titulares / reservas. 
	 * @param titularReserva
	 */
	public void setTitularReserva(int titularReserva) {
		this.titularReserva = titularReserva;
	}


	public List<HashMap<String, Object>> getDemanda() {
		return demanda;
	}


	public void setDemanda(List<HashMap<String, Object>> demanda) {
		this.demanda = demanda;
	}


	public Map<String, Object> getIsencaoVestibular() {
		return isencaoVestibular;
	}

	public void setIsencaoVestibular(Map<String, Object> isencaoVestibular) {
		this.isencaoVestibular = isencaoVestibular;
	}

	public Collection<PessoaVestibular> getStatusFoto() {
		return statusFoto;
	}

	public void setStatusFoto(Collection<PessoaVestibular> statusFoto) {
		this.statusFoto = statusFoto;
	}


	public Collection<LinhaQuestionarioRespostas> getLinhaQuestionarioRespostas() {
		return linhaQuestionarioRespostas;
	}


	public void setLinhaQuestionarioRespostas(
			Collection<LinhaQuestionarioRespostas> linhaQuestionarioRespostas) {
		this.linhaQuestionarioRespostas = linhaQuestionarioRespostas;
	}


	public boolean isDemandaFinal() {
		return demandaFinal;
	}


	public void setDemandaFinal(boolean demandaFinal) {
		this.demandaFinal = demandaFinal;
	}


	public boolean isColetaTipoDemanda() {
		return coletaTipoDemanda;
	}


	public void setColetaTipoDemanda(boolean coletaTipoDemanda) {
		this.coletaTipoDemanda = coletaTipoDemanda;
	}


	public int getIdConvocacaoProcessoSeletivo() {
		return idConvocacaoProcessoSeletivo;
	}


	public void setIdConvocacaoProcessoSeletivo(int idConvocacaoProcessoSeletivo) {
		this.idConvocacaoProcessoSeletivo = idConvocacaoProcessoSeletivo;
	}


	public Collection<ConvocacaoProcessoSeletivoDiscente> getConvocacoesDiscente() {
		return convocacoesDiscente;
	}


	public void setConvocacoesDiscente(
			Collection<ConvocacaoProcessoSeletivoDiscente> convocacoesDiscente) {
		this.convocacoesDiscente = convocacoesDiscente;
	}

	public PessoaVestibular getPessoa() {
		return pessoa;
	}

	public void setPessoa(PessoaVestibular pessoa) {
		this.pessoa = pessoa;
	}

	public Collection<InscricaoVestibular> getInscricoes() {
		return inscricoes;
	}

	public void setInscricoes(Collection<InscricaoVestibular> inscricoes) {
		this.inscricoes = inscricoes;
	}

	public InscricaoVestibular getInscricao() {
		return inscricao;
	}

	public void setInscricao(InscricaoVestibular inscricao) {
		this.inscricao = inscricao;
	}

	public Date getDataVencimentoGRU() {
		return dataVencimentoGRU;
	}

	public void setDataVencimentoGRU(Date dataVencimentoGRU) {
		this.dataVencimentoGRU = dataVencimentoGRU;
	}

	public boolean isSelecionaFormato() {
		return selecionaFormato;
	}

	public void setSelecionaFormato(boolean selecionaFormato) {
		this.selecionaFormato = selecionaFormato;
	}

	public int getFormatoArquivo() {
		return formatoArquivo;
	}

	public void setFormatoArquivo(int formatoArquivo) {
		this.formatoArquivo = formatoArquivo;
	}
	
}