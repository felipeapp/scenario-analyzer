/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.TipoNecessidadeEspecial;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;

/**
 * MBean respons�vel pelas consultas e gera��o de alguns relat�rios em jasper reports.
 * 
 * @author leonardo
 *
 */
public class RelatoriosJasperMBean extends SigaaAbstractController<Object> {

	/** Departamento ao qual se restringe os dados do relat�rio. */
	private Unidade departamento;
	/** Ano ao qual se restringe os dados do relat�rio. */
    private Integer ano;
    /** Per�odo ao qual se restringe os dados do relat�rio. */
    private Integer periodo;
    /** Formato do relat�rio (PDF, HTML, Excel). */
    private String formato;

    // Detalhes da exibi��o do formul�rio
    // (dependem de cada tipo de formul�rio)
    /** T�tulo do relat�rio. */
    private String titulo;
    /** Nome do arquivo Jasper do relat�rio. */
    private String relatorio;

    /** Indica se dever� exibir a sele��o do departamento no formul�rio de gera��o do relat�rio. */
    private boolean exibeDepartamento;
    /** Indica se dever� exibir a sele��o do centro no formul�rio de gera��o do relat�rio. */
    private boolean exibeCentro;
    /** Indica se dever� exibir a sele��o do tipo de componente curricular no formul�rio de gera��o do relat�rio. */
    private boolean exibeTipoComponente;
    /** Indica se dever� exibir a sele��o do tipo de necessidade especial no formul�rio de gera��o do relat�rio. */
    private boolean exibeTipoNecessidade;
    /** Indica se dever� exibir a sele��o do ano-per�odo no formul�rio de gera��o do relat�rio. */
    private boolean ocultaAnoPeriodo;
    
    /** Tipo de componente curricular ao qual se restringe os dados do relat�rio. */
    private TipoComponenteCurricular tipoComponente;
    /** Tipo de necessidade especial ao qual se restringe os dados do relat�rio. */
    private TipoNecessidadeEspecial tipoNecessidade;

	/** Construtor padr�o. */
	public RelatoriosJasperMBean(){
    	clear();
    }

	/** Reseta os valores dos par�metros utilizados na gera��o do relat�rio. */
	private void clear() {
		departamento = new Unidade();
		tipoComponente = new TipoComponenteCurricular();
		tipoNecessidade = new TipoNecessidadeEspecial();
		formato = "pdf";
	}
	
	/*
     * METODOS DE INICIALIZACAO
     */
	
	/** Inicia a gera��o do relat�rio sint�tico de turmas n�o consolidadas.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarTurmasNaoConsolidadasSintetico(){
		titulo = "Relat�rio Sint�tico de Turmas N�o Consolidadas";
		relatorio = "TurmasNaoConsolidadasSintetico";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeDepartamento = false;
		exibeCentro = true;
		
		return iniciar();
	}
	
	/** Inicia a gera��o do relat�rio de docentes que n�o cosolidaram as turmas.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarDocentesQueNaoConsolidaramTurmas(){
		titulo = "Relat�rio de Docentes que n�o consolidaram as turmas";
		relatorio = "DocentesQueNaoConsolidaramTurmas";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeDepartamento = true;
		
		return iniciar();
	}
	
	/** Inicia a gera��o do relat�rio de alunos com todos os componentes curriculares reprovados em um per�odo letivo.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarAlunosTodosComp_Reprov(){
		titulo = "Relat�rio de alunos com todos os componentes curriculares reprovados em um per�odo letivo";
		relatorio = "trf6948_AlunosTodosComp_Reprov";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeCentro = true;
		
		return iniciar();
	}
	
	/** Inicia a gera��o do relat�rio de alunos com todos os componentes curriculares trancados em um per�odo letivo.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarAlunosTodosCompTranc(){
		titulo = "Relat�rio de alunos com todos os componentes curriculares trancados em um per�odo letivo";
		relatorio = "trf6948_AlunosTodosCompTranc";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeCentro = true;
		exibeTipoComponente = true;
		
		return iniciar();
	}
	
	/** Inicia a gera��o do relat�rio de alunos com necessidades especiais.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 *             /SIGAA/app/sigaa.ear/sigaa.war/portais/rh_plan/abas/graduacao.jsp
	 * @return
	 */
	public String iniciarNecessidadesEspeciais(){
		titulo = "Relat�rio de alunos com necessidades especiais";
		relatorio = "trf8011_AlunosNec_Especiais";
		exibeTipoNecessidade = true;
		ocultaAnoPeriodo = true;
		
		return iniciar();
	}
	
	/** Inicia o relat�rio de apostilamentos.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarApostilamentos() {
	   titulo = "Relat�rio de Apostilamentos";
	   relatorio = "trf4951_Apostilamentos";
	   ano = getCalendarioVigente().getAno();
	   periodo = getCalendarioVigente().getPeriodo();
	   exibeCentro = true;
	   
	   return iniciar();
	}
	
	/** Inicia o relat�rio de estruturas curriculares.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarEstruturasCurriculares() {
		   titulo = "Relat�rio de Estruturas Curriculares";
		   relatorio = "trf6789_EstrutCurricul_Centro";
		   ano = getCalendarioVigente().getAno();
		   periodo = getCalendarioVigente().getPeriodo();
		   exibeCentro = true;
		   ocultaAnoPeriodo = true;
		   
		   return iniciar();
	 }
	
	/** Inicia o relat�rio quantitativo de orienta��es acad�micas por curso.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarQuantitativoOrientacoesAcademicas() {
		   titulo = "Relat�rio Quantitativo de Orienta��es Acad�micas por Curso";
		   relatorio = "trf13276_Relatorio_Qtd_Orientacao_Academica";
		   ano = getCalendarioVigente().getAno();
		   periodo = getCalendarioVigente().getPeriodo();
		   this.exibeCentro = false;
		   this.exibeDepartamento = false;
		   this.exibeTipoComponente = false;
		   this.exibeTipoNecessidade = false;
		   this.ocultaAnoPeriodo = false;
		   return iniciar();
	 }
	
	/**
     * M�todo para iniciar a gera��o do relat�rio, compartilhado por todos os tipos de relat�rio.
     * Redireciona para a pagina do formul�rio de par�metros do relat�rio.
     *
     * @return
     */
    private String iniciar() {
        clear();
        return forward("/graduacao/relatorios/form_relatorios.jsp");
    }
    
    /**
     * Realizar a gera��o do relat�rio, de acordo com os crit�rios selecionados
     * Chamado por /graduacao/relatorios/form_relatorios.jsp
     * @return
     * @throws DAOException
     */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formul�rio
        if ( !validate() ) {
            return null;
        }

        // Gerar relat�rio
        Connection con = null;
        try {

            // Popular par�metros do relat�rio
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("SUBREPORT_DIR", JasperReportsUtil.PATH_RELATORIOS_SIGAA );
            if (exibeDepartamento || exibeCentro) {
            	parametros.put("unidade", departamento.getId() );
            }
            parametros.put("subSistema", getSubSistema().getNome());
            parametros.put("subSistemaLink", getSubSistema().getLink());
            parametros.put("ano", ano );
            parametros.put("periodo", periodo );
            parametros.put("tipo_componente", tipoComponente.getId() );
            parametros.put("tipo", tipoNecessidade.getId() );

            // Preencher relat�rio
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(relatorio+".jasper"), parametros, con );

            if (prt.getPages().size() == 0) {
				addMensagemWarning("O relat�rio n�o possui dados.");
				return null;
			}
            // Exportar relat�rio de acordo com o formato escolhido
            String nomeArquivo = "Relatorio"+relatorio+"."+formato;
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
     * Validar dados do formul�rio
     *
     * @return
     */
    private boolean validate() {
        ListaMensagens erros = new ListaMensagens();

        if (exibeDepartamento) {
        	ValidatorUtil.validateRequiredId(departamento.getId(), "Departamento", erros);
        }
        
        if(!ocultaAnoPeriodo){
        	ValidatorUtil.validaInt(ano, "Ano", erros);
        	ValidatorUtil.validaInt(periodo, "Per�odo", erros);
        }

        addMensagens(erros);
        return erros.isEmpty();
    }

	/** Retorna o departamento ao qual se restringe os dados do relat�rio. 
	 * @return
	 */
	public Unidade getDepartamento() {
		return departamento;
	}

	/** Seta o departamento ao qual se restringe os dados do relat�rio.
	 * @param departamento
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	/** Retorna o ano ao qual se restringe os dados do relat�rio. 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano ao qual se restringe os dados do relat�rio.
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o per�odo ao qual se restringe os dados do relat�rio. 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o per�odo ao qual se restringe os dados do relat�rio.
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	/** Retorna o formato do relat�rio (PDF, HTML, Excel). 
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/** Seta o formato do relat�rio (PDF, HTML, Excel).
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Retorna o t�tulo do relat�rio. 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o t�tulo do relat�rio.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna o nome do arquivo Jasper do relat�rio. 
	 * @return
	 */
	public String getRelatorio() {
		return relatorio;
	}

	/** Seta o nome do arquivo Jasper do relat�rio.
	 * @param relatorio
	 */
	public void setRelatorio(String relatorio) {
		this.relatorio = relatorio;
	}

	/** Indica se dever� exibir a sele��o do departamento no formul�rio de gera��o do relat�rio. 
	 * @return
	 */
	public boolean isExibeDepartamento() {
		return this.exibeDepartamento;
	}

	/** Seta se dever� exibir a sele��o do departamento no formul�rio de gera��o do relat�rio. 
	 * @param exibeDepartamento
	 */
	public void setExibeDepartamento(boolean exibeDepartamento) {
		this.exibeDepartamento = exibeDepartamento;
	}

	/** Indica se dever� exibir a sele��o do centro no formul�rio de gera��o do relat�rio. 
	 * @return
	 */
	public boolean isExibeCentro() {
		return exibeCentro;
	}

	/** Seta se dever� exibir a sele��o do centro no formul�rio de gera��o do relat�rio. 
	 * @param exibeCentro
	 */
	public void setExibeCentro(boolean exibeCentro) {
		this.exibeCentro = exibeCentro;
	}

	/** Indica se dever� exibir a sele��o do tipo de componente curricular no formul�rio de gera��o do relat�rio.  
	 * @return
	 */
	public boolean isExibeTipoComponente() {
		return exibeTipoComponente;
	}

	/** Seta se dever� exibir a sele��o do tipo de componente curricular no formul�rio de gera��o do relat�rio. 
	 * @param exibeTipoComponente
	 */
	public void setExibeTipoComponente(boolean exibeTipoComponente) {
		this.exibeTipoComponente = exibeTipoComponente;
	}

	/** Retorna o tipo de componente curricular ao qual se restringe os dados do relat�rio. 
	 * @return
	 */
	public TipoComponenteCurricular getTipoComponente() {
		return tipoComponente;
	}

	/** Seta o tipo de componente curricular ao qual se restringe os dados do relat�rio.
	 * @param tipoComponente
	 */
	public void setTipoComponente(TipoComponenteCurricular tipoComponente) {
		this.tipoComponente = tipoComponente;
	}
	
    /** Retorna o tipo de necessidade especial ao qual se restringe os dados do relat�rio. 
     * @return
     */
    public TipoNecessidadeEspecial getTipoNecessidade() {
		return tipoNecessidade;
	}

    /** Seta o tipo de necessidade especial ao qual se restringe os dados do relat�rio.
	 * @param tipoNecessidade
	 */
	public void setTipoNecessidade(TipoNecessidadeEspecial tipoNecessidade) {
		this.tipoNecessidade = tipoNecessidade;
	}

	/** Indica se dever� exibir a sele��o do tipo de necessidade especial no formul�rio de gera��o do relat�rio. 
	 * @return
	 */
	public boolean isExibeTipoNecessidade() {
		return exibeTipoNecessidade;
	}

	/** Seta se dever� exibir a sele��o do tipo de necessidade especial no formul�rio de gera��o do relat�rio. 
	 * @param exibeTipoNecessidade
	 */
	public void setExibeTipoNecessidade(boolean exibeTipoNecessidade) {
		this.exibeTipoNecessidade = exibeTipoNecessidade;
	}

	/** Indica se dever� exibir a sele��o do ano-per�odo no formul�rio de gera��o do relat�rio. 
	 * @return
	 */
	public boolean isOcultaAnoPeriodo() {
		return ocultaAnoPeriodo;
	}

	/** Seta se dever� exibir a sele��o do ano-per�odo no formul�rio de gera��o do relat�rio. 
	 * @param ocultaAnoPeriodo
	 */
	public void setOcultaAnoPeriodo(boolean ocultaAnoPeriodo) {
		this.ocultaAnoPeriodo = ocultaAnoPeriodo;
	}
}
