/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 */
package br.ufrn.sigaa.ensino.metropoledigital.dominio;

import static br.ufrn.arq.mensagens.MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;


import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.UnidadeTempo;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;

/**
 * Cronogra de execução de Aulas do IMD
 * 
 * @author Rafael Silva
 *
 */
@Entity
@Table(name="cronograma_execucao", schema="metropole_digital")
public class CronogramaExecucaoAulas implements PersistDB, Validatable {
	/**ID do Cronograma*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	          parameters={ @Parameter(name="sequence_name", value="metropole_digital.cronograma_execucao_id_cronograma_execucao_seq") })
	@Column(name="id_cronograma_execucao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/**Descrição abreviada do cronograma*/
	@Column(name="descricao")
	private String descricao;
	
	/**Data de início do cronograma*/
	@Column(name="data_inicio")
	private Date dataInicio;
	
	/**Data final do cronograma*/
	@Column(name="data_fim")
	private Date dataFim;
	
	/**Ano de início do Cronograma*/
	@Column(name="ano_referencia")
	private Integer ano=0;
	
	/**Período de ínicio do cronograma*/
	@Column(name="periodo_referencia")
	private Integer periodo=0;
	
	/**Módulo ao qual o cronograma pertence*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_modulo")
	private Modulo modulo;
	
	/**Curso ao qual o cronograma pertence*/
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_curso")
	private Curso curso;
	
	/**Unidade para o qual o cronograma foi criado*/
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_unidade_tempo")
	private UnidadeTempo unidadeTempo;
	
	/**Períodos de avaliação do cronograma*/
	@OneToMany(fetch=FetchType.LAZY, cascade = CascadeType.ALL, mappedBy="cronogramaExecucaoAulas")
	private List<PeriodoAvaliacao> periodosAvaliacao;
	
	public CronogramaExecucaoAulas() {
		ano = new Integer(0);
		periodo = new Integer(0);
		periodosAvaliacao = new ArrayList<PeriodoAvaliacao>();
	}
	
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(curso, "Curso", lista);
		ValidatorUtil.validateRequired(ano, "Ano", lista);
		ValidatorUtil.validateRequired(periodo, "Período", lista);
		ValidatorUtil.validateRequired(dataInicio, "Data Início", lista);
		ValidatorUtil.validateRequired(dataFim, "Data Fim", lista);
		
		if (ano==0) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		}
		if (periodo==0 || periodo==null) {
			lista.addMensagem(CAMPO_OBRIGATORIO_NAO_INFORMADO, "Período");
		}
		
		
		return lista;
	}
	//GETTERS AND SETTERS
	@Override
	public int getId() {
		return id;
	}
	@Override
	public void setId(int id) {
		this.id = id;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
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
	/**
	 * Retorna o ano do cronograma
	 * 
	 * @return
	 */
	public Integer getAno() {
		if (ano==0) {
			return null;
		}
		return ano;
	}
	
	/**
	 * Método utilizado para atribuir um valor a variável ano;
	 * 
	 * @param ano
	 */
	public void setAno(Integer ano) {
		if (ano==null) {
			this.ano = 0;
		}else{
			this.ano = ano;
		}
	}
	
	/**
	 * Retorna o período do cronograma
	 * 
	 * @return
	 */
	public Integer getPeriodo() {
		if (periodo==0) {
			return null;
		}
		return periodo;
	}	
	
	/**
	 * Método utilizado para atribuir um valor a variável "periodo"
	 * 
	 * @param periodo
	 */
	public void setPeriodo(Integer periodo) {
		if (periodo==null) {
			this.periodo = 0;
		}else{
			this.periodo = periodo;
		}
	}
	public Modulo getModulo() {
		return modulo;
	}
	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}
	public Curso getCurso() {
		return curso;
	}
	public void setCurso(Curso curso) {
		this.curso = curso;
	}
	public List<PeriodoAvaliacao> getPeriodosAvaliacao() {
		return periodosAvaliacao;
	}
	public void setPeriodosAvaliacao(List<PeriodoAvaliacao> periodosAvaliacao) {
		this.periodosAvaliacao = periodosAvaliacao;
	}

	public UnidadeTempo getUnidadeTempo() {
		return unidadeTempo;
	}

	public void setUnidadeTempo(UnidadeTempo unidadeTempo) {
		this.unidadeTempo = unidadeTempo;
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
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CronogramaExecucaoAulas other = (CronogramaExecucaoAulas) obj;
		if (id != other.id)
			return false;
		return true;
	}
	

}
