/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '12/11/2007'
 * 
 */
package br.ufrn.sigaa.ensino.latosensu.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.ProcessoSeletivoDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.RelatoriosLatoDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.ensino.latosensu.relatorios.LinhaAcompanhamentoCursoLato;

/**
 * Managed Bean do portal do coordenador lato sensu
 * @author leonardo
 *
 */

@Component("portalCoordenadorLato") @Scope("session")
public class PortalCoordenadorLatoMBean extends SigaaAbstractController<Object> {

	/** Define a situação da turma selecionada. */
	private String situacaoTurma;

	/** Define o curso selecionado. */
	private Curso curso;
	
	/** Define a lista de acompanhamento do curso selecionado. */
	private Collection<LinhaAcompanhamentoCursoLato> acompanhamentoCurso;
	
	/** Processos seletivos cadastrados para o curso. */
	private Collection<ProcessoSeletivo> processos;
	
	/** Construtor */
	public PortalCoordenadorLatoMBean() throws DAOException{
	}
	
	/**
	 * Retorna uma coleção de acompanhamento pelo coordenador das disciplinas que possuem ou não turmas criadas para estas.
	 * <br>
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 *	<ul>
	 *		<li>sigaa.war/lato/coordenador.jsp</li>
	 *	</ul>
	 * @return
	 * @throws DAOException
	 */
	public Collection<LinhaAcompanhamentoCursoLato> getAcompanhamentoCurso() throws DAOException { 
		if (acompanhamentoCurso == null || acompanhamentoCurso.isEmpty())
			acompanhamentoCurso = getDAO(RelatoriosLatoDao.class).findAcompanhamentoCursoLato(getCursoAtualCoordenacao());
		return acompanhamentoCurso;
	}
	
	/**
	 * Retorna o processo seletivo mais recente do curso atual.
	 * @return
	 * @throws DAOException
	 */
	public ProcessoSeletivo getProcessoSeletivoMaisRecente() throws DAOException {
		if(isEmpty(processos)) {
			processos = getDAO(ProcessoSeletivoDao.class).findByCursoLato(getCursoAtualCoordenacao().getId());
			Collections.sort( (List<ProcessoSeletivo>) processos, new Comparator<ProcessoSeletivo>(){
				public int compare(ProcessoSeletivo p1,	ProcessoSeletivo p2) {						
					return p2.getEditalProcessoSeletivo().getInicioInscricoes().
						compareTo(p1.getEditalProcessoSeletivo().getInicioInscricoes()); 					
				}
			});
		}
		return !isEmpty(processos) ? processos.iterator().next() : new ProcessoSeletivo();
	}
	
	public void setAcompanhamentoCurso(
			Collection<LinhaAcompanhamentoCursoLato> acompanhamentoCurso) {
		this.acompanhamentoCurso = acompanhamentoCurso;
	}

	public String getSituacaoTurma() {
		return situacaoTurma;
	}

	public void setSituacaoTurma(String situacaoTurma) {
		this.situacaoTurma = situacaoTurma;
	}

    public Curso getCurso() {
        return curso;
    }

    public void setCurso(Curso curso) {
        this.curso = curso;
    }

}
