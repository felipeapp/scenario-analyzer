package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

@Entity
@Table(schema = "ensino", name = "tipo_bolsa")
public class TipoBolsas implements Validatable {	
	
	
	public static final int BOLSA_CNPQ = 81;
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_bolsa", nullable = false)
	private int id;
	
	@Column(name = "descricao", unique = false, nullable = true, insertable = true, updatable = true, length = 40)
	private String descricao;
	
	@Column(name = "id_bolsa_sipac")
	private Integer idBolsaSipac;
	
	@Column(name = "nivel")
	private char nivel;
	
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

	public Integer getIdBolsaSipac() {
		return idBolsaSipac;
	}

	public void setIdBolsaSipac(Integer idBolsaSipac) {
		this.idBolsaSipac = idBolsaSipac;
	}
	

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	

}
