package br.ufrn.sigaa.ouvidoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.OrigemManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.OrigemManifestacao;

/**
 * Controller para operações em {@link OrigemManifestacao}.
 * 
 * @author Bernardo
 *
 */
@Component(value = "origemManifestacao") @Scope(value = "request")
public class OrigemManifestacaoMBean extends SigaaAbstractController<OrigemManifestacao> {

    public OrigemManifestacaoMBean() {
    	init();
    }

    /**
     * Inicia os dados necessários ao MBean
     */
    private void init() {
    	obj = new OrigemManifestacao();
    }

    /**
     * Retorna todas as origens cadastradas.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<OrigemManifestacao> getAllOrigens() throws DAOException {
		OrigemManifestacaoDao dao = getDAO(OrigemManifestacaoDao.class);
		Collection<OrigemManifestacao> origens = new ArrayList<OrigemManifestacao>();
	
		try {
		    origens = dao.getAllOrigensManifestacao();
		} finally {
		    dao.close();
		}
	
		return origens;
    }

    /**
     * Retorna todas as origens cadastradas em forma de campo.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllOrigensCombo() throws DAOException {
    	return toSelectItems(getAllOrigens(), "id", "descricao");
    }

}
