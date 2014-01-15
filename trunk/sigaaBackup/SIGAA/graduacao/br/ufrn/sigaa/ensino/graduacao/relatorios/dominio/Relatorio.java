/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.dominio;

/**
 * Configura o nome, descrição e o método de Consulta que será chamado no controlador para invocar o relatório no banco de dados.
 *
 * @author Ricardo Wendell
 *
 */
public abstract class Relatorio {

	protected String titulo;

	protected String descricao;

	protected String metodoConsulta;

	public String getDescricao() {
		return this.descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getMetodoConsulta() {
		return this.metodoConsulta;
	}

	public void setMetodoConsulta(String metodoConsulta) {
		this.metodoConsulta = metodoConsulta;
	}

	public String getTitulo() {
		return this.titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

}
