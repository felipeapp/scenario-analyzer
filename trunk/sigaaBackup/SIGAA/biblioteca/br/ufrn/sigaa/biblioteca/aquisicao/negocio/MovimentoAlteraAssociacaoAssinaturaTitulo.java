/*
 * MovimentoAlteraAssociacaoAssinaturaTitulo.java
 *
 * Universidade Federal do Rio Grande no Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/11/2009
 * Autor: jadson
 *
 */
package br.ufrn.sigaa.biblioteca.aquisicao.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.Assinatura;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *   Passa os dados para o Processador
 *   
 * @author jadson
 *
 */
public class MovimentoAlteraAssociacaoAssinaturaTitulo extends AbstractMovimentoAdapter{

	private Assinatura assinatura;  // a assinatura cuja associação com o Título vai mudar
	
	private CacheEntidadesMarc antigoTituloAssinatura; // estava associada a esse Título.
	
	private CacheEntidadesMarc novoTituloAssinatura; // vai ficar associada a esse novo Título

	
	/**
	 * Construtor do movimento
	 */
	public MovimentoAlteraAssociacaoAssinaturaTitulo(Assinatura assinatura, 
			CacheEntidadesMarc antigoTituloAssinatura, CacheEntidadesMarc novoTituloAssinatura) {
		this.assinatura = assinatura;
		this.antigoTituloAssinatura = antigoTituloAssinatura;
		this.novoTituloAssinatura = novoTituloAssinatura;
	}

	
	public Assinatura getAssinatura() {
		return assinatura;
	}

	public CacheEntidadesMarc getAntigoTituloAssinatura() {
		return antigoTituloAssinatura;
	}

	public CacheEntidadesMarc getNovoTituloAssinatura() {
		return novoTituloAssinatura;
	}
	
	
	
}
