/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 29/04/2008
 */
package br.ufrn.sigaa.avaliacao.dominio;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Pergunta da avalia��o institucional.
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="pergunta", schema="avaliacao")
@Cache ( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
public class Pergunta implements PersistDB, Comparable<Pergunta> {

	/** Chave prim�ria. */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	private int id;
	
	/** Grupo de perguntas ao qual esta pergunta pertence. */
	@ManyToOne(fetch=FetchType.LAZY) 
	@JoinColumn(name="id_grupo")
	private GrupoPerguntas grupo;
	
	/** Descri��o da pergunta. */
	private String descricao;
	
	/** Tipo da pergunta. */
	@Enumerated(EnumType.ORDINAL)
	@Column(name="tipo_pergunta")
	private TipoPergunta tipoPergunta;
	
	/** Indica se a pergunta permite avaliar turmas. */
	@Column(name="avaliar_turmas")
	private boolean avaliarTurmas;
	
	/** Lista de alternativas de respostas � pergunta. */
	@OneToMany(fetch=FetchType.LAZY, mappedBy="pergunta", cascade=CascadeType.ALL)
	@OrderBy("id")
	private List<AlternativaPergunta> alternativas;
	
	/** Indica se esta pergunta est� ativa. */
	private boolean ativa;
	
	/** Indica a ordem que a pergunta aparece no question�rio. */
	private int ordem;

	/** Construtor padr�o. */
	public Pergunta() {
		ativa = true;
		alternativas = new ArrayList<AlternativaPergunta>();
	}
	
	/** Construtor parametrizado.
	 * @param id Chave prim�ria.
	 */
	public Pergunta(int id) {
		this();
		this.id = id;
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

	/** Retorna o grupo de perguntas ao qual esta pergunta pertence. 
	 * @return Grupo de perguntas ao qual esta pergunta pertence. 
	 */
	public GrupoPerguntas getGrupo() {
		return grupo;
	}

	/** Seta o grupo de perguntas ao qual esta pergunta pertence. 
	 * @param grupo Grupo de perguntas ao qual esta pergunta pertence. 
	 */
	public void setGrupo(GrupoPerguntas grupo) {
		this.grupo = grupo;
	}

	/** Retorna a descri��o da pergunta. 
	 * @return Descri��o da pergunta. 
	 */
	public String getDescricao() {
		return descricao;
	}

	/** Seta a descri��o da pergunta. 
	 * @param descricao Descri��o da pergunta. 
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Retorna o tipo da pergunta.
	 * @return Tipo da pergunta. 
	 */
	public TipoPergunta getTipoPergunta() {
		return tipoPergunta;
	}

	/** Seta o tipo da pergunta. 
	 * @param tipoPergunta Tipo da pergunta. 
	 */
	public void setTipoPergunta(TipoPergunta tipoPergunta) {
		this.tipoPergunta = tipoPergunta;
	}

	/** Indica se a pergunta permite avaliar turmas. 
	 * @return true, caso se a pergunta permitir avaliar turmas. 
	 */
	public boolean isAvaliarTurmas() {
		return avaliarTurmas;
	}

	/** Seta se a pergunta permite avaliar turmas. 
	 * @param avaliarTurmas true, caso se a pergunta permitir avaliar turmas.
	 */
	public void setAvaliarTurmas(boolean avaliarTurmas) {
		this.avaliarTurmas = avaliarTurmas;
	}

	/** Retorna a lista  de alternativas de respostas � pergunta.
	 * @return Alternativas de respostas � pergunta. 
	 */
	public List<AlternativaPergunta> getAlternativas() {
		return alternativas;
	}

	/** Seta a lista de alternativas de respostas � pergunta. 
	 * @param alternativas Alternativas de respostas � pergunta. 
	 */
	public void setAlternativas(List<AlternativaPergunta> alternativas) {
		this.alternativas = alternativas;
	}

	/** Indica se esta pergunta est� ativa. 
	 * @return caso true, indica que esta pergunta est� ativa. 
	 */
	public boolean isAtiva() {
		return ativa;
	}

	/** Seta se esta pergunta est� ativa. 
	 * @param ativa true, indica que esta pergunta est� ativa.
	 */
	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}
	
	/** Indica se esta pergunta � do tipo NOTA.
	 * @return
	 */
	public boolean isNota() {
		return tipoPergunta.isNota();
	}

	/** Indica se esta pergunta � do tipo �NICA ESCOLHA.
	 * @return
	 */
	public boolean isEscolhaUnica() {
		return tipoPergunta.isUnicaEscolha();
	}
	
	/** Indica se esta pergunta � do tipo M�LTIPLA ESCOLHA.
	 * @return
	 */
	public boolean isMultiplaEscolha() {
		return tipoPergunta.isMultiplaEscolha();
	}
	
	/** Indica se esta pergunta � do tipo SIM/N�O.
	 * @return
	 */
	public boolean isSimNao() {
		return tipoPergunta.isSimNao();
	}

	/** Calcula o c�digo hash para este objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}

	/** Compara este objeto com o objeto especificado
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Pergunta other = (Pergunta) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/** Compara este objeto com o objeto especificado
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Pergunta o) {
		return this.descricao.compareTo(o.getDescricao());
	}

	/** Retorna uma representa��o textual desta pergunta.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return descricao;
	}

	public int getOrdem() {
		return ordem;
	}

	public void setOrdem(int ordem) {
		this.ordem = ordem;
	}

	/** Adiciona uma alternativa de resposta � pergunta.
	 * @param alternativa
	 */
	public void adicionaAlternativa(AlternativaPergunta alternativa) {
		if (alternativas == null) 
			alternativas = new ArrayList<AlternativaPergunta>();
		alternativa.setPergunta(this);
		alternativa.setOrdem(alternativas.size());
		alternativas.add(alternativa);
	}
	
}
