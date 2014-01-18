/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 15/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

/**
 * Possíveis situações que as respostas da Auto Avalição do Stricto Sensu pode
 * ter.
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public enum SituacaoRespostasAutoAvaliacao {
	
	/** Indica que as respostas estão salvas, isto é, o coordenador ainda está respondendo o formulário. */
	SALVO,
	/** Indica que as respostas foram submetidas para a PPPg apreciar. */
	SUBMETIDO,
	/** Indica que as respostas foram retornadas ao coordenador para adequação. */
	RETORNADO,
	/** Indica que as respostas foram aceitas e que não poderão se mais alteradas. */
	ACEITO,
	/** Indica que as respostas foram rejeitadas e que não poderão se mais alteradas. */
	REJEITADO;

	/** Retorna um texto para exibir ao usuário descrevendo o que significa cada situação
	 * @return
	 */
	public String getAjuda() {
		switch(this) {
			case SALVO : return "A Auto Avaliação está sendo preenchida pelo Coordenador";
			case SUBMETIDO : return "A Auto Avaliação aguarda a análise pelo Comissão";
			case RETORNADO : return "Retorna a Auto Avaliação para o Coordenador fazer ajustes";
			case ACEITO : return "As informações foram aceitas.";
			case REJEITADO : return "As informações foram rejeitadas";
			default : return "";
		}
	}
	
	/** Indica se o coordenador poderá alterar a Auto Avaliação.
	 * @return
	 */
	public boolean isCoordenadorPodeAlterar() {
		return this == SALVO || this == RETORNADO || this == REJEITADO;
	}
}
