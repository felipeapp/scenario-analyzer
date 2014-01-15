/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
* Classe que cont�m as informa��es que s�o passadas ao sistema remoto sobre um MaterialInformacional 
* da biblioteca.
*
* @author jadson
* @since 22/09/2008
* @version 1.0 cria��o da classe
*
*/

public class MaterialInformacionalDto implements Serializable{

	public static final long serialVersionUID = 1L;

	/**
	 * Uma refer�ncia ao material verdadeiro
	 */
	public int idMaterial;        

	/**
	 * O c�digo de barras do material
	 */
	public String codigoBarras; 

	/**
	 * O id da biblioteca do material
	 */
	public Integer idBiblioteca;
	
	/**
	 * A descri��o da biblioteca do material
	 */
	public String biblioteca;
	
	/** A descri��o do Status do material: regular, especial, etc. */
	public String status;
	
	/** A descri��o da Cole��o do material. */
	public String colecao;
	
	/** C�digo de Barras do pai, se este material for um anexo. */
	public String codigoBarrasPai;

	
	/** Uma lista com os poss�veis tipos de empr�stimo para este material. */
	public List <TipoEmprestimoDto> tiposEmprestimos = new ArrayList <TipoEmprestimoDto> ();
	
	/** Informa��es do t�tulo desse material (t�tulo-> 245$a ; autor-> 100$a  ; ano -> 260$ ; edi��o -> 250$a) */
	public TituloCatalograficoDto tituloDto;

	/**  Indica se o material est� dispon�vel para empr�stimo */
	public boolean estaDisponivel = false; // 
	
	/**   Indica quando o material est� emprestado */
	public boolean estaEmprestado = false; //


	/** A descricao da situa��o para ser mostrada ao usu�rio */
	public String situacao; 
	
	/** Cont�m o nome o usu�rio a quem o material est� emprestado */
	public String nomeUsuarioEmpretimo;

    /** Guarda as informa��es sobre as poss�veis pol�ticas de empr�stimos para mostrar ao operador (quantidade, prazo, renova��es, etc.. )*/
	public List<String> informacoesPoliticasEmprestimos;
	
	
	/**
	 * Guarda as informa��es do empr�stimo ativo do mateirial, se existir. Utilizado na busca de materiais
	 * 
	 * Usado na opera��o de checkout
	 */
	public EmprestimoDto emprestimoAtivoMaterial;
	
	
	
    /**
     * Construtor default, obrigat�rios nos DTOs.
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
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getCodigoBarras() {
		return codigoBarras;
	}
	
	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public TituloCatalograficoDto getTituloDto() {
		return tituloDto;
	}

	
	
}