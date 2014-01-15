/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Classe de dom�nio usada para fazer a distribui��o de a��es e relat�rios para
 * membros da comiss�o e avaliadores externos de extens�o.
 * 
 * @author Ilueny Santos
 * 
 ******************************************************************************/
public class DistribuicaoAtividadeExtensao {

	/** Usado na distribui��o de relat�rios. */
	private RelatorioProjetoExtensao relatorioAtividade = new RelatorioProjetoExtensao();

	/** Usado na distribui��o manual de a��o de extens�o. */
	private AtividadeExtensao atividade = new AtividadeExtensao();

	/** Usado na distribui��o autom�tica de a��o de extens�o. */
	private List<AtividadeExtensao> atividades = new ArrayList<AtividadeExtensao>();
	
	/** Avaliadores poss�veis. */
	private Collection<AvaliadorAtividadeExtensao> avaliadoresPossiveis = new HashSet<AvaliadorAtividadeExtensao>();

	/** Avaliadores poss�veis. */
	private Collection<MembroComissao> membrosComitePossiveis = new HashSet<MembroComissao>();

	/** Avalia��es removidas. */
	private Set<AvaliacaoAtividade> avaliacoesRemovidas = new HashSet<AvaliacaoAtividade>();
	
	/** Utilizado na distribui��o autom�tica. */
	private TipoAvaliacao tipoAvaliacao = new TipoAvaliacao();
	
	/** Utilizado na distribui��o autom�tica. */
	private AreaTematica areaTematica = new AreaTematica();
	
	/** Quantidade de avalia��es por a serem distribu�das para cada projeto. */
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
