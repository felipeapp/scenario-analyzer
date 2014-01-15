/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 07/10/2010
 */

package br.ufrn.sigaa.estagio.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Entidade que representa as funções possíveis que um Concedente de Estágio pode ter.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "funcao_concedente", schema = "estagio")
public class ConcedentePessoaFuncao implements PersistDB {
	
	/** Constante que identifica a função de Administrador do Estágio */
	public static final int ADMINISTRADOR = 1;
	/** Constante que identifica a função de Supervisor de estágio na Empresa */
	public static final int SUPERVISOR = 2;
	/** Constante que identifica a função de Publicador de estágio  */
	public static final int PUBLICADOR = 3;
	
	/** Construtor Padrão */
	public ConcedentePessoaFuncao() {	}
	
	/** Construtor passando o id */
	public ConcedentePessoaFuncao(int id){
		this.id = id;
	}
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_funcao_concedente")	
	private int id;
	
	/** Descrição da Função */
	private String descricao;

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

}
