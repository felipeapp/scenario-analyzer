/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 04/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 * <p> Procesador que atualiza as inforamações da frequência, autoriza declaração e autoriza certifiado de cada participante.</p>
 *
 * <p> <i> São as informações que o coordenador precisa alterar para poder emitir os certificados e declaraçõses. 
 * Como a quantidade de participantes pode ser grande, é preciso um processador específico para alterar apenas o necessário</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizarInformacoesParticipantesEmtirCertificados extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		
		validate(mov);
		
		MovimentoCadastro movi = (MovimentoCadastro) mov;
		
		@SuppressWarnings("unchecked")
		List<ParticipanteAcaoExtensao> participantes = (List<ParticipanteAcaoExtensao>) movi.getColObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movi);
			
			// Atualiza os 3 campos para os participantes //
			for (ParticipanteAcaoExtensao p : participantes) {
				dao.updateFields(ParticipanteAcaoExtensao.class, p.getId()
						,  new String[]{"frequencia", "autorizacaoDeclaracao", "autorizacaoCertificado"}
						, new Object[]{p.getFrequencia(), p.isAutorizacaoDeclaracao(), p.isAutorizacaoCertificado()});
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
		// Por enquanto sem validações //
	}

}
