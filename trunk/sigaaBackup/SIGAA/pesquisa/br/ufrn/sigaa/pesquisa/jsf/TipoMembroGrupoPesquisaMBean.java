/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/03/2009
 *
 */
/**
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.TipoMembroGrupoPesquisa;

/**
 * MBean para gerenciamento de tipos de membros dos grupos de pesquisa.
 * 
 * @author Leonardo
 *
 */
@Component("tipoMembroGrupoPesquisa") @Scope("request")
public class TipoMembroGrupoPesquisaMBean extends SigaaAbstractController<TipoMembroGrupoPesquisa> {

	@Override
	public Collection<SelectItem> getAllCombo() throws ArqException {
		return toSelectItems(getGenericDAO().findAll(TipoMembroGrupoPesquisa.class), "id", "descricao");
	}
}
