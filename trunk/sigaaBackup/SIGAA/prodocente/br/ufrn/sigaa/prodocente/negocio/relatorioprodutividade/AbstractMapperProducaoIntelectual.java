/*
 * U	niversidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: Jan 31, 2011
 */
package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ItemRelatorioProdutividade;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ProducaoIntelectual;


/**
 *  Classe abstrata que realiza o processamento das produ��es intelectuais para a constru��o do relat�rio de produtividade
 * Todas as classes que realizam o mapeamento do comportamento dos items do relat�rio de produtividade que s�o produ��es intelectuais devem herdar desta classe 
 * pois � nesta classe onde � implementado o comportamento principal. 
 * @author Victor Hugo
 */
public abstract class AbstractMapperProducaoIntelectual<T extends Producao> implements ProducaoMapper {

	/**
	 * Classe que ser� processada
	 */
	protected Class<T> classe;
	
	protected AbstractMapperProducaoIntelectual(Class<T> classe) {
		this.classe = classe;
	}
	
	/**
	 * Verifica se o parametro passado � do tipo da classe setada 
	 * @param producao
	 * @return
	 */
	protected boolean isOfType(Producao producao) {
		return classe.isInstance(producao);
	}
	
	/**
	 * Verifica se o objeto passado no parametro atende aos crit�rios que definem o item da produ��o intelectual no relat�rio de produtividade
	 * @param producao
	 * @return
	 */
	protected abstract boolean isAtendeCriterios(T producao);
	
	/**
	 * Retorna a data de referencia, por padr�o retorna getDataProducao()
	 * Mas caso necess�rio este m�todo deve ser sobrescrito para retornar a data correta.
	 * @param p
	 * @return
	 */
	public Date getDataReferencia(T p) {
		return p.getDataProducao();
	}
	
	/**
	 * realiza o processamento principal
	 */
	public final void process(Producao producao, int codigoItem,
			List<ItemRelatorioProdutividade> itensAvaliacao, boolean todos,
			Hashtable<Integer, Integer> validades, int anoReferencia,
			Integer anoVigencia,
			Hashtable<Integer, Collection<ProducaoIntelectual>> tabelaProducoes) {
		
		if (  RelatorioHelper.verificarInsercaoItem(getIdItemRelatorioProdutividade(), itensAvaliacao, todos, validades, anoReferencia, anoVigencia)) {
            if ( isTipoAtendeCriterios(producao) ) {
            	RelatorioHelper.adicionarProducao(getIdItemRelatorioProdutividade(),tabelaProducoes, producao, anoVigencia, getDataReferencia((T) producao));
            }
        }
	}

	/**
	 * M�todo que simplifica o processamento, verifica se � do tipo correto e se atende os crit�rios em um �nico local.
	 * @param producao
	 * @return
	 */
	protected boolean isTipoAtendeCriterios(Producao producao) {
		return classe.isInstance(producao) && isAtendeCriterios(classe.cast(producao));
	}
	
	/**
	 * Id do ItemRelatorioProdutividade que implementa este mapper.
	 */
	private int idItemRelatorioProdutividade;

	public int getIdItemRelatorioProdutividade() {
		return idItemRelatorioProdutividade;
	}

	public void setIdItemRelatorioProdutividade(int idItemRelatorioProdutividade) {
		this.idItemRelatorioProdutividade = idItemRelatorioProdutividade;
	}

	
}
