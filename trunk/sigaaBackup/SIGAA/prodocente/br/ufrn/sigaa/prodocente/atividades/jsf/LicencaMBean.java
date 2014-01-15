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
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.Licenca;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoAfastamento;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class LicencaMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.Licenca> {
	public LicencaMBean() {
		obj = new Licenca();
		obj.setAfastamento(new TipoAfastamento());
		obj.setServidor(new Servidor());
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(Licenca.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new Licenca();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PRH);
	}
	
	@Override
	public String getUrlRedirecRemover(){
		return "/sigaa/prodocente/atividades/Licenca/lista.jsf";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}

	@Override
	public String getListPage() {
		return "/prodocente/atividades/Licenca/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
}	