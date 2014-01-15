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
import br.ufrn.sigaa.arq.dao.ensino.BolsistaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.Usuario;

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
		BolsistaDao dao = new BolsistaDao();		
		try{				
			Bolsista bolsista =  mov.getObjMovimentado();
			validate(mov);
			if ( bolsista.getIdBolsa() == 0 ) {
				// Validar se já existe um bolsista ativo para o discente informado
				boolean bolsistaJaCadastrado = dao.hasAtivoByDiscente(bolsista.getDiscente());
				if (bolsistaJaCadastrado) {
					throw new NegocioException("Já existe um registro ativo de bolsa para o discente informado");
				}
				
				bolsista.setIdTipoBolsa(Bolsista.BOLSA_CNPQ);
				dao.create(bolsista);
			}  else {
				dao.update(bolsista);
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
		BolsistaDao dao = new BolsistaDao();
		try{
			Bolsista bolsista =  mov.getObjMovimentado();
			
			if ( bolsista.getIdBolsa() != 0 ) {
				bolsista.setDataFinalizacao(new Date());
				bolsista.setUsuarioFinalizacao((Usuario) mov.getUsuarioLogado());
				dao.finalizar(bolsista);
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
	private BolsistaDao getBolsistaDao(Movimento mov) throws DAOException {
		return getDAO(BolsistaDao.class, mov);
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		Bolsista bolsista =  ((MovimentoCadastro) mov).getObjMovimentado();
		ListaMensagens mensagens = bolsista.validate();
		checkValidation(mensagens);
	}

}
