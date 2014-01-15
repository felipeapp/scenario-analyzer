package br.ufrn.sigaa.ouvidoria.struts;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.struts.AbstractAction;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.ouvidoria.dao.ManifestacaoDao;

/**
 * Action que controla a entrada no módulo de ouvidoria.
 * 
 * @author Bernardo
 *
 */
public class EntrarOuvidoriaAction extends AbstractAction {
    
    @Override
    public ActionForward execute(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	verificarDesignacoesOuvidoria(getUsuarioLogado(request), request);
    	
	    setSubSistemaAtual(request, SigaaSubsistemas.OUVIDORIA);
		return mapping.findForward("sucesso");
    }
    
    /**
     * Verifica se a pessoa associada ao usuário possui alguma designação de ouvidoria cadastrada.
     * 
     * @param usuario
     * @return
     * @throws ArqException 
     */
    private boolean verificarDesignacoesOuvidoria(UsuarioGeral usuario, HttpServletRequest req) throws ArqException {
		boolean possui = false;
		ManifestacaoDao dao = getDAO(ManifestacaoDao.class, req);
	
		try {
		    possui = dao.possuiManifestacoesDesignadas(usuario.getPessoa());
		    
		    if(possui) {
		    	usuario.addPapelTemporario(SigaaPapeis.DESIGNADO_OUVIDORIA);
		    }
		} finally {
		    dao.close();
		}
		
		return possui;
    }

}
