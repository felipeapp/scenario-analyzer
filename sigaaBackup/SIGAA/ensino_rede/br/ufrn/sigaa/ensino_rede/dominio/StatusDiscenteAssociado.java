/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Descreve a situa��o do curso associado na rede de ensino. Ex.: ativo, cancelado, trancado, etc.
 * @author Henrique Andr�
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(schema="ensino_rede", name = "status_discente_associado")
public class StatusDiscenteAssociado implements PersistDB  {
	
	//Constantes
	/** Constante que define o discente que est� cursando regularmente o curso. */
	public static final int ATIVO = 1;
	/** Constante que define o discente que est� pre cadastrado no curso. */
	public static final int PRE_CADASTRADO = 2;
	/** Constante que define o discente que est� exclu�do curso. */
	public static final int EXCLUIDO = 3;
	/** Constante que define o discente que est� trancado no curso. */
	public static final int TRANCADO = 4;	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_status_discente_associado", nullable = false)
	private int id;
	
	/** Descri��o do status do discente. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Construtor parametrizado. 
	 */
	public StatusDiscenteAssociado() {
	}
	
	/** Construtor parametrizado.
	 * @param id
	 */
	public StatusDiscenteAssociado(int id) {
		this();
		this.id = id;
	}
	
	/** Construtor parametrizado.
	 * @param id
	 * @param descricao
	 */
	public StatusDiscenteAssociado(int id, String descricao) {
		this(id);
		this.descricao = descricao;
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
	
	/** Retorna uma descri��o textual do Status informado.
	 * @param status
	 * @return
	 */
	public static String getDescricao(int status){
		switch (status) {
		case ATIVO:          return "ATIVO";
		case PRE_CADASTRADO: return "PR� CADASTRADO";
		case EXCLUIDO:       return "EXCLU�DO";
		default:             return "DESCONHECIDO";
		}
	}
	
 	/** Retorna uma cole��o com todos status de discentes.
	 * @return
	 */
	public static Collection<StatusDiscenteAssociado> getStatusAtivos(){
		ArrayList<StatusDiscenteAssociado> listaStatus = new ArrayList<StatusDiscenteAssociado>();
		listaStatus.add(new StatusDiscenteAssociado(ATIVO,getDescricao(ATIVO)));
		return listaStatus;
	}
	
	/** Retorna uma descri��o textual deste objeto (status).
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDescricao(this.id);
	}
}
