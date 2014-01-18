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
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;

/**
 * Entidade que associa um candidato inscrito no processo seletivo do IMD com o
 * grupo específico de reserva de vagas.
 * 
 * @author Gleydson Lima, Rafael Barros
 * 
 */

@Entity
@Table(name = "reserva_vaga_candidato", schema = "metropole_digital")
public class ReservaVagaCandidato implements PersistDB, Validatable {

	/**
     * Chave primária
     */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.id_reserva_vaga_candidato") })
	@Column(name = "id_reserva_vaga_candidato", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
     * Objeto correspondente a inscrição e ao candidato ao curso do IMD
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_inscricao_processo_seletivo_tecnico")
	private InscricaoProcessoSeletivoTecnico candidato;

	/**
     * Objeto correspondente ao grupo de reserva de vaga na seleção
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_reserva_vaga_grupo")
	private ReservaVagaGrupo tipo;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public InscricaoProcessoSeletivoTecnico getCandidato() {
		return candidato;
	}

	public void setCandidato(InscricaoProcessoSeletivoTecnico candidato) {
		this.candidato = candidato;
	}

	public ReservaVagaGrupo getTipo() {
		return tipo;
	}

	public void setTipo(ReservaVagaGrupo tipo) {
		this.tipo = tipo;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

}
