/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 22/09/2006
 *
 */
package br.ufrn.sigaa.pesquisa.form;


import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.seguranca.SigaaSubsistemas;
import br.ufrn.sigaa.arq.dao.pesquisa.RelatorioProjetoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.pesquisa.dominio.RelatorioProjeto;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 *
 * Form com os dados necessários para o controle dos relatórios finais
 * de projetos de pesquisa
 *
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
public class RelatorioProjetoForm extends SigaaForm<RelatorioProjeto> {

	/** Constantes utilizadas durante as buscas */
	public static final int BUSCA_COORDENADOR = 1;
	public static final int BUSCA_AREA_CONHECIMENTO = 2;
	public static final int BUSCA_STATUS_RELATORIO = 3;
	public static final int BUSCA_ANO = 4;
	
	private String parecerAnterior;
	
	public RelatorioProjetoForm(){
		super.obj = new RelatorioProjeto();
		obj.getProjetoPesquisa().setCoordenador( new Servidor() );
		obj.getProjetoPesquisa().setAreaConhecimentoCnpq( new AreaConhecimentoCnpq() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public Collection customSearch(HttpServletRequest req) throws ArqException {
		RelatorioProjetoDao dao = getDAO(RelatorioProjetoDao.class, req);

		Collection lista = null;
		// Seleciona a lista de projetos onde o servidor logado é coordenador
		Usuario usuario = (Usuario) getUsuarioLogado(req);
		if ( !usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA)
				|| usuario.isUserInRole(SigaaPapeis.GESTOR_PESQUISA) && !getSubSistemaCorrente(req).equals( SigaaSubsistemas.PESQUISA) )  {
			lista = dao.findByCoordenador( usuario.getServidor() );
		} else {
			lista = dao.findAll();
		}

		req.setAttribute( "custom" , true);
		return lista;
	}

	private int[] filtros = {};

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public String getParecerAnterior() {
		return parecerAnterior;
	}

	public void setParecerAnterior(String parecerAnterior) {
		this.parecerAnterior = parecerAnterior;
	}
	
}