package br.ufrn.sigaa.monitoria.jsf.dominio;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import br.ufrn.sigaa.monitoria.dominio.ProjetoEnsino;
import br.ufrn.sigaa.monitoria.dominio.RelatorioProjetoMonitoria;
import br.ufrn.sigaa.monitoria.dominio.ResumoSid;
import br.ufrn.sigaa.projetos.dominio.MembroComissao;

/**
 * Classe de dom�nio usada para fazer a distribui��o de projetos e relat�rios para membros da
 * comiss�o.
 *
 * @author Gleydson
 * @author ilueny
 *
 */
public class DistribuicaoProjeto {
	
	
	/** usado na distribui��o de relat�rios */
	private RelatorioProjetoMonitoria relatorio = new RelatorioProjetoMonitoria();
	
	/** usado na distribui��o de projetos */
	private ProjetoEnsino projetoEnsino = new ProjetoEnsino();

	/** usado na distribui��o de resumos SID */
	private ResumoSid resumo = new ResumoSid();

	
	private Collection<MembroComissao> avaliadoresPossiveis =  new HashSet<MembroComissao>();
	
	private Set<MembroComissao> avaliadoresRemovidos =  new HashSet<MembroComissao>();
	
	
	public DistribuicaoProjeto(){
	}

	
	/**
	 * Avaliadores que poder�o receber o projeto para avaliar.
	 * 
	 * @return
	 */
	public Collection<MembroComissao> getAvaliadoresPossiveis() {
		return avaliadoresPossiveis;
	}


	public void setAvaliadoresPossiveis(
			Collection<MembroComissao> avaliadoresPossiveis) {
		this.avaliadoresPossiveis = avaliadoresPossiveis;
	}



	/**
	 * Avaliadores que estavam avaliando o projeto de ensino e agora est�o sendo
	 * retirados da avalia��o.
	 * 
	 * @return
	 */
	public Set<MembroComissao> getAvaliadoresRemovidos() {
		return avaliadoresRemovidos;
	}




	public void setAvaliadoresRemovidos(
			Set<MembroComissao> avaliadoresRemovidos) {
		this.avaliadoresRemovidos = avaliadoresRemovidos;
	}



	/**
	 * Projeto de ensino que ser� distribu�do entre os membros da 
	 * comiss�o de monitoria para ser avaliado.
	 * 
	 * @return
	 */
	public ProjetoEnsino getProjetoEnsino() {
		return projetoEnsino;
	}

	
	
	public void setProjetoEnsino(ProjetoEnsino projetoEnsino) {
		this.projetoEnsino = projetoEnsino;
	}


	public RelatorioProjetoMonitoria getRelatorio() {
		return relatorio;
	}


	public void setRelatorio(RelatorioProjetoMonitoria relatorio) {
		this.relatorio = relatorio;
	}


	public ResumoSid getResumo() {
		return resumo;
	}


	public void setResumo(ResumoSid resumo) {
		this.resumo = resumo;
	}	
	
}
