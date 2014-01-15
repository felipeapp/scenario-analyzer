package br.ufrn.sigaa.extensao.negocio;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 * Classe com m�todos respons�veis pela valida��o das opera��es realizadas com
 * cadastros de participantes de a��es de extens�o
 * 
 * @author Ilueny santos
 * 
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class ParticipanteAcaoExtensaoValidator {

	
	/**
	 * Verifica se j� tem um participante
	 * 
	 * @param tipoRelatorio
	 * @param lista
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaNovoParticipante(ParticipanteAcaoExtensao participante,
			ListaMensagens lista) throws DAOException, ArqException {

		if(participante.getFrequencia() == null) {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Frequ�ncia");
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// A valida��o � apenas pelo nome e data de nascimento, n�o pode ser pelo CPF nem passaporte porque   //
		// muitas vezes s�o crian�as e n�o t�m essa informa��o                                                //
		////////////////////////////////////////////////////////////////////////////////////////////////////////
		
//		if( participante.getTipoParticipante() == ParticipanteAcaoExtensao.DISCENTE_UFRN){ 
//			if(participante.getDiscente().getId() <= 0)
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Discente");
//		}
//		
//		if( participante.getTipoParticipante() == ParticipanteAcaoExtensao.SERVIDOR_UFRN){ 
//			if(participante.getServidor().getId() <= 0)
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Servidor");
//		}
		
//		if( participante.getTipoParticipante() == ParticipanteAcaoExtensao.OUTROS){ 
//			
//			if (!participante.isInternacional() && (participante.getCpf() == null || participante.getCpf() <= 0)) {
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
//			}
//
//			if (participante.isInternacional() && (   StringUtils.isEmpty( participante.getPassaporte())     )  ) {
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Passaporte");
//			}
//			
//			if (StringUtils.isEmpty(participante.getNome())) {
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Nome");
//			}
//		}
		
		
//		// VALIDA CPF
//		if(participante.getTipoParticipante() == ParticipanteAcaoExtensao.OUTROS && participante.getCpf() != null){
//			if(!ValidadorCPFCNPJ.getInstance().validaCpfCNPJ(participante.getCpf()))
//				lista.addMensagem(MensagensArquitetura.FORMATO_INVALIDO, "CPF");		
//		}
//		
		
		//Se for CURSO ou EVENTO e o participante for novo
//		if( (participante.getId() == 0) && 
//			(participante.getAcaoExtensao().getTipoAtividadeExtensao().isCurso() 
//				|| participante.getAcaoExtensao().getTipoAtividadeExtensao().isEvento()) ) {
//
//			Integer numeroVagasCursoEvento = participante.getAcaoExtensao().getCursoEventoExtensao().getNumeroVagas();
//			Integer numeroParticipantesCadastrados = participante.getAcaoExtensao().getParticipantesNaoOrdenados().size();
//
//			if( numeroParticipantesCadastrados >= numeroVagasCursoEvento) {
//				lista.addErro("Esta a��o de extens�o pode ter no m�ximo " + numeroVagasCursoEvento + " participantes. A a��o de extens�o j� possui o n�mero m�ximo de participantes cadastrados.");
//			}
//		}
//	
//		
//		for (ParticipanteAcaoExtensao p : participante.getAcaoExtensao().getParticipantes()) {
//			if (p.isAtivo() && participante.equals(p) && (participante.getId() == 0)) {
//				lista.addErro("Participante j� faz parte desta a��o de extens�o.");
//				break;
//			}
//		}
		
		if(!lista.isEmpty()) {
			return ;
		}

	}
}
