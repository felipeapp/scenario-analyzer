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

/**
 * Classe que cont�m as informa��es do empr�stimo que s�o passadas ao
 * sistemas desktop de empr�stimos ou retornadas do desktop para o servidor.
 *
 * Na ida, s�o passados os dados de um empr�stimo e do seu material. Para cada empr�stimo
 * ativo do usu�rio, � criado um objeto EmprestimoTDO.
 *
 * Na volta, � retornado o c�digo de barras do material para o empr�stimo ou renova��o
 * (dentro do MaterialInformacionalDto) e a a��o que ser� realizada.
 *
 * @author Fred
 * @since 22/09/2008
 * @version 1.0 cria��o da classe
 *          1.1 altera��o para n�o usar datas no DTO, usar sempre a string j� formatada pelo servidor. 
 *          Porque no hor�rio de ver�o alguns computadores formatam a data errada. Ent�o a data j� vai ser formatada no servidor.
 *
 */

public class EmprestimoDto implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final int STATUS_EM_DIA = 1;
	public static final int STATUS_ATRASADO = 2;

	// Guarda a mensagem que o servidor retorna em caso de sucesso do empr�stimo ou renova��o
//	public static final String MENSAGEM_SUCESSO_EMPRESTIMO = "Empr�stimos Realizados com sucesso";
//	public static final String MENSAGEM_SUCESSO_RENOVACAO = "Empr�stimos Renovados com sucesso";
//	public static final String MENSAGEM_SUCESSO_DEVOLUCAO = "Material Informacional devolvido com sucesso";
//	public static final String MENSAGEM_SUCESSO_EMPRESTIMO_RENOVACAO = "Empr�stimos/Renova��es Realizadas com sucesso";

	// A��es a se realizar sobre um EmprestimoDTO
	public static final int ACAO_EMPRESTAR = 1;
	public static final int ACAO_RENOVAR = 2;

	/**  Uma refer�ncia ao empr�stimo verdadeiro */
	public int idEmprestimo;         

	/**  A data de in�cio do empr�stimo */
	public String dataEmprestimoFormatado; 
	
	/**   A data quando ele vai acabar */
	public String prazoFormatado;         
	
	/**  A data que foi renovado. Se foi */
	public String dataRenovacaoFormatado; 
	
	/**  A data que foi devolvido. Se foi */
	public String dataDevolucaoFormatado; 
	
	/**  Se est� em dia ou atrasado */
	public int status;        
	
	/**  O id do tipo de empr�stimo (normal, fotoc�pia, outro que criarem) */
	public int idTipoEmprestimo; // 
    
	
	/** O n�mero que ser� impresso no comprovante de devolu��o do empr�stimo para verificar sua atenticidade. */
	public String numeroAutenticacao;
	
	
    /** Indica se o empr�stimo pode ser renovado. */
    public boolean podeRenovar;

	/** O usu�rio para que o material est� emprestado, utilizando na busca de materiais. */
	public UsuarioBibliotecaDto usuario;

	/**  Os dados do material do empr�stimo (cada empr�stimo tem apenas um material) */
	public MaterialInformacionalDto materialDto;

	/** Indica se � para emprestar ou renovar  */
	public int acao; 

	
	/**
     * Construtor default, obrigat�rios nos DTOs.
     */
	public EmprestimoDto() {
		super();
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public MaterialInformacionalDto getMaterialDto() {
		return materialDto;
	}

	/**
	 * Verifica se o objeto passado cont�m um material com o mesmo c�digo de barras que este EmprestimoDTO
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		EmprestimoDto other = (EmprestimoDto) obj;
		if (materialDto == null) {
			if (other.materialDto != null)
				return false;
		} else if (!materialDto.equals(other.materialDto))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((materialDto == null) ? 0 : materialDto.hashCode());
		return result;
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
    public boolean podeRenovar() {
        return podeRenovar;
    }
    
    /**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getPrazoFormatado() {
		return prazoFormatado;
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getDataDevolucaoFormatado() {
		return dataDevolucaoFormatado;
	}

	/**
	 * Usado nas p�gina JSF
	 * @return
	 */
	public String getNumeroAutenticacao() {
		return numeroAutenticacao;
	}
    
    
    
}