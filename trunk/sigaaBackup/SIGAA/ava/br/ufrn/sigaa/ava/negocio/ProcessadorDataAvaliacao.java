package br.ufrn.sigaa.ava.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SolicitacaoReposicaoAvaliacao;

/**
 * Processador para remover data de avaliação 
 * 
 * @author Victor Hugo
 *
 */
public class ProcessadorDataAvaliacao extends ProcessadorCadastroAva {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastroAva mov = (MovimentoCadastroAva) movimento;
		DataAvaliacao data = mov.getObjMovimentado();
		TurmaVirtualDao dao = getDAO(TurmaVirtualDao.class, mov);
		
		if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_DATA_AVALIACAO)) {
			
			/** se a avaliação em questão possui solicitação de reposição então é necessário remove-las antes. */
			Collection<SolicitacaoReposicaoAvaliacao> solicitacoes = dao.findByExactField(SolicitacaoReposicaoAvaliacao.class, "dataAvaliacao.id", data.getId());
			if( isNotEmpty(solicitacoes) ){
				for( SolicitacaoReposicaoAvaliacao sra : solicitacoes ){
					dao.remove(sra);
				}
			}
			
			dao.updateField(DataAvaliacao.class, data.getId(), "ativo", false);
		}
		
		return null;
	}

}