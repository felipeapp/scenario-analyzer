/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;

/**
 * Formulário para cadastros de itens de avaliação de projetos de pesquisa
 * 
 * @author ricardo
 *
 */
public class ItemAvaliacaoForm extends SigaaForm<ItemAvaliacao> {

	public static final int TODOS = 1;
	public static final int DESCRICAO = 2;
	
	private int tipoBusca;
	
	private String dataCadastro;
	
	public ItemAvaliacaoForm() {
		this.obj = new ItemAvaliacao();
		this.obj.setPeso(1);
		if (obj.getDataCriacao() == null) {
			this.dataCadastro = formataDate(new Date());
		}
		
		registerSearchData("tipoBusca", "obj.descricao");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		GenericDAO dao = getGenericDAO(req);
		Collection<ItemAvaliacao> lista = null;
		
		switch( Integer.parseInt(getSearchItem(req, "tipoBusca")) ) {
			case DESCRICAO:
				lista = dao.findByLikeField(ItemAvaliacao.class, "descricao", getSearchItem(req, "obj.descricao"));
				break;
		}
	
		return lista;
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		if (parseDate(this.dataCadastro) != null) {
			this.obj.setDataCriacao(parseDate(this.dataCadastro));
		}
		
		super.validate(req);
		
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public String getDataCadastro() {
		return dataCadastro;
	}

	public void setDataCadastro(String dataCadastro) {
		this.dataCadastro = dataCadastro;
	}
	
}
