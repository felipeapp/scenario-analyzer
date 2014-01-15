/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on '11/05/2007'
 *
 */
package br.ufrn.sigaa.pessoa.dominio;

/**
 * Esta interface deve ser implementada por todas as entidades que puderem
 * de alguma forma serem tratadas como docente (como Servidor, DocenteExterno)
 * @author leonardo
 *
 */
public interface Docente {

	public int getId();
	
	public String getIdentificacao();
	
	public String getNome();
}
