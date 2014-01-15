/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/08/2011
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.List;

import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * @author David Pereira
 *
 */
public interface ConsultaJubilamento {

	public List<Discente> buscarAlunosPassiveisJubilamento(int ano, int periodo);
	
}
