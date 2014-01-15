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
import java.util.Date;
import java.util.List;

/**
*
* Classe que cont�m as informa��es do usu�rio que s�o passadas ao sistemas remove desktop de empr�stimos.
*
* @author jadson
* @since 22/09/2008
* @version 1.0 cria��o da classe
* @version 1.1 Fred - adicionados os campos, getters/setters
*
*/
public class UsuarioBibliotecaDto implements Serializable{

	private static final long serialVersionUID = 1L;

	// Constantes dos pap�is do usu�rio
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT = 1;
	public static final int BIBLIOTECA_SETOR_CIRCULACAO = 2;
	public static final int BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO = 3;

	//////////////////////////     Informa��es do Usu�rio    ////////////////////////////

	 /** Id do usu�rio biblioteca no sistema para associar ao empr�stimo */
	public int idUsuarioBiblioteca;        

	/** O Cpf do usu�rio */
	public Long cpf;

	/** Para usu�rios extrageiros que n�o possuem CPF */
	public String passaporte;
	
	/** O nome do usu�rio. */
	public String nome;
	
	/** O Centro do qual o curso do usu�rio faz parte, se for discente. */
	public String centro;

	/** A unidade onde o usu�rio est� lotado, se for servidor. */
	public String lotacao;

	/** O cargo do usu�rio, se for servidor. */
	public String cargo;

	/** O curso do usu�rio, se for discente. */
	public String curso;

	/** Id da foto do usu�rio. */
	public Integer idFoto;

	/** Matr�cula do usu�rio, se for aluno. */
	public Long matricula;

	/** Matr�cula do usu�rio se, for professor. */
	public Integer siape;

	/** O valor que representa o v�nculo do Usu�rio.
	 *  Usando tipos primitivos para o DTO n�o depender de nada. */
	public Integer valorVinculoUsuario;
	
	/** Id da entidade que representa o v�nculo, idDiscente, idServidor, idDocenteExterno, etc.. */
	public int identificacaoVinculo;
	
	/** Data de nascimento para diferenciar usu�rios com nomes iguais. */
	public Date dataNascimento;

	/** True se o v�nculo do usu�rio com a biblioteca acabou. */
	public boolean fimVinculo;

	/** Motivo pelo qual o usu�rio externo teve seu v�nculo cancelado. */
	public String motivoCancelamentoVinculo;
	
	/** Indica se o aluno possui bolsa de inicia��o cient�fica. */
	public boolean iniciacaoCientifica;

	/** Indica se o aluno � de mobilidade estudantil. */
	public Boolean mobilidadeEstudantil;

	/** Indica a pessoa do usu�rio. */
	public int idPessoa;

	/** Indica se � aluno normal ou especial. */
	public Integer tipoDiscente;

	/**
	 * O hash da senha do usu�rio para validar no cliente e evitar chamadas desnecess�rias ao servidor<br><br>
	 * 
	 * LEMBRETE: Isso aqui � a senha da biblioteca que � diferente da senha para o sistema, aqui s�o
	 * somente n�meros.
	 */
	public String senha;

	
	/**
	 * O array de bytes que representam a digital do usu�rio do dedo direito
	 */
	public byte[] digitalDedoDireito;
	
	/**
	 * O array de bytes que representam a digital do usu�rio do dedo esquerdo
	 */
	public byte[] digitalDedoEsquerdo;
	
	

	/////////////////////// Informa��o da situa��o do Usu�rio /////////////////////////////

	//  Usadas tamb�m pelo caso de uso verificar situa��o do usu�rio                         //


	/** 
	 * Uma lista com os valores das situa��es do usu�rio com rela��o a realiza��o dos empretimos
	 * Passa a lista com dados primitivos para o DTO n�o depender de nada.
	 */
	public List<Integer> valoresSituacoesUsuario = new ArrayList<Integer>();

	/** Uma lista com as descri��es da situa��es do usu�rio para ser mostradas do lado do desktop porque n�o
	 * foi poss�vel passar essa informa��o pelo Enum */
	public List <String> descricoesDetalhadasSituacoesUsuario = new ArrayList <String> ();
	
	
	/**
	 * Uma lista de empr�stimo ATIVOS para mostrar na tela e o operador saber sem precisar validar se
	 * o usu�rio vai pode realizar outro empr�stimo ou n�o.
	 */
	public List <EmprestimoDto> emprestimos = new ArrayList <EmprestimoDto> ();


	/**
     * Construtor default, obrigat�rios nos DTOs.
     *
	 */
	public UsuarioBibliotecaDto(){

	}
	
}