/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/10/2006
 *
 */
package br.ufrn.sigaa.projetos.dominio;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * Essa classe é responsável por manter todas as restrições referentes a coodenação de projetos.
 */
@Embeddable
public class RestricaoCoordenacao implements Serializable {

	/** Informa o total de coordenações ativas que o docente pode ter para neste mesmo edital. Ex.: Um docente pode coordenar ao mesmo tempo até 2 projetos.*/
	@Column(name="max_coordenacoes_ativas", nullable = false)
	private Integer maxCoordenacoesAtivas;
	
	/** Permite que docentes coordenem projetos submetidos para este edital. */
	@Column(name="permitir_coordenador_docente", nullable = false)
	private boolean permitirCoordenadorDocente;
	
	/** Permite que técnicos administrativos coordenem projetos submetidos para este edital. */
	@Column(name="permitir_coordenador_tecnico", nullable = false)
	private boolean permitirCoordenadorTecnico;
	
	/** Permite que somente servidores (docentes ou técnicos) do quadro e em efetivo exercicio de suas funções possam coordenar projetos submetidos para este edital. */
	@Column(name="apenas_servidor_ativo_coordena", nullable = false)
	private boolean apenasServidorAtivoCoordena;
	
	/** Permite que somente servidores técnicos administrativos com nível superior coordenem projetos submetidos a este edital. */
	@Column(name="apenas_tecnico_superior_coordena", nullable = false)
	private boolean apenasTecnicoSuperiorCoordena;
	
	

	public Integer getMaxCoordenacoesAtivas() {
	    return maxCoordenacoesAtivas;
	}

	public void setMaxCoordenacoesAtivas(Integer maxCoordenacoesAtivas) {
	    this.maxCoordenacoesAtivas = maxCoordenacoesAtivas;
	}

	public boolean isPermitirCoordenadorDocente() {
	    return permitirCoordenadorDocente;
	}

	public void setPermitirCoordenadorDocente(boolean permitirCoordenadorDocente) {
	    this.permitirCoordenadorDocente = permitirCoordenadorDocente;
	}

	public boolean isPermitirCoordenadorTecnico() {
	    return permitirCoordenadorTecnico;
	}

	public void setPermitirCoordenadorTecnico(boolean permitirCoordenadorTecnico) {
	    this.permitirCoordenadorTecnico = permitirCoordenadorTecnico;
	}

	public boolean isApenasServidorAtivoCoordena() {
	    return apenasServidorAtivoCoordena;
	}

	public void setApenasServidorAtivoCoordena(boolean apenasServidorAtivoCoordena) {
	    this.apenasServidorAtivoCoordena = apenasServidorAtivoCoordena;
	}

	public boolean isApenasTecnicoSuperiorCoordena() {
	    return apenasTecnicoSuperiorCoordena;
	}

	public void setApenasTecnicoSuperiorCoordena(
		boolean apenasTecnicoSuperiorCoordena) {
	    this.apenasTecnicoSuperiorCoordena = apenasTecnicoSuperiorCoordena;
	}

}
