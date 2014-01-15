/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 23/04/2013 
 */
package br.ufrn.sigaa.prodocente.cnpq.wsclient;

import javax.swing.JOptionPane;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.util.CryptUtils;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;

/**
 * Criptografa e armazena os parâmetros das credenciais para acesso ao Web Service do CNPq.
 * 
 * @author Leonardo Campos
 *
 */
public class EncriptarSenhaLattes {

	/**
	 * Criptografa e armazena os parâmetros das credenciais para acesso ao Web Service do CNPq.
	 */
	public static void main(String[] args) {
		Database.setDirectMode();
		
		String userEncript = CryptUtils.crypt(JOptionPane.showInputDialog("Usuário:"), CryptUtils.BLOWFISH);
		String passEncript = CryptUtils.crypt(JOptionPane.showInputDialog("Senha:"), CryptUtils.BLOWFISH);
		
		System.out.println(userEncript);
		System.out.println(passEncript);
		
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getComumDs());
		
		template.update("update comum.parametro set valor = '"+userEncript+"' where codigo = '" + ParametrosProdocente.USER_WS_CURRICULO_LATTES+"'");
		template.update("update comum.parametro set valor = '"+passEncript+"' where codigo = '" + ParametrosProdocente.PASSWD_WS_CURRICULO_LATTES+"'");
		
	}
}
