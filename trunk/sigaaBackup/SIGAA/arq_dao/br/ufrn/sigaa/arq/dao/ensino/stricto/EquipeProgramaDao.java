/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 26/11/2007
 *
 */	
package br.ufrn.sigaa.arq.dao.ensino.stricto;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.stricto.dominio.AreaConcentracao;
import br.ufrn.sigaa.ensino.stricto.dominio.EquipePrograma;
import br.ufrn.sigaa.ensino.stricto.dominio.LinhaPesquisaStricto;
import br.ufrn.sigaa.ensino.stricto.dominio.NivelEquipe;
import br.ufrn.sigaa.ensino.stricto.dominio.VinculoEquipe;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
/**
 * DAO para consultas de equipes de programas de stricto
 *
 * @author Andre Dantas
 *
 */
public class EquipeProgramaDao extends GenericSigaaDAO {

	public List<EquipePrograma> findByPrograma(int programa) throws DAOException {
		return findByPrograma(programa, null);
	}

	/**
	 * Retorna todos os EquipePrograma de um programa especificado com o vínculo especificado
	 * se o vínculo for nulo retorna todos
	 * @param programa
	 * @param vinculo
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<EquipePrograma> findByPrograma(int programa, VinculoEquipe vinculo) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM EquipePrograma ep WHERE ep.programa.id=:programa and ep.ativo=trueValue() ");
		if (vinculo != null){
			hql.append(" AND vinculo.id = :vinculo ");
		}

		Query q = getSession().createQuery(hql.toString());

		q.setInteger("programa", programa);
		if (vinculo != null)
			q.setInteger("vinculo", vinculo.getId());
		
		List<EquipePrograma> equipe = q.list();

		/**
		 * Como existem servidores e docentes externos fica mais fácil ordenar pelo nome usando um comparator
		 */
		Collections.sort(equipe, new Comparator<EquipePrograma>(){

			public int compare(EquipePrograma o1, EquipePrograma o2) {
				 return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
			
		});
		
		return equipe;
	}
	
	/**
	 * Retorna todos os EquipePrograma de um programa especificado com o nível especificado
	 * se o nível for nulo retorna todos ordenados por área, nível e nome
	 * @param programa
	 * @param nivel
	 * @return
	 * @throws DAOException
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<EquipePrograma> findByPrograma(int programa, VinculoEquipe vinculo, NivelEquipe nivel) throws DAOException {
		StringBuilder hql = new StringBuilder();
		
		hql.append( " SELECT ep.id, ep.nivel.denominacao, ac.denominacao, lp, p.id, " );
		hql.append( "  p.nome, p2.id, p2.nome, s.id, s.siape, de.id, s.idPerfil, de.idPerfil, v.denominacao " );
		hql.append( " FROM EquipePrograma ep  LEFT JOIN ep.docenteExterno de  LEFT JOIN de.pessoa p2 ");
		hql.append( " LEFT JOIN ep.servidor s  LEFT JOIN s.pessoa p LEFT JOIN ep.vinculo v " );
		hql.append( " LEFT JOIN ep.areaConcentracaoPrincipal ac LEFT JOIN ep.linhasPesquisa lp " );
		hql.append( " WHERE ep.programa.id = :programa and ep.ativo = trueValue() ");
				
		if (vinculo != null)
			hql.append(" AND nivel.id = :nivel ");
		
		if (vinculo != null)
			hql.append(" AND vinculo.id = :vinculo ");
		
		//hql.append("  ORDER BY  ac.denominacao, ep.nivel.denominacao");	
	
		Query q = getSession().createQuery(hql.toString());
		
		q.setInteger("programa", programa);

		if (nivel != null)
			q.setInteger("nivel", nivel.getId());
		
		if (vinculo != null)
			q.setInteger("vinculo", vinculo.getId());
		
		List docentesPrograma = q.list();
		Iterator it = docentesPrograma.iterator();
		
		ArrayList<EquipePrograma> result = new ArrayList<EquipePrograma>();
		
		EquipePrograma ep = new EquipePrograma();
		Set<LinhaPesquisaStricto> listaLp = new HashSet<LinhaPesquisaStricto>();
		
		int idOld = 0;
		
		while (it.hasNext()) {

			Object[] colunas = (Object[]) it.next();

		
			
			int idNew = (Integer) colunas[0];
			
			//Se id da equipe do programa diferente do anterior
			if (idOld != idNew){
				
				if(idOld != 0){
					ep.setLinhasPesquisa(listaLp);
					result.add(ep);
					listaLp = new HashSet<LinhaPesquisaStricto>();
				}
				ep = new EquipePrograma();
				
				ep.setId(idNew);
				ep.setNivel(new NivelEquipe());
				ep.getNivel().setDenominacao((String)colunas[1]);
				
				ep.setAreaConcentracaoPrincipal(new AreaConcentracao());
				ep.getAreaConcentracaoPrincipal().setDenominacao((String) colunas[2]);
				
	
				//Subconsulta para pegar os dados do usuário relacionado a pessoa seja Docente Externo ou Servidor.
				StringBuilder hql2 = new StringBuilder();
				hql2.append( " FROM Usuario u" );
				hql2.append( " WHERE u.pessoa.id = :pessoa");
				Query q2 = getSession().createQuery(hql2.toString());
				Usuario u;
				
				//Se Docente(Servidor)
				if(colunas[4] != null){
					ep.setServidor(new Servidor((Integer) colunas[8],(String)colunas[5]));
					ep.getServidor().setSiape((Integer) colunas[9]);
					if(colunas[11] != null)
						ep.getServidor().setIdPerfil((Integer) colunas[11]);
					
					q2.setInteger("pessoa",(Integer) colunas[4]);
					Collection<Usuario> usuarios =   q2.list();
					if(usuarios.size()>0){
						u = usuarios.iterator().next();
						ep.getServidor().getPessoa().setTelefone(u.getRamal());
						ep.getServidor().getPessoa().setEmail(u.getEmail());
					}
				}
				//Se Docente Externo
				if(colunas[6] != null){
					ep.setDocenteExterno(new DocenteExterno((Integer)colunas[10], null, (String)colunas[7]));
					q2.setInteger("pessoa",(Integer) colunas[6]);
					
					Collection<Usuario> usuarios =   q2.list();
					if(usuarios.size()>0){
						u = usuarios.iterator().next();
						ep.getDocenteExterno().getPessoa().setTelefone(u.getRamal());
						ep.getDocenteExterno().getPessoa().setEmail(u.getEmail());
						if(colunas[12] != null)
							ep.getDocenteExterno().setIdPerfil((Integer) colunas[12]);
					}	
				}	
				ep.setVinculo(new VinculoEquipe());
				ep.getVinculo().setDenominacao((String) colunas[13]);

				idOld = idNew;	
				
			}
			
			//Adiciona o conjunto de linhas de pesquisa do docente
			if(colunas[3] != null)
				listaLp.add((LinhaPesquisaStricto) colunas[3]);

		}
		if(!isEmpty(ep)){
			ep.setLinhasPesquisa(listaLp);
			result.add(ep);
		}
		return result;
	}

	/**
	 * Retorna o vínculo EquipePrograma do membro informado no programa informado
	 * @param programa
	 * @param idServidor
	 * @param idDocenteExterno
	 * @return
	 * @throws DAOException
	 */
	public EquipePrograma findByProgramaMembro(int programa, Integer idServidor, Integer idDocenteExterno) throws DAOException {
		StringBuilder hql = new StringBuilder();
		hql.append(" FROM EquipePrograma ep WHERE ep.programa.id=:programa and ep.ativo=trueValue() ");
		if (idServidor != null){
			hql.append(" AND servidor.id = :idServidor ");
		}
		if (idDocenteExterno != null){
			hql.append(" AND docenteExterno.id = :idDocenteExterno ");
		}
		Query q = getSession().createQuery(hql.toString());

		q.setInteger("programa", programa);
		if (idServidor != null)
			q.setInteger("idServidor", idServidor);
		if (idDocenteExterno != null)
			q.setInteger("idDocenteExterno", idDocenteExterno);
		return (EquipePrograma) q.uniqueResult();
	}

	/**
	 * Retorna todos os programas que o servidor indicado possui vínculo ativo
	 * @param servidor
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Unidade> findProgramasByServidor(int servidor) throws DAOException {
		Query q = getSession().createQuery(
				"SELECT ep.programa FROM EquipePrograma ep " + "WHERE ep.servidor.id=:servidor and ep.ativo=trueValue() ");
		q.setInteger("servidor", servidor);
		return q.list();
	}


	/**
	 * Retorna todos os programas que a pessoa especificada possui algum vínculo ativo
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<EquipePrograma> findByPessoa(int pessoa) throws DAOException {
		Query q = getSession().createQuery(
				"FROM EquipePrograma WHERE ativo=trueValue() and servidor.pessoa.id=" + pessoa);
		Collection<EquipePrograma> res =  q.list();
		q = getSession().createQuery(
				"FROM EquipePrograma WHERE ativo=trueValue() and docenteExterno.pessoa.id=" + pessoa);
		res.addAll(q.list());
		return res;
	}

	/**
	 * Retorna o total de servidores da Instituição que são membros de programas de pós STRICTO SENSU
	 * @return
	 * @throws DAOException
	 */
	public int countTotalServidoresPrograma(Integer idPrograma) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append( " SELECT COUNT(distinct ep.id_servidor) FROM stricto_sensu.equipe_programa ep " );
		sql.append( " JOIN rh.servidor s on ep.id_servidor = s.id_servidor " );
		sql.append( " WHERE ep.ativo = trueValue() " );

		if( idPrograma != null )
			sql.append( " AND ep.id_programa = " + idPrograma );
			
		Query q = getSession().createSQLQuery( sql.toString() );
		return ((BigInteger) q.uniqueResult()).intValue();
	}
	
	/**
	 * Retorna o total de docentes externos da ufrn que são membros de programas de pós STRICTO SENSU
	 * @return
	 * @throws DAOException
	 */
	public int countTotalDocentesExternosPrograma( Integer idPrograma ) throws DAOException {
		
		StringBuilder sql = new StringBuilder();
		sql.append( " SELECT COUNT(distinct ep.id_docente_externo) FROM stricto_sensu.equipe_programa ep " );
		sql.append( " JOIN ensino.docente_externo de on de.id_docente_externo = ep.id_docente_externo " );
		sql.append( " WHERE ep.ativo = trueValue() " );

		if( idPrograma != null )
			sql.append( " AND ep.id_programa = " + idPrograma );
			
		Query q = getSession().createSQLQuery( sql.toString() );
		return ((BigInteger) q.uniqueResult()).intValue();
		
	}
	
	/**
	 * Retorna o total de docentes da ufrn que são membros de programas de pós STRICTO SENSU, tanto interno quanto externos
	 * @return
	 * @throws DAOException
	 */
	public int countTotalMembrosPrograma( Integer idPrograma ) throws DAOException {
		return countTotalDocentesExternosPrograma(idPrograma) + countTotalServidoresPrograma(idPrograma);
	}
	
}