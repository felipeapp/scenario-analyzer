/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 21/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoAtividadeParticipanteDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;

/**
 *
 * <p>Processador com as regras de estorno de um pagamento de curso ou evento de extensão </p>
 *
 * <p> <i> Inscrições APROVADAS não podem ter seu pagamento estornado. </i> </p>
 * 
 *  <p> <i> No estorno de uma inscrição, devem ser guardados as informação de auditoria. </i> </p>
 *
 * 
 * @author jadson
 *
 */
public class ProcessadorEstornaPagamentoGRUCursosEventosExtensao extends ProcessadorCadastro{

	/**
	 * Ver comentário na tarefa pai
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		
		InscricaoAtividadeParticipanteDao dao = null;
		
		try {
			
			MovimentoEstornaPagamentoGRUCursosEventosExtensao movi = (MovimentoEstornaPagamentoGRUCursosEventosExtensao) movimento;
			
			dao = getDAO(InscricaoAtividadeParticipanteDao.class, movi);
			
			List<Integer> idsInscricoesParticipante = new ArrayList<Integer>();
			
			for( InscricaoAtividadeParticipante inscricao : movi.getInscricoesConfirmarEstornoPagamento()){
				idsInscricoesParticipante.add(inscricao.getId());
			}
			
			if(idsInscricoesParticipante.size() > 0 ) {
				
				////////// estorna o pagamento, guardando que foi que estornou ///////////////
				dao.update(" UPDATE extensao.inscricao_atividade_participante SET status_pagamento = "+StatusPagamentoInscricao.ESTORNADO
						+", id_registro_estorno = ?, data_estorno = ? "
						+" WHERE ( status_pagamento = "+StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
						+" OR status_pagamento = "+StatusPagamentoInscricao.CONFIRMADO_MANUALMENTE +" )"
						+" AND id_inscricao_atividade_participante in  "+ UFRNUtils.gerarStringIn(idsInscricoesParticipante)+"  "
						, movi.getUsuarioLogado().getRegistroEntrada().getId(), new Date() );
			}
			
			return null;
			
		}finally{
			if(dao != null) dao.close();
		}
	}

	/**
	 * Ver comentário na tarefa pai
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoEstornaPagamentoGRUCursosEventosExtensao movi = (MovimentoEstornaPagamentoGRUCursosEventosExtensao) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesEstornoPamento  = movi.getInscricoesConfirmarEstornoPagamento();
		
		for (InscricaoAtividadeParticipante inscricaoAtividadeParticipante : inscricoesEstornoPamento) {
			
			
			if(inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.APROVADO  ){
				erros.addErro(" Não é possível estornar o pagamento da inscrição do participante: \""
						+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
						+"\", pois a inscrição dele está aprovada, para estornar o pagamento é preciso recusar a inscrição.");
				
			}
			
			if(inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
					&& inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_MANUALMENTE ){
				erros.addErro(" Não é possível estornar o pagamento da inscrição do participante: \""
						+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
						+"\", pois o pagamento não foi confirmado.");
				
			}
		}
		
		checkValidation(erros);
		
	}
}
