/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 04/06/2009
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import javax.persistence.PrePersist;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.CategoriaProjetoPesquisa;

/**
 * MBean para CRUDs das categorias de projetos de pesquisa
 * 
 * @author Leonardo Campos
 *
 */
@Component("categoriaProjetoPesquisaBean") @Scope("request")
public class CategoriaProjetoPesquisaMBean extends SigaaAbstractController<CategoriaProjetoPesquisa> {

	public CategoriaProjetoPesquisaMBean() {
		obj = new CategoriaProjetoPesquisa();
	}
	
	@Override
	public String getFormPage() {
		return "/pesquisa/CategoriaProjetoPesquisa/form.jsf";
	}
	
	@Override
	public String getListPage() {
		return "/pesquisa/CategoriaProjetoPesquisa/lista.jsf";
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_PESQUISA);
	}
	
	@Override
	public String remover() throws ArqException {
		setId();
		prepareMovimento(ArqListaComando.REMOVER);
		return super.remover();
	}
	
}
