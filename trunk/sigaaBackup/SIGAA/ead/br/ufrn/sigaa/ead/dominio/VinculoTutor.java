/**
 * 
 */
package br.ufrn.sigaa.ead.dominio;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Representa o tipo de vínculo que um tutor tem
 * com a UFRN.
 * 
 * @author David Pereira
 *
 */
@Entity
@Table(name="vinculo_tutor", schema="ead")
public class VinculoTutor implements PersistDB {

	@Id
	@GeneratedValue(strategy=GenerationType.SEQUENCE)
	private int id;
	
	/** Nome do vínculo */
	private String nome;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
