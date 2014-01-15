/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/07/2012
 * 
 */
package br.ufrn.sigaa.extensao.helper;

import java.util.List;

import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;

/**
 * <p>Classe utilitária para envio de emails no sigEventos </p>
 * 
 * @author jadson
 *
 */
public class EnviarEmailExtensaoHelper {

	
	
	/**
	 * Formatodor padrão de emais do sigEventos.
	 *
	 * @param assuntoEmail
	 * @param cabecalhoEmail
	 * @param nomeUsuario
	 * @param emailUsuario
	 * @param ccs
	 * @param mensagemEmail
	 */
	public static void enviarEmail(String assuntoEmail, String cabecalhoEmail, String nomeUsuario, String emailUsuario, String mensagemEmail, List<String> ccs, List<String> bccs){
		
		
		String siglaSistema =  RepositorioDadosInstitucionais.get("siglaSigaa");
		String textoRodape = RepositorioDadosInstitucionais.get("rodapeSistema");
		
		MailBody email = new MailBody();
		
		email.setAssunto(assuntoEmail);
		email.setContentType(MailBody.HTML);
		email.setReplyTo("noReply@noReply.br");
		
		if(nomeUsuario != null && emailUsuario != null){
			email.setNome(nomeUsuario);
			email.setEmail(emailUsuario);
		}
			
		boolean temCcs = false;
		
		if(ccs != null && ccs.size() > 0 ){
			 temCcs = true;
			
			StringBuilder ccFormatado = new StringBuilder(); 
			
			for (String cc : ccs) {
				ccFormatado.append(cc+";"); // separe os email de cópia por vírgula
			}
			
			email.setCc(ccFormatado.toString());
		}
		
		boolean temBccs = false;
		
		if(bccs != null && bccs.size() > 0 ){
			temBccs = true;
			
			StringBuilder bccFormatado = new StringBuilder(); 
			
			for (String bcc : bccs) {
				bccFormatado.append(bcc+";"); // separe os email de cópia por vírgula
			}
			
			email.setBcc(bccFormatado.toString());
		}	
		
		
		//   Monta a mensgem do email //
		StringBuilder mensagem  = new StringBuilder("");
		
		mensagem.append("<html>");
		mensagem.append("<body>");
		
		mensagem.append("<div style=\"font-weight: bold; padding-top: 30px; padding-bottom: 30px; text-align: center;\"> ESTE E-MAIL FOI GERADO AUTOMATICAMENTE PELO SISTEMA "+siglaSistema+". POR FAVOR, NÃO RESPONDÊ-LO. </div>");
		
		mensagem.append("<table style=\"width: 90%; margin-left: auto; margin-right: auto; border: 1px solid #C8D5EC;\">");
		mensagem.append("<caption style=\"font-weight: bold; color: #333366; background-color: #C8D5EC\" > "+cabecalhoEmail+" </caption>");
					
		mensagem.append("<tbody>");
		mensagem.append("<tr>");
		mensagem.append("<td>");
		
		if( ! temCcs && !temBccs) // email pessoal
			mensagem.append(" Prezado "+nomeUsuario+", <br/><br/>");
			
		
		mensagem.append(mensagemEmail);
			
			
		mensagem.append("</td>");
		mensagem.append("</tr>");
		
		mensagem.append("</tbody>");

		mensagem.append("<tfoot>");
		mensagem.append("<tr>");
		mensagem.append("<td style=\"font-style: italic; font-weight: bold; padding-top: 30px; text-align: center; font-size: small;\">");
		mensagem.append("<p> Não nos responsabilizamos pelo não recebimento deste e-mail por qualquer motivo técnico.</p>");
		mensagem.append("<p> O "+siglaSistema+" não envia e-mails solicitando senhas ou dados pessoais.</p>");
		mensagem.append("</td>");
		mensagem.append("</tr>");
		mensagem.append("</tfoot>");
		
		mensagem.append("</table>");
		
		mensagem.append("<div style=\"padding-top: 30px; padding-bottom: 30px;  text-align: center; width: 90%; font-size: x-small;\"> "+siglaSistema +" | "+ textoRodape +" </div>");
				
		mensagem.append("</body>");	
		mensagem.append("</html>");
			
		email.setMensagem(mensagem.toString());
		Mail.send(email);	
	}
}
