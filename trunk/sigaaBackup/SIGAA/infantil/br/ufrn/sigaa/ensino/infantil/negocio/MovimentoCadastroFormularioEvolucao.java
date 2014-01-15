/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/04/2010
 *
 */
package br.ufrn.sigaa.ensino.infantil.negocio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.ensino.infantil.dominio.Area;

/**
 * Movimento específico para Cadastrar o Formulário de Evolução
 * 
 * @author Thalisson Muriel
 *
 */
public class MovimentoCadastroFormularioEvolucao extends MovimentoCadastro{
	
	//Responsável por armazenar os blocos com finalidade de alteração no banco de dados
	private Collection<Area> blocos;
	//Responsável por armazenar a Área com finalidade de alteração no banco de dados
	private Area area;

	public MovimentoCadastroFormularioEvolucao(){
		blocos = new ArrayList<Area>();
	}

	public Collection<Area> getBlocos() {
		return blocos;
	}

	public void setBlocos(Collection<Area> blocos) {
		this.blocos = blocos;
	}
	
	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}
}
