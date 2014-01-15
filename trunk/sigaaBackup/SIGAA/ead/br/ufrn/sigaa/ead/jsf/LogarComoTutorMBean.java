/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 31/10/2007
 */
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
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
 * tutores presenciais.
 *
 * @author David Pereira
 *
 */
@SuppressWarnings("serial")
@Component("logarComo") @Scope("request")
public class LogarComoTutorMBean extends SigaaAbstractController<Object> {

	/**
	 * recebe o usuário que vai ser logado
	 */
	private Usuario usuario = new Usuario();

	/**
	 * Lista com os usuários dos tutores
	 */
	private List<Usuario> usuarios;

	/**
	 * Curso que os tutores que pertecem
	 */
	private Curso curso = new Curso();

	/**
	 * Polo que os tutores que pertecem
	 */
	private Polo polo = new Polo();

	/**
	 * Construtor padrão
	 */
	public LogarComoTutorMBean() {
		usuario = new Usuario();
	}

	/**
	 * Executa o procedimento de logar com o usuário escolhido <br />
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ead/TutorOrientador/logarComo.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws Exception
	 */
	public String logar() throws Exception {
		checkRole(SigaaPapeis.SEDIS);
		try {
			if (usuario.getId() == 0) {
				addMensagemErro("Selecione um usuário.");
				return null;
			}

			prepareMovimento(SigaaListaComando.LOGAR_COMO_TUTOR);

			GenericDAO dao = getGenericDAO();
			PermissaoDAO pDao = getDAO(PermissaoDAO.class);
			usuario = dao.findByPrimaryKey(usuario.getId(), Usuario.class);
			usuario.setPermissoes(pDao.findPermissoesByUsuario(usuario));
			//usuario.getTutor().getPoloCurso().getPolo().getDescricao();

			UsuarioMov mov = new UsuarioMov();
			mov.setCodMovimento(SigaaListaComando.LOGAR_COMO_TUTOR);
			mov.setUsuario(usuario);
			mov.setIP(getCurrentRequest().getRemoteAddr());
			mov.setUsuarioLogado(getUsuarioLogado());
			executeWithoutClosingSession(mov, getCurrentRequest());

			removerAtributosSessao();
			
			getCurrentSession().setAttribute("usuarioAnterior", getCurrentSession().getAttribute("usuario"));
			getCurrentSession().setAttribute("ADMINISTRACAO_EAD", "true");
			getCurrentSession().setAttribute("acessoAnterior",	getCurrentSession().getAttribute("acesso"));

			usuario.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
			getCurrentSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);

			List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, getCurrentRequest());
			usuario.setVinculos(vinculos);
			
			if (!isEmpty(vinculos)) {
				for (VinculoUsuario v : vinculos) {
					if (v.isVinculoTutorOrientador()) {
						usuario.setVinculoAtivo(v);
					}
				}
			}
			
			LogonProgress progress = (LogonProgress) WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()).getBean("logonProgress");
			AcessoMenu acesso = new AcessoMenu(usuario, progress);
			acesso.executar(getCurrentRequest());
			if (acesso.getDados().isMultiplosVinculos()) {
				for (VinculoUsuario vinculo : vinculos) {
					if (vinculo.isVinculoTutorOrientador()) {
						usuario.setVinculoAtivo(vinculo);
						break;
					}
				}
			}
			
			setSubSistemaAtual(SigaaSubsistemas.PORTAL_TUTOR);
			getCurrentSession().setAttribute("acesso", acesso.getDados());
			resetBean("portalTutor");
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		}

		redirect("/verPortalTutor.do");
		return null;
	}

	/**
	 * Retorna o combo com os tutores presenciais a serem selecionados <br />
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ead/TutorOrientador/logarComo.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws Exception
	 */
	public List<SelectItem> getTutores() throws ArqException {
		if (usuarios == null)
			carregaUsuarios();
		return toSelectItems(usuarios, "id", "nomeLogin");
	}

	/**
	 * Retorna o combo com os tutores à distância a serem selecionados <br />
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ead/TutorDistancia/logarComo.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws Exception
	 */
	public List<SelectItem> getTutoresDistancia() throws ArqException {
		if (usuarios == null)
			carregaUsuariosDistancia();
		return toSelectItems(usuarios, "id", "nomeLogin");
	}
	
	/**
	 * Retorna o combo com os tutores presenciais a serem selecionados, após serem filtrados pelo curso. <br />
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ead/TutorOrientador/logarComo.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws Exception
	 */
	public void carregarTutoresCurso(ValueChangeEvent e) throws DAOException {
		curso = new Curso((Integer) e.getNewValue());
		carregaUsuarios();
	}

	/**
	 * Retorna o combo com os tutores presenciais a serem selecionados, após serem filtrados pelo pólo. <br />
	 * 
	 * Método invocado pela(s) seguinte(s) JSP(s):
     * <ul>
     * <li>/sigaa.war/ead/TutorOrientador/logarComo.jsp</li>
     * </ul>
     * 
	 * @return
	 * @throws Exception
	 */
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

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	/**
	 * Busca os tutores presenciais para o combo. <br />
	 * 
	 * Não invocado por JSPs
     * 
	 * @return
	 * @throws Exception
	 */
	private void carregaUsuarios() throws DAOException {
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		usuarios = dao.findUsuariosTutores(curso, polo);
	}

	/**
	 * Busca os tutores à distância para o combo. <br />
	 * 
	 * Não invocado por JSPs
     * 
	 * @return
	 * @throws Exception
	 */
	private void carregaUsuariosDistancia() throws DAOException {
		TutorOrientadorDao dao = getDAO(TutorOrientadorDao.class);
		usuarios = dao.findUsuariosTutoresDistancia();
	}
	
}
