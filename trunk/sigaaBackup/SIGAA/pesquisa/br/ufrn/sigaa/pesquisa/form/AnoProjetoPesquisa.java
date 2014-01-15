package br.ufrn.sigaa.pesquisa.form;

import java.text.ParseException;
import java.util.Date;

import br.ufrn.arq.util.CalendarUtils;

/**
 * Classe auxiliar para a flexibilização da quantidade de anos do edital de Pesquisa.
 * 
 * @author Jean Guerethes
 */
public class AnoProjetoPesquisa {

	/** Quantidade em anos de um edital de pesquisa */
	private int qntAnos;
	/** Data Final de execução de um projeto de Pesquisa */
	private Date fimExecucao;

	public int getQntAnos() {
		return qntAnos;
	}
	public void setQntAnos(int qntAnos) {
		this.qntAnos = qntAnos;
	}
	public Date getFimExecucao() {
		return fimExecucao;
	}
	public void setFimExecucao(Date fimExecucao) {
		this.fimExecucao = fimExecucao;
	}
	
	/** Retorna a data final da execução formatada */
	public String getDataFormatada() throws ParseException{
		return CalendarUtils.format(fimExecucao, "dd/MM/yyyy");
	}
	
}