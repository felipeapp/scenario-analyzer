/*
 * Sistema Integrado de Patrimônio e Administração de Contratos
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ensino.timer;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;

import java.util.Arrays;
import java.util.List;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dao.JdbcTemplate;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * Thread que roda periodicamente calculando os alunos da pós-graduação que
 * encontram-se pendentes.
 * 
 * @author Gleydson
 * 
 */
public class CalculosDiscenteStrictoTimer extends TarefaTimer {

	@Override
	public void run() {
		JdbcTemplate template = new JdbcTemplate(Database.getInstance()
				.getSigaaDs());
		// Todos os alunos ativos devem ser recalculados
		@SuppressWarnings("unchecked")
		List<Integer> discenteACalcular = template
				.queryForList("select id_discente from discente" +
						" inner join stricto_sensu.discente_stricto using (id_discente)" +
						" where nivel in ('S','E', 'D')" +
						" and ultima_atualizacao_totais is null" +
						" and status in " + gerarStringIn( new int[]{ StatusDiscente.ATIVO, StatusDiscente.DEFENDIDO} )	+
						BDUtils.limit(ParametroHelper.getInstance().getParametroInt(ParametrosGerais.QUANTIDADE_CALCULAR_CALCULOS_DISCENTE_GRADUACAO_TIMER)), Integer.class);
		GenericDAO dao = new GenericSigaaDAO();
		DiscenteStricto discente = null;
		try {
			Usuario usuario = new Usuario(Usuario.TIMER_SISTEMA);
			// percorre os alunos e chama o processador para calcular
			for (Integer idDiscente : discenteACalcular) {
				discente = dao.findByPrimaryKey(idDiscente,DiscenteStricto.class);
				MovimentoCadastro mov = new MovimentoCadastro();
				mov.setObjMovimentado(discente);
				mov.setCodMovimento(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO);
				mov.setUsuarioLogado(new Usuario(Usuario.TIMER_SISTEMA));
				mov.setSistema(Sistema.SIGAA);
				facade.prepare(SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_STRICTO.getId(), usuario, Sistema.SIGAA);
				facade.execute(mov, usuario, Sistema.SIGAA);
			}
		} catch (Exception e) {			
			e.printStackTrace();
			// se der algum erro nesta rotina manda email pra administração pra notificar do erro. 
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "Erro SIGAA - CALCULOS DISCENTE STRICTO TIMER: " + e.getMessage();
			String mensagem =  "Server: " + NetworkUtils.getLocalName() + "\n" +
			e.getClass().getName() + ": " + e.getMessage() + "\n" + 
			(discente != null ? "Discente: " + discente.getMatriculaNome() : "") + "\n" +
			"\n\n" + Arrays.toString(e.getStackTrace()).replace(",", "\n") +
			(e.getCause() != null ? Arrays.toString(e.getCause().getStackTrace()).replace(",", "\n") : "");

			//enviando email para administração do sistema para notificar do erro
			MailBody mail = new MailBody();
			mail.setContentType(MailBody.TEXT_PLAN);
			mail.setEmail( email );
			mail.setAssunto(assunto);
			mail.setMensagem( mensagem );
			Mail.send(mail);
			
		} finally {
			dao.close();
		}

	}
	
}
