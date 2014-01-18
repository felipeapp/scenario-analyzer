/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 15/08/2013
 *
 */
package br.ufrn.sigaa.ensino.stricto.dominio;

/**
 * Poss�veis situa��es que as respostas da Auto Avali��o do Stricto Sensu pode
 * ter.
 * 
 * @author �dipo Elder F. de Melo
 * 
 */
public enum SituacaoRespostasAutoAvaliacao {
	
	/** Indica que as respostas est�o salvas, isto �, o coordenador ainda est� respondendo o formul�rio. */
	SALVO,
	/** Indica que as respostas foram submetidas para a PPPg apreciar. */
	SUBMETIDO,
	/** Indica que as respostas foram retornadas ao coordenador para adequa��o. */
	RETORNADO,
	/** Indica que as respostas foram aceitas e que n�o poder�o se mais alteradas. */
	ACEITO,
	/** Indica que as respostas foram rejeitadas e que n�o poder�o se mais alteradas. */
	REJEITADO;

	/** Retorna um texto para exibir ao usu�rio descrevendo o que significa cada situa��o
	 * @return
	 */
	public String getAjuda() {
		switch(this) {
			case SALVO : return "A Auto Avalia��o est� sendo preenchida pelo Coordenador";
			case SUBMETIDO : return "A Auto Avalia��o aguarda a an�lise pelo Comiss�o";
			case RETORNADO : return "Retorna a Auto Avalia��o para o Coordenador fazer ajustes";
			case ACEITO : return "As informa��es foram aceitas.";
			case REJEITADO : return "As informa��es foram rejeitadas";
			default : return "";
		}
	}
	
	/** Indica se o coordenador poder� alterar a Auto Avalia��o.
	 * @return
	 */
	public boolean isCoordenadorPodeAlterar() {
		return this == SALVO || this == RETORNADO || this == REJEITADO;
	}
}
