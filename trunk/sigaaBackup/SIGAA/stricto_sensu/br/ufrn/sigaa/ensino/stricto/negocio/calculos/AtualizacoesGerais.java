/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 12/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio.calculos;

import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;

/**
 * Atualizações gerais, que não couberam nas demais classes
 * da cadeia de cálculos de discentes stricto.
 * 
 * @author David Pereira
 *
 */
public class AtualizacoesGerais extends CalculosDiscenteChainNode<DiscenteStricto> {

	@Override
	public void processar(DiscenteStricto d, Movimento mov, boolean preProcessamento) throws ArqException, NegocioException {
		DiscenteStrictoDao dao = getDAO(DiscenteStrictoDao.class, mov);
		try {
			dao.updateField(DiscenteStricto.class, d.getId(), "ultimaAtualizacaoTotais", new Date());
		} finally {
			dao.close();
		}
	}
	
}
