/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 27/04/2012
 */

package br.ufrn.arq.dominio;

/**
 * Classe utilizada para registrar informações do dispositivo móvel que acessou os sistemas.
 * @author Itamir Filho
 *
 */
public class RegistroEntradaDevice implements PersistDB {

	/**
	 * Identificador do registro.
	 */
	private int id;
	
	/**
	 * Registro de entrada com informações gerais do login.
	 */
	private RegistroEntrada registroEntrada;
	
	/**
	 * Informação do device info do dispositivo móvel.
	 */
	private String deviceInfo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public String getDeviceInfo() {
		return deviceInfo;
	}

	public void setDeviceInfo(String deviceInfo) {
		this.deviceInfo = deviceInfo;
	}
}
