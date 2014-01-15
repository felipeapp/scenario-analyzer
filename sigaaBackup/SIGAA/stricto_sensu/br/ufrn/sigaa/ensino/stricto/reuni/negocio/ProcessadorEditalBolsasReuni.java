/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.stricto.reuni.negocio;

import java.io.IOException;
import java.rmi.RemoteException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.comum.dominio.Arquivo;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.EditalBolsasReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dao.SolicitacaoBolsasReuniDao;
import br.ufrn.sigaa.ensino.stricto.reuni.dominio.EditalBolsasReuni;

/**
 * Processador responsável pelo cadastro de editais de concessão 
 * de bolsas REUNI para a pós-graduação
 * 
 * @author wendell
 *
 */
public class ProcessadorEditalBolsasReuni extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		
		if ( SigaaListaComando.CADASTRAR_EDITAL_BOLSAS_REUNI.equals(mov.getCodMovimento()) ) {
			validate(mov);
			cadastrar(mov);
		} else if ( SigaaListaComando.REMOVER_EDITAL_BOLSAS_REUNI.equals(mov.getCodMovimento()) ) {
			remover( mov );
		}
		return getEdital(mov);
	}

	/**
	 * Cadastra ou atualiza as informações de um edital na base de dados
	 * 
	 * @param mov
	 * @throws ArqException 
	 * @throws DAOException
	 * @throws IOException 
	 */
	private void cadastrar(Movimento mov) throws ArqException {
		EditalBolsasReuni edital = getEdital(mov);
		EditalBolsasReuniDao dao = getDAO(EditalBolsasReuniDao.class, mov);
		
		Arquivo arquivo = getArquivo(mov);
		if ( arquivo != null ) {
			try {
				int idArquivo = EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo, arquivo.getBytes(),
						arquivo.getContentType(), arquivo.getName());
				edital.setIdArquivoEdital(idArquivo);
			} catch (Exception e) {
				throw new ArqException(e);
			}
		}

		dao.createOrUpdate(edital);
	}

	/**
	 * Remove um edital da base de dados
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private void remover(Movimento mov) throws NegocioException, ArqException {
		EditalBolsasReuni edital = getEdital(mov);
		
		// Verificar a existência de solicitações vinculadas ao edital
		SolicitacaoBolsasReuniDao solicitacaoDao = getDAO(SolicitacaoBolsasReuniDao.class, mov);
		long totalSolicitacoes = solicitacaoDao.countByEdital(edital); 
		if ( totalSolicitacoes > 0) {
			throw new NegocioException("Não é possível excluir o edital selecionado pois existem " + 
					totalSolicitacoes + " solicitações já associadas a ele." );
		}
		
		solicitacaoDao.remove(edital);
		
		// Remover o arquivo do edital da base de dados de arquivos
		if (edital.getIdArquivoEdital() != null) {
			EnvioArquivoHelper.removeArquivo(edital.getIdArquivoEdital());
		}
		
	}
	
	/* (non-Javadoc)
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkValidation( getEdital(mov).validate() );
	}
	
	private EditalBolsasReuni getEdital(Movimento mov) {
		return ((MovimentoEditalBolsasReuni) mov).getEdital();
	}
	
	private Arquivo getArquivo(Movimento mov) {
		return ((MovimentoEditalBolsasReuni) mov).getArquivoEdital();
	}
}
