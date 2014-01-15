/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
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
 * <p>Processador que cont�m as regras de neg�cio para o gerenciamento (Criar, Alterar, Validar, Atender, Transferir ou Cancelar)
 * das solicita��es de orienta��o de normaliza��o.</p> 
 *
 * @author Felipe Rivas
 */
public class ProcessadorSolicitacaoOrientacao extends AbstractProcessador {
	
	
	/** A assinatura padr�o enviada ao usu�rio pelas resposta do setor de informa��o e refer�ncia. */
	public static final String ASSINATURA_SETOR_INFORMACAO_E_REFERENCIA = "<p><br><br>Atenciosamente,<br>Setor de Informa��o e Refer�ncia</p>";


	/**
	 * Executa as opera��es para a solicita��o de orienta��o 
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
			 *  O usu�rio cadastrou uma nova solicita��o de agendamento                  *
			 * ***************************************************************************/
			if( SigaaListaComando.CADASTRAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				obj.setSituacao( TipoSituacao.SOLICITADO ); // Cria a solicita��o.
				obj.setNumeroSolicitacao( dao.getNextSeq("biblioteca", "numero_indentificador_solicitacao_sequence"));
				dao.create( obj );
				enviarEmailNovoAgendamento(mov, obj);
			
			/* ***************************************************************************
			 *  O usu�rio alterou os dados da solicita��o                                 *
			 * ***************************************************************************/	
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				dao.update(mov.getObjMovimentado());
			
			
			/* ***************************************************************************
			 *  O bibliotec�rio transferiu a solicita��o para outra biblioteca           *
			 * ***************************************************************************/		
			} else if( SigaaListaComando.TRANSFERIR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				obj.setBiblioteca(mov.getBibliotecaDestino());
				dao.update(obj);
				
				enviarEmailAoUsuarioAgendamentoTransferido(mov, mov.getBibliotecaOrigem(), mov.getBibliotecaDestino(), obj);
				enviarEmailAosBibliotecariosAgendamentoTransferido(mov, mov.getBibliotecaOrigem(), mov.getBibliotecaDestino(), obj);
				
			/* ***************************************************************************
			 *  O bibliotec�rio atendou a solicita��o do usu�rio informando o hor�rio    *
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
			 *  O  usu�rio confirmou o comparecimento no prazo definido pelo bibliotec�rio   *
			 * *******************************************************************************/		
			} else if( SigaaListaComando.CONFIRMAR_SOLICITACAO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);

				solicitacao.setSituacao( TipoSituacao.CONFIRMADO );
				solicitacao.setDataConfirmacao( new Date() );
				solicitacao.setRegistroConfirmacao( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				enviaEmailConfirmacaoComparecimentoOrientacao(mov, obj);
			
			/* *******************************************************************************
			 *  O  bibliotec�rio disse n�o vai atender a solicita��o                         *
			 * *******************************************************************************/				
			} else if( SigaaListaComando.NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO.equals( mov.getCodMovimento() ) ){
				
				SolicitacaoOrientacao solicitacao = dao.refresh(obj);

				solicitacao.setSituacao( TipoSituacao.NAO_CONFIRMADO );
				solicitacao.setDataCancelamento( new Date() );
				solicitacao.setRegistroConfirmacao( mov.getUsuarioLogado().getRegistroEntrada() );
				dao.update(solicitacao);
				enviaEmailNaoComparecimentoOrientacao(mov, obj);
				
			/* *******************************************************************************
			 *  O  bibliotec�rio disse n�o vai atender a solicita��o                         *
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
	 * Envia um email informando aos bibliotec�rios de informa��o e refer�ncia sobre o novo agendamento criado.
	 */
	private void enviarEmailNovoAgendamento(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{
		
		String assunto = " Aviso de Novo Agendamento Realizado no Sistema";
		String titulo = " Novo Agendamento de Orienta��o de Normaliza��o ";
		
		String mensagemNivel1Email =  " O usu�rio "+solicitacao.getPessoa().getNome()+" agendou uma orienta��o de normaliza��o para essa biblioteca.";
		String mensagemNivel3Email =  " Esse agendamento est� pendente de marca��o de um hor�rio de acordo com a disponibilidade escolhida pelo usu�rio.";
		
		List<String> emails = EmailsNotificacaoServicosBibliotecaUtil.getEmailsNotificacao(TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO, solicitacao.getBiblioteca().getId());
		
		for (String email : emails) {
			new EnvioEmailBiblioteca().enviaEmail( solicitacao.getBiblioteca().getDescricao(), email, assunto, titulo
				, EnvioEmailBiblioteca.AVISO_INFORMACAO_REFERENCIA, null, mensagemNivel1Email, null, mensagemNivel3Email, null
				, null, null,  null, null);
		}
	}
	
	
	
	/**
	 * M�todo que envia o email para o usu�rio que fez a solicita��o de agendamento, avisando que sua solicita��o foi transferida
	 *
	 * @param mov
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 * @param solicitacao
	 * @throws DAOException
	 */
	private void enviarEmailAoUsuarioAgendamentoTransferido(MovimentoSolicitacaoOrientacao mov, Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino, SolicitacaoOrientacao solicitacao) throws DAOException{
		String assuntoEmail = "Solicita��o transferida";
		
		String conteudo =
				"<p>A sua solicita��o de orienta��o sua foi transferida da " +
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
			// Se n�o tem usu�rio biblioteca ativo n�o quitado, n�o envia o email de aviso.
		}
		
	}
	
	
	
	/**
	 * Envia um email informando aos bibliotec�rios da biblioteca destino informando que o agendamento foi transferido para essa biblioteca. 
	 *
	 * @param mov
	 * @param bibliotecaOrigem
	 * @param bibliotecaDestino
	 * @param solicitacao
	 * @throws DAOException
	 */
	private void enviarEmailAosBibliotecariosAgendamentoTransferido(MovimentoSolicitacaoOrientacao mov, Biblioteca bibliotecaOrigem, Biblioteca bibliotecaDestino, SolicitacaoOrientacao solicitacao) throws DAOException{
		
		String assuntoEmail = "Aviso de Solicita��o Transferida";
		String tituloEmail = "Solicita��o transferida";
		
		String conteudo =
				"  <p>A solicita��o de orienta��o do usu�rio: "+solicitacao.getPessoa().getNome()+" foi transferida da " +
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
	
	
	
	/** Envia um email ao solicitante quando a solicita��o � cancelada pelo bibliotec�rios */
	private void enviarEmailCancelamentoSolicitacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{
		String assuntoEmail = "Solicita��o N�O atendida";

		String  conteudo = "<p>A sua solicita��o de orienta��o n�o p�de ser atendida. "
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
			// Se n�o tem usu�rio biblioteca ativo n�o quitado, n�o envia o email de aviso.
		}
	
	}
	
	
	/** Envia um email ao solicitante quando a solicita��o � atendida pelo bibliotec�rios */
	private void enviarEmailAtendimentoSolicitacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException{

		String assuntoEmail = "Solicita��o atendida";
		
		String conteudo = "<p>A sua solicita��o de agendamento de orienta��o de normaliza��o foi atendida. "
				+"Por favor, verifique e confirme do hor�rio agendado acessando suas solicita��es." +
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
			// Se n�o tem usu�rio biblioteca ativo n�o quitado, n�o envia o email de aviso.
		}
	
	}
	
	
	/**
	 * Envia um email para os bibliotec�rios quando o usu�rio confirma o comparecimento na biblioteca.
	 *
	 * @param mov
	 * @param obj
	 * @throws DAOException 
	 */
	private void enviaEmailConfirmacaoComparecimentoOrientacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException {
		
		String assuntoEmail = "Agendamento de Orienta��o de Normaliza��o Confirmado";
		String tituloEmail = "Agendamento confirmado";
		
		String conteudo = "<p>O usu�rio " + solicitacao.getSolicitante() + " " +
				"confirmou o comparecimento � " + solicitacao.getBiblioteca().getDescricao() + " " +
				"no " + solicitacao.getDescricaoHorarioAtendimento().toLowerCase() + " " +
				"para receber a orienta��o sobre a normaliza��o do trabalho dele.</p>";
		
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
	 * Envia um email para os bibliotec�rios quando o usu�rio n�o confirma o comparecimento na biblioteca.
	 *
	 * @param mov
	 * @param obj
	 * @throws DAOException 
	 */
	private void enviaEmailNaoComparecimentoOrientacao(MovimentoSolicitacaoOrientacao mov, SolicitacaoOrientacao solicitacao) throws DAOException {
		
		String assuntoEmail = "Agendamento de Orienta��o de Normaliza��o N�o Confirmado ";
		String tituloEmail = "Agendamento n�o confirmado";
		
		String conteudo = "<p>O usu�rio " + solicitacao.getSolicitante() + " " +
				"cancelou o comparecimento � " + solicitacao.getBiblioteca().getDescricao() + " " +
				"no " + solicitacao.getDescricaoHorarioAtendimento().toLowerCase() + " " +
				"para receber a orienta��o sobre a normaliza��o do trabalho dele.</p>"
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
				
				// No caso de orienta��o, s� pode solicitar outra se estiver todas estiverem CONFIRMADO ou CANCELADO
				if(dao.contaSolicitacoesDoUsuaro(obj.getPessoa().getId(), TipoSituacao.SOLICITADO, TipoSituacao.ATENDIDO) > 0){
					mensagens.addErro("N�o � poss�vel realizar duas solicita��es de "+TipoServicoInformacaoReferencia.ORIENTACAO_NORMALIZACAO.getDescricao()+" ao mesmo tempo.");
				}else{
					Date dataUltimaSolcitacao = dao.recuperaDataFinalUltimaSolicitacoesAtivasDoUsuaro(obj.getPessoa().getId());
					
					if(dataUltimaSolcitacao != null && dataUltimaSolcitacao.after(new Date())){
						mensagens.addErro("J� existe uma solicita��o de orienta��o marcada para o dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(dataUltimaSolcitacao)+" .");
					}
				}
				
				mensagens.addAll(obj.validate());
				
			} else if( SigaaListaComando.ATENDER_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
	
				if (mov.getDataInicio() != null && mov.getDataFim() != null) {		
					if (mov.getDataInicio().after(mov.getDataFim())) {
						mensagens.addErro("O hor�rio de in�cio do atendimento n�o pode ser maior ou igual ao hor�rio de fim.");
					}
			
					if (CalendarUtils.compareTo(new Date(), mov.getDataInicio()) > 0) {
						mensagens.addErro("A data de atendimento n�o pode ser anterior � data de hoje.");
					}
				}
				else if (mov.getDataInicio() != null && mov.getDataFim() == null) {
					mensagens.addErro("N�o � permitido definir o hor�rio de in�cio do atendimento sem definir o hor�rio de fim.");
				}
				else if (mov.getDataInicio() == null && mov.getDataFim() != null) {
					mensagens.addErro("N�o � permitido definir o hor�rio de fim do atendimento sem definir o hor�rio de in�cio.");
				}
				else {
					mensagens.addErro("A data e os hor�rios de in�cio e fim de atendimento devem ser informados.");
				}
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicita��o n�o pode ser atendida, pois ela foi removida pelo solicitante.");
				}
	
				if(!obj.isSolicitado()){
					mensagens.addErro("A Solicita��o n�o pode ser atendida, pois ela n�o foi solicitada.");
				}
	
				if (StringUtils.isNotEmpty(obj.getComentariosBibliotecario()) && obj.getComentariosBibliotecario().length() > 200) {
					mensagens.addErro("O campo 'Coment�rios' n�o pode ultrapassar 200 caracteres.");
				}
				
			} else if( SigaaListaComando.CONFIRMAR_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicita��o n�o pode ser confirmada, pois ela foi removida pelo solicitante.");
				}
				
				if(!obj.isAtendido()){
					mensagens.addErro("A Solicita��o n�o pode ser confirmada, pois ela n�o foi atendida.");
				}
				
			} else if( SigaaListaComando.NAO_CONFIRMAR_HORARIO_AGENDADO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicita��o n�o pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isCancelado() ){
					mensagens.addErro("Esta solicita��o de orienta��o j� foi cancelada e portanto n�o pode ser cancelada novamente.");
				}
				
				if(StringUtils.isEmpty(mov.getMotivoCancelamento())){
					mensagens.addErro("Informe o motivo do cancelamento.");
				}
				
				else if (mov.getMotivoCancelamento().length() > 100) {
					mensagens.addErro("O campo 'Motivo' n�o pode ultrapassar 100 caracteres.");
				}
				
			} else if( SigaaListaComando.CANCELAR_ATENDIMENTO_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicita��o n�o pode ser cancelada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isCancelado() ){
					mensagens.addErro("Esta solicita��o de orienta��o j� foi cancelada e portanto n�o pode ser cancelada novamente.");
				}
				
				if(StringUtils.isEmpty(mov.getMotivoCancelamento())){
					mensagens.addErro("Informe o motivo do cancelamento.");
				}
				else if (mov.getMotivoCancelamento().length() > 100) {
					mensagens.addErro("O campo 'Motivo' n�o pode ultrapassar 100 caracteres.");
				}
				
			} else if( SigaaListaComando.ALTERAR_SOLICITACAO_ORIENTACAO.equals( movimento.getCodMovimento() ) ){
				
				if(!obj.isAtiva()){
					mensagens.addErro("A Solicita��o n�o pode ser alterada, pois ela foi removida pelo solicitante.");
				}
				
				if( obj.isAtendido()){
					mensagens.addErro("Esta solicita��o de orienta��o j� foi atendida por um bibliotec�rio e portanto n�o pode ser mais alterada.");
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
