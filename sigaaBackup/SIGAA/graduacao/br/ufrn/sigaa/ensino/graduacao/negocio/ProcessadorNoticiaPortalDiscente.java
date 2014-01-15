/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;

/**
 * Processador responsável por gerenciar as noticias publicadas no portal do discente
 * pelos cursos e programas
 * 
 * @author Henrique André
 *
 */
public class ProcessadorNoticiaPortalDiscente extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		checkRole(new int[] {SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO,
				SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS}, mov);

		MovimentoNoticiaPortalDiscente movNoticia = (MovimentoNoticiaPortalDiscente) mov;
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ADICIONAR_NOTICIA_PORTAL_DISCENTE))
			criarNoticia(movNoticia);
		else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_NOTICIA_PORTAL_DISCENTE))
			atualizar(movNoticia);
		else if (mov.getCodMovimento().equals(SigaaListaComando.REMOVER_NOTICIA_PORTAL_DISCENTE))
			remover(movNoticia);
		else if (mov.getCodMovimento().equals(SigaaListaComando.DESPUBLICAR_NOTICIA_PORTAL_DISCENTE))
			atualizar(movNoticia);
		else if (mov.getCodMovimento().equals(SigaaListaComando.PUBLICAR_NOTICIA_PORTAL_DISCENTE))
			atualizar(movNoticia);
		
		return movNoticia.getNoticia();
	}

	/**
	 * Excluir noticia
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void remover(MovimentoNoticiaPortalDiscente mov) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.remove(mov.getNoticia());
		} finally {
			dao.close();
		}
	}

	/**
	 * Realiza alteração da noticia
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void atualizar(MovimentoNoticiaPortalDiscente mov) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.update(mov.getNoticia());
		} finally {
			dao.close();
		}

		
	}

	/**
	 * Persiste uma nova noticia
	 * 
	 * @param mov
	 * @throws DAOException
	 */
	private void criarNoticia(MovimentoNoticiaPortalDiscente mov) throws DAOException {
		GenericDAO dao = DAOFactory.getGeneric(Sistema.COMUM);
		try {
			dao.create(mov.getNoticia());
		} finally {
			dao.close();
		}
		
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		// TODO Auto-generated method stub
		
	}

}
