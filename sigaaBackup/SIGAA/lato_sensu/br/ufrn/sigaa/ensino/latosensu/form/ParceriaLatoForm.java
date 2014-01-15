/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '19/10/2006'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.form;

import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.latosensu.dominio.ParceriaLato;
import br.ufrn.sigaa.pessoa.dominio.PessoaJuridica;

/**
 * Form Bean responsável pelas operações que envolvem a associação um Curso Lato Sensu a uma pessoa jurídica.
 * 
 * @author leonardo
 *
 */
public class ParceriaLatoForm extends SigaaForm<ParceriaLato> {

	/** Tipo de busca a ser realizada. */
	private int tipoBusca;

	public ParceriaLatoForm() {
		obj = new ParceriaLato();

		registerSearchData("tipoBusca", "obj.cursoLato.id",
				"obj.pessoaJuridica.pessoa.id");

	}

	@Override
	public void beforePersist(HttpServletRequest req) throws DAOException {
		GenericDAO dao = getGenericDAO(req);

		Collection<PessoaJuridica> c = dao.findByExactField(PessoaJuridica.class, "pessoa.id", getObj().getPessoaJuridica().getId());
		if(!c.isEmpty() && c != null)
			for (PessoaJuridica pj : c) {
				getObj().setPessoaJuridica( pj );
			}
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {

		ListaMensagens erros = new ListaMensagens();

		ValidatorUtil.validateRequired(obj.getCursoLato(), "Curso", erros);
		ValidatorUtil.validateRequired(obj.getPessoaJuridica(), "Pessoa Jurídica", erros);
		ValidatorUtil.validateRequired(obj.getDescricao(), "Descrição", erros);

		if( obj.getPessoaJuridica().getId() > 0 ){
			GenericDAO dao = getGenericDAO(req);

			Collection<PessoaJuridica> c = dao.findByExactField(PessoaJuridica.class, "pessoa.id", getObj().getPessoaJuridica().getId());
			if(c.isEmpty() || c == null)
				addMensagemErro("Essa pessoa não é uma pessoa jurídica.", req);
			dao.close();
		}

		if (erros != null) {
			for (MensagemAviso erro : erros.getMensagens()) {
				addMensagem(erro, req);
			}
		}
	}

	@Override
	public Collection<ParceriaLato> customSearch(HttpServletRequest req) throws DAOException {
		// tipos de parâmetros de consulta vindo da pagina JSP
		final String CURSO_LATO = "1";
		final String PESSOA_JURIDICA = "2";
		final String TODOS = "3";

		GenericDAO dao = getGenericDAO(req);
		Collection<ParceriaLato> lista = null;

		try {
			String tipoBusca = getSearchItem(req, "tipoBusca");
			// Especificar as buscas
			if (PESSOA_JURIDICA.equals(tipoBusca))
				lista = dao.findByExactField(ParceriaLato.class,
						"pessoaJuridica.pessoa.id", getSearchItem(req,
								"obj.pessoaJuridica.pessoa.id"), getPaging(req));
			else if (CURSO_LATO.equals(tipoBusca))
				lista = dao.findByExactField(ParceriaLato.class,
						"cursoLato.id", getSearchItem(req, "obj.cursoLato.id"),
						getPaging(req));
			else if (TODOS.equals(tipoBusca))
				lista = dao.findAll(ParceriaLato.class, getPaging(req));
		} finally {
			dao.close();
		}

		return lista;
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		addAll("pessoasJuridicas", PessoaJuridica.class);
		Usuario user = (Usuario) getUsuarioLogado(req);
		if( user.isCoordenadorLato() )
			getReferenceData().put("cursosLato", user.getCursosLato());
	}

	public int getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(int tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

}
