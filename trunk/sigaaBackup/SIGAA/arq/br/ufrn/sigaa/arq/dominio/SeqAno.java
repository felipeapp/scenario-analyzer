/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/12/2004
 *
 */
package br.ufrn.sigaa.arq.dominio;


/**
 * Classe que será usada para obter intervalos sequencias com LOCK UPDATE no
 * banco de dados.
 *
 * Este Design foi realizado ao invés de usar uma sequence, pois a
 * transacionalidade do EJB garante que caso o tombamento não seja realizado,
 * essa atualização seja retornada e a sequencia de tombamento.
 *
 * @author Gleydson Lima
 *
 */
public enum SeqAno{

	/** Sequência genérica. */
	SEQUENCIA_GENERICA,
	/** Sequência utlizada em projetos de pesquisa. */
	SEQUENCIA_CODIGO_PROJETO_PESQUISA,
	/** Sequência utlizada em projetos de monitoria. */
	SEQUENCIA_PROJETO_MONITORIA,
	/** Sequência utlizada em projetos de extensão. */
	SEQUENCIA_PROJETO_EXTENSAO,
	/** Sequência utlizada em disciplinas de lato sensu. */
	SEQUENCIA_DISCIPLINA_LATO,
	/** Sequência utlizada em resumos CIC. */
	SEQUENCIA_RESUMO_CIC,
	/** Sequência utlizada em notificação de invenções. */
	SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO;

}