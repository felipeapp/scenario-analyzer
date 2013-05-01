package br.com.ecommerce.jsf.administration.productManagement;

import java.util.List;

import javax.faces.model.SelectItem;

import org.ajax4jsf.model.KeepAlive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.com.ecommerce.arq.erros.DAOException;
import br.com.ecommerce.arq.jsf.CadastroAbstractController;
import br.com.ecommerce.arq.mensagem.MensagensArquitetura;
import br.com.ecommerce.arq.sbeans.SBeanCadastro;
import br.com.ecommerce.arq.sbeans.SBeanRemocao;
import br.com.ecommerce.dominio.produto.TipoCampoExtraProduto;

/**
 * Controlador respons�vel por opera��es relacionadas a Tipos de Campos extrass de produtos.
 * Trabalha em cima do cadastro, remo��o, listagem.
 * Oferece m�todos para suggestion-box, e listagem de items.
 * 
 * @author Rodrigo Dutra de Oliveira
 *
 */
@Component
@Scope("request")
@KeepAlive
public class TipoCampoExtraProdutoMBean extends CadastroAbstractController<TipoCampoExtraProduto>{

	/**
	 * Necess�rio pelo uso do KeepAlive
	 */
	private static final long serialVersionUID = 2L;
	
	/**
	 * P�gina repons�vel pelo crud do tipo de produto.
	 */
	private static final String CRUD_FORM = "/administration/content_management/product_database_management/cruds/tipo_campo_extra_produto_form.jsf";
	
	/**
	 * P�gina repons�vel pela visualiza��o detalhada do tipo de produto.
	 */
	private static final String VISUALIZAR_DETALHADAMENTE = "";
	
	/**
	 * Processador Cadastro.
	 */
	@Autowired
	private SBeanCadastro sBeanCadastro;
	
	/**
	 * Processador respons�vel pela inativa��o de PersistDBs
	 */
	@Autowired
	private SBeanRemocao sBeanRemocao;
	
	/**
	 * Tipo de Campo extra de produto que ser� visualizado.
	 */
	private TipoCampoExtraProduto tipoCampoExtraVisualizado;
	
	public TipoCampoExtraProdutoMBean(){
		reset();
	}
	
	/**
	 * Reinicia as vari�veis do mbean.
	 */
	private void reset(){
		obj = new TipoCampoExtraProduto();
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
	 * M�todo chamado para se validar e cadastrar um novo tipo de campo de produtos.
	 * 
	 * @return null
	 * @throws DAOException 
	 */
	public String cadastrarNovoTipoCampo() throws DAOException{
		
		if(isEmpty(obj.getDenominacao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Denomina��o");
		
		if(isEmpty(obj.getDescricao()))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Descri��o");

		if(hasMensagens())
			return null;

		sBeanCadastro.cadastrar(obj);
		
		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO, "Cadastro de Tipo de Produto");
		
		reset();
		return null;
	}
	
	/**
	 * Permite a remo��o de um determinado tipo de campo extra de produto.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws NumberFormatException 
	 */
	public String remover() throws NumberFormatException, DAOException{
		TipoCampoExtraProduto tipoCampoExtra = getGenericDAO().findByPrimaryKey(Integer.parseInt(getParameter("idTipoCampoExtraProduto")), TipoCampoExtraProduto.class);
		
		if(tipoCampoExtra == null)
			addMensagem(MensagensArquitetura.SOLICITACAO_JA_PROCESSADA, "Remo��o do tipo de Campo Extra de Produto");
		
		if(hasMensagens())
			return null;
		
		sBeanRemocao.remover(tipoCampoExtra);
		
		addMensagem(MensagensArquitetura.OPERACAO_REALIZADA_COM_SUCESSO, "Remo��o do tipo de Campo Extra de Produto");
		
		return redirect(CRUD_FORM);
	}
	
	/**
	 * Termite a visualiza��o de forma detalhada de um determinado tipo de campo extra de produto.
	 * 
	 * @return null
	 * @throws DAOException 
	 * @throws NumberFormatException 
	 */
	public String visualizarDetalhadamenteTipo() throws NumberFormatException, DAOException{
		TipoCampoExtraProduto tipoCampoExtra = getGenericDAO().findByPrimaryKey(Integer.parseInt(getParameter("idTipoCampoExtraProduto")), TipoCampoExtraProduto.class);
		
		if(tipoCampoExtra == null)
			addMensagem(MensagensArquitetura.ELEMENTO_NAO_DISPONIVEL_NO_BANCO, "Tipo de Campo Extra de Produto");
		
		if(hasMensagens())
			return null;
		
		tipoCampoExtraVisualizado = tipoCampoExtra;
		
		return forward(VISUALIZAR_DETALHADAMENTE);
	}
	
	/**
	 * M�todo usado para se buscar por todos os campos extras de produtos
	 * @return os tipos ativos encontrados.
	 * 
	 * @throws DAOException 
	 */
	public List<SelectItem> getAllCombo() throws DAOException{
		return toSelectItems(getGenericDAO().findAll(TipoCampoExtraProduto.class), "id", "denominacao");
	}
	
	/**
	 * Busca todos os tipos de campos ativos.
	 * 
	 * @return
	 * @throws DAOException 
	 */
	public List<TipoCampoExtraProduto> getAllTiposCamposExtrasProduto() throws DAOException{
		return (List<TipoCampoExtraProduto>) getGenericDAO().findAll(TipoCampoExtraProduto.class);
	}

	public void setTipoCampoExtraVisualizado(TipoCampoExtraProduto tipoCampoExtraVisualizado) {
		this.tipoCampoExtraVisualizado = tipoCampoExtraVisualizado;
	}

	public TipoCampoExtraProduto getTipoCampoExtraVisualizado() {
		return tipoCampoExtraVisualizado;
	}
	
}
