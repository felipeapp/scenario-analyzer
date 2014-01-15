
package br.ufrn.shared.wireless.web;

import br.ufrn.arq.web.struts.AbstractForm;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Form para a action de consulta de conex›es da rede wireless
 * 
 * @author David Pereira
 *
 */
public class ConsultaWirelessForm extends AbstractForm {

	public static final int CONSULTA_DATAS = 1;
	public static final int CONSULTA_USUARIO = 2;
	
	private int tipoConsulta = CONSULTA_USUARIO;
	
	private UsuarioGeral usuario = new UsuarioGeral();
	
	private String dataInicio;
	
	private String dataFim;

	/**
	 * @return the dataFim
	 */
	public String getDataFim() {
		return dataFim;
	}

	/**
	 * @param dataFim the dataFim to set
	 */
	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	/**
	 * @return the dataInicio
	 */
	public String getDataInicio() {
		return dataInicio;
	}

	/**
	 * @param dataInicio the dataInicio to set
	 */
	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	/**
	 * @return the tipoConsulta
	 */
	public int getTipoConsulta() {
		return tipoConsulta;
	}

	/**
	 * @param tipoConsulta the tipoConsulta to set
	 */
	public void setTipoConsulta(int tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	/**
	 * @return the usuario
	 */
	public UsuarioGeral getUsuario() {
		return usuario;
	}

	/**
	 * @param usuario the usuario to set
	 */
	public void setUsuario(UsuarioGeral usuario) {
		this.usuario = usuario;
	}		
	
}
