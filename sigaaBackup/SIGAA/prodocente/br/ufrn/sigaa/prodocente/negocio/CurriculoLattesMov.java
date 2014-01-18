/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '18/04/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;

/**
 * Movimento para a importa��o do curr�culo Lattes no ProDocente.
 * 
 * @author David Pereira
 *
 */
public class CurriculoLattesMov extends AbstractMovimentoAdapter {

	/** Lista de produ��es a serem importadas */
	private List<? extends Producao> producoes;

	/** Servidor cujas produ��es est�o sendo importadas */
	private Servidor servidor;
	
	/** Pessoa cujas produ��es est�o sendo importadas */
	private Pessoa pessoa;

	/** XML do arquivo do curr�culo importado */
	private String xml;
	
	/** Conte�do do arquivo sendo importado */
	private String conteudo;
	
	/** Ano de refer�ncia da importa��o */
	private Integer anoReferencia;
	
	/** Indica se h� produ��es n�o importadas */
	private boolean producoesNaoImportadas;
	
	/** Indica se deve importar as produ��es mais recentes que o ano de refer�ncia,
	 * ou seja, cujo ano for maior ou igual ao ano de refer�ncia. */
	private boolean importarProducoesMaisRecentes = false;
	

	public List<? extends Producao> getProducoes() {
		return producoes;
	}

	public void setProducoes(List<? extends Producao> producoes) {
		this.producoes = producoes;
	}

	public void setServidor(Servidor servidor) {
		this.servidor = servidor;
	}

	public Servidor getServidor() {
		return servidor;
	}

	public String getConteudo() {
		return conteudo;
	}

	public void setConteudo(String conteudo) {
		this.conteudo = conteudo;
	}

	public String getXml() {
		return xml;
	}

	public void setXml(String xml) {
		this.xml = xml;
	}

	public Integer getAnoReferencia() {
		return anoReferencia;
	}

	public void setAnoReferencia(Integer anoReferencia) {
		this.anoReferencia = anoReferencia;
	}

	public void setProducoesNaoImportadas(boolean producoesNaoImportadas) {
		this.producoesNaoImportadas = producoesNaoImportadas;
	}

	public boolean isProducoesNaoImportadas() {
		return producoesNaoImportadas;
	}

	public Pessoa getPessoa() {
		return pessoa;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public boolean isImportarProducoesMaisRecentes() {
		return importarProducoesMaisRecentes;
	}

	public void setImportarProducoesMaisRecentes(
			boolean importarProducoesMaisRecentes) {
		this.importarProducoesMaisRecentes = importarProducoesMaisRecentes;
	}

	
}
