/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 11/06/2010
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *
 * <p>Thread que roda periodicamente enviando e-mail para os usuários da biblioteca
 * que possuem emprétimos em atraso.</p>
 *
 * <p><strong>Observação: </strong> Para testar o envio de email crie um link temporário em alguma página do sistema que estancie e inicie a thread.
 *     Não é possível testar o envio executando a classe de fora do servidor.
 * </p>
 *     
 * <p>
 * <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: 
 *  horaExecucao: 5h 
 *  diaExecucao: SEG = 2
 * </p>
 *     
 * <p><pre>
 *      NotificacaoEmprestimosEmAtrasoTimer a = new NotificacaoEmprestimosEmAtrasoTimer();
 *		a.start();
 *  </pre>
 * </p>
 * 
 * @author jadson
 *
 */
public class NotificacaoEmprestimosEmAtrasoTimer extends TarefaTimer {

	@Override
	public void run() {
		try {
			notificarEmprestimo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Método que realiza a consulta dos usuários que estão com emprétimos vencidos
	 *
	 */
	@SuppressWarnings("unchecked")
	public void notificarEmprestimo(){

		JdbcTemplate template = null;

		int qtdEmailsEnviados = 0;

		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");  // ex.: SIGAA
		String assuntoEmail = " Aviso de Débito na Biblioteca ";
		String tituloEmail = " Empréstimo com prazo vencido ";
		
		final int prazoUsuarioIncorreIregularidade = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_USUARIO_INCORRE_IRREGULARIDADE_ADMINISTRATIVA); 
		String mensagemAlertaRodape = "O não cumprimento dos prazos poderá acarretar em sanções de acordo com o regimento interno da biblioteca. ";
		
		// Se essa funcionalidade está ativa, avisa ao usuário para depois ele não poder alegar que não sabia //
		if(prazoUsuarioIncorreIregularidade > 0){
			mensagemAlertaRodape += ParametroHelper.getInstance().getParametro(ParametrosBiblioteca.MENSAGEM_ALERTA_EMAIL_EMPRESTIMOS_EM_ATRASO);
		}
		
		EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
		
		try {
			
			template = new JdbcTemplate( Database.getInstance().getSigaaDs());
			
			
			 ///// empréstimos de exemplares que vão vencer para pessoas  /////////
			@SuppressWarnings("rawtypes")
			Collection usuarios1 = template.queryForList(
			 " select  p.nome as nome_usuario,  u.email as email_usuario, emp.prazo as prazo , m.codigo_barras as codigoBarras, c.titulo as titulo, c.autor as autor "
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join comum.pessoa p on (p.id_pessoa = ub.id_pessoa) "
			+" inner join comum.usuario u on (p.id_pessoa = u.id_pessoa) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" inner join biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional "
			+" inner join biblioteca.titulo_catalografico t on e.id_titulo_catalografico = t.id_titulo_catalografico "
			+" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "
			+" where emp.data_devolucao is null and emp.data_estorno is null "
			+" and  emp.prazo < "+SQLDialect.now() );
			
			
			
			/////  empréstimos de fascículos que vão vencer para pessoas ///////////
			@SuppressWarnings("rawtypes")
			Collection usuarios2 = template.queryForList(
			" select  p.nome as nome_usuario,  u.email as email_usuario, emp.prazo as prazo , m.codigo_barras as codigoBarras, c.titulo as titulo, c.autor as autor "
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join comum.pessoa p on (p.id_pessoa = ub.id_pessoa) "
			+" inner join comum.usuario u on (p.id_pessoa = u.id_pessoa) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" inner join biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional "
			+" inner join biblioteca.assinatura a on f.id_assinatura = a.id_assinatura "
			+" inner join biblioteca.titulo_catalografico t on a.id_titulo_catalografico = t.id_titulo_catalografico "
			+" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "
			+" where emp.data_devolucao is null and emp.data_estorno is null "
			+" and  emp.prazo < "+SQLDialect.now() );
			
			
			/////////   empréstimos de exemplares que vão vencer para biblbiotecas  /////////////
			@SuppressWarnings("rawtypes")
			Collection usuarios3 = template.queryForList(
			" select b.descricao as nome_usuario, b.email as email_usuario, emp.prazo as prazo , m.codigo_barras as codigoBarras, c.titulo as titulo, c.autor as autor "
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join biblioteca.biblioteca b on (b.id_biblioteca = ub.id_biblioteca) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" inner join biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional "
			+" inner join biblioteca.titulo_catalografico t on e.id_titulo_catalografico = t.id_titulo_catalografico "
			+" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "
			+" where emp.data_devolucao is null and emp.data_estorno is null "
			+" and  emp.prazo < "+SQLDialect.now() );
			
			
			////////////////   empréstimos de fascículos que vão vencer para bibliotecas  //////////////
			@SuppressWarnings("rawtypes")
			Collection usuarios4 = template.queryForList(
			" select b.descricao as nome_usuario, b.email as email_usuario, emp.prazo as prazo , m.codigo_barras as codigoBarras, c.titulo as titulo, c.autor as autor "
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join biblioteca.biblioteca b on (b.id_biblioteca = ub.id_biblioteca) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" inner join biblioteca.fasciculo f on f.id_fasciculo = m.id_material_informacional "
			+" inner join biblioteca.assinatura a on f.id_assinatura = a.id_assinatura "
			+" inner join biblioteca.titulo_catalografico t on a.id_titulo_catalografico = t.id_titulo_catalografico "
			+" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "
			+" where emp.data_devolucao is null and emp.data_estorno is null "
			+" and  emp.prazo < "+SQLDialect.now() );
			
			
			
			@SuppressWarnings("rawtypes")
			Iterator it = usuarios1.iterator();
			while(it.hasNext()){
				Map<String, Object> mapa = (Map<String, Object>) it.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")
						+" \"</i> <strong>venceu no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo) +"</strong>.";
				String mensagemFechamento = "Por favor, devolva o material supracitado o mais breve possível.";
				
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
						, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCIDOS, mensagemUsuario, null, null, null, null
						,null, mensagemFechamento, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
			}
			
			@SuppressWarnings("rawtypes")
			Iterator it2 = usuarios2.iterator();
			while(it2.hasNext()){
				Map<String, Object> mapa = (Map<String, Object>) it2.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")
					+" \"</i> <strong>venceu no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo) +"</strong>.";
				String mensagemFechamento = "Por favor, devolva o material supracitado o mais breve possível.";
			
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
					, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCIDOS, mensagemUsuario, null, null, null, null
					,null, mensagemFechamento, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
			}
			
			@SuppressWarnings("rawtypes")
			Iterator it3 = usuarios3.iterator();
			while(it3.hasNext()){
				Map<String, Object> mapa = (Map<String, Object>) it3.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")
					+" \"</i> <strong>venceu no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo) +"</strong>.";
				String mensagemFechamento = "Por favor, devolva o material supracitado o mais breve possível.";
			
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
					, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCIDOS, mensagemUsuario, null, null, null, null
					,null, mensagemFechamento, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
			}
			
			@SuppressWarnings("rawtypes")
			Iterator it4 = usuarios4.iterator();
			while(it4.hasNext()){
				Map<String, Object> mapa = (Map<String, Object>) it4.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")
					+" \"</i> <strong>venceu no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo) +"</strong>.";
				String mensagemFechamento = "Por favor, devolva o material supracitado o mais breve possível.";
			
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
					, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCIDOS, mensagemUsuario, null, null, null, null
					,null, mensagemFechamento, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
			}

			/**
			 * Envia um email para a administração avisando, quantos email foram enviados.
			 */
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "NOTIFICACAO DOS EMPRESTIMOS EM ATRASO - EXECUTADO EM " + new Date();
			String mensagem = "Server: " + NetworkUtils.getLocalName() + "<br>Total de email enviados: " + qtdEmailsEnviados; 
			MailBody mail = new MailBody();
			mail.setEmail(email);
			mail.setAssunto(assunto);
			mail.setMensagem(mensagem);
			
			Mail.send(mail);

				
				
		} catch (Exception e) {
			
			/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
			e.printStackTrace();
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "Erro "+siglaSigaa+" - NOTIFICACAO DOS EMPRESTIMOS EM ATRASO : " + e.getMessage();
			String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");
			//enviando email para administracao do sistema para notificar do erro
			MailBody mail = new MailBody();
			mail.setEmail( email );
			mail.setAssunto(assunto);
			mail.setMensagem( mensagem );
			Mail.send(mail);
			
		} finally {
			try {
				
				if(template != null) template.getDataSource().getConnection().close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
				e.printStackTrace();
				String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
				String assunto = "Erro "+siglaSigaa+" -  NOTIFICACAO DOS EMPRESTIMOS EM ATRASO: Não foi possível fechar a conexão do JdbcTemplate ";
				String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
				e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
				(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");
				//enviando email para administracao do sistema para notificar do erro
				MailBody mail = new MailBody();
				mail.setEmail( email );
				mail.setAssunto(assunto);
				mail.setMensagem( mensagem );
				Mail.send(mail);
				
			}
			
		}
		
	}
	
}
