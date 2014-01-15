package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.academico.dominio.MembroProjeto;
import br.ufrn.academico.dominio.ProjetoAcademico;
import br.ufrn.academico.dominio.TipoProjetoAcademico;
import br.ufrn.arq.dao.GenericDAOImpl;
import br.ufrn.arq.seguranca.log.LogInterceptor;
import br.ufrn.comum.dominio.Sistema;

/**
 * DAO que provê consultas ao banco de dados do sistema acadêmico para
 * recuperar informações sobre projetos acadêmicos (pesquisa, extensão, ensino, etc.)
 * e as encapsula em DTOs do tipo ProjetoAcademico para uso nos sistemas administrativos. 
 *
 * @author wendell
 *
 */
@SuppressWarnings("unchecked")
public class ProjetoAcademicoDao extends GenericDAOImpl{

	private static final String CABECALHO_SQL_PESQUISA = "select p.id_projeto as id, p.titulo, pp.id_coordenador, p.data_inicio, p.data_fim," +
				" p.objetivos as identificacao, p.justificativa, pp.id_area_conhecimento_cnpq, " +
				" mp.id_pessoa, mp.id_discente as id_discente, mp.id_servidor,mp.ch_dedicada as ch_dedicada, (case when tipo_participacao = 1 then 'COORDENADOR' END) as tipo_participacao " +
				" from projetos.projeto p join pesquisa.projeto_pesquisa pp on (p.id_projeto = pp.id_projeto) " +
				" join projetos.membro_projeto mp on (mp.id_projeto = pp.id_projeto) " + 
				" WHERE p.id_tipo_situacao_projeto in (215,221) " +
				" AND p.data_fim >= now()";
	
	private static final String CABECALHO_SQL_EXTENSAO = "select "+
				"p.id_projeto as id, p.titulo, "+
				"coord.id_servidor as id_coordenador, "+
				"p.data_inicio, p.data_fim, "+
				"p.objetivos as identificacao, p.justificativa, a.id_area_conhecimento_cnpq, "+
				"mp.id_pessoa, mp.id_discente as id_discente, "+
				"mp.id_servidor,mp.ch_dedicada as ch_dedicada, f.descricao as tipo_participacao "+
				"from projetos.projeto p "+
				"join extensao.atividade a using (id_projeto) "+
				"join projetos.membro_projeto mp using (id_projeto) "+
				"join projetos.funcao_membro f using (id_funcao_membro) "+
				"left join projetos.membro_projeto coord on p.id_coordenador = coord.id_membro_projeto "+
				"where p.id_tipo_situacao_projeto not in (101, 104, 105, 107, 108, 111, 116)";
	
	
	
	private static final String CABECALHO_SQL_LATO_SENSU = "select c.id_curso as id, c.nome as titulo, cc.id_servidor as id_coordenador," +
				" cl.data_inicio, cl.data_fim, pcl.justificativa, pcl.importancia as identificacao," +
				" c.id_area_curso as id_area_conhecimento_cnpq, " +
				" cs.id_servidor,null as ch_dedicada, 'participante' as tipo_participacao " +
				" from curso c join lato_sensu.curso_lato cl using(id_curso) " +
				" join ensino.coordenacao_curso cc using(id_curso) " +
				" join lato_sensu.proposta_curso_lato pcl using(id_proposta) " +
				" join lato_sensu.curso_servidor cs using(id_curso) " +
				" WHERE cl.data_fim >= now() ";

	private static final String CABECALHO_SQL_ASSOCIADOS = "select "+
				"p.id_projeto as id, p.titulo, "+
				"coord.id_servidor as id_coordenador, "+
				"p.data_inicio, p.data_fim, "+
				"p.objetivos as identificacao, p.justificativa, 0 as id_area_conhecimento_cnpq, "+
				"mp.id_pessoa, mp.id_discente as id_discente, "+
				"mp.id_servidor,mp.ch_dedicada as ch_dedicada, f.descricao as tipo_participacao "+
				"from projetos.projeto p "+
				"join projetos.membro_projeto mp using (id_projeto) "+
				"join projetos.funcao_membro f using (id_funcao_membro) "+
				"left join projetos.membro_projeto coord on p.id_coordenador = coord.id_membro_projeto "+
				"where p.id_tipo_projeto = 4 ";
	

	public ProjetoAcademicoDao() {
		setSistema(Sistema.SIGAA);
		interceptor = new LogInterceptor();
		interceptor.setSistema(Sistema.COMUM);
	}
	
	/**
	 *  ResultSetExtractor para os dados retornados pelas consultas de projetos academicos,
	 *  mapeando-os para entidades ProjetoAcademico
	 */
	ResultSetExtractor projetoAcademicoExtractor = new ResultSetExtractor() {
		public Object extractData(ResultSet rs) throws SQLException,
				DataAccessException {
			List<ProjetoAcademico> projetos = new ArrayList<ProjetoAcademico>();
			
			while (rs.next()) {
				ProjetoAcademico p = new ProjetoAcademico();
				p.setId(rs.getInt("id"));
				
				if (projetos.contains(p)) {
					p = projetos.get(  projetos.indexOf(p) );
				} else {
					p.setTitulo(rs.getString("titulo") );
					p.setInicio( rs.getDate("data_inicio") );
					p.setFim( rs.getDate("data_fim") );
					p.setIdentificacaoObjeto(rs.getString("identificacao") );
					p.setJustificativa(rs.getString("justificativa") );
					p.setIdAreaConhecimento( rs.getInt("id_area_conhecimento_cnpq") );
					p.setIdCoordenador( rs.getInt("id_coordenador") );
					projetos.add(p);
				}
				
				MembroProjeto membro = new MembroProjeto();
				try {
					Integer idDiscente = (Integer) rs.getObject("id_discente");
				
					if (idDiscente != null) {
						membro.setIdDiscente(idDiscente);
					}
				} catch (Exception e) {}

				membro.setIdServidor((Integer) rs.getObject("id_servidor") );
				membro.setTipoParticipacao(rs.getString("tipo_participacao"));
				membro.setChHorariaSemanal((Integer) rs.getObject("ch_dedicada"));
				
				if(membro.getIdDiscente() != null || membro.getIdServidor() != null)
					p.adicionarMembro(membro);
			}
			
			return projetos;
		}
	};
	
	/**
	 * Busca um projeto acadêmico pelo seu identificador e tipo
	 * 
	 * @param codigo
	 * @param ano
	 * @param tipo
	 * @return
	 */
	public ProjetoAcademico findByIdTipo(int id, int tipo) {
		switch (tipo) {
			case TipoProjetoAcademico.PROJETO_PESQUISA: return findProjetoPesquisaById(id);
			case TipoProjetoAcademico.PROJETO_EXTENSAO: return findProjetoExtensaoById(id);
			case TipoProjetoAcademico.CURSO_LATO_SENSU: return findCursoLatoById(id);
		}
		return null;
	}
	
	private ProjetoAcademico findProjetoPesquisaById(int id) {
		String sql = CABECALHO_SQL_PESQUISA + " AND p.id_projeto = ?";
		return consultarProjeto(sql, id, TipoProjetoAcademico.PROJETO_PESQUISA);
	}

	private ProjetoAcademico findProjetoExtensaoById(int id) {
		String sql = CABECALHO_SQL_EXTENSAO + " AND p.id_projeto = ?";
		return consultarProjeto(sql, id, TipoProjetoAcademico.PROJETO_EXTENSAO);
	}

	private ProjetoAcademico findCursoLatoById(int id) {
		String sql = CABECALHO_SQL_LATO_SENSU + " AND c.id_curso";
		return consultarProjeto(sql, id, TipoProjetoAcademico.CURSO_LATO_SENSU);
	}

	/**
	 * Buscar todos os projetos academicos que um servidor coordena
	 * 
	 * @param idServidor
	 * @return
	 */
	public Collection<ProjetoAcademico> findByCoordenador(int idServidor) {
		return findByCoordenador(idServidor, null);
	}
	
	/**
	 * Buscar todos os projetos acadêmicos que um servidor coordena,
	 * passando opcionalmente os tipos de projetos desejados
	 * 
	 * @param idServidor
	 * @param tipo
	 * @return
	 */
	public Collection<ProjetoAcademico> findByCoordenador(int idServidor, Integer tipo) {
		Collection<ProjetoAcademico> projetos = new ArrayList<ProjetoAcademico>();
		// Buscar todos
		if (tipo == null) {
			projetos.addAll( findProjetosPesquisaByCoordenador(idServidor) );
			projetos.addAll( findProjetosExtensaoByCoordenador(idServidor) );
			projetos.addAll( findCursosLatoByCoordenador(idServidor) );
			projetos.addAll( findProjetosAssociadoByCoordenador(idServidor) );
		} else {
			// Buscar por tipo
			switch (tipo) {
				case TipoProjetoAcademico.PROJETO_PESQUISA: projetos = findProjetosPesquisaByCoordenador(idServidor); break;
				case TipoProjetoAcademico.PROJETO_EXTENSAO: projetos = findProjetosExtensaoByCoordenador(idServidor); break;
				case TipoProjetoAcademico.CURSO_LATO_SENSU: projetos = findCursosLatoByCoordenador(idServidor); break;
				case TipoProjetoAcademico.PROJETO_ASSOCIADO: projetos = findProjetosAssociadoByCoordenador(idServidor); break;
			}
		}
				
		return projetos;
	}
	 
	/**
	 * Buscar os projetos de pesquisa coordenados por um servidor
	 * 
	 * @param idServidor
	 * @return
	 */
	public Collection<ProjetoAcademico> findProjetosPesquisaByCoordenador(int idServidor) {
		String sql = CABECALHO_SQL_PESQUISA + " AND pp.id_coordenador = ? order by p.ano, p.titulo";
		return consultarProjetos(sql, idServidor, TipoProjetoAcademico.PROJETO_PESQUISA);
	}

	/**
	 * Buscar as atividades de extensao coordenadas por um servidor
	 * 
	 * @param idServidor
	 * @return
	 */
	public Collection<ProjetoAcademico> findProjetosExtensaoByCoordenador(int idServidor) {
		String sql = CABECALHO_SQL_EXTENSAO + " and coord.id_servidor = ? order by p.ano, p.titulo";
		return consultarProjetos(sql, idServidor, TipoProjetoAcademico.PROJETO_EXTENSAO);
	}
	
	/**
	 * Buscar todos os cursos de especialização coordenados por um servidor
	 * 
	 * @param idServidor
	 * @return
	 */
	public Collection<ProjetoAcademico> findCursosLatoByCoordenador(int idServidor) {
		String sql = CABECALHO_SQL_LATO_SENSU + " AND cc.id_servidor = ? order by cl.data_inicio, c.nome ";
		return consultarProjetos(sql, idServidor, TipoProjetoAcademico.CURSO_LATO_SENSU);
	}
	
	/**
	 * Buscar todos os cursos de especialização coordenados por um servidor
	 * 
	 * @param idServidor
	 * @return
	 */
	public Collection<ProjetoAcademico> findProjetosAssociadoByCoordenador(int idServidor) {
		String sql = CABECALHO_SQL_ASSOCIADOS + " and coord.id_servidor = ? order by p.ano, p.titulo";
		return consultarProjetos(sql, idServidor, TipoProjetoAcademico.PROJETO_ASSOCIADO);
	}

	/**
	 * Busca projetos academicos de acordo com a consulta passada, o id do servidor designado como coordenador
	 * do projeto e o tipo do projeto acadêmico 
	 * 
	 * @param consulta
	 * @param idServidor
	 * @param tipoProjetoAcademico
	 * @return
	 */
	private Collection<ProjetoAcademico> consultarProjetos(String consulta, int idServidor, int tipoProjetoAcademico) {
		Collection<ProjetoAcademico> projetos = (Collection<ProjetoAcademico>) getJdbcTemplate().query(consulta, new Object[] {idServidor}, projetoAcademicoExtractor);
		return popularTipoProjeto(projetos, tipoProjetoAcademico);
	}
	
	private ProjetoAcademico consultarProjeto(String consulta, int id, int tipoProjetoAcademico) {
		Collection<ProjetoAcademico> projetos = (Collection<ProjetoAcademico>) getJdbcTemplate().query(consulta, new Object[] {id},  projetoAcademicoExtractor);
		projetos = popularTipoProjeto(projetos, tipoProjetoAcademico);
		
		if (!isEmpty(projetos)) {
			return projetos.iterator().next(); 
		}
		
		return null;
	}

	/**
	 * Popula os projetos de uma coleção com um determinado tipo de Projeto Academico
	 * 
	 * @param projetos
	 * @param tipo
	 * @return
	 */
	private Collection<ProjetoAcademico> popularTipoProjeto(Collection<ProjetoAcademico> projetos, final int tipo) {
		for (ProjetoAcademico projeto : projetos) {
			projeto.setTipo(tipo);
		}
		return projetos;
	}
	
}
