/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2012
 * Autor:     R�mulo Augusto
 *
 */
package br.ufrn.arq.mainframe;

import br.gov.dpf.dpf3270.SIAPE3270;

/**
 * 
 * Implementa��o da comunica��o com o SIAPE via Mainframe utilizando tamb�m
 * autentica��o no servi�o de Emula��o 3270 via Web Browser (HOD) do SERPRO.
 * 
 * @author Johnny Marcal
 * @author R�mulo Augusto
 */
public class SIAPE3270ComAutenticaoHOD extends SIAPE3270 {


	private String cpf;
	private String senhaHOD;
	private String senhaSIAPE;
	
	/**
	 * 
	 * @param hostname - Host de conex�o
	 * @param cpf - Login do usu�rio
	 * @param senhaHOD - Senha de acesso ao HOD. Mesma utilizada no browser.
	 * @param senhaSIAPE - Senha de acesso ao sistema no terminal
	 */
	public SIAPE3270ComAutenticaoHOD(String hostname, String cpf, String senhaHOD, String senhaSIAPE) {
		super(hostname);
		this.cpf = cpf;
		this.senhaHOD = senhaHOD;
		this.senhaSIAPE = senhaSIAPE;
	}
	
	public void login() throws Exception {

		String LU = ConexaoHODSerproHelper.conectar(cpf, senhaHOD);
		hostName = LU + "@" + hostName + ":23000";

		setSERPROParam(this.cpf, this.senhaSIAPE, "SIAPE");
		super.login();

	}

}
