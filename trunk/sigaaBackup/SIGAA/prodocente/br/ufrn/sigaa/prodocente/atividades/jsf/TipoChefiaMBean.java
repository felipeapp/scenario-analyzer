/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoChefia;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoChefiaMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoChefia> {
	public TipoChefiaMBean() {
		obj = new TipoChefia();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoChefia.class, "id", "descricao");
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		super.cadastrar();
		return listar();
	}
	
	@Override
	protected void afterCadastrar() {
		obj = new TipoChefia();
	}
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	@Override
	public String getListPage() {
		return "/prodocente/atividades/TipoChefia/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
	
}