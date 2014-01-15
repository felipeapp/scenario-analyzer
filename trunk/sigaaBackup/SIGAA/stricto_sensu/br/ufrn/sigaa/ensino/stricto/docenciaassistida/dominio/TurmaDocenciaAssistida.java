/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/03/2010
 */
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio;

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

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Entidade que representa as Turmas do Plano de Docência Assistida Enviado pelo Docente.
 * 
 * (Docência assistida é a atuação do pós-graduando em atividades acadêmicas sob a 
 * supervisão direta de professor do quadro efetivo da UFRN, de acordo com plano aprovado 
 * pelo colegiado e pelo departamento responsável pelo componente curricular.)
 * 
 * @author Arlindo Rodrigues
 *
 */
@Entity
@Table(name="turma_docencia_assistida", schema="stricto_sensu")
public class TurmaDocenciaAssistida implements PersistDB {
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_turma_docencia_assistida")
	private int id;
	
	/** Plano de Docência Assistida. */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_plano_docencia_assistida")
	private PlanoDocenciaAssistida planoDocenciaAssistida;
	
	/** Turma */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_turma")
	private Turma turma;
	
	/** Componente Curricular da turma */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_disciplina")
	private ComponenteCurricular componenteCurricular;
	 
	/** Curso com reserva para a turma */
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="id_curso")
	private Curso curso;	
	
	/** Data de início da turma */
	@Column(name="data_inicio")
	private Date dataInicio;
	
	/** Data de fim da turma */
	@Column(name="data_fim")
	private Date dataFim;	

	public TurmaDocenciaAssistida() {
		super();
	}
	
	public TurmaDocenciaAssistida(int id) {
		this();
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public PlanoDocenciaAssistida getPlanoDocenciaAssistida() {
		return planoDocenciaAssistida;
	}

	public void setPlanoDocenciaAssistida(
			PlanoDocenciaAssistida planoDocenciaAssistida) {
		this.planoDocenciaAssistida = planoDocenciaAssistida;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public ComponenteCurricular getComponenteCurricular() {
		return componenteCurricular;
	}

	public void setComponenteCurricular(ComponenteCurricular componenteCurricular) {
		this.componenteCurricular = componenteCurricular;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}	
	
	public String getNomeDiscente() {
		return planoDocenciaAssistida.getDiscente().getNome();
	}
	
	public String getNomeDiscenteAbreviado() {
		return planoDocenciaAssistida.getDiscente().getPessoa().getPrimeiroNome();
	}
	
	@Override
	public String toString() {
		return  getTurma().getDescricaoResumida() + ": " + getNomeDiscente();
	}
}
