/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/09/19 - 19:28:02
 */
package br.ufrn.sigaa.ead.negocio;

import java.rmi.RemoteException;
import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.ead.PoloDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ead.dominio.PoloCurso;

/**
 * Processador para cadastro de Pólos e suas associações
 * com cursos.
 * 
 * @author David Pereira
 *
 */
public class ProcessadorCadastroPolo extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		MovimentoPolo pMov = (MovimentoPolo) mov; 
		
		try {
			if (SigaaListaComando.CADASTRAR_POLO.equals(mov.getCodMovimento()))
				cadastrar(pMov);
			else
				remover(pMov);
		} catch(ConstraintViolationException e) {
			throw new NegocioException("Não foi possível remover este pólo pois ele está sendo usado pelo sistema.");
		}
		
		return null;
	}
	
	private void cadastrar(MovimentoPolo mov) throws DAOException {
		PoloDao dao = getDAO(PoloDao.class, mov);
		Polo polo = mov.getPolo();
		List<Curso> cursos = mov.getCursos();
		
		if (polo.getId() == 0) {
			dao.create(polo);
			
			// Cadastrar associações com cursos
			for (Curso curso : cursos) {
				PoloCurso pc = new PoloCurso();
				pc.setCurso(curso);
				pc.setPolo(polo);
				dao.create(pc);
			}		
			
		} else {
			dao.update(polo);
			
			// Atualizar associações com cursos
			List<Curso> cursosAtuais = dao.findCursosByPolo(polo);			
			for (Curso c : cursosAtuais) {
				if (!cursos.contains(c)) {
					PoloCurso pc = dao.findPoloCurso(polo, c);
					dao.remove(pc);
				} else {
					cursos.remove(c);
				}
			}
					
			for (Curso c : cursos) {
				PoloCurso pc = new PoloCurso();
				pc.setCurso(c);
				pc.setPolo(polo);
				dao.create(pc);
			}
		}
	}
	
	private void remover(MovimentoPolo mov) throws DAOException {
		PoloDao dao = getDAO(PoloDao.class, mov);
		Polo polo = dao.findByPrimaryKey(mov.getPolo().getId(), Polo.class);
		
		// Remover associações com cursos
		List<PoloCurso> pcs = dao.findPolosCurso(polo);
		for (PoloCurso pc : pcs) {
			dao.remove(pc);
		}
		
		dao.remove(polo);
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

}
