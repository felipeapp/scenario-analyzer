/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.ForumDao;
import br.ufrn.sigaa.ava.dominio.DenunciaMensagem;
import br.ufrn.sigaa.ava.dominio.ForumMensagem;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;

/**
 * Processador que existe para implementar a regra de negócio do cadastro de uma
 * mensagem. Basicamente é contar a quantidade de respostas associadas.
 * 
 * @author Gleydson
 * 
 */
public class ProcessadorMensagemForum extends ProcessadorCadastro {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		validate(mov);
		Object obj = null;

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_TOPICO_FORUM)) {
			obj = criar(mov);
		}else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_MENSAGEM_TOPICO_FORUM)) {
			removerMensagemTopico(mov);
		}else if (mov.getCodMovimento().equals(SigaaListaComando.DENUNCIAR_MENSAGEM_TOPICO_FORUM)){
			denunciarMensagemTopico(mov);
		}else if (mov.getCodMovimento().equals(ArqListaComando.ALTERAR)) {
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.REMOVER)) {
			obj = remover(mov);
		} else if (mov.getCodMovimento().equals(ArqListaComando.DESATIVAR)) {
			obj = desativar(mov);
		}

		return obj;
	}
	
	@Override
	protected Object criar(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {
		calculaTotalRespostas((ForumMensagem) mov.getObjMovimentado(), mov);
		return super.criar(mov);
	}
	

	/**
	 * Remove o tópico ou mensagem individualmente. 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void removerMensagemTopico(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {
		ForumMensagem mensagem = mov.getObjMovimentado();
		//Remove todas mensagens relacionadas e atualiza os dados do tópico
		getDAO(ForumDao.class, mov).removerMensagemTopicoIndividual( mensagem.getId() );
	}
	
	/**
	 * Denunciar o tópico ou mensagem individualmente. 
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void denunciarMensagemTopico(MovimentoCadastro mov) throws DAOException,
	NegocioException, ArqException {
		super.criar(mov);
		enviarEmailNotificacaoCoordenacao(mov);
	}
	
	/**
	 * Envia um email para o Coordenador do curso do discente, com as mensagens pendetes de moderação do forum associadas ao seu curso.
	 * 
	 * @param movimento
	 * @throws DAOException
	 */
	private void enviarEmailNotificacaoCoordenacao( Movimento movimento ) throws DAOException {
		
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		DenunciaMensagem denunciaMensagem = mov.getObjMovimentado();

		CoordenacaoCursoDao daoCoord = getDAO( CoordenacaoCursoDao.class, movimento );
		
		try{
			
			Collection<CoordenacaoCurso> coordenadores; 
			
			coordenadores = daoCoord.findCoordViceByCursoNivel(denunciaMensagem.getForumMensagem().getForum().getIdCursoCoordenador(), 
											denunciaMensagem.getForumMensagem().getForum().getNivel());
			
			for( CoordenacaoCurso cc : coordenadores ){
				
				if (cc.getEmailContato() != null ) {
					MailBody mail = new MailBody();
					//destinatário: usuário que cadastrou a solicitação
					//nome: nome do destinatário
					mail.setNome( cc.getServidor().getNome() );
					mail.setEmail(cc.getEmailContato());
					if (true){
						//assunto e mensagem para solicitação indeferida
						mail.setAssunto("SIGAA - Solicitação de moderação do conteúdo postado no fórum do curso "+cc.getCurso().toString());
						mail.setMensagem("Caro(a) " + cc.getServidor().getNome() +", \n\n" + 
								"Solicitação de moderação do conteúdo postado no fórum do curso," + cc.getCurso().toString() +
								"\n\nFórum:" + cc.getCurso().toString() +
								"\nTópico:" + denunciaMensagem.getForumMensagem().getTitulo() +
								"\nPostado por:" + denunciaMensagem.getForumMensagem().getUsuario().getNome() +
								"\nData: " +  CalendarUtils.format(denunciaMensagem.getForumMensagem().getData(), "dd/MM/yyyy HH:mm:ss") +
								"\nConteúdo da Postagem: " + denunciaMensagem.getForumMensagem().getConteudo() + 
								"\n\nSolicitante: " + denunciaMensagem.getRegistroCadastro().getUsuario().getNome() +
								"\nData: " +  CalendarUtils.format(denunciaMensagem.getData(), "dd/MM/yyyy HH:mm:ss") +
								"\nMotivo da Denúncia: " + denunciaMensagem.getMotivoDenuncia());
						
						//usuário que esta enviando e email para resposta.
						mail.setContentType(MailBody.TEXT_PLAN);
						Mail.send(mail);
					}
				}
			}
			
		}finally{
			daoCoord.close();
		}
	}

	/**
	 * Calcula o total de respostas do tópico do fórum
	 * @param topico
	 * @param mov
	 * @throws DAOException
	 */
	public void calculaTotalRespostas(ForumMensagem mensagem, Movimento mov) throws DAOException {

		ForumDao dao = getDAO(ForumDao.class, mov);
		
		try{
			if ( mensagem.getTopico() != null ) {
				// mensagens com tópico != null é que são comentários de tópicos
				dao.updateField(ForumMensagem.class, mensagem.getTopico().getId(), "respostas", 
						dao.findCountMensagensByTopico(mensagem.getTopico().getId()));
				dao.updateField(ForumMensagem.class, mensagem.getTopico().getId(), 
						"ultimaPostagem", new Date());
			}
		}finally {
			dao.close();
		}
		
	}

}
