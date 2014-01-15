 /*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 04/04/2012
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;

/**
 * Classe que representa uma Movimentação sobre objetos FaltaDocente
 * 
 * @author Fred_Castro
 * 
 */

public class MovimentoDadosTurmasCT extends AbstractMovimentoAdapter {
	
	/** Ano para consulta */
	private int ano;
	
	/** Periodo para consulta */
	private int periodo;
	
	/** Login do usuário consultando */
	private String login;
	
	/** Senha do usuário consultando */
	private String senha;
	
	/** Instância da requisição para ser passada ao processado */
	private HttpServletRequest request;
	
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	public int getPeriodo() {
		return periodo;
	}
	public void setPeriodo(int periodo) {
		this.periodo = periodo;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getSenha() {
		return senha;
	}
	public void setSenha(String senha) {
		this.senha = senha;
	}
	public HttpServletRequest getRequest() {
		return request;
	}
	public void setRequest(HttpServletRequest request) {
		this.request = request;
	}
}