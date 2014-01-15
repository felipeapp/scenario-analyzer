/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
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
 * Tipos de membros possíveis na banca de defesa de TCC
 * 
 * @author arlindo
 *
 */
@Entity
@Table(name = "tipo_membro_banca", schema = "ensino")
public class TipoMembroBanca implements PersistDB {
	
	/** Constante que define o membro como sendo examinador interno da banca. */
	public static final int EXAMINADOR_INTERNO = 1;
	/** Constante que define o membro como sendo examinador externo da banca. */
	public static final int EXAMINADOR_EXTERNO = 2;
	/** Constante que define o membro da banca como sendo examinador externo à instituição. */
	public static final int EXAMINADOR_EXTERNO_A_INSTITUICAO = 3;

	/** Construtor padrão */
	public TipoMembroBanca() {
	}
	
	/** Construtor passando o id */
	public TipoMembroBanca(int id) {
		this.id = id;
	}	
	
	/** Chave primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_membro_banca")	
	private int id;
	
	/** Descrição do tipo do membro */
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
