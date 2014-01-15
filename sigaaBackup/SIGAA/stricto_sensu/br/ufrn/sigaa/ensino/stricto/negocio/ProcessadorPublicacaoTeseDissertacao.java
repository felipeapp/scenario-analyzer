/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 *
 * Created on 24/05/2010
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.io.IOException;
import java.rmi.RemoteException;

import org.apache.myfaces.custom.fileupload.UploadedFile;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.caixa_postal.Mensagem;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.TermoAutorizacaoPublicacaoTeseDissertacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.stricto.dominio.TermoAutorizacaoPublicacaoTeseDissertacao;

/**
 * Processador para realiza��o de opera��es relacionadas a publica��o 
 * de Teses e Disserta��es de p�s-gradua��o stricto-sensu.
 *
 * @author Arlindo Rodrigues
 *
 */
public class ProcessadorPublicacaoTeseDissertacao extends AbstractProcessador{

	/**
	 * M�todo invocado pela arquitetura
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException,	RemoteException {		
		validate(mov);

		salvarTermo(mov);					
		
		return null;
	}
	
	/**
	 * Salva o Termo de Publica��o de Tese/Disserta��o
	 * @param mov
	 * @throws DAOException
	 * @throws ArqException
	 * @throws NegocioException
	 * @throws RemoteException 
	 */
	private void salvarTermo(Movimento mov) throws DAOException, ArqException,	NegocioException, RemoteException {
		TermoAutorizacaoPublicacaoTeseDissertacaoDao dao = getDAO(TermoAutorizacaoPublicacaoTeseDissertacaoDao.class, mov);
		MovimentoTermoPublicacaoTeseDissertacao pMov = (MovimentoTermoPublicacaoTeseDissertacao) mov;
		
		try {
			if (ValidatorUtil.isEmpty(pMov.getTermoAutorizacaoPublicacao().getInstituicaoFomento()))
				pMov.getTermoAutorizacaoPublicacao().setInstituicaoFomento(null);		
			
			if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO) ||
					mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO)){						
				if (pMov.getArquivo() != null){				
					salvarArquivo(pMov);
					dao.update(pMov.getTermoAutorizacaoPublicacao().getBanca().getDadosDefesa());				
				}
				
				if (!pMov.getTermoAutorizacaoPublicacao().isParcial())
					pMov.getTermoAutorizacaoPublicacao().setRestricoes(null);
				
				dao.createOrUpdate(pMov.getTermoAutorizacaoPublicacao());
				
//				if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO))						
//					notificar(criarMensagemPublicacao(pMov), new Object[]{findOrientador(pMov)});
				
			} else if (mov.getCodMovimento().equals(SigaaListaComando.HOMOLOGAR_SOLICITACAO_PUBLICACAO_TESE_DISSERTACAO)) {
				if (pMov.getTermoAutorizacaoPublicacao().getStatus() == TermoAutorizacaoPublicacaoTeseDissertacao.REPROVADO)
					pMov.getTermoAutorizacaoPublicacao().setAtivo(false);
				
				dao.update(pMov.getTermoAutorizacaoPublicacao());
				
				notificar(criarMensagemHomologacao(pMov), new Object[]{findDiscente(pMov)});
			} else {
				if (mov.getCodMovimento().equals(SigaaListaComando.PUBLICAR_TESE_DISSERTACAO)) {		
					String url = pMov.getTermoAutorizacaoPublicacao().getUrlBDTD();
					if (url.toLowerCase().indexOf("http://") < 0 && url.toLowerCase().indexOf("https://") < 0)
						url = "http://"+url;				
					pMov.getTermoAutorizacaoPublicacao().getBanca().getDadosDefesa().setLinkArquivo(url);
					dao.update(pMov.getTermoAutorizacaoPublicacao().getBanca().getDadosDefesa());
				}
				
				dao.createOrUpdate(pMov.getTermoAutorizacaoPublicacao());
			}
		} finally {
			dao.close();
		}
	}
	
	/**
	 * Salva o trabalho do aluno
	 * 
	 * @param hMov
	 * @param homologacao
	 * @throws IOException
	 * @throws DAOException 
	 */
	private void salvarArquivo(MovimentoTermoPublicacaoTeseDissertacao mov) throws DAOException {
		try {
			UploadedFile arquivo = mov.getArquivo();
			int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
			EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(), arquivo.getContentType(), arquivo.getName());
			
			mov.getTermoAutorizacaoPublicacao().getBanca().getDadosDefesa().setIdArquivo(idArquivo);
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}	
	
	/**
	 * Cria a estrutura da mensagem de notifica��o
	 * @param titulo
	 * @param conteudo
	 * @return
	 */
	private Mensagem criarMensagem(String titulo, String conteudo){
		Mensagem mensagem = new Mensagem();
		mensagem.setAutomatica(true);
		mensagem.setTitulo(titulo.toUpperCase());
		mensagem.setMensagem(conteudo);	
		return mensagem;
	}
	
	/**
	 * Notifica os Usu�rios que foi criado uma solicita��o de reposi��o de prova.
	 * @param rpMov
	 * @param destinatarios
	 * @throws RemoteException  
	 * @throws NegocioException 
	 */
	private void notificar(Mensagem mensagem, Object[] usuarios) throws ArqException, NegocioException, RemoteException {				
		if (usuarios == null) {
			throw new IllegalArgumentException();
		}
		
		// enviando e-mail.
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
		for (Object object : usuarios) {	
			Usuario usuario = (Usuario) object;
			if (usuario != null){
				MailBody email = new MailBody();
				email.setAssunto("["+siglaSigaa+"] "+mensagem.getTitulo());
				email.setContentType(MailBody.HTML);
				
				email.setNome(usuario.getNome());
				email.setEmail(usuario.getEmail());
				email.setMensagem(mensagem.getMensagem());
				Mail.send(email);									
			}
		}			
	}	
	
	/**
	 * Cria a Mensagem que ser� enviada para o Docente (Orientador), informando que foi criado uma
	 * solicita��o de publica��o de tese/disserta��o.
	 * @param rp
	 * @return
	 */
	private Mensagem criarMensagemPublicacao(MovimentoTermoPublicacaoTeseDissertacao mov){
		String discente = mov.getTermoAutorizacaoPublicacao().getDiscente().getNome();
		String matricula = String.valueOf( mov.getTermoAutorizacaoPublicacao().getDiscente().getMatricula() );
		String programa = mov.getTermoAutorizacaoPublicacao().getDiscente().getUnidade().getNome();
		
		String titulo = "Termo de Autoriza��o de Publica��o de Tese/Disserta��o";
		String conteudo = "Caro Orientador,";
		
		conteudo += "<p>O discente "+discente+" de matr�cula N� "+matricula+", do Programa de "+
					programa+", enviou o Termo de Autoriza��o de Publica��o de Tese/Disserta��o, para que seja analisado.</p>";		
		
		return criarMensagem(titulo, conteudo); 
	}		
	
	
	/**
	 * Cria a Mensagem que ser� enviada para o Discente ao ser Homologado o Termo.
	 * @param rp
	 * @return
	 */
	private Mensagem criarMensagemHomologacao(MovimentoTermoPublicacaoTeseDissertacao mov){
		String titulo = "Termo de Autoriza��o de Publica��o de Tese/Disserta��o";
		String conteudo = "Caro Aluno,";
		
		String situacao = "";
		String reprovacao = "";
		if (mov.getTermoAutorizacaoPublicacao().isAprovado())
			situacao = "APROVOU";
		else {
			situacao = "REPROVOU";
			reprovacao = "<p>Sua solicita��o atual ser� desativada, assim sendo necess�rio a cria��o e uma nova para avalia��o.</p>";
		}
		conteudo += "<p>Seu Orientador, <b>"+situacao+"</b> sua solicita��o de Publica��o de Tese/Disserta��o.</p>";
		
		conteudo += reprovacao;
		
		return criarMensagem(titulo, conteudo); 
	}	
	
	
	/**
	 * Retorna o Usu�rio do Orientador do Discente.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Usuario findOrientador(MovimentoTermoPublicacaoTeseDissertacao mov) throws DAOException {		
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		Usuario usuario = null;
		try{
			if (ValidatorUtil.isNotEmpty(mov.getTermoAutorizacaoPublicacao().getDiscente().getOrientacao())){
				usuario = uDao.findPrimeiroUsuarioByPessoa(mov.getTermoAutorizacaoPublicacao().getDiscente().getOrientacao().getPessoa().getId());
				if (ValidatorUtil.isEmpty( usuario ) && !ValidatorUtil.isEmpty( mov.getTermoAutorizacaoPublicacao().getDiscente().getOrientacao().getPessoa().getEmail())){
					usuario = new Usuario();
					usuario.setNome(mov.getTermoAutorizacaoPublicacao().getDiscente().getOrientacao().getPessoa().getNome());
					usuario.setEmail(mov.getTermoAutorizacaoPublicacao().getDiscente().getOrientacao().getPessoa().getEmail());
				}
			}
		} finally {
			if (uDao != null)
				uDao.close();
		}
		return usuario;
	}	
	
	/**
	 * Retorna o Usu�rio do Discente.
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private Usuario findDiscente(MovimentoTermoPublicacaoTeseDissertacao mov) throws DAOException {		
		UsuarioDao uDao = getDAO(UsuarioDao.class, mov);
		Usuario usuario = null;
		try{
			usuario = uDao.findByMatriculaDiscente(mov.getTermoAutorizacaoPublicacao().getDiscente().getMatricula());					
		} finally {
			if (uDao != null)
				uDao.close();
		}
		return usuario;
	}		
	
	/**
	 * Valida os campos.
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {

	}
	

}
