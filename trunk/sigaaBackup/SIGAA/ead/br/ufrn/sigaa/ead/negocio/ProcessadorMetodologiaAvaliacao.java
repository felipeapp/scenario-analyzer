package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ead.dominio.MetodologiaAvaliacao;

/**
 * Processador utilizado para inativar uma metodologia de avaliação.
 * 
 * @author bernardo
 *
 */
public class ProcessadorMetodologiaAvaliacao extends AbstractProcessador {

    @Override
    public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
    	MovimentoCadastro mov = (MovimentoCadastro) movimento;
    	validate(mov);
    	
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_METODOLOGIA_AVALIACAO)) 
		    cadastrar(mov);
		else if (mov.getCodMovimento().equals(SigaaListaComando.INATIVAR_METODOLOGIA_AVALIACAO)) 
		    inativar(mov);
		
		return null;
    }

    /**
     * Cadastra uma nova metodologia
     * @param mov
     * @throws DAOException
     */
    private void cadastrar(MovimentoCadastro mov) throws DAOException {
    	
    	MetodologiaAvaliacao obj = mov.getObjMovimentado();
    	
		getGenericDAO(mov).create(obj);
	}

	/**
     * Efetua a inativação de uma metodologia de avaliação.
     * 
     * @param mov
     * @return
     * @throws DAOException 
     */
    private void inativar(MovimentoCadastro mov) throws DAOException {
    	
    	MetodologiaAvaliacao obj = mov.getObjMovimentado();
    	
    	obj.setAtiva(false);
    	obj.setDataInativacao(new Date());
    	
    	GenericDAO dao = getGenericDAO(mov);
    	try{
    		dao.update(mov.getObjMovimentado());
    	}finally{
    		if(dao != null) dao.close();
    	}
    }
    

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		
    }

}
