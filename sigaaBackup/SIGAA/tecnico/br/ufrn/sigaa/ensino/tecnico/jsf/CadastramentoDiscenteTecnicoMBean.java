/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.InputStream;
import java.sql.Connection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.ValidadorCPFCNPJ;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.tecnico.dao.CadastramentoDiscenteTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dao.ConvocacaoProcessoSeletivoTecnicoDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.CancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.MotivoCancelamentoConvocacaoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoCadastramentoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPreCadastramentoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.negocio.MovimentoCadastramentoDiscenteTecnico;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controlador responsável por gerenciar o cadastramento dos discentes aprovados.
 * 
 * @author Leonardo Campos
 * @author Fred_Castro
 * 
 */
@Component("cadastramentoDiscenteTecnico") @Scope("request")
public class CadastramentoDiscenteTecnicoMBean extends SigaaAbstractController<Object> {

	/** Constante com o endereço da view do formulário. */
	private static final String JSP_FORM = "/tecnico/cadastramento_discente/form_cadastramento.jsp";
	   	/** Mapa dos parâmetros */
    private HashMap<String, Object> parametros = new HashMap<String, Object>();
	/** Ano de referência trabalhado na operação. */
	private ProcessoSeletivoTecnico processoSeletivo;
	/** Discentes retornados pela busca. */
	private List <ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes;
	/** Discentes confirmados para serem cadastrados. */
	private Collection<Discente> cadastrados;
	/** Discentes confirmados para serem cancelados. */
	private Collection<Discente> cancelados;
	/** Cancelamentos gerados para aqueles discentes que não foram confirmados. */
	private Collection<CancelamentoConvocacaoTecnico> cancelamentos;
	/** Indica que a confirmação do cadastramento é para os discentes que foram importados de outros processos seletivos. */
	private boolean discentesImportados;
	/** Lista de discente que foram importados. */
	private Collection<DiscenteTecnico> discentes;
	/** Status padrão para os discente encontrados na busca. */
	private int idStatusTodosDiscentes;
	/** Indica se a operação é de cadastro ou pré-cadastro */
	private boolean preCadastro;
	/** Indica se a operação é de enviar email a um grupo de candidatos. */
	private boolean enviarEmail;
	
	private String motivoIndeferimento;
	
	private String tituloEmail;
	
	private String textoEmail;
	
	private String nome;
	
	private int opcao;
	
	private Long cpf;
	
	private int tipoRelatorio;
	
	private String formatoRelatorio;

	public String getFormatoRelatorio() {
		return formatoRelatorio;
	}

	public void setFormatoRelatorio(String formatoRelatorio) {
		this.formatoRelatorio = formatoRelatorio;
	}

	private int ordenacaoRelatorio;
	
	private int idConvocacao;
	
	private List <Object []> relatorio;
	
	private int status;
	
	private Boolean reservaVagas;
	
	private Pessoa pessoa = new Pessoa();
	
	private boolean consultarIndeferimentos = false;
	
	/** Construtor padrão. */
	public CadastramentoDiscenteTecnicoMBean() {
		clear();
	}
	
	/**
	 * Inicializa as informações utilizadas em todo o caso de uso.
	 */
	private void clear(){
		processoSeletivo = new ProcessoSeletivoTecnico();
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		cadastrados = new ArrayList<Discente>();
		cancelados = new ArrayList<Discente>();
		cancelamentos = new ArrayList<CancelamentoConvocacaoTecnico>();
		discentesImportados = false;
		pessoa = new Pessoa();
		consultarIndeferimentos = false;
		formatoRelatorio = "html";
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		clear();
		status = StatusDiscente.PRE_CADASTRADO;
		setPreCadastro(false);
		setEnviarEmail(false);
		
		return telaFormulario();
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarPreCadastro () throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		clear();
		setPreCadastro(true);
		setEnviarEmail(false);
		status = StatusDiscente.PENDENTE_CADASTRO;
		
		return telaFormulario();
	}
	
	/**
	 * Popula as informações necessárias e inicia o caso de uso. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/menus/administracao.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciarEnvioEmail () throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO,SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		clear();
		
		setPreCadastro(false);
		setEnviarEmail(true);
		
		status = 0;
		
		return telaFormulario();
	}
	
	public String buscarIndeferimentos () throws DAOException, SegurancaException {
		consultarIndeferimentos = true;
		return buscar();
	}
	
	/**
	 * Busca os discentes de acordo com os dados informados. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @throws SegurancaException 
	 */
	public String buscar() throws DAOException, SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		ValidatorUtil.validateRequiredId(processoSeletivo.getId(), "Processo Seletivo", erros);
		
		if(!hasErrors()){
			CadastramentoDiscenteTecnicoDao dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
			processoSeletivo = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoTecnico.class);
			// Adicionar filtros
			convocacoes = (List<ConvocacaoProcessoSeletivoDiscenteTecnico>) dao.findConvocacoesByPSPoloNomeCpfConvocacao(processoSeletivo.getId(), opcao, nome, cpf, status, idConvocacao, consultarIndeferimentos);
			if (ValidatorUtil.isEmpty(convocacoes))
				addMensagemErro("Não foram encontrados discentes de acordo com os parâmetros de busca informados.");
			
			if (consultarIndeferimentos)
				return forward ("/tecnico/cadastramento_discente/form_consultar_indeferimentos.jsp");
		}
		this.idStatusTodosDiscentes = OpcaoPreCadastramentoTecnico.IGNORAR.ordinal();
		
		setOperacaoAtiva(isPreCadastro() ? SigaaListaComando.CONFIRMAR_PRE_CADASTRAMENTO_TECNICO.getId() : isEnviarEmail() ? SigaaListaComando.CONFIRMAR_ENVIO_EMAIL_TECNICO.getId() : SigaaListaComando.CONFIRMAR_CADASTRAMENTO_TECNICO.getId());
		
		return null;
	}
	
	public String relatorioGeralDeClassificacao () {
		return forward("/tecnico/cadastramento_discente/form_relatorio_geral_de_classificacao.jsp");
	}
	
	public String relatorioDeConvocacoes () {
		return forward("/tecnico/cadastramento_discente/form_relatorio_de_convocacoes.jsp");
	}
	
	public String relatorioDeCadastramento () {
		return forward("/tecnico/cadastramento_discente/form_relatorio_de_cadastramento.jsp");
	}
	
	public String relatorioQuantitativoConvocadosCadastrados () {
		return forward("/tecnico/cadastramento_discente/form_relatorio_quantitativo_convocados_cadastrados.jsp");
	}
	
	public String consultarIndeferimentos () {
		return forward("/tecnico/cadastramento_discente/form_consultar_indeferimentos.jsp");
	}
	
	public String consultarIndeferimento () throws DAOException {
		
		
		Integer idCandidato = getParameterInt("idCandidato");
		
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
		
			convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
			convocacoes.add(dao.findConvocacaoDiscenteById(idCandidato));
			
			ConvocacaoProcessoSeletivoDiscenteTecnico c = convocacoes.get(0);
			CancelamentoConvocacaoTecnico cancelamento = c.getCancelamento();
			dao.initialize(cancelamento);
			c.setCancelamento(cancelamento);
			
			ProcessoSeletivoTecnico p = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoTecnico.class);
			c.getInscricaoProcessoSeletivo().setProcessoSeletivo(p);
			
			return forward("/tecnico/cadastramento_discente/consultar_indeferimento.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	/**
	 * Metodo que gera os relatorios Geral De Classificacao do modulo tecnico.
	 * Metodo invocado por
	 *<ul><li>/sigaa.war/tecnico/cadastramento_discente/form_relatorio_geral_de_classificacao.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	
	public String gerarRelatorioGeralDeClassificacao () throws DAOException {
		
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
			if (tipoRelatorio == 0 && formatoRelatorio.equals("html")) {
				relatorio = dao.relatorioGeralDeClassificacaoSintetico (processoSeletivo.getId(), opcao);
				return forward ("/tecnico/cadastramento_discente/relatorio_geral_de_classificacao_sintetico.jsp");
				
			} if (tipoRelatorio == 1 && formatoRelatorio.equals("html")){
				relatorio = dao.relatorioGeralDeClassificacaoAnalitico (processoSeletivo.getId(), opcao, ordenacaoRelatorio);
				return forward ("/tecnico/cadastramento_discente/relatorio_geral_de_classificacao_analitico.jsp");
			}
			
		      if (formatoRelatorio.equals("pdf") || formatoRelatorio.equals("xls")){
		    	OpcaoPoloGrupo opcaoCarregada = dao.findByPrimaryKey(opcao, OpcaoPoloGrupo.class);
				processoSeletivo = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoTecnico.class);
				if(tipoRelatorio == 0)
					gerarRelatorioGeralDeClassificacaoSintetico();
				else if (tipoRelatorio == 1)
					gerarRelatorioGeralDeClassificacaoAnalitico(opcaoCarregada);
				
				return null;
			}
			
		} 		
		finally {
			if (dao != null)
				dao.close();
		}

return null;
		
		
	}
	
/**
 * Metodo que gera o relatorio sintetico em pdf e xls, da operação
 * Metodo nao invocado por jsp
 * @return
 * @throws DAOException
 */
	
	public String gerarRelatorioGeralDeClassificacaoSintetico () throws DAOException {

		if(opcao == 0){
			opcao = -1;
		}

	parametros = new HashMap<String, Object>();
	parametros.put("idProcessoSeletivo", processoSeletivo.getId());
	parametros.put("opcao", opcao);
	parametros.put("processoSeletivo", processoSeletivo.getNome());
	

	// Gerar relatório
    Connection con = null;
    try {
  
        // Preencher relatório
        con = Database.getInstance().getSigaaConnection();
		InputStream relatorio = JasperReportsUtil.getReportSIGAA("relatorio_geral_de_classificacao_sintetico.jasper");
        JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con );

		if (prt.getPages().size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

        // Exportar relatório de acordo com o formato escolhido
        String nomeArquivo = "relatorio_geral_de_classificacao_sintetico."+formatoRelatorio;
        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formatoRelatorio);
        FacesContext.getCurrentInstance().responseComplete();

    } catch (Exception e) {
        e.printStackTrace();
        notifyError(e);
        addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
        return null;
    } finally {
        Database.getInstance().close(con);
    }

	return null;
}


/**
 * Metodo que gera o relatorio analitico em pdf e xls, da operação
 * Metodo nao invocado por jsp
 * @return
 * @throws DAOException
 */
	public String gerarRelatorioGeralDeClassificacaoAnalitico (OpcaoPoloGrupo opcaoCarregada) throws DAOException {
	String ordem = "";
	String qualPolo = "";
	
	//transforma opção em -1 caso for escolhido opção "todos" na jsp
		if(opcao == 0){
			opcao = -1;
		}
	
	//carrega qual polo foi escolhido, ou se foi "todos" para mostrar no relatorio
	if(opcao == -1){
		qualPolo = "TODOS";
	}else{
		qualPolo = opcaoCarregada.getDescricao();
	}
	
	// auxilia sql do ireport para saber ordenação do relatorio
	switch (ordenacaoRelatorio){
		case 0: ordem += " c.classificacao_aprovado"; break;
		case 1: ordem += " p.nome"; break;
	}
	
	parametros = new HashMap<String, Object>();
	parametros.put("idProcessoSeletivo", processoSeletivo.getId());
	parametros.put("opcao", opcao);
	parametros.put("processoSeletivo", processoSeletivo.getNome());
	parametros.put("ordem", ordem);
	parametros.put("qualPolo", qualPolo);
	
	// Gerar relatório
    Connection con = null;
    try {
  
        // Preencher relatório
        con = Database.getInstance().getSigaaConnection();
		InputStream relatorio = JasperReportsUtil.getReportSIGAA("relatorio_geral_de_classificacao_analitico.jasper");
        JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con);

		if (prt.getPages().size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

        // Exportar relatório de acordo com o formato escolhido
        String nomeArquivo = "relatorio_geral_de_classificacao_analitico."+formatoRelatorio;
        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formatoRelatorio);
        FacesContext.getCurrentInstance().responseComplete();

    } catch (Exception e) {
        e.printStackTrace();
        notifyError(e);
        addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
        return null;
    } finally {
        Database.getInstance().close(con);
    }

	return null;
}

/**
 * Metodo que gera os Relatório de Convocações de Discentes do modulo tecnico.
 * Metodo invocado por
 *<ul><li>/sigaa.war/tecnico/cadastramento_discente/form_relatorio_de_convocacao.jsp</li></ul>
 * @return
 * @throws DAOException
 */
	public String gerarRelatorioDeConvocacao () throws DAOException {
		
		if (processoSeletivo.getId() == 0){
			addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Processo Seletivo");
			return null;
		}
		
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
			
			//chama o relatorio em html
			if (formatoRelatorio.equals("html")) {
				relatorio = dao.relatorioDeConvocacao (processoSeletivo.getId(), idConvocacao, opcao);
				return forward ("/tecnico/cadastramento_discente/relatorio_de_convocacao.jsp");			
			}
			
			//chama metodo para gerar pdf ou xls
		    if (formatoRelatorio.equals("pdf") || formatoRelatorio.equals("xls")){
				OpcaoPoloGrupo opcaoCarregada = dao.findByPrimaryKey(opcao, OpcaoPoloGrupo.class);
				ConvocacaoProcessoSeletivoTecnico convocacaoCarregada = dao.findByPrimaryKey(idConvocacao, ConvocacaoProcessoSeletivoTecnico.class);
				processoSeletivo = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoTecnico.class);
				gerarRelatorioConvocacao(opcaoCarregada , convocacaoCarregada);
			}
					
	} finally {	
			if (dao != null)
				dao.close();
		}
		 return null;
}
	
	/**
	 * Metodo que gera o relatorio de convocação do ensino tecnico em pdf e xls, da operação
	 * Metodo nao invocado por jsp
	 * @return
	 * @throws DAOException
	 */
	
	public String gerarRelatorioConvocacao(OpcaoPoloGrupo opcaoCarregada , ConvocacaoProcessoSeletivoTecnico convocacaoCarregada) throws DAOException {

		String qualPolo = "";
		String qualConv = "";
		String sqlConv = "";
		String sqlOp = "";
		
		if (idConvocacao > 0){
			sqlConv = " and conv.id_convocacao_processo_seletivo_tecnico = "+idConvocacao+" ";
			
			}
		
		if (opcao > 0)
			sqlOp += " and i.id_opcao = "+opcao+" ";
		
	//transforma opção em -1 caso for escolhido opção "todos" na jsp
		if(opcao == 0){
			opcao = -1;
		}
	//carrega qual polo foi escolhido, ou se foi "todos" para mostrar no relatorio
	if(opcao == -1){
		qualPolo = "TODOS";
	}else{
		qualPolo = opcaoCarregada.getDescricao();
	}
	
	//transforma opção em -1 caso for escolhido opção "todos" na jsp
	if(idConvocacao == 0){
		idConvocacao = -1;
			}
	
	if(idConvocacao == -1){
		qualConv = "TODAS";
	}else{
		qualConv = convocacaoCarregada.getDescricao();
	}

	parametros = new HashMap<String, Object>();
	parametros.put("idProcessoSeletivo", processoSeletivo.getId());
	parametros.put("opcao", opcao);
	parametros.put("idConvocacao" , idConvocacao);
	parametros.put("processoSeletivo", processoSeletivo.getNome());
	parametros.put("qualPolo", qualPolo);
	parametros.put("qualConv", qualConv);
	parametros.put("sqlConv", sqlConv);
	parametros.put("sqlOp", sqlOp);
	
	// Gerar relatório
    Connection con = null;
    try {
  
        // Preencher relatório
        con = Database.getInstance().getSigaaConnection();
		InputStream relatorio = JasperReportsUtil.getReportSIGAA("relatorio_de_convocacao.jasper");
        JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con );

		if (prt.getPages().size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

        // Exportar relatório de acordo com o formato escolhido
        String nomeArquivo = "relatorio_de_convocacao."+formatoRelatorio;
        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formatoRelatorio);
        FacesContext.getCurrentInstance().responseComplete();

    } catch (Exception e) {
        e.printStackTrace();
        notifyError(e);
        addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
        return null;
    } finally {
        Database.getInstance().close(con);
    }

	return null;
}
	
	/**
	 * Metodo que gera os Relatório de Convocações de Discentes do modulo tecnico.
	 * Metodo invocado por
	 *<ul><li>/sigaa.war/tecnico/cadastramento_discente/form_relatorio_de_cadastramento.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	
	public String gerarRelatorioQuantitativoConvocadosCadastrados () throws DAOException {
		
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
				relatorio = dao.relatorioDeConvocacao (processoSeletivo.getId(), idConvocacao, opcao);
				return forward ("/tecnico/cadastramento_discente/relatorio_quantitativo_convocados_cadastrados.jsp");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	
	/**
	 * Metodo que gera o Relatório de Cadastramento do modulo tecnico.
	 * Metodo invocado por
	 *<ul><li>/sigaa.war/tecnico/cadastramento_discente/form_relatorio_de_cadastramento.jsp</li></ul>
	 * @return
	 * @throws DAOException
	 */
	
	public String gerarRelatorioDeCadastramento () throws DAOException {
	
		if (processoSeletivo.getId() == 0){
			addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Processo Seletivo");
			return null;
		}
		
	CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
			
			//chama o relatorio em html
			if (formatoRelatorio.equals("html")) {
				relatorio = dao.relatorioDeCadastramento (processoSeletivo.getId(), opcao, status, reservaVagas);
				return forward ("/tecnico/cadastramento_discente/relatorio_de_cadastramento.jsp");	
			}
			
			//chama metodo para gerar pdf ou xls
		    if (formatoRelatorio.equals("pdf") || formatoRelatorio.equals("xls")){
				OpcaoPoloGrupo opcaoCarregada = dao.findByPrimaryKey(opcao, OpcaoPoloGrupo.class);
				ConvocacaoProcessoSeletivoTecnico convocacaoCarregada = dao.findByPrimaryKey(idConvocacao, ConvocacaoProcessoSeletivoTecnico.class);
				processoSeletivo = dao.findByPrimaryKey(processoSeletivo.getId(), ProcessoSeletivoTecnico.class);
				gerarRelatorioCadastramentoTecnico(opcaoCarregada , convocacaoCarregada);
			}
					
	} finally {	
			if (dao != null)
				dao.close();
		}
		 return null;
	}
	
	public int getTotalGeralRelatorioCadastramento(){
		
		return relatorio.size();
	}
	
	/**
	 * Metodo que gera o relatorio de convocação do ensino tecnico em pdf e xls, da operação
	 * Metodo nao invocado por jsp
	 * @return
	 * @throws DAOException
	 */
	
	public String gerarRelatorioCadastramentoTecnico(OpcaoPoloGrupo opcaoCarregada , ConvocacaoProcessoSeletivoTecnico convocacaoCarregada ) 
			throws DAOException {

		String qualPolo = "";
		String qualStatus = "";
		String qualReservaDeVaga= "";
		String qualConv = "";
		String sqlStatus = "";
		String sqlOp = "";
		String sqlReservaVagas = "";
		
		if (status > 0){
			sqlStatus = " and i.id_opcao ="+status+" ";
			}

		if (opcao > 0)
			sqlOp = " and i.id_opcao = "+opcao+" ";
		
		if (reservaVagas != null)
			sqlReservaVagas = " and i.reserva_vagas = "+(reservaVagas  ? "true" : "false")+" ";
		
	//transforma opção em -1 caso for escolhido opção "todos" na jsp
		if(opcao == 0){
			opcao = -1;
		}
	//carrega qual polo foi escolhido, ou se foi "todos" para mostrar no relatorio
	if(opcao == -1){
		qualPolo = "TODOS";
	}else{
		qualPolo = opcaoCarregada.getDescricao();
	}
	
	//transforma opção em -1 caso for escolhido opção "todos" na jsp
	if(status == 0){
		status = -1;
			}
	
	if(status == -1){
		qualStatus = "TODAS";
	}else{
		qualStatus = StatusDiscente.getDescricao(status);
	}

	if(reservaVagas == null){
		qualReservaDeVaga = "AMBAS";
		}
	 if(reservaVagas == Boolean.TRUE){
			qualReservaDeVaga = "Sim";
		}
	 if(reservaVagas == Boolean.FALSE){
			qualReservaDeVaga = "Não";
		}
	parametros = new HashMap<String, Object>();
	//parametros para consulta sql e montagem do relatorio no jasper
	parametros.put("idProcessoSeletivo", processoSeletivo.getId());
	parametros.put("opcao", opcao);
	parametros.put("idConvocacao" , idConvocacao);
	parametros.put("processoSeletivo", processoSeletivo.getNome());
	parametros.put("status", status);
	//parametros q passam hsql
	parametros.put("sqlOp", sqlOp);
	parametros.put("sqlReservaVagas", sqlReservaVagas);
	parametros.put("sqlStatus", sqlStatus);
	//parametros que passam o valor do combo
	parametros.put("qualStatus", qualStatus);
	parametros.put("qualReservaDeVaga", qualReservaDeVaga);
	parametros.put("qualConv", qualConv);
	parametros.put("qualPolo", qualPolo);
	
	// Gerar relatório
    Connection con = null;
    try {
  
        // Preencher relatório
        con = Database.getInstance().getSigaaConnection();
		InputStream relatorio = JasperReportsUtil.getReportSIGAA("relatorio_cadastramento_tecnico.jasper");
        JasperPrint prt = JasperFillManager.fillReport(relatorio, parametros, con );

		if (prt.getPages().size() == 0) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}

        // Exportar relatório de acordo com o formato escolhido
        String nomeArquivo = "relatorio_cadastramento_tecnico."+formatoRelatorio;
        JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formatoRelatorio);
        FacesContext.getCurrentInstance().responseComplete();

    } catch (Exception e) {
        e.printStackTrace();
        notifyError(e);
        addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor,contacte o suporte através do \"Abrir Chamado\"");
        return null;
    } finally {
        Database.getInstance().close(con);
    }

	return null;
}
	
	/**
	 * Processa as informações fornecidas encaminhando para a tela de confirmação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String preProcessar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_PRE_CADASTRAMENTO_TECNICO.getId()))
			return null;
		if(ValidatorUtil.isEmpty(convocacoes)){
			addMensagemErro("Não há discentes a processar.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CONFIRMAR_PRE_CADASTRAMENTO_TECNICO);
		
		cadastrados.clear();
		cancelamentos.clear();
		cancelados.clear();
		
		CadastramentoDiscenteTecnicoDao dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
		
		Map<Integer, MovimentacaoAluno> mapaMovimentacoesCancelamento = dao.findMapaMovimentacoesCancelamento(convocacoes);
		
		for (ConvocacaoProcessoSeletivoDiscenteTecnico c: convocacoes) {
			
			if  (c.getCancelamento() != null) {
				continue;
			}
			
			Discente d = c.getDiscente().getDiscente();
			DiscenteTecnico dt = c.getDiscente();
			ConvocacaoProcessoSeletivoDiscenteTecnico convocacao = copiaConvocacao(c);
			Discente discente = convocacao.getDiscente().getDiscente();
			
			if(OpcaoPreCadastramentoTecnico.PRESENTE.verificar(dt) && d.isPendenteCadastro()){
				// verifica se possui convocação anterior não cancelada associada à um discente ativo
				Integer statusAtivos[] = StatusDiscente.getStatusComVinculo().toArray(new Integer[StatusDiscente.getStatusComVinculo().size()]);
				int statusAnterior = dt.getStatus();
				dt.setStatus(StatusDiscente.PRE_CADASTRADO);
				Collection<DiscenteTecnico> discentes = dao.findByPessoaSituacao( dt.getPessoa().getId(), statusAtivos);
				dt.setStatus(statusAnterior);
				if( !isEmpty(discentes) )
					addMensagemWarning("O discente " + discente.getPessoa().getNome() + " possui outro vínculo ativo associado." );

				// Compareceu ao cadastramento
				discente.setStatus(StatusDiscente.PRE_CADASTRADO);
				cadastrados.add(discente);
			} else if (OpcaoPreCadastramentoTecnico.PRESENTE.verificar(dt) && d.isCancelado()) {
				// Aluno cancelado deve ser desmarcado
				addMensagemErro("Aluno com status CANCELADO não pode ter seu cadastramento confirmado. " +
						"Desmarque o aluno para que a convocação do mesmo seja cancelada.");
				return null;
			} else if (OpcaoPreCadastramentoTecnico.AUSENTE.verificar(dt) && d.isPendenteCadastro()) {
				// Não compareceu ao cadastramento
				discente.setStatus(StatusDiscente.EXCLUIDO);
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacaoTecnico.NAO_COMPARECIMENTO_CADASTRO, mapaMovimentacoesCancelamento);
				cancelados.add(discente);
			} else if (OpcaoPreCadastramentoTecnico.AUSENTE.verificar(dt) && d.isCancelado())
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacaoTecnico.REGRA_REGULAMENTO, mapaMovimentacoesCancelamento);
		}
		
		return telaResumo();
	}
	
	/**
	 * Processa as informações fornecidas encaminhando para a tela de confirmação. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException
	 */
	public String processarEnvioEmail () throws ArqException {
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO,SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		if(ValidatorUtil.isEmpty(convocacoes)){
			addMensagemErro("Não há candidatos a processar.");
			return null;
		}
		
		cadastrados.clear();
		cancelamentos.clear();
		cancelados.clear();
		
		for (ConvocacaoProcessoSeletivoDiscenteTecnico c: convocacoes)
			if (c.isSelecionado())
				cadastrados.add(c.getDiscente().getDiscente());
		
		prepareMovimento(SigaaListaComando.CONFIRMAR_ENVIO_EMAIL_TECNICO);
				
		return forward("/tecnico/cadastramento_discente/confirma_envio_email.jsp");
	}
	
	public String processar() throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO_TECNICO.getId()))
			return null;
		if(ValidatorUtil.isEmpty(convocacoes)){
			addMensagemErro("Não há discentes a processar.");
			return null;
		}
		
		prepareMovimento(SigaaListaComando.CONFIRMAR_CADASTRAMENTO_TECNICO);
		
		cadastrados.clear();
		cancelamentos.clear();
		cancelados.clear();
		
		CadastramentoDiscenteTecnicoDao dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
		
		Map<Integer, MovimentacaoAluno> mapaMovimentacoesCancelamento = dao.findMapaMovimentacoesCancelamento(convocacoes);
		
		for (ConvocacaoProcessoSeletivoDiscenteTecnico c: convocacoes) {
			
			if  (c.getCancelamento() != null)
				continue;
			
			DiscenteTecnico d = c.getDiscente();
			DiscenteTecnico dt = c.getDiscente();
			ConvocacaoProcessoSeletivoDiscenteTecnico convocacao = copiaConvocacao(c);
			Discente discente = convocacao.getDiscente().getDiscente();
			
			if(OpcaoCadastramentoTecnico.DEFERIR.verificar(dt) && d.isPreCadastrado()){
				// verifica se possui convocação anterior não cancelada associada à um discente ativo
				Integer statusAtivos[] = StatusDiscente.getStatusComVinculo().toArray(new Integer[StatusDiscente.getStatusComVinculo().size()]);
				int statusAnterior = dt.getStatus();
				dt.setStatus(StatusDiscente.CADASTRADO);
				Collection<DiscenteTecnico> discentes = dao.findByPessoaSituacao( dt.getPessoa().getId(), statusAtivos);
				dt.setStatus(statusAnterior);
				if( !isEmpty(discentes) )
					addMensagemWarning("O discente " + discente.getPessoa().getNome() + " possui outro vínculo ativo associado." );

				// Compareceu ao cadastramento
				discente.setStatus(StatusDiscente.CADASTRADO);
				cadastrados.add(discente);
			} else if (OpcaoCadastramentoTecnico.DEFERIR.verificar(dt) && d.getDiscente().isCancelado()) {
				// Aluno cancelado deve ser desmarcado
				addMensagemErro("Aluno com status CANCELADO não pode ter seu cadastramento confirmado. " +
						"Desmarque o aluno para que a convocação do mesmo seja cancelada.");
				return null;
			} else if (OpcaoCadastramentoTecnico.INDEFERIR.verificar(dt) && d.isPreCadastrado()) {
				// Não cumpriu com alguma regra
				discente.setStatus(StatusDiscente.EXCLUIDO);
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacaoTecnico.REGRA_REGULAMENTO, mapaMovimentacoesCancelamento);
				cancelados.add(discente);
			} else if (OpcaoCadastramentoTecnico.INDEFERIR.verificar(dt) && d.getDiscente().isCancelado())
				adicionarCancelamento(convocacao, MotivoCancelamentoConvocacaoTecnico.REGRA_REGULAMENTO, mapaMovimentacoesCancelamento);
		}
		
		return telaResumo();
	}
	
	/**
	 * Acrescenta um cancelamento de convocação na coleção de cancelamentos.
	 * @param mapaInscricoes
	 * @param discente
	 * @throws SegurancaException 
	 */
	private void adicionarCancelamento(ConvocacaoProcessoSeletivoDiscenteTecnico convocacao, MotivoCancelamentoConvocacaoTecnico motivo, Map<Integer, MovimentacaoAluno> mapaMovimentacoesCancelamento) throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		CancelamentoConvocacaoTecnico c = new CancelamentoConvocacaoTecnico();
		c.setConvocacao(convocacao);
		c.setMotivo(motivo);
		c.setMovimentacaoCancelamento(mapaMovimentacoesCancelamento.get(convocacao.getDiscente().getId()));
		cancelamentos.add(c);
	}
	
	/**
	 * Retorna uma nova instância de Discente com uma cópia das informações utilizadas no caso de uso. 
	 * @param d
	 * @return
	 * @throws SegurancaException 
	 */
	private ConvocacaoProcessoSeletivoDiscenteTecnico copiaConvocacao(ConvocacaoProcessoSeletivoDiscenteTecnico c) throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		ConvocacaoProcessoSeletivoDiscenteTecnico copia = new ConvocacaoProcessoSeletivoDiscenteTecnico(c.getId());
		
		DiscenteTecnico d = new DiscenteTecnico();
		d.setId(c.getDiscente().getId());
		d.getDiscente().setId(c.getDiscente().getId());
		d.setMatricula(c.getDiscente().getMatricula());
		d.getDiscente().getPessoa().setNome(c.getDiscente().getPessoa().getNome());
		d.getDiscente().getPessoa().setEmail(c.getDiscente().getPessoa().getEmail());
		d.getDiscente().getPessoa().setDataNascimento(c.getDiscente().getPessoa().getDataNascimento());
		d.getDiscente().getPessoa().setRegistroGeral(c.getDiscente().getPessoa().getRegistroGeral());
		d.getDiscente().getPessoa().setCpf_cnpj(c.getDiscente().getPessoa().getCpf_cnpj());
		d.setStatus(c.getDiscente().getStatus());
		d.setAnoIngresso(c.getDiscente().getAnoIngresso());
		d.setPeriodoIngresso(c.getDiscente().getPeriodoIngresso());
		
		ConvocacaoProcessoSeletivoTecnico cd = new ConvocacaoProcessoSeletivoTecnico();
		cd.setDescricao(c.getConvocacaoProcessoSeletivo().getDescricao());
		
		copia.setDiscente(d);
		copia.setConvocacaoProcessoSeletivo(cd);
		copia.setCancelamento(c.getCancelamento());
		
		return copia;
	}

	/**
	 * Invoca o processador para persistir as informações do cadastramento. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/resumo_cadastramento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmarPre () throws ArqException {
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_PRE_CADASTRAMENTO_TECNICO.getId()))
			return null;
		MovimentoCadastramentoDiscenteTecnico mov = new MovimentoCadastramentoDiscenteTecnico();
		mov.setCodMovimento(getUltimoComando());
		mov.setCadastrados(cadastrados);
		mov.setCancelados(cancelados);
		mov.setCancelamentos(cancelamentos);
		mov.setDiscentesImportados(discentesImportados);
		
		try {
			execute(mov);
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		addMensagemInformation("Cadastramento realizado com sucesso! <br/>" +
				"Foram realizadas "+ cadastrados.size() + " confirmações e "+ cancelamentos.size() +" cancelamentos de convocação.");
		
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		discentes = new ArrayList<DiscenteTecnico>();
		cancelados = new ArrayList<Discente>();
		buscar();
		return telaFormulario();
	}
	
	/**
	 * Invoca o processador para persistir as informações do cadastramento. <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/resumo_cadastramento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String confirmarEnvioEmail () throws ArqException {
		
		checkRole(SigaaPapeis.GESTOR_IMPORTACAO_PROCESSO_SELETIVO_TECNICO, SigaaPapeis.GESTOR_CONVOCACAO_PROCESSO_SELETIVO_TECNICO,SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_ENVIO_EMAIL_TECNICO.getId()))
			return null;
		
		if (StringUtils.isEmpty(tituloEmail))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Assunto do Email");
		
		if (StringUtils.isEmpty(textoEmail))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Texto do Email");
		
		if (hasErrors())
			return null;
		
		// Envia os emails para os candidatos selecionados que possuem email cadastrado.
		for (Discente d : cadastrados){
			if (!StringUtils.isEmpty(d.getPessoa().getEmail())){
				MailBody body = new MailBody();
				body.setAssunto(tituloEmail);
				body.setMensagem(textoEmail);
				body.setFromName("SIGAA - IMD");
				body.setContentType(MailBody.HTML);
				body.setEmail(d.getPessoa().getEmail());
				Mail.send(body);
			}
		}
		
		tituloEmail = "";
		textoEmail = "";
		
		removeOperacaoAtiva();
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
		
		return forward("/ensino/menus/menu_tecnico.jsp");
	}
	
	public String confirmar () throws ArqException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		if (!checkOperacaoAtiva(SigaaListaComando.CONFIRMAR_CADASTRAMENTO_TECNICO.getId()))
			return null;
		
		if (!cancelados.isEmpty() && StringUtils.isEmpty(motivoIndeferimento)){
			addMensagemErro("Informe o motivo para o indeferimento dos candidatos.");
			return null;
		}
		
		for (CancelamentoConvocacaoTecnico c : cancelamentos)
			c.setObservacoes(motivoIndeferimento);
		
		MovimentoCadastramentoDiscenteTecnico mov = new MovimentoCadastramentoDiscenteTecnico();
		mov.setCodMovimento(getUltimoComando());
		mov.setCadastrados(cadastrados);
		mov.setCancelados(cancelados);
		mov.setCancelamentos(cancelamentos);
		mov.setDiscentesImportados(discentesImportados);
		
		try {
			execute(mov);
			
			if (!cadastrados.isEmpty())
				for (Discente d : cadastrados){
					Pessoa p = d.getPessoa();
					notificarCadastrado(p.getEmail(), p.getNome(), d.getMatricula(), p.getRegistroGeral(), p.getCpf_cnpj(), p.getDataNascimento());
				}
			
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		}
		
		addMensagemInformation("Cadastramento realizado com sucesso! <br/>" +
				"Foram realizadas "+ cadastrados.size() + " confirmações e "+ cancelamentos.size() +" cancelamentos de convocação.");
		addMensagemInformation("Os discentes que informaram o email no processo seletivo foram notificados sobre o cadastramento e receberam seu número de matrícula.");
		
		convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscenteTecnico>();
		discentes = new ArrayList<DiscenteTecnico>();
		cancelados = new ArrayList<Discente>();
		buscar();
		return telaFormulario();
	}
	
	/**
	 * Envia o email de confirmação de cadastramento para o candidato.
	 * 
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String enviarConfirmacaoDeCadastroADiscente () throws DAOException, SegurancaException {
		Integer idCandidato = getParameterInt("idCandidato");
		
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
		
			ConvocacaoProcessoSeletivoDiscenteTecnico conv = dao.findConvocacaoDiscenteById(idCandidato);
		
			DiscenteTecnico d = conv.getDiscente();
			Pessoa p = d.getPessoa();
			
			notificarCadastrado(p.getEmail(), p.getNome(), d.getMatricula(), p.getRegistroGeral(), p.getCpf_cnpj(), p.getDataNascimento());
			addMensagem(MensagensArquitetura.OPERACAO_SUCESSO);
			buscar();
			return telaFormulario();
		} finally {
			if (dao != null)
				dao.close();
		}
		
	}
	
	protected void notificarCadastrado (String email, String nome, Long matricula, String rg, Long cpf, Date nascimento) {
		if (!StringUtils.isEmpty(email)) {
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			
			String texto = ParametroHelper.getInstance().getParametro(ParametrosTecnico.TEXTO_EMAIL_CONFIRMACAO_CADASTRO);
			texto = texto.replace("[NOME_ALUNO]", nome);
			texto = texto.replace("[MATRICULA_ALUNO]", ""+matricula);
			texto = texto.replace("[RG]", ""+rg);
			texto = texto.replace("[CPF]", ""+Formatador.getInstance().formatarCPF_CNPJ(cpf));
			texto = texto.replace("[DATA_NASCIMENTO]", ""+ (nascimento != null ? sdf.format(nascimento) : ""));
			texto = texto.replace("[EMAIL]", email);
			
			MailBody body = new MailBody();
			body.setAssunto(ParametroHelper.getInstance().getParametro(ParametrosTecnico.ASSUNTO_EMAIL_CONFIRMACAO_CADASTRO));
			body.setMensagem((texto));
			body.setFromName("SIGAA - IMD");
			body.setContentType(MailBody.HTML);
			body.setEmail(email);
			
			Mail.send(body);
		}
	}
	
	public List <Object []> autocompleteNomeDiscente (Object event) throws DAOException {
		String nome = event.toString();
		CadastramentoDiscenteTecnicoDao dao = null;
		
		try {
			dao = getDAO(CadastramentoDiscenteTecnicoDao.class);
			return dao.findCandidatosByNome(nome);
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Encaminha para a tela do formulário.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/resumo_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaFormulario() {
		return forward(JSP_FORM);
	}
	
	/**
	 * Encaminha para a tela de resumo.
	 *  <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/graduacao/cadastramento_discente/form_cadastramento.jsp</li>
	 * </ul>
	 * @return
	 */
	public String telaResumo() {
		return forward("/tecnico/cadastramento_discente/confirma_cadastramento.jsp");
	}

	public Collection<Discente> getCadastrados() {
		return cadastrados;
	}

	public void setCadastrados(Collection<Discente> cadastrados) {
		this.cadastrados = cadastrados;
	}
	public Collection<CancelamentoConvocacaoTecnico> getCancelamentos() {

		return cancelamentos;
	}

	public void setCancelamentos(Collection<CancelamentoConvocacaoTecnico> cancelamentos) {
		this.cancelamentos = cancelamentos;
	}

	public ProcessoSeletivoTecnico getProcessoSeletivo() {
		return processoSeletivo;
	}

	public void setProcessoSeletivo(ProcessoSeletivoTecnico processoSeletivo) {
		this.processoSeletivo = processoSeletivo;
	}

	public Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> getConvocacoes() {
		return convocacoes;
	}

	public void setConvocacoes(Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> convocacoes) {
		this.convocacoes = (List<ConvocacaoProcessoSeletivoDiscenteTecnico>) convocacoes;
	}

	/**
	 * Retorna uma lista de opções de cadastramento para ser utilizada na view.
	 * @return
	 */
	public List<SelectItem> getOpcoesPreCadastramentoCombo(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for(OpcaoPreCadastramentoTecnico op: OpcaoPreCadastramentoTecnico.values()){
			SelectItem it = new SelectItem();
			it.setLabel(op.getLabel());
			it.setValue(op.ordinal());
			itens.add(it);
		}
		return itens;
	}
	
	public List<SelectItem> getOpcoesCadastramentoCombo(){
		List<SelectItem> itens = new ArrayList<SelectItem>();
		for(OpcaoCadastramentoTecnico op: OpcaoCadastramentoTecnico.values()){
			SelectItem it = new SelectItem();
			it.setLabel(op.getLabel());
			it.setValue(op.ordinal());
			itens.add(it);
		}
		return itens;
	}
	
	public List<SelectItem> getConvocacoesCombo () throws DAOException {
		ConvocacaoProcessoSeletivoTecnicoDao dao = null;
		
		try {
			dao = getDAO(ConvocacaoProcessoSeletivoTecnicoDao.class);
			return toSelectItems(dao.findAll(ConvocacaoProcessoSeletivoTecnico.class), "id", "descricao");
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	public List<SelectItem> getStatusDiscenteCombo () throws DAOException {
		List <SelectItem> rs = new ArrayList<SelectItem>();
		
		rs.add(new SelectItem (StatusDiscente.PENDENTE_CADASTRO, StatusDiscente.getDescricao(StatusDiscente.PENDENTE_CADASTRO)));
		rs.add(new SelectItem (StatusDiscente.PRE_CADASTRADO, StatusDiscente.getDescricao(StatusDiscente.PRE_CADASTRADO)));
		rs.add(new SelectItem (StatusDiscente.CADASTRADO, StatusDiscente.getDescricao(StatusDiscente.CADASTRADO)));
		rs.add(new SelectItem (StatusDiscente.CANCELADO, StatusDiscente.getDescricao(StatusDiscente.CANCELADO)));
		rs.add(new SelectItem (StatusDiscente.EXCLUIDO, StatusDiscente.getDescricao(StatusDiscente.EXCLUIDO)));
		
		return rs;
	}

	public boolean isDiscentesImportados() {
		return discentesImportados;
	}

	public void setDiscentesImportados(boolean discentesImportados) {
		this.discentesImportados = discentesImportados;
	}

	public Collection<DiscenteTecnico> getDiscentes() {
		return discentes;
	}

	public Collection<Discente> getCancelados() {
		return cancelados;
	}

	public int getIdStatusTodosDiscentes() {
		return idStatusTodosDiscentes;
	}

	public void setIdStatusTodosDiscentes(int idStatusTodosDiscentes) {
		this.idStatusTodosDiscentes = idStatusTodosDiscentes;
	}

	public boolean isPreCadastro() {
		return preCadastro;
	}

	public void setPreCadastro(boolean preCadastro) {
		this.preCadastro = preCadastro;
	}
	
	public boolean isEnviarEmail() {
		return enviarEmail;
	}

	public void setEnviarEmail(boolean enviarEmail) {
		this.enviarEmail = enviarEmail;
	}

	public String getMotivoIndeferimento() {
		return motivoIndeferimento;
	}

	public void setMotivoIndeferimento(String motivoIndeferimento) {
		this.motivoIndeferimento = motivoIndeferimento;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getOpcao() {
		return opcao;
	}

	public void setOpcao(int opcao) {
		this.opcao = opcao;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	public List<Object[]> getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(List<Object[]> relatorio) {
		this.relatorio = relatorio;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public int getOrdenacaoRelatorio() {
		return ordenacaoRelatorio;
	}

	public void setOrdenacaoRelatorio(int ordenacaoRelatorio) {
		this.ordenacaoRelatorio = ordenacaoRelatorio;
	}

	public int getIdConvocacao() {
		return idConvocacao;
	}

	public void setIdConvocacao(int idConvocacao) {
		this.idConvocacao = idConvocacao;
	}

	
	public static String getJspForm() {
		return JSP_FORM;
	}
	
	public String getDescricaoProcessoSeletivo () throws DAOException {
		ConvocacaoProcessoSeletivoTecnicoMBean cBean = getMBean("convocacaoProcessoSeletivoTecnico");
		List <SelectItem> ps = cBean.getProcessosCombo();
		
		for (SelectItem p : ps)
			if (Integer.parseInt(""+p.getValue()) == processoSeletivo.getId())
				return p.getLabel();
				
		return "";
	}
	
	public String getDescricaoOpcao () throws DAOException {
		ConvocacaoProcessoSeletivoTecnicoMBean cBean = getMBean("convocacaoProcessoSeletivoTecnico");
		List <SelectItem> ps = cBean.getPolosCombo();
		
		for (SelectItem p : ps)
			if (Integer.parseInt(""+p.getValue()) == opcao)
				return p.getLabel();
				
		return "Todos";
	}
	
	public String getDescricaoStatusDiscente () {
		if (status > 0)
			return StatusDiscente.getDescricao(status);
		return "Todos";
	}
	
	public String getDescricaoConvocacao () throws DAOException {
		List <SelectItem> cs = getConvocacoesCombo();
		
		for (SelectItem c : cs)
			if (Integer.parseInt(""+c.getValue()) == opcao)
				return c.getLabel();
				
		return "Todas";
	}

	public Boolean getReservaVagas() {
		return reservaVagas;
	}

	public void setReservaVagas(Boolean reservaVagas) {
		this.reservaVagas = reservaVagas;
	}
	
	public String atualizarDadosCandidato () throws ArqException, NegocioException {
		checkRole(SigaaPapeis.GESTOR_CADASTRAMENTO_PROCESSO_SELETIVO_TECNICO);
		
		pessoa.getId();
		pessoa.getNome();
		pessoa.getEmail();
		pessoa.getCpf_cnpj();
		
		ListaMensagens lista = new ListaMensagens();

		ValidatorUtil.validateRequired(pessoa.getNome(), "Nome", lista);
		ValidatorUtil.validateRequired(pessoa.getEmail(), "Email", lista);
		ValidatorUtil.validateRequired(pessoa.getCpf_cnpj(), "CPF", lista);
		if (!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(pessoa.getCpf_cnpjString()))
			lista.addMensagem(new MensagemAviso("O Cpf informado é inválido",TipoMensagemUFRN.ERROR));
		
		if (!lista.isEmpty()){
			addMensagens(lista);
			return null;
		}
		
		prepareMovimento(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE_TECNICO);
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setObjMovimentado(pessoa);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_DADOS_PESSOAIS_DISCENTE_TECNICO);
		
		execute (mov);
		addMensagem(MensagensArquitetura.ALTERADO_COM_SUCESSO, "Dados do discente");
		
		return buscar();
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public String getTituloEmail() {
		return tituloEmail;
	}

	public void setTituloEmail(String tituloEmail) {
		this.tituloEmail = tituloEmail;
	}

	public String getTextoEmail() {
		return textoEmail;
	}

	public void setTextoEmail(String textoEmail) {
		this.textoEmail = textoEmail;
	}
}