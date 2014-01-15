package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import static br.ufrn.arq.util.ValidatorUtil.validaDoublePositivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Classe responsável pelas validações do projetos de apoio aos grupos de pesquisa. 
 * 
 * @author Jean Guerethes
 */
public class ProjetoApoioGrupoPesquisaValidator {

	/**
	 * Faz a validação do orçamento de uma ação de extensão.
	 * 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaAdicionaOrcamento(OrcamentoDetalhado orcamento, ListaMensagens lista) {

		validateRequired(orcamento.getDiscriminacao(), "Discriminação", lista);
		validaDoublePositivo(orcamento.getValorUnitario(), "Valor Unitário", lista);
		validateRequired(orcamento.getElementoDespesa(), "Selecione um elemento de despesa.", lista);
		validateRequired(orcamento.getQuantidade(), "Quantidade", lista);
		

		if (orcamento.getElementoDespesa() != null) {
			validateRequiredId(orcamento.getElementoDespesa().getId(), "Selecione um elemento de despesa.", lista);
		}
		if ((orcamento.getQuantidade() != null)	&& (orcamento.getQuantidade() == 0)) {
			lista.addErro("Quantidade deve ser maior que 0 (zero)");
		}
	}
	
}