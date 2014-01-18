package br.ufrn.sigaa.ensino_rede.relatorios.helper;

import java.util.List;
import java.util.Map;

import br.ufrn.arq.negocio.validacao.ListaMensagens;

public class RelatoriosEnsinoRedeValidate {

	public static void validate(Map<String, List<?>> dados, ListaMensagens erros) {
		if ( dados.get("filtrarTaxa") != null && dados.get("filtrarTaxa").isEmpty() ) {
			erros.addErro("É necessário informar a taxa que deve ser verificada.");
		}
	}

}