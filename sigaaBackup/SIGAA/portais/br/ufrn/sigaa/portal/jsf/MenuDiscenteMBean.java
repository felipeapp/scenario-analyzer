/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 20/11/2006 
 *
 */
package br.ufrn.sigaa.portal.jsf;

import java.io.IOException;

import javax.faces.event.ActionEvent;

import org.apache.myfaces.custom.navmenu.NavigationMenuItem;
import org.apache.myfaces.custom.navmenu.jscookmenu.HtmlCommandJSCookMenu;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Controlador para as operações do menu do discente
 *
 * @author Gleydson
 *
 */
public class MenuDiscenteMBean extends SigaaAbstractController<Discente> {

	/**
	 * Retorna o tipo de unidade acadêmica do usuário
	 * Método não invocado por JSP's.
	 * @return
	 */
	public int getTipoUnidade() {
		if (getUsuarioLogado().getDiscenteAtivo() != null) {
			Integer tipo = getUsuarioLogado().getDiscenteAtivo().getGestoraAcademica().getTipoAcademica();
			return (tipo == null ? 0 : tipo);
		}
		
		return 0;
	}

	/**
	 * Redirecionar no menu de acordo com o valor passado no menu
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 *   </ul> 
	 * @param evt
	 */
	public void redirecionar(ActionEvent evt) {

		System.out.println(evt.getSource());
		HtmlCommandJSCookMenu itemMenu = (HtmlCommandJSCookMenu) evt
				.getSource();
		if (getTipoUnidade() == TipoUnidadeAcademica.ESCOLA) {
			getCurrentSession().setAttribute("nivel", NivelEnsino.TECNICO);
		}
		redirect((String) itemMenu.getValue());

	}

	/**
	 * Verifica se a sessão atual é relacionada ao nível de ensino técnico, 
	 * onde o mesmo faz um redirecionamento para a página pertinente a este.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
  	 *    <li>/sigaa.war/portais/docente/menu_docente.jsp/li>
	 *   </ul> 
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaEstruturaTec(ActionEvent evt) throws DAOException,
			IOException {
		getCurrentSession().setAttribute("nivel", NivelEnsino.TECNICO);
		redirect("/sigaa/ensino/tecnico/estruturaCurricular/wizard.do?dispatch=list");
	}

	/**
	 * Retorna um objeto do tipo {@link NavigationMenuItem} para ser utilizado no menu discente.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public NavigationMenuItem getPaginaCurso() {
		NavigationMenuItem item = null;
		
		if ( getUsuarioLogado().getDiscenteAtivo().getCurso() != null ){
			int id = getUsuarioLogado().getDiscenteAtivo().getCurso().getId();
			
			//Se o browser for IE, utilizar como action o método #redirectPaginaCurso
			if(getCurrentRequest().getHeader("User-Agent").contains("MSIE"))
				item = new NavigationMenuItem("Página do Curso", "#{menuDiscente.redirectPaginaCurso}");
			else //Caso contrário, utilizar o endereço (o componente o força a abrir em uma nova janela)
				item = new NavigationMenuItem("Página do Curso", "http://www.sigaa.ufrn.br/sigaa/public/curso/portal.jsf?id="+id+"&lc=pt_BR");
		}
		return item;
	}
	
	/**
	 * Direciona o usuário para a página de seu curso.
	 * <br />
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>/sigaa.war/portais/discente/menu_discente.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String redirectPaginaCurso() {
		int id = getUsuarioLogado().getDiscenteAtivo().getCurso().getId();
		
		return redirectSemContexto("http://www.sigaa.ufrn.br/sigaa/public/curso/portal.jsf?id="+id+"&lc=pt_BR");
	}
}
