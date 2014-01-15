/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 08/10/2007 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.web.jsf.AbstractPerfilPessoaMBean;
import br.ufrn.comum.dao.PerfilPessoaDAO;
import br.ufrn.comum.dominio.PerfilPessoa;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Controlador do perfil do coordenador de pólo de ensino a distância.
 * 
 * @author David Pereira
 *
 */
@Component("perfilCoordPolo") @Scope("request")
public class PerfilCoordenadorPoloMBean extends AbstractPerfilPessoaMBean {

	@SuppressWarnings("unchecked")
	public Usuario getUsuarioLogado() {
		return (Usuario) getCurrentRequest().getSession().getAttribute("usuario");
	}

	@Override
	public PerfilPessoa getPerfilUsuario() {
		if (getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo().getIdPerfil() == null) return null;
		return PerfilPessoaDAO.getDao().get(getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo().getIdPerfil());
	}

	@Override
	public void setPerfilPortal(PerfilPessoa perfil) {
		getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo().setIdPerfil(perfil.getId());
		PortalCoordenadorPoloMBean portal = (PortalCoordenadorPoloMBean) getMBean("portalCoordPolo");
		portal.setPerfil(perfil);
	}

	@Override
	public void setTipoPerfil(PerfilPessoa perfil) {
		perfil.setIdCoordPolo(getUsuarioLogado().getVinculoAtivo().getCoordenacaoPolo().getId());
	}
	
}
