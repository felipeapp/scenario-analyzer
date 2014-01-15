/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 25/02/2008
 *
 */
package br.ufrn.sigaa.extensao.dominio;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import br.ufrn.sigaa.extensao.relatorio.dominio.RelatorioProjetoExtensao;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/*******************************************************************************
 * Classe de domínio usada para fazer a distribuição de ações e relatórios para
 * membros da comissão e avaliadores externos de extensão.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class DistribuicaoAtividadeExtensao {

	/** Usado na distribuição de relatórios. */
	private RelatorioProjetoExtensao relatorioAtividade = new RelatorioProjetoExtensao();

	/** Usado na distribuição manual de ação de extensão. */
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Usado na distribuição automática de ação de extensão. */
	private List<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();
	
	/** Avaliadores possíveis. */
	private Collection<AvaliadorAtividadeExtensao> avaliadoresPossiveis = new HashSet<AvaliadorAtividadeExtensao>();

	/** Avaliadores possíveis. */
	private Collection<MembroComissao> membrosComitePossiveis = new HashSet<MembroComissao>();

	/** Avaliações removidas. */
	private Set<AvaliacaoAtividade> avaliacoesRemovidas = new HashSet<AvaliacaoAtividade>();
	
	/** Utilizado na distribuição automática. */
	private TipoAvaliacao tipoAvaliacao = new TipoAvaliacao();
	
	/** Utilizado na distribuição automática. */
	private AreaTematica areaTematica = new AreaTematica();
	
	/** Quantidade de avaliações por a serem distribuídas para cada projeto. */
	private int numAvaliacoesPorProjeto;

	public int getNumAvaliacoesPorProjeto() {
		return numAvaliacoesPorProjeto;
	}

	public void setNumAvaliacoesPorProjeto(int numAvaliacoesPorProjeto) {
		this.numAvaliacoesPorProjeto = numAvaliacoesPorProjeto;
	}

	public DistribuicaoAtividadeExtensao() {
	}

	public RelatorioProjetoExtensao getRelatorioAtividade() {
		return relatorioAtividade;
	}

	public void setRelatorioAtividade(RelatorioProjetoExtensao relatorio) {
		this.relatorioAtividade = relatorio;
	}

	public AtividadeExtensao getAtividade() {
		return atividade;
	}

	public void setAtividade(AtividadeExtensao atividade) {
		this.atividade = atividade;
	}

	public Collection<AvaliadorAtividadeExtensao> getAvaliadoresPossiveis() {
		return avaliadoresPossiveis;
	}

	public void setAvaliadoresPossiveis(
			Collection<AvaliadorAtividadeExtensao> avaliadoresPossiveis) {
		this.avaliadoresPossiveis = avaliadoresPossiveis;
	}

	public Set<AvaliacaoAtividade> getAvaliacoesRemovidas() {
		return avaliacoesRemovidas;
	}

	public void setAvaliacoesRemovidas(
			Set<AvaliacaoAtividade> avaliacoesRemovidas) {
		this.avaliacoesRemovidas = avaliacoesRemovidas;
	}

	public Collection<MembroComissao> getMembrosComitePossiveis() {
		return membrosComitePossiveis;
	}

	public void setMembrosComitePossiveis(
			Collection<MembroComissao> membrosComitePossiveis) {
		this.membrosComitePossiveis = membrosComitePossiveis;
	}

	public List<AtividadeExtensao> getAtividades() {
		return atividades;
	}

	public void setAtividades(List<AtividadeExtensao> atividades) {
		this.atividades = atividades;
	}

	public TipoAvaliacao getTipoAvaliacao() {
		return tipoAvaliacao;
	}

	public void setTipoAvaliacao(TipoAvaliacao tipoAvaliacao) {
		this.tipoAvaliacao = tipoAvaliacao;
	}

	public AreaTematica getAreaTematica() {
		return areaTematica;
	}

	public void setAreaTematica(AreaTematica areaTematica) {
		this.areaTematica = areaTematica;
	}

}
