/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 27/03/2013 
 */
package br.ufrn.sigaa.prodocente.timer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.tasks.TarefaTimer;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.parametros.dominio.ParametrosProdocente;
import br.ufrn.sigaa.prodocente.dao.CVLattesDao;
import br.ufrn.sigaa.prodocente.lattes.dominio.PessoaLattes;
import br.ufrn.sigaa.prodocente.negocio.PessoaLattesMov;

/**
 * Rotina para buscar periodicamente informações dos Currículos Lattes no CNPq.
 * 
 * @author Leonardo Campos
 *
 */
public class ImportarCurriculoLattesTimer extends TarefaTimer {

	@Override
	public void run() {
		CVLattesDao dao = DAOFactory.getInstance().getDAO(CVLattesDao.class);
		try {
			Usuario usuario = new Usuario(Usuario.TIMER_SISTEMA);
			PessoaLattesMov mov = new PessoaLattesMov();
			
			mov.setCodMovimento(SigaaListaComando.IMPORTAR_CURRICULOS_LATTES_EM_LOTE);
			
			List<PessoaLattes> pessoasAutorizadas = dao.findPessoasAutorizadas();
			List<PessoaLattes> pessoasProcessar = new ArrayList<PessoaLattes>();
			int qtd = ParametroHelper.getInstance().getParametroInt(ParametrosProdocente.TAMANHO_LOTE_IMPORTACAO_CV_LATTES);
			
			for(PessoaLattes pl: pessoasAutorizadas) {
				if(ValidatorUtil.isEmpty(pl.getUltimaVerificacao()) 
						||  ((new Date()).getTime() - pl.getUltimaVerificacao().getTime()) 
							>= ParametroHelper.getInstance().getParametroLong(ParametrosProdocente.INTERVALO_IMPORTACAO_CV_LATTES)) {
					pessoasProcessar.add(pl);
					if(--qtd <= 0) break;
				}
			}
			
			if(ValidatorUtil.isNotEmpty(pessoasProcessar)) {
				mov.setPessoasLattes(pessoasProcessar);
				
				facade.prepare(SigaaListaComando.IMPORTAR_CURRICULOS_LATTES_EM_LOTE.getId(), usuario, Sistema.SIGAA);
				mov.setAcao(PessoaLattesMov.ACAO_SINCRONIZAR_IDS);
				mov = (PessoaLattesMov) facade.execute(mov, usuario, Sistema.SIGAA);
				
				facade.prepare(SigaaListaComando.IMPORTAR_CURRICULOS_LATTES_EM_LOTE.getId(), usuario, Sistema.SIGAA);
				mov.setAcao(PessoaLattesMov.ACAO_VERIFICAR_DATAS);
				mov = (PessoaLattesMov) facade.execute(mov, usuario, Sistema.SIGAA);
				
				facade.prepare(SigaaListaComando.IMPORTAR_CURRICULOS_LATTES_EM_LOTE.getId(), usuario, Sistema.SIGAA);
				mov.setAcao(PessoaLattesMov.ACAO_EXTRAIR_IMPORTAR_CVS);
				mov = (PessoaLattesMov) facade.execute(mov, usuario, Sistema.SIGAA);
				
				notificarSucesso(mov);
			}
		} catch (Exception e) {
			notificarErro(e);
		} finally {
			dao.close();
		}
	}

	/**
	 * Envia um e-mail para a administração. 
	 * @param mov
	 */
	private void notificarSucesso(PessoaLattesMov mov) {
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "SIGAA - IMPORTAÇÃO AUTOMÁTICA DE CURRÍCULOS LATTES";
		StringBuilder mensagem = new StringBuilder();
		mensagem.append("As pessoas abaixo foram verificadas pela rotina:");
		mensagem.append("<br/><br/>");
		for(PessoaLattes pl: mov.getPessoasLattes()){
			mensagem.append(pl.getPessoa().getCpfNomeFormatado());
			mensagem.append(pl.getObservacoes());
			mensagem.append("<br/>");
		}

		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem.toString() );
		Mail.send(mail);
	}

	/**
	 * Envia um e-mail pra administração em caso de erro na rotina dessa classe.
	 * 
	 * @param e
	 */
	private void notificarErro(Exception e) {
		e.printStackTrace();
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "ERRO SIGAA - IMPORTAÇÃO DE CURRÍCULOS LATTES: " + e.getMessage();
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br>" +
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
