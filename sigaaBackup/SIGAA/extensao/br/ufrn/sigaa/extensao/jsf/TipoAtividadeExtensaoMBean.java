/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */
package br.ufrn.sigaa.extensao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.prodocente.producao.jsf.AbstractControllerProdocente;
/**
 * Representa os tipos de ações de extensão possíveis. Ex:
 * Programa, projeto, curso, evento.
 * 
 * Gerado pelo CrudBuilder
 **/
@Component("tipoAtividadeExtensao")
@Scope("request")
public class TipoAtividadeExtensaoMBean extends AbstractControllerProdocente<TipoAtividadeExtensao> {
	
	public TipoAtividadeExtensaoMBean() { 
		obj = new TipoAtividadeExtensao();
	}
	
	
	/*
	 * Utilizado para exibição dos tipos de Atividades de extensão.
	 * 
	 * Chamado por inúmeras JSP's do sistema.
	 * 
	 * (non-Javadoc)
	 * @see br.ufrn.arq.web.jsf.AbstractControllerCadastro#getAllCombo()
	 */
	public Collection<SelectItem> getAllCombo() throws ArqException { 
		return toSelectItems(getAllAtivos(), "id", "descricao");
	}
	
	/**
	 * Utilizado para exibição dos tipo de curso/evento.
	 *	  
	 * Chamado por:
	 * sigaa.war/public/extensao/consulta_inscricoes.jsp
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<SelectItem> getCursoEventoCombo() throws DAOException {
		return toSelectItems(getDAO(AtividadeExtensaoDao.class).findTipoCursoEvento(), "id", "descricao" );
	}
}
