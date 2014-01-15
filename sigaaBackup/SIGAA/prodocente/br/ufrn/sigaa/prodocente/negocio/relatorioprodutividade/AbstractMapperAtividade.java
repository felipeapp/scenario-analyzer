package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.Hashtable;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe abstrata que realiza o processamento das atividades para a constru��o do relat�rio de produtividade
 * Todas as classes que realizam o mapeamento do comportamento dos items do relat�rio de produtividade que s�o atividades devem herdar desta classe 
 * pois � nesta classe onde � implementado o comportamento principal. 
 * @author Victor Hugo
 *
 */
public abstract class AbstractMapperAtividade implements AtividadeMapper {

	/**
	 * Id do ItemRelatorioProdutividade que implementa este mapper.
	 */
	private int idItemRelatorioProdutividade;
	
	/**
	 * o servi�o remoto que busca Forma��es Academicas (informa��es armazenadas no banco administrativo)
	 * deve ser setado aqui pois ser� utilizado por alguns mappers
	 */
	protected FormacaoAcademicaRemoteService serviceFormacao;

	/**
	 * realiza a busca das atividades, este m�todo n�o � implementado nesta classe abstrata, por�m DEVE ser 
	 * sobrescrito pelas classes filhas a fim de buscar as atividades mapeadas pela classe em quest�o
	 * @param docente
	 * @param ano
	 * @param validade
	 * @return
	 * @throws DAOException
	 */
	public abstract Collection<? extends ViewAtividadeBuilder> buscarAtividades(Servidor docente, Integer ano, Integer validade) throws DAOException;
	
	/**
	 * M�todo que realiza o processamento das atividades de cada docente.
	 * Implementa o padr�o TemplateMethod. Este � o template que faz chamada dos m�todos abstratos que ser�o implementados pelas especializa��es desta classe.
	 */
	public final void process(int idItem, Servidor docente, int ano,
			Hashtable<Integer, Integer> validades,
			Hashtable<Integer, Collection<ViewAtividadeBuilder>> mapaAtividades) throws DAOException {
		
		Integer validade = validades.get(idItem);
		Collection<? extends ViewAtividadeBuilder> atividades = buscarAtividades(docente, ano, validade);
		RelatorioHelper.adicionarAtividade(idItem, mapaAtividades, atividades); 
		
	}
	
	public int getIdItemRelatorioProdutividade() {
		return idItemRelatorioProdutividade;
	}

	public void setIdItemRelatorioProdutividade(int idItemRelatorioProdutividade) {
		this.idItemRelatorioProdutividade = idItemRelatorioProdutividade;
	}
	
	public void setServiceFormacao(FormacaoAcademicaRemoteService serviceFormacao){
		this.serviceFormacao = serviceFormacao;
	}

	/**
	 * O c�lculo da pontua��o deve ser sobrescrito por cada mapper espec�fico.
	 */
	public float calculaPontuacao(ViewAtividadeBuilder atividade){
		return 0;
	}
}
