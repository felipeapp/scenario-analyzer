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

import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.prodocente.atividades.dominio.IniciacaoCientifica;
import br.ufrn.sigaa.prodocente.jsf.AbstractControllerAtividades;
import br.ufrn.sigaa.projetos.dominio.EntidadeFinanciadora;

/**
 * Gerado pelo CrudBuilder
 */
public class IniciacaoCientificaMBean
		extends
		AbstractControllerAtividades<br.ufrn.sigaa.prodocente.atividades.dominio.IniciacaoCientifica> {
	@SuppressWarnings("deprecation")
	public IniciacaoCientificaMBean() {
		obj = new IniciacaoCientifica();
		obj.setEntidadeFinanciadora(new EntidadeFinanciadora());
		obj.setArea(new AreaConhecimentoCnpq());
		obj.setDepartamento(new Unidade());
		obj.setServidor(new Servidor());
		obj.setSubArea(new AreaConhecimentoCnpq());
	}

	@SuppressWarnings("deprecation")
	public Collection<SelectItem> getAllCombo() {
		return getAll(IniciacaoCientifica.class, "id", "nomeProjeto");
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void afterCadastrar() {
		obj = new IniciacaoCientifica();
	}

	@Override
	public void checkChangeRole() throws SegurancaException {
		checkRole(SigaaPapeis.PRODOCENTE_PROPESQ);
	}

//	@SuppressWarnings("deprecation")
//	@Override
//	public Collection<IniciacaoCientifica> getAllAtividades() throws DAOException {
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
		return "/sigaa/prodocente/atividades/IniciacaoCientifica/lista.jsf";
	}

}