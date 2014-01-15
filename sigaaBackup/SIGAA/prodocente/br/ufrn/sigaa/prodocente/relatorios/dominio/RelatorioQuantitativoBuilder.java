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
 * Classe responsável pela construção do relatório "Relatório Quantitativo de Pesquisa", essa classe responsável
 * tem a função de formatar o relatório de forma que o mesmo não ultrapasse as margens e seja agrupado conforme a seleção do
 * usuário. 
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
	 * Serve para montar o cabeçalho de por ano, para se evitar que seja ultrapassada a margem lateral.
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
	 * Reponsável pela construção do corpo do relatório e pelo somatório das linhas.
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
	 * Método responsável pela construção da tabela quando não solicitado o detalhamento das informaçoes.
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
				  "<caption> Relatório " + (CalendarUtils.getAno(dataInicial) + i) + "</caption>" +
					"<tr>" +
						"<td>Descrição</td>" +
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
	 * Metódo responsável pela construção da tabela de visualização quando solicitado o detalhamento das informações.
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
							"<td>Descrição</td>" +
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