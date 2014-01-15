/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criada em: 07/06/2010
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

import br.ufrn.arq.dao.CampoAtivo;
import br.ufrn.arq.dominio.PersistDB;
/**
 * Representa o tipo de projeto acad�mico. 1-Ensino, 2-Pesquisa, 3-Extens�o, 4-A��es Associadas.
 * 
 * @author Ilueny Santos
 *
 */
@Entity
@Table(name = "tipo_projeto", schema = "projetos")
public class TipoProjeto implements PersistDB {
	
	/** Projeto de Ensino (Monitoria) */
	public static final int ENSINO		= 1;
	/** Projeto de Pesquisa */
	public static final int PESQUISA	= 2;
	/** Projeto de Extens�o */
	public static final int EXTENSAO	= 3;	
	/** A��es acad�micas de Ensino, Pesquisa e Extens�o no mesmo projeto. */
	public static final int ASSOCIADO	= 4; 
    
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_projeto")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	@CampoAtivo
	private boolean ativo;
	
	/** Construtor padr�o. */
	public TipoProjeto(){}
	
	/** Construtor simplificado. */
	public TipoProjeto(int id) {
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
	
	/** Retorna a descri��o do tipo de projeto. */
	@Override
	public String toString() {
		return getDescricao();
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}
	
	/** Retorna true se for do tipo Projeto de Ensino.*/
	public boolean isEnsino() {
		return id == ENSINO;
	}
	
	/** Returna true se for do tipo Projeto de Pesquisa. */
	public boolean isPesquisa() {
		return id == PESQUISA;
	}
	
	/** Retorna true se for do tipo Projeto de Extens�o. */
	public boolean isExtensao() {
		return id == EXTENSAO;
	}
	
	/** Retorna true se for do tipo Projeto Associado (Ensino, Pesquisa e Extens�o)*/
	public boolean isAssociado() {
		return id == ASSOCIADO;
	}
	
}
