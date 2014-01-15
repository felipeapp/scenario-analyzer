/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2008
 *
 */
package br.ufrn.sigaa.ensino.tecnico.jsf;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * Managed Bean responsável por chamar o Jasper e passar os parâmetros
 * para o relatório quantitativo de alunos do técnico de música por cidade.
 * 
 * @author leonardo
 *
 */
@SuppressWarnings("unchecked")
@Component("relatorioQuantAlunosCidadeBean") @Scope("request")
public class RelatorioQuantitativoAlunosPorCidadeMBean extends
		SigaaAbstractController {

	/**
	 * Construtor.
	 */
	public RelatorioQuantitativoAlunosPorCidadeMBean() {
	}
	
	/**
	 * Responsável por gerar o Relátorio Quantitativo de Alunos do Técnico de Música por Cidade.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public String gerarRelatorio() throws DAOException {

        // Gerar relatório
        Connection con = null;
        try {
        	
            // Popular parâmetros do relatório
            HashMap<String, Object> parametros = new HashMap<String, Object>();
            parametros.put("municipioID", -1 );
            parametros.put("subSistema", getSubSistema().getNome());
	        parametros.put("subSistemaLink", getSubSistema().getLink());
	        parametros.put("unidade", getUsuarioLogado().getVinculoAtivo().getUnidade().getId());
	        
            // Preencher relatório
            con = Database.getInstance().getSigaaConnection();
            JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil.getReportSIGAA("trf10177_QtdAlunoCursoCidade_CursoTecnicoMusica.jasper"), parametros, con );

            // Exportar relatório de acordo com o formato escolhido
            String nomeArquivo = "QuantAlunosCidadeTecnicoMusica.pdf";
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

}
