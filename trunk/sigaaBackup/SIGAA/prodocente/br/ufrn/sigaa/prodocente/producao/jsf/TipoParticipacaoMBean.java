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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoParticipacaoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TipoParticipacao> {
	public TipoParticipacaoMBean() {
		obj = new TipoParticipacao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoParticipacao.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoParticipacao();

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
		return "/prodocente/producao/TipoParticipacao/lista.jsf";
	}
	
	@Override
	public String cancelar() {
		return forward(getUrlRedirecRemover());
	}
}