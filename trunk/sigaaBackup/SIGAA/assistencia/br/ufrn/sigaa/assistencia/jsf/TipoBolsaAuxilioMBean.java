package br.ufrn.sigaa.assistencia.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;

@Component @Scope("request")
public class TipoBolsaAuxilioMBean extends SigaaAbstractController<TipoBolsaAuxilio> {

	public TipoBolsaAuxilioMBean() {
		clear();
	}
	
	private void clear() {
		obj = new TipoBolsaAuxilio();
	}

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return getAll(TipoBolsaAuxilio.class, "id", "denominacao");
	}
	
}