/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * MBean respons�vel por gerar o relat�rio sobre o question�rio utilizado no
 * cadastro �nico
 * 
 * @author Henrique Andr�
 *
 */
@Component("relQuestionarioCadUnico") @Scope("request")
public class RelatorioQuestionarioCadastroUnicoMBean extends SigaaAbstractController<RelatorioQuestionarioCadastroUnico> {

	private List<RelatorioQuestionarioCadastroUnico> espectro;
	
	/**
	 * Inicia o relat�rio
	 * <br/><br/>
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
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
