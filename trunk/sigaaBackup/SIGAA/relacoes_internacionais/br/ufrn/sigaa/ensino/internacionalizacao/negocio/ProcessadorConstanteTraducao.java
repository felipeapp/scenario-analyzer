/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 21/01/2013
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
import br.ufrn.sigaa.ensino.internacionalizacao.dao.ConstanteTraducaoDao;
import br.ufrn.sigaa.ensino.internacionalizacao.dominio.ConstanteTraducao;

/**
 * Processador responsável pelo cadastro e manutenção da internacionalização 
 * das constantes utilizadas nos documentos oficiais da Instituição.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessadorConstanteTraducao extends ProcessadorCadastro {
	
	@Override
	public Object execute(Movimento movimento) throws NegocioException,ArqException, RemoteException {
		
		validate(movimento);

		@SuppressWarnings("unchecked")
		List<ConstanteTraducao> listConstanteTraducao = (List<ConstanteTraducao>) ((MovimentoCadastro)movimento).getObjAuxiliar();
		ConstanteTraducaoDao dao = getDAO(ConstanteTraducaoDao.class, movimento);
		
		try{
			for (ConstanteTraducao elem : listConstanteTraducao) {
				if ( ValidatorUtil.isEmpty(elem.getValor()) )
					dao.remove(elem);
				else if (elem.getId() > 0)
					dao.update(elem);
				else
					dao.create(elem);
			}
		} finally{
			dao.close();
		}
		return movimento;
	}
	
}
