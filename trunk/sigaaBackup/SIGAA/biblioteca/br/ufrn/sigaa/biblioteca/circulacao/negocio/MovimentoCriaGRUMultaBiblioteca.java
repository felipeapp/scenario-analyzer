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

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;

/**
 *
 * <p> Passa os dados para o processador </p>
 *
 * 
 * @author jadson
 *
 */
public class MovimentoCriaGRUMultaBiblioteca extends AbstractMovimentoAdapter{

	/** Quando é emitido uma GRU por multa. */
	private MultaUsuarioBiblioteca multa;
	
	/** Quando é emitido uma GRU para pagar várias multas */
	private List<MultaUsuarioBiblioteca> multas;
	

	/** Se está emitindo a GRU para pagar uma multa simples ou várias multas numa mesma GRU */
	private boolean emitindoGRUVariasMultas = false;
	
	public MovimentoCriaGRUMultaBiblioteca(MultaUsuarioBiblioteca multa) {
		this.multa = multa;
		emitindoGRUVariasMultas = false;
	}

	public MovimentoCriaGRUMultaBiblioteca(List<MultaUsuarioBiblioteca> multas) {
		this.multas = multas;
		emitindoGRUVariasMultas = true;
	}
	
	public MultaUsuarioBiblioteca getMulta() {
		return multa;
	}

	public List<MultaUsuarioBiblioteca> getMultas() {
		return multas;
	}

	public boolean isEmitindoGRUVariasMultas() {
		return emitindoGRUVariasMultas;
	}
	
	
}
