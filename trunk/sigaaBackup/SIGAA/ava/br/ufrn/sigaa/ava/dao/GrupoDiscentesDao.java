/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/09/2010
 *
 */
package br.ufrn.sigaa.ava.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para operações relacionadas aos grupos de discentes da turma virtual.
 *
 * @author Fred_Castro
 *
 */
public class GrupoDiscentesDao extends GenericSigaaDAO {

	/**
	 * Busca os grupos ativos da turma.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public List<GrupoDiscentes> findGruposDiscentesAtivosByTurma (int idTurma) throws DAOException {
			Criteria c = getCriteria(GrupoDiscentes.class);
			c.add(Expression.eq("turma.id", idTurma));
			c.add(Expression.eq("ativo", true));
			c.addOrder(Order.asc("nome"));
			
			@SuppressWarnings("unchecked")
			List <GrupoDiscentes> lista = c.list();
			
			return lista;
	}
	
	/**
	 * Busca os grupos ativos por discente.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	public GrupoDiscentes findGrupoDiscenteAtivosByDiscenteTurma (int idDiscente , int idTurma) throws DAOException {

		Query queryGrupoDiscentes = getSession().createSQLQuery("select gd.id_grupo_discentes , gd.id_grupo_pai , gd.nome as nome_aluno , dd2.id_discente , d.matricula, d.id_foto , d.id_pessoa , d.id_perfil , u.id_usuario , u.login, p.nome , p.email , d.id_curso , c.nome as nome_curso " +
																	" from ava.grupo_discentes_discente as dd2  " + 
																	" inner join ava.grupo_discentes as gd on dd2.id_grupo_discentes = gd.id_grupo_discentes " +
																	" inner join ava.grupo_discentes_discente as dd1 on dd1.id_grupo_discentes = gd.id_grupo_discentes " +
																	" inner join discente as d on dd2.id_discente = d.id_discente " +
																	" inner join comum.pessoa as p on p.id_pessoa = d.id_pessoa " +
																	" inner join comum.usuario as u on u.id_pessoa = p.id_pessoa " +
																	" left join curso as c on d.id_curso = c.id_curso " +
																	" where dd1.id_discente = " +idDiscente+ " and gd.id_turma = " +idTurma+ " and gd.ativo = true"
																);
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = queryGrupoDiscentes.list();
		
		if ( result != null && result.size() > 0 )
		{	
			GrupoDiscentes g = new GrupoDiscentes();
			List <Discente> discentes = new ArrayList <Discente> ();
			
			for ( Object[] linha : result )
			{
				
				Integer i = 0;
				g.setId( ((Integer) linha[i++]) );
				g.setTurma( new Turma() );
				g.getTurma().setId( idTurma );
				if ( linha[i] != null ) {	
					g.setGrupoPai( new GrupoDiscentes() );
					g.getGrupoPai().setId((Integer) linha[i++]);
				} else
					i++;
				g.setNome( (String) linha[i++] );
				Discente d = new Discente ();
				d.setId( (Integer) linha[i++] );
				d.setMatricula( ((BigInteger) linha[i++]).longValue() );
				d.setIdFoto( (Integer) linha[i++] );
				d.setPessoa( new Pessoa());
				d.getPessoa().setId((Integer) linha[i++]);
				d.setIdPerfil((Integer) linha[i++]);
				d.setUsuario(new Usuario ());
				d.getUsuario().setId((Integer) linha[i++]);
				d.getUsuario().setLogin((String)linha[i++]);
				d.getPessoa().setNome((String)linha[i++]);
				d.getPessoa().setEmail((String)linha[i++]);
				Integer idCurso = (Integer) linha[i++];
				d.setCurso(new Curso ());
				d.getCurso().setId(idCurso == null ? 0 : idCurso );
				d.getCurso().setNome((String)linha[i++]);

				discentes.add(d);
			}
			g.setDiscentes(discentes);
			return g;
		}	

		return null;
	}
	
	/**
	 * Busca os grupos ativos por discente.
	 * 
	 * @param idTurma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<GrupoDiscentes> findAllByTurma (int idTurma){

		String projecao = " g.id , g.nome , g.turma.id , g.grupoPai.id , g.ativo ";
		List<Object[]> list = getHibernateTemplate().find("select " +projecao+ " from GrupoDiscentes g where g.turma.id = "+idTurma);

		List<GrupoDiscentes> result = (List<GrupoDiscentes>) HibernateUtils.parseTo(list, projecao, GrupoDiscentes.class, "g");
		
		return result;
	}
}