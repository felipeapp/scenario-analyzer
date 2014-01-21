/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoPeriodicoMBean
		extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TipoPeriodico> {
	public TipoPeriodicoMBean() {
		obj = new TipoPeriodico();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoPeriodico.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoPeriodico();
	}

	/**
	 * Seta o id, para true caso, evitando assim que ao cadastrar ou atualizar o mesmo passe o ativo
	 * para false o que impossibilitaria a sua visualiza��o.
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
			obj.setAtivo(true);
		super.beforeCadastrarAfterValidate();
	}

	@Override
	public void checkDocenteRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}

	@Override
	public String getUrlRedirecRemover() {
		return "/prodocente/producao/TipoPeriodico/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getUrlRedirecRemover());
	}
}