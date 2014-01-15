package br.ufrn.sigaa.monitoria.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;

/*******************************************************************************
 * <p>
 * Representa os tipos de projeto de ensino existentes:<br>
 * PROJETO DE MONITORIA e PROJETO PAMQEG
 * </p>
 * 
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
@SuppressWarnings("serial")
@Entity
@Table(name = "tipo_projeto_ensino", schema = "monitoria", uniqueConstraints = {})
public class TipoProjetoEnsino implements PersistDB {

	/** TIPOS DE PROJETOS DE ENSINO POSSÍVEIS */
	/** Projeto de Ensino de monitoria */
	public static final int PROJETO_DE_MONITORIA = 1;
	
	/** Programa de Apoio à Melhoria da Qualidade do Ensino de Graduação */
	public static final int PROJETO_PAMQEG = 2;

	/** Programa de Apoio à Melhoria da Qualidade do Ensino de Graduação */
	public static final int AMBOS_MONITORIA_PAMQEG = 3;
		
	// Fields
	/** Chave Primária */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_tipo_projeto_ensino")
	private int id;

	/** Descrição do tipo de projeto */
	@Column(name = "descricao", nullable = false)
	private String descricao;

	// Constructors

	/** default constructor */
	public TipoProjetoEnsino() {
	}

	public TipoProjetoEnsino(int id) {
		this.id = id;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	/**
	 * Retorna uma sigla com o tipo de projeto de ensino.
	 */
	public String getSigla() {	    
	    switch (id) {
	    case PROJETO_DE_MONITORIA:
		return "MONITORIA";
	    case PROJETO_PAMQEG:
		return "PAMQEG";
	    default:
		return "MONITORIA E/OU PAMQEG";
	    }	    
	}

}
