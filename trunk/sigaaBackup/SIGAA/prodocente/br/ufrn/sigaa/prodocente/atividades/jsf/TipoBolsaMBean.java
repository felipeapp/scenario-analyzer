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
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.prodocente.TipoBolsaProdocenteDao;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoBolsaMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoBolsaProdocente> {
	public TipoBolsaMBean() {
		obj = new TipoBolsaProdocente();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoBolsaProdocente.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllAtivoCombo() {
		return getAll(TipoBolsaProdocente.class, "id", "descricao");
	}

	public Collection<SelectItem> getAllProdutividadeCombo() throws DAOException {
		Collection tipos = getDAO( TipoBolsaProdocenteDao.class ).findAllProdutividade();
		return toSelectItems(tipos, "id" , "descricao");
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	@Override
	public String getListPage() {
		return "/prodocente/atividades/TipoBolsaProdocente/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
}
