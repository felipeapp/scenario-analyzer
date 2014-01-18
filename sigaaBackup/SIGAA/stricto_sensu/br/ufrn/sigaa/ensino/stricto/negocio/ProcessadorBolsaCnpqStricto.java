/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.negocio;

import java.rmi.RemoteException;
import java.util.Date;

import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.BolsistaSigaaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Bolsistas;

/**
 * Processador responsável pelo registro e finalização de bolsas do CNPq
 * de discentes de pós-graduação Stricto Sensu
 * 
 * @author wendell
 *
 */
public class ProcessadorBolsaCnpqStricto extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		Comando comando = mov.getCodMovimento();
		MovimentoCadastro movimento = (MovimentoCadastro) mov;
		
		if (comando.equals(SigaaListaComando.CADASTRAR_BOLSA_CNPQ_STRICTO)) {
			cadastrar(movimento);
		}
		
		if (comando.equals(SigaaListaComando.FINALIZAR_BOLSA_CNPQ_STRICTO) ) {
			finalizar(movimento);
		}
		
		return null;
	}

	/**
	 * Cadastra ou atualiza as informações referentes à bolsa
	 * 
	 * @param mov
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	private void cadastrar(MovimentoCadastro mov) throws NegocioException, ArqException {		
		BolsistaSigaaDao dao = getBolsistaSigaaDao(mov);		
		try{				
			Bolsistas bolsista =  mov.getObjMovimentado();
			validate(mov);
			if ( bolsista.getBolsa().getId() == 0 ) {
				// Validar se já existe um bolsista ativo para o discente informado
				boolean bolsistaJaCadastrado = dao.hasAtivoByDiscente(bolsista.getDiscente());
				if (bolsistaJaCadastrado) {
					throw new NegocioException("Já existe um registro ativo de bolsa para o discente informado");
				}				
				dao.create(bolsista);
				bolsista.getBolsa().setBolsista(bolsista);
				dao.create(bolsista.getBolsa());
			}  else {
				dao.update(bolsista.getBolsa());
			}
		}
		finally {
			dao.close();
		}
	}
	
	/**
	 * Finaliza a bolsa do bolsista informado
	 * 
	 * @param movimento
	 * @throws DAOException 
	 * @throws NegocioException 
	 */
	private void finalizar(MovimentoCadastro mov) throws DAOException, NegocioException {
		BolsistaSigaaDao dao = getBolsistaSigaaDao(mov);
		try{
			Bolsistas bolsista =  mov.getObjMovimentado();
			
			if ( bolsista.getBolsa().getId() != 0 ) {
				bolsista.getBolsa().setFim(new Date());				
				dao.finalizar(bolsista,(Usuario) mov.getUsuarioLogado());
			} else {
				throw new NegocioException("É necessário informar o bolsista cuja bolsa terá seu registro finalizado.");
			}
		}
		finally {
			dao.close();
		}
	}


	/**
	 * Retorna o DAO para consulta aos dados registrados dos bolsistas
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private BolsistaSigaaDao getBolsistaSigaaDao(Movimento mov) throws DAOException {
		return getDAO(BolsistaSigaaDao.class, mov);
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		Bolsistas bolsista =  ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens mensagens = bolsista.validate();
		mensagens.addAll(bolsista.getBolsa().validate());
		checkValidation(mensagens);
	}

}
