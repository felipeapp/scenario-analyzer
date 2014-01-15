/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '07/10/2010'
 *
 */
package br.ufrn.sigaa.arq.dao.projetos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Query;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.AreaConhecimentoCnpq;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.Avaliacao;
import br.ufrn.sigaa.projetos.dominio.DistribuicaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Edital;
import br.ufrn.sigaa.projetos.dominio.ModeloAvaliacao;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoAvaliador;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoAvaliacao;
import br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto;

/**
 * Dao para acesso aos dados de avaliações de projetos.
 *  
 * @author ilueny santos.
 *
 */
public class AvaliacaoDao extends GenericSigaaDAO {

	
    /**
     * Retorna a avaliação ativa do projeto que foi distribuída para o avaliador. 
     * 
     * @param avaliador
     * @param projeto
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
	public List<Avaliacao> findByProjeto(Projeto projeto) throws DAOException{
	try {
	    String hql = "from br.ufrn.sigaa.projetos.dominio.Avaliacao aval " +
	    		"where aval.ativo = trueValue() " +
	    		"and aval.projeto.id = :idProjeto ";	    		
	    Query query = getSession().createQuery(hql);
	    query.setInteger("idProjeto", projeto.getId());
	    return query.list();
	} catch (Exception e) {
	    throw new DAOException(e);
	}
    }
	
    /**
     * Retorna a avaliação ativa do projeto que foi distribuída para o avaliador. 
     * 
     * @param avaliador
     * @param projeto
     * @return
     * @throws DAOException
     */
    public Avaliacao findByAvaliadorProjeto(Usuario avaliador, Projeto projeto) throws DAOException{
	try {
	    String hql = "from br.ufrn.sigaa.projetos.dominio.Avaliacao aval " +
	    		"where aval.ativo = trueValue() " +
	    		"and aval.avaliador.id = :idUsuario " +
	    		"and aval.projeto.id = :idProjeto ";
	    Query query = getSession().createQuery(hql);
	    query.setInteger("idUsuario", avaliador.getId());
	    query.setInteger("idProjeto", projeto.getId());
	    query.setMaxResults(1);
	    return (Avaliacao) query.uniqueResult();
	} catch (Exception e) {
	    throw new DAOException(e);
	}
    }
    
    
    /**
     * Retorna a lista de avaliações distribuídas para o avaliador informado.
     * 
     * @param avaliador
     * @return
     * @throws DAOException
     */
    @SuppressWarnings("unchecked")
    public List<Avaliacao> findByAvaliador(Usuario avaliador, boolean somenteRealizadas) throws DAOException {
		String sql = "select ava.id_avaliacao, p.nome, unid.nome as nomeunid, ava.data_avaliacao, proj.titulo, " +
					 " proj.id_projeto, proj.id_tipo_situacao_projeto, proj.ano, cnpq.nome as area, " +
					 " ava.id_tipo_situacao_avaliacao, tsa.descricao, ava.nota, ta.descricao as avaliador, " +
					 " tava.descricao as tipoAva, ava.parecer" +
					 " from projetos.avaliacao ava" +
					 " join comum.usuario u on ( ava.id_usuario_avaliador = u.id_usuario )" +
					 " join comum.pessoa p using ( id_pessoa )" +
					 " join comum.unidade unid using ( id_unidade )" +
					 " join projetos.projeto proj using ( id_projeto )" +
					 " join comum.area_conhecimento_cnpq cnpq using ( id_area_conhecimento_cnpq )" +
					 " join projetos.tipo_situacao_avaliacao tsa on ( tsa.id_tipo_situacao_avaliacao = ava.id_tipo_situacao_avaliacao )" +
					 " join projetos.distribuicao_avaliacao da on ( ava.id_distribuicao_avaliacao = da.id_distribuicao_avaliacao ) " +
					 " join projetos.tipo_avaliador ta on ( da.id_tipo_avaliador = ta.id_tipo_avaliador )" +
					 " join projetos.modelo_avaliacao ma on ( da.id_modelo_avaliacao = ma.id_modelo_avaliacao )" +
					 " join projetos.tipo_avaliacao tava on ( ma.id_tipo_avaliacao = tava.id_tipo_avaliacao )" +
					 " where ava.ativo = trueValue() and ava.id_usuario_avaliador = ?";
				if ( somenteRealizadas )
					sql += " and ava.id_tipo_situacao_avaliacao = " + TipoSituacaoAvaliacao.REALIZADA; 

				sql += " order by ava.id_tipo_situacao_avaliacao, proj.ano desc";
				
		List<Avaliacao> lista = getJdbcTemplate().query(sql.toString(), new Object[] { avaliador.getId() }, new RowMapper() {

			public Object mapRow(ResultSet rs, int row) throws SQLException {
				Avaliacao ava = new Avaliacao();
				ava.setId( rs.getInt("id_avaliacao") );
				ava.setAvaliador(new Usuario());
				ava.getAvaliador().setPessoa(new Pessoa());
				ava.getAvaliador().getPessoa().setNome(rs.getString("nome"));
				ava.getAvaliador().setUnidade(new Unidade());
				ava.getAvaliador().getUnidade().setNome(rs.getString("nomeunid"));
				ava.setDataAvaliacao(rs.getDate("data_avaliacao"));
				ava.setProjeto(new Projeto());
				ava.getProjeto().setTitulo( rs.getString("titulo") );
				ava.getProjeto().setId( rs.getInt("id_projeto") );
				ava.getProjeto().setSituacaoProjeto(new TipoSituacaoProjeto());
				ava.getProjeto().getSituacaoProjeto().setId( rs.getInt("id_tipo_situacao_projeto") );
				ava.getProjeto().setAno( rs.getInt("ano") );
				ava.getProjeto().setAreaConhecimentoCnpq( new AreaConhecimentoCnpq() );
				ava.getProjeto().getAreaConhecimentoCnpq().setNome( rs.getString("area") );
				ava.setSituacao(new TipoSituacaoAvaliacao());
				ava.getSituacao().setId( rs.getInt("id_tipo_situacao_avaliacao") );
				ava.setDistribuicao(new DistribuicaoAvaliacao());
				ava.getDistribuicao().setModeloAvaliacao(new ModeloAvaliacao());
				ava.getDistribuicao().getModeloAvaliacao().setTipoAvaliacao(new TipoAvaliacao());
				ava.getDistribuicao().getModeloAvaliacao().getTipoAvaliacao().setDescricao( rs.getString("tipoava") );
				ava.setNota( rs.getDouble("nota") );
				ava.getSituacao().setDescricao( rs.getString("descricao") );
				ava.getDistribuicao().setTipoAvaliador(new TipoAvaliador());
				ava.getDistribuicao().getTipoAvaliador().setDescricao( rs.getString("avaliador") );
				ava.setParecer( rs.getString("parecer") );
				return ava;
			}

		});
		
		return lista;
    }  

    /**
     * Retorna a lista de avaliações de acordo com os parâmetros passados.
     * 
     * @param avaliador
     * @param situacao
     * @param dataAvaliacao
     * @param titulo
     * @param ano
     * @return
     * @throws DAOException
     */
    public List<Avaliacao> filter(Usuario avaliador, TipoSituacaoAvaliacao situacao, String titulo, Integer ano, Unidade unidadeProponente) throws DAOException {
    	try {
   	    		StringBuffer hql = new StringBuffer(
    	    				"select aval.id, aval.dataAvaliacao, aval.nota, " +
    	    				"distribuicao.id, distribuicao.tipoAvaliador, " +
    	    				"modelo.id, modelo.tipoAvaliacao.id, modelo.tipoAvaliacao.descricao, " +
    	    				"situacao.id, situacao.descricao, avaliador.id, pessoa.id, pessoa.nome, " +
    	    				"projeto.id, projeto.ano, projeto.titulo, unidade.id, unidade.nome, unidade.sigla " +
    	    				"from br.ufrn.sigaa.projetos.dominio.Avaliacao aval " +
    	    				"join aval.projeto projeto " +
    	    				"join projeto.unidade unidade " +    				
    	    				"join aval.situacao situacao " +
    	    				"join aval.avaliador avaliador " +
    	    				"join aval.distribuicao distribuicao " +
    	    				"join distribuicao.modeloAvaliacao modelo " +
    	    				"join avaliador.pessoa pessoa " +
    	    		"where aval.ativo = trueValue() and projeto.ativo = trueValue() ");

    		if (avaliador != null) {
    			hql.append(" and avaliador.id = :idAvaliador");
    		}
    		
    		if (situacao != null) {
    			hql.append(" and situacao.id = :idSituacao");
    		}
    		
    		if (titulo != null) {
    		    hql.append(" and "
    			    + UFRNUtils.toAsciiUpperUTF8("projeto.titulo") + " like "
    			    + UFRNUtils.toAsciiUTF8(":titulo"));
    		}
    		
    		if (ano != null) {    		
    			hql.append(" and projeto.ano = :ano");
    		}

    		if (unidadeProponente != null) {    		
    			hql.append(" and projeto.unidade.id = :idUnidade");
    		}

    		
    		hql.append(" order by projeto.id, situacao.id");    		
    		Query query = getSession().createQuery(hql.toString());
    		
    		if (avaliador != null) {
    			query.setInteger("idAvaliador", avaliador.getId());
    		}
    		
    		if (situacao != null) {
    			query.setInteger("idSituacao", situacao.getId());
    		}
    		
    		if (titulo != null) {
    			query.setString("titulo", "%" + titulo.toUpperCase() + "%");
    		}
    		
    		if (ano != null) {    		
    			query.setInteger("ano", ano);
    		}

    		if (unidadeProponente != null) {    		
    			query.setInteger("idUnidade", unidadeProponente.getId());
    		}
    		
    		@SuppressWarnings("unchecked")
    		List<Object> lista = query.list();
    		ArrayList<Avaliacao> result = new ArrayList<Avaliacao>();

    		for (int a = 0; a < lista.size(); a++) {

    		    int col = 0;
    		    Object[] colunas = (Object[]) lista.get(a);

    		    Avaliacao avaliacao = new Avaliacao();
    		    avaliacao.setId((Integer) colunas[col++]);
    		    avaliacao.setDataAvaliacao((Date) colunas[col++]);
    		    avaliacao.setNota((Double) colunas[col++]);
    		    DistribuicaoAvaliacao dis = new DistribuicaoAvaliacao();
    		    dis.setId((Integer) colunas[col++]);
    		    dis.setTipoAvaliador((TipoAvaliador) colunas[col++]);
    		    ModeloAvaliacao mod = new ModeloAvaliacao();
    		    mod.setId((Integer) colunas[col++]);
    		    TipoAvaliacao tipo = new TipoAvaliacao();
    		    tipo.setId((Integer) colunas[col++]);
    		    tipo.setDescricao((String) colunas[col++]);
    		    mod.setTipoAvaliacao(tipo);
    		    dis.setModeloAvaliacao(mod);
    		    avaliacao.setDistribuicao(dis);
    		    
    		    TipoSituacaoAvaliacao sit = new TipoSituacaoAvaliacao();
    		    sit.setId((Integer) colunas[col++]);
    		    sit.setDescricao((String) colunas[col++]);
    		    avaliacao.setSituacao(sit);
    		    
    		    Usuario av = new Usuario();
    		    av.setId((Integer) colunas[col++]);
    		    Pessoa p = new Pessoa ((Integer) colunas[col++]);
    		    p.setNome((String) colunas[col++]);
    		    av.setPessoa(p);
    		    avaliacao.setAvaliador(av);
    		    
    		    Projeto pj = new Projeto((Integer) colunas[col++]);
    			pj.setAno((Integer) colunas[col++]);
    			pj.setTitulo((String) colunas[col++]);
    			Unidade u = new Unidade((Integer) colunas[col++]);
    			u.setNome((String) colunas[col++]);
    			u.setSigla((String) colunas[col++]);
    			pj.setUnidade(u);
    			avaliacao.setProjeto(pj);
    			
    			result.add(avaliacao);
    		}
    		
    		return result;

    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    }  

    
    /**
     * Retorna todas as avaliações para a distribuição e situação selecionadas.
     * 
     * @param idDistribuicao
     * @param idSituacao
     * @return
     * @throws DAOException
     */
    public List<Avaliacao> findByDistribuicaoSituacao(Integer idDistribuicao, Integer idSituacao) throws DAOException {
    	try {
   	    		StringBuffer hql = new StringBuffer(
    	    				"select aval.id, aval.dataAvaliacao, aval.nota, distribuicao.id, " +
    	    				"situacao.id, situacao.descricao " +
    	    				"from br.ufrn.sigaa.projetos.dominio.Avaliacao aval " +
    	    				"join aval.situacao situacao " +
    	    				"join aval.distribuicao distribuicao " +
    	    		"where aval.ativo = trueValue() and distribuicao.ativo = trueValue() ");

    		if (idDistribuicao != null) {
    			hql.append(" and distribuicao.id = :idDistribuicao");
    		}
    		
    		if (idSituacao != null) {
    			hql.append(" and situacao.id = :idSituacao");
    		}
    		
    		Query query = getSession().createQuery(hql.toString());
    		
    		if (idDistribuicao != null) {
    			query.setInteger("idDistribuicao", idDistribuicao);
    		}
    		
    		if (idSituacao != null) {
    			query.setInteger("idSituacao", idSituacao);
    		}
    		
    		@SuppressWarnings("unchecked")
    		List<Object> lista = query.list();
    		ArrayList<Avaliacao> result = new ArrayList<Avaliacao>();

    		for (int a = 0; a < lista.size(); a++) {
    		    int col = 0;
    		    Object[] colunas = (Object[]) lista.get(a);

    		    Avaliacao avaliacao = new Avaliacao();
    		    avaliacao.setId((Integer) colunas[col++]);
    		    avaliacao.setDataAvaliacao((Date) colunas[col++]);
    		    avaliacao.setNota((Double) colunas[col++]);
    		    DistribuicaoAvaliacao dis = new DistribuicaoAvaliacao();
    		    dis.setId((Integer) colunas[col++]);
    		    avaliacao.setDistribuicao(dis);
    			
    			result.add(avaliacao);
    		}
    		
    		return result;

    	} catch (Exception e) {
    		throw new DAOException(e);
    	}
    }  
    
    /**
     * Retorna a lista de projetos com avaliação finalizada submetidos ao edital informado.
     * Utilizado na publicação do resultado das avaliações de projetos.
     * 
     * @param edital
     * @return
     * @throws DAOException 
     */
    public List<Map<String, Object>> findProjetosAvaliadosClassificados(Edital edital) throws DAOException {
    	
    	StringBuffer sql = new StringBuffer(" select ");
    	
    	//projeto
    	sql.append("pj.id_projeto, pj.titulo, pj.ano, pj.classificacao, pj.media, " +
    			"pj.total_discentes_envolvidos, pj.bolsas_solicitadas, " +
    			"pj.ensino, pj.pesquisa, pj.extensao, ");

    	//coordenador
    	sql.append("(select nome from comum.pessoa " +
    			" join rh.servidor as s using(id_pessoa) " +
    			" join projetos.membro_projeto mpc using(id_servidor) " +
    			" join projetos.projeto pc on pc.id_coordenador = mpc.id_membro_projeto" +
    			" where mpc.ativo = trueValue() and pc.id_projeto = pj.id_projeto) as coordenador, ");
    	
    	//unidade 
    	sql.append("u.sigla as sigla_unidade, ");
    	
    	//área do cnpq
    	sql.append("cnpq.nome as area_cnpq, ");
    	
    	//orçamento solicitado
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 29) as pessoa_juridica, ");
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 31) as pessoa_fisica, ");
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 33) as material_consumo, ");
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 34) as diarias, ");
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 35) as passagens, ");
    	sql.append("(select sum(quantidade * valor_unitario) from projetos.orcamento_detalhado where id_projeto = pj.id_projeto and id_elemento_despesa = 38) as equipamentos, ");

    	//total avaliações realizadas (ad hoc)
    	sql.append("(select count(id_avaliacao) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 1 " + //ad hoc
    			"and ma.id_tipo_avaliacao = 1) as total_avaliacoes_ad_hoc, ");

    	//maior nota ad hoc
    	sql.append("(select max(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 1 " +
    			"and ma.id_tipo_avaliacao = 1) as max_avaliacao_ad_hoc, ");

    	//menor nota ad hoc
    	sql.append("(select min(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 1 " +
    			"and ma.id_tipo_avaliacao = 1) as min_avaliacao_ad_hoc, ");

    	//média nota ad hoc
    	sql.append("(select avg(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 1 " +
    			"and ma.id_tipo_avaliacao = 1) as med_avaliacao_ad_hoc, ");

    	
    	//total avaliações realizadas (comitê interno)
    	sql.append("(select count(id_avaliacao) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 2 " + //comitê interno
    			"and ma.id_tipo_avaliacao = 1) as total_avaliacoes_comite, ");

    	//maior nota comitê interno
    	sql.append("(select max(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 2 " +
    			"and ma.id_tipo_avaliacao = 1) as max_avaliacao_comite, ");

    	//menor nota comitê interno
    	sql.append("(select min(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 2 " +
    			"and ma.id_tipo_avaliacao = 1) as min_avaliacao_comite, ");

    	//média nota  comitê interno
    	sql.append("(select avg(av.nota) from projetos.avaliacao av " +
    			"join projetos.distribuicao_avaliacao da using(id_distribuicao_avaliacao) " +
    			"join projetos.modelo_avaliacao ma using(id_modelo_avaliacao) " +
    			"where av.ativo = trueValue() " +
    			"and av.id_projeto = pj.id_projeto " +
    			"and av.id_tipo_situacao_avaliacao = 2 " +
    			"and da.id_tipo_avaliador = 2 " +
    			"and ma.id_tipo_avaliacao = 1) as med_avaliacao_comite ");
    	
    	
    	//from
    	sql.append(" from projetos.projeto pj " +
    			"left join comum.area_conhecimento_cnpq cnpq	using(id_area_conhecimento_cnpq) " +
    			"join comum.unidade u using(id_unidade) " +
    			"where id_edital = ? " +
    			"and pj.ativo = trueValue() " +
    			"and pj.id_tipo_situacao_projeto = ? " +
    			"and media is not null " +
    			"order by pj.media desc ");
    	
    	Connection con = null;
		try {
			con = Database.getInstance().getSigaaConnection();
			PreparedStatement st = con.prepareStatement(sql.toString());
			st.setInt(1, edital.getId());
			st.setInt(2, TipoSituacaoProjeto.PROJETO_BASE_AVALIADO);
			ResultSet rs = st.executeQuery();
			
			List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
			while (rs.next()) {
				Map<String, Object> mapa = new HashMap<String, Object>();
				
				//projeto
				mapa.put("id_projeto", rs.getInt("id_projeto"));
				mapa.put("titulo", rs.getString("titulo"));
				mapa.put("ano", rs.getInt("ano"));
				mapa.put("classificacao", rs.getInt("classificacao"));
				mapa.put("media", rs.getDouble("media"));
				mapa.put("total_discentes_envolvidos", rs.getInt("total_discentes_envolvidos"));
				mapa.put("bolsas_solicitadas", rs.getInt("bolsas_solicitadas"));				
				mapa.put("ensino", rs.getBoolean("ensino"));
				mapa.put("pesquisa", rs.getBoolean("pesquisa"));
				mapa.put("extensao", rs.getBoolean("extensao"));
				
				mapa.put("coordenador", rs.getString("coordenador"));
				mapa.put("sigla_unidade", rs.getString("sigla_unidade"));
				mapa.put("area_cnpq", rs.getString("area_cnpq"));
				
				//orçamento
		    	mapa.put("pessoa_juridica", rs.getDouble("pessoa_juridica"));		    	 		
		    	mapa.put("pessoa_fisica", rs.getDouble("pessoa_fisica"));
		    	mapa.put("material_consumo", rs.getDouble("material_consumo"));
		    	mapa.put("diarias", rs.getDouble("diarias"));
		    	mapa.put("passagens", rs.getDouble("passagens"));
		    	mapa.put("equipamentos", rs.getDouble("equipamentos"));
		    	
		    	//avaliações
		    	mapa.put("total_avaliacoes_ad_hoc", rs.getInt("total_avaliacoes_ad_hoc"));
		    	mapa.put("max_avaliacao_ad_hoc", rs.getDouble("max_avaliacao_ad_hoc"));
		    	mapa.put("min_avaliacao_ad_hoc", rs.getDouble("min_avaliacao_ad_hoc"));
		    	mapa.put("med_avaliacao_ad_hoc", rs.getDouble("med_avaliacao_ad_hoc"));

		    	mapa.put("total_avaliacoes_comite", rs.getInt("total_avaliacoes_comite"));
		    	mapa.put("max_avaliacao_comite", rs.getDouble("max_avaliacao_comite"));
		    	mapa.put("min_avaliacao_comite", rs.getDouble("min_avaliacao_comite"));
		    	mapa.put("med_avaliacao_comite", rs.getDouble("med_avaliacao_comite"));

				result.add(mapa);
			}
			
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		} finally {
			Database.getInstance().close(con);
		}
    	
    }
    
    
}


