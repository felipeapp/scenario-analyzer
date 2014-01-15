package br.ufrn.rh.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.AbstractMovimento;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.comum.dominio.AmbienteOrganizacionalUnidade;

/**
 * Classe que representa os ambientes.
 * @author Itamir
 *
 */
@Entity
@Table(schema="comum", name = "atividade_ambiente")
public class AtividadeAmbiente extends AbstractMovimento implements PersistDB{
	
	@Id
	@Column(name = "id_atividade_ambiente")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_ambiente")
	private AmbienteOrganizacionalUnidade ambiente;

	private int atividade;
	
	/** Atributo transiente usado para auxiliar o caso de uso de cadastrar Processo de trabalho */
	@Transient
	private boolean checkd;
	
	private String descricao;

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public AmbienteOrganizacionalUnidade getAmbiente() {
		return ambiente;
	}

	public void setAmbiente(AmbienteOrganizacionalUnidade ambiente) {
		this.ambiente = ambiente;
	}

	public int getAtividade() {
		return atividade;
	}

	public void setAtividade(int atividade) {
		this.atividade = atividade;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setCheckd(boolean checkd) {
		this.checkd = checkd;
	}

	public boolean isCheckd() {
		return checkd;
	}

}
