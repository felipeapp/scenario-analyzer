/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 25/02/2011
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.biblioteca.ReservaMaterialBibliotecaDao;
import br.ufrn.sigaa.arq.dao.biblioteca.UsuarioBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.ReservaMaterialBiblioteca.StatusReserva;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.UsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.dominio.EnvioEmailBiblioteca;
import br.ufrn.sigaa.biblioteca.util.ReservaMaterialBibliotecaUtil;

/**
 *
 * <p>
 * Timer que deve rodar todo dia antes do empediente das bibliotecas começar, verifica na fila de reservas 
 * aquelas que estão na situação EM_ESPERA, cujo tempo de espera já passaou do limite permitido. Cancelar essas 
 * reservas e colocar em espera a próxima reserva do título.  Avisando o solicitador da sua reserva está disponível 
 * no prazo <code>ParametrosBiblioteca.PRAZO_EM_DIAS_USUARIO_TEM_PARA_EFETIVAR_RESERVA</code>.
 * </p>
 *
 *<p>
 * <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: 
 *  horaExecucao: 4h 
 *  tipoReplicacao: D = Diário
 *</p>
 *
 * 
 * @author jadson
 *
 */
public class VerificaReservasMaterialBibliotecaVencidasTimer extends TarefaTimer {

	@Override
	public void run() {
		try {
			
			if(ReservaMaterialBibliotecaUtil.isSistemaTrabalhaComReservas()){
				verificarFilaReservas();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * <p>Método que verifica na fila de reservas qual das reservas EM_ESPERA já venceram, ou seja, 
	 * possuem o prazoRetiradaMaterial < hoje. Ex.: prazoRetiradaMaterial = 01/01/2011 e hoje é 02/01/2011. </p>
	 *
	 * <p>Caso encontre essa situação, deve cancelar a reserva, colocar a próxima reserva do título 
	 * no status de EM_ESPERA, calcular o prazoRetiradaMaterial dessa próxima reserva e enviar um mail para o
	 * usuária avisando que sua reserva está pronta.</p>
	 *
	 */
	@SuppressWarnings("unchecked")
	public void verificarFilaReservas(){
		
		UsuarioBibliotecaDao dao = null;
		ReservaMaterialBibliotecaDao daoReserva = null;
		
		JdbcTemplate template = null;
		
		String sqlReservasAtivas = "select r.id_reserva_material_biblioteca, r.id_titulo_catalografico, r.prazo_retirada_material, r.status, r.id_usuario_biblioteca "
			+" FROM biblioteca.reserva_material_biblioteca r"
			+" WHERE status in"+UFRNUtils.gerarStringIn(ReservaMaterialBiblioteca.getReservasAtivas())
			+" ORDER BY id_titulo_catalografico, data_solicitacao, id_reserva_material_biblioteca"; // Retornar para ordem para pode liberar a reserva corretamente

		
		List<DadosEnviaEmailReservasAtivadas> emaisASeremEnviados = new ArrayList<DadosEnviaEmailReservasAtivadas>(); 
		
		EnvioEmailBiblioteca sender = new EnvioEmailBiblioteca();
		
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		
		
		try{
			
			dao = DAOFactory.getInstance().getDAO(UsuarioBibliotecaDao.class); 
			
			daoReserva = DAOFactory.getInstance().getDAO(ReservaMaterialBibliotecaDao.class); 
			
			template = new JdbcTemplate( Database.getInstance().getSigaaDs());
			
			Collection reservas = template.queryForList(sqlReservasAtivas);
			
			int qtdReservasCanceladas = 0;
			int qtdUsuariosAvisados = 0;
			
			Date hoje  = new Date();
			
			Iterator it = reservas.iterator();
			
			Map<String, Object> mapa = null;
	
			
			/* ************************************************************************
			 *  <p>Guarda a quantidade de reservas que precisa ser ativadas do título</p>
			 *
			 *  <p>No formato: </p>
			 *  <idTitulo, quantiadeReservasCanceladas>
			 * *************************************************************************/
			Map<Integer, Integer> mapaQuantidadeReservaAtivar = new HashMap<Integer, Integer>();
			
			
			//
			// Para cada reserva ativa (EM ESPERA ou Solicitada  ) //
			// As reservas EM ESPERA sempre vêm primeiro para o mesmo título, pois as primeira 
			// solicitadas são sempre as que fiam em espera primeiro.
			//
			// Essa ordem é importante para o algoritmo abaixo funcionar
			//
			while(it.hasNext()){

				mapa = (Map) it.next();
				

				int idReserva = (Integer) mapa.get("id_reserva_material_biblioteca");
				int idTitulo = (Integer) mapa.get("id_titulo_catalografico");
				Date prazoRetirada = (Date) mapa.get("prazo_retirada_material");
				int status = (Integer) mapa.get("status");
				int idUsuarioBiblioteca = (Integer) mapa.get("id_usuario_biblioteca");
				
				
				if(StatusReserva.EM_ESPERA.toString().equals(""+status)){
					if( venceuPrazoRetiradaMaterail(prazoRetirada, hoje) ){
						
						qtdReservasCanceladas++;
						
						if(! mapaQuantidadeReservaAtivar.containsKey(idTitulo)){
							mapaQuantidadeReservaAtivar.put(idTitulo, 1);
						}else{
							int quantiade = mapaQuantidadeReservaAtivar.get(idTitulo);
							mapaQuantidadeReservaAtivar.put(idTitulo, ++quantiade);
						}
						
						template.update("update biblioteca.reserva_material_biblioteca set status = ?, data_cancelamento = ? where id_reserva_material_biblioteca = ? ", new Object[]{ Integer.parseInt(StatusReserva.CANCELADA.toString()), hoje, idReserva});
					}
					
				}else{ // Se é uma reserva SOLICITADA
					
					// Como só tem 1 em espera por vez por Título, só vai entrar aqui 1 vez
					// Quando o Título tiver uma reserva que passou EM_ESPERA -> CANCELADA e a atual reserva é a próxima reserva da fila.
					Integer quantidadeReservasCujoPrazoFoiVencidoDoTitulo = mapaQuantidadeReservaAtivar.get(idTitulo); 
					
					if(possuiReservaEmEsperaCancelada(quantidadeReservasCujoPrazoFoiVencidoDoTitulo)){
						
						mapaQuantidadeReservaAtivar.put(idTitulo, --quantidadeReservasCujoPrazoFoiVencidoDoTitulo);
						
						Date prazoRetirarMaterial = ReservaMaterialBibliotecaUtil.calculaPrazoRetiradaProximaReserva(idTitulo);
						
						qtdUsuariosAvisados++;
						
						template.update("update biblioteca.reserva_material_biblioteca set status = ?, data_em_Espera = ? , prazo_retirada_material = ? where id_reserva_material_biblioteca = ? ", new Object[]{Integer.parseInt(StatusReserva.EM_ESPERA.toString()), hoje, prazoRetirarMaterial, idReserva});
						
						emaisASeremEnviados.add(new DadosEnviaEmailReservasAtivadas(idUsuarioBiblioteca, idTitulo, idReserva, prazoRetirarMaterial));	
						
					}
					
				}
				
									
			}  // while todas as reservas ativas
			
			
			//// Envia email só no final, se não ocorrer nenhum erro   ////
			for (DadosEnviaEmailReservasAtivadas dados : emaisASeremEnviados) {
				enviaEmailAvisoRetiradaMaterial(dao, sender, dados.getIdUsuarioBiblioteca(), dados.getIdTitulo(), dados.getIdReserva(), dados.getPrazoRetirarMaterial());
			}
			
			
			/**
			 * Envia um email para a administração avisando, que a verificação da reserva rodou.
			 */
			enviaEmailNotificacaoAdministradorSistema(qtdReservasCanceladas, qtdUsuariosAvisados);
			
		}catch (Exception e) {
				
				/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
				e.printStackTrace();
				enviaEmailErroAdministradorSistema(siglaSigaa, "VERIFICAÇÃO FILA DE RESERVAS DA BIBLIOTECA", e);
				
				 
		}finally {
			try {
				
				if(dao != null) dao.close();
				if(daoReserva != null) daoReserva.close();
				
				if(template != null) template.getDataSource().getConnection().close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
				e.printStackTrace();
				enviaEmailErroAdministradorSistema(siglaSigaa, "VERIFICAÇÃO FILA DE RESERVAS DA BIBLIOTECA: Não foi possível fechar a conexão do JdbcTemplate", e);
				
			}
			
		}
	}
	
	
	
	/**
	 * Verifica se possui reserva EM ESPERA cancelada, se sim, precisa atualizar próxima reserva solicitada
	 *
	 * @param quantiadeReservasCanceladasTitulo
	 * @return
	 */
	private boolean possuiReservaEmEsperaCancelada(Integer quantiadeReservasCanceladasTitulo){
		if(quantiadeReservasCanceladasTitulo != null && quantiadeReservasCanceladasTitulo.compareTo(0) > 0)
			return true;
		else
			return false;
		
	}
	
	
	
	/**
	 * Envia o para o informar ao próximo usuário da fila que sua reserva está disponível 
	 */
	private void enviaEmailAvisoRetiradaMaterial(UsuarioBibliotecaDao dao, EnvioEmailBiblioteca sender, int idUsuarioBiblioteca, int idTitulo, int idReserva, Date prazoRetirarMaterial) throws DAOException{
			
		Object[] informacoesUsuario = dao.findNomeEmailUsuarioBiblioteca(new UsuarioBiblioteca(idUsuarioBiblioteca));
		ReservaMaterialBibliotecaUtil.enviaEmailReservaDisponivel(sender, (String)informacoesUsuario[0], (String)informacoesUsuario[1], idReserva, idTitulo , prazoRetirarMaterial);
		
	}
	
	
	
	/** Verifica se o prazo de retirada é uma data anterior a hoje, se for está vencido */
	private boolean venceuPrazoRetiradaMaterail(Date prazoRetirada, Date hoje){
		if(prazoRetirada.before( CalendarUtils.descartarHoras(hoje)) )
			return true;
		else
			return false;
	}
	
	
	/**
	 * Envia um email com a informação do erro na execuçção da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailNotificacaoAdministradorSistema(final int qtdReservasCanceladas, final int qtdUsuariosAvisados){
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "VERIFICAÇÃO FILA DE RESERVAS DA BIBLIOTECA - EXECUTADO EM " + new Date();
		String mensagem = "Server: " + NetworkUtils.getLocalName() 
			+ "<br>Total de reservas canceladas porque o prazo para retirar o material expirou : " + qtdReservasCanceladas
			+ "<br>Total usuários avisados que suas reservas estão dinpovíveis : " + qtdUsuariosAvisados
			+ "<br>As próximas reservas foram ativadas, e seus usuários comunicados."; 
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		
		Mail.send(mail);
	}
	
	
	
	/**
	 * Envia um email com a informação do erro na execuçção da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailErroAdministradorSistema(String siglaSigaa, String assuntoEmail, Exception e){
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro "+siglaSigaa+" - "+assuntoEmail+": " + e.getMessage();
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

/**
 * <p> Guarda os dados do envio do email para só enviar no final caso não tenha ocorrido nenhum erro </p>
 * 
 * @author jadson
 *
 */
class DadosEnviaEmailReservasAtivadas{
	
	private int idUsuarioBiblioteca;
	private int idTitulo;
	private int idReserva;
	private Date prazoRetirarMaterial;
	
	
	
	public DadosEnviaEmailReservasAtivadas(int idUsuarioBiblioteca, int idTitulo,
			int idReserva, Date prazoRetirarMaterial) {
		this.idUsuarioBiblioteca = idUsuarioBiblioteca;
		this.idTitulo = idTitulo;
		this.idReserva = idReserva;
		this.prazoRetirarMaterial = prazoRetirarMaterial;
	}
	public int getIdUsuarioBiblioteca() {
		return idUsuarioBiblioteca;
	}
	public int getIdTitulo() {
		return idTitulo;
	}
	public int getIdReserva() {
		return idReserva;
	}
	public Date getPrazoRetirarMaterial() {
		return prazoRetirarMaterial;
	}
	
	 
	 
}
