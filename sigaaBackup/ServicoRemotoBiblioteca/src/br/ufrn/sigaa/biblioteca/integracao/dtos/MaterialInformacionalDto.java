/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2008
 *
 */

package br.ufrn.sigaa.biblioteca.integracao.dtos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
*
* Classe que contém as informações que são passadas ao sistema remoto sobre um MaterialInformacional 
* da biblioteca.
*
* @author jadson
* @since 22/09/2008
* @version 1.0 criação da classe
*
*/

public class MaterialInformacionalDto implements Serializable{

	public static final long serialVersionUID = 1L;

	/**
	 * Uma referência ao material verdadeiro
	 */
	public int idMaterial;        

	/**
	 * O código de barras do material
	 */
	public String codigoBarras; 

	/**
	 * O id da biblioteca do material
	 */
	public Integer idBiblioteca;
	
	/**
	 * A descrição da biblioteca do material
	 */
	public String biblioteca;
	
	/** A descrição do Status do material: regular, especial, etc. */
	public String status;
	
	/** A descrição da Coleção do material. */
	public String colecao;
	
	/** Código de Barras do pai, se este material for um anexo. */
	public String codigoBarrasPai;

	
	/** Uma lista com os possíveis tipos de empréstimo para este material. */
	public List <TipoEmprestimoDto> tiposEmprestimos = new ArrayList <TipoEmprestimoDto> ();
	
	/** Informações do título desse material (título-> 245$a ; autor-> 100$a  ; ano -> 260$ ; edição -> 250$a) */
	public TituloCatalograficoDto tituloDto;

	/**  Indica se o material está disponível para empréstimo */
	public boolean estaDisponivel = false; // 
	
	/**   Indica quando o material está emprestado */
	public boolean estaEmprestado = false; //


	/** A descricao da situação para ser mostrada ao usuário */
	public String situacao; 
	
	/** Contém o nome o usuário a quem o material está emprestado */
	public String nomeUsuarioEmpretimo;

    /** Guarda as informações sobre as possíveis políticas de empréstimos para mostrar ao operador (quantidade, prazo, renovações, etc.. )*/
	public List<String> informacoesPoliticasEmprestimos;
	
	
	/**
	 * Guarda as informações do empréstimo ativo do mateirial, se existir. Utilizado na busca de materiais
	 * 
	 * Usado na operação de checkout
	 */
	public EmprestimoDto emprestimoAtivoMaterial;
	
	
	
    /**
     * Construtor default, obrigatórios nos DTOs.
     */
	public MaterialInformacionalDto() {
		super();
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((codigoBarras == null) ? 0 : codigoBarras.hashCode());
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MaterialInformacionalDto other = (MaterialInformacionalDto) obj;
		if (codigoBarras == null) {
			if (other.codigoBarras != null)
				return false;
		} else if (!codigoBarras.equals(other.codigoBarras))
			return false;
		return true;
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public String getCodigoBarras() {
		return codigoBarras;
	}
	
	/**
	 * Usado nas página JSF
	 * @return
	 */
	public TituloCatalograficoDto getTituloDto() {
		return tituloDto;
	}

	
	
}