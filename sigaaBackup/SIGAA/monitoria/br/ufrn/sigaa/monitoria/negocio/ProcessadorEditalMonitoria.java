/**
 * 
 */
package br.ufrn.sigaa.monitoria.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.monitoria.dominio.EditalMonitoria;
import br.ufrn.sigaa.projetos.dominio.Edital;

/**
 * Processador para manipular publicar/editar editais de monitoria
 * 
 * @author Leonardo Campos
 * @author Ilueny Santos
 *
 */
public class ProcessadorEditalMonitoria extends AbstractProcessador {

    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
	checkRole(SigaaPapeis.GESTOR_MONITORIA, mov);
	validate(mov);
	EditalMonitoria editalMonitoria = ((MovimentoCadastro) mov).getObjMovimentado();

	GenericDAO dao = getGenericDAO(mov);
	try {
	    if(mov.getCodMovimento().equals(SigaaListaComando.PUBLICAR_EDITAL_MONITORIA)){        			
		dao.createOrUpdate(editalMonitoria.getEdital());
		dao.createOrUpdate(editalMonitoria);        			
	    } else if(mov.getCodMovimento().equals(SigaaListaComando.REMOVER_EDITAL_MONITORIA)){
		if(editalMonitoria.getIdArquivo() != null) {
		    EnvioArquivoHelper.removeArquivo(editalMonitoria.getIdArquivo());
		}        			
		dao.updateField(Edital.class, editalMonitoria.getEdital().getId(), "ativo", Boolean.FALSE);
	    }
	    return null;
	}finally {
	    dao.close();
	}
    }

    public void validate(Movimento mov) throws NegocioException, ArqException {
	ListaMensagens mensagens = new ListaMensagens();
	EditalMonitoria editalMonitoria = ((MovimentoCadastro) mov).getObjMovimentado();
	editalMonitoria.validate();
	checkValidation(mensagens);
    }

}
