/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;


/**
 * Interface para as constantes de mensagens do Módulo de Monitoria que serão exibidas para o usuário.
 * 
 * @author Igor Linnik 
 *
 */
public interface MensagensMonitoria {
	
	/**
	 * Prefixo para estas mensagens.
	 */
	static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.MONITORIA.getId() + "_";	
	
	
	/**
	 * Mensagem exibida quando usuário submeter resumo Sid e o período de envio ainda não foi definido pela PROGRAD.
	 *  
	 * Conteúdo: Período de envio de Resumos ainda não foi definido pela Pró-Reitoria de Graduação!
	 * Tipo: ERROR
	 */
	public static final String PERIODO_ENVIO_RESUMOS_NAO_DEFINIDO = prefix + "01";
	
	/**
	 * Mensagem exibida quando usuário submeter resumo Sid fora do prazo definido pela PROGRAD
	 *  
	 * Conteúdo: Atualmente, o recebimento de Resumos de Projetos para o Seminário de Iniação à Docência está autorizado somente para projetos submetidos em %s
	 * Tipo: ERROR
	 */
	public static final String SUBMISSAO_RESUMO_FORA_DO_PRAZO = prefix + "02";
	
	
	/**
	 * Mensagem exibida para informar o período de recebimento dos resumos do sid de acordo com o ano
	 *  
	 * Conteúdo: O período de recebimento de Resumos dos Projetos de %s é de %s até %s
	 * Tipo: ERROR
	 */
	public static final String PERIODO_SUBMISSAO_RESUMOS_DE_ACORDO_COM_O_ANO = prefix + "03";
	
	
	/**
	 * Mensagem exibida para informar o período onde o coordenador pode alterar o resumo sid.
	 *  
	 * Conteúdo: O prazo para Alteração de Resumos dos Projetos de %s terminou em %s
	 * Tipo: ERROR
	 */
	public static final String PERIODO_ALTERAR_RESUMOS_DE_ACORDO_COM_O_ANO = prefix + "04";
	
	
	/**
	 * Mensagem exibida para informar que o discente não tem o direito de emitir certificado de participação do resumo Sid.
	 *  
	 * Conteúdo: Discente não tem direito ao certificado porque não apresentou ou não participou da elaboração do resumo do SID
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_DIREITO_A_CERTIFICADO_DE_PARTICIPACAO_RESUMO_SID = prefix + "05";
	
	
	/**
	 * Mensagem exibida para que informar que não há participantes com direito a certificado em um projeto
	 *  
	 * Conteúdo: Não há participantes com direito a certificado neste projeto.
	 * Tipo: ERROR
	 */
	public static final String NAO_HA_PARTICIPANTES_COM_DIREITO_CERTIFICADO_RESUMI_SID = prefix + "06";
	
	
	/**
	 * Mensagem exibida para que informar que ja foi enviado o relatório a PROGRAD.
	 *  
	 * Conteúdo: Relatório já enviado para PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_JA_ENVIADO_PROGRAD = prefix + "07";
	
	
	/**
	 * Mensagem exibida para que informar que ja foi enviado o relatório a PROGRAD com sucesso.
	 *  
	 * Conteúdo: Relatório enviado para PROGRAD com sucesso!
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_ENVIADO_PROGRAD_SUCESSO = prefix + "08";
	
	
	/**
	 * Mensagem exibida para informar que não é possível alterar o coordenador de projeto
	 * de ensino associada. Isto pelo portal de monitoria. 
	 *  
	 * Conteúdo: Não é possível alterar o coordenador deste projeto de ensino pois ele faz parte de um projeto associado.
	 * Tipo: ERROR
	 */
	public static final String ALTERAR_COORDENADOR_PROJETO_ENSINO_ASSOCIADO = prefix + "09";
	
	/**
	 * Mensagem exibida para informar que a data de saida do projeto
	 * é inválida pelo portal de monitoria. 
	 *  
	 * Conteúdo: Data de saída não pode ser anterior a data de entrada no projeto.
	 * Tipo: ERROR
	 */
	public static final String REGRA_DATA_SAIDA_PROJETO = prefix + "10";
	
	
	
	/**
	 * Mensagem exibida para informar que o Relatório foi enviado com sucesso para validação da Coordenação do Projeto. 
	 *  
	 * Conteúdo: Relatório enviado com sucesso para validação da Coordenação do Projeto.
	 * Tipo: INFORMATION
	 */
	public static final String RELATORIO_ENVIADO_COORDENACAO_PROJETO = prefix + "11";
	
	
	/**
	 * Mensagem exibida para informar que o projeto de monitoria não pode ser removido enquanto houverem discentes ativos. 
	 *  
	 * Conteúdo: Este Projeto ainda possui monitores ativos. Finalize todos os monitores e tente remover o projeto novamente.
	 * Tipo: ERROR
	 */
	public static final String FINALIZAR_MONITORES_REMOVER_PROJETO = prefix + "12";

	
	/**
	 * Mensagem exibida para informar que ainda existem projetos de monitoria com avaliações discrepantes para serem reavaliados. 
	 *  
	 * Conteúdo: Ainda existem projetos de monitoria pendentes de avaliação. Avalie os projetos com notas discrepantes e tente publicar o resultado novamente.
	 * Tipo: ERROR
	 */
	public static final String AVALIAR_PROJETOS_NOTAS_DISCREPANTES = prefix + "13";
	
	/**
	 * Mensagem exibida para informar quem tem permissão de finalizar monitores. 
	 *  
	 * Conteúdo: Somente Coordenadores(a) de Projetos de Monitoria ou membro da PROGRAD podem finalizar monitores.
	 * Tipo: ERROR
	 */
	public static final String PERMISSAO_FINALIZAR_MONITOR = prefix + "14";
	
	
	/**
	 * Mensagem exibida para informar que o relatório de desligamento ainda não foi validado pela coordenação do projeto ou pela PROGRAD. 
	 *  
	 * Conteúdo: O Relatório de Desligamento enviado pelo discente ainda não foi validado pela Coordenação do projeto ou pela PROGRAD!
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_DESLIGAMENTO_NAO_VALIDADO = prefix + "15";
	
	
	/**
	 * Mensagem exibida esta pendente quanto ao envio de relatório parcial. 
	 *  
	 * Conteúdo: Este Docente possui Projetos de Monitoria submetidos entre %s e %s pendentes de entrega dos Relatórios Parciais. 
	 * Para regularizar esta situação, entre em contato com a PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String DOCENTE_PENDENTE_RELATORIO_PARCIAL = prefix + "16";
	
	/**
	 * Mensagem exibida esta pendente quanto ao envio de relatório final. 
	 *  
	 * Conteúdo: Este Docente possui Projetos de Monitoria submetidos entre %s e %s pendentes de entrega dos Relatórios Finais. 
	 * Para regularizar esta situação, entre em contato com a PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String DOCENTE_PENDENTE_RELATORIO_FINAL = prefix + "17";

	
	/**
	 * Mensagem exibida quando um componente curricular sem turmas consolidadas ou abertas é adicionado a uma prova seletiva. 
	 *  
	 * Conteúdo: Não foram encontradas turmas abertas ou consolidadas para o componente curricular adicionado (%s). Por se 
	 * tratar de um componente curricular novo, não será considerado como Pré-requisito para inscrição de discentes nesta prova. 
	 * Para regularizar esta situação, entre em contato com a PROGRAD.
	 * Tipo: WARNING
	 */
	public static final String COMPONENTE_CURRICULAR_NOVO = prefix + "18";
	
}
