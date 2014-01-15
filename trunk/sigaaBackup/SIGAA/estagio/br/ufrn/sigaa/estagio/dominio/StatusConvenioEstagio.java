/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 28/09/2010
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
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;


/**
 * Situa��es poss�veis do Conv�nio de Est�gio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_convenio_estagio", schema = "estagio")
public class StatusConvenioEstagio implements PersistDB {
	
	/** Indica que a solicita��o de est�gio foi SUBMETIDA */
	public static final int SUBMETIDO = 1;
	/** Indica que a solicita��o de est�gio foi APROVADA */
	public static final int EM_ANALISE = 2;
	/** Indica que a solicita��o de est�gio est� EM_ANALISE */
	public static final int APROVADO = 3;
	/** Indica que a solicita��o de est�gio foi FINALIZADA */
	public static final int FINALIZADO = 4;
	/** Indica que a solicita��o de est�gio foi RECUSADA */
	public static final int RECUSADO = 5;
	
	/**
	 * Chave prim�ria
	 */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })		
	@Column(name = "id_status_convenio_estagio")	
	private int id;
	
	/**
	 * Descri��o do status do Conv�nio
	 */
	private String descricao;
	
	/** Construtor Padr�o */
	public StatusConvenioEstagio() {
	}
	
	/** Construtor paramterizado
	 * @param id
	 */
	public StatusConvenioEstagio(int id){
		this();
		this.id = id;
	}
	
	/** Construtor paramterizado
	 * 
	 * @param id
	 * @param descricao
	 */
	public StatusConvenioEstagio(int id, String descricao){
		this();
		this.id = id;
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

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}
	
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id, descricao);
	}
	
	@Override
	public String toString() {
		return id + "-" + descricao;
	}
}
