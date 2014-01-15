/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */

package br.ufrn.sigaa.ensino.tecnico.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;

/**
 * Classe que modela o resultado da classificação do candidato no processo seletivo.
 * @author Rafael Gomes
 * @author Fred_Castro
 *
 */
@Entity
@Table(name = "resultado_classificacao_candidato_tecnico", schema = "tecnico")
public class ResultadoClassificacaoCandidatoTecnico implements PersistDB, Validatable {

	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="tecnico.seq_resultado_classificacao_candidato") })
	@Column(name = "id_resultado_classificacao_candidato_tecnico", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	@ManyToOne
	@JoinColumn(name = "id_inscricao_processo_seletivo_tecnico", nullable = false, insertable = true, updatable = true)
	private InscricaoProcessoSeletivoTecnico inscricaoProcessoSeletivo;
	
	@Column(name="argumento_final")
	private Double argumentoFinal;
	
	/** Atributo utilizado para informar em qual classificação o candidato foi aprovado. */
	@Column(name = "classificacao_aprovado")
	private Integer classificacaoAprovado;
		
	@ManyToOne
	@JoinColumn(name = "id_situacao_candidato")
	private SituacaoCandidatoTecnico situacaoCandidato = new SituacaoCandidatoTecnico();
	
	@Column(name = "data_cadastro")
	@CriadoEm
	private Date dataCadastro;
	
	@ManyToOne
	@JoinColumn(name = "id_registro_cadastro")
	@CriadoPor
	private RegistroEntrada registroCadastro;
	
	/** Convocacao anterior do candidato. */
	@Transient
	private ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoAnterior;
	
	
	/**
	 * Minimal Constructor
	 */
	public ResultadoClassificacaoCandidatoTecnico() {
	}
	
	/**
	 * Constructor
	 */
	public ResultadoClassificacaoCandidatoTecnico( int id ) {
		this.id = id;
	}
	
	/** Getters and Setters **/
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @return the classificacaoAprovado
	 */
	public Integer getClassificacaoAprovado() {
		return classificacaoAprovado;
	}

	/**
	 * @param classificacaoAprovado the classificacaoAprovado to set
	 */
	public void setClassificacaoAprovado(Integer classificacaoAprovado) {
		this.classificacaoAprovado = classificacaoAprovado;
	}

	/**
	 * @return the situacaoCandidato
	 */
	public SituacaoCandidatoTecnico getSituacaoCandidato() {
		return situacaoCandidato;
	}

	/**
	 * @param situacaoCandidato the situacaoCandidato to set
	 */
	public void setSituacaoCandidato(SituacaoCandidatoTecnico situacaoCandidato) {
		this.situacaoCandidato = situacaoCandidato;
	}

	/**
	 * @return the dataCadastro
	 */
	public Date getDataCadastro() {
		return dataCadastro;
	}

	/**
	 * @param dataCadastro the dataCadastro to set
	 */
	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	/**
	 * @return the registroCadastro
	 */
	public RegistroEntrada getRegistroCadastro() {
		return registroCadastro;
	}

	/**
	 * @param registroCadastro the registroCadastro to set
	 */
	public void setRegistroCadastro(RegistroEntrada registroCadastro) {
		this.registroCadastro = registroCadastro;
	}


	@Override
	public ListaMensagens validate() {
		return null;
	}

	public InscricaoProcessoSeletivoTecnico getInscricaoProcessoSeletivo() {
		return inscricaoProcessoSeletivo;
	}

	public void setInscricaoProcessoSeletivo(InscricaoProcessoSeletivoTecnico inscricaoProcessoSeletivo) {
		this.inscricaoProcessoSeletivo = inscricaoProcessoSeletivo;
	}

	public Double getArgumentoFinal() {
		return argumentoFinal;
	}

	public void setArgumentoFinal(Double argumentoFinal) {
		this.argumentoFinal = argumentoFinal;
	}

	public ConvocacaoProcessoSeletivoDiscenteTecnico getConvocacaoAnterior() {
		return convocacaoAnterior;
	}

	public void setConvocacaoAnterior(
			ConvocacaoProcessoSeletivoDiscenteTecnico convocacaoAnterior) {
		this.convocacaoAnterior = convocacaoAnterior;
	}
}
