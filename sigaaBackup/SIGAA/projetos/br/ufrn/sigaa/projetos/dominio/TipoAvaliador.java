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
 * Representa o tipo de avaliador para o qual o projeto 
 * foi distribuido.
 * 
 * @author ilueny santos
 *
 */

@Entity
@Table(name = "tipo_avaliador", schema = "projetos")
public class TipoAvaliador implements PersistDB {

	/** Tipos poss�veis*/
	/** Representado por membros da comunidade acad�mica em geral, inclusive externos � IFES. */
	public static final int CONSULTORES_AD_HOC	= 1; 
	/*** Comit� formado especificamente para tomada de decis�es relevantes sobre os projetos de a��es acad�micas integradas. */
	public static final int COMITE_INTEGRADO_ENSINO_PESQUISA_EXTENSAO		= 2;  
	/*** Comiss�o formada especificamente para tomada de decis�es relevantes sobre as diretrizes da pesquisa na IFES. */
	public static final int COMISSAO_PESQUISA	= 3;  
    
	/** Identificador �nico do objeto. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@Column(name="id_tipo_avaliador")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	private int id;
	
	/** Descri��o do tipo de avaliador. */
	@Column(name = "descricao", nullable = false)
	private String descricao;
	
	/** Informa se o tipo de avalia��o est� ativo no sistema. */
	@CampoAtivo
	private boolean ativo;
	
	public TipoAvaliador() {
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
