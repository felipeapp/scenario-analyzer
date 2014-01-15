/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Criado em: 02/02/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.circulacao.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.jsf.PoliticaEmprestimoMBean;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;

/**
 *
 * <p>MBean que permite aos usu�rios da biblioteca visualizarem as pol�ticas de empr�stimos 
 *  ( os prazos e quantidades de materiais que podem levar emprestado ) 
 * </p>
 * 
 * <p><i> <strong>Observa��o:</strong> Esse MBean extende PoliticaEmprestimoMBean porque a maioria dos m�todos � igual, por�m 
 * aqui o usu�rio vai ter a op��o apenas de visuliz�-las.</i></p>
 * 
 * @author jadson
 * @see PoliticaEmprestimoMBean
 */
@Component("visualizarPoliticasDeEmprestimoMBean")
@Scope("request")
public class VisualizarPoliticasDeEmprestimoMBean extends PoliticaEmprestimoMBean {

	/**  P�gina na qual os usu�rios do sistema ir�o visualizar as informa��es das pol�ticas de emprestimo do sistema. */
	public static final String PAGINA_MOSTRA_INFORMACOES_POLITICAS_EMPRESTIMOS = "/biblioteca/circulacao/paginaMostraInformacoesPoliticasEmprestimos.jsp";

	/** Informa��es sobre o usu�rio para sabe o v�nculo que ele est� utilizando no momento */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/**
	 *  <p>Inicia o caso de uso de verificar os v�nculos do usu�rio utilizados pelos funcion�rios de circula��o.. </p>
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *   	<li>/sigaa.war/biblioteca/modulo_biblioteca_servidor.jsp</li>
	 *   	<li>/sigaa.war/biblioteca/modulo_biblioteca_servidor.jsp</li>
	 *   	<li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   	<li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 *   </ul>
	 *
	 *
	 *
	 * @return
	 * @throws DAOException
	 * @throws SegurancaException
	 */
	public String iniciarVisualizacao() throws DAOException, SegurancaException{
		
		try{
			usuarioBiblioteca = UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(getUsuarioLogado().getPessoa().getId(), null);
		}catch (NegocioException ne) {
			// N�o faz nada !  Mesmo se o Usu�rio n�o tiver conta da biblioteca ele pode visualizar a pol�tica. 
		}
		
		return telaMostraInformacoesPoliticasEmprestimos();
	}
	

	/**
	 * Retorna tela que exibe as informa��es sobre os pol�ticas de empr�stimos
	 * 
	 *    <br/>
	 *    <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 * 
	 */
	private String telaMostraInformacoesPoliticasEmprestimos(){
		return forward(PAGINA_MOSTRA_INFORMACOES_POLITICAS_EMPRESTIMOS);
	}
	
	
	
	// sets e gets //
	
	public UsuarioBiblioteca getUsuarioBiblioteca(){return usuarioBiblioteca;}
	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {this.usuarioBiblioteca = usuarioBiblioteca;}

	
}
