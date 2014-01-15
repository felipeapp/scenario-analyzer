package br.ufrn.sigaa.extensao.negocio;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.extensao.dominio.ParticipanteAcaoExtensao;

/**
 * Classe com métodos responsáveis pela validação das operações realizadas com
 * cadastros de participantes de ações de extensão
 * 
 * @author Ilueny santos
 * 
 * @throws ArqException
 * @throws DAOException
 * 
 */
public class ParticipanteAcaoExtensaoValidator {

	
	/**
	 * Verifica se já tem um participante
	 * 
	 * @param tipoRelatorio
	 * @param lista
	 * @throws DAOException
	 * @throws ArqException
	 */
	public static void validaNovoParticipante(ParticipanteAcaoExtensao participante,
			ListaMensagens lista) throws DAOException, ArqException {

		if(participante.getFrequencia() == null) {
			lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO,"Frequência");
		}

		////////////////////////////////////////////////////////////////////////////////////////////////////////
		// A validação é apenas pelo nome e data de nascimento, não pode ser pelo CPF nem passaporte porque   //
		// muitas vezes são crianças e não têm essa informação                                                //
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
//				lista.addErro("Esta ação de extensão pode ter no máximo " + numeroVagasCursoEvento + " participantes. A ação de extensão já possui o número máximo de participantes cadastrados.");
//			}
//		}
//	
//		
//		for (ParticipanteAcaoExtensao p : participante.getAcaoExtensao().getParticipantes()) {
//			if (p.isAtivo() && participante.equals(p) && (participante.getId() == 0)) {
//				lista.addErro("Participante já faz parte desta ação de extensão.");
//				break;
//			}
//		}
		
		if(!lista.isEmpty()) {
			return ;
		}

	}
}
