/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/01/2010
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;


/**
 * Interface para as constantes de mensagens do M�dulo de Monitoria que ser�o exibidas para o usu�rio.
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
	 * Mensagem exibida quando usu�rio submeter resumo Sid e o per�odo de envio ainda n�o foi definido pela PROGRAD.
	 *  
	 * Conte�do: Per�odo de envio de Resumos ainda n�o foi definido pela Pr�-Reitoria de Gradua��o!
	 * Tipo: ERROR
	 */
	public static final String PERIODO_ENVIO_RESUMOS_NAO_DEFINIDO = prefix + "01";
	
	/**
	 * Mensagem exibida quando usu�rio submeter resumo Sid fora do prazo definido pela PROGRAD
	 *  
	 * Conte�do: Atualmente, o recebimento de Resumos de Projetos para o Semin�rio de Inia��o � Doc�ncia est� autorizado somente para projetos submetidos em %s
	 * Tipo: ERROR
	 */
	public static final String SUBMISSAO_RESUMO_FORA_DO_PRAZO = prefix + "02";
	
	
	/**
	 * Mensagem exibida para informar o per�odo de recebimento dos resumos do sid de acordo com o ano
	 *  
	 * Conte�do: O per�odo de recebimento de Resumos dos Projetos de %s � de %s at� %s
	 * Tipo: ERROR
	 */
	public static final String PERIODO_SUBMISSAO_RESUMOS_DE_ACORDO_COM_O_ANO = prefix + "03";
	
	
	/**
	 * Mensagem exibida para informar o per�odo onde o coordenador pode alterar o resumo sid.
	 *  
	 * Conte�do: O prazo para Altera��o de Resumos dos Projetos de %s terminou em %s
	 * Tipo: ERROR
	 */
	public static final String PERIODO_ALTERAR_RESUMOS_DE_ACORDO_COM_O_ANO = prefix + "04";
	
	
	/**
	 * Mensagem exibida para informar que o discente n�o tem o direito de emitir certificado de participa��o do resumo Sid.
	 *  
	 * Conte�do: Discente n�o tem direito ao certificado porque n�o apresentou ou n�o participou da elabora��o do resumo do SID
	 * Tipo: ERROR
	 */
	public static final String DISCENTE_SEM_DIREITO_A_CERTIFICADO_DE_PARTICIPACAO_RESUMO_SID = prefix + "05";
	
	
	/**
	 * Mensagem exibida para que informar que n�o h� participantes com direito a certificado em um projeto
	 *  
	 * Conte�do: N�o h� participantes com direito a certificado neste projeto.
	 * Tipo: ERROR
	 */
	public static final String NAO_HA_PARTICIPANTES_COM_DIREITO_CERTIFICADO_RESUMI_SID = prefix + "06";
	
	
	/**
	 * Mensagem exibida para que informar que ja foi enviado o relat�rio a PROGRAD.
	 *  
	 * Conte�do: Relat�rio j� enviado para PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_JA_ENVIADO_PROGRAD = prefix + "07";
	
	
	/**
	 * Mensagem exibida para que informar que ja foi enviado o relat�rio a PROGRAD com sucesso.
	 *  
	 * Conte�do: Relat�rio enviado para PROGRAD com sucesso!
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_ENVIADO_PROGRAD_SUCESSO = prefix + "08";
	
	
	/**
	 * Mensagem exibida para informar que n�o � poss�vel alterar o coordenador de projeto
	 * de ensino associada. Isto pelo portal de monitoria. 
	 *  
	 * Conte�do: N�o � poss�vel alterar o coordenador deste projeto de ensino pois ele faz parte de um projeto associado.
	 * Tipo: ERROR
	 */
	public static final String ALTERAR_COORDENADOR_PROJETO_ENSINO_ASSOCIADO = prefix + "09";
	
	/**
	 * Mensagem exibida para informar que a data de saida do projeto
	 * � inv�lida pelo portal de monitoria. 
	 *  
	 * Conte�do: Data de sa�da n�o pode ser anterior a data de entrada no projeto.
	 * Tipo: ERROR
	 */
	public static final String REGRA_DATA_SAIDA_PROJETO = prefix + "10";
	
	
	
	/**
	 * Mensagem exibida para informar que o Relat�rio foi enviado com sucesso para valida��o da Coordena��o do Projeto. 
	 *  
	 * Conte�do: Relat�rio enviado com sucesso para valida��o da Coordena��o do Projeto.
	 * Tipo: INFORMATION
	 */
	public static final String RELATORIO_ENVIADO_COORDENACAO_PROJETO = prefix + "11";
	
	
	/**
	 * Mensagem exibida para informar que o projeto de monitoria n�o pode ser removido enquanto houverem discentes ativos. 
	 *  
	 * Conte�do: Este Projeto ainda possui monitores ativos. Finalize todos os monitores e tente remover o projeto novamente.
	 * Tipo: ERROR
	 */
	public static final String FINALIZAR_MONITORES_REMOVER_PROJETO = prefix + "12";

	
	/**
	 * Mensagem exibida para informar que ainda existem projetos de monitoria com avalia��es discrepantes para serem reavaliados. 
	 *  
	 * Conte�do: Ainda existem projetos de monitoria pendentes de avalia��o. Avalie os projetos com notas discrepantes e tente publicar o resultado novamente.
	 * Tipo: ERROR
	 */
	public static final String AVALIAR_PROJETOS_NOTAS_DISCREPANTES = prefix + "13";
	
	/**
	 * Mensagem exibida para informar quem tem permiss�o de finalizar monitores. 
	 *  
	 * Conte�do: Somente Coordenadores(a) de Projetos de Monitoria ou membro da PROGRAD podem finalizar monitores.
	 * Tipo: ERROR
	 */
	public static final String PERMISSAO_FINALIZAR_MONITOR = prefix + "14";
	
	
	/**
	 * Mensagem exibida para informar que o relat�rio de desligamento ainda n�o foi validado pela coordena��o do projeto ou pela PROGRAD. 
	 *  
	 * Conte�do: O Relat�rio de Desligamento enviado pelo discente ainda n�o foi validado pela Coordena��o do projeto ou pela PROGRAD!
	 * Tipo: ERROR
	 */
	public static final String RELATORIO_DESLIGAMENTO_NAO_VALIDADO = prefix + "15";
	
	
	/**
	 * Mensagem exibida esta pendente quanto ao envio de relat�rio parcial. 
	 *  
	 * Conte�do: Este Docente possui Projetos de Monitoria submetidos entre %s e %s pendentes de entrega dos Relat�rios Parciais. 
	 * Para regularizar esta situa��o, entre em contato com a PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String DOCENTE_PENDENTE_RELATORIO_PARCIAL = prefix + "16";
	
	/**
	 * Mensagem exibida esta pendente quanto ao envio de relat�rio final. 
	 *  
	 * Conte�do: Este Docente possui Projetos de Monitoria submetidos entre %s e %s pendentes de entrega dos Relat�rios Finais. 
	 * Para regularizar esta situa��o, entre em contato com a PROGRAD.
	 * Tipo: ERROR
	 */
	public static final String DOCENTE_PENDENTE_RELATORIO_FINAL = prefix + "17";

	
	/**
	 * Mensagem exibida quando um componente curricular sem turmas consolidadas ou abertas � adicionado a uma prova seletiva. 
	 *  
	 * Conte�do: N�o foram encontradas turmas abertas ou consolidadas para o componente curricular adicionado (%s). Por se 
	 * tratar de um componente curricular novo, n�o ser� considerado como Pr�-requisito para inscri��o de discentes nesta prova. 
	 * Para regularizar esta situa��o, entre em contato com a PROGRAD.
	 * Tipo: WARNING
	 */
	public static final String COMPONENTE_CURRICULAR_NOVO = prefix + "18";
	
}
