/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '21/12/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.Collection;
import java.util.HashMap;

import javax.faces.model.SelectItem;

import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 * MBean para entidade de Forma��o de RH
 *
 * @author Andre
 *
 */
public class FormacaoMBean extends SigaaAbstractController<Formacao> {
	public FormacaoMBean() {
		obj = new Formacao();
	}

	@Override
	public Collection<SelectItem> getAllCombo() {

		/*
		 * os valores da tabela rh.formacao est�o duplicados
		 * O c�digo a baixo elimina os valores duplicados para exibir no select apenas um
		 */
		Collection<Formacao> formacoes = getAllObj(Formacao.class);
		HashMap<String, Formacao> map = new HashMap<String, Formacao>();
		for( Formacao f : formacoes ){
			if(f.getId() != 0 && !map.containsKey(f.getDenominacao()) )
				map.put(f.getDenominacao(), f);
		}
		return toSelectItems(map.values(), "id", "denominacao");
		//return getAll(Formacao.class, "id", "denominacao");
	}
}
