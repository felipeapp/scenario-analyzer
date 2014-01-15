package br.ufrn.sigaa.assistencia.dominio;

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

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 * Esta classe representa uma SOLICITAÇÃO de bolsa auxílio-creche
 * O auxílio-creche destina-se aos alunos de graduação presencial
 * que apresentem situação de vulnerabilidade socioeconômica, com guarda
 * e responsabilidade legal de crianças com idade de 0 a 6 anos.
 *
 */
@Entity
@Table(name = "bolsa_auxilio_creche", schema = "sae")
public class BolsaAuxilioCreche implements Validatable {
	
	/** Chave primária da bolsa de auxilio */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
   	@Column(name = "id_bolsa_auxilio_creche")
	private int id;

	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_bolsa_auxilio")
	private BolsaAuxilio bolsaAuxilio;

	/** Trabalho do candidato à bolsa. */
	@Column(name = "trabalho_candidato")
	private String trabalhoCandidato;
	
	/** Salário do candidato. */
	@Column(name = "salario_candidato")
	private Double salarioCandidato;
	
	/** Trabalho do cônjuge do candidato. */
	@Column(name = "trabalho_conjuge_candidato")
	private String trabalhoConjugeCandidato;
	
	/** Salário do cônjuge do candidato. */
	@Column(name = "salario_conjuge_candidato")
	private Double salarioConjugeCandidato;
	
	/** Número de filhos do candidato. */
	@Column(name = "numero_filhos_candidato")
	private Integer numeroFilhosCandidato;
	
	/** Idade dos filhos do candidato */ 
	@Column(name = "idade_filhos_candidato")
	private String idadeFilhosCandidato;
	
	/** Indica se o candidato recebe ou não pensão. */
	@Column(name = "recebe_pensao")
	private boolean recebePensao;
	
	/** Valor da pensão recebida pelo candidato. */
	@Column(name = "valor_pensao")
	private Double valorPensao;

	/** Indica se o candidato é ou não solteiro. */
	@Column(name = "solteiro")
	private boolean solteiro;

	/** Estados que uma Bolsa Auxílio pode se encontrar. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_periodo_atividade_academica")
	private PeriodoAtividadeAcademica periodoAtividadeAcademica;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public BolsaAuxilio getBolsaAuxilio() {
		return bolsaAuxilio;
	}

	public void setBolsaAuxilio(BolsaAuxilio bolsaAuxilio) {
		this.bolsaAuxilio = bolsaAuxilio;
	}

	public String getTrabalhoCandidato() {
		return trabalhoCandidato;
	}

	public void setTrabalhoCandidato(String trabalhoCandidato) {
		this.trabalhoCandidato = trabalhoCandidato;
	}

	public String getTrabalhoConjugeCandidato() {
		return trabalhoConjugeCandidato;
	}

	public void setTrabalhoConjugeCandidato(String trabalhoConjugeCandidato) {
		this.trabalhoConjugeCandidato = trabalhoConjugeCandidato;
	}

	public Integer getNumeroFilhosCandidato() {
		return numeroFilhosCandidato;
	}

	public void setNumeroFilhosCandidato(Integer numeroFilhosCandidato) {
		this.numeroFilhosCandidato = numeroFilhosCandidato;
	}

	public String getIdadeFilhosCandidato() {
		return idadeFilhosCandidato;
	}

	public void setIdadeFilhosCandidato(String idadeFilhosCandidato) {
		this.idadeFilhosCandidato = idadeFilhosCandidato;
	}

	public boolean isRecebePensao() {
		return recebePensao;
	}

	public void setRecebePensao(boolean recebePensao) {
		this.recebePensao = recebePensao;
	}

	public boolean isSolteiro() {
		return solteiro;
	}

	public void setSolteiro(boolean solteiro) {
		this.solteiro = solteiro;
	}

	public PeriodoAtividadeAcademica getPeriodoAtividadeAcademica() {
		return periodoAtividadeAcademica;
	}

	public void setPeriodoAtividadeAcademica(
			PeriodoAtividadeAcademica periodoAtividadeAcademica) {
		this.periodoAtividadeAcademica = periodoAtividadeAcademica;
	}

	public Double getSalarioCandidato() {
		return salarioCandidato;
	}

	public void setSalarioCandidato(Double salarioCandidato) {
		this.salarioCandidato = salarioCandidato;
	}

	public Double getSalarioConjugeCandidato() {
		return salarioConjugeCandidato;
	}

	public void setSalarioConjugeCandidato(Double salarioConjugeCandidato) {
		this.salarioConjugeCandidato = salarioConjugeCandidato;
	}

	public Double getValorPensao() {
		return valorPensao;
	}

	public void setValorPensao(Double valorPensao) {
		this.valorPensao = valorPensao;
	}

	@Override
	public ListaMensagens validate() {
		return null;
	}

}