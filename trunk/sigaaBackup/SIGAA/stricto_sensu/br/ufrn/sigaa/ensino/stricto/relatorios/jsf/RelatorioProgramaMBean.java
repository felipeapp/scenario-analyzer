/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;

/**
 * MBean responsável pela geração dos relatórios 
 * referente aos Programas.
 * 
 * @author Jean
 */
@Scope("request")
@Component("relatorioPrograma")
public class RelatorioProgramaMBean extends RelatoriosStrictoMBean {

	private Integer ano;
	private Integer periodo;

	/** Dados do relatório. */
	private List<HashMap<String,Object>> listaPrograma = new ArrayList<HashMap<String,Object>>();

	/** 
	 * Inicia o relatório dos Programas que não Realizaram Matricula online.
     * 
     * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/relatorios.jsp

	 * @return
	 * @throws DAOException
	 */
	public String iniciarProgramaNMatriculaOnline() throws DAOException {
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		return forward("/stricto/relatorios/form_prog_n_matricula_online.jsf");
	}
	
	/**
	 * Gera um relatório com os Programas que não fizeram Matricula on-line, no ano e período
	 * informados. 
	 * 
	 * JSP: form_prog_n_matricula_online.jsp
	 * @return
	 * @throws DAOException
	 */
	public String relatorioProgramasNFezMatriculaOnline() throws DAOException{
		validarCampos();

		if (hasErrors())
			return forward("/stricto/relatorios/form_prog_n_matricula_online.jsf");
		
		CursoDao dao = getDAO(CursoDao.class);
		listaPrograma = dao.relatorioProgramasNFezMatriculaOnline(ano, periodo);
		if (listaPrograma.size() == 0) {
			addMensagemErro("Não foi encotrado nenhum registro.");
			forward("/stricto/relatorios/form_prog_n_matricula_online.jsf");
		}
		dao.close();
		return forward("/stricto/relatorios/relatorio_prog_n_matricula_online.jsf");
	}

	/** 
	 * Inicia o relatório dos Programas que não Realizaram Matricula online.
     * 
     * Chamado por /SIGAA/app/sigaa.ear/sigaa.war/stricto/menus/relatorios.jsp

	 * @return
	 * @throws DAOException
	 */
	public String iniciarProgramaNProcessoSeletivoOnline() throws DAOException {
		ano = getAnoAtual();
		periodo = getPeriodoAtual();
		return forward("/stricto/relatorios/form_prog_n_proc_seletivo.jsf");
	}

	/**
	 * Gera um relatório com os Programas que não fizeram Matricula on-line, no ano e período
	 * informados. 
	 * 
	 * JSP: form_prog_n_proc_seletivo.jsp
	 * @return
	 * @throws DAOException
	 */
	public String relatorioProgramasNFezProcessoSeletivo() throws DAOException{
		validarCampos();

		if (hasErrors())
			return forward("/stricto/relatorios/form_prog_n_proc_seletivo.jsf");
		
		CursoDao dao = getDAO(CursoDao.class);
		listaPrograma = dao.relatorioProgramasNFezProcessoSeletivo(ano, periodo);
		if (listaPrograma.size() == 0) {
			addMensagemErro("Não foi encotrado nenhum registro.");
			forward("/stricto/relatorios/form_prog_n_proc_seletivo.jsf");
		}
		dao.close();
		return forward("/stricto/relatorios/relatorio_prog_n_proc_seletivo.jsf");
	}

	/**
	 * Serve para validar o ano e período informado pelo usuário se estão dentro do permitido.
	 * 
	 * JSP: Não invocado por JSP.
	 */
	private void validarCampos() {
		if (ano == null || ano == 0 || ano > getAnoAtual()) {
			addMensagemErro("Ano: Campo obrigatório não informado.");
		}
		if (periodo == null || periodo == 0 || periodo == 3 || periodo == 4 ) {
			addMensagemErro("Período: Campo obrigatório não informado.");
		}
	}
	
	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public List<HashMap<String, Object>> getListaPrograma() {
		return listaPrograma;
	}

	public void setListaPrograma(List<HashMap<String, Object>> listaPrograma) {
		this.listaPrograma = listaPrograma;
	}
	
}