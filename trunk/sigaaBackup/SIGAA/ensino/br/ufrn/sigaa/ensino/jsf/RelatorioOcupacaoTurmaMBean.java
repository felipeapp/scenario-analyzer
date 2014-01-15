/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 06/06/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.RelatorioOcupacaoTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;

/**
 * MBean para geração de relatórios de ocupação de turma.
 * @author leonardo
 *
 */
@Component("relatorioOcupacaoTurma")
@Scope("request")
public class RelatorioOcupacaoTurmaMBean extends SigaaAbstractController {

	public final String JSP_BUSCA_COMPONENTE = "/graduacao/relatorios/ocupacao_turma/busca_componente.jsp";
	public final String JSP_RELATORIO = "/graduacao/relatorios/ocupacao_turma/relatorio.jsp";

	private ComponenteCurricular componente;

	private int ano, periodo;

	private Map relatorio;

	public RelatorioOcupacaoTurmaMBean(){
		init();
	}

	private void init() {
		componente = new ComponenteCurricular();

		try {
			ano = getCalendarioVigente().getAno();
			periodo = getCalendarioVigente().getPeriodo();
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErroPadrao();
		}
	}

	public String iniciar() throws SegurancaException{
		checkRole(new int[]{SigaaPapeis.DAE});

		return forward(JSP_BUSCA_COMPONENTE);
	}

	public String gerarRelatorio() throws DAOException {
//		String param = getParameter("paramBusca");
//		if (param == null) {
//			addMensagemErro("Selecione um tipo de busca e digite o parâmetro de busca");
//			return null;
//		}

		RelatorioOcupacaoTurmaDao dao = getDAO(RelatorioOcupacaoTurmaDao.class);
//		int id;


//		if("codigo".equalsIgnoreCase(param)){
//			ComponenteCurricularDao daoC = (ComponenteCurricularDao) getDAO(ComponenteCurricularDao.class);
//			id = daoC.findIdByCodigo(componente.getCodigo(), -1, getNivelEnsino());
//			relatorio = dao.findOcupacaoTurmasByComponente(id, ano, periodo);
//		}else if("nome".equalsIgnoreCase(param)){
			if(componente.getId() > 0 && ano > 0 && periodo > 0)
				relatorio = dao.findOcupacaoTurmasByComponente(componente.getId(), ano, periodo);
			else {
				addMensagemErro("Informe o nome e aguarde a lista ser carregada.");
				return null;
			}
//		}

		return forward(JSP_RELATORIO);
	}

	public ComponenteCurricular getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricular componente) {
		this.componente = componente;
	}

	public int getAno() {
		return ano;
	}

	public void setAno(int ano) {
		this.ano = ano;
	}

	public int getPeriodo() {
		return periodo;
	}

	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}

	public Map getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Map relatorio) {
		this.relatorio = relatorio;
	}
}
