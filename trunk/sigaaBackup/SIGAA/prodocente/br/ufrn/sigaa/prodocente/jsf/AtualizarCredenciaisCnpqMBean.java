/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Criado em: 06/05/2013 
 */
package br.ufrn.sigaa.prodocente.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.ConfiguracaoAmbienteException;
import br.ufrn.arq.util.CryptUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;

/**
 * Respons�vel pela configura��o/atualiza��o das credenciais da institui��o para acesso
 * aos Web Services do CNPq.
 * 
 * @author Leonardo Campos
 *
 */
@Component
@Scope("request")
public class AtualizarCredenciaisCnpqMBean extends SigaaAbstractController<Object>{

	/** Campos do formul�rio. */
	private String usuario, senha, confirmaSenha;
	
	/**
	 * Inicializa os campos e encaminha para o formul�rio de atualiza��o das credenciais. 
	 * 
	 * <br>
	 * JSPs:
	 * <ul> 
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/abas/administracao.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciar() {
		clear();
		return forward("/prodocente/wsclient/credenciais_ws.jsf");
	}
	
	/**
	 * Limpa os campos do formul�rio.
	 */
	private void clear() {
		usuario = "";
		senha = "";
		confirmaSenha = "";
	}

	/**
	 * Confirma a submiss�o do formul�rio, atualizando os par�metros com as credenciais.
	 * <br>
	 * JSPs:
	 * <ul> 
	 * 	<li>/SIGAA/app/sigaa.ear/sigaa.war/prodocente/wsclient/credenciais_ws.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws Exception 
	 */
	public String confirmar() throws ArqException {
		
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getComumDs());
		
		try {
			if(!ValidatorUtil.isEmpty(usuario)){
				String userEncript = CryptUtils.crypt(usuario, CryptUtils.BLOWFISH);
				template.update("update comum.parametro set valor = '"+userEncript+"' where codigo = '"+ ParametrosProdocente.USER_WS_CURRICULO_LATTES +"'");
			} else {
				addMensagemErro("Usu�rio n�o informado corretamente.");
			}
			
			if(!ValidatorUtil.isEmpty(senha) && !ValidatorUtil.isEmpty(confirmaSenha) && senha.equals(confirmaSenha)){
				String passEncript = CryptUtils.crypt(senha, CryptUtils.BLOWFISH);
				template.update("update comum.parametro set valor = '"+passEncript+"' where codigo = '"+ ParametrosProdocente.PASSWD_WS_CURRICULO_LATTES +"'");
			} else {
				addMensagemErro("Senha e/ou sua confirma��o n�o informados corretamente.");
			}
		} catch (ConfiguracaoAmbienteException e) {
			notifyError(e);
			throw new ConfiguracaoAmbienteException(e.getMessage());
		}
		
		if(hasErrors() || !confirmaSenha())
			return null;
		
		addMensagemInformation("Opera��o realizada com sucesso!");
		
		return cancelar();
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getConfirmaSenha() {
		return confirmaSenha;
	}

	public void setConfirmaSenha(String confirmaSenha) {
		this.confirmaSenha = confirmaSenha;
	}
	
}
