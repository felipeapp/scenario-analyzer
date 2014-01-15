/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 30/10/2006
*/
package br.ufrn.sigaa.ensino.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.ensino.HorarioDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.Horario;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe Horario.
 * 
 * @author Eric
 */
@Deprecated
public class HorarioForm extends SigaaForm<Horario> {

	/** Constante do tipo de busca a ser realizada. */
	private int tipoBusca;

	/** Curso no qual está adicionando um horário */
	private CursoLato curso = new CursoLato();

	/** Inicio do horário */
	private String inicio;

	/** Término do horário */
	private String fim;

	public HorarioForm() {
		this.obj = new Horario();
		obj.setUnidade(new Unidade());
	}

	public String getFim() {
		return fim;
	}

	public void setFim(String fim) {
		this.fim = fim;
	}

	public String getInicio() {
		return inicio;
	}

	public void setInicio(String inicio) {
		this.inicio = inicio;
	}

	@Override
	public void setObj(Horario obj) {
		super.setObj(obj);
		this.inicio = Formatador.getInstance().formatarHora(obj.getInicio());
		this.fim = Formatador.getInstance().formatarHora(obj.getFim());
	}

	@Override
	public void setDefaultProps() {
		this.inicio = "";
		this.fim = "";
		this.obj = new Horario();
	}

	@Override
	public Collection<Horario> customSearch(HttpServletRequest req) throws DAOException {
		HorarioDao dao = new HorarioDao();
		Collection<Horario> lista = null;

		try {
			lista = dao.findAtivoByUnidade(getUnidadeGestora(req),getNivelEnsino(req));
		} catch (ArqException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return lista;
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		super.referenceData(req);
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}
}
