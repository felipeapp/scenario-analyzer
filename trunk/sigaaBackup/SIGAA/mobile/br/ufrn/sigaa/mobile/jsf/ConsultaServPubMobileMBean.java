package br.ufrn.sigaa.mobile.jsf;

import java.util.List;
import java.util.Map;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularDao;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.LinhaListaCursos;

/**
 * MBean responsavel por realizar as consultas 
 * da página pública mobile 
 * 
 * @author agostinho
 *
 */

@Component("consultaServPubMobileMBean")
@Scope("request")
public class ConsultaServPubMobileMBean extends SigaaAbstractController {

	private String codigoDisciplina;
	private List<ComponenteCurricular> listaCC;
	private List<DocenteTurma> listaTurmas;
	private Integer ano, semestre;
	private char nivelEnsino;
	private Map<Unidade, LinhaListaCursos> listaCursosCentro;
	
	public ConsultaServPubMobileMBean() {
		ano = getAnoAtual();
		semestre = getPeriodoAtual();
	}

	public String iniciarBuscaDisciplinas() {
		String str = "/public/mobile/comp_curr.jsp";
		return forward(str);
	}
	
	public String iniciarBuscaTurmas() {
		String str = "/public/mobile/turma.jsp";
		return forward(str);
	
	}
	public String iniciarBuscaCursos() {
		String str = "/public/mobile/curso.jsp";
		return forward(str);
	}
	
	public String consultarTurmas() throws HibernateException, DAOException {
		if (ano != null && semestre != null && codigoDisciplina != null && !codigoDisciplina.equals(""))
			listaTurmas = getDAO(TurmaDao.class).findTurmasAtivasByCodigo((codigoDisciplina.toUpperCase()), ano, semestre);
		
		return null;
	}
	
	public String consultarComponentesCurric() throws DAOException {
		if (codigoDisciplina != null && !codigoDisciplina.equals(""))
			listaCC = getDAO(ComponenteCurricularDao.class).findByCodigo((codigoDisciplina.toUpperCase()));
		
		return null;
	}
	
	public String consultarCursos() throws DAOException {	
		listaCursosCentro = getDAO(CursoDao.class).findCursosCentro(nivelEnsino);
		return null;
	}
	
	public String getCodigoDisciplina() {
		return codigoDisciplina;
	}

	public void setCodigoDisciplina(String codigoDisciplina) {
		this.codigoDisciplina = codigoDisciplina;
	}

	public List<ComponenteCurricular> getListaCC() {
		return listaCC;
	}

	public void setListaCC(List<ComponenteCurricular> listaCC) {
		this.listaCC = listaCC;
	}

	public List<DocenteTurma> getListaTurmas() {
		return listaTurmas;
	}

	public void setListaTurmas(List<DocenteTurma> listaTurmas) {
		this.listaTurmas = listaTurmas;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getSemestre() {
		return semestre;
	}

	public void setSemestre(Integer semestre) {
		this.semestre = semestre;
	}
	
	public char getNivelEnsino() {
		return nivelEnsino;
	}

	public void setNivelEnsino(char nivelEnsino) {
		this.nivelEnsino = nivelEnsino;
	}

	public Map<Unidade, LinhaListaCursos> getListaCursosCentro() {
		return listaCursosCentro;
	}

	public void setListaCursosCentro(
			Map<Unidade, LinhaListaCursos> listaCursosCentro) {
		this.listaCursosCentro = listaCursosCentro;
	}

}
