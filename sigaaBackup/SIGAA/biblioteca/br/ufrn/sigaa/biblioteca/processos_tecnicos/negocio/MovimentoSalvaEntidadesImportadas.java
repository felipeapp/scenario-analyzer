/*
 * MovimentoSalvaEntidadesImportadas.java
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
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;

/**
 *
 * Classe que passa os dados para o processador {@link ProcessadorSalvaEntidadesImportadas}.
 *
 * @author jadson
 * @since 31/08/2009
 * @version 1.0 criacao da classe
 *
 */
public class MovimentoSalvaEntidadesImportadas extends AbstractMovimentoAdapter{

	/** lista de t�tulos importadas */
	private List<TituloCatalografico> listaTitulosImportados;
	/** lista de Autoridade importadas */
	private List<Autoridade> listaAutoriadesImportadas;
	
	/** Se vai salvar T�tulos ou autoridades*/
	private boolean salvarTitulos;
	
	/** Guarda para n�o precisar ficar buscando no banco */
	private List<ClassificacaoBibliografica> classificacoesUtilizadas = new ArrayList<ClassificacaoBibliografica>();
	
	/**
	 * Construtor padr�o para t�tulos.
	 * @param listaEntidadesImportadas
	 */
	public MovimentoSalvaEntidadesImportadas(List<TituloCatalografico> listaTitulosImportados, List<ClassificacaoBibliografica> classificacoesUtilizadas) {
		this.listaTitulosImportados = listaTitulosImportados;
		this.salvarTitulos = true;
		this.classificacoesUtilizadas = classificacoesUtilizadas;
	}

	/**
	 * Construtor padr�o para autoridades.
	 * @param listaEntidadesImportadas
	 * @param a apenas para diferenciar um construtor de outro.
	 */
	public MovimentoSalvaEntidadesImportadas(List<Autoridade> listaAutoriadesImportadas, boolean a) {
		this.listaAutoriadesImportadas = listaAutoriadesImportadas;
		this.salvarTitulos = false;
	}

	public List<TituloCatalografico> getListaTitulosImportados() {
		return listaTitulosImportados;
	}

	public List<Autoridade> getListaAutoriadesImportadas() {
		return listaAutoriadesImportadas;
	}

	public boolean isSalvarTitulos() {
		return salvarTitulos;
	}

	public List<ClassificacaoBibliografica> getClassificacoesUtilizadas() {
		return classificacoesUtilizadas;
	}
	
	
	
}
