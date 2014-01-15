/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 10/12/2012
 * 
 */
package br.ufrn.sigaa.extensao.negocio.inscricoes_atividades;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 *
 * <p>Altera dos dados do participante pelo coordenador</p>
 *
 * <p> <i> </i> </p>
 * 
 * @author jadson
 *
 */
public class ProcessadorAlteraParticipanteAtividadeExtensaoCoordenador extends AbstractProcessador{

	/**
	 * Ver comentários da classe pai.<br/>
	 *
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,RemoteException {
		
		validate(mov);
		
		MovimentoCadastro  movi = (MovimentoCadastro) mov;
		
		ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) movi.getObjMovimentado();
		
		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(movi);
			dao.updateFields(ParticipanteAcaoExtensao.class, participante.getId()
					, new String[]{"tipoParticipacao.id", "frequencia", "autorizacaoCertificado", "autorizacaoDeclaracao", "observacaoCertificado"}
					, new Object[]{participante.getTipoParticipacao().getId(), participante.getFrequencia()
					, participante.isAutorizacaoCertificado(), participante.isAutorizacaoDeclaracao(), participante.getObservacaoCertificado()});
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
		
		MovimentoCadastro  movi = (MovimentoCadastro) mov;
		
		ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) movi.getObjMovimentado();
		erros.addAll(participante.validate());
		checkValidation(erros);
	}

}
