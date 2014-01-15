/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on '23/08/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import java.util.Date;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * Implementação do cálculo do prazo máximo para discentes de pós graduação
 * 
 * @author Henrique André
 *
 */
public class CalculoPrazoMaximoStricto implements CalculoPrazoMaximo<Date> {

	@Override
	public Date calcular(DiscenteAdapter discente, Movimento mov) throws ArqException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public char getNivel() {
		return NivelEnsino.STRICTO;
	}

}
