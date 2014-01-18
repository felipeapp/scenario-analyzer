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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Agrupamento de perguntas para a avalia��o institucional
 * 
 * @author David Pereira
 *
 */
@Entity @Table(name="grupo_perguntas", schema="avaliacao")
@Cache ( usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE )
public class GrupoPerguntas implements PersistDB, Comparable<GrupoPerguntas> {

	// constantes que definem as dimen��es

	/** Chave prim�ria. */
	@Id 
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
    				  parameters={ @Parameter(name="sequence_name", value="avaliacao.avaliacao_seq") })
	private int id;

	/** T�tulo do grupo. */
	private String titulo;
	
	/** Descri��o do grupo. */
	private String descricao;
	
	/** Indica se este grupo est� ativo. */
	private boolean ativa;
	
	/** Indica se o grupo � respondido por discente. */
	private boolean discente;
	
	/** Indica se o grupo pode avaliar turmas. */
	@Column(name="avalia_turmas")
	private boolean avaliaTurmas;
	
	/** Lista de perguntas deste grupo. */
	@OrderBy("ordem ASC")
	@OneToMany(fetch = FetchType.EAGER, mappedBy="grupo", cascade=CascadeType.ALL)
	private List<Pergunta> perguntas;
	
	/** Indica se o grupo de perguntas � para a Avalia��o Institucional de cursos � dist�ncia. */
	@Column(name = "a_distancia")
	private boolean aDistancia;

	/** Construtor padr�o. */
	public GrupoPerguntas() {

	}

	/** Construtor parametrizado.
	 * @param id Chave prim�ria.
	 */
	public GrupoPerguntas(int id) {
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

	/** Retorna o t�tulo do grupo. 
	 * @return T�tulo do grupo. 
	 */
	public String getTitulo() {
		return titulo;
	}

	/** Seta o t�tulo do grupo. 
	 * @param titulo T�tulo do grupo. 
	 */
	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	/** Retorna a descri��o do grupo.
	 * @return Descri��o do grupo. 
	 */
	public String getDescricao() {
		return descricao;
	}
	
	/** Retorna a descri��o completa do grupo (t�tulo, descri��o e se avalia turmas ou n�o).
	 * @return Descri��o do grupo. 
	 */
	public String getDescricaoCompleta() {
		StringBuilder str = new StringBuilder(titulo);
		str.append(" - ").append(descricao);
		if (avaliaTurmas)
			str.append(" (Avalia Turmas)");
		return str.toString();
	}

	/** Seta a descri��o do grupo. 
	 * @param descricao Descri��o do grupo. 
	 */
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/** Indica se este grupo est� ativo. 
	 * @return caso true, este grupo est� ativo. 
	 */
	public boolean isAtiva() {
		return ativa;
	}

	/** Seta se este grupo est� ativo. 
	 * @param ativa true, se este grupo est� ativo.
	 */
	public void setAtiva(boolean ativa) {
		this.ativa = ativa;
	}

	/** Indica se o grupo � respondido por discente. 
	 * @return caso true, indica se o grupo � respondido por discente. 
	 */
	public boolean isDiscente() {
		return discente;
	}

	/** Seta se o grupo � respondido por discente. 
	 * @param discente true, se o grupo � respondido por discente.
	 */
	public void setDiscente(boolean discente) {
		this.discente = discente;
	}

	/** Retorna a lista de perguntas deste grupo. 
	 * @return Lista de perguntas deste grupo. 
	 */
	public List<Pergunta> getPerguntas() {
		return perguntas;
	}
	
	/** Retorna a lista de perguntas ativas deste grupo. 
	 * @return Lista de perguntas deste grupo. 
	 */
	public List<Pergunta> getPerguntasAtivas() {
		List<Pergunta> ativas = new ArrayList<Pergunta>();
		if (perguntas != null) {
			for (Pergunta p : perguntas) {
				if (p.isAtiva()) ativas.add(p);
			}
		}
		return ativas;
	}

	/** Seta a lista de perguntas deste grupo.
	 * @param perguntas Lista de perguntas deste grupo. 
	 */
	public void setPerguntas(List<Pergunta> perguntas) {
		this.perguntas = perguntas;
	}

	/** Indica se o grupo pode avaliar turmas. 
	 * @return casto true, Indica se o grupo pode avaliar turmas. 
	 */
	public boolean isAvaliaTurmas() {
		return avaliaTurmas;
	}

	/** Seta se o grupo pode avaliar turmas. 
	 * @param avaliaTurmas true, se o grupo pode avaliar turmas.
	 */
	public void setAvaliaTurmas(boolean avaliaTurmas) {
		this.avaliaTurmas = avaliaTurmas;
	}

	/** Calcula o c�digo hash deste objeto.
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, titulo);
	}

	/** Indica se este objeto � igual ao objeto passado como par�metro.
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
		final GrupoPerguntas other = (GrupoPerguntas) obj;
		if (id != other.id)
			return false;
		return true;
	}

	/** Compara este objeto ao objeto passado como par�metro.
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(GrupoPerguntas o) {
		return this.titulo.compareTo(o.titulo);
	}
	
	/** Retorna uma representa��o textual deste grupo.
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return titulo + " / " + descricao;
	}

	/** Indica se o grupo de perguntas � para a Avalia��o Institucional de cursos � dist�ncia.
	 * @return
	 */
	public boolean isaDistancia() {
		return aDistancia;
	}

	/** Seta se o grupo de perguntas � para a Avalia��o Institucional de cursos � dist�ncia.
	 * @param aDistancia
	 */
	public void setaDistancia(boolean aDistancia) {
		this.aDistancia = aDistancia;
	}

	/** Adiciona uma pergunta ao Grupo de perguntas.
	 * @param pergunta
	 */
	public void adicionaPergunta(Pergunta pergunta) {
		if (perguntas == null) 
			perguntas = new ArrayList<Pergunta>();
		pergunta.setOrdem(perguntas.size());
		pergunta.setGrupo(this);
		perguntas.add(pergunta);
	}
	
}
