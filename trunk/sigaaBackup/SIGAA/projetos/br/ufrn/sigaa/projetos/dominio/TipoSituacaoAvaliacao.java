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

/**
 * Todos os projetos acadêmicos, em sua fase de aprovação, são distribuídos
 * e avaliados por uma comissão específica de consultores.
 * Esta classes representa um tipo de situação para uma avaliação do projeto.
 * 
 * @author Ilueny Santos
 * 
 */
@Entity
@Table(name = "tipo_situacao_avaliacao", schema = "projetos")
public class TipoSituacaoAvaliacao implements PersistDB {

	/** Situações possíveis para uma avaliação de projetos.*/
	public static final int PENDENTE 	= 1;
	public static final int REALIZADA 	= 2;
	public static final int CANCELADA 	= 3;
    
	/** Identificador único do tipo de avaliação.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_situacao_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição do tipo. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Informa se o tipo de avaliação está ativo. */
	@CampoAtivo
	private boolean ativo;
	
	public TipoSituacaoAvaliacao() {
	}
	
	public TipoSituacaoAvaliacao(int id) {
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
	
	@Override
	public String toString() {
		return getDescricao();
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

}
