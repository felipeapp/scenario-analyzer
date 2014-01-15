/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática - UFRN
* Diretoria de Sistemas
*
* Created on 15/01/2010
*
*/
package br.ufrn.sigaa.ensino.stricto.timer;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.academico.dominio.TipoUnidadeAcademica;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dao.UsuarioDAO;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.dao.ensino.CoordenacaoCursoDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CoordenacaoCurso;
import br.ufrn.sigaa.ensino.dominio.GrupoMovimentacaoAluno;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;
import br.ufrn.sigaa.parametros.dominio.ParametrosStrictoSensu;

/*******************************************************************************
 * Thread que roda periodicamente enviando e-mail para os alunos, 
 * o programa (listando todos os alunos do programa) 
 * e PPG (listando todos os alunos agrupando por programa)
 * 
 * @author Arlindo Rodrigues
 * 
 ******************************************************************************/
public class NotificacaoMatriculaOnLineTimer extends TarefaTimer {
	
	@Override
	public void run() {
		try {
			notificarMatriculaOnLine();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Método chamado pelo timer, onde notifica todos os alunos, coordenadores e membros da PPG.
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	private void notificarMatriculaOnLine() throws DAOException{
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());
		
		Usuario usuario = new Usuario();
		List<Usuario> usuarios = new ArrayList<Usuario>();
		
		UnidadeDao uniDao = null;
		CoordenacaoCursoDao coordenacaoDao = null;
		UsuarioDao usuarioDao = null;
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		
		int emailsAlunos = 0;
		int emailsCoord = 0;
		int emailsPPG = 0;
		
		try {
			uniDao = DAOFactory.getInstance().getDAO(UnidadeDao.class);
			coordenacaoDao = DAOFactory.getInstance().getDAO(CoordenacaoCursoDao.class);
			
			Collection<Unidade> unidades = uniDao.findByTipoUnidadeAcademica(TipoUnidadeAcademica.PROGRAMA_POS);	
			String listaTodos = "";
			String lista = "";
			
			//resgata a quantidade de dias para iniciar a verificação.
			int quantDias = ParametroHelper.getInstance().getParametroInt(ParametrosStrictoSensu.QUANTIDADE_DIAS_VERIFICACAO_MATRICULA_ONLINE);
						
			if (quantDias > 0){
				for (Unidade u : unidades){		
					u.setTipoAcademica(TipoUnidadeAcademica.PROGRAMA_POS);
					CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendario(u);
					Date data = new Date();			
					
					int diasRestantes = 0;
					
					if (cal.getInicioMatriculaOnline() != null && cal.getFimReMatricula() != null){
						//Verifica a quantidade de dias para finalizar a re-matricula.
						diasRestantes = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(data, cal.getFimReMatricula());
						
						if (diasRestantes >= 0){ 
							// verifica se passaram a quantidade de dias após o inicio da matrícula 
							Boolean iniciaNotificacao = CalendarUtils.calculaQuantidadeDiasEntreDatasIntervaloFechado(cal.getInicioMatriculaOnline(), data) >= quantDias;
							
							if (cal != null && iniciaNotificacao  &&  data.getTime() <= cal.getFimReMatricula().getTime()){
								usuarios.clear();						 	
								// retorna todos os alunos não matriculados e ativos do ano e período referente ao calendário da unidade corrente
								Collection alunos = template.queryForList(
										" select distinct d.matricula, p.nome, p.email, p.id_pessoa "
										+" from ensino.matricula_componente m, discente d, comum.pessoa p," 
										+"     ensino.situacao_matricula s, ensino.componente_curricular c "
										+"  where m.id_discente = d.id_discente"
										+"  and d.id_pessoa = p.id_pessoa"
										+"  and m.id_situacao_matricula = s.id_situacao_matricula"
										+"  and m.id_componente_curricular = c.id_disciplina"
										+"  and c.nivel in " + gerarStringIn(NivelEnsino.getNiveisStricto())
										+"  and m.ano = "+cal.getAno()
										+"  and m.periodo = "+cal.getPeriodo()
										+"  and c.id_unidade = "+u.getId()
										+"  and d.status in "+ UFRNUtils.gerarStringIn(StatusDiscente.getAtivos())	
										+"  and not exists (select m.id_matricula_componente from ensino.matricula_componente m "
										+ "	where m.id_discente = d.id_discente "
										+ "	and m.id_situacao_matricula in" + gerarStringIn(SituacaoMatricula.getSituacoesPositivas()) + ")"
										+ " and not exists (select ma.id_movimentacao_aluno from ensino.movimentacao_aluno ma "
										+ " inner join ensino.tipo_movimentacao_aluno tma using(id_tipo_movimentacao_aluno)"
										+ " where ma.id_discente = d.id_discente "
										+ " and tma.grupo = ' " + GrupoMovimentacaoAluno.AFASTAMENTO_TEMPORARIO + " ' "
										+ " and ma.ano_referencia = " + cal.getAno()
										+ " and ma.periodo_referencia = " + cal.getPeriodo() + ") "
										+" order by p.nome"		
								);	
								// notifica todos os alunos por email do programa corrente. 
								Iterator it = alunos.iterator();
								while(it.hasNext()){
									Map<String, Object> mapa = (Map) it.next();
									
									String nome = (String) mapa.get("nome");
									Long matricula = (Long) mapa.get("matricula");
									String email = (String) mapa.get("email");
									Integer id = (Integer) mapa.get("id_pessoa");
									
									String msg = 
										"Caro(a) "+nome+", <br> resta(m) <b>"+diasRestantes+ " dia(s)</b> para o fim da "+
										"realização da matrícula On-Line, referente a sua matrícula "+matricula +" do Programa <b>"+u.getNome()+"</b>.";
									
									/**
									 * Seta as informações de envio para um aluno.
									 */
									if (!(email == null)) {
										usuario = new Usuario();
										usuario.setId(id);
										usuario.getPessoa().setNome(nome);
										usuario.setEmail(email);
										usuario.setMatricula(String.valueOf( matricula ));
									}
									
									enviarEmail(usuario, msg);
									usuarios.add(usuario);
									
									emailsAlunos += 1;
								}
								
								if (!usuarios.isEmpty()){
									String periodo = df.format(cal.getInicioMatriculaOnline()) + " - "+df.format(cal.getFimReMatricula());
									
									if (listaTodos.isEmpty()){
										listaTodos = "<table width = '100%'>";
									}
									
									listaTodos += "<tr>" +
									"<td colspan='2' width = '100%'><br><b>Programa: "+u.getNome()+"</b><br>" +
									"Período de Matrícula: "+periodo+"</td>" +												
									"</tr>"+
									"<tr>" +
									"	<td width = '100%' colspan='2'></td>" +											
									"</tr>"+											 
									"<tr>" +
									"	<td width = '20%'><b>Matrícula</b></td>" +
									"	<td width = '80%'><b>Nome</b></td>"+											
									"</tr>";
									
									String corpoMsg =
										"<table width = '80%'>" +
										"	<tr>" +
										"		<td width = '30%'><b>Matrícula<b></td>" +
										"		<td width = '60%'><b>Nome</b></td>";
									lista = "";
									for (Usuario user : usuarios){
										lista += 
											"	<tr>"+
											"		<td>"+user.getMatricula()+"</td>" +
											"		<td>"+user.getPessoa().getNome()+"</td>" +
											"	</tr>" ;																	
									}
									
									corpoMsg += lista + "</table>";
									
									listaTodos += lista;								
									// Envia email para todos os coordenadores do programa a relação de alunos que não se matricularam
									List<CoordenacaoCurso> coordenadores = (List<CoordenacaoCurso>) coordenacaoDao.findByPrograma(u.getId(), TipoUnidadeAcademica.PROGRAMA_POS, true, null);
									for (CoordenacaoCurso coord : coordenadores){
										String nome = coord.getServidor().getNome();
										String email = coord.getEmailContato();
										usuario.getPessoa().setNome(nome);
										usuario.setEmail(email);
										
										String msg =
											"Caro(a) Coordenador(a) "+nome+", <br> resta(m) <b>"+diasRestantes+ " dia(s)</b>, para o fim da "+
											"realização da matrícula On-Line, referente ao Programa <b>"+u.getNome()+"</b>.<br>";
										
										msg += "<br><br><b>Relação de alunos não matriculados:</b><hr>"+corpoMsg;
										
										enviarEmail(usuario, msg);
										emailsCoord += 1;
									}
								}
							}					 
						}
					}
				}
				if (!listaTodos.isEmpty()){
					listaTodos += "</table> ";
					
					UsuarioDAO userDao = DAOFactory.getInstance().getDAO(UsuarioDAO.class);
					Collection<UsuarioGeral> usuariosPPG = userDao.findByPapel(SigaaPapeis.PPG, true); 
					
					// envia email para todos os usuários com o Papel PPG com a relação de todos os alunos que não se matricularam agrupados por programa.
					for (UsuarioGeral user : usuariosPPG){
						String nome = user.getPessoa().getNome();
						String email = user.getEmail();
						
						usuario.getPessoa().setNome(nome);
						usuario.setEmail(email);
						
						String msg =
							"Caro(a) Coordenador(a) "+nome+", <br> segue abaixo a relação de todos os alunos não matriculados por programa</b>.<br>";
						
						msg += "<br><br><b>Alunos não matriculados:</b><hr>"+listaTodos;
						
						enviarEmail(usuario, msg);
						emailsPPG += 1;					
					}				
				}
			}
			
			
			/**
			 * Envia um email para a administração avisando, quantos email foram enviados.
			 */
			if (emailsAlunos > 0 || emailsCoord > 0 || emailsPPG > 0){
				String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
				String assunto = "NOTIFICACAO DE MATRÍCULAS ON-LINE DA POS - EXECUTADO EM " + new Date();
				String mensagem = "Server: " + AmbienteUtils.getLocalName() + "<br>Total de email enviados: <br/>"+
				                   " Alunos: "+ emailsAlunos+"<br/>"+
								   " Coordenadores: "+ emailsCoord+"<br/>"+
								   " PPG: "+ emailsPPG;
				MailBody mail = new MailBody();
				mail.setEmail(email);
				mail.setAssunto(assunto);
				mail.setMensagem(mensagem);				
				Mail.send(mail);
			}
		} catch (Exception e) {
			/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
			e.printStackTrace();
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "Erro SIGAA - NOTIFICACAO DE MATRÍCULAS ON-LINE DA POS: " + e.getMessage();
			String mensagem =  "Server: " + AmbienteUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");
			//enviando email para administracao do sistema para notificar do erro
			MailBody mail = new MailBody();
			mail.setEmail( email );
			mail.setAssunto(assunto);
			mail.setMensagem( mensagem );
			Mail.send(mail);
		} finally {
			if (uniDao != null)
				uniDao.close();
			if (coordenacaoDao != null)
				coordenacaoDao.close();
			if (usuarioDao != null)
				usuarioDao.close();
		}
	}

	/**
	 * Envia o email de notificação para o usuário passado como parâmetro.
	 * @param usuario
	 * @param msg
	 */
	private void enviarEmail(Usuario usuario, String msg){
		// enviando e-mail.
		MailBody email = new MailBody();
		email.setAssunto("[SIGAA] Aviso de Matrículas On-Line");
		email.setContentType(MailBody.HTML);
		email.setNome(usuario.getPessoa().getNome());
		email.setEmail(usuario.getEmail());
		email.setMensagem(msg);
		Mail.send(email);

	}

	public static void main(String[] args) throws DAOException{

		System.out.println("\n\n\n INVOCANDO TIMER \n\n\n ");

		NotificacaoMatriculaOnLineTimer timer = new NotificacaoMatriculaOnLineTimer();
		timer.notificarMatriculaOnLine();

		System.out.println("\n\n\n TIMER REALIZADO COM SUCESSO \n\n\n ");

	}
}