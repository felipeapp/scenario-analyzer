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
 * Todos os projetos acad�micos, em sua fase de aprova��o, s�o distribu�dos
 * e avaliados por uma comiss�o espec�fica de consultores.
 * Esta classes representa um tipo de situa��o para uma avalia��o do projeto.
 * 
 * @author Ilueny Santos
 * 
 */
@Entity
@Table(name = "tipo_situacao_avaliacao", schema = "projetos")
public class TipoSituacaoAvaliacao implements PersistDB {

	/** Situa��es poss�veis para uma avalia��o de projetos.*/
	public static final int PENDENTE 	= 1;
	public static final int REALIZADA 	= 2;
	public static final int CANCELADA 	= 3;
    
	/** Identificador �nico do tipo de avalia��o.*/
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_situacao_avaliacao")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descri��o do tipo. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Informa se o tipo de avalia��o est� ativo. */
	@CampoAtivo
	private boolean ativo;
	
	public TipoSituacaoAvaliacao() {
	}
	
	public TipoSituacaoAvaliacao(int id) {
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
