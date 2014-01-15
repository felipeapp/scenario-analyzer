/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 24/03/2009
 *
 */	
package br.ufrn.sigaa.assistencia.negocio;

import java.util.List;

import br.ufrn.arq.dominio.AbstractMovimentoAdapter;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.CalendarioBolsaAuxilio;

/**
 * Movimento para Movimento CalendarioBolsaAuxilio
 * 
 * @author agostinho
 *
 */
public class MovimentoPeriodoResultados extends AbstractMovimentoAdapter {

	private List<CalendarioBolsaAuxilio> confPeriodoResultadosList;

	private AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE;
	
	public AnoPeriodoReferenciaSAE getAnoPeriodoReferenciaSAE() {
        return anoPeriodoReferenciaSAE;
    }

    public void setAnoPeriodoReferenciaSAE(
            AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE) {
        this.anoPeriodoReferenciaSAE = anoPeriodoReferenciaSAE;
    }

    public List<CalendarioBolsaAuxilio> getConfPeriodoResultadosList() {
		return confPeriodoResultadosList;
	}

	public void setConfPeriodoResultadosList(List<CalendarioBolsaAuxilio> confPeriodoResultadosList) {
		this.confPeriodoResultadosList = confPeriodoResultadosList;
	}

}
