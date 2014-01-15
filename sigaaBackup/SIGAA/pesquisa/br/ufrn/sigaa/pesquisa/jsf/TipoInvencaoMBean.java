/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/07/2008
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.TipoInvencao;

/**
 * MBean para CRUD dos tipos de invenção
 * 
 * @author leonardo
 *
 */
@Component("tipoInvencao") @Scope("request")
public class TipoInvencaoMBean extends SigaaAbstractController<TipoInvencao>{

	public TipoInvencaoMBean() {
		obj = new TipoInvencao();
	}
	
	public Collection<SelectItem> getCategoriasCombo(){
		return toSelectItems(TipoInvencao.getCategorias());
	}
	
	public Collection<SelectItem> getAllCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(TipoInvencao.class), "id", "descricao");
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/TipoInvencao/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/TipoInvencao/lista.jsf";
	}
	
}
