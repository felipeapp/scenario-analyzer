package br.ufrn.sigaa.ouvidoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.StatusManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.StatusManifestacao;

/**
 * Controller para operações com {@link StatusManifestacao}.
 * 
 * @author Bernardo
 *
 */
@Component(value="statusManifestacao") @Scope(value="request")
public class StatusManifestacaoMBean extends SigaaAbstractController<StatusManifestacao> {

    public StatusManifestacaoMBean() {
    	init();
    }

    /**
     * Inicia os objetos necessários do MBean
     */
    private void init() {
    	obj = new StatusManifestacao();
    }
    
    /**
     * Retorna todos os status cadastrados.<br />
     * Método não chamado por JSPs.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<StatusManifestacao> getAllStatusManifestacao() throws DAOException {
		StatusManifestacaoDao dao = getDAO(StatusManifestacaoDao.class);
		Collection<StatusManifestacao> status = new ArrayList<StatusManifestacao>();
		
		try {
		    status = dao.getAllStatus();
		} finally {
		    dao.close();
		}
		
		return status;
    }
    
    /**
     * Retorna todos os status cadastrados em forma de combo.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/relatorios/geral/form.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllStatusManifestacaoCombo() throws DAOException {
    	return toSelectItems(getAllStatusManifestacao(), "id", "descricao");
    }

}
