/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 30/07/2009
 * 
 */
package br.ufrn.sigaa.avaliacao.dominio;

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

/**
 * Percentual de respostas "SIM" e "N�O" das notas de uma pergunta dada a um docente de
 * uma turma.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="percentual_sim_nao")
public class PercentualSimNao implements PersistDB, Comparable<PercentualSimNao> {
	
	/** Chave prim�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.resultado_seq") })
	@Column(name = "id_percentual_sim_nao", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;
	
	/** Pergunta avaliada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pergunta")
	private Pergunta pergunta;
	
	/** Percentual de respostas "SIM" dadas � pergunta. */
	@Column(name = "percentual_sim")
	private Double percentualSim;
	
	/** Percentual de respostas "N�O" dadas � pergunta. */
	@Column(name = "percentual_nao")
	private Double percentualNao;

	/** Resultado da avalia��o do docente ao qual esta m�dia pertence. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_resultado_avaliacao_docente")
	private ResultadoAvaliacaoDocente resultadoAvaliacaoDocente;

	/** Construtor padr�o. */
	public PercentualSimNao() {
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public PercentualSimNao(int id) {
		this();
		setId(id);
	}
	
	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Retorna a pergunta avaliada. 
	 * @return Pergunta avaliada. 
	 */
	public Pergunta getPergunta() {
		return pergunta;
	}

	/** Retorna o resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 * @return Resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 */
	public ResultadoAvaliacaoDocente getResultadoAvaliacaoDocente() {
		return resultadoAvaliacaoDocente;
	}

	/** Seta a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Seta a pergunta avaliada. 
	 * @param pergunta Pergunta avaliada. 
	 */
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	/** Seta o resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 * @param resultadoAvaliacaoDocente Resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 */
	public void setResultadoAvaliacaoDocente(
			ResultadoAvaliacaoDocente resultadoAvaliacaoDocente) {
		this.resultadoAvaliacaoDocente = resultadoAvaliacaoDocente;
	}
	
	public Double getPercentualSim() {
		return percentualSim;
	}

	public void setPercentualSim(Double percentualSim) {
		this.percentualSim = percentualSim;
	}

	/** Retorna o percentual de respostas "N�O" dadas � pergunta. 
	 * @return Percentual de respostas "N�O" dadas � pergunta. 
	 */
	public Double getPercentualNao() {
		return percentualNao;
	}

	/** Seta o percentual de respostas "N�O" dadas � pergunta.
	 * @param percentualNao Percentual de respostas "N�O" dadas � pergunta. 
	 */
	public void setPercentualNao(Double percentualNao) {
		this.percentualNao = percentualNao;
	}
	
	/**
	 * Retorna uma representa��o textual deste objeto no formato: media, seguido
	 * por uma barra, seguido pelo desvio padr�o.
	 */
	@Override
	public String toString() {
		return "Sim: " + percentualSim + "% / N�o:" + percentualNao + "%"; 
	}
	
	/** Compara a ordem de dois percentuais. A ordem � definida pela ordem das perguntas.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compareTo(PercentualSimNao other) {
		if (other != null && this.getPergunta() != null && other.getPergunta() != null) {
			if (this.getPergunta().getGrupo() != null && other.getPergunta().getGrupo() != null) {
				int cmp = this.getPergunta().getGrupo().compareTo(other.getPergunta().getGrupo());
				if (cmp == 0)
					return this.getPergunta().getOrdem() - other.getPergunta().getOrdem();
				else return cmp;
			} else {
				return this.getPergunta().getOrdem() - other.getPergunta().getOrdem();
			}
		}
		return 0;
	}
}
