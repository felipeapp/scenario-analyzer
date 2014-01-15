package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@Entity
@Table(schema="ensino_rede", name = "coordenacao_geral_rede")
public class CoordenacaoGeralRede implements PersistDB {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.coordenacao_seq") })
	@Column(name = "id_coordenacao_geral", nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_programa_rede", nullable=false)
	private ProgramaRede programaRede;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", nullable=false)
	private Pessoa pessoa;

	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cargo_academico", nullable = false)
	private CargoAcademico cargo;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProgramaRede getProgramaRede() {
		return programaRede;
	}

	public void setProgramaRede(ProgramaRede programaRede) {
		this.programaRede = programaRede;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public CargoAcademico getCargo() {
		return cargo;
	}

	public void setCargo(CargoAcademico cargo) {
		this.cargo = cargo;
	}
	
}
