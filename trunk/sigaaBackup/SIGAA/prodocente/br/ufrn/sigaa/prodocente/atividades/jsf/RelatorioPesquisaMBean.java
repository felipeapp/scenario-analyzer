/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '15/03/2007'
 *
 */
package br.ufrn.sigaa.prodocente.atividades.jsf;

import java.util.Collection;

import javax.faces.model.SelectItem;

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.prodocente.atividades.dominio.RelatorioPesquisa;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;

public class RelatorioPesquisaMBean extends
AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.RelatorioPesquisa> {
	
	public RelatorioPesquisaMBean(){
		obj = new RelatorioPesquisa();
		setBuscaServidor(true);
	}

	public Collection<SelectItem> getAllCombo() {
		return getAll(RelatorioPesquisa.class, "id", "descricao");
	}
	
	@Override
	protected void afterCadastrar() {
		obj = new RelatorioPesquisa();
	}
	
	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PROPESQ);
	}
	
//	@SuppressWarnings("deprecation")
//	@Override
//	public Collection<RelatorioPesquisa> getAllAtividades() throws DAOException {
//		Servidor servidorLogado = null;	
//		if (getUsuarioLogado().getVinculoAtivo().getServidor() != null) {
//				servidorLogado = getUsuarioLogado().getVinculoAtivo().getServidor();
//			}
//			if (obj.getServidor().getId() != 0) {
//				getUsuarioLogado().getVinculoAtivo().setServidor(obj.getServidor());  
//		
//			return super.getAllAtividades();
//		}else {
//			atividades = null;	
//		}
//		getUsuarioLogado().getVinculoAtivo().setServidor(servidorLogado);
//		return atividades;
//	}

	@Override
	public String getUrlRedirecRemover(){
		return "/sigaa/prodocente/atividades/RelatorioPesquisa/lista.jsf";
	}
	
}