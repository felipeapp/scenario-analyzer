/*
 * MovimentoDevolveEmprestimos.java
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

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Movimento que guarda os dados passados ao processador que devolve empr�stimos de materiais. 
 *
 * @author Fred
 * @since 15/10/2008
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoDevolveEmprestimo extends MovimentoCadastro {
	
	/** O material cujo empr�stimos ser� devolvido no momento  */
	private int idMaterial;
	
	/** A biblioteca em que o usu�rio est� operando, utilizado nos empr�stimos via desktop, onde o usu�rio escolhe uma biblioteca espec�fica para operar */
	private Integer idBibliotecaOperacao;
	
	
	/**
	 * Construtor padr�o
	 * 
	 * @param idMaterial
	 * @param usuario
	 */
	public MovimentoDevolveEmprestimo(int idMaterial, UsuarioGeral usuario, Integer idBibliotecaOperacao){
		this.idMaterial = idMaterial;
		setUsuarioLogado(usuario);
		this.idBibliotecaOperacao = idBibliotecaOperacao;
		setCodMovimento(SigaaListaComando.DEVOLVE_EMPRESTIMO);
	}

	public int getIdMaterial() {
		return idMaterial;
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