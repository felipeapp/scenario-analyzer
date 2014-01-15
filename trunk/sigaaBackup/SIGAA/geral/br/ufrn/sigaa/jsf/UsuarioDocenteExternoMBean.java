/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Jun 6, 2007
 *
 */
package br.ufrn.sigaa.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.usuarios.UsuarioMov;
import br.ufrn.comum.dominio.TipoUsuario;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.negocio.DocenteExternoValidator;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;

/**
 * MBeam responsável por realizar o cadastro do usuário de docentes externos
 * @author Victor Hugo
 */
@Component("usuarioDocenteExterno")  @Scope("request")
public class UsuarioDocenteExternoMBean extends SigaaAbstractController<DocenteExterno> {

	/** Constante utilizada para designar a tela de formulário do cadastro para usuário do docente externo. */
	private final String FORM_USUARIO = "/administracao/docente_externo/usuario.jsp";

	/** Objeto do usuário do docente externo mantido. */
	private Usuario usuario = new Usuario();

	/** usado apenas na view, para definir se o usuário deverá informar a unidade do usuário do docente ou não */
	private boolean departamento = false;

	public UsuarioDocenteExternoMBean() {
		this.obj = new DocenteExterno();
		usuario.setUnidade( new Unidade() );

	}

	/** Método responsável pela inicialização do objeto. */
	void init(){
		usuario  = new Usuario();
		usuario.setUnidade( new Unidade() );
	}

	/**
	 * Realiza as validações necessárias e inicia o caso de uso de cadastrar usuário para docente externo
	 * realizado pelo DAE!
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/lista.jsp</li>
	 *	</ul>
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException{
		checkRole(SigaaPapeis.DAE, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.GESTOR_INFANTIL,
				SigaaPapeis.PPG, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, 
				SigaaPapeis.GESTOR_LATO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.GESTOR_TECNICO,
				SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		init();

		Integer id = getParameterInt("id");
		if( id == null || id == 0 ){
			addMensagemErro("Selecione um docente externo.");
			return null;
		}

		UsuarioDao dao = getDAO( UsuarioDao.class );
		obj = dao.findByPrimaryKey(id, DocenteExterno.class);

		usuario = dao.findByDocenteExterno(id);

		if( usuario != null && usuario.getId() != 0 ){
			addMensagemErro("Este docente já possui um usuário.");
			return null;
		}

		usuario = new Usuario();
		usuario.setDocenteExterno(obj);
		usuario.setPessoa( obj.getPessoa() );
		if( obj.getUnidade() != null)
			usuario.setUnidade( obj.getUnidade() );
		else
			usuario.setUnidade(new Unidade());

		prepareMovimento(SigaaListaComando.CADASTRAR_USUARIO);

		if( isUserInRole(SigaaPapeis.DAE, SigaaPapeis.PPG, SigaaPapeis.GESTOR_LATO) )
			departamento = false;
		else if( isUserInRole(SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.CHEFE_DEPARTAMENTO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.COORDENADOR_GERAL_EAD, SigaaPapeis.GESTOR_INFANTIL, 
				SigaaPapeis.GESTOR_TECNICO, SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO ) )
			departamento = true;

		return forward(FORM_USUARIO);
	}

	/**
	 * Persiste o usuário do docente.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/administracao/docente_externo/usuario.jsp</li>
	 *	</ul>
	 */
	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {

		ListaMensagens erros = new ListaMensagens();

		DocenteExternoValidator.validaUsuarioDocente(usuario, erros);

		if ( !erros.isEmpty() ){
			addMensagens(erros);
			return null;
		}

		UsuarioDao dao = getDAO( UsuarioDao.class );
		obj = dao.findByPrimaryKey(obj.getId(), DocenteExterno.class);

		usuario.setDocenteExterno(obj);
		usuario.setPessoa( obj.getPessoa() );
		usuario.setTipo(new TipoUsuario(TipoUsuario.DOCENTE_EXTERNO));
		usuario.setIdDocenteExterno( obj.getId() );
		UsuarioMov mov = new UsuarioMov();
		mov.setUsuario(usuario);
		mov.setCodMovimento(SigaaListaComando.CADASTRAR_USUARIO);
		mov.setRequest(getCurrentRequest());

		try {
			executeWithoutClosingSession(mov, getCurrentRequest());

			addMessage("Cadastro de usuário realizado com sucesso!", TipoMensagemUFRN.INFORMATION);
		}catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch(Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			e.printStackTrace();
			return null;
		}

		init();
		return cancelar();

	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public boolean isDepartamento() {
		return departamento;
	}

	public void setDepartamento(boolean departamento) {
		this.departamento = departamento;
	}

}
