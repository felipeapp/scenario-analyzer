/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em 13/04/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
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
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.graduacao.dominio.TipoMudancaCurricular;

/**
 * Registro de alteração de Turma
 *
 * @author Igor Linnik
 *
 */
@Entity
@Table(name = "alteracao_turma", schema = "ensino")
public class AlteracaoTurma implements PersistDB, TipoMudancaCurricular {
	
	
	/** Chave primária. */
	@Id @GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	parameters={ @Parameter(name="hibernate_sequence", value="public.hibernate_sequence") })
	@Column(name = "id_alteracao_turma", nullable = false)
	private int id;
	
	/** Turma Alterada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_turma_alterada")
	private Turma turmaAlterada;
	
	/** Turma Alterada */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_agrupadora_anterior")
	private Turma turmaAgrupadoraAnterior;
	
	/** Tipo anterior da turma*/
	@Column(name="tipo_turma_anterior")
	private Integer tipoTurmaAnterior;
	
	/** Campus da turma antes da alteração*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name="id_campus_anterior")
	private CampusIes campusAnterior;
	
	/** Código da turma antes da alteração */	
	@Column(name="codigo_turma_anterior")
	private String codigoTurmaAnterior;
	
	/** Capacidade da turma antes da alteração */	
	@Column(name="capacidade_aluno_anterior")	
	private Integer capacidadeAlunoAnterior;
	
	/** Data início da turma antes da alteração */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="inicio_anterior")
	private Date inicioAnterior;
	
	/** Data fim da turma antes da alteração */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="fim_anterior")
	private Date fimAnterior;
	
	/** Horário anterior da turma antes da alteração */	
	@Column(name="horario_anterior")
	private String horarioAnterior;	
	
	/** Docentes participantes da turma antes da alteração */	
	@Column(name="docentes_anteriores")
	private String docentesAnteriores;
	
	/** Data que ocorreu a alteração */	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="data_alteracao")
	private Date dataAlteracao;
	
	/** Registro de entrada do responsável pela alteração */	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_registro_entrada")	
	private RegistroEntrada registroEntrada;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Integer getTipoTurmaAnterior() {
		return tipoTurmaAnterior;
	}
	public void setTipoTurmaAnterior(Integer tipoTurmaAnterior) {
		this.tipoTurmaAnterior = tipoTurmaAnterior;
	}
	public CampusIes getCampusAnterior() {
		return campusAnterior;
	}
	public void setCampusAnterior(CampusIes campusAnterior) {
		this.campusAnterior = campusAnterior;
	}
	public String getCodigoTurmaAnterior() {
		return codigoTurmaAnterior;
	}
	public void setCodigoTurmaAnterior(String codigoTurmaAnterior) {
		this.codigoTurmaAnterior = codigoTurmaAnterior;
	}
	public Integer getCapacidadeAlunoAnterior() {
		return capacidadeAlunoAnterior;
	}
	public void setCapacidadeAlunoAnterior(Integer capacidadeAlunoAnterior) {
		this.capacidadeAlunoAnterior = capacidadeAlunoAnterior;
	}
	public Date getInicioAnterior() {
		return inicioAnterior;
	}
	public void setInicioAnterior(Date inicioAnterior) {
		this.inicioAnterior = inicioAnterior;
	}
	public Date getFimAnterior() {
		return fimAnterior;
	}
	public void setFimAnterior(Date fimAnterior) {
		this.fimAnterior = fimAnterior;
	}
	public String getHorarioAnterior() {
		return horarioAnterior;
	}
	public void setHorarioAnterior(String horarioAnterior) {
		this.horarioAnterior = horarioAnterior;
	}
	public String getDocentesAnteriores() {
		return docentesAnteriores;
	}
	public void setDocentesAnteriores(String docentesAnteriores) {
		this.docentesAnteriores = docentesAnteriores;
	}
	public Date getDataAlteracao() {
		return dataAlteracao;
	}
	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}
	public RegistroEntrada getRegistroEntrada() {
		return registroEntrada;
	}
	public void setRegistroEntrada(RegistroEntrada registroEntrada) {
		this.registroEntrada = registroEntrada;
	}
	public Turma getTurmaAlterada() {
		return turmaAlterada;
	}
	public void setTurmaAlterada(Turma turmaAlterada) {
		this.turmaAlterada = turmaAlterada;
	}
	public Turma getTurmaAgrupadoraAnterior() {
		return turmaAgrupadoraAnterior;
	}
	public void setTurmaAgrupadoraAnterior(Turma turmaAgrupadoraAnterior) {
		this.turmaAgrupadoraAnterior = turmaAgrupadoraAnterior;
	}
}