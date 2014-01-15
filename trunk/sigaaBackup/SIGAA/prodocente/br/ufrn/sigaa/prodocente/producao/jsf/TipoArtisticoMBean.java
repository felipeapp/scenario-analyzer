/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on '27/10/2006'
 *
 */
package br.ufrn.sigaa.prodocente.producao.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.web.jsf.AbstractControllerCadastro;
import br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico;

/**
 * Gerado pelo CrudBuilder
 */
public class TipoArtisticoMBean
		extends
		AbstractControllerCadastro<br.ufrn.sigaa.prodocente.producao.dominio.TipoArtistico> {
	public TipoArtisticoMBean() {
		obj = new TipoArtistico();
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(TipoArtistico.class, "id", "descricao");
	}

	@Override
	protected void afterCadastrar() {
		obj = new TipoArtistico();
	}
	
}
