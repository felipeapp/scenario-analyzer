/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 26/06/2007
 */
package br.ufrn.arq.arquivos;

/**
 * O cluster de arquivo serve para que o sistema possa armazenar arquivos
 * através de vários bancos de dados sem necessitar
 *
 * @author Gleydson Lima
 *
 */
public class ClusterArquivo implements Comparable<ClusterArquivo> {

	private int numeroCluster;

	private String ip;

	private long capacidade;

	private long utilizado;

	private int qtdArquivos;

	private short peso;

	private boolean ativo;

	private String dataSource;

	// utilizado para decidir qual cluster enviar o arquivo
	private int uploads;

	// utilizado dinamicamente para calculo de decremento de prioridade
	private int prioridade;

	public boolean isAtivo() {
		return ativo;
	}

	public void setAtivo(boolean ativo) {
		this.ativo = ativo;
	}

	public long getCapacidade() {
		return capacidade;
	}

	public void setCapacidade(long capacidade) {
		this.capacidade = capacidade;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getNumeroCluster() {
		return numeroCluster;
	}

	public void setNumeroCluster(int numeroCluster) {
		this.numeroCluster = numeroCluster;
	}

	public short getPeso() {
		return peso;
	}

	public void setPeso(short peso) {
		this.peso = peso;
	}

	public int getQtdArquivos() {
		return qtdArquivos;
	}

	public void setQtdArquivos(int qtdArquivos) {
		this.qtdArquivos = qtdArquivos;
	}

	public long getUtilizado() {
		return utilizado;
	}

	public void setUtilizado(long utilizado) {
		this.utilizado = utilizado;
	}

	public int getUploads() {
		return uploads;
	}

	public void setUploads(int uploads) {
		this.uploads = uploads;
	}

	public int getPrioridade() {
		return prioridade;
	}

	public void setPrioridade(int prioridade) {
		this.prioridade = prioridade;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + numeroCluster;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ClusterArquivo other = (ClusterArquivo) obj;
		if (numeroCluster != other.numeroCluster)
			return false;
		return true;
	}

	public int compareTo(ClusterArquivo o) {

		if (prioridade > o.getPrioridade()) {
			return 1;
		} else if (o.getPrioridade() == getPrioridade()) {
			return getPeso() - o.getPeso(); // retorna o de maior peso
		} else {
			return -1;
		}

	}

	public void decrementaPrioridade() {
		prioridade--;
	}

	public String getDataSource() {
		return dataSource;
	}

	public void setDataSource(String dataSource) {
		this.dataSource = dataSource;
	}

}
