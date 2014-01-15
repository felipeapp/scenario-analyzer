package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

@Entity
@Table(name = "curriculo_componente_rede", schema = "ensino_rede")
public class CurriculoComponenteRede implements PersistDB {
	
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
           parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_curriculo_componente_rede")
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_componente_curricular")
	private ComponenteCurricularRede componente;
	
	@ManyToOne
	@JoinColumn(name = "id_curriculo_rede")
	private CurriculoRede curriculo;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ComponenteCurricularRede getComponente() {
		return componente;
	}

	public void setComponente(ComponenteCurricularRede componente) {
		this.componente = componente;
	}

	public CurriculoRede getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(CurriculoRede curriculo) {
		this.curriculo = curriculo;
	}

}
