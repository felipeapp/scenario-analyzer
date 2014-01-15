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
 * 
 * Tipo de avalia��o.
 * Ex. Avalia��o de projeto de monitoria, Avalia��o de projeto de pesquisa,
 * Avalia��o de Relat�rio de Extens�o.
 * 
 * @author Geyson Karlos
 *
 */
@Entity(name="br.ufrn.sigaa.projetos.dominio.TipoAvaliacao")
@Table(name = "tipo_avaliacao", schema = "projetos")
public class TipoAvaliacao implements PersistDB {
	
	/** Avalia��o de projetos. */
	public static final int PROJETOS	= 1;
	/** Avalia��o de relat�rios. */
	public static final int RELATORIOS	= 2;
    
	/** Identificador �nico do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descri��o do tipo de avalia��o. */
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
