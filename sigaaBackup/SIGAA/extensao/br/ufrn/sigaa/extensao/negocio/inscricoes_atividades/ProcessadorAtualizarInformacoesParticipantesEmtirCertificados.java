/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
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
 * <p> Procesador que atualiza as inforama��es da frequ�ncia, autoriza declara��o e autoriza certifiado de cada participante.</p>
 *
 * <p> <i> S�o as informa��es que o coordenador precisa alterar para poder emitir os certificados e declara��ses. 
 * Como a quantidade de participantes pode ser grande, � preciso um processador espec�fico para alterar apenas o necess�rio</i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAtualizarInformacoesParticipantesEmtirCertificados extends AbstractProcessador{

	/**
	 * Ver coment�rios da classe pai.<br/>
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
	 * Ver coment�rios da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		// Por enquanto sem valida��es //
	}

}
