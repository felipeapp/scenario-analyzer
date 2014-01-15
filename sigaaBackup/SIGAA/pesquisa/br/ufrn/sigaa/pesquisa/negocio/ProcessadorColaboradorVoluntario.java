/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 02/04/2008
 *
 */

package br.ufrn.sigaa.pesquisa.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.sigaa.arq.dao.pesquisa.ColaboradorVoluntarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ColaboradorVoluntario;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Processador para efetuar o cadastro colaboradores voluntários de pesquisa
 * 
 * @author Leonardo
 *
 */
public class ProcessadorColaboradorVoluntario extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException,
			ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_COLABORADOR_VOLUNTARIO)){
			obj = criar(mov);
		}
		
		return obj;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ColaboradorVoluntarioDao dao = getDAO(ColaboradorVoluntarioDao.class, mov);
		ColaboradorVoluntario colaboradorMov = (ColaboradorVoluntario) ((MovimentoCadastro)mov).getObjMovimentado();
		
		// verifica se o servidor é inativo
		Servidor servidor = dao.findByPrimaryKey(colaboradorMov.getServidor().getId(), Servidor.class);
		if( servidor.getAtivo().getId() != Ativo.APOSENTADO )
			throw new NegocioException("Para ser um colaborador voluntário o servidor deve estar inativo.");
		
		// verifica se já não foi cadastrado como colaborador voluntário
		if(dao.isColaboradorVoluntario(servidor))
			throw new NegocioException("O servidor já está cadastrado como um colaborador voluntário.");
	}
}
