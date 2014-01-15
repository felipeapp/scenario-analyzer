/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
import br.ufrn.sigaa.biblioteca.controle_estatistico.dominio.DadosInternosRelatorioCrescimentoPorAgrupamento.DadosInternos;

/**
 *
 * <p>Agrupa os resultados do relat�rio de crescimento dentro de um mesmo m�s </p>
 * 
 * @author jadson
 *
 */
public class DadosInternosRelatorioCrescimentoPorMes {

	/**
	 * O m�s de crescimento dos T�tulos ou Materiais
	 */
	private int mes;
	
	/**
	 * <p>Para cada ano e m�s do relat�rio vai conter uma lista com a classifica��o, quantidade do crescimento,
	 * quantidade anteritor e o agrupamento utilizado (Tipo Materail ou Cole��o), para o caso de materiais </p>
	 */
	private List<DadosInternosRelatorioCrescimentoPorAgrupamento> dadosInternos;	
	
	/**
	 * Dentro do mesmo m�s retorna os totais por classifica��o
	 */
	private Map<String, Long> totaisPorClasificacao = new TreeMap<String, Long>();
	
	/**
	 * Verifica se o relat�rio est� contandos os dados anteriores.  Em caso afirmativo, os resultados v�o 
	 * se acumulando, neste caso a totaliza��o � sempre o �ltimo resultado.  Caso contr�rio, soma todos os resultados 
	 * para obter a totaliza��o.
	 */
	private boolean contandoResultadosAnteriores = false;
	
	
	public DadosInternosRelatorioCrescimentoPorMes(int mes) {
		this.mes = mes;
	}

	public DadosInternosRelatorioCrescimentoPorMes(Double mes) {
		if(mes != null)
			this.mes = mes.intValue();
	}
	
	/**
	 * Retorna  o somat�rio do crescimento por mes.
	 *
	 * @return
	 */
	public long getTotalPorMes(){
		
		long somatorio = 0l;
		
		if(dadosInternos != null){
		
			if(! contandoResultadosAnteriores ){
				for (DadosInternosRelatorioCrescimentoPorAgrupamento dado : dadosInternos) {
					somatorio += dado.getTotalPorAgrupamento();
				}
			}else{
				somatorio = dadosInternos.get(dadosInternos.size()-1).getTotalPorAgrupamento();
			}
		}
		
		return somatorio;
	}
	
	/**
	 * <p>Dentro deste m�s realiza a totaliza��o por classifica��o utilizada. (pode ser black ou CDU).</p>
	 *
	 * <p>� obrigat�rio ser chamado para cada m�s antes do relat�rio ser mostrado para o usu�rio.</p>
	 *
	 */
	public void totalizaPorClassificacao(){
		if(totaisPorClasificacao == null){
			totaisPorClasificacao = new HashMap<String, Long>();
		}
		
	
		for (DadosInternosRelatorioCrescimentoPorAgrupamento dados : dadosInternos) {
			
			if( ! contandoResultadosAnteriores){
				for (DadosInternos dado : dados.getDadosInternos()) {
					if( totaisPorClasificacao.get(dado.getClassificacao()) != null )
						totaisPorClasificacao.put(dado.getClassificacao(),  ( totaisPorClasificacao.get(dado.getClassificacao()) + ( dado.getQuantidade()+dado.getQuantidadeAnterior() ) ) );
					else
						totaisPorClasificacao.put(dado.getClassificacao(), (dado.getQuantidade()+dado.getQuantidadeAnterior()) );
				}
			}else{ // No caso j� est� totalizado, por j� � somado com o anterior, ent�o repete apenas o valor do �ltimo.
				for (DadosInternos dado : dados.getDadosInternos()) {
					totaisPorClasificacao.put(dado.getClassificacao(), dado.getQuantidadeAnterior() + dado.getQuantidade() );
				}
			}
		}
		
	}
	
	
	/**
	 * 
	 * Adiciona a quantidade de materiais dentro de um agrupamento espec�fico desse m�s.
	 *  
	 *
	 * @param agrupamento
	 * @param classificacao
	 * @param quantidade
	 */
	public void addDadosInternos(String agrupamento,  String classificacao, long quantidade){
		
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternosRelatorioCrescimentoPorAgrupamento>();
		}
		
		
		DadosInternosRelatorioCrescimentoPorAgrupamento dado = new DadosInternosRelatorioCrescimentoPorAgrupamento(agrupamento);
		
		if( dadosInternos.contains(dado) ){
			dado = dadosInternos.get(dadosInternos.indexOf(dado));
			dado.addDadosInternos(classificacao, quantidade);
		}else{
			dado.addDadosInternos(classificacao, quantidade);
			dadosInternos.add(dado);
		}
		
	}

	
	/**
	 * Retorna o resultado da totaliza��o por classifica��o.
	 * 
	 * @return
	 */
	public Map<String, Long> getTotaisPorClasificacao() {
		return totaisPorClasificacao;
	}

	

	/**
	 * Consigura que o relat�rio est� totalizando os resultados anteriores, n�o s� o crescimento.
	 *
	 */
	public void setContandoResultadosAnteriores(){
		contandoResultadosAnteriores = true;
	}
	
	
	/**
	 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
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
		
		for (DadosInternosRelatorioCrescimentoPorAgrupamento dados : dadosInternos) {
			buider.append( dados.toString());
		}
		
		return buider.toString();
	}
	
	/**
	 * Um dado do relat�rio � igual a outro se possuir os mesmos dados pelo qual ele � agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, new String[]{"mes"} );
	}
	
	public List<DadosInternosRelatorioCrescimentoPorAgrupamento> getDadosInternos() {
		return dadosInternos;
	}

	public int getMes() {
		return mes;
	}
	
}
