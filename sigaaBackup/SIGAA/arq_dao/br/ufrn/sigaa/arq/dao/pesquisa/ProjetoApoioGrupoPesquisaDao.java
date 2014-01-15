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
import br.ufrn.sigaa.pesquisa.dominio.MembroGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.ProjetoApoioGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoMembroGrupoPesquisa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * DAO responsável pelas consultas aos Projetos de Apoio aos Grupos de Pesquisa
 *
 * @author Jean Guerethes
 */
public class ProjetoApoioGrupoPesquisaDao extends GenericSigaaDAO {

	/**
	 * Método responsável por retornar todos os projetos de Apoio a grupos de Pesquisa, que o usuário participa.
	 * 
	 * @param idPessoa
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoApoioGrupoPesquisa> projetosUsuarioParticipa( int idPessoa ) throws DAOException {
		
		Collection<ProjetoApoioGrupoPesquisa> result = new ArrayList<ProjetoApoioGrupoPesquisa>();
		String sb = " SELECT pa.id_projeto_apoio_grupo_pesquisa, p.titulo, gp.codigo, p.id_tipo_situacao_projeto, tsp.descricao, mgp.classificacao" +
					" FROM pesquisa.membro_grupo_pesquisa mgp" +
					" JOIN pesquisa.projeto_apoio_grupo_pesquisa pa using ( id_grupo_pesquisa )" +
					" JOIN projetos.projeto p using ( id_projeto )" +
					" JOIN pesquisa.grupo_pesquisa gp on ( pa.id_grupo_pesquisa = gp.id_grupo_pesquisa )" +
					" JOIN projetos.tipo_situacao_projeto tsp on  ( tsp.id_tipo_situacao_projeto = p.id_tipo_situacao_projeto )"+
					" WHERE mgp.id_pessoa = :idPessoa " +
					" AND p.id_tipo_situacao_projeto <> " + TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO;
		
		Query q = getSession().createSQLQuery(sb);
		q.setInteger("idPessoa", idPessoa);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		for (int i = 0; i < lista.size(); i++){
			int col = 0;
			Object[] colunas = lista.get(i);
			ProjetoApoioGrupoPesquisa  projApoio = new ProjetoApoioGrupoPesquisa();
			projApoio.setId((Integer) colunas[col]);
			projApoio.getProjeto().setTitulo((String) colunas[++col]);
			projApoio.getGrupoPesquisa().setCodigo((String) colunas[++col]);
			projApoio.getProjeto().getSituacaoProjeto().setId((Integer) colunas[++col]);
			projApoio.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[++col]);
			projApoio.getGrupoPesquisa().setCoordenaGrupo(
					MembroGrupoPesquisa.COORDENADOR == (Integer) colunas[++col]
							&& projApoio.getProjeto().getSituacaoProjeto().getId() == 
							TipoSituacaoProjeto.PROJETO_BASE_CADASTRO_EM_ANDAMENTO);
			result.add(projApoio);
		}
		
		return result;
	}	
	
	/**
	 * Carrega as informaçãoes utilizadas na visualização dos projetos de Apoio aos Grupos de Pesquisa.
	 * 
	 * @param idProjetoApoio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ProjetoApoioGrupoPesquisa carregarProjetoApoio( int idProjetoApoio ) throws HibernateException, DAOException {
		try {
			String hql = "select projApoio.id, projApoio.projeto.titulo, projApoio.editalPesquisa.edital.descricao, " +
					    " projApoio.grupoPesquisa.id, projApoio.grupoPesquisa.nome, " +
					    " projApoio.projeto.justificativa, projApoio.integracao, " +
						" projApoio.projeto.id" +
						" FROM ProjetoApoioGrupoPesquisa projApoio " +
						" WHERE projApoio.id = :idProjeto";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idProjeto", idProjetoApoio);
			query.setMaxResults(1);
			Object[] result = (Object[]) query.uniqueResult();
			if(result != null){
				ProjetoApoioGrupoPesquisa projApoio = new ProjetoApoioGrupoPesquisa();
				projApoio.setId((Integer) result[0]);
				projApoio.getProjeto().setTitulo((String) result[1]);
				projApoio.getEditalPesquisa().getEdital().setDescricao((String) result[2]);
				projApoio.getGrupoPesquisa().setId((Integer) result[3]);
				projApoio.getGrupoPesquisa().setNome((String) result[4]);
				projApoio.getProjeto().setJustificativa((String) result[5]);
				projApoio.setIntegracao((String) result[6]);
				projApoio.getProjeto().setId((Integer) result[7]);
				projApoio.getProjeto().setApoioGrupoPesquisa(true);
				return projApoio;
			}
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
		return null;
	}

	/**
	 * Carrega as principais informações do Projeto de Apoio aos Grupos de Pesquisa. 
	 * 
	 * @param idProjetoApoio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public ProjetoApoioGrupoPesquisa findLeve( int idProjetoApoio ) throws HibernateException, DAOException {
			String hql = "SELECT projApoio.id, projApoio.editalPesquisa.id, projApoio.projeto.id, " +
						 " projApoio.grupoPesquisa.id, projApoio.integracao" +
						 " FROM ProjetoApoioGrupoPesquisa projApoio " +
						 " WHERE projApoio.id = :idProjeto";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idProjeto", idProjetoApoio);
			query.setMaxResults(1);
			Object[] result = (Object[]) query.uniqueResult();
			if(result != null){
				ProjetoApoioGrupoPesquisa projApoio = new ProjetoApoioGrupoPesquisa();
				projApoio.setId((Integer) result[0]);
				projApoio.getEditalPesquisa().setId((Integer) result[1]);
				projApoio.getProjeto().setId((Integer) result[2]);
				projApoio.getGrupoPesquisa().setId((Integer) result[3]);
				projApoio.setIntegracao((String) result[4]);
				return projApoio;
			}
		return null;
	}

	/**
	 * Retorna os id dos projetos cadastrados para o edital e grupo de pesquisa informados.
	 * 
	 * @param idEditalPesquisa
	 * @param idGrupoPesquisa
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Integer> haProjetoCadastrado( int idEditalPesquisa, int idGrupoPesquisa ) throws HibernateException, DAOException {
			String hql = "SELECT projApoio.id FROM ProjetoApoioGrupoPesquisa projApoio " +
						 " WHERE projApoio.editalPesquisa.id = :idEditalPesquisa" +
						 " AND projApoio.grupoPesquisa.id = :idGrupoPesquisa " +
						 " AND projApoio.projeto.situacaoProjeto.id <> :idSituacaoProjeto";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idEditalPesquisa", idEditalPesquisa);
			query.setInteger("idGrupoPesquisa", idGrupoPesquisa);
			query.setInteger("idSituacaoProjeto", TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO);
			return query.list();  
	}

	/**
	 * Responsável por retornar os Projetos de Apoio para os editais selecionados
	 * @param editais
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Servidor> findDocentesProjetoApoio( List<Integer> editais ) throws HibernateException, DAOException {
			Collection<Servidor>  servidores = new ArrayList<Servidor>();
			
			String hql = " SELECT distinct mgp.id_servidor" +
					     " FROM pesquisa.projeto_apoio_grupo_pesquisa pagp" +
					     " JOIN pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa )" +
					     " JOIN pesquisa.membro_grupo_pesquisa mgp using ( id_grupo_pesquisa )" +
					     " JOIN projetos.projeto p on ( pagp.id_projeto = p.id_projeto )" +  
					     " WHERE mgp.id_tipo_membro_grupo_pesquisa = " + TipoMembroGrupoPesquisa.PERMANENTE +
					     " AND pagp.id_edital_pesquisa in " + UFRNUtils.gerarStringIn(editais.toArray()) +
					     " AND p.id_tipo_situacao_projeto <> " + TipoSituacaoProjeto.PROJETO_BASE_REMOVIDO +
					     " AND mgp.id_servidor is not null";

			Query q = getSession().createSQLQuery(hql);
			List<Integer> lista = q.list();
			for (int i = 0; i < lista.size(); i++){
				Servidor serv = new Servidor(lista.get(i));
				servidores.add(serv);
			}
			return servidores;
	}

	/**
	 * Retorna os projetos de apoio a grupos de pesquisa que estão pendentes de avaliação.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public Collection<ProjetoApoioGrupoPesquisa> projetosParaAvaliar() throws DAOException {
		
		Collection<ProjetoApoioGrupoPesquisa> result = new ArrayList<ProjetoApoioGrupoPesquisa>();
		String sb = " SELECT pa.id_projeto_apoio_grupo_pesquisa, p.titulo, gp.codigo, p.id_tipo_situacao_projeto, tsp.descricao, u.sigla" +
					" FROM pesquisa.projeto_apoio_grupo_pesquisa pa " +
					" JOIN projetos.projeto p using ( id_projeto )" +
					" JOIN comum.unidade u using ( id_unidade )" +
					" JOIN pesquisa.grupo_pesquisa gp on ( pa.id_grupo_pesquisa = gp.id_grupo_pesquisa )" +
					" JOIN projetos.tipo_situacao_projeto tsp on  ( tsp.id_tipo_situacao_projeto = p.id_tipo_situacao_projeto )"+
					" WHERE p.id_tipo_situacao_projeto in " + UFRNUtils.gerarStringIn(new int[]{TipoSituacaoProjeto.PROJETO_BASE_AGUARDANDO_AVALIACAO, TipoSituacaoProjeto.PROJETO_BASE_AVALIADO}) +
					" ORDER BY u.id_gestora";
		
		Query q = getSession().createSQLQuery(sb);
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		for (int i = 0; i < lista.size(); i++){
			int col = 0;
			Object[] colunas = lista.get(i);
			ProjetoApoioGrupoPesquisa  projApoio = new ProjetoApoioGrupoPesquisa();
			projApoio.setId((Integer) colunas[col]);
			projApoio.getProjeto().setTitulo((String) colunas[++col]);
			projApoio.getGrupoPesquisa().setCodigo((String) colunas[++col]);
			projApoio.getProjeto().getSituacaoProjeto().setId((Integer) colunas[++col]);
			projApoio.getProjeto().getSituacaoProjeto().setDescricao((String) colunas[++col]);
			projApoio.getProjeto().setUnidade(new Unidade());
			projApoio.getProjeto().getUnidade().setSigla((String) colunas[++col]);
			
			result.add(projApoio);
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
				.setString(1, "%GRUPOS DE PESQUISA%").setMaxResults(1)
				.uniqueResult();
	}
}