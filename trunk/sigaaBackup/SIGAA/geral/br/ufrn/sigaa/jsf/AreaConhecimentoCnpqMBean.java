/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '30/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

public class AreaConhecimentoCnpqMBean extends
		SigaaAbstractController<AreaConhecimentoCnpq> {

	public AreaConhecimentoCnpqMBean() {
		obj = new AreaConhecimentoCnpq();
	}

	public Collection<SelectItem> getAllCombo() {
		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findGrandeAreasConhecimentoCnpq(), "id",
					"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
			// TODO: Notificar erro
		}
	}

	public Collection<SelectItem> getAllGrandeAreas() {
		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findGrandeAreasConhecimentoCnpq(), "id",
					"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
		}
	}

	public Collection<SelectItem> getAllAreas() {
		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findAllProjection(AreaConhecimentoCnpq.class, "nome", "asc", new String[]{"id", "nome"}), "id",	"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
			// TODO: Notificar erro
		}
	}

	
	/**
	 * Retorna somente as áreas do CNPQ
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllAreasCombo() {
		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findAreas(null), "id",	"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
			// TODO: Notificar erro
		}
	}

	/**
	 * Retorna somente as grandes áreas do CNPQ
	 * 
	 * @return
	 */
	public Collection<SelectItem> getAllGrandesAreasCombo() {
		AreaConhecimentoCnpqDao dao = (AreaConhecimentoCnpqDao) getDAO(AreaConhecimentoCnpqDao.class);
		try {
			return toSelectItems(dao.findGrandeAreasConhecimentoCnpq(), "id",	"nome");
		} catch (DAOException e) {
			return new ArrayList<SelectItem>();
			// TODO: Notificar erro
		}
	}
	
	/**
	 * Método que possibilitar a criação de autocompletes de Área de Conhecimento CNPQ.
	 * <br/>
	 * Método Chamado pelas seguintes JSPs:
	 * <ul>
	 *   <li>/SIGAA/app/sigaa.ear/sigaa.war/pesquisa/areaTecnologica/form.jsp</li>
	 * </ul>
	 * @param event
	 * @return
	 * @throws DAOException
	 */
	public Collection<AreaConhecimentoCnpq> autocompleteNomeArea(Object event) throws DAOException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class);
		try {
			String nome = event.toString();
			Collection<AreaConhecimentoCnpq> areas = dao.findByNome(nome, false);
			return areas;  			
		} finally {
			dao.close();
		}
	}
	
}
