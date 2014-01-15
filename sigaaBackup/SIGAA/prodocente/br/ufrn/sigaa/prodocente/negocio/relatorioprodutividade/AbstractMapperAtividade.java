package br.ufrn.sigaa.prodocente.negocio.relatorioprodutividade;

import java.util.Collection;
import java.util.Hashtable;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.integracao.interfaces.FormacaoAcademicaRemoteService;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.negocio.RelatorioHelper;
import br.ufrn.sigaa.prodocente.relatorios.dominio.ViewAtividadeBuilder;

/**
 * Classe abstrata que realiza o processamento das atividades para a construção do relatório de produtividade
 * Todas as classes que realizam o mapeamento do comportamento dos items do relatório de produtividade que são atividades devem herdar desta classe 
 * pois é nesta classe onde é implementado o comportamento principal. 
 * @author Victor Hugo
 *
 */
public abstract class AbstractMapperAtividade implements AtividadeMapper {

	/**
	 * Id do ItemRelatorioProdutividade que implementa este mapper.
	 */
	private int idItemRelatorioProdutividade;
	
	/**
	 * o serviço remoto que busca Formações Academicas (informações armazenadas no banco administrativo)
	 * deve ser setado aqui pois será utilizado por alguns mappers
	 */
	protected FormacaoAcademicaRemoteService serviceFormacao;

	/**
	 * realiza a busca das atividades, este método não é implementado nesta classe abstrata, porém DEVE ser 
	 * sobrescrito pelas classes filhas a fim de buscar as atividades mapeadas pela classe em questão
	 * @param docente
	 * @param ano
	 * @param validade
	 * @return
	 * @throws DAOException
	 */
	public abstract Collection<? extends ViewAtividadeBuilder> buscarAtividades(Servidor docente, Integer ano, Integer validade) throws DAOException;
	
	/**
	 * Método que realiza o processamento das atividades de cada docente.
	 * Implementa o padrão TemplateMethod. Este é o template que faz chamada dos métodos abstratos que serão implementados pelas especializações desta classe.
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
	 * O cálculo da pontuação deve ser sobrescrito por cada mapper específico.
	 */
	public float calculaPontuacao(ViewAtividadeBuilder atividade){
		return 0;
	}
}
