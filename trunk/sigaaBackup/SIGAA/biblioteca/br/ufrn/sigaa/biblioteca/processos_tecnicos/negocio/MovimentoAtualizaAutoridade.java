/*
 * MovimentoAtualizaAutoridade.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * Campos Universit�rio Lagoa Nova
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

	/** A autoridade que est� sendo atualizada */
	private Autoridade autoridade;
	
	/** O sistema est� atualizando automaticamente a autoridade */
	private boolean alteracaoAutomatica;
	
	/** As classifica��es bibliogr�ficas utilizadas no sistema, utilizado caso precisa atualizar as inforam��es do T�tulo relacionado � autoridade */
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
