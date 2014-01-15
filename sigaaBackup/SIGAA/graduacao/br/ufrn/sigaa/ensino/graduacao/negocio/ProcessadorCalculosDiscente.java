/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 21/08/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.Arrays;

import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.email.Mail;
import br.ufrn.arq.email.MailBody;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.NetworkUtils;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;

/**
 * Recalcula totais de integralização do discente de graduação
 *
 * @author Andre Dantas
 *
 */
public class ProcessadorCalculosDiscente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		MovimentoCadastro m = (MovimentoCadastro) mov;
		DiscenteGraduacao dg =  (DiscenteGraduacao) m.getObjMovimentado();
		DiscenteGraduacaoDao dao = getDAO(DiscenteGraduacaoDao.class, mov);
		
		Object[] aux = (Object[]) m.getObjAuxiliar();
		if (aux == null) aux = new Object[] { Boolean.FALSE, Boolean.TRUE };
		
		Boolean zerar = (Boolean) aux[0];
		
		try {
			if (zerar != null && zerar) {
				dao.zerarIntegralizacoes(dg.getId());
			}

			if (SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE.equals(mov.getCodMovimento())) {
				DiscenteCalculosHelper.atualizarTodosCalculosDiscente(dg, mov);
			} else if (SigaaListaComando.CALCULAR_INTEGRALIZACOES_DISCENTE_ANTIGO_REGULAMENTO.equals(mov.getCodMovimento())) {
				DiscenteCalculosHelper.atualizarTodosCalculosDiscenteAntigo(dg, mov);
			}
			
		} catch (ArqException e ) {
			notificarErro(dg, m, e);
			throw e;
		} catch (NegocioException e) {
			notificarErro(dg, m, e);
			throw e;
		} finally {
			dao.close();
		}

		return null;
	}

	/**
	 * Se acontecer algum erro nesta rotina, manda email para a administração pra notificar o erro.
	 * @param dg
	 * @param m
	 * @param e
	 * @throws DAOException
	 */
	public void notificarErro(DiscenteGraduacao dg, MovimentoCadastro m, Exception e) throws DAOException{
		
		String email = ParametroHelper.getInstance().getParametro(ConstantesParametroGeral.EMAIL_ALERTAS_ADMISTRADOR).trim();
		String assunto = "Erro SIGAA - CALCULO DO DISCENTE DE GRADUAÇÂO: " + e.getMessage();
		String mensagem =  "Server: " + NetworkUtils.getLocalName() + "<br><br>" +
		 "DISCENTE: " + dg.toString() + "<br>" +
		 "IDDiscente: " + dg.getId() + "<br>" +
		 "CodMovimento: " + m.getCodMovimento() + "<br>" +
		 "Usuario logado: " + m.getUsuarioLogado() + "<br><br>" +
		e.getMessage() + "<br><br><br>" + Arrays.toString(e.getStackTrace()).replace(",", "\n");

		//enviando email para administração do sistema para notificar do erro
		MailBody mail = new MailBody();
		mail.setEmail( email );
		mail.setAssunto(assunto);
		mail.setMensagem( mensagem );
		Mail.send(mail);
		
	}
	
	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
