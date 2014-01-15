/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 28/08/2008
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.apache.commons.lang.WordUtils;
import org.hibernate.HibernateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.integracao.dto.AlunoDTO;
import br.ufrn.integracao.interfaces.DadosBolsistasRemoteService;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.MunicipioDao;
import br.ufrn.sigaa.arq.dao.sae.AdesaoCadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.dao.sae.BolsaAuxilioDao;
import br.ufrn.sigaa.arq.dao.sae.RelatorioSAEDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.assistencia.dao.DesempenhoAcademicoBolsistaDao;
import br.ufrn.sigaa.assistencia.dao.MovimentacaoDiscenteDao;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.relatorio.dominio.LinhaDesempenhoAcademico;
import br.ufrn.sigaa.assistencia.relatorio.dominio.LinhaMovimentacaoDiscente;
import br.ufrn.sigaa.assistencia.restaurante.dominio.RegistroAcessoRU;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.TipoBolsa;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.jsf.CursoMBean;
import br.ufrn.sigaa.parametros.dominio.ParametrosSAE;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * MBean respons�vel por passar os par�metros
 * para os relat�rios (.jasper) do SAE.
 * 
 * @author agostinho campos
 *
 */

@Component("relatoriosSaeMBean")
@Scope("session")
public class RelatoriosSaeMBean extends SigaaAbstractController<RelatoriosSaeMBean> {
	
	/** Servi�o respons�vel pela comunica��o com SIPAC para retornar os discente bolsistas */
	@Autowired
	private DadosBolsistasRemoteService service;

	/** Constante indicativa do diret�rio dos arquivos-fontes dos relat�rios. **/
	public static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/relatorios/fontes/";
	
	/** Constante indicativa do diret�rio dos arquivos-fontes dos relat�rios. **/
	public static final String JSP_REL_CONTEMPLADOS_DEFERIDOS = "rel_contemplados_deferidos.jsf";

	/**
	 * ATEN��O:
	 * 
	 * O SAE informou que boa parte dos relat�rios nunca chegaram a ser usados pois
	 * n�o atendiam as suas necessidades. Eles solicitaram a remo��o dos mesmos para 
	 * n�o ficar poluindo o Portal. Os links que est�o comentados em /sae/menu.jsp
	 * representa os links que n�o est�o mais sendo usados. 
	 * 
	 * Dessa forma, existem alguns m�todos que n�o est�o sendo invocados nessa classe. 
	 */
	
	/** Constante indicativa do Relat�rio a ser gerado: Discentes cuja situa��o 
	 * da solicita��o de bolsa aux�lio � contemplada e deferida. **/
	private static final int RELATORIO_CONTEMPLADOS_DEFERIDOS = 1;
	
	/** Constante indicativa do Relat�rio a ser gerado: escolaridade do pai
	 *  do solicitante de bolsa aux�lio. **/
	private static final int RELATORIO_ESCOLARIDADE_PAI = 2;
	
	/** Constante indicativa do Relat�rio a ser gerado: bairro do solicitante de bolsa aux�lio. **/
	private static final int RELATORIO_BAIRRO = 3;
	
	/** Constante indicativa do Relat�rio a ser gerado: da profiss�o do pai do solicitante de bolsa aux�lio. **/
	private static final int RELATORIO_PROFISSAO_PAI = 4;
	
	/** Constante indicativa do Relat�rio a ser gerado: dos solicitante de bolsa aux�lio ALIMENTA��O e RESID�NCIA. **/
	private static final int RELATORIO_ALIMENTACAO_RESIDENCIA = 5;
	
	/** Constante indicativa do Relat�rio a ser gerado: todos os discentes com bolsa aux�lio ALIMENTA��O. **/
	private static final int RELATORIO_TODOS_ALUNOS_COM_BOLSA_ALIMENTACAO = 6;
	
	/** Constante indicativa do Relat�rio a ser gerado: desempenho do solicitante de bolsa aux�lio. **/
	private static final int RELATORIO_DESEMPENHO = 7;

	/** Ano relativo a gera��o de relat�rios. **/
	private Integer ano = 0;
	
	/** Indica se o campo � de preenchimento obrigat�rio. **/
	private boolean obrigatorio = true;
	
	/** Per�odo relativo a gera��o de relat�rios. **/
	private Integer periodo = 0;
	
	/** Total de discentes ocupantes da Resid�ncia de Gradua��o. **/
	private Integer totalOcupantesResidenciaGrad = 0;
	
	/** Total de discentes ocupantes da Resid�ncia da P�s. **/
	private Integer totalOcupantesResidenciaPos = 0;
	
	/** Total de discentes ocupantes da Resid�ncia. **/
	private Integer totalOcupantesResidencia = 0;
	
	
	/** Filtros utilizados exclusivamente na consulta de desempenho dos discente bolsistas **/
	
	/** Ano sem o usufruto da bolsa aux�lio. **/
	private Integer anoSem = 0;
	
	/** Per�odo sem o usufruto da bolsa aux�lio. **/
	private Integer periodoSem = 0;
	
	/** N�vel de ensino considerado no relat�rio. **/
	private Character nivel;
	
	/** Matr�cula do discente considerado no relat�rio. **/
	private Long matricula;
	
	/** Nome do discente considerado no relat�rio. **/
	private String nome;
	
	/** Nome do relat�rio gerado em formato de arquivo. **/
	private String arquivoRelatorio;	
	
	/** Tipo do formato usado no relat�rio. **/
	private String formato = "pdf";
	
	/** Cole��o de todos os cursos. **/
	private Collection<SelectItem> allCursos;
	
	/**
	 * Usado como filtro no relat�rio
	 */
	
	/** Fitrar pelo Munic�pio **/
	private int idMunicipio;
	
	/** Fitrar pela Unidade da Federa��o. **/
	private int idUF;
	
	/** Fitrar pelo Centro. **/
	private int idCentro;
	
	/** Fitrar pela Resid�ncia Universit�ria. **/
	private ResidenciaUniversitaria residencia;
	
	/** Unidade utilizado no relat�rio. **/
	private Unidade unidade = new Unidade();
	
	/** Curso utilizado no relat�rio. **/
	private Curso curso = new Curso();
	
	/** Fitrar pelo Curso. **/
	private boolean buscaCurso;
	
	/** Fitrar pela Unidade. **/
	private boolean buscaUnidade;
	
	/** Rela��o de discentes do cadastro �nico. **/
	private Map<String, List<AdesaoCadastroUnicoBolsa>> discentesCadastroUnico;
	
	/**
	 * 
	 *  Especifica o tipo de Relat�rio a ser gerado:Discentes cuja situa��o
	 *  da solicita��o de bolsa aux�lio � contemplada e deferida.
	 *   
	**/
	private boolean relatorioContempladosDeferidos;
	
	/**
	 * 
	 *  Especifica o tipo de Relat�rio a ser gerado: escolaridade do pai do
	 *  solicitante de bolsa aux�lio.
	 *   
	 **/
	private boolean relatorioEscolaridadePai;
	
	/**
	 * 
	 * Especifica o tipo de Relat�rio a ser gerado: bairro do solicitante 
	 * de bolsa aux�lio.
	 *  
	 **/
	private boolean relatorioBairro;
	
	/**
	 *  
	 * Especifica o tipo de Relat�rio a ser gerado:  da profiss�o do pai 
	 * do solicitante de bolsa aux�lio.
	 *  
	 **/
	private boolean relatorioProfissao;
	
	/**
	 * 
	 *  Especifica o tipo de Relat�rio a ser gerado: solicitantes de
	 *  bolsa aux�lio ALIMENTA��O e RESID�NCIA.
	 *   
	 **/
	private boolean relatorioAlimentacaoResidencia;
	
	/** 
	 * 
	 * Especifica o tipo de Relat�rio a ser gerado: todos os discentes 
	 * com bolsa aux�lio ALIMENTA��O. 
	 * 
	 **/
	private boolean relatorioTodosAlunosAlimentacao;
	
	/** 
	 * 
	 * Especifica o tipo de Relat�rio a ser gerado: residentes que 
	 * possuem cadastro �nico. 
	 * 
	 **/
	private boolean relatorioResidentesNoCadastroUnico;
	
	/**
	 * 
	 *  Especifica o tipo de Relat�rio a ser gerado: desempenho
	 *  do solicitante de bolsa aux�lio. 
	 *  
	 **/
	private boolean relatorioDesempenho;
	
	/**
	 * Listagem de munic�pios que ser�o geradas
	 */
	private Collection<SelectItem> municipiosEndereco = new ArrayList<SelectItem>(0);
	
	/**
	 * Listagem de resid�ncias da P�s.
	 */
	private List<BolsaAuxilio> listaResidenciasPos;
	
	/**
	 * Listagem de resid�ncias da Gradua��o.
	 */
	private List<BolsaAuxilio> listaResidenciasGraduacao;
	
	/**
	 * Listagem de resid�ncias da Gradua��o.
	 */
	private List<BolsaAuxilio> listaBolsistasContemplados;
	
	/**
	 * Discentes que s�o localizados e exibidos
	 */
	private List<Discente> listaDiscentes;

	/**
	 * Discentes que acessam o RU atrav�s da catraca
	 */
	private List<RegistroAcessoRU> listaRegistroAcessoRU;
	
	/**
	 * Residentes que possuem Cadastro �nico  
	 */
	private List<Object> listaResidentesCadastroUnico;
	
	/**
	 * Desempenho dos discentes bolsitas  
	 */
	private List<Map<String,Object>> listaDesempenhoDiscentes;
	
	/**
	 * Usado para exibir todos os tipos de bolsa do SAE em ComboBox  
	 */
	private Collection<SelectItem> allTiposBolsas;
	
	/**
	 * Tipo Bolsa Auxilio selecionada
	 */
	private TipoBolsaAuxilio tipoBolsaAuxilio;
	
	/**
	 * Bolsas do SIPAC do tipo Monitoria, Extens�o, Ap�io T�cnico Adm. e de Pesquisa.
	 */
	private Collection<SelectItem> allTiposBolsasSIPAC;
	
	/**
	 * Flag usada quando usu�rio selecionar relat�rio de bolsistas do SIPAC 
	 */
	private boolean relatorioBolsasSIPAC;
	
	/**
	 * Usado para referenciar o tipo de bolsa selecionado 
	 */
	private TipoBolsa tipoBolsaSIPAC;
	
	/**
	 * Listagem de discentes com bolsas diferentes do SAE (Monitoria, Extens�o, Ap�io T�cnico Adm. e de Pesquisa.)
	 */
	private List<Bolsista> listaBolsistasSIPAC;
	
	/** Desempenho dos discentes com bolsistas */
	private List<LinhaDesempenhoAcademico> desempenhoDiscentes;

	/** Desempenho dos discentes com bolsistas */
	private String descricaoBolsa;
	
	/** seleciona tipo bolsa ao relat�rio */
	private boolean checkTipoBolsa;
	
	/** tipo de bolsa selecionado para gerar relat�rio  */
	private Integer tipoBolsaSelecionada;
	
	/** Op��es de relat�rio de movimenta��o dos discentes */
	private Collection<SelectItem> tiposRelatorioMovimentacao = new ArrayList<SelectItem>();
	
	/** Apresenta as op��es selecionadas dos relat�rios */
	private List<String> relatorioMovimentacaoEscolhido;
	
	/** Apresenta o resultado do relat�rio de movimenta��o dos discentes */
	private Collection<LinhaMovimentacaoDiscente> movimentacaoDiscente;
	
	/**Lista dos discentes que possuem simultaneamente v�nculo de gradua��o e p�s.*/
	private List<Map<String,Object>> discentesGraducaoPos;
	
	/**
	 * 
	 * Construtor populando todos os cursos para ser usado no combo. 
	 * 
	 * @throws DAOException 
	 */
	public RelatoriosSaeMBean() throws DAOException {
		allCursos = getAllCursoGraduacaoCombo();
	}
	
	@Override
	public String getDirBase() {
		return "/sae/relatorios/";
	}
	
	/**
	 * Retorna N�vel de ensino como SelectItem
	 * <br />
	 * M�todo n�o invocado por JSP�s
	 *   
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllCursoGraduacaoCombo() throws DAOException{
		CursoDao dao = getDAO(CursoDao.class);
		return toSelectItems(dao.findByNivel(NivelEnsino.GRADUACAO), "id", "descricao");
	}
	
	
	/**
	 * Exibe as bolsas deferidas e contempladas
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioContempladosDeferidos() {
	
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		relatorioContempladosDeferidos = true;
		listaBolsistasContemplados = new ArrayList<BolsaAuxilio>();
		listaBolsistasSIPAC = new ArrayList<Bolsista>();
		
		return forward("/sae/relatorios/form_relatorio.jsp");
	}
	
	/**
	 * Inicia relat�rio que mostra a escolaridade do pai
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioEscolaridadePai() {
		arquivoRelatorio = "trf7884_SAE_i_Escolaridade";
		relatorioEscolaridadePai = true;
	
		relatorioTodosAlunosAlimentacao = false;
		relatorioContempladosDeferidos = false;
		relatorioBairro = false;
		relatorioProfissao = false;
		relatorioAlimentacaoResidencia = false;
		return forward("/sae/relatorios/form_relatorio.jsp");
	}
	
	/**
	 * Exibe as bolsas por bairro
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioBairro() {
		arquivoRelatorio = "trf7884_SAE_g_Bairro";
		relatorioBairro = true;
	
		relatorioTodosAlunosAlimentacao = false;
		relatorioEscolaridadePai = false;
		relatorioContempladosDeferidos = false;
		relatorioProfissao = false;
		relatorioAlimentacaoResidencia = false;
		return forward("/sae/relatorios/form_relatorio.jsp");
	}
	
	/**
	 * Exibe os relat�rios exibindo a profiss�o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioProfissao() {
		arquivoRelatorio = "trf7884_SAE_j_Profissao";
		relatorioProfissao = true;
		
		relatorioTodosAlunosAlimentacao = false;
		relatorioContempladosDeferidos = false;
		relatorioEscolaridadePai = false;
		relatorioBairro = false;
		relatorioAlimentacaoResidencia = false;
		return forward("/sae/relatorios/form_relatorio.jsp");
	}
	
	/**
	 * Exibe um resumo das bolsas Alimenta��o e Resid�ncia
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return 
	 */
	public String iniciarRelatorioAlimentacaoResidencia() {
		arquivoRelatorio = "trf7884_SAE_ef_UF_Municipio";
		relatorioAlimentacaoResidencia = true;
		
		relatorioTodosAlunosAlimentacao = false;
		relatorioContempladosDeferidos = false;
		relatorioEscolaridadePai = false;
		relatorioBairro = false;
		relatorioProfissao = false;
		return forward("/sae/relatorios/form_relatorio.jsp");
	}
	
	/**
	 * Prepara o formul�rio para gera��o do relat�rio de desempenho dos bolsistas
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarRelatorioDesempenho() throws HibernateException, DAOException {
		
		relatorioDesempenho = true;
		
		relatorioTodosAlunosAlimentacao = false;
		relatorioContempladosDeferidos = false;
		relatorioEscolaridadePai = false;
		relatorioBairro = false;
		relatorioProfissao = false;
		
		tipoBolsaAuxilio = new TipoBolsaAuxilio();
		curso = new Curso();
		
		return forward("/sae/relatorios/form_desempenho.jsp");
	}

	/**
	 * Prepara o formul�rio para gera��o do relat�rio de desempenho dos bolsistas
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return 
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String iniciarRelatorioDesempenhoAcademico() throws HibernateException, DAOException {
		ano = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		
		return forward("/sae/relatorios/form_desempenho_academico.jsp");
	}

	
	/**
	 * Exibe todos os alunos com bolsa alimenta��o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioTodosAlunosBolsaAlimentacao() {
		arquivoRelatorio = "trf10134_SAEAlunosBolsaAlim";
		relatorioTodosAlunosAlimentacao = true;
		
		relatorioAlimentacaoResidencia = false;
		relatorioContempladosDeferidos = false;
		relatorioEscolaridadePai = false;
		relatorioBairro = false;
		relatorioProfissao = false;
		return forward("/sae/relatorios/form_relatorio.jsp");
	}

	/**
	 * Inicia o relat�rio que busca os alunos residentes e seu v�nculo com o cadastro �nico
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp  </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarRelatorioResidentesNoCadastroUnico() throws DAOException {
		ano = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(); 
		periodo = 	CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();
		return forward("/sae/relatorios/form_residente_cadastro_unico.jsp");
	}
	
	/**
	 * Formul�rio do Relat�rio que busca alunos no cadastro �nico por centro ou curso
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarRelatorioAlunosCadastroUnico() throws DAOException {
		unidade = new Unidade();
		curso = new Curso();
		periodo = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();
		ano = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno();
		
		return forward("/sae/relatorios/form_qtd_alunos_cad_unico.jsp");
	}
	
	/**
	 * Formul�rio do Relat�rio que busca alunos que estejam em situa��o carente ou n�o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciarRelatorioDiscentesSituacaoCarencia() throws DAOException {
		allTiposBolsas = getAll(TipoBolsaAuxilio.class, "id", "denominacao");
		tipoBolsaAuxilio = new TipoBolsaAuxilio();
		listaDiscentes = new ArrayList<Discente>();
		relatorioBolsasSIPAC = false;
		
		ano = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getAno(); 
		periodo = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad().getPeriodo();
		
		return forward("/sae/relatorios/form_discentes_carentes.jsp");
	}
		
	/**
	 * Gera o relat�rio de alunos que possuem simultaneamente um v�nculo de gradua��o e outro de p�s gradua��o
	 * 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 */
	public String gerarRelatorioDiscentesVinculadosGraducaoPos() {
		RelatorioSAEDao dao = getDAO(RelatorioSAEDao.class);
		try {
			discentesGraducaoPos = dao.findDiscentesVinculadosGraduacaoPos();
		}
		catch (DAOException e) {
			tratamentoErroPadrao(e);
		}
		finally {
			if (dao != null)
				dao.close();
		}
		return  forward("/sae/relatorios/relatorio_discentes_graduacao_pos.jsp");
	}
	
	/**
	 * Formul�rio do Relat�rio que busca alunos que estejam em situa��o carente ou n�o.
	 * Os tipos de bolsa exibido aqui s�o as que est�o no SIPAC 
	 * 
	 *  BOLSA_MONITORIA = 3;
	 *	BOLSA_EXTENSAO = 4; 
	 *	APOIO_TECNICO = 5;
	 *	BOLSA_PESQUISA = 25;
	 *		
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciarRelatorioDiscentesSituacaoCarenciaSIPAC() {
		Collection<TipoBolsa> lista = getDAO(RelatorioSAEDao.class).findTipoBolsaSIPAC();
		allTiposBolsasSIPAC = toSelectItems(lista, "id", "descricao");
		relatorioBolsasSIPAC = true;
		tipoBolsaSIPAC = new TipoBolsa();
		
		return forward("/sae/relatorios/form_discentes_carentes.jsp");
	}
	
	/**
	 * Retorna todas as Bolsas existentes (SIPAC)
	 * @author Edson Anibal
	 */
	public Collection<SelectItem> getAllBolsaCombo() throws DAOException
	{
		DesempenhoAcademicoBolsistaDao dao = getDAO(DesempenhoAcademicoBolsistaDao.class);
		return toSelectItems(dao.findAllTipoBolsa(), "id", "denominacao");
	}
	
	/**
	 * Gera o relat�rio de bolsistas que tem bolsas n�o finalizadas de acordo com o tipo da bolsa 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/relatorios/form_discentes_carentes.jsp </ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioDiscentesSituacaoCarenciaSIPAC() throws HibernateException, DAOException {
		if (isEmpty(tipoBolsaSIPAC.getId()) ) {
			addMensagemErro("Voc� precisa selecionar um tipo de bolsa");
			return null;
		}
		
		RelatorioSAEDao dao = getDAO(RelatorioSAEDao.class); 
		listaBolsistasSIPAC = dao.findBolsistasSIPACByTipoBolsa(tipoBolsaSIPAC.getId());
		tipoBolsaSIPAC = dao.findTipoBolsaSIPACByID(tipoBolsaSIPAC.getId());
		
		return forward("/sae/relatorios/lista_discentes_carentes.jsf");
	}
	

	/**
	 * Gera o relat�rio de desempenho dos bolsistas.
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/relatorios/form_desempenho.jsp</li></ul>
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioDesempenho() throws HibernateException, DAOException {
		
		erros = validarEntrada();
		
		if (hasErrors()) {
			return null;
		}
		
		RelatorioSAEDao dao = getDAO(RelatorioSAEDao.class); 
		
		setCurso(dao.findByPrimaryKey(getCurso().getId(), Curso.class));
		setTipoBolsaAuxilio(dao.findByPrimaryKey(getTipoBolsaAuxilio().getId(), TipoBolsaAuxilio.class));				
		
		listaDesempenhoDiscentes = dao.findDesempenhoDiscentesBolsaAuxilio(anoSem, periodoSem,
				ano, periodo, curso, tipoBolsaAuxilio, matricula, nome, null);
		
		if (listaDesempenhoDiscentes.size() <= 0){
			addMensagemErro("Nenhum registro foi encontrado com os par�metros fornecidos.");
			return null;
		}
		
		return forward("/sae/relatorios/lista_discentes_desempenho.jsf");
	}

	/**
	 * M�todo respons�vel pela gera��o do relat�rio de desempenho acad�mico dos bolsistas.
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li> /SIGAA/app/sigaa.ear/sigaa.war/sae/relatorios/form_desempenho_academico.jsp </li>
	 * 	</ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String gerarRelatorioDesempenhoAcademico() throws HibernateException, DAOException {
		
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Per�odo", erros);
		if(checkTipoBolsa){
			ValidatorUtil.validaInt(tipoBolsaSelecionada, "Tipo de Bolsa", erros);
		}
		
		if (hasErrors())
			return null;
		
		Date dataInicio;
		Date dataFim;
		
		if (periodo == 1){ 
			dataInicio = CalendarUtils.createDate(01, 0, ano);
			dataFim = CalendarUtils.createDate(30, 05, ano);
		} else {
			dataInicio = CalendarUtils.createDate(01, 06, ano);
			dataFim = CalendarUtils.createDate(31, 11, ano);
		}
		List<LinhaDesempenhoAcademico> linhas = new ArrayList<LinhaDesempenhoAcademico>();
		List<AlunoDTO> alunoDTO = service.findAllDiscenteBolsistasPorPeriodo(dataInicio, dataFim, checkTipoBolsa ? tipoBolsaSelecionada : 0);
		desempenhoDiscentes = new ArrayList<LinhaDesempenhoAcademico>();
		LinhaDesempenhoAcademico linha = null;
		int tipoBolsa = 0;
			
		if ( ValidatorUtil.isAllEmpty( alunoDTO ) ) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

		for (AlunoDTO aluno : alunoDTO) {
			
			if (tipoBolsa == 0 || tipoBolsa != aluno.getIdBolsa()) {
				if (tipoBolsa != aluno.getIdBolsa() && tipoBolsa != 0 && linha != null){
					linhas.add(linha);
				}
					linha = new LinhaDesempenhoAcademico();
			}

			if (linha != null && LinhaDesempenhoAcademico.compareTo(linha.getIdTipoBolsa(), desempenhoDiscentes)){
				linha.setBolsa(aluno.getCurso().getDenominacao());
				linha.setIdTipoBolsa(aluno.getIdBolsa());
				linha.getAlunos().add(aluno);
				tipoBolsa = aluno.getIdBolsa();
				
				if(checkTipoBolsa && LinhaDesempenhoAcademico.compareToBolsas(aluno.getIdBolsa(), alunoDTO) && linhas.size() == 0){
					linhas.add(linha);
				}
				
			} else if( linha != null ){
				linha.getAlunos().add(aluno);
			}
		}
		
		if(!linhas.isEmpty())
			adicionarInformacaoLinhaRelatorioDesempenho(linhas);

		checkTipoBolsa = false;
		tipoBolsaSelecionada = new Integer(0);
		return forward("/sae/relatorios/lista_discente_desempenho_academico.jsf");
	}

	/**
	 * M�todo respons�vel pela gera��o de uma linha do relat�rio de desempenho acad�mico do discente.
	 * 
	 * @param linhas
	 * @throws DAOException
	 */
	private void adicionarInformacaoLinhaRelatorioDesempenho(List<LinhaDesempenhoAcademico> linhas) throws DAOException {
		
		for (LinhaDesempenhoAcademico linha : linhas) {
		
			int total;
			linha.setTotalBolsistas( (float) linha.getAlunos().size() );
			linha.setTotalPrioritarios( (float) linha.carregarMatricula(true).size() );
			linha.setTotalNPrioritarios( linha.getTotalBolsistas() - linha.getTotalPrioritarios() );
			
			total = linha.carregarBolsistasReprovados(linha.getIdTipoBolsa(), ano, periodo, true).size();
			linha.setTotalReprovadosP((float) linha.getTotalPrioritarios() != 0.0 ? (( total * 100 ) / linha.getTotalPrioritarios()) : 0);
			
			total = linha.carregarBolsistasReprovados(linha.getIdTipoBolsa(), ano, periodo, false).size();
			linha.setTotalReprovadosNP((float) linha.getTotalNPrioritarios() != 0.0 ? (( total * 100) / linha.getTotalNPrioritarios()) : 0);
	
			total = linha.carregarBolsistasTrancados(linha.getIdTipoBolsa(), ano, periodo, true).size();
			linha.setTotalTrancadosP((float) linha.getTotalPrioritarios() != 0.0 ? (( total * 100 ) / linha.getTotalPrioritarios()) : 0);
			
			total = linha.carregarBolsistasTrancados(linha.getIdTipoBolsa(), ano, periodo, false).size();
			linha.setTotalTrancadosNP((float) linha.getTotalNPrioritarios() != 0.0 ? (( total * 100 ) / linha.getTotalNPrioritarios()) : 0);
			
			total = linha.carregarBolsistasSemTrancamentoESemReprovacao(linha.getIdTipoBolsa(), ano, periodo, true).size();
			linha.setTotalTrancadosReprovadosP((float) linha.getTotalPrioritarios() != 0.0 ? (( total * 100 ) / linha.getTotalPrioritarios()) : 0);
			
			total = linha.carregarBolsistasSemTrancamentoESemReprovacao(linha.getIdTipoBolsa(), ano, periodo, false).size();
			linha.setTotalTrancadosReprovadosNP((float) linha.getTotalNPrioritarios() != 0.0 ? (( total * 100 ) / linha.getTotalNPrioritarios()) : 0);
			
			linha.setLinha(desempenhoDiscentes.size());
			
			desempenhoDiscentes.add(linha);
		}
	}

	/**
	 * M�todo respons�vel por exibir os discentes de acordo com a linha e coluna selecionda.
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * 	<ul>
	 * 		<li> /SIGAA/app/sigaa.ear/sigaa.war/sae/relatorios/lista_discente_desempenho_academico.jsp </li>
	 * 	</ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public String viewTotalBolsistas() throws HibernateException, DAOException{
		listaDiscentes = new ArrayList<Discente>();  
		int colunaTabela = getParameterInt("idColuna", 0);
		int linhaTabela = getParameterInt("idLinha", 0);
		
		LinhaDesempenhoAcademico linha = new LinhaDesempenhoAcademico();
		linha = desempenhoDiscentes.get(linhaTabela);
		descricaoBolsa = 
			WordUtils.capitalizeFully( desempenhoDiscentes.get(linhaTabela).getBolsa().toLowerCase() ); 
		
		if (colunaTabela == 0)
			listaDiscentes = (List<Discente>) linha.getNumeroTotalBolsistas(ano, periodo);
		if (colunaTabela == 1)
			listaDiscentes = (List<Discente>) linha.getTotalBolsistasPrioritario(ano, periodo, true);
		if (colunaTabela == 2)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasReprovados(linha.getIdTipoBolsa(), ano, periodo, true);
		if (colunaTabela == 3)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasReprovados(linha.getIdTipoBolsa(), ano, periodo, false);
		if (colunaTabela == 4)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasTrancados(linha.getIdTipoBolsa(), ano, periodo, true);
		if (colunaTabela == 5)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasTrancados(linha.getIdTipoBolsa(), ano, periodo, false);
		if (colunaTabela == 6)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasSemTrancamentoESemReprovacao(linha.getIdTipoBolsa(), ano, periodo, true);
		if (colunaTabela == 7)
			listaDiscentes = (List<Discente>) linha.carregarBolsistasSemTrancamentoESemReprovacao(linha.getIdTipoBolsa(), ano, periodo, false);
		
		return forward("/sae/relatorios/view_discentes.jsf");
	}
	
	/**
	 * Valida��o de dados.
	 * <br />
	 * M�todo n�o invocado por JSP�s
	 *   
	 */
	private ListaMensagens validarEntrada() {
		
		ListaMensagens lista = new ListaMensagens();
		
		if (isEmpty(ano) || isEmpty(periodo) ) {
			addMensagemErro("Ano e Per�odo com a bolsa: Campo obrigat�rio n�o preenchido.");
		}
		
		if (isEmpty(anoSem) || isEmpty(periodoSem) ) {
			addMensagemErro("Ano e Per�odo sem a bolsa:  Campo obrigat�rio n�o preenchido.");
		}
		
		if (isEmpty(tipoBolsaAuxilio) ) {
			addMensagemErro("Tipo de Bolsa: Campo obrigat�rio n�o preenchido."); 
		}
		
		if (isEmpty(matricula) ) {
			if (isEmpty(curso) ) {
				addMensagemErro("Curso: Campo obrigat�rio n�o preenchido.");
			}
			
			if ( (isEmpty(nivel)) || (nivel == 0) ) {
				addMensagemErro("N�vel de Ensino: Campo obrigat�rio n�o preenchido.");
			}
		}
		return lista;
	}
	
	/**
	 * Gerar o relat�rio de todos os discentes que solicitaram bolsa e que s�o carentes. 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_discentes_carentes.jsp </ul>
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public String gerarRelatorioDiscentesSolicitaramCarentesAnoPeriodo() throws HibernateException, DAOException {
		if (isEmpty(ano) || isEmpty(periodo)) {
			addMensagemErro("Informe o Ano e Per�odo.");
			return null;
		}
		if (isEmpty(tipoBolsaAuxilio) ) {
			addMensagemErro("Selecione um tipo de Bolsa Aux�lio.");
			return null;
		}
		
		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		tipoBolsaAuxilio = dao.findByPrimaryKey(tipoBolsaAuxilio.getId(), TipoBolsaAuxilio.class); 
		
		listaDiscentes = dao.listarDiscentesSolicitaramBolsaAnoPeriodo(ano, periodo, tipoBolsaAuxilio, curso.getId());
		if (curso.getId() != 0 ){
			CursoDao cDao = getDAO(CursoDao.class);
			curso = cDao.findByPrimaryKey(curso.getId(),Curso.class); 
		}
		else {
			curso = new Curso();
		}
		
		if(listaDiscentes.isEmpty()){
			addMensagemWarning("N�o foi encontrado resultado com os crit�rios selecionados na busca.");
			return null;
		}
		
		return forward("/sae/relatorios/lista_discentes_carentes.jsf");
	}
	
	/**
	 * Exibe Relat�rio que busca alunos no cadastro �nico por centro ou curso
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li>  /sigaa.war/sae/relatorios/form_qtd_alunos_cad_unico.jsp </ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorioAlunosCadastroUnico() throws DAOException {

		if (isEmpty(ano) || isEmpty(periodo)) {
			addMensagemErro("Informe o Ano e Per�odo.");
			return null;
		}
		
		String opcao = getParameter("opcao");
		
		if ("centro".equals(opcao)) {
			unidade = getGenericDAO().findByPrimaryKey(unidade.getId(), Unidade.class);
			curso = new Curso();
		}
		else {
			curso = getGenericDAO().findByPrimaryKey(curso.getId(), Curso.class);
			unidade = new Unidade();
		}
		

		AdesaoCadastroUnicoBolsaDao dao = getDAO(AdesaoCadastroUnicoBolsaDao.class);
		discentesCadastroUnico = dao.findAllDiscentesCadastroUnico(curso, unidade, ano, periodo);
		
		if (isEmpty(discentesCadastroUnico)) {
			addMensagemErro("Nenhum discente localizado com estes crit�rios.");
			return null;
		}
		
		return forward("/sae/relatorios/lista_qtd_alunos_cad_unico.jsp");
	}	
	
	/**
	 * Volta para o menu do SAE
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li>/sigaa.war/sae/relatorios/form_qtd_alunos_cu.jsp
	 * 	<li>/sigaa.war/sae/relatorios/form_relatorio.jsp
	 * 	<li>/sae/relatorios/form_residente_cu.jsp
	 * </ul>
	 */
	@Override
	public String cancelar() {
		resetBean();
		return redirectJSF(getSubSistema().getLink());
	}
	
	/**
	 * Carrega munic�pios de um estado
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 * @param e
	 * @throws DAOException
	 */
	public void carregarMunicipios(ValueChangeEvent e) throws DAOException {

		String selectId = e.getComponent().getId();
		if (selectId != null && e.getNewValue() != null) {
			Integer ufId = (Integer) e.getNewValue();

			if (selectId.toLowerCase().contains("ufend")) {
				carregarMunicipiosEndereco(ufId);
			}
		}
	}
	
	/**
	 * Carrega munic�pios de um estado
	 * <br/>
	 * <ul><li>M�todo n�o invocado por JSP�s </ul>
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarCursos() throws DAOException {
		CursoMBean cursoMBean = getMBean("curso");
		if (isEmpty(nivel)) 
			return;
		allCursos = cursoMBean.getCursoNivelCombo(nivel);
	}

	/**
	 * Carrega munic�pios de um estado
	 * <br/>
	 * <ul><li>M�todo n�o invocado por JSP�s</ul>
	 * @param idUf
	 * @throws DAOException
	 */
	public void carregarMunicipiosEndereco(Integer idUf) throws DAOException {
		
		MunicipioDao dao = getDAO(MunicipioDao.class);
		UnidadeFederativa uf = dao.findByPrimaryKey(idUf, UnidadeFederativa.class);
	
		Collection<Municipio> municipios = dao.findByUF(idUf);
		municipiosEndereco = new ArrayList<SelectItem>(0);
		municipiosEndereco.add(new SelectItem(uf.getCapital().getId(), uf.getCapital().getNome()));
		municipiosEndereco.addAll(toSelectItems(municipios, "id", "nome"));
		
	}
	
	/**
	 *  Exibe as bolsa deferidas e contempladas
	 *  <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 * @throws DAOException 
	 */
	public String gerarRelatorioContempladosDeferidos() throws DAOException {
		
		
		if (isEmpty(ano) || isEmpty(periodo) ) {
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Ano.Per�odo");
		}else{
			ValidatorUtil.validateRange(ano, 2008, CalendarUtils.getAnoAtual(), "Ano", erros);
			ValidatorUtil.validateRange(periodo, 1, 2, "Per�odo", erros);
		}
		
		if( hasErrors() )
			return null;
		
		curso = null;
		if( getAcessoMenu().isCoordenadorCursoGrad() )
			curso = getCursoAtualCoordenacao(); 
				
		Date dataInicio = null;
		Date dataFim = null;
		if (periodo == 1){ 
			dataInicio = CalendarUtils.createDate(01, 0, ano);
			dataFim = CalendarUtils.createDate(30, 05, ano);
		} else {
			dataInicio = CalendarUtils.createDate(01, 06, ano);
			dataFim = CalendarUtils.createDate(31, 11, ano);
		}
		listaBolsistasContemplados = new ArrayList<BolsaAuxilio>();
		listaBolsistasSIPAC = new ArrayList<Bolsista>();
		
		/** Popula os bolisistas contemplados no SIGAA */
		Collection<BolsaAuxilio> bolsistas = getDAO(RelatorioSAEDao.class).
			findContempladosByAnoPeriodo(curso, ano, periodo);
		for (BolsaAuxilio b : bolsistas) {
			listaBolsistasContemplados.add(b);
		}
		
		/** Popula os bolsistas remunerados no SIPAC */
		Collection<Integer> unidades = new ArrayList<Integer>();
		unidades.add( ParametroHelper.getInstance().getParametroInt( ParametrosSAE.UNIDADE_SOLICITACAO_BOLSA_SAE ) );
		
		List<AlunoDTO> alunoDTO = null;
		if(service != null) {
			alunoDTO = service.
				findAllDiscenteBolsistasPorPeriodoUnidadeMatricula(dataInicio, dataFim,	null, null, null);
		}
		
		if( alunoDTO != null ) {
			for (AlunoDTO aDTO : alunoDTO) {
				
				if( curso == null || aDTO.getCurso().getId() == curso.getId() ){
					Bolsista b = new Bolsista();
					
					b.setDiscente( new Discente( aDTO.getId(), Long.valueOf( aDTO.getMatricula() ), aDTO.getNomeAluno() ) );
					b.setDataInicio( aDTO.getInicioBolsa() );
					b.setDataFim( aDTO.getFimBolsa() );
					
					listaBolsistasSIPAC.add( b );
				}
				
			}
		}
		
		if( isEmpty(listaBolsistasSIPAC) && isEmpty(listaBolsistasContemplados) ){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
			
		
		return forward( getDirBase() + "/" + JSP_REL_CONTEMPLADOS_DEFERIDOS);
	}
	
	/**
	 * Exibe as bolsas por escolaridade do pai do List<Object> listaResidentesCadastroUnicodiscente
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 */
	public String gerarRelatorioEscolaridadePai() throws SQLException, JRException, IOException {
		configurarParametrosRelatorio(RELATORIO_ESCOLARIDADE_PAI);
		return null;
	}
	
	/**
	 * Exibe as bolsas por bairro
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 */
	public String gerarRelatorioBairro() throws SQLException, JRException, IOException {
		configurarParametrosRelatorio(RELATORIO_BAIRRO);
		return null;
	}
	
	/**
	 * Exibe os relat�rios exibindo a profiss�o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 */
	public String gerarRelatorioProfissaoPai() throws SQLException, JRException, IOException {
		configurarParametrosRelatorio(RELATORIO_PROFISSAO_PAI);
		return null;
	}
	
	/**
	 * Exibe um resumo das bolsas Alimenta��o e Resid�ncia
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 */
	public String gerarRelatorioAlimentacaoResidencia() throws SQLException, JRException, IOException {
		configurarParametrosRelatorio(RELATORIO_ALIMENTACAO_RESIDENCIA);
		return null;
	}
	
	/**
	 * Exibe todos os alunos com bolsa alimenta��o
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/relatorios/form_relatorio.jsp </ul>
	 */
	public String gerarRelatorioTodosAlunosComBolsa() throws SQLException, JRException, IOException {
		configurarParametrosRelatorio(RELATORIO_TODOS_ALUNOS_COM_BOLSA_ALIMENTACAO);
		return null;
	}
	
	/**
	 * Relat�rio com todos os discentes que possuem bolsa resid�ncia e seu status no cadastro �nico.
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\sae\relatorios\form_residente_cadastro_unico.jsp</li>
	 *   </ul>
	 * @return
	 * @throws SQLException
	 * @throws JRException
	 * @throws IOException
	 */
	public String gerarRelatorioResidentesNoCadastroUnico() throws SQLException, JRException, IOException {
		
		if (isEmpty(ano) || isEmpty(periodo)) {
			addMensagemErro("Informe o Ano e Per�odo.");
			return null;
		}
		
		DiscenteDao dao = getDAO(DiscenteDao.class);
		listaResidentesCadastroUnico = dao.findAllResidentesNoCadastroUnico(ano, periodo);
		
		if ( listaResidentesCadastroUnico == null ||  listaResidentesCadastroUnico.isEmpty() ) {
			addMensagemErro("Nenhum registro localizado com os parametros informados.");
			return null;
		}
		
		return forward("/sae/relatorios/relatorio_residentes_cadastro_unico.jsp");
	}
	
	/**
	 * Configura os par�metros do relat�rio passado como argumento
	 * 
	 * @param tipoRelatorio
	 * @throws SQLException
	 * @throws JRException
	 * @throws IOException
	 */
	private void configurarParametrosRelatorio(int tipoRelatorio) throws SQLException, JRException, IOException {
		
		HashMap<String, Object> parametros = new HashMap<String, Object>();
		
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());
		
		if (tipoRelatorio != 3) {
			parametros.put("ano", ano);
			parametros.put("periodo", periodo);
		}
		if (tipoRelatorio == 3) {
			parametros.put("ano", ano);
			parametros.put("periodo", periodo);
			parametros.put("municipio", idMunicipio);
			parametros.put("uf", idUF);	
		}
		if (tipoRelatorio == 6) {
			parametros.put("centro", idCentro);	
		}
		
		
		if (tipoRelatorio == 0) {
			parametros = null;
		}		
		
		exibirRelatorio(parametros);
	}
	
	/**
	 * Exibe o relat�rio na tela, de acordo com os par�metros populados
	 * anteriormente (de acordo com a entrada do usu�rio)
	 * 
	 */
	private void exibirRelatorio(HashMap<String, Object> parametros) throws SQLException, JRException, IOException {
		
		if (ano > 0 && periodo > 0 && arquivoRelatorio != null && formato != null) {
			Connection con = Database.getInstance().getSigaaConnection();
	        JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReport(BASE_REPORT_PACKAGE, arquivoRelatorio+".jasper"), parametros, con );
	        String nomeArquivo = "relatorio_" + arquivoRelatorio + "." + formato;
	        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
	        FacesContext.getCurrentInstance().responseComplete();	
		}
		else {
			addMensagemWarning("Defina o Ano/Per�odo corretamente para visualizar o relat�rio");
		}
	}
	
	/**
	 * Incia o formul�rio de busca do relat�rio de ocupa��o das resid�ncias
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/app/sae/relatorios/form_ocupacao_residencia.jsp </ul>
	 * @return 
	 * @throws SQLException 
	 * @throws DAOException 
	 */
	public String gerarRelatorioOcupacaoResidencia() throws SQLException, DAOException {
	
		if( isEmpty(ano) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if( isEmpty(periodo) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo");
		if( isEmpty(residencia) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Resid�ncia");
		
		if( hasErrors() )
			return null;
		
		BolsaAuxilioDao buscaBolsaAuxilioDao = getDAO(BolsaAuxilioDao.class);
		listaResidenciasGraduacao = buscaBolsaAuxilioDao.
			relatorioOcupacaoResidencia(residencia.getId(), ano, periodo,
				Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosSAE.RESIDENCIA_GRADUACAO)));
		listaResidenciasPos = buscaBolsaAuxilioDao.
			relatorioOcupacaoResidencia(residencia.getId(), ano, periodo,
				Integer.parseInt(ParametroHelper.getInstance().getParametro(ParametrosSAE.RESIDENCIA_POS)));
		totalOcupantesResidenciaGrad = listaResidenciasGraduacao.size(); 
		totalOcupantesResidenciaPos = listaResidenciasPos.size(); 
		totalOcupantesResidencia = totalOcupantesResidenciaPos + totalOcupantesResidenciaGrad;  
		
		if( totalOcupantesResidencia == 0 ){
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}else
			residencia = getGenericDAO().refresh(residencia);
		
		return forward("/sae/relatorios/relatorio_ocupacao_residencia.jsp");
	}

	/**
	 * 
	 * 
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/app/sae/relatorios/form_movimentacao_discente.jsp </ul>
	 * @return 
	 * @throws SQLException 
	 * @throws DAOException 
	 */
	public String gerarRelatorioMovimentacaoDiscente() throws SQLException, DAOException {
	
		if( isEmpty(ano) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if( isEmpty(periodo) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Per�odo");
		if( isEmpty(relatorioMovimentacaoEscolhido) )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Tipo Relat�rio Escolhido");
		
		if( hasErrors() )
			return null;
		
		MovimentacaoDiscenteDao dao = getDAO(MovimentacaoDiscenteDao.class);
		try {
		
			movimentacaoDiscente = dao.findByMovimentacaoDiscente(ano, periodo, relatorioMovimentacaoEscolhido);
			
		} finally {
			dao.close();
		}
		
		if (movimentacaoDiscente.isEmpty()) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		
		return forward("/sae/relatorios/rel_movimentacao_discente.jsp");
	}

	/**
	 * Cria relat�rio exibindo p�gina em formato
	 * de impress�o com todas as resid�ncias e seus
	 * respectivos alunos (gradua��o e p�s)
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * @return 
	 * @throws SQLException 
	 */
	public String iniciarRelatorioOcupacaoResidencia(){
		
		residencia = new ResidenciaUniversitaria();
		ano  = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		
		return forward("/sae/relatorios/form_ocupacao_residencia.jsp");
	}

	/**
	 * Inicio da gera��o do relat�rio de movimenta��o discente
	 * 
	 * <br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * @return 
	 * @throws SQLException 
	 */
	public String iniciarRelatorioMovimentacaoDiscente(){
		ano  = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		return forward("/sae/relatorios/form_movimentacao_discente.jsp");
	}

	/**
	 * Monta combo com todos os tipos de bolsa
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\sae\busca_bolsa_auxilio.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getAllTiposBolsasCombo() throws DAOException{
		return getAll(TipoBolsaAuxilio.class, "id", "denominacao");
	}
	
	
	/**
	 * Caso o campo matr�cula estiver preechido
	 * os campos N�vel de Ensino e Curso n�o devem ser obrigat�rios.
	 * 
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>sigaa.war\sae\relatorios\form_desempenho.jsp</li>
	 *   </ul>
	 * @return
	 * @throws DAOException
	 */
	public void verificaObrigatoriedade(ValueChangeEvent event){
		Long matricula = (Long) event.getNewValue(); 
		if (isEmpty(matricula))
			obrigatorio = true;
		else 
			obrigatorio = false;
	}
	
	/**
	 * GETS / SETS
	 */
	
	public List<BolsaAuxilio> getListaResidenciasPos() {
		return listaResidenciasPos;
	}

	public List<RegistroAcessoRU> getListaRegistroAcessoRU() {
		return listaRegistroAcessoRU;
	}

	public void setListaRegistroAcessoRU(List<RegistroAcessoRU> listaRegistroAcessoRU) {
		this.listaRegistroAcessoRU = listaRegistroAcessoRU;
	}

	public void setListaResidenciasPos(List<BolsaAuxilio> listaResidenciasPos) {
		this.listaResidenciasPos = listaResidenciasPos;
	}

	public List<BolsaAuxilio> getListaResidenciasGraduacao() {
		return listaResidenciasGraduacao;
	}

	public void setListaResidenciasGraduacao(List<BolsaAuxilio> listaResidenciasGraduacao) {
		this.listaResidenciasGraduacao = listaResidenciasGraduacao;
	}

	public Integer getAno() {
		return ano;
	}
	
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public String getFormato() {
		return formato;
	}

	public void setFormato(String formato) {
		this.formato = formato;
	}

	public boolean isRelatorioContempladosDeferidos() {
		return relatorioContempladosDeferidos;
	}

	public void setRelatorioContempladosDeferidos(boolean relatorioContempladosDeferidos) {
		this.relatorioContempladosDeferidos = relatorioContempladosDeferidos;
	}

	public boolean isRelatorioEscolaridadePai() {
		return relatorioEscolaridadePai;
	}

	public void setRelatorioEscolaridadePai(boolean relatorioEscolaridadePai) {
		this.relatorioEscolaridadePai = relatorioEscolaridadePai;
	}

	public boolean isRelatorioBairro() {
		return relatorioBairro;
	}

	public void setRelatorioBairro(boolean relatorioBairro) {
		this.relatorioBairro = relatorioBairro;
	}

	public Collection<SelectItem> getMunicipiosEndereco() {
		return municipiosEndereco;
	}

	public void setMunicipiosEndereco(Collection<SelectItem> municipiosEndereco) {
		this.municipiosEndereco = municipiosEndereco;
	}

	public int getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(int idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public int getIdUF() {
		return idUF;
	}

	public void setIdUF(int idUF) {
		this.idUF = idUF;
	}

	public boolean isRelatorioProfissao() {
		return relatorioProfissao;
	}

	public void setRelatorioProfissao(boolean relatorioProfissao) {
		this.relatorioProfissao = relatorioProfissao;
	}

	public String getArquivoRelatorio() {
		return arquivoRelatorio;
	}

	public void setArquivoRelatorio(String arquivoRelatorio) {
		this.arquivoRelatorio = arquivoRelatorio;
	}

	public boolean isRelatorioAlimentacaoResidencia() {
		return relatorioAlimentacaoResidencia;
	}

	public void setRelatorioAlimentacaoResidencia(
			boolean relatorioAlimentacaoResidencia) {
		this.relatorioAlimentacaoResidencia = relatorioAlimentacaoResidencia;
	}

	public boolean isRelatorioTodosAlunosAlimentacao() {
		return relatorioTodosAlunosAlimentacao;
	}

	public void setRelatorioTodosAlunosAlimentacao(
			boolean relatorioTodosAlunosAlimentacao) {
		this.relatorioTodosAlunosAlimentacao = relatorioTodosAlunosAlimentacao;
	}

	public int getIdCentro() {
		return idCentro;
	}

	public void setIdCentro(int idCentro) {
		this.idCentro = idCentro;
	}

	public boolean isRelatorioResidentesNoCadastroUnico() {
		return relatorioResidentesNoCadastroUnico;
	}

	public void setRelatorioResidentesNoCadastroUnico(
			boolean relatorioResidentesNoCadastroUnico) {
		this.relatorioResidentesNoCadastroUnico = relatorioResidentesNoCadastroUnico;
	}

	public List<Object> getListaResidentesCadastroUnico() {
		return listaResidentesCadastroUnico;
	}

	public void setListaResidentesCadastroUnico(
			List<Object> listaResidentesCadastroUnico) {
		this.listaResidentesCadastroUnico = listaResidentesCadastroUnico;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isBuscaCurso() {
		return buscaCurso;
	}

	public void setBuscaCurso(boolean buscaCurso) {
		this.buscaCurso = buscaCurso;
	}

	public boolean isBuscaUnidade() {
		return buscaUnidade;
	}

	public void setBuscaUnidade(boolean buscaUnidade) {
		this.buscaUnidade = buscaUnidade;
	}

	public Map<String, List<AdesaoCadastroUnicoBolsa>> getDiscentesCadastroUnico() {
		return discentesCadastroUnico;
	}

	public void setDiscentesCadastroUnico(
			Map<String, List<AdesaoCadastroUnicoBolsa>> discentesCadastroUnico) {
		this.discentesCadastroUnico = discentesCadastroUnico;
	}

	public Collection<SelectItem> getAllTiposBolsas() {
		return allTiposBolsas;
	}

	public void setAllTiposBolsas(Collection<SelectItem> allTiposBolsas) {
		this.allTiposBolsas = allTiposBolsas;
	}

	public TipoBolsaAuxilio getTipoBolsaAuxilio() {
		return tipoBolsaAuxilio;
	}

	public void setTipoBolsaAuxilio(TipoBolsaAuxilio tipoBolsaAuxilio) {
		this.tipoBolsaAuxilio = tipoBolsaAuxilio;
	}

	public List<Discente> getListaDiscentes() {
		return listaDiscentes;
	}

	public void setListaDiscentes(List<Discente> listaDiscentes) {
		this.listaDiscentes = listaDiscentes;
	}

	public Collection<SelectItem> getAllTiposBolsasSIPAC() {
		return allTiposBolsasSIPAC;
	}

	public void setAllTiposBolsasSIPAC(Collection<SelectItem> allTiposBolsasSIPAC) {
		this.allTiposBolsasSIPAC = allTiposBolsasSIPAC;
	}

	public TipoBolsa getTipoBolsaSIPAC() {
		return tipoBolsaSIPAC;
	}

	public void setTipoBolsaSIPAC(TipoBolsa tipoBolsaSIPAC) {
		this.tipoBolsaSIPAC = tipoBolsaSIPAC;
	}

	public boolean isRelatorioBolsasSIPAC() {
		return relatorioBolsasSIPAC;
	}

	public void setRelatorioBolsasSIPAC(boolean relatorioBolsasSIPAC) {
		this.relatorioBolsasSIPAC = relatorioBolsasSIPAC;
	}

	public List<Bolsista> getListaBolsistasSIPAC() {
		return listaBolsistasSIPAC;
	}

	public void setListaBolsistasSIPAC(List<Bolsista> listaBolsistasSIPAC) {
		this.listaBolsistasSIPAC = listaBolsistasSIPAC;
	}

	public Collection<SelectItem> getAllCursos() {
		return allCursos;
	}

	public void setAllCursos(Collection<SelectItem> allCursos) {
		this.allCursos = allCursos;
	}

	public boolean isRelatorioDesempenho() {
		return relatorioDesempenho;
	}

	public void setRelatorioDesempenho(boolean relatorioDesempenho) {
		this.relatorioDesempenho = relatorioDesempenho;
	}

	public Integer getAnoSem() {
		return anoSem;
	}

	public void setAnoSem(Integer anoSem) {
		this.anoSem = anoSem;
	}

	public Integer getPeriodoSem() {
		return periodoSem;
	}

	public void setPeriodoSem(Integer periodoSem) {
		this.periodoSem = periodoSem;
	}

	public void setMatricula(Long matricula) {
		this.matricula = matricula;
	}

	public Long getMatricula() {
		return matricula;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getNome() {
		return nome;
	}

	public List<Map<String, Object>> getListaDesempenhoDiscentes() {
		return listaDesempenhoDiscentes;
	}

	public void setListaDesempenhoDiscentes(
			List<Map<String, Object>> listaDesempenhoDiscentes) {
		this.listaDesempenhoDiscentes = listaDesempenhoDiscentes;
	}

	public String getNivelDescricao() {
		return NivelEnsino.getDescricao(nivel);
	}
	
	public Character getNivel() {
		return nivel;
	}

	public void setNivel(Character nivel) {
		this.nivel = nivel;
	}

	public Integer getTotalOcupantesResidenciaGrad() {
		return totalOcupantesResidenciaGrad;
	}

	public void setTotalOcupantesResidenciaGrad(Integer totalOcupantesResidenciaGrad) {
		this.totalOcupantesResidenciaGrad = totalOcupantesResidenciaGrad;
	}

	public Integer getTotalOcupantesResidenciaPos() {
		return totalOcupantesResidenciaPos;
	}

	public void setTotalOcupantesResidenciaPos(Integer totalOcupantesResidenciaPos) {
		this.totalOcupantesResidenciaPos = totalOcupantesResidenciaPos;
	}

	public void setTotalOcupantesResidencia(Integer totalOcupantesResidencia) {
		this.totalOcupantesResidencia = totalOcupantesResidencia;
	}

	public Integer getTotalOcupantesResidencia() {
		return totalOcupantesResidencia;
	}
	
	public boolean isObrigatorio() {
		return obrigatorio;
	}

	public void setObrigatorio(boolean obrigatorio) {
		this.obrigatorio = obrigatorio;
	}

	public List<LinhaDesempenhoAcademico> getDesempenhoDiscentes() {
		return desempenhoDiscentes;
	}

	public void setDesempenhoDiscentes(
			List<LinhaDesempenhoAcademico> desempenhoDiscentes) {
		this.desempenhoDiscentes = desempenhoDiscentes;
	}

	public String getDescricaoBolsa() {
		return descricaoBolsa;
	}

	public void setDescricaoBolsa(String descricaoBolsa) {
		this.descricaoBolsa = descricaoBolsa;
	}

	public boolean isCheckTipoBolsa() {
		return checkTipoBolsa;
	}

	public void setCheckTipoBolsa(boolean checkTipoBolsa) {
		this.checkTipoBolsa = checkTipoBolsa;
	}

	public Integer getTipoBolsaSelecionada() {
		return tipoBolsaSelecionada;
	}

	public void setTipoBolsaSelecionada(Integer tipoBolsaSelecionada) {
		this.tipoBolsaSelecionada = tipoBolsaSelecionada;
	}

	public ResidenciaUniversitaria getResidencia() {
		return residencia;
	}

	public void setResidencia(ResidenciaUniversitaria residencia) {
		this.residencia = residencia;
	}

	public List<BolsaAuxilio> getListaBolsistasContemplados() {
		return listaBolsistasContemplados;
	}

	/** Retorna os tipos de relat�rio de movimenta��o 
	 * 
 	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li> /SIGAA/app/sigaa.ear/sigaa.war/sae/relatorios/rel_movimentacao_discente.jsp </li>
	 * </ul>
	 */
	public Collection<SelectItem> getTiposRelatorioMovimentacao() {
		Collection<SelectItem> itens = new ArrayList<SelectItem>();
		itens.add( new SelectItem(LinhaMovimentacaoDiscente.TRANCAMENTO_COMPONENTE, "Verificar Trancamento de Componentes Curriculares"));
		itens.add( new SelectItem(LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO, "Verificar Trancamento de Cursos"));
		itens.add( new SelectItem(LinhaMovimentacaoDiscente.CANCELAMENTO_CURSO, "Verificar Cancelamento de Programa"));
		itens.add( new SelectItem(LinhaMovimentacaoDiscente.CONCLUSAO_CURSO, "Verificar Conclus�o de Programa"));
		return itens;
	}

	/** Retorna o nome do relat�rio selecionado
	 *  
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	  <li> /SIGAA/app/sigaa.ear/sigaa.war/sae/relatorios/rel_movimentacao_discente.jsp </li>
	 * </ul>
	 */
	public String getRelatorioMovimentacaoEscolhidoString() {
		String relatorios = "";
		for (String tipoRelatorio : relatorioMovimentacaoEscolhido) {
			if ( Integer.parseInt(tipoRelatorio) == LinhaMovimentacaoDiscente.TRANCAMENTO_COMPONENTE )
				relatorios += "Verificar Trancamento de Componentes Curriculares";
			if ( Integer.parseInt(tipoRelatorio) == LinhaMovimentacaoDiscente.TRANCAMENTO_CURSO )
				relatorios += relatorios.isEmpty() ? "Verificar Trancamento de Cursos" : ", Verificar Trancamento de Cursos";
			if ( Integer.parseInt(tipoRelatorio) == LinhaMovimentacaoDiscente.CANCELAMENTO_CURSO )
				relatorios += relatorios.isEmpty() ? "Verificar Cancelamento de Programa" : ", Verificar Cancelamento de Programa";
			if ( Integer.parseInt(tipoRelatorio) == LinhaMovimentacaoDiscente.CONCLUSAO_CURSO )
				relatorios += relatorios.isEmpty() ? "Verificar Conclus�o de Programa" : ", Verificar Conclus�o de Programa";
		}
		return relatorios;
	}
	
	public void setTiposRelatorioMovimentacao(
			Collection<SelectItem> tiposRelatorioMovimentacao) {
		this.tiposRelatorioMovimentacao = tiposRelatorioMovimentacao;
	}

	public List<String> getRelatorioMovimentacaoEscolhido() {
		return relatorioMovimentacaoEscolhido;
	}

	public void setRelatorioMovimentacaoEscolhido(
			List<String> relatorioMovimentacaoEscolhido) {
		this.relatorioMovimentacaoEscolhido = relatorioMovimentacaoEscolhido;
	}

	public Collection<LinhaMovimentacaoDiscente> getMovimentacaoDiscente() {
		return movimentacaoDiscente;
	}

	public void setMovimentacaoDiscente(
			Collection<LinhaMovimentacaoDiscente> movimentacaoDiscente) {
		this.movimentacaoDiscente = movimentacaoDiscente;
	}

	public List<Map<String, Object>> getDiscentesGraducaoPos() {
		return discentesGraducaoPos;
	}

	public void setDiscentesGraducaoPos(
			List<Map<String, Object>> discentesGraducaoPos) {
		this.discentesGraducaoPos = discentesGraducaoPos;
	}
	
}