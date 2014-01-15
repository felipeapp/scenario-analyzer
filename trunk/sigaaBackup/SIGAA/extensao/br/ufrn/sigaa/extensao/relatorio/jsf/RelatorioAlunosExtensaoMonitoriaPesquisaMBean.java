/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Managed Bean respons�vel por chamar o Jasper e passar os par�metros para o
 * relat�rio de alunos em atividades de extens�o, monitoria e pesquisa.
 * 
 * @author leonardo
 * 
 ******************************************************************************/
@Component("relatorioAlunosExtensaoMonitoriaPesquisaBean")
@Scope("request")
public class RelatorioAlunosExtensaoMonitoriaPesquisaMBean extends SigaaAbstractController<Object> {

	//Utilizado para armazenar informa��o inserida em tela de busca 
	private Integer ano;
	//Utilizado para armazenar informa��o inserida em tela de busca
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
			addMensagemErro("Informe um ano v�lido.");
			return null;
		}

		if (curso == null || (curso != null && curso.getId() <= 0)) {
			addMensagemErro("Selecione um curso.");
			return null;
		}

		// Gerar relat�rio
		Connection con = null;
		try {

			// Popular par�metros do relat�rio
			HashMap<String, Object> parametros = new HashMap<String, Object>();
			parametros.put("SUBREPORT_DIR", JasperReportsUtil.getReportSIGAA("trf6186_Pesq_Monit_Extens.jasper"));
			parametros.put("ano", ano);
			parametros.put("curso", curso.getId());			
			parametros.put("subSistema", getSubSistema().getNome());
			parametros.put("subSistemaLink", getSubSistema().getLink());

			// Preencher relat�rio
			con = Database.getInstance().getSigaaConnection();
			JasperPrint prt = JasperFillManager.fillReport(JasperReportsUtil
					.getReportSIGAA("trf6186_Pesq_Monit_Extens.jasper"),
					parametros, con);

			// Exportar relat�rio de acordo com o formato escolhido
			String nomeArquivo = "Alunos_Pesq_Monit_Extens.pdf";
			JasperReportsUtil.exportar(prt, nomeArquivo, getCurrentRequest(), getCurrentResponse(), "pdf");
			FacesContext.getCurrentInstance().responseComplete();

		} catch (Exception e) {
			addMensagemErro("Ocorreu um erro durante a gera��o deste relat�rio. Por favor, contacte o suporte atrav�s do \"Abrir Chamado\"");
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
