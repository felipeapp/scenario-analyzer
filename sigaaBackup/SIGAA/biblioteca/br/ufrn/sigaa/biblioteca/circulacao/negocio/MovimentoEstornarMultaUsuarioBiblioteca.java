/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/03/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.negocio;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;

/**
 *
 * <p>Passa os dados para o procesador</p>
 * 
 * @author jadson
 *
 */
public class MovimentoEstornarMultaUsuarioBiblioteca extends MovimentoCadastro{

	/**
	 * A multa que vai ser estornada
	 */
	private MultaUsuarioBiblioteca multa;
	
	/**
	 * O usuário a quem a multa pertence
	 */
	private UsuarioBiblioteca usuarioDaMulta;

	
	
	public MovimentoEstornarMultaUsuarioBiblioteca(MultaUsuarioBiblioteca multa, UsuarioBiblioteca usuarioDaMulta) {
		this.multa = multa;
		this.usuarioDaMulta = usuarioDaMulta;
	}

	public MultaUsuarioBiblioteca getMulta() {
		return multa;
	}

	public UsuarioBiblioteca getUsuarioDaMulta() {
		return usuarioDaMulta;
	}
	
	
	
}
