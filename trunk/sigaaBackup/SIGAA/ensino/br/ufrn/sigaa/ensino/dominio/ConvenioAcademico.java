/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 26/02/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Convênio estabelecidos com cursos. por exemplo PRO-BASICA
 *
 * @author André
 *
 */
@Entity
@Table(name = "convenio_academico", schema = "ensino")
@Cache ( usage = CacheConcurrencyStrategy.READ_ONLY )
public class ConvenioAcademico implements PersistDB {

	public static final int PROBASICA = 1;
	public static final int PRONERA = 2;
	public static final int PARFOR = 3;

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_convenio_academico", nullable = false)
	private int id;

	private String descricao;

	public ConvenioAcademico() {
	}
	public ConvenioAcademico(int convenio) {
		id = convenio;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String denominacao) {
		this.descricao = denominacao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Transient
	public boolean isProBasica() {
		return id == PROBASICA;
	}
	
	@Transient
	public boolean isParfor() {
		return id == PARFOR;
	}

}
