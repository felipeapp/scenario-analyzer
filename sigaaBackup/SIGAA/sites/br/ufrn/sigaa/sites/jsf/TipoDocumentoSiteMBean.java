/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/05/2009
 *
 */
package br.ufrn.sigaa.sites.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.sites.dominio.TipoDocumentoSite;

/**
 * Classe Manage Bean responsável pelo inserção, alteração e remoção dos tipos de documentos relacionados
 * aos portais públicos (unidade e curso).
 * 
 * @author Mário Rizzi
 */
@Component("tipoDocumentoSite") @Scope("request")
public class TipoDocumentoSiteMBean extends SigaaAbstractController<TipoDocumentoSite> {

	public TipoDocumentoSiteMBean() {
		obj = new TipoDocumentoSite();
	}
	
	/**
	 * Método responsável em definir o diretório raiz do caso de uso.
	 * <br/><br/>Não é chamado por nenhuma JSP. 
	 */
	@Override
	public String getDirBase() {
		return "/site/tipo_documento_site";
	}
	
	/**
	 * Método responsável em direcionar para a listagem dos tipos de documento associados ao portal público.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>admin.war/portal/menu_administracao.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String listar() throws ArqException {
		return super.listar();
	}
	
	/**
	 * Método responsável em retornar uma coleção de tipos de documento associados ao portal público
	 * em um selectItem.
	 * 
	 * <br />
	 * <br/><b>Método chamado pela(s) seguinte(s) JSP(s):</b>
	 * <ul>
	 * 	<li>/sigaa.war/site/documento_site/form.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoDocumentoSite.class, "id", "nome");
	}
	
}
