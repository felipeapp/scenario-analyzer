/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 26/03/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.sigaa.ensino.stricto.dominio.ConceitoNota;

/**
 * Registra uma retificação de alguma matrícula, alterando nota e/ou faltas
 *
 * @author Andre M Dantas
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "retificacao_matricula", schema = "ensino")
public class RetificacaoMatricula implements PersistDB {

	/** Id referente a {@link RetificacaoMatricula}.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="public.hibernate_sequence") }) 
	@Column(name = "id_retificacao_matricula", nullable = false)
	private int id;

	// Campos que podem ser alterados pelo usuário
	
	/**  Média final registrada na matrícula antes da retificação. */
	private Double mediaFinalAntiga;

	/** Número de faltas registrada na matrícula antes da retificação. */
	private Integer numeroFaltasAntigo;

	/** Apartamento antigo. */
	@Deprecated	
	private Boolean aptoAntigo;

	/** Conceito que o aluno tinha antes da retificação. */
	private Double conceitoAntigo;
	// FIM campos que podem ser alterados pelo usuário

	//campos que podem ser alterados pelo sistema depois da alteração do usuário
	
	/** Situação em que se encontrava a matrícula antes da retificação. */
	@ManyToOne()
	@JoinColumn(name = "id_situacao")
	private SituacaoMatricula situacaoAntiga;

	//  FIM campos que podem ser alterados pelo sistema depois da alteração do usuário

	// Campos para controle da administração do sistema
	
	/** Data em que a retificação foi cadastrada. */
	@Temporal(TemporalType.TIMESTAMP)
	private Date data;

	/** Matrícula cujos dados foram retificados. */
	@ManyToOne()
	@JoinColumn(name = "id_matricula")
	private MatriculaComponente matriculaAlterada;

	/** Registro de entrada do usuário que cadastrou a {@link RetificacaoMatricula} */
	@ManyToOne()
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	public RetificacaoMatricula() {
		super();
	}

	public RetificacaoMatricula(MatriculaComponente mat) {
		matriculaAlterada = mat;
		mediaFinalAntiga = mat.getMediaFinal();
		numeroFaltasAntigo = mat.getNumeroFaltas();
		aptoAntigo = mat.getApto();
		conceitoAntigo = mat.getConceito();
		situacaoAntiga = mat.getSituacaoMatricula();
	}

	public Boolean getAptoAntigo() {
		return aptoAntigo;
	}

	public void setAptoAntigo(Boolean aptoAntigo) {
		this.aptoAntigo = aptoAntigo;
	}

	public Double getConceitoAntigo() {
		return conceitoAntigo;
	}

	/**
	 * Retorna o char refetente ao {@link ConceitoNota} do aluno antes da retificação.
	 * @return
	 */
	public Character getConceitoAntigoChar() {
		if( conceitoAntigo == null )
			return null;

		return ConceitoNota.getDescricao(conceitoAntigo).charAt(0);
	}

	public void setConceitoAntigo(Double conceitoAntigo) {
		this.conceitoAntigo = conceitoAntigo;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public MatriculaComponente getMatriculaAlterada() {
		return matriculaAlterada;
	}

	public void setMatriculaAlterada(MatriculaComponente matriculaAlterada) {
		this.matriculaAlterada = matriculaAlterada;
	}

	public Double getMediaFinalAntiga() {
		return mediaFinalAntiga;
	}

	public void setMediaFinalAntiga(Double mediaFinalAntiga) {
		this.mediaFinalAntiga = mediaFinalAntiga;
	}

	public Integer getNumeroFaltasAntigo() {
		return numeroFaltasAntigo;
	}

	public void setNumeroFaltasAntigo(Integer numeroFaltasAntigo) {
		this.numeroFaltasAntigo = numeroFaltasAntigo;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public SituacaoMatricula getSituacaoAntiga() {
		return situacaoAntiga;
	}

	public void setSituacaoAntiga(SituacaoMatricula situacaoAntiga) {
		this.situacaoAntiga = situacaoAntiga;
	}

}
