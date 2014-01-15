package br.ufrn.sigaa.biblioteca.util;

import java.util.List;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.LevantamentoBibliograficoInfra;

/**
 * Classe utilizada pelo MBean LevantamentoBibliograficoInfraMBean
 * para validar o form de cadastramento
 * 
 * @author agostinho
 *
 */

public class ValidarFormSolicitanteLevantBiblio {

	public static ListaMensagens validarFormSolicitante(LevantamentoBibliograficoInfra obj, List<String> linguasSelecionadas, 
			String outraLinguaDescricao, String linguaPadraoSelecionada) {

		ListaMensagens listaMensagens = new ListaMensagens();
		
		listaMensagens = validar(obj, linguasSelecionadas, outraLinguaDescricao, listaMensagens, linguaPadraoSelecionada);
		
		return listaMensagens;
	}

	public static ListaMensagens validarFormFuncionarioBiblioteca(LevantamentoBibliograficoInfra obj, List<String> linguasSelecionadas) {

		ListaMensagens listaMensagens = new ListaMensagens();
		
		if (obj.getFontesPesquisadas() == null || obj.getFontesPesquisadas().equals(""))
			listaMensagens.addErro("� necess�rio informar as fontes pesquisadas!");
		
		return listaMensagens;
	}

	private static ListaMensagens validar(LevantamentoBibliograficoInfra obj, List<String> linguasSelecionadas, String outraLinguaDescricao, 
			ListaMensagens listaMensagens, String outraLinguaSelecionada) {
		
		if (obj.getDetalhesAssunto() == null || obj.getDetalhesAssunto() == "") {
			listaMensagens.addErro("� necess�rio informar um assunto!");
		}
		
		for (String string : linguasSelecionadas) {
				if (string.equals("4")) {
					if (outraLinguaDescricao.equals(""))
						listaMensagens.addErro("Voc� especificou outra lingua. Por favor informe a mesma!");
				}
		}
		
		if (!linguasSelecionadas.contains("4") && !outraLinguaDescricao.equals(""))
			listaMensagens.addErro("Voc� precisa marcar a op��o 'Outra' caso deseje informar outra lingua!");
		
		if (linguasSelecionadas.size() == 0)
			listaMensagens.addErro("Voc� precisa selecionar ao menos uma lingua!");
		
		
		return listaMensagens;
	}

}
