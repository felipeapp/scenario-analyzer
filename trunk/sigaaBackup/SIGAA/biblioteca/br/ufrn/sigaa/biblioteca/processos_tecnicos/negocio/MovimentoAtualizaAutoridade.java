/*
 * MovimentoAtualizaAutoridade.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 * 
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Autoridade;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.ClassificacaoBibliografica;

/**
 *
 *    Dados passados para o processador que atualiza uma autoridade na base. 
 *
 * @author jadson
 * @since 29/04/2009
 * @version 1.0 criacao da classe
 *
 */

public class MovimentoAtualizaAutoridade extends AbstractMovimentoAdapter{

	/** A autoridade que está sendo atualizada */
	private Autoridade autoridade;
	
	/** O sistema está atualizando automaticamente a autoridade */
	private boolean alteracaoAutomatica;
	
	/** As classificações bibliográficas utilizadas no sistema, utilizado caso precisa atualizar as inforamções do Título relacionado à autoridade */
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	public MovimentoAtualizaAutoridade(Autoridade autoridade, boolean alteracaoAutomatica, List<ClassificacaoBibliografica> classificacoesUtilizadas){
		this.autoridade = autoridade;
		this.alteracaoAutomatica = alteracaoAutomatica;
		this.classificacoesUtilizadas = classificacoesUtilizadas;
	}

	public Autoridade getAutoridade() {
		return autoridade;
	}

	public boolean isAlteracaoAutomatica() {
		return alteracaoAutomatica;
	}
	public List<ClassificacaoBibliografica> getClassificacoesUtilizadas() {
		return classificacoesUtilizadas;
	}
	
	
}
