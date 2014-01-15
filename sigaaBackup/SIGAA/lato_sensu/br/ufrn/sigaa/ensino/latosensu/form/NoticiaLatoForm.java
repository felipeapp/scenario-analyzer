/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 03/11/2006
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.latosensu.dominio.NoticiaLato;

/**
 * Form bean que encapsula uma Notícia de Lato Sensu
 *
 * @author Leonardo
 *
 */
public class NoticiaLatoForm extends SigaaForm<NoticiaLato> {

	private int tipoBusca;

	private String data;

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


	public NoticiaLatoForm(){
		obj = new NoticiaLato();
		setData( Formatador.getInstance().formatarData( new Date( System.currentTimeMillis() ) ) );

		registerSearchData("tipoBusca", "obj.titulo", "obj.data");
	}

	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {

		final String TITULO = "1";
		final String DATA = "2";
		final String TODOS = "3";

		GenericDAO dao = getGenericDAO(req);
		Collection lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			//Especificar as buscas
			if (TITULO.equals(tipoBusca))
				lista = dao.findByLikeField(NoticiaLato.class, "titulo", getSearchItem(req, "obj.titulo"), getPaging(req));
			else if (DATA.equals(tipoBusca))
				lista = dao.findByExactField(NoticiaLato.class, "data", getSearchItem(req, "obj.data"), getPaging(req));
			else if (TODOS.equals(tipoBusca))
				lista = dao.findAll(NoticiaLato.class);
		} finally {
			dao.close();
		}

		return lista;

	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		super.validate(req);
		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired( obj.getTitulo(),"Título", erros );
		ValidatorUtil.validateRequired( obj.getTexto(),"Texto", erros );
		obj.setData( ValidatorUtil.validaData(getData(), "Data", erros) );
		ValidatorUtil.validateRequired( obj.getData(),"Data", erros );

		if (erros != null) {
			for (MensagemAviso erro : erros.getMensagens()) {
				addMensagem(erro, req);
			}
		}
	}

	public String getData() {
		if( obj.getData() != null )
			data = Formatador.getInstance().formatarData( obj.getData() );
		return data;
	}

	public void setData(String data) {
		obj.setData( parseDate(data) );
		this.data = data;
	}

	@Override
	public void clear() throws Exception {
		super.clear();
		data = "";
	}

}
