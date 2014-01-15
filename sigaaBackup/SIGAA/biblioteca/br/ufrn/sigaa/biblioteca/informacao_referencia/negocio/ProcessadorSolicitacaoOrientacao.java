/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 08/10/2008
 *
 */
package br.ufrn.sigaa.biblioteca.informacao_referencia.negocio;

import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.Biblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dao.SolicitacaoOrientacaoDAO;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoOrientacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.SolicitacaoServico.TipoSituacao;
import br.ufrn.sigaa.biblioteca.informacao_referencia.dominio.TipoServicoInformacaoReferencia;
import br.ufrn.sigaa.biblioteca.util.EmailsNotificacaoServicosBibliotecaUtil;
import br.ufrn.sigaa.biblioteca.util.UsuarioBibliotecaUtil;

/**
 * <p>Processador que contém as regras de negócio para o gerenciamento (Criar, Alterar, Validar, Atender, Transferir ou Cancelar)
 * das solicitações de orientação de normalização.</p> 
 *
 * @author Felipe Rivas
 */
public class ProcessadorSolicitacaoOrientacao extends AbstractProcessador {
	
	
	/** A assinatura padrão enviada ao usuário pelas resposta do setor de informação e referência. */
	public static final String ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA = "<p><br><br>Atenciosamente,<br>Setor de Informação e Referência</p>";


	/**
	 * Executa as operações para a solicitação de orientação 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		
		MovimentoSolicitacaoOrientacao mov = (MovimentoSolicitacaoOrientacao) movimento;
		GenericDAO dao = null; 
		
		try{
			dao = getGenericDAO(mov);
		
			SolicitacaoOrientacao obj = mov.getObjMovimentado();
			
			validate(movimento);
			
			/* ***************************************************************************
			 *  O usuário cadastrou uma nova solicitação de agendamento                  *
			 * ***************************************************************************/
			if( SigaaListaComando.CADASTRAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				obj.setSituacao( TipoSituacao.SOLICITADO ); // Cria a solicitação.
				obj.setNumeroSolicitacao( dao.getNextSeq("biblioteca", "numero_indentificador_solicitacao_sequence"));
				dao.create( obj );
				enviarEmailNovoAgendamento(mov, obj);
			
			/* ***************************************************************************
			 *  O usuário alterou os dados da solicitação                                 *
			 * ***************************************************************************/	
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				dao.update(mov.getObjMovimentado());
			
			
			/* ***************************************************************************
			 *  O bibliotecário transferiu a solicitação para outra biblioteca           *
			 * ***************************************************************************/		
			} else if( SigaaListaComando.TRANSFERIR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				obj.setBiblioteca(mov.getBibliotecaDestino());
				dao.update(obj);
				
				enviarEmailAoUsuarioAgendamentoTransferido(mov, mov.getBibliotecaOrigem(), mov.getBibliotecaDestino(), obj);
				enviarEmailAosBibliotecariosAgendamentoTransferido(mov, mov.getBibliotecaOrigem(), mov.getBibliotecaDestino(), obj);
				
			/* ***************************************************************************
			 *  O bibliotecário atendou a solicitação do usuário informando o horário    *
			 * ***************************************************************************/	
			} else if( SigaaListaComando.ATENDER_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);
				
				solicitacao.setDataInicio(mov.getDataInicio());
				solicitacao.setDataFim(mov.getDataFim());
				solicitacao.setComentariosBibliotecario(mov.getComentariosBibliotecario());
				
				solicitacao.setSituacao( TipoSituacao.ATENDIDO );
				solicitacao.setDataAtendimento( new Date() );
				solicitacao.setRegistroAtendimento( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
			
				enviarEmailAtendimentoSolicitacao(mov, obj);
				
			/* *******************************************************************************
			 *  O  usuário confirmou o comparecimento no prazo definido pelo bibliotecário   *
			 * *******************************************************************************/		
			} else if( SigaaListaComando.CONFIRMAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);

				solicitacao.setSituacao( TipoSituacao.CONFIRMADO );
				solicitacao.setDataConfirmacao( new Date() );
				solicitacao.setRegistroConfirmacao( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				enviaEmailConfirmacaoComparecimentoOrientacao(mov, obj);
			
			/* *******************************************************************************
			 *  O  bibliotecário disse não vai atender a solicitação                         *
			 * *******************************************************************************/				
			} else if( SigaaListaComando.NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);

				solicitacao.setSituacao( TipoSituacao.NAO_CONFIRMADO );
				solicitacao.setDataCancelamento( new Date() );
				solicitacao.setRegistroConfirmacao( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				enviaEmailNaoComparecimentoOrientacao(mov, obj);
				
			/* *******************************************************************************
			 *  O  bibliotecário disse não vai atender a solicitação                         *
			 * *******************************************************************************/			
			} else if( SigaaListaComando.CANCELAR_ATENDIMENTO_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);
				solicitacao.setSituacao( TipoSituacao.CANCELADO );
				solicitacao.setDataCancelamento( new Date() );
				solicitacao.setRegistroCancelamento( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				
				enviarEmailCancelamentoSolicitacao(mov, solicitacao);
				
			}
			
		}finally{
			if (dao != null) dao.close();
		}
		
		return null;
	}




	/**
	 * Envia um email informando aos bibliotecários de informação e referência sobre o novo agendamento criado.
	 */
	private void enviarEmailNovoAgendamento(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{
		
		String assunto = " Aviso de Novo Agendamento Realizado no Sistema";
		String titulo = " Novo Agendamento de Orientação de Normalização ";
		
		String mensagemNivel1Email =  " O usuário "+solicitacao.getPessoa().getNome()+" agendou uma orientação de normalização para essa biblioteca.";
		String mensagemNivel3Email =  " Esse agendamento está pendente de marcação de um horário de acordo com a disponibilidade escolhida pelo usuário.";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmail( solicitacao.getBiblioteca().getDescricao(), email, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  null, null);
		}
	}
	
	
	
	/**
	 * Método que envia o email para o usuário que fez a solicitação de agendamento, avisando que sua solicitação foi transferida
	 *
	 * @param mov
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 * @param solicitacao
	 * @throws DAOException
	 */
	private void enviarEmailAoUsuarioAgendamentoTransferido(MovimentoSolicitacaoOrientacao mov, Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino, SolicitacaoOrientacao solicitacao) throws DAOException{
		String assuntoEmail = "Solicitação transferida";
		
		String conteudo =
				"<p>A sua solicitação de orientação sua foi transferida da " +
				"   <strong>" + bibliotecaOrigem.getDescricao() + "</strong> para a <strong>" +
				bibliotecaDestino.getDescricao() + "</strong>.</p>" +
				ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		try {
			UsuarioBiblioteca ub =  UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(solicitacao.getPessoa().getId(), null);
		
			Object[] informacoesUsuario =  getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(ub);
		
			new EnvioEmailBiblioteca().enviaEmailSimples( (String) informacoesUsuario[0], 
					 (String) informacoesUsuario[1], 
					assuntoEmail, 
					assuntoEmail, 
					EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
					conteudo);
		
		} catch (NegocioException e) {
			// Se não tem usuário biblioteca ativo não quitado, não envia o email de aviso.
		}
		
	}
	
	
	
	/**
	 * Envia um email informando aos bibliotecários da biblioteca destino informando que o agendamento foi transferido para essa biblioteca. 
	 *
	 * @param mov
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 * @param solicitacao
	 * @throws DAOException
	 */
	private void enviarEmailAosBibliotecariosAgendamentoTransferido(MovimentoSolicitacaoOrientacao mov, Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino, SolicitacaoOrientacao solicitacao) throws DAOException{
		
		String assuntoEmail = "Aviso de Solicitação Transferida";
		String tituloEmail = "Solicitação transferida";
		
		String conteudo =
				"  <p>A solicitação de orientação do usuário: "+solicitacao.getPessoa().getNome()+" foi transferida da " +
				"  <strong>" + bibliotecaOrigem.getDescricao() + "</strong> para a sua biblioteca.";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO, mov.getBibliotecaDestino().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmailSimples(mov.getBibliotecaDestino().getDescricao(), email,
				assuntoEmail, 
				tituloEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
		}
	}
	
	
	
	/** Envia um email ao solicitante quando a solicitação é cancelada pelo bibliotecários */
	private void enviarEmailCancelamentoSolicitacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{
		String assuntoEmail = "Solicitação NÃO atendida";

		String  conteudo = "<p>A sua solicitação de orientação não pôde ser atendida. "
			+"Motivo: " + mov.getMotivoCancelamento() + ". <br><br></p>"+
			ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		try {
			UsuarioBiblioteca ub =  UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(solicitacao.getPessoa().getId(), null);
		
			Object[] informacoesUsuario =  getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(ub);
		
			new EnvioEmailBiblioteca().enviaEmailSimples( (String) informacoesUsuario[0], 
					 (String) informacoesUsuario[1], 
					assuntoEmail, 
					assuntoEmail, 
					EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
					conteudo);
		
		} catch (NegocioException e) {
			// Se não tem usuário biblioteca ativo não quitado, não envia o email de aviso.
		}
	
	}
	
	
	/** Envia um email ao solicitante quando a solicitação é atendida pelo bibliotecários */
	private void enviarEmailAtendimentoSolicitacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{

		String assuntoEmail = "Solicitação atendida";
		
		String conteudo = "<p>A sua solicitação de agendamento de orientação de normalização foi atendida. "
				+"Por favor, verifique e confirme do horário agendado acessando suas solicitações." +
				ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA;
		
		try {
			UsuarioBiblioteca ub =  UsuarioBibliotecaUtil.retornaUsuarioBibliotecaUnicoDaPessoa(solicitacao.getPessoa().getId(), null);
		
			Object[] informacoesUsuario =  getDAO( UsuarioBibliotecaDao.class, mov).findNomeEmailUsuarioBiblioteca(ub);
		
			new EnvioEmailBiblioteca().enviaEmailSimples( (String) informacoesUsuario[0], 
					 (String) informacoesUsuario[1], 
					assuntoEmail, 
					assuntoEmail, 
					EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
					conteudo);
		
		} catch (NegocioException e) {
			// Se não tem usuário biblioteca ativo não quitado, não envia o email de aviso.
		}
	
	}
	
	
	/**
	 * Envia um email para os bibliotecários quando o usuário confirma o comparecimento na biblioteca.
	 *
	 * @param mov
	 * @param obj
	 * @throws DAOException 
	 */
	private void enviaEmailConfirmacaoComparecimentoOrientacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException {
		
		String assuntoEmail = "Agendamento de Orientação de Normalização Confirmado";
		String tituloEmail = "Agendamento confirmado";
		
		String conteudo = "<p>O usuário " + solicitacao.getSolicitante() + " " +
				"confirmou o comparecimento à " + solicitacao.getBiblioteca().getDescricao() + " " +
				"no " + solicitacao.getDescricaoHorarioAtendimento().toLowerCase() + " " +
				"para receber a orientação sobre a normalização do trabalho dele.</p>";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmailSimples(solicitacao.getBiblioteca().getDescricao(), email,
				assuntoEmail, 
				tituloEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
		}
		
	}
	
	
	
	/**
	 * Envia um email para os bibliotecários quando o usuário não confirma o comparecimento na biblioteca.
	 *
	 * @param mov
	 * @param obj
	 * @throws DAOException 
	 */
	private void enviaEmailNaoComparecimentoOrientacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException {
		
		String assuntoEmail = "Agendamento de Orientação de Normalização Não Confirmado ";
		String tituloEmail = "Agendamento não confirmado";
		
		String conteudo = "<p>O usuário " + solicitacao.getSolicitante() + " " +
				"cancelou o comparecimento à " + solicitacao.getBiblioteca().getDescricao() + " " +
				"no " + solicitacao.getDescricaoHorarioAtendimento().toLowerCase() + " " +
				"para receber a orientação sobre a normalização do trabalho dele.</p>"
				+"<p>Motivo: " + mov.getMotivoCancelamento() + ". <br><br></p>";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmailSimples(solicitacao.getBiblioteca().getDescricao(), email,
				assuntoEmail, 
				tituloEmail, 
				EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, 
				conteudo);
		}
		
	}
	
	
	
	/**
	 * Valida os Atributos.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		
		MovimentoSolicitacaoOrientacao mov = (MovimentoSolicitacaoOrientacao) movimento;
		SolicitacaoOrientacao obj = mov.getObjMovimentado();
		
		ListaMensagens mensagens = new ListaMensagens(); 
		
		SolicitacaoOrientacaoDAO dao = null;
		
		try{
			
			dao = getDAO(SolicitacaoOrientacaoDAO.class, mov);
		
			if( SigaaListaComando.CADASTRAR_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				// No caso de orientação, só pode solicitar outra se estiver todas estiverem CONFIRMADO ou CANCELADO
				if(dao.contaSolicitacoesDoUsuaro(obj.getPessoa().getId(), TipoSituacao.SOLICITADO, TipoSituacao.ATENDIDO) > 0){
					mensagens.addErro("Não é possível realizar duas solicitações de "+TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO.getDescricao()+" ao mesmo tempo.");
				}else{
					Date dataUltimaSolcitacao = dao.recuperaDataFinalUltimaSolicitacoesAtivasDoUsuaro(obj.getPessoa().getId());
					
					if(dataUltimaSolcitacao != null && dataUltimaSolcitacao.after(new Date())){
						mensagens.addErro("Já existe uma solicitação de orientação marcada para o dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataUltimaSolcitacao)+" .");
					}
				}
				
				mensagens.addAll(obj.validate());
				
			} else if( SigaaListaComando.ATENDER_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
	
				if (mov.getDataInicio() != null && mov.getDataFim() != null) {		
					if (mov.getDataInicio().after(mov.getDataFim())) {
						mensagens.addErro("O horário de início do atendimento não pode ser maior ou igual ao horário de fim.");
					}
			
					if (CalendarUtils.compareTo(new Date(), mov.getDataInicio()) > 0) {
						mensagens.addErro("A data de atendimento não pode ser anterior à data de hoje.");
					}
				}
				else if (mov.getDataInicio() != null && mov.getDataFim() == null) {
					mensagens.addErro("Não é permitido definir o horário de início do atendimento sem definir o horário de fim.");
				}
				else if (mov.getDataInicio() == null && mov.getDataFim() != null) {
					mensagens.addErro("Não é permitido definir o horário de fim do atendimento sem definir o horário de início.");
				}
				else {
					mensagens.addErro("A data e os horários de início e fim de atendimento devem ser informados.");
				}
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela foi removida pelo solicitante.");
				}
	
				if(!obj.isSolicitado()){
					mensagens.addErro("A Solicitação não pode ser atendida, pois ela não foi solicitada.");
				}
	
				if (StringUtils.isNotEmpty(obj.getComentariosBibliotecario()) && obj.getComentariosBibliotecario().length() > 200) {
					mensagens.addErro("O campo 'Comentários' não pode ultrapassar 200 caracteres.");
				}
				
			} else if( SigaaListaComando.CONFIRMAR_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser confirmada, pois ela foi removida pelo solicitante.");
				}
				
				if(!obj.isAtendido()){
					mensagens.addErro("A Solicitação não pode ser confirmada, pois ela não foi atendida.");
				}
				
			} else if( SigaaListaComando.NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isCancelado() ){
					mensagens.addErro("Esta solicitação de orientação já foi cancelada e portanto não pode ser cancelada novamente.");
				}
				
				if(StringUtils.isEmpty(mov.getMotivoCancelamento())){
					mensagens.addErro("Informe o motivo do cancelamento.");
				}
				
				else if (mov.getMotivoCancelamento().length() > 100) {
					mensagens.addErro("O campo 'Motivo' não pode ultrapassar 100 caracteres.");
				}
				
			} else if( SigaaListaComando.CANCELAR_ATENDIMENTO_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isCancelado() ){
					mensagens.addErro("Esta solicitação de orientação já foi cancelada e portanto não pode ser cancelada novamente.");
				}
				
				if(StringUtils.isEmpty(mov.getMotivoCancelamento())){
					mensagens.addErro("Informe o motivo do cancelamento.");
				}
				else if (mov.getMotivoCancelamento().length() > 100) {
					mensagens.addErro("O campo 'Motivo' não pode ultrapassar 100 caracteres.");
				}
				
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicitação não pode ser alterada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isAtendido()){
					mensagens.addErro("Esta solicitação de orientação já foi atendida por um bibliotecário e portanto não pode ser mais alterada.");
				} else {
					mensagens.addAll(obj.validate());
				}
			}
		
		}finally{
			if(dao != null) dao.close();
		}
		
		checkValidation(mensagens);
		
	}

}
