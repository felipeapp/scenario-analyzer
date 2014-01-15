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

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAvaliacaoOrganizacaoEvento;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
/**
 	Gerado pelo CrudBuilder
**/
public class TipoAvaliacaoOrganizacaoEventoMBean extends AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoAvaliacaoOrganizacaoEvento> {
	public TipoAvaliacaoOrganizacaoEventoMBean() {
		obj = new TipoAvaliacaoOrganizacaoEvento();
	}
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoAvaliacaoOrganizacaoEvento.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoAvaliacaoOrganizacaoEvento();

	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
}
