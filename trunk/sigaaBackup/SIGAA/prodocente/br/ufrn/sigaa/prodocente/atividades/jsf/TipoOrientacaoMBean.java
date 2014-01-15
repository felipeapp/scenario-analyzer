/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '18/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoOrientacaoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoOrientacao> {
	
	public TipoOrientacaoMBean() {
		obj = new TipoOrientacao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoOrientacao.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoOrientacao();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	public String direcionar() {
		return forward("/prodocente/atividades/TipoOrientacao/form.jsf");
	}
	
	@Override
	public String getListPage() {
		return "/prodocente/atividades/TipoOrientacao/lista.jsf";
	}

	
	/**
	 * Seta o id, para true caso, evitando assim que ao cadastrar ou atualizar o mesmo passe o ativo
	 * para false o que impossibilitaria a sua visualização.
	 */
	@Override
	public void beforeCadastrarAndValidate() throws NegocioException,
			SegurancaException, DAOException {

		Character UpperCase = Character.toUpperCase(obj.getNivelEnsino());
		obj.setNivelEnsino(UpperCase);
		obj.setAtivo(true);
		
		GenericDAO dao = getDAO(GenericDAOImpl.class);
		Collection<TipoOrientacao> mesmoNome = dao.findByExactField(
				TipoOrientacao.class, "descricao", obj.getDescricao());
		
		for (TipoOrientacao to : mesmoNome) {
			if (to.getId() == obj.getId()) {
				super.beforeCadastrarAndValidate();	
			}else {
				addMensagemErro("Já existe um Tipo de Orientação cadastrada com essa Descrição.");
				direcionar();
			}
		}
		try {
			prepareMovimento(ArqListaComando.CADASTRAR);
		} catch (ArqException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String cancelar() {
		return forward(getListPage());
	}
}