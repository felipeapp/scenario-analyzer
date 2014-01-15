package br.ufrn.sigaa.estagio.dominio;
/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 01/04/2013
 *
 */

import static br.ufrn.arq.util.ValidatorUtil.validaOrdemTemporalDatas;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.util.Date;

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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/** Define um horário que o discente trabalhará no estágio.
 * @author Édipo Elder F. de Melo
 *
 */
@Entity
@Table(name = "horario_estagio", schema = "estagio")
public class HorarioEstagio implements Validatable, Comparable<HorarioEstagio> {

	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_horario_estagio")		
	private int id;
	
	/** Dia da semana: Ex, 2 (segunda),3 (terca),4 (quarta) */
	private char dia;
	
	/** Hora que o turno inicia */
	@Column(name = "hora_inicio")
	private Date horaInicio;

	/** Hora que turno termina */
	@Column(name = "hora_fim")
	private Date horaFim;
	
	/** Estagiário ao qual este horário se refere. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="id_estagiario", nullable = false)
	private Estagiario estagio;

	/**
	 * Construtor Padrão
	 */
	public HorarioEstagio() {
		
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public HorarioEstagio(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param dia
	 */
	public HorarioEstagio(int id, char dia) {
		this(id);
		this.dia = dia;
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		validateRequired(dia, "Dia da semana", lista);
		validateRequired(horaInicio, "Hora inicial", lista);
		validateRequired(horaFim, "Hora de final", lista);
		validaOrdemTemporalDatas(horaInicio, horaFim, true, "Hora de início e fim", lista);
		return lista;
	}

	public char getDia() {
		return dia;
	}

	public void setDia(char dia) {
		this.dia = dia;
	}

	public Date getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Date horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Date getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Date horaFim) {
		this.horaFim = horaFim;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll("id");
	}

	public Estagiario getEstagio() {
		return estagio;
	}

	public void setEstagio(Estagiario estagio) {
		this.estagio = estagio;
	}

	@Override
	public int compareTo(HorarioEstagio other) {
		int cmp = this.dia - other.dia;
		if (cmp == 0)
			cmp = (int) (this.horaInicio.getTime() - other.horaInicio.getTime());
		return cmp;
	}
	
	/** Retorna uma representação textual do dia. Ex.: 2 -> segunda-feira, 3 -> terça-feira, etc.
	 * @return
	 */
	public String getDescricaoDia() {
		switch (dia) {
		case '2': return "segunda-feira";
		case '3': return "terça-feira";
		case '4': return "quarta-feira";
		case '5': return "quinta-feira";
		case '6': return "sexta-feira";
		case '7': return "sábado";
		default:
			return null;
		}
	}
}
