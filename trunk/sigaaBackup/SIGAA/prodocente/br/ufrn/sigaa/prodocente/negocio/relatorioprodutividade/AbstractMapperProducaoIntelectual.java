/*
 * U	niversidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
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
 *  Classe abstrata que realiza o processamento das produções intelectuais para a construção do relatório de produtividade
 * Todas as classes que realizam o mapeamento do comportamento dos items do relatório de produtividade que são produções intelectuais devem herdar desta classe 
 * pois é nesta classe onde é implementado o comportamento principal. 
 * @author Victor Hugo
 */
public abstract class AbstractMapperProducaoIntelectual<T extends Producao> implements ProducaoMapper {

	/**
	 * Classe que será processada
	 */
	protected Class<T> classe;
	
	protected AbstractMapperProducaoIntelectual(Class<T> classe) {
		this.classe = classe;
	}
	
	/**
	 * Verifica se o parametro passado é do tipo da classe setada 
	 * @param producao
	 * @return
	 */
	protected boolean isOfType(Producao producao) {
		return classe.isInstance(producao);
	}
	
	/**
	 * Verifica se o objeto passado no parametro atende aos critérios que definem o item da produção intelectual no relatório de produtividade
	 * @param producao
	 * @return
	 */
	protected abstract boolean isAtendeCriterios(T producao);
	
	/**
	 * Retorna a data de referencia, por padrão retorna getDataProducao()
	 * Mas caso necessário este método deve ser sobrescrito para retornar a data correta.
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
	 * Método que simplifica o processamento, verifica se é do tipo correto e se atende os critérios em um único local.
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
