package br.ufrn.sigaa.ouvidoria.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.InteressadoNaoAutenticado;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * Processador para cadastro de manifestações feitas pela comunidade externa.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorManifestacaoComunidadeExterna extends AbstractProcessadorManifestacao {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MANIFESTACAO_COMUNIDADE_EXTERNA)) {
		    cadastrarManifestacao(mov);
		    
		    MovimentoCadastro movimento = (MovimentoCadastro) mov;
			Manifestacao manifestacao = movimento.getObjMovimentado();
			
			InteressadoNaoAutenticado interessadoNaoAutenticado = manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
			
			Pessoa p = new Pessoa();
			p.setNome(interessadoNaoAutenticado.getNome());
			p.setEmail(interessadoNaoAutenticado.getEmail());
			
		    notificarCriacaoManifestacao(p, manifestacao, mov);
		}
		
		return null;
    }

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		Manifestacao manifestacao = movimento.getObjMovimentado();
		
		checkValidation(manifestacao.validateCompleto(true));
		
		InteressadoNaoAutenticado interessadoNaoAutenticado = manifestacao.getInteressadoManifestacao().getDadosInteressadoManifestacao().getInteressadoNaoAutenticado();
		    
		interessadoNaoAutenticado.validarEndereco();
    }

}
