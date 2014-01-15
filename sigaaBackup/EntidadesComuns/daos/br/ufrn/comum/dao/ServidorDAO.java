/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Criado em: 12/12/2009
 */
package br.ufrn.comum.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.arq.dao.GenericSharedDBDao;
import br.ufrn.arq.dominio.ConstantesParametroGeral;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.PessoaGeral;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;
import br.ufrn.rh.dominio.AtividadeServidor;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Cargo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Designacao;
import br.ufrn.rh.dominio.Escolaridade;
import br.ufrn.rh.dominio.Ferias;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.rh.dominio.NivelDesignacao;
import br.ufrn.rh.dominio.Servidor;
import br.ufrn.rh.dominio.Situacao;

/**
 * DAO para busca de servidores.
 *
 * @author Adriana Alves
 * @author David Pereira
 *
 */
public class ServidorDAO extends GenericSharedDBDao {

	private static final String SQL_SERVIDOR = "SELECT DISTINCT s.id_servidor, s.siape, s.nome_identificacao, " +
															"p.id_pessoa, p.cpf_cnpj, p.nome, p.email, p.sexo, " +
															"u.id_unidade, u.codigo_unidade, u.nome as unidade, " +
															"s.id_unidade_lotacao, ul.codigo_unidade as codigo_lotacao, ul.nome as unidade_lotacao, " +
															"c.id_categoria, c.descricao as categoria, " +
															"ss.id_situacao, ss.descricao as situacao, " +
															"e.id_escolaridade, e.descricao as escolaridade, " +
															"f.id_formacao, f.denominacao as formacao, " +
															"a.id_ativo, a.descricao as ativo " +
												"FROM rh.servidor s " +
													"LEFT JOIN comum.pessoa p USING (id_pessoa) " +
													"LEFT JOIN comum.unidade u on u.id_unidade = s.id_unidade " +
													"LEFT JOIN comum.unidade ul ON s.id_unidade_lotacao = ul.id_unidade " +
													"LEFT JOIN comum.responsavel_unidade ru ON ru.id_servidor = s.id_servidor " +
													"LEFT JOIN rh.categoria c USING (id_categoria) " +
													"LEFT JOIN rh.situacao_servidor ss USING (id_situacao) " +
													"LEFT JOIN rh.escolaridade e USING (id_escolaridade) " +
													"LEFT JOIN rh.formacao f USING (id_formacao) " +
													"LEFT JOIN rh.ativo a USING (id_ativo) " +
												"WHERE 1 = 1 ";

	private static final RowMapper SERVIDOR_MAPPER = new RowMapper() {
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Servidor s = new Servidor();

			s.setId(rs.getInt("id_servidor"));
			s.setSiape(rs.getInt(("siape")));
			s.setNomeIdentificacao(rs.getString("nome_identificacao"));

			s.setPessoa(new PessoaGeral(rs.getInt("id_pessoa")));
			s.getPessoa().setCpf_cnpj(rs.getLong("cpf_cnpj"));
			s.getPessoa().setNome(rs.getString("nome"));
			s.getPessoa().setEmail(rs.getString("email"));
			s.getPessoa().setFormacao(new Formacao(rs.getInt("id_formacao")));
			s.getPessoa().getFormacao().setDenominacao(rs.getString("formacao"));
			s.getPessoa().setSexo(rs.getString("sexo").charAt(0));

			s.setUnidade(new UnidadeGeral(rs.getInt("id_unidade")));
			s.getUnidade().setCodigo(rs.getLong("codigo_unidade"));
			s.getUnidade().setNome(rs.getString("unidade"));
			
			s.setUnidadeLotacao(new UnidadeGeral(rs.getInt("id_unidade_lotacao")));
			s.getUnidadeLotacao().setCodigo(rs.getLong("codigo_lotacao"));
			s.getUnidadeLotacao().setNome(rs.getString("unidade_lotacao"));

			s.setCategoria(new Categoria(rs.getInt("id_categoria")));
			s.getCategoria().setDescricao(rs.getString("categoria"));

			s.setSituacao(new Situacao(rs.getInt("id_situacao")));
			s.getSituacao().setDescricao(rs.getString("situacao"));

			s.setEscolaridade(new Escolaridade(rs.getInt("id_escolaridade"), rs.getString("escolaridade")));

			s.setFormacao(s.getPessoa().getFormacao());

			s.setAtivo(new Ativo(rs.getInt("id_ativo")));
			s.getAtivo().setDescricao(rs.getString("ativo"));

			return s;
		}
	};

	private static final RowMapper SERVIDOR_DESIGNACAO_MAPPER = new RowMapper() {
		@Override
		public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			Servidor s = new Servidor();

			s.setId(rs.getInt("id_servidor"));
			s.setSiape(rs.getInt(("siape")));
			s.setMatriculaInterna(rs.getInt("matricula_interna"));

			s.setPessoa(new PessoaGeral(rs.getInt("id_pessoa")));
			s.getPessoa().setCpf_cnpj(rs.getLong("cpf_cnpj"));
			s.getPessoa().setNome(rs.getString("nome"));
			s.getPessoa().setEmail(rs.getString("email"));
			s.getPessoa().setFormacao(new Formacao(rs.getInt("id_formacao")));
			s.getPessoa().getFormacao().setDenominacao(rs.getString("formacao"));
			s.getPessoa().setSexo(rs.getString("sexo").charAt(0));

			s.setUnidade(new UnidadeGeral(rs.getInt("id_unidade")));
			s.getUnidade().setCodigo(rs.getLong("codigo_unidade"));
			s.getUnidade().setNome(rs.getString("unidade"));

			s.setUnidadeLotacao(new UnidadeGeral(rs.getInt("id_unidade_lotacao")));
			s.getUnidadeLotacao().setCodigo(rs.getLong("codigo_lotacao"));
			s.getUnidadeLotacao().setNome(rs.getString("unidade_lotacao"));
			
			s.setCategoria(new Categoria(rs.getInt("id_categoria")));
			s.getCategoria().setDescricao(rs.getString("categoria"));

			s.setSituacao(new Situacao(rs.getInt("id_situacao")));
			s.getSituacao().setDescricao(rs.getString("situacao"));

			s.setEscolaridade(new Escolaridade(rs.getInt("id_escolaridade"), rs.getString("escolaridade")));

			s.setFormacao(s.getPessoa().getFormacao());

			s.setAtivo(new Ativo(rs.getInt("id_ativo")));
			s.getAtivo().setDescricao(rs.getString("ativo"));

			return s;
		}
	};

	/**
	 * Busca servidores por nome.
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findServidores(String nome) {
		return getJdbcTemplate().query(SQL_SERVIDOR + " and upper(p.nome) like upper(?) order by p.nome asc",
				new Object[] { nome + "%" }, SERVIDOR_MAPPER);
	}

	/**
	 * Busca servidores por siape, nome, CPF ou lotação.
	 */
	public List<Servidor> findServidores(Integer siape, String nome, String cpf, UnidadeGeral lotacao) {
		return findServidores(siape, nome, cpf, lotacao, true);
	}

	/**
	 * Busca servidores por siape, nome, CPF, exercício, lotação e ativos ou não.
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findServidores(Integer siape, String nome, String cpf, UnidadeGeral lotacao, Boolean apenasAtivos) {
		StringBuilder sql = new StringBuilder(SQL_SERVIDOR);
		List<Object> params = new ArrayList<Object>();

		if (siape != null) {
			sql.append(" and s.siape = ?");
			params.add(siape);
		}

		if (nome != null) {
			sql.append(" and upper(p.nome)like upper(?)");
			params.add(nome + "%");
		}

		if (!isEmpty(cpf)) {
			sql.append(" and p.cpf_cnpj = ?");
			params.add(Formatador.getInstance().parseCPFCNPJ(cpf));
		}

		if (lotacao != null && lotacao.getId() != 0) {
			sql.append(" and s.id_unidade = ?");
			params.add(lotacao.getId());
		}

		if(apenasAtivos) {
			sql.append( " and s.id_ativo =  " + Ativo.SERVIDOR_ATIVO);
		}

		sql.append(" order by p.nome asc");
		return getJdbcTemplate().query(sql.toString(), params.toArray(), SERVIDOR_MAPPER);
	}

	/**
	 * Busca servidores por: siape, matricula interna, CPF, nome ou qualquer parte do nome, apenas ativos,
	 * apenas docentes, unidade e níveis de responsabilidade.
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findServidores(Integer siapeOrMatriculaInterna, Long cpf, String nome, Boolean qualquerParteDoNome, Boolean apenasAtivos, Boolean apenasDocentes,int[] ativos, int[] unidades, Character[] niveisResponsabilidade, int[] categorias, int[] situacoes) {
		StringBuilder sql = new StringBuilder(SQL_SERVIDOR);
		List<Object> params = new ArrayList<Object>();
		boolean consultaMatriculaInterna =
			ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.UTILIZA_CONCEITO_MATRICULA_INTERNA);

		sql.append(" AND ss.id_situacao <> "+Situacao.BENEFICIARIO_PENSAO);
		if (siapeOrMatriculaInterna != null) {
			sql.append(" and (s.siape = ?");
			params.add(siapeOrMatriculaInterna);
			if(consultaMatriculaInterna){
				sql.append(" or s.matricula_interna = ?");
				params.add(siapeOrMatriculaInterna);
			}
			sql.append(")");
		}

		if (cpf != null) {
			sql.append(" and p.cpf_cnpj = ?");
			params.add(cpf);
		}

		if (nome != null) {
			nome = StringUtils.toAscii( nome );
			sql.append(" and (p.nome_ascii ilike ?");
			if(qualquerParteDoNome != null && qualquerParteDoNome)
				params.add("%"+ nome +"%");
			else
				params.add(nome + "%");

			sql.append(" or s.nome_identificacao ilike ?");
			if(qualquerParteDoNome != null && qualquerParteDoNome)
				params.add("%"+ nome +"%");
			else
				params.add(nome + "%");
			sql.append (")");

		}

		if (!isEmpty(ativos)){
			sql.append(" and s.id_ativo IN " + UFRNUtils.gerarStringIn(ativos) );
		}

		if (!isEmpty(unidades)){
			sql.append(" and (" );
			for (int i = 0; i < unidades.length; i++) {
				sql.append(" u.hierarquia_organizacional like '%." +  unidades[i] + ".%'");
				if(i < (unidades.length - 1))
					sql.append(" or " );
			}
			sql.append(" ) " );
		}

		if(apenasAtivos) {
			sql.append(" and s.id_ativo in  " + UFRNUtils.gerarStringIn(new int[]{Ativo.SERVIDOR_ATIVO, Ativo.CEDIDO}));
			sql.append(" AND s.data_desligamento IS NULL ");
		}

		if(apenasDocentes) {
			sql.append(" and s.id_categoria =  " + Categoria.DOCENTE);
		}

		if (!isEmpty(niveisResponsabilidade)){
			sql.append(" AND ru.nivel_responsabilidade IN " +  UFRNUtils.gerarStringIn(niveisResponsabilidade));
			sql.append(" AND ru.data_inicio <= current_date AND (ru.data_fim IS NULL OR ru.data_fim >= current_date) ");
		}

		if (!isEmpty(categorias) && !apenasDocentes) {
			sql.append(" and s.id_categoria in " + UFRNUtils.gerarStringIn(categorias));
		}

		if (!isEmpty(situacoes)) {
			sql.append(" and ss.id_situacao in " + UFRNUtils.gerarStringIn(situacoes));
		}

		sql.append(" order by p.nome asc");
		return getJdbcTemplate().query(sql.toString(), params.toArray(), SERVIDOR_MAPPER);
	}

	/**
	 * Localiza servidores com designação ativa para as unidades informadas
	 * , ou sem designações em outras unidades e lotado em uma das unidades informadas.
	 */
	@SuppressWarnings("unchecked")
	public List<Servidor> findServidoresDesignacoes(Integer siapeOrMatriculaInterna, Long cpf, String nome, Boolean qualquerParteDoNome, Boolean apenasAtivos,Boolean apenasDocentes,
			int[] ativos, int[] unidades, int[] categorias, int[] situacoes,
			boolean permiteHomologacaoHierarquia, boolean permiteHomologacaoUnidadeResponsabilidade) throws DAOException {
		try{

			List<Object> params = new ArrayList<Object>();
			StringBuilder sql = configSqlServidorDesignacoes(params, siapeOrMatriculaInterna, cpf, nome, qualquerParteDoNome, apenasAtivos, apenasDocentes, ativos, categorias, situacoes);

			if (!isEmpty(unidades)){
				//para cada unidade de responsabilização do usuário (chefe)
				for (int i = 0; i < unidades.length; i++) {

					if(i==0) {
						sql.append(" and (");
					}

					//a unidade de lotação do servidor está na hierarquia da unidade de responsabilização do usuário (chefe)
					if(permiteHomologacaoHierarquia)
						sql .append(" (ue.hierarquia_organizacional like '%." + unidades[i] + ".%' ) ");
					else //ou é exatamente a unidade de chefia
						sql .append(" (ue.id_unidade = " + unidades[i] + " ) ");

					//se o servidor não tem designação ativa nesta unidade OU tem designação que não é do nível CD OU tem designação CD SEM responsabilidade de unidade
					if(permiteHomologacaoUnidadeResponsabilidade)
						sql.append(" and (d.id_designacao is null or (gd.tipo_designacao != '"+NivelDesignacao.CD+"' or urd.id_unidade is null)) ");

					//ou o servidor tem designação em unidade na hierarquia da unidade de responsabilização do usuário (chefe) COM nível CD E com responsabilidade de unidade
					if(permiteHomologacaoUnidadeResponsabilidade) {
						sql.append(" or (");
						if (permiteHomologacaoHierarquia) {
							sql.append("ud.hierarquia_organizacional like '%." + unidades[i] + ".%'");
						} else {
							sql.append("ud.id_unidade = " + unidades[i]);
						}
						sql.append(" and gd.tipo_designacao = '"+NivelDesignacao.CD+"' and urd.id_unidade is not null) ");
					}
					if (i != unidades.length - 1) {
						sql.append(" or ");
					} else {
						sql.append(")");
					}
				}
			}

			sql.append(" order by p.nome asc");
			return getJdbcTemplate().query(sql.toString(), params.toArray(), SERVIDOR_DESIGNACAO_MAPPER);
		}catch(Exception e){
			throw new DAOException(e);
		}
	}

	/**
	 * Configura o SQL para consultas de servidores com designações, setando os parâmetros de acordo com
	 * verificações.
	 */
	public StringBuilder configSqlServidorDesignacoes(List<Object> params, Integer siapeOrMatriculaInterna, Long cpf, String nome, Boolean qualquerParteDoNome, Boolean apenasAtivos, Boolean apenasDocentes, int[] ativos, int[] categorias, int[] situacoes) {

		StringBuilder sql = new StringBuilder();

		sql	.append("select distinct s.id_servidor, s.siape, s.matricula_interna, p.id_pessoa, p.cpf_cnpj, p.sexo, p.nome, p.email, ue.id_unidade, ue.codigo_unidade, ue.nome as unidade, ")
		    .append("s.id_unidade_lotacao, ul.codigo_unidade as codigo_lotacao, ul.nome as unidade_lotacao, ")
			.append("c.id_categoria, c.descricao as categoria, ss.id_situacao, ss.descricao as situacao, e.id_escolaridade, " )
			.append("e.descricao as escolaridade, f.id_formacao, f.denominacao as formacao, a.id_ativo, a.descricao as ativo from " )
			.append("rh.servidor s " )
			.append("left join comum.pessoa p using (id_pessoa) ")
			//ul = unidade de lotação
			.append("left join comum.unidade ul on(s.id_unidade_lotacao = ul.id_unidade) ")
			//ue = unidade de exercício
			.append("left join comum.unidade ue on(ue.id_unidade = s.id_unidade) ")
			.append("left join rh.categoria c using (id_categoria) ")
			.append("left join rh.situacao_servidor ss using (id_situacao) ")
			.append("left join rh.escolaridade e using (id_escolaridade) ")
			.append("left join rh.formacao f using (id_formacao) ")
			.append("left join rh.ativo a using (id_ativo) ")
			.append(" left join rh.designacao d on(d.id_servidor = s.id_servidor and (d.fim > ?  or d.fim is NULL)) ")
			.append(" left join funcional.nivel_designacao nd on(d.id_nivel_designacao = nd.id_nivel_designacao) ")
			.append(" left join funcional.grupo_nivel_designacao gd on(gd.id_grupo_nivel_designacao = nd.id_grupo_nivel_designacao) ")
			//ud = unidade de designação
			.append(" left join comum.unidade ud on(d.id_unidade = ud.id_unidade)")
			//urd = unidade de responsabilização
			.append(" left join comum.responsavel_unidade urd on (urd.id_servidor = s.id_servidor and ud.id_unidade = urd.id_unidade and (urd.data_fim > now() or urd.data_fim is NULL)) ")
			.append(" WHERE ss.id_situacao <> "+Situacao.BENEFICIARIO_PENSAO+" ");

		params.add(CalendarUtils.descartarHoras(new Date()));

		if (siapeOrMatriculaInterna != null) {
			sql.append(" and (s.siape = ?");
			params.add(siapeOrMatriculaInterna);

			boolean consultaMatriculaInterna = ParametroHelper.getInstance().getParametroBoolean(ConstantesParametroGeral.UTILIZA_CONCEITO_MATRICULA_INTERNA);
			if(consultaMatriculaInterna){
				sql.append(" or s.matricula_interna = ?");
				params.add(siapeOrMatriculaInterna);
			}
			sql.append(")");
		}

		if (cpf != null) {
			sql.append(" and p.cpf_cnpj = ?");
			params.add(cpf);
		}

		if (nome != null) {
			sql.append(" and p.nome ilike ?");
			if(qualquerParteDoNome != null && qualquerParteDoNome)
				params.add("%"+ nome +"%");
			else
				params.add(nome + "%");
		}

		if (!isEmpty(ativos)) {
			sql.append(" and a.id_ativo in " +  UFRNUtils.gerarStringIn(ativos));
		}

		if (apenasDocentes) {
			sql.append(" and s.id_categoria =  " + Categoria.DOCENTE);
		}

		if (!isEmpty(categorias) && !apenasDocentes) {
			sql.append(" and s.id_categoria in " + UFRNUtils.gerarStringIn(categorias));
		}

		if (!isEmpty(situacoes)) {
			sql.append(" and ss.id_situacao in " + UFRNUtils.gerarStringIn(situacoes));
		}

		if(apenasAtivos) {
			sql.append(" and s.id_ativo in  " + UFRNUtils.gerarStringIn(new int[]{Ativo.SERVIDOR_ATIVO, Ativo.CEDIDO}));
			sql.append(" AND s.data_desligamento IS NULL ");
		}

		return sql;
	}

	/**
	 * Busca os dados de um servidor de acordo com o seu id.
	 */
	public Servidor findByPrimaryKey(int id) {
		return (Servidor) getJdbcTemplate().queryForObject(
				"SELECT "
				+ "s.id_servidor, s.siape, s.matricula_interna, s.dedicacao_exclusiva, s.regime_trabalho, p.id_pessoa, "
				+ "p.cpf_cnpj, p.nome, p.sexo, p.data_nascimento, p.endereco, p.bairro, p.cep, p.email, p.cidade, p.uf, p.telefone, p.fax, u.id_unidade, u.codigo_unidade, "
				+ "u.nome as unidade, s.id_unidade_lotacao, ul.codigo_unidade as codigo_lotacao, ul.nome as unidade_lotacao, c.id_categoria, c.descricao as categoria, ss.id_situacao, ss.descricao as situacao, e.id_escolaridade, "
				+ "e.descricao as escolaridade, f.id_formacao, f.denominacao as formacao, a.id_ativo, a.descricao as ativo, "
				+ "ativ.id_atividade, ativ.descricao as atividade, ca.id as id_cargo, ca.denominacao as cargo, nome_identificacao "
				+ "FROM "
				+ "rh.servidor s left join comum.pessoa p using (id_pessoa) left join comum.unidade u on(s.id_unidade = u.id_unidade) left join comum.unidade ul on(s.id_unidade_lotacao = ul.id_unidade) "
				+ "left join rh.categoria c using (id_categoria) "
				+ "left join rh.situacao_servidor ss using (id_situacao) left join rh.escolaridade e using (id_escolaridade) "
				+ "left join rh.formacao f using (id_formacao) left join rh.ativo a using (id_ativo) left join rh.atividade ativ using (id_atividade) "
				+ "left join rh.cargo ca on (s.id_cargo = ca.id) where s.id_servidor = ?", new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Servidor s = (Servidor) SERVIDOR_MAPPER.mapRow(rs, rowNum);
				s.setAtividade(new AtividadeServidor());
				s.getAtividade().setId(rs.getInt("id_atividade"));
				s.getAtividade().setDescricao(rs.getString("atividade"));
				s.setDedicacaoExclusiva(rs.getBoolean("dedicacao_exclusiva"));
				s.setRegimeTrabalho(rs.getInt("regime_trabalho"));
				s.setCargo(new Cargo(rs.getInt("id_cargo")));
				s.getCargo().setDenominacao(rs.getString("cargo"));
				s.getPessoa().setSexo(rs.getString("sexo").charAt(0));
				s.getPessoa().setDataNascimento(rs.getDate("data_nascimento"));
				s.getPessoa().setEndereco(rs.getString("endereco"));
				s.getPessoa().setBairro(rs.getString("bairro"));
				s.getPessoa().setCEP(rs.getString("cep"));
				s.getPessoa().setEmail(rs.getString("email"));
				s.getPessoa().setCidade(rs.getString("cidade"));
				s.getPessoa().setUF(rs.getString("uf"));
				s.getPessoa().setTelefone(rs.getString("telefone"));
				s.getPessoa().setFax(rs.getString("fax"));
				return s;
			}
		});
	}

	/**
	 * Busca as designações de um servidor.
	 */
	@SuppressWarnings("unchecked")
	public List<Designacao> findDesignacoesByServidor(int id) {
		 return getJdbcTemplate().query("select d.inicio, d.fim, d.gerencia, d.id_atividade as ativ, a.codigo_rh, a.descricao as atividade, u.nome as nome_unidade, d.id_unidade "  
				 + "from rh.designacao d left join rh.atividade a using (id_atividade) inner join comum.unidade u on (d.id_unidade = u.id_unidade) where id_servidor = ?", new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Designacao d = new Designacao();
				d.setInicio(rs.getDate("inicio"));
				d.setFim(rs.getDate("fim"));
				d.setGerencia(rs.getString("gerencia"));
				d.setAtividade(new AtividadeServidor());
				d.getAtividade().setId(rs.getInt("ativ"));
				d.getAtividade().setCodigoRH(rs.getInt("codigo_rh"));
				d.getAtividade().setDescricao(rs.getString("atividade"));
				d.setUnidade(rs.getString("nome_unidade"));
				d.setIdUnidade(rs.getInt("id_unidade"));
				return d;
			}
		});
	}

	/**
	 * Busca as designações que ainda não expiraram de um servidor.
	 */
	@SuppressWarnings("unchecked")
	public List<Designacao> findDesignacoesAtivasByServidor(int id) {
		return getJdbcTemplate(Sistema.SIGADMIN).query("select d.id_designacao, d.inicio, d.fim, a.codigo_rh, a.descricao as atividade, d.nome_unidade, d.id_unidade "
				+ "from rh.designacao d left join rh.atividade a using (id_atividade) where id_servidor = ? and (d.fim is null or d.fim >= current_date)", new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Designacao d = new Designacao();
				d.setId(rs.getInt("id_designacao"));
				d.setInicio(rs.getDate("inicio"));
				d.setFim(rs.getDate("fim"));
				d.setAtividade(new AtividadeServidor());
				d.getAtividade().setCodigoRH(rs.getInt("codigo_rh"));
				d.getAtividade().setDescricao(rs.getString("atividade"));
				d.setIdUnidade(rs.getInt("id_unidade"));
				d.setUnidade(rs.getString("nome_unidade"));
				return d;
			}
		});
	}

	/**
	 * Busca as férias de um servidor.
	 */
	@SuppressWarnings("unchecked")
	public List<Ferias> findFeriasByServidor(int id) {
		return getJdbcTemplate().query("select * from rh.servidor_ferias where id_servidor = ?", new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				Ferias f = new Ferias();
				f.setDataInicio(rs.getDate("inicio"));
				f.setDataFim(rs.getDate("fim"));
				return f;
			}
		});
	}

	/**
	 * Busca os usuários de um servidor.
	 * @param id
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuariosByServidor(int id) {
		String sql = "select * from comum.usuario where id_pessoa = (select id_pessoa from rh.servidor where id_servidor = ?)";
		return getJdbcTemplate().query(sql, new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsuarioGeral ug = new UsuarioGeral();
				ug.setLogin(rs.getString("login"));
				ug.setInativo(rs.getBoolean("inativo"));
				return ug;
			}
		});
	}

	/**
	 * Dada a matrícula SIAPE do servidor, retorna o seu ID.
	 */
	public Integer findIdServidorAtivoBySiape(int siape) {
		try {
			return getJdbcTemplate(Sistema.COMUM).queryForInt("select s.id_servidor from rh.servidor s where s.siape = ? and s.id_ativo = ? and s.data_desligamento is null",
					new Object[] { siape, Ativo.SERVIDOR_ATIVO });
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Busca os usuários ativos de um servidor.
	 */
	@SuppressWarnings("unchecked")
	public List<UsuarioGeral> findUsuariosAtivosByServidor(int id) {
		String sql = "select u.*, p.nome from comum.usuario u " +
					 "join comum.pessoa p on (u.id_pessoa = p.id_pessoa) " +
					 "where inativo = false and u.id_pessoa = (select id_pessoa from rh.servidor where id_servidor = ?)";
		return getJdbcTemplate(Sistema.COMUM).query(sql, new Object[] { id }, new RowMapper() {
			@Override
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				UsuarioGeral ug = new UsuarioGeral();
				ug.setId(rs.getInt("id_usuario"));
				ug.setLogin(rs.getString("login"));
				ug.setEmail(rs.getString("email"));
				ug.getPessoa().setNome(rs.getString("nome"));
				return ug;
			}
		});
	}

	/**
	 * Verifica se o servidor já é válido
	 */
	public Boolean isServidorValido(int idServidor) {

		String sql =
			"SELECT count(s.id_servidor) FROM rh.servidor s  where id_servidor = ? " ;

		Object[] lista = {idServidor};

		try{
			Integer total = getJdbcTemplate().queryForInt(sql, lista);
			return (total > 0)?true:false;
		}catch (EmptyResultDataAccessException e) {
			return false;
		}

	}

	/**
	 * Dada a matrícula SIAPE, retorna as informações do servidor.
	 */
	public Servidor findAtivoByMatricula(int siape) {
		try {
			return (Servidor) getJdbcTemplate().queryForObject(SQL_SERVIDOR + " and s.siape = ?", new Object[] { siape }, SERVIDOR_MAPPER);
		} catch(EmptyResultDataAccessException e) {
			return null;
		}
	}

	/**
	 * Dado o CPF do servidor, retorna as informações do servidor. Pode buscar somente
	 * nos servidores ativos se o parâmetro somenteAtivo for true.
	 */
	public Servidor findByCPF(long cpf, boolean somenteAtivo) {
		String sql =
				SQL_SERVIDOR +
				" AND p.cpf_cnpj = ? ";

		if (somenteAtivo) {
			sql += " AND a.id_ativo = " + Ativo.SERVIDOR_ATIVO + " ";
		}
		
		sql += BDUtils.limit(1);
		
		try {
			return (Servidor) getJdbcTemplate().queryForObject(sql, new Object[] { cpf }, SERVIDOR_MAPPER);
		} catch(IncorrectResultSizeDataAccessException e) {
			return null;
		}
	}

}
