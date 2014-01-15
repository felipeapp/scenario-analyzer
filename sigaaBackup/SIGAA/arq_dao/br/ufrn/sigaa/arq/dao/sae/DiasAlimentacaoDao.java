/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '24/07/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.sae;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.AnoPeriodoReferenciaSAE;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.BolsaAuxilioPeriodo;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoBolsaAuxilio;
import br.ufrn.sigaa.assistencia.dominio.TipoRefeicaoRU;
import br.ufrn.sigaa.assistencia.restaurante.dominio.DiasAlimentacao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO responsável por gerenciar o cadastramento e exibição dos de alimentação que os discentes da instituição
 * podem ter.
 * 
 * @author agostinho campos
 */

public class DiasAlimentacaoDao extends GenericSigaaDAO {

	private static final int NUMERO_REFEICOES_DIARIAS = 3;
	
	public DiasAlimentacaoDao() {
		super();
	}
	
	/**
	 * Localiza a Bolsa Auxílio do discente de acordo com o CPF e o Ano/Período de referência do SAE 
	 *  
	 * @param cpf
	 * @param anoPeriodoReferenciaSAE
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public BolsaAuxilio findBolsaAuxilio(Long cpf, String passaporte, AnoPeriodoReferenciaSAE anoPeriodoReferenciaSAE, boolean internacional, int tipoBolsa) throws HibernateException, DAOException {
		
		StringBuilder hql = new StringBuilder();    
		hql.append("select bolsaAnoPeriodo from BolsaAuxilioPeriodo bolsaAnoPeriodo " +
				"inner join bolsaAnoPeriodo.bolsaAuxilio bolsa " +
				"inner join bolsa.discente discente " +
				"inner join discente.pessoa p " +
		"inner join discente.curso curso ");
		
		if (internacional && !passaporte.isEmpty()) {
			hql.append(" WHERE p.passaporte = ? ");
		}else{
			hql.append("WHERE p.cpf_cnpj = ? "); 
		}

		hql.append("AND bolsa.situacaoBolsa in " + 
				UFRNUtils.gerarStringIn(new int[]{ SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA }) +
				"AND bolsaAnoPeriodo.ano = ? " +
		"AND bolsaAnoPeriodo.periodo = ? " +
		"AND bolsa.tipoBolsaAuxilio.id = ? ");

		Query q = getSession().createQuery(hql.toString());
		if(internacional){
			q.setString(0, passaporte);
		}else{
			q.setLong(0, cpf);
		}
		
		q.setInteger(1, anoPeriodoReferenciaSAE.getAno());
		q.setInteger(2, anoPeriodoReferenciaSAE.getPeriodo());
		q.setInteger(3, tipoBolsa );

		BolsaAuxilioPeriodo bolsaAnoPeriodo = (BolsaAuxilioPeriodo) q.uniqueResult();
		return bolsaAnoPeriodo != null ? bolsaAnoPeriodo.getBolsaAuxilio() : new BolsaAuxilio();
	}

	public BolsaAuxilio findBolsaAuxilioDiscente( int idDiscente ) throws HibernateException, DAOException {
		
		String hql = "SELECT ba from BolsaAuxilio ba " +
					 "WHERE ba.discente.id = ? " +
					 "and ba.tipoBolsaAuxilio.id in " + 
					 	UFRNUtils.gerarStringIn( new int[]{ TipoBolsaAuxilio.RESIDENCIA_GRADUACAO, TipoBolsaAuxilio.RESIDENCIA_POS }) +
					 "and ba.situacaoBolsa.id in " + 
					 	UFRNUtils.gerarStringIn( new int[]{ SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA }) +
					 "order by id ";

		Query q = getSession().createQuery(hql);
		
		q.setInteger(0, idDiscente );
		q.setMaxResults(1);
		
		return (BolsaAuxilio) q.uniqueResult();
	} 
	
	/**
	 * Lista todos os alunos deferidos e contemplados com seus dias de alimentação definidos. 
	 * É utilizado para gerar o relatório de Mapa de Acesso ao RU. 
	 * 
	 * @param calendarioSAE
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> findDiscentesDiasAlimentacaoMapaRU(Long cpf, AnoPeriodoReferenciaSAE calendarioSAE, long tipoBolsa) throws HibernateException, DAOException {
	
		String consulta = "";
		
		if ( cpf != null && tipoBolsa != TipoBolsaAuxilio.ALIMENTACAO )
			consulta = "select distinct ";
		else 
			consulta = "select ";
		
		consulta += 
			"dias.segunda, dias.terca, dias.quarta, dias.quinta, dias.sexta, dias.sabado, dias.domingo, " +
			"p.nome as nome_pessoa, p.cpf_cnpj as cpf_pessoa, tipo.id_tipo_refeicao_ru, tipo.descricao, " +
			"tipo.hora_inicio, tipo.hora_fim, tipo.minuto_inicio, tipo.minuto_fim, " +
			"b.id_bolsa_auxilio, b.id_situacao_bolsa, d.matricula, c.nome, tipobolsa.id_tipo_bolsa_auxilio, tipobolsa.denominacao as tipo_bolsa " +
			"from sae.dias_alimentacao dias " +
			"inner join sae.tipo_refeicao_ru tipo on tipo.id_tipo_refeicao_ru = dias.id_tipo_refeicao_ru " + 
			"inner join sae.bolsa_auxilio b on b.id_bolsa_auxilio = dias.id_bolsa_auxilio " +
			"inner join sae.bolsa_auxilio_periodo bap on bap.id_bolsa_auxilio = b.id_bolsa_auxilio " +
			"inner join discente d on d.id_discente = b.id_discente " +
			"inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
			"inner join curso c on c.id_curso = d.id_curso " +
			"inner join sae.tipo_bolsa_auxilio tipobolsa on tipobolsa.id_tipo_bolsa_auxilio = b.id_tipo_bolsa_auxilio ";
		
		StringBuilder strBuild = new StringBuilder();
		strBuild.append(consulta);
				
		Object[] parametros = null;
		
		if (cpf == null) {
			strBuild.append(" WHERE bap.ano = ? AND bap.periodo = ? AND b.id_tipo_bolsa_auxilio = ? and b.id_situacao_bolsa = ? " );
			parametros = new Object[] { calendarioSAE.getAnoAlimentacao(), calendarioSAE.getPeriodoAlimentacao(), TipoBolsaAuxilio.ALIMENTACAO, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA };
		}
		else if ( cpf != null && tipoBolsa == TipoBolsaAuxilio.ALIMENTACAO ) {
			strBuild.append(" WHERE bap.ano = ? AND bap.periodo = ? AND p.cpf_cnpj = ? AND ( b.id_situacao_bolsa = ? OR b.id_situacao_bolsa = ? )");
			parametros = new Object[] { calendarioSAE.getAnoAlimentacao(), calendarioSAE.getPeriodoAlimentacao(), cpf, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA };
		} else {
			strBuild.append(" WHERE p.cpf_cnpj = ? AND ( b.id_situacao_bolsa = ? OR b.id_situacao_bolsa = ? )");
			parametros = new Object[] { cpf, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA };
		}
				
		if ( cpf != null && tipoBolsa != TipoBolsaAuxilio.ALIMENTACAO )
			strBuild.append(" order by b.id_bolsa_auxilio desc, p.nome, tipo.id_tipo_refeicao_ru limit 3");
		else 
			strBuild.append(" order by p.nome, tipo.id_tipo_refeicao_ru");
		
	      return (List<BolsaAuxilio>) getJdbcTemplate().query(strBuild.toString(), parametros, new ResultSetExtractor() {

	          @SuppressWarnings("rawtypes")
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
	              
	              List<BolsaAuxilio> resultado = null;
	              BolsaAuxilio bolsa = new BolsaAuxilio();
	              
	              bolsa.setId(0);
	              while (rs.next()) {

	                  int idAtual = rs.getInt("id_bolsa_auxilio");
	                  
	                      if (bolsa.getId() != idAtual) {
	                          bolsa = new BolsaAuxilio();
	                          bolsa.setId(rs.getInt("id_bolsa_auxilio"));

	                          if (resultado == null)
	                            resultado = new ArrayList();
	      
	                          resultado.add(bolsa);
	                      }
	                      
                        DiasAlimentacao detalhe = new DiasAlimentacao();
	      				
                        Pessoa pessoa = new Pessoa();
		      				pessoa.setNome( rs.getString("nome_pessoa") );
		      				pessoa.setCpf_cnpj(rs.getLong("cpf_pessoa"));
	      
	      				Discente discente = new Discente();
		      				discente.setPessoa(pessoa);
		      				discente.setMatricula(rs.getLong("matricula"));
	      				
	      				br.ufrn.sigaa.dominio.Curso c = new br.ufrn.sigaa.dominio.Curso();
	      					c.setNome(rs.getString("nome"));
	      				
	      				discente.setCurso(c);
	      				
	    				TipoRefeicaoRU tipo = new TipoRefeicaoRU();
		    				tipo.setId(rs.getInt("id_tipo_refeicao_ru"));
		    				tipo.setDescricao(rs.getString("descricao"));
		    				
		    				tipo.setHoraInicio(rs.getInt("hora_inicio"));
		    				tipo.setMinutoInicio(rs.getInt("minuto_inicio"));
		    				
		    				tipo.setHoraFim(rs.getInt("hora_fim"));
		    				tipo.setMinutoFim(rs.getInt("minuto_fim") + 1);
	      				
	    				detalhe.setTipoRefeicao(tipo);
	    				
	      				detalhe.setSegunda(rs.getBoolean("segunda"));
	      				detalhe.setTerca(rs.getBoolean("terca"));
	      				detalhe.setQuarta(rs.getBoolean("quarta"));
	      				detalhe.setQuinta(rs.getBoolean("quinta"));
	      				detalhe.setSexta(rs.getBoolean("sexta"));
	      				detalhe.setSabado(rs.getBoolean("sabado"));
	      				detalhe.setDomingo(rs.getBoolean("domingo"));
	               
	      				bolsa.setDiscente(discente);
	      				
	      				TipoBolsaAuxilio tipoBolsa = new TipoBolsaAuxilio();
	      					tipoBolsa.setId(rs.getInt("id_tipo_bolsa_auxilio"));
	      					tipoBolsa.setDenominacao(rs.getString("tipo_bolsa"));
	      				
	      				bolsa.setTipoBolsaAuxilio(tipoBolsa);
	      				
                        bolsa.setSituacaoBolsa(new SituacaoBolsaAuxilio());
                        bolsa.getSituacaoBolsa().setId(rs.getInt("id_situacao_bolsa"));
	      				
	      				bolsa.getDiasAlimentacao().add(detalhe);
	              }
	              return resultado;
	          }
	        });	      
      }
	
	/**
	 * Lista todos os alunos deferidos e contemplados com seus dias de alimentação definidos. 
	 * É utilizado para gerar o relatório de Mapa de Acesso ao RU. 
	 * 
	 * @param calendarioSAE
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> findDiscentesDiasAlimentacaoMapa(Long cpf, AnoPeriodoReferenciaSAE calendarioSAE, int idTipoBolsa, int idSituacaoBolsa ) throws HibernateException, DAOException {
	
		String consulta = 
			"select dias.segunda, dias.terca, dias.quarta, dias.quinta, dias.sexta, dias.sabado, dias.domingo, " +
			"p.nome as nome_pessoa, p.cpf_cnpj as cpf_pessoa, tipo.id_tipo_refeicao_ru, tipo.descricao, " +
			"tipo.hora_inicio, tipo.hora_fim, tipo.minuto_inicio, tipo.minuto_fim, " +
			"b.id_bolsa_auxilio, d.matricula, c.nome, tipobolsa.id_tipo_bolsa_auxilio, tipobolsa.denominacao as tipo_bolsa, tipoBolsa " +
			"from sae.dias_alimentacao dias " +
			"inner join sae.tipo_refeicao_ru tipo on tipo.id_tipo_refeicao_ru = dias.id_tipo_refeicao_ru " + 
			"inner join sae.bolsa_auxilio b on b.id_bolsa_auxilio = dias.id_bolsa_auxilio " +
			"inner join sae.bolsa_auxilio_periodo bap on bap.id_bolsa_auxilio = b.id_bolsa_auxilio " +
			"inner join discente d on d.id_discente = b.id_discente " +
			"inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
			"inner join curso c on c.id_curso = d.id_curso " +
			"inner join sae.tipo_bolsa_auxilio tipobolsa on tipobolsa.id_tipo_bolsa_auxilio = b.id_tipo_bolsa_auxilio ";
		
		StringBuilder strBuild = new StringBuilder();
		strBuild.append(consulta);
				
		Object[] parametros = null;
		
		if (cpf == null) {
			strBuild.append(" WHERE bap.ano = ? AND bap.periodo = ? AND b.id_tipo_bolsa_auxilio = ? and b.id_situacao_bolsa = ? " );
			parametros = new Object[] { calendarioSAE.getAno(), calendarioSAE.getPeriodo(), idTipoBolsa, idSituacaoBolsa };
		}
		else if (cpf != null) {
			strBuild.append(" WHERE bap.ano = ? AND bap.periodo = ? AND p.cpf_cnpj = ? AND b.id_situacao_bolsa = ?");
			parametros = new Object[] { calendarioSAE.getAno(), calendarioSAE.getPeriodo(), cpf, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA };
		}
				
		strBuild.append(" order by p.nome, tipo.id_tipo_refeicao_ru");
		
	      return (List<BolsaAuxilio>) getJdbcTemplate().query(strBuild.toString(), parametros, new ResultSetExtractor() {

	          @SuppressWarnings("rawtypes")
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
	              
	              List<BolsaAuxilio> resultado = null;
	              BolsaAuxilio bolsa = new BolsaAuxilio();
	              
	              bolsa.setId(0);
	              while (rs.next()) {

	                  int idAtual = rs.getInt("id_bolsa_auxilio");
	                  
	                      if (bolsa.getId() != idAtual) {
	                          bolsa = new BolsaAuxilio();
	                          bolsa.setId(rs.getInt("id_bolsa_auxilio"));
	                          
	                          if (resultado == null)
	                            resultado = new ArrayList();
	      
	                          resultado.add(bolsa);
	                      }
	                      
                        DiasAlimentacao detalhe = new DiasAlimentacao();
	      				
                        Pessoa pessoa = new Pessoa();
		      				pessoa.setNome( rs.getString("nome_pessoa") );
		      				pessoa.setCpf_cnpj(rs.getLong("cpf_pessoa"));
	      
	      				Discente discente = new Discente();
		      				discente.setPessoa(pessoa);
		      				discente.setMatricula(rs.getLong("matricula"));
	      				
	      				br.ufrn.sigaa.dominio.Curso c = new br.ufrn.sigaa.dominio.Curso();
	      					c.setNome(rs.getString("nome"));
	      				
	      				discente.setCurso(c);
	      				
	    				TipoRefeicaoRU tipo = new TipoRefeicaoRU();
		    				tipo.setId(rs.getInt("id_tipo_refeicao_ru"));
		    				tipo.setDescricao(rs.getString("descricao"));
		    				
		    				tipo.setHoraInicio(rs.getInt("hora_inicio"));
		    				tipo.setMinutoInicio(rs.getInt("minuto_inicio"));
		    				
		    				tipo.setHoraFim(rs.getInt("hora_fim"));
		    				tipo.setMinutoFim(rs.getInt("minuto_fim"));
	      				
	    				detalhe.setTipoRefeicao(tipo);
	    				
	      				detalhe.setSegunda(rs.getBoolean("segunda"));
	      				detalhe.setTerca(rs.getBoolean("terca"));
	      				detalhe.setQuarta(rs.getBoolean("quarta"));
	      				detalhe.setQuinta(rs.getBoolean("quinta"));
	      				detalhe.setSexta(rs.getBoolean("sexta"));
	      				detalhe.setSabado(rs.getBoolean("sabado"));
	      				detalhe.setDomingo(rs.getBoolean("domingo"));
	               
	      				bolsa.setDiscente(discente);
	      				
	      				TipoBolsaAuxilio tipoBolsa = new TipoBolsaAuxilio();
	      					tipoBolsa.setId(rs.getInt("id_tipo_bolsa_auxilio"));
	      					tipoBolsa.setDenominacao(rs.getString("tipo_bolsa"));
	      				
	      				bolsa.setTipoBolsaAuxilio(tipoBolsa);
	      				
	      				bolsa.getDiasAlimentacao().add(detalhe);
	              }
	              return resultado;
	          }
	        });	      
      }
	
	
	/**
	 * Lista todos os alunos deferidos e contemplados com seus dias de alimentação definidos. 
	 * É utilizado para gerar o relatório de Mapa de Acesso ao RU. 
	 * 
	 * @param calendarioSAE
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<BolsaAuxilio> findDiscentesDiasAlimentacaoMapa(Long cpf) throws HibernateException, DAOException {
	
		String consulta = 
			"select dias.segunda, dias.terca, dias.quarta, dias.quinta, dias.sexta, dias.sabado, dias.domingo, " +
			"p.nome as nome_pessoa, p.cpf_cnpj as cpf_pessoa, tipo.id_tipo_refeicao_ru, tipo.descricao, " +
			"tipo.hora_inicio, tipo.hora_fim, tipo.minuto_inicio, tipo.minuto_fim, " +
			"b.id_bolsa_auxilio, d.matricula, c.nome, tipobolsa.id_tipo_bolsa_auxilio, tipobolsa.denominacao as tipo_bolsa, " +
			"bap.ano as ano, bap.periodo as periodo " +
			"from sae.dias_alimentacao dias " +
			"inner join sae.tipo_refeicao_ru tipo on tipo.id_tipo_refeicao_ru = dias.id_tipo_refeicao_ru " + 
			"inner join sae.bolsa_auxilio b on b.id_bolsa_auxilio = dias.id_bolsa_auxilio " +
			"inner join sae.bolsa_auxilio_periodo bap on bap.id_bolsa_auxilio = b.id_bolsa_auxilio " +
			"inner join discente d on d.id_discente = b.id_discente " +
			"inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
			"inner join curso c on c.id_curso = d.id_curso " +
			"inner join sae.tipo_bolsa_auxilio tipobolsa on tipobolsa.id_tipo_bolsa_auxilio = b.id_tipo_bolsa_auxilio ";
		
		StringBuilder strBuild = new StringBuilder();
		strBuild.append(consulta);
				
		Object[] parametros = null;
		
		if (cpf == null) {
			strBuild.append(" WHERE b.id_tipo_bolsa_auxilio = ? and b.id_situacao_bolsa = ? " );
			parametros = new Object[] { TipoBolsaAuxilio.ALIMENTACAO, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA };
		}
		else if (cpf != null) {
			strBuild.append(" WHERE p.cpf_cnpj = ? AND ( b.id_situacao_bolsa = ? OR b.id_situacao_bolsa = ? ) AND (b.id_tipo_bolsa_auxilio = ? OR b.id_tipo_bolsa_auxilio = ?)");
			parametros = new Object[] {  cpf, SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA, SituacaoBolsaAuxilio.BOLSA_AUXILIO_MORADIA, 
						TipoBolsaAuxilio.RESIDENCIA_GRADUACAO, TipoBolsaAuxilio.RESIDENCIA_POS };
		}
				
		strBuild.append(" order by bap.ano desc, bap.periodo desc, p.nome, tipo.id_tipo_refeicao_ru limit "+NUMERO_REFEICOES_DIARIAS);
		
	      return (List<BolsaAuxilio>) getJdbcTemplate().query(strBuild.toString(), parametros, new ResultSetExtractor() {

	          @SuppressWarnings("rawtypes")
			public Object extractData(ResultSet rs) throws SQLException, DataAccessException {
	              
	              List<BolsaAuxilio> resultado = null;
	              BolsaAuxilio bolsa = new BolsaAuxilio();
	              
	              bolsa.setId(0);
	              while (rs.next()) {
	                  int idAtual = rs.getInt("id_bolsa_auxilio");
	                  
	                      if (bolsa.getId() != idAtual) {
	                          bolsa = new BolsaAuxilio();
	                          bolsa.setId(rs.getInt("id_bolsa_auxilio"));
	                          
	                          if (resultado == null)
	                            resultado = new ArrayList();
	      
	                          resultado.add(bolsa);
	                      }
	                      
                        DiasAlimentacao detalhe = new DiasAlimentacao();
	      				
                        Pessoa pessoa = new Pessoa();
		      				pessoa.setNome( rs.getString("nome_pessoa") );
		      				pessoa.setCpf_cnpj(rs.getLong("cpf_pessoa"));
	      
	      				Discente discente = new Discente();
		      				discente.setPessoa(pessoa);
		      				discente.setMatricula(rs.getLong("matricula"));
	      				
	      				br.ufrn.sigaa.dominio.Curso c = new br.ufrn.sigaa.dominio.Curso();
	      					c.setNome(rs.getString("nome"));
	      				
	      				discente.setCurso(c);
	      				
	    				TipoRefeicaoRU tipo = new TipoRefeicaoRU();
		    				tipo.setId(rs.getInt("id_tipo_refeicao_ru"));
		    				tipo.setDescricao(rs.getString("descricao"));
		    				
		    				tipo.setHoraInicio(rs.getInt("hora_inicio"));
		    				tipo.setMinutoInicio(rs.getInt("minuto_inicio"));
		    				
		    				tipo.setHoraFim(rs.getInt("hora_fim"));
		    				tipo.setMinutoFim(rs.getInt("minuto_fim"));
	      				
	    				detalhe.setTipoRefeicao(tipo);
	    				
	      				detalhe.setSegunda(rs.getBoolean("segunda"));
	      				detalhe.setTerca(rs.getBoolean("terca"));
	      				detalhe.setQuarta(rs.getBoolean("quarta"));
	      				detalhe.setQuinta(rs.getBoolean("quinta"));
	      				detalhe.setSexta(rs.getBoolean("sexta"));
	      				detalhe.setSabado(rs.getBoolean("sabado"));
	      				detalhe.setDomingo(rs.getBoolean("domingo"));
	               
	      				bolsa.setDiscente(discente);
	      				
	      				TipoBolsaAuxilio tipoBolsa = new TipoBolsaAuxilio();
	      					tipoBolsa.setId(rs.getInt("id_tipo_bolsa_auxilio"));
	      					tipoBolsa.setDenominacao(rs.getString("tipo_bolsa"));
	      				
	      				bolsa.setTipoBolsaAuxilio(tipoBolsa);
	      				
	      				bolsa.getDiasAlimentacao().add(detalhe);
	              }
	              return resultado;
	          }
	        });	      
      }

	/**
	 * Retorna os dias de alimentação que estão associados a bolsa do discente.
	 * 
	 * @param idBolsaAuxilio
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<DiasAlimentacao> findDiasAlimentacaoByBolsa(int idBolsaAuxilio) throws HibernateException, DAOException {
		String hql = " select dias from DiasAlimentacao dias where dias.bolsaAuxilio.id = ?";
		Query q = getSession().createQuery(hql);
			q.setInteger(0, idBolsaAuxilio);
			
		List<DiasAlimentacao> diasAliemntAlimentacaos = q.list();
		return diasAliemntAlimentacaos;
	}
	
	/** Cria os dias de alimentação do discente bolsista */
	public void criarDiasAlimentacao( BolsaAuxilio ba, boolean preechido ) {
		if ( preechido ) {
			for (int i = 1; i <= 3; i++) {
				if ( i == TipoRefeicaoRU.CAFE ) {
					getJdbcTemplate().update("insert into sae.dias_alimentacao (id_dias_alimentacao, segunda, terca, quarta, quinta, sexta, " +
							"sabado, domingo, id_tipo_refeicao_ru, id_bolsa_auxilio) values " +
							"(nextval('hibernate_sequence'), falsevalue(), falsevalue(), falsevalue(), falsevalue()," +
							"falsevalue(),falsevalue(),falsevalue(), ?, ?)", new Object[] { i, ba.getId() });
					
				} else { 
					getJdbcTemplate().update("insert into sae.dias_alimentacao (id_dias_alimentacao, segunda, terca, quarta, quinta, sexta, " +
							"sabado, domingo, id_tipo_refeicao_ru, id_bolsa_auxilio) values " +
							"(nextval('hibernate_sequence'), truevalue(),truevalue(),truevalue(),truevalue()," +
							"truevalue(),truevalue(),truevalue(), ?, ?)", new Object[] { i, ba.getId() });
				}
			}
		} else {
			for (int i = 1; i <= 3; i++) {
				getJdbcTemplate().update("insert into sae.dias_alimentacao (id_dias_alimentacao, id_tipo_refeicao_ru, id_bolsa_auxilio) values " +
						"(nextval('hibernate_sequence'), ?, ?)", new Object[] { i, ba.getId() });
			}
		}
	}
	
	/** Altera os dias de alimentação conforme a situação da bolsa */
	public void alterarDiasAlimentacao( BolsaAuxilio ba, boolean preencher ) {
		if (preencher) {
			for (int i = 1; i <= 3; i++) {
				if ( i == TipoRefeicaoRU.CAFE ) {
					getJdbcTemplate().update("UPDATE sae.dias_alimentacao SET segunda = falsevalue(), terca = falsevalue(), " +
							"quarta = falsevalue(), quinta = falsevalue(), sexta = falsevalue(), " +
							"sabado = falsevalue(), domingo = falsevalue() " +
							"WHERE id_bolsa_auxilio = ? ", new Object[] { ba.getId() });
				} else {
					getJdbcTemplate().update("UPDATE sae.dias_alimentacao SET segunda = truevalue(), terca = truevalue(), " +
						"quarta = truevalue(), quinta = truevalue(), sexta =truevalue(), " +
						"sabado = truevalue(), domingo = truevalue() " +
						"WHERE id_bolsa_auxilio = ? ", new Object[] { ba.getId() });
				}
			}
		} else {
			getJdbcTemplate().update("UPDATE sae.dias_alimentacao SET segunda = falsevalue(), terca = falsevalue(), " +
					"quarta = falsevalue(), quinta = falsevalue(), sexta = falsevalue(), sabado = falsevalue(), domingo = falsevalue() " +
					"WHERE id_bolsa_auxilio = ? ", new Object[] { ba.getId() });
		}
		
	}
	
}