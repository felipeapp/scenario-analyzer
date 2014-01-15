/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/01/2011
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/**
 * <p>
 * Representa a situa��o do discente vinculado a um projeto associado.
 * </p>
 * 
 * @author geyson
 * 
 **/
@Entity
@Table(schema = "projetos", name = "tipo_situacao_discente_projeto")
public class TipoSituacaoDiscenteProjeto implements PersistDB {
	
	/** Situa��o de inscrito no processo seletivo */
	public static final int INSCRITO_PROCESSO_SELETIVO = 1;
	
	/** Situa��o de exclu�do */
	public static final int EXCLUIDO = 2;

	/** Situa��o de selecionado */
	public static final int SELECIONADO = 3;

	/** situa��o de ativo, ap�s ser selecionado */
	public static final int ASSUMIU = 4;
	
	/** Situa��o finalizado do plano de trabalho */
	public static final int FINALIZADO = 5;

	/** identificador do tipo situa��o do discente projeto */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name = "id_tipo_situacao_discente_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;

	/** Descri��o do tipo de situa��o */
	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 50)
	private String descricao;

	/** Construtor padr�o. */
	public TipoSituacaoDiscenteProjeto() {
	}

	/** Construtor min�mo */
	public TipoSituacaoDiscenteProjeto(int id) {
		this.id = id;
	}

	/** Construtor completo */
	public TipoSituacaoDiscenteProjeto(int id, String descricao) {
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

}
