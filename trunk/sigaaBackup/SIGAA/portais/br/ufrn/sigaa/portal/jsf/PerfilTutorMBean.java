/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/10/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import br.ufrn.arq.web.jsf.AbstractPerfilPessoaMBean;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Controlador do perfil do tutor
 * 
 * @author David Pereira
 *
 */
public class PerfilTutorMBean extends AbstractPerfilPessoaMBean {

	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentRequest().getSession().getAttribute("usuario");
	}

	@Override
	public PerfilPessoa getPerfilUsuario() {
		if (getUsuarioLogado().getVinculoAtivo().getTutor().getIdPerfil() == null) return null;
		return PerfilPessoaDAO.getDao().get(getUsuarioLogado().getVinculoAtivo().getTutor().getIdPerfil());
	}

	@Override
	public void setPerfilPortal(PerfilPessoa perfil) {
		getUsuarioLogado().getVinculoAtivo().getTutor().setIdPerfil(perfil.getId());
		PortalTutorMBean portal = (PortalTutorMBean) getMBean("portalTutor");
		portal.setPerfil(perfil);
	}

	@Override
	public void setTipoPerfil(PerfilPessoa perfil) {
		perfil.setIdTutor(getUsuarioLogado().getVinculoAtivo().getTutor().getId());
	}
	
}
