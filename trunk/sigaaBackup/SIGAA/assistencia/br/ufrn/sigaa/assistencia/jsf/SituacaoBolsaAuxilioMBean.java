package br.ufrn.sigaa.assistencia.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;

@Component @Scope("request")
public class SituacaoBolsaAuxilioMBean extends SigaaAbstractController<SituacaoBolsaAuxilio> {

	public SituacaoBolsaAuxilioMBean() {
		obj = new SituacaoBolsaAuxilio();
	}
	
}