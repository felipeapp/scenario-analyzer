
package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.validateEmail;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ead.dominio.CoordenacaoPolo;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.negocio.MovimentoUsuarioCoordPolo;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaPessoaMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoPessoa;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorPessoa;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Managed bean para operações de cadastro
 * de Coordenadores de Pólos de Educação a Distância
 * @author David Pereira
 *
 */
@Component("coordenacaoPolo") @Scope("session")
public class CoordenacaoPoloMBean extends SigaaAbstractController<CoordenacaoPolo> implements OperadorPessoa {

	/** {@link Usuario} a ser cadastrado como coordenador de polo. */
	private Usuario usuario = new Usuario();
	
	public CoordenacaoPoloMBean() {
		obj = new CoordenacaoPolo();
		obj.setPessoa(new Pessoa());
		obj.setPolo(new Polo());
	}

	/**
	 * Retorna  todos os pólos
	 * @return
	 * @throws DAOException
	 */
	public List<SelectItem> getAllPolos() throws DAOException {
		PoloDao dao = getDAO(PoloDao.class);
		return toSelectItems(dao.findAllPolos(), "id", "cidade.nomeUF");
	}

	/**
	 * Retorna lista de todos os coordenadores de pólo 
	 */
	@Override
	public Collection<CoordenacaoPolo> getAll() throws ArqException {
		return getGenericDAO().findAll(CoordenacaoPolo.class, "pessoa.nome", "asc");
	}

	/**
	 * Faz o pré-processamento de cadastro.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ead/menu.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String preCadastrar() throws ArqException,  NegocioException {
		checkRole(new int[] { SigaaPapeis.DAE, SigaaPapeis.SEDIS });
		prepareMovimento(ArqListaComando.CADASTRAR);
		BuscaPessoaMBean mbean = (BuscaPessoaMBean) getMBean("buscaPessoa");
		mbean.setCodigoOperacao(OperacaoPessoa.COORDENADOR_POLO);
		setConfirmButton("Cadastrar");
		setReadOnly(false);
		return mbean.popular();
	}
	
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException{
		
		if (getConfirmButton().equalsIgnoreCase("cadastrar")){
			UsuarioDao userDao = getDAO(UsuarioDao.class);
			List<Usuario> users;
			
			users = userDao.findByPessoa(obj.getPessoa());
			
			if(users != null && users.size() > 0)
				obj.setUsuario(users.get(0));
		}
		
		return super.cadastrar();
	}
	
	/**
	 * Redireciona a página após o cadastro.
	 * <br />
	 * Método não chamado por JSPs.
	 */
	@Override
	public String forwardCadastrar() {
		return "/ead/CoordenacaoPolo/lista.jsf";
	}

	/**
	 * Direciona para a página do formulário.
	 * <br />
	 * Método não chamado por JSPs.
	 */
	public String selecionaPessoa() {
		return forward(getFormPage());
	}

	public void setPessoa(Pessoa pessoa) {
		obj.setPessoa(pessoa);
	}
	
	/**
	 * Redireciona para a página de cadastro de usuário.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ead/CoordenacaoPolo/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String formUsuario() throws ArqException {
		obj = new CoordenacaoPolo();
		usuario = new Usuario();
		
		setId();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CoordenacaoPolo.class);
		prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO_COORD_POLO);
		return forward("/ead/CoordenacaoPolo/cadastro_usuario.jsp");
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	/**
	 * Realiza o cadastro do usuário.
	 * <br />
	 * Chamado pela JSP:
	 * <ul>
	 * <li>/sigaa.war/ead/CoordenacaoPolo/cadastro_usuario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String cadastrarUsuario() throws DAOException {
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CoordenacaoPolo.class);

		validateRequired(usuario.getLogin(), "Login", erros);
		validateRequired(usuario.getEmail(), "E-Mail", erros);
		validateEmail(usuario.getEmail(), "E-Mail", erros);
		if (hasErrors()) return null;
		
		MovimentoUsuarioCoordPolo mov = new MovimentoUsuarioCoordPolo();
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO_COORD_POLO);
		mov.setUsuario(usuario);
		mov.setCoordenador(obj);
		mov.setRequest(getCurrentRequest());

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage("Usuário cadastrado com sucesso!", TipoMensagemUFRN.INFORMATION);
			redirect(getContextPath() + "/ead/CoordenacaoPolo/lista.jsf");
			all = null;
			return null;
		} catch(Exception e) {
			notifyError(e);
			addMensagemErro(e.getMessage());
			return null;
		}

	}
	
}
