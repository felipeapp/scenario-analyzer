/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 09/07/2009
 *
 */
package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;

/**
 * Processador para realizar o cadastro coletivo de vários participantes de uma
 * ação de extensão.
 * 
 * @author Daniel Augusto
 * 
 * @Deprecated o participante não vai ser mais criado por aqui. @see ProcessadorInscreveParticipantesCursoEventoExtensao
 */
@Deprecated
public class ProcessadorParticipantesExtensao extends AbstractProcessador {

//	/**
//	 * Recebe o movimento e executa o comando que foi definido no MBean.
//	 * 
//	 * @return Object
//	 * @throws NegocioException, ArqException, RemoteException
//	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
//		
//	    GenericDAO dao = getGenericDAO(movimento);
//	    try {
//		MovimentoCadastro mov = (MovimentoCadastro) movimento;
//		if (SigaaListaComando.CADASTRAR_PARTICIPANTES_EXTENSAO.equals(mov.getCodMovimento())){
//
//		    validate(mov);
//		    for (PersistDB obj : mov.getColObjMovimentado()) {
//		    	ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) obj;
//		    	//participante.anularAtributosVazios();
//		    	dao.createNoFlush(participante);
//		    }
//
//		} else if (SigaaListaComando.ALTERAR_PARTICIPACOES_EXTENSAO.equals(mov.getCodMovimento())){
//
//		    for (PersistDB obj : mov.getColObjMovimentado()) {			
//			final int novaFrequencia = ((ParticipanteAcaoExtensao) obj).getFrequencia();
//			final boolean certificado = ((ParticipanteAcaoExtensao) obj).isAutorizacaoCertificado();
//			final boolean declaracao = ((ParticipanteAcaoExtensao) obj).isAutorizacaoDeclaracao();
//			
//			dao.updateFields(ParticipanteAcaoExtensao.class, obj.getId(), 
//				new String[] {"frequencia","autorizacaoCertificado", "autorizacaoDeclaracao"}, 
//				new Object[] {novaFrequencia, certificado, declaracao});
//		    }
//		}
		return movimento;
//	    } finally {
//		dao.close();
//	    }
	}
//
//	/**
//	 * Verifica todas as informações necessárias para o cadastro dos
//	 * participantes na ação de extensão.
//	 * 
//	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
//		Usuario usuario = (Usuario) mov.getUsuarioLogado();
//
//		// Se o usuário não for servidor ou docente externo e estiver tentando realizar esta operação.
//		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
//			throw new NegocioException("Apenas Docentes ou Técnicos Administrativos podem realizar esta operação.");
//		}
//
//		for (PersistDB obj : ((MovimentoCadastro) mov).getColObjMovimentado()) {
//			ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) obj;
//			checkValidation(participante.validate());
//		}
	}

}
