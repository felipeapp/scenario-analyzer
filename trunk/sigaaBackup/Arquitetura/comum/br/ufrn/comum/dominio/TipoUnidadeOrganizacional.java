/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 11/05/2007
 */
package br.ufrn.comum.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Contem os tipos de unidades do organograma da instituição.
 * Ex.: centro, departamento, pr-reitoria, superintendência, hospital, etc.
 * 
 * @author David Pereira
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name = "tipo_unidade_organizacional", schema="comum")
public class TipoUnidadeOrganizacional implements PersistDB{
	
	//unidades raízes
	public static final int CENTRO  = 1;
	public static final int DEPARTAMENTO = 2;
	public static final int PRO_REITORIA = 4;
	public static final int HOSPITAL = 5;
	public static final int UNIVERSIDADE = 9;
	public static final int BIBLIOTECA = 10;
	
	public static final int[] raizes = {CENTRO, DEPARTAMENTO, PRO_REITORIA, HOSPITAL, UNIVERSIDADE, BIBLIOTECA};
	
	@Id
	@Column(name = "id_tipo_unidade_organizacional")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	/** Identificador */
	private int id;
	
	/** Nome do tipo de unidade organizacional */
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

