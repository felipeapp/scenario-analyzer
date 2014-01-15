/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 13/03/2008
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.negocio.calculos.CalculosDiscenteChainNode;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.ensino.stricto.negocio.calculos.AtualizarMesAtualPrazoConclusaoStrictoEmLote;

/**
 * Recalcular totais de integralização do discente stricto
 * @author David Pereira
 */
public class ProcessadorCalculosDiscenteStricto extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro m = (MovimentoCadastro) mov;
		DiscenteStricto ds =  (DiscenteStricto) m.getObjMovimentado();
		
		if (SigaaListaComando.CALCULAR_PRAZO_CONCLUSAO_DISCENTE_STRICTO_EM_LOTE.equals(mov.getCodMovimento())){			
			new CalculosDiscenteChainNode<DiscenteStricto>().setNext(new AtualizarMesAtualPrazoConclusaoStrictoEmLote()).executar(ds, mov, false);
		}else { 
			DiscenteStrictoCalculosHelper.realizarCalculosDiscenteChain(ds, mov);
		}
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}