package br.ufrn.sigaa.monitoria.jsf;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ElementoDespesa;
import br.ufrn.sigaa.extensao.dominio.ResumoOrcamentoDetalhado;
import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

@Component @Scope("request") 
public class ValorFinanciadoProjetoMBean extends SigaaAbstractController<ProjetoEnsino> {

    /** Armazena a tabela orçamentária. **/
    private Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria = new HashMap<ElementoDespesa, ResumoOrcamentoDetalhado>();
	
	public ValorFinanciadoProjetoMBean() {
		obj = new ProjetoEnsino();
	}

	@Override
	public String atualizar() throws ArqException {
		setId();
		setObj( getGenericDAO().findByPrimaryKey(obj.getId(), ProjetoEnsino.class) );
		recalculaTabelaOrcamentaria(obj.getOrcamentosDetalhados());
		if ( !( (obj.isProjetoPAMQEG() || obj.isAmbosProjetos() ) && obj.getEditalMonitoria() != null && !tabelaOrcamentaria.isEmpty()) ) {
			addMensagemErro("O projeto de monitoria não solicitou financiamento ou não é vínculado a edital.");
			return null;
		} else {
			return forward("/monitoria/ValorFinanciado/form.jsp");
		}
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException, NegocioException {
		ValidatorUtil.validaDoublePositivo(obj.getValorFinanciamento(), "Valor do Financiamento", erros);
		if ( hasOnlyErrors() ) {
			addMensagens(erros);
			return null;
		}
		getGenericDAO().updateField(ProjetoEnsino.class, obj.getId(), "valorFinanciamento", obj.getValorFinanciamento());
		return forward("/monitoria/AlterarSituacaoProjeto/lista.jsf?aba=projetos");
	}
	
    /**
     * Facilita a exibição da tabela de orçamentos da ação que está sendo cadastrada/alterada.
     * 
     * @param orcamentos {@link Collection} de {@link OrcamentoDetalhado} com itens do orçamento da ação atual.
     */
	private void recalculaTabelaOrcamentaria(Collection<OrcamentoDetalhado> orcamentos) {
		tabelaOrcamentaria.clear();

		for (OrcamentoDetalhado orca : orcamentos) {
			ResumoOrcamentoDetalhado resumo = tabelaOrcamentaria.get(orca
					.getElementoDespesa());
			if (resumo == null) {
				resumo = new ResumoOrcamentoDetalhado();
			}
			resumo.getOrcamentos().add(orca);
			tabelaOrcamentaria.put(orca.getElementoDespesa(), resumo);
		}
	}

	public Map<ElementoDespesa, ResumoOrcamentoDetalhado> getTabelaOrcamentaria() {
		return tabelaOrcamentaria;
	}

	public void setTabelaOrcamentaria(
			Map<ElementoDespesa, ResumoOrcamentoDetalhado> tabelaOrcamentaria) {
		this.tabelaOrcamentaria = tabelaOrcamentaria;
	}
	
}