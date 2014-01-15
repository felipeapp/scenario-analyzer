/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
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
 * Processador para realizar o cadastro coletivo de v�rios participantes de uma
 * a��o de extens�o.
 * 
 * @author Daniel Augusto
 * 
 * @Deprecated o participante n�o vai ser mais criado por aqui. @see ProcessadorInscreveParticipantesCursoEventoExtensao
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
//	 * Verifica todas as informa��es necess�rias para o cadastro dos
//	 * participantes na a��o de extens�o.
//	 * 
//	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
//		Usuario usuario = (Usuario) mov.getUsuarioLogado();
//
//		// Se o usu�rio n�o for servidor ou docente externo e estiver tentando realizar esta opera��o.
//		if (!usuario.getVinculoAtivo().isVinculoServidor() && !usuario.getVinculoAtivo().isVinculoDocenteExterno()) {
//			throw new NegocioException("Apenas Docentes ou T�cnicos Administrativos podem realizar esta opera��o.");
//		}
//
//		for (PersistDB obj : ((MovimentoCadastro) mov).getColObjMovimentado()) {
//			ParticipanteAcaoExtensao participante = (ParticipanteAcaoExtensao) obj;
//			checkValidation(participante.validate());
//		}
	}

}
