/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 16/06/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.controle_estatistico.dominio;

import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;

/**
 *
 * <p>Classe que agrupa os dados do relatório de crescimento por área CNPq por ano, fica mais fácil de entender do que vários 
 * mapas alinhados.</p>
 *
 * 
 * @author jadson
 *
 */
public class DadosRelatorioCrescimentoPorAreaCNPq {

	/**
	 * O ano de crescimento dos Títulos ou Materiais
	 */
	private int ano;
	
	/**
	 * Quantidade de crescimento por mês dentro deste ano.
	 */
	private List<DadosInternosRelatorioCrescimentoPorAreaCNPqMes> dadosInternos;	

	/**
	 * Construtor
	 * @param ano
	 */
	public DadosRelatorioCrescimentoPorAreaCNPq(int ano) {
		this.ano = ano;
	}
	
	/**
	 * Construtor utilizando para popular os dados pois o banco retorno um double, já que o 
	 * resultado vem de uma função de agregração. 
	 * 
	 * @param ano
	 */
	public DadosRelatorioCrescimentoPorAreaCNPq(Double ano) {
		if(ano != null)
			this.ano = ano.intValue();
	}
	
	/**
	 * Verifica se o relatório está contandos os dados anteriores.  Em caso afirmativo, os resultados vão 
	 * se acumulando, neste caso a totalização é sempre o último resultado.  Caso contrário, soma todos os resultados 
	 * para obter a totalização.
	 */
	private boolean contandoResultadosAnteriores = false;
	
	
	/**
	 * Retorna  o somatório do crescimento por ano.
	 *
	 * @return
	 */
	public long getTotalPorAno(){
		
		long somatorio = 0l;
		if(dadosInternos != null){
			if(! contandoResultadosAnteriores ){
				for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes dado : dadosInternos) {
					somatorio += dado.getTotalPorMes();
				}
			}else{
				somatorio = dadosInternos.get(dadosInternos.size()-1).getTotalPorMes();
			}
		}
		
		return somatorio;
	}
	
	
	/**
	 * Consigura que o relatório está totalizando os resultados anteriores, não só o crescimento.
	 *
	 */
	public void setContandoResultadosAnteriores(){
		contandoResultadosAnteriores = true;
	}
	
	/**
	 * 
	 * Adiciona a quantidade de materiais a um mês expecífico dentro deste ano.
	 *
	 * @param mes
	 * @param agrupamento
	 * @param area
	 * @param quantidade
	 */
	public void adicionaDadosInternosPorMes( Double mes, String agrupamento, String descricaoArea, long quantidade){
		
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternosRelatorioCrescimentoPorAreaCNPqMes>();
		}
		
		DadosInternosRelatorioCrescimentoPorAreaCNPqMes dado = new DadosInternosRelatorioCrescimentoPorAreaCNPqMes(mes);
		
		if( dadosInternos.contains(dado) ){
			dado = dadosInternos.get(dadosInternos.indexOf(dado));
			dado.addDadosInternos(agrupamento, descricaoArea, quantidade);
		}else{
			dado.addDadosInternos(agrupamento, descricaoArea, quantidade);
			dadosInternos.add(dado);
		}
		
	}
	

	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(ano );
	}

	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, new String[]{"ano"} );
	}
	
	
	@Override
	public String toString() {
		StringBuilder buider = new StringBuilder();
		
		buider.append( ano + " => " );
		
		for (DadosInternosRelatorioCrescimentoPorAreaCNPqMes dados : dadosInternos) {
			buider.append( dados.toString());
		}
		
		return buider.toString();
	}
	
	/// Acesso ao dados ///
	
	
	public int getAno() {
		return ano;
	}

	public List<DadosInternosRelatorioCrescimentoPorAreaCNPqMes> getDadosInternos() {
		return dadosInternos;
	}


	
	
}
