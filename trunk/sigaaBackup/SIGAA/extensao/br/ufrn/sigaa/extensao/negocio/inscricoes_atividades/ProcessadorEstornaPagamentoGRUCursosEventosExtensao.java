/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p>Processador com as regras de estorno de um pagamento de curso ou evento de extens�o </p>
 *
 * <p> <i> Inscri��es APROVADAS n�o podem ter seu pagamento estornado. </i> </p>
 * 
 *  <p> <i> No estorno de uma inscri��o, devem ser guardados as informa��o de auditoria. </i> </p>
 *
 * 
 * @author jadson
 *
 */
public class ProcessadorEstornaPagamentoGRUCursosEventosExtensao extends ProcessadorCadastro{

	/**
	 * Ver coment�rio na tarefa pai
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
	 * Ver coment�rio na tarefa pai
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
		ListaMensagens erros = new ListaMensagens();
		
		MovimentoEstornaPagamentoGRUCursosEventosExtensao movi = (MovimentoEstornaPagamentoGRUCursosEventosExtensao) mov;
		
		List<InscricaoAtividadeParticipante> inscricoesEstornoPamento  = movi.getInscricoesConfirmarEstornoPagamento();
		
		for (InscricaoAtividadeParticipante inscricaoAtividadeParticipante : inscricoesEstornoPamento) {
			
			
			if(inscricaoAtividadeParticipante.getStatusInscricao().getId() == StatusInscricaoParticipante.APROVADO  ){
				erros.addErro(" N�o � poss�vel estornar o pagamento da inscri��o do participante: \""
						+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
						+"\", pois a inscri��o dele est� aprovada, para estornar o pagamento � preciso recusar a inscri��o.");
				
			}
			
			if(inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_AUTOMATICAMENTE
					&& inscricaoAtividadeParticipante.getStatusPagamento() != StatusPagamentoInscricao.CONFIRMADO_MANUALMENTE ){
				erros.addErro(" N�o � poss�vel estornar o pagamento da inscri��o do participante: \""
						+inscricaoAtividadeParticipante.getCadastroParticipante().getNome()
						+"\", pois o pagamento n�o foi confirmado.");
				
			}
		}
		
		checkValidation(erros);
		
	}
}
