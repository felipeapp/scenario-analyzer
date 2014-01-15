/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.extensao.dominio.TipoProduto;

/**
 * Responsável pelo gerenciamento dos tipos de produtos de extensão possíveis.
 * Ex: Livro, Revista, CDs.
 * 
 * Gerado pelo CrudBuilder
 */
@Scope("session")
@Component("tipoProduto") 
public class TipoProdutoMBean extends SigaaAbstractController<TipoProduto> {

	/**
	 * Construtor
	 */
	public TipoProdutoMBean() {
		obj = new TipoProduto();
	}

	/**
	 * Utilizado para disponibilizar os tipos de Produto ativos.
	 * @return
	 */
	public Collection<SelectItem> getAllAtivosCombo() {
		return getAllAtivo(TipoProduto.class, "id", "descricao");
	}

	/*
	 * Utilizado para executar determinada ação antes de inativar um tipo de produto.
	 * 
	 * (non-Javadoc)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#beforeInativar()
	 */
	@Override
	protected void beforeInativar() {
		try {
			prepareMovimento(ArqListaComando.DESATIVAR);
		} catch (ArqException e) {
			notifyError(e);
		}
		setOperacaoAtiva(ArqListaComando.DESATIVAR.getId());
		super.beforeInativar();
	}

	
	/**
	 * Utilizado para disponibilizar os tipos de Produto ativos.
	 * @return
	 */
	@Override
	public java.util.Collection<TipoProduto> getAllAtivos() throws ArqException {

		return getGenericDAO().findByExactField(TipoProduto.class, "ativo",
				Boolean.TRUE);
	}
	
	/**
	 * Inicia o cadastro de tipos de produtos.
	 * Chamado por:
	 * <ul><li>/sigaa.war/extensao/menu.jsp</li></ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws ArqException
	 * @throws NegocioException
	 */
	public String iniciarCadastro() throws ArqException {
	    prepareMovimento(ArqListaComando.CADASTRAR);
	    return forward("/extensao/TipoProduto/form.jsp");
	}

}
