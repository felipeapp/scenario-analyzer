/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 31/10/2007
 */
package br.ufrn.sigaa.ead.jsf;

import java.util.Iterator;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.dominio.SigaaConstantes;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.Polo;

/**
 * Managed bean para que os gestores de ensino a distância possam logar como
 * coordenadores de pólo.
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("unchecked")
@Component("logarComoCoordPolo") @Scope("request")
public class LogarComoCoordenadorPoloMBean extends SigaaAbstractController {

	private Usuario usuario = new Usuario();

	private List<Usuario> usuarios;

	private Polo polo = new Polo();

	@Autowired
	private LogonProgress progress;
	
	public LogarComoCoordenadorPoloMBean() {
		usuario = new Usuario();
	}

	public String logar() throws Exception {
		
		if (!getUsuarioLogado().isUserInRole(SigaaPapeis.SEDIS) && !getAcessoMenu().isCoordenadorCursoEad()) {
			throw new SegurancaException("Usuário não autorizado a realizar esta operação.");
		}
		
		try {
			if (polo.getId() == 0) {
				addMensagemErro("Selecione um polo.");
			}
			if (usuario.getId() == 0) {
				addMensagemErro("Selecione um usuário.");
			}
			if(hasErrors()) return null;
			
			prepareMovimento(SigaaListaComando.LOGAR_COMO_COORD_POLO);

			GenericDAO dao = getGenericDAO();
			PermissaoDAO pDao = getDAO(PermissaoDAO.class);
			usuario = dao.findByPrimaryKey(usuario.getId(), Usuario.class);
			usuario.setPermissoes(pDao.findPermissoesByUsuario(usuario));

			UsuarioMov mov = new UsuarioMov();
			mov.setCodMovimento(SigaaListaComando.LOGAR_COMO_COORD_POLO);
			mov.setUsuario(usuario);
			mov.setIP(getCurrentRequest().getRemoteAddr());
			mov.setUsuarioLogado(getUsuarioLogado());
			executeWithoutClosingSession(mov, getCurrentRequest());

			removerAtributosSessao();
			
			getCurrentSession().setAttribute("usuarioAnterior", getCurrentSession().getAttribute("usuario"));
			getCurrentSession().setAttribute("acessoAnterior",	getCurrentSession().getAttribute("acesso"));

			usuario.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			getCurrentSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);

			AcessoMenu acesso = prepararVinculosUsuario();
			acesso.executar(getCurrentRequest());
			
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_COORDENADOR_POLO);
			getCurrentSession().setAttribute("acesso", acesso.getDados());
			resetBean("portalCoordPolo");
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}

		redirect("/verPortalCoordPolo.do");
		return null;
	}

	private AcessoMenu prepararVinculosUsuario() throws ArqException {
		List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, getCurrentRequest());
		Iterator<VinculoUsuario> it = vinculos.iterator();
		while (it.hasNext()) {
			VinculoUsuario vu = it.next();
			if (vu.getTipoVinculo().isCoordenacaoPolo() && 
					vu.getCoordenacaoPolo() != null &&
					vu.getCoordenacaoPolo().getPolo() != null &&
					vu.getCoordenacaoPolo().getPolo().getId() != polo.getId()) {
				it.remove();
			} 
		}
		
		usuario.setVinculos(vinculos);
		
		AcessoMenu acesso = new AcessoMenu(usuario, progress);
		for (VinculoUsuario vinculo : vinculos) {
			if (vinculo.isVinculoCoordenacaoPolo()) {
				usuario.setVinculoAtivo(vinculo);
				break;
			}
		}
		return acesso;
	}

	public List<SelectItem> getCoordenadoresPolo() throws ArqException {
		if (usuarios == null)
			carregaUsuarios();
		return toSelectItems(usuarios, "id", "nomeLogin");
	}

	public void carregarTutoresPolo(ValueChangeEvent e) throws DAOException {
		polo = new Polo((Integer) e.getNewValue());
		carregaUsuarios();
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<Usuario> getUsuarios() {
		return usuarios;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	private void carregaUsuarios() throws DAOException {
		PoloDao dao = getDAO(PoloDao.class);
		Curso curso = null;
		if (getAcessoMenu().isCoordenadorCursoEad())
			curso = getCursoAtualCoordenacao();	
		usuarios = dao.findUsuariosCoordenadoresPolo(polo, curso);
	}

}
