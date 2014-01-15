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
import br.ufrn.sigaa.biblioteca.circulacao.dominio.Emprestimo;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.parametros.dominio.ParametrosBiblioteca;

/**
 *     <p>Thread que roda periodicamente enviando e-mail para os usuários da biblioteca
 * que estão a <code>ParametrosBiblioteca.PRAZO_ENVIO_EMAIL_EMPRESTIMO_VENCENDO</code> dias ou menos 
 * da data de entrega do livro.</p>
 * 
 *     <p><strong>Observação: </strong> Para testar o envio de email crie um link temporário em alguma página do sistema que estancie e inicie a thread.
 *     Não é possível testar o envio executando a classe de fora do servidor.
 *     </p>
 *     
 *     <p>
 *     <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: 
 *     horaExecucao: 3h 
 *     tipoReplicacao: D = Diário
 *    </p>
 *     
 *     <p><pre>
 *		NotificacaoEmprestimoPertoDeVencerTimer a = new NotificacaoEmprestimoPertoDeVencerTimer();
 *		a.start();
 *     </pre>
 *     </p>
 *     
 *    
 * 
 * @author Jean Guerethes
 * 
 */
public class NotificacaoEmprestimoPertoDeVencerTimer extends TarefaTimer {
	
	
	@Override
	public void run() {
		try {
			notificarEmprestimo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	/**
	 * Método que realiza a consulta dos usuários que estão com emprétimos prestes a vencer.
	 *
	 */
	@SuppressWarnings("unchecked")
	public void notificarEmprestimo(){

		JdbcTemplate template = null;

		int qtdEmailsEnviados = 0;
		
		final int prazoEnvioEmail = ParametroHelper.getInstance().getParametroInt(ParametrosBiblioteca.PRAZO_ENVIO_EMAIL_EMPRESTIMO_VENCENDO); 
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");  // ex.: SIGAA
		String siglaInstituicao = RepositorioDadosInstitucionais.get("siglaInstituicao");  // ex.: UFRN
		
		/**
		 * Buscará por todos os empréstimos realizados, que esteja faltando 2 dias para o encerramento do prazo. 
		 */
		try {
			
			template = new JdbcTemplate( Database.getInstance().getSigaaDs());
			
			
			String assuntoEmail = " Aviso de Prazo do Empréstimo ";
			String tituloEmail = " Prazo do Empréstimo Vencendo ";
			String mensagemAlertaRodape =  "Esta mensagem é de caráter apenas informativo e um serviço extra prestado pelo sistema de biblioteca da "+siglaInstituicao+". Não desobriga o usuário de verificar os prazos de vencimento dos seus empréstimos.";
			String mensagemNivel2Email = "Não se esqueça de devolvê-lo ou renová-lo.";
			
			EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
			
			
			
			
			 ///// empréstimos de exemplares que vão vencer para pessoas  /////////
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
			+" where emp.situacao =  "+Emprestimo.EMPRESTADO
			+" and  EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) <= "+prazoEnvioEmail
			+" and EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) >= 0 ");
			
			
			/////  empréstimos de fascículos que vão vencer para pessoas ///////////
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
			+" where emp.situacao =  "+Emprestimo.EMPRESTADO
			+" and  EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) <= "+prazoEnvioEmail
			+" and EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) >= 0 ");
			
			
			/////////   empréstimos de exemplares que vão vencer para biblbiotecas  /////////////
			Collection usuarios3 = template.queryForList(
			" select b.descricao as nome_usuario, b.email as email_usuario, emp.prazo as prazo , m.codigo_barras as codigoBarras, c.titulo as titulo, c.autor as autor "
			+" from biblioteca.emprestimo emp "
			+" inner join biblioteca.usuario_biblioteca ub on emp.id_usuario_biblioteca = ub.id_usuario_biblioteca "
			+" inner join biblioteca.biblioteca b on (b.id_biblioteca = ub.id_biblioteca) "
			+" inner join biblioteca.material_informacional m on emp.id_material = m.id_material_informacional "
			+" inner join biblioteca.exemplar e on e.id_exemplar = m.id_material_informacional "
			+" inner join biblioteca.titulo_catalografico t on e.id_titulo_catalografico = t.id_titulo_catalografico "
			+" inner join biblioteca.cache_entidades_marc c on c.id_titulo_catalografico = t.id_titulo_catalografico "
			+" where emp.situacao = "+Emprestimo.EMPRESTADO
			+" and  EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) <= "+prazoEnvioEmail
			+" and EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) >= 0 ");
			
			
			////////////////   empréstimos de fascículos que vão vencer para bibliotecas  //////////////
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
			+" where emp.situacao = "+Emprestimo.EMPRESTADO
			+" and  EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) <= "+prazoEnvioEmail
			+" and EXTRACT(DAYS FROM (emp.prazo - "+SQLDialect.now()+" )) >= 0 ");
			
			
			
			Iterator it = usuarios1.iterator();
			while(it.hasNext()){
				Map<String, Object> mapa = (Map) it.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")+" \"</i> ,";
				String mensagemNivel1Email = " <strong>vencerá no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo)+"</strong>.";
				
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
						, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCENDO, mensagemUsuario, mensagemNivel1Email, mensagemNivel2Email, null, null
						,null, null, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
				
				
			}
			
			
			Iterator it2 = usuarios2.iterator();
			while(it2.hasNext()){
				Map<String, Object> mapa = (Map) it2.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")+" \"</i> ,";
				String mensagemNivel1Email = " <strong>vencerá no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo)+"</strong>.";
				
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
						, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCENDO, mensagemUsuario, mensagemNivel1Email, mensagemNivel2Email, null, null
						,null, null, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
				
			}
			
			
			Iterator it3 = usuarios3.iterator();
			while(it3.hasNext()){
				Map<String, Object> mapa = (Map) it3.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")+" \"</i> ,";
				String mensagemNivel1Email = " <strong>vencerá no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo)+"</strong>.";
				
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
						, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCENDO, mensagemUsuario, mensagemNivel1Email, mensagemNivel2Email, null, null
						,null, null, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
				
			}
			
			
			Iterator it4 = usuarios4.iterator();
			while(it4.hasNext()){
				Map<String, Object> mapa = (Map) it4.next();
				String nomeUsuario = (String) mapa.get("nome_usuario");
				String emailUsuario = (String) mapa.get("email_usuario");
				Date prazo = (Date) mapa.get("prazo");
				String codigoBarras = (String) mapa.get("codigoBarras");
				String titulo = (String) mapa.get("titulo");
				String autor = (String) mapa.get("autor");
				
				String mensagemUsuario = "O empréstimo do material: "+codigoBarras+" - <i>\""+ (StringUtils.notEmpty(autor) ? autor: " ")+" "+ (StringUtils.notEmpty(titulo) ? titulo: " ")+" \"</i> ,";
				String mensagemNivel1Email = " <strong>vencerá no dia "+new SimpleDateFormat("dd/MM/yyyy HH:mm").format(prazo)+"</strong>.";
				
				sender.enviaEmail( nomeUsuario, emailUsuario, assuntoEmail, tituloEmail
						, EnvioEmailBiblioteca.AVISO_EMPRESTIMO_VENCENDO, mensagemUsuario, mensagemNivel1Email, mensagemNivel2Email, null, null
						,null, null, null, mensagemAlertaRodape);
				
				qtdEmailsEnviados++;
				
			}

			/**
			 * Envia um email para a administração avisando, quantos email foram enviados.
			 */
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "NOTIFICACAO DO PRAZO DOS EMPRESTIMOS - EXECUTADO EM " + new Date();
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
			String assunto = "Erro "+siglaSigaa+" - NOTIFICACAO DO PRAZO DOS EMPRESTIMOS: " + e.getMessage();
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
				String assunto = "Erro "+siglaSigaa+" - NOTIFICACAO DO PRAZO DOS EMPRESTIMOS: Não foi possível fechar a conexão do JdbcTemplate ";
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