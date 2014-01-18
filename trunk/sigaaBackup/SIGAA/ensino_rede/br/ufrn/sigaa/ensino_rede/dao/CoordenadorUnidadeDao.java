package br.ufrn.sigaa.ensino_rede.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.CampusIes;
import br.ufrn.sigaa.dominio.InstituicoesEnsino;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.CargoAcademico;
import br.ufrn.sigaa.ensino_rede.dominio.CoordenadorUnidade;
import br.ufrn.sigaa.ensino_rede.dominio.CursoAssociado;
import br.ufrn.sigaa.ensino_rede.dominio.DadosCursoRede;
import br.ufrn.sigaa.ensino_rede.dominio.ProgramaRede;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/** 
 * Dao responsável pelas consultas referentes a coodenadores de unidade.
 * 
 */
public class CoordenadorUnidadeDao extends GenericSigaaDAO {

	/**
	 * Retorna uma lista de coordenadores relacionados ao campus passado no parametro
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 */
	public Collection<CoordenadorUnidade> findCoordenadoresByCampusIes(int idCampus) throws DAOException{
		
		String projecao = "coord.id,  coord.pessoa.nome, coord.cargo.id, coord.cargo.descricao, coord.dadosCurso.curso.nome" +
						  ", cp.instituicao.id,cp.instituicao.nome,cp.instituicao.sigla,cp.id,cp.nome ";
		
		String hql = ("select " + projecao + 
					  " from CoordenadorUnidade coord " +
					  " inner join coord.dadosCurso dc " +
					  " inner join dc.campus cp " +
					  " where cp.id = :idCampus " +
					  " and ativo = trueValue() ");
		Query query = getSession().createQuery(hql);
		query.setParameter("idCampus", idCampus);
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.list();
		Iterator<Object[]> it = list.iterator();

		List<CoordenadorUnidade> resultado = null;
		
		while(it.hasNext()) {
			
			if (resultado == null)
				resultado = new ArrayList<CoordenadorUnidade>();
			
			int coluna = 0;
			CoordenadorUnidade coordenador = new CoordenadorUnidade();
			Object[] obj = it.next();
			
			coordenador.setId((Integer) obj[coluna++]);
			coordenador.setPessoa(new Pessoa());
			coordenador.getPessoa().setNome((String) obj[coluna++]);
			coordenador.setCargo(new CargoAcademico());
			coordenador.getCargo().setId((Integer) obj[coluna++]);
			coordenador.getCargo().setDescricao((String) obj[coluna++]);
			coordenador.setDadosCurso(new DadosCursoRede());
			coordenador.getDadosCurso().setCurso(new CursoAssociado());
			coordenador.getDadosCurso().getCurso().setNome((String) obj[coluna++]);
			coordenador.setInstituicao(new InstituicoesEnsino());
			coordenador.getInstituicao().setId((Integer) obj[coluna++]);
			coordenador.getInstituicao().setNome((String) obj[coluna++]);
			coordenador.getInstituicao().setSigla((String) obj[coluna++]);
			coordenador.setCampus(new CampusIes((Integer) obj[coluna++]));
			coordenador.getCampus().setNome((String) obj[coluna++]);
			
			resultado.add(coordenador);
		}
		
		
		return resultado;
	}
	
	/**
	 * Retorna uma lista de coordenadores relacionados ao Programa em rede e a instituição passados no parametro
	 * @param programa
	 * @param idIes
	 * @return
	 * @throws DAOException 
	 */
	public Collection<CoordenadorUnidade> findCoordenacaoByIesCargos(ProgramaRede programa, int idIes,Integer idCampus,Integer... cargos) 
			throws  DAOException{
		
		String projecao = "coord_und.id, coord_und.pessoa.nome, coord_und.cargo.id,coord_und.cargo.descricao,coord_und.ativo," +
				" cp.instituicao.id,cp.instituicao.nome,cp.instituicao.sigla, cp.id, cp.nome,u.login ";
		
		String hql = "select " + projecao + 
				" from Usuario u, CoordenadorUnidade coord_und" +
				" inner join coord_und.pessoa p " +
				" inner join coord_und.dadosCurso dc" +
				" inner join dc.campus cp " +
				" right join u.pessoa p " +
				" where dc.programaRede.id = :programa " +
				" and p.id = coord_und.pessoa.id" +
				" and coord_und.ativo = trueValue()" +
				" and coord_und.cargo.id in "+ UFRNUtils.gerarStringIn(cargos);
		
		if (idIes > 0)
			hql += " and cp.instituicao.id = :idIes";
		if (idCampus != null)
			hql += " and cp.id = :idCampus";
		hql += " order by cp.instituicao.sigla, cp.sigla,coord_und.cargo.id ";
		
		Query query = getSession().createQuery(hql);
		query.setParameter("programa", programa.getId());
		
		if (idIes > 0)
			query.setParameter("idIes", idIes);
		if (idCampus != null)
			query.setParameter("idCampus", idCampus);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		Iterator<Object[]> it = lista.iterator();

		List<CoordenadorUnidade> resultado = new ArrayList<CoordenadorUnidade>();
		int id = 0;
		while(it.hasNext()) {
			
			int coluna = 0;
			
			CoordenadorUnidade coordenador = new CoordenadorUnidade();
			Object[] obj = it.next();
			if(id == (Integer) obj[0])
				continue;
			id = (Integer) obj[0];
			
			coordenador.setId((Integer) obj[coluna++]);
			coordenador.setPessoa(new Pessoa());
			coordenador.getPessoa().setNome((String) obj[coluna++]);
			coordenador.setCargo(new CargoAcademico());
			coordenador.getCargo().setId((Integer) obj[coluna++]);
			if(coordenador.getCargo().getId() == CargoAcademico.SECRETARIA){
				coordenador.getCargo().setDescricao("Secretário(a)");
				coluna++;
			}else
				coordenador.getCargo().setDescricao((String) obj[coluna++]);
			coordenador.setAtivo((Boolean) obj[coluna++]);
			coordenador.setInstituicao(new InstituicoesEnsino());
			coordenador.getInstituicao().setId((Integer) obj[coluna++]);
			coordenador.getInstituicao().setNome((String) obj[coluna++]);
			coordenador.getInstituicao().setSigla((String) obj[coluna++]);
			coordenador.setCampus(new CampusIes((Integer) obj[coluna++]));
			coordenador.getCampus().setNome((String) obj[coluna++]);
			coordenador.setUsuario(new Usuario());
			coordenador.getUsuario().setLogin((obj[coluna] == null) ? "" : (String) obj[coluna]);
			resultado.add(coordenador);
		}
		
		
		return resultado;
	}
	
	/**
	 * Retorna coordenação ativa do docente
	 * @param programa
	 * @param idIes
	 * @return
	 * @throws DAOException 
	 */
	public CoordenadorUnidade findCoordenacaoAtivaByPessoa(Integer idPessoa) throws DAOException {
		Criteria coord = getCriteria(CoordenadorUnidade.class);
		coord.add(Restrictions.eq("pessoa.id", idPessoa));
		coord.add(Restrictions.eq("ativo", true));
		
		CoordenadorUnidade coordenacao = (CoordenadorUnidade)coord.uniqueResult();
		return coordenacao;
	}
	
	/**
	 * Retorna secretaria ativa cadastrada com o CPF do parametro.
	 * @param cpf
	 * @return
	 * @throws DAOException 
	 */
	public CoordenadorUnidade findSecretarioAtivoByCPF(Long cpf) throws DAOException {
		String hql = "select coordenador "+
				 " from CoordenadorUnidade coordenador " +
				 " join coordenador.pessoa pessoa " +
				 " where pessoa.cpf_cnpj = :cpfCnpj " +
				 " and coordenador.ativo = trueValue()" +
				 " and coordenador.cargo.id = "+ CargoAcademico.SECRETARIA ;
		
		Query query = getSession().createQuery(hql);
		query.setLong("cpfCnpj", cpf);
		
		return (CoordenadorUnidade) query.uniqueResult();
	}
	
	/**
	 * Retorna secretario ativo de acordo com os parametros
	 * @param programa
	 * @param Integer
	 * @param idCampus
	 * @return
	 * @throws DAOException 
	 */
	public CoordenadorUnidade findMembroCoordenacaoAtivoBycargo(ProgramaRede programa, Integer idIes,Integer idCampus,Integer idCargo) throws DAOException {
		String hql = "select coordenador "+
				 " from CoordenadorUnidade coordenador " +
				 " inner join coordenador.dadosCurso dc" +
				 " inner join dc.campus cp "+
				 " join coordenador.pessoa pessoa " +
				 " where  coordenador.ativo = trueValue()" +
				 " and dc.programaRede.id = :programa " +
				 " and cp.instituicao.id = :idIes " +
				 " and cp.id = :idCampus" +
				 " and coordenador.cargo.id = "+ idCargo ;
		
		Query query = getSession().createQuery(hql);
		query.setParameter("programa", programa.getId());
		query.setParameter("idIes", idIes);
		query.setParameter("idCampus", idCampus);
		
		return (CoordenadorUnidade) query.uniqueResult();
	}
	
}
