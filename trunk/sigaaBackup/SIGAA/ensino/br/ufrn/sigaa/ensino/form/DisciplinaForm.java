/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 31/10/2006
 *
 */
package br.ufrn.sigaa.ensino.form;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.comum.dominio.SubSistema;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.DisciplinaDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.tecnico.dominio.CursoTecnico;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe ComponenteCurricular.
 * 
 * @author Gleydson Lima
 */
public class DisciplinaForm extends SigaaForm<ComponenteCurricular> {

	private int tipoBusca;

	private Curso curso;

	private ArrayList<Integer> maxAvaliacoes;

	public DisciplinaForm() {
		this.obj = new ComponenteCurricular();
		this.curso = new Curso();
		registerSearchData("tipoBusca", "obj.nome", "obj.codigo", "curso.id");
	}

	@Override
	public Collection<ComponenteCurricular> customSearch(HttpServletRequest req) throws ArqException {

		DisciplinaDao dao = getDAO(DisciplinaDao.class, req);
		Collection<ComponenteCurricular> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");

			char tEnsino = getNivelEnsino(req);

			if ("1".equals(tipoBusca)) {
				ComponenteCurricular d = dao.findByCodigo(getSearchItem(req,
						"obj.codigo"), getUnidadeGestora(req), 0, tEnsino, true);
				lista = new ArrayList<ComponenteCurricular>();
				if (d != null) {
					lista.add(d);
				}
			} else if ("2".equals(tipoBusca)) {
				lista = dao.findByNome(getSearchItem(req, "obj.nome"),
						getUnidadeGestora(req), 0, tEnsino, true, getPaging(req), false,false,null);
			} else if ("3".equals(tipoBusca)) {
				Object obj = dao.findByPrimaryKey(new Integer(getSearchItem(
						req, "curso.id")), Curso.class);
				if (obj != null) {
					Curso curso = (Curso) obj;
					lista = curso.getDisciplinas();
				}
			} else if ("4".equals(tipoBusca)) {
				lista = dao.findByNome("",
						getUnidadeGestora(req), 0, tEnsino, true, getPaging(req), false, false,TipoComponenteCurricular.DISCIPLINA);
			}
		} catch (LimiteResultadosException e) {
			addMensagemErro(e.getMessage(), req);
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return lista;
	}


	@SuppressWarnings("unchecked")
	@Override
	public void referenceData(HttpServletRequest req) throws DAOException {
		SubSistema subSistema = getSubSistemaCorrente(req);
		Class subClasse = Curso.class;
		if (subSistema.equals(SigaaSubsistemas.TECNICO)) {
			subClasse = CursoTecnico.class;
		} else if (subSistema.equals(SigaaSubsistemas.LATO_SENSU) || subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO)) {
			subClasse = CursoLato.class;
		}
		CursoDao dao = new CursoDao();
		try {
			if (!subSistema.equals(SigaaSubsistemas.LATO_SENSU) && !subSistema.equals(SigaaSubsistemas.PORTAL_COORDENADOR_LATO))
				mapa.put("cursos", dao.findAll(getUnidadeGestora(req),
						getNivelEnsino(req), subClasse, null));
			else {
				Usuario user = (Usuario) getUsuarioLogado(req);
				if (!user.isCoordenadorLato())
					mapa.put("cursos", dao.findAll(0, getNivelEnsino(req),
							subClasse, null));
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
	}

	/**
	 * @return the tipoBusca
	 */
	public int getTipoBusca() {
		return tipoBusca;
	}

	/**
	 * @param tipoBusca
	 *            the tipoBusca to set
	 */
	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}

	public ArrayList<Integer> getMaxAvaliacoes() {
		return maxAvaliacoes;
	}

	public void setMaxAvaliacoes(ArrayList<Integer> maxAvaliacoes) {
		this.maxAvaliacoes = maxAvaliacoes;
	}


}
