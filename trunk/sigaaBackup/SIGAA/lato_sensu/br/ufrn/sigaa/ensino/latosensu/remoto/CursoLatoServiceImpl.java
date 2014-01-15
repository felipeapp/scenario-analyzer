/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '30/09/2009'
 *
 */
package br.ufrn.sigaa.ensino.latosensu.remoto;

import java.util.ArrayList;
import java.util.Collection;

import javax.jws.WebService;

import org.springframework.remoting.RemoteAccessException;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.integracao.dto.CursoLatoDTO;
import br.ufrn.integracao.dto.TurmaLatoDTO;
import br.ufrn.integracao.interfaces.CursoLatoRemoteService;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.latosensu.CursoLatoDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;

/**
 * Implementação local da interface CursoLatoService.
 * O SIGPRH irá se comunicar com essa classe através do Spring HTTP Invoker.
 * @author Diogo Souto
 */
@Component("cursoLatoServiceImpl") @WebService
public class CursoLatoServiceImpl implements CursoLatoRemoteService {

	/**
	 * Retorna as turmas de um determinado curso lato.
	 * @throws DAOException 
	 */
	public Collection<TurmaLatoDTO> findTurmasAbertasOuConsolidadasByCursoLato(int idCursoLato) {
		TurmaDao dao = new TurmaDao();
		dao.setSistema(Sistema.SIGAA);

		try{
			Collection<Turma> turmas = dao.findByCursoLatoSituacao(idCursoLato, SituacaoTurma.ABERTA, SituacaoTurma.CONSOLIDADA);

			ArrayList<TurmaLatoDTO> turmasDTO = new ArrayList<TurmaLatoDTO>();

			for(Turma turma : turmas){
				TurmaLatoDTO turmaDTO = new TurmaLatoDTO();
				turmaDTO.setId(turma.getId());
				turmaDTO.setNome(turma.getNome());
				turmaDTO.setSituacao(turma.getSituacaoTurma().getDescricao());
				turmaDTO.setCargaHoraria(turma.getChTotalTurma());

				turmasDTO.add(turmaDTO);
			}

			return turmasDTO;
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}finally{
			dao.close();
		}
	}

	/**
	 * Retorna os cursos latos coordenados pelo servidor especificado.
	 */
	public Collection<CursoLatoDTO> findCursosLatoCoordenadosPor(int idServidor) {
		CursoLatoDao dao = new CursoLatoDao();
		dao.setSistema(Sistema.SIGAA);

		try{
			Collection<Curso> cursos = dao.findAllCoordenadoPor(idServidor);

			ArrayList<CursoLatoDTO> cursosDTO = new ArrayList<CursoLatoDTO>();

			for(Curso curso : cursos){
				CursoLatoDTO cursoDTO = new CursoLatoDTO();
				cursoDTO.setId(curso.getId());
				cursoDTO.setNome(curso.getNome());

				Unidade unidade = curso.getUnidade();

				UnidadeGeral u = new UnidadeGeral();
				u.setId(unidade.getId());
				u.setNome(u.getNome());
				u.setCodigo(unidade.getCodigo());
				u.setHierarquiaOrganizacional(unidade.getHierarquiaOrganizacional());

				cursoDTO.setIdUnidade(u.getId());

				cursosDTO.add(cursoDTO);
			}

			return cursosDTO;
		} catch (DAOException e) {
			throw new RemoteAccessException(e.getMessage());
		}finally{
			dao.close();
		}
	}
}
