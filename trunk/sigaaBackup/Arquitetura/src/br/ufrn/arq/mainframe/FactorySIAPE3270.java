/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2012
 * Autor:     Johnny Marçal, Rômulo Augusto
 *
 */
package br.ufrn.arq.mainframe;

import java.util.ResourceBundle;

import br.gov.dpf.dpf3270.SIAPE3270;

/**
 * 
 * Utilizada para definir dinamicamente qual classe de comunicação com o SIAPE será utilizada.
 * A configuração é feita no arquivo "autenticarHod.properties".
 * Nas situações em que o acesso ao sistema estruturante depende de uma conexão prévia no
 * Serviço de Emulação 3270 via Web Browser (HOD) do SERPRO, o valor do parâmetro "autenticar_hod" 
 * deve ser "true". 
 * 
 * @author Johnny Marçal
 * @author Rômulo Augusto
 */
public class FactorySIAPE3270 {
		
	private static ResourceBundle config = ResourceBundle.getBundle("br.ufrn.arq.mainframe.autenticarHod");
	
	/**
	 * Recupera a instância correta a partir do parâmetro "autenticar_hod" do arquivo de propriedades.
	 * 
	 * @param hostname - Host de conexão
	 * @param cpf - Login do usuário
	 * @param senhaHOD - Senha de acesso ao HOD. Mesma utilizada no browser.
	 * @param senhaSIAPE - Senha de acesso ao sistema no terminal
	 * @return
	 */
	public static SIAPE3270 getInstance(String hostname, String cpf, String senhaHOD, String senhaSIAPE){
							
		if(config.getString("autenticar_hod") != null && Boolean.valueOf(config.getString("autenticar_hod"))){
			
			SIAPE3270ComAutenticaoHOD siape3270ComAutenticaoHOD = new SIAPE3270ComAutenticaoHOD(hostname, cpf, senhaHOD, senhaSIAPE);
									
			return siape3270ComAutenticaoHOD;
		}
			
		return new SIAPE3270(hostname);
	}
}
