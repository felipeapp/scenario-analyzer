/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
*  Created on '18/12/2009'
*
* @author Emerson Sena, Johnny Marçal
*/
package br.ufrn.arq.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.springframework.jdbc.core.JdbcTemplate;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.ASyncMsgDelegate;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.caixa_postal.MensagemDAO;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.RuntimeNegocioException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Processador usado para cadastrar mensagens
 * 
 * @author Emerson Sena, Johnny Marçal
 * 
 */
public class ProcessadorMensagemBean extends AbstractProcessador {

	public static final int GESTOR_PATRIMONIO_GLOBAL = 600001;
	
	public static final int ADMINISTRADOR_SIPAC = 200001;
	
	public static final int ENVIADOR_MENSAGEM = 200005;

	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MensagemDAO msgDAO = new MensagemDAO();

		try {
			Mensagem msg = (Mensagem) mov;

			if (msg.getReplyFrom() != null && msg.getReplyFrom().getId() == 0) {
				msg.setReplyFrom(null);
			}

			switch (mov.getCodMovimento().getId()) {
			case ArqListaComando.MENSAGEM_ENVIAR_COD:
				if ((msg.getIdPapel() != null && msg.getIdPapel()> 0)) {
					// Caso tenha sido escolhido enviar mensagens por papel

					if (msg.getIdPapel() != GESTOR_PATRIMONIO_GLOBAL) {
						checkRole(new int[]{ADMINISTRADOR_SIPAC, ENVIADOR_MENSAGEM}, mov);
					}

					ASyncMsgDelegate.getInstance().enviaMensagemUsuarios(msg, mov.getUsuarioLogado(),msg.getDestinatarios() );
					
				} else {

					// Caso seja para um usuário específico
					Collection<UsuarioGeral> destinatarios = new ArrayList<UsuarioGeral>();

					//Mensagem para usuários informados
					destinatarios = msg.getDestinatarios();

					msgDAO = new MensagemDAO();
					msgDAO.setOpenSessionInView(true);

					for (UsuarioGeral usr : destinatarios) {

						br.ufrn.arq.caixa_postal.Mensagem  nova = new br.ufrn.arq.caixa_postal.Mensagem();
						nova.setUsuarioDestino(usr);
						nova.setRemetente(mov.getUsuarioLogado());
						nova.setTitulo(msg.getTitulo());
						nova.setDescricao(msg.getDescricao());
						nova.setMensagem(msg.getMensagem());
						nova.setTipo(msg.getTipo());
						nova.setAutomatica(msg.isAutomatica());
						nova.setTipoChamado(msg.getTipoChamado());
						
						
						if (msg.getArquivo() != null) 
						{
							nova.setNomeArquivo(msg.getArquivo().getName());
							
							try 
							{
								int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
								EnvioArquivoHelper.inserirArquivo(idArquivo, msg.getArquivo().getBytes(), msg.getArquivo().getContentType(), msg.getArquivo().getName());
								nova.setIdArquivo(idArquivo);
							} 
							catch (Exception e)
							{ 
									throw new ArqException(e);
							}
						}
						else
						{
							if( (msg.isAnexarArqResposta()) && (msg.getIdArquivo() !=  null) )
							{
								nova.setNomeArquivo(msg.getNomeArquivo());
								nova.setIdArquivo(msg.getIdArquivo());
							}								
						}
							

						//transforma o id da msg anterior (Sipac) em uma msg do ARQ
						br.ufrn.arq.caixa_postal.Mensagem msgAnt = new br.ufrn.arq.caixa_postal.Mensagem();
						if (msg.getReplyFrom() != null) {

							Mensagem msgAnteriorCompleta = msgDAO.findByPrimaryKey(msg.getReplyFrom().getId(), Mensagem.class);
							
							// no caso do usuário responder uma mensagem vinda do suporte setar a resposta como sendo do chamado
							if ( msgAnteriorCompleta != null && msgAnteriorCompleta.getReplyFrom() != null && msgAnteriorCompleta.getReplyFrom().isChamado() ) {
								nova.setReplyFrom(msgAnteriorCompleta.getReplyFrom());

								new JdbcTemplate(Database.getInstance().getComumDs()).update("update comum.mensagem set status_chamado = 7 " +
										"where num_chamado = " + msgAnteriorCompleta.getReplyFrom().getNumChamado());

								// status 7 - respondido pelo usuário

							} else {
								msgAnt.setId(msg.getReplyFrom().getId());
								nova.setReplyFrom(msgAnt);
							}
							

							//Caso alguém responda um Chamado patrimonial, os outros que receberam o mesmo chamado ficaram com os mesmo com status de lido. 
							if (msgAnteriorCompleta != null && msgAnteriorCompleta.getNumChamadoPatrimonial() != null && msgAnteriorCompleta.getNumChamadoPatrimonial() != 0) {
									new JdbcTemplate(Database.getInstance().getComumDs()).update("UPDATE comum.mensagem SET lida = trueValue() " +
										"where num_chamado_patrimonial = " + msgAnteriorCompleta.getNumChamadoPatrimonial());
							}
							
							
						} else {
							// caso não exista mensagem anterior
							nova.setReplyFrom(null);
						}
						
						if ( msg.isEnviarEmail() ) {
							MailBody mailEnvio = msg.toMailBody(null, usr);
							mailEnvio.setContentType(MailBody.HTML);
							
							if (!ValidatorUtil.isEmpty(msg.getArquivo())) {
								try {
									mailEnvio.addAttachment(new Object[] { msg.getArquivo().getName(), msg.getArquivo().getBytes()});
								} catch (IOException e) {
									e.printStackTrace();
								}
							}
							
							Mail.send(mailEnvio);
						}


						nova.setLida(false);
						nova.setLeituraObrigatoria(msg.isLeituraObrigatoria());
						nova.setConfLeitura(msg.isConfLeitura());

						nova.setPapel(null);

						if (nova.isChamado()) {
							nova.setNumChamado(msgDAO.getNextChamado());
							nova.setStatusChamadoDesc(-1); // status pendente.
						}
						
						if(nova.getTipo() == Mensagem.CHAMADO_SIPAC_PATRIMONIO) {
							nova.setNumChamadoPatrimonial(msgDAO.getNextChamadoPatrimonio());
						}

						msgDAO.createNoFlush(nova);

						if (nova.isChamado())
							return nova;
					}
				}
				break;
			case ArqListaComando.MENSAGEM_LIDA_COD:
				if (!msg.isLida()) {
					
					if(msg.getUsuarioDestino().getId() == mov.getUsuarioLogado().getId())
					{
					
						msg.setLida(true);
						msg.setDataLeitura(new Date());
	
						if ( isEmpty(msg.getIdPapel()) ) {
							msg.setPapel(null);
							msg.setIdPapel(null);
						}
	
						if (msg.isConfLeitura()) 
						{						
							UsuarioGeral remetente = msgDAO.findByPrimaryKey(msg.getRemetente().getId(), UsuarioGeral.class);
							
							if(!ValidatorUtil.isEmpty(remetente) 
									&& !ValidatorUtil.isEmpty(msg.getUsuarioDestino()) 
									&& mov.getUsuarioLogado().getPessoa().getId() == msg.getUsuarioDestino().getPessoa().getId() )
							{							
								MailBody body = new MailBody();
								body.setAssunto("Confirmação de leitura de mensagem");
								body.setEmail(remetente.getEmail());
								body.setMensagem("<p>Caro " + remetente.getPessoa().getNome() + ",</p>"
										+ "<p>Conforme solicitado, informamos que o usuário " + mov.getUsuarioLogado().getPessoa().getNome() 
										+ " leu a mensagem \"" + msg.getTitulo() + "\" no dia "
										+ new SimpleDateFormat("dd 'de' MMMM 'de' yyyy 'às' HH:mm").format(new Date())
										+ ".</p>"
										+ "Atenciosamente,<br/> "
										+ RepositorioDadosInstitucionais.get("nomeResponsavelInformatica") +  ".");
								
								Mail.send(body);
							}
						}
						
						msgDAO.update(msg);
					}

				}
				break;
			case ArqListaComando.MENSAGEM_EXCLUIR_COD:
				Mensagem msgArq = msgDAO.findByPrimaryKey(msg.getId(),Mensagem.class);
				if (msgArq != null) {
					if (msgArq.getRemetente() != null&& msgArq.getRemetente().getId() == mov.getUsuarioLogado().getId()) {
						if (msgArq.getRemetente().getId() == msgArq.getUsuarioDestino().getId())
							msgArq.setRemovidaDestinatario(true);
						msgArq.setRemovidaRemetente(true);
					}
					else
						msgArq.setRemovidaDestinatario(true);
					
					msgDAO.update(msgArq);
				}
				else
					throw new RuntimeNegocioException("Mensagem não encontrada.");
				
				break;

			case ArqListaComando.MENSAGEM_EXCLUIR_LOTE_COD:
				//pegando uma mensagem do lote
				int idMensLote = msg.getIdMensagem()[0];
				Mensagem mens =  msgDAO.findByPrimaryKey(idMensLote, br.ufrn.arq.caixa_postal.Mensagem.class); 
				
				//verificando se o usuário que está removendo a mensagem é o remetente
				boolean remetente = false;
				if(mens.getRemetente().getId() == mov.getUsuarioLogado().getId())
					remetente = true;
				
				msgDAO.removerMensagens(msg.getIdMensagem(), remetente);
				break;
				
			case ArqListaComando.MENSAGEM_EXCLUIR_LOTE_LIXEIRA_COD:
				
				//pegando uma mensagem do lote
				int idMensLoteLixeira = msg.getIdMensagem()[0];
				Mensagem mens_lixeira =  msgDAO.findByPrimaryKey(idMensLoteLixeira, br.ufrn.arq.caixa_postal.Mensagem.class); 
				
				//verificando se o usuário que está removendo a mensagem é o remetente
				boolean remetente_lixeira = false;
				if(mens_lixeira.getRemetente().getId() == mov.getUsuarioLogado().getId())
					remetente_lixeira = true;
				
				msgDAO.removerMensagensLixeira(msg.getIdMensagem(), remetente_lixeira);
				break;
				
			case ArqListaComando.MENSAGEM_EXCLUIR_TODAS_LIXEIRA_COD:
				
				msgDAO.removerTodasMensagensLixeira(mov.getUsuarioLogado().getId());
				break; 
				
			case ArqListaComando.MENSAGEM_MARCAR_LIDA_LOTE_COD:
				
				msgDAO.marcarMsgComoLidaLote(msg.getIdMensagem());
				break;	
				
			case ArqListaComando.MENSAGEM_MARCAR_TODAS_LIDA_COD:
				
				msgDAO.marcarTodasMsgComoLidas(mov.getUsuarioLogado().getId());
				break; 
				
			case ArqListaComando.MENSAGEM_MARCAR_COMO_NAO_LIDA_COD:				
							
				msgDAO.marcarComoNaoLiada(mov.getUsuarioLogado().getId(), msg.getIdMensagem());
				break;	
				
			}
		} finally {
			msgDAO.close();
		}
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
