/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: Arq_UFRN
 * Criado em: 2007/10/26
 */
package br.ufrn.comum.jsf;

import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Link;
import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.SubSistema;

/**
 * Controller geral para os managed beans de Comum.
 * 
 * @author David Pereira
 *
 */
public class ComumAbstractController<T> extends AbstractControllerCadastro<T> {

	/**
	 * Coleção genérica a ser utilizada para manipulação de dados
	 */
	protected DataModel colecao;
	
	public GenericDAO getGenericDAO() {
		GenericDAOImpl dao = new GenericDAOImpl(Sistema.COMUM);
		dao.setSession(getSessionRequest());
		return dao;
	}
	
	public String cancelar() {
		resetBean();
		removeOperacaoAtiva();
		
		if (getSubSistemaAtual() > 0){
			
			// Primeiro procura código do subsistema em sessão
			Object subsistema = getCurrentSession().getAttribute("subsistema");

			// Se código não estiver em sessão, busca nos outros escopos
			if (subsistema != null) {
				Link link = null;
				
				if (subsistema instanceof SubSistema) {
					link = new Link(((SubSistema) subsistema).getNome(), ((SubSistema) subsistema).getLink());
				} else if (subsistema instanceof String) {
					SubSistema ssbd;
					try {
						ssbd = getGenericDAO().findByPrimaryKey(Integer.parseInt(subsistema.toString()), SubSistema.class);
						if (ssbd != null) link = new Link(ssbd.getNome(), ssbd.getLink());
					} catch (DAOException e) { }
				}
				
				if (link != null)
					return redirect(getCurrentRequest().getContextPath() + link.getUrl());
			}
		}
		
		return forward("/portal/index.jsf");
	}
	
	/**
	 * Retorna o subsistema que esta sendo usado atualmente
	 *
	 * @param req
	 * @return subsistema
	 */
	public static int getSubSistemaAtual() {
		Object subsistema = null;
		HttpSession session = ((HttpServletRequest) FacesContext.getCurrentInstance()
				.getExternalContext().getRequest()).getSession();

		if (session != null)
			subsistema = session.getAttribute("subsistema");

		if (subsistema instanceof SubSistema) {
			return ((SubSistema) subsistema).getId();
		} else {
			return (subsistema == null) ? 0 : Integer.parseInt(subsistema.toString());
		}
	}
	
	public DataModel getColecao() {
		return colecao;
	}

	public void setColecao(DataModel colecao) {
		this.colecao = colecao;
	}

	
}
