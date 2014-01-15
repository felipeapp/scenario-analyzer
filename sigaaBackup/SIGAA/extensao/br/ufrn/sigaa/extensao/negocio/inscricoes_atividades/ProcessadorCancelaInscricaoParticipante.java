/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Cont�m as regras de neg�cio para cancelar a inscri��o de um participante.</p>
 *
 * <p> <i> - Caso a inscri��o j� tenha sido aprovada, tem que remover o participante criado na a��o de extens�o. <br/><br/>
 *         - Caso a inscri��o seja em uma atividade, tem que cancelar tamb�m todas as inscri��es 
 *         nas mini atividades que por acaso tenham sido feitas. <br/><br/>
 *     </i>
 * </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorCancelaInscricaoParticipante  extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
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
		
		// Para cancelar a inscri��o de um participante � s� alterar seu status para cancelado //
		
		try{
			dao = getDAO(InscricaoParticipanteSubAtividadeExtensaoDao.class, movi);
			
			// Cancela a inscri��o passada //
			cancelaInscricaoParticipante(dao, inscricao);
			
			// se for uma inscri��o em atividade, cancela as inscri��o nas mini atividade dessa atividade tamb�m //
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
	 * Cancela a inscri��o de um participipante.
	 *  
	 *  <br/>
	 *  M�todo chamado pela(s) seguinte(s) JSP(s):
	 *   <ul>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *    <li>/sigaa.war/biblioteca/</li>
	 *   </ul>
	 *
	 *   <p>M�todo n�o chamado por nenhuma p�gina jsp.</p>
	 *
	 * @param dao
	 * @param inscricaoAtividadeParticipante
	 * @throws DAOException
	 */
	private void cancelaInscricaoParticipante(InscricaoParticipanteSubAtividadeExtensaoDao dao, InscricaoAtividadeParticipante inscricaoParticipante) throws DAOException {
		
		///// Cancelar a Inscri��o do participante /////
		dao.updateField(InscricaoAtividadeParticipante.class, inscricaoParticipante.getId(), "statusInscricao", StatusInscricaoParticipante.CANCELADO);
		
		// MUITO IMPORTANTE: Se ele j� foi aceito pelo coordenador remove o participante que foi criado para ele//
		if(inscricaoParticipante.getParticipanteExtensao() != null ){
			dao.updateField(ParticipanteAcaoExtensao .class, inscricaoParticipante.getParticipanteExtensao().getId() , "ativo", false);
		}
	}

	
	
	/**
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		InscricaoAtividadeParticipante inscricao =  movi.getObjMovimentado();
	
		if(inscricao.getStatusInscricao().isStatusCancelado() || inscricao.getStatusInscricao().isStatusRecusado() ){
			erros.addErro("Essa inscri��o j� foi cancelada.");
		}
		
		checkValidation(erros);
	}

}
