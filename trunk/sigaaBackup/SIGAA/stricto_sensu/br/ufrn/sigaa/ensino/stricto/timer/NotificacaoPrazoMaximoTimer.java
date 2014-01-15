/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 22/01/2010
 *
 */	
package br.ufrn.sigaa.ensino.stricto.timer;

import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.comum.dominio.notificacoes.Notificacao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.arq.dao.ensino.SecretariaUnidadeDao;
import br.ufrn.sigaa.arq.dao.ensino.stricto.DiscenteStrictoDao;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.SecretariaUnidade;

/*******************************************************************************
 * Thread que roda periodicamente enviando e-mail para os alunos com prazo máximo espirado ou faltando 
 * menos de 1 semestre para espirar, envia para o coordenador de cada programa (listando todos os alunos do seu programa) 
 * e membros da PPG (listando todos os alunos de todos programas) que estão com prazo máximo espirado.
 * 
 * @author Arlindo Rodrigues
 * 
 ******************************************************************************/
public class NotificacaoPrazoMaximoTimer extends TarefaTimer {
	
	/** Lista de todos os alunos notificados, agrupados por programa */
	private String listaTodos = "";
	/** Lista de alunos notificados referente ao programa de cada coordenador */
	private String listaCoord = "";
	/** Nome do programa corrente */
	private String unidadeNome = null;
	/** Armazena os Dados do Usuário a ser notificado */
	private UsuarioGeral usuario;
	/** Armazena os Dados da notificação */
	private Notificacao notificacao;
	/** Acumula a quantidade total de email's enviados */
	private int emailsEnviados = 0;
	/** Armazena o id do programa corrente */
	private int idPrograma;
	
	/**
	 * Executa o Timer
	 */
	@Override
	public void run() {
		try {
			notificarPrazoMaximo();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método que é chamado pelo timer.
	 * @throws DAOException
	 */
	private void notificarPrazoMaximo() throws DAOException{
		UsuarioDao usuarioDao = null;		
		DiscenteStrictoDao discentesDao = DAOFactory.getInstance().getDAO(DiscenteStrictoDao.class);		
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		try {
			List<Map<String, Object>> discentes = discentesDao.findDiscentesPrazoMaximoEstourado();						
			
			int idUnidade = 0;
			String lista = "";
			// notifica todos os alunos por email.
			for (Map<String, Object> discente : discentes){				
				String nome = (String) discente.get("nome");
				BigInteger matricula = (BigInteger) discente.get("matricula");
				String email = (String) discente.get("email");
				Integer id = (Integer) discente.get("id_discente");
				idPrograma = (Integer) discente.get("id_unidade");
				String programa = (String) discente.get("programa");
				Date prazoMaximo = (Date) discente.get("data_prazo_maximo");				
				
				String msg;
				String textoPrazo;
				String destacado;
				if (CalendarUtils.estorouPrazo(prazoMaximo, new Date())){
					msg = 												
					"Caro(a) "+nome+", <br>"+
					"O prazo máximo de conclusão do seu curso expirou no dia <b>"+df.format(prazoMaximo)+"</b>."+
					" Por favor, regularize sua situação junto à coordenação do curso.";
					textoPrazo = "<td style='color: red; font-weight: bold; text-align:center;'>"+df.format(prazoMaximo)+"</td>";
					destacado = "<td style='color: red; font-weight: bold;'>";					
				} else {
					msg = 												
					"Caro(a) "+nome+", <br> O prazo máximo de conclusão do seu curso expira no dia <b>"+df.format(prazoMaximo)+"</b>.";
					textoPrazo = "<td style='text-align:center;'>"+df.format(prazoMaximo)+"</td>";
					destacado = "<td>";
				}
			
				// Monta o corpo da mensagem 
				lista = 
					"	<tr>"+
					destacado + matricula+"</td>" +
					destacado + nome+"</td> " +
					textoPrazo  +
					"	</tr>" ;
				
				
				// Seta as informações de envio para um aluno.
				if (!(email == null)) {
					usuario = new Usuario();
					usuario.setId(id);
					usuario.getPessoa().setNome(nome);
					usuario.setEmail(email);
					usuario.setMatricula(String.valueOf( matricula ));
					
					notificacao = new Notificacao();
					notificacao.setId(id);				
					notificacao.setUsuario(usuario);
					notificacao.setMensagem(msg);	
					
					enviarEmail(notificacao);				
					emailsEnviados++;	
				}					
				
				// Cria uma lista de todos os usuários notificados e enviar aos coordenadores.
				if (idUnidade != idPrograma){
					if (idUnidade  > 0){
						enviarCoordenadores(idUnidade);									
					}										
					idUnidade = idPrograma;
					unidadeNome = programa;									
				}			
				
				listaCoord += lista;				
			}
						
			if (!listaTodos.isEmpty()){
				// Envia o último da lista de alunos aos coordenadores do programa desta lista de alunos
				if ( idUnidade != idPrograma )
					enviarCoordenadores(idUnidade);
				// Envia para todos da PPG a lista de todos os alunos de todos os programas 
				enviarPPG();
			}
			
			// Envia um email para a administração avisando, quantos email foram enviados.
			if (emailsEnviados > 0){
				String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
				String assunto = "NOTIFICACAO DE PRAZO MAXIMO PARA CONCLUSAO - EXECUTADO EM " + new Date();
				String mensagem = "Server: " + NetworkUtils.getLocalName() + "<br>Total de email enviados: "+ emailsEnviados; 
				MailBody mail = new MailBody();
				mail.setEmail(email);
				mail.setAssunto(assunto);
				mail.setMensagem(mensagem);				
				Mail.send(mail);
			}
		} catch (Exception e) {
			// Se der algum erro nesta rotina manda email pra administração pra notificar do erro. 
			e.printStackTrace();
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "Erro SIGAA - NOTIFICACAO DE PRAZO MAXIMO PARA CONCLUSAO: " + e.getMessage();
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
			if (discentesDao != null)
				discentesDao.close();
			if (usuarioDao != null)
				usuarioDao.close();
		}
	}

	/**
	 * Envia a lista de todos os alunos para os usuários PPG.
	 * @throws DAOException
	 */
	private void enviarPPG() throws DAOException {
		UsuarioDAO userDao = DAOFactory.getInstance().getDAO(UsuarioDAO.class);
		Collection<UsuarioGeral> usuariosPPG = userDao.findByPapel(SigaaPapeis.PPG, true); 
			
		try{
			// envia email para todos os usuários com o Papel PPG com a relação de todos os alunos que não se matricularam agrupados por programa.
			for (UsuarioGeral user : usuariosPPG){
				String nome = user.getPessoa().getNome();
				String email = user.getEmail();
				
				usuario = new Usuario();
				usuario.setId(user.getId());		
				usuario.getPessoa().setNome(nome);
				usuario.setEmail(email);
				
				String msg =
					"Caro(a) "+nome+", <br> segue abaixo a relação de todos os alunos, agrupados por programa, com prazo máximo de conclusão expirado, "+
					"ou faltando menos de 1(um) semestre para expirar.</b>.<br>";
				
				msg += "<br><br><b>Relação de Alunos:</b><hr><br><table width = '100%'>"+ listaTodos +"</table>";
				
				notificacao = new Notificacao();
				notificacao.setId(0);				
				notificacao.setUsuario(usuario);
				notificacao.setMensagem(msg);
				
				enviarEmail(notificacao);	
				
				emailsEnviados++;
			}			
		} finally {
			if (userDao != null)
				userDao.close();
		}
	}

	/**
	 * Notifica todos os coordenadores do programa passado como parâmetro
	 * @param idPrograma
	 * @throws DAOException
	 */
	private void enviarCoordenadores(int idPrograma) throws DAOException {
		CoordenacaoCursoDao coordenacaoDao = DAOFactory.getInstance().getDAO(CoordenacaoCursoDao.class);
		try {
			listaCoord = getCabecalho(unidadeNome) + listaCoord;
			List<CoordenacaoCurso> coordenadores = (List<CoordenacaoCurso>) coordenacaoDao.findByPrograma(idPrograma, TipoUnidadeAcademica.PROGRAMA_POS, true, null);
			// Envia email para todos os coordenadores do programa a relação de alunos que estão com o prazo máximo estourado
			for (CoordenacaoCurso coord : coordenadores){
				String nome = coord.getServidor().getNome();
				String email = coord.getEmailContato();
				
				if (email != null && !email.isEmpty()){
					usuario = new Usuario();
					usuario.setId(coord.getServidor().getPessoa().getId());						
					usuario.getPessoa().setNome(nome);
					usuario.setEmail(email);
					
					
					String msgCoord =
						"Caro(a) Coordenador(a) "+nome+", <br> segue a relação de alunos do Programa <b>"+unidadeNome+"</b>"+
						" que estão com o prazo máximo de conclusão expirado ou faltando menos de 1 (um) semestre para expirar.";
					
					msgCoord += "<br><br><b>Relação de alunos:</b><hr><br><table width = '100%'>"+ listaCoord +"</table>";
					
					notificacao = new Notificacao();
					notificacao.setId(idPrograma);				
					notificacao.setUsuario(usuario);
					notificacao.setMensagem(msgCoord);	
					
					enviarEmail(notificacao);
					emailsEnviados++;
				}
				
			}				
		} finally {
			if (coordenacaoDao != null){
				coordenacaoDao.close();
			}
		}
		
		SecretariaUnidadeDao secretariaDao = DAOFactory.getInstance().getDAO(SecretariaUnidadeDao.class);
		try {
			Collection<SecretariaUnidade> secretarios = secretariaDao.findByUnidade(idPrograma,null);
			// Envia email para todos os secretários do programa a relação de alunos que estão com o prazo máximo estourado
			for (SecretariaUnidade sec : secretarios){
				String nome = sec.getUsuario().getPessoa().getNome();
				String email = sec.getUsuario().getEmail();
				
				if (email != null && !email.isEmpty()){
					usuario = new Usuario();
					usuario.setId(sec.getUsuario().getPessoa().getId());						
					usuario.getPessoa().setNome(nome);
					usuario.setEmail(email);
					
					
					String msgCoord =
						"Caro(a) Secretário(a) "+nome+", <br> segue a relação de alunos do Programa <b>"+unidadeNome+"</b>"+
						" que estão com o prazo máximo de conclusão expirado ou faltando menos de 1 (um) semestre para expirar.";
					
					msgCoord += "<br><br><b>Relação de alunos:</b><hr><br><table width = '100%'>"+ listaCoord +"</table>";
					
					notificacao = new Notificacao();
					notificacao.setId(idPrograma);				
					notificacao.setUsuario(usuario);
					notificacao.setMensagem(msgCoord);	
					
					enviarEmail(notificacao);
					emailsEnviados++;					
				}								
			}				
		} finally {
			if (secretariaDao != null){
				secretariaDao.close();
			}
		}
		listaTodos += listaCoord;
		listaCoord = "";			
	}
	
	/**
	 * Monta o cabeçalho com o programa passado por parâmetro
	 * @param programa
	 * @return
	 */
	private String getCabecalho(String programa){
		String cabecalho =
			"<tr>" +
			"	<td colspan='3' width = '100%'><br><b>Programa: "+programa+"</b>" +												
			"</tr>"+
			"<tr>" +
			"	<td colspan='3'></td>" +											
			"</tr>"+											 
			"<tr>" +
			"	<td width = '10%'><b>Matrícula</b></td>" +
			"	<td width = '60%'><b>Nome</b></td>"+
			"	<td width = '30%' style='text-align:center;'><b>Prazo Máximo</b></td>"+
			"</tr>";				
		return cabecalho;
	}

	/**
	 * Envia email de notificação para o usuário passado por parâmetro
	 * @param notificacao
	 */
	public void enviarEmail(Notificacao notificacao){
		// enviando e-mail.
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] Aviso do Prazo Máximo de Conclusão");
		email.setContentType(MailBody.HTML);
		email.setNome(notificacao.getUsuario().getPessoa().getNome());
		email.setEmail(notificacao.getUsuario().getEmail());
		email.setMensagem(notificacao.getMensagem());
		Mail.send(email);

	}
}