/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 25/07/2011
 *
 */
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.medio.dominio.ObservacaoDiscenteSerie;

/**
 * Processador respons�vel pelo cadastro de observa��es de discentes
 *
 * @author Arlindo
 *
 */
public class ProcessadorObservacaoDiscenteSerie extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		checkRole(new int[] {SigaaPapeis.SECRETARIA_MEDIO, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.GESTOR_MEDIO},  mov);

		ObservacaoDiscenteSerie observacao = ((MovimentoCadastro) mov).getObjMovimentado();

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE_SERIE)) {
			cadastrar(observacao, mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE_SERIE)) {
			remover(observacao, mov);
		}

		return null;
	}

	/**
	 * M�todo respons�vel pela remo��o de uma observa��o do discente.
	 * @param mov
	 * @throws ArqException
	 */
	private void remover(ObservacaoDiscenteSerie obj, Movimento mov) throws ArqException {
		try {
			GenericDAO dao = getGenericDAO(mov);	
			try {
				dao.updateField(ObservacaoDiscenteSerie.class, obj.getId(), "ativo", false);
			} finally {
				dao.close();
			}
		} catch (Exception e) {
			throw new ArqException(e);
		} 
		
	}

	/**
	 * M�todo respons�vel pelo cadastro ou altera��o de uma observa��o do discente.
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void cadastrar(ObservacaoDiscenteSerie observacao, Movimento mov) throws NegocioException, ArqException {
		validate(mov);

		try {
			observacao.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			observacao.setData(new Date());
			observacao.setMatricula(observacao.getMatricula());

			GenericDAO dao = getGenericDAO(mov);
			try {
				dao.create(observacao);
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			throw new ArqException(e);
		}
	}

	/** 
	 * Realiza a valida��o da observa��o
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ObservacaoDiscenteSerie observacao = ((MovimentoCadastro) mov).getObjMovimentado();

		// Realizar valida��es
		ListaMensagens erros = new ListaMensagens();
		
		if (ValidatorUtil.isEmpty(observacao.getMatricula())) {
			erros.addErro("� necess�rio selecionar uma matr�cula.");
		}		

		if (observacao.getObservacao() == null || "".equals(observacao.getObservacao().trim())) {
			erros.addErro("� necess�rio informar uma observa��o");
		}
		checkValidation(erros);

	}

}