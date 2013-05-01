package br.com.ecommerce.jsf.administration.productManagement;

import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.ecommerce.arq.erros.DAOException;
import br.com.ecommerce.arq.jsf.AbstractController;
import br.com.ecommerce.arq.mensagem.MensagensArquitetura;
import br.com.ecommerce.arq.sbeans.SBeanCadastro;
import br.com.ecommerce.arq.sbeans.SBeanInativacao;
import br.com.ecommerce.dominio.produto.TipoProduto;
import br.com.ecommerce.jsf.ConstantesNavegacao;

/**
 * Controlador respons�vel por opera��es relacionadas � tipos de produtos.
 * Trabalha em cima do cadastro, remo��o, listagem.
 * Oferece m�todos para suggestion-box, e listagem de items.
 * 
 * @author Rodrigo Dutra de Oliveira
 *
 */
@Component
@Scope("request")
public class TipoProdutoMBean extends AbstractController{

	/**
	 * P�gina repons�vel pelo crud do tipo de produto.
	 */
	private static final String CRUD_FORM = "/administration/content_management/product_database_management/cruds/tipo_produto.jsf";
	
	/**
	 * P�gina repons�vel pela visualiza��o detalhada do tipo de produto.
	 */
	private static final String VISUALIZAR_DETALHADAMENTE = "";
	
	/**
	 * Tipo de produto que esta sendo trabalhado atualmente.
	 */
	private TipoProduto tipo;
	
	/**
	 * Tipo de produto que ser� visualizado.
	 */
	private TipoProduto tipoProdutoVisualizado;
	
	/**
	 * Processador Cadastro.
	 */
	@Autowired
	private SBeanCadastro sBeanCadastro;
	
	/**
	 * Processador respons�vel pela inativa��o de PersistDBs
	 */
	@Autowired
	private SBeanInativacao sBeanInativacao;
	
	public TipoProdutoMBean(){
		reset();
	}
	
	/**
	 * Reinicia as vari�veis do mbean.
	 */
	private void reset(){
		tipo = new TipoProduto();
	}
	
	/**
	 * M�todo chamado para se enviar para a p�gina de crud.
	 * 
	 * @return forward(CRUD_FORM)
	 */
	public String iniciarCadastro(){
		reset();
		return forward(CRUD_FORM);
	}
	
	/**
	 * M�todo chamado para se validar e cadastrar um novo tipo de produtos.
	 * 
	 * @return null
	 * @throws DAOException 
	 */
	public String cadastrarNovoTipo() throws DAOException{
		
		if(isEmpty(tipo.getDenominacao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Denomina��o");
		
		if(isEmpty(tipo.getDescricao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");

		if(hasMensagens())
			return null;

		sBeanCadastro.cadastrar(tipo);
		
		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO, "Cadastro de Tipo de Produto");
		
		reset();
		return cancelar();
	}
	
	/**
	 * M�todo usado para se cancelar.
	 * @return
	 */
	public String cancelar(){
		return redirect(ConstantesNavegacao.PORTAL_ADMINISTRACAO);
	}
	
	/**
	 * Termite a visualiza��o de forma detalhada de um determinado tipo de produto.
	 * 
	 * @return null
	 * @throws DAOException 
	 * @throws NumberFormatException 
	 */
	public String visualizarDetalhadamenteTipo() throws NumberFormatException, DAOException{
		TipoProduto tipoProduto = getGenericDAO().findByPrimaryKey(Integer.parseInt(getParameter("idTipoProduto")), TipoProduto.class);
		
		if(tipoProduto.isInativo())
			addMensagem(MensagensArquitetura.ELEMENTO_NAO_DISPONIVEL_NO_BANCO, "Tipo de Produto");
		
		if(hasMensagens())
			return null;
		
		return forward(VISUALIZAR_DETALHADAMENTE);
	}
	
	/**
	 * Permite a remo��o de um determinado tipo de produto.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws NumberFormatException 
	 */
	public String removerTipo() throws NumberFormatException, DAOException{
		TipoProduto tipoProduto = getGenericDAO().findByPrimaryKey(Integer.parseInt(getParameter("idTipoProduto")), TipoProduto.class);
		
		if(tipoProduto.isInativo())
			addMensagem(MensagensArquitetura.SOLICITACAO_JA_PROCESSADA, "Remo��o do tipo de Produto");
		
		if(hasMensagens())
			return null;
		
		sBeanInativacao.inativar(tipoProduto);
		
		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO, "Remo��o do tipo de produto");
		
		return redirect(CRUD_FORM);
	}
	
	/**
	 * M�todo usado para se buscar por todos os tipos de produtos ativos.
	 * @return os tipos ativos encontrados.
	 * 
	 * @throws DAOException 
	 */
	public List<SelectItem> getAllCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAllAtivos(TipoProduto.class), "id", "denominacao");
	}
	
	/**
	 * Busca todos os tipos ativos.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<TipoProduto> getAllTiposProduto() throws DAOException{
		return (List<TipoProduto>) getGenericDAO().findAllAtivos(TipoProduto.class);
	}

	public void setTipo(TipoProduto tipo) {
		this.tipo = tipo;
	}

	public TipoProduto getTipo() {
		return tipo;
	}

	public void setTipoProdutoVisualizado(TipoProduto tipoProdutoVisualizado) {
		this.tipoProdutoVisualizado = tipoProdutoVisualizado;
	}

	public TipoProduto getTipoProdutoVisualizado() {
		return tipoProdutoVisualizado;
	}
}
