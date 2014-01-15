package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;

/**
 * <p>
 * Representa a participa��o de um discente de monitoria em um resumo do SID. Os
 * resumos do SID(Semin�rio de inicia��o � docencia) s�o enviados uma vez por ano e s�o apresentados na semana de
 * ci�ncia e tecnologia da UFRN
 * </p>
 * 
 * <p>
 * � atrav�s da participa��o do resumo do SID que saberemos se o monitor tem
 * direito ao certificado de participa��o na semana de ci�ncia e tecnologia
 * </p>
 * 
 * 
 */
@Entity
@Table(name = "participacao_sid", schema = "monitoria")
public class ParticipacaoSid implements PersistDB {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_participacao_sid")
	private int id;

	@Column(name = "participou")
	private boolean participou;

	@Column(name = "apresentou")
	private boolean apresentou;

	@Column(name = "justificativa")
	private String justificativa = "";

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_discente_monitoria")
	private DiscenteMonitoria discenteMonitoria = new DiscenteMonitoria();

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_resumo_sid")
	private ResumoSid resumoSid = new ResumoSid();
	
	private boolean ativo;

	// Constructors

	/** Default constructor */
	public ParticipacaoSid() {
	}

	/** Minimal constructor */
	public ParticipacaoSid(int id) {
		this.id = id;
	}

	// Property accessors

	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	/**
	 * Justificativa para a n�o participa��o no Resumo Sid. 
	 * 
	 * @return
	 */
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public DiscenteMonitoria getDiscenteMonitoria() {
		return discenteMonitoria;
	}

	public void setDiscenteMonitoria(DiscenteMonitoria discenteMonitoria) {
		this.discenteMonitoria = discenteMonitoria;
	}

	public ResumoSid getResumoSid() {
		return resumoSid;
	}

	public void setResumoSid(ResumoSid resumoSid) {
		this.resumoSid = resumoSid;
	}

	/**
	 * Informa se o discente participou da elabora��o do resumo
	 * 
	 * @return
	 */
	public boolean isParticipou() {
		return participou;
	}

	public void setParticipou(boolean participou) {
		this.participou = participou;
	}

	/**
	 * Informa se o discente apresentou o trabalho no dia do semin�rio,
	 * importante para recebimento do certificado.
	 * 
	 * @return
	 */
	public boolean isApresentou() {
		return apresentou;
	}

	public void setApresentou(boolean apresentou) {
		this.apresentou = apresentou;
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

}
