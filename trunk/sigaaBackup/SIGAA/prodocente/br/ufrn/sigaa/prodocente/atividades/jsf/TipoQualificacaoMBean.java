/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '22/12/2006'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.TipoQualificacao;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoQualificacaoMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.TipoQualificacao> {
	public TipoQualificacaoMBean() {
		obj = new TipoQualificacao();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoQualificacao.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoQualificacao();
	}
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.ADMINISTRADOR_PRODOCENTE);
	}
	
	@Override
	public String cancelar() {
		return forward("/prodocente/index.jsf");
	}
}
