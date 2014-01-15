/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2008
 *
 */
package br.ufrn.sigaa.extensao.relatorio.jsf;

import java.sql.Connection;
import java.util.HashMap;

import javax.faces.context.FacesContext;

import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;

/*******************************************************************************
 * Managed Bean responsável por chamar o Jasper e passar os parâmetros para o
 * relatório de alunos em atividades de extensão, monitoria e pesquisa.
 * 
 * @author leonardo
 * 
 ******************************************************************************/
@Component("relatorioAlunosExtensaoMonitoriaPesquisaBean")
@Scope("request")
public class RelatorioAlunosExtensaoMonitoriaPesquisaMBean extends SigaaAbstractController<Object> {

	//Utilizado para armazenar informação inserida em tela de busca 
	private Integer ano;
	//Utilizado para armazenar informação inserida em tela de busca
	private Curso curso;
	private boolean coordenador = false;

	public RelatorioAlunosExtensaoMonitoriaPesquisaMBean() {
		curso = new Curso();
	}

	public String iniciar() {
		ano = CalendarUtils.getAnoAtual();
		return forward("/extensao/Relatorios/alunos_extensao_monitoria_pesquisa.jsf");
	}

	public String iniciarCoordenador() {
		ano = CalendarUtils.getAnoAtual();
		coordenador = true;
		curso = getCursoAtualCoordenacao();
		return forward("/extensao/Relatorios/alunos_extensao_monitoria_pesquisa.jsf");
	}

	public String gerarRelatorio() throws DAOException {

		if (ano == null || (ano != null && ano <= 0)) {
			addMensagemErro("Informe um ano válido.");
			return null;
		}

		if (curso == null || (curso != null && curso.getId() <= 0)) {
			addMensagemErro("Selecione um curso.");
			return null;
		}

		// Gerar relatório
		Connection con = null;
		try {

			// Popular parâmetros do relatório
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA("trf6186_Pesq_Monit_Extens.jasper"));
			parametros.put("ano", ano);
			parametros.put("curso", curso.getId());			
			parametros.put("subSistema", getSubSistema().getNome());
			parametros.put("subSistemaLink", getSubSistema().getLink());

			// Preencher relatório
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil
					.getReportSIGAA("trf6186_Pesq_Monit_Extens.jasper"),
					parametros, con);

			// Exportar relatório de acordo com o formato escolhido
			String nomeArquivo = "Alunos_Pesq_Monit_Extens.pdf";
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			addMensagemErro("Ocorreu um erro durante a geração deste relatório. Por favor, contacte o suporte através do \"Abrir Chamado\"");
			notifyError(e);
			return null;
		} finally {
			Database.getInstance().close(con);
		}

		return null;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public boolean isCoordenador() {
		return coordenador;
	}

	public void setCoordenador(boolean coordenador) {
		this.coordenador = coordenador;
	}

}
