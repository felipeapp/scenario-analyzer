/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 17/07/2007
 *
 */

package br.ufrn.sigaa.pesquisa.jsf;

import java.util.Collection;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.pesquisa.GrupoPesquisaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.pesquisa.dominio.GrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * MBean para efetuar buscas de grupos de pesquisa na área pública do sigaa.
 * 
 * @author Leonardo Campos
 *
 */
public class ConsultaBasesPesquisaMBean extends SigaaAbstractController<GrupoPesquisa> {

	public final String JSP_CONSULTA_BASES = "/public/pesquisa/consulta_bases.jsp";

	private boolean filtroNome;
	private boolean filtroCoordenador;

	public ConsultaBasesPesquisaMBean(){
		obj = new GrupoPesquisa();
		obj.setCoordenador(new Servidor());
	}

	public String buscar() throws DAOException, ArqException {
		GrupoPesquisaDao dao = getDAO(GrupoPesquisaDao.class);

		String nome = null;
		String nomeCoordenador = null;

		ListaMensagens erros = new ListaMensagens();

		if(filtroNome){
			nome = obj.getNome();
			ValidatorUtil.validateRequired(nome, "Nome do Grupo", erros);
		}
		if(filtroCoordenador){
			nomeCoordenador = obj.getCoordenador().getPessoa().getNome();
			ValidatorUtil.validateRequired(nomeCoordenador, "Coordenador", erros);
		}

		if( erros.isEmpty() ) {

			Collection<GrupoPesquisa> lista = dao.findOtimizado(nome, nomeCoordenador);

			if (!lista.isEmpty()) {
				setResultadosBusca(lista);
			} else {
				addMessage("Nenhum grupo de pesquisa foi encontrado de acordo com os critérios de busca utilizados.", TipoMensagemUFRN.WARNING);
			}
		} else
			addMensagens(erros);


		return forward(JSP_CONSULTA_BASES);
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		super.cancelar();
		return forward("/public/home.jsp");
	}

	public boolean isFiltroCoordenador() {
		return filtroCoordenador;
	}

	public void setFiltroCoordenador(boolean filtroCoordenador) {
		this.filtroCoordenador = filtroCoordenador;
	}

	public boolean isFiltroNome() {
		return filtroNome;
	}

	public void setFiltroNome(boolean filtroNome) {
		this.filtroNome = filtroNome;
	}
}
