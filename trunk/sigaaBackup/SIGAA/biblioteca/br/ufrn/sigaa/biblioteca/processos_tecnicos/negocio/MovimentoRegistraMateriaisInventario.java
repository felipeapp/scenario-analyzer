/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 15/03/2012
 * 
 */

package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.InventarioAcervoBiblioteca;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.MaterialInformacional;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ResultadoRegistraMateriaisInventario;

/**
 *
 * Movimento que guarda os dados passados ao processador que registra os materiais no invent�rio. 
 *
 * @author felipe
 * @since 15/03/2012
 * @version 1.0 cria��o da classe
 *
 */
public class MovimentoRegistraMateriaisInventario extends AbstractMovimentoAdapter {

	
	/** Lote com c�digos de barras dos materiais para registro no invent�rio. */
	private List<String> codigoBarrasList;
	
	/**
	 * Lote de materiais para registro no invent�rio.
	 * 
	 * OBS: Campo utilizado exclusivamente pelo processador!
	 */
	private List<MaterialInformacional> materiais = new ArrayList<MaterialInformacional>();
	/**
	 * Invent�rio para o qual os materiais ser�o registrados.
	 */
	private InventarioAcervoBiblioteca inventario;
	/**
	 * O resultado do processamento do registro do lote de materiais.
	 */
	private ResultadoRegistraMateriaisInventario resultado = new ResultadoRegistraMateriaisInventario();

	
	public MovimentoRegistraMateriaisInventario(List<String> codigoBarrasList, InventarioAcervoBiblioteca inventario) {
		this.codigoBarrasList = codigoBarrasList;
		this.inventario = inventario;
	}
	
	public MovimentoRegistraMateriaisInventario(InventarioAcervoBiblioteca inventario, List<MaterialInformacional> materiais) {
		this.materiais = materiais;
		this.inventario = inventario;
	}

	public InventarioAcervoBiblioteca getInventario() {
		return inventario;
	}

	public List<String> getCodigoBarrasList() {
		return codigoBarrasList;
	}

	public List<MaterialInformacional> getMaterialList() {
		return materiais;
	}

	public ResultadoRegistraMateriaisInventario getResultado() {
		return resultado;
	}

	
}
