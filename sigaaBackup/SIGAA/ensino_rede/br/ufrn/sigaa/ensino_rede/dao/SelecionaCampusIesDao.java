package br.ufrn.sigaa.ensino_rede.dao;

import static br.ufrn.arq.util.ValidatorUtil.isNotEmpty;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;

public class SelecionaCampusIesDao extends GenericSigaaDAO {

	public List<CampusIes> buscar(ProgramaRede programa, String siglaIes) throws DAOException {
		String hql = "select campus from DadosCursoRede d "
				+ "	join d.programaRede programa "
				+ "	join d.campus campus "
				+ "	join campus.instituicao ies "
				+ " where programa.id = :programa ";
		
		if (isNotEmpty(siglaIes))
			hql+= " and ies.sigla = :siglaIes";
		
		hql+= " order by ies.sigla, campus.sigla ";

		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("programa", programa.getId());
		
		if (isNotEmpty(siglaIes))
			query.setParameter("siglaIes", siglaIes);
		
		return query.list();
	}
	
}
