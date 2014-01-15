/**
 *
 */
package br.ufrn.rh.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.comum.dominio.Unidade;

/**
 * 
 * Classe que contém dados sobre as condições de trabalho de uma unidade
 * organizacional
 * 
 * @author Itamir
 *
 */

@Entity
@Table(schema="comum", name = "competencias_setor")
public class CompetenciasSetor extends AbstractMovimento implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="comum.competencia_setor_seq") })
	private int id;

	/** Unidade que possui a competência */
	@ManyToOne
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;
	
	/** Usuário de cadastro */
	@Column(name = "id_usuario")
	private int idUsuario;

	/** Data de cadastro */
	@Column(name = "data_cadastro")
	private Date dataCadastro;

	/** Descrição da competência */
	@Column(name = "descricao", columnDefinition= HibernateUtils.TEXT_COLUMN_DEFINITION)
	private String descricao;
	
	/**
	 * Frequência da competência.
	 */
	@ManyToOne
	@JoinColumn(name = "id_frequencia_atribuicao_unidade")
	private FrequenciaAtribuicaoUnidade frequencia;
	
	/**
	 * Nível de complexidade da competência.
	 */
	@ManyToOne
	@JoinColumn(name = "id_nivel_complexidade_atribuicao_unidade")
	private NivelComplexidadeAtribuicaoUnidade nivelComplexidade;

	@Transient
	/** Atributo utilizado para o manuseamento de datatable - JSF */
	boolean selecionado;
	
	/** Quantidade de atribuições associada. */
	@Transient
	private int quantidade;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public int getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(int idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CompetenciasSetor other = (CompetenciasSetor) obj;
		if (id != other.id)
			return false;
		return true;
	}

	public FrequenciaAtribuicaoUnidade getFrequencia() {
		return frequencia;
	}
	
	public void setFrequencia( FrequenciaAtribuicaoUnidade frequencia ) {
		this.frequencia = frequencia;
	}
	
	public NivelComplexidadeAtribuicaoUnidade getNivelComplexidade() {
		return nivelComplexidade;
	}

	public void setNivelComplexidade( NivelComplexidadeAtribuicaoUnidade nivelComplexidade ) {
		this.nivelComplexidade = nivelComplexidade;
	}

	public int getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(int quantidade) {
		this.quantidade = quantidade;
	}
	
	
	
}