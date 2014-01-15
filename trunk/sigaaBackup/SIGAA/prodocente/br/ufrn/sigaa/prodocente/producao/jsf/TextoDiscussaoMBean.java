/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.sigaa.prodocente.producao.dominio.Producao;
import br.ufrn.sigaa.prodocente.producao.dominio.TextoDiscussao;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoProducao;

/**
 * Gerado pelo CrudBuilder
 */
public class TextoDiscussaoMBean
		extends
		AbstractControllerProdocente<br.ufrn.sigaa.prodocente.producao.dominio.TextoDiscussao> {
	public TextoDiscussaoMBean() {
		obj = new TextoDiscussao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TextoDiscussao.class, "id", "descricao");
	}

	@Override
	public void beforeCadastrarAndValidate() throws SegurancaException, DAOException, NegocioException {
		obj.setTipoProducao(TipoProducao.TEXTO_DISCUSSAO);
		obj.setServidor(getServidorUsuario());
		
		super.beforeCadastrarAndValidate();
	}

	@Override
	protected void afterCadastrar() {
		obj = new TextoDiscussao();
	}


	@Override
	public void popularObjeto(Producao p) {
		obj =(TextoDiscussao) p;
	}

	public List<SelectItem> getTipoParticipacao() throws DAOException {
		return getTipoParticipacao(TipoProducao.TEXTO_DISCUSSAO);
	}
}
