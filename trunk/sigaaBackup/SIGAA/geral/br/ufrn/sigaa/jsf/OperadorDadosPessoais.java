/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '20/03/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Interface a ser implementada pelos Managed Beans que usar�o um formul�rio
 * de dados pessoais gen�rico
 * (id�ia id�ntica ao OperadorDiscente)
 * @author Andre M Dantas
 *
 */
public interface OperadorDadosPessoais {

	/**
	 * Atributo de sess�o utilizado para armazenar a opera��o de dados pessoais ativa
	 * afim de evitar problemas de inconsist�ncia devido a altera��o m�ltipla de dados pessoais.
	 */
	public static final String OPERACAO_ATIVA_SESSION = "operacaoDadosPessoaisAtiva";
	
	/**
	 * Define o objeto populado com os dados pessoais nos MBeans que implementares essa interface
	 * @param pessoa
	 */
	public void setDadosPessoais(Pessoa pessoa);

	/**
	 * M�todo invocado pelos MBeans ap�s a submiss�o dos dados
	 * @return
	 */
	public String submeterDadosPessoais();

}
