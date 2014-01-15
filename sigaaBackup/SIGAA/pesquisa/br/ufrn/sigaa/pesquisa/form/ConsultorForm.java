/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 28/09/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.AreaConhecimentoCnpqDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;

/**
 * Formulário para cadastros de consultores de projetos de pesquisa
 *
 * @author ilueny santos
 */
public class ConsultorForm extends SigaaForm<Consultor> {

	public static final int TODOS = 1;
	public static final int NOME = 2;

	private int tipoBusca;
	
	private boolean propesq;

	public ConsultorForm() {
		obj = new Consultor();

		registerSearchData("tipoBusca", "obj.servidor.pessoa.nome");
		registerSearchData("buscar", "true");

		setBuscar(true);
	}

	@Override
	public boolean isUnregister() {
		return true;
	}
	
	@Override
	public void unregisterSearchData(HttpServletRequest req) {
		setBuscar(true);
		registerSearchData("buscar", "true");
	}
	
	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		GenericDAO dao = getGenericDAO(req);

		Collection<Consultor> lista;

		String tipoBuscaString = getSearchItem(req, "tipoBusca");
		if(tipoBuscaString != null) {
			int tipo = Integer.parseInt(tipoBuscaString);
			switch( tipo ) {
			case NOME:
				lista = dao.findByLikeField(Consultor.class, "nome",
						getSearchItem(req, "obj.servidor.pessoa.nome"));
				break;
			default:
				lista = dao.findAll(Consultor.class, getPaging(req), "nome", "asc");
			}
		} else
			lista = dao.findAll(Consultor.class, getPaging(req), "nome", "asc");
		

		return lista;
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		AreaConhecimentoCnpqDao daoAreaConhecimento = getDAO(AreaConhecimentoCnpqDao.class, req);
		mapa.put("areasCNPQ", daoAreaConhecimento.findAreas(null));
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException{

		ListaMensagens lista = new ListaMensagens(); 
		lista.addAll(obj.validate());
		
		super.validate(req);
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public boolean isPropesq() {
		return propesq;
	}

	public void setPropesq(boolean propesq) {
		this.propesq = propesq;
	}
	
}