/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jean
 *
 */
public class LinhaRelatorioQuantitativoTrancamentos {

//	Map<String, BigInteger> justificativas = new HashMap<String, BigInteger>();
	
	private List<LinhaJustificativaRelatorioQuantitativoTrancamentos> justificativas =  new ArrayList<LinhaJustificativaRelatorioQuantitativoTrancamentos>();
	
	private String codigoTurma;
	
	private String codigoCurricular;

	private String nome;
	
	private int idUnidade;
	
	private String departamento;
	
	private int idUnidadeGestora;
	
	private String centro;
	
	private int anoPeriodo;
	
	private BigInteger total;
	
	
	public LinhaRelatorioQuantitativoTrancamentos() {
	
	}


//	public Map<String, BigInteger> getJustificativas() {
//		return justificativas;
//	}
//
//
//	public void setJustificativas(Map<String, BigInteger> justificativas) {
//		this.justificativas = justificativas;
//	}


	public String getCodigoTurma() {
		return codigoTurma;
	}

	public List<LinhaJustificativaRelatorioQuantitativoTrancamentos> getJustificativas() {
		return justificativas;
	}


	public void setJustificativas(
			List<LinhaJustificativaRelatorioQuantitativoTrancamentos> justificativas) {
		this.justificativas = justificativas;
	}


	public void setCodigoTurma(String codigoTurma) {
		this.codigoTurma = codigoTurma;
	}

	public String getCodigoCurricular() {
		return codigoCurricular;
	}

	public void setCodigoCurricular(String codigoCurricular) {
		this.codigoCurricular = codigoCurricular;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public int getIdUnidade() {
		return idUnidade;
	}

	public void setIdUnidade(int idUnidade) {
		this.idUnidade = idUnidade;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public int getIdUnidadeGestora() {
		return idUnidadeGestora;
	}

	public void setIdUnidadeGestora(int idUnidadeGestora) {
		this.idUnidadeGestora = idUnidadeGestora;
	}

	public String getCentro() {
		return centro;
	}

	public void setCentro(String centro) {
		this.centro = centro;
	}

	public int getAnoPeriodo() {
		return anoPeriodo;
	}

	public void setAnoPeriodo(int anoPeriodo) {
		this.anoPeriodo = anoPeriodo;
	}

	public BigInteger getTotal() {
		return total;
	}

	public void setTotal(BigInteger total) {
		this.total = total;
	}

}