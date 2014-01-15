/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 06/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.extensao.ParticipanteAcaoExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 *
 * <p>Processador Com as regras para remover um participante de uma atividade de extensão. 
 *   Ele tendo feito inscrição ou não. </p>
 *
 * <p> <i> Caso um participante esteja inscrito nas mini atividades dessa atividade, ele será removido também de lá.</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRemoveParticipanteAtividadeExtensaoCoordenador extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);

		MovimentoCadastro  movi = (MovimentoCadastro) mov;
		
		ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) movi.getObjMovimentado();
		
		ParticipanteAcaoExtensaoDao dao = null;
		
		try{
			dao = getDAO(ParticipanteAcaoExtensaoDao.class, mov);
			
			List<ParticipanteAcaoExtensao> participantesMiniAtividade = dao.findParticipantesMiniAtividadesDaAtividade(
							participante.getCadastroParticipante().getId(), participante.getAtividadeExtensao().getId());
			
			dao.updateField(ParticipanteAcaoExtensao.class, participante.getId(), "ativo", false);
			
			// Remove os participante nas mini atividade também, poís não pode ficar      //
			// inscrito apenas na mini atividade sem está na atividade principal          //
			for (ParticipanteAcaoExtensao participanteMiniAtividade : participantesMiniAtividade) {
				dao.updateField(ParticipanteAcaoExtensao.class, participanteMiniAtividade.getId(), "ativo", false);
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
		// sem validação por enquanto
	}

}
