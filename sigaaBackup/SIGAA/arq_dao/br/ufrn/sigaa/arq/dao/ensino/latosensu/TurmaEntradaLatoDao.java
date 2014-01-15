/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2006
 *
 */
package br.ufrn.sigaa.arq.dao.ensino.latosensu;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.management.QueryPerformance;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.TipoPeriodicidadeAula;
import br.ufrn.sigaa.ensino.latosensu.dominio.TurmaEntradaLato;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Consultas de TurmaEntradaLato
 *
 * @author Leonardo
 *
 */
public class TurmaEntradaLatoDao extends GenericSigaaDAO {

	public TurmaEntradaLatoDao (){
		daoName = "TurmaEntradaLatoDao";
	}

	/**
	 * Retorna as turmas de entrada de um determinado curso
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Collection<TurmaEntradaLato> findByCursoLato(int idCurso, boolean ativas) throws DAOException {
		long init = System.currentTimeMillis();
		try {
			String hql = "from TurmaEntradaLato turmaEntrada where " +
			"turmaEntrada.cursoLato.id=:idCurso";
			
			if (ativas)
				 hql += " and turmaEntrada.ativo = trueValue()";	
			
			Query q = getSession().createQuery(hql);
			q.setInteger("idCurso", idCurso);
			return q.list();
        } catch (Exception e) {
            throw new DAOException(e);
        } finally {
			QueryPerformance.getInstance().registerQuery(daoName,
					GenericDAOImpl.class, "findByCursolato(CursoLato)",
					System.currentTimeMillis() - init);
		}

	}
	/**
	 * OBS. Eu tentei usar findByPrimaryKey e depois getGenericDao.inicialize no turno,
	 * em tipoPeriodicidadeAula e em campusIes mas dizia que eles nao estavam mapeados,
	 * mas estão no hibernate.cfg. Logo criei este médoto. 
	 * @param idTurma
	 * @return
	 */
	public TurmaEntradaLato findTurmaEntradaLato(Integer idTurma)  {
		try {
			String hql = null;
			hql = "SELECT t.id, t.ativo, t.campusIes, t.turno, t.tipoPeriodicidadeAula, " +
					" t.cursoLato, t.municipio, t.dataInicio, t.vagas, t.inscritos, " +
					" t.dataFim, t.codigo " +
					" FROM TurmaEntradaLato t " +					
					" WHERE t.id = :idTurma "; 

			Query query = getSession().createQuery(hql);
			query.setInteger("idTurma", idTurma);
			List<Object> lista = query.list();
			Object[] obj = (Object[]) lista.get(0);
							
			TurmaEntradaLato turmaEntradaLato = new TurmaEntradaLato();
				
			turmaEntradaLato.setId((Integer) obj[0]);
			turmaEntradaLato.setAtivo((Boolean) obj[1]);
			turmaEntradaLato.setCampusIes((CampusIes) obj[2]);
			turmaEntradaLato.setTurno((Turno) obj[3]);
			turmaEntradaLato.setTipoPeriodicidadeAula((TipoPeriodicidadeAula) obj[4]);
			turmaEntradaLato.setCursoLato((CursoLato) obj[5]);
			turmaEntradaLato.setMunicipio((Municipio) obj[6]);
			turmaEntradaLato.setDataInicio((Date) obj[7]);
			turmaEntradaLato.setVagas((Integer) obj[8]);
			turmaEntradaLato.setInscritos((Integer) obj[9]);
			turmaEntradaLato.setDataFim((Date) obj[10]);
			turmaEntradaLato.setCodigo((String) obj[11]);
			
			
			return turmaEntradaLato;
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}	
}