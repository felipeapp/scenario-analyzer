/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 30/04/2012
 *
 */
package br.ufrn.sigaa.vestibular.jsf;

import static br.ufrn.arq.mensagens.MensagensArquitetura.OPERACAO_SUCESSO;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.InscricaoVestibularDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

@Component("processaPagamentoGRUMBean")
@Scope("session")
public class ProcessaPagamentoGRUMBean extends SigaaAbstractController<InscricaoVestibular> {
	
	public String iniciar() throws ArqException {
		checkRole(SigaaPapeis.VESTIBULAR);
		resultadosBusca = null;
		obj = new InscricaoVestibular();
		prepareMovimento(SigaaListaComando.MARCAR_GRUS_QUITADAS);
		setOperacaoAtiva(SigaaListaComando.MARCAR_GRUS_QUITADAS.getId());
		return forward("/vestibular/processamentos/form_gru_pagas.jsp");
	}
	
	public String buscar() throws HibernateException, DAOException, SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		if (!checkOperacaoAtiva(SigaaListaComando.MARCAR_GRUS_QUITADAS.getId()))
			return null;
		InscricaoVestibularDao dao = getDAO(InscricaoVestibularDao.class);
		resultadosBusca = dao.verificaInscricaoGRUPaga(obj.getProcessoSeletivo().getId());
		if (isEmpty(resultadosBusca)) {
			addMensagem(MensagensArquitetura.BUSCA_SEM_RESULTADOS);
			return null;
		}
		return forward("/vestibular/processamentos/confirma_gru_pagas.jsp");
	}
	
	public String confirmar() throws SegurancaException {
		checkRole(SigaaPapeis.VESTIBULAR);
		if (!checkOperacaoAtiva(SigaaListaComando.MARCAR_GRUS_QUITADAS.getId()))
			return null;
		MovimentoCadastro mov = new MovimentoCadastro();
		mov.setCodMovimento(SigaaListaComando.MARCAR_GRUS_QUITADAS);
		mov.setColObjMovimentado(resultadosBusca);
		try {
			execute(mov);
			addMensagem(OPERACAO_SUCESSO);
		} catch (Exception e) {
			notifyError(e);
			addMensagemErroPadrao();
			return null;
		}
		removeOperacaoAtiva();
		return cancelar();
	}

}
