/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.SolicitacaoBolsasReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.PlanoTrabalhoReuni;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.SolicitacaoBolsasReuni;

/**
 * Processador responsável pelo cadastro de solicitações de bolsas REUNI 
 * de pós-graduação e seus planos de trabalho associados.
 * 
 * @author wendell
 *
 */
public class ProcessadorSolicitacaoBolsasReuni extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		SolicitacaoBolsasReuni solicitacao = getSolicitacao(mov);
		SolicitacaoBolsasReuniDao solicitacaoDao = getSolicitacaoDao(mov);
		try {
		validate(mov);
		definirStatusSolicitacao(mov);
		prepararDadosPlanosTrabalho(mov);
		
		if ( solicitacao.getId() == 0 ) {
			solicitacaoDao.create(solicitacao);
		} else {
			persistirPossivelRemocaoPlanos(mov, solicitacaoDao);
			solicitacaoDao.update(solicitacao);
		}
		} finally {
			solicitacaoDao.close();
		}
		return solicitacao;
	}

	/**
	 * Prepara os dados dos planos de trabalho para se sejam persistidos
	 * @param mov 
	 * 
	 * @param solicitacao
	 */
	private void prepararDadosPlanosTrabalho(Movimento mov) {
		for (PlanoTrabalhoReuni plano : getSolicitacao(mov).getPlanos() ) {
			if (plano.getLinhaAcao() == PlanoTrabalhoReuni.LINHA_ACAO_1) {
				plano.setAreaConhecimento(null);
			} else {
				plano.setComponenteCurricular(null);
				plano.setJustificativaComponenteCurricular(null);
				plano.setDocentes(null);
				plano.setCursos(null);
			}
			
			if (plano.getId() == 0) {
				plano.setRegistroCadastro(mov.getUsuarioLogado().getRegistroEntrada());
				plano.setDataCadastro(new Date());
			}
			
		}
	}

	/**
	 * Persistir a remoção de qualquer plano removido de uma solicitação previamente
	 * cadastrada
	 * 
	 * @param mov
	 * @param solicitacaoDao2 
	 * @throws DAOException
	 * @throws NegocioException 
	 */
	private void persistirPossivelRemocaoPlanos(Movimento mov, SolicitacaoBolsasReuniDao solicitacaoDao) throws DAOException, NegocioException {
		SolicitacaoBolsasReuni solicitacao = getSolicitacao(mov);
		
		SolicitacaoBolsasReuni solicitacaoCadastrada = solicitacaoDao.findAndFetch(solicitacao.getId(), SolicitacaoBolsasReuni.class, "planos");
		if (solicitacaoCadastrada == null) {
			return;
		}
		solicitacaoDao.detach(solicitacaoCadastrada);
		
		Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			Statement st = con.createStatement();
			
			Collection<PlanoTrabalhoReuni> planosCadastrados = solicitacaoCadastrada.getPlanos();
			for (PlanoTrabalhoReuni plano : planosCadastrados) {
				if ( !solicitacao.getPlanos().contains(plano) ) {
					st.addBatch("DELETE FROM stricto_sensu.plano_trabalho_reuni WHERE id_plano_trabalho_reuni = "+ solicitacao.getId());
				}
			}			
			st.executeBatch();
		}catch (SQLException e) {
			e.printStackTrace();
			throw new NegocioException("Erro ao remover Planos de Trabalhos");
		} finally {
			try {
				if(con != null)
					con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Define o status em que a solicitação será gravada, dependendo da operação definida
	 * 
	 * @param mov
	 * @return
	 * @throws NegocioException
	 */
	private void definirStatusSolicitacao(Movimento mov) throws NegocioException {
		SolicitacaoBolsasReuni solicitacao = getSolicitacao(mov);
		
		if (!SigaaListaComando.ALTERAR_STATUS_SOLICITACAO_BOLSAS_REUNI.equals(mov.getCodMovimento())){
			if ( SigaaListaComando.SALVAR_SOLICITACAO_BOLSAS_REUNI.equals(mov.getCodMovimento()) ) {
				solicitacao.setStatus(SolicitacaoBolsasReuni.CADASTRADA);
			} else if ( SigaaListaComando.SUBMETER_SOLICITACAO_BOLSAS_REUNI.equals(mov.getCodMovimento()) ) {
				solicitacao.setStatus(SolicitacaoBolsasReuni.SUBMETIDA);
			} else {
				throw new NegocioException("É necessário especificar a operação a realizar sobre a solitação") ;
			}			
		}
		
	}

	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		SolicitacaoBolsasReuni solicitacao = getSolicitacao(mov);
		SolicitacaoBolsasReuniDao solicitacaoDao = getSolicitacaoDao(mov);
		try {
		checkValidation( solicitacao.validate() );

		// Validar solicitações duplicadas de um programa para um mesmo edital
		SolicitacaoBolsasReuni solicitacaoCadastrada = solicitacaoDao.findByProgramaAndEdital(solicitacao.getPrograma() , solicitacao.getEdital());
		if ( !isEmpty(solicitacaoCadastrada) && (isEmpty(solicitacao) || solicitacaoCadastrada.getId() != solicitacao.getId()) ) {
			throw new NegocioException("Não é possível cadastrar uma outra solicitação para o mesmo programa neste edital");
		}
		
		solicitacaoDao.detach(solicitacaoCadastrada);
		} finally {
			solicitacaoDao.close();
		}
	}
	/**
	 * Retorna a solicitação conforme o movimento
	 * @param mov
	 * @return
	 */
	private SolicitacaoBolsasReuni getSolicitacao(Movimento mov) {
		return ((MovimentoCadastro) mov).getObjMovimentado();
	}
	
	/**
	 * Retorna o Dao da solicitação conforme o movimento
	 * @param mov
	 * @return
	 * @throws DAOException
	 */
	private SolicitacaoBolsasReuniDao getSolicitacaoDao(Movimento mov)	throws DAOException {
		return getDAO(SolicitacaoBolsasReuniDao.class, mov);
	}	
	
	
}
