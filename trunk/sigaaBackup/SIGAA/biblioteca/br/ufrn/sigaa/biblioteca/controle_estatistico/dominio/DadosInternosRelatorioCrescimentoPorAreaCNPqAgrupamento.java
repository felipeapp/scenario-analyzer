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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import br.ufrn.arq.util.EqualsUtil;
import br.ufrn.arq.util.HashCodeUtil;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * <p>Agrupa os dados do relatório de crescimento por área de CNPq dependendo o agrupamento utilizado
 * (Normalmente Coleção, Tipo de Material, Situação de Material ou Biblioteca.) </p>
 * 
 * @author jadson
 *
 */
public class DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento {

	/**
	 * Todoas as possíveis classes principais CDU mostradas no relatório.  Como esse dado dificilmente deve mudar e é válido para os possíveis contextos do sistema, pode 
	 * ficar em forma de constante.
	 * 
	 * <p>Esse dado é utilizado para deixar sempre o relatório com o mesmo número de colunas. Senão fica muito confuso de entender </p>
	 */
	//final static String[] AREAS = new String[]{"CA", "CB", "CET", "CH", "CS", "CSA", "CT", "LLA", "MULTI", "Sem Área"};
	
	/**
	 * A descriação do agrupamento utilizando, por exemplo a descriação da Coleção
	 */
	private String descricaoAgrupamento;
	
	/**
	 * Contém a quantidade de materiais adicionados por área dentro deste agrupamento.
	 */
	private List<DadosInternos> dadosInternos = new ArrayList<DadosInternos>();
	
	/**
	 * <p>Organizar melhor os dados do relatório, já que o relatório é uma matriz</p>
	 * 
	 * <p>Contém a quantidade de materiais por área.</p>
	 * 
	 * @author jadson
	 *
	 */
	public class DadosInternos implements Comparable<DadosInternos>{
		
		/**
		 * A área de busca por onde é feito o agrupamento
		 */
		//private String area;
		
		/**
		 * A descrição da área de busca por onde é feito o agrupamento
		 */
		private String descricaoArea;
		
		/**
		 * A quantidade de materais da área indicada pela variával acima. 
		 */
		private long quantidade;
		
		/**
		 * Mostra a quantidade que existia antes do crescimento.
		 */
		private long quantidadeAnterior = 0l;

		
		/*public DadosInternos(String area) {
			this.area = area;
		}*/
		
		public DadosInternos(String descricaoArea) {
			//this.area = area;
			this.descricaoArea = descricaoArea;
		}
		
		/*public DadosInternos(String area, long quantidade) {
			this.area = area;
			this.quantidade = quantidade;
		}*/
		
		public DadosInternos(String descricaoArea, long quantidade) {
			//this.area = area;
			this.descricaoArea = descricaoArea;
			this.quantidade = quantidade;
		}


		public String getDescricaoAgrupamento() {
			return descricaoAgrupamento;
		}

//		public String getArea() {
//			return area;
//		}

		public String getDescricaoArea() {
			return descricaoArea;
		}

		public long getQuantidade() {
			return quantidade;
		}
		
		public long getQuantidadeAnterior() {
			return quantidadeAnterior;
		}
		
		/**
		 * Método set
		 *
		 * @param quantidade
		 */
		public void setQuantidade(Long quantidade) {
			if(quantidade == null)
				this.quantidade = 0;
			else
			 this.quantidade = quantidade;
		}
		
		/**
		 * Método set
		 *
		 * @param quantidade
		 */
		public void setQuantidadeAnterior(Long quantidadeAnterior) {
			if(quantidadeAnterior == null)
				this.quantidadeAnterior = 0;
			else
			 this.quantidadeAnterior = quantidadeAnterior;
		}
		
		/**
		 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
		 *
		 * @return
		 */
		@Override
		public int hashCode() {
			return HashCodeUtil.hashAll(descricaoArea );
		}

		/**
		 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
		 *
		 * @return
		 */
		@Override
		public boolean equals(Object obj) {
			return EqualsUtil.testEquals(this, obj, new String[]{"descricaoArea"} );
		}
		
		@Override
		public String toString() {
			return "area = "+descricaoArea + " quantidade = "+quantidade;
		}


		/**
		 * Ver comentários da classe pai.<br/>
		 *
		 * @see java.lang.Comparable#compareTo(java.lang.Object)
		 */
		@Override
		public int compareTo(DadosInternos d) {
			return this.descricaoArea.compareTo(d.getDescricaoArea());
		}
		
	}
	
	/**
	 * <p>Método que preenche as classe não retornas pela consulta com o valor zero</p>
	 * <p>Essse método deve ser chamado sempre após realizar a consulta e antes de mostrar os dados para o usuário.
	 * Garante que o relatório vai possuir sempre a mesma quantidade colunas, senão fica muito difícil opera-lo e visualiza-lo.</p>
	 */
	public void adicionaAreasSemCrescimento(Collection<AreaConhecimentoCnpq> areas){
		
		for (AreaConhecimentoCnpq area : areas) {
			DadosInternos d = new DadosInternos(area.getSigla(), 0);
			if(! dadosInternos.contains(d)){
				dadosInternos.add(d);
			}
		}
		
		Collections.sort(dadosInternos, new Comparator<DadosInternos>() {
			@Override
			public int compare(DadosInternos o1, DadosInternos o2) {
				if (o1.descricaoArea == null) {
					if (o2.descricaoArea == null) {
						return 0;
					} else {
						return -1;
					}
				} else {
					if (o2.descricaoArea == null) {
						return 1;
					} else {
						//int comparacaoDescricao = o1.descricaoArea.compareTo(o2.descricaoArea);
						
						//if (comparacaoDescricao == 0) {
							return o1.descricaoArea.compareTo(o2.descricaoArea);
						//} else {
						//	return comparacaoDescricao;
						//}
					}
				}
			}		
		});
	}
	
	
	public DadosInternosRelatorioCrescimentoPorAreaCNPqAgrupamento(String descricaoAgrupamento) {
		this.descricaoAgrupamento = descricaoAgrupamento;
	}


	/**
	 * Adiciona a quantidade de materiais da área
	 *
	 * @param area
	 * @param quantidade
	 */
	public void addDadosInternos(String descricaoArea, long quantidade){
		if(dadosInternos == null){
			dadosInternos = new ArrayList<DadosInternos>();
		}
		
		DadosInternos temp = new DadosInternos(descricaoArea);
		
		if(dadosInternos.contains(temp)){
			temp = dadosInternos.get(dadosInternos.indexOf(temp));
			temp.setQuantidade(quantidade);
		}else{
			dadosInternos.add( new DadosInternos(descricaoArea, quantidade));
		}
		
		
	}
	
	
	/**
	 * Retorna  o somatório do crescimento por agrupamento.
	 *
	 * @return
	 */
	public long getTotalPorAgrupamento(){
		
		long somatorio = 0l;
		if(dadosInternos != null)
		for (DadosInternos dado : dadosInternos) {
			somatorio += dado.getQuantidade();
			somatorio += dado.getQuantidadeAnterior(); // Caso o usuário queira saber a quantidade antes do crescimento
		}
		
		return somatorio;
	}
	
	
	public String getDescricaoAgrupamento() {
		return descricaoAgrupamento;
	}

	public List<DadosInternos> getDadosInternos() {
		return dadosInternos;
	}

	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public int hashCode() {
		return HashCodeUtil.hashAll(descricaoAgrupamento );
	}

	/**
	 * Um dado do relatório é igual a outro se possuir os mesmos dados pelo qual ele é agrupado da consulta
	 *
	 * @return
	 */
	@Override
	public boolean equals(Object obj) {
		return EqualsUtil.testEquals(this, obj, new String[]{"descricaoAgrupamento"} );
	}
	
	/**
	 * Imprime a descrição do agrupamento.<br/>
	 *
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder buider = new StringBuilder();
		
		buider.append( descricaoAgrupamento + " => " );
		
		for (DadosInternos dados : dadosInternos) {
			buider.append( dados.toString());
		}
		
		return buider.toString();
	}
	
	
	
}
