/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa um horário reservado para o uso de 
 * um espaço físico da instituição
 * 
 * @author wendell
 *
 */
@Entity
@Table(name = "reserva_horario", schema = "espaco_fisico")
public class ReservaHorario implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_reserva_horario")
	private int id;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_inicio")
	private Date inicio;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "data_fim")
	private Date fim;

	/** Título do compromisso. */
	@Column(name = "titulo")
	private String titulo;

	/** Descrição textual do compromisso. */
	@Column(name = "descricao")
	private String descricao;

	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_reserva")
	private Reserva reserva;

	@Column(name = "ativo")
	private boolean ativo = true;
	
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "id_repeticao_horario")
	private RepeticaoHorario repeticaoHorario;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getInicio() {
		return inicio;
	}

	public void setInicio(Date inicio) {
		this.inicio = inicio;
	}

	public Date getFim() {
		return fim;
	}

	public void setFim(Date fim) {
		this.fim = fim;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Reserva getReserva() {
		return reserva;
	}

	public void setReserva(Reserva reserva) {
		this.reserva = reserva;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public RepeticaoHorario getRepeticaoHorario() {
		return repeticaoHorario;
	}

	public void setRepeticaoHorario(RepeticaoHorario repeticaoHorario) {
		this.repeticaoHorario = repeticaoHorario;
	}

}
