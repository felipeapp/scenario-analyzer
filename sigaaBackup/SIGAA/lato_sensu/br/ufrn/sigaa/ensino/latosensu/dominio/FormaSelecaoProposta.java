/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * Associação de uma forma de seleção com uma proposta de criação de curso lato sensu
 *
 * @author Leonardo
 *
 */
@Entity
@Table(name = "forma_selecao_proposta", schema = "lato_sensu", uniqueConstraints = {})
public class FormaSelecaoProposta implements PersistDB {

	/** Chave primária */
	private int id;
	
	/** Forma de Seleção */
	private FormaSelecao formaSelecao;
	
	/** Proposta da Forma de seleção */
	private PropostaCursoLato proposta;
	
	// Constructors
	
	/** default constructor */
	public FormaSelecaoProposta() {
	}

	/** minimal constructor */
	public FormaSelecaoProposta(int idFormaSelecaoProposta) {
		this.id = idFormaSelecaoProposta;
	}

	/** full constructor */
	public FormaSelecaoProposta(int idFormaSelecaoProposta, PropostaCursoLato proposta,
			FormaSelecao formaSelecao) {
		this.id = idFormaSelecaoProposta;
		this.proposta = proposta;
		this.formaSelecao = formaSelecao;
	}


	/**
	 * Retorna a chave primária
	 */
	@Id
	@Column(name = "id_forma_selecao_proposta", unique = true, nullable = false, insertable = true, updatable = true)
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator")
	public int getId() {
		return id;
	}

	/**
	 * Seta a chave primária
	 */
	public void setId(int id) {
		this.id = id;
	}
	
	/**
	 * Retorna a Forma de Seleção
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.EAGER)
	@JoinColumn(name = "id_forma_selecao", unique = false, nullable = true, insertable = true, updatable = true)
	public FormaSelecao getFormaSelecao() {
		return formaSelecao;
	}

	/**
	 * Seta a forma de Seleção
	 */
	public void setFormaSelecao(FormaSelecao formaSelecao) {
		this.formaSelecao = formaSelecao;
	}

	/**
	 * Retornar a proposta de Curso associada
	 */
	@ManyToOne(cascade = {}, fetch = FetchType.LAZY)
	@JoinColumn(name = "id_proposta", unique = false, nullable = true, insertable = true, updatable = true)
	public PropostaCursoLato getProposta() {
		return proposta;
	}

	/**
	 * Seta a proposta de Curso Associada
	 */
	public void setProposta(PropostaCursoLato proposta) {
		this.proposta = proposta;
	}
	
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testTransientEquals(this, obj, "formaSelecao.id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(formaSelecao.getId());
	}
	
	/**
	 * Verifica se o id informado está presente na coleção das formas de Seleção 
	 * 
	 * @param id
	 * @param formaSelecao
	 * @return
	 */
	public static boolean compareTo(String id, Collection<FormaSelecaoProposta> formaSelecao) {
		for (FormaSelecaoProposta forma : formaSelecao) {
			if (forma.getFormaSelecao().getId() == Integer.parseInt(id))
				return false;
		}
		return true;
	}

	/**
	 * Verifica se o id informado está presente na coleção das formas de Seleção, para que possa ser efetuada a remoção.
	 * 
	 * @param formaSelecao
	 * @param id
	 * @return
	 */
	public static boolean compareToRemocao(Collection<String> formaSelecao, int id) {
		for (String forma : formaSelecao) {
			if (Integer.parseInt(forma) ==  id)
				return false;
		}
		return true;
	}

}