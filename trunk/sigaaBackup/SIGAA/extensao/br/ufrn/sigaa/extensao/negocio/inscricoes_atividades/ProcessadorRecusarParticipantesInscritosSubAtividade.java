/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/11/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.extensao.dao.inscricoes_atividades.InscricaoSubAtividadeExtensaoDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;

/**
 *
 * <p>Contém as regras para recusar uma inscrição de um participante de uma mini atividades de extensão.</p>
 * 
 *  <p> A recusa de uma inscrição em uma mini atividade é simple, simplemente muda o status!!!</p>
 * 
 * @author jadson
 *
 */
public class ProcessadorRecusarParticipantesInscritosSubAtividade  extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		validate(mov);
		
		MovimentoRecusarParticipantesInscritosSubAtividade movi = (MovimentoRecusarParticipantesInscritosSubAtividade) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesSelecionadas = movi.getInscricoesSelecionadas();
		
		InscricaoSubAtividadeExtensaoDao dao = null;
			
		try{
			dao = getDAO(InscricaoSubAtividadeExtensaoDao.class, movi);
			
			List<Integer> idsInscricoesRecusadas = new ArrayList<Integer>();
			
			for (InscricaoAtividadeParticipante inscricaoParticipante : inscricoesSelecionadas) {
				idsInscricoesRecusadas.add( inscricaoParticipante.getId() );
			}
			
			// Recusa TODAS as inscrições ds atividades selecionadas com as inscrições nas sub atividades //
			getGenericDAO(movi).update(" UPDATE extensao.inscricao_atividade_participante set id_status_inscricao_participante = ? " +
					" where id_inscricao_atividade_participante in "
					+UFRNUtils.gerarStringIn(idsInscricoesRecusadas), StatusInscricaoParticipante.RECUSADO);
		
			
			/// Se recursar as inscrições podem ser que elas esteja aprovadas e tenham participantes, então desativa esses participantes ////
			
			getGenericDAO(movi).update(" UPDATE extensao.participante_acao_extensao set ativo = false " +
					" where id_inscricao_atividade_participante in "+UFRNUtils.gerarStringIn(idsInscricoesRecusadas) );
			
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
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoRecusarParticipantesInscritosSubAtividade movi = (MovimentoRecusarParticipantesInscritosSubAtividade) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesSelecioandas = movi.getInscricoesSelecionadas();
		
		try{
			// Para cadas inscrição do participante, verifica se ela pode ser recursadas
			for (InscricaoAtividadeParticipante inscricaoAtividadeParticipante : inscricoesSelecioandas) {
				
				if(inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.CANCELADO 
						|| inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.RECUSADO ){
					erros.addErro(" Não é possível recusar a inscrição do participante: \""
							+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
							+"\", pois a inscrição dele já foi cancelada.");
					
				}
			
			}
			
		}finally{
			checkValidation(erros);
		}
		
	}

}
