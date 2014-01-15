/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Entidade que representa os Tipos de Est�gios.
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "tipo_estagio", schema = "estagio")
public class TipoEstagio  implements PersistDB {
	
	/** Tipo referente a Est�gio Curricular obrigat�rio */
	public static final int ESTAGIO_CURRICULAR_OBRIGATORIO = 1;
	/** Tipo referente a est�gio curricular n�o obrigat�rio */
	public static final int ESTAGIO_CURRICULAR_NAO_OBRIGATORIO = 2;
	
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="estagio.hibernate_sequence") })	
	@Column(name = "id_tipo_estagio")
	private int id;
	
	/** Descri��o do Tipo do Est�gio */
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
