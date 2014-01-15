/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 19/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoParticipanteSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.CadastroParticipanteAtividadeExtensao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;

/**
 * <p> Contém as regras de negócio para cancelar a inscrição de um participante.</p>
 *
 * <p> <i> - Caso a inscrição já tenha sido aprovada, tem que remover o participante criado na ação de extensão. <br/><br/>
 *         - Caso a inscrição seja em uma atividade, tem que cancelar também todas as inscrições 
 *         nas mini atividades que por acaso tenham sido feitas. <br/><br/>
 *     </i>
 * </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCancelaInscricaoParticipante  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		InscricaoAtividadeParticipante inscricao =  movi.getObjMovimentado();
		
		CadastroParticipanteAtividadeExtensao participanteLogado = (CadastroParticipanteAtividadeExtensao) movi.getObjAuxiliar(); 
		
		InscricaoParticipanteSubAtividadeExtensaoDao dao =  null;
		
		// Para cancelar a inscrição de um participante é só alterar seu status para cancelado //
		
		try{
			dao = getDAO(InscricaoParticipanteSubAtividadeExtensaoDao.class, movi);
			
			// Cancela a inscrição passada //
			cancelaInscricaoParticipante(dao, inscricao);
			
			// se for uma inscrição em atividade, cancela as inscrição nas mini atividade dessa atividade também //
			if(inscricao.getInscricaoAtividade().isInscricaoAtividade() ){
				
				List<InscricaoAtividadeParticipante> inscricoesSubAtividade = 
							dao.findInscricoesParticipanteSubAtividadesAtivasDaAtivade(inscricao.getInscricaoAtividade().getAtividade().getId(), participanteLogado.getId());
				
				for (InscricaoAtividadeParticipante inscricaoAtividadeParticipante : inscricoesSubAtividade) {
					
					cancelaInscricaoParticipante(dao, inscricaoAtividadeParticipante);
				}
			}
			
		}finally{
			if(dao != null) dao.close();
		}
		
		return null;
	}

	/**
	 * Cancela a inscrição de um participipante.
	 *  
	 *  <br/>
	 *  Método chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>Método não chamado por nenhuma página jsp.</p>
	 *
	 * @param dao
	 * @param inscricaoAtividadeParticipante
	 * @throws DAOException
	 */
	private void cancelaInscricaoParticipante(InscricaoParticipanteSubAtividadeExtensaoDao dao, InscricaoAtividadeParticipante inscricaoParticipante) throws DAOException {
		
		///// Cancelar a Inscrição do participante /////
		dao.updateField(InscricaoAtividadeParticipante.class, inscricaoParticipante.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);
		
		// MUITO IMPORTANTE: Se ele já foi aceito pelo coordenador remove o participante que foi criado para ele//
		if(inscricaoParticipante.getParticipanteExtensao() != null ){
			dao.updateField(ParticipanteAcaoExtensao .class, inscricaoParticipante.getParticipanteExtensao().getId() , "ativo", false);
		}
	}

	
	
	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		InscricaoAtividadeParticipante inscricao =  movi.getObjMovimentado();
	
		if(inscricao.getStatusInscricao().isStatusCancelado() || inscricao.getStatusInscricao().isStatusRecusado() ){
			erros.addErro("Essa inscrição já foi cancelada.");
		}
		
		checkValidation(erros);
	}

}
