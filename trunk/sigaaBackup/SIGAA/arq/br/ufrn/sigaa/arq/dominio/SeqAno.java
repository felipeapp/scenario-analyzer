/*
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 * 
 * Created on 29/12/2004
 *
 */
package br.ufrn.sigaa.arq.dominio;


/**
 * Classe que ser� usada para obter intervalos sequencias com LOCK UPDATE no
 * banco de dados.
 *
 * Este Design foi realizado ao inv�s de usar uma sequence, pois a
 * transacionalidade do EJB garante que caso o tombamento n�o seja realizado,
 * essa atualiza��o seja retornada e a sequencia de tombamento.
 *
 * @author Gleydson Lima
 *
 */
public enum SeqAno{

	/** Sequ�ncia gen�rica. */
	SEQUENCIA_GENERICA,
	/** Sequ�ncia utlizada em projetos de pesquisa. */
	SEQUENCIA_CODIGO_PROJETO_PESQUISA,
	/** Sequ�ncia utlizada em projetos de monitoria. */
	SEQUENCIA_PROJETO_MONITORIA,
	/** Sequ�ncia utlizada em projetos de extens�o. */
	SEQUENCIA_PROJETO_EXTENSAO,
	/** Sequ�ncia utlizada em disciplinas de lato sensu. */
	SEQUENCIA_DISCIPLINA_LATO,
	/** Sequ�ncia utlizada em resumos CIC. */
	SEQUENCIA_RESUMO_CIC,
	/** Sequ�ncia utlizada em notifica��o de inven��es. */
	SEQUENCIA_CODIGO_NOTIFICACAO_INVENCAO;

}