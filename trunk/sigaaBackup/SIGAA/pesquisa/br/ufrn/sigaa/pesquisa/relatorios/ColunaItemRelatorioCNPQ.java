package br.ufrn.sigaa.pesquisa.relatorios;

/**
 * Auxiliar na construção do cabeçalho dos relatórios CNPq
 * 
 * @author Jean Guerethes
 */
public class ColunaItemRelatorioCNPQ implements Comparable<ColunaItemRelatorioCNPQ> {

	private Integer ordem;
	private String coluna;
	
	public ColunaItemRelatorioCNPQ(Integer ordem, String descricao) {
		this.ordem  = ordem;
		this.coluna = descricao;
	}
	
	@Override
	public int compareTo(ColunaItemRelatorioCNPQ o) {
		return ordem.compareTo(o.getOrdem());
	}

	@Override
	public String toString() {
		return getColuna();
	}
	
	public Integer getOrdem() {
		return ordem;
	}

	public void setOrdem(Integer ordem) {
		this.ordem = ordem;
	}

	public String getColuna() {
		return coluna;
	}

	public void setColuna(String coluna) {
		this.coluna = coluna;
	}

	@Override
	public boolean equals(Object obj) {
		return this.coluna.equals(((ColunaItemRelatorioCNPQ) obj).getColuna());
	}
	
}