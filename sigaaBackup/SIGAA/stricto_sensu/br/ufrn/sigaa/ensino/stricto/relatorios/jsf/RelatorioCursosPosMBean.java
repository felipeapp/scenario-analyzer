/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on May 27, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import java.util.HashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.comum.dominio.Sistema;

/**
 *
 * @author Victor Hugo
 */
@Component("relatorioCursosPos") @Scope("session")
public class RelatorioCursosPosMBean extends RelatoriosStrictoMBean {

	public static final String NOME_RELATORIO = "trf6365_Stricto_Sensu_ConceitoCAPES.jasper";

	/**
	 * Popula a p�gina inicial da gera��o do Relat�rio
	 * 
	 * Chamado por:
	 * /stricto/menus/relatorios.jsp
	 * 
	 * @return
	 */
	public String iniciar() {
        titulo = "Cursos de P�s-Gradua��o";
//        relat�rio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO);
        origemDados = Sistema.SIGAA;
        tipoUnidade = CENTRO;
        //tipoRelatorio = CURSOS_DE_POS;
        return forward("/stricto/relatorios/cursos_pos.jsp");
    }

	/**
	 * Popula o relat�rio Jasper
	 * 
	 * Chamado por:
	 * /stricto/relatorios/cursos_pos.jsp
	 */
	@Override
	public String gerarRelatorio() throws DAOException {
		relatorio = JasperReportsUtil.getReportSIGAA(NOME_RELATORIO);
		parametros = new HashMap<String, Object>();
		parametros.put("centro", unidade.getId());
		parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());

		return super.gerarRelatorio();
	}

}
