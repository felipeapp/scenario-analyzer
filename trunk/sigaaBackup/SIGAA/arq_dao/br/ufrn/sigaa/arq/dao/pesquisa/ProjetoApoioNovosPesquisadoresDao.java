package br.ufrn.sigaa.arq.dao.pesquisa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioNovosPesquisadores;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO responsável pelas consultas aos Projetos de Apoio aos Novos Pesquisadores
 *
 * @author Jean Guerethes
 */
public class ProjetoApoioNovosPesquisadoresDao extends GenericSigaaDAO {

	public Collection<ProjetoApoioNovosPesquisadores> projetosCadastradosLeve( int idServidor ) throws DAOException {
		
		Collection<ProjetoApoioNovosPesquisadores> result = new ArrayList<ProjetoApoioNovosPesquisadores>();
		String sql = " SELECT panp.id_projeto_apoio_novos_pesquisadores, p.titulo, tsp.id_tipo_situacao_projeto, " +
					 " tsp.descricao, gp.codigo" +
					 " FROM pesquisa.projeto_apoio_novos_pesquisadores panp" +
					 " JOIN projetos.projeto p using ( id_projeto )" +
					 " JOIN projetos.tipo_situacao_projeto tsp using ( id_tipo_situacao_projeto )" +
					 " LEFT JOIN pesquisa.grupo_pesquisa gp on ( gp.id_grupo_pesquisa = panp.id_grupo_pesquisa  )" +
					 " where panp.id_coordenador = :idServidor " +
					 " AND p.id_tipo_situacao_projeto <> " + TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO + 
					 " ORDER BY p.id_tipo_situacao_projeto, p.data_cadastro desc";
		
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idServidor", idServidor);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if(result != null){
			for (int i = 0; i < lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);
				ProjetoApoioNovosPesquisadores  projApoio = new ProjetoApoioNovosPesquisadores();
				projApoio.setId((Integer) colunas[col]);
				projApoio.getProjeto().setTitulo((String) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setId((Integer) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[++col]);
				projApoio.getGrupoPesquisa().setCodigo((String) colunas[++col]);
				projApoio.setConcluido( projApoio.getProjeto().getSituacaoProjeto().getId() != 
						TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO );
				result.add(projApoio);
			}
		}
		return result;
	}	
	
	public ProjetoApoioNovosPesquisadores carregarProjetoApoio( int idProjetoApoio ) throws HibernateException, DAOException {
		try {
			String hql = " SELECT panp.id, panp.grupoPesquisa.nome, panp.projeto.id, " +
						 " panp.projeto.titulo, panp.projeto.metodologia, panp.projeto.objetivos," +
					 	 " panp.projeto.resultados, panp.projeto.referencias, panp.integracao, panp.editalPesquisa.id," +
					 	 " panp.coordenador.id" +
						 " FROM ProjetoApoioNovosPesquisadores panp" +
						 " LEFT JOIN panp.grupoPesquisa gp" +
						 " LEFT JOIN panp.projeto p" +
						 " WHERE panp.id = :idProjeto";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idProjeto", idProjetoApoio);
			query.setMaxResults(1);
			Object[] result = (Object[]) query.uniqueResult();
			if(result != null){
				ProjetoApoioNovosPesquisadores projApoio = new ProjetoApoioNovosPesquisadores();
				int col = 0;
				projApoio.setId((Integer) result[col]);
				projApoio.getGrupoPesquisa().setNome((String) result[++col]);
				projApoio.getProjeto().setId((Integer) result[++col]);
				projApoio.getProjeto().setTitulo((String) result[++col]);
				projApoio.getProjeto().setMetodologia((String) result[++col]);
				projApoio.getProjeto().setObjetivos((String) result[++col]);
				projApoio.getProjeto().setResultados((String) result[++col]);
				projApoio.getProjeto().setReferencias((String) result[++col]);
				projApoio.setIntegracao((String) result[++col]);
				projApoio.getEditalPesquisa().setId((Integer) result[++col]);
				projApoio.getCoordenador().setId((Integer) result[++col]);
				projApoio.getProjeto().setApoioNovosPesquisadores(true);
				return projApoio;
			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Integer> haProjetoCadastrado( int idEditalPesquisa, int idCoordenador ) throws HibernateException, DAOException {
		String hql = " SELECT proj.id FROM ProjetoApoioNovosPesquisadores proj " +
					 " WHERE proj.editalPesquisa.id = :idEditalPesquisa" +
					 " AND proj.coordenador.id = :idCoordenador" +
					 " AND proj.projeto.situacaoProjeto.id <> :idSituacaoProjeto";
		
		Query query = getSession().createQuery(hql);
		query.setInteger("idEditalPesquisa", idEditalPesquisa);
		query.setInteger("idCoordenador", idCoordenador);
		query.setInteger("idSituacaoProjeto", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
		return query.list();
	}
	
	@SuppressWarnings("unchecked")
	public Collection<Servidor> findDocentesProjetoApoio( List<Integer> editais ) throws HibernateException, DAOException {
		String hql = " SELECT proj.coordenador FROM ProjetoApoioNovosPesquisadores proj " +
					 " WHERE proj.editalPesquisa.id in " + UFRNUtils.gerarStringIn(editais.toArray()) +
					 " AND proj.projeto.situacaoProjeto.id in " + UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO, TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, TipoSituacaoProjeto.PROJETO_BASE_AVALIADO});
		
		Query query = getSession().createQuery(hql);
		return query.list();
	}

	public Collection<ProjetoApoioNovosPesquisadores> filter(Integer ano, String titulo, Integer idCoordenador, Integer status) throws DAOException {
		Collection<ProjetoApoioNovosPesquisadores> result = new ArrayList<ProjetoApoioNovosPesquisadores>();
		StringBuilder sql = new StringBuilder(" SELECT panp.id_projeto_apoio_novos_pesquisadores, p.titulo, tsp.id_tipo_situacao_projeto, " +
					 " tsp.descricao, gp.codigo, pe.nome" +
					 " FROM pesquisa.projeto_apoio_novos_pesquisadores panp" +
					 " JOIN projetos.projeto p using ( id_projeto )" +
					 " JOIN projetos.tipo_situacao_projeto tsp using ( id_tipo_situacao_projeto )" +
					 " JOIN rh.servidor s on s.id_servidor = panp.id_coordenador" +
					 " JOIN comum.pessoa pe on pe.id_pessoa = s.id_pessoa" +
					 " LEFT JOIN pesquisa.grupo_pesquisa gp on ( gp.id_grupo_pesquisa = panp.id_grupo_pesquisa  )" +
					 " WHERE p.id_tipo_situacao_projeto <> " + TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
		if(ano != null)
			sql.append(" AND p.ano = :ano ");
		if(titulo != null)
			sql.append(" AND p.titulo ilike :titulo ");
		if(idCoordenador != null)
			sql.append(" AND panp.id_coordenador = :idServidor ");
		if(status != null)
			sql.append(" AND p.id_tipo_situacao_projeto = :status");
		sql.append(" ORDER BY p.id_tipo_situacao_projeto, p.data_cadastro desc");
		
		Query q = getSession().createSQLQuery(sql.toString());
		if(ano != null) q.setInteger("ano", ano);
		if(titulo != null) q.setString("titulo", "%"+titulo+"%");
		if(idCoordenador != null) q.setInteger("idServidor", idCoordenador);
		if(status != null) q.setInteger("status", status);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if(result != null){
			for (int i = 0; i < lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);
				ProjetoApoioNovosPesquisadores  projApoio = new ProjetoApoioNovosPesquisadores();
				projApoio.setId((Integer) colunas[col]);
				projApoio.getProjeto().setTitulo((String) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setId((Integer) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[++col]);
				projApoio.getGrupoPesquisa().setCodigo((String) colunas[++col]);
				projApoio.setConcluido( projApoio.getProjeto().getSituacaoProjeto().getId()== 
						TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO );
				projApoio.setCoordenador(new Servidor());
				projApoio.getCoordenador().getPessoa().setNome((String) colunas[++col]);
				result.add(projApoio);
			}
		}
		return result;
	}
	
	/**
	 * Retorna os projetos de apoio a grupos de pesquisa que estão pendentes de avaliação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoApoioNovosPesquisadores> projetosParaAvaliar(EditalPesquisa edital) throws DAOException {
		 
		Collection<ProjetoApoioNovosPesquisadores> result = new ArrayList<ProjetoApoioNovosPesquisadores>();
		StringBuilder sql = new StringBuilder(" SELECT panp.id_projeto_apoio_novos_pesquisadores, p.titulo, tsp.id_tipo_situacao_projeto, " +
					 " tsp.descricao, gp.codigo, pe.nome, u.sigla" +
					 " FROM pesquisa.projeto_apoio_novos_pesquisadores panp" +
					 " JOIN projetos.projeto p using ( id_projeto )" +
					 " JOIN comum.unidade u using ( id_unidade )" +
					 " JOIN projetos.tipo_situacao_projeto tsp using ( id_tipo_situacao_projeto )" +
					 " JOIN rh.servidor s on s.id_servidor = panp.id_coordenador" +
					 " JOIN comum.pessoa pe on pe.id_pessoa = s.id_pessoa" +
					 " LEFT JOIN pesquisa.grupo_pesquisa gp on ( gp.id_grupo_pesquisa = panp.id_grupo_pesquisa  )" +
					 " WHERE p.id_tipo_situacao_projeto in " + UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, TipoSituacaoProjeto.PROJETO_BASE_AVALIADO}));
		
		if(edital != null)
			sql.append(" AND panp.id_edital_pesquisa = " + edital.getId());
		sql.append(" ORDER BY u.id_gestora");
		Query q = getSession().createSQLQuery(sql.toString());
				
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if(result != null){
			for (int i = 0; i < lista.size(); i++){
				int col = 0;
				Object[] colunas = lista.get(i);
				ProjetoApoioNovosPesquisadores  projApoio = new ProjetoApoioNovosPesquisadores();
				projApoio.setId((Integer) colunas[col]);
				projApoio.getProjeto().setTitulo((String) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setId((Integer) colunas[++col]);
				projApoio.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[++col]);
				projApoio.getGrupoPesquisa().setCodigo((String) colunas[++col]);
				projApoio.setConcluido( projApoio.getProjeto().getSituacaoProjeto().getId()== 
						TipoSituacaoProjeto.PROJETO_BASE_SUBMETIDO );
				projApoio.setCoordenador(new Servidor());
				projApoio.getCoordenador().getPessoa().setNome((String) colunas[++col]);
				projApoio.getProjeto().setUnidade(new Unidade());
				projApoio.getProjeto().getUnidade().setSigla((String) colunas[++col]);
				result.add(projApoio);
			}
		}
		return result;
	}
	
	public DistribuicaoAvaliacao distribuicaoMaisRecente() throws HibernateException, DAOException{
		return (DistribuicaoAvaliacao) getSession()
				.createQuery(
						"from DistribuicaoAvaliacao d "
								+ " where d.modeloAvaliacao.edital.ano = ? "
								+ " and d.modeloAvaliacao.edital.descricao like ? ")
				.setInteger(0, CalendarUtils.getAnoAtual())
				.setString(1, "%NOVOS PESQUISADORES%").setMaxResults(1)
				.uniqueResult();
	}
}