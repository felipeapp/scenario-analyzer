/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 29/01/2009
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.JasperReportsUtil.getReportSIGAA;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.util.HashMap;
import java.util.List;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.graduacao.RelatoriosConcluintesDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * MBean responsável por chamar o Jasper do relatório
 * de concluintes da pós-graduação stricto sensu
 * 
 * @author leonardo
 *
 */
@Component("relatorioConcluintesPosBean") @Scope("request")
public class RelatorioConcluintesPosMBean extends SigaaAbstractController<RelatorioConcluintesPosMBean> {

	/** Ano que será gerado o relatório */
	private Integer ano;
	/** Nome do relatório */
	private String nomeRelatorio;

	/** Construtor da Classe */
	public RelatorioConcluintesPosMBean() {

	}
	
	/**
	 * Inicializa o relatório de concluintes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.PPG);
		ano = getCalendarioVigente().getAno();
		nomeRelatorio = "trf7997_Concluintes_Pos";
		return forward("/stricto/relatorios/form_concluintes.jsf");
	}
	
	/**
	 * Gera o relatório de concluintes.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/relatorios/form_concluintes.jsp</li>
	 * </ul>
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
        	
        	RelatoriosConcluintesDao dao = getDAO(RelatoriosConcluintesDao.class);
        	List<RelatorioConcluintePos> relatorio = dao.findConcluintesPosGraduacao(ano);
        	
        	if( isEmpty(relatorio) ){
        		addMensagemErro("Não há concluintes no ano indicado.");
        		return null;
        	}
        	
        	 // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("ano", ano );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
	        
	        // Preencher relatório
        	JRDataSource jrds = new JRBeanCollectionDataSource(relatorio);
    		JasperPrint prt = JasperFillManager.fillReport( getReportSIGAA(nomeRelatorio + ".jasper"), parametros, jrds);
        	
        	/*
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper"), parametros, con );
            */
            
            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = nomeRelatorio+".pdf";
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
            FacesContext.getCurrentInstance().responseComplete();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
            return null;
        } finally {
            Database.getInstance().close(con);
        }

        return null;
	}
	
	/**
	 * Valida os dados
	 * @return
	 */
	private boolean validate() {
        ListaMensagens erros = new ListaMensagens();

      	ValidatorUtil.validateRequired(ano, "Ano", erros);

        addMensagens(erros);
        return erros.isEmpty();
    }
	
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

}
