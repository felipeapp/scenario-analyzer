package br.ufrn.sigaa.estagio.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.estagio.dominio.StatusConvenioEstagio;

/** Controller responsável por controlar algumas informações exibidas no módulo de Convênios de Estágios.
 * @author Édipo Elder F. de Melo
 *
 */
@Component("moduloConvenioEstagioMBean") @Scope("request")
public class ModuloConvenioEstagioMBean extends SigaaAbstractController<Object> {

	private Integer qtdConveniosPendentesAnalise = null;
	/** Retorna quantidade de convênios pendente de análise.
	 * @return
	 */
	public int getQtdConveniosPendentesAnalise() {
		if (qtdConveniosPendentesAnalise == null) {
			GenericDAO dao = getGenericDAO();
			Integer status[] = {StatusConvenioEstagio.EM_ANALISE, StatusConvenioEstagio.SUBMETIDO};
			qtdConveniosPendentesAnalise = dao.count("select count(*) from estagio.convenio_estagio where status in " + UFRNUtils.gerarStringIn(status));
		}
		return qtdConveniosPendentesAnalise;
	}
}
