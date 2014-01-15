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

/**
 * Classe que contém as informações do empréstimo que são passadas ao
 * sistemas desktop de empréstimos ou retornadas do desktop para o servidor.
 *
 * Na ida, são passados os dados de um empréstimo e do seu material. Para cada empréstimo
 * ativo do usuário, é criado um objeto EmprestimoTDO.
 *
 * Na volta, é retornado o código de barras do material para o empréstimo ou renovação
 * (dentro do MaterialInformacionalDto) e a ação que será realizada.
 *
 * @author Fred
 * @since 22/09/2008
 * @version 1.0 criação da classe
 *          1.1 alteração para não usar datas no DTO, usar sempre a string já formatada pelo servidor. 
 *          Porque no horário de verão alguns computadores formatam a data errada. Então a data já vai ser formatada no servidor.
 *
 */

public class EmprestimoDto implements Serializable{

	private static final long serialVersionUID = 1L;

	public static final int STATUS_EM_DIA = 1;
	public static final int STATUS_ATRASADO = 2;

	// Guarda a mensagem que o servidor retorna em caso de sucesso do empréstimo ou renovação
//	public static final String MENSAGEM_SUCESSO_EMPRESTIMO = "Empréstimos Realizados com sucesso";
//	public static final String MENSAGEM_SUCESSO_RENOVACAO = "Empréstimos Renovados com sucesso";
//	public static final String MENSAGEM_SUCESSO_DEVOLUCAO = "Material Informacional devolvido com sucesso";
//	public static final String MENSAGEM_SUCESSO_EMPRESTIMO_RENOVACAO = "Empréstimos/Renovações Realizadas com sucesso";

	// Ações a se realizar sobre um EmprestimoDTO
	public static final int ACAO_EMPRESTAR = 1;
	public static final int ACAO_RENOVAR = 2;

	/**  Uma referência ao empréstimo verdadeiro */
	public int idEmprestimo;         

	/**  A data de início do empréstimo */
	public String dataEmprestimoFormatado; 
	
	/**   A data quando ele vai acabar */
	public String prazoFormatado;         
	
	/**  A data que foi renovado. Se foi */
	public String dataRenovacaoFormatado; 
	
	/**  A data que foi devolvido. Se foi */
	public String dataDevolucaoFormatado; 
	
	/**  Se está em dia ou atrasado */
	public int status;        
	
	/**  O id do tipo de empréstimo (normal, fotocópia, outro que criarem) */
	public int idTipoEmprestimo; // 
    
	
	/** O número que será impresso no comprovante de devolução do empréstimo para verificar sua atenticidade. */
	public String numeroAutenticacao;
	
	
    /** Indica se o empréstimo pode ser renovado. */
    public boolean podeRenovar;

	/** O usuário para que o material está emprestado, utilizando na busca de materiais. */
	public UsuarioBibliotecaDto usuario;

	/**  Os dados do material do empréstimo (cada empréstimo tem apenas um material) */
	public MaterialInformacionalDto materialDto;

	/** Indica se é para emprestar ou renovar  */
	public int acao; 

	
	/**
     * Construtor default, obrigatórios nos DTOs.
     */
	public EmprestimoDto() {
		super();
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public MaterialInformacionalDto getMaterialDto() {
		return materialDto;
	}

	/**
	 * Verifica se o objeto passado contém um material com o mesmo código de barras que este EmprestimoDTO
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
	 * Usado nas página JSF
	 * @return
	 */
    public boolean podeRenovar() {
        return podeRenovar;
    }
    
    /**
	 * Usado nas página JSF
	 * @return
	 */
	public String getPrazoFormatado() {
		return prazoFormatado;
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public String getDataDevolucaoFormatado() {
		return dataDevolucaoFormatado;
	}

	/**
	 * Usado nas página JSF
	 * @return
	 */
	public String getNumeroAutenticacao() {
		return numeroAutenticacao;
	}
    
    
    
}