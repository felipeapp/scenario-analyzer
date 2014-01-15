/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '03/06/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.comum.dominio.Sistema;
import br.ufrn.integracao.dto.InclusaoBolsaAcademicaDTO;
import br.ufrn.integracao.dto.PessoaDto;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.ResidenciaUniversitaria;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.pessoa.dominio.Banco;
import br.ufrn.sigaa.pessoa.dominio.ContaBancaria;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável pela consulta de Bolsa Auxilio
 * 
 * @author agostinho campos
 *
 */
	
public class BolsaAuxilioDao extends GenericSigaaDAO {
	
	/**
	 * Localiza todas as solicitações de bolsa auxílio de um discente
	 * 
	 * @param idDiscente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilioPeriodo> findAllSolicitacoesBolsaAuxilio(int idDiscente) throws HibernateException, DAOException {
		Query q = getSession().createQuery("select bolsaAuxilioPeriodo " +
										   " from BolsaAuxilioPeriodo bolsaAuxilioPeriodo " +
										   " where bolsaAuxilioPeriodo.bolsaAuxilio.discente.id = " + idDiscente +
										   " order by bolsaAuxilioPeriodo.ano desc, bolsaAuxilioPeriodo.periodo desc ");
		return q.list();
	}
	
	/**
	 * Localiza a bolsa de acordo com os parâmetros
	 * 
	 * @param tipoBolsa
	 * @param idResidencia
	 * @param situacaoBolsa
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @param nivel
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> findBolsas(Integer tipoBolsa, Integer idResidencia, 
				Integer situacaoBolsa, Integer ano, Integer periodo,
				Integer idCurso, Integer idMunicipio, char nivel, Boolean incluir, char sexo, int status) throws DAOException {
			StringBuffer sb = new StringBuffer();
		
			sb.append(" select distinct ba.id_bolsa_auxilio, ba.data_solicitacao, d.id_discente, d.matricula, d.nivel, p.nome, " +
					  " p.id_conta_bancaria, cb.agencia, cb.numero, cb.id_banco, b.codigo, p.cpf_cnpj, d.id_pessoa, p.email, " +
					  " tb.id_tipo_bolsa_auxilio, tb.denominacao, ba.id_situacao_bolsa, ba.id_tipo_bolsa_sipac, m.nome as municipio, " +
					  " m.id_municipio, uf.sigla, c.id_curso, c.nome as curso" +
					  " from sae.bolsa_auxilio_periodo bap" +
					  " inner join sae.bolsa_auxilio ba on bap.id_bolsa_auxilio=ba.id_bolsa_auxilio" +
					  " inner join sae.tipo_bolsa_auxilio tb on ba.id_tipo_bolsa_auxilio=tb.id_tipo_bolsa_auxilio" +
					  " inner join discente d on ba.id_discente=d.id_discente" +
					  " inner join curso c on d.id_curso=c.id_curso" +
					  " inner join comum.municipio m on c.id_municipio=m.id_municipio" +
					  " inner join comum.pessoa p on ( d.id_pessoa=p.id_pessoa )" +
					  " left join comum.conta_bancaria cb on ( p.id_conta_bancaria=cb.id_conta_bancaria )" +
					  " left join comum.banco b on ( cb.id_banco=b.id_banco )" +
					  " left join comum.unidade_federativa uf on ( m.id_unidade_federativa=uf.id_unidade_federativa ) WHERE 1=1 ");
			
			Query q = defineParametrosBuscaBolsas(tipoBolsa, idResidencia, situacaoBolsa, ano, periodo, idCurso, idMunicipio, nivel, sb, incluir, sexo, status);
			
			ArrayList<BolsaAuxilio> listaBolsAuxilio = new ArrayList<BolsaAuxilio>();
			List<Object[]> result = q.list();
			for (int i = 0; i < result.size(); i++) {
				
				int cont = 0;
				Object[] obj = result.get(i);
				
				BolsaAuxilio b = new BolsaAuxilio();
				b.setId((Integer)obj[cont++]);
				b.setDataSolicitacao((Date)obj[cont++]);
				
				Discente disc = new Discente((Integer)obj[cont++]);
				disc.setMatricula( ((BigInteger) obj[cont++]).longValue() );
				disc.setNivel((Character) obj[cont++] );
				
				Pessoa p = new Pessoa();
				p.setNome( (String)obj[cont++] );
				
				if ( obj[cont] != null ) {
					p.setContaBancaria(new ContaBancaria());
					p.getContaBancaria().setId( (Integer)obj[cont++] );
					p.getContaBancaria().setAgencia( (String)obj[cont++] );
					p.getContaBancaria().setNumero( (String)obj[cont++] );
					p.getContaBancaria().setBanco(new Banco());
					p.getContaBancaria().getBanco().setId( (Integer)obj[cont++] );
					p.getContaBancaria().getBanco().setCodigo( (Integer)obj[cont++] );
				} else {
					cont = 11;
				}
				
				p.setCpf_cnpj( ((BigInteger) obj[cont++]).longValue() );
				p.setId( (Integer) obj[cont++] );
				p.setEmail( (String)obj[cont++] );
				
				disc.setPessoa(p);
				
				b.setDiscente(disc);
				
				TipoBolsaAuxilio tipo = new TipoBolsaAuxilio();
				tipo.setId( (Integer) obj[cont++] );
				tipo.setDenominacao( (String) obj[cont++]);
				b.setTipoBolsaAuxilio(tipo);
				
				b.setSituacaoBolsa(new SituacaoBolsaAuxilio());
				b.getSituacaoBolsa().setId( (Integer)obj[cont++] );
				b.setTipoBolsaSIPAC( (Integer)obj[cont++] );
				
				Municipio m = new Municipio();
				m.setNome((String)obj[cont++]);
				m.setId((Integer)obj[cont++]);
				m.setUnidadeFederativa(new UnidadeFederativa());
				m.getUnidadeFederativa().setSigla( (String)obj[cont++] );
				
				Curso c = new Curso();
				c.setId( (Integer) obj[cont++] );
				c.setNome((String) obj[cont++]);
				c.setMunicipio(m);
				
				disc.setCurso(c);
				
				listaBolsAuxilio.add(b);
			}
			
		return listaBolsAuxilio;
	}

	/**
	 * Define os parâmetros da bolsa de acordo com as seleções
	 * feitas na tela
	 * 
	 * @param tipoBolsa
	 * @param idResidencia
	 * @param situacaoBolsa
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @param nivel
	 * @param sb
	 * @return
	 * @throws DAOException
	 */
	private Query defineParametrosBuscaBolsas(Integer tipoBolsa, Integer idResidencia, Integer situacaoBolsa, Integer ano, Integer periodo, 
			Integer idCurso, Integer idMunicipio, char nivel, StringBuffer sb, Boolean incluir, char sexo, int status) throws DAOException {
		
		if (situacaoBolsa != 0)
			sb.append("AND ba.id_situacao_bolsa = :situacaoBolsa ");

		if (idCurso != 0) {
			sb.append("AND c.id_curso = :curso ");
		}
		
		if (idResidencia != 0) {
			sb.append("AND b.id_residencia = :residencia ");
		}
		
		if (idMunicipio != 0) {
			sb.append("AND m.id_municipio = :municipio ");
		}
		
		if (tipoBolsa != 0) {
			sb.append("AND tb.id_tipo_bolsa_auxilio = :tipoBolsa ");
		}
		
		if ( ano != null && ano != 0) {
			sb.append("AND bap.ano = :ano ");
		}
		
		if ( periodo != null && periodo != 0) {
			sb.append("AND bap.periodo = :periodo ");
		}
		
		if ( sexo != ' ' ) {
			sb.append("AND p.sexo = :sexo ");
		}

		if ( status != 0 ) {
			sb.append("AND d.status = :status ");
		}

		if (nivel != 'G' && nivel != '0') {
			sb.append("and (d.nivel = 'L' OR ");
			sb.append("d.nivel = 'S' OR ");
			sb.append("d.nivel = 'E' OR ");
			sb.append("d.nivel = 'D' )");
		}
		else if (nivel == 'G') {
			sb.append("AND d.nivel = 'G' ");
		}

		if (incluir != null) {
			sb.append("AND ba.bolsa_ativa_sipac = :incluir ");
			if ( !incluir )
				sb.append("AND ba.bolsa_ativa_sipac is null ");
			else
				sb.append("AND ba.bolsa_ativa_sipac is not null ");
		}
		
		sb.append("ORDER BY p.nome");
		
		Query q = getSession().createSQLQuery(sb.toString());
		
		if (tipoBolsa != 0) {
			q.setParameter("tipoBolsa", tipoBolsa);
		}
		if (ano != null && ano != 0) {
			q.setParameter("ano", ano);
		}
		if ( periodo != null && periodo != 0) {
			q.setParameter("periodo", periodo);
		}
		if ( sexo != ' ' ) {
			q.setParameter("sexo", sexo);
		}
		if ( status != 0 ) {
			q.setParameter("status", status);
		}
		if (situacaoBolsa != 0) {
			q.setParameter("situacaoBolsa", situacaoBolsa);
		}
		if (idCurso != 0) { 
			q.setParameter("curso", idCurso);
		}
		if (idResidencia != 0) {
			q.setParameter("residencia", idResidencia);
		}
		if (idMunicipio != 0) {
			q.setParameter("municipio", idMunicipio);
		}
		if (incluir != null) {
			q.setParameter("incluir", incluir);
		}

		return q;
	}

	/**
	 * Conta o numero total de registros de acordo com os parâmetros
	 * 
	 * @param idTipoBolsaAuxilio
	 * @param situacaoBolsa
	 * @param ano
	 * @param periodo
	 * @param idCurso
	 * @return
	 * @throws DAOException
	 */
	public Long findCountBolsas(Integer idTipoBolsaAuxilio, Integer situacaoBolsa, Integer ano, Integer periodo, 
			Integer idCurso, Integer idMunicipio, char sexo, int status) throws DAOException {

		StringBuffer sb = new StringBuffer();		  
	
		sb.append("SELECT COUNT(p) " +
				" FROM BolsaAuxilioPeriodo p " +
				" INNER JOIN p.bolsaAuxilio b " +
				" INNER JOIN b.discente d " +
				" INNER JOIN d.curso c " +
				" WHERE b.tipoBolsaAuxilio.id = :tipoBolsa ");
				
		if ( ano != null && ano != 0 )
			sb.append(" AND p.ano = :ano");
		if ( periodo != null && periodo != 0 )
			sb.append(" AND p.periodo = :periodo");
		if ( !isEmpty(situacaoBolsa) )
			sb.append(" AND b.situacaoBolsa.id = :situacaoBolsa ");
		if ( !isEmpty(idCurso) )
			sb.append(" AND b.discente.curso.id = :idCurso ");
		if ( !isEmpty(idMunicipio) )
			sb.append(" AND c.municipio.id = :idMunicipio ");
		if ( sexo != ' ' )
			sb.append(" AND d.pessoa.sexo = :sexo ");
		if ( status != 0 )
			sb.append(" AND d.status = :status ");
		
		Query q = getSession().createQuery(sb.toString());
		
		q.setParameter("tipoBolsa", idTipoBolsaAuxilio);

		if ( ano != null && ano != 0 )
			q.setParameter("ano", ano);
		if ( periodo != null && periodo != 0 )
			q.setParameter("periodo", periodo);
		if (situacaoBolsa != 0)
			q.setParameter("situacaoBolsa", situacaoBolsa);
		if (idCurso != 0) 
			q.setParameter("idCurso", idCurso);
		if (idMunicipio != 0)
			q.setParameter("idMunicipio", idMunicipio);
		if ( sexo != ' ' )
			q.setParameter("sexo", sexo);
		if ( status != 0 )
			q.setParameter("status", status);
		
			if (q.uniqueResult() != null) 
				return (Long) q.uniqueResult();
			else
				return new Long(0); 
	}

	/**
	 * Lista Discentes que possuem digital cadastrada.
	 * 
	 * discentesComBolsaAuxilio = trueValue() (exibe os discentes que possuem BolsaAuxilio)
	 * discentesComBolsaAuxilio = false (exibe os discentes do da Unidade ECT)
	 * 
	 * @param sistema
	 * @param discentesComBolsaAuxilio
	 * @param idCurso 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public List<PessoaDto> findDiscentesDigitalCadastrada(int sistema, boolean discentesComBolsaAuxilio, int idCurso, int ano, int periodo) {
		
		String consulta = "";	

		if (sistema == Sistema.COMUM) {
			 consulta = "select distinct(cpf) from comum.identificacao_pessoa order by cpf";
			 
				List<PessoaDto> listaPessoasDigital = getJdbcTemplate(sistema).query(consulta, new RowMapper() {
					
					public PessoaDto mapRow(ResultSet rs, int row) throws SQLException {
						PessoaDto identPessoa = new PessoaDto();
							identPessoa.setCpf(rs.getLong("cpf"));
						return identPessoa;
					}
				});
				return listaPessoasDigital;
		}
		
		// discentes que tenham BolsaAuxilio e com bolsa contempladas
		if (sistema == Sistema.SIGAA && discentesComBolsaAuxilio) {
			consulta =   " select distinct(cpf_cnpj), p.nome_ascii, d.matricula, c.nome " +
					" from sae.bolsa_auxilio baux " +
					" inner join sae.bolsa_auxilio_periodo baperiodo on baperiodo.id_bolsa_auxilio = baux.id_bolsa_auxilio " +
            		" inner join discente d on baux.id_discente = d.id_discente " +
            		" inner join curso c on c.id_curso = d.id_curso " +
            		" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
            		" WHERE d.status in (1, 2, 8, 9) AND baperiodo.ano = " + ano + " AND baperiodo.periodo = " + periodo  +
            		" AND baux.id_situacao_bolsa = " + SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA +
            		" group by c.nome, p.nome_ascii, d.matricula, cpf_cnpj " +
            		" order by c.nome, p.nome_ascii, d.matricula, cpf_cnpj ";
	 
			List<PessoaDto> listaPessoasDigital = getJdbcTemplate(sistema).query(consulta, new RowMapper() {
				
				public PessoaDto mapRow(ResultSet rs, int row) throws SQLException {
					PessoaDto identPessoa = new PessoaDto();
						identPessoa.setCpf(rs.getLong("cpf_cnpj"));
						identPessoa.setNome(rs.getString("nome_ascii"));
						identPessoa.setMatricula(rs.getLong("matricula"));
						identPessoa.setCurso(rs.getString("nome"));
					return identPessoa;
				}
			});
			
			return listaPessoasDigital;
		}
		
		// discentes de acordo com a Unidade do Coordenador logado
		if (sistema == Sistema.SIGAA && !discentesComBolsaAuxilio) {
			 consulta =  " select distinct(cpf_cnpj),p.nome, d.matricula " +
			 		" from discente d " +
			 		" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " + 
			 		" inner join curso c on c.id_curso = d.id_curso " +
			 		" where d.status in (1, 2, 8, 9) AND cpf_cnpj is not null AND c.id_curso = " + idCurso +
			 		" order by p.nome, cpf_cnpj";
			 
				List<PessoaDto> listaPessoasDigital = getJdbcTemplate(sistema).query(consulta, new RowMapper() {
					
					public PessoaDto mapRow(ResultSet rs, int row) throws SQLException {
						PessoaDto identPessoa = new PessoaDto();
							identPessoa.setCpf(rs.getLong("cpf_cnpj"));
							identPessoa.setNome(rs.getString("nome"));
							identPessoa.setMatricula(rs.getLong("matricula"));
						return identPessoa;
					}
				});
				return listaPessoasDigital;
		}
		
		return null;
	}

	/**
	 * Cria relatório exibindo pagina em formato
	 * de impressão com todas as residência e seus
	 * respectivos alunos (graduação e pós).
	 * 
	 * RESIDENCIA_GRADUACAO = 1 
	 * RESIDENCIA_POS = 2 
	 * 
	 * @return
	 * @throws SQLException 
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> relatorioOcupacaoResidencia(int idResidencia, int ano, int periodo, int tipoBolsaAuxilio) throws SQLException {
		String sql = " SELECT DISTINCT tipoBolsa.denominacao, residencia.localizacao, residencia.tipo, p.nome,d.matricula,c.nome as nomeCurso " +
						" FROM sae.bolsa_auxilio bolsa " +
						" INNER JOIN sae.bolsa_auxilio_periodo bolsa_periodo using(id_bolsa_auxilio) " +
						" INNER JOIN discente d on d.id_discente = bolsa.id_discente " +
						" INNER JOIN curso c on d.id_curso = c.id_curso "+
						" INNER JOIN comum.pessoa p on p.id_pessoa = d.id_pessoa " +
						" INNER JOIN sae.tipo_bolsa_auxilio tipoBolsa on tipoBolsa.id_tipo_bolsa_auxilio = bolsa.id_tipo_bolsa_auxilio " +
						" LEFT JOIN sae.residencia_universitaria residencia on residencia.id_residencia_universitaria = bolsa.id_residencia " +
						" WHERE bolsa.id_residencia = ? " +
						" AND bolsa_periodo.ano = ?  AND bolsa_periodo.periodo = ? " +
						" AND tipoBolsa.id_tipo_bolsa_auxilio = ? " +
						" AND bolsa.id_situacao_bolsa = "+SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA+
						" AND d.status NOT IN "+ UFRNUtils.gerarStringIn(StatusDiscente.getAfastamentosPermanentes())+""+
						" ORDER BY denominacao, localizacao, tipo, p.nome";
		
		return getJdbcTemplate().query(sql, new Object[]{idResidencia, ano, periodo, tipoBolsaAuxilio}, new RowMapper() {

			public Object mapRow(ResultSet rs, int row) throws SQLException {
					
					TipoBolsaAuxilio tipoBolsa = new TipoBolsaAuxilio();
						tipoBolsa.setDenominacao(rs.getString("denominacao"));
						
					ResidenciaUniversitaria residencia = new ResidenciaUniversitaria();
						residencia.setLocalizacao(rs.getString("localizacao"));
						residencia.setSexo(rs.getString("tipo"));
						
					Pessoa pessoa = new Pessoa();
						pessoa.setNome(rs.getString("nome"));
					
					Discente discente = new Discente();
						discente.setPessoa(pessoa);
						discente.setMatricula(rs.getLong("matricula"));
						discente.getCurso().setNome(rs.getString("nomeCurso"));
						
					BolsaAuxilio bolsa = new BolsaAuxilio();
						bolsa.setTipoBolsaAuxilio(tipoBolsa);
						bolsa.setResidencia(residencia);
						bolsa.setDiscente(discente);
					
						
					return bolsa;
				}
			});
	}
	
	/**
	 * Retorna a denominação de um determinado tipo de bolsa.
	 * 
	 * @param id
	 */
	public Map<String, Object> findBolsasEspecificasSIPAC(int id) {
		Map<String, Object> map = getJdbcTemplate(getDataSource(Sistema.SIPAC)).queryForMap("select id, denominacao from bolsas.tipo_bolsa where id = ?", new Object[] { id });
		return map;
	}
	
	/**
	 * Retorna o id do tipo de bolsa que cpf informado apresenta. 
	 *  
	 * @param cpf
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public int findTipoBolsaDiscente(long cpf) throws HibernateException, DAOException {
		
		Query q = getSession().createSQLQuery("SELECT ba.id_tipo_bolsa_auxilio" +
				" FROM sae.bolsa_auxilio ba" +
				" JOIN discente d using ( id_discente )" +
				" JOIN comum.pessoa p using ( id_pessoa )" +
				" WHERE p.cpf_cnpj = ?" +
				" order by id_bolsa_auxilio desc" +
				" limit 1");

		q.setLong(0, cpf);

		return (Integer) q.uniqueResult();
	}

	@SuppressWarnings("unchecked")
	public Collection<BolsaAuxilio> findAllBolsasDiscente(Discente discente) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(BolsaAuxilio.class);
			c.add(Restrictions.eq("discente", discente));
			c.add(Restrictions.eq("situacaoBolsa.id", SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA) );
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BolsaAuxilioPeriodo> acompanhamentoSolicitacaoBolsa(int idDiscente) throws SQLException,DAOException {
		String sql = " SELECT DISTINCT ba.id_bolsa_auxilio, tba.denominacao as tipo_bolsa, sba.denominacao, bap.ano, bap.periodo,cba.inicio_divulgacao_resultado" +
				" FROM sae.ano_periodo_referencia apr" +
				" JOIN sae.calendario_bolsa_auxilio cba on ( apr.id = cba.id_ano_periodo_referencia )" +
				" , sae.bolsa_auxilio ba" +
				" JOIN comum.usuario u on(ba.id_usuario = u.id_usuario)" +
				" JOIN comum.unidade un on (un.id_unidade = u.id_unidade)" +
				", sae.tipo_bolsa_auxilio tba, sae.situacao_bolsa_auxilio sba, sae.bolsa_auxilio_periodo bap" +
				" WHERE apr.vigente and apr.ativo and cba.ativo" +
				" and ba.id_discente = ?" +
				" and ( cba.fim_divulgacao_resultado is null or now() <= cba.fim_divulgacao_resultado ) " +
				" and bap.id_bolsa_auxilio = ba.id_bolsa_auxilio" +
				" and ba.id_tipo_bolsa_auxilio = cba.id_tipo_bolsa_auxilio" +
				" and tba.id_tipo_bolsa_auxilio = ba.id_tipo_bolsa_auxilio" +
				" and sba.id_situacao_bolsa_auxilio = ba.id_situacao_bolsa" +
				" and cba.id_municipio = un.id_municipio";
		
		return getJdbcTemplate().query(sql, new Object[]{idDiscente}, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
					Date inicioDivulgacaoResultado = rs.getDate("inicio_divulgacao_resultado");
					BolsaAuxilioPeriodo bolsa = new BolsaAuxilioPeriodo();
					bolsa.setBolsaAuxilio(new BolsaAuxilio());
					bolsa.getBolsaAuxilio().setId( rs.getInt("id_bolsa_auxilio") );
					bolsa.getBolsaAuxilio().setTipoBolsaAuxilio(new TipoBolsaAuxilio());
					bolsa.getBolsaAuxilio().getTipoBolsaAuxilio().setDenominacao( rs.getString("tipo_bolsa") );
					bolsa.getBolsaAuxilio().setSituacaoBolsa(new SituacaoBolsaAuxilio());
					if(inicioDivulgacaoResultado != null && inicioDivulgacaoResultado.compareTo(new Date()) <= 0){
						bolsa.getBolsaAuxilio().getSituacaoBolsa().setDenominacao( rs.getString("denominacao"));
					}else{						
						try {
							bolsa.getBolsaAuxilio().getSituacaoBolsa().setDenominacao(findByPrimaryKey(SituacaoBolsaAuxilio.EM_ANALISE, SituacaoBolsaAuxilio.class).getDenominacao());
						} catch (DAOException e) {
							e.printStackTrace();
						}
						
					}
					bolsa.setAno( rs.getInt("ano") );
					bolsa.setPeriodo( rs.getInt("periodo") );
					return bolsa;
				}
			});
	}


	@SuppressWarnings("unchecked")
	public Collection<BolsaAuxilioPeriodo> findAllBolsasDiscenteAnoPeriodo(Discente discente, int ano, int periodo) throws DAOException {
		try {
			Criteria c = getSession().createCriteria(BolsaAuxilioPeriodo.class);
			c.add(Restrictions.eq("ano", ano));
			c.add(Restrictions.eq("periodo", periodo));
			c.createCriteria("bolsaAuxilio").add(Restrictions.eq("discente", discente));
			return c.list();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	public void inativarBolsa(InclusaoBolsaAcademicaDTO bolsaDTO) {
		update("UPDATE sae.bolsa_auxilio SET bolsa_ativa_sipac = ? WHERE id_tipo_bolsa_sipac = ? ", new Object[] { Boolean.FALSE, bolsaDTO.getIdBolsa() });
	}

	public BolsaAuxilioPeriodo findBolsaAuxilioPeriodoDiscente(int idBolsaAuxilio) throws DAOException {
		Criteria c = getSession().createCriteria(BolsaAuxilioPeriodo.class);
		c.add(Restrictions.eq("bolsaAuxilio.id", idBolsaAuxilio));
		return (BolsaAuxilioPeriodo) c.uniqueResult();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<BolsaAuxilioPeriodo> visualizarHistorico( BolsaAuxilioPeriodo bolsaAuxilioPeriodo ) throws SQLException {
		String sql = " SELECT ba.id_bolsa_auxilio, d.matricula, p.nome, bap.ano, bap.periodo, tba.denominacao, s.denominacao as situacao" +
					 " FROM sae.bolsa_auxilio_periodo  bap" +
					 " JOIN sae.bolsa_auxilio ba using ( id_bolsa_auxilio )" +
					 " JOIN sae.tipo_bolsa_auxilio tba using ( id_tipo_bolsa_auxilio )" +
					 " JOIN sae.situacao_bolsa_auxilio s on ( ba.id_situacao_bolsa = s.id_situacao_bolsa_auxilio )" +
					 " JOIN discente d on ( ba.id_discente = d.id_discente )" +
					 " JOIN comum.pessoa p on ( d.id_pessoa = p.id_pessoa )" +
				     " WHERE ba.id_discente = ? " +
				     " AND bap.data_cadastro <= ? " +
				     " ORDER BY bap.ano desc, bap.periodo desc";
		
		return getJdbcTemplate().query(sql, new Object[]{ bolsaAuxilioPeriodo.getBolsaAuxilio().getDiscente().getId(), bolsaAuxilioPeriodo.getDataCadastro() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int row) throws SQLException {
					BolsaAuxilioPeriodo bolsa = new BolsaAuxilioPeriodo();
					bolsa.setBolsaAuxilio(new BolsaAuxilio());
					bolsa.getBolsaAuxilio().setId( rs.getInt("id_bolsa_auxilio") );
					bolsa.getBolsaAuxilio().setDiscente(new Discente());
					bolsa.getBolsaAuxilio().getDiscente().setMatricula( rs.getLong("matricula") );
					bolsa.getBolsaAuxilio().getDiscente().setPessoa(new Pessoa());
					bolsa.getBolsaAuxilio().getDiscente().getPessoa().setNome( rs.getString("nome") );
					bolsa.setAno( rs.getInt("ano") );
					bolsa.setPeriodo( rs.getInt("periodo") );
//					bolsa.getBolsaAuxilio().setObservacoes( rs.getString("observacoes") );
					bolsa.getBolsaAuxilio().setTipoBolsaAuxilio(new TipoBolsaAuxilio());
					bolsa.getBolsaAuxilio().getTipoBolsaAuxilio().setDenominacao( rs.getString("denominacao") );
					bolsa.getBolsaAuxilio().setSituacaoBolsa(new SituacaoBolsaAuxilio());
					bolsa.getBolsaAuxilio().getSituacaoBolsa().setDenominacao(rs.getString("situacao"));
					return bolsa;
				}
			});
	}
	
}