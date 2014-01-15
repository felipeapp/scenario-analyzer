/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/06/2009
 *
 */	
package br.ufrn.sigaa.assistencia.dominio;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Representa o meio de transporte utilizado pelos
 * discentes para acessarem a universidade. Serve como
 * parâmetro para o SAE classificar o grau de carência de
 * discente.
 * 
 * @author Agostinho
 *
 */
@Entity
@Table(schema="sae", name="meio_transporte")
public class TipoMeioTransporte implements PersistDB {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name="id_meio_transporte")
	private int id;
	
	/**
	 * Ex: Carro, Moto, Ônibus, etc. 
	 */
	@Column(name="meio_transporte")
	private String meioTransporte;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMeioTransporte() {
		return meioTransporte;
	}

	public void setMeioTransporte(String meioTransporte) {
		this.meioTransporte = meioTransporte;
	}
}
