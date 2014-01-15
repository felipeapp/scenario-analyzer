package br.ufrn.sigaa.ouvidoria.jsf;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ouvidoria.dao.TipoManifestacaoDao;
import br.ufrn.sigaa.ouvidoria.dominio.TipoManifestacao;

/**
 * Controller para receptar operações referentes a {@link TipoManifestacao}.
 * 
 * @author Bernardo
 *
 */
@Component(value="tipoManifestacao") @Scope(value="request")
public class TipoManifestacaoMBean extends SigaaAbstractController<TipoManifestacao> {

    public TipoManifestacaoMBean() {
    	init();
    }

    /**
     * Inicia os objetos necessários do MBean
     */
    private void init() {
    	obj = new TipoManifestacao();
    }
    
    /**
     * Retorna todos os tipos de manifestação cadastrados.<br />
     * Método não invocado por JSPs.
     * 
     * @return
     * @throws DAOException
     */
    public Collection<TipoManifestacao> getAllTiposManifestacao() throws DAOException {
		TipoManifestacaoDao dao = getDAO(TipoManifestacaoDao.class);
		Collection<TipoManifestacao> tipos = new ArrayList<TipoManifestacao>();
		
		try {
		    tipos = dao.getAllTipos();
		} finally {
		    dao.close();
		}
		
		return tipos;
    }
    
    /**
     * Retorna todos os tipos de manifestação cadastrados em forma de combo.<br />
	 * Método invocado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/discente/form.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/docente/form.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/ouvidor/dados_manifestacao.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/servidor/form.jsp</li>
	 * <li>/sigaa.war/ouvidoria/Manifestacao/form.jsp</li>
	 * </ul>
     * 
     * @return
     * @throws DAOException
     */
    public Collection<SelectItem> getAllTiposManifestacaoCombo() throws DAOException {
    	ArrayList<SelectItem> itens = new ArrayList<SelectItem>();
    	
    	for (TipoManifestacao t : getAllTiposManifestacao())
			itens.add(new SelectItem(t.getId(), t.getDescricao()));
    	
    	return itens;
    }

}
