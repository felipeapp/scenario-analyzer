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
import br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico;

/**
 * Gerado pelo CrudBuilder
 */
public class SubTipoArtisticoMBean
		extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.SubTipoArtistico> {

	public final String JSP_LISTA = "/prodocente/producao/SubTipoArtistico/lista.jsf?aba=adm";
	
	public SubTipoArtisticoMBean() {
		clear();
	}

	private void clear() {
		obj = new SubTipoArtistico();
	}
	
	public Collection<SelectItem> getAllCombo() {
		return getAll(SubTipoArtistico.class, "id", "descricao");
	}

	/**
	 * Para quando remover um elemento da lista, retornar para mesma lista.
	 */
	@Override
	public String getUrlRedirecRemover() {
		return JSP_LISTA;
	}
	
	/**
	 * Logo ap�s o cadastro instanciar um novo objeto. 
	 */
	@Override
	protected void afterCadastrar() {
		clear();
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

	/**
	 * Serve para checar se o usu�rio tem permiss�o para acessar esse determinado servi�o.
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	/**
	 * Para quando o usu�rio clicar no bot�o cancelar, ele seja direcionado para a tela da listagem.
	 * 
	 * JSP: form.jsp
	 */
	@Override
	public String cancelar() {
		return forward(JSP_LISTA);
	}
}