package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.extensao.AtividadeExtensaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.extensao.dominio.AtividadeExtensao;
import br.ufrn.sigaa.negocio.ProjetoHelper;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

public class ProcessadorEncerrarAcoesEmExecucao extends AbstractProcessador {

	/**
	 * Encerra açôes que tenho data fim maior do que prazo determinado pela PROEX.
	 */
	@Override
	@SuppressWarnings("unchecked")
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		if(mov.getCodMovimento().equals(SigaaListaComando.ENCERRAR_ACOES_COM_DATA_FIM_MAIOR_QUE_PRAZO)){
			validate(mov);
			Collection<AtividadeExtensao> lista = (Collection<AtividadeExtensao>) ((MovimentoCadastro) mov).getColObjMovimentado();
			AtividadeExtensaoDao dao = getDAO(AtividadeExtensaoDao.class, mov);
			try{
				for (AtividadeExtensao atividade : lista) {
					atividade.setSituacaoProjeto(new TipoSituacaoProjeto(TipoSituacaoProjeto.EXTENSAO_PENDENTE_DE_RELATORIO));
					dao.initialize(atividade.getSituacaoProjeto());
					dao.updateField(AtividadeExtensao.class, atividade.getId(), "situacaoProjeto.id", TipoSituacaoProjeto.EXTENSAO_PENDENTE_DE_RELATORIO);
					ProjetoHelper.gravarHistoricoSituacaoProjeto(atividade.getSituacaoProjeto().getId(), atividade.getProjeto().getId(), null);
					ProjetoHelper.sincronizarSituacaoComProjetoBase(dao, atividade);
				}
			}
			finally{
				dao.close();
			}
		}
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
	}

}
