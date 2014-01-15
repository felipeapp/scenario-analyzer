package br.ufrn.sigaa.assistencia.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.dominio.PersistDB;


/**
 * Entidade que representa a Resid�ncia Universit�ria
 * 
 * @author agostinho
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "residencia_universitaria", schema = "sae")
public class ResidenciaUniversitaria implements PersistDB {

	/**  Constante indicadora do tipo de Resid�ncia: Masculina. */
	public final static String RESIDENCIA_MASCULINA = "M"; 
	
	/** Constante indicadora do tipo de Resid�ncia: Feminina. */
	public final static String RESIDENCIA_FEMININA = "F"; 
	
	/** Constante indicadora do tipo de Resid�ncia: Mista. */
	public final static String RESIDENCIA_MISTA = "MF"; 
	
	/** Identificador da Resid�ncia Universit�ria. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_residencia_universitaria", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Indica se a Resid�ncia est� ativa ou n�o. */
	private boolean ativo = true;

	/** Tipo da resid�ncia (Masculina, Feminina ou Mista) */
	@Column(name = "tipo")
	private String sexo;

	/** Localiza��o da resid�ncia, pois pode ser fora do campus. */
	private String localizacao;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLocalizacao() {
		return localizacao;
	}

	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}

	public String getSexo() {
		return sexo;
	}

	public void setSexo(String sexo) {
		this.sexo = sexo;
	}

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}
	
	/**
	 * Retorna a descri��o do sexo da resid�ncia
	 * @return
	 */
	public String getDescricaoSexo(){
		if(sexo == null)
			return "N�o Informado";
		if( sexo.equals(RESIDENCIA_MASCULINA) )
			return "Masculina";
		else if( sexo.equals(RESIDENCIA_FEMININA) )
			return "Feminina";
		else 
			return "Mista";
	}
	
	/**
	 * Retorna a descri��o que permite a completa identifica��o da resid�ncia.
	 * @return
	 */
	public String getDescricao(){
		return getLocalizacao() + " (" + getDescricaoSexo() + ")";
	}
}
