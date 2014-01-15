/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * 
 * Tipo de avaliação.
 * Ex. Avaliação de projeto de monitoria, Avaliação de projeto de pesquisa,
 * Avaliação de Relatório de Extensão.
 * 
 * @author Geyson Karlos
 *
 */
@Entity(name="br.ufrn.sigaa.projetos.dominio.TipoAvaliacao")
@Table(name = "tipo_avaliacao", schema = "projetos")
public class TipoAvaliacao implements PersistDB {
	
	/** Avaliação de projetos. */
	public static final int PROJETOS	= 1;
	/** Avaliação de relatórios. */
	public static final int RELATORIOS	= 2;
    
	/** Identificador único do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descrição do tipo de avaliação. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	@CampoAtivo
	private boolean ativo;
	
	public TipoAvaliacao(){}
	
	
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
	public String toString() {
		return getDescricao();
	}

	public boolean isAtivo() {
	    return ativo;
	}

	public void setAtivo(boolean ativo) {
	    this.ativo = ativo;
	}

}
