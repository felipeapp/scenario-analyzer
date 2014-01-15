/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável pelas consultas e geração dos relatórios
 * por departamento para a COMISSÃO PERMANENTE DE DESENVOLVIMENTO INSTITUCIONAL -CPDI
 *
 * @author Ricardo Wendell
 *
 */
@Component("relatoriosDepartamentoCpdi") 
@Scope("request")
public class RelatoriosDepartamentoCpdiMBean extends SigaaAbstractController<Object> {

	/** Diretório base dos relatórios. */
	public static final String BASE_REPORT_PACKAGE = "/br/ufrn/sigaa/portal/cpdi/relatorios/";

	/** Departamento ao qual os relatórios se restringe.*/
    private Unidade departamento;
    
    /** Ano inicial do relatório. */
    private Integer anoInicio;
    
    /** Ano final do relatório. */
    private Integer anoFim;
    
    /** Indica se o relatório é analítico. */
    private boolean analitico;
    
    /** Formato do relatório. */
    private String formato;
    
    /** Ano ao qual o relatório se restringe. */
    private Integer ano;
    
    /** Período ao qual o relatório se restringe. */
    private Integer periodo;

    // Detalhes da exibição do formulário
    // (dependem de cada tipo de formulário)
    
    /** Título do relatório. */
    private String titulo;
    
    /** Indica se o relatório necessita da especificação do período. */
    private boolean necessitaPeriodo;

    /** Indica se o relatório necessita da especificação do semestre. */
	private boolean necessitaSemestre;
    
	/** Indica se o usuário pode selecionar o tipo de relatório. */
    private boolean selecaoTipo = true;
    
    /** Relatório a ser gerado. */
    private String relatorio;

    /** Sistema de origem dos dados de cada relatório. */
    private int origemDados = Sistema.SIGAA;
    
    /** Lista de dados do relatório. */
    private List<Map<String,Object>> lista = new ArrayList<Map<String,Object>>();

    /** Indica se não deve validar os dados de geração do relatório. */
    private boolean naoValidar = false;
    
    /** Descrição da Operação, a se exibida para o usuário no formulário.*/
    private String descricaoOperacao;

    /** Indica se o usuário pode selecionar o ano para geração do relatório. */
	private boolean necessitaAno;

    /** Construtor padrão. */
    public RelatoriosDepartamentoCpdiMBean() throws DAOException {
        clear();
    }

    /**
     *  Inicializa os dados do formulário
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

    /** Inicia a geração do relatório Indicadores da situação docente atual do departamento.<br />
     * Método chamado pela(s) seguinte(s) JSP(s):
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
        titulo = "Indicadores da situação docente atual do departamento";
        descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
        		" os professores do quadro permanente;" +
        		" número de professores substitutos, visitantes, etc;" +
        		" perdas desde a última aquisição;" +
        		" número de professores que já cumprem as condições legais para aposentadoria integral;" +
        		" número de professores com afastamento;" +
        		" número de professores em funções administrativas;" +
        		" titulação do corpo docente (permanentes + substitutos, visitantes, etc).";
        necessitaPeriodo = false;
        necessitaAno = false;
        necessitaSemestre = false;
        relatorio = "situacaoDocente_1879";
        origemDados = Sistema.SIPAC;
        return iniciar();
    }

	/**
	 * <p>
	 * Inicia a geração do Relatório de Indicadores de Ensino.<br>
	 * Este relatório lista, para o departamento escolhido: média de créditos
	 * por professor no ensino técnico, graduação e pós-graduação; número de
	 * trabalhos de fim de curso com participação de professores deste dpto (no
	 * ano); número de trabalhos de fim de curso sob orientação de professores
	 * deste dpto (no ano) carga horária semanal efetiva dispendida pelo docente
	 * em estágios supervisionados (no ano); e carga horária semanal efetiva
	 * dispendida pelo docente em residências médicas.
	 * </p>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
        descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
        		" média de créditos por professor no ensino técnico, graduação e pós-graduação;" +
				" número de trabalhos de fim de curso com participação de professores deste dpto (no ano);" +
				" número de trabalhos de fim de curso sob orientação de professores deste dpto (no ano)" +
				" carga horária semanal efetiva dispendida pelo docente em estágios supervisionados (no ano); e" +
				" carga horária semanal efetiva dispendida pelo docente em residências médicas";
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
	 * Inicia a geração do relatório Distribuição de turmas considerando o
	 * número de alunos.
	 * </p>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarDistribuicaoTurmas() throws DAOException {
        titulo = "Distribuição de turmas considerando o número de alunos";
        descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
		        " código e nome do componente curricular;" +
		        " quantidade de turmas por número de discentes matriculados.";
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
	 * Inicia a geração do relatório Indicadores de Pesquisa.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
        descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
		        " bases de pesquisa;" +
				" professores do departamento envolvidos em atividade de pesquisa;" +
				" projetos com participação de docentes do departamento;" +
				" bolsistas dos programas ic;" +
				" dissertações e teses defendidas; e" +
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
	 * Inicia a geração do relatório Indicadores de Pesquisa (Agrupado por
	 * Docente)<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresPesquisaAgrupadoDocente() throws DAOException {
    	titulo = "Indicadores de Pesquisa (Agrupado por Docente)";
    	descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
    			" projetos de pesquisa;" +
    			" bolsistas envolvidos; e" +
    			" teses e dissertações publicadas.";
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
	 * Inicia a geração do relatório Indicadores de Extensão.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/docente/menu_docente.jsp</li>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarIndicadoresExtensao() throws DAOException {
        titulo = "Indicadores de Extensão";
        descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
        		" os de projetos e programas de extensão" +
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
	 * Inicia a geração do Relatório Sintético<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/abas/sitdepartamento.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String iniciarRelatorioSintetico() throws DAOException {
    	titulo = "Relatório Sintético";
		descricaoOperacao = "Este relatório lista, para o departamento escolhido:" +
				" os docentes com o vínculo," +
				" carga horária," +
				" turmas e disciplinas lecionadas na graduação e na pós-graduação," +
				" número de discentes orientandos," +
				" número de atividades de pesquisa e extensão," +
				" atividades administrativa" +
				" e a produção intelectual.";
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
	 * Inicia a geração do relatório de Turmas por Departamento.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
	 * Método que carrega os dados necessários para acessar o relatório de turmas por departamento.
	 * @throws DAOException
	 */
	private void carregaDadosIniciarTurma() throws DAOException{
		clear();
		anoInicio = CalendarUtils.getAnoAtual();
		periodo = getPeriodoAtual();
		necessitaPeriodo = false;
		necessitaAno = true;
		necessitaSemestre = true;
		descricaoOperacao = "Informe os dados para a geração do relatório." +
			" Observe que a quantidade de discentes ativos e discentes" +
			" com orientação acadêmica refletem a quantidade no ano/período atual," +
			" independente do ano/período informado.";
	}
	
	/** 
	 * Método utilizado para facilitar a integração com o sistema de Recursos Humanos (SIGRH).
	 * <br /><br />Método não invocado por JSP's.
	 */
	public String iniciarTurmasDepartamentoSelecaoSIGRH() throws DAOException, IOException{
		setSubSistemaAtual(SigaaSubsistemas.PORTAL_CPDI);
		carregaDadosIniciarTurma();
		getCurrentResponse().sendRedirect("/sigaa/portais/cpdi/relatorios/form_departamento.jsf?id_unidade="+getParameter("id_unidade"));
		return null;
	}
	
	/**
	 * Inicia a geração do Relatório Gerencial Acadêmico<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
		titulo = "Relatório Gerencial Acadêmico";
		if( isUserInRole(SigaaPapeis.DIRETOR_CENTRO) ){
			necessitaPeriodo = true;
			ano = getCalendarioVigente().getAno();
			periodo = getCalendarioVigente().getPeriodo();
		}
		return  forward("/portais/cpdi/relatorios/form_ano.jsp");
	}

    /** Indica se o usuário tem acesso completo à geração dos relatórios. 
     * @return
     */
    public boolean isAcessoCompleto() {
    	return isUserInRole(SigaaPapeis.MEMBRO_CPDI, SigaaPapeis.DAE, SigaaPapeis.CDP);
    }

    /**
     * Método para iniciar a geração do relatório, compartilhado por todos os tipos de relatório.
     * Redireciona para a página do formulário.
     * @return
     * @throws DAOException 
     */
    private String iniciar() throws DAOException {
        clear();
        return forward("/portais/cpdi/relatorios/form_indicadores.jsp");
    }

	/**
	 * Realiza a geração do relatório, de acordo com os critérios selecionados.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/portais/cpdi/relatorios/form_indicadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formulário
        if ( !naoValidar && !validate() ) {
            return null;
        }

        // Gerar relatório
        Connection con = null;
        try {
        	
            // Popular parâmetros do relatório
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

            // Preencher relatório
            con = getConexao();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReport(BASE_REPORT_PACKAGE, relatorio+".jasper"), parametros, con );

            if (prt.getPages().size() == 0) {
            	addMensagemErro("Não foram encontrados resultados para os valores informados.");
				return null;
            }
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "indicativo_"+relatorio+"."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
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
	 * <p>
	 * Gera o Relatório Gerencial Acadêmico.<br>
	 * O Relatório Gerencial Acadêmico lista informações quantitativas
	 * acadêmicas do centro tais como discentes, cursos, departamentos,
	 * docentes, projetos e pesquisas.
	 * </p>
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
          	ValidatorUtil.validaInt(periodo, "Período", erros);
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
	 * Gera o relatório de Quantidade de Cursos.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/graduacao/menus/relatorios_cdp.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
    public String relatorioQuantidadeCurso() throws DAOException {
   
    	formato = "pdf";

        // Gerar relatório
        Connection con = null;
        try {

            // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();

            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf6788_UnidadeCursoTurnoCidadeModalidadeHabilitacao.jasper"), parametros, con );
            
            //Verifica se a consulta feita pelo relatorio encontrou algum resultado.
            //Caso a cosulta interna não tenha obtido êxito o número de páginas será zero. 
            if(prt == null || ValidatorUtil.isEmpty(prt.getPages())) {
            	addMensagemWarning("Não há resultados a serem mostrados.");
            	return null;
            }
            
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "RelatorioUnidadeCursoTurnoCidadeModalidadeHabilitacao."+formato;
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), formato);
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
     * Retorna a conexão de acordo com a origem dos dados específica
     * de cada relatório.
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
     * Validar dados do formulário.
     *
     * @return
     * @throws DAOException 
     */
    private boolean validate() throws DAOException {
        ListaMensagens erros = new ListaMensagens();

        // Se o usuário não possuir acesso completo, poderá consultar apenas o seu departamento
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
	 * Gera o relatório de turmas por departamento.<br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
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
    		// Todos períodos
    		lista = new ArrayList<Map<String,Object>>();
    		for (int i = 1; i<= 4; i++)
    			lista.addAll(dao.findListaTurmasDocenteByDepartamento(departamento.getId(), ano, i));
    	} else {
    		lista = dao.findListaTurmasDocenteByDepartamento(departamento.getId(), ano, periodo);
    	}
    	if (lista == null || lista.isEmpty()) {
    		addMensagemErro("Não foram encontradas turmas com os parâmetros informados.");
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

    /** Indica se o relatório é analítico. 
     * @return
     */
    public boolean isAnalitico() {
        return this.analitico;
    }

    /** Seta se o relatório é analítico. 
     * @param analitico
     */
    public void setAnalitico(boolean analitico) {
        this.analitico = analitico;
    }

    /** Retorna o ano final do relatório. 
     * @return
     */
    public Integer getAnoFim() {
        return this.anoFim;
    }

    /** Seta o ano final do relatório. 
     * @param anoFim
     */
    public void setAnoFim(Integer anoFim) {
        this.anoFim = anoFim;
    }

    /** Retorna o ano inicial do relatório. 
     * @return
     */
    public Integer getAnoInicio() {
        return this.anoInicio;
    }

    /** Seta o ano inicial do relatório.
     * @param anoInicio
     */
    public void setAnoInicio(Integer anoInicio) {
        this.anoInicio = anoInicio;
    }

    /** Retorna o departamento ao qual os relatórios se restringe.
     * @return
     */
    public Unidade getDepartamento() {
        return this.departamento;
    }

    /** Seta o departamento ao qual os relatórios se restringe.
     * @param departamento
     */
    public void setDepartamento(Unidade departamento) {
        this.departamento = departamento;
    }

    /** Retorna o formato do relatório. 
     * @return
     */
    public String getFormato() {
        return this.formato;
    }

    /** Seta o formato do relatório.
     * @param formato
     */
    public void setFormato(String formato) {
        this.formato = formato;
    }

    /** Indica se o relatório necessita da especificação do período. 
     * @return
     */
    public boolean isNecessitaPeriodo() {
        return this.necessitaPeriodo;
    }

    /** Seta se o relatório necessita da especificação do período. 
     * @param exibirPeriodo
     */
    public void setNecessitaPeriodo(boolean exibirPeriodo) {
        this.necessitaPeriodo = exibirPeriodo;
    }

    /** Retorna o relatório a ser gerado. 
     * @return
     */
    public String getRelatorio() {
        return this.relatorio;
    }

    /** Seta o relatório a ser gerado.
     * @param relatorio
     */
    public void setRelatorio(String relatorio) {
        this.relatorio = relatorio;
    }

    /** Retorna o título do relatório. 
     * @return
     */
    public String getTitulo() {
        return this.titulo;
    }

    /** Seta o título do relatório.
     * @param titulo
     */
    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    /** Retorna o sistema de origem dos dados de cada relatório. 
     * @return
     */
    public int getOrigemDados() {
        return this.origemDados;
    }

    /** Seta o sistema de origem dos dados de cada relatório.
     * @param origemDados
     */
    public void setOrigemDados(int origemDados) {
        this.origemDados = origemDados;
    }

	/** Indica se o usuário pode selecionar o tipo de relatório. 
	 * @return
	 */
	public boolean isSelecaoTipo() {
		return this.selecaoTipo;
	}

	/** Seta se o usuário pode selecionar o tipo de relatório. 
	 * @param selecaoTipo
	 */
	public void setSelecaoTipo(boolean selecaoTipo) {
		this.selecaoTipo = selecaoTipo;
	}

	/** Retorna a lista de dados do relatório. 
	 * @return
	 */
	public List<Map<String, Object>> getLista() {
		return lista;
	}

	/** Seta a lista de dados do relatório. 
	 * @param lista
	 */
	public void setLista(List<Map<String, Object>> lista) {
		this.lista = lista;
	}

	/** Retorna o número de registros encontrados.
	 * @return
	 */
	public int getNumeroRegistrosEncontrados() {
		if(lista!=null)
			return lista.size();
		else
			return 0;
	}

	/** Indica se não deve validar os dados de geração do relatório. 
	 * @return
	 */
	public boolean isNaoValidar() {
		return naoValidar;
	}

	/** Seta se não deve validar os dados de geração do relatório. 
	 * @param naoValidar
	 */
	public void setNaoValidar(boolean naoValidar) {
		this.naoValidar = naoValidar;
	}

	/** Retorna o ano ao qual o relatório se restringe. 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano ao qual o relatório se restringe.
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o período ao qual o relatório se restringe. 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o período ao qual o relatório se restringe.
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}
	
    /** Indica se o relatório necessita da especificação do semestre. 
     * @return
     */
    public boolean isNecessitaSemestre() {
		return necessitaSemestre;
	}

	/** Seta se o relatório necessita da especificação do semestre. 
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
	 * Utilizado na integração pelo SIGRH para relatório de departamentos CPDI.
	 * @return
	 */
	public String getUnidadeIntegracao() {
		String idUnidade = getParameter("id_unidade");
		if(!isEmpty(idUnidade)) 
			departamento.setId(Integer.parseInt(idUnidade));
		return null;
	}

}
