/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 20/12/2011
 *
 */
package br.ufrn.sigaa.extensao.negocio;


/**
 * Responsável pelo cadastro das inscrições de participantes de cursos e eventos na área pública. 
 * 
 * @author Julio Cesar 
 * 
 */
public class InscricaoAtividadeParticipanteValidator {
	
//	public static void validaInscricaoSelecaoNacional(InscricaoAtividadeParticipante inscricao, ListaMensagens lista) throws ArqException {
//		
//		InscricaoAtividadeParticipanteDao dao = DAOFactory.getInstance().getDAO(InscricaoAtividadeParticipanteDao.class);
//		
//		try{
//			if (inscricao.getCpf() == 0 || inscricao.getCpf() == null) {
//				lista.addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "CPF");
//				
//			}else if(dao.findInscricaoParticipanteByStatus(
//					inscricao.getInscricaoAtividade().getId(), inscricao.getCpf(), StatusInscricaoParticipante.CONFIRMADO) != null) {
//				lista.addMensagem(MensagensExtensao.INSCRICAO_ATIVA_RECENTE_EXISTENTE);
//				
//			}else if (dao.findInscricaoParticipanteByStatus(
//					inscricao.getInscricaoAtividade().getId(), inscricao.getCpf(), StatusInscricaoParticipante.APROVADO) != null) {
//				lista.addMensagem(MensagensExtensao.INSCRITO_EH_PARTICIPANTE_ACAO);
//			}
//		}finally{
//			dao.close();
//		}
//		
//	}
//		
//	public static void validaInscricaoSelecaoInternacional(InscricaoAtividadeParticipante inscricao, ListaMensagens lista) throws ArqException {
//		
//		InscricaoAtividadeParticipanteDao dao = DAOFactory.getInstance().getDAO(InscricaoAtividadeParticipanteDao.class);
//		
//		try{
//			if (dao.findInscricaoParticipanteByStatusEstrangeiro(
//					inscricao.getInscricaoAtividade().getId(), inscricao.getPassaporte(), StatusInscricaoParticipante.CONFIRMADO) != null) {
//				lista.addMensagem(MensagensExtensao.INSCRICAO_ATIVA_RECENTE_EXISTENTE);
//			}else if (dao.findInscricaoParticipanteByStatusEstrangeiro(
//					inscricao.getInscricaoAtividade().getId(), inscricao.getPassaporte(), StatusInscricaoParticipante.APROVADO) != null) {
//				lista.addMensagem(MensagensExtensao.INSCRITO_EH_PARTICIPANTE_ACAO);
//			}
//		}finally{
//			dao.close();
//		}
//		
//	}
			
}
