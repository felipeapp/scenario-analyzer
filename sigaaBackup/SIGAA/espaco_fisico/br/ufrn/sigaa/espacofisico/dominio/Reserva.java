/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 08/12/2008
 *
 */
package br.ufrn.sigaa.espacofisico.dominio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.seguranca.log.CriadoEm;
import br.ufrn.arq.seguranca.log.CriadoPor;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

@Entity
@Table(name = "reserva", schema = "espaco_fisico")
public class Reserva implements PersistDB {

	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_reserva")
	private int id;

	private String descricao;
	
	private Integer status;
	
	@OneToMany(mappedBy = "reserva", cascade = CascadeType.ALL)
	private List<ReservaHorario> horarios;

	@ManyToOne
	@JoinColumn(name = "id_espaco_fisico")
	private EspacoFisico espacoFisico;
	
	@ManyToOne
	@JoinColumn(name = "id_usuario_solicitante")
	private Usuario usuarioSolicitante;
	
	@ManyToOne
	@JoinColumn(name = "id_turma")
	private Turma turma;

	private boolean ativo = true;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")
	@CriadoPor 
	private RegistroEntrada registroEntrada;
	
	@Column(name="data_cadastro")
	@Temporal(TemporalType.TIMESTAMP)
	@CriadoEm 
	private Date dataCadastro;

	public Reserva() {
		
	}
	
	public Reserva(int id) {
		this();
		this.id = id;
	}
	
	/**
	 * Adiciona um horário na reserva
	 * 
	 * @param horario
	 */
	public void addReservaHorario(ReservaHorario horario) {
		if (horarios == null) {
			horarios = new ArrayList<ReservaHorario>();
		}
		horario.setReserva(this);
		horarios.add(horario);
	}

	public boolean hasHorariosCadastrados() {
		return horarios != null && !horarios.isEmpty();
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public EspacoFisico getEspacoFisico() {
		return espacoFisico;
	}

	public void setEspacoFisico(EspacoFisico espacoFisico) {
		this.espacoFisico = espacoFisico;
	}

	public Usuario getUsuarioSolicitante() {
		return usuarioSolicitante;
	}

	public void setUsuarioSolicitante(Usuario solicitante) {
		this.usuarioSolicitante = solicitante;
	}

	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}

	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public List<ReservaHorario> getHorarios() {
		return horarios;
	}

	public void setHorarios(List<ReservaHorario> horarios) {
		this.horarios = horarios;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

}
