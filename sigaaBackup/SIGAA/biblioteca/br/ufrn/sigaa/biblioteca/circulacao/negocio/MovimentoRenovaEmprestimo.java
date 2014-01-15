/*
 * MovimentoRenovaEmprestimos.java
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

import java.util.List;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 * Movimento que guarda os dados passados ao processador que renova empréstimos de materiais.
 *
 * @author Fred_Castro
 * @since 06/10/2008
 * @version 1.0 criação da classe
 *
 */
public class MovimentoRenovaEmprestimo extends MovimentoCadastro {

	/** Uma lista de ids materiais a renovar. */
	private List <Integer> idsMateriais;
	
	/** O usuário que emprestou os materiais. */
	private UsuarioBiblioteca usuarioBiblioteca;

	/** A senha do usuário que levou os materiais. */
	private String senhaDigitada;
	
	/** Guarda a id do material que estava sendo processado. Caso dê erro, é possível saber qual causou o problema. */
	private int idMaterialProcessando;
	
	/**
	 * Construtor padrão.
	 * 
	 * @param idsMateriais
	 * @param idUsuario
	 * @param tipoUsuario
	 * @param senhaDigitada
	 */
	public MovimentoRenovaEmprestimo(List <Integer> idsMateriais, UsuarioBiblioteca usuarioBiblioteca, String senhaDigitada){
		this.usuarioBiblioteca = usuarioBiblioteca;
		this.idsMateriais = idsMateriais;
		this.senhaDigitada = senhaDigitada;
		setCodMovimento(SigaaListaComando.RENOVA_EMPRESTIMO);
	}

	public List<Integer> getIdsMateriais() {
		return idsMateriais;
	}

	public UsuarioBiblioteca getUsuarioBiblioteca() {
		return usuarioBiblioteca;
	}

	public String getSenhaDigitada() {
		return senhaDigitada;
	}
	
	public void setIdMaterialProcessando(int idMaterialProcessando) {
		this.idMaterialProcessando = idMaterialProcessando;
	}

	public int getIdMaterialProcessando() {
		return idMaterialProcessando;
	}
	
}