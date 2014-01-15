/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em 06/08/2013
 * 
 */
package br.ufrn.sigaa.ensino_rede.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

 /**
  * Descreve a situa��o do curso associado na rede de ensino. Ex.: em atividade, proposto, etc.
  * @author Henrique Andr�
  *
  */
@Entity
@Table(schema="ensino_rede", name = "situacao_curso_associado")
public class SituacaoCursoAssociado implements PersistDB  {
	/** Chave prim�ria */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
			parameters={ @Parameter(name="sequence_name", value="ensino_rede.hibernate_sequence") })
	@Column(name = "id_situacao_curso_associado", nullable = false)
	private int id;
	
	/** Descri��o do programa da rede de ensino. */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	/**
	 * Construtor parametrizado. 
	 */
	public SituacaoCursoAssociado() {
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
