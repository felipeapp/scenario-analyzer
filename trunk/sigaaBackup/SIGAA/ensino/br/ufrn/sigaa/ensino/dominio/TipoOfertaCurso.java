/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 24/05/2007
*/
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade responsável pela definição do tipo de oferta dos cursos;
 * como por exemplo:
 * 		Temporário, Regular
 *
 * @author Victor Hugo
 */

@Entity
@Table(name = "tipo_oferta_curso", schema = "ensino", uniqueConstraints = {})
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class TipoOfertaCurso implements PersistDB {

	/** Constante que informa o tipo do curso */
	public static final int REGULAR  = 2;

	/** TipoOfertaCurso.REGULAR */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_oferta_curso", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Descrição do tipo de oferta do curso. */
	private String descricao;

	public TipoOfertaCurso(int id) {
		this.id = id;
	}

	public TipoOfertaCurso() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
