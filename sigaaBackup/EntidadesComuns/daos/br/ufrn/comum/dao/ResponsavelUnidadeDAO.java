/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 18/03/2008
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dao.dialect.SQLDialect;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Responsavel;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.Unidade;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Servidor;

/**
 * DAO comum com consultas referentes à tabela responsavel_unidade
 *
 * @author Raphaela Galhardo
 *
 */
public class ResponsavelUnidadeDAO extends GenericSharedDBDao {

	public ResponsavelUnidadeDAO(){}

	public ResponsavelUnidadeDAO(int sistema){
		setSistema(sistema);
	}

	/**
	 * Mapeamento do ResultSet retornado pelas consultas para a classe de domínio UnidadeGeral.java;
	 */
	RowMapper unidadeMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			UnidadeGeral uni = new UnidadeGeral();
			uni.setId(rs.getInt("id_unidade"));
			uni.setNome(rs.getString("nome"));
			uni.setCategoria(rs.getInt("categoria"));
			uni.setTipo(rs.getInt("tipo"));
			uni.setCodigo(rs.getLong("codigo_unidade"));
			return uni;
		}
	};

	/**
	 * Mapeamento do ResultSet retornado pelas consultas para a classe de domínio Responsavel.java,
	 * NÃO considerando a unidade de lotação do servidor associado ao registro de responsável;
	 */
	RowMapper responsavelUnidadeMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Responsavel resp = new Responsavel();
			resp.setId(rs.getInt("id"));
			resp.setUnidade(new Unidade(rs.getInt("id_unidade")));
			resp.getUnidade().setNome(rs.getString("nome_unidade"));
			resp.getUnidade().setCodigo(rs.getLong("codigo_unidade"));
			resp.setNivelResponsabilidade(rs.getString("nivel_responsabilidade").charAt(0));
			resp.setObservacao(rs.getString("observacao"));
			resp.setInicio(rs.getDate("data_inicio"));
			resp.setFim(rs.getDate("data_fim"));
			resp.setDataCadastro(rs.getDate("data_cadastro"));
			resp.setUltimaAtualizacao(rs.getDate("ultima_atualizacao"));
			resp.setNomeServidor(rs.getString("nome_servidor"));
			resp.setMatricula(rs.getInt("matricula"));
			resp.setServidor(new Servidor());
			resp.getServidor().setId(rs.getInt("id_servidor"));
			resp.getServidor().setSiape(resp.getMatricula());
			resp.getServidor().setPessoa(new PessoaGeral());
			resp.getServidor().getPessoa().setId(rs.getInt("id_pessoa"));
			resp.getServidor().getPessoa().setEmail(rs.getString("email_pessoa"));

			return resp;
		}
	};

	/**
	 * Mapeamento do ResultSet retornado pelas consultas para a classe de domínio Responsavel.java,
	 * considerando a unidade de lotação do servidor associado ao registro de responsável;
	 */
	RowMapper responsavelComUnidadeLotacaoMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Responsavel resp = new Responsavel();
			resp.setId(rs.getInt("id"));
			resp.setUnidade(new Unidade(rs.getInt("id_unidade")));
			resp.getUnidade().setNome(rs.getString("nome_unidade"));
			resp.setNivelResponsabilidade(rs.getString("nivel_responsabilidade").charAt(0));
			resp.setInicio(rs.getDate("data_inicio"));
			resp.setFim(rs.getDate("data_fim"));
			resp.setDataCadastro(rs.getDate("data_cadastro"));
			resp.setUltimaAtualizacao(rs.getDate("ultima_atualizacao"));
			resp.setNomeServidor(rs.getString("nome_servidor"));
			resp.setMatricula(rs.getInt("matricula"));
			resp.setServidor(new Servidor());
			resp.getServidor().setId(rs.getInt("id_servidor"));
			resp.getServidor().setSiape(resp.getMatricula());
			resp.getServidor().setPessoa(new PessoaGeral());
			resp.getServidor().getPessoa().setId(rs.getInt("id_pessoa"));
			resp.getServidor().getPessoa().setNome(resp.getNomeServidor());
			resp.getServidor().getPessoa().setEmail(rs.getString("EMAIL_PESSOA"));

			UnidadeGeral unidade = new UnidadeGeral();
			unidade.setId(rs.getInt("ID_UNIDADE_LOTACAO"));
			unidade.setNome(rs.getString("NOME_UNIDADE_LOTACAO"));
			
			try{
				
				UsuarioGeral usuario = new UsuarioGeral();
				usuario.setLogin(rs.getString("LOGIN_USUARIO"));
				PessoaGeral pe = new PessoaGeral();
				pe.setNome(rs.getString("NOME_USUARIO"));
				usuario.setPessoa(pe);
				resp.setUsuario(usuario);
				pe.setEmail(rs.getString("EMAIL_PESSOA"));
				
			}catch (Exception e) {			}

			resp.getServidor().setUnidade(unidade);

			return resp;
		}
	};

	/**
	 * Mapeamento detalhado do ResultSet retornado pelas consultas para a classe de domínio Responsavel.java.
	 */
	RowMapper responsavelMapper = new RowMapper() {
		public Object mapRow(ResultSet rs, int row) throws SQLException {
			Responsavel resp = new Responsavel();
			resp.setId(rs.getInt("id"));
			resp.setUnidade(new Unidade(rs.getInt("id_unidade")));
			resp.getUnidade().setNome(rs.getString("nome_unidade"));
			
			String nivelResponsabilidade = rs.getString("nivel_responsabilidade");
			
			if (nivelResponsabilidade != null)
				resp.setNivelResponsabilidade(nivelResponsabilidade.charAt(0));
			else
				resp.setNivelResponsabilidade(null);
			
			resp.setInicio(rs.getDate("data_inicio"));
			resp.setFim(rs.getDate("data_fim"));
			resp.setDataCadastro(rs.getDate("data_cadastro"));
			resp.setUltimaAtualizacao(rs.getDate("ultima_atualizacao"));
			resp.setNomeServidor(rs.getString("nome_servidor"));
			resp.setMatricula(rs.getInt("matricula"));
			resp.setServidor(new Servidor());
			resp.getServidor().setId(rs.getInt("id_servidor"));
			resp.getServidor().setPessoa(new PessoaGeral());
			resp.getServidor().getPessoa().setId(rs.getInt("id_pessoa"));
			resp.setCargo(new Cargo());
			resp.getCargo().setId(rs.getInt("id_cargo"));
			if(rs.getString("origem") != null)
				resp.setOrigem(rs.getString("origem").charAt(0));
			else
				resp.setOrigem(null);
			return resp;
		}
	};

	/**
	 * Retorna o identificador da tabela responsavel_unidade que diz respeito
	 * exatamente aos dados passados como argumento.
	 *
	 * @param idUnidade
	 * @param idServidor
	 * @param nivelResponsabilidade --> C - Chefe/Diretor; V - Vice-Chefe/Vice-Diretor; S - Secretaria; G - Gerente
	 * @param inicio
	 * @param fim
	 * @return
	 * @throws DAOException
	 */
	public int getIdResponsavelUnidade(int idUnidade, int idServidor, String nivelResponsabilidade, Date inicio, Date fim) throws DAOException {

		String sql =
			" SELECT ID FROM COMUM.RESPONSAVEL_UNIDADE RU WHERE RU.ID_UNIDADE = ? AND ID_SERVIDOR = ? AND nivel_responsabilidade = ? AND RU.DATA_INICIO = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND ";

		if (fim == null)
			sql +=	" RU.DATA_FIM IS NULL ";
		else
			sql += " RU.DATA_FIM = ? ";

		Object[] lista = null;
		if (fim != null)
			lista = new Object[5];
		else
			lista = new Object[4];

		lista[0] = idUnidade;
		lista[1] = idServidor;
		lista[2] = nivelResponsabilidade;
		lista[3] = inicio;

		if (fim != null)
			lista[4] = fim;

		try{
			return getJdbcTemplate().queryForInt(sql, lista);
		}catch (EmptyResultDataAccessException e) {
			return -1;
		}

	}
	
	/**
	 * Retorna os identificadores das unidades que o servidor passado como argumento é responsável atualmente.
	 * Considera o nível de responsabilidade passado como argumento.
	 *
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<UnidadeGeral> getUnidadesByResponsavel(int idServidor, Character[] niveisResponsabilidade) throws DAOException {

		String sql = "SELECT U.ID_UNIDADE, U.NOME, U.CATEGORIA, U.TIPO,U.CODIGO_UNIDADE FROM COMUM.RESPONSAVEL_UNIDADE RU " +
						"INNER JOIN COMUM.UNIDADE U ON U.ID_UNIDADE = RU.ID_UNIDADE " +
						"WHERE RU.ID_SERVIDOR = ? AND RU.DATA_INICIO <= ? AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= ?) " +
							"AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL " +
							"AND nivel_responsabilidade IN" + UFRNUtils.gerarStringIn(niveisResponsabilidade);


		Object[] lista = {idServidor, new Date(), new Date()};

		try{
			return getJdbcTemplate().query(sql, lista, unidadeMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	/**
	 * Procura por todas as unidades e os parâmetros da responsabilidade
	 * (data de início e nível de responsabilidade, por exemplo) que são e que já foram
	 * responsabilizadas por um determinado servidor.
	 *
	 * @param idServidor - Identificador do servidor
	 * @return Lista de objetos <code>Responsavel</code> com todas as informações da
	 * responsabilidade de um servidor para uma unidade.
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> findAllResponsabilidadesByResponsavel(int idServidor) {

		String sql = "SELECT U.ID_UNIDADE, U.NOME AS NOME_UNIDADE, U.SIGLA AS SIGLA_UNIDADE, U.CODIGO_UNIDADE, P.NOME AS USUARIO_CADASTRO, " +
				"RU.DATA_INICIO, RU.DATA_FIM, RU.NIVEL_RESPONSABILIDADE, RU.DATA_CADASTRO " +
				"FROM COMUM.RESPONSAVEL_UNIDADE RU INNER JOIN COMUM.UNIDADE U ON RU.ID_UNIDADE = U.ID_UNIDADE " +
				"LEFT JOIN comum.USUARIO US ON RU.ID_USUARIO = US.ID_USUARIO " +
				"LEFT JOIN comum.PESSOA P ON US.ID_PESSOA = P.ID_PESSOA " +
				"WHERE RU.ID_SERVIDOR = ? " +
				"  AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ORDER BY RU.NIVEL_RESPONSABILIDADE,U.NOME";

		try {
			return getJdbcTemplate().query(sql, new Object[] {idServidor}, new RowMapper() {
				public Object mapRow(ResultSet rs, int row) throws SQLException {

					Unidade unidade = new Unidade();
					unidade.setId(rs.getInt("id_unidade"));
					unidade.setNome(rs.getString("nome_unidade"));
					unidade.setSigla(rs.getString("sigla_unidade"));
					unidade.setCodigo(rs.getLong("codigo_unidade"));

					UsuarioGeral usuario = new UsuarioGeral();
					usuario.setPessoa(new PessoaGeral());
					if (rs.getString("usuario_cadastro") != null)
						usuario.getPessoa().setNome(rs.getString("usuario_cadastro"));
					else
						usuario.getPessoa().setNome("FITA ESPELHO");

					Responsavel responsavel = new Responsavel();
					responsavel.setUnidade(unidade);
					responsavel.setUsuario(usuario);
					responsavel.setInicio(rs.getDate("data_inicio"));
					responsavel.setFim(rs.getDate("data_fim"));
					responsavel.setNivelResponsabilidade(rs.getString("nivel_responsabilidade").charAt(0));
					responsavel.setDataCadastro(rs.getDate("data_cadastro"));

					return responsavel;
				}
			});
		} catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Retorna os responsáveis por uma determinada unidade.
	 *
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> getResponsaveisByUnidade(int idUnidade) throws DAOException {

		String sql =
			"SELECT DISTINCT RU.*, U.NOME AS NOME_UNIDADE, U.CODIGO_UNIDADE, S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA AS ID_PESSOA, P.EMAIL AS EMAIL_PESSOA " +
			"FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U,RH.SERVIDOR S,comum.PESSOA P " +
			"WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA "+
			"AND RU.ID_UNIDADE = ? AND U.ID_UNIDADE = RU.ID_UNIDADE AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ORDER BY RU.DATA_FIM DESC,U.NOME ASC" ;

		Object[] lista = {idUnidade};
		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, lista, responsavelUnidadeMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}catch (Exception e){
			throw new DAOException(e);
		}

	}

	/**
	 * Retorna o responsável atual da unidade especificada que possua o nível de responsabilidade informado.
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavelAtualByUnidade(int idUnidade, char nivelResponsabilidade) {
		return findResponsavelAtualByUnidade(idUnidade, nivelResponsabilidade, false);
	}

	/**
	 * Retorna os responsáveis da unidade especificada que possuam os níveis de responsabilidade informado.
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Responsavel> findResponsavelAtualByUnidade(int idUnidade, Character[] niveisResponsabilidades) throws DAOException {

		String sql = "SELECT DISTINCT(RU.ID_SERVIDOR) " +
			"FROM COMUM.RESPONSAVEL_UNIDADE RU " +
			"WHERE RU.ID_UNIDADE = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND RU.NIVEL_RESPONSABILIDADE IN " + UFRNUtils.gerarStringIn(niveisResponsabilidades) +
			" AND RU.DATA_INICIO <= CURRENT_DATE AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE)";


		Object[] lista = {idUnidade};

		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, lista, new RowMapper() {
				public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
					Responsavel resp = new Responsavel();
					resp.setServidor(new Servidor());
					resp.getServidor().setId(rs.getInt("id_servidor"));
					return resp;
				}
			});
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	/**
	 * Retorna o responsável atual da unidade especificada que possua o nível de responsabilidade informado.
	 * Se somenteResponsaveisLotadosNaUnidade = true, só deverá recuperar o responsável caso o mesmo seja chefe
	 * da unidade a qual ele é lotado.
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @param somenteResponsaveisLotadosNaUnidade
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavelAtualByUnidade(int idUnidade, char nivelResponsabilidade, boolean somenteResponsaveisLotadosNaUnidade) {

		String sql = "SELECT RU.*, U.NOME AS NOME_UNIDADE,S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA, P.EMAIL AS EMAIL_PESSOA, ";
			sql += " LOT.ID_UNIDADE AS ID_UNIDADE_LOTACAO, LOT.NOME AS NOME_UNIDADE_LOTACAO ";
			sql += " FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U,RH.SERVIDOR S,comum.PESSOA P, COMUM.UNIDADE LOT ";
			sql += " WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA AND S.ID_UNIDADE = LOT.ID_UNIDADE ";
			sql += " AND RU.ID_UNIDADE = ? AND U.ID_UNIDADE = RU.ID_UNIDADE AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND RU.NIVEL_RESPONSABILIDADE = ? ";
			sql += " AND RU.DATA_INICIO <= CURRENT_DATE AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE) ";

			if(somenteResponsaveisLotadosNaUnidade)
				sql += " AND RU.ID_UNIDADE = LOT.ID_UNIDADE ";

			sql += " ORDER BY RU.DATA_CADASTRO DESC " ;
			
			sql = SQLDialect.limit(sql, 1,0);
			
		Object[] lista = {idUnidade, String.valueOf(nivelResponsabilidade), };

		try{
			return (Responsavel) getJdbcTemplate(Sistema.COMUM).queryForObject(sql, lista, responsavelComUnidadeLotacaoMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	/**
	 * Retorna os responsáveis atuais (diretos e indiretos) da unidade especificada que possua algum dos níveis de responsabilidade informados.
	 * É considerado responsável indireto, aqueles que são responsáveis pelas unidades acima da unidade especificada,
	 * considerando a hierarquia reversa das mesmas.
	 *
	 * @param idUnidade
	 * @param niveisResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Responsavel> findResponsaveisAtuaisPorHierarquiaByUnidade(int idUnidade, Character... niveisResponsabilidade)
	throws DAOException {

		String sql = "SELECT RU.*, U.NOME AS NOME_UNIDADE,S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA, ";
		sql += " LOT.ID_UNIDADE AS ID_UNIDADE_LOTACAO, LOT.NOME AS NOME_UNIDADE_LOTACAO ";
		sql += " FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U,RH.SERVIDOR S,comum.PESSOA P, COMUM.UNIDADE LOT ";
		sql += " WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA AND S.ID_UNIDADE = LOT.ID_UNIDADE ";
		sql += " AND RU.NIVEL_RESPONSABILIDADE IN ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND RU.DATA_INICIO <= CURRENT_DATE AND RU.ID_UNIDADE = U.ID_UNIDADE ";
		sql += " AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE) ";
		sql += " AND (SELECT DISTINCT HIERARQUIA_ORGANIZACIONAL FROM UNIDADE WHERE ID_UNIDADE = ?) LIKE U.HIERARQUIA_ORGANIZACIONAL||'%' ";
		sql += " ORDER BY RU.DATA_CADASTRO DESC";

		Object[] lista = {UFRNUtils.gerarStringIn(niveisResponsabilidade), idUnidade};

		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, lista, responsavelComUnidadeLotacaoMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}

	/**
	 * Retorna se o servidor passado como argumento possui o nível de responsabilidade também passado como argumento
	 * pela unidade informada.
	 *
	 * @param idServidor
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	public boolean isResponsavelByUnidade(int idServidor, int idUnidade, String nivelResponsabilidade) throws DAOException {

		String sql =
			" SELECT COUNT(RU.ID) FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U WHERE U.ID_UNIDADE = RU.ID_UNIDADE " +
			"AND RU.ID_UNIDADE = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND RU.ID_SERVIDOR = ? AND nivel_responsabilidade = ? AND RU.DATA_INICIO <= ? AND ";
		sql +=	" (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= ?)";

		Object[] lista = {idUnidade, idServidor, nivelResponsabilidade, new Date(), new Date()};

		try{

			Integer total = getJdbcTemplate().queryForInt(sql, lista);

			return (total > 0)?true:false;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}

	}

	/**
	 * Retorna o nome do responsável por uma unidade passado como argumento o idServidor.
	 * Utilizado na atualização do responsável
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public String findNomeResponsavelUnidade(int idServidor) throws DAOException{
		String sql =
			" SELECT p.nome FROM comum.responsavel_unidade ru inner join rh.servidor s on ru.id_servidor =  s.id_servidor " +
			" inner join comum.pessoa p on s.id_pessoa = p.id_pessoa  WHERE ru.id_servidor = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ";

		Object[] lista = {idServidor};

		String nome =(String)getJdbcTemplate(Sistema.COMUM).queryForObject(sql, lista, String.class);
		return nome;
	}

	/**
	 * Retorna se o servidor já é um responsável pela unidade informada passado como argumento a unidade e o servidor
	 * 	 *
	 * @param idServidor
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public boolean isExisteResponsavel(int idServidor, int idUnidade) throws DAOException {
		String sql ="SELECT count(ru.id) FROM COMUM.RESPONSAVEL_UNIDADE ru where ru.id_unidade = ? and ru.id_servidor = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ";

		Object[] lista = {idUnidade, idServidor};

		try{
			Integer responsavel = getJdbcTemplate().queryForInt(sql, lista);

			return (responsavel > 0)?true:false;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}
	}



	/**
	 * Retorna o registro do responsável por uma determinada unidade passados o idUnidade e o IdServidor.
	 *
	 * @param idUnidade
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavelByUnidade(int idUnidade, int idServidor) {
		String sql =
			"SELECT RU.*, U.NOME AS NOME_UNIDADE, U.CODIGO_UNIDADE, S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA, P.EMAIL AS EMAIL_PESSOA " +
			"FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U,RH.SERVIDOR S,comum.PESSOA P " +
			"WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL "+
			"AND RU.ID_UNIDADE = ? AND U.ID_UNIDADE = RU.ID_UNIDADE  AND RU.ID_SERVIDOR = ? order by ru.data_fim desc " + BDUtils.limit(1);


		Object[] lista = {idUnidade,idServidor};

		try {
			return (Responsavel) getJdbcTemplate(Sistema.COMUM).queryForObject(sql,lista, responsavelUnidadeMapper);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}
	
	/**
	 * Retorna os registros dos responsáveis cadastrados em determinada data de unidades vinculadas à determinada unidade gestora, conforme os níveis de responsabilidade passados
	 *
	 * @param dataCadastro: data de cadastro do responsável
	 * @param idUnidadeGestora: identificador da unidade gestora da unidade do responsável
	 * @param niveisResponsabilidade: níveis de responsabilidade considerados: chefe, vice, etc.
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<Responsavel> findResponsaveisByGestora(Date dataCadastro, Date ultimaAtualizacao, int idUnidadeGestora, Character... niveisResponsabilidade)
	throws DAOException {

			
		String sql = "SELECT RU.*, U.NOME AS NOME_UNIDADE, U.CODIGO_UNIDADE, S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA, ";
		sql += " LOT.ID_UNIDADE AS ID_UNIDADE_LOTACAO, LOT.NOME AS NOME_UNIDADE_LOTACAO, US.LOGIN AS LOGIN_USUARIO, PE_US.NOME AS NOME_USUARIO ";
		sql += " FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U, RH.SERVIDOR S, comum.PESSOA P, COMUM.UNIDADE LOT, COMUM.USUARIO US , COMUM.PESSOA PE_US ";
		sql += " WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA AND S.ID_UNIDADE = LOT.ID_UNIDADE ";
		sql += " AND US.ID_USUARIO = RU.ID_USUARIO AND PE_US.ID_PESSOA = US.ID_PESSOA ";
		sql += " AND RU.NIVEL_RESPONSABILIDADE IN  " + UFRNUtils.gerarStringIn(niveisResponsabilidade);
		sql += " AND RU.ID_UNIDADE = U.ID_UNIDADE ";
		sql += " AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ";
		sql += " AND U.HIERARQUIA LIKE  ?";
		sql += " AND  (cast(RU.DATA_CADASTRO as date) = ? OR cast(RU.ultima_atualizacao as date) = ?)";
		sql += " ORDER BY RU.DATA_CADASTRO DESC";

		java.sql.Date dataCadastroSemHoras = new java.sql.Date(CalendarUtils.descartarHoras(dataCadastro).getTime());
		java.sql.Date dataAtualizacaoSemHoras = new java.sql.Date(CalendarUtils.descartarHoras(ultimaAtualizacao).getTime());
		
		Object[] lista = { "%." + idUnidadeGestora + ".%", dataCadastroSemHoras, dataAtualizacaoSemHoras};

		try{
			
			return getJdbcTemplate(Sistema.COMUM).query(sql, lista, responsavelComUnidadeLotacaoMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}


	/**
	 * Retorna o registro do responsável por identificador.
	 *
	 * @param idUnidade
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public Responsavel findResponsavel(int idResponsavel) throws DAOException{
		String sql =
			"SELECT RU.*, U.NOME AS NOME_UNIDADE, S.ID_SERVIDOR AS ID_SERVIDOR, RU.ID_CARGO AS ID_CARGO, RU.ORIGEM AS ORIGEM," +
			" S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA " +
			"FROM COMUM.RESPONSAVEL_UNIDADE RU, COMUM.UNIDADE U,RH.SERVIDOR S,comum.PESSOA P " +
			"WHERE S.ID_SERVIDOR = RU.ID_SERVIDOR AND S.ID_PESSOA = P.ID_PESSOA AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL "+
			"AND RU.ID = ? AND U.ID_UNIDADE = RU.ID_UNIDADE " ;

		try{
			return (Responsavel) getJdbcTemplate(Sistema.COMUM).queryForObject(sql,new Object[] {idResponsavel}, responsavelMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}

	}


	/**
	 * Retorna o nível de responsabilidade associado a um registro de responsavel_unidade
	 * @param idResponsavelUnidade
	 * @return
	 * @throws DAOException
	 */
	public Character findNivelResponsabilidade(int idResponsavelUnidade) throws DAOException {
		String sql =
			" SELECT nivel_responsabilidade FROM COMUM.RESPONSAVEL_UNIDADE  WHERE ID = ? ";

		Object[] lista = {idResponsavelUnidade};

		try{
			String nivel = (String) getJdbcTemplate().queryForObject(sql, lista, String.class);
			
			if (nivel != null)
				return nivel.charAt(0);
			else
				return null;
		}catch (EmptyResultDataAccessException e) {
			/**
			 * Não encontrou resultados.
			 */
			return null;
		}
	}

	/**
	 * Cria um registro na tabela responsavel_unidade, retornando o ID (PK)
	 * @param idUnidade
	 * @param idServidor
	 * @param nivelResponsabilidade
	 * @param inicio
	 * @param fim
	 * @param idUsuario
	 *
	 * @return id -> Identificador da linha criada (PK)
	 * @throws DAOException
	 */
	public int create(int idUnidade, int idServidor, String nivelResponsabilidade, Date inicio, Date fim, int idUsuario) throws DAOException {
		String sql =
			" INSERT INTO COMUM.RESPONSAVEL_UNIDADE (ID, ID_UNIDADE, DATA_INICIO, DATA_FIM, ID_USUARIO, DATA_CADASTRO, ID_SERVIDOR, NIVEL_RESPONSABILIDADE) "
			+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?) ";

		int id = getJdbcTemplate().queryForInt("select nextval ('resp_seq')");

		Object[] lista = {id, idUnidade, inicio, null, idUsuario, new Date(), idServidor, nivelResponsabilidade};

		if (fim != null) lista[3] = fim;

		getJdbcTemplate().update(sql, lista);

		return id;
	}
	
	/**
	 * Retorna as informações de responsabilidades que um servidor teve ou tem por unidades
	 * até o presente momento.
	 *
	 * @param idServidor
	 * @return
	 * @throws DAOException
	 */
	public List<Responsavel> getResponsabilidadesByServidor(int idServidor) throws DAOException {
		return getResponsabilidadesByServidorPeriodoVigencia(idServidor, null);
	}
	
	/**
	 * Retorna as informações de responsabilidades que um servidor teve ou tem por unidades
	 * de acordo com o período de vigência.
	 *
	 * @param idServidor
	 * @param vigente
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> getResponsabilidadesByServidorPeriodoVigencia(int idServidor, Boolean vigente) throws DAOException {
		
		String sql = "SELECT RU.*, U.NOME AS NOME_UNIDADE, U.CODIGO_UNIDADE, S.SIAPE AS MATRICULA,P.NOME AS NOME_SERVIDOR, P.ID_PESSOA, P.EMAIL AS EMAIL_PESSOA " +
					"FROM COMUM.UNIDADE U INNER JOIN COMUM.RESPONSAVEL_UNIDADE RU "+
					"ON (U.ID_UNIDADE = RU.ID_UNIDADE) "+
					"INNER JOIN RH.SERVIDOR S "+
					"ON (S.ID_SERVIDOR = RU.ID_SERVIDOR) "+
					"INNER JOIN comum.PESSOA P "+
					"ON S.ID_PESSOA = P.ID_PESSOA "+
					"WHERE RU.ID_SERVIDOR = ? " +
					"AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL ";
		
		if(vigente != null){
			if(vigente)
				sql += "AND (RU.DATA_FIM IS NULL OR RU.DATA_FIM >= CURRENT_DATE) ";
			else
				sql += "AND RU.DATA_FIM < CURRENT_DATE ";
		}
		
		Object[] lista = {idServidor};

		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, lista, responsavelUnidadeMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Atualização de responsavel_unidade
	 *
	 * @param id
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @param inicio
	 * @param fim
	 * @throws DAOException
	 */
	public void update(int id, int idUnidade, Character nivelResponsabilidade, Date inicio, Date fim) throws DAOException {
		String sql =
			"UPDATE COMUM.RESPONSAVEL_UNIDADE SET ID_UNIDADE = ?, DATA_INICIO = ?, DATA_FIM = ?, NIVEL_RESPONSABILIDADE = ? "
			+ "WHERE ID = ? ";

		Object[] lista = {idUnidade, inicio, null, String.valueOf(nivelResponsabilidade), id};
		if (fim != null) lista[2] = fim;

		getJdbcTemplate().update(sql, lista);
	}

	/**
	 * Retorna o diretor e o vice de acordo com a unidade informada.
	 *
	 * @param idUnidade
	 * @return
	 * @throws DAOException
	 */
	public Collection<HashMap<String, Object>> getDiretoresByUnidade(int idUnidade, Date data) throws DAOException {

		Calendar cal = Calendar.getInstance();
		cal.setTime(data);
		cal.set(Calendar.HOUR_OF_DAY,0);
		cal.set(Calendar.MINUTE,0);
		cal.set(Calendar.SECOND,0);

		data = cal.getTime();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT P.NOME AS NOME, P.EMAIL AS EMAIL " );
		sql.append("FROM COMUM.RESPONSAVEL_UNIDADE RU ");
		sql.append("INNER JOIN COMUM.UNIDADE U ");
		sql.append("ON (U.ID_UNIDADE = RU.ID_UNIDADE) ");
		sql.append("INNER JOIN RH.SERVIDOR S ");
		sql.append("ON (S.ID_SERVIDOR = RU.ID_SERVIDOR) ");
		sql.append("INNER JOIN comum.PESSOA P ");
		sql.append("ON S.ID_PESSOA = P.ID_PESSOA ");
		sql.append("WHERE RU.NIVEL_RESPONSABILIDADE in('C','V') and RU.id_unidade = ? AND RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL and (RU.data_fim is null or RU.data_fim > ? ) " );

		Collection<HashMap<String, Object>> retorno = new ArrayList<HashMap<String,Object>>();
		try{
			PreparedStatement sta = getConnection().prepareStatement(sql.toString());
			sta.setInt(1, idUnidade);
			sta.setTimestamp(2, new Timestamp(data.getTime()));

			ResultSet result = sta.executeQuery();

			while(result.next()){
				HashMap<String, Object> values = new HashMap<String, Object>();
				values.put("nome", result.getString("nome"));
				values.put("email", result.getString("email"));

				retorno.add(values);
			}

			result.close();
			sta.clearBatch();
			sta.close();

	} catch (Exception e) {
		throw new DAOException(e);
	}
		return retorno;

	}

	/**
	 * Inativa a responsabilidade de unidade passada como parâmetro setando a sua 
	 * data de fim para a data passada como parâmetro.  
	 * @param responsavel
	 * @param time
	 */
	public void inativaResponsabilidade(Responsavel responsavel, Date time) {
		getJdbcTemplate(Sistema.COMUM).update("update comum.responsavel_unidade set data_fim = ? where id_responsavel = ?", new Object[] { time, responsavel.getId() });
		if(Sistema.isSipacAtivo())
			getJdbcTemplate(Sistema.SIPAC).update("update comum.responsavel_unidade set data_fim = ? where id_responsavel = ?", new Object[] { time, responsavel.getId() });
		else if(Sistema.isSigrhAtivo())
			getJdbcTemplate(Sistema.SIGRH).update("update comum.responsavel_unidade set data_fim = ? where id_responsavel = ?", new Object[] { time, responsavel.getId() });
		if (Sistema.isSigaaAtivo())
			getJdbcTemplate(Sistema.SIGAA).update("update comum.responsavel_unidade set data_fim = ? where id_responsavel = ?", new Object[] { time, responsavel.getId() });
	}

	/**
	 * Cria uma responsabilidade de unidade para a unidade e servidor passados como parâmetro.
	 * Informa ainda a data de início e de fim da chefia, além do nível de responsabilidade.
	 * Para que o fim da chefia seja indefinido, deve-se passar o parâmetro dataFim null.
	 * @param idUnidade
	 * @param idServidor
	 * @param dataInicio
	 * @param dataFim
	 * @param chefe
	 */
	public void criaResponsabilidade(int idUnidade, int idServidor, Date dataInicio, Date dataFim, char chefe) {
		int idResponsabilidade = getJdbcTemplate(Sistema.COMUM).queryForInt("select nextval('resp_seq')");

		String sql = "insert into comum.responsavel_unidade (id, id_unidade, data_inicio, data_fim, data_cadastro, id_servidor, nivel_responsabilidade) "
			+ "values (?, ?, ?, ?, now(), ?, ?)";

		getJdbcTemplate(Sistema.COMUM).update(sql, new Object[] { idResponsabilidade, idUnidade, dataInicio, dataFim, idServidor, String.valueOf(chefe) });
		if(Sistema.isSipacAtivo())
			getJdbcTemplate(Sistema.SIPAC).update(sql, new Object[] { idResponsabilidade, idUnidade, dataInicio, dataFim, idServidor, String.valueOf(chefe) });
		else if(Sistema.isSigrhAtivo())
			getJdbcTemplate(Sistema.SIGRH).update(sql, new Object[] { idResponsabilidade, idUnidade, dataInicio, dataFim, idServidor, String.valueOf(chefe) });
		if (Sistema.isSigaaAtivo())
			getJdbcTemplate(Sistema.SIGAA).update(sql, new Object[] { idResponsabilidade, idUnidade, dataInicio, dataFim, idServidor, String.valueOf(chefe) });
	}

	/**
	 * Retorna os responsáveis por uma determinada unidade.
	 *
	 * @param idUnidade
	 * @param nivelResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> findResponsaveisModificadosPorPeriodo(Date dataInicio, Date dataTermino, Character origem) throws DAOException {

		String 	sql = "SELECT DISTINCT ru.*, u.nome AS nome_unidade, u.codigo_unidade, s.siape AS matricula, p.nome AS nome_servidor,";
				sql += " p.id_pessoa AS id_pessoa, p.email AS email_pessoa FROM comum.responsavel_unidade ru ";
				sql += " INNER JOIN rh.servidor s ON (s.id_servidor = ru.id_servidor) ";
				sql += " INNER JOIN comum.pessoa p ON (s.id_pessoa = p.id_pessoa) ";
				sql += " INNER JOIN comum.unidade u ON (u.id_unidade = ru.id_unidade) ";
				sql += " WHERE RU.ID_REGISTRO_ENTRADA_EXCLUSAO IS NULL AND ((ru.data_cadastro BETWEEN ? AND ?) OR (ru.ultima_atualizacao BETWEEN ? AND ? )) ";

				if(!isEmpty(origem))
					sql += " AND ru.origem = ? ";

				sql += " ORDER BY ultima_atualizacao ASC ";

		ArrayList<Object> parametros = new ArrayList<Object>();
		parametros.add(dataInicio);
		parametros.add(dataTermino);
		parametros.add(dataInicio);
		parametros.add(dataTermino);

		if(!isEmpty(origem))
			parametros.add(String.valueOf(origem));

		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, parametros.toArray(), responsavelUnidadeMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}catch (Exception e){
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna os responsáveis atuais de acordo com as unidades e níveis de responsabilidade informados.
	 * @param idUnidades
	 * @param niveisResponsabilidade
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<Responsavel> findResponsaveisAtuais(int[] idUnidades, Character... niveisResponsabilidade) throws DAOException {

		String 	sql = " SELECT DISTINCT ru.*, u.id_unidade, u.nome as nome_unidade, u.sigla, u.codigo_unidade, "; 
				sql += " s.id_servidor, s.siape as matricula, p.id_pessoa, p.nome as nome_servidor, p.email as email_pessoa ";
				sql += " FROM comum.responsavel_unidade ru ";
				sql += " INNER JOIN rh.servidor s ON (s.id_servidor = ru.id_servidor) ";
				sql += " INNER JOIN comum.pessoa p ON (s.id_pessoa = p.id_pessoa) ";
				sql += " INNER JOIN comum.unidade u ON (u.id_unidade = ru.id_unidade) ";
				sql += " WHERE ((current_date BETWEEN ru.data_inicio AND data_fim) OR (current_date >= data_inicio AND data_fim IS NULL)) ";
				sql += " AND ru.id_unidade IN "+ UFRNUtils.gerarStringIn(idUnidades);
				
				if(!isEmpty(niveisResponsabilidade))
					sql += " AND ru.nivel_responsabilidade IN "+ UFRNUtils.gerarStringIn(niveisResponsabilidade);
		
		try{
			return getJdbcTemplate(Sistema.COMUM).query(sql, responsavelUnidadeMapper);
		}catch (EmptyResultDataAccessException e) {
			return null;
		}catch (Exception e){
			throw new DAOException(e);
		}
	}
}