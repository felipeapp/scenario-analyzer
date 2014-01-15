/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 27/08/2007
 *
 */
package br.ufrn.rh.dominio;

import java.util.Date;

import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.util.CalendarUtils;

/**
 * Classe de Processo de Avalia��o
 *
 * @author Rejane
 *
 */
public class ProcessoAvaliacao implements PersistDB {

	protected int id;
	
	/** Descri��o do processo */
	protected String descricao;

	/** Per�odo para a etapa de planejamento */
	protected Date dataInicioPlanejamento;

	protected Date dataFinalPlanejamento;

	/** Per�odo para a etapa de acompanhamento */
	protected Date dataInicioAcompanhamento;

	protected Date dataFinalAcompanhamento;

	/** Per�odo para a etapa de registro */
	protected Date dataInicioRegistro;

	protected Date dataFinalRegistro;

	/** Per�odo para a etapa de valida��o */
	protected Date dataInicioValidacao;

	protected Date dataFinalValidacao;

	/** Per�odo para a etapa de processamento */
	protected Date dataInicioProcessamento;

	protected Date dataFinalProcessamento;
	
	/** Per�odo para a etapa de localiza��o */
	protected Date dataInicioLocalizacao;

	protected Date dataFinalLocalizacao;

	/** Per�odo de vig�ncia*/
	protected Date dataInicioVigencia;

	protected Date dataFimVigencia;
	
	/** Data de cadastro do processo */
	protected Date dataCadastro;
	
	public ProcessoAvaliacao(){}
	public ProcessoAvaliacao(int id){
		this.id = id;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Date getDataInicioPlanejamento() {
		return dataInicioPlanejamento;
	}

	public void setDataInicioPlanejamento(Date dataInicioPlanejamento) {
		this.dataInicioPlanejamento = dataInicioPlanejamento;
	}

	public Date getDataFinalPlanejamento() {
		return dataFinalPlanejamento;
	}

	public void setDataFinalPlanejamento(Date dataFinalPlanejamento) {
		this.dataFinalPlanejamento = dataFinalPlanejamento;
	}

	public Date getDataInicioAcompanhamento() {
		return dataInicioAcompanhamento;
	}

	public void setDataInicioAcompanhamento(Date dataInicioAcompanhamento) {
		this.dataInicioAcompanhamento = dataInicioAcompanhamento;
	}

	public Date getDataFinalAcompanhamento() {
		return dataFinalAcompanhamento;
	}

	public void setDataFinalAcompanhamento(Date dataFinalAcompanhamento) {
		this.dataFinalAcompanhamento = dataFinalAcompanhamento;
	}

	public Date getDataInicioRegistro() {
		return dataInicioRegistro;
	}

	public void setDataInicioRegistro(Date dataInicioRegistro) {
		this.dataInicioRegistro = dataInicioRegistro;
	}

	public Date getDataFinalRegistro() {
		return dataFinalRegistro;
	}

	public void setDataFinalRegistro(Date dataFinalRegistro) {
		this.dataFinalRegistro = dataFinalRegistro;
	}

	public Date getDataInicioValidacao() {
		return dataInicioValidacao;
	}

	public void setDataInicioValidacao(Date dataInicioValidacao) {
		this.dataInicioValidacao = dataInicioValidacao;
	}

	public Date getDataFinalValidacao() {
		return dataFinalValidacao;
	}

	public void setDataFinalValidacao(Date dataFinalValidacao) {
		this.dataFinalValidacao = dataFinalValidacao;
	}

	public Date getDataInicioProcessamento() {
		return dataInicioProcessamento;
	}

	public void setDataInicioProcessamento(Date dataInicioProcessamento) {
		this.dataInicioProcessamento = dataInicioProcessamento;
	}

	public Date getDataFinalProcessamento() {
		return dataFinalProcessamento;
	}

	public void setDataFinalProcessamento(Date dataFinalProcessamento) {
		this.dataFinalProcessamento = dataFinalProcessamento;
	}
	
	public Date getDataInicioLocalizacao() {
		return dataInicioLocalizacao;
	}
	
	public void setDataInicioLocalizacao(Date dataInicioLocalizacao) {
		this.dataInicioLocalizacao = dataInicioLocalizacao;
	}
	
	public Date getDataFinalLocalizacao() {
		return dataFinalLocalizacao;
	}
	
	public void setDataFinalLocalizacao(Date dataFinalLocalizacao) {
		this.dataFinalLocalizacao = dataFinalLocalizacao;
	}

	public Date getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(Date dataCadastro) {
		this.dataCadastro = dataCadastro;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getDenominacaoFormatada(){
		return descricao + " - " + CalendarUtils.getMesAbreviado(dataCadastro) + "/" + CalendarUtils.getAno(dataCadastro);
	}
}

