/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 07/06/2010
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * 
 * @author Geyson Karlos
 *
 *
 */
@Entity
@Table(name = "pergunta_avaliacao", schema = "projetos")
public class PerguntaAvaliacao implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_pergunta_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	@CampoAtivo
	private boolean ativo;
	
	public PerguntaAvaliacao(){}

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

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "descricao");
	}

	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(id, descricao);
	}

}
