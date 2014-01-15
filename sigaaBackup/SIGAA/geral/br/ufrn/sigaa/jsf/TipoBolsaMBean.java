/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '15/05/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.TipoBolsa;

/**
 * Representa os tipos de bolsa da UFRN
 *
 * @author Victor Hugo
 *
 */
public class TipoBolsaMBean extends SigaaAbstractController<TipoBolsa> {

	public TipoBolsaMBean() {
		obj = new TipoBolsa();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAllAtivo(TipoBolsa.class, "id", "descricao");
	}

}
