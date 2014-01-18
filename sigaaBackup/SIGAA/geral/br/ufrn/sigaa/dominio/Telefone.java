package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que guarda informações de telefones 
 * das diversas localidades do país.
 *
 * @author joab
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "telefone",schema = "comum")
public class Telefone implements PersistDB{

	/**
	 * Constante para tipo de Telefone Fixo
	 * @see #tipo
	 */
	public static final short FIXO = 1;

	/**
	 * Constante para tipo de Telefone Celular
	 * @see #tipo
	 */
	public static final short CELULAR = 2;
		
	/**Representa o Id do telefone*/
	@Id
	@Column(name = "id_telefone", nullable = false)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;

	/**Número do telefone*/
	@Column(name = "numero", nullable = false)
	private String numero;
	
	/**codigo de área*/
	@Column(name = "cod_area", nullable = false)
	private Integer codigoArea;
	
	/**tipo de telefone (CELULAR,FIXO)*/
	@Column(name = "tipo", nullable = false)
	private short tipo;
	
	/**Ramal do telefone usado em algumas instituições*/
	@Column(name = "ramal")
	private Integer ramal = null;
	
	/**
	 * Verifica se o objeto passado por parametro é igual ao objeto atual.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if(obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Telefone other = (Telefone) obj;
		if(this.codigoArea == other.getCodigoArea() && this.numero.equals(other.getNumero()))
			return true;
		return false;
	}
	
	public Integer getCodigoArea() {
		return codigoArea;
	}

	public void setCodigoArea(Integer codigoArea) {
		this.codigoArea = codigoArea;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public short getTipo() {
		return tipo;
	}

	public void setTipo(short tipo) {
		this.tipo = tipo;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Integer getRamal() {
		return ramal;
	}

	public void setRamal(Integer ramal) {
		this.ramal = ramal;
	}
}
