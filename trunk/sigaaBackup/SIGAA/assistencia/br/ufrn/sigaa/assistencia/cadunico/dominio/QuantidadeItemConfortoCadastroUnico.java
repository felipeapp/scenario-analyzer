/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 02/04/2009
 *
 */	
package br.ufrn.sigaa.assistencia.cadunico.dominio;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa Item de conforto Familiar
 * 
 * @author agostinho
 * 
 */
@Entity
@Table(name = "quantidade_itens_cadastro_unico", schema = "sae")
public class QuantidadeItemConfortoCadastroUnico implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_quantidade")
	private int id;

	private int quantidade;

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_adesao")
	private AdesaoCadastroUnicoBolsa adesao;

	@ManyToOne(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_item")
	private ItemConfortoFamiliar item;

	public QuantidadeItemConfortoCadastroUnico() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}

	public ItemConfortoFamiliar getItem() {
		return item;
	}

	public void setItem(ItemConfortoFamiliar item) {
		this.item = item;
	}

	public AdesaoCadastroUnicoBolsa getAdesao() {
		return adesao;
	}

	public void setAdesao(AdesaoCadastroUnicoBolsa adesao) {
		this.adesao = adesao;
	}

}
