/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 01/07/2011
 */

package br.ufrn.sigaa.arq.vinculo.processamento;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.sigaa.arq.vinculo.tipos.TipoVinculo;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.dominio.VinculoUsuario;

/**
 * Classe para guardar informações do processamento dos vínculos
 * 
 * @author Henrique André
 *
 */
public class DadosProcessamentoVinculos {

	/**
	 * Usuário que está sendo processados os vínculos
	 */
	private Usuario usuario;
	
	/**
	 * Quantidade de vínculos
	 */
	private int numero;
	
	/**
	 * Lista de vínculos
	 */
	private List<VinculoUsuario> vinculos = new ArrayList<VinculoUsuario>();

	public DadosProcessamentoVinculos(Usuario usuario) {
		this.usuario = usuario;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

	public List<VinculoUsuario> getVinculos() {
		return vinculos;
	}

	public void setVinculos(List<VinculoUsuario> vinculos) {
		this.vinculos = vinculos;
	}

	/**
	 * Incrementa o número dos vínculos
	 * @return
	 */
	private int incrementarVinculo () {
		return ++numero;
	}
	
	public List<VinculoUsuario> getVinculosServidor() {
		List<VinculoUsuario> resultado = new ArrayList<VinculoUsuario>();
		
		for (VinculoUsuario vinculo : vinculos) {
			if (vinculo.isVinculoServidor())
				resultado.add(vinculo);
		}
		
		return resultado;
	}
	
	public synchronized void addVinculo(Unidade unidade, boolean prioritario, TipoVinculo tipo) {
		vinculos.add(new VinculoUsuario(incrementarVinculo() , unidade, prioritario, tipo));
	}
	
}
