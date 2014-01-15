package br.ufrn.rh.dominio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;

/**
 *
 * Categoria do servidor. Ex.: docente, t�cnico administrativo
 *
 * @author Gleydson Lima
 *
 */
@Entity
@Table(name="categoria", schema="rh")
public class Categoria implements Validatable  {

	/** Identificador da entidade */
	
	@Id
	@GeneratedValue(generator = "seqGenerator")
    @GenericGenerator(name = "seqGenerator", strategy="br.ufrn.arq.dao.SequenceStyleGenerator", 
    				  parameters = {@Parameter(name = "sequence_name", value = "rh.categoria_seq")})
    @Column(name = "id_categoria", nullable = false)
	private int id;

	/** Descri��o da categoria*/
	@Column(name = "descricao")
	private String descricao;

	/** o valor dessas constantes � guardado do id */
	public static final int DOCENTE = 1;
	public static final int TECNICO_ADMINISTRATIVO = 2;
	public static final int NAO_ESPECIFICADO = 3;
	public static final int MEDICO_RESIDENTE = 6;

	public Categoria() {

	}

	public Categoria(int id) {
		this.id = id;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	/**
	* Verifica se o servidor atual � docente.
	*
	* @return true se for, false caso contr�rio.
	*/
	public boolean isDocente(){
		if(this.id == Categoria.DOCENTE)
			return true;
		return false;
	}

	/**
	* Verifica se o servidor atual � um t�cnico.
	*
	* @return true se for, false caso contr�rio.
	*/
	public boolean isTecnico(){
		if(this.id == Categoria.TECNICO_ADMINISTRATIVO)
			return true;
		return false;
	}

	/**
	 * M�todo usado para validar os atributos privados desta classe.
	 */
	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens ();
		validateRequired(descricao, "Descri��o", lista);
		return lista;
	}

	
}
