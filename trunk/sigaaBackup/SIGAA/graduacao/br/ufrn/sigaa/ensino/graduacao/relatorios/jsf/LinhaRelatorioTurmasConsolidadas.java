/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * 02/07/2007
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;


/**
 * Classe auxiliar utilizada pra gerar relatórios de turmas consolidadas
 * @author leonardo
 *
 */
public class LinhaRelatorioTurmasConsolidadas {

	private String nome;
	
	private String codigo;

	private String situacao;
	
	private long total;
	
	public LinhaRelatorioTurmasConsolidadas() {
	}	

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
	
}
