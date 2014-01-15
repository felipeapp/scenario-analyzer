/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 23/12/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.dominio;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 *
 * <p> Classe responsável por enviar os emails da biblioteca. </p>
 * 
 * @author jadson
 *
 */
public final class EnvioEmailBiblioteca {

	/**
	 * Corres do tipo de email para aviso sobre os empréstimos, renovações, devoluções e estornos.
	 */
	public static final String[] AVISO_EMPRESTIMO = new String[]{"#333366", "#C8D5EC"};

	/**
	 * Corres do tipo de email para avisar sobre a prorrogação dos empréstimos
	 */
	public static final String[] AVISO_PRORROGACAO_EMPRESTIMO = new String[]{"#FFFFFF", "#4E8975"};
	
	/**
	 * Corres do tipo de email para avisar sobre o bloqueio de uma mateiral
	 */
	public static final String[] AVISO_BLOQUEIO_MATERIAL_EMPRESTADO = new String[]{"#000000", "#C0C0C0"};
	
	/**
	 * Corres do tipo de email para avisar sobre os empréstimos que estão prestes a vencer
	 */
	public static final String[] AVISO_EMPRESTIMO_VENCENDO = new String[]{"#000000", "#FFFFC6"};
	
	/**
	 * Corres do tipo de email para aviso sobre os empréstimos já vencidos.
	 */
	public static final String[] AVISO_EMPRESTIMO_VENCIDOS = new String[]{"#FFFFFF", "#CC0033"};
	
	/** 
	 * Cor de identificação dos emails da parde de informação e referência 
	 */
	public static final String[] AVISO_INFORMACAO_REFERENCIA = new String[]{"#000000", "#FFEFD5"};
	
	/**
	 * Corres do tipo de email para avisar sobre reservas
	 */
	public static final String[] AVISO_RESERVAS_MATERIAL = new String[]{"#FFFFFF", "#8B7D6B"};
	
	
	/**
	 * Sigla do sistema
	 */
	private String siglaSistema;
	
	/**
	 * Texto mostrado no rodapé
	 */
	private String textoRodape;
	
	
	public EnvioEmailBiblioteca(){
		siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		textoRodape = RepositorioDadosInstitucionais.get("rodapeSistema");
	}
	
	/**
	 *  <p>Envia um email com o template padrão da biblioteca mostrado abaixo:</p>
	 *
	 *  <table style="border: 1px solid #C8D5EC;">
	 *		<caption style="color: #FFFFFF; background-color: #000000;">tituloCorpoEmail<caption>
	 *  	<tbody>
	 *			<tr>
	 *				<td>  
	 *  				<p>Caro(a), nomeUsuario</p>                          
	 *
	 *  				<p>mensagemNivel1Email</p>
	 *  			</td>
	 *  		</tr>
	 *  	<tbody>
	 *	
	 *  	<tfoot>	
	 *			<tr>
	 *				<td>
	 *					<b>
	 *						<p> Não nos responsabilizamos pelo não recebimento deste email por qualquer motivo técnico.</p>
	 *					</b>
	 *				</td>
	 *			</tr>
	 *  	</tfoot>
	 *  
	 *  </table>
	 *  
	 *  <div style="padding-top: 30px; padding-bottom: 30px;  text-align: center; font-size: x-small;"> UXXX  | CopyRight 2xxx  </div>
	 *
	 * @throws DAOException
	 */
	public void enviaEmailSimples(Collection<UsuarioGeral> destinatarios
			, final String assuntoEmail, final String tituloCorpoEmail, final String[] coresEmail
			, final String mensagemNivel1Email) {
		for (UsuarioGeral destinatario : destinatarios) {
			enviaEmail(destinatario.getNome(), 
					destinatario.getEmail(), 
					assuntoEmail, 
					tituloCorpoEmail, 
					coresEmail, 
					null, 
					mensagemNivel1Email, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null, 
					null);
		}
	}
	
	/**
	 *  <p>Envia um email com o template padrão da biblioteca mostrado abaixo:</p>
	 *
	 *  <table style="border: 1px solid #C8D5EC;">
	 *		<caption style="color: #FFFFFF; background-color: #000000;">tituloCorpoEmail<caption>
	 *  	<tbody>
	 *			<tr>
	 *				<td>  
	 *  				<p>Caro(a), nomeUsuario</p>                          
	 *
	 *  				<p>mensagemNivel1Email</p>
	 *  			</td>
	 *  		</tr>
	 *  	<tbody>
	 *	
	 *  	<tfoot>	
	 *			<tr>
	 *				<td>
	 *					<b>
	 *						<p> Não nos responsabilizamos pelo não recebimento deste email por qualquer motivo técnico.</p>
	 *					</b>
	 *				</td>
	 *			</tr>
	 *  	</tfoot>
	 *  
	 *  </table>
	 *  
	 *  <div style="padding-top: 30px; padding-bottom: 30px;  text-align: center; font-size: x-small;"> UXXX  | CopyRight 2xxx  </div>
	 *
	 * @throws DAOException
	 */
	public void enviaEmailSimples(final String nomeUsuario, final String emailUsuario
			, final String assuntoEmail, final String tituloCorpoEmail, final String[] coresEmail
			, final String mensagemNivel1Email) {
		enviaEmail(nomeUsuario, 
				emailUsuario, 
				assuntoEmail, 
				tituloCorpoEmail, 
				coresEmail, 
				null, 
				mensagemNivel1Email, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null, 
				null);
	}

	/**
	 *   <p>Envia um email com o template padrão da biblioteca mostrado abaixo:</p>
	 *
	 *  <table style="border: 1px solid #C8D5EC;">
	 *                             <caption style="color: #FFFFFF; background-color: #000000;">tituloCorpoEmail<caption>
	 *  <tbody>
	 *	<tr>
	 *	<td>
	 *  
	 *  <p>Caro(a), nomeUsuario</p>                          
	 *  <p>mensagemSaudacaoUsuario</p>                             
	 *
	 *  <p>mensagemNivel1Email</p>  
	 *  <p>mensagemNivel2Email</p>  
	 *  <p><i>mensagemNivel3Email</i></p>  
	 *
	 *  <ul>
	 *  	<li>listaInformacoesEmail(0)</li>
	 *  	<li>listaInformacoesEmail(1)</li>
	 *  	<li>listaInformacoesEmail(N)</li>
	 *  </ul>
	 *  <p><b>mensagemAlertaCorpo</b></p>
	 *  <p>mensagemFechamento </p>
	 * 
	 *  </td>
	 *  </tr>
	 *  <tbody>
	 *	
	 *  <tfoot>
	 *  <tr>
	 *  <td>
	 *	<i><p>Código de Autenticação: codigoAutenticacaoEmail </p></i>
	 *	</td>
	 *  </tr>
	 *  		
	 *	<tr>
	 *	<td>
	 *	<b>
	 *	<p>mensagemAlertaRodape</p>
	 *	<p> Não nos responsabilizamos pelo não recebimento deste email por qualquer motivo técnico.</p>
	 *	</b>
	 *	</td>
	 *	</tr>
	 *  </tfoot>
	 *  
	 *  </table>
	 *  
	 *  <div style="padding-top: 30px; padding-bottom: 30px;  text-align: center; font-size: x-small;"> UXXX  | CopyRight 2xxx  </div>
	 *
	 * @param usuarioBibliotecaRenovacao
	 * @throws DAOException
	 */
	public void enviaEmail(Collection<UsuarioGeral> destinatarios
			, final String assuntoEmail, final String tituloCorpoEmail, final String[] coresEmail
			, final String mensagemSaudacaoUsuario 
			, final String mensagemNivel1Email, final String mensagemNivel2Email, final String mensagemNivel3Email
			, final List<String> listaInformacoesEmail
			, final String mensagemAlertaCorpo, final String mensagemFechamento
			, final String codigoAutenticacaoEmail, final String mensagemAlertaRodape, final Object[]... arquivosAnexo) {
		for (UsuarioGeral destinatario : destinatarios) {
			enviaEmail(destinatario.getNome(), 
					destinatario.getEmail(), 
					assuntoEmail, 
					tituloCorpoEmail, 
					coresEmail, 
					mensagemSaudacaoUsuario, 
					mensagemNivel1Email, 
					mensagemNivel2Email, 
					mensagemNivel3Email, 
					listaInformacoesEmail, 
					mensagemAlertaCorpo, 
					mensagemFechamento, 
					codigoAutenticacaoEmail, 
					mensagemAlertaRodape,
					arquivosAnexo);
		}
	}
	
	/**
	 *   <p>Envia um email com o template padrão da biblioteca mostrado abaixo:</p>
	 *
	 *  <table style="border: 1px solid #C8D5EC;">
	 *                             <caption style="color: #FFFFFF; background-color: #000000;">tituloCorpoEmail<caption>
	 *  <tbody>
	 *	<tr>
	 *	<td>
	 *  
	 *  <p>Caro(a), nomeUsuario</p>                          
	 *  <p>mensagemSaudacaoUsuario</p>                             
	 *
	 *  <p>mensagemNivel1Email</p>  
	 *  <p>mensagemNivel2Email</p>  
	 *  <p><i>mensagemNivel3Email</i></p>  
	 *
	 *  <ul>
	 *  	<li>listaInformacoesEmail(0)</li>
	 *  	<li>listaInformacoesEmail(1)</li>
	 *  	<li>listaInformacoesEmail(N)</li>
	 *  </ul>
	 *  <p><b>mensagemAlertaCorpo</b></p>
	 *  <p>mensagemFechamento </p>
	 * 
	 *  </td>
	 *  </tr>
	 *  <tbody>
	 *	
	 *  <tfoot>
	 *  <tr>
	 *  <td>
	 *	<i><p>Código de Autenticação: codigoAutenticacaoEmail </p></i>
	 *	</td>
	 *  </tr>
	 *  		
	 *	<tr>
	 *	<td>
	 *	<b>
	 *	<p>mensagemAlertaRodape</p>
	 *	<p> Não nos responsabilizamos pelo não recebimento deste email por qualquer motivo técnico.</p>
	 *	</b>
	 *	</td>
	 *	</tr>
	 *  </tfoot>
	 *  
	 *  </table>
	 *  
	 *  <div style="padding-top: 30px; padding-bottom: 30px;  text-align: center; font-size: x-small;"> UXXX  | CopyRight 2xxx  </div>
	 *
	 * @param usuarioBibliotecaRenovacao
	 * @throws DAOException
	 */
	public void enviaEmail(final String nomeUsuario, final String emailUsuario
			, final String assuntoEmail, final String tituloCorpoEmail, final String[] coresEmail
			, final String mensagemSaudacaoUsuario 
			, final String mensagemNivel1Email, final String mensagemNivel2Email, final String mensagemNivel3Email
			, final List<String> listaInformacoesEmail
			, final String mensagemAlertaCorpo, final String mensagemFechamento
			, final String codigoAutenticacaoEmail, final String mensagemAlertaRodape, final Object[]... arquivosAnexo){
		
		// Se o usuário não tiver nome, nem email não pode enviar o email
		if(StringUtils.isEmpty(emailUsuario))
			return;
		
		/////////////////////////////////////////////////////////////////////////////////
		// Validação dos dados obrigatórios que devem ser passados pelo programaador
		/////////////////////////////////////////////////////////////////////////////////
		if(StringUtils.isEmpty(assuntoEmail))
			throw new IllegalArgumentException("O assunto email deve ser informado! ");
		
		if(StringUtils.isEmpty(tituloCorpoEmail))
			throw new IllegalArgumentException("O título do corpo do email deve ser informado! ");		
		
		MailBody email = new MailBody();
		email.setAssunto("["+siglaSistema+"] "+assuntoEmail+" ");
		email.setContentType(MailBody.HTML);
		email.setNome(nomeUsuario);
		email.setEmail(emailUsuario);
		email.setReplyTo("noReply@ufrn.br");
		
		if(arquivosAnexo != null && arquivosAnexo.length > 0)
			email.setAttachments(Arrays.asList(arquivosAnexo));
		
		StringBuilder mensagem  = new StringBuilder("");
		
		mensagem.append("<html>");
		mensagem.append("<body>");
		
		mensagem.append("<div style=\"font-weight: bold; padding-top: 30px; padding-bottom: 30px; text-align: center;\"> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA DE BIBLIOTECAS DO "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. </div>");
		
		mensagem.append("<table style=\"width: 90%; margin-left: auto; margin-right: auto; border: 1px solid #C8D5EC;\">");
		mensagem.append("<caption style=\"font-weight: bold; color: "+coresEmail[0]+"; background-color: "+coresEmail[1]+"\" > "+tituloCorpoEmail+" </caption>");
					
		mensagem.append("<tbody>");
		mensagem.append("<tr>");
		mensagem.append("<td>");
		
		if(StringUtils.notEmpty(nomeUsuario))
			mensagem.append("<p> Caro(a) "+nomeUsuario+", </p>");
		
		/* *************************************************************
		 *  Mensagens opcionais que podem ser enviadas no corpo do email
		 * *************************************************************/
		
		if(StringUtils.notEmpty(mensagemSaudacaoUsuario))
			mensagem.append("<p> "+mensagemSaudacaoUsuario+" </p>");
		
		if(StringUtils.notEmpty(mensagemNivel1Email))
			mensagem.append("<p> "+mensagemNivel1Email+" </p>");
		
		if(StringUtils.notEmpty(mensagemNivel2Email))
			mensagem.append("<p> "+mensagemNivel2Email+" </p>");
		
		if(StringUtils.notEmpty(mensagemNivel3Email))
			mensagem.append("<p style=\"font-style: italic;\">"+mensagemNivel3Email+" </p>");
		
		
		/* *************************************************************
		 *  Mensagens opcionais em forma de lista enviadas no corpo do email
		 * *************************************************************/
		
		if(listaInformacoesEmail != null && listaInformacoesEmail.size() > 0){
			mensagem.append("<ul>");					
			for (String info : listaInformacoesEmail) {
				mensagem.append("<li> "+ info +"</li>");
			}											
			mensagem.append("</ul>");	
		}
		
		if(StringUtils.notEmpty(mensagemAlertaCorpo))
			mensagem.append("<p style=\"font-weight: bold; \">"+mensagemAlertaCorpo+"</p>");			
		if(StringUtils.notEmpty(mensagemFechamento))
			mensagem.append("<p>"+mensagemFechamento+"</p>");		
		
		mensagem.append("</td>");
		mensagem.append("</tr>");
		
		
		mensagem.append("</tbody>");

		mensagem.append(montaRodapeEmail(codigoAutenticacaoEmail, mensagemAlertaRodape));
		
			
		mensagem.append("</table>");
		
		mensagem.append("<div style=\"padding-top: 30px; padding-bottom: 30px;  text-align: center; width: 90%; font-size: x-small;\"> "+siglaSistema +" | "+ textoRodape +" </div>");
			
			
		mensagem.append("</body>");	
		mensagem.append("</html>");
		
		email.setMensagem(mensagem.toString());
		Mail.send(email);	
	}
	
	
	
	
	/**
	 *  Monta o Rodapé dos emais da biblioteca
	 *
	 * @return
	 */
	private String montaRodapeEmail(String codigoAutenticacaoEmail, String mensagemAlertaRodape){
		
		StringBuilder mensagem  = new StringBuilder("");
		
		mensagem.append("<tfoot>");
		
		if(StringUtils.notEmpty(codigoAutenticacaoEmail)){
			mensagem.append("<tr>");
			mensagem.append("<td style=\"text-align: center; font-style: italic;\">");
			mensagem.append("<p>Código de Autenticação: "+codigoAutenticacaoEmail+"</p>");
			mensagem.append("</td>");
			mensagem.append("</tr>");
		}
				
		mensagem.append("<tr>");
		mensagem.append("<td style=\"font-style: italic; font-weight: bold; padding-top: 30px; text-align: center; font-size: small;\">");
		if(StringUtils.notEmpty(mensagemAlertaRodape)){
			mensagem.append("<p> "+mensagemAlertaRodape+" </p>");
		}
		mensagem.append("<p> Não nos responsabilizamos pelo não recebimento deste e-mail por qualquer motivo técnico.</p>");
		mensagem.append("<p> O "+siglaSistema+" não envia e-mails solicitando senhas ou dados pessoais.</p>");
		mensagem.append("</td>");
		mensagem.append("</tr>");
				
		mensagem.append("</tfoot>");
		
		return mensagem.toString();
	}
	
	
}
