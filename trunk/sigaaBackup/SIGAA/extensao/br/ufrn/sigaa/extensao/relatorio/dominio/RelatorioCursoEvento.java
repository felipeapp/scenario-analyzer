/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 01/08/2008
 *
 */
package br.ufrn.sigaa.extensao.relatorio.dominio;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.Validatable;
import br.ufrn.arq.util.ValidatorUtil;

/**
 * Relat�rio de cursos e eventos de extens�o. � atrav�s deste relat�rio que o
 * coordenador da a��o de extens�o presta contas dos recursos utilizados durante
 * a execu��o da a��o e informa se os objetivos propostos foram alcan�ados.
 * 
 * @author Gleydson
 * @author Ilueny Santos
 * 
 */
@SuppressWarnings("serial")
@Entity
@DiscriminatorValue("CE")
public class RelatorioCursoEvento extends RelatorioAcaoExtensao implements Validatable {

	//Valor cobrado para matricular-se
	@Column(name = "taxa_matricula")
	private double taxaMatricula = 0;

	//Valor total arrecadado
	@Column(name = "total_arrecadado")
	private double totalArrecadado = 0;
	
	//N�meros de concluintes
	@Column(name = "numero_concluintes")
	private int numeroConcluintes = 0;	

	public double getTotalArrecadado() {
		return totalArrecadado;
	}

	public void setTotalArrecadado(double totalArrecadado) {
		this.totalArrecadado = totalArrecadado;
	}

	public ListaMensagens validate() {
		ListaMensagens lista = new ListaMensagens();
		ValidatorUtil.validateRequired(getAtividade().getCursoEventoExtensao(), "Selecione o  de Extens�o", lista);
		ValidatorUtil.validateRequired(getAtividadesRealizadas(), "Atividades Realizadas", lista);
		ValidatorUtil.validateRequired(getResultadosQualitativos(), "Resultados Qualitativos", lista);
		ValidatorUtil.validateRequired(getResultadosQuantitativos(), "Resultados Quantitativos", lista);
		ValidatorUtil.validateRequired(getDificuldadesEncontradas(), "Dificuldades Encontradas", lista);
		ValidatorUtil.validateRequired(getPublicoRealAtingido(), "P�blico Real Atingido", lista);
		return lista;
	}

	public int getNumeroConcluintes() {
	    return numeroConcluintes;
	}

	public void setNumeroConcluintes(int numeroConcluintes) {
	    this.numeroConcluintes = numeroConcluintes;
	}

	public double getTaxaMatricula() {
	    return taxaMatricula;
	}

	public void setTaxaMatricula(double taxaMatricula) {
	    this.taxaMatricula = taxaMatricula;
	}

}
