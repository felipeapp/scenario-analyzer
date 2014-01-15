/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '24/10/2006'
 *
 */
package br.ufrn.sigaa.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.TipoAtividade;

public class TipoAtividadeMBean extends SigaaAbstractController<TipoAtividade> {
	public TipoAtividadeMBean() {
		obj = new TipoAtividade();
	}

	public Collection<SelectItem> getAllCombo() {
		try {
			char nivel =getNivelEnsino();
			if (!NivelEnsino.isValido(nivel))
				nivel = NivelEnsino.GRADUACAO;

			Collection<TipoAtividade> tipos = getGenericDAO().findByExactField(TipoAtividade.class, "nivelEnsino", nivel);
			if (!isEmpty(tipos))
				return toSelectItems(tipos, "id", "descricao");
		} catch (DAOException e) {
			e.printStackTrace();
		}
		return new ArrayList<SelectItem>();
	}

	/**
	 * Para redirecionar o usuário para a tela da listagem logo após uma nova inserção.
	 */
	@Override
	public String forwardCadastrar() {
		return getListPage();
	}

	@Override
	public Collection<TipoAtividade> getAllPaginado() throws ArqException {
		setTamanhoPagina(20);
		return super.getAllPaginado();
	}
	
	@Override
	public String getAtributoOrdenacao() {
		return "descricao";
	}

	@Override
	public String cadastrar() throws SegurancaException, ArqException,
			NegocioException {
	
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoAtividade> mesmaTipo = dao.findByExactField(TipoAtividade.class, "descricao", obj.getDescricao());
		for (TipoAtividade as : mesmaTipo) {
			if (as.getId() == obj.getId()) {
				return super.cadastrar();
			} if(as.getDescricao().equals(obj.getDescricao())){
				addMensagem(MensagensArquitetura.OBJETO_JA_CADASTRADO, "Tipo Atividade");
				return null;
			}
		}
		return super.cadastrar();
	}
}