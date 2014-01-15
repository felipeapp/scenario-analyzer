/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on 28/05/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Classe que agrega os v�rios tipos de movimenta��o que o aluno
 * tem.
 *
 * @author Gleydson Lima
 *
 */
public class GrupoMovimentacaoAluno {

	/** Afastamentos tempor�rios, trancamento de curso, por exemplo */
	public static final String AFASTAMENTO_TEMPORARIO = "AT";

	/** Jubilamento, conclus�o de curso, etc. */
	public static final String AFASTAMENTO_PERMANENTE = "AP";

	/** Prorroga��es de prazos para conclus�o de curso */
	public static final String PRORROGACAO_PRAZO = "PR";
	
	/** Antecipa��es de prazos para conclus�o de curso */
	public static final String ANTECIPACAO_PRAZO = "AN";

	/** Entradas no curso */
	public static final String ENTRADA = "EN";

}
