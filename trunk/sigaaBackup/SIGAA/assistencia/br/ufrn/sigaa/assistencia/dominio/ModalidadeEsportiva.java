package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/*
 * Classe que representa uma modalidade esportiva.
 */
@Entity
@Table(name = "modalidade_esportiva", schema = "sae")
public class ModalidadeEsportiva implements Validatable {

	public static final int ATLETISMO = 1;
	
	/** Chave primária da bolsa de auxilio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_modalidade_esportiva")
	private int id;

	/** Descrição da modalidade */
	private String descricao;

	public ModalidadeEsportiva() {
		// TODO Auto-generated constructor stub
	}

	public ModalidadeEsportiva(int id) {
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
	public ListaMensagens validate() {
		return null;
	}
	
}