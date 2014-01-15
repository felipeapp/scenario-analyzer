/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 25/06/2008
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;

/**
 *	Esta entidade específica o tipo de oferta de disciplinas de um curso
 * Ex: Anual, Bimestral, Trimestral
 * @author Victor Hugo
 */
@Entity
@Table(name = "tipo_oferta_disciplina", schema = "ensino", uniqueConstraints = {})
@Cache(usage=CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class TipoOfertaDisciplina implements PersistDB {

	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE)
	@Column(name = "id_tipo_oferta_disciplina")
	private int id;

	private String denominacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDenominacao() {
		return denominacao;
	}

	public void setDenominacao(String denominacao) {
		this.denominacao = denominacao;
	}

}
