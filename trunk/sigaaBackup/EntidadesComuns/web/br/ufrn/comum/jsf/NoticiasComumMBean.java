package br.ufrn.comum.jsf;

import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.comum.dao.NoticiaPortalDAO;
import br.ufrn.comum.dominio.LocalizacaoNoticiaPortal;
import br.ufrn.comum.dominio.NoticiaPortal;
import br.ufrn.comum.dominio.Portal;

/**
 * Mbean responsavel por trazer todas as noticias do portal de qualquer sistema (SIGAA, SIGRH, SIPAC...)
 * 
 * @author Henrique André
 *
 */
@Component("noticiasComum") @Scope("request")
public class NoticiasComumMBean extends ComumAbstractController<NoticiaPortal> {

	
	// armazena todas as noticias
	public Collection<NoticiaPortal> noticias = null;
	
	// descricao do portal requerido
	public Portal portalNoticia = new Portal();
	
	/**
	 * Carrega informações para sem exibidas na pagina
	 * O nome do portal é passado em md5
	 * 
	 * @throws SegurancaException 
	 * @throws DAOException 
	 */
	public String getInit() throws DAOException, SegurancaException {
		
		String portalMD5 = getParameter("portal");
		portalNoticia = LocalizacaoNoticiaPortal.getPortalByMD5(portalMD5);
		if (portalNoticia != null)
			noticias = carregarNoticias(portalNoticia.getNome());
		return "";
	}
	

	/**
	 * Pega todas as noticis do portal informado
	 * 
	 * @param portal
	 * @return
	 * @throws DAOException
	 */
	private Collection<NoticiaPortal> carregarNoticias(String portal) throws DAOException {
		NoticiaPortalDAO dao = getDAO(NoticiaPortalDAO.class);
		try {
			return dao.findPublicadasByLocalizacao(portal);
		} finally {
			dao.close();
		}
	}

	public Portal getPortalNoticia() {
		return portalNoticia;
	}

	public void setPortalNoticia(Portal portalNoticia) {
		this.portalNoticia = portalNoticia;
	}

	public Collection<NoticiaPortal> getNoticias() {
		return noticias;
	}

	public void setNoticias(Collection<NoticiaPortal> noticias) {
		this.noticias = noticias;
	}
	
}
