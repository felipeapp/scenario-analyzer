/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 13/10/2010
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
 * Situa��es poss�veis da Oferta de Est�gio.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "status_oferta_estagio", schema = "estagio")
public class StatusOfertaEstagio implements PersistDB {
	
	/**
	 * Status que indica que foi cadastrado, por�m necessita de aprova��o do
	 * coordenador do curso
	 */
	public static final int CADASTRADO = 1;
	/** Indica que que a oferta foi aprovada */
	public static final int APROVADO = 2;
	/** Indica que que a oferta foi Indeferida */
	public static final int INDEFERIDO = 3;
	/** Indica que que a oferta foi Cancelada */
	public static final int CANCELADO = 4;
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })		
	@Column(name = "id_status_oferta_estagio")	
	private int id;
	
	/** Descri��o do status */
	private String descricao;
	
	/** Construtor padr�o */
	public StatusOfertaEstagio() {}
	/** Construtor passando o id como padr�o */
	public StatusOfertaEstagio(int id){
		this.id = id;
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
}
