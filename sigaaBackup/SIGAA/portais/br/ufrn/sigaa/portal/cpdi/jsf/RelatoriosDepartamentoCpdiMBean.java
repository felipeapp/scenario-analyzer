/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 22/03/2009 
 *
 */
package br.ufrn.sigaa.portal.cpdi.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.tecnico.dao.RelatoriosTecnicoDao;
import br.ufrn.sigaa.portal.cpdi.dao.RelatorioGerencialDAO;

/**
 * MBean respons�vel pelas consultas e gera��o dos relat�rios
 * por departamento para a COMISS�O PERMANENTE DE DESENVOLVIMENTO INSTITUCIONAL -CPDI
 *
 * @author Ricardo Wendell
 *
 */
@Component("relatoriosDepartamentoCpdi") 
@Scope("request")
public class RelatoriosDepartamentoCpdiMBean extends SigaaAbstractController<Object> {

	/** Diret�rio base dos relat�rios. */
	public static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/portal/cpdi/relatorios/";

	/** Departamento ao qual os relat�rios se restringe.*/
    private Unidade departamento;
    
    /** Ano inicial do relat�rio. */
    private Integer anoInicio;
    
    /** Ano final do relat�rio. */
    private Integer anoFim;
    
    /** Indica se o relat�rio � anal�tico. */
    private boolean analitico;
    
    /** Formato do relat�rio. */
    private String formato;
    
    /** Ano ao qual o relat�rio se restringe. */
    private Integer ano;
    
    /** Per�odo ao qual o relat�rio se restringe. */
    private Integer periodo;

    // Detalhes da exibi��o do formul�rio
    // (dependem de cada tipo de formul�rio)
    
    /** T�tulo do relat�rio. */
    private String titulo;
    
    /** Indica se o relat�rio necessita da especifica��o do per�odo. */
    private boolean necessitaPeriodo;

    /** Indica se o relat�rio necessita da especifica��o do semestre. */
	private boolean necessitaSemestre;
    
	/** Indica se o usu�rio pode selecionar o tipo de relat�rio. */
    private boolean selecaoTipo = true;
    
    /** Relat�rio a ser gerado. */
    private String relatorio;

    /** Sistema de origem dos dados de cada relat�rio. */
    private int origemDados = Sistema.SIGAA;
    
    /** Lista de dados do relat�rio. */
    private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();

    /** Indica se n�o deve validar os dados de gera��o do relat�rio. */
    private boolean naoValidar = false;
    
    /** Descri��o da Opera��o, a se exibida para o usu�rio no formul�rio.*/
    private String descricaoOperacao;

    /** Indica se o usu�rio pode selecionar o ano para gera��o do relat�rio. */
	private boolean necessitaAno;

    /** Construtor padr�o. */
    public RelatoriosDepartamentoCpdiMBean() throws DAOException {
        clear();
    }

    /**
     *  Inicializa os dados do formul�rio
     * @throws DAOException 
     */
    private void clear() throws DAOException {
    	analitico = true;

    	if ( isAcessoCompleto() ) {
    		departamento = new Unidade();
    		formato = "xls";
    	} else {
    		departamento = getUsuarioLogado().getVinculoAtivo().getUnidade();
    		formato = "pdf";
    	}
    }

    /** Inicia a gera��o do relat�rio Indicadores da situa��o docente atual do departamento.<br />
     * M�todo chamado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/graduacao/menus/relatorios_dae.jsp</li>
     * <li>/graduacao/menus/relatorios_cdp.jsp</li>
     * <li>/portais/docente/menu_docente.jsp</li>
     * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
     * </ul>
     * @return
     * @throws DAOException
     */
    public String iniciarSituacaoDocente() throws DAOException {
        titulo = "Indicadores da situa��o docente atual do departamento";
        descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
        		" os professores do quadro permanente;" +
        		" n�mero de professores substitutos, visitantes, etc;" +
        		" perdas desde a �ltima aquisi��o;" +
        		" n�mero de professores que j� cumprem as condi��es legais para aposentadoria integral;" +
        		" n�mero de professores com afastamento;" +
        		" n�mero de professores em fun��es administrativas;" +
        		" titula��o do corpo docente (permanentes + substitutos, visitantes, etc).";
        necessitaPeriodo = false;
        necessitaAno = false;
        necessitaSemestre = false;
        relatorio = "situacaoDocente_1879";
        origemDados = Sistema.SIPAC;
        return iniciar();
    }

	/**
	 * <p>
	 * Inicia a gera��o do Relat�rio de Indicadores de Ensino.<br>
	 * Este relat�rio lista, para o departamento escolhido: m�dia de cr�ditos
	 * por professor no ensino t�cnico, gradua��o e p�s-gradua��o; n�mero de
	 * trabalhos de fim de curso com participa��o de professores deste dpto (no
	 * ano); n�mero de trabalhos de fim de curso sob orienta��o de professores
	 * deste dpto (no ano) carga hor�ria semanal efetiva dispendida pelo docente
	 * em est�gios supervisionados (no ano); e carga hor�ria semanal efetiva
	 * dispendida pelo docente em resid�ncias m�dicas.
	 * </p>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp Chamado por</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresEnsino() throws DAOException {
        titulo = "Indicadores de Ensino";
        descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
        		" m�dia de cr�ditos por professor no ensino t�cnico, gradua��o e p�s-gradua��o;" +
				" n�mero de trabalhos de fim de curso com participa��o de professores deste dpto (no ano);" +
				" n�mero de trabalhos de fim de curso sob orienta��o de professores deste dpto (no ano)" +
				" carga hor�ria semanal efetiva dispendida pelo docente em est�gios supervisionados (no ano); e" +
				" carga hor�ria semanal efetiva dispendida pelo docente em resid�ncias m�dicas";
        necessitaPeriodo = false;
        necessitaAno = true;
        necessitaSemestre = true;
        relatorio = "ensino";
        anoFim = CalendarUtils.getAnoAtual();
        anoInicio = anoFim;
        origemDados = Sistema.SIGAA;
        return iniciar();
    }

	/**
	 * <p>
	 * Inicia a gera��o do relat�rio Distribui��o de turmas considerando o
	 * n�mero de alunos.
	 * </p>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarDistribuicaoTurmas() throws DAOException {
        titulo = "Distribui��o de turmas considerando o n�mero de alunos";
        descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
		        " c�digo e nome do componente curricular;" +
		        " quantidade de turmas por n�mero de discentes matriculados.";
        necessitaPeriodo = false;
        necessitaAno = true;
        necessitaSemestre = true;
        selecaoTipo = true;
        relatorio = "ensino_b_distribuicaoDeTurmas";
        anoFim = CalendarUtils.getAnoAtual();
        anoInicio = anoFim;
        origemDados = Sistema.SIGAA;
        return iniciar();
    }

	/**
	 * Inicia a gera��o do relat�rio Indicadores de Pesquisa.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresPesquisa() throws DAOException {
        titulo = "Indicadores de Pesquisa";
        descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
		        " bases de pesquisa;" +
				" professores do departamento envolvidos em atividade de pesquisa;" +
				" projetos com participa��o de docentes do departamento;" +
				" bolsistas dos programas ic;" +
				" disserta��es e teses defendidas; e" +
				" bolsistas de produtividade - CNPq." ;
        necessitaPeriodo = true;
        necessitaAno = false;
        necessitaSemestre = false;
        relatorio = "pesquisa";
        anoFim = CalendarUtils.getAnoAtual();
        anoInicio = anoFim - 1;
        origemDados = Sistema.SIGAA;
        return iniciar();
    }

	/**
	 * Inicia a gera��o do relat�rio Indicadores de Pesquisa (Agrupado por
	 * Docente)<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresPesquisaAgrupadoDocente() throws DAOException {
    	titulo = "Indicadores de Pesquisa (Agrupado por Docente)";
    	descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
    			" projetos de pesquisa;" +
    			" bolsistas envolvidos; e" +
    			" teses e disserta��es publicadas.";
    	selecaoTipo = false;
    	necessitaPeriodo = true;
    	necessitaAno = false;
    	necessitaSemestre = false;
    	relatorio = "pesquisa_8719_agru_docente";
    	anoFim = CalendarUtils.getAnoAtual();
    	anoInicio = anoFim - 1;
    	origemDados = Sistema.SIGAA;
    	return iniciar();
    }

	/**
	 * Inicia a gera��o do relat�rio Indicadores de Extens�o.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresExtensao() throws DAOException {
        titulo = "Indicadores de Extens�o";
        descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
        		" os de projetos e programas de extens�o" +
        		" em andamento coordenados por docentes do departamento." ;
        necessitaPeriodo = true;
        necessitaAno = false;
    	necessitaSemestre = false;
        relatorio = "extensao";
        anoFim = CalendarUtils.getAnoAtual();
        anoInicio = anoFim - 1;
        origemDados = Sistema.SIGAA;
        return iniciar();
    }

	/**
	 * Inicia a gera��o do Relat�rio Sint�tico<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarRelatorioSintetico() throws DAOException {
    	titulo = "Relat�rio Sint�tico";
		descricaoOperacao = "Este relat�rio lista, para o departamento escolhido:" +
				" os docentes com o v�nculo," +
				" carga hor�ria," +
				" turmas e disciplinas lecionadas na gradua��o e na p�s-gradua��o," +
				" n�mero de discentes orientandos," +
				" n�mero de atividades de pesquisa e extens�o," +
				" atividades administrativa" +
				" e a produ��o intelectual.";
    	selecaoTipo = false;
    	necessitaPeriodo = false;
    	necessitaAno = true;
    	relatorio = "cpdi_proplan_sintetico";
    	anoFim = CalendarUtils.getAnoAtual();
    	anoInicio = anoFim - 1;
    	origemDados = Sistema.SIGAA;
    	return iniciar();
    }

	/**
	 * Inicia a gera��o do relat�rio de Turmas por Departamento.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String iniciarTurmasDepartamentoDocente() throws DAOException {
		carregaDadosIniciarTurma();
		return forward("/portais/cpdi/relatorios/form_departamento.jsp");
	}

	/**
	 * M�todo que carrega os dados necess�rios para acessar o relat�rio de turmas por departamento.
	 * @throws DAOException
	 */
	private void carregaDadosIniciarTurma() throws DAOException{
		clear();
		anoInicio = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		necessitaPeriodo = false;
		necessitaAno = true;
		necessitaSemestre = true;
		descricaoOperacao = "Informe os dados para a gera��o do relat�rio." +
			" Observe que a quantidade de discentes ativos e discentes" +
			" com orienta��o acad�mica refletem a quantidade no ano/per�odo atual," +
			" independente do ano/per�odo informado.";
	}
	
	/** 
	 * M�todo utilizado para facilitar a integra��o com o sistema de Recursos Humanos (SIGRH).
	 * <br /><br />M�todo n�o invocado por JSP's.
	 */
	public String iniciarTurmasDepartamentoSelecaoSIGRH() throws DAOException, IOException{
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_CPDI);
		carregaDadosIniciarTurma();
		getCurrentResponse().sendRedirect("/sigaa/portais/cpdi/relatorios/form_departamento.jsf?id_unidade="+getParameter("id_unidade"));
		return null;
	}
	
	/**
	 * Inicia a gera��o do Relat�rio Gerencial Acad�mico<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 */
	public String iniciarRelatorioGerencialAcademico() throws HibernateException, DAOException{
		anoInicio = CalendarUtils.getAnoAtual();
		titulo = "Relat�rio Gerencial Acad�mico";
		if( isUserInRole(SigaaPapeis.DIRETOR_CENTRO) ){
			necessitaPeriodo = true;
			ano = getCalendarioVigente().getAno();
			periodo = getCalendarioVigente().getPeriodo();
		}
		return  forward("/portais/cpdi/relatorios/form_ano.jsp");
	}

    /** Indica se o usu�rio tem acesso completo � gera��o dos relat�rios. 
     * @return
     */
    public boolean isAcessoCompleto() {
    	return isUserInRole(SigaaPapeis.MEMBRO_CPDI, SigaaPapeis.DAE, SigaaPapeis.CDP);
    }

    /**
     * M�todo para iniciar a gera��o do relat�rio, compartilhado por todos os tipos de relat�rio.
     * Redireciona para a p�gina do formul�rio.
     * @return
     * @throws DAOException 
     */
    private String iniciar() throws DAOException {
        clear();
        return forward("/portais/cpdi/relatorios/form_indicadores.jsp");
    }

	/**
	 * Realiza a gera��o do relat�rio, de acordo com os crit�rios selecionados.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/relatorios/form_indicadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formul�rio
        if ( !naoValidar && !validate() ) {
            return null;
        }

        // Gerar relat�rio
        Connection con = null;
        try {
        	
            // Popular par�metros do relat�rio
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("SUBREPORT_DIR", BASE_REPORT_PACKAGE );
            parametros.put("subSistema", getSubSistema().getNome());
            parametros.put("subSistemaLink", getSubSistema().getLink());
            parametros.put("unidade", departamento.getId() );
            parametros.put("analitico", analitico );
        	parametros.put("anoInicio", anoInicio );
        	parametros.put("anoFim", anoFim );
        	parametros.put("ano", anoInicio );
        	parametros.put("periodo", periodo );

            // Preencher relat�rio
            con = getConexao();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReport(BASE_REPORT_PACKAGE, relatorio+".jasper"), parametros, con );

            if (prt.getPages().size() == 0) {
            	addMensagemErro("N�o foram encontrados resultados para os valores informados.");
				return null;
            }
            // Exportar relat�rio de acordo com o formato escolhido
            String nomeArquivo = "indicativo_"+relatorio+"."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a gera��o deste relat�rio. Por favor,contacte o suporte atrav�s do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
    }

	/**
	 * <p>
	 * Gera o Relat�rio Gerencial Acad�mico.<br>
	 * O Relat�rio Gerencial Acad�mico lista informa��es quantitativas
	 * acad�micas do centro tais como discentes, cursos, departamentos,
	 * docentes, projetos e pesquisas.
	 * </p>
	 * <br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/relatorios/form_ano.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
    public String gerarRelatorioGerencial() throws HibernateException, DAOException {
    	int idCentro = getUsuarioLogado().getVinculoAtivo().getUnidade().getUnidadeGestora().getId();
    	RelatorioGerencialDAO dao = getDAO(RelatorioGerencialDAO.class);

    	 if (necessitaPeriodo) {
          	ValidatorUtil.validaInt(ano, "Ano", erros);
          	ValidatorUtil.validaInt(periodo, "Per�odo", erros);
        }else{
        	ano = getCalendarioVigente().getAno();
        	periodo = getCalendarioVigente().getPeriodo();
        }
    	 
    	if ( hasErrors() ) {
			return null;
		}
    	
    	
    	List<Map<String, Object>> qtdDiscentesGraduacao = dao.findQtdAtivosMatriculadosByCentro(idCentro, getAno(), getPeriodo(), 
    			new Character[]{NivelEnsino.GRADUACAO});
    	List<Map<String, Object>> qtdCursoDepartamento = dao.findQtdDepartamentoCursoGraduacaoByCentro(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdDiscentesPos = dao.findQtdAtivosMatriculadosByCentro(idCentro, getAno(), getPeriodo(), 
    				new Character[]{NivelEnsino.STRICTO, NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO, NivelEnsino.LATO});
    	List<Map<String, Object>> qtdCursosPos = dao.findQtdCursosPosByCentro(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdTesesDissertacoes = dao.findQtdDefesasTeses(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdServidor = dao.findQtdPerfilByCentro(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdDocentesCentro = dao.findQtdDocenteByCentro(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdBasesPesquisa = dao.findQtdPesquisasAtivasByUnidade(idCentro, getAno(), getPeriodo());
    	List<Map<String, Object>> qtdProjetosExtensao = dao.findQtdProjetosExtensaoAtivosByUnidade(idCentro, null, getAno(), getPeriodo());
    	
    	getCurrentRequest().setAttribute("qtdDiscentesGraduacao", qtdDiscentesGraduacao);
    	getCurrentRequest().setAttribute("qtdCursoDepartamento", qtdCursoDepartamento);
    	getCurrentRequest().setAttribute("qtdDiscentesPos", qtdDiscentesPos);
    	getCurrentRequest().setAttribute("qtdCursosPos", qtdCursosPos);
    	getCurrentRequest().setAttribute("qtdTesesDissertacoes", qtdTesesDissertacoes);
    	getCurrentRequest().setAttribute("qtdServidor", qtdServidor);
    	getCurrentRequest().setAttribute("qtdDocentesCentro", qtdDocentesCentro);
    	getCurrentRequest().setAttribute("qtdBasesPesquisa", qtdBasesPesquisa);
    	getCurrentRequest().setAttribute("qtdProjetosExtensao", qtdProjetosExtensao);
    	
    	return forward("/portais/cpdi/relatorios/relatorio_gerencial.jsp");
    }
    
    /**
	 * Gera o relat�rio de Quantidade de Cursos.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/relatorios_cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String relatorioQuantidadeCurso() throws DAOException {
   
    	formato = "pdf";

        // Gerar relat�rio
        Connection con = null;
        try {

            // Popular par�metros do relat�rio
            HashMap<String, Object> parametros = new HashMap<String, Object>();

            // Preencher relat�rio
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf6788_UnidadeCursoTurnoCidadeModalidadeHabilitacao.jasper"), parametros, con );
            
            //Verifica se a consulta feita pelo relatorio encontrou algum resultado.
            //Caso a cosulta interna n�o tenha obtido �xito o n�mero de p�ginas ser� zero. 
            if(prt == null || ValidatorUtil.isEmpty(prt.getPages())) {
            	addMensagemWarning("N�o h� resultados a serem mostrados.");
            	return null;
            }
            
            // Exportar relat�rio de acordo com o formato escolhido
            String nomeArquivo = "RelatorioUnidadeCursoTurnoCidadeModalidadeHabilitacao."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a gera��o deste relat�rio. Por favor,contacte o suporte atrav�s do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }
		return null;
    }
    
    
    /**
     * Retorna a conex�o de acordo com a origem dos dados espec�fica
     * de cada relat�rio.
     *
     * @return
     * @throws SQLException
     */
    private Connection getConexao() throws SQLException {
        Connection con = null;
        switch (origemDados) {
            case Sistema.SIGAA: con = Database.getInstance().getSigaaConnection(); break;
            case Sistema.SIPAC: con = Database.getInstance().getSipacConnection(); break;
        }
        return con;
    }


    /**
     * Validar dados do formul�rio.
     *
     * @return
     * @throws DAOException 
     */
    private boolean validate() throws DAOException {
        ListaMensagens erros = new ListaMensagens();

        // Se o usu�rio n�o possuir acesso completo, poder� consultar apenas o seu departamento
        if ( isAcessoCompleto() ) {
        	 ValidatorUtil.validateRequiredId(departamento.getId(), "Departamento", erros);
        } else {
        	if (ValidatorUtil.isEmpty(departamento)) {
	        	if(getAcessoMenu().isChefeDepartamento()){
	        		setDepartamento( new Unidade(getUsuarioLogado().getVinculoAtivo().getResponsavel().getUnidade().getId())  );
	        	}else {
	        		setDepartamento( getUsuarioLogado().getVinculoAtivo().getUnidade() );
	        	}
        	}
        }

        if (necessitaPeriodo) {
        	ValidatorUtil.validaInt(anoInicio, "Ano inicial", erros);
        	ValidatorUtil.validaInt(anoFim, "Ano final", erros);
        }


        addMensagens(erros);
        return erros.isEmpty();
    }

	/**
	 * Gera o relat�rio de turmas por departamento.<br />
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/relatorios/form_departamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String gerarRelatorioTurmasDocenteDepartamento() throws DAOException {

    	ano = anoInicio;
    	ValidatorUtil.validateRequiredId(departamento.getId(), "Departamento", erros);
    	ValidatorUtil.validaInt(ano, "Ano", erros);

    	if(hasErrors()){
    		this.necessitaSemestre = true;
    		return null;
    	}

    	RelatoriosTecnicoDao dao = getDAO(RelatoriosTecnicoDao.class);
    	if(periodo == null)
    		periodo = getPeriodoAtual();
    	if (periodo == -1) {
    		// Todos per�odos
    		lista = new ArrayList<Map<String,Object>>();
    		for (int i = 1; i<= 4; i++)
    			lista.addAll(dao.findListaTurmasDocenteByDepartamento(departamento.getId(), ano, i));
    	} else {
    		lista = dao.findListaTurmasDocenteByDepartamento(departamento.getId(), ano, periodo);
    	}
    	if (lista == null || lista.isEmpty()) {
    		addMensagemErro("N�o foram encontradas turmas com os par�metros informados.");
    		return null;
    	}
		
		Collections.sort(lista, new Comparator<Map<String,Object>>(){
			public int compare(Map<String, Object> m1,
					Map<String, Object> m2) {
				int result = 0;
				result = ((String) m1.get("docente_nome")).compareTo((String) m2.get("docente_nome"));
				
				if( result == 0 )
					result = ((String)m1.get("siape")).compareTo((String)m2.get("siape"));
				if( result == 0 )
					result = ((Character)m1.get("nivel")).compareTo((Character)m2.get("nivel"));
				if( result == 0 )
					result = (Integer)m1.get("periodo") - (Integer)m2.get("periodo");
				if( result == 0 )
					result = ((String)m1.get("componente_nome")).compareTo((String)m2.get("componente_nome"));
				if( result == 0 )
					result = ((String)m1.get("componente_codigo")).compareTo((String)m2.get("componente_codigo"));
				if( result == 0 ) {
					if( m1.get("id_polo") != null && m2.get("id_polo") != null)
						result = (Integer)m1.get("id_polo") - (Integer)m2.get("id_polo");
					else
						result = ((String)m1.get("codigo_turma")).compareTo((String)m2.get("codigo_turma"));
				}
				
				return result;
			}
		});
		
    	departamento = dao.findByPrimaryKey(departamento.getId(), Unidade.class, "nome");
    	return forward("/portais/cpdi/relatorios/lista_turmas_docente.jsp");
    }

    /** Indica se o relat�rio � anal�tico. 
     * @return
     */
    public boolean isAnalitico() {
        return this.analitico;
    }

    /** Seta se o relat�rio � anal�tico. 
     * @param analitico
     */
    public void setAnalitico(boolean analitico) {
        this.analitico = analitico;
    }

    /** Retorna o ano final do relat�rio. 
     * @return
     */
    public Integer getAnoFim() {
        return this.anoFim;
    }

    /** Seta o ano final do relat�rio. 
     * @param anoFim
     */
    public void setAnoFim(Integer anoFim) {
        this.anoFim = anoFim;
    }

    /** Retorna o ano inicial do relat�rio. 
     * @return
     */
    public Integer getAnoInicio() {
        return this.anoInicio;
    }

    /** Seta o ano inicial do relat�rio.
     * @param anoInicio
     */
    public void setAnoInicio(Integer anoInicio) {
        this.anoInicio = anoInicio;
    }

    /** Retorna o departamento ao qual os relat�rios se restringe.
     * @return
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /** Seta o departamento ao qual os relat�rios se restringe.
     * @param departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /** Retorna o formato do relat�rio. 
     * @return
     */
    public String getFormato() {
        return this.formato;
    }

    /** Seta o formato do relat�rio.
     * @param formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /** Indica se o relat�rio necessita da especifica��o do per�odo. 
     * @return
     */
    public boolean isNecessitaPeriodo() {
        return this.necessitaPeriodo;
    }

    /** Seta se o relat�rio necessita da especifica��o do per�odo. 
     * @param exibirPeriodo
     */
    public void setNecessitaPeriodo(boolean exibirPeriodo) {
        this.necessitaPeriodo = exibirPeriodo;
    }

    /** Retorna o relat�rio a ser gerado. 
     * @return
     */
    public String getRelatorio() {
        return this.relatorio;
    }

    /** Seta o relat�rio a ser gerado.
     * @param relatorio
     */
    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    /** Retorna o t�tulo do relat�rio. 
     * @return
     */
    public String getTitulo() {
        return this.titulo;
    }

    /** Seta o t�tulo do relat�rio.
     * @param titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /** Retorna o sistema de origem dos dados de cada relat�rio. 
     * @return
     */
    public int getOrigemDados() {
        return this.origemDados;
    }

    /** Seta o sistema de origem dos dados de cada relat�rio.
     * @param origemDados
     */
    public void setOrigemDados(int origemDados) {
        this.origemDados = origemDados;
    }

	/** Indica se o usu�rio pode selecionar o tipo de relat�rio. 
	 * @return
	 */
	public boolean isSelecaoTipo() {
		return this.selecaoTipo;
	}

	/** Seta se o usu�rio pode selecionar o tipo de relat�rio. 
	 * @param selecaoTipo
	 */
	public void setSelecaoTipo(boolean selecaoTipo) {
		this.selecaoTipo = selecaoTipo;
	}

	/** Retorna a lista de dados do relat�rio. 
	 * @return
	 */
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	/** Seta a lista de dados do relat�rio. 
	 * @param lista
	 */
	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	/** Retorna o n�mero de registros encontrados.
	 * @return
	 */
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}

	/** Indica se n�o deve validar os dados de gera��o do relat�rio. 
	 * @return
	 */
	public boolean isNaoValidar() {
		return naoValidar;
	}

	/** Seta se n�o deve validar os dados de gera��o do relat�rio. 
	 * @param naoValidar
	 */
	public void setNaoValidar(boolean naoValidar) {
		this.naoValidar = naoValidar;
	}

	/** Retorna o ano ao qual o relat�rio se restringe. 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano ao qual o relat�rio se restringe.
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo ao qual o relat�rio se restringe. 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo ao qual o relat�rio se restringe.
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
    /** Indica se o relat�rio necessita da especifica��o do semestre. 
     * @return
     */
    public boolean isNecessitaSemestre() {
		return necessitaSemestre;
	}

	/** Seta se o relat�rio necessita da especifica��o do semestre. 
	 * @param necessitaSemestre
	 */
	public void setNecessitaSemestre(boolean necessitaSemestre) {
		this.necessitaSemestre = necessitaSemestre;
	}

	public String getDescricaoOperacao() {
		return descricaoOperacao;
	}

	public boolean isNecessitaAno() {
		return necessitaAno;
	}

	public void setNecessitaAno(boolean necessitaAno) {
		this.necessitaAno = necessitaAno;
	}
	
	/**
	 * Utilizado na integra��o pelo SIGRH para relat�rio de departamentos CPDI.
	 * @return
	 */
	public String getUnidadeIntegracao() {
		String idUnidade = getParameter("id_unidade");
		if(!isEmpty(idUnidade)) 
			departamento.setId(Integer.parseInt(idUnidade));
		return null;
	}

}
