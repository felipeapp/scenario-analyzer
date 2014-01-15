/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.latosensu.dominio.OutrosDocumentos;

/**
 * Form Bean responsável pelas operações que envolvem a manipulação de outros documentos dos cursos de Lato Sensu.
 * 
 * @author Eric Moura
 *
 */
public class OutrosDocumentosForm extends SigaaForm<OutrosDocumentos> {
	
	/** Tipo de busca a ser realizada. */
	private int tipoBusca;

	private String data;

	public OutrosDocumentosForm() {
		obj = new OutrosDocumentos();

		registerSearchData("tipoBusca", "obj.curso.id", "obj.descricao");

	}

	@Override
	public Collection<OutrosDocumentos> customSearch(HttpServletRequest req) throws DAOException {
		final String CURSO_LATO = "1";
		final String DESCRICAO = "2";
		final String TODOS = "3";
		GenericDAO dao = getGenericDAO(req);
		Collection<OutrosDocumentos> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			//Especificar as buscas
			if (CURSO_LATO.equals(tipoBusca))
				lista = dao.findByExactField(OutrosDocumentos.class, "curso.id", getSearchItem(req, "obj.curso.id"),getPaging(req));
			else if (DESCRICAO.equals(tipoBusca))
				lista = dao.findByLikeField(OutrosDocumentos.class, "descricao", getSearchItem(req, "obj.descricao"),getPaging(req));
			else if (TODOS.equals(tipoBusca))
				lista = dao.findAll(OutrosDocumentos.class);
		} finally {
			dao.close();
		}

		return lista;
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

	public String getData() {
		if ( obj.getData() != null )
			data = Formatador.getInstance().formatarData( obj.getData() );
		return data;
	}

	public void setData(String data) {
		obj.setData( parseDate(data) );
		this.data = data;
	}
	@Override
	public void validate(HttpServletRequest req) {
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(obj.getCurso(), "Curso", erros);
		ValidatorUtil.validateRequired(obj.getDescricao(), "Descrição", erros);
		ValidatorUtil.validaData(getData(), "Data", erros);
		ValidatorUtil.validateRequired(getData(), "Data", erros);


		if (erros != null) {
			for (MensagemAviso erro : erros.getMensagens()) {
				addMensagem(erro, req);
			}
		}
	}

}
