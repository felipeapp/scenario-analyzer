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
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoProex;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class OrientacaoProexMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.OrientacaoProex> {
	public OrientacaoProexMBean() {
		obj = new OrientacaoProex();
		obj.setServidor(new Servidor());
		obj.setDepartamento(new Unidade());
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(OrientacaoProex.class, "id", "descricao");
	}
	
	@Override
	public String getUrlRedirecRemover(){
		return "/prodocente/atividades/OrientacaoProex/lista.jsf";
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		prepareMovimento(ArqListaComando.CADASTRAR);
		return super.cadastrar();
	}
	
	/**
	 * Serve para quando o usuário cancelar a operação, o mesmo seja direcionado para a tela da listagem.
	 * 
	 * JSP: form.jsp
	 */
	@Override
	public String cancelar() {
		return forward(getUrlRedirecRemover());
	}
	
	/**
	 * Para que o usuário seja direcionado para a tela da listagem logo após um novo cadastro.
	 * 
	 * JSP: form.jsp
	 */
	@Override
	protected void afterCadastrar() throws ArqException {
		forward(getUrlRedirecRemover());
	}

	@SuppressWarnings("deprecation")
	@Override
	public Collection<OrientacaoProex> getAllAtividades() throws DAOException {
		Servidor servidorLogado = null;	
		if (getUsuarioLogado().getVinculoAtivo().getServidor() != null) {
				servidorLogado = getUsuarioLogado().getVinculoAtivo().getServidor();
		}
		if (obj.getServidor().getId() != 0) {
			return super.getAllAtividades();
		}else {
			atividades = null;	
		}
		return atividades;
	}
}