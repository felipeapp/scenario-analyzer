/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 05/10/2006
 *
 */

package br.ufrn.sigaa.pesquisa.form;

import java.util.Collection;
import java.util.Iterator;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.sigaa.arq.dao.pesquisa.ItemAvaliacaoDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.AvaliacaoProjeto;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ItemAvaliacao;
import br.ufrn.sigaa.pesquisa.dominio.NotaItem;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/**
 * Formulário para manipulações de avaliações de projetos de pesquisa
 *
 * @author ricardo
 *
 */
@SuppressWarnings("serial")
public class AvaliacaoProjetoForm extends SigaaForm<AvaliacaoProjeto> {

	String[] notas;

	Integer ano;

	/** Campos do formulário de consulta */
	private int[] filtros;

	public static final int CONSULTOR 			= 1;
	public static final int CENTRO 				= 2;
	public static final int TIPO_DISTRIBUICAO 	= 3;
	public static final int SITUACAO 			= 4;
	public static final int EDITAL	 			= 5;

	private Collection<AvaliacaoProjeto> avaliacoesPendentes;
	
	public AvaliacaoProjetoForm() throws Exception {
		this.clear();
	}

	@Override
	public void clear() throws Exception {
		this.ano = getAnoAtual();

		this.obj = new AvaliacaoProjeto();
		this.obj.setConsultor( new Consultor() );
		this.obj.setProjetoPesquisa( new ProjetoPesquisa() );
		this.obj.getProjetoPesquisa().setCentro( new Unidade() );
		this.obj.getProjetoPesquisa().setEdital( new EditalPesquisa() );
	}

	@SuppressWarnings("unchecked")
	@Override
	public void referenceData(HttpServletRequest req) throws ArqException {
		ItemAvaliacaoDao dao = getDAO(ItemAvaliacaoDao.class, req);
		getReferenceData().put("itens", dao.findByTipo(ItemAvaliacao.AVALIACAO_PROJETO));
		notas = new String[((Collection) getReferenceData().get("itens")).size()];

		if (!obj.getNotasItens().isEmpty()){
			Iterator<NotaItem> it = obj.getNotasItens().iterator();
			for (int i = 0;i < notas.length; i++) {
				notas[i] = Formatador.getInstance().formatarDecimal1(it.next().getNota());
			}
		}

	}

	/**
	 * @return the notas
	 */
	public String[] getNotas() {
		return notas;
	}

	/**
	 * @param notas the notas to set
	 */
	public void setNotas(String[] notas) {
		this.notas = notas;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Collection<AvaliacaoProjeto> getAvaliacoesPendentes() {
		return avaliacoesPendentes;
	}

	public void setAvaliacoesPendentes(
			Collection<AvaliacaoProjeto> avaliacoesPendentes) {
		this.avaliacoesPendentes = avaliacoesPendentes;
	}



}
