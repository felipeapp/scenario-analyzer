/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/05/2009
 *
 */	
package br.ufrn.sigaa.assistencia.jsf;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.dao.sae.CadastroUnicoBolsaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.relatorio.dominio.RelatorioQuestionarioCadastroUnico;

/**
 * MBean responsável por gerar o relatório sobre o questionário utilizado no
 * cadastro único
 * 
 * @author Henrique André
 *
 */
@Component("relQuestionarioCadUnico") @Scope("request")
public class RelatorioQuestionarioCadastroUnicoMBean extends SigaaAbstractController<RelatorioQuestionarioCadastroUnico> {

	private List<RelatorioQuestionarioCadastroUnico> espectro;
	
	/**
	 * Inicia o relatório
	 * <br/><br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul><li> /sigaa.war/sae/menu.jsp </ul>
	 * @return
	 */
	public String iniciar() {
		CadastroUnicoBolsaDao dao = getDAO(CadastroUnicoBolsaDao.class);
		espectro = dao.contabilizarRespostas();
		
		return forward("/sae/relatorios/relatorio_resposta_cadastro_unico.jsp");
	}

	public List<RelatorioQuestionarioCadastroUnico> getEspectro() {
		return espectro;
	}

	public void setEspectro(List<RelatorioQuestionarioCadastroUnico> espectro) {
		this.espectro = espectro;
	}

}
