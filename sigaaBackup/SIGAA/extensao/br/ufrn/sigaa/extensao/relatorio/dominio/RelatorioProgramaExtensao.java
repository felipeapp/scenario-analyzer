/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * Relat�rio de programas de extens�o. � atrav�s deste relat�rio que o
 * coordenador da a��o de extens�o presta contas dos recursos utilizados durante
 * a execu��o da a��o e informa se os objetivos propostos foram alcan�ados.
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
		ValidatorUtil.validateRequired(getPublicoRealAtingido(), "P�blico Real Atingido", lista);
		return lista;
	}

}
