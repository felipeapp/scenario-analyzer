/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '17/01/2007'
 *
 */
package br.ufrn.sigaa.prodocente.relatorios.dominio;

/**
 *
 * Representa o item do relatório quantitativo de produções intelectual de uma
 * unidade
 *
 * @author André
 *
 */
public class ItemQuantitativoProducao {

	private String descTipoProducao;

	private int[] anos;

	private int[] resultadoAno;

	public ItemQuantitativoProducao(int[] anos2) {
		anos = anos2;
		resultadoAno = new int[anos.length];
	}

	public int[] getAnos() {
		return anos;
	}

	public void setAnos(int[] anos) {
		this.anos = anos;
	}

	public int[] getResultadoAno() {
		return resultadoAno;
	}

	public void setResultadoAno(int[] resultadoAno) {
		this.resultadoAno = resultadoAno;
	}

	public String getDescTipoProducao() {
		return descTipoProducao;
	}

	public void setDescTipoProducao(String descTipoProducao) {
		this.descTipoProducao = descTipoProducao;
	}

	public Integer getValorTotal() {
		int soma = 0;
		for (int i = 0; i < anos.length; i++) {
			soma += resultadoAno[i];
		}
		return soma;
	}

	public Double getValorMedio() {
		return getValorTotal() / (resultadoAno.length + 0.0);
	}

	public void addResultadoAno(Object arg0, Object res) {
		int ano = (Integer) arg0;
		for (int i = 0; i < resultadoAno.length; i++) {
			if (ano == anos[i])
				resultadoAno[i] = new Integer(res.toString());
		}
	}

	public int getResultPorAno(Object arg0) {
		int ano = (Integer) arg0;
		for (int i = 0; i < resultadoAno.length; i++) {
			if (ano == anos[i])
				return resultadoAno[i];
		}
		return -1;
	}

	@Override
	public String toString() {
		return descTipoProducao + " + " + resultadoAno;
	}
}
