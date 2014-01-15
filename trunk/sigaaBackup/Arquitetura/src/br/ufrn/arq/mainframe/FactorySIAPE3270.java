/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 21/05/2012
 * Autor:     Johnny Mar�al, R�mulo Augusto
 *
 */
package br.ufrn.arq.mainframe;

import java.util.ResourceBundle;

import br.gov.dpf.dpf3270.SIAPE3270;

/**
 * 
 * Utilizada para definir dinamicamente qual classe de comunica��o com o SIAPE ser� utilizada.
 * A configura��o � feita no arquivo "autenticarHod.properties".
 * Nas situa��es em que o acesso ao sistema estruturante depende de uma conex�o pr�via no
 * Servi�o de Emula��o 3270 via Web Browser (HOD) do SERPRO, o valor do par�metro "autenticar_hod" 
 * deve ser "true". 
 * 
 * @author Johnny Mar�al
 * @author R�mulo Augusto
 */
public class FactorySIAPE3270 {
		
	private static ResourceBundle config = ResourceBundle.getBundle("br.ufrn.arq.mainframe.autenticarHod");
	
	/**
	 * Recupera a inst�ncia correta a partir do par�metro "autenticar_hod" do arquivo de propriedades.
	 * 
	 * @param hostname - Host de conex�o
	 * @param cpf - Login do usu�rio
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
