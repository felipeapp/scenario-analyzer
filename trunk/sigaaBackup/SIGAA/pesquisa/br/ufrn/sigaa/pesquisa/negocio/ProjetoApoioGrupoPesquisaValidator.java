package br.ufrn.sigaa.pesquisa.negocio;

import static br.ufrn.arq.util.ValidatorUtil.validateRequired;
import static br.ufrn.arq.util.ValidatorUtil.validateRequiredId;
import static br.ufrn.arq.util.ValidatorUtil.validaDoublePositivo;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.projetos.dominio.OrcamentoDetalhado;

/**
 * Classe respons�vel pelas valida��es do projetos de apoio aos grupos de pesquisa. 
 * 
 * @author Jean Guerethes
 */
public class ProjetoApoioGrupoPesquisaValidator {

	/**
	 * Faz a valida��o do or�amento de uma a��o de extens�o.
	 * 
	 * 
	 * @param atividade
	 * @param orcamento
	 * @param lista
	 */
	public static void validaAdicionaOrcamento(OrcamentoDetalhado orcamento, ListaMensagens lista) {

		validateRequired(orcamento.getDiscriminacao(), "Discrimina��o", lista);
		validaDoublePositivo(orcamento.getValorUnitario(), "Valor Unit�rio", lista);
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