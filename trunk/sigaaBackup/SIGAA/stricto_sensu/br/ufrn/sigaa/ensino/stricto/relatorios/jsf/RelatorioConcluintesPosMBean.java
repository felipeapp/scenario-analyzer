/* 
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
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
 * MBean respons�vel por chamar o Jasper do relat�rio
 * de concluintes da p�s-gradua��o stricto sensu
 * 
 * @author leonardo
 *
 */
@Component("relatorioConcluintesPosBean") @Scope("request")
public class RelatorioConcluintesPosMBean extends SigaaAbstractController<RelatorioConcluintesPosMBean> {

	/** Ano que ser� gerado o relat�rio */
	private Integer ano;
	/** Nome do relat�rio */
	private String nomeRelatorio;

	/** Construtor da Classe */
	public RelatorioConcluintesPosMBean() {

	}
	
	/**
	 * Inicializa o relat�rio de concluintes.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
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
	 * Gera o relat�rio de concluintes.
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/relatorios/form_concluintes.jsp</li>
	 * </ul>
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
        	
        	RelatoriosConcluintesDao dao = getDAO(RelatoriosConcluintesDao.class);
        	List<RelatorioConcluintePos> relatorio = dao.findConcluintesPosGraduacao(ano);
        	
        	if( isEmpty(relatorio) ){
        		addMensagemErro("N�o h� concluintes no ano indicado.");
        		return null;
        	}
        	
        	 // Popular par�metros do relat�rio
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("ano", ano );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
	        
	        // Preencher relat�rio
        	JRDataSource jrds = new JRBeanCollectionDataSource(relatorio);
    		JasperPrint prt = JasperFillManager.fillReport( getReportSIGAA(nomeRelatorio + ".jasper"), parametros, jrds);
        	
        	/*
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper"), parametros, con );
            */
            
            // Exportar relat�rio de acordo com o formato escolhido
            String nomeArquivo = nomeRelatorio+".pdf";
            JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
            FacesContext.getCurrentInstance().responseComplete();
            
            
        } catch (Exception e) {
            e.printStackTrace();
            notifyError(e);
            addMensagemErro("Ocorreu um erro durante a gera��o deste relat�rio. Por favor, contacte o suporte atrav�s do \"Abrir Chamado\"");
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
