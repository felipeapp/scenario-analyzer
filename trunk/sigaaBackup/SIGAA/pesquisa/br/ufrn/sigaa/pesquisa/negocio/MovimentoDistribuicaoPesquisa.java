/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Possibilita a montagem de um movimento de distribui��o de projetos de pesquisa
 * para consultores(internos e externos) avaliarem.
 *
 * Essa distribui��o pode ser autom�tica ou manual dependendo da escolha do operador
 * do sistema.
 *
 * Durante a distribui��o autom�tica o usu�rio informa somente a quantidade de
 * consultores que devem receber o projeto para avalia��o.
 *
 * Na distribui��o manual o usu�rio informa uma lista de consultores com as suas
 * respectivas �reas de conhecimento (grande �rea) e todos os projetos da �rea
 * informada ser�o distribu�dos para ele.
 *
 * Na escolha da grande �rea somente os consultores habilitados para avaliação
 * devem ser listados para receber o projeto.
 *
 * @author ilueny santos
 * @author Ricardo Wendell
 *
 */
@SuppressWarnings("serial")
public class MovimentoDistribuicaoPesquisa extends AbstractMovimentoAdapter {

	/** Diferentes a��es de distribui��o */
	public static final int DISTRIBUIR_AUTOMATICAMENTE 	= 1;
	public static final int DISTRIBUIR_POR_CENTROS 		= 2;
	public static final int DISTRIBUIR_MANUALMENTE 		= 3;
	public static final int DISTRIBUIR_AUTOMATICAMENTE_ESPECIAIS 		= 4;

	private int acao;

	/** Total de consultores por item (DIST AUTOMATICA) */
	private int consultoresPorItem;
	
	/** Rela��o de distribui��o de consultores (DIST CENTROS) */
	private Map<Consultor, AreaConhecimentoCnpq> mapaDistribuicaoManual;

	/** Consultor respons�vel (DIST MANUAL) */
	private Consultor consultor;

	/** Projetos a distribuir (DIST MANUAL) */
	private Collection<ProjetoPesquisa> projetos;
	
	/** Id do edital cujos projetos ser�o distribu�dos */
	private Integer idEdital;

	/**
	 * A��o que o movimento ir� executar.
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
	 * Retorna o mapa com a configuração da distribuição manual
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
