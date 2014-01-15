/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 05/07/2006
 */
package br.ufrn.arq.email;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import br.ufrn.arq.dao.TemplateDocumentoDao;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.templates.TemplateDocumento;
import br.ufrn.comum.dominio.UsuarioGeral;


/**
 * Classe que contém os métodos estáticos para envio de e-mail.
 * 
 * @author Gleydson Lima
 */
public class Mail {

	/**
	 * Endereço de resposta do e-mail
	 */
	public static final String DEFAULT_REPLY_TO = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.DEFAULT_REPLY_TO);
	
	/** Contador de Threads */
	public static int msgAdded = 0;

	public static int msgDelivery = 0;

	/** Constantes para tipo da mensagem **/
	public static final int OUTROS = 0;

	/** Mensagem é um aviso sobre requisição */
	public static final int REQUISICAO = 1;

	/** Mensagem é um aviso sobre tombamento */
	public static final int TOMBAMENTO = 2;

	/** Mensagem é uma mensagem da caixa postal */
	public static final int CAIXA_POSTAL = 3;

	/** Menagem é sobre uma requisião pra unidade */
	public static final int REQUISICAO_UNIDADE = 4;

	/**
	 * fila de envio de e-mails
	 */
	public static Queue<MailBody> queue = new LinkedList<MailBody>();

	/** Creates a new instance of Mail */

	public Mail() {

	}

	/**
	 *
	 * @param nome
	 *            nome do destinatário da mensagem
	 * @param email
	 * @param assunto
	 * @param mensagem
	 */
	public static void sendMessage(String nome, String email, String assunto, String mensagem) {
		enfileira(nome, email, assunto, mensagem, MailBody.HTML, null, null);
	}

	public static void sendMessage(String nome, String email, String assunto, String mensagem, String tipo) {
		enfileira(nome, email, assunto, mensagem, tipo, null, null);
	}

	public static void sendMessage(String nome, String email, String assunto, String mensagem, String tipo, String replyTo, String confirmacaoLeitura) {
		enfileira(nome, email, assunto, mensagem, tipo, replyTo, confirmacaoLeitura);
	}
	
	private static void enfileira(String nome, String email, String assunto, String mensagem, String tipo, String replyTo, String confirmacaoLeitura) {
		enfileira(nome, email, assunto, mensagem, tipo, replyTo, confirmacaoLeitura, null);
	}
	
	private static void enfileira(String nome, String email, String assunto, String mensagem, String tipo, String replyTo, String confirmacaoLeitura, List<Object[]> anexos) {
		MailBody mail = new MailBody();

		if (!isEmpty(nome != null)) {
			try {
				mail.setNome(URLEncoder.encode(nome, "US-ASCII"));
			} catch (Exception e) { }
		}
		
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		if (!isEmpty(tipo))
			mail.setContentType(tipo);
		mail.setReplyTo(replyTo);
		mail.setConfirmacaoLeitura(confirmacaoLeitura);

		if(anexos != null)
			mail.setAttachments(anexos);
		
		
		synchronized (queue) {
			msgAdded++;
			queue.add(mail);
			queue.notify();
		}

	}

	/**
	 * 
	 * @param verifica os emails enviados e adiciona a fila de envios de email
	 */
	public static void send(MailBody mail) {
		synchronized (queue) {
			msgAdded++;
			queue.add(mail);
			queue.notify();
		}
	}

	/** Envia email de alerta para os administradores do sistema */
	public static void sendAlerta(String alerta) {

		boolean enviarEmail = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.EMAIL_DE_ALERTA);

		if (enviarEmail) {
			MailBody mail = new MailBody();
			mail.setAssunto("ALERTA");
			mail.setNome("Sistemas/" + RepositorioDadosInstitucionais.getSiglaInstituicao() + "/" + RepositorioDadosInstitucionais.get("nomeResponsavelInformatica"));
			mail.setEmail(ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR));
			mail.setRegistrarEnvio(false);
			mail.setMensagem(alerta);

			synchronized (queue) {

				msgAdded++;
				queue.add(mail);
				queue.notify();

			}
		}

	}

	/**
	 * Envia email utilizando template definido em arquivo de propriedades para
	 * o usuário passado como parametro.
	 *
	 * @param key
	 *            chave do template no arquivo de propriedades.
	 * @param usuario
	 *            destinatátio
	 * @param args
	 */
	public static void sendMessage(String key, UsuarioGeral usuario, Object... args) {
		MailGenerator mailGenerator = MailGenerator.getInstance();

		String mensagem = mailGenerator.getMensagem(key + ".mensagem", args) + mailGenerator.getMensagem("mail.rodape", RepositorioDadosInstitucionais.get("nomeInstituicao"));
		String assunto = mailGenerator.getMensagem(key + ".assunto", args);

		enfileira(usuario.getNome(), usuario.getEmail(), assunto, mensagem, MailBody.HTML, null, null);
	}

	/**
	 * Envia email utilizando template definido em arquivo de propriedades para
	 * o email passado como parametro.
	 *
	 * @param key
	 *            chave do template no arquivo de propriedades.
	 * @param email
	 *            destinatátio
	 * @param args
	 */
	public static void sendMessage(String key, String email, Object... args) {
		MailGenerator mailGenerator = MailGenerator.getInstance();

		String mensagem = mailGenerator.getMensagem(key + ".mensagem", args) + mailGenerator.getMensagem("mail.rodape", RepositorioDadosInstitucionais.get("nomeInstituicao"));
		String assunto = mailGenerator.getMensagem(key + ".assunto", args);

		enfileira("", email, assunto, mensagem, MailBody.HTML, null, null);
	}

	/**
	 * Envia email utilizando template definido em arquivo de propriedades para
	 * o email passado como parametro setando reply especificado.
	 *
	 * @param key
	 *            chave do template no arquivo de propriedades.
	 * @param email
	 *            destinatátio
	 * @pram reply endereço de retorno.
	 * @param args
	 *
	 */
	public static void sendMessageWithReply(String key, String email, String reply, Object... args) {
		MailGenerator mailGenerator = MailGenerator.getInstance();

		String mensagem = mailGenerator.getMensagem(key + ".mensagem", args) + mailGenerator.getMensagem("mail.rodape", RepositorioDadosInstitucionais.get("nomeInstituicao"));
		String assunto = mailGenerator.getMensagem(key + ".assunto", args);

		enfileira("", email, assunto, mensagem, null, reply, null);
	}

	public static void sendMessageWithReply(String nome, String email, String assunto, String mensagem, String reply) {
		enfileira(nome, email, assunto, mensagem, null, reply, null);
	}

	/**
	 * Método para enviar alertas para administrador em caso de falhas críticas
	 * do sistema. Nenhuma exceção é disparada.
	 *
	 * @param mansagem
	 *            mensagem de alerta
	 * @param erro
	 *            exceção disparada. Se for diferente de null as informações da
	 *            exceção são adicionadas à mensagem
	 */
	public static void sendAlertaAdmin(String mensagem, Throwable erro) {
		String mailAdmin = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR);

		try {
			String assunto = "Falha de sistema " + erro;
			mensagem += " \n\n" + "Data: " + new Date();
			if (erro != null) {
				mensagem += " \n\n" + erro;
				mensagem += " \n\n" + Arrays.toString(erro.getStackTrace());
			}

			sendMessage("Admistrador Sistemas", mailAdmin, assunto, mensagem);
		} catch (Exception e) {
			System.err.println("Erro ao enviar alerta para administrador: " + e.getMessage());
			e.printStackTrace();
		}
	}
	
	private static TemplateDocumento recuperaTemplate(String template, Map<String, String> params) {
		TemplateDocumentoDao dao = null;
		
		try {
			dao = new TemplateDocumentoDao();
			TemplateDocumento temp = dao.findByCodigo(template);
			if (temp != null) temp.substituirVariaveis(params);
			return temp;
		} catch(DAOException e) {
			e.printStackTrace();
			return null;
		} finally {
			if (dao != null) dao.close();
		}
		
	}
	
	/**
	 * Envia um e-mail com o template informado com o parâmetro, substituindo 
	 * as variáveis do template pelas variáveis definidas no mapa de parâmetros.
	 * 
	 * @param nome
	 * @param email
	 * @param template
	 * @param params
	 */
	public static void enviaComTemplate(String nome, String email, String template, Map<String, String> params) {
		enviaComTemplate(nome, email, template, params, null);
	}
	
	
	
	/**
	 * Envia um e-mail com o template informado com o parâmetro, substituindo 
	 * as variáveis do template pelas variáveis definidas no mapa de parâmetros.
	 * 
	 * @param nome
	 * @param email
	 * @param template
	 * @param params
	 */
	public static void enviaComTemplate(String nome, String email, String template, Map<String, String> params, List<Object[]> anexos) {
		TemplateDocumento temp = recuperaTemplate(template, params);
		if (temp != null) {
			if(anexos != null)
				enfileira(nome, email, temp.getTitulo(), temp.getTexto(), MailBody.HTML, null, null, anexos);
			else
				sendMessage(nome, email, temp.getTitulo(), temp.getTexto());
		}
	}
	
	/**
	 * Envia um e-mail com o template informado como parâmetro, substituindo 
	 * as variáveis do template pelas variáveis definidas no mapa de parâmetros.
	 * É possível também informar qual o email de resposta através do parâmetro
	 * reply. 
	 * 
	 * @param nome
	 * @param email
	 * @param reply
	 * @param template
	 * @param params
	 */
	public static void enviaComTemplateReply(String nome, String email, String reply, String template, Map<String, String> params) {
		TemplateDocumento temp = recuperaTemplate(template, params);
		if (temp != null) sendMessageWithReply(nome, email, temp.getTitulo(), temp.getTexto(), reply);
	}
	
	/**
	 * Envia um e-mail com o template informado como parâmetro, substituindo 
	 * as variáveis do template pelas variáveis definidas no mapa de parâmetros.
	 * É possível também informar qual o email de resposta através do parâmetro
	 * reply. 
	 * 
	 * @param nome
	 * @param email
	 * @param reply
	 * @param template
	 * @param params
	 */
	public static void enviaComTemplate(String nome, String email, String reply, String confirmacaoResposta, String template, Map<String, String> params) {
		TemplateDocumento temp = recuperaTemplate(template, params);
		if (temp != null) sendMessage(nome, email, temp.getTitulo(), temp.getTexto(), null, reply, confirmacaoResposta);
	}

	/**
	 * Valida endereços de mail
	 *
	 * @param email
	 * @return true se email for válido
	 */
	public static boolean validaEmail(String email) {
		return (email != null && email.length() > 0
				&& email.trim().equals(email) && email.contains(".")
				&& email.contains("@") && !email.contains(" "));
	}

}