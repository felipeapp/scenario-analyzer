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
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Entidade que representa um Coordenador de uma unidade de um programa em rede.
 */
@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "coordenador_unidade")
public class CoordenadorUnidade implements PersistDB {

	/** Identificador do coordenador da unidade*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_coordenador_unidade", nullable = false)
	private int id;
	
	/**Classe responsável pela associação de cursos a instituições de ensino dentro de uma rede de ensino*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_dados_curso", nullable=false)
	private DadosCursoRede dadosCurso;
	
	/**Pessoa do coordenador da unidade*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pessoa", nullable=false)
	private Pessoa pessoa;
	
	/**define se usuario exerce o cardo de COORDENADOR ou VICE-COORDENADOR*/
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_cargo_academico", nullable = false)
	private CargoAcademico cargo;

	/**Instituição de ensino que pertence a unidade*/
	@Transient
	private InstituicoesEnsino instituicao;

	/** Serve para informar se o Coordenador está ativo ou não. */
	@Column(name = "ativo")
	@CampoAtivo
	private boolean ativo;
	
	/**Instituição de ensino que pertence a unidade*/
	@Transient
	private CampusIes campus;
	
	/**Objeto transient com os dados do Usuario do coordenador*/
	@Transient
	private Usuario usuario;
	
	/**Dados exclusivos do usuario enquanto coordenador*/
	@Embedded
	private DadosCoordenacaoUnidade dados = new DadosCoordenacaoUnidade();
	
	/**retorna a descrição do Programa em Rede juntamente com a Silga da instituição e o campus.*/
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
	
	public InstituicoesEnsino getInstituicao() {
		return instituicao;
	}

	public void setInstituicao(InstituicoesEnsino instituicao) {
		this.instituicao = instituicao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public CampusIes getCampus() {
		return campus;
	}

	public void setCampus(CampusIes campus) {
		this.campus = campus;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	

}
