/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Entidade que representa as fun��es poss�veis que um Concedente de Est�gio pode ter.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "funcao_concedente", schema = "estagio")
public class ConcedentePessoaFuncao implements PersistDB {
	
	/** Constante que identifica a fun��o de Administrador do Est�gio */
	public static final int ADMINISTRADOR = 1;
	/** Constante que identifica a fun��o de Supervisor de est�gio na Empresa */
	public static final int SUPERVISOR = 2;
	/** Constante que identifica a fun��o de Publicador de est�gio  */
	public static final int PUBLICADOR = 3;
	
	/** Construtor Padr�o */
	public ConcedentePessoaFuncao() {	}
	
	/** Construtor passando o id */
	public ConcedentePessoaFuncao(int id){
		this.id = id;
	}
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_funcao_concedente")	
	private int id;
	
	/** Descri��o da Fun��o */
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
