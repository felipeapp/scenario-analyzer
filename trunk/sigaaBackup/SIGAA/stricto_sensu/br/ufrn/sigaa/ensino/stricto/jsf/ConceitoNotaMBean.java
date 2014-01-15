/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 26/03/2007
 *
 */
package br.ufrn.sigaa.ensino.stricto.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.jsf.ConsolidacaoMBean;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;

/**
 * MBean para as operações de conceitos para stricto sensu
 * 
 * @author Andre M Dantas
 *
 */
@Component("conceitoNota") @Scope("request")
public class ConceitoNotaMBean extends SigaaAbstractController<ConceitoNota> {

	public ConceitoNotaMBean() {
		obj = new ConceitoNota();
	}

	/**
	 * Método que traz uma Collection de SelectItem para ser usada
	 * como combobox de objetos ConceitoNota.
	 */
	public Collection<SelectItem> getAllCombo() {
		return getAll(ConceitoNota.class, "id", "conceito");
	}

	/**
	 * Método que traz uma Collection de SelectItem para ser usada
	 * como combobox de objetos ConceitoNota.
	 */
	public Collection<SelectItem> getOrderedCombo() throws DAOException {
		Collection<ConceitoNota> conceitos = (new ConsolidacaoMBean()).getConceitos();
		return toSelectItems(conceitos, "valor", "conceito");
	}

}
