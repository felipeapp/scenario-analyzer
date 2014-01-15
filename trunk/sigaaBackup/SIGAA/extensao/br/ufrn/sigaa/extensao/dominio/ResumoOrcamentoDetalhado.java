/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/10/2007
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/*******************************************************************************
 * Classe utilizada na montagem da tabela de orçamentos informado no ato do
 * cadastro da atividade de extensão.
 * <br>
 * Uso somente na view
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class ResumoOrcamentoDetalhado {

	private Collection<OrcamentoDetalhado> orcamentos = new ArrayList<OrcamentoDetalhado>();

	public ResumoOrcamentoDetalhado() {
	}

	public Collection<OrcamentoDetalhado> getOrcamentos() {
		return orcamentos;
	}

	public void setOrcamentos(Collection<OrcamentoDetalhado> orcamentos) {
		this.orcamentos = orcamentos;
	}

	public double getQuantidadeTotal() {

		double quantidadeTotal = 0;

		for (OrcamentoDetalhado orc : orcamentos) {
			double orcamento = orc.getQuantidade() != null ? orc.getQuantidade() : 0;
			quantidadeTotal += orcamento;
		}

		return quantidadeTotal;

	}

	public double getValorTotalRubrica() {
		double valorTotal = 0;
		for (OrcamentoDetalhado orc : orcamentos) {
			valorTotal += orc.getValorTotal();
		}
		return UFRNUtils.truncateDouble(valorTotal, 2);
	}

}
