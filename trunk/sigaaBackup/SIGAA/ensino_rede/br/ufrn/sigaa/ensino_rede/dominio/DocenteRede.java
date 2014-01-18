package br.ufrn.sigaa.ensino_rede.dominio;

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
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.annotations.Range;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.pessoa.dominio.Docente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "docente_rede")
public class DocenteRede implements Docente, PersistDB {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_docente_rede", nullable = false)
	private int id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa")
	private Pessoa pessoa;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_tipo")
	private TipoDocenteRede tipo;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_situacao")
	private SituacaoDocenteRede situacao;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dados_curso_rede")
	private DadosCursoRede dadosCurso;
	
	@Transient
	private String cargo;
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public Pessoa getPessoa() {
		return pessoa;
	}
	
	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}
	
	@Override
	public String getIdentificacao() {
		return pessoa.getCpfCnpjFormatado();
	}
	
	@Override
	public String getNome() {
		return pessoa.getNome();
	}
	
	public TipoDocenteRede getTipo() {
		return tipo;
	}
	
	public void setTipo(TipoDocenteRede tipo) {
		this.tipo = tipo;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
	}
	
	public SituacaoDocenteRede getSituacao() {
		return situacao;
	}

	public void setSituacao(SituacaoDocenteRede situacao) {
		this.situacao = situacao;
	}
	
	@Override
	public boolean equals (Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

}