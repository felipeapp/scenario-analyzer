/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 03/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.LinhaPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Form para manipulações com Grupos de Pesquisa (Bases)
 *
 * @author ricardo
 *
 */
@SuppressWarnings("serial")
public class GrupoPesquisaForm extends SigaaForm<GrupoPesquisa> {

	public static final int TODOS = 100;
	public static final int GRUPO_PESQUISA_NOME = 1;
	public static final int AREA_DE_CONHECIMENTO = 2;
	public static final int CATEGORIA = 3;
	public static final int LINHA_PESQUISA_NOME = 4;
	public static final int COORDENADOR = 5;

	/** Constantes das finalidades de busca */
	public static final int DECLARACAO_COORDENADOR = 11;
	public static final int DECLARACAO_COLABORADOR = 12;

	private int finalidadeBusca;

	private int[] filtros = {};

	private int tipoBusca;

	private LinhaPesquisa linhaPesquisa;


	public GrupoPesquisaForm() {
		clear();
		registerSearchData("tipoBusca", "obj.nome", "obj.areaConhecimentoCnpq.nome",
				"obj.categoriaGrupoPesquisa.nome", "linhaPesquisa.nome",
				"obj.coordenador.id");
	}

	@Override
	public void clear() {
		obj = new GrupoPesquisa();
		obj.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		obj.setCoordenador(new Servidor());

		linhaPesquisa = new LinhaPesquisa();
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		super.validate(req);

		GrupoPesquisa grupo = this.getObj();
		if (grupo.getCoordenador().getId() <= 0) {
			addMensagemErro("O coordenador informado não encontra-se cadastrado na base de dados", req);
		}

	}

	@SuppressWarnings("unchecked")
    @Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class, req);
		Collection<GrupoPesquisa> lista = null;

		String tipoBusca = getSearchItem(req, "tipoBusca");
		if ( tipoBusca == null ) {
			tipoBusca = "0";
		}

		switch( Integer.parseInt(tipoBusca) ) {
			case GRUPO_PESQUISA_NOME:
				lista = dao.findByLikeField(GrupoPesquisa.class, "nome", getSearchItem(req, "obj.nome"), getPaging(req), "asc", "codigo");
				break;
			case AREA_DE_CONHECIMENTO:
				lista = dao.findByLikeField(GrupoPesquisa.class, "areaConhecimentoCnpq.nome", getSearchItem(req, "obj.areaConhecimentoCnpq.nome"), getPaging(req), "asc", "codigo");
				break;
			case CATEGORIA:
				lista = dao.findByLikeField(GrupoPesquisa.class, "categoriaGrupoPesquisa.nome", getSearchItem(req, "obj.categoriaGrupoPesquisa.nome"), getPaging(req), "asc", "codigo");
				break;
			case COORDENADOR:
				lista = dao.findByLikeField(GrupoPesquisa.class, "coordenador.id", Integer.valueOf( getSearchItem(req, "obj.coordenador.id")), getPaging(req), "asc", "codigo");
				break;
			case LINHA_PESQUISA_NOME:
				break;
			default:
				lista = dao.findAllOrdenado(getPaging(req));
		}

		return lista;
	}

	@Override
	public Object formBackingObject(HttpServletRequest req) throws Exception {
		GrupoPesquisa grupo =  (GrupoPesquisa) super.formBackingObject(req);

		if (grupo.getAreaConhecimentoCnpq() == null ) {
			grupo.setAreaConhecimentoCnpq(new AreaConhecimentoCnpq());
		}

		if (grupo.getCoordenador() == null ) {
			grupo.setCoordenador(new Servidor());
		}

		return grupo;
	}

	@Override
	public void beforePersist(HttpServletRequest req) throws DAOException {
		obj.setDataCriacao(new Date());
	}


	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class, req);
		mapa.put("areas", dao.findAll(AreaConhecimentoCnpq.class, "nome", "asc"));
		mapa.put("count", dao.findCountGrupoPesquisa());
	}

	/**
	 * @return the linhaPesquisa
	 */
	public LinhaPesquisa getLinhaPesquisa() {
		return linhaPesquisa;
	}

	/**
	 * @return the tipoBusca
	 */
	public int getTipoBusca() {
		return tipoBusca;
	}

	/**
	 * @param linhaPesquisa the linhaPesquisa to set
	 */
	public void setLinhaPesquisa(LinhaPesquisa linhaPesquisa) {
		this.linhaPesquisa = linhaPesquisa;
	}

	/**
	 * @param tipoBusca the tipoBusca to set
	 */
	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public int getFinalidadeBusca() {
		return finalidadeBusca;
	}

	public void setFinalidadeBusca(int finalidadeBusca) {
		this.finalidadeBusca = finalidadeBusca;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}


}
