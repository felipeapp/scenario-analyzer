package br.ufrn.sigaa.biblioteca.processos_tecnicos.remoto;

import java.util.List;

import javax.jws.WebService;

import org.springframework.context.annotation.Scope;
import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.integracao.dto.MaterialInformacionalDTO;
import br.ufrn.integracao.interfaces.DadosBibliotecaRemoteService;
import br.ufrn.sigaa.arq.dao.UnidadeDao;
import br.ufrn.sigaa.arq.dao.biblioteca.RelatoriosBibliotecaDao;

/**
 * Implementação da interface remota que disponibiliza dados da biblioteca para que outros sistemas acessem.
 * 
 * @author Fred_Castro
 *
 */

@Component("dadosBiblioteca")
@Scope("singleton")
@WebService
public class DadosBibliotecaRemoteServiceImpl implements
		DadosBibliotecaRemoteService {

	/**
	 * @throws DAOException 
	 * @see br.ufrn.integracao.interfaces.DadosBibliotecaRemoteService#getQuantidadeMateriaisAtivosPorTitulo()
	 */
	public List<MaterialInformacionalDTO> getQuantidadeMateriaisAtivosPorTitulo(List <Integer> ids) {
		
		RelatoriosBibliotecaDao dao = null;
		try {
			dao = DAOFactory.getInstance().getDAO(RelatoriosBibliotecaDao.class);
			
			return dao.findQuantidadeMateriaisAtivosPorTitulo(ids);
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}
	}
	
	/**
	 * Método responsável por realizar a busca na unidade da coordenação para um servidor
	 * 
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public int getUnidadeCoordenacaoServidor(int idServidor) {
		UnidadeDao unidadeDAO = null;
		try {
			unidadeDAO = DAOFactory.getInstance().getDAO(UnidadeDao.class);
			
			UnidadeGeral unidade = unidadeDAO.findUnidadeCoordenacaoServidor(idServidor);
			
			int idUnidade = 0;
			
			if(!ValidatorUtil.isEmpty(unidade))
				idUnidade = unidade.getId();
			
			return idUnidade; 
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			if (unidadeDAO != null)
				unidadeDAO.close();
		}
	}
	
	/**
	 * Método responsável por realizar a busca na unidade da coordenação para um curso
	 * 
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public int getUnidadeCoordenacaoAluno(int idCurso,char nivel) {
		UnidadeDao unidadeDAO = null;
		try {
			unidadeDAO = DAOFactory.getInstance().getDAO(UnidadeDao.class);
			
			UnidadeGeral unidade = unidadeDAO.findUnidadeCoordenacaoByCursoAluno(idCurso,nivel);
			
			int idUnidade = 0;
			
			if(!ValidatorUtil.isEmpty(unidade))
				idUnidade = unidade.getId();
			
			return idUnidade; 
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		} finally {
			if (unidadeDAO != null)
				unidadeDAO.close();
		}
	}
}
