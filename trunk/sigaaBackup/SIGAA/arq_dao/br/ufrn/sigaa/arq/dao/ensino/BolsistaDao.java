package br.ufrn.sigaa.arq.dao.ensino;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.bolsas.dominio.Bolsista;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * DAO responsável por consultas e gerenciamento de informações de bolsas diretamente no sistema administrativo.
 * 
 * @author wendell
 *
 */
public class BolsistaDao extends GenericSigaaDAO {

	public BolsistaDao() {
		setSistema(Sistema.getSistemaAdministrativoAtivo());
	}
	
	private static final String CABECALHO_CONSULTA = "SELECT " +
			" b.id, b.id_bolsista, b.id_tipo_bolsa, b.inicio, b.fim, " +
			" tb.denominacao as tipoBolsa, a.matricula, a.nivel_sigaa, p.nome, " +
			" u.codigo_unidade as codigoUnidade, u.nome as nomeUnidade " +
			" FROM bolsas.bolsa b " +
			" JOIN bolsas.bolsista bs on b.id_bolsista = bs.id" +
			" JOIN bolsas.tipo_bolsa tb on b.id_tipo_bolsa = tb.id" +
			" JOIN comum.unidade u on b.id_unidade = u.id_unidade" +
			" JOIN academico.aluno a on a.matricula = bs.matricula " +
			" JOIN comum.pessoa p on bs.id_pessoa = p.id_pessoa";

	private static final String ORDENACAO_CONSULTA = " ORDER BY p.nome ";
	
	private static RowMapper bolsistaRowMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Bolsista b = new Bolsista();
			b.setIdBolsa(rs.getInt("id"));
			b.setIdBolsista(rs.getInt("id_bolsista"));
			b.setIdTipoBolsa(rs.getInt("id_tipo_bolsa"));
			b.setDataInicio( rs.getDate("inicio") );
			b.setDataFim( rs.getDate("fim") );
			b.setTipoBolsa( rs.getString("tipoBolsa") );
			
			String nivelEnsino = rs.getString("nivel_sigaa");
			
			Discente discente = new Discente();
			discente.setMatricula( rs.getLong("matricula") );
			discente.setNivel( isEmpty(nivelEnsino) ? " ".charAt(0) : nivelEnsino.charAt(0) );
			discente.getPessoa().setNome( rs.getString("nome"));
			b.setDiscente(discente);

			Unidade unidade = new Unidade();
			unidade.setCodigo( rs.getLong("codigoUnidade") );
			unidade.setNome( rs.getString("nomeUnidade") );
			b.setUnidade(unidade);

			return b;
		}
	};

	/**
	 * Cria as entradas necessárias para o registro de uma nova bolsa e bolsista correspondente 
	 * 
	 * @param bolsista
	 * @throws DAOException
	 */
	public void create(Bolsista bolsista) throws DAOException {
		int idBolsa = getJdbcTemplate().queryForInt("select nextval('bolsa_seq')");
		int idBolsista = getJdbcTemplate().queryForInt("select nextval('bolsista_seq')");
		long idPessoa = getJdbcTemplate().queryForLong("select id_pessoa from comum.pessoa where cpf_cnpj = ?", 
				bolsista.getDiscente().getPessoa().getCpf_cnpj());
		
		int idAluno = findIdAluno(bolsista.getDiscente());
		
		update("insert into bolsas.bolsista " +
				" (id, id_pessoa, id_aluno, id_curso, matricula) " +
				" values (?,?,?,?,?) ", new Object[] {idBolsista, idPessoa, 
				idAluno, bolsista.getDiscente().getCurso().getId(), 
				bolsista.getDiscente().getMatricula()});

		update("insert into bolsas.bolsa " +
				" (id, id_bolsista, id_tipo_bolsa, id_unidade, id_setor_trabalho, inicio, fim) " +
				" values (?,?,?,?,?,?,?)", new Object[] {idBolsa, idBolsista, bolsista.getIdTipoBolsa(), 
				bolsista.getUnidade().getId(), bolsista.getUnidade().getId(),
				bolsista.getDataInicio(), bolsista.getDataFim()});
		
		bolsista.setIdBolsa(idBolsa);
		bolsista.setIdBolsista(idBolsista);
	}
	
	/**
	 * Busca o id de um aluno no banco administrativo
	 * 
	 * @param discente
	 * @return
	 */
	private int findIdAluno(Discente discente) {
		return getJdbcTemplate().queryForInt("select id_aluno from academico.aluno " +
				" where matricula = ? " +
				" and nivel_sigaa in " + gerarStringIn(NivelEnsino.getNiveisStricto()) + 
				BDUtils.limit(1), 
				discente.getMatricula());
	}

	/**
	 * Atualiza os dados de um bolsista
	 * 
	 * @param bolsista
	 * @throws DAOException
	 */
	public void update(Bolsista bolsista) throws DAOException {
		update("update bolsas.bolsa " +
				" set inicio = ?, fim = ? where id = ?", new Object[] { bolsista.getDataInicio(), bolsista.getDataFim(), 
				bolsista.getIdBolsa()});
	}

	
	/**
	 * Buscar todos os bolsistas de uma unidade, opcionalmente por tipo de bolsa
	 * 
	 * @param unidade
	 * @param idTipoBolsa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Bolsista> findByUnidade(Unidade unidade, Integer idTipoBolsa) throws DAOException {
		Object[] parametros = null;
		
		StringBuilder sql = new StringBuilder(CABECALHO_CONSULTA);
		sql.append(" WHERE b.id_unidade = ? and b.fim > now() ");
		
		if ( idTipoBolsa == null ) {
			parametros = new Object[] {unidade.getId()};
		} else {
			sql.append(" AND b.id_tipo_bolsa = ?");
			parametros = new Object[] {unidade.getId(), idTipoBolsa};
		}

		sql.append(ORDENACAO_CONSULTA);
		
		@SuppressWarnings("unchecked")
		Collection<Bolsista> lista = getJdbcTemplate().query(sql.toString(), parametros, bolsistaRowMapper);
		return lista;
	}

	
	/**
	 * Busca as informações da bolsa e do bolsista pelo id da bolsa
	 * 
	 * @param idBolsa
	 * @return
	 * @throws DAOException
	 */
	public Bolsista findByPrimaryKey(int idBolsa) throws DAOException {
		return (Bolsista) getJdbcTemplate().queryForObject(CABECALHO_CONSULTA + " WHERE b.id = ? ", new Object[] {idBolsa}, bolsistaRowMapper);
	}

	/**
	 * Busca o bolsista ativo associado ao discente informado, se houver.
	 * 
	 * @param discente
	 * @return
	 */
	public boolean hasAtivoByDiscente(Discente discente) {
		int idAluno = findIdAluno(discente);
		return !getJdbcTemplate().query(CABECALHO_CONSULTA + " WHERE bs.id_aluno = ? AND b.fim > now() ", 
				new Object[] {idAluno}, bolsistaRowMapper).isEmpty();
	}

	/**
	 * Registra a finalização de uma bolsa
	 * 
	 * @param bolsista
	 */
	public void finalizar(Bolsista bolsista) {
		update("update bolsas.bolsa " +
				" set data_finalizacao = ?, fim = ?, id_usuario_finalizacao = ? where id = ?", 
				new Object[] { bolsista.getDataFinalizacao(), bolsista.getDataFinalizacao(), bolsista.getUsuarioFinalizacao().getId(), bolsista.getIdBolsa()});
	}
	
	/**
	 * Buscar todos os bolsistas de uma unidade, opcionalmente por tipo de bolsa
	 * 
	 * @param unidade
	 * @param idTipoBolsa
	 * @return
	 * @throws DAOException
	 */
	public Collection<Bolsista> findByDiscente(DiscenteAdapter discente) throws DAOException {
		
		StringBuilder sql = new StringBuilder(CABECALHO_CONSULTA);
		sql.append(" WHERE a.matricula = ? order by fim");
		
		@SuppressWarnings("unchecked")
		Collection<Bolsista> lista = getJdbcTemplate().query(sql.toString(), new Object[] {discente.getMatricula()}, bolsistaRowMapper);
		return lista;
	}
}