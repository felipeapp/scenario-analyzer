/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 20/08/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.SubAtividadeExtensao;
import br.ufrn.sigaa.extensao.negocio.helper.SubAtividadeExtensaoHelper;

/**
 *
 * <p>Processador que contém as regras para remover uma sub atividade (também conhecida como mini atividade) 
 * de extensão depois que a ação estiver em execução, nesse caso podem existir inscrições nessa mini atividade 
 * e as inscrições realizadas precisam ser desatividadas.</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoverSubAtividadeEmExecucao  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		SubAtividadeExtensao subAtividade  = (SubAtividadeExtensao) movimento.getObjMovimentado();
		
		@SuppressWarnings("unchecked")
		List<InscricaoAtividadeParticipante> participantes = (List<InscricaoAtividadeParticipante> ) movimento.getColObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			
			dao = getGenericDAO(movimento);
		
			dao.updateField(SubAtividadeExtensao.class, subAtividade.getId(), "ativo", false); // remove a mini atividade

		
			if(participantes != null){
				for (InscricaoAtividadeParticipante participante : participantes) {
					dao.updateField(InscricaoAtividadeParticipante.class, participante.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);               // cancelando a inscrição do participante  removida
					dao.updateField(InscricaoAtividade.class, participante.getInscricaoAtividade().getId(), "ativo", false);   // desativa a inscrição da mini atividade removida
				}
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		SubAtividadeExtensao subAtividade  = (SubAtividadeExtensao) movimento.getObjMovimentado();
		

		if ( ! SubAtividadeExtensaoHelper.isCoordenadorMiniAtividade(subAtividade, movimento.getUsuarioLogado()) ){
			throw new NegocioException(
				SubAtividadeExtensaoHelper.monstaMensagensErroAlteracaoMiniAtividade(subAtividade, movimento.getUsuarioLogado())
			);
		}
		
	}

}
