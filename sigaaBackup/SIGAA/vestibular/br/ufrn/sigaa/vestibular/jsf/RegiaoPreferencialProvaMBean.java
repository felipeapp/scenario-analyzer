/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/08/2008
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.vestibular.dominio.RegiaoPreferencialProva;

/** Controller responsável pelas operações sobre Região Preferência de Prova.
 * @author Édipo Elder F. Melo
 *
 */
@Component("regiaoPreferencialProva")
@Scope("session")
public class RegiaoPreferencialProvaMBean extends
		SigaaAbstractController<RegiaoPreferencialProva> {

	/** Construtor padrão. */
	public RegiaoPreferencialProvaMBean() {
		obj = new RegiaoPreferencialProva();
	}

	/** Verifica os papéis: VESTIBULAR.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#checkChangeRole()
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
	}

	/**
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		Collection<SelectItem> combo = new ArrayList<SelectItem>();
		for (RegiaoPreferencialProva regiao : getAll()) {
			combo.add(new SelectItem(regiao.getId(), regiao.getDenominacao()));
		}
		return combo;
	}
}
