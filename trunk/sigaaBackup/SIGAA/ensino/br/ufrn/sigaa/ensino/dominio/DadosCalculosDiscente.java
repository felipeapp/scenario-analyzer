/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/04/2010
 */
package br.ufrn.sigaa.ensino.dominio;

import java.util.Date;

import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

/**
 * Classe contendo informa��es sobre o discente, o seu curr�culo, a
 * sua data de entrada e data de sa�da. Essas informa��es s�o utilizadas 
 * em um cache para agilizar a busca de equival�ncias de componentes
 * para um discente. 
 *  
 * @author David Pereira
 *
 */
public class DadosCalculosDiscente {

	/** Discente em quest�o */
	private Integer idDiscente;
	
	/** Data da entrada do discente no curso */
	private Date dataInicio;
	
	/** Data da conclus�o do curso */
	private Date dataFim;
	
	/** Curr�culo ao qual pertence o discente */
	private Curriculo curriculo;

	public boolean isDatasDisponiveis() {
		return dataInicio != null || dataFim != null;
	}
	
	public Integer getIdDiscente() {
		return idDiscente;
	}

	public void setIdDiscente(Integer idDiscente) {
		this.idDiscente = idDiscente;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public Curriculo getCurriculo() {
		return curriculo;
	}

	public void setCurriculo(Curriculo curriculo) {
		this.curriculo = curriculo;
	}

}
