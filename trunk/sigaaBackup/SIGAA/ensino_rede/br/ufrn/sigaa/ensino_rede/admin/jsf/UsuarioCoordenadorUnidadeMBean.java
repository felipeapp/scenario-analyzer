/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 17/08/2013 
 */
package br.ufrn.sigaa.ensino_rede.admin.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.component.UIData;
import org.apache.myfaces.component.html.ext.HtmlDataTable;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UsuarioMov;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.integracao.interfaces.PermissaoUsuarioSistemaRemoteService;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino_rede.dao.CoordenadorUnidadeDao;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampus;
import br.ufrn.sigaa.ensino_rede.jsf.SelecionaCampusIesMBean;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Controlador respons�vel por criar usu�rios para coordenadores de unidade.
 * 
 * @author Leonardo Campos
 *
 */
@Component("usuarioCoordenadorUnidadeMBean") @Scope("session")
public class UsuarioCoordenadorUnidadeMBean extends SigaaAbstractController<CoordenadorUnidade> implements SelecionaCampus {

	/** Tela de listagem dos coordenadores de unidade */
	private static final String JSP_LISTA_COORDENADORES = "/ensino_rede/usuario_coordenador_unidade/lista_coordenadores.jsp";
	/** Tela de formul�rio do cadastro de usu�rio */
	private static final String JSP_FORM_USUARIO = "/ensino_rede/usuario_coordenador_unidade/form_usuario.jsp";

	/** Campus de refer�ncia */
	private CampusIes campus;
	
	/** Tabela de poss�veis coordenadores */
	private UIData coordenadoresPossiveis = new HtmlDataTable();
	
	/** Usu�rio a ser cadastrado */
	private Usuario usuario;
	
	/**
	 * Inicia o caso de uso.
	 * 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino_rede/modulo/menu.jsp</li>
	 * </ul>
	 * 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.COORDENADOR_GERAL_REDE);
		clear();
		
		setOperacaoAtiva(SigaaListaComando.CADASTRAR_USUARIO.getId());
		prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO);
		
		SelecionaCampusIesMBean mBean = getMBean("selecionaCampusRedeMBean");
		mBean.setRequisitor(this);
		
		return mBean.iniciar();
	}
	
	/**
	 * Limpa os objetos utilizados no caso de uso.
	 */
	private void clear() {
		obj = new CoordenadorUnidade();
		campus = new CampusIes();
		usuario = new Usuario();
	}


	@Override
	public String selecionaCampus() throws ArqException {
		if(ValidatorUtil.isEmpty(getCoordenadoresCampus())){
			addMensagemErro("N�o h� coordenador(es) para o campus selecionado.");
			return null;
		}
		return forward(JSP_LISTA_COORDENADORES);
	}
	
	/**
	 * Seleciona o coordenador desejado para cria��o do usu�rio.
	 * 
	 * * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino_rede/usuario_coordenador_rede/lista_coordenadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws SegurancaException
	 * @throws DAOException
	 */
	public String selecionaCoordenador() throws SegurancaException, DAOException {
		checkChangeRole();
		obj = (CoordenadorUnidade) getCoordenadoresPossiveis().getRowData();
		obj = getGenericDAO().findByPrimaryKey(obj.getId(), CoordenadorUnidade.class);
		
		ListaMensagens erros = new ListaMensagens();
		validaUsuarioExistente(obj.getPessoa(), erros);
		if ( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}
		return forward(JSP_FORM_USUARIO);
	}
	
	/**
	 * Valida se o coordenador selecionado j� possui usu�rio.
	 * 
	 * M�todo n�o chamado por JSPs.
	 * 
	 * @param pessoa
	 * @param lista
	 * @throws DAOException
	 */
	public void validaUsuarioExistente(Pessoa pessoa, ListaMensagens lista) throws DAOException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		List<Usuario> users = dao.findByPessoa(pessoa);
		if(ValidatorUtil.isNotEmpty(users))
			lista.addErro("J� existe usu�rio cadastrado para essa pessoa.");
	}
	
	/**
	 * Cadastra o usu�rio para o coordenador de unidade.
	 * 
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino_rede/usuario_coordenador_rede/form_usuario.jsp</li>
	 * </ul>
	 * 
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
		
		ListaMensagens erros = new ListaMensagens();
		validaDadosUsuario(usuario, erros);
		if ( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}
		
		usuario.setPessoa(getObj().getPessoa());
		usuario.setTipo(new TipoUsuario(TipoUsuario.TIPO_OUTROS));
		usuario.setUnidade(new Unidade(UnidadeGeral.UNIDADE_DIREITO_GLOBAL));
		UsuarioMov mov = new UsuarioMov();
		mov.setUsuario(usuario);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO);
		mov.setRequest(getCurrentRequest());

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());

			PermissaoUsuarioSistemaRemoteService service = getMBean("permissaoUsuarioSistemaInvoker");
			service.configurarPermissaoUsuario(usuario.getId(), null, 'D');
			service.configurarPermissaoUsuario(usuario.getId(), Sistema.SIGAA, 'G');
			
			addMessage("Cadastro de usu�rio realizado com sucesso!", TipoMensagemUFRN.INFORMATION);
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			return null;
		} catch(Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		clear();
		return cancelar();
	}
	
	/**
	 * Efetua a valida��o do formul�rio de cadastro de usu�rio.
	 * 
	 * M�todo n�o chamado por JSPs.
	 * 
	 * @param usuario
	 * @param lista
	 * @throws DAOException
	 */
	public void validaDadosUsuario(Usuario usuario, ListaMensagens lista) throws DAOException{
		
		ValidatorUtil.validateEmailRequired(usuario.getEmail(), "E-mail", lista);
		ValidatorUtil.validateRequired(usuario.getLogin(), "Login", lista);
		ValidatorUtil.validateMaxLength(usuario.getLogin(), 20, "Login", lista);
		ValidatorUtil.validateRequired(usuario.getSenha(), "Senha", lista);
		ValidatorUtil.validateMinLength(usuario.getSenha(), 5, "Senha", erros);
		
		if (usuario.getSenha() != null && usuario.getConfirmaSenha() != null && !usuario.getSenha().equals(usuario.getConfirmaSenha())) {
			lista.addErro("A senha n�o confere com a confirma��o.");
		}
		
		if(usuario.getLogin() != null || !"".equals(usuario.getLogin().trim())){
			UsuarioDao dao = getDAO(UsuarioDao.class);
			try {
				Usuario u = dao.findByLogin( usuario.getLogin() );
				if( u != null && u.getId() > 0 ){
					lista.addErro("J� existe um usu�rio no sistema com o login informado.");
				}
			} finally {
				if(dao != null)
					dao.close();
			}
		}
	}
	
	/**
	 * Volta para a tela de listagem dos campi.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino_rede/usuario_coordenador_rede/lista_coordenadores.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarTelaListaCampus() {
		return forward("/ensino_rede/busca_geral/campus_ies/lista_campus.jsp");
	}
	
	/**
	 * Volta para a tela de listagem dos coordenadores de unidade.
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 * 	<li>/sigaa.war/ensino_rede/usuario_coordenador_rede/form_usuario.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String voltarTelaListaCoord() {
		return forward(JSP_LISTA_COORDENADORES);
	}
	
	/**
	 * Retorna uma cole��o de coordenadores de unidade do campus de refer�ncia.
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<CoordenadorUnidade> getCoordenadoresCampus() throws HibernateException, DAOException {
		return getDAO(CoordenadorUnidadeDao.class).findCoordenadoresByCampusIes(getCampus().getId());
	}

	public CampusIes getCampus() {
		return campus != null ? campus : new CampusIes();
	}

	@Override
	public void setCampus(CampusIes campus) throws ArqException {
		this.campus = campus;	
	}

	public UIData getCoordenadoresPossiveis() {
		return coordenadoresPossiveis;
	}

	public void setCoordenadoresPossiveis(UIData coordenadoresPossiveis) {
		this.coordenadoresPossiveis = coordenadoresPossiveis;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
