/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Data de Criação: 03/06/2008 
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Classe para permitir que administradores do DAE realizem a
 * recuperação de senhas de discentes.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("session")
public class RecuperarSenhaDiscenteMBean extends SigaaAbstractController<DiscenteAdapter> implements OperadorDiscente {

	private Usuario usuario;
	
	private List<Usuario> usuarios;
	
	private String novaSenha;
	
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE);
		
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.RECUPERACAO_SENHA_DISCENTE);
		return buscaDiscenteMBean.popular();
	}
	
	public String selecionaDiscente() throws ArqException {
		prepareMovimento(SigaaListaComando.RECUPERAR_SENHA_DISCENTE);
		
		novaSenha = null;
		usuarios = getDAO(UsuarioDao.class).findByPessoa(obj.getPessoa());
		
		if (!isEmpty(usuarios)) {
			if (usuarios.size() == 1) {
				usuario = usuarios.iterator().next();
				return forward("/graduacao/recuperar_senha/recuperar_senha.jsp");
			} else {
				return forward("/graduacao/recuperar_senha/lista_logins.jsp");
			}
		} else {
			addMensagemErro("Nenhum usuário foi encontrado para o discente selecionado.");
			return null;
		}
		
	}
	
	public String selecionaUsuario() throws DAOException {
		int id = getParameterInt("id");
		usuario = getGenericDAO().findByPrimaryKey(id, Usuario.class);
		return forward("/graduacao/recuperar_senha/recuperar_senha.jsp");
	}

	public String recuperar() throws ArqException {
		
		try {
			MovimentoCadastro mov = new MovimentoCadastro();
			mov.setCodMovimento(SigaaListaComando.RECUPERAR_SENHA_DISCENTE);
			mov.setRequest(getCurrentRequest());
			mov.setObjMovimentado(usuario);
			
			novaSenha = (String) execute(mov);
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
		}
		
		return null;
	}
	
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.obj = discente;
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

	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}

	public String getNovaSenha() {
		return novaSenha;
	}

	public void setNovaSenha(String novaSenha) {
		this.novaSenha = novaSenha;
	}
	
}
