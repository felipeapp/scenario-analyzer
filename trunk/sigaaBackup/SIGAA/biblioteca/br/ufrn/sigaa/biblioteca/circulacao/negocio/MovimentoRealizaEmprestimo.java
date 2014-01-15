/*
 * MovimentoRealizaEmprestimo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendencia de Informatica
 * Diretoria de Sistemas
 * Campos Universitario Lagoa Nova
 * Natal - RN - Brasil
 *
 * Este software eh confidencial e de propriedade intelectual da
 * UFRN - Universidade Federal do Rio Grande no Norte
 * Nao se deve utilizar este produto em desacordo com as normas
 * da referida instituicao.
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import java.util.Map;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 * Movimento que guarda os dados passados ao processador que realiza o empr�stimo de itens. 
 *
 * @author jadson
 * @since 26/09/2008
 * @version 1.0 cria��o da classe
 *
 */
public class MovimentoRealizaEmprestimo extends MovimentoCadastro{

	/** < IdMaterialCatalografico , IdTipoEmprestimo > */
	private Map <Integer, Integer> idsTiposEmprestimosMateriais;
	
	/** O usu�rio que est� emprestando os materiais. */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/** A senha do usu�rio que vai levar os materiais. */
	private String senhaDigitada;
	
	/** Informa a quantidade de dias a emprestar para os empr�stimos sem pol�tica. */
	private Integer diasAEmprestar;
	
	
	/** Guarda o id do material que estava sendo processado, para poder dizer onde ocorreu o erro. */
	private int idMaterialProcessando;
	
	/** A biblioteca onde o usu�rio est� opearando no momento. Utilizado para os empr�stimos do m�dulo 
	 * de circula��o desktop. Onde o usu�rio opera uma uma biblioteca por vez. */
	private Integer idBibliotecaOperacao;
	
	/**
	 * Construtor com os dados necess�rios para realizar um empr�timos no sistema.
	 * 
	 * 
	 * @param idsTiposEmprestimos     um mapa contendo <idMaterial, idTipoEmprestimo>
	 * @param diasAEmprestar          passa a quantidade de dias do empr�stimo (utilizado apenas nos casos de empr�timo personalizavel)
	 * @param usuarioBiblioteca       o usu�rio para quem o empr�stimo vai pertencer
	 * @param tipoUsuario             o v�nculo que o usu�rio est� usando para realizar o empr�stimos
	 * @param senhaDigitada           a senha do usu�rio digitada para autorizar o empr�stimo
	 * @param identificacaoUsuario    a identifica��o do v�nculo do usu�rio do empr�stimos, depende do v�nculo que ele est� usando para realizar o empr�stimo
	 */
	public MovimentoRealizaEmprestimo(Map<Integer, Integer> idsTiposEmprestimosMateriais, UsuarioBiblioteca usuarioBiblioteca, String senhaDigitada
			,Integer diasAEmprestar, Integer idBibliotecaOperacao){
		
		this.idsTiposEmprestimosMateriais = idsTiposEmprestimosMateriais;
		this.diasAEmprestar = diasAEmprestar;
		this.usuarioBiblioteca = usuarioBiblioteca;
		this.senhaDigitada = senhaDigitada;
		
		setCodMovimento(SigaaListaComando.REALIZA_EMPRESTIMO);
		
		
		this.idBibliotecaOperacao = idBibliotecaOperacao;
		
	}
	
	public Map<Integer, Integer> getIdsTiposEmprestimosMateriais() {
		return idsTiposEmprestimosMateriais;
	}

	public void setIdsTiposEmprestimosMateriais(Map<Integer, Integer> idsTiposEmprestimosMateriais) {
		this.idsTiposEmprestimosMateriais = idsTiposEmprestimosMateriais;
	}

	public int getIdMaterialProcessando() {
		return idMaterialProcessando;
	}

	public void setIdMaterialProcessando(int idMaterialProcessando) {
		this.idMaterialProcessando = idMaterialProcessando;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public String getSenhaDigitada() {
		return senhaDigitada;
	}


	public int getDiasAEmprestar() {
		return diasAEmprestar;
	}
	
	public Integer getIdBibliotecaOperacao() {
		return idBibliotecaOperacao;
	}

	/**
	 * <p>M�todo que determina se o usu�rio est� operando a parte de circula��o em uma biblioteca espec�fica</p>
	 * <p>Nesse caso o processador deve validar se a biblioteca do operador � igual a biblioteca do material para deixar a devolu��o ser feita</p>
	 * 
	 * @return
	 */
	public boolean isOperandoBibliotecaEspecifica(){
		if(idBibliotecaOperacao != null)
			return true;
		else
			return false;
	}
	
}