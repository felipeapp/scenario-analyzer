/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 24/09/2008
 *
 */
package br.ufrn.sigaa.ensino.timer;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.RegistroEntrada;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.AmbienteUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.sincronizacao.SincronizadorRegistroEntrada;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Thread que roda periodicamente calculando os alunos de graduação que
 * encontram-se pendentes de cálculo.
 * 
 * @author Gleydson
 * 
 */
public class CalculosDiscenteGraduacaoTimer extends TarefaTimer {

	@Override
	public void run() {

		// Todos os alunos ativos devem ser recalculados
		JdbcTemplate template = new JdbcTemplate(Database.getInstance().getSigaaDs());
		
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> discenteACalcular = template
				.queryForList("select id_discente_graduacao from graduacao.discente_graduacao dg," +
						" discente d where d.id_discente = dg.id_discente_graduacao " +
						" and d.status in " + UFRNUtils.gerarStringIn(StatusDiscente.getStatusComVinculo()) +  
						" and ultima_atualizacao_totais is null " +
						" and d.tipo <> " + Discente.ESPECIAL  +
						BDUtils.limit(ParametroHelper.getInstance().getParametroInt(
								ParametrosGerais.QUANTIDADE_CALCULAR_CALCULOS_DISCENTE_GRADUACAO_TIMER)));
		
		if (discenteACalcular == null || discenteACalcular.isEmpty())
			return;
		
		GenericDAO dao = new GenericSigaaDAO();
		RegistroEntrada registro = null;
		Usuario usuario = null;
		try {	
			// usuário e registro de entrada do timer, para fins de log
			usuario = new Usuario(Usuario.TIMER_SISTEMA);
			registro = new RegistroEntrada();
			registro.setUsuario(usuario);
			registro.setCanal(RegistroEntrada.CANAL_WEB);
			registro.setData(new Date());
			registro.setIP(AmbienteUtils.getLocalAddress());
			registro.setServer(AmbienteUtils.getLocalName());
			registro.setSistema(Sistema.SIGAA);

			SincronizadorRegistroEntrada.usandoSistema(Sistema.SIGAA).cadastrarRegistroEntrada(registro);
			
			usuario.setRegistroEntrada(registro);
			int totalCalculados = 0;
			for (Map<String, Object> mapaDiscente : discenteACalcular) {

				Integer idDiscente = (Integer) mapaDiscente.get("id_discente_graduacao");
				DiscenteGraduacao discente = dao.findByPrimaryKey(idDiscente, DiscenteGraduacao.class);

				MovimentoCadastro mov = new MovimentoCadastro(discente, SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE);
				mov.setUsuarioLogado(usuario);

				facade.prepare(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE.getId(), usuario, Sistema.SIGAA);
				facade.execute(mov, usuario, Sistema.SIGAA);
				
				totalCalculados++;
			}
			
			if (totalCalculados > 0) {
				notificarAdministradores(totalCalculados);
			}
			dao.updateField(RegistroEntrada.class, registro.getId(), "dataSaida", new Date());
		} catch (Exception e) {
			notificarErro(e);
		} finally {
			dao.close();
		}
	}

	/**
	 * Método que realiza a notificação dos administradores sobre o total
	 * de alunos de graduação que encontram-se pendentes.
	 * 
	 * @param totalCalculados
	 */
	private void notificarAdministradores(int totalCalculados) {
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "TIMER DISCENTE GRADUAÇÃO - EXECUTADO EM " + new Date();
		String mensagem = "Server: " + AmbienteUtils.getLocalName() + "<br>Total de alunos calculados: " + totalCalculados; 
		MailBody mail = new MailBody();
		mail.setEmail(email);
		mail.setAssunto(assunto);
		mail.setMensagem(mensagem);
		
		Mail.send(mail);
	}

	/**
	 * Método que manda email para a administração em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro SIGAA - CALCULOS DISCENTE GRADUACAO TIMER: " + e.getMessage();
		String mensagem =  "Server: " + AmbienteUtils.getLocalName() + "<br>" +
			e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");

		// Enviando email para administração do sistema para notificar do erro
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
	}

	
}
