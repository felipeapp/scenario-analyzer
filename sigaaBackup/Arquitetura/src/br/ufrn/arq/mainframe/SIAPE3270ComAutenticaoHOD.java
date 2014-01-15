/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2012
 * Autor:     Rômulo Augusto
 *
 */
package br.ufrn.arq.mainframe;

import br.gov.dpf.dpf3270.SIAPE3270;

/**
 * 
 * Implementação da comunicação com o SIAPE via Mainframe utilizando também
 * autenticação no serviço de Emulação 3270 via Web Browser (HOD) do SERPRO.
 * 
 * @author Johnny Marcal
 * @author Rômulo Augusto
 */
public class SIAPE3270ComAutenticaoHOD extends SIAPE3270 {


	private String cpf;
	private String senhaHOD;
	private String senhaSIAPE;
	
	/**
	 * 
	 * @param hostname - Host de conexão
	 * @param cpf - Login do usuário
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
