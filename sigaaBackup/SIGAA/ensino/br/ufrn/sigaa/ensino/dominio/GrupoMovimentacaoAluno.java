/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 28/05/2007
 *
 */
package br.ufrn.sigaa.ensino.dominio;

/**
 * Classe que agrega os vários tipos de movimentação que o aluno
 * tem.
 *
 * @author Gleydson Lima
 *
 */
public class GrupoMovimentacaoAluno {

	/** Afastamentos temporários, trancamento de curso, por exemplo */
	public static final String AFASTAMENTO_TEMPORARIO = "AT";

	/** Jubilamento, conclusão de curso, etc. */
	public static final String AFASTAMENTO_PERMANENTE = "AP";

	/** Prorrogações de prazos para conclusão de curso */
	public static final String PRORROGACAO_PRAZO = "PR";
	
	/** Antecipações de prazos para conclusão de curso */
	public static final String ANTECIPACAO_PRAZO = "AN";

	/** Entradas no curso */
	public static final String ENTRADA = "EN";

}
