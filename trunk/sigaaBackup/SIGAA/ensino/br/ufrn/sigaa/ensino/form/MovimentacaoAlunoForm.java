/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 30/05/2007
*/
package br.ufrn.sigaa.ensino.form;

import java.util.Collection;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.MovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.dao.ensino.TipoMovimentacaoAlunoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.MovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.TipoMovimentacaoAluno;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe MovimentacaoAluno feitos em Struts.
 * 
 * @author amdantas
 */
public class MovimentacaoAlunoForm extends SigaaForm<MovimentacaoAluno> {

	/** Constantes referente a movimentação do aluno. */
	public static final int LISTA_CRUD_SIMPLES = 1;
	public static final int LISTA_RETORNO_AFASTAMENTO = 2;

	/** Informação referente ao tipo de lista. */
	private int tipoLista;

	/** Referente ao tipo de busca que se deseja realizar. */
	private String[] tipoBusca;

	/** Data de afastamento do Aluno */
	private String dataAfastamento;

	/** Data de retorno do Aluno */
	private String dataRetorno;

	public int getTipoLista() {
		return tipoLista;
	}

	public void setTipoLista(int tipoBusca) {
		this.tipoLista = tipoBusca;
	}

	public String[] getTipoBusca() {
		return tipoBusca;
	}

	public void setTipoBusca(String[] tipoBusca) {
		this.tipoBusca = tipoBusca;
	}

	public MovimentacaoAlunoForm() {
		obj = new MovimentacaoAluno();
		obj.setDiscente(new Discente());
		obj.setTipoMovimentacaoAluno(new TipoMovimentacaoAluno());
		obj.setUsuarioCadastro(new Usuario());
		obj.setUsuarioRetorno(new Usuario());

		registerSearchData("tipoBusca","obj.discente.id","obj.tipoAfastamentoAluno.id");
	}

	@Override
	public void beforePersist(HttpServletRequest req) throws DAOException {
		if(getTipoLista()!=2)
			obj.setUsuarioRetorno(null);
	}

	public String getDataAfastamento() {
		if (obj.getDataOcorrencia() != null)
			return formataDate(obj.getDataOcorrencia());
		return dataAfastamento;
	}

	public void setDataAfastamento(String dataAfastamento) {
		this.dataAfastamento = dataAfastamento;
	}

	public String getDataRetorno() {
		return dataRetorno;
	}

	public void setDataRetorno(String dataRetorno) {
		this.dataRetorno = dataRetorno;
	}

	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		TipoMovimentacaoAlunoDao dao = new TipoMovimentacaoAlunoDao();

		try {
			mapa.put("tipoAfastamentoAlunoTecs", dao.findAtivos(getNivelEnsino(req)));
		} finally {
			dao.close();
		}
	}

	@Override
	public Collection<MovimentacaoAluno> customSearch(HttpServletRequest req) throws DAOException {
		Collection<MovimentacaoAluno> lista = null;

		MovimentacaoAlunoDao dao = new MovimentacaoAlunoDao();
		try {

			String tipoBusca = getSearchItem(req, "tipoBusca");

			if ("discente".equals(tipoBusca)) {
				int idDiscente = 0;
				try {
					idDiscente = new Integer( getSearchItem(req, "obj.discente.id"));
				} catch (Exception e) { }
				if( getNivelEnsino(req) != NivelEnsino.LATO )
					lista = dao.findByDiscenteOrTipoMovimentacao(idDiscente, 0, true, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
				else
					lista = dao.findByDiscenteOrTipoMovimentacao(idDiscente, 0, true, 0, getNivelEnsino(req), getPaging(req));
			} else if ("tipo".equals(tipoBusca)) {
				int idTipo = 0;
				try {
					idTipo = new Integer( getSearchItem(req, "obj.tipoAfastamentoAluno.id"));
				} catch (Exception e) { }
				if( getNivelEnsino(req) != NivelEnsino.LATO )
					lista = dao.findByDiscenteOrTipoMovimentacao(0, idTipo, true, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
				else
					lista = dao.findByDiscenteOrTipoMovimentacao(0, idTipo, true, 0, getNivelEnsino(req), getPaging(req));
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}

		return lista;
	}

	@Override
	public void clear() throws Exception {
		super.clear();
		dataAfastamento = "";
		dataRetorno = "";
	}

	@Override
	public void validate(HttpServletRequest req) throws DAOException {
		ListaMensagens erros = new ListaMensagens();

		if ("true".equalsIgnoreCase(req.getParameter("cadastroRetorno"))) {
			GenericDAO dao = getGenericDAO(req);
			try {
				obj = dao.findByPrimaryKey(obj.getId(), obj.getClass());
			} finally {
				dao.close();
			}
			ValidatorUtil.validaData(dataRetorno, "Data de Retorno", erros);

		} else {
			erros = obj.validate();
			Date data = ValidatorUtil.validaData(dataAfastamento, "Data de Afastamento", erros);
			obj.setDataOcorrencia(data);
			ValidatorUtil.validateRequired(dataAfastamento, "Data de Afastamento", erros);
		}

		if (erros != null) {
			for (MensagemAviso erro : erros.getMensagens()) {
				addMensagem(erro, req);
			}
		}
	}
}
