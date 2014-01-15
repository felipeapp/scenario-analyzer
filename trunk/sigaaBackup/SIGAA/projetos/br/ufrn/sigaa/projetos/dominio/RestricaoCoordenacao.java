/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Essa classe � respons�vel por manter todas as restri��es referentes a coodena��o de projetos.
 */
@Embeddable
public class RestricaoCoordenacao implements Serializable {

	/** Informa o total de coordena��es ativas que o docente pode ter para neste mesmo edital. Ex.: Um docente pode coordenar ao mesmo tempo at� 2 projetos.*/
	@Column(name="max_coordenacoes_ativas", nullable = false)
	private Integer maxCoordenacoesAtivas;
	
	/** Permite que docentes coordenem projetos submetidos para este edital. */
	@Column(name="permitir_coordenador_docente", nullable = false)
	private boolean permitirCoordenadorDocente;
	
	/** Permite que t�cnicos administrativos coordenem projetos submetidos para este edital. */
	@Column(name="permitir_coordenador_tecnico", nullable = false)
	private boolean permitirCoordenadorTecnico;
	
	/** Permite que somente servidores (docentes ou t�cnicos) do quadro e em efetivo exercicio de suas fun��es possam coordenar projetos submetidos para este edital. */
	@Column(name="apenas_servidor_ativo_coordena", nullable = false)
	private boolean apenasServidorAtivoCoordena;
	
	/** Permite que somente servidores t�cnicos administrativos com n�vel superior coordenem projetos submetidos a este edital. */
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
