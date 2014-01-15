package br.ufrn.sigaa.prodocente.relatorios.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.pesquisa.relatorios.MesAno;

/**
 * Classe respons�vel pela constru��o do relat�rio "Relat�rio Quantitativo de Pesquisa", essa classe respons�vel
 * tem a fun��o de formatar o relat�rio de forma que o mesmo n�o ultrapasse as margens e seja agrupado conforme a sele��o do
 * usu�rio. 
 * 
 * @author Jean Guerethes
 */
public class RelatorioQuantitativoBuilder {

	private Map<MesAno, Long> cabecalho;
	private int totalAnos;
	private Collection<String> relatorio;
	
	private void clear(){
		cabecalho = new HashMap<MesAno, Long>();
		relatorio = new ArrayList<String>();
		totalAnos = 0;
	}
	
	/**
	 * Serve para montar o cabe�alho de por ano, para se evitar que seja ultrapassada a margem lateral.
	 *  
	 * @param resultado
	 * @param ano
	 * @return
	 */
	public static String montarCabecalho( Map<String, Map<MesAno, Long>> resultado, int ano ){
		String cabecalho = "";
		Set<String> result = resultado.keySet();
		for (String linha : result) {
			Map<MesAno, Long> cabecalhoMesAno = resultado.get( linha );
			Set<MesAno> header = cabecalhoMesAno.keySet();
				for (MesAno string : header) {
					if ( ano == string.getAno() && !cabecalho.contains(string.toString())) {
						cabecalho += "<td>" + string.toString() + "</td>";
					}
				}
		}
		return cabecalho;
	}

	/**
	 * Repons�vel pela constru��o do corpo do relat�rio e pelo somat�rio das linhas.
	 * 
	 * @param resultado
	 * @param ano
	 * @return
	 */
	public static String montarCorpo( Map<String, Map<MesAno, Long>> resultado, int ano ){
		String cabecalho = "";
		Set<String> result = resultado.keySet();
		for (String linha : result) {
			cabecalho += "<tr>";
			Map<MesAno, Long> cabecalhoMesAno = resultado.get( linha );
			Set<MesAno> header = cabecalhoMesAno.keySet();
			cabecalho += "<td>" + linha + "</td>";
			int total = 0;
			for (MesAno string : header) {
					if ( ano == string.getAno() ) {
						cabecalho += "<td>" + cabecalhoMesAno.get(string) + "</td>";
						total += cabecalhoMesAno.get(string);
					}
				}
			cabecalho += "<td>" + total + "</td>";
			cabecalho += "</tr>";
		}
		return cabecalho;
	}
	
	/**
	 * M�todo respons�vel pela constru��o da tabela quando n�o solicitado o detalhamento das informa�oes.
	 * 
	 * @param resultado
	 * @param dataInicial
	 * @param dataFinal
	 */
	public void builderTable( Map<String, Map<MesAno, Long>> resultado, Date dataInicial, Date dataFinal ){
		clear();
		totalAnos = intervalo(dataInicial, dataFinal);
		String tabela = "";
		for (Integer i = 0; i <= totalAnos; i++) {
			tabela = "";
			tabela = 
				"<table class='listagem'>" +
				  "<caption> Relat�rio " + (CalendarUtils.getAno(dataInicial) + i) + "</caption>" +
					"<tr>" +
						"<td>Descri��o</td>" +
						montarCabecalho(resultado, CalendarUtils.getAno(dataInicial) + i) +	
						"<td><strong>Total</strong></td>" +
					"</tr>" +
					montarCorpo(resultado, CalendarUtils.getAno(dataInicial) + i) +
				"</table>" +
				
				"<br /> ";
			relatorio.add(tabela);
		}
	}

	/**
	 * Met�do respons�vel pela constru��o da tabela de visualiza��o quando solicitado o detalhamento das informa��es.
	 * 
	 * @param resultado
	 * @param dataInicial
	 * @param dataFinal
	 */
	public void builderTableDetalhado( Map<String, Map<String, Map<MesAno, Long>>> resultado, Date dataInicial, Date dataFinal ){
		clear();
		totalAnos = intervalo(dataInicial, dataFinal);
		String tabela = "";
		for (Integer i = 0; i <= totalAnos; i++) {
			Set<String> docente = resultado.keySet();
			for (String nomeDocente : docente) {
				tabela = "";
				tabela = 
					"<table class='listagem'>" +
					  "<caption> " + nomeDocente + " </caption>" +
					  "<tr>" +
							"<td>Descri��o</td>" +
							montarCabecalho(resultado.get(nomeDocente), CalendarUtils.getAno(dataInicial) + i) +	
							"<td><strong>Total</strong></td>" +
						"</tr>" +
						montarCorpo(resultado.get(nomeDocente), CalendarUtils.getAno(dataInicial) + i) +
					"</table>" +
					
					"<br /> ";
				relatorio.add(tabela);
			}
		}
	}

	public Map<MesAno, Long> getCabecalho() {
		return cabecalho;
	}

	public void setCabecalho(Map<MesAno, Long> cabecalho) {
		this.cabecalho = cabecalho;
	}

	private int intervalo( Date dataInicio, Date dataFim ){
		return CalendarUtils.calculoAnos(dataInicio, dataFim);
	}
	
	public int getTotalAnos() {
		return totalAnos;
	}

	public void setTotalAnos(int totalAnos) {
		this.totalAnos = totalAnos;
	}

	public Collection<String> getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(Collection<String> relatorio) {
		this.relatorio = relatorio;
	}
	
}