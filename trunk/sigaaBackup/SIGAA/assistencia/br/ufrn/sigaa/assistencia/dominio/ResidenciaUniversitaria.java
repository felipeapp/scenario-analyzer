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
 * Entidade que representa a Residência Universitária
 * 
 * @author agostinho
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "residencia_universitaria", schema = "sae")
public class ResidenciaUniversitaria implements PersistDB {

	/**  Constante indicadora do tipo de Residência: Masculina. */
	public final static String RESIDENCIA_MASCULINA = "M"; 
	
	/** Constante indicadora do tipo de Residência: Feminina. */
	public final static String RESIDENCIA_FEMININA = "F"; 
	
	/** Constante indicadora do tipo de Residência: Mista. */
	public final static String RESIDENCIA_MISTA = "MF"; 
	
	/** Identificador da Residência Universitária. */
	@Id
	@GeneratedValue(generator="seqGenerator")
	@GenericGenerator(name="seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator",
	           parameters={ @Parameter(name="sequence_name", value="hibernate_sequence") })
	@Column(name = "id_residencia_universitaria", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	/** Indica se a Residência está ativa ou não. */
	private boolean ativo = true;

	/** Tipo da residência (Masculina, Feminina ou Mista) */
	@Column(name = "tipo")
	private String sexo;

	/** Localização da residência, pois pode ser fora do campus. */
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
	 * Retorna a descrição do sexo da residência
	 * @return
	 */
	public String getDescricaoSexo(){
		if(sexo == null)
			return "Não Informado";
		if( sexo.equals(RESIDENCIA_MASCULINA) )
			return "Masculina";
		else if( sexo.equals(RESIDENCIA_FEMININA) )
			return "Feminina";
		else 
			return "Mista";
	}
	
	/**
	 * Retorna a descrição que permite a completa identificação da residência.
	 * @return
	 */
	public String getDescricao(){
		return getLocalizacao() + " (" + getDescricaoSexo() + ")";
	}
}
