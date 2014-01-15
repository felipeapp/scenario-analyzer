/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on 01/03/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.pessoa.dominio.ObservacaoDiscente;

/**
 * Processador respons�vel pelo cadastro de observa��es de discentes
 *
 * @author Ricardo Wendell
 *
 */
public class ProcessadorObservacaoDiscente extends AbstractProcessador {

	/**
	 * Executa o processamento
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS,SigaaPapeis.DAE, SigaaPapeis.CDP, SigaaPapeis.PPG, SigaaPapeis.GESTOR_TECNICO, 
				SigaaPapeis.GESTOR_FORMACAO_COMPLEMENTAR, SigaaPapeis.GESTOR_NEE, SigaaPapeis.COORDENADOR_MEDIO, SigaaPapeis.GESTOR_MEDIO},  mov);

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_OBSERVACAO_DISCENTE)) {
			cadastrar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_OBSERVACAO_DISCENTE)) {
			remover(mov);
		}

		return null;
	}

	/**
	 * M�todo respons�vel pela remo��o de uma observa��o do discente.
	 * @param mov
	 * @throws ArqException
	 */
	private void remover(Movimento mov) throws ArqException {
		ObservacaoDiscente observacao = (ObservacaoDiscente) mov;

		try {
			GenericDAO dao = getGenericDAO(mov);
			
			try {
				observacao = dao.refresh(observacao);
	
				observacao.setAtivo(false);
	
				dao.update(observacao);
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
	private void cadastrar(Movimento mov) throws NegocioException, ArqException {
		validate(mov);

		ObservacaoDiscente observacao = (ObservacaoDiscente) mov;

		try {
			observacao.setRegistro(mov.getUsuarioLogado().getRegistroEntrada());
			observacao.setData(new Date());
			observacao.setDiscente(observacao.getDiscente().getDiscente());

			GenericDAO dao = getGenericDAO(mov);

			try {
				ObservacaoDiscente anterior = observacao.getObservacaoAnterior();
				if (isNotEmpty(anterior)) {
					anterior.setSistema(mov.getSistema());
					anterior.setUsuarioLogado(mov.getUsuarioLogado());
					remover(anterior);
				}
				dao.create(observacao);
			} finally {
				dao.close();
			}

		} catch (Exception e) {
			throw new ArqException(e);
		}
	}

	/** 
	 * Valida os campos de observa��o
	 * @see br.ufrn.arq.ejb.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		ObservacaoDiscente observacao = (ObservacaoDiscente) mov;

		// Realizar valida��es
		ListaMensagens erros = new ListaMensagens();

		if (observacao.getObservacao() == null || "".equals(observacao.getObservacao().trim())) {
			erros.addErro("� necess�rio informar uma observa��o");
		}
		checkValidation(erros);

	}

}
