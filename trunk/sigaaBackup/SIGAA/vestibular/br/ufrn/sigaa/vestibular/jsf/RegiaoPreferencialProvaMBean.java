/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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

/** Controller respons�vel pelas opera��es sobre Regi�o Prefer�ncia de Prova.
 * @author �dipo Elder F. Melo
 *
 */
@Component("regiaoPreferencialProva")
@Scope("session")
public class RegiaoPreferencialProvaMBean extends
		SigaaAbstractController<RegiaoPreferencialProva> {

	/** Construtor padr�o. */
	public RegiaoPreferencialProvaMBean() {
		obj = new RegiaoPreferencialProva();
	}

	/** Verifica os pap�is: VESTIBULAR.
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
