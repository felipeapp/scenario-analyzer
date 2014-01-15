/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 17/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento.DadosInternos;

/**
 *
 * <p>Agrupa os resultados do relatório de crescimento dentro de um mesmo mês </p>
 * 
 * @author jadson
 *
 */
public class DadosInternosRelatorioCrescimentoPorAreaCNPqMes {

	/**
	 * O mês de crescimento dos Títulos ou Materiais
	 */
	private int mes;
	
	/**
	 * <p>Para cada ano e mês do relatório vai conter uma lista com a área, quantidade do crescimento,
	 * quantidade anteritor e o agrupamento utilizado (Tipo Materail ou Coleção), para o caso de materiais </p>
	 */
	private List<DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento> dadosInternos;	
	
	/**
	 * Dentro do mesmo mês retorna os totais por área
	 */
	private Map<String, Long> totaisPorArea = new TreeMap<String, Long>();
	
	/**
	 * Verifica se o relatório está contandos os dados anteriores.  Em caso afirmativo, os resultados vão 
	 * se acumulando, neste caso a totalização é sempre o último resultado.  Caso contrário, soma todos os resultados 
	 * para obter a totalização.
	 */
	private boolean contandoResultadosAnteriores = false;
	
	
	public DadosInternosRelatorioCrescimentoPorAreaCNPqMes(int mes) {
		this.mes = mes;
	}

	public DadosInternosRelatorioCrescimentoPorAreaCNPqMes(Double mes) {
		if(mes != null)
			this.mes = mes.intValue();
	}
	
	/**
	 * Retorna  o somatório do crescimento por mes.
	 *
	 * @return
	 */
	public long getTotalPorMes(){
		
		long somatorio = 0l;
		
		if(dadosInternos != null){
		
			if(! contandoResultadosAnteriores ){
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento dado : dadosInternos) {
					somatorio += dado.getTotalPorAgrupamento();
				}
			}else{
				somatorio = dadosInternos.get(dadosInternos.size()-1).getTotalPorAgrupamento();
			}
		}
		
		return somatorio;
	}
	
	/**
	 * <p>Dentro deste mês realiza a totalização por área utilizada. (pode ser black ou CDU).</p>
	 *
	 * <p>É obrigatório ser chamado para cada mês antes do relatório ser mostrado para o usuário.</p>
	 *
	 */
	public void totalizaPorArea(){
		if(totaisPorArea == null){
			totaisPorArea = new HashMap<String, Long>();
		}
		
	
		for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento dados : dadosInternos) {
			
			if( ! contandoResultadosAnteriores){
				for (DadosInternos dado : dados.getDadosInternos()) {
					if( totaisPorArea.get(dado.getDescricaoArea()) != null )
						totaisPorArea.put(dado.getDescricaoArea(),  ( totaisPorArea.get(dado.getDescricaoArea()) + ( dado.getQuantidade()+dado.getQuantidadeAnterior() ) ) );
					else
						totaisPorArea.put(dado.getDescricaoArea(), (dado.getQuantidade()+dado.getQuantidadeAnterior()) );
				}
			}else{ // No caso já está totalizado, por já é somado com o anterior, então repete apenas o valor do último.
				for (DadosInternos dado : dados.getDadosInternos()) {
					totaisPorArea.put(dado.getDescricaoArea(), dado.getQuantidadeAnterior() + dado.getQuantidade() );
				}
			}
		}
		
	}
	
	
	/**
	 * 
	 * Adiciona a quantidade de materiais dentro de um agrupamento específico desse mês.
	 *  
	 *
	 * @param agrupamento
	 * @param area
	 * @param quantidade
	 */
	public void addDadosInternos(String agrupamento, String descricaoArea, long quantidade){
		
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento>();
		}
		
		
		DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento dado = new DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento(agrupamento);
		
		if( dadosInternos.contains(dado) ){
			dado = dadosInternos.get(dadosInternos.indexOf(dado));
			dado.addDadosInternos(descricaoArea, quantidade);
		}else{
			dado.addDadosInternos(descricaoArea, quantidade);
			dadosInternos.add(dado);
		}
		
	}

	
	/**
	 * Retorna o resultado da totalização por área.
	 * 
	 * @return
	 */
	public Map<String, Long> getTotaisPorArea() {
		return totaisPorArea;
	}

	

	/**
	 * Consigura que o relatório está totalizando os resultados anteriores, não só o crescimento.
	 *
	 */
	public void setContandoResultadosAnteriores(){
		contandoResultadosAnteriores = true;
	}
	
	
	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(mes );
	}

	@Override
	public String toString() {
		StringBuilder buider = new StringBuilder();
		
		buider.append( mes + " => " );
		
		for (DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento dados : dadosInternos) {
			buider.append( dados.toString());
		}
		
		return buider.toString();
	}
	
	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, new String[]{"mes"} );
	}
	
	public List<DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento> getDadosInternos() {
		return dadosInternos;
	}

	public int getMes() {
		return mes;
	}
	
}
