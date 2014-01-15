package br.ufrn.sigaa.arq.dao.projetos;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.FuncaoMembro;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO responsável restritamente para consultas relacionadas a projetos associados. 
 * 
 * @author geyson
 *
 */
public class ProjetoAssociadoDao extends GenericSigaaDAO {
	
	/**
	 * Retorna projetos que tem pessoa como membro.
	 * 
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Projeto> findByPessoa(Pessoa pessoa) throws DAOException {

		try {

			String hql = null;
			hql = "SELECT distinct projeto.id, projeto.ano, projeto.titulo, s.descricao, s.id, edital  "
					+ "FROM Projeto projeto "
					+ "INNER JOIN projeto.equipe m "
					+ "INNER JOIN projeto.situacaoProjeto s "
					+ "LEFT JOIN projeto.edital edital "
					+ "WHERE m.pessoa.id = :idPessoa "
					+ "AND m.ativo = trueValue() "
					+ "AND (projeto.tipoProjeto.id = :idTipoProjeto) " 
					+ "AND projeto.ativo = trueValue() "
					+ "ORDER BY projeto.ano desc";

			Query query = getSession().createQuery(hql);
			query.setInteger("idPessoa", pessoa.getId());
			query.setInteger("idTipoProjeto", TipoProjeto.ASSOCIADO);

			@SuppressWarnings("unchecked")
			List<Object[]> lista = query.list();

			ArrayList<Projeto> result = new ArrayList<Projeto>();
			for (int a = 0; a < lista.size(); a++) {

				int col = 0;
				Object[] colunas = lista.get(a);

				Projeto projeto = new Projeto();
				projeto.setId((Integer) colunas[col++]);
				projeto.setAno((Integer) colunas[col++]);
				projeto.setTitulo((String) colunas[col++]);

				TipoSituacaoProjeto sit = new TipoSituacaoProjeto();
				String desc = (String) colunas[col++];
				if (desc != null) {
				    sit.setDescricao(desc);
				    sit.setId((Integer) colunas[col++]);
				}
				projeto.setSituacaoProjeto(sit);
			    projeto.setEdital((Edital) colunas[col++]);
				result.add(projeto);

			}

			return result;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
	
	/**
	 * Método utilizado para buscar o coordenador do projeto associado
	 * 
	 * @param coordenador
	 * @return
	 * @throws DAOException
	 */
	public Collection<MembroProjeto> findByCoordenadorAssociados(Pessoa coordenador) throws DAOException {

		try {

			String hql = "select " +
					"membro.id, membro.chDedicada, membro.dataInicio, membro.dataFim, membro.categoriaMembro, membro.funcaoMembro, " +
					"p.id, p.ano, p.titulo, p.dataInicio, p.dataFim , p.ativo, p.situacaoProjeto.id, " +
					"membro.pessoa.id, membro.pessoa.nome " +
					"from Projeto p " +
					"inner join p.equipe membro " +
					"where p.id in (" +
						"select pj.id " +
						"from Projeto pj " +
						"inner join pj.coordenador coord " +
							"where coord.pessoa.id = :idPessoa and coord.ativo = trueValue() " +
							"and pj.situacaoProjeto.id not in (:PROJETO_BASE_GRUPO_INVALIDO) and " +
							"(pj.situacaoProjeto.id in (:PROJETOS_EM_EXECUCAO)) and pj.ativo = trueValue() order by pj.id)" +
					" and membro.ativo = trueValue() order by p.ano desc, p.id ";
			Query query = getSession().createQuery(hql);

			query.setInteger( "idPessoa", coordenador.getId() );
			query.setParameterList("PROJETO_BASE_GRUPO_INVALIDO", TipoSituacaoProjeto.PROJETO_BASE_GRUPO_INVALIDO);		
			query.setParameterList("PROJETOS_EM_EXECUCAO", TipoSituacaoProjeto.SITUACOES_VALIDAS_ASSOCIADOS);
			@SuppressWarnings("rawtypes")
			List lista = query.list();

	        	Collection<MembroProjeto> equipe = new ArrayList<MembroProjeto>();
	        	for (int a = 0; a < lista.size(); a++) {
					int col = 0;
					Object[] colunas = (Object[]) lista.get(a);
					MembroProjeto mp = new MembroProjeto();
					mp.setId((Integer) colunas[col++]);
					mp.setChDedicada((Integer) colunas[col++]);
					mp.setDataInicio((Date) colunas[col++]);
					mp.setDataFim((Date) colunas[col++]);
					mp.setCategoriaMembro((CategoriaMembro) colunas[col++]);
					mp.setFuncaoMembro((FuncaoMembro) colunas[col++]);					
					Projeto projeto = new Projeto();
					projeto.setId((Integer) colunas[col++]);
					projeto.setAno((Integer) colunas[col++]);
					projeto.setTitulo((String) colunas[col++]);
					projeto.setDataInicio( (Date)colunas[col++] );
					projeto.setDataFim( (Date)colunas[col++] );
					projeto.setAtivo((Boolean)colunas[col++]);
					projeto.setSituacaoProjeto( new TipoSituacaoProjeto( (Integer)colunas[col++] ) );
					mp.setProjeto(projeto);						
					Pessoa p = new Pessoa();
					p.setId((Integer) colunas[col++]);
					p.setNome((String) colunas[col++]);					
					mp.setPessoa(p);
					if(mp.getFuncaoMembro().getId() == FuncaoMembro.COORDENADOR){
						projeto.setCoordenador(mp);
					}

					equipe.add(mp);
	        	}

	            return equipe;

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	/** Retorna todos os projeto Integrados do servidor no período informado. */
	@SuppressWarnings("unchecked")
	public Collection<Projeto> findByProjetoAssociadosServidor(Servidor servidor, 
			Integer anoInicio, Integer anoFim, Integer periodoInicio, Integer periodoFim) throws DAOException {

		try {
			String hql = "SELECT distinct proj " +
					     " FROM Projeto proj" +
				         " INNER JOIN proj.equipe m " +
				         " WHERE m.pessoa.id = :idPessoa " +
				         " and proj.tipoProjeto.id = " + TipoProjeto.ASSOCIADO +
				         " and proj.ativo = trueValue() " +
				         " and proj.situacaoProjeto.id = " + TipoSituacaoProjeto.PROJETO_BASE_CONCLUIDO +  
				         " and " + HibernateUtils.generateDateIntersection("proj.dataInicio", "proj.dataFim", ":inicial" , ":final");
						
			Query query = getSession().createQuery(hql);
			query.setInteger("idPessoa", servidor.getPessoa().getId());
			query.setDate("inicial", CalendarUtils.createDate(01,  periodoInicio == 1 ? 0 : 5 , anoInicio) );
			query.setDate("final", CalendarUtils.createDate(01,  periodoInicio == 1 ? 0 : 5 , anoFim) );
			return query.list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}

	}
	
}
