/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
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
 * <p>MBean que permite aos usuários da biblioteca visualizarem as políticas de empréstimos 
 *  ( os prazos e quantidades de materiais que podem levar emprestado ) 
 * </p>
 * 
 * <p><i> <strong>Observação:</strong> Esse MBean extende PoliticaEmprestimoMBean porque a maioria dos métodos é igual, porém 
 * aqui o usuário vai ter a opção apenas de visulizá-las.</i></p>
 * 
 * @author jadson
 * @see PoliticaEmprestimoMBean
 */
@Component("visualizarPoliticasDeEmprestimoMBean")
@Scope("request")
public class VisualizarPoliticasDeEmprestimoMBean extends PoliticaEmprestimoMBean {

	/**  Página na qual os usuários do sistema irão visualizar as informações das políticas de emprestimo do sistema. */
	public static final String PAGINA_MOSTRA_INFORMACOES_POLITICAS_EMPRESTIMOS = "/biblioteca/circulacao/paginaMostraInformacoesPoliticasEmprestimos.jsp";

	/** Informações sobre o usuário para sabe o vínculo que ele está utilizando no momento */
	private UsuarioBiblioteca usuarioBiblioteca;
	
	/**
	 *  <p>Inicia o caso de uso de verificar os vínculos do usuário utilizados pelos funcionários de circulação.. </p>
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
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
			// Não faz nada !  Mesmo se o Usuário não tiver conta da biblioteca ele pode visualizar a política. 
		}
		
		return telaMostraInformacoesPoliticasEmprestimos();
	}
	

	/**
	 * Retorna tela que exibe as informações sobre os políticas de empréstimos
	 * 
	 *    <br/>
	 *    <p>Método não chamado por nenhuma página jsp.</p>
	 * 
	 */
	private String telaMostraInformacoesPoliticasEmprestimos(){
		return forward(PAGINA_MOSTRA_INFORMACOES_POLITICAS_EMPRESTIMOS);
	}
	
	
	
	// sets e gets //
	
	public UsuarioBiblioteca getUsuarioBiblioteca(){return usuarioBiblioteca;}
	public void setUsuarioBiblioteca(UsuarioBiblioteca usuarioBiblioteca) {this.usuarioBiblioteca = usuarioBiblioteca;}

	
}
