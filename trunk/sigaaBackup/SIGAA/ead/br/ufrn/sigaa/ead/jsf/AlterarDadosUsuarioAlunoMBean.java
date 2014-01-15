package br.ufrn.sigaa.ead.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.apache.commons.validator.EmailValidator;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;

/**
 * Managed Bean para permitir que a SEDIS altere o e-mail e senha de alunos de EAD.
 * 
 * @author David Pereira
 * @author Bernardo
 *
 */
@Component(value="alterarDadosUsuarioAluno")
@Scope("session")
public class AlterarDadosUsuarioAlunoMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	/** JSP de dados de um usuário. */
	private static final String JSP_DADOS_USUARIO = "/ead/discente/dados_usuario.jsp";

	/** JSP de seleção de usuários. */
	private static final String JSP_USUARIOS = "/ead/discente/usuarios.jsp";

	/** Lista contendo a lista de usuários relacionados ao discente.*/
	private List<Usuario> usuariosDoDiscente;
	
	/** Armazena o usuário selecionado para a realização de alteração de dados do usuário.*/
	private Usuario usuarioSelecionado;
	
	/** Repetição da senha para confirmar a atualização de senha do usuário.*/
	private String reNovaSenha;
	
	/**
	 * Inicia a busca de discentes ead para alteração de dados.
	 * <br>
	 * Método Invocado pela JSP: 
	 * <ul><li>/sigaa.war/ead/menu.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		return buscaDiscente();
	}

	/**
	 * Método responsável pela busca genérica do discente utilizado pela classe #OperacaoDiscente
	 * @param ead
	 * @return
	 * @throws ArqException
	 */
	private String buscaDiscente() throws ArqException {
		checkRole(SigaaPapeis.SEDIS);
		prepareMovimento(SigaaListaComando.ALTERAR_DADOS_USUARIO_ALUNO);
		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.ALTERAR_DADOS_USUARIO_ALUNO);
		buscaDiscenteMBean.setEad(true);

		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Método responsável por manipular o discente selecionado na tela de busca de discente.
	 * <br>
	 * Método Invocado pela JSP: 
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul> 
	 */
	public String selecionaDiscente() throws ArqException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		usuariosDoDiscente = dao.findByPessoa(obj.getPessoa());
		
		if (isEmpty(usuariosDoDiscente)) {
			addMensagemErro("Não foi encontrado nenhum usuário para o discente selecionado.");
			return null;
		} else if (usuariosDoDiscente.size() > 1) {
			return forward(JSP_USUARIOS);
		} else {
			usuarioSelecionado = usuariosDoDiscente.iterator().next();
			return forward(JSP_DADOS_USUARIO);
		}
	}
	
	/**
	 * Método responsável pela manipulação do usuário selecionado na busca de usuários.
	 * <br>
	 * Método Invocado pela JSP: 
	 * <ul><li>/sigaa.war/ead/discente/usuarios.jsp</li></ul> 
	 * @return
	 * @throws DAOException
	 */
	public String selecionaUsuario() throws DAOException {
		int idUsuario = getParameterInt("idUsuario");
		usuarioSelecionado = getGenericDAO().findByPrimaryKey(idUsuario, Usuario.class);
		return forward(JSP_DADOS_USUARIO);
	}
	
	/**
	 * Método responsável por verificar e atualizar os dados do discente.
	 * <br>
	 * Método Invocado pela JSP: 
	 * <ul><li>/sigaa.war/ead/discente/dados_usuario.jsp</li></ul> 
	 * @return
	 */
	public String alterarDados() {
		if (!EmailValidator.getInstance().isValid(usuarioSelecionado.getEmail())) {
			addMensagemErro("O e-mail digitado é inválido.");
			return null;
		}
		
		if (isEmpty(usuarioSelecionado.getSenha())) {
			usuarioSelecionado.setSenha(null);
		}
		
		if (!isEmpty(usuarioSelecionado.getSenha()) && !usuarioSelecionado.getSenha().equals(reNovaSenha)) {
			addMensagemErro("Nova Senha e sua confirmação não conferem.");
			return null;
		} 
		
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setUsuario(usuarioSelecionado);
		mov.setCodMovimento(SigaaListaComando.ALTERAR_DADOS_USUARIO_ALUNO);
		try {
			execute(mov);
			addMensagemInformation("Dados alterados com sucesso!");
			return cancelar();
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		} catch(Exception e) {
			tratamentoErroPadrao(e);
		}
		return null;
	}
	
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj = discente;
	}

	public List<Usuario> getUsuariosDoDiscente() {
		return usuariosDoDiscente;
	}

	public void setUsuariosDoDiscente(List<Usuario> usuariosDoDiscente) {
		this.usuariosDoDiscente = usuariosDoDiscente;
	}

	public Usuario getUsuarioSelecionado() {
		return usuarioSelecionado;
	}

	public void setUsuarioSelecionado(Usuario usuarioSelecionado) {
		this.usuarioSelecionado = usuarioSelecionado;
	}

	public String getReNovaSenha() {
		return reNovaSenha;
	}

	public void setReNovaSenha(String reNovaSenha) {
		this.reNovaSenha = reNovaSenha;
	}

}
