package br.ufrn.sigaa.monitoria.dominio;

// Generated 09/10/2006 10:44:38 by Hibernate Tools 3.1.0.beta5

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;

/*******************************************************************************
 * <p>
 * Representa um registro de renova��o de projetos de monitoria. Um projeto de
 * monitoria para ser renovado deve ter uma relat�rio parcial submetido a
 * avalia��o dos membros da comiss�o de monitoria
 * </p>
 * 
 * ResumoSid generated by hbm2java 
 ******************************************************************************/
@Entity
@Table(name = "renovacao_projeto_monitoria", schema = "monitoria")
public class RenovacaoProjetoMonitoria implements PersistDB {

	// Fields

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_renovacao_projeto_monitoria")
	private int id;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_renovacao")
	private Date dataRenovacao;

	@Temporal(TemporalType.DATE)
	@Column(name = "data_vigencia")
	private Date dataVigencia;

	@ManyToOne
	@JoinColumn(name = "id_projeto_monitoria")
	private ProjetoEnsino projetoEnsino;

	@ManyToOne
	@JoinColumn(name = "id_edital")
	private EditalMonitoria edital;

	@ManyToOne
	@JoinColumn(name = "id_registro_entrada")
	private RegistroEntrada registroEntrada;

	// Constructors

	/** default constructor */
	public RenovacaoProjetoMonitoria() {
	}

	/** minimal constructor */
	public RenovacaoProjetoMonitoria(int idRenovacaoProjetoMonitoria,
			Date dataRenovacao, ProjetoEnsino projetoEnsino,
			EditalMonitoria edital) {
		this.id = idRenovacaoProjetoMonitoria;
		this.dataRenovacao = dataRenovacao;
		this.projetoEnsino = projetoEnsino;
		this.edital = edital;
	}

	/** full constructor */
	public RenovacaoProjetoMonitoria(int idRenovacaoProjetoMonitoria,
			Date dataRenovacao, Date dataVigencia, ProjetoEnsino projetoEnsino,
			EditalMonitoria edital) {
		this.id = idRenovacaoProjetoMonitoria;
		this.dataRenovacao = dataRenovacao;
		this.dataVigencia = dataVigencia;
		this.projetoEnsino = projetoEnsino;
		this.edital = edital;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idRenovacaoProjetoMonitoria) {
		this.id = idRenovacaoProjetoMonitoria;
	}

	public Date getDataRenovacao() {
		return this.dataRenovacao;
	}

	public void setDataRenovacao(Date dataRenovacao) {
		this.dataRenovacao = dataRenovacao;
	}

	public Date getDataVigencia() {
		return this.dataVigencia;
	}

	public void setDataVigencia(Date dataVigencia) {
		this.dataVigencia = dataVigencia;
	}

	public ProjetoEnsino getProjetoEnsino() {
		return this.projetoEnsino;
	}

	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}

	public EditalMonitoria getEdital() {
		return this.edital;
	}

	public void setEdital(EditalMonitoria edital) {
		this.edital = edital;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

}
