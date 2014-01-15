/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * M�dia e desvio padr�o geral das notas de uma pergunta dada a um docente de
 * uma turma.
 * 
 * @author �dipo Elder F. Melo
 * 
 */
@Entity 
@Table(schema="avaliacao", name="media_notas")
public class MediaNotas implements PersistDB, Comparable<MediaNotas> {
	
	/** Chave prim�ria. */
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
	
	/** M�dia das notas da pergunta dada ao docente. */
	private Double media;

	/** Desvio padr�o das notas da pergunta dada ao docente. */
	@Column(name = "desvio_padrao")
	private Double desvioPadrao;
	
	/** Resultado da avalia��o do docente ao qual esta m�dia pertence. */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_resultado_avaliacao_docente",nullable=false)
	private ResultadoAvaliacaoDocente resultadoAvaliacaoDocente;

	/** Construtor padr�o*/
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
	
	/** Retorna a chave prim�ria
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria
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

	/** Retorna a m�dia das notas da pergunta dada ao docente. 
	 * @return M�dia das notas da pergunta dada ao docente. 
	 */
	public Double getMedia() {
		return media;
	}

	/** Seta a m�dia das notas da pergunta dada ao docente.
	 * @param media M�dia das notas da pergunta dada ao docente. 
	 */
	public void setMedia(Double media) {
		this.media = media;
	}

	/** Retorna o desvio padr�o das notas da pergunta dada ao docente. 
	 * @return Desvio padr�o das notas da pergunta dada ao docente. 
	 */
	public Double getDesvioPadrao() {
		return desvioPadrao;
	}

	/** Seta o desvio padr�o das notas da pergunta dada ao docente.
	 * @param desvioPadrao Desvio padr�o das notas da pergunta dada ao docente. 
	 */
	public void setDesvioPadrao(Double desvioPadrao) {
		this.desvioPadrao = desvioPadrao;
	}

	/** Retorna o resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 * @return Resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 */
	public ResultadoAvaliacaoDocente getResultadoAvaliacaoDocente() {
		return resultadoAvaliacaoDocente;
	}

	/** Seta o resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 * @param resultadoAvaliacaoDocente Resultado da avalia��o do docente ao qual esta m�dia pertence. 
	 */
	public void setResultadoAvaliacaoDocente(
			ResultadoAvaliacaoDocente resultadoAvaliacaoDocente) {
		this.resultadoAvaliacaoDocente = resultadoAvaliacaoDocente;
	}

	/**
	 * Retorna uma representa��o textual deste objeto no formato: media, seguido
	 * por uma barra, seguido pelo desvio padr�o.
	 */
	@Override
	public String toString() {
	return media + " / " + desvioPadrao;
	}
	
	/** Indica se o ID deste objeto � igual ao do informando.
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MediaNotas && obj != null)
			return this.id == ((MediaNotas) obj).getId();
		else return false;
	}

	/** Calcula o c�digo Hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/** Compara a ordem de duas m�dias. A ordem � definida pela ordem das perguntas.
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
