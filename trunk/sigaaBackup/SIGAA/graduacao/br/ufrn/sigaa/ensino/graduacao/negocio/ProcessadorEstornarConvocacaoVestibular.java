/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 16/12/2011
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.vestibular.ConvocacaoProcessoSeletivoDiscenteDao;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.negocio.DiscenteHelper;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;

/** Remove uma convocação de discentes e volta o discente para o status PENDENTE DE CADASTRO.
 * @author Édipo Elder F. de Melo
 *
 */
public class ProcessadorEstornarConvocacaoVestibular extends AbstractProcessador {

	/** Processa o estorno da convocação
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);
		ConvocacaoProcessoSeletivoDiscente convocacaoDiscente = ((MovimentoCadastro) mov).getObjMovimentado();
		ConvocacaoProcessoSeletivoDiscenteDao dao = getDAO(ConvocacaoProcessoSeletivoDiscenteDao.class, mov);
		try {
			// reverte o status do discente
			DiscenteHelper.alterarStatusDiscente(convocacaoDiscente.getDiscente(), StatusDiscente.PENDENTE_CADASTRO, "EXTORNO DE CONVOCAÇÃO DO VESTIBULAR", mov, dao);
			// volta a matrícula antiga do discente (caso não nula)
			if (convocacaoDiscente.getDiscente().getDiscente().getMatriculaAntiga() != null) {
				dao.updateField(Discente.class, convocacaoDiscente.getDiscente().getId(), "matricula", convocacaoDiscente.getDiscente().getDiscente().getMatriculaAntiga());
			}
			// remove o cancelamento da convocação
			CancelamentoConvocacao cancelamento = convocacaoDiscente.getCancelamento();
			if (cancelamento == null) {
				cancelamento = dao.findCancelamentoByConvocacao(convocacaoDiscente.getId());
			}
			if (!isEmpty(cancelamento))
				dao.remove(cancelamento);
		} finally {
			dao.close();
		}
		return null;
	}

	/** Valida os dados para o estorno.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ConvocacaoProcessoSeletivoDiscente convocacaoDiscente = ((MovimentoCadastro) mov).getObjMovimentado();
		if (convocacaoDiscente == null)
			throw new NegocioException(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
		checkRole(new int [] {SigaaPapeis.ADMINISTRADOR_DAE, SigaaPapeis.GESTOR_CONVOCACOES_PROCESSOS_SELETIVOS}, mov);
	}

}
