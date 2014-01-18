package br.ufrn.sigaa.ensino_rede.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino_rede.dominio.CursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.DiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.OfertaCursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.StatusDiscenteAssociado;
import br.ufrn.sigaa.ensino_rede.resources.DadosCursoRedeDTO;

public class ConfirmacaoVinculoDao extends GenericSigaaDAO {
	
	@SuppressWarnings("unchecked")
	public List<DiscenteAssociado> findDiscenteByCampusConvocacao(int idCampus, int idConvocacao) throws HibernateException, DAOException {
		
		String projecao = "da.id, da.pessoa.nome, da.status.id, da.status.descricao, da.dadosCurso.id, da.dadosCurso.curso.nome, da.dadosCurso.curso.programa.descricao";
		String hql = " select " + projecao +
					 " from DiscenteAssociado da " +
					 " join da.dadosCurso dc " +
					 " join dc.campus c " +
					 " where da.convocacao.id = " + idConvocacao +
					 " and c.id = " +idCampus+
					 " and da.status.id = " + StatusDiscenteAssociado.PRE_CADASTRADO;

		hql += " order by da.pessoa.nome ";
		
		Query q = getSession().createQuery(hql.toString());
		
		List <Object[]> lista = q.list();
		return new ArrayList<DiscenteAssociado>(HibernateUtils.parseTo(lista, projecao, DiscenteAssociado.class, "da"));
	}

	@SuppressWarnings("unchecked")
	public Collection<OfertaCursoAssociado> findQuadroConvocacao(int ano, int periodo) throws HibernateException, DAOException {
		
		String sql = "select ca.nome, oca.total_vagas, count(*) as total_ativos " +
				 " FROM ensino_rede.discente_associado da " +
				 " JOIN ensino_rede.dados_curso_rede dcr using ( id_dados_curso_rede )" +
				 " JOIN ensino_rede.curso_associado ca using ( id_curso_associado )" +
				 " JOIN ensino_rede.oferta_curso_associado oca on ( dcr.id_dados_curso_rede = oca.id_dados_curso_rede )" +
				 " WHERE da.ano_ingresso = 2013 and da.periodo_ingresso = 2" +
				 " and oca.ano = da.ano_ingresso and oca.periodo = da.periodo_ingresso" +
				 " and da.id_status = 1" +
				 " GROUP BY ca.nome, oca.total_vagas";

		@SuppressWarnings("unchecked")
		List <Object[]> ls = getSession().createSQLQuery(sql).list();
		
		List<OfertaCursoAssociado> rs = new ArrayList<OfertaCursoAssociado>();
		
		OfertaCursoAssociado oferta = null;
		
		for (Object [] l : ls){
			int i = 0;
			
			oferta = new OfertaCursoAssociado();
			oferta.setDadosCurso(new DadosCursoRede());
			oferta.getDadosCurso().setCurso(new CursoAssociado());
			oferta.getDadosCurso().getCurso().setNome( (String) l[i++] );
			oferta.setTotalVagas( (Integer) l[i ++] );
			oferta.setTotalAtivos( (Integer) l[i ++] );
			rs.add(oferta);
		}
		
		return rs;
	}
	
}