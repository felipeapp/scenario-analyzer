/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 02/06/2008
 *
 */	
package br.ufrn.sigaa.assistencia.cadunico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa o Item Conforto Familiar
 * 
 * @author agostinho
 *
 */
@Entity 
@Table(name="itens_conforto_familiar", schema="sae")
public class ItemConfortoFamiliar implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name="id_item")
	private int id;
	
	/**
	 * Representa algum objeto físico que representa poder aquisitivo.
	 * Ex: TV, Geladeira, Máquina de Lavar, etc.
	 */
	private String item;
	
	private boolean ativo;

	public ItemConfortoFamiliar() {
	}
	
	public ItemConfortoFamiliar(String item) {
		this.item = item;
	}
	
	public String getItem() {
		return item;
	}
	public void setItem(String item) {
		this.item = item;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
}
