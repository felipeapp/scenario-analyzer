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
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoProducaoTecnologicaMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TipoProducaoTecnologica> {
	public TipoProducaoTecnologicaMBean() {
		obj = new TipoProducaoTecnologica();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoProducaoTecnologica.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoProducaoTecnologica();
	}
	@Override
	public void checkDocenteRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
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
	 * 
	 *  JSP: form.jsp
	 */
	@Override
	public String getUrlRedirecRemover() {
		return "/prodocente/producao/TipoProducaoTecnologica/lista.jsf";
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