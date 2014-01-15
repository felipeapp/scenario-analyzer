/* 
 * Superintendência de Informática - Diretoria de Sistemas
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
 * MBean responsável pelas consultas e geração de alguns relatórios em jasper reports.
 * 
 * @author leonardo
 *
 */
public class RelatoriosJasperMBean extends SigaaAbstractController<Object> {

	/** Departamento ao qual se restringe os dados do relatório. */
	private Unidade departamento;
	/** Ano ao qual se restringe os dados do relatório. */
    private Integer ano;
    /** Período ao qual se restringe os dados do relatório. */
    private Integer periodo;
    /** Formato do relatório (PDF, HTML, Excel). */
    private String formato;

    // Detalhes da exibição do formulário
    // (dependem de cada tipo de formulário)
    /** Título do relatório. */
    private String titulo;
    /** Nome do arquivo Jasper do relatório. */
    private String relatorio;

    /** Indica se deverá exibir a seleção do departamento no formulário de geração do relatório. */
    private boolean exibeDepartamento;
    /** Indica se deverá exibir a seleção do centro no formulário de geração do relatório. */
    private boolean exibeCentro;
    /** Indica se deverá exibir a seleção do tipo de componente curricular no formulário de geração do relatório. */
    private boolean exibeTipoComponente;
    /** Indica se deverá exibir a seleção do tipo de necessidade especial no formulário de geração do relatório. */
    private boolean exibeTipoNecessidade;
    /** Indica se deverá exibir a seleção do ano-período no formulário de geração do relatório. */
    private boolean ocultaAnoPeriodo;
    
    /** Tipo de componente curricular ao qual se restringe os dados do relatório. */
    private TipoComponenteCurricular tipoComponente;
    /** Tipo de necessidade especial ao qual se restringe os dados do relatório. */
    private TipoNecessidadeEspecial tipoNecessidade;

	/** Construtor padrão. */
	public RelatoriosJasperMBean(){
    	clear();
    }

	/** Reseta os valores dos parâmetros utilizados na geração do relatório. */
	private void clear() {
		departamento = new Unidade();
		tipoComponente = new TipoComponenteCurricular();
		tipoNecessidade = new TipoNecessidadeEspecial();
		formato = "pdf";
	}
	
	/*
     * METODOS DE INICIALIZACAO
     */
	
	/** Inicia a geração do relatório sintético de turmas não consolidadas.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarTurmasNaoConsolidadasSintetico(){
		titulo = "Relatório Sintético de Turmas Não Consolidadas";
		relatorio = "TurmasNaoConsolidadasSintetico";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeDepartamento = false;
		exibeCentro = true;
		
		return iniciar();
	}
	
	/** Inicia a geração do relatório de docentes que não cosolidaram as turmas.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarDocentesQueNaoConsolidaramTurmas(){
		titulo = "Relatório de Docentes que não consolidaram as turmas";
		relatorio = "DocentesQueNaoConsolidaramTurmas";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeDepartamento = true;
		
		return iniciar();
	}
	
	/** Inicia a geração do relatório de alunos com todos os componentes curriculares reprovados em um período letivo.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarAlunosTodosComp_Reprov(){
		titulo = "Relatório de alunos com todos os componentes curriculares reprovados em um período letivo";
		relatorio = "trf6948_AlunosTodosComp_Reprov";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeCentro = true;
		
		return iniciar();
	}
	
	/** Inicia a geração do relatório de alunos com todos os componentes curriculares trancados em um período letivo.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarAlunosTodosCompTranc(){
		titulo = "Relatório de alunos com todos os componentes curriculares trancados em um período letivo";
		relatorio = "trf6948_AlunosTodosCompTranc";
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		exibeCentro = true;
		exibeTipoComponente = true;
		
		return iniciar();
	}
	
	/** Inicia a geração do relatório de alunos com necessidades especiais.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 *             /SIGAA/app/sigaa.ear/sigaa.war/portais/rh_plan/abas/graduacao.jsp
	 * @return
	 */
	public String iniciarNecessidadesEspeciais(){
		titulo = "Relatório de alunos com necessidades especiais";
		relatorio = "trf8011_AlunosNec_Especiais";
		exibeTipoNecessidade = true;
		ocultaAnoPeriodo = true;
		
		return iniciar();
	}
	
	/** Inicia o relatório de apostilamentos.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarApostilamentos() {
	   titulo = "Relatório de Apostilamentos";
	   relatorio = "trf4951_Apostilamentos";
	   ano = getCalendarioVigente().getAno();
	   periodo = getCalendarioVigente().getPeriodo();
	   exibeCentro = true;
	   
	   return iniciar();
	}
	
	/** Inicia o relatório de estruturas curriculares.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_cdp.jsp
	 * @return
	 */
	public String iniciarEstruturasCurriculares() {
		   titulo = "Relatório de Estruturas Curriculares";
		   relatorio = "trf6789_EstrutCurricul_Centro";
		   ano = getCalendarioVigente().getAno();
		   periodo = getCalendarioVigente().getPeriodo();
		   exibeCentro = true;
		   ocultaAnoPeriodo = true;
		   
		   return iniciar();
	 }
	
	/** Inicia o relatório quantitativo de orientações acadêmicas por curso.
	 * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/graduacao/menus/relatorios_dae.jsp
	 * @return
	 */
	public String iniciarQuantitativoOrientacoesAcademicas() {
		   titulo = "Relatório Quantitativo de Orientações Acadêmicas por Curso";
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
     * Método para iniciar a geração do relatório, compartilhado por todos os tipos de relatório.
     * Redireciona para a pagina do formulário de parâmetros do relatório.
     *
     * @return
     */
    private String iniciar() {
        clear();
        return forward("/graduacao/relatorios/form_relatorios.jsp");
    }
    
    /**
     * Realizar a geração do relatório, de acordo com os critérios selecionados
     * Chamado por /graduacao/relatorios/form_relatorios.jsp
     * @return
     * @throws DAOException
     */
    public String gerarRelatorio() throws DAOException {

    	// Validar campos do formulário
        if ( !validate() ) {
            return null;
        }

        // Gerar relatório
        Connection con = null;
        try {

            // Popular parâmetros do relatório
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

            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(relatorio+".jasper"), parametros, con );

            if (prt.getPages().size() == 0) {
				addMensagemWarning("O relatório não possui dados.");
				return null;
			}
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "Relatorio"+relatorio+"."+formato;
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
     * Validar dados do formulário
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
        	ValidatorUtil.validaInt(periodo, "Período", erros);
        }

        addMensagens(erros);
        return erros.isEmpty();
    }

	/** Retorna o departamento ao qual se restringe os dados do relatório. 
	 * @return
	 */
	public Unidade getDepartamento() {
		return departamento;
	}

	/** Seta o departamento ao qual se restringe os dados do relatório.
	 * @param departamento
	 */
	public void setDepartamento(Unidade departamento) {
		this.departamento = departamento;
	}

	/** Retorna o ano ao qual se restringe os dados do relatório. 
	 * @return
	 */
	public Integer getAno() {
		return ano;
	}

	/** Seta o ano ao qual se restringe os dados do relatório.
	 * @param ano
	 */
	public void setAno(Integer ano) {
		this.ano = ano;
	}

	/** Retorna o período ao qual se restringe os dados do relatório. 
	 * @return
	 */
	public Integer getPeriodo() {
		return periodo;
	}

	/** Seta o período ao qual se restringe os dados do relatório.
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	/** Retorna o formato do relatório (PDF, HTML, Excel). 
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/** Seta o formato do relatório (PDF, HTML, Excel).
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Retorna o título do relatório. 
	 * @return
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o título do relatório.
	 * @param titulo
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna o nome do arquivo Jasper do relatório. 
	 * @return
	 */
	public String getRelatorio() {
		return relatorio;
	}

	/** Seta o nome do arquivo Jasper do relatório.
	 * @param relatorio
	 */
	public void setRelatorio(String relatorio) {
		this.relatorio = relatorio;
	}

	/** Indica se deverá exibir a seleção do departamento no formulário de geração do relatório. 
	 * @return
	 */
	public boolean isExibeDepartamento() {
		return this.exibeDepartamento;
	}

	/** Seta se deverá exibir a seleção do departamento no formulário de geração do relatório. 
	 * @param exibeDepartamento
	 */
	public void setExibeDepartamento(boolean exibeDepartamento) {
		this.exibeDepartamento = exibeDepartamento;
	}

	/** Indica se deverá exibir a seleção do centro no formulário de geração do relatório. 
	 * @return
	 */
	public boolean isExibeCentro() {
		return exibeCentro;
	}

	/** Seta se deverá exibir a seleção do centro no formulário de geração do relatório. 
	 * @param exibeCentro
	 */
	public void setExibeCentro(boolean exibeCentro) {
		this.exibeCentro = exibeCentro;
	}

	/** Indica se deverá exibir a seleção do tipo de componente curricular no formulário de geração do relatório.  
	 * @return
	 */
	public boolean isExibeTipoComponente() {
		return exibeTipoComponente;
	}

	/** Seta se deverá exibir a seleção do tipo de componente curricular no formulário de geração do relatório. 
	 * @param exibeTipoComponente
	 */
	public void setExibeTipoComponente(boolean exibeTipoComponente) {
		this.exibeTipoComponente = exibeTipoComponente;
	}

	/** Retorna o tipo de componente curricular ao qual se restringe os dados do relatório. 
	 * @return
	 */
	public TipoComponenteCurricular getTipoComponente() {
		return tipoComponente;
	}

	/** Seta o tipo de componente curricular ao qual se restringe os dados do relatório.
	 * @param tipoComponente
	 */
	public void setTipoComponente(TipoComponenteCurricular tipoComponente) {
		this.tipoComponente = tipoComponente;
	}
	
    /** Retorna o tipo de necessidade especial ao qual se restringe os dados do relatório. 
     * @return
     */
    public TipoNecessidadeEspecial getTipoNecessidade() {
		return tipoNecessidade;
	}

    /** Seta o tipo de necessidade especial ao qual se restringe os dados do relatório.
	 * @param tipoNecessidade
	 */
	public void setTipoNecessidade(TipoNecessidadeEspecial tipoNecessidade) {
		this.tipoNecessidade = tipoNecessidade;
	}

	/** Indica se deverá exibir a seleção do tipo de necessidade especial no formulário de geração do relatório. 
	 * @return
	 */
	public boolean isExibeTipoNecessidade() {
		return exibeTipoNecessidade;
	}

	/** Seta se deverá exibir a seleção do tipo de necessidade especial no formulário de geração do relatório. 
	 * @param exibeTipoNecessidade
	 */
	public void setExibeTipoNecessidade(boolean exibeTipoNecessidade) {
		this.exibeTipoNecessidade = exibeTipoNecessidade;
	}

	/** Indica se deverá exibir a seleção do ano-período no formulário de geração do relatório. 
	 * @return
	 */
	public boolean isOcultaAnoPeriodo() {
		return ocultaAnoPeriodo;
	}

	/** Seta se deverá exibir a seleção do ano-período no formulário de geração do relatório. 
	 * @param ocultaAnoPeriodo
	 */
	public void setOcultaAnoPeriodo(boolean ocultaAnoPeriodo) {
		this.ocultaAnoPeriodo = ocultaAnoPeriodo;
	}
}
