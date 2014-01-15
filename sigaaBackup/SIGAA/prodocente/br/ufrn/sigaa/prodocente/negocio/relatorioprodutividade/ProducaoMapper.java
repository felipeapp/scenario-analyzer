package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.Hashtable;
import java.util.List;

import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ProducaoIntelectual;


/**
 * Interface que deve ser implementada por todas as classe que fazem
 * o processamento do vínculo entre o ítem do relatório de produtividade e as produções dos docentes.
 * @author Victor Hugo
 *
 */
public interface ProducaoMapper {

	/**
	 * método principal do Template Method, realiza o processamento principal
	 * @param producao
	 * @param codigoItem
	 * @param itensAvaliacao
	 * @param todos
	 * @param validades
	 * @param anoReferencia
	 * @param anoVigencia
	 * @param tabelaProducoes
	 */
	public void process(Producao producao, int codigoItem, List<ItemRelatorioProdutividade> itensAvaliacao, boolean todos,
			Hashtable<Integer, Integer> validades, int anoReferencia, Integer anoVigencia, Hashtable<Integer, Collection<ProducaoIntelectual> > tabelaProducoes);

	/**
	 * retorna o id do item do relatório de produtividade
	 * @return
	 */
	public int getIdItemRelatorioProdutividade();
	
	/**
	 * seta o id do item do relatório de produtividade
	 * @param id
	 */
	public void setIdItemRelatorioProdutividade(int id);
	
}
