/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 10/10/2007
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.pesquisa.CongressoIniciacaoCientificaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.pesquisa.dominio.AvaliadorCIC;
import br.ufrn.sigaa.pesquisa.dominio.CongressoIniciacaoCientifica;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador para efetuar o cadastro dos avaliadores do CIC.
 * 
 * @author Leonardo Campos
 *
 */
public class ProcessadorAvaliadorCIC extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_AVALIADOR_CIC)){
			obj = criar(mov);
		}
		
		return obj;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		AvaliadorCIC obj = (AvaliadorCIC) ((MovimentoCadastro)mov).getObjMovimentado();
		CongressoIniciacaoCientificaDao dao = getDAO(CongressoIniciacaoCientificaDao.class, mov);
		try{
			
			if(!dao.isAvaliador(obj.getCongresso(), obj.getDocente(), obj.getDiscente() , obj.getArea())){
				throw new NegocioException("O discente/docente informado j� est� cadastrado como Avaliador desse congresso.");
			}
		}finally{
			dao.close();
		}
		
	}
}
