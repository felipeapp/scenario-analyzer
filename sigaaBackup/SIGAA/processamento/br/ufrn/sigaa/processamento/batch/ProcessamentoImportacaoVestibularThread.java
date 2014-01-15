/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 16/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.processamento.batch;

import java.util.Arrays;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.negocio.FacadeDelegate;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.vestibular.dominio.Inscrito;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.negocio.MovimentoImportacaoDadosProcessoSeletivo;

/**
 * Thread que consome os candidatos inscritos no processo seletivo
 * a serem importados para a base de dados do sistema.
 * 
 * @author Rafael Gomes
 *
 */
public class ProcessamentoImportacaoVestibularThread extends Thread{

	/** Processo Seletivo a processar. */
	private ProcessoSeletivoVestibular processoSeletivo;
	/** Usuário responsável pelo processamento. */
	private Usuario usuario;
	
	/**
	 * Construtor
	 * @param tipo
	 * @param periodo 
	 * @param ano 
	 */
	public ProcessamentoImportacaoVestibularThread(ProcessoSeletivoVestibular processoSeletivo, Usuario usuario) {
		this.processoSeletivo = processoSeletivo;
		this.usuario = usuario;
	}
	
	/** Processa a importação de discentes.
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Inscrito inscrito = null;
		try {
			FacadeDelegate facade = new FacadeDelegate("ejb/SigaaFacade");
			
			MovimentoImportacaoDadosProcessoSeletivo mov = new MovimentoImportacaoDadosProcessoSeletivo();
			while (ListaInscritosVestibular.possuiInscritos()) {

				inscrito = ListaInscritosVestibular.getProximoInscrito();

				mov.setInscrito(inscrito);
				mov.setCodMovimento(SigaaListaComando.PROCESSAR_IMPORTACAO_DADOS_PROCESSO_SELETIVO);
				mov.setUsuarioLogado(usuario);
				mov.setSistema(Sistema.SIGAA);
				mov.setProcessoSeletivo(processoSeletivo);
				
				facade.prepare(SigaaListaComando.PROCESSAR_IMPORTACAO_DADOS_PROCESSO_SELETIVO.getId(), usuario, Sistema.SIGAA);
				facade.execute(mov, usuario, Sistema.SIGAA);
				
				ListaInscritosVestibular.registraProcessada();

				System.out.println("Total Processados: " + ListaInscritosVestibular.getTotalProcessados());

			}
		} catch (Exception e) {
			// comunicando ao usuário
			ListaInscritosVestibular.setErro(e);
			e.printStackTrace();
			
			// se der algum erro nesta rotina manda email pra administração pra notificar do erro. 
			String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
			String assunto = "Erro SIGAA - IMPORTAÇÃO DE RESULTADO DO VESTIBULAR: " + e.getMessage();
			String mensagem =  "Server: " + NetworkUtils.getLocalName() + "\n" +
			e.getClass().getName() + ": " + e.getMessage() + "\n" + 
			(inscrito != null ? "Inscrito: " + inscrito.getInscricaoVestibular().toString() : "") + "\n" +
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
			
		}
	}	
}
