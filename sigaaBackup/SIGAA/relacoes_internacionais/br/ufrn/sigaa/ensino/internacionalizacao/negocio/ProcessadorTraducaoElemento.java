/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 17/07/2012
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.internacionalizacao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.internacionalizacao.dao.TraducaoElementoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.TraducaoElemento;

/**
 * Processador responsável pelo cadastro e manutenção da internacionalização 
 * dos elementos pertencentes aos documentos oficiais da Instituição.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorTraducaoElemento extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException,ArqException, RemoteException {
		
		validate(movimento);

		@SuppressWarnings("unchecked")
		List<TraducaoElemento> listElementosComTraducao = (List<TraducaoElemento>) ((MovimentoCadastro)movimento).getObjAuxiliar();
		TraducaoElementoDao dao = getDAO(TraducaoElementoDao.class, movimento);
		
		try{
			if ( movimento.getCodMovimento().equals(SigaaListaComando.TRADUZIR_ELEMENTO) ){
				for (TraducaoElemento elem : listElementosComTraducao) {
					if ( ValidatorUtil.isEmpty(elem.getValor()) )
						dao.remove(elem);
					else if (elem.getId() > 0)
						dao.update(elem);
					else
						dao.create(elem);
				}
			}
		} finally{
			dao.close();
		}
		return movimento;
	}
	
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		super.validate(mov);
	}
	
}
