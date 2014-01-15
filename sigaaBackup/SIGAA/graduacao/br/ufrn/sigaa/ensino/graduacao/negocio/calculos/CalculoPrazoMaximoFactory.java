/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '23/08/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Date;

import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.parametros.dominio.ParametrosGraduacao;

/**
 * Retorna a implementa��o adequada do c�lculo do prazo m�ximo
 * 
 * @author henrique
 *
 */
public class CalculoPrazoMaximoFactory {
	
	public static CalculoPrazoMaximo<Integer> getCalculoGraduacao(DiscenteAdapter discente) throws NegocioException {

		if (isEmpty(discente.getAnoIngresso()))
			throw new NegocioException("O discente n�o possui ano de ingresso.");
		
		if (isEmpty(discente.getPeriodoIngresso()))
			throw new NegocioException("O discente n�o possui per�odo de ingresso.");
		
		int anoPeriodoDiscente = new Integer (discente.getAnoIngresso() + "" + discente.getPeriodoIngresso());
		
		if (anoPeriodoDiscente >= ParametroHelper.getInstance().getParametroInt(ParametrosGraduacao.ANO_PERIODO_PERFIL_INICIAL_DIMINUINDO_PRAZO_MAXIMO))
			return new CalculoPrazoMaximoGraduacaoNovoRegulamento();
		
		return new CalculoPrazoMaximoGraduacaoAntigo();
	}
	
	//TODO: implementar a vers�o stricto
	public static CalculoPrazoMaximo<Date> getCalculoStricto(DiscenteAdapter discente) throws NegocioException {
		return null;
	}	
	
}
