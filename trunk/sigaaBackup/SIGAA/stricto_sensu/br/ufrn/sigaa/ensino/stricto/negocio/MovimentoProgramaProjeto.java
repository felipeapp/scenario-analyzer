/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 11/12/2008
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.stricto.dominio.ProgramaProjeto;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoPesquisa;

/** 
 * Movimento para realizar as operações sobre ProgramaProjeto
 * @author Victor Hugo
 */
public class MovimentoProgramaProjeto extends AbstractMovimentoAdapter {

	private Unidade programa;
	private List<ProjetoPesquisa> projetos = null;
	private List<ProgramaProjeto> programasProjeto = null;
	
	public MovimentoProgramaProjeto( Unidade programa, List<ProjetoPesquisa> projetos, List<ProgramaProjeto> programasProjeto ) {
		this.programa = programa;
		this.projetos = projetos;
		this.programasProjeto = programasProjeto; 
	}
	
	public List<ProjetoPesquisa> getProjetos() {
		return projetos;
	}
	public void setProjetos(List<ProjetoPesquisa> projetos) {
		this.projetos = projetos;
	}
	public List<ProgramaProjeto> getProgramasProjeto() {
		return programasProjeto;
	}
	public void setProgramasProjeto(List<ProgramaProjeto> programasProjeto) {
		this.programasProjeto = programasProjeto;
	}

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	
}
