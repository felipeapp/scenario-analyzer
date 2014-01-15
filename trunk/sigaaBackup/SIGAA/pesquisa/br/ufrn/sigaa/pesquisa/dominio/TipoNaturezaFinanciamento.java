/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */
package br.ufrn.sigaa.pesquisa.dominio;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Classe que registra os tipos de natureza de financiamentos possíveis para um projeto
 * 
 * @author Ricardo
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "tipo_natureza_financ", schema = "pesquisa", uniqueConstraints = {})
public class TipoNaturezaFinanciamento implements PersistDB {

	// Fields    

	private int id;

	private String descricao;

	// Constructors

	/** default constructor */
	public TipoNaturezaFinanciamento() {
	}

	/** default minimal constructor */
	public TipoNaturezaFinanciamento(int id) {
		this.id = id;
	}
	
	/** full constructor */
	public TipoNaturezaFinanciamento(int idNaturezaFinanc, String descricao) {
		this.id = idNaturezaFinanc;
		this.descricao = descricao;
	}

	// Property accessors
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_tipo_natureza_financ", unique = true, nullable = false, insertable = true, updatable = true)
	public int getId() {
		return this.id;
	}

	public void setId(int idNaturezaFinanc) {
		this.id = idNaturezaFinanc;
	}

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "descricao");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}

}
