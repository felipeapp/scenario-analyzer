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

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.pesquisa.LinhaPesquisaDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Formulário para manipulações em Linhas de Pesquisa
 * 
 * @author ricardo
 *
 */
public class LinhaPesquisaForm extends SigaaForm<LinhaPesquisa> {

	public static final int LINHA_PESQUISA_NOME = 1;
	public static final int PROJETO_PESQUISA_NOME = 2;
	public static final int GRUPO_PESQUISA_NOME = 3;
	public static final int TODOS = 4;
	
	private int tipoBusca;
	
	private ProjetoPesquisa projetoPesquisa;
	
	public LinhaPesquisaForm() {
		this.obj = new LinhaPesquisa();
		projetoPesquisa = new ProjetoPesquisa();
		
		registerSearchData("tipoBusca", "obj.nome", "projetoPesquisa.nome", "obj.grupoPesquisa.nome");
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection customSearch(HttpServletRequest req) throws DAOException {

		LinhaPesquisaDao linhaDAO = new LinhaPesquisaDao();
		Collection<LinhaPesquisa> lista = null;
		
		try {
			switch( Integer.parseInt(getSearchItem(req, "tipoBusca")) ) {
				case LINHA_PESQUISA_NOME:
					lista = linhaDAO.findByLikeField(LinhaPesquisa.class, "nome", getSearchItem(req, "obj.nome"));
					break;
				case PROJETO_PESQUISA_NOME:
					lista = linhaDAO.findByNomeProjeto(getSearchItem(req, "projetoPesquisa.titulo"));
					break;
				case GRUPO_PESQUISA_NOME:
					lista = linhaDAO.findByLikeField(LinhaPesquisa.class, "grupoPesquisa.nome", getSearchItem(req, "obj.grupoPesquisa.nome"));
					break;
			}
		} finally {
			linhaDAO.close();
		}
		
		return lista;
	}

	@Override
	public void beforePersist(HttpServletRequest req) throws DAOException {
		
		if (obj.getGrupoPesquisa().getId() == 0){
			obj.setGrupoPesquisa(null);
		}
		
	}

	@Override
	public void checkRole(HttpServletRequest req) throws ArqException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA, req);
	}
	
	/**
	 * @return the projetoPesquisa
	 */
	public ProjetoPesquisa getProjetoPesquisa() {
		return projetoPesquisa;
	}

	/**
	 * @param projetoPesquisa the projetoPesquisa to set
	 */
	public void setProjetoPesquisa(ProjetoPesquisa projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
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
