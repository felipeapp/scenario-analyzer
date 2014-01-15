/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.negocio;

import java.util.ArrayList;
import java.util.Collection;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoItem;
import br.ufrn.sigaa.prodocente.relatorios.dominio.GrupoRelatorioProdutividade;

/**
 * @author Eric Moura (Eriquim)
 *
 */
public class RelatorioProdutividadeMov extends MovimentoCadastro {


	Collection<GrupoRelatorioProdutividade> grupoRelatorioProdutividadeExcluidos = new ArrayList<GrupoRelatorioProdutividade>();

	Collection<GrupoItem> grupoItemExcluidos = new ArrayList<GrupoItem>();

	public Collection<GrupoItem> getGrupoItemExcluidos() {
		return grupoItemExcluidos;
	}

	public void setGrupoItemExcluidos(Collection<GrupoItem> grupoItemExcluidos) {
		this.grupoItemExcluidos = grupoItemExcluidos;
	}

	public Collection<GrupoRelatorioProdutividade> getGrupoRelatorioProdutividadeExcluidos() {
		return grupoRelatorioProdutividadeExcluidos;
	}

	public void setGrupoRelatorioProdutividadeExcluidos(
			Collection<GrupoRelatorioProdutividade> grupoRelatorioProdutividadeExcluidos) {
		this.grupoRelatorioProdutividadeExcluidos = grupoRelatorioProdutividadeExcluidos;
	}

}
