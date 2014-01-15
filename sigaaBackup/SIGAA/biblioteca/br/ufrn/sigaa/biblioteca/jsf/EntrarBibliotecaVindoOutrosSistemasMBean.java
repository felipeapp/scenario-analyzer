/*
 * EntrarBibliotecaVindoOutrosSistemasMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 26/01/2010
 */
package br.ufrn.sigaa.biblioteca.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;

/**
 *
 * MBean respons�vel por carregar o subsistema da biblioteca quando a entrada no subsistema 
 * vem dos outros sistemas sem passar pelo menu principal do mesmo.
 *
 * @author jadson
 * @since 26/01/2010
 * @version 1.0 criacao da classe
 *
 */
@Component("entrarBibliotecaVindoOutrosSistemasMBean")
@Scope("request")
public class EntrarBibliotecaVindoOutrosSistemasMBean extends SigaaAbstractController <Object>{
	
	/**
	 * <p>M�todo que realiza a a��o de entra no subsistema da biblioteca sem ser pelo menu principal.</p>
	 * <p><i>(Geralmente o usu�rio vem redirecionado de outro sistema )</i></p>
	 * <br><br>
	 * M�todo chamado pela seguinte JSP: /biblioteca/entrarSubSistemaBibliotecaVindoOutrosSistemas.jsp
	 * 
	 * @return um redirect para a p�gina principal da biblioteca
	 */
	public String getEntrarSubSistemaBiblioteca(){
		
		setSubSistemaAtual(SigaaSubsistemas.BIBLIOTECA);
		
		/* *****************************************************************************************
		 *  Indica que o usu�rio n�o entrou no sistema pela menu do m�dulo, nesse caso, deve       *
		 *  habilitar o menu "modulo_servidor" como padr�o.                                        *
		 * *****************************************************************************************/
		getCurrentSession().setAttribute("naoEntrandoPeloMenuBiblioteca", true);
		
		return redirect("/biblioteca/index.jsf");
	}
	

	
}
