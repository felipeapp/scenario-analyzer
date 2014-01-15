/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '08/06/2011'
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Relatório de programas de extensão. É através deste relatório que o
 * coordenador da ação de extensão presta contas dos recursos utilizados durante
 * a execução da ação e informa se os objetivos propostos foram alcançados.
 * 
 * @author Ilueny Santos
 * 
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("PG")
public class RelatorioProgramaExtensao extends RelatorioAcaoExtensao implements Validatable {

	@Override
	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(getPublicoRealAtingido(), "Público Real Atingido", lista);
		return lista;
	}

}
