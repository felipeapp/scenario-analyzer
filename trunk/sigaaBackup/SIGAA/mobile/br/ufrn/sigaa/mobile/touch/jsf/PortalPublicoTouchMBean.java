package br.ufrn.sigaa.mobile.touch.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dao.NoticiaPortalDAO;
import br.ufrn.comum.dominio.LocalizacaoNoticiaPortal;
import br.ufrn.comum.dominio.NoticiaPortal;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.mobile.commons.SigaaTouchAbstractController;

@Component("portalPublicoTouch") @Scope("request")
public class PortalPublicoTouchMBean extends SigaaTouchAbstractController {
	
	private static final int MAX_NOTICIAS_PORTAL = 3;
	
	private static final String LINK_DOWNLOAD_PUBLICO = "http://www.sistemas.ufrn.br/download/sigaa/public";
	
	private Collection<NoticiaPortal> ultimasNoticiasPortalPublico;
	
	public Collection<NoticiaPortal> getUltimasNoticiasPortalPublico() throws HibernateException, DAOException {
		if(isEmpty(ultimasNoticiasPortalPublico)) {
			ultimasNoticiasPortalPublico = carregarNoticias();
		}
		
		return ultimasNoticiasPortalPublico;
	}

	private Collection<NoticiaPortal> carregarNoticias() throws DAOException {
		NoticiaPortalDAO dao = getDAO(NoticiaPortalDAO.class, Sistema.COMUM);
		
		try {
			return dao.findPublicadasByLocalizacao(LocalizacaoNoticiaPortal.PORTAL_PUBLICO_SIGAA, MAX_NOTICIAS_PORTAL);
		} finally {
			dao.close();
		}
	}
	
	public String abrirCalendarioAcademico() {
		return redirect(LINK_DOWNLOAD_PUBLICO + "/calendario_universitario.pdf");
	}
	
	public String forwardPortalPublico() {
		return forward("/mobile/touch/public/principal.jsf");
	}
	
	public String redirectPortalPublico() {
		return redirect("/mobile/touch/public/principal.jsf");
	}
}
