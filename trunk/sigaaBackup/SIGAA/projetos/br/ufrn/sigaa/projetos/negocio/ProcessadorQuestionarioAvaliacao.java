package br.ufrn.sigaa.projetos.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.projetos.dominio.ItemAvaliacaoProjeto;
import br.ufrn.sigaa.projetos.dominio.QuestionarioAvaliacao;

/**
 * Processador com operações sobre questionários de avaliação de projetos.
 * 
 * @author Geyson
 *
 */
public class ProcessadorQuestionarioAvaliacao extends AbstractProcessador {

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		validate(mov);
		GenericDAO dao = getGenericDAO(mov);
		try {
			MovimentoCadastro cMov = (MovimentoCadastro) mov;
			QuestionarioAvaliacao quest = cMov.getObjMovimentado();
			
			if(mov.getCodMovimento().equals(SigaaListaComando.GRAVAR_QUESTIONARIO_ITENS)){
				
				//cria questionario
				dao.createOrUpdate(quest);
				
				
				//cria itens do questionário
				for (ItemAvaliacaoProjeto it : quest.getItensAvaliacao()) {
					dao.createOrUpdate(it);
				}
				//no caso remoçao de algum item ja cadastrado anteriomente
				Collection<ItemAvaliacaoProjeto> listaItens = dao.findAtivosByExactField(ItemAvaliacaoProjeto.class, "questionario.id", quest.getId());
				if(listaItens != null){
					for (ItemAvaliacaoProjeto itemAvaliacaoProjeto : listaItens) {
						if(!quest.getItensAvaliacao().contains(itemAvaliacaoProjeto)){
							dao.updateField(ItemAvaliacaoProjeto.class, itemAvaliacaoProjeto.getId(), "ativo", false);
						}
						
					}
				}
				
			}
			if(mov.getCodMovimento().equals(SigaaListaComando.REMOVER_QUESTIONARIO_ITENS)){
				dao.updateField(QuestionarioAvaliacao.class, quest.getId(), "ativo", false);
				
				for (ItemAvaliacaoProjeto it : quest.getItensAvaliacao()) {
					dao.updateField(ItemAvaliacaoProjeto.class, it.getId(), "ativo", false);
				}
				
			}
		} finally {
			dao.close();
		}
		
		return null;
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[]{SigaaPapeis.MEMBRO_COMITE_INTEGRADO, SigaaPapeis.GESTOR_PESQUISA}, mov);
		
		Usuario usuario = (Usuario) mov.getUsuarioLogado();
		// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
			throw new NegocioException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
		}
	}

}
