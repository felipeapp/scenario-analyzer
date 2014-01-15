/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoParticipacaoOrganizacaoEventosMBean
		extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacaoOrganizacaoEventos> {
	
	public TipoParticipacaoOrganizacaoEventosMBean() {
		obj = new TipoParticipacaoOrganizacaoEventos();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoParticipacaoOrganizacaoEventos.class, "id",
				"descricao");
	}

	public Collection<SelectItem> getAllAtivoCombo() {
		return getAllAtivo(TipoParticipacaoOrganizacaoEventos.class, "id",
				"descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoParticipacaoOrganizacaoEventos();
	}

	/**
	 * Seta o id, para true caso, evitando assim que ao cadastrar ou atualizar o mesmo passe o ativo
	 * para false o que impossibilitaria a sua visualização.
	 */
	@Override
	public void beforeCadastrarAfterValidate() throws NegocioException,
			SegurancaException, DAOException {
			obj.setAtivo(true);
		super.beforeCadastrarAfterValidate();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	@Override
	public String getUrlRedirecRemover() {
		return "/prodocente/producao/TipoParticipacaoOrganizacaoEventos/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getUrlRedirecRemover());
	}
}