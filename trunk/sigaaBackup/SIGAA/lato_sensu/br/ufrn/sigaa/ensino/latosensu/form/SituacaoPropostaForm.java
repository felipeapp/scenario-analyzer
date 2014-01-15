/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '04/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.latosensu.dominio.SituacaoProposta;

/**
 * Form Bean responsável pelas operações que envolvem a manipulação da Situação da Proposta de Curso Lato Sensu.
 * 
 * @author Eric Moura
 *
 */
public class SituacaoPropostaForm extends SigaaForm<SituacaoProposta> {
	
	/** Tipo de busca a ser realizada. */
	private int tipoBusca;

	public SituacaoPropostaForm() {
		obj = new SituacaoProposta();
		registerSearchData("tipoBusca", "obj.descricao");
	}

	@Override
	public Collection<SituacaoProposta> customSearch(HttpServletRequest req) throws DAOException {
		final String DESCRICAO = "1";
		final String TODOS = "2";
		GenericDAO dao = getGenericDAO(req);
		Collection<SituacaoProposta> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			//Especificar as buscas
			if (DESCRICAO.equals(tipoBusca))
				lista = dao.findByLikeField(SituacaoProposta.class, "descricao", getSearchItem(req, "obj.descricao"), getPaging(req));
			else if (TODOS.equals(tipoBusca))
				lista = dao.findAll(SituacaoProposta.class);
		} finally {
			dao.close();
		}

		return lista;
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		super.validate(req);
		validateRequired(obj.getDescricao(), "Descrição", req);
	}

	/**
	 * @return the tipoBusca
	 */
	public int getTipoBusca() {
		return tipoBusca;
	}

	/**
	 * @param tipoBusca the tipoBusca to set
	 */
	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

}
