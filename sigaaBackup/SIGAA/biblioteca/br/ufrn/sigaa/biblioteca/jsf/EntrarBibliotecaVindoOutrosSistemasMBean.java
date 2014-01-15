/*
 * EntrarBibliotecaVindoOutrosSistemasMBean.java
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * MBean responsável por carregar o subsistema da biblioteca quando a entrada no subsistema 
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
	 * <p>Método que realiza a ação de entra no subsistema da biblioteca sem ser pelo menu principal.</p>
	 * <p><i>(Geralmente o usuário vem redirecionado de outro sistema )</i></p>
	 * <br><br>
	 * Método chamado pela seguinte JSP: /biblioteca/entrarSubSistemaBibliotecaVindoOutrosSistemas.jsp
	 * 
	 * @return um redirect para a página principal da biblioteca
	 */
	public String getEntrarSubSistemaBiblioteca(){
		
		setSubSistemaAtual(SigaaSubsistemas.BIBLIOTECA);
		
		/* *****************************************************************************************
		 *  Indica que o usuário não entrou no sistema pela menu do módulo, nesse caso, deve       *
		 *  habilitar o menu "modulo_servidor" como padrão.                                        *
		 * *****************************************************************************************/
		getCurrentSession().setAttribute("naoEntrandoPeloMenuBiblioteca", true);
		
		return redirect("/biblioteca/index.jsf");
	}
	

	
}
