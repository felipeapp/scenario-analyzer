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

	/** JSP de dados de um usu�rio. */
	private static final String JSP_DADOS_USUARIO = "/ead/discente/dados_usuario.jsp";

	/** JSP de sele��o de usu�rios. */
	private static final String JSP_USUARIOS = "/ead/discente/usuarios.jsp";

	/** Lista contendo a lista de usu�rios relacionados ao discente.*/
	private List<Usuario> usuariosDoDiscente;
	
	/** Armazena o usu�rio selecionado para a realiza��o de altera��o de dados do usu�rio.*/
	private Usuario usuarioSelecionado;
	
	/** Repeti��o da senha para confirmar a atualiza��o de senha do usu�rio.*/
	private String reNovaSenha;
	
	/**
	 * Inicia a busca de discentes ead para altera��o de dados.
	 * <br>
	 * M�todo Invocado pela JSP: 
	 * <ul><li>/sigaa.war/ead/menu.jsp</li></ul> 
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String iniciar() throws ArqException {
		return buscaDiscente();
	}

	/**
	 * M�todo respons�vel pela busca gen�rica do discente utilizado pela classe #OperacaoDiscente
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
	 * M�todo respons�vel por manipular o discente selecionado na tela de busca de discente.
	 * <br>
	 * M�todo Invocado pela JSP: 
	 * <ul><li>/sigaa.war/graduacao/busca_discente.jsp</li></ul> 
	 */
	public String selecionaDiscente() throws ArqException {
		UsuarioDao dao = getDAO(UsuarioDao.class);
		usuariosDoDiscente = dao.findByPessoa(obj.getPessoa());
		
		if (isEmpty(usuariosDoDiscente)) {
			addMensagemErro("N�o foi encontrado nenhum usu�rio para o discente selecionado.");
			return null;
		} else if (usuariosDoDiscente.size() > 1) {
			return forward(JSP_USUARIOS);
		} else {
			usuarioSelecionado = usuariosDoDiscente.iterator().next();
			return forward(JSP_DADOS_USUARIO);
		}
	}
	
	/**
	 * M�todo respons�vel pela manipula��o do usu�rio selecionado na busca de usu�rios.
	 * <br>
	 * M�todo Invocado pela JSP: 
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
	 * M�todo respons�vel por verificar e atualizar os dados do discente.
	 * <br>
	 * M�todo Invocado pela JSP: 
	 * <ul><li>/sigaa.war/ead/discente/dados_usuario.jsp</li></ul> 
	 * @return
	 */
	public String alterarDados() {
		if (!EmailValidator.getInstance().isValid(usuarioSelecionado.getEmail())) {
			addMensagemErro("O e-mail digitado � inv�lido.");
			return null;
		}
		
		if (isEmpty(usuarioSelecionado.getSenha())) {
			usuarioSelecionado.setSenha(null);
		}
		
		if (!isEmpty(usuarioSelecionado.getSenha()) && !usuarioSelecionado.getSenha().equals(reNovaSenha)) {
			addMensagemErro("Nova Senha e sua confirma��o n�o conferem.");
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
