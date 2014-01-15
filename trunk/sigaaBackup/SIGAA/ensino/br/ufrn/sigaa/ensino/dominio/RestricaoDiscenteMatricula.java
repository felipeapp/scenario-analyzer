/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/01/2012
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.dominio.Unidade;

/**
 * Entidade que permite configurar validações específicas aplicadas na matrícula de discentes de 
 * determinada Unidade e Nível. A Classe validadora é chamada sempre que o período (início e fim)
 * estiver vigente. Também é possível configurar a situação da matrícula e o texto de instruções
 * apresentado na tela de matrícula. 
 * 
 * @author Leonardo Campos
 *
 */
@Entity
@Table(name = "restricao_discente_matricula", schema = "ensino")
public class RestricaoDiscenteMatricula implements Validatable {

	/** Chave primária */
	@Id
	@Column(name="id_restricao_discente_matricula")
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
		           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição da restrição */
	private String descricao;
	
	/** Unidade aplicadora da restrição */
	@ManyToOne
	@JoinColumn(name = "id_unidade")
	private Unidade unidade;
	
	/** Nível de ensino ao qual se aplica a restrição */
	private char nivel;
	
	/** Início do período de aplicação da restrição */
	private Date inicio;
	
	/** Fim do período de aplicação da restrição */
	private Date fim;
	
	/** Classe validadora a ser instanciada para verificar a restrição */
	private String classe;
	
	/** Situação que a matrícula deve ficar após ser executada */
	@ManyToOne
	@JoinColumn(name = "id_situacao_matricula")
	private SituacaoMatricula situacaoMatricula;
	
	/** Instruções específicas para a matrícula. */
	private String instrucoes;
	
	@Transient
	private Integer horaInicio;
	
	@Transient
	private Integer minutoInicio;

	@Transient
	private Integer horaFim;

	@Transient
	private Integer minutoFim;

	public RestricaoDiscenteMatricula() {
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

	public Unidade getUnidade() {
		return unidade;
	}

	public void setUnidade(Unidade unidade) {
		this.unidade = unidade;
	}

	public char getNivel() {
		return nivel;
	}

	public void setNivel(char nivel) {
		this.nivel = nivel;
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

	public String getClasse() {
		return classe;
	}

	public void setClasse(String classe) {
		this.classe = classe;
	}

	public SituacaoMatricula getSituacaoMatricula() {
		return situacaoMatricula;
	}

	public void setSituacaoMatricula(SituacaoMatricula situacaoMatricula) {
		this.situacaoMatricula = situacaoMatricula;
	}

	public String getInstrucoes() {
		return instrucoes;
	}

	public void setInstrucoes(String instrucoes) {
		this.instrucoes = instrucoes;
	}
	
	public boolean isPeriodoVigente() {
		return CalendarUtils.isDentroPeriodo(inicio, fim);
	}

	public void popularCampos(Collection<RestricaoDiscenteMatricula> restricoes) {
		Calendar cal = Calendar.getInstance();
		for (RestricaoDiscenteMatricula restricaoDiscenteMatricula : restricoes) {
			if ( restricaoDiscenteMatricula.getInicio() != null ) {
				cal.setTime(restricaoDiscenteMatricula.getInicio());
				restricaoDiscenteMatricula.setHoraInicio( cal.get(Calendar.HOUR_OF_DAY) );
				restricaoDiscenteMatricula.setMinutoInicio( cal.get(Calendar.MINUTE) );
			}
			if ( restricaoDiscenteMatricula.getFim() != null ) {
				cal.setTime(restricaoDiscenteMatricula.getFim());
				restricaoDiscenteMatricula.setHoraFim( cal.get(Calendar.HOUR_OF_DAY) );
				restricaoDiscenteMatricula.setMinutoFim( cal.get(Calendar.MINUTE) );
			}
		}
	}

	public Integer getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(Integer horaInicio) {
		this.horaInicio = horaInicio;
	}

	public Integer getMinutoInicio() {
		return minutoInicio;
	}

	public void setMinutoInicio(Integer minutoInicio) {
		this.minutoInicio = minutoInicio;
	}

	public Integer getHoraFim() {
		return horaFim;
	}

	public void setHoraFim(Integer horaFim) {
		this.horaFim = horaFim;
	}

	public Integer getMinutoFim() {
		return minutoFim;
	}

	public void setMinutoFim(Integer minutoFim) {
		this.minutoFim = minutoFim;
	}
	
	@Override
	public ListaMensagens validate() {
		// TODO Auto-generated method stub
		return null;
	}
	
}