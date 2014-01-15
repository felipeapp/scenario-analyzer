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
 * Representa a participação de um discente de monitoria em um resumo do SID. Os
 * resumos do SID(Seminário de iniciação à docencia) são enviados uma vez por ano e são apresentados na semana de
 * ciência e tecnologia da UFRN
 * </p>
 * 
 * <p>
 * É através da participação do resumo do SID que saberemos se o monitor tem
 * direito ao certificado de participação na semana de ciência e tecnologia
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
	 * Justificativa para a não participação no Resumo Sid. 
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
	 * Informa se o discente participou da elaboração do resumo
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
	 * Informa se o discente apresentou o trabalho no dia do seminário,
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
