package br.ufrn.sigaa.ouvidoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.CategoriaSolicitanteDao;
import br.ufrn.sigaa.ouvidoria.dominio.CategoriaSolicitante;

/**
 * Controller para operações em {@link CategoriaSolicitante}.
 * 
 * @author Bernardo
 *
 */
@Component(value = "categoriaSolicitante") @Scope(value = "request")
public class CategoriaSolicitanteMBean extends SigaaAbstractController<CategoriaSolicitante> {

    public CategoriaSolicitanteMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários ao MBean
     */
    private void init() {
    	obj = new CategoriaSolicitante();
    }

    /**
     * Retorna todas as categorias cadastradas.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<CategoriaSolicitante> getAllCategorias() throws DAOException {
		CategoriaSolicitanteDao dao = getDAO(CategoriaSolicitanteDao.class);
		Collection<CategoriaSolicitante> categorias = new ArrayList<CategoriaSolicitante>();
	
		try {
		    categorias = dao.getAllCategoriasSolicitante();
		} finally {
		    dao.close();
		}
	
		return categorias;
    }

    /**
     * Retorna todas as categorias cadastradas em forma de combo.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllCategoriasCombo() throws DAOException {
    	return toSelectItems(getAllCategorias(), "id", "descricao");
    }

}
