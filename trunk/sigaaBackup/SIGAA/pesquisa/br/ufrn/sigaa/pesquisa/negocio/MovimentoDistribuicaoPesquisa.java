/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 13/10/2006
 *
 */
package br.ufrn.sigaa.pesquisa.negocio;

import java.util.Collection;
import java.util.Map;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.Consultor;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;


/**
 * Possibilita a montagem de um movimento de distribuição de projetos de pesquisa
 * para consultores(internos e externos) avaliarem.
 *
 * Essa distribuição pode ser automática ou manual dependendo da escolha do operador
 * do sistema.
 *
 * Durante a distribuição automática o usuário informa somente a quantidade de
 * consultores que devem receber o projeto para avaliação.
 *
 * Na distribuição manual o usuário informa uma lista de consultores com as suas
 * respectivas áreas de conhecimento (grande área) e todos os projetos da área
 * informada serão distribuídos para ele.
 *
 * Na escolha da grande área somente os consultores habilitados para avaliaÃ§Ã£o
 * devem ser listados para receber o projeto.
 *
 * @author ilueny santos
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
public class MovimentoDistribuicaoPesquisa extends AbstractMovimentoAdapter {

	/** Diferentes ações de distribuição */
	public static final int DISTRIBUIR_AUTOMATICAMENTE 	= 1;
	public static final int DISTRIBUIR_POR_CENTROS 		= 2;
	public static final int DISTRIBUIR_MANUALMENTE 		= 3;
	public static final int DISTRIBUIR_AUTOMATICAMENTE_ESPECIAIS 		= 4;

	private int acao;

	/** Total de consultores por item (DIST AUTOMATICA) */
	private int consultoresPorItem;
	
	/** Relação de distribuição de consultores (DIST CENTROS) */
	private Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicaoManual;

	/** Consultor responsável (DIST MANUAL) */
	private Consultor consultor;

	/** Projetos a distribuir (DIST MANUAL) */
	private Collection<ProjetoPesquisa> projetos;
	
	/** Id do edital cujos projetos serão distribuídos */
	private Integer idEdital;

	/**
	 * Ação que o movimento irá executar.
	 *
	 * @return int
	 */
	public int getAcao() {
		return acao;
	}

	/**
	 * Usar constates de {@link MovimentoDistribuicaoPesquisa}
	 *	DISTRIBUIR_MANUALMENTE 		= 1;
	 *	DISTRIBUIR_AUTOMATICAMENTE 	= 2;
	 *
	 * @param acao
	 */
	public void setAcao(int acao) {
		this.acao = acao;
	}

	/**
	 * Retorna o mapa com a configuraÃ§Ã£o da distribuiÃ§Ã£o manual
	 *
	 * @return
	 */
	public Map<Consultor, AreaConhecimentoCnpq> getMapaDistribuicaoManual() {
		return mapaDistribuicaoManual;
	}

	public void setMapaDistribuicaoManual(
			Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicaoManual) {
		this.mapaDistribuicaoManual = mapaDistribuicaoManual;
	}

	public int getConsultoresPorItem() {
		return consultoresPorItem;
	}

	public void setConsultoresPorItem(int consultoresPorItem) {
		this.consultoresPorItem = consultoresPorItem;
	}

	public Consultor getConsultor() {
		return consultor;
	}

	public void setConsultor(Consultor consultor) {
		this.consultor = consultor;
	}

	public Collection<ProjetoPesquisa> getProjetos() {
		return projetos;
	}

	public void setProjetos(Collection<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}

	public Integer getIdEdital() {
		return idEdital;
	}

	public void setIdEdital(Integer idEdital) {
		this.idEdital = idEdital;
	}


}
