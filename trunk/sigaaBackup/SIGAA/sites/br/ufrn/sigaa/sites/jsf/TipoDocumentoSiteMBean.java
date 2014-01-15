/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe Manage Bean respons�vel pelo inser��o, altera��o e remo��o dos tipos de documentos relacionados
 * aos portais p�blicos (unidade e curso).
 * 
 * @author M�rio Rizzi
 */
@Component("tipoDocumentoSite") @Scope("request")
public class TipoDocumentoSiteMBean extends SigaaAbstractController<TipoDocumentoSite> {

	public TipoDocumentoSiteMBean() {
		obj = new TipoDocumentoSite();
	}
	
	/**
	 * M�todo respons�vel em definir o diret�rio raiz do caso de uso.
	 * <br/><br/>N�o � chamado por nenhuma JSP. 
	 */
	@Override
	public String getDirBase() {
		return "/site/tipo_documento_site";
	}
	
	/**
	 * M�todo respons�vel em direcionar para a listagem dos tipos de documento associados ao portal p�blico.
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
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
	 * M�todo respons�vel em retornar uma cole��o de tipos de documento associados ao portal p�blico
	 * em um selectItem.
	 * 
	 * <br />
	 * <br/><b>M�todo chamado pela(s) seguinte(s) JSP(s):</b>
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
