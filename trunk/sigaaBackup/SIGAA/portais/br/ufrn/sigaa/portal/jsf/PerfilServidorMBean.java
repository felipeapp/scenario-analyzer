/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 14/06/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import br.ufrn.arq.web.jsf.AbstractPerfilPessoaMBean;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Controlador do perfil do docente
 *
 * @author Gleydson
 *
 */
public class PerfilServidorMBean extends AbstractPerfilPessoaMBean {

	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentRequest().getSession().getAttribute("usuario");
	}

	@Override
	public PerfilPessoa getPerfilUsuario() {
		Integer idPerfil = null;
		
		if (getUsuarioLogado().getServidor() == null) {
			if (getUsuarioLogado().getDocenteExterno() == null || getUsuarioLogado().getDocenteExterno().getIdPerfil() == null)
				return null;
			else
				idPerfil = getUsuarioLogado().getDocenteExterno().getIdPerfil();
		} else {
			if (getUsuarioLogado().getServidor().getIdPerfil() != null)
				idPerfil = getUsuarioLogado().getServidor().getIdPerfil();
		}
		
		return PerfilPessoaDAO.getDao().get(idPerfil);
	}

	@Override
	public void setPerfilPortal(PerfilPessoa perfil) {
		if (getUsuarioLogado().getServidor() != null)
			getUsuarioLogado().getServidor().setIdPerfil(perfil.getId());
		else
			getUsuarioLogado().getDocenteExterno().setIdPerfil(perfil.getId());
		
		PortalDocenteMBean portal = (PortalDocenteMBean) getMBean("portalDocente");
		portal.setPerfil(perfil);
	}

	@Override
	public void setTipoPerfil(PerfilPessoa perfil) {
		if (getUsuarioLogado().getServidor() == null)
			perfil.setIdDocenteExterno(getUsuarioLogado().getDocenteExterno().getId());
		else
			perfil.setIdServidor(getUsuarioLogado().getServidor().getId());
	}
	
}
