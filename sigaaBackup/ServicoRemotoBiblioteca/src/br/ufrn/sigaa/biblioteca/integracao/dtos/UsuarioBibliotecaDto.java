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
import java.util.Date;
import java.util.List;

/**
*
* Classe que contém as informações do usuário que são passadas ao sistemas remove desktop de empréstimos.
*
* @author jadson
* @since 22/09/2008
* @version 1.0 criação da classe
* @version 1.1 Fred - adicionados os campos, getters/setters
*
*/
public class UsuarioBibliotecaDto implements Serializable{

	private static final long serialVersionUID = 1L;

	// Constantes dos papéis do usuário
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT = 1;
	public static final int BIBLIOTECA_SETOR_CIRCULACAO = 2;
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO = 3;

	//////////////////////////     Informações do Usuário    ////////////////////////////

	 /** Id do usuário biblioteca no sistema para associar ao empréstimo */
	public int idUsuarioBiblioteca;        

	/** O Cpf do usuário */
	public Long cpf;

	/** Para usuários extrageiros que não possuem CPF */
	public String passaporte;
	
	/** O nome do usuário. */
	public String nome;
	
	/** O Centro do qual o curso do usuário faz parte, se for discente. */
	public String centro;

	/** A unidade onde o usuário está lotado, se for servidor. */
	public String lotacao;

	/** O cargo do usuário, se for servidor. */
	public String cargo;

	/** O curso do usuário, se for discente. */
	public String curso;

	/** Id da foto do usuário. */
	public Integer idFoto;

	/** Matrícula do usuário, se for aluno. */
	public Long matricula;

	/** Matrícula do usuário se, for professor. */
	public Integer siape;

	/** O valor que representa o vínculo do Usuário.
	 *  Usando tipos primitivos para o DTO não depender de nada. */
	public Integer valorVinculoUsuario;
	
	/** Id da entidade que representa o vínculo, idDiscente, idServidor, idDocenteExterno, etc.. */
	public int identificacaoVinculo;
	
	/** Data de nascimento para diferenciar usuários com nomes iguais. */
	public Date dataNascimento;

	/** True se o vínculo do usuário com a biblioteca acabou. */
	public boolean fimVinculo;

	/** Motivo pelo qual o usuário externo teve seu vínculo cancelado. */
	public String motivoCancelamentoVinculo;
	
	/** Indica se o aluno possui bolsa de iniciação científica. */
	public boolean iniciacaoCientifica;

	/** Indica se o aluno é de mobilidade estudantil. */
	public Boolean mobilidadeEstudantil;

	/** Indica a pessoa do usuário. */
	public int idPessoa;

	/** Indica se é aluno normal ou especial. */
	public Integer tipoDiscente;

	/**
	 * O hash da senha do usuário para validar no cliente e evitar chamadas desnecessárias ao servidor<br><br>
	 * 
	 * LEMBRETE: Isso aqui é a senha da biblioteca que é diferente da senha para o sistema, aqui são
	 * somente números.
	 */
	public String senha;

	
	/**
	 * O array de bytes que representam a digital do usuário do dedo direito
	 */
	public byte[] digitalDedoDireito;
	
	/**
	 * O array de bytes que representam a digital do usuário do dedo esquerdo
	 */
	public byte[] digitalDedoEsquerdo;
	
	

	/////////////////////// Informação da situação do Usuário /////////////////////////////

	//  Usadas também pelo caso de uso verificar situação do usuário                         //


	/** 
	 * Uma lista com os valores das situações do usuário com relação a realização dos empretimos
	 * Passa a lista com dados primitivos para o DTO não depender de nada.
	 */
	public List<Integer> valoresSituacoesUsuario = new ArrayList<Integer>();

	/** Uma lista com as descrições da situações do usuário para ser mostradas do lado do desktop porque não
	 * foi possível passar essa informação pelo Enum */
	public List <String> descricoesDetalhadasSituacoesUsuario = new ArrayList <String> ();
	
	
	/**
	 * Uma lista de empréstimo ATIVOS para mostrar na tela e o operador saber sem precisar validar se
	 * o usuário vai pode realizar outro empréstimo ou não.
	 */
	public List <EmprestimoDto> emprestimos = new ArrayList <EmprestimoDto> ();


	/**
     * Construtor default, obrigatórios nos DTOs.
     *
	 */
	public UsuarioBibliotecaDto(){

	}
	
}