package br.ufrn.sigaa.ensino.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.CategoriaDiscenteEspecial;
/** 
 * Controller responsável pelas operações de cadastro de categorias de discente especial na instituição, 
 * 
 * @author Joab F. Galdino
 */

@Component("categoriaDiscenteEspecial")
@Scope("request")
public class CategoriaDiscenteEspecialMBean extends SigaaAbstractController<CategoriaDiscenteEspecial> {

	/** Construtor padrão. */
	public CategoriaDiscenteEspecialMBean() {
		clear();
	}
	
	/** Inicializa os atributos do controller.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li> NENHUMA JSP</li>
	 * </ul>
	 */
	public void clear() {
		obj = new CategoriaDiscenteEspecial();
	}
	/** Retorna uma coleção de selectItem com todas categorias de discente especial.
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(CategoriaDiscenteEspecial.class, "id", "descricao");
	}
		
	/** Inicia o cadastro de categoria de discente especial
	 *  <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/administracao/cadastro/CategoriaDiscenteEspecial/lista.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#preCadastrar()
	 */
	@Override
	public String preCadastrar() throws ArqException, NegocioException {
		checkChangeRole();
		setConfirmButton("Cadastrar");
		return forward(getFormPage());
	}
	
	/** Remove uma categoria de discente especial.
	 * <br/>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *	<li>/administracao/cadastro/CategoriaDiscenteEspecial/lista.jsp</li>
	 * </ul>
	 * Verifica se o objeto já foi removido, para evitar o nullPointer
	 */
	@Override
	public String remover() throws ArqException {

		Integer id = getParameterInt("id", 0);
		GenericDAO dao = getDAO(GenericDAOImpl.class);		
		obj = dao.findByPrimaryKey(id, CategoriaDiscenteEspecial.class);

		if (obj == null) {
			clear();
		}
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	/** Cadastra / atualiza a categoria discente especial
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/CategoriaDiscenteEspecial/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#cadastrar()
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {	
	GenericDAO dao = getDAO(GenericDAOImpl.class);  	
	Collection<CategoriaDiscenteEspecial> mesmaCategoria = dao.findByExactField(CategoriaDiscenteEspecial.class, "descricao",obj.getDescricao());
	for (CategoriaDiscenteEspecial cde : mesmaCategoria) {
		if (cde.getId() == obj.getId()) {
			super.cadastrar();
			return forwardCadastrar();
		} else if(cde.getDescricao().equals(obj.getDescricao())){
			addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Categoria Discente Especial");
			return null;
		}
	}
		super.cadastrar();
	return forwardCadastrar();
	}
	
	/** Cadastra / atualiza a categoria discente especial
	 * 
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/administracao/cadastro/CategoriaDiscenteEspecial/form.jsp</li>
	 * </ul>
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override 
	public String cancelar(){
		return forward(getListPage());
	}
	/** Redireciona para a página de listagem de Categoria de discente especial
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#forwardCadastrar()
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}
	
	/** defina a ordem da lista de categorias discente especial
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAtributoOrdenacao()
	 */
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}
	
	/** verifica se o obj é valido
	 * Método não invocado por JSP
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#afterAtualizar()
	 */
	@Override
	public void afterAtualizar() throws ArqException {
		if(obj == null && !hasErrors()){
			erros.addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
			obj = new CategoriaDiscenteEspecial();			
		}
	}
	
	/**
	 * Página do formulário
	 * 
	 * @return
	 */
	@Override
	public String getFormPage() {
		if(hasErrors())
			return getListPage();
		return getDirBase() + "/form.jsf";
	}
	/**
	 * Diretório base da lista
	 * 
	 * @return
	 */
	@Override
	public String getListPage() {
		getPaginacao().setPaginaAtual(0);
		return getDirBase() + "/lista.jsf";
	}
}
