/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 */
package br.ufrn.sigaa.ava.negocio;

import java.rmi.RemoteException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import org.hibernate.StaleStateException;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.PortaArquivosDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.PastaArquivos;
import br.ufrn.sigaa.cv.dominio.ArquivoComunidade;
import br.ufrn.sigaa.dominio.Usuario;

/**
 * Processador para realizar as operações do porta arquivos
 * no ambiente virtual de aprendizado.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorPortaArquivos extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {

		PortaArquivosDao dao = getDAO(PortaArquivosDao.class, mov);
		MovimentoCadastro cMov = (MovimentoCadastro) mov;

		try {

			if (mov.getCodMovimento().equals(SigaaListaComando.RENOMEAR_ARQUIVO)) {
				ArquivoUsuario arq = (ArquivoUsuario) cMov.getObjMovimentado();
				renomearArquivo(arq, dao);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_ARQUIVO)) {
				ArquivoUsuario arq = (ArquivoUsuario) cMov.getObjMovimentado();
				removerArquivo(arq, dao);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_PASTA)) {
				PastaArquivos pasta = (PastaArquivos) cMov.getObjMovimentado();
				pasta.setUsuario((Usuario) mov.getUsuarioLogado());
				pasta.setData(new Date());
				cadastrarPasta(pasta, dao);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_PASTA)) {
				PastaArquivos pasta = (PastaArquivos) cMov.getObjMovimentado();
				removerPasta(pasta, dao, cMov);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.MOVER_ARQUIVO)) {
				ArquivoUsuario arq = (ArquivoUsuario) cMov.getObjMovimentado();
				moverArquivo(arq, dao);
			} else if (mov.getCodMovimento().equals(SigaaListaComando.MOVER_PASTA)) {
				MovimentoPortaArquivos paMov = (MovimentoPortaArquivos) cMov;
				moverPasta(paMov, dao);
			}

		} catch(Exception e) {
			throw new ArqException(e.getMessage(), e);
		} finally {
			dao.close();
		}

		return null;
	}

	/**
	 * Move a pasta, alterando a pasta "pai" da pasta passada no movimento para ser a pasta de destino.
	 * 
	 * @param paMov
	 * @param dao
	 * @throws DAOException
	 */
	private void moverPasta(MovimentoPortaArquivos paMov, PortaArquivosDao dao) throws DAOException {
		if (paMov != null) {
			int idOrigem = paMov.getOrigem();
			int idDestino = paMov.getDestino();
			
			PastaArquivos pasta = dao.findByPrimaryKey(idOrigem, PastaArquivos.class);
			
			if (idDestino == -1) { // Meus Arquivos
				pasta.setPai(null);
			} else {				
				PastaArquivos novoPai = dao.findByPrimaryKey(idDestino, PastaArquivos.class);
				pasta.setPai(novoPai);
			}
			
			dao.update(pasta);
		}
	}

	/**
	 * Move um arquivo de uma pasta para outra
	 * @throws DAOException 
	 */
	private void moverArquivo(ArquivoUsuario arq, PortaArquivosDao dao) throws DAOException {
		if (arq != null) {
			int idPasta = arq.getPasta().getId();
			arq = dao.findByPrimaryKey(arq.getId(), ArquivoUsuario.class);
			
			if (idPasta == -1) { // Meus Arquivos
				arq.setPasta(null);
			} else {				
				PastaArquivos pasta = dao.findByPrimaryKey(idPasta, PastaArquivos.class);
				arq.setPasta(pasta);
			}
			
			dao.update(arq);
		}
	}

	/**
	 * Insere uma nova pasta na árvore de diretórios
	 * @param pasta
	 * @param dao
	 * @throws DAOException
	 */
	private void cadastrarPasta(PastaArquivos pasta, PortaArquivosDao dao) throws DAOException {
		if (pasta.getPai().getId() == -1)
			pasta.setPai(null);
		if (pasta.getId() == 0)
			dao.create(pasta);
		else
			dao.update(pasta);
	}

	/**
	 * Remove uma pasta e o seu conteúdo (pastas filhas e arquivos)
	 * @param pasta
	 * @param dao
	 * @throws SQLException
	 * @throws ArqException
	 */
	private void removerPasta(PastaArquivos pasta, PortaArquivosDao dao, MovimentoCadastro mov) throws ArqException {
		if (pasta != null)
			pasta = dao.findByPrimaryKey(pasta.getId(), PastaArquivos.class);
		if (pasta != null) {
			try {
				
				List<ArquivoComunidade> arqComunidades = dao.findArquivosComunidadeByPasta(pasta);
							
				for (ArquivoComunidade ac : arqComunidades) {
					dao.remove(ac);
				}
				
				dao.remove(pasta);
			} catch(StaleStateException e) {
				// Silenciar exceção. Como a remoção é feita por ajax, o arquivo pode ser 
				// "removido" mais de uma vez, causando o erro.
			}
		}
	}

	/**
	 * Renomeia um arquivo
	 * @param arq
	 * @param dao
	 * @throws DAOException
	 */
	private void renomearArquivo(ArquivoUsuario arq, PortaArquivosDao dao) throws DAOException {
		if (arq != null) {
			String novoNome = arq.getNome();
			arq = dao.findByPrimaryKey(arq.getId(), ArquivoUsuario.class);
			arq.setNome(novoNome);
			dao.update(arq);
		}
	}

	/**
	 * Remove um arquivo
	 * @param arq
	 * @param dao
	 * @throws DAOException
	 * @throws SQLException
	 */
	public void removerArquivo(ArquivoUsuario arq, PortaArquivosDao dao) throws ArqException  {
		if (arq != null) {
			arq = dao.findByPrimaryKey(arq.getId(), ArquivoUsuario.class);
			if (arq != null) {
				EnvioArquivoHelper.removeArquivo(arq.getIdArquivo());
				
				List<ArquivoTurma> arqTurmas = dao.findArquivosTurmaByArquivo(arq);
				List<ArquivoComunidade> arqComunidades = dao.findArquivosComunidadeByArquivo(arq);
				
				for (ArquivoTurma at : arqTurmas) {
					dao.remove(at);
				}
				
				for (ArquivoComunidade ac : arqComunidades) {
					dao.remove(ac);
				}
				
				dao.remove(arq);
			}
		}
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
