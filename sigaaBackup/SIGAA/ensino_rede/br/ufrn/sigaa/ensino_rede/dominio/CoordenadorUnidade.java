package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Embedded;
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

@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "coordenador_unidade")
public class CoordenadorUnidade implements PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.coordenacao_seq") })
	@Column(name = "id_coordenador_unidade", nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_dados_curso", nullable=false)
	private DadosCursoRede dadosCurso;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", nullable=false)
	private Pessoa pessoa;
	
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cargo_academico", nullable = false)
	private CargoAcademico cargo;

	@Embedded
	private DadosCoordenacaoUnidade dados;
	
	public String getDescricaoDetalhado() {
		
		StringBuilder str = new StringBuilder();
		
		if (getDadosCurso() == null) 
			return "";
		
		if (getDadosCurso().getProgramaRede() != null)
			str.append(getDadosCurso().getProgramaRede().getDescricao() + " - ");
		if (getDadosCurso().getCampus() != null && getDadosCurso().getCampus().getInstituicao() != null)
			str.append(getDadosCurso().getCampus().getInstituicao().getSigla() + "/");
		
		if (getDadosCurso().getCampus() != null)
			str.append(getDadosCurso().getCampus().getSigla());
			
		return str.toString();
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
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

	public DadosCoordenacaoUnidade getDados() {
		return dados;
	}

	public void setDados(DadosCoordenacaoUnidade dados) {
		this.dados = dados;
	}

}
