package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import javax.persistence.Column;
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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.sigaa.ensino.tecnico.dominio.OpcaoPoloGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;

/**
 * 
 * Entidade que mapeia o total de vagas por cada um dos tipos de reservas de
 * candidatos.
 * 
 * @author Gleydson Lima, Rafael Barros
 * 
 */

@Entity
@Table(name = "reserva_vaga_processo_seletivo", schema = "metropole_digital")
public class ReservaVagaProcessoSeletivo implements PersistDB, Validatable {

	/**
     * Chave primária
     */
	@Id
	@GeneratedValue(generator = "seqGenerator")
	@GenericGenerator(name = "seqGenerator", strategy = "br.ufrn.arq.dao.SequenceStyleGenerator", parameters = { @Parameter(name = "sequence_name", value = "metropole_digital.id_reserva_vaga_processo_seletivo") })
	@Column(name = "id_reserva_vaga_processo_seletivo", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/**
     * Objeto que corresponde ao processo seletivo em que a reserva de vagas será aplicada
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_processo_seletivo_tecnico")
	private ProcessoSeletivoTecnico processo;

	/**
     * Objeto que corresponde a opção pólo grupo em que a reserva de vagas será aplicada
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_opcao_polo_grupo")
	private OpcaoPoloGrupo opcao;

	/**
     * Objeto que corresponde a opção pólo grupo em que a reserva de vagas será aplicada
     */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_reserva_vaga_grupo")
	private ReservaVagaGrupo tipoReserva;

	@Column ( name="vagas")
	private int vagas;
	
	@Transient
	private int convocados;
	
	@Transient
	private Long remanescentes;
	

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public ProcessoSeletivoTecnico getProcesso() {
		return processo;
	}

	public void setProcesso(ProcessoSeletivoTecnico processo) {
		this.processo = processo;
	}

	public OpcaoPoloGrupo getOpcao() {
		return opcao;
	}

	public void setOpcao(OpcaoPoloGrupo opcao) {
		this.opcao = opcao;
	}

	public ReservaVagaGrupo getTipoReserva() {
		return tipoReserva;
	}

	public void setTipoReserva(ReservaVagaGrupo tipoReserva) {
		this.tipoReserva = tipoReserva;
	}

	public int getVagas() {
		return vagas;
	}

	public void setVagas(int vagas) {
		this.vagas = vagas;
	}
	

	public Long getRemanescentes() {
		return remanescentes;
	}

	public void setRemanescentes(Long remanescentes) {
		this.remanescentes = remanescentes;
	}

	public int getConvocados() {
		return convocados;
	}

	public void setConvocados(int convocados) {
		this.convocados = convocados;
	}

	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}

}