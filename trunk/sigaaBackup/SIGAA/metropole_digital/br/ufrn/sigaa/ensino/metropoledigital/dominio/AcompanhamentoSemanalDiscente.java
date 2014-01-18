package br.ufrn.sigaa.ensino.metropoledigital.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.tecnico.dominio.DiscenteTecnico;

/**
 * Esta entidade representa o acompanhamento avaliativo de um discente do IMD para cada per�odo de avalia��o.
 * Ele ir� armazenar as notas de participa��o virtual, presencial e a frequ�ncia do discente
 * dentro de um determinado per�odo de avalia��o.
 * 
 * 
 * @author Rafael Barros
 * 
 */

@Entity
@Table(name = "acompanhamento_semanal_discente", schema = "metropole_digital")
public class AcompanhamentoSemanalDiscente implements PersistDB, Validatable {
	
	/** Chave prim�ria da tabela acompanhamento_semanal_discente **/
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.acompanhamento_semanal_discen_id_acompanhmaneto_semanal_dis_seq") })
	@Column(name = "id_acompanhamento_semanal_discente", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Nota de participa��o presencial do discente em um determinado periodo no encontro presencial **/
	@Column(name = "participacao_presencial")
	private Double participacaoPresencial;

	/** Nota de participa��o virtual presente no Moodle do discente em um determinado periodo, esta nota � atribu�da atrav�s de um c�lculo baseado nas participa��es virtuais do discente no Moodle**/
	@Column(name = "participacao_virtual")
	private Double participacaoVirtual;

	/** Frequencia do discente em um determinado periodo no encontro presencial - 1 = PRESENTE; 0.5 = MEIA PRESEN�A; 0 = FALTA **/
	private Double frequencia;

	/** Periodo de avalia��o que o acompanhamento pertence **/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_periodo_avaliacao")
	private PeriodoAvaliacao periodoAvaliacao = new PeriodoAvaliacao();

	/** Discente t�cnico que o acompanhamento pertence **/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_discente_tecnico")
	private DiscenteTecnico discente = new DiscenteTecnico();
	
	/** Atributo que serve para controlar se a participa��o virtual foi ou n�o sincronizada com o Moodle **/
	@Column(name = "pv_sincronizada")
	private boolean pvSincronizada;

	public AcompanhamentoSemanalDiscente() {
	}

	public AcompanhamentoSemanalDiscente(int id,
			PeriodoAvaliacao periodoAvaliacao, DiscenteTecnico discente) {
		super();
		this.id = id;
		this.periodoAvaliacao = periodoAvaliacao;
		this.discente = discente;
	}

	public AcompanhamentoSemanalDiscente(int id, double participacaoPresencial,
			double participacaoVirtual, double frequencia,
			PeriodoAvaliacao periodoAvaliacao, DiscenteTecnico discente, boolean pvSincronizada) {
		super();
		this.id = id;
		this.participacaoPresencial = participacaoPresencial;
		this.participacaoVirtual = participacaoVirtual;
		this.frequencia = frequencia;
		this.periodoAvaliacao = periodoAvaliacao;
		this.discente = discente;
		this.pvSincronizada = pvSincronizada;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Double getParticipacaoPresencial() {
		return participacaoPresencial;
	}

	public void setParticipacaoPresencial(Double participacaoPresencial) {
		this.participacaoPresencial = participacaoPresencial;
	}

	public Double getParticipacaoVirtual() {
		return participacaoVirtual;
	}

	public void setParticipacaoVirtual(Double participacaoVirtual) {
		this.participacaoVirtual = participacaoVirtual;
	}

	public Double getFrequencia() {
		return frequencia;
	}

	public void setFrequencia(Double frequencia) {
		this.frequencia = frequencia;
	}

	public PeriodoAvaliacao getPeriodoAvaliacao() {
		return periodoAvaliacao;
	}

	public void setPeriodoAvaliacao(PeriodoAvaliacao periodoAvaliacao) {
		this.periodoAvaliacao = periodoAvaliacao;
	}

	public DiscenteTecnico getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteTecnico discente) {
		this.discente = discente;
	}
	
	public boolean isPvSincronizada() {
		return pvSincronizada;
	}

	public void setPvSincronizada(boolean pvSincronizada) {
		this.pvSincronizada = pvSincronizada;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
}
