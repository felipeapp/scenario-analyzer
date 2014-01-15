/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;

/**
 * Classe responsável pela realização de consulta referente a área de conhecimento 
 * CNPQ
 * 
 * @author Gleydson
 */
public class AreaConhecimentoForm extends SigaaForm<AreaConhecimentoCnpq> {

	public static final int TODOS = 100;
	public static final int GRANDE_AREA = 1;
	public static final int NOME = 2;

	private int tipoBusca;

	public AreaConhecimentoForm() {
		obj = new AreaConhecimentoCnpq();
		obj.setGrandeArea(new AreaConhecimentoCnpq());

		registerSearchData("tipoBusca","obj.grandeArea.id", "obj.nome");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		GenericDAO dao = getGenericDAO(req);
		Collection<AreaConhecimentoCnpq> lista = null;

		String tipoBusca = getSearchItem(req, "tipoBusca");
		if ( tipoBusca == null ) {
			tipoBusca = "0";
		}

		switch( Integer.parseInt(tipoBusca) ) {
			case GRANDE_AREA:
				lista = dao.findByExactField(AreaConhecimentoCnpq.class, "grandeArea.id", getSearchItem(req, "obj.grandeArea.id"), "asc", "nome");
				break;
			case NOME:
				lista = dao.findByLikeField(AreaConhecimentoCnpq.class, "nome", getSearchItem(req, "obj.nome"), getPaging(req), "asc", "nome");
				break;
			default:
				lista = dao.findAll(AreaConhecimentoCnpq.class, getPaging(req), "grandeArea.id", "asc");
		}

		return lista;
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		AreaConhecimentoCnpqDao dao = getDAO(AreaConhecimentoCnpqDao.class,req);
		req.setAttribute("grandesAreas", dao.findGrandeAreasConhecimentoCnpq());
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

}
