/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática 
 * Diretoria de Sistemas
 * 
 * Criado em: 07/11/2006
 */
package br.ufrn.comum.sincronizacao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementSetter;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import br.ufrn.arq.dao.Database;
import br.ufrn.arq.dominio.Comando;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.seguranca.log.LogJdbcUpdate;
import br.ufrn.arq.seguranca.log.LogProcessorDelegate;
import br.ufrn.arq.util.ReflectionUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.comum.dominio.UnidadeGeral;
import br.ufrn.comum.dominio.UsuarioGeral;

/**
 * Classe para sincronizar o cadastro de unidades feitas no SigAdmin para os
 * bancos acadêmico e administrativo. As unidades são replicadas por três bancos: sistemas_comum, administrativo e sigaa.
 *
 * @author David Ricardo
 * @author Gleydson Lima
 *
 */
public class SincronizadorUnidades {

	/** Conexão do banco com o qual se irá sincronizar as unidades */
	private JdbcTemplate jt;
	private Comando comando;
	private UsuarioGeral usuario;
	private int sistema;
	
	private SincronizadorUnidades(DataSource ds) {
		if (ds != null)
			this.jt = new JdbcTemplate(ds);
	}
	
	private SincronizadorUnidades(Connection con) {
		if (con != null)
			this.jt = new JdbcTemplate(new SingleConnectionDataSource(con, true));
	}
	
	private SincronizadorUnidades(JdbcTemplate jt) {
		if (jt != null)
			this.jt = jt;
	}

	/**
	 * Remove a unidade passada como parâmetro do banco de dados.
	 * @param u
	 * @throws Exception
	 */
	public void removerUnidade(UnidadeGeral u) {
		String sql = "delete from comum.unidade where id_unidade=?";
		Object[] params = new Object[] { u.getId() }; 
		jt.update(sql, params);
		log(sql, params);
	}

	public static int getNextIdUnidade() {
		return new JdbcTemplate(Database.getInstance().getComumDs()).queryForInt("select nextval('comum.unidade_seq')");
	}
	
	/**
	 * Atualiza as hierarquias orçamentária e organizacional da unidade.
	 * @param u
	 */
	public void sincronizarHierarquias(final UnidadeGeral u) {
		jt.update("update comum.unidade set hierarquia = ?, hierarquia_organizacional = ? where id_unidade = ?", new Object[] { u.getHierarquia(), u.getHierarquiaOrganizacional(), u.getId() });
	}
	
	/**
	 * Sincroniza as informações de cadastro e atualização de unidade entre bancos administrativo,
	 * acadêmico e comum.
	 * 
	 * @param u
	 * @param 
	 * @throws Exception
	 */
	public void sincronizarUnidade(final UnidadeGeral u) {
		StringBuilder sqlBuilder = new StringBuilder();
			
		if (hasUnidade(u)) {
			//Atualização
			sqlBuilder.append("update comum.unidade set  ");
			sqlBuilder.append("codigo_siorg = ?, ");
			sqlBuilder.append("avaliacao = ?, ");
			sqlBuilder.append("categoria = ?, ");
			sqlBuilder.append("cnpj = ?, ");
			sqlBuilder.append("codigo_gestao_siafi = ?, ");
			sqlBuilder.append("codigo_siapecad = ?, ");
			sqlBuilder.append("codigo_unidade = ?, ");
			sqlBuilder.append("codigo_unidade_gestora_siafi = ?, ");
			sqlBuilder.append("compradora = ?, ");
			sqlBuilder.append("compradora_engenharia = ?, ");
			sqlBuilder.append("data_cadastro = ?, ");
			sqlBuilder.append("data_criacao = ?, ");
			sqlBuilder.append("data_extincao = ?, ");
			sqlBuilder.append("email = ?, ");
			sqlBuilder.append("funcao_remunerada = ?, ");
			sqlBuilder.append("gestora_frequencia = ?, ");
			sqlBuilder.append("hierarquia = ?, ");
			sqlBuilder.append("hierarquia_organizacional = ?, ");
			sqlBuilder.append("id_ambiente_organizacional = ?, ");
			sqlBuilder.append("id_area_atuacao = ?, ");
			sqlBuilder.append("id_classificacao_unidade = ?, ");
			sqlBuilder.append("id_gestora = ?, ");
			sqlBuilder.append("id_gestora_academica = ?, ");
			sqlBuilder.append("id_nivel_organizacional = ?, ");
			sqlBuilder.append("id_responsavel = ?, ");
			sqlBuilder.append("id_tipo_organizacional = ?, ");
			sqlBuilder.append("id_unid_resp_org = ?, ");
			sqlBuilder.append("id_usuario_cadastro = ?, ");
			sqlBuilder.append("metas = ?, ");
			sqlBuilder.append("nome = ?, ");
			sqlBuilder.append("nome_ascii = ?, ");
			sqlBuilder.append("nome_capa = ?, ");
			sqlBuilder.append("organizacional = ?, ");
			sqlBuilder.append("prazo_envio_bolsa_fim = ?, ");
			sqlBuilder.append("prazo_envio_bolsa_inicio = ?, ");
			sqlBuilder.append("presidente_comissao = ?, ");
			sqlBuilder.append("sequencia_modalidade_compra = ?, ");
			sqlBuilder.append("sigla = ?, ");
			sqlBuilder.append("sigla_academica = ?, ");
			sqlBuilder.append("sipac = ?, ");
			sqlBuilder.append("submete_proposta_extensao = ?, ");
			sqlBuilder.append("telefones = ?, ");
			sqlBuilder.append("template_parecer_dl = ?, ");
			sqlBuilder.append("tipo = ?, ");
			sqlBuilder.append("tipo_academica = ?, ");
			sqlBuilder.append("tipo_funcao_remunerada = ?, ");
			sqlBuilder.append("unidade_academica = ?, ");
			sqlBuilder.append("unidade_orcamentaria = ?, ");
			sqlBuilder.append("unidade_responsavel = ?, ");
			sqlBuilder.append("permite_gestao_centros_gestora_superior = ?, ");
			sqlBuilder.append("cep = ?, ");
			sqlBuilder.append("endereco = ?, ");
			sqlBuilder.append("uf = ?, ");
			sqlBuilder.append("protocolizadora = ?, ");
			sqlBuilder.append("radical = ?, ");
			sqlBuilder.append("id_municipio = ? ");
			
			sqlBuilder.append(" where id_unidade = ? ");
				
		}else{
			//Cadastro
			sqlBuilder.append("insert into comum.unidade ( ");
			sqlBuilder.append("codigo_siorg, ");
			sqlBuilder.append("avaliacao, ");
			sqlBuilder.append("categoria, ");
			sqlBuilder.append("cnpj, ");
			sqlBuilder.append("codigo_gestao_siafi, ");
			sqlBuilder.append("codigo_siapecad, ");
			sqlBuilder.append("codigo_unidade, ");
			sqlBuilder.append("codigo_unidade_gestora_siafi, ");
			sqlBuilder.append("compradora, ");
			sqlBuilder.append("compradora_engenharia, ");
			sqlBuilder.append("data_cadastro, ");
			sqlBuilder.append("data_criacao, ");
			sqlBuilder.append("data_extincao, ");
			sqlBuilder.append("email, ");
			sqlBuilder.append("funcao_remunerada, ");
			sqlBuilder.append("gestora_frequencia, ");
			sqlBuilder.append("hierarquia, ");
			sqlBuilder.append("hierarquia_organizacional, ");
			sqlBuilder.append("id_ambiente_organizacional, ");
			sqlBuilder.append("id_area_atuacao, ");
			sqlBuilder.append("id_classificacao_unidade, ");
			sqlBuilder.append("id_gestora, ");
			sqlBuilder.append("id_gestora_academica, ");
			sqlBuilder.append("id_nivel_organizacional, ");
			sqlBuilder.append("id_responsavel, ");
			sqlBuilder.append("id_tipo_organizacional, ");
			sqlBuilder.append("id_unid_resp_org, ");
			sqlBuilder.append("id_usuario_cadastro, ");
			sqlBuilder.append("metas, ");
			sqlBuilder.append("nome, ");
			sqlBuilder.append("nome_ascii, ");
			sqlBuilder.append("nome_capa, ");
			sqlBuilder.append("organizacional, ");
			sqlBuilder.append("prazo_envio_bolsa_fim, ");
			sqlBuilder.append("prazo_envio_bolsa_inicio, ");
			sqlBuilder.append("presidente_comissao, ");
			sqlBuilder.append("sequencia_modalidade_compra, ");
			sqlBuilder.append("sigla, ");
			sqlBuilder.append("sigla_academica, ");
			sqlBuilder.append("sipac, ");
			sqlBuilder.append("submete_proposta_extensao, ");
			sqlBuilder.append("telefones, ");
			sqlBuilder.append("template_parecer_dl, ");
			sqlBuilder.append("tipo, ");
			sqlBuilder.append("tipo_academica, ");
			sqlBuilder.append("tipo_funcao_remunerada, ");
			sqlBuilder.append("unidade_academica, ");
			sqlBuilder.append("unidade_orcamentaria, ");
			sqlBuilder.append("unidade_responsavel, ");
			sqlBuilder.append("permite_gestao_centros_gestora_superior, ");
			sqlBuilder.append("cep, ");
			sqlBuilder.append("endereco, ");
			sqlBuilder.append("uf, ");
			sqlBuilder.append("protocolizadora, ");
			sqlBuilder.append("radical, ");
			sqlBuilder.append("id_municipio,");
			sqlBuilder.append("id_unidade )");
			
			sqlBuilder.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
		}
			
		final List<Object> params = new ArrayList<Object>();
		jt.update(sqlBuilder.toString(), new PreparedStatementSetter() {
			@Override
			public void setValues(PreparedStatement st) throws SQLException {
				int j = 1;


				if (u.getCodigoSIORG() == null){
					st.setNull(j++, Types.INTEGER); 
				}else{
					st.setInt(j++, u.getCodigoSIORG());
				}
				params.add(u.getCodigoSIORG());
				
				st.setBoolean(j++, u.isAvaliacao());
				params.add(u.isAvaliacao());
				
				st.setInt(j++, u.getCategoria());
				params.add(u.getCategoria());
				
				if (u.getCnpj() == null)			
					st.setNull(j++, Types.BIGINT); 	
				else 
					st.setLong(j++, u.getCnpj());
				params.add(u.getCnpj());
				
				
				if (u.getCodigoGestaoSIAFI() == null)  
					st.setNull(j++, Types.INTEGER); 
				else 
					st.setInt(j++, u.getCodigoGestaoSIAFI());
				params.add(u.getCodigoGestaoSIAFI());
				
				if (u.getCodigoSiapecad() == null)  
					st.setNull(j++, Types.BIGINT); 
				else 
					st.setLong(j++, u.getCodigoSiapecad());
				params.add(u.getCodigoSiapecad());
				
				st.setLong(j++, u.getCodigo());
				params.add(u.getCodigo());
				
				if (u.getCodigoUnidadeGestoraSIAFI() == null)  
					st.setNull(j++, Types.INTEGER); 
				else 
					st.setInt(j++, u.getCodigoUnidadeGestoraSIAFI());
				params.add(u.getCodigoUnidadeGestoraSIAFI());
				
				st.setBoolean(j++, u.getCompradora());
				params.add(u.getCompradora());
				
				st.setBoolean(j++, u.getCompradoraEngenharia());
				params.add(u.getCompradoraEngenharia());
				
				if (u.getDataCadastro() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setDate(j++, new java.sql.Date(u.getDataCadastro().getTime()));
				params.add(u.getDataCadastro());
				
				if (u.getDataCriacao() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setDate(j++, new java.sql.Date(u.getDataCriacao().getTime()));
				params.add(u.getDataCriacao());
				
				if (u.getDataExtincao() == null)
					st.setNull(j++, Types.DATE);
				else
					st.setDate(j++, new java.sql.Date(u.getDataExtincao().getTime()));
				params.add(u.getDataExtincao());
				
				st.setString(j++, u.getEmail());
				params.add(u.getEmail());
				
				st.setBoolean(j++, u.isFuncaoRemunerada());
				params.add(u.isFuncaoRemunerada());
				
				st.setBoolean(j++, u.isGestoraFrequencia());
				params.add(u.isGestoraFrequencia());
				
				st.setString(j++, u.getHierarquia());
				params.add(u.getHierarquia());
				
				st.setString(j++, u.getHierarquiaOrganizacional());
				params.add(u.getHierarquiaOrganizacional());
				
				if (u.getAmbienteOrganizacional() == null || u.getAmbienteOrganizacional().getId() == 0) {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				} else { 
					st.setInt(j++, u.getAmbienteOrganizacional().getId());
					params.add(u.getAmbienteOrganizacional().getId());
				}
				
				
				if (u.getAreaAtuacao() == null || u.getAreaAtuacao().getId() == 0) {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				} else {
					st.setInt(j++, u.getAreaAtuacao().getId());
					params.add(u.getAreaAtuacao().getId());
				}
				
				if (u.getClassificacaoUnidade() != null && u.getClassificacaoUnidade().getId() > 0) {
					st.setInt(j++, u.getClassificacaoUnidade().getId());
					params.add(u.getClassificacaoUnidade().getId());
				} else {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				}
				
				st.setInt(j++, u.getGestora().getId());
				params.add(u.getGestora());
				
				if (u.getGestoraAcademica() != null && u.getGestoraAcademica().getId() != 0) {
					st.setInt(j++, u.getGestoraAcademica().getId());
					params.add(u.getGestoraAcademica().getId());
				} else {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				}
				
				if (u.getNivelOrganizacional() != null && u.getNivelOrganizacional().getId() > 0) {
					st.setInt(j++, u.getNivelOrganizacional().getId());
					params.add(u.getNivelOrganizacional().getId());
				} else {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				}
				
				if (u.getIdResponsavelPatrimonial() != null && u.getIdResponsavelPatrimonial() > 0)
					st.setInt(j++, u.getIdResponsavelPatrimonial());
				else
					st.setNull(j++, Types.INTEGER);
				params.add(u.getIdResponsavelPatrimonial());
				
				if (u.getTipoOrganizacional() == null || u.getTipoOrganizacional().getId() == 0) {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				} else {
					st.setInt(j++, u.getTipoOrganizacional().getId());
					params.add(u.getTipoOrganizacional().getId());
				}
				
				if (u.getResponsavelOrganizacional() == null || u.getResponsavelOrganizacional().getId() == 0) {
					st.setNull(j++, Types.INTEGER);
					params.add(null);
				} else {
					st.setInt(j++, u.getResponsavelOrganizacional().getId());
					params.add(u.getResponsavelOrganizacional().getId());
				}

				if (u.getIdUsuarioCadastro() != null && u.getIdUsuarioCadastro() > 0)
					st.setInt(j++, u.getIdUsuarioCadastro());
				else
					st.setNull(j++, Types.INTEGER);
				params.add(u.getIdUsuarioCadastro());
				
				if (u.getMetas() != null)
					st.setBoolean(j++, u.getMetas());
				else
					st.setBoolean(j++, false);
				params.add(u.getMetas());
				
				st.setString(j++, u.getNome());
				params.add(u.getNome());
				st.setString(j++, u.getNomeAscii());
				params.add(u.getNomeAscii());
				st.setString(j++, u.getNomeCapa());
				params.add(u.getNomeCapa());
				st.setBoolean(j++, u.isOrganizacional());
				params.add(u.isOrganizacional());
				
				if (u.getPrazoEnvioBolsaFim() == null || u.getPrazoEnvioBolsaFim() == 0)
					st.setNull(j++, Types.INTEGER);
				else
					st.setInt(j++, u.getPrazoEnvioBolsaFim());
				params.add(u.getPrazoEnvioBolsaFim());
				
				if (u.getPrazoEnvioBolsaInicio() == null || u.getPrazoEnvioBolsaInicio() == 0)
					st.setNull(j++, Types.INTEGER);
				else
					st.setInt(j++, u.getPrazoEnvioBolsaInicio());
				params.add(u.getPrazoEnvioBolsaInicio());
				
				st.setString(j++, u.getPresidenteComissao());
				params.add(u.getPresidenteComissao());
				
				if (u.getSequenciaModalidadeCompra() == null || u.getSequenciaModalidadeCompra() == 0)
					st.setNull(j++, Types.INTEGER);
				else
					st.setInt(j++, u.getSequenciaModalidadeCompra());
				params.add(u.getSequenciaModalidadeCompra());
				
				st.setString(j++, u.getSigla());
				params.add(u.getSigla());
				
				if (u.getSiglaAcademica() != null && !"".equals(u.getSiglaAcademica().trim()))
					st.setString(j++, u.getSiglaAcademica());
				else
					st.setNull(j++, Types.VARCHAR);
				params.add(u.getSiglaAcademica());
				
				st.setBoolean(j++, u.isUnidadeSipac());
				params.add(u.isUnidadeSipac());
				
				if (u.getSubmetePropostaExtensao() != null)
					st.setBoolean(j++, u.getSubmetePropostaExtensao());
				else
					st.setNull(j++, Types.BOOLEAN);
				params.add(u.getSubmetePropostaExtensao());
				
				st.setString(j++, u.getTelefone());
				params.add(u.getTelefone());
				
				if (u.getTemplateParecerDL() == null || u.getTemplateParecerDL() == 0)
					st.setNull(j++, Types.INTEGER);
				else
					st.setInt(j++, u.getTemplateParecerDL());
				params.add(u.getTemplateParecerDL());
				
				st.setInt(j++, u.getTipo());
				params.add(u.getTipo());
				
				if (u.getTipoAcademica() != null && u.getTipoAcademica() != 0)
					st.setInt(j++, u.getTipoAcademica());
				else
					st.setNull(j++, Types.INTEGER);
				params.add(u.getTipoAcademica());
				
				st.setInt(j++, u.getTipoFuncaoRemunerada());
				params.add(u.getTipoFuncaoRemunerada());
				st.setBoolean(j++, u.isUnidadeAcademica());
				params.add(u.isUnidadeAcademica());
				st.setBoolean(j++, u.isUnidadeOrcamentaria());
				params.add(u.isUnidadeOrcamentaria());
				st.setInt(j++, u.getUnidadeResponsavel().getId());
				params.add(u.getUnidadeResponsavel());

				st.setBoolean(j++, u.isPermiteGestaoCentrosPelaGestoraSuperior());
				params.add(u.isPermiteGestaoCentrosPelaGestoraSuperior());
				
				st.setString(j++, u.getCep());
				params.add(u.getCep());
				st.setString(j++, u.getEndereco());
				params.add(u.getEndereco());
				st.setString(j++, u.getUf());
				params.add(u.getUf());
				
				st.setBoolean(j++, u.isProtocolizadora());
				params.add(u.isProtocolizadora());
				
				if (u.getRadical() != null)
					st.setInt(j++, u.getRadical());
				else
					st.setNull(j++, Types.INTEGER);
				params.add(u.getRadical());
				
				Integer idMunicipio = getMunicipio(u);
				if (idMunicipio == null) {
					st.setNull(j++, Types.INTEGER);
				} else {
					st.setInt(j++, idMunicipio);
				}
				params.add(idMunicipio);
				
				st.setInt(j++, u.getId());
				params.add(u.getId());
			}

		});
		
		log(sqlBuilder.toString(), params.toArray());
	}

	private Integer getMunicipio(UnidadeGeral u) {
		try {
			String nome = (String) ReflectionUtils.getProperty(u, "municipio.nome");
			String sigla = (String) ReflectionUtils.getProperty(u, "municipio.unidadeFederativa.sigla");
			
			if (sistema == Sistema.SIPAC || sistema == Sistema.SIGRH)
				return jt.queryForInt("select id_municipio from comum.municipio m, comum.estado uf where m.id_unidade_federativa = uf.id and upper(m.nome) = upper(?) and upper(uf.denominacao) = upper(?) and m.ativo = true limit 1", new Object[] { nome, sigla });
			else
				return jt.queryForInt("select id_municipio from comum.municipio m, comum.unidade_federativa uf where m.id_unidade_federativa = uf.id_unidade_federativa  and upper(m.nome) = upper(?) and upper(uf.sigla) = upper(?) and m.ativo = true limit 1", new Object[] { nome, sigla });
		} catch (Exception e) {
			return null;
		}
	}
	
	private void log(String sql, Object... params) {
		if (comando != null) {
			Integer idUsuario = usuario == null ? null : usuario.getId();
			Integer idRegistro = (usuario == null || usuario.getRegistroEntrada() == null) ? null : usuario.getRegistroEntrada().getId();
			
			LogJdbcUpdate log = new LogJdbcUpdate();
			log.setCodMovimento(comando.getId());
			log.setData(new Date());
			log.setSql(sql);
			log.setParams(params);
			log.setSistema(sistema);
			log.setIdUsuario(idUsuario);
			log.setIdRegistroEntrada(idRegistro);
	
			LogProcessorDelegate.getInstance().writeJdbcUpdateLog(log);
		}
	}
	
	/**
	 * Verifica se a unidade de ID informado está na base de dados
	 * @param u
	 * @return
	 * @throws SQLException
	 */
	public boolean hasUnidade(UnidadeGeral u) {
		int count = jt.queryForInt("select count(*) from comum.unidade where id_unidade = ?", new Object[] {u.getId()});
		return count > 0;
	}

	/**
	 * Na remoção do responsável por uma unidade se o mesmo tiver um nível de chefia atribuído
	 * antes da remoção do responsável o campo id_responsavel da tabela unidade é atualizado, recebendo um valor nulo
	 * para não haver conflito de chave estrangeira
	 * @param u
	 * @return
	 * @throws SQLException
	 */
	 
	 public void atualizaUnidadeResponsavel(UnidadeGeral u) {
		 String sql = "update comum.unidade set id_responsavel = ? where id_unidade = ? ";
		 Object[] params = new Object[] { null, u.getId() };
		 jt.update(sql, params);
	 }

	 /**
	  * Método Fábrica da classe. Instancia um novo objeto <code>SincronizadorUnidades</code> 
	  * associado a um data source específico.
	  *  
	  * @param ds
	  * @return
	  */
	 public static SincronizadorUnidades usandoSistema(Movimento mov, int sistema) {
		 DataSource ds = Database.getInstance().getDataSource(sistema);
		 SincronizadorUnidades sincronizadorUnidades =  MOCK;
		 if (ds != null) {
			 sincronizadorUnidades = new SincronizadorUnidades(ds);
		 }
		 if (mov != null) {
			 sincronizadorUnidades.comando = mov.getCodMovimento();
			 sincronizadorUnidades.usuario = mov.getUsuarioLogado();
		 }
		 sincronizadorUnidades.sistema = sistema;
		 return sincronizadorUnidades;
	 }
	 
	 private static final SincronizadorUnidades MOCK = new SincronizadorUnidades((DataSource) null) {
		 public void removerUnidade(UnidadeGeral u) { }
		 public void sincronizarUnidade(UnidadeGeral u) { }
		 public boolean hasUnidade(UnidadeGeral u) { return false; }
		 public void atualizaUnidadeResponsavel(UnidadeGeral u) { }
	 };
}
