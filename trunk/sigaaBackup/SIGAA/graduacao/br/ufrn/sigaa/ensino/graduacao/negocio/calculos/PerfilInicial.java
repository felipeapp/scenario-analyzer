/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '22/07/2010'
 *
 */

package br.ufrn.sigaa.ensino.graduacao.negocio.calculos;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;

/**
 * 
 * @author Henrique Andr�
 *
 */
public interface PerfilInicial {
	public Integer calcular(DiscenteAdapter d, Movimento mov) throws ArqException;
}
