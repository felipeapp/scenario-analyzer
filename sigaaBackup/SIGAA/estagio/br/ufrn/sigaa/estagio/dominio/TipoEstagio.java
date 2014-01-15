/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 20/10/2010
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
 * Entidade que representa os Tipos de Estágios.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "tipo_estagio", schema = "estagio")
public class TipoEstagio  implements PersistDB {
	
	/** Tipo referente a Estágio Curricular obrigatório */
	public static final int ESTAGIO_CURRICULAR_OBRIGATORIO = 1;
	/** Tipo referente a estágio curricular não obrigatório */
	public static final int ESTAGIO_CURRICULAR_NAO_OBRIGATORIO = 2;
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_tipo_estagio")
	private int id;
	
	/** Descrição do Tipo do Estágio */
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
