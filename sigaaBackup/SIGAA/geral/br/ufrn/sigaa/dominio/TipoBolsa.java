/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 15/05/2007
 *
 */
package br.ufrn.sigaa.dominio;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 * Tipo de Bolsa que congrega os vários tipos
 *
 */
@Entity
@Table(name = "tipo_bolsa", uniqueConstraints = {})
public class TipoBolsa implements PersistDB {

	/**
	 * estas constantes devem estar sincronizadas com os valores do banco 
	 */
	public static final int BOLSA_PECG = 1;
	
	public static final int BOLSA_MONITORIA = 3; // Id de bolsas de monitoria
	public static final int BOLSA_EXTENSAO = 4; // Id de bolsas de extensão
	public static final int APOIO_TECNICO = 5;
	public static final int AUXILIO_ESTAGIO = 6;
	public static final int BOLSA_PESQUISA = 25; // Id de bolsas de pesquisa
	public static final int BOLSA_PPQ = 26; // Id de bolsas de pesquisa
	public static final int ALUNO_CARENTE = 58;
	public static final int BOLSA_PET = 59;
	public static final int BOLSA_PESQUISA_REUNI = 90;
	public static final int BOLSA_MONITORIA_REUNI = 92; // Id de bolsas de monitoria do reuni
	public static final int BOLSA_EXTENSAO_REUNI = 93; // Id de bolsas de extensao do reuni
	
	/**
	 * IDs referentes ao tipo bolsa do SIPAC.
	 */
	public static final int BOLSA_AUXILIO_ALIMENTACAO = 149;
	public static final int BOLSA_AUXILIO_RESIDENCIA_POS = 150;
	public static final int BOLSA_AUXILIO_RESIDENCIA_GRADUACAO = 151;
	public static final int BOLSA_AUXILIO_TRANSPORTE = 91;
	
	/** bolsas de convênio */
	public static final int MESTRADO = 60;
	public static final int DOUTORADO = 61;
	public static final int MESTRADO_ESPECIAL = 64;
	public static final int AUXILIO_RETORNO = 65;
	public static final int AUXILIO_TESE_DOUTORADO = 66;
	public static final int AUXILIO_TESE_MESTRADO = 67;
	public static final int TAXAS_ESCOLARES = 70;

	
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE)
	@Column(name = "id_tipo_bolsa", unique = true, nullable = false, insertable = true, updatable = true)
	private int id;

	@Column(name = "descricao", unique = false, nullable = false, insertable = true, updatable = true, length = 80)
	private String descricao;

	private boolean interna;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_categoria_tipo_bolsa")
	private CategoriaTipoBolsa categoriaTipoBolsa;

	// Constructors

	/** default constructor */
	public TipoBolsa() {
	}

	/** default minimal constructor */
	public TipoBolsa(int id) {
		this.id = id;
	}

	/** full constructor */
	public TipoBolsa(int idTipoBolsa, String descricao) {
		this.id = idTipoBolsa;
		this.descricao = descricao;
	}

	// Property accessors
	public int getId() {
		return this.id;
	}

	public void setId(int idTipoBolsa) {
		this.id = idTipoBolsa;
	}

	
	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, "id");
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(id);
	}

	public boolean isInterna() {
		return interna;
	}

	public void setInterna(boolean interna) {
		this.interna = interna;
	}

	public CategoriaTipoBolsa getCategoriaTipoBolsa() {
		return categoriaTipoBolsa;
	}

	public void setCategoriaTipoBolsa(CategoriaTipoBolsa categoriaTipoBolsa) {
		this.categoriaTipoBolsa = categoriaTipoBolsa;
	}

	/**
	 * Métodos utilizados quando se vai solicitar bolsas a partir do SAE
	 * 
	 */
	public boolean isBolsaAlimentacao() {
		if (id == BOLSA_AUXILIO_ALIMENTACAO)
			return true;
		else 
			return false;
	}
	
	public boolean isBolsaResidencia() {
		if (id == BOLSA_AUXILIO_RESIDENCIA_GRADUACAO || id == BOLSA_AUXILIO_RESIDENCIA_POS)
			return true;
		else
			return false;
	}
}
