/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 26/08/2013
 *
 */
package br.ufrn.sigaa.ensino_rede.jsf;

import java.util.Collection;
import java.util.LinkedList;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.ensino_rede.dominio.CursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

/**
 * Controller responsável pelo cadastro/alteração de dados de um curso em rede.
 * @author Édipo Elder F. de Melo
 *
 */
@Component @Scope("request")
public class DadosCursoRedeMBean extends EnsinoRedeAbstractController<DadosCursoRede> {

	private Collection<SelectItem> allProgramaRedeCombo;
	private Collection<SelectItem> cursosFromProgramaRedeCombo;
	private Collection<SelectItem> campusFromProgramaRedeCombo;
	
	public DadosCursoRedeMBean() {
	}
	
	public Collection<SelectItem> getAllProgramaRedeCombo() throws DAOException {
		if (allProgramaRedeCombo == null) {
			GenericDAO dao = getGenericDAO();
			Collection<ProgramaRede> lista = dao.findAll(ProgramaRede.class);
			allProgramaRedeCombo  = toSelectItems(lista, "id", "descricao");
		}
		System.out.println("allProgramaRedeCombo:" + allProgramaRedeCombo.size());
		return allProgramaRedeCombo;
	}
	
	public Collection<SelectItem> getCursosFromProgramaRedeCombo() throws DAOException {
		if (cursosFromProgramaRedeCombo == null) {
			cursosFromProgramaRedeCombo = new LinkedList<SelectItem>();
			if (obj != null) {
				GenericDAO dao = getGenericDAO();
				String fields[] = {"programa.id"};
				Object values[] = {obj.getProgramaRede().getId()};
				Collection<CursoAssociado> lista = dao.findByExactField(CursoAssociado.class, fields, values);
				cursosFromProgramaRedeCombo  = toSelectItems(lista, "id", "nome");
			}
		}
		System.out.println("cursosFromProgramaRedeCombo:" + cursosFromProgramaRedeCombo.size());
		return cursosFromProgramaRedeCombo;
	}
	
	public Collection<SelectItem> getCampusFromCursoProgramaRedeCombo() throws DAOException {
		if (campusFromProgramaRedeCombo == null) {
			campusFromProgramaRedeCombo = new LinkedList<SelectItem>();
			if (obj != null) {
				GenericDAO dao = getGenericDAO();
				String fields[] = {"programaRede.id", "curso.id"};
				Object values[] = {obj.getProgramaRede().getId(), obj.getCurso().getId()};
				Collection<DadosCursoRede> lista = dao.findByExactField(DadosCursoRede.class, fields, values);
				campusFromProgramaRedeCombo  = toSelectItems(lista, "id", "campus.instituicao.nome");
			}
		}
		System.out.println("campusFromProgramaRedeCombo:" + campusFromProgramaRedeCombo.size());
		return campusFromProgramaRedeCombo;
	}
}
