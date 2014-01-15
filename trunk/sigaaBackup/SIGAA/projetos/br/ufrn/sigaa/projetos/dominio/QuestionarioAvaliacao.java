/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 05/10/2010
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Questionário utilizado para elaboração do modelo de avaliação.
 * 
 * @author ilueny santos
 * @author Geyson Karlos
 *
 */
@Entity
@Table(name = "questionario_avaliacao", schema = "projetos")
public class QuestionarioAvaliacao implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_questionario_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	@CampoAtivo
	private boolean ativo;
	
	@OneToMany(mappedBy = "questionario", fetch=FetchType.LAZY)
	private Collection<ItemAvaliacaoProjeto> itensAvaliacao = new ArrayList<ItemAvaliacaoProjeto>();
	
	
	public QuestionarioAvaliacao() {
	}

	public QuestionarioAvaliacao(Integer id) {
		this.id = id;
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

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

	public Collection<ItemAvaliacaoProjeto> getItensAvaliacao() {
	    return itensAvaliacao;
	}

	public void setItensAvaliacao(Collection<ItemAvaliacaoProjeto> itensAvaliacao) {
	    this.itensAvaliacao = itensAvaliacao;
	}
	
	@Override
	public boolean equals(Object obj) {
	    return EqualsUtil.testEquals(this, obj, "id", "descricao", "itensAvaliacao");
	}

	@Override
	public int hashCode() {
	    return HashCodeUtil.hashAll(id, descricao, itensAvaliacao);
	}

}
