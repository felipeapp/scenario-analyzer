package br.ufrn.sigaa.ouvidoria.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Processador para cadastro de manifestações feitas por um servidor.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorManifestacaoServidor extends AbstractProcessadorManifestacao {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MANIFESTACAO_SERVIDOR)) {
		    cadastrarManifestacao(mov);
		    
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
			Manifestacao manifestacao = movimento.getObjMovimentado();
			
		    notificarCriacaoManifestacao(manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getPessoa(), manifestacao, mov);
		}
		
		return null;
    }

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		Manifestacao manifestacao = movimento.getObjMovimentado();
		
		checkValidation(manifestacao.validateCompleto(false));
    }

}
