/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 29/04/2009
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/** Controller resposável por elaborar relatórios para o Censo do INEP
 * @author Édipo Elder F. Melo
 *
 */
@Component("relatoriosExtensaoCenso")
@Scope("request")
public class RelatoriosExtensaoCensoMBean extends SigaaAbstractController<Object> {
	
	/** Formulário de ano-base do relatório. */
	private final String JSP_ANO = "/extensao/Relatorios/form_ano.jsp";
	/** Link do Relatório do Número Total de Programas e seus Respectivos Projetos Vinculados. */
	private final String JASPER_REL_TOTAL_PROGRAMAS_PROJETOS_VINCULADOS = "trf1151_Total_Programas_por_Area_Tematica";
	/** Link do Relatório do Número Total de Projetos não-Vinculados, Público Atendido e Pessoas Envolvidas na Execução, Segundo a Área Temática. */
	private final String JASPER_REL_PROJETOS_NAO_VINCULADOS = "trf1151_Projetos_Nao_Vinculados_Segundo_Area_Tematica";
	/** Link do Relatório do Número Total de Cursos, Total de Carga Horária, Concluintes e ministrantes em Curso de Extensão Presencial(1), Segundo a área de Conhecimento CNPq. */
	private final String JASPER_REL_TOTAL_CURSOS_SEGUNDO_AREA_CONHECIMENTO = "trf1151_Total_Cursos_Segundo_Area_Conhecimento";
	/** Link do Relatório do Número Total de Eventos Desenvolvidos, por Tipo de Evento e Público Participante, Segundo Área Temática de Extensão. */
	private final String JASPER_REL_TOTAL_EVENTOS_SEGUNDO_TEMATICA_EXTENSAO = "trf1151_Total_Eventos_Segundo_Tematica_Extensao";
	
	/** Relatório do Número Total de Programas e seus Respectivos Projetos Vinculados. */
	private final int REL_TOTAL_PROGRAMAS_PROJETOS_VINCULADOS = 4;
	/** Relatório do Número Total de Projetos não-Vinculados, Público Atendido e Pessoas Envolvidas na Execução, Segundo a Área Temática. */
	private final int REL_TOTAL_PROJETOS_NAO_VINCULADOS = 5;
	/** Relatório do Número Total de Cursos, Total de Carga Horária, Concluintes e ministrantes em Curso de Extensão Presencial, Segundo a área de Conhecimento CNPq. */
	private final int REL_TOTAL_CURSOS_SEGUNDO_AREA_CONHECIMENTO = 6;
	/** Relatório do Número Total de Eventos Desenvolvidos, por Tipo de Evento e Público Participante, Segundo Área Temática de Extensão. */
	private final int REL_TOTAL_EVENTOS_SEGUNDO_TEMATICA_EXTENSAO = 7;
	
	
	/** Ano base do relatório. */
	private int ano;
	
	/** Dados do relatório. */
	private Map<Object, Object> relatorio;
	
	/** Indica qual relatório será processado. */
	private int tipoRelatorio;

	/** Indica qual relatório Jasper será processado. */
	private String relatorioJasper;
	
	/** Indica qual o formato do relatório Jasper (PDF, HTML ou Excel). */
	private String formato = "pdf";
	
	/** Indica se o formulário deve exibir a opção de escolha do formato do relatório. */
	private boolean escolheFormato = false;

	/** Retorna o relatório desejado.
	 * Chamado por /extensao/Relatorios/form_ano.jsp
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 * @throws ArqException
	 */
	public String submeterAno() throws DAOException, SegurancaException, ArqException {
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO});
		ValidatorUtil.validaInt(ano, "Ano", erros);
		if(hasErrors()){
			return null;
		}
		switch (tipoRelatorio) {
			case REL_TOTAL_PROGRAMAS_PROJETOS_VINCULADOS :
				relatorioJasper = JASPER_REL_TOTAL_PROGRAMAS_PROJETOS_VINCULADOS;
				return gerarRelatorioJasper();
			case REL_TOTAL_PROJETOS_NAO_VINCULADOS :
				relatorioJasper = JASPER_REL_PROJETOS_NAO_VINCULADOS;
				return gerarRelatorioJasper();
			case REL_TOTAL_CURSOS_SEGUNDO_AREA_CONHECIMENTO :
				relatorioJasper = JASPER_REL_TOTAL_CURSOS_SEGUNDO_AREA_CONHECIMENTO;
				return gerarRelatorioJasper();
			case REL_TOTAL_EVENTOS_SEGUNDO_TEMATICA_EXTENSAO :
				relatorioJasper = JASPER_REL_TOTAL_EVENTOS_SEGUNDO_TEMATICA_EXTENSAO;
				return gerarRelatorioJasper();
		}
		return redirectMesmaPagina();
	}
	
	/** Retorna o relatório Jasper.
	 * @return
	 * @throws DAOException
	 */
	private String gerarRelatorioJasper() throws DAOException {
        // Gerar relatorio
        Connection con = null;
        try {
            // Popular parametros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA(relatorioJasper + ".jasper") );
            parametros.put("subSistema", getSubSistema().getNome());
            parametros.put("subSistemaLink", getSubSistema().getLink());
            parametros.put("ano", ano );
            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(relatorioJasper + ".jasper"), parametros, con );
            // verifica se o relatório não é vazio
            if (prt.getPages().size() == 0) {
		addMensagemWarning("O relatório não possui dados para o ano informado.");
		return null;
            }
            // Exportar relatorio de acordo com o formato escolhido
            String nomeArquivo = relatorioJasper+"."+formato;
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
	
	/** Retorna o Relatório do Número Total de Programas e seus Respectivos Projetos Vinculados.
	 *  Chamado por /extensao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioTotalProgramasProjetosVinculados() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO});
		tipoRelatorio = REL_TOTAL_PROGRAMAS_PROJETOS_VINCULADOS;
		ano = CalendarUtils.getAnoAtual() - 1;
		escolheFormato = true;
		return forward(JSP_ANO);
	}
	
	/** Retorna o Relatório do Número Total de Projetos não-Vinculados, Público Atendido e Pessoas Envolvidas na Execução, Segundo a Área Temática. 
	 * Chamado por /extensao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioTotalProjetosNaoVinculados() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO});
		tipoRelatorio = REL_TOTAL_PROJETOS_NAO_VINCULADOS;
		ano = CalendarUtils.getAnoAtual() - 1;
		escolheFormato = true;
		return forward(JSP_ANO);
	}
	
	/** retorna o Relatório do Número Total de Cursos, Total de Carga Horária, Concluintes e ministrantes em Curso de Extensão Presencial, Segundo a área de Conhecimento CNPq.  
	 * Chamado por /extensao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioTotalCursosSegundoAreaConhecimento() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO});
		tipoRelatorio = REL_TOTAL_CURSOS_SEGUNDO_AREA_CONHECIMENTO;
		ano = CalendarUtils.getAnoAtual() - 1;
		escolheFormato = true;
		return forward(JSP_ANO);
	}
	
	/** Retorna o  Relatório do Número Total de Eventos Desenvolvidos, por Tipo de Evento e Público Participante, Segundo Área Temática de Extensão. 
	 * Chamado por /extensao/menu.jsp
	 * @return
	 * @throws SegurancaException
	 */
	public String relatorioTotalEventosSegundoTematicaExtensao() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.GESTOR_EXTENSAO});
		tipoRelatorio = REL_TOTAL_EVENTOS_SEGUNDO_TEMATICA_EXTENSAO;
		ano = CalendarUtils.getAnoAtual() - 1;
		escolheFormato = true;
		return forward(JSP_ANO);
	}
	
	/** Retorna o ano base do relatório. 
	 * @return
	 */
	public int getAno() {
		return ano;
	}

	/** Seta o ano base do relatório.
	 * @param ano
	 */
	public void setAno(int ano) {
		this.ano = ano;
	}

	/** Retorna os dados do relatório. 
	 * @return
	 */
	public Map<Object, Object> getRelatorio() {
		return relatorio;
	}

	/** Seta os dados do relatório.
	 * @param relatorio
	 */
	public void setRelatorio(Map<Object, Object> relatorio) {
		this.relatorio = relatorio;
	}

	/** Indica qual relatório será processado. 
	 * @return
	 */
	public int getTipoRelatorio() {
		return tipoRelatorio;
	}

	/** Seta qual relatório será processado.
	 * @param tipoRelatorio
	 */
	public void setTipoRelatorio(int tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

	/** Indica qual o formato do relatório Jasper (PDF, HTML ou Excel). 
	 * @return
	 */
	public String getFormato() {
		return formato;
	}

	/** Seta qual o formato do relatório Jasper (PDF, HTML ou Excel).
	 * @param formato
	 */
	public void setFormato(String formato) {
		this.formato = formato;
	}

	/** Indica se o formulário deve exibir a opção de escolha do formato do relatório. 
	 * @return
	 */
	public boolean isEscolheFormato() {
		return escolheFormato;
	}

	/** Seta se o formulário deve exibir a opção de escolha do formato do relatório.
	 * @param escolheFormato
	 */
	public void setEscolheFormato(boolean escolheFormato) {
		this.escolheFormato = escolheFormato;
	}

}
