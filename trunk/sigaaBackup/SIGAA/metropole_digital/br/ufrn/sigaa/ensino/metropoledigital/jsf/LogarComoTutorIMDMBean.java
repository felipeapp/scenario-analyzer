package br.ufrn.sigaa.ensino.metropoledigital.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.faces.event.ValueChangeEvent;
import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.arq.web.LogonProgress;
import br.ufrn.comum.dao.PermissaoDAO;
import br.ufrn.sigaa.arq.acesso.AcessoMenu;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.TutorOrientadorDao;
import br.ufrn.sigaa.arq.dominio.SigaaConstantes;
import br.ufrn.sigaa.arq.dominio.UsuarioMov;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.metropoledigital.dao.CoordenadorTutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutorIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dao.TutoriaIMDDao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.TutorIMD;
import br.ufrn.sigaa.parametros.dominio.ParametrosTecnico;

/**
 * Managed bean para que os gestores do IMD efetuem o login como tutores do IMD
 *
 * @author Rafael Barros
 *
 */
@SuppressWarnings("serial")
@Component("logarComoTutorIMD") @Scope("request")
public class LogarComoTutorIMDMBean extends SigaaAbstractController<Object> {

	/** Entidade que corresponde ao usuário que vai ser logado */
	private Usuario usuario = new Usuario();

	/** Coleção com os usuários dos tutores do IMD */
	private Collection<Usuario> usuarios;

	/** Entidade que corresponde ao polo em que os tutores que pertecem */
	private Polo polo = new Polo();
	
	/**Coleção de itens do combo box de polo**/
	private Collection<SelectItem> polosCombo = new ArrayList<SelectItem>();

	/**Variável booleana de controle que armazena TRUE quando o sistema efetuou a busca de usuarios e FALSE quando o sistema não efetuou a busca de usuarios **/
	private boolean buscouUsuarios = false;
	
	/** Construtor padrão */
	public LogarComoTutorIMDMBean() {
		usuario = new Usuario();
		usuarios = new ArrayList<Usuario>();
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
		checkRole(SigaaPapeis.GESTOR_METROPOLE_DIGITAL, SigaaPapeis.COORDENADOR_TUTOR_IMD);
		
		GenericDAO dao = getGenericDAO();
		PermissaoDAO pDao = getDAO(PermissaoDAO.class);
		TutoriaIMDDao tutorDao = new TutoriaIMDDao();
		UsuarioDao usuDao = new UsuarioDao();
		
		int idTutor = getParameterInt("id");

		try {
			if (idTutor == 0) {
				addMensagemErro("Selecione um usuário.");
				return null;
			} else {

				TutorIMD tutor = new TutorIMD();
				tutor = dao.findByPrimaryKey(idTutor, TutorIMD.class);
				
				List<Usuario> listaUsuarios = new ArrayList<Usuario>();
				listaUsuarios = usuDao.findByPessoa(tutor.getPessoa());
				
				for(Usuario usu: listaUsuarios){
					usuario = usu;
				}
				
				Boolean retornoTutoria = tutorDao.possuiTutoria(usuario.getPessoa().getId());
				
				if(usuario.getId() == getUsuarioLogado().getId()){
					addMensagemErro("O usuário selecionado é o usuário que já está logado no sistema.");
					return null;
				} else {
				
					if(retornoTutoria == true){
					
						prepareMovimento(SigaaListaComando.LOGAR_COMO_TUTOR_IMD);
						
						usuario.setPermissoes(pDao.findPermissoesByUsuario(usuario));
						//usuario.getTutor().getPoloCurso().getPolo().getDescricao();
			
						UsuarioMov mov = new UsuarioMov();
						mov.setCodMovimento(SigaaListaComando.LOGAR_COMO_TUTOR_IMD);
						mov.setUsuario(usuario);
						mov.setIP(getCurrentRequest().getRemoteAddr());
						mov.setUsuarioLogado(getUsuarioLogado());
						executeWithoutClosingSession(mov, getCurrentRequest());
			
						removerAtributosSessao();
						
						getCurrentSession().setAttribute("usuarioAnterior", getCurrentSession().getAttribute("usuario"));
						getCurrentSession().setAttribute("ADMINISTRACAO_IMD", "true");
						getCurrentSession().setAttribute("acessoAnterior",	getCurrentSession().getAttribute("acesso"));
			
						usuario.setRegistroEntrada(getUsuarioLogado().getRegistroEntrada());
						getCurrentSession().setAttribute(SigaaConstantes.USUARIO_SESSAO, usuario);
			
						List<VinculoUsuario> vinculos = VinculoUsuario.processarVinculosUsuario(usuario, getCurrentRequest());
						usuario.setVinculos(vinculos);
						
						if (!isEmpty(vinculos)) {
							for (VinculoUsuario v : vinculos) {
								if (v.isVinculoTutorIMD()) {
									usuario.setVinculoAtivo(v);
								}
							}
						}
						
						LogonProgress progress = (LogonProgress) WebApplicationContextUtils.getWebApplicationContext(getCurrentSession().getServletContext()).getBean("logonProgress");
						AcessoMenu acesso = new AcessoMenu(usuario, progress);
						acesso.executar(getCurrentRequest());
						if (acesso.getDados().isMultiplosVinculos()) {
							for (VinculoUsuario vinculo : vinculos) {
								if (vinculo.isVinculoTutorIMD()) {
									usuario.setVinculoAtivo(vinculo);
									break;
								}
							}
						}
						
						setSubSistemaAtual(SigaaSubsistemas.PORTAL_TUTOR_IMD);
						getCurrentSession().setAttribute("acesso", acesso.getDados());
						resetBean("portalTutoriaIMD");
						
					} else {
						
						addMensagemErro("O usuário selecionado não possui nenhuma tutoria do IMD ativa.");
						return null;
						
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			addMensagemErro(e.getMessage());
			return null;
		} finally{
			dao.close();
			pDao.close();
			tutorDao.close();
			usuDao.close();
		}

		redirect("/verPortalTutoriaIMD.do");
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
		if (usuarios == null) {
			carregaUsuarios();
		}
		return toSelectItems(usuarios, "id", "nomeLogin");
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

	public Collection<Usuario> getUsuarios() {
		return usuarios;
	}

	public Polo getPolo() {
		return polo;
	}

	public void setPolo(Polo polo) {
		this.polo = polo;
	}

	public boolean isBuscouUsuarios() {
		return buscouUsuarios;
	}

	public void setBuscouUsuarios(boolean buscouUsuarios) {
		this.buscouUsuarios = buscouUsuarios;
	}

	/**
	 * Busca os usuários dos tutores do IMD para o combo. <br />
	 * 
	 * Não invocado por JSPs
     * 
	 * @return
	 * @throws DAOException
	 */
	private void carregaUsuarios() throws DAOException {
		TutorIMDDao dao = getDAO(TutorIMDDao.class);
		try {
			if(polo.getId() > 0) {
				
				usuarios = dao.findUsuariosByPolo(polo.getId());
				buscouUsuarios = true;
				
				if(usuarios.isEmpty()){
					addMessage("Nenhum usuário foi encontrado para o pólo selecionado.", TipoMensagemUFRN.ERROR);
				}
			} else {
				buscouUsuarios = false;
			}
		} finally {
			dao.close();
		}
	}
	
	/** Método responsável por retornar o atributo polosCombo, caso o atributo esteja vazio, o método efetua o preenchimento da lista
	 * 
	 * Método é chamado nas seguintes JSP's:
	 * <ul>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/lista.jsp</li>
	 * 	<li>/sigaa.war/metropole_digital/tutor_imd/form.jsp</li>
	 * </ul>
	 * 
	 * @param 
	 * @return polosCombo
	 */
	public Collection<SelectItem> getPolosCombo() throws DAOException {
		if(polosCombo.isEmpty()){
			polosCombo = toSelectItems(getGenericDAO().findAll(Polo.class), "id", "descricao");
		}
		return polosCombo;
	}

	public void setUsuarios(Collection<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public void setPolosCombo(Collection<SelectItem> polosCombo) {
		this.polosCombo = polosCombo;
	}

	
}

