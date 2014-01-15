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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoInstancia;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoInstanciaMBean
		extends AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TipoInstancia> {

	public final String JSP_LISTA = "/prodocente/producao/TipoInstancia/lista.jsf?aba=adm";
	
	public TipoInstanciaMBean() {
		obj = new TipoInstancia();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoInstancia.class, "id", "descricao");
	}

	/**
	 * Logo após o cadastro instanciar um novo objeto. 
	 */
	@Override
	protected void afterCadastrar() {
		obj = new TipoInstancia();
	}

	/**
	 * Para quando remover um elemento da lista, retornar para mesma lista.
	 */
	@Override
	public String getUrlRedirecRemover() {
		return JSP_LISTA;
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

	/**
	 * Checa se o usuário logado tem permissão para acessar. 
	 */
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	/**
	 * Para quando o usuário cancelar a operação que tiver fazendo, o mesmo pare na tela da listagem. 
	 * 
	 * JSP: form.jsp
	 */
	@Override
	public String cancelar() {
		return forward(getUrlRedirecRemover());
	}
}