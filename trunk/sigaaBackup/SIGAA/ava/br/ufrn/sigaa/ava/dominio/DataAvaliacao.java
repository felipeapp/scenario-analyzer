/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.dominio;

import java.util.Calendar;
import java.util.Date;

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

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.ava.util.HumanName;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Entidade para marcar as datas das avaliações para que os alunos fiquem
 * sabendo no seu portal
 *
 * @author Gleydson Lima
 * @author David Pereira
 */
@Entity @Table(name = "avaliacao_data", schema = "ava")
@HumanName(value="Data de Avaliação", genero='F')
public class DataAvaliacao implements DominioTurmaVirtual {

	/** Chave Primária */
	/**Chave primaria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_avaliacao_data", nullable = false)
	private int id;

	/** Turma onde a data da avaliação foi cadastrada. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_turma")
	private Turma turma;

	/** Data em que ocorrerá a avaliação. */
	private Date data;

	/** Hora em que ocorrerá a avaliação. */
	private String hora;

	/** Descrição da avaliação. */
	private String descricao;

	/** Observações sobre a avaliação. */
	private String observacoes;
	
	/** Usuário que criou a avaliação. */
	@ManyToOne (fetch=FetchType.LAZY)
	@JoinColumn(name="id_usuario_cadastro")
	private Usuario criadoPor; 

	/** Data em que foi criada a data da avaliação. */
	@Column(name="data_cadastro")
	private Date criadoEm;
	
	/** Indica a id da tarefa para esta data. */
	@Transient
	private int idTarefa;
	
	/** Indica a id do questionário para esta data. */
	@Transient
	private int idQuestionario;
	
	/** Indica se o aluno já concluiu a atividade. */
	@Transient
	private boolean concluida;
	
	/** Define até que horas a tarefa pode ser entregue. */
	@Transient
	private int horaEntrega;
	
	/** Define até que minutos a tarefa pode ser entregue. */
	@Transient
	private int minutoEntrega;
	
	private boolean ativo = true;
	
	public DataAvaliacao() {
	}

	public DataAvaliacao(int id, String descricao, Date data, String hora, int idTurma) {
		this.id = id;
		this.descricao = descricao;
		this.data = data;
		this.hora = hora;
		this.turma = new Turma(idTurma);
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public String getHora() {
		return hora;
	}

	public void setHora(String hora) {
		this.hora = hora;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Usuario getCriadoPor() {
		return criadoPor;
	}

	public void setCriadoPor(Usuario criadoPor) {
		this.criadoPor = criadoPor;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public String getObservacoes() {
		return observacoes;
	}

	public void setObservacoes(String observacoes) {
		this.observacoes = observacoes;
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}	
	
	/**
	 * Verifica se a avaliação está no mês 
	 * @return
	 */
	public boolean isInMonth() {
		if (data != null) {
			Calendar now = Calendar.getInstance();
			Calendar dataCal = Calendar.getInstance();
			dataCal.setTime(data);
	
			if ( now.get(Calendar.MONTH) == dataCal.get(Calendar.MONTH) ) 
				return true;
		}
		return false;
	}
	
	/**
	 * Verifica se a avaliação está no mês 
	 * @return
	 */
	public boolean isInWeek() {
		
		if ( data == null ) {
			return false;
		}
		Date now = new Date();
		Calendar c = Calendar.getInstance();
		c.setTime(data);
		
		Calendar cNow = Calendar.getInstance();
		cNow.setTime(now);
		
		if ( c.get(Calendar.DAY_OF_YEAR) - cNow.get(Calendar.DAY_OF_YEAR) <= 7 ) {
			return true;
		}
		
		return false;
	}
	

	/**
	 * Verifica se a avaliação está no mês 
	 * @return
	 */
	public int getDias() {
		
		if ( data == null ) {
			return 0;
		}
		
		Date hoje = CalendarUtils.descartarHoras(new Date());
		 
		return CalendarUtils.calculoDias(hoje, data );
	}

	public String getMensagemAtividade() {
		return "Avaliação marcada para o dia " + Formatador.getInstance().formatarData(data);
	}

	public int getIdTarefa() {
		return idTarefa;
	}

	public void setIdTarefa(int idTarefa) {
		this.idTarefa = idTarefa;
	}

	public boolean isConcluida() {
		return concluida;
	}

	public void setConcluida(boolean concluida) {
		this.concluida = concluida;
	}

	public void setHoraEntrega(int horaEntrega) {
		this.horaEntrega = horaEntrega;
	}

	public int getHoraEntrega() {
		return horaEntrega;
	}

	public void setMinutoEntrega(int minutoEntrega) {
		this.minutoEntrega = minutoEntrega;
	}

	public int getMinutoEntrega() {
		return minutoEntrega;
	}

	public void setIdQuestionario(int idQuestionario) {
		this.idQuestionario = idQuestionario;
	}

	public int getIdQuestionario() {
		return idQuestionario;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

}