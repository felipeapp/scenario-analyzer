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

import org.apache.struts.action.ActionMapping;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe CoordenacaoCurso.
 * 
 * @author Eric Moura
 * @author Leonardo
 */
public class CoordenacaoCursoForm extends SigaaForm<CoordenacaoCurso> {

	/** Constantes de Tipos de Buscas */
	private static final String BUSCAR_POR_SERVIDOR = "1";
	private static final String BUSCAR_POR_CURSO = "2";

	/** Constante para a realização das consultas */
	private int tipoBusca;

	/** Data inicial do mandato do coordenador do curso */
	private String dataInicioMandato;

	/** Data final do mandato do coordenador do curso */
	private String dataFimMandato;

	public CoordenacaoCursoForm(){
		obj = new CoordenacaoCurso();

		registerSearchData("tipoBusca", "obj.servidor.id", "obj.curso.id");
	}

	@Override
	public Collection<CoordenacaoCurso> customSearch(HttpServletRequest req) throws DAOException {

		CoordenacaoCursoDao dao = getDAO(CoordenacaoCursoDao.class, req);
		Collection<CoordenacaoCurso> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");

			if (BUSCAR_POR_SERVIDOR.equals(tipoBusca))  {
				int docenteId = 0;
				try{
					docenteId = Integer.parseInt(getSearchItem(req, "obj.servidor.id"));
				}catch (Exception e) {}
				lista = dao.findByServidor(docenteId, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
			} else if (BUSCAR_POR_CURSO.equals(tipoBusca))  {
				int cursoId = 0;
				try{
					cursoId = Integer.parseInt(getSearchItem(req, "obj.curso.id"));
				}catch (Exception e) {}
				lista = dao.findByCurso(cursoId, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
			}

		} catch (ArqException e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return lista;
	}

	@Override
	public void reset(ActionMapping arg0, HttpServletRequest arg1) {
		obj = new CoordenacaoCurso();
		setDefaultProps();
		super.reset(arg0, arg1);
	}

	@Override
	public void setDefaultProps() {
		this.dataFimMandato = "";
		this.dataInicioMandato = "";
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		addAll("cargos", CargoAcademico.class);
		CursoDao dao = new CursoDao();
		try {
			mapa.put("cursos", dao.findAll(getUnidadeGestora(req), getNivelEnsino(req), Curso.class, null));
		} finally {
			dao.close();
		}
	}

	public String getDataFimMandato() {
		CoordenacaoCurso c = obj;
		if ( c.getDataFimMandato() != null ){
			dataFimMandato = Formatador.getInstance().formatarData( c.getDataFimMandato() );
		}
		return dataFimMandato;
	}

	public void setDataFimMandato(String dataFimMandato) {
		CoordenacaoCurso c = obj;
		c.setDataFimMandato( parseDate( dataFimMandato ) );
		this.dataFimMandato = dataFimMandato;
	}

	public String getDataInicioMandato() {
		CoordenacaoCurso c = obj;
		if ( c.getDataInicioMandato() != null ){
			dataInicioMandato = Formatador.getInstance().formatarData( c.getDataInicioMandato() );
		}
		return dataInicioMandato;
	}

	public void setDataInicioMandato(String dataInicioMandato) {
		CoordenacaoCurso c = obj;
		c.setDataInicioMandato( parseDate( dataInicioMandato ) );
		this.dataInicioMandato = dataInicioMandato;
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		ListaMensagens erros = obj.validate();

		ValidatorUtil.validateRequired(getDataInicioMandato(), "Ínicio de Mandato", erros);
		ValidatorUtil.validateRequired(getDataFimMandato(), "Término de Mandato", erros);
		ValidatorUtil.validaData(getDataInicioMandato(), "Ínicio de Mandato", erros);
		ValidatorUtil.validaData(getDataFimMandato(), "Término de Mandato", erros);
		ValidatorUtil.validateMinValue(getDataFimMandato(), getDataInicioMandato(), "Término de Mandato", erros);

		if (erros != null) {
			for (MensagemAviso erro : erros.getMensagens()) {
				addMensagem(erro, req);
			}
		}
	}
}
