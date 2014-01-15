package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.Hashtable;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Interface que deve ser implementada por todas as classes que realizam 
 * mapeamentos de atividades do relat�rio de produtividade
 * @author Victor Hugo
 *
 */
public interface AtividadeMapper {

	/**
	 * M�todo principal do template
	 * @param idItem
	 * @param docente
	 * @param ano
	 * @param validades
	 * @param mapaAtividades
	 * @throws DAOException
	 */
	public void process(int idItem, Servidor docente, int ano, Hashtable<Integer, Integer> validades, Hashtable<Integer, Collection<ViewAtividadeBuilder> > mapaAtividades) throws DAOException;

	/**
	 * Retorna o id do item do relat�rio de produtividade
	 * @return
	 */
	public int getIdItemRelatorioProdutividade();
	
	/**
	 * seta o id do item do relat�rio de produtividade
	 * @param id
	 */
	public void setIdItemRelatorioProdutividade(int id);
	
	/**
	 * Seta o Servi�o remoto FormacaoAcademicaRemoteService nesta classe para que seja utilizado em alguns casos pelas classes que herdam desta 
	 * e realizam o processamento das atividades
	 * @param serviceFormacao
	 */
	public void setServiceFormacao(FormacaoAcademicaRemoteService serviceFormacao);
	
	/**
	 * Calcula a pontua��o que deve ser conferida para a atividade.
	 * @param atividade
	 * @return
	 */
	public float calculaPontuacao(ViewAtividadeBuilder atividade);
	
}
