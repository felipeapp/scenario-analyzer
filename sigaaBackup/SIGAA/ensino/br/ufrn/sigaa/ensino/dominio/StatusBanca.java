/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/02/2011
 *
 */
package br.ufrn.sigaa.ensino.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * Status poss�veis das bancas de TCC.
 * 	
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_banca", schema = "ensino")
public class StatusBanca implements PersistDB {
	
	/** Status que indica que a banca est� ativa */
	public static final int ATIVO = 1;
	/** Status que indica que a banca est� cancelada */
	public static final int CANCELADO = 2;

	/** Construtor Padr�o */
	public StatusBanca() {

	}

	/** Construtor passando o id */
	public StatusBanca(int id) {
		this.id = id;
	}
	
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_status_banca")	
	private int id;
	
	/** Descri��o do status da banca */
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
