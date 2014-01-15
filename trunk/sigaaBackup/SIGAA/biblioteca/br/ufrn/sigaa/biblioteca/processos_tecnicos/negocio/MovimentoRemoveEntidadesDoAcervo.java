/*
 * MovimentoRemoveEntidadesDoAcervo.java
 *
 * UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE
 * Superintendência de Informática
 * Diretoria de Sistemas
 * Campos Universitário Lagoa Nova
 * Natal - RN - Brasil
 *
 */
package br.ufrn.sigaa.biblioteca.processos_tecnicos.negocio;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.CacheEntidadesMarc;

/**
 *    Passa os dados para o processador {@link ProcessadorRemoveEntidadesDoAcervo}
 *
 * @author jadson
 * @since 25/09/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoRemoveEntidadesDoAcervo extends AbstractMovimentoAdapter{
	
	/** O cache do Título, autoridade, ou artigo que vai ser removido. */
	private CacheEntidadesMarc entidade; 
	
	private boolean removendoTitulo = false;
	private boolean removendoAutoridade = false;
	private boolean removendoArtigo = false;
	
	public MovimentoRemoveEntidadesDoAcervo(CacheEntidadesMarc entidade) {
		super();
		this.entidade = entidade;
	}

	public void setRemocaoDeTitulo() {
		removendoTitulo = true;
		removendoAutoridade = false;
		removendoArtigo = false;
	}
	
	public void setRemocaoDeAutoridade() {
		removendoTitulo = false;
		removendoAutoridade = true;
		removendoArtigo = false;
	}
	
	public void setRemocaoDeArtigo() {
		this.removendoTitulo = false;
		removendoAutoridade = false;
		removendoArtigo = true;
	}
	
	public CacheEntidadesMarc getEntidade() {
		return entidade;
	}

	public boolean isRemovendoTitulo() {
		return removendoTitulo;
	}

	public boolean isRemovendoAutoridade() {
		return removendoAutoridade;
	}

	public boolean isRemovendoArtigo() {
		return removendoArtigo;
	}

}
