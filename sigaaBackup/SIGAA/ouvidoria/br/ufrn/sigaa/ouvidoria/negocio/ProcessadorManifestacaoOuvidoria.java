package br.ufrn.sigaa.ouvidoria.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ouvidoria.dominio.Manifestacao;

/**
 * Processador para operações em manifestações feitas pela ouvidoria.
 * 
 * @author Bernardo
 *
 */
public class ProcessadorManifestacaoOuvidoria extends AbstractProcessador {

    @Override
    public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		
		if(mov.getCodMovimento().equals(SigaaListaComando.EDITAR_MANIFESTACAO)) {
		    editarManifestacao(mov);
		}
		
		return null;
    }

    /**
     * Edita as manifestações de acordo com os dados informados pela ouvidoria.
     * 
     * @param mov
     * @throws DAOException
     */
    private void editarManifestacao(Movimento mov) throws DAOException {
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		Manifestacao manifestacao = movimento.getObjMovimentado();
		
		getGenericDAO(mov).updateField(Manifestacao.class, manifestacao.getId(), "assuntoManifestacao", manifestacao.getAssuntoManifestacao());
    }

    @Override
    public void validate(Movimento mov) throws NegocioException, ArqException {
		ListaMensagens erros = new ListaMensagens();
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		Manifestacao manifestacao = movimento.getObjMovimentado();
		
		if(manifestacao.getAssuntoManifestacao() != null && isEmpty(manifestacao.getAssuntoManifestacao().getCategoriaAssuntoManifestacao()))
		    erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Categoria do Assunto");
		if(isEmpty(manifestacao.getAssuntoManifestacao()))
		    erros.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Assunto");
		
		checkValidation(erros);
    }

}
