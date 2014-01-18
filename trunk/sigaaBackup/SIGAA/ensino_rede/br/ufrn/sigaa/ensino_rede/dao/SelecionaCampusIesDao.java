package br.ufrn.sigaa.ensino_rede.dao;

import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.ensino_rede.jsf.ParametrosSelecionaCampus;
import br.ufrn.sigaa.ensino_rede.jsf.ValoresSelecionaCampus;

public class SelecionaCampusIesDao extends GenericSigaaDAO {

	@SuppressWarnings("unchecked")
	public List<CampusIes> buscar(ProgramaRede programa, ParametrosSelecionaCampus param, ValoresSelecionaCampus valor) throws DAOException {
		String hql = "select campus from DadosCursoRede d "
				+ "	join d.programaRede programa "
				+ "	join d.campus campus "
				+ "	join fetch campus.instituicao ies "
				+ " where programa.id = :programa ";
		
		if (valor != null && valor.getValorIes() > 0)
			hql+= " and ies.id = :idIes";
		
		hql+= " order by ies.sigla, campus.sigla ";

		
		Query query = getSession().createQuery(hql);
		
		query.setParameter("programa", programa.getId());
		
		if (valor != null && valor.getValorIes() > 0)
			query.setParameter("idIes", valor.getValorIes());
		
		return query.list();
	}
	

	@SuppressWarnings("unchecked")
	public List<InstituicoesEnsino> findIesByPrograma(ProgramaRede programa) throws DAOException {
		String hql = "select distinct ie from DadosCursoRede dados "
				+ "	join dados.campus campus "
				+ "	join campus.instituicao ie "
				+ "	join dados.programaRede programa "
				+ " where programa.id = :idPrograma "
				+ " order by ie.sigla ";
		
		Query q = getSession().createQuery(hql);
		q.setParameter("idPrograma", programa.getId());
		
		return q.list();
	}
	
}
