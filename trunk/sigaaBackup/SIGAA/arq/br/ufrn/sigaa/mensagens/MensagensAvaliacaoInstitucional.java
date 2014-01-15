/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on 24/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** 
 * Interface para as constantes de mensagens do Módulo de Avaliação Institucional que serão exibidas ao usuário.
 * 
 * @author Édipo Elder F. Melo
 *
 */
public interface MensagensAvaliacaoInstitucional {

	/** Prefixo para estas mensagens. Valor atual: "2_11500_". */
	public static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + "_";
	
	/**
	 * Mensagem exibida quando a avaliação institucional não está disponível para docentes no ano-período atual.
	 *  
	 * Conteúdo: A avaliação institucional não está disponível para docentes neste ano-período.
	 * Tipo: ERROR
	 */
	public static final String INDISPONIVEL_NO_ANO_PERIODO = prefix + "001";
	
	/**
	 * Mensagem exibida quando não é possível realizar a avaliação.
	 *  
	 * Conteúdo: Não é possível realizar a avaliação porque %s.
	 * Parâmetro: motivo da indisponibilidade.
	 * Tipo: ERROR
	 */
	public static final String AVALIACAO_INDISPONIVEL = prefix + "002";
	
	/**
	 * Mensagem exibida quando a avaliação institucional foi realizada em definitivo.
	 *  
	 * Conteúdo: Avaliação Institucional já realizada.
	 * Tipo: ERROR
	 */
	public static final String AVALIACAO_REALIZADA = prefix + "003";
	
	/**
	 * Mensagem exibida quando não há avaliação no ano-período anterior.
	 *  
	 * Conteúdo: Não há avaliação no ano-período anterior.
	 * Tipo: ERROR
	 */
	public static final String SEM_AVALIACAO_NO_PERIODO_ANTERIOR = prefix + "004";
	
	/**
	 * Mensagem exibida quando não foi definido o período para preenchimento da avaliação institucional.
	 *  
	 * Conteúdo: O período da Avaliação Institucional não está definido.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_AVALIACAO_NAO_DEFINIDO = prefix + "005";
	
	/**
	 * Mensagem exibida quando não é possível acessar a Avaliação Institucional diretamente. 
	 *  
	 * Conteúdo: Não é possível acessar a Avaliação Institucional diretamente. %s
	 * Parâmetro: aviso adicional. Ex.: "Por favor, utilize os links do Portal Discente."
	 * Tipo: ERROR
	 */
	public static final String ACESSO_INDIRETO_AVALIACAO  = prefix + "006";
	
	/**
	 * Mensagem exibida informando o período de preenchimento da Avaliação Institucional.
	 *  
	 * Conteúdo: O período da Avaliação Institucional é de %1$td/%1$tm/%1$tY até %2$td/%2$tm/%2$tY
	 * Parâmetros: dataInicio, dataFim
	 * Tipo: ERROR
	 */
	public static final String PERIODO_AVALIACAO_INSTITUCIONAL = prefix + "007";
	
	/**
	 * Mensagem exibida informando quais perguntas não foram respondidas.
	 *  
	 * Conteúdo: As perguntas em vermelho não foram respondidas. É necessário responder todas as perguntas para finalizar a avaliação.
	 * Tipo: ERROR
	 */
	public static final String PERGUNTAS_NAO_RESPONDIDAS = prefix + "008";

	/**
	 * Mensagem exibida quando o processamento das notas na avaliação está sendo executado e o usuário solicita um novo processamento.
	 *  
	 * Conteúdo: Há um processamento em andamento. Por favor, não utilize o botão "voltar" do seu navegador.
	 * Tipo: ERROR
	 */
	public static final String PROCESSAMENTO_EM_ANDAMENTO = prefix + "009";

	/**
	 * Mensagem exibida quando ocorre um erro no processamento das notas na avaliação.
	 *  
	 * Conteúdo: Ocorreu um erro no processamento da avaliação institucional.
	 * Parâmetro: erro ocorrido. 
	 * Tipo: ERROR
	 */
	public static final String OCORREU_ERRO_PROCESSAMENTO = prefix + "010";
	
	/**
	 * Mensagem exibida quando o processador inicia a persistência dos dados calculados da avaliação institucional.
	 *  
	 * Conteúdo: Gravando resultado...
	 * Tipo: ERROR
	 */
	public static final String PERSISTINDO_RESULTADO = prefix + "011";

	/**
	 * Mensagem exibida quando o usuário informa um ano-período maior que o ano/período atual.
	 *  
	 * Conteúdo: Informe um ano/período menor ou igual ao atual.
	 * Tipo: ERROR
	 */
	public static final String ANO_PERIODO_ALEM_DO_ATUAL = prefix + "012";
	
	/**
	 * Mensagem exibida quando o usuário informa um período de férias (3 ou 4).
	 *  
	 * Conteúdo: Informe um período regular.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_NAO_REGULAR = prefix + "013";
}

