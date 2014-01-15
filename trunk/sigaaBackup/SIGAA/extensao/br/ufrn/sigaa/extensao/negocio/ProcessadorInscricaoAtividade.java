package br.ufrn.sigaa.extensao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;

/**
 * Processador para realizar o cadastro de Inscrições de Cursos e Eventos na Área Pública.
 * Deverá ser mantida somente uma inscrição ativa por vez.
 * 
 * @author Daniel Augusto
 * @deprecated Código impossível de se manter. Não vou apagar para ficar de exemplo de como não se programar!
 */
@Deprecated 
public class ProcessadorInscricaoAtividade extends ProcessadorCadastro {

	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {

//		final String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
//		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
//
//		if (SigaaListaComando.CADASTRAR_INSCRICAO_ACAO_EXTENSAO.equals(mov.getCodMovimento())) {
//			((InscricaoAtividade) mov.getObjMovimentado()).setSubAtividade(null);
//			((InscricaoAtividade) mov.getObjMovimentado()).setSequencia(
//					getDAO(InscricaoAtividadeExtensaoDao.class, mov).getNextSeq("extensao", "inscricao_sequence"));			
//			validate(mov);
//			
//			if(ValidatorUtil.isEmpty(((InscricaoAtividade) mov.getObjMovimentado()).getQuestionario())) {
//				((InscricaoAtividade) mov.getObjMovimentado()).setQuestionario(null);
//			}
//			
//			criar(mov);
//		} else if(SigaaListaComando.CADASTRAR_INSCRICAO_SUB_ATIVIDADE.equals(mov.getCodMovimento())) {			
//			((InscricaoAtividade) mov.getObjMovimentado()).setAtividade(null);
//			((InscricaoAtividade) mov.getObjMovimentado()).setSequencia(
//					getDAO(InscricaoAtividadeExtensaoDao.class, mov).getNextSeq("extensao", "inscricao_sequence"));
//			criar(mov);
//			
//		} else if (SigaaListaComando.ALTERAR_VAGAS_CURSO_EVENTO_EXTENSAO.equals(mov.getCodMovimento())) {
//			validate(mov);
//			CursoEventoExtensao cursoEvento = ((InscricaoAtividade) mov.getObjMovimentado()).getAtividade().getCursoEventoExtensao();
//			getGenericDAO(mov).updateField(CursoEventoExtensao.class, cursoEvento.getId(), "numeroVagas", cursoEvento.getNumeroVagas());
//			
//		} else if (SigaaListaComando.ALTERAR_VAGAS_MINI_ATIVIDADE.equals(mov.getCodMovimento())) {
//			validate(mov);
//			SubAtividadeExtensao subAtividade = ((InscricaoAtividade) mov.getObjMovimentado()).getSubAtividade();
//			getGenericDAO(mov).updateField(SubAtividadeExtensao.class, subAtividade.getId(), "numeroVagas", subAtividade.getNumeroVagas());
//			
//		} else if (SigaaListaComando.SUSPENDER_INSCRICAO_ACAO_EXTENSAO.equals(mov.getCodMovimento())) {
//
//			InscricaoAtividade inscricao = (InscricaoAtividade) mov.getObjMovimentado();
//			GenericDAO dao = getGenericDAO(mov);
//			try {
//			    //Finaliza o período de inscrição. Define o período final de inscrição como ontem.
//			    Date ontem = CalendarUtils.subtraiDias(new Date(), 1);
//				dao.updateField(InscricaoAtividade.class, inscricao.getId(), "periodoFim", ontem);
//				dao.updateField(InscricaoAtividade.class, inscricao.getId(), "motivoCancelamento", inscricao.getMotivoCancelamento());
//			} finally {
//				dao.close();
//			}
//			
//			//Avisando aos inscritos que a inscrição foi suspensa.
//			for (InscricaoAtividadeParticipante p : inscricao.getParticipantesInscritos()) {
//
//				StringBuffer mensagem = new StringBuffer();
//				mensagem.append("Prezado(a) ");
//				mensagem.append(p.getNome());
//				mensagem.append(", \n\n");
//				mensagem.append("\tO período de inscrição para a Ação \"");
//				
//				// "Solução Temporaria" para consertar problema, vou separar em 2 processadores, uma para Atividade e outra para SubAtividade !!!!!!!
//				if(inscricao.getAtividade() != null){
//					mensagem.append( StringUtils.upperCase(inscricao.getAtividade().getTitulo()) );
//				}else{
//					if(inscricao.getSubAtividade() != null)
//						mensagem.append(StringUtils.upperCase(inscricao.getSubAtividade().getTitulo()));
//				}
//				
//				mensagem.append("\" no qual você havia se inscrito foi suspenso");
//				/*mensagem.append(inscricao.getMotivoCancelamento().isEmpty() ? 
//						"." : " pelo seguinte motivo: " + 
//						inscricao.getMotivoCancelamento());*/
//				mensagem.append(".\n\nConsulte o portal público do "+siglaSigaa+" para acompanhar " +
//				"novos períodos de inscrições de Cursos e Eventos de Extensão.");
//
//				MailBody mail = new MailBody();
//				mail.setContentType(MailBody.TEXT_PLAN);
//				mail.setAssunto("Período de Inscrição Suspenso - MENSAGEM AUTOMÁTICA");
//				mail.setMensagem(mensagem.toString());
//				mail.setEmail(p.getEmail());
//				mail.setNome(p.getNome());
//				Mail.send(mail);
//			}
//			if (!inscricao.getParticipantesInscritos().isEmpty()) {
//				ProcessadorInscricaoParticipante proc = new ProcessadorInscricaoParticipante();
//				MovimentoCadastro cadastroMov = new MovimentoCadastro();
//				cadastroMov.setObjMovimentado(inscricao);
//				cadastroMov.setSistema(Sistema.SIGAA);
//				cadastroMov.setUsuarioLogado(mov.getUsuarioLogado());
//				//cadastroMov.setCodMovimento(SigaaListaComando.RECUSAR_INSCRICAO_PARTICIPANTE_EXTENSAO);
//				proc.recusarInscritos(cadastroMov, inscricao.getParticipantesInscritos(), siglaSigaa);
//			}
//		} else if (SigaaListaComando.EXCLUIR_INSCRICAO_ACAO_EXTENSAO.equals(mov.getCodMovimento())) {
//			
//			InscricaoAtividade inscricao = (InscricaoAtividade) mov.getObjMovimentado();
//			GenericDAO dao = getGenericDAO(mov);
//			try {
//				dao.updateField(InscricaoAtividade.class, inscricao.getId(), "ativo", false);
//			} catch (Exception e) {
//				e.printStackTrace();
//				throw new DAOException(e);
//			} finally {
//				dao.close();
//			}
//		}
		return mov.getObjMovimentado();
	}

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
//		ListaMensagens mensagens = new ListaMensagens();
//
//		InscricaoAtividade obj = (InscricaoAtividade) ((MovimentoCadastro)mov).getObjMovimentado();		
//		InscricaoAtividadeExtensaoDao dao = getDAO(InscricaoAtividadeExtensaoDao.class, mov);
//		InscricaoAtividadeParticipanteDao daoAp = getDAO(InscricaoAtividadeParticipanteDao.class, mov);
//
//		try {
//
//			if (SigaaListaComando.CADASTRAR_INSCRICAO_ACAO_EXTENSAO.equals(mov.getCodMovimento())) {
//				
//				InscricaoAtividade ia = dao.findInscricaoAbertaByAtividade(obj.getAtividade().getId());
//				if (!ValidatorUtil.isEmpty(ia)) {
//					mensagens.addErro("Já existe uma inscrição ativa para esta ação de extensão.");
//				}
//				
//				mensagens = obj.validate();
//				int numeroInscritos = daoAp.countParticipantesByAcao(obj.getAtividade().getId());
//				daoAp.initialize(obj.getAtividade());
//				daoAp.initialize(obj.getAtividade().getCursoEventoExtensao());
//				if (obj.getQuantidadeVagas() > (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() - numeroInscritos)) {
//					mensagens.addErro("Limite de vagas reservadas ultrapassa a quantidade de vagas disponíveis (" + (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() - numeroInscritos) + ") para esta ação." );
//				}
//			}
//
//			if (SigaaListaComando.ALTERAR_VAGAS_CURSO_EVENTO_EXTENSAO.equals(mov.getCodMovimento())) {
//				if (obj.getAtividade().getCursoEventoExtensao().getNumeroVagas() < obj.getAtividade().getCursoEventoExtensao().getNumeroInscritos()) {
//					mensagens.addErro("O número de vagas não pode ser inferior a quantidade de Participantes Inscritos e já confirmados no Curso/Evento.");
//				}
//			}
//
//			if (SigaaListaComando.ALTERAR_VAGAS_MINI_ATIVIDADE.equals(mov.getCodMovimento())) {
//				if (obj.getSubAtividade().getNumeroVagas() < obj.getSubAtividade().getNumeroInscritos()) {
//					mensagens.addErro("O número de vagas não pode ser inferior a quantidade de Participantes Inscritos e já confirmados no Curso/Evento.");
//				}
//			}
//			
//		}finally {
//			dao.close();
//			daoAp.close();
//		}
//
//		checkValidation(mensagens);
	}

}
