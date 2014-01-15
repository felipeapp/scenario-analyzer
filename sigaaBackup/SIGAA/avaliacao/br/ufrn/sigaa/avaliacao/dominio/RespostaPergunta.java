/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 29/04/2008
 */
package br.ufrn.sigaa.avaliacao.dominio;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dominio.TurmaDocenciaAssistida;

/**
 * Resposta do discente ou docente a uma pergunta da avaliação
 * institucional.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="resposta_pergunta", schema="avaliacao")
public class RespostaPergunta implements PersistDB {

	/** Chave primária. */
	@Id @GeneratedValue(strategy=GenerationType.SEQUENCE, generator="resposta_pergunta_seq")
	@SequenceGenerator(name = "resposta_pergunta_seq", sequenceName = "avaliacao.resposta_pergunta_seq", allocationSize=1)
	private int id;

	/** Avaliação Institucional ao qual esta resposta pertence.*/
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_avaliacao")
	private AvaliacaoInstitucional avaliacao;
	
	/** Pergunta ao qual esta resposta se refere. */
	@ManyToOne  (fetch=FetchType.EAGER) 
	@JoinColumn(name="id_pergunta")
	private Pergunta pergunta;

	/** DocenteTurma ao qual esta resposta se refere. */
	@ManyToOne (fetch=FetchType.LAZY) 
	@JoinColumn(name="id_docente_turma")
	private DocenteTurma docenteTurma;
	
	/** Turma de docência assistida ao qual esta resposta se refere-se. */
	@ManyToOne  (fetch=FetchType.LAZY) 
	@JoinColumn(name = "id_turma_docencia_assistida")
	private TurmaDocenciaAssistida turmaDocenciaAssistida;
	
	/** Resposta dada. */
	private Integer resposta;

	/** Citação referente à pergunta. */
	private String citacao;
	
	/** Construtor padrão. */
	public RespostaPergunta() {
	}
	
	/** Construtor parametrizado. 
	 * @param id
	 */
	public RespostaPergunta(int id) {
		setId(id);
	}

	/** Retorna chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#getId()
	 */
	public int getId() {
		return id;
	}

	/** Seta a chave primária.
	 * @see br.ufrn.arq.dominio.PersistDB#setId(int)
	 */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a Avaliação Institucional ao qual esta resposta pertence.
	 * @returnAvaliação Institucional ao qual esta resposta pertence.
	 */
	public AvaliacaoInstitucional getAvaliacao() {
		return avaliacao;
	}

	/** Seta a Avaliação Institucional ao qual esta resposta pertence.
	 * @param avaliacao Avaliação Institucional ao qual esta resposta pertence.
	 */
	public void setAvaliacao(AvaliacaoInstitucional avaliacao) {
		this.avaliacao = avaliacao;
	}

	/** Retorna a pergunta ao qual esta resposta se refere.
	 * @return Pergunta ao qual esta resposta se refere.
	 */
	public Pergunta getPergunta() {
		return pergunta;
	}

	/** Seta a pergunta ao qual esta resposta se refere. 
	 * @param pergunta Pergunta ao qual esta resposta se refere. 
	 */
	public void setPergunta(Pergunta pergunta) {
		this.pergunta = pergunta;
	}

	/** Retorna o DocenteTurma ao qual esta resposta se refere. 
	 * @return DocenteTurma ao qual esta resposta se refere. 
	 */
	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	/** Seta o DocenteTurma ao qual esta resposta se refere. 
	 * @param docenteTurma DocenteTurma ao qual esta resposta se refere. 
	 */
	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	/** Retorna a resposta dada. 
	 * @return Resposta dada. 
	 */
	public Integer getResposta() {
		return resposta;
	}

	/** Seta a resposta dada. 
	 * @param resposta Resposta dada. 
	 */
	public void setResposta(Integer resposta) {
		this.resposta = resposta;
	}

	/** Retorna a citação referente à pergunta.
	 * @return Citação referente à pergunta. 
	 */
	public String getCitacao() {
		return citacao;
	}

	/** Seta a citação referente à pergunta. 
	 * @param citacao Citação referente à pergunta. 
	 */
	public void setCitacao(String citacao) {
		this.citacao = citacao;
	}

	/** Retorna uma representação textual do valor SIM/NÃO desta resposta.
	 * @return
	 */
	public String getValorSimNao() {
		if (pergunta != null && pergunta.isSimNao() && resposta != null)
			return resposta == 1 ? "S" : "N";
		return null;
	}
	
	/** Retorna uma representação textual desta resposta
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		if (pergunta != null || pergunta.isSimNao())
			return resposta == 1 ? "S" : "N";
		else 
			return String.valueOf(resposta);
	}
	
	/**
	 * Compara este objeto com outro e retorna o valor true caso ambos possuam a
	 * mesma chave, ou sejam resposta da mesma pergunta de um docente/turma.
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RespostaPergunta) {
			RespostaPergunta other = (RespostaPergunta) obj;
			if (this.id == other.getId())
				return true;
			if (this.docenteTurma != null) {
				if (this.pergunta != null && this.pergunta.equals(other.getPergunta()) && this.docenteTurma.equals(other.getDocenteTurma()))
					return true;
			} else if (this.turmaDocenciaAssistida != null) {
				if (this.pergunta != null && this.pergunta.equals(other.getPergunta()) && this.turmaDocenciaAssistida.equals(other.getTurmaDocenciaAssistida()))
					return true;
			}
		}
		return false;
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	/** Retorna a turma de docência assistida ao qual esta resposta se refere-se. 
	 * @return
	 */
	public TurmaDocenciaAssistida getTurmaDocenciaAssistida() {
		return turmaDocenciaAssistida;
	}

	/** Seta a turma de docência assistida ao qual esta resposta se refere-se. 
	 * @param turmaDocenciaAssistida
	 */
	public void setTurmaDocenciaAssistida(
			TurmaDocenciaAssistida turmaDocenciaAssistida) {
		this.turmaDocenciaAssistida = turmaDocenciaAssistida;
	}
}
