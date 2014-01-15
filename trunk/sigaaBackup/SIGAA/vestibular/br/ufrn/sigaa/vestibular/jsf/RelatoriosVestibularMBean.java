/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Controller respons�vel por gera��o de relat�rios relacionados ao Vestibular.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Component("relatoriosVestibular")
@Scope("session")
public class RelatoriosVestibularMBean extends SigaaAbstractController<ProcessoSeletivoVestibular> {

	/** ID do processo seletivo do relat�rio. */
	private int idProcessoSeletivo;
	
	/** ID do local de aplica��o de prova do relat�rio. */
	private int idLocalAplicacaoProva;
	
	/** Formato do relat�rio. */
	private String formato;
	
	/** T�tulo do relat�rio. */
	private String nomeRelatorio;
	
	/** Curso do relat�rio. */
	private int idCurso;
	
	/** ID da unidade do relat�rio. */
	private int idUnidade;
	
	/** Perfil de usu�rio do relat�rio. */
	private int perfil;
	
	/** Data de aplica��o da prova. */
	private Date dataAplicacao;
	
	/** Data de vencimento da GRU . */
	private Date dataVencimentoGRU;
	
	/** Indica se capta a data de aplica��o da prova para gera��o do relat�rio. */
	private boolean coletaDataAplicacao = false;
	
	/** Indica se deve abrir o relat�rio em uma nova janela no navegador. */
	private boolean novaJanela = false;
	
	/** Indica o tipo de demanda solicitada pelo usu�rio */
	private boolean coletaTipoDemanda = false;
	
	/** Indica de o relat�rio deve ser o relat�rio final da demanda ou o parcial */
	private boolean demandaFinal = false;

	/** Indica se o usu�rio pode ou n�o informar o formato do relat�rio. */
	private boolean selecionaFormato = false;
	
	/** Locais de aplica��o de prova do relat�rio. */
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

	/** Constante que indica a p�gina para sele��o do processo seletivo. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO = "/vestibular/relatorios/seleciona_ps.jsp";
	/** Constante que indica a p�gina para sele��o do processo seletivo e local de aplica��o. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_LOCAL_APLICACAO = "/vestibular/relatorios/seleciona_ps_local.jsp";
	/** Constante que indica a p�gina para sele��o do processo seletivo e perfil. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_PERFIL = "/vestibular/relatorios/seleciona_ps_perfil.jsp";
	/** Constante que indica a p�gina para sele��o do processo seletivo. */
	private final static String FORWARD_SELECIONA_PROCESSO_SELETIVO_CONVOCACAO = "/vestibular/relatorios/seleciona_ps_convocacao.jsp";
	/** Constante que indica a p�gina para a escolha dos parametros para a exibi��o da inscri��o do candidato. */
	private final static String FORWARD_SELECIONA_INSCRICAO_CANDIDATO = "/vestibular/relatorios/seleciona_inscricao_candidato.jsp";
	/** Constante que indica a p�gina para a escolha dos parametros para a exibi��o da inscri��o do candidato. */
	private final static String FORWARD_DETALHE_INSCRICAO_CANDIDATO = "/vestibular/relatorios/detalhe_inscricao_candidato.jsp";
	/** Constante que indica a p�gina para a escolha dos parametros para a exibi��o da inscri��o do candidato, para imopress�o */
	private final static String FORWARD_DETALHE_INSCRICAO_CANDIDATO_IMPRESSAO = "/vestibular/relatorios/detalhe_inscricao_candidato_print.jsp";
	
	// relat�rios de fiscais
	/** Relat�rio Lista Frequ�ncia Durante Aplica��o de Provas. */
	private final static String LISTA_FREQUENCIA_APLICACAO_FISCAIS = "Lista Frequ�ncia Durante Aplica��o de Provas";
	/** Relat�rio Lista de Frequ�ncia da Reuni�o de Fiscais. */
	private final static String LISTA_FREQUENCIA_REUNIAO_FISCAIS = "Lista de Frequ�ncia da Reuni�o de Fiscais";
	/** Relat�rio Lista de Distribui��o dos Fiscais no Pr�dio. */
	private final static String LISTA_DISTRIBUICAO_FISCAL = "Lista de Distribui��o dos Fiscais no Pr�dio";
	/** Relat�rio Lista de Locais de Aplica��o de Prova. */
	private final static String LISTA_LOCAIS_PROVA = "Lista de Locais de Aplica��o de Prova";
	/** Relat�rio Lista de fiscais Selecionados. */
	private final static String LISTA_FISCAIS_SELECIONADOS = "Lista de Fiscais Selecionados";
	/** Relat�rio Lista de Contato de Fiscais Reservas. */
	private final static String LISTA_CONTATO_FISCAIS_RESERVAS = "Lista de Contato de Fiscais Reservas";
	/** Relat�rio Ficha de Avalia��o Individual de Fiscal. */
	private final static String FICHA_AVALIACAO_FISCAL = "Ficha de Avalia��o Individual de Fiscal";
	/** Relat�rio Lista de Fiscais n�o Alocados. */
	private final static String LISTA_FISCAIS_NAO_ALOCADOS = "Lista de Fiscais n�o Alocados";
	/** Relat�rio de IRA M�ximos e M�nimos, por curso, dos Fiscais Selecionados. */
	private final static String RESUMO_SELECAO_FISCAIS = "Resumo do Processamento da Sele��o de Fiscais";
	/** Relat�rio Frequencia de fiscais para confer�ncia e solicita��o de pagamento. */
	private final static String FREQUENCIA_FISCAIS = "Relat�rio de Frequ�ncia de Fiscais para Confer�ncia";
	//relat�rios de inscri��o do vestibular
	/** Relat�rio Estat�tico de Inscritos no Vestibular por dia de inscri��o. */
	private final static String ESTATISTICA_DE_INSCRITOS_POR_DIA = "Estat�sticas dos Candidatos Inscritos por Dia";
	/** Relat�rio Estat�tico de Inscritos no Vestibular por dia de inscri��o. */
	private final static String ESTATISTICA_DE_INSCRITOS_POR_SEMANA = "Estat�stica de Candidatos Inscritos por Semana";
	/** Relat�rio Estat�stico dos Benefici�rios com a isen��o na inscri��o do Vestibular */
	private final static String ESTATISTICA_DE_BENEFICIARIO_ISENCAO_INSCRICAO = "Estat�sticas dos Beneficiados com a Isen��o na Inscri��o";
	/** Relat�rio da Demanda Parcial de Candidatos Inscritos por Curso */
	private final static String DEMANDA_PARCIAL_CANDIDATO_INSCRITO_POR_CURSO = "Demanda de Candidatos Inscritos por Curso";
	/** Relat�rio Estat�stico dos pagantes x Isentos no vestibular */
	private final static String ESTATISTICAS_DOS_PAGANTES_X_ISENTOS_INSCRITOS_NO_VESTIBULAR = "Estat�stica dos Pagantes X Isentos Inscritos no Vestibular";
	/** Relat�rio quantitativo das situa��es das fotos */
	private final static String QUANTITATITVO_STATUS_FOTO = "Quantitativo de Situa��es de Fotos";
	/** Relat�rio do Question�rio s�cio Econ�mico */
	private final static String RELATORIO_QUESTIONARIO_SOCIO_ECONOMICO = "Relat�rio Estat�stico das Respostas ao Question�rio Socioecon�mico";
	/** Visualiza��o dos dados do candidato do vestibular */
	private final static String VISUALIZACAO_DADOS_CANDIDATO_VESTIBULAR = "Visualiza��o da Ficha de Inscri��o";
	/** Relat�rio do Question�rio s�cio Econ�mico */
	private final static String CONTATO_CANDIDATOS_CONVOCADOS = "Lista de Contato dos Candidatos Aprovados no Vestibular";
	/** Relat�rio de GRUs pagas. */
	private final static String LISTA_GRU_PAGAS = "Lista de GRUs pagas";
	
	
	/** Indica se o formul�rio deve exibir a op��o de fiscais titulares / reservas. */
	private boolean requerTitularReserva;
	
	/** Indica se o relat�rio deve restringir por fiscais titulares / reservas. */
	private int titularReserva;

	/** Lista dos beneficiarios da isen��o da taxa de inscri��o do vestibular */
	private List<HashMap<String, Object>> demanda = new ArrayList<HashMap<String, Object>>();
	
	/** Cole��o que armazena os status das fotos. */
	private Collection<PessoaVestibular> statusFoto = new ArrayList<PessoaVestibular>();
	
	/** Map que armazena os isentos do vestibular */
	private Map<String, Object> isencaoVestibular = new TreeMap<String, Object>();

	/** Cole��o utilizada na gera��o do relat�rio estat�stico do question�rio s�cio econ�mico. */
	private Collection<LinhaQuestionarioRespostas> linhaQuestionarioRespostas = new ArrayList<LinhaQuestionarioRespostas>();

	/** Cole��o de convoca��es de um determinador processo seletivo. */
	private Collection<ConvocacaoProcessoSeletivo> convocacoesProcessoSeletivo;

	/** ID da convoca��o dos candidatos aprovados no Vestibular. */
	private int idConvocacaoProcessoSeletivo;
	
	/** Cole��o de convoca��es de discentes de um determinador processo seletivo. */
	private Collection<ConvocacaoProcessoSeletivoDiscente> convocacoesDiscente;

	/** Cole��o de convoca��es de discentes de um determinador processo seletivo. */
	private Collection<InscricaoVestibular> inscricoes;

	/** Cole��o de convoca��es de discentes de um determinador processo seletivo. */
	private InscricaoVestibular inscricao;

	/** Define que o controler ir� exportar os dados dos candidatos no formato PDF. */
	private final int ARQUIVO_PDF = 1;
	/** Define que o controler ir� exportar os dados dos candidatos no formato CSV. */
	private final int ARQUIVO_CSV = 2;
	/** Formato do arquivo de dados dos candidatos a ser exportadas (SQL ou CSV). */
	private int formatoArquivo;

	
	/** Realiza uma busca de inscri��es de fiscais no processo seletivo e seta como atributo "inscri��es".
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
	 * sess�o "fiscais".
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
	 * Busca fiscais do processo seletivo em um determinado local de aplica��o e
	 * seta o resultado como atributo de sess�o "fiscais".
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

	/** Busca inscri��es de fiscais de um curso e processo seletivo, e seta como atributo "fiscais".
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

	/** Busca inscri��es de fiscais por processo seletivo e unidade, setando como atributo "inscri��es".
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

	/** Gera o relat�rio correspondente do tipo desejado.
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

	/** Gera os relat�rios em formado PDF
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioPDF() throws DAOException {

		validaDados();
		if (hasErrors()) 
			return null;

		// Gerar relat�rio
		Connection con = null;
		try {
			formato = "pdf";
			// Popular par�metros do relat�rio
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA);
			parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
			parametros.put("concurso", idProcessoSeletivo);
			parametros.put("indiceSelecao", ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO));
			InputStream relatorio = null;
			if (nomeRelatorio.equals(LISTA_FREQUENCIA_APLICACAO_FISCAIS)) {
				if (dataAplicacao == null) {
					addMensagemErro("Insira uma data v�lida");
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

			// Preencher relat�rio
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(relatorio,
					parametros, con);
			if (prt.getPages().size() == 0) {
				addMensagemWarning("N�o foi poss�vel gerar o relat�rio, pois n�o foram encontrados fiscais associados ao processo seletivo escolhido.");
				return null;
			}
			// Exportar relat�rio de acordo com o formato escolhido
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

	/**  Validar campos do formul�rio */
	private void validaDados() {
		ValidatorUtil.validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		if (selecionaFormato && formatoArquivo == 0)
			erros.addErro("Formato do Relat�rio: Selecione um Formato para a gera��o do relat�rio.");
	}
	
	/**
	 * Gera��o do relat�rio de Frequ�ncia dos fiscais do vestibular, 
	 * ainda realiza a sua exporta��o para o formato cvs. 
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
				addMensagemWarning("N�o foi poss�vel gerar o relat�rio, pois n�o foram encontrados fiscais associados ao processo seletivo escolhido.");
				return null;
			}
			getCurrentResponse().setContentType("text/csv");
			getCurrentResponse().setCharacterEncoding("iso-8859-15");
			getCurrentResponse().setHeader("Content-disposition", "attachment; filename=Relat�rio.csv");
			PrintWriter out = getCurrentResponse().getWriter();
			out.println(dados);
			FacesContext.getCurrentInstance().responseComplete();
			return null;	
			
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Gerar o relat�rio com as Inscri��es Agrupadas semanalmente.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
	 * Cria uma inst�ncia de uma Estat�stica dos Inscritos por dia.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
	 * </ul>
	 * 
	 * @param est
	 * @param semana
	 * @return
	 */
	private EstatisticaInscritosPorDia inserirEstatistica(EstatisticaInscritosPorDia est, int semana, int mes) {
		EstatisticaInscritosPorDia estatistica = new EstatisticaInscritosPorDia();
		estatistica.setNumeroSemana(semana + "� Semana de " + CalendarUtils.getNomeMes(mes + 1));
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
	 * Gerar o relat�rio com as Inscri��es Di�rias. 
	 *
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
	
	/** Gera o relat�rio de inscritos por perfil.
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

	/** Gera o relat�rio de Ficha de Avalia��o do Fiscal.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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

	/** Gera o relat�rio do resultado do processamento da sele��o de fiscais.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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

	/** Gera o relat�rio de locais de prova.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
	
	/** Gera o relat�rio de fiscais n�o alocados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
	 * Gera o relat�rio de contato de fiscais reservas (nome, bairro, telefone,
	 * celular), utilizado para convoca��o de fiscais que preencher�o a lacuna
	 * deixada por fiscais ausentes no dia da prova. 
	 *  
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String gerarListaContatoFiscaisReservas() throws DAOException {
		Boolean reserva;
		// Validar campos do formul�rio
		ValidatorUtil.validateRequiredId(idLocalAplicacaoProva, "Local de Aplica��o", erros);
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
	 * Gera o relat�rio de IRA m�nimo e m�ximo, por curso, dos fiscais selecionados.
	 * 
	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
			addMensagemWarning("N�o h� dados para exibir no relat�rio.");
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
	 * Gera o relat�rio Estat�stico dos Benefici�rios com a Isen��o na Inscri��o do Vestibular
	 * 
 	 * <br />
	 * Chamado por 
	 * <ul>
	 * 	<li>N�o � invocado por JSP</li>
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
	 * Gerar um relat�rio atual com a demanda parcial de candidatos inscritos por curso.
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
	 * Gera um relat�rios com os candidatos beneficiados com a isen��o, candidatos pagantes, dos candidatos pagantes com CPF's distintos
	 * o n�mero de candidatos isentos inscritos como pagante, total de inscri��es e o total de inscri��es com cpf distinto.
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
			isencaoVestibular.put("Candidatos beneficiados com a isen��o", daoIsento.quantCandidatosBeneficiadoIsencao(idProcessoSeletivo));
			isencaoVestibular.put("Candidatos pagantes", dao.quantCandidatosPagantes(idProcessoSeletivo));
			isencaoVestibular.put("Candidatos pagantes (CPF's distintos)", dao.quantCandidatosPagantesDistintos(idProcessoSeletivo));
			isencaoVestibular.put("Inscri��es com CPF's repetidos (candidatos pagantes)", 
					((Integer) isencaoVestibular.get("Candidatos pagantes")) - (Integer)isencaoVestibular.get("Candidatos pagantes (CPF's distintos)"));
			isencaoVestibular.put("N�mero de candidatos isentos inscritos como pagante", daoIsento.quatIsentoCadPagante(idProcessoSeletivo));
			isencaoVestibular.put("Total de inscri��es no banco", dao.quantTotalInscricoes(idProcessoSeletivo, demandaFinal));
			isencaoVestibular.put("Total de inscri��es no banco (CPF's distintos)", dao.quantTotalInscricoesDistintas(idProcessoSeletivo, demandaFinal));
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
	 * Gerar um relat�rio quantitativo dos status das fotos.
	 * 
 	 * <br/>M�todo n�o invocado por JSP�s.
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
	 * Gerar o relat�rio s�cio econ�mico.
	 * 
 	 * <br/>M�todo n�o invocado por JSP�s.
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
				addMensagemWarning("O processo seletivo n�o possui question�rio Socioecon�mico.");
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
				addMensagemErro("O Question�rio Socioecon�mico ainda n�o foi respondido.");
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
	 * Gera o relat�rio com dados de contato dos candidatos convocados.
	 * 
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioGRUPagas() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		// Validar campos do formul�rio
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
	 * Gera o relat�rio com dados de contato dos candidatos convocados.
	 * 
	 * <br/>M�todo n�o invocado por JSP�s.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String gerarRelatorioContatoCandidatosConvocados() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR, SigaaPapeis.ADMINISTRADOR_DAE);
		// Validar campos do formul�rio
		ValidatorUtil.validateRequiredId(idProcessoSeletivo, "Processo Seletivo", erros);
		ValidatorUtil.validateRequiredId(idConvocacaoProcessoSeletivo, "Convoca��o", erros);
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
	 * Respons�vel pela realiza��o de uma busca, para retornar todos os inscritos. Baseando-se nos filtros.
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
	
	/** Selecionar uma Inscri��o do Vestibular para reimpress�o da GRU
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
			addMensagemErro("Erro na identifica��o da inscri��o.");
			return null;
		}
		dataVencimentoGRU = inscricao.getProcessoSeletivo().getDataVencimentoBoleto();
		return forward("/vestibular/relatorios/seleciona_vencimento_gru.jsp");
	}
	
	/** Imprime a GRU para pagamento da taxa de inscri��o do vestibular.
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
		validateRequired(inscricao, "Erro na identifica��o da inscri��o.", erros);
		validateRequired(dataVencimentoGRU, "Data de Vencimento da GRU", erros);
		if (hasErrors())
			return null;
		inscricao = getGenericDAO().refresh(inscricao);
		try {
			if (inscricao.getIdGRU() != null) {
	 			GuiaRecolhimentoUniao gru = GuiaRecolhimentoUniaoHelper.getGRUByID(inscricao.getIdGRU());
	 			if (gru != null) {
	 				// altera a data de vencimento, caso o usu�rio informe outra
	 				gru.setVencimento(dataVencimentoGRU);
	 				GuiaRecolhimentoUniaoHelper.geraCodigoBarras(gru);
		 			// par�metros do relat�rio
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
 				throw new NegocioException("N�o foi poss�vel encontrar os dados para gerar a GRU.");
 			}
 		} catch (JRException e) {
 			throw new ArqException(e);
 		}
		return null;
	}
	
	/**
	 * Respons�vel pelo detalhamento das informa��es da inscri��o do candidato.
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
	 * Respons�vel pelo detalhamento das informa��es da inscri��o do candidato no formato para impress�o.
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
	
	/** Retorna o formato do relat�rio. */
	public String getFormato() {
		return formato;
	}

	/** Retorna o ID do curso do relat�rio. */
	public int getIdCurso() {
		return idCurso;
	}

	/** Seta o ID do processo seletivo do relat�rio. */
	public int getIdProcessoSeletivo() {
		return idProcessoSeletivo;
	}

	/** Retorna a ID da unidade do relat�rio. */
	public int getIdUnidade() {
		return idUnidade;
	}

	/** Retorna o t�tulo do relat�rio. */
	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public int getPerfil() {
		return perfil;
	}
	
	/** Inicia a gera��o do Relat�rio Lista Frequ�ncia Durante Aplica��o de Provas. 
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
	
	/** Inicia a gera��o do Relat�rio Lista Frequ�ncia Durante Aplica��o de Provas. 
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

	/** Inicia a gera��o do Relat�rio Lista Frequ�ncia Durante Aplica��o de Provas. 
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
	 * Inicia a gera��o do Relat�rio dos Benefici�rios com a iser��o na Inscri��o
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
	 * Inicia o Relat�rio dos Pagantes X Isentos Inscritos no Vestibular.
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
	
	/** Inicia a gera��o do Relat�rio Lista Frequ�ncia Durante Aplica��o de Provas. 
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

	/** Inicia a gera��o do Relat�rio Ficha de Avalia��o Individual de Fiscal.
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

	/** Inicia a gera��o do Relat�rio Lista de Frequ�ncia da Reuni�o de Fiscais.
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

	/** Inicia a gera��o do Relat�rio Lista de fiscais Selecionados.
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

	/** Inicia a gera��o do relat�rio de inscritos.
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

	/** Inicia a gera��o do Relat�rio Lista de Locais de Aplica��o de Prova.
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

	/** Inicia a gera��o do Relat�rio Lista de Distribui��o dos Fiscais no Pr�dio.
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
	
	/** Inicia a gera��o do Relat�rio Lista de Fiscais n�o alocados.
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
	
	/** Inicia a gera��o do Relat�rio Lista de Contatos de Fiscais Reservas.
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
	
	/** Inicia a gera��o do Relat�rio IRA M�ximos e M�nimos dos Fiscais Selecionados.
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
	
	/** Inicia a gera��o do Relat�rio IRA M�ximos e M�nimos dos Fiscais Selecionados.
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
	 * Quantitativo das descri��es e dos quantitativos dos status das fotos.
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
	 * Quantitativo das descri��es e dos quantitativos dos status das fotos.
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
	 * Serve para direcionar para o formul�rio de busca dos dados do candidato do Vestibular
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
	 * Relat�rio de Contato dos Candidatos Convocados no Vestibular. 
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
	
	/** Seta o formato do relat�rio. */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Seta o ID do curso do relat�rio. */
	public void setIdCurso(int idCurso) {
		this.idCurso = idCurso;
	}

	/** Seta o ID do processo seletivo do relat�rio. */
	public void setIdProcessoSeletivo(int idProcessoSeletivo) {
		this.idProcessoSeletivo = idProcessoSeletivo;
	}

	/** Seta a ID da unidade do relat�rio. */
	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	/** Seta o t�tulo do relat�rio. */
	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

	public void setPerfil(int perfil) {
		this.perfil = perfil;
	}

	/** Retorna a data de aplica��o da prova. 
	 * @return
	 */
	public Date getDataAplicacao() {
		return dataAplicacao;
	}

	/** Seta a data de aplica��o da prova.
	 * @param dataAplicacao
	 */
	public void setDataAplicacao(Date dataAplicacao) {
		this.dataAplicacao = dataAplicacao;
	}

	/** Indica se capta a data de aplica��o da prova para gera��o do relat�rio. 
	 * @return
	 */
	public boolean isColetaDataAplicacao() {
		return coletaDataAplicacao;
	}

	/** Seta se capta a data de aplica��o da prova para gera��o do relat�rio. 
	 * @param coletaDataAplicacao
	 */
	public void setColetaDataAplicacao(boolean coletaDataAplicacao) {
		this.coletaDataAplicacao = coletaDataAplicacao;
	}

	/** Retorna o ID do local de aplica��o de prova do relat�rio. 
	 * @return
	 */
	public int getIdLocalAplicacaoProva() {
		return idLocalAplicacaoProva;
	}

	/** Seta o ID do local de aplica��o de prova do relat�rio.
	 * @param idLocalAplicacaoProva
	 */
	public void setIdLocalAplicacaoProva(int idLocalAplicacaoProva) {
		this.idLocalAplicacaoProva = idLocalAplicacaoProva;
	}

	/** Carrega os locais de aplica��o de prova.
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
	
	/** Carrega os locais de aplica��o de prova.
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

	/** Carrega as convoca��es relativas ao processo seletivo.
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
	
	/** Retorna uma lista de SelectItem de locais de aplica��o de prova.
	 * @return
	 */
	public List<SelectItem> getLocaisAplicacao() {
		return toSelectItems(locaisAplicacao, "id", "nome");
	}
	
	/** Retorna uma lista de SelectItem de locais de aplica��o de prova.
	 * @return
	 */
	public List<SelectItem> getConvocacoesProcessoSeletivo() {
		return toSelectItems(convocacoesProcessoSeletivo, "id", "descricao");
	}

	/** Seta a lista de locais de aplica��o de prova do relat�rio. 
	 * @param locaisAplicacao
	 */
	public void setLocaisAplicacao(List<LocalAplicacaoProva> locaisAplicacao) {
		this.locaisAplicacao = locaisAplicacao;
	}

	/**
	 * "zera" todas vari�veis utilizadas para a gera��o dos relat�rios
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

	/** Retorna os poss�veis formatados para a gera��o do relat�rio. */
	public Collection<SelectItem> getFormatosRelatorio(){
		Collection<SelectItem> opcoes = new ArrayList<SelectItem>();
		opcoes.add(new SelectItem(ARQUIVO_PDF, "Exportar formato PDF"));
		opcoes.add(new SelectItem(ARQUIVO_CSV, "Exportar formato CSV"));
		return opcoes;
	}

	/** Indica se deve abrir o relat�rio em uma nova janela no navegador. 
	 * @return
	 */
	public boolean isNovaJanela() {
		return novaJanela;
	}


	/** Seta se deve abrir o relat�rio em uma nova janela no navegador. 
	 * @param novaJanela
	 */
	public void setNovaJanela(boolean novaJanela) {
		this.novaJanela = novaJanela;
	}


	/** Indica se o formul�rio deve exibir a op��o de fiscais titulares / reservas. 
	 * @return
	 */
	public boolean isRequerTitularReserva() {
		return requerTitularReserva;
	}


	/** Seta se o formul�rio deve exibir a op��o de fiscais titulares / reservas. 
	 * @param requerTitularReserva
	 */
	public void setRequerTitularReserva(boolean requerTitularReserva) {
		this.requerTitularReserva = requerTitularReserva;
	}


	/** Indica se o relat�rio deve restringir por fiscais titulares / reservas. 
	 * @return
	 */
	public int getTitularReserva() {
		return titularReserva;
	}


	/** Seta se o relat�rio deve restringir por fiscais titulares / reservas. 
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