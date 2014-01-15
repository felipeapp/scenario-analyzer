/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 07/05/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.extensao.InscricaoAtividadeParticipanteDao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.extensao.negocio.inscricoes_atividades.MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente;

/**
 * <p> Confirma o pagamento dos cursos e eventos manualmente mudando o status do pagamento. </p>
 *
 * @author jadson
 *
 */
public class ProcessadorConfirmaPagamentoGRUAtividadesExtensaoManualmente extends ProcessadorCadastro {

	/**
	 * Ver comentário na tarefa pai
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

		validate(movimento);
		
		InscricaoAtividadeParticipanteDao dao = null;
		
		try {
			
			MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente movi = (MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente) movimento;
			
			dao = getDAO(InscricaoAtividadeParticipanteDao.class, movi);
			
			List<Integer> idsInscricoesParticipante = new ArrayList<Integer>();
			
			for( InscricaoAtividadeParticipante inscricao : movi.getInscricoesConfirmarPagamento()){
				idsInscricoesParticipante.add(inscricao.getId());
			}
			
			dao.confirmaPagamentosManuaisGRUs(idsInscricoesParticipante);
			
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
		
		MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente movi = (MovimentoConfirmaPagamentoGRUAtividadesExtensaoManualmente) mov;
		
		for( InscricaoAtividadeParticipante inscricao : movi.getInscricoesConfirmarPagamento()){
			
			if(inscricao.getStatusPagamento() != StatusPagamentoInscricao.EM_ABERTO){
				erros.addErro("Não é possível confirmar manualmente o pagamento  do participante \""+inscricao.getCadastroParticipante().getNome()+"\" porque o pagamento não está aberto.");
			}
		}
		
		checkValidation(erros);
		
		
	}
	
}
