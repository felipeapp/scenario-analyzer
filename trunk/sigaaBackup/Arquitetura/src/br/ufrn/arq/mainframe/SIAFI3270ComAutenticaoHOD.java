/**
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/05/2012
 * Autor:     Johnny Marçal
 *
 */
package br.ufrn.arq.mainframe;

import br.gov.dpf.dpf3270.SIAFI3270;
import br.ufrn.arq.util.CalendarUtils;

/**
 * 
 * Implementação da comunicação com o SIAFI via Mainframe utilizando também
 * autenticação no serviço de Emulação 3270 via Web Browser (HOD) do SERPRO.
 * 
 * @author Johnny Marçal
 *
 */
public class SIAFI3270ComAutenticaoHOD extends SIAFI3270 {

	private String cpf;
	private String senhaHOD;
	private String senhaSIAFI;
	
	public SIAFI3270ComAutenticaoHOD(String hostname, String cpf,
			String senhaHOD, String senhaSIAFI) {
		
		super(hostname);
		this.cpf = cpf;
		this.senhaHOD = senhaHOD;
		this.senhaSIAFI = senhaSIAFI;
	}
	
	public void login() throws Exception {

		String LU = ConexaoHODSerproHelper.conectar(cpf, senhaHOD);
		hostName = LU + "@" + hostName + ":23000";
			
		setSERPROParam(this.cpf, this.senhaHOD, "SIAFI");
		setSystemParam (this.cpf, this.senhaSIAFI, "SIAFI"+CalendarUtils.getAnoAtual());		
		
		super.login();

	}

}
