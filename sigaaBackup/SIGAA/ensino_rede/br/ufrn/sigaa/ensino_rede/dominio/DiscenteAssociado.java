/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Discente associado a uma rede de ensino.
 * @author Henrique André
 *
 */
@Entity
@Table(schema="ensino_rede", name = "discente_associado")
public class DiscenteAssociado implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.discente_seq") })
	@Column(name = "id_discente_associado", nullable = false)
	private int id;
	
	/** Dados pessoais do discente associado. */ 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_pessoa", nullable = false)
	private Pessoa pessoa;
	
	/** Nível de ensino do discente. */
	@Column(name = "nivel", nullable = false)
	private char nivel;
	
	/** Ano de ingresso do discente */
	@Column(name = "ano_ingresso", nullable = false)
	private Integer anoIngresso;
	
	/** Período de ingresso do discente */
	@Column(name = "periodo_ingresso", nullable = false)
	private Integer periodoIngresso;
	
	/** Dados do Curso do discente. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_dados_curso_rede", nullable = false)
	private DadosCursoRede dadosCurso;
	
	/** Situação do discente. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_status", nullable = false)
	private StatusDiscenteAssociado status;
	
	/** Data de cadastro do discente */
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	/** Registro de entrada do discente */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor
	private RegistroEntrada criadoPor;
	
	/** Convocação em que o discente foi cadastrado. */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_convocacao_discente_associado")
	private ConvocacaoDiscenteAssociado convocacao;

	/** Observações que o usuário pode realizar nos discentes */
	private String observacao;
	
	/** Registra os discentes que estão marcados para alteração */
	@Transient
	private boolean selected;
	
	/**
	 * Construtor padrão 
	 */
	public DiscenteAssociado() {
	}
	
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

	public char getNivel() {
		return nivel;
	}
	
	public String getNivelDesc() {
		return NivelEnsino.getDescricao(nivel);
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
	}

	public DadosCursoRede getDadosCurso() {
		return dadosCurso;
	}

	public void setDadosCurso(DadosCursoRede dadosCurso) {
		this.dadosCurso = dadosCurso;
	}

	public StatusDiscenteAssociado getStatus() {
		return status;
	}

	public void setStatus(StatusDiscenteAssociado status) {
		this.status = status;
	}

	public Integer getAnoIngresso() {
		return anoIngresso;
	}

	public void setAnoIngresso(Integer anoIngresso) {
		this.anoIngresso = anoIngresso;
	}

	public Integer getPeriodoIngresso() {
		return periodoIngresso;
	}

	public void setPeriodoIngresso(Integer periodoIngresso) {
		this.periodoIngresso = periodoIngresso;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public RegistroEntrada getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(RegistroEntrada criadoPor) {
		this.criadoPor = criadoPor;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}	

	@Transient
	public String getNome() {
		return ((this.pessoa.getNome() == null) ? "" : this.pessoa.getNome());
	}


	public ConvocacaoDiscenteAssociado getConvocacao() {
		return convocacao;
	}

	public void setConvocacao(ConvocacaoDiscenteAssociado convocacao) {
		this.convocacao = convocacao;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	@Override
	public boolean equals (Object o){
		return EqualsUtil.testEquals(this, o, "id");
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}
	
}
