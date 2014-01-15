/*
 *
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em: 30/04/2012
 * 
 */
package br.ufrn.sigaa.biblioteca.timer;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.parametrizacao.RepositorioDadosInstitucionais;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.gru.dominio.TipoArrecadacao;
import br.ufrn.sigaa.arq.dao.biblioteca.MultaUsuariosBibliotecaDao;
import br.ufrn.sigaa.biblioteca.circulacao.dominio.MultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.MovimentoQuitaMultaUsuarioBiblioteca;
import br.ufrn.sigaa.biblioteca.circulacao.negocio.ProcessadorQuitaMultaUsuarioBiblioteca;


/**
*
* <p> Timer da biblioteca que verifica diarimente as multas pagas de forma automática e realiza a baixa da multa na biblioteca.</p>
*
* <p> Busca todas multa EM ABERTO na biblioteca, a partir o id da GRU da multa, verifica no banco comum se ela foi paga, me caso afirmativo,
* chama o processador ProcessadorQuitaMultaUsuarioBiblioteca para quitar a multa no sistema e enviar um email para os usuários que a sua multa 
* foi quitada.</p>
*
*<p>
* <strong>Observação: </strong> inserir um registro na tabela INFRA.REGISTRO_TIMER (Banco SISTEMAS_COMUM). Vai possuir 2 parâmetros: <br/>
*  horaExecucao: 2h <br/>
*  tipoReplicacao: D = Diário <br/>
*  expressão cron: * 0 2 1/1 * ? * <br/>
*</p>
*
*<pre>
*
* insert into infra.registro_timer (id, dia_execucao, hora_execucao, tempo, tipo_repeticao, classe, ativa
* , servidor_execucao, servidor_restricao, dia_mes_execucao, executar_agora, em_execucao, expressao_cron)
* values (34, 0, 2, 0, 'D', 'br.ufrn.sigaa.biblioteca.timer.BaixarMultasPagasAutomaticamenteBibliotecaTimer', true, 'sistemas1', 'sistemas1i1'
* , 0, false, false, '0 0 2 1/1 * ? *')
*
*</pre>
*
* 
* @author jadson
*
*/
public class BaixarMultasPagasAutomaticamenteBibliotecaTimer extends TarefaTimer{

	/**
	 * Inicia a executação em paralelo
	 */
	@Override
	public void run() {
		try {	
			executaBaixaMultasPagasAutomaticamente();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Executa a baixa das multas pagas automaticamente.
	 */
	public void executaBaixaMultasPagasAutomaticamente(){
		
		String siglaSigaa = RepositorioDadosInstitucionais.get("siglaSigaa");
		
		MultaUsuariosBibliotecaDao dao = null;	
		
		try{
			
			dao = DAOFactory.getInstance().getDAO(MultaUsuariosBibliotecaDao.class);
			
			List<Integer> idsGRUsMultasQuitas = new ArrayList<Integer>();
			List<Integer> idsGRUsMultasAbertas = new ArrayList<Integer>();
			
			List<MultaUsuarioBiblioteca> multasAbertas = dao.findAllMultasAtivasComGRUSistema();
			
			for (MultaUsuarioBiblioteca multaAberta : multasAbertas) {
				idsGRUsMultasAbertas.add(multaAberta.getIdGRUQuitacao());
			}
		
			if(idsGRUsMultasAbertas.size() > 0){
			
				idsGRUsMultasQuitas = retornaGRUsMultasQuitadas(idsGRUsMultasAbertas, siglaSigaa);
			
				if(idsGRUsMultasQuitas.size() > 0 ){
				
					List<MultaUsuarioBiblioteca> multasQuitadas = new ArrayList<MultaUsuarioBiblioteca>();
					
					for (MultaUsuarioBiblioteca multaAberta : multasAbertas) {
						
						if( multaAberta.getIdGRUQuitacao()  != null && idsGRUsMultasQuitas.contains(multaAberta.getIdGRUQuitacao() )  ){
							multasQuitadas.add(multaAberta);
						}
					}
					
					// OBSERVAÇÃO: Segundo a tarefa #69267 - Gerencia dos Timers os timers são Spring Beans com contexto transacional  //
					MovimentoQuitaMultaUsuarioBiblioteca movimento  = new MovimentoQuitaMultaUsuarioBiblioteca(multasQuitadas);
					ProcessadorQuitaMultaUsuarioBiblioteca procesador = new ProcessadorQuitaMultaUsuarioBiblioteca();
					procesador.execute(movimento);
				}
				
			}
			
			enviaEmailNotificacaoAdministradorSistema(idsGRUsMultasQuitas.size());
			
		} catch (Exception ex) {
			/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
			ex.printStackTrace();
			enviaEmailErroAdministradorSistema(siglaSigaa, "EXECUÇÃO DA BAIXA DAS MULTAS PAGAS AUTOMATICAMENTE", ex);
		}finally{
			if(dao != null) dao.close();
		}
	}
	
	
	/**
	 * Retorna os ids das GRUs que foram quitas a partir dos ids das GRUs das multas abertas no sistema.
	 * 
	 * Essa busca é realizada no bancao comum.
	 * 
	 * @param idsGRUsMultasAbertas
	 * @return
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private List<Integer> retornaGRUsMultasQuitadas(List<Integer> idsGRUsMultasAbertas, String siglaSigaa){
		
		List<Integer> idsGRUsMultasQuitas = new ArrayList<Integer>();
		
		JdbcTemplate template = null;
		
		try{
		
			String sqlGRUsPagas = "select gru.id_gru "
				+" FROM gru.guia_recolhimento_uniao gru "
				+" WHERE gru.id_gru in "+UFRNUtils.gerarStringIn(idsGRUsMultasAbertas)
				+" AND gru.quitada = trueValue() AND gru.id_tipo_arrecadacao = "+TipoArrecadacao.PAGAMENTO_MULTAS_BIBLIOTECA;
			
			
			template = new JdbcTemplate( Database.getInstance().getComumDs());
			
			Collection multasQuitads = template.queryForList(sqlGRUsPagas);
			Iterator it = multasQuitads.iterator();
			
			Map<String, Object> mapa = null;
			
			while(it.hasNext()){
				
				mapa= (Map) it.next();
				
				
				int idGRU = (Integer) mapa.get("id_gru");
				idsGRUsMultasQuitas.add(idGRU);
			}
		
		}finally {
			try {
				
				if(template != null) template.getDataSource().getConnection().close();
				
			} catch (SQLException e) {
				e.printStackTrace();
				
				/** Se der algum erro nesta rotina manda email pra administração pra notificar do erro. */
				e.printStackTrace();
				enviaEmailErroAdministradorSistema(siglaSigaa, "EXECUÇÃO DA BAIXA DAS MULTAS PAGAS AUTOMATICAMENTE: Não foi possível fechar a conexão do JdbcTemplate", e);
				
			}
			
		}
		
		return idsGRUsMultasQuitas;
	}
	
	
	
	
	
	/**
	 * Envia um email com a informação do erro na execuçção da rotina para os administradores do sistema.
	 *
	 * @param siglaSigaa
	 * @param assuntoEmail
	 * @param e
	 */
	private void enviaEmailNotificacaoAdministradorSistema(final int qtdMultasDadoBaixa){
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "DAR BAIXA NAS MULTAS PAGAS AUTOMATICAMENTE - EXECUTADO EM " + new Date();
		String mensagem = "Server: " + NetworkUtils.getLocalName() 
			+ "<br>Total de multas confirmados os pagamentos automaticamente dado baixa no sistema : " + qtdMultasDadoBaixa; 
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
