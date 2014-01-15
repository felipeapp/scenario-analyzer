/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/10/2008
 * 
 */
package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.EstagioDesenvolvimentoInvencao;

/**
 * MBean para CRUD dos est�gios de desenvolvimento de uma inven��o
 * 
 * @author Leonardo Campos
 *
 */
@Component("estagioInvencaoBean") @Scope("request")
public class EstagioDesenvolvimentoInvencaoMBean extends SigaaAbstractController<EstagioDesenvolvimentoInvencao> {

	/**
	 * Construtor padr�o
	 */
	public EstagioDesenvolvimentoInvencaoMBean() {
		obj = new EstagioDesenvolvimentoInvencao();
	}
	
	public Collection<SelectItem> getAllCombo() throws DAOException {
		return toSelectItems(getGenericDAO().findAll(EstagioDesenvolvimentoInvencao.class), "id", "descricao");
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/EstagioDesenvolvimentoInvencao/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/EstagioDesenvolvimentoInvencao/lista.jsf";
	}
}