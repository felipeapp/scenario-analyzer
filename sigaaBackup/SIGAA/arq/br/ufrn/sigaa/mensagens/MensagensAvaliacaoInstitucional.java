/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 24/11/2009
 *
 */
package br.ufrn.sigaa.mensagens;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.Sistema;

/** 
 * Interface para as constantes de mensagens do M�dulo de Avalia��o Institucional que ser�o exibidas ao usu�rio.
 * 
 * @author �dipo Elder F. Melo
 *
 */
public interface MensagensAvaliacaoInstitucional {

	/** Prefixo para estas mensagens. Valor atual: "2_11500_". */
	public static final String prefix = Sistema.SIGAA + "_" + SigaaSubsistemas.PORTAL_AVALIACAO_INSTITUCIONAL.getId() + "_";
	
	/**
	 * Mensagem exibida quando a avalia��o institucional n�o est� dispon�vel para docentes no ano-per�odo atual.
	 *  
	 * Conte�do: A avalia��o institucional n�o est� dispon�vel para docentes neste ano-per�odo.
	 * Tipo: ERROR
	 */
	public static final String INDISPONIVEL_NO_ANO_PERIODO = prefix + "001";
	
	/**
	 * Mensagem exibida quando n�o � poss�vel realizar a avalia��o.
	 *  
	 * Conte�do: N�o � poss�vel realizar a avalia��o porque %s.
	 * Par�metro: motivo da indisponibilidade.
	 * Tipo: ERROR
	 */
	public static final String AVALIACAO_INDISPONIVEL = prefix + "002";
	
	/**
	 * Mensagem exibida quando a avalia��o institucional foi realizada em definitivo.
	 *  
	 * Conte�do: Avalia��o Institucional j� realizada.
	 * Tipo: ERROR
	 */
	public static final String AVALIACAO_REALIZADA = prefix + "003";
	
	/**
	 * Mensagem exibida quando n�o h� avalia��o no ano-per�odo anterior.
	 *  
	 * Conte�do: N�o h� avalia��o no ano-per�odo anterior.
	 * Tipo: ERROR
	 */
	public static final String SEM_AVALIACAO_NO_PERIODO_ANTERIOR = prefix + "004";
	
	/**
	 * Mensagem exibida quando n�o foi definido o per�odo para preenchimento da avalia��o institucional.
	 *  
	 * Conte�do: O per�odo da Avalia��o Institucional n�o est� definido.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_AVALIACAO_NAO_DEFINIDO = prefix + "005";
	
	/**
	 * Mensagem exibida quando n�o � poss�vel acessar a Avalia��o Institucional diretamente. 
	 *  
	 * Conte�do: N�o � poss�vel acessar a Avalia��o Institucional diretamente. %s
	 * Par�metro: aviso adicional. Ex.: "Por favor, utilize os links do Portal Discente."
	 * Tipo: ERROR
	 */
	public static final String ACESSO_INDIRETO_AVALIACAO  = prefix + "006";
	
	/**
	 * Mensagem exibida informando o per�odo de preenchimento da Avalia��o Institucional.
	 *  
	 * Conte�do: O per�odo da Avalia��o Institucional � de %1$td/%1$tm/%1$tY at� %2$td/%2$tm/%2$tY
	 * Par�metros: dataInicio, dataFim
	 * Tipo: ERROR
	 */
	public static final String PERIODO_AVALIACAO_INSTITUCIONAL = prefix + "007";
	
	/**
	 * Mensagem exibida informando quais perguntas n�o foram respondidas.
	 *  
	 * Conte�do: As perguntas em vermelho n�o foram respondidas. � necess�rio responder todas as perguntas para finalizar a avalia��o.
	 * Tipo: ERROR
	 */
	public static final String PERGUNTAS_NAO_RESPONDIDAS = prefix + "008";

	/**
	 * Mensagem exibida quando o processamento das notas na avalia��o est� sendo executado e o usu�rio solicita um novo processamento.
	 *  
	 * Conte�do: H� um processamento em andamento. Por favor, n�o utilize o bot�o "voltar" do seu navegador.
	 * Tipo: ERROR
	 */
	public static final String PROCESSAMENTO_EM_ANDAMENTO = prefix + "009";

	/**
	 * Mensagem exibida quando ocorre um erro no processamento das notas na avalia��o.
	 *  
	 * Conte�do: Ocorreu um erro no processamento da avalia��o institucional.
	 * Par�metro: erro ocorrido. 
	 * Tipo: ERROR
	 */
	public static final String OCORREU_ERRO_PROCESSAMENTO = prefix + "010";
	
	/**
	 * Mensagem exibida quando o processador inicia a persist�ncia dos dados calculados da avalia��o institucional.
	 *  
	 * Conte�do: Gravando resultado...
	 * Tipo: ERROR
	 */
	public static final String PERSISTINDO_RESULTADO = prefix + "011";

	/**
	 * Mensagem exibida quando o usu�rio informa um ano-per�odo maior que o ano/per�odo atual.
	 *  
	 * Conte�do: Informe um ano/per�odo menor ou igual ao atual.
	 * Tipo: ERROR
	 */
	public static final String ANO_PERIODO_ALEM_DO_ATUAL = prefix + "012";
	
	/**
	 * Mensagem exibida quando o usu�rio informa um per�odo de f�rias (3 ou 4).
	 *  
	 * Conte�do: Informe um per�odo regular.
	 * Tipo: ERROR
	 */
	public static final String PERIODO_NAO_REGULAR = prefix + "013";
}

