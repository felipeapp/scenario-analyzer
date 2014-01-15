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

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.jsf.AbstractControllerMenu;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;

/**
 * Controlador para as operações do menu do Docente.
 *
 * @author Gleydson
 *
 */
public class MenuDocenteMBean extends AbstractControllerMenu {

	/**
	 * Define a ação do menu
	 * <br/><br/>
	 * Método não chamado por JSP.
	 * @param evt
	 */
	public void acaoMenu(ActionEvent evt) {
		System.out.println(evt.getSource());
	}

	/**
	 * Consulta disciplinas de nível de Ensino técnico.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaDisciplinaTec(ActionEvent evt) throws DAOException {

		GenericDAO dao = getGenericDAO();
		dao.lock(getServidorUsuario());

		if (getTipoUnidade() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			getCurrentSession().setAttribute("nivel", NivelEnsino.TECNICO);
			redirect("/sigaa/ensino/cadastroDisciplina.do?dispatch=list&page=0");
		}

	}

	/**
	 * Consultar turma.
	 * <br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/graduacao/menu_coordenador.jsp</li>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * <li>/sigaa.war/portais/menus/menu_lato_coordenador.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaTurma(ActionEvent evt) throws DAOException {
		GenericDAO dao = getGenericDAO();

		if (getServidorUsuario() != null) {
			dao.lock(getServidorUsuario());
		}

		BuscaTurmaMBean turma = (BuscaTurmaMBean) getMBean("buscaTurmaBean");
		turma.popularBuscaGeral();
	}

	/**
	 * Consulta de estrutura curricular de nível técnico.<br />
	 * Chamado pelas JSPs:
	 * <ul>
	 * <li>/sigaa.war/portais/docente/menu_docente.jsp</li>
	 * </ul>
	 * 
	 * @param evt
	 * @throws DAOException
	 * @throws IOException
	 */
	public void consultaEstruturaTec(ActionEvent evt) throws DAOException {
		//GenericDAO dao = getGenericDAO();
		//dao.lock(getServidorUsuario());

		//if (getTipoUnidade() == TipoUnidadeAcademica.ESCOLA || getTipoUnidade() == TipoUnidadeAcademica.UNID_ACADEMICA_ESPECIALIZADA) {
			//getCurrentSession().setAttribute("nivel", NivelEnsino.TECNICO);
			redirect("/sigaa/ensino/tecnico/estruturaCurricular/wizard.do?dispatch=list");
		//}
	}

}
