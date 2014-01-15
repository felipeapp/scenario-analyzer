/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 12/05/2010
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
 * Média e desvio padrão geral das notas de uma pergunta dada a um docente de
 * uma turma.
 * 
 * @author Édipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="media_notas")
public class MediaNotas implements PersistDB, Comparable<MediaNotas> {
	
	/** Chave primária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="avaliacao.resultado_seq") })
	@Column(name = "id_media_notas", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Pergunta avaliada. */
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "id_pergunta")
	private Pergunta pergunta;
	
	/** Média das notas da pergunta dada ao docente. */
	private Double media;

	/** Desvio padrão das notas da pergunta dada ao docente. */
	@Column(name = "desvio_padrao")
	private Double desvioPadrao;
	
	/** Resultado da avaliação do docente ao qual esta média pertence. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_resultado_avaliacao_docente",nullable=false)
	private ResultadoAvaliacaoDocente resultadoAvaliacaoDocente;

	/** Construtor padrão*/
	public MediaNotas() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public MediaNotas(int id) {
		this();
		setId(id);
	}

	/** Construtor parametrizado. 
	 * @param id
	 * @param idDocenteTurma
	 * @param idPergunta
	 * @param media
	 * @param desvioPadrao
	 */
	public MediaNotas(int id, int idPergunta, Double media, Double desvioPadrao) {
		this();
		this.id = id;
		this.pergunta = new Pergunta(idPergunta);
		this.media = media;
		this.desvioPadrao = desvioPadrao;
	}
	
	/** Retorna a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a pergunta avaliada. 
	 * @return Pergunta avaliada. 
	 */
	public Pergunta getPergunta() {
		return pergunta;
	}

	/** Seta a pergunta avaliada. 
	 * @param pergunta Pergunta avaliada. 
	 */
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	/** Retorna a média das notas da pergunta dada ao docente. 
	 * @return Média das notas da pergunta dada ao docente. 
	 */
	public Double getMedia() {
		return media;
	}

	/** Seta a média das notas da pergunta dada ao docente.
	 * @param media Média das notas da pergunta dada ao docente. 
	 */
	public void setMedia(Double media) {
		this.media = media;
	}

	/** Retorna o desvio padrão das notas da pergunta dada ao docente. 
	 * @return Desvio padrão das notas da pergunta dada ao docente. 
	 */
	public Double getDesvioPadrao() {
		return desvioPadrao;
	}

	/** Seta o desvio padrão das notas da pergunta dada ao docente.
	 * @param desvioPadrao Desvio padrão das notas da pergunta dada ao docente. 
	 */
	public void setDesvioPadrao(Double desvioPadrao) {
		this.desvioPadrao = desvioPadrao;
	}

	/** Retorna o resultado da avaliação do docente ao qual esta média pertence. 
	 * @return Resultado da avaliação do docente ao qual esta média pertence. 
	 */
	public ResultadoAvaliacaoDocente getResultadoAvaliacaoDocente() {
		return resultadoAvaliacaoDocente;
	}

	/** Seta o resultado da avaliação do docente ao qual esta média pertence. 
	 * @param resultadoAvaliacaoDocente Resultado da avaliação do docente ao qual esta média pertence. 
	 */
	public void setResultadoAvaliacaoDocente(
			ResultadoAvaliacaoDocente resultadoAvaliacaoDocente) {
		this.resultadoAvaliacaoDocente = resultadoAvaliacaoDocente;
	}

	/**
	 * Retorna uma representação textual deste objeto no formato: media, seguido
	 * por uma barra, seguido pelo desvio padrão.
	 */
	@Override
	public String toString() {
	return media + " / " + desvioPadrao;
	}
	
	/** Indica se o ID deste objeto é igual ao do informando.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MediaNotas && obj != null)
			return this.id == ((MediaNotas) obj).getId();
		else return false;
	}

	/** Calcula o código Hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/** Compara a ordem de duas médias. A ordem é definida pela ordem das perguntas.
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compareTo(MediaNotas other) {
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
