package br.ufrn.sigaa.ensino_rede.academico.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "tipo_movimentacao")
public class TipoMovimentacao implements PersistDB {

	public static final TipoMovimentacao TRANCAMENTO = new TipoMovimentacao(1);

	public TipoMovimentacao() {
	}
	
	public TipoMovimentacao(int id) {
		this.id = id;
	}
	
	/**
	 * chave primária
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name="id_tipo", nullable = false)
	private int id;

	@Column(name="descricao", nullable = false)
	private String descricao;

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
