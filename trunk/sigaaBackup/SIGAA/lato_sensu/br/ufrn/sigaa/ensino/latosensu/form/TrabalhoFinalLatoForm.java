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

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.upload.FormFile;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.latosensu.dominio.TrabalhoFinalLato;

/**
 * Form bean que encapsula um Trabalho Final de Lato Sensu
 *
 * @author Leonardo
 *
 */
public class TrabalhoFinalLatoForm extends SigaaForm<TrabalhoFinalLato> {

	private int tipoBusca;
	
	// Arquivo do trabalho final
	private FormFile arquivoTrabalhoFinal;

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


	public TrabalhoFinalLatoForm(){
		obj = new TrabalhoFinalLato();

		registerSearchData("tipoBusca", "obj.discenteLato.id", "obj.titulo");
	}

	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {

		final String DISCENTE = "1";
		final String TITULO = "2";
		final String TODOS = "3";

		GenericDAO dao = getGenericDAO(req);
		Collection lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			//TODO Especificar as buscas
			if (DISCENTE.equals(tipoBusca))
				lista = dao.findByExactField(TrabalhoFinalLato.class, "discenteLato", getSearchItem(req, "obj.discenteLato.id"), getPaging(req));
			else if (TITULO.equals(tipoBusca))
				lista = dao.findByLikeField(TrabalhoFinalLato.class, "titulo", getSearchItem(req, "obj.titulo"), getPaging(req));
			else if (TODOS.equals(tipoBusca))
				lista = dao.findAll(TrabalhoFinalLato.class);
		} finally {
			dao.close();
		}

		return lista;

	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		super.validate(req);
		validateRequired(obj.getTitulo(),"Título",req);
		if( obj.getDiscenteLato().getId() <= 0 )
			addMensagemErro("É obrigatório informar um Aluno",req);
		if( obj.getServidor().getId() <= 0 )
			addMensagemErro("É obrigatório informar um Orientador",req);
	}

	public FormFile getArquivoTrabalhoFinal() {
		return arquivoTrabalhoFinal;
	}

	public void setArquivoTrabalhoFinal(FormFile arquivoTrabalhoFinal) {
		this.arquivoTrabalhoFinal = arquivoTrabalhoFinal;
	}

}
