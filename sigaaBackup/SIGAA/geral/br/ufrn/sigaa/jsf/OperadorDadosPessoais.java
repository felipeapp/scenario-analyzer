/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '20/03/2007'
 *
 */
package br.ufrn.sigaa.jsf;

import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Interface a ser implementada pelos Managed Beans que usarão um formulário
 * de dados pessoais genérico
 * (idéia idêntica ao OperadorDiscente)
 * @author Andre M Dantas
 *
 */
public interface OperadorDadosPessoais {

	/**
	 * Atributo de sessão utilizado para armazenar a operação de dados pessoais ativa
	 * afim de evitar problemas de inconsistência devido a alteração múltipla de dados pessoais.
	 */
	public static final String OPERACAO_ATIVA_SESSION = "operacaoDadosPessoaisAtiva";
	
	/**
	 * Define o objeto populado com os dados pessoais nos MBeans que implementares essa interface
	 * @param pessoa
	 */
	public void setDadosPessoais(Pessoa pessoa);

	/**
	 * Método invocado pelos MBeans após a submissão dos dados
	 * @return
	 */
	public String submeterDadosPessoais();

}
