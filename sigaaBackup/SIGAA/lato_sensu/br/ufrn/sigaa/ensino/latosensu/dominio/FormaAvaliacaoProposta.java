/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 17/11/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.dominio;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Associa��o de uma forma de avalia��o a uma proposta de curso lato sensu
 * 
 * @author Leonardo
 * 
 */
@Entity
@Table(name = "forma_avaliacao_proposta", schema = "lato_sensu", uniqueConstraints = {})
public class FormaAvaliacaoProposta implements PersistDB {

	/** Chave prim�ria da Forma de Avalia��o da proposta */
	private int id;

	/** Armazena a forma de Avalia��o */
	private FormaAvaliacao formaAvaliacao;

	/** Armazena a proposta de curso da forma de Avalia��o */
	private PropostaCursoLato proposta;

	// Constructors

	/** default constructor */
	public FormaAvaliacaoProposta() {
	}

	/** minimal constructor */
	public FormaAvaliacaoProposta(int idFormaAvaliacaoProposta) {
		this.id = idFormaAvaliacaoProposta;
	}

	/** full constructor */
	public FormaAvaliacaoProposta(int idFormaAvaliacaoProposta,
			PropostaCursoLato proposta, FormaAvaliacao formaAvaliacao) {
		this.id = idFormaAvaliacaoProposta;
		this.proposta = proposta;
		this.formaAvaliacao = formaAvaliacao;
	}

	// Property accessors
	/**
	 * @return the id
	 */
	
	/** Retornar a chave prim�ria */
	@Id
	@Column(name = "id_forma_avaliacao_proposta", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	public int getId() {
		return id;
	}

	/** Seta a chave prim�ria */
	public void setId(int id) {
		this.id = id;
	}

	/** Retorna a forma de Avalia��o */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forma_avaliacao", unique = false, nullable = true, insertable = true, updatable = true)
	public FormaAvaliacao getFormaAvaliacao() {
		return formaAvaliacao;
	}

	/** Seta a forma de Avalia��o */
	public void setFormaAvaliacao(FormaAvaliacao formaAvaliacao) {
		this.formaAvaliacao = formaAvaliacao;
	}

	/** Retorna a Proposta de Curso da Forma de Avalia��o */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proposta", unique = false, nullable = true, insertable = true, updatable = true)
	public PropostaCursoLato getProposta() {
		return proposta;
	}
	
	/** Seta a Proposta do Curso Lato Sensu */
	public void setProposta(PropostaCursoLato proposta) {
		this.proposta = proposta;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "id", "formaAvaliacao");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, formaAvaliacao);
	}

	/**
	 * Compara se o id passado est� presente na cole��o 
	 * 
	 * @param id
	 * @param formaAvaliacao
	 * @return
	 */
	public static boolean compareTo(String id, Collection<FormaAvaliacaoProposta> formaAvaliacao) {
		for (FormaAvaliacaoProposta forma : formaAvaliacao) {
			if (forma.getFormaAvaliacao().getId() == Integer.parseInt(id))
				return false;
		}
		return true;
	}

	/**
	 * Verifica se o id informando est� dentro da cole��o para que possa ser removido
	 * 
	 * @param formaAvaliacao
	 * @param id
	 * @return
	 */
	public static boolean compareToRemocao(Collection<String> formaAvaliacao, int id) {
		for (String forma : formaAvaliacao) {
			if (Integer.parseInt(forma) ==  id)
				return false;
		}
		return true;
	}

}
