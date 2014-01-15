/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Created on 10/03/2010
 *
 */

package br.ufrn.sigaa.arq.dao.ensino.stricto;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.jdbc.core.RowMapper;

import br.ufrn.arq.dao.BDUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.OrientacaoAcademica;
import br.ufrn.sigaa.ensino.stricto.remoto.DiscenteStrictoCapesDTO;
import br.ufrn.sigaa.ensino.stricto.remoto.TeseDissertacaoCapesDTO;

/**
 * DAO responsável pelas consultas das informações de discentes de stricto sensu para o preenchimento do cliente de webService da CAPES.
 * @author Rafael Gomes 
 *
 */
public class DiscenteStrictoCapesDao extends GenericSigaaDAO{

	
	/**
	 * Retorna os dados para popular a classe de transferência dos dados do Objeto {@link br.ufrn.sigaa.ensino.stricto.remoto.DiscenteStrictoCapesDTO}
	 * 
	 */
	public DiscenteStrictoCapesDTO findDiscenteStrictoCapes(long matricula) {
		String sql = 
			" SELECT c.nome AS curso, c.id_curso, c.cod_Programa_CAPES, d.id_discente, p.cpf_cnpj, d.data_cadastro AS data_Matricula, p.data_Nascimento, " +
			"	ds.prazo_maximo_conclusao, ds.data_atualizacao, p.email, p.sexo AS genero, d.nivel, p.nome, pa.nome AS paisNascimento, " +
			"	p.passaporte, d.status AS situacaoDiscente, tcs.descricao AS desc_tipo_curso, tcs.nivel AS tipo_curso" +
			" FROM stricto_sensu.discente_stricto ds " +
			"  INNER JOIN discente d ON d.id_discente = ds.id_discente " +
			"  INNER JOIN comum.pessoa p ON p.id_pessoa = d.id_pessoa " +
			"  INNER JOIN curso c ON c.id_curso = d.id_curso " +
			"  INNER JOIN comum.pais pa ON pa.id_pais = p.id_pais_nacionalidade " +
			"  INNER JOIN stricto_sensu.tipo_curso_stricto tcs ON tcs.id_tipo_curso_stricto = c.id_tipo_curso_stricto " +
			" WHERE d.matricula = ? ";
		
		
		return (DiscenteStrictoCapesDTO) getJdbcTemplate().queryForObject(sql, new Object[] { matricula }, 
				new RowMapper() {
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						DiscenteStrictoCapesDTO dsDTO = new DiscenteStrictoCapesDTO();
						
						dsDTO.setPrograma(rs.getString("cod_Programa_CAPES"));
						dsDTO.setIdCurso(rs.getInt("id_curso"));
						dsDTO.setIdDiscente(rs.getInt("id_discente"));
						dsDTO.setCpf(rs.getString("cpf_cnpj"));
						dsDTO.setDataMatricula(rs.getDate("data_Matricula"));
						dsDTO.setDataNascimento(rs.getDate("data_Nascimento"));
						dsDTO.setDataPrevisaoConclusao(rs.getDate("prazo_maximo_conclusao"));
						dsDTO.setDataSituacaoDiscente(rs.getDate("data_atualizacao"));
						dsDTO.setEmail(rs.getString("email"));
						dsDTO.setGenero(rs.getString("genero"));
						dsDTO.setNivel(rs.getString("nivel"));
						dsDTO.setNome(rs.getString("nome"));
						dsDTO.setPaisNascimento(rs.getString("paisNascimento"));
						dsDTO.setPassaporte(rs.getString("passaporte"));
						dsDTO.setSituacaoDiscente(rs.getString("situacaoDiscente"));
						dsDTO.setTipoCurso(rs.getString("tipo_curso"));
						
						return dsDTO;
					}
				});

	}
	
	/**
	 * Retorna os dados para popular a classe de transferência dos dados do Objeto {@link br.ufrn.sigaa.ensino.stricto.remoto.TeseDissertacaoCapesDTO}
	 * 
	 */
	@SuppressWarnings("unchecked")
	public List<TeseDissertacaoCapesDTO> findTeseDissertacaoCapes(long matricula) {
		
		String sql = 
			" SELECT  d.id_discente, p.id_pessoa, dd.id_dados_defesa, c.nome AS curso, c.cod_Programa_CAPES, " +
			"  	ac.nome AS Area_conhecimento, ac.codigo AS CODIGO_AREA_CONHECIMENTO, p.cpf_cnpj AS CPF_AUTOR, bp.id_banca_pos, " +
			"	bp.data AS data_defesa, d.nivel, dd.paginas, dd.palavras_Chave, p.passaporte, dd.resumo AS RESUMO_DEFESA, " +
			"	dd.titulo,  " +
			"	(SELECT distinct(p2.nome) FROM graduacao.orientacao_academica o " +
			"		JOIN rh.servidor s ON o.id_servidor = s.id_servidor JOIN comum.pessoa p2 ON s.id_pessoa = p2.id_pessoa " +
			"		WHERE o.tipoorientacao = '" + OrientacaoAcademica.ORIENTADOR + "' AND o.id_discente = d.id_discente  " +
			"		AND o.cancelado = falseValue() " + BDUtils.limit(1)+ " ) as orientador_nome, " +
			"	(SELECT distinct(p2.cpf_cnpj) FROM graduacao.orientacao_academica o " +
			"		JOIN rh.servidor s ON o.id_servidor = s.id_servidor JOIN comum.pessoa p2 ON s.id_pessoa = p2.id_pessoa " +
			"		WHERE o.tipoorientacao = '" + OrientacaoAcademica.ORIENTADOR + "' AND o.id_discente = d.id_discente  " +
			"		AND o.cancelado = falseValue() " + BDUtils.limit(1)+ " ) as orientador_cpf, " +
			"	(SELECT distinct(p2.nome) FROM graduacao.orientacao_academica o " +
			"		JOIN rh.servidor s ON o.id_servidor = s.id_servidor JOIN comum.pessoa p2 ON s.id_pessoa = p2.id_pessoa " +
			"		WHERE o.tipoorientacao = '" + OrientacaoAcademica.CoORIENTADOR + "' AND o.id_discente = d.id_discente  " +
			"		AND o.cancelado = falseValue() " + BDUtils.limit(1)+ " ) as CoOrientador_nome, " +
			"	(SELECT distinct(p2.cpf_cnpj) FROM graduacao.orientacao_academica o " +
			"		JOIN rh.servidor s ON o.id_servidor = s.id_servidor JOIN comum.pessoa p2 ON s.id_pessoa = p2.id_pessoa " +
			"		WHERE o.tipoorientacao = '" + OrientacaoAcademica.CoORIENTADOR + "' AND o.id_discente = d.id_discente  " +
			"		AND o.cancelado = falseValue() " + BDUtils.limit(1)+ " ) as CoOrientador_cpf 	 " +
			" FROM stricto_sensu.discente_stricto ds " +
			"	INNER JOIN discente d ON d.id_discente = ds.id_discente " +
			"	INNER JOIN comum.pessoa p ON p.id_pessoa = d.id_pessoa " +
			"   INNER JOIN curso c ON c.id_curso = d.id_curso " +
			"   INNER JOIN stricto_sensu.dados_defesa dd ON dd.id_discente = ds.id_discente " +
			"	INNER JOIN stricto_sensu.banca_pos bp ON bp.id_dados_defesa = dd.id_dados_defesa " +
			"	INNER JOIN comum.area_conhecimento_cnpq ac ON ac.id_area_conhecimento_cnpq = dd.id_area " +
			" WHERE d.matricula = ? " +
			" ORDER BY dd.data_cadastro DESC " + BDUtils.limit(1)+ " ";
		
		List<TeseDissertacaoCapesDTO> lista = getJdbcTemplate().query(sql, new Object[] { matricula }, 
				new RowMapper() {
					public Object mapRow(ResultSet rs, int arg1) throws SQLException {
						
						TeseDissertacaoCapesDTO tdcDTO = new TeseDissertacaoCapesDTO();
					
						String[] codArea = {rs.getString("CODIGO_AREA_CONHECIMENTO")};
						tdcDTO.setAreasConhecimento(codArea);
						tdcDTO.setIdDiscente(rs.getInt("id_discente"));
						tdcDTO.setPrograma(rs.getString("cod_Programa_CAPES"));
						tdcDTO.setCpfAutor(rs.getString("CPF_AUTOR"));
						tdcDTO.setDataDefesa(rs.getDate("data_defesa"));
						tdcDTO.setNivel(rs.getString("nivel"));
						tdcDTO.setPaginas(rs.getInt("paginas"));
						tdcDTO.setPalavrasChave(rs.getString("palavras_Chave"));
						tdcDTO.setPassaporteAutor(rs.getString("passaporte"));
						tdcDTO.setResumo(rs.getString("RESUMO_DEFESA"));
						tdcDTO.setTitulo(rs.getString("titulo"));
						tdcDTO.setIdioma("Português");
						tdcDTO.setBiblioteca("Biblioteca Central Zila Mamede");
						tdcDTO.setIdBancaPos(rs.getInt("id_banca_pos"));
												
						return tdcDTO;
					}
				});
		
		return lista;

	}
	
	/**
	 * Responsável pela consulta das informações de bolsa dos discentes de stricto no SIPAC.
	 */
//	public BolsaCapes findDadosBolsistasSipac(int[] idDiscente) throws ArqException {
//		Connection con = null;
//		
//		final String SIGLA_SIPAC = RepositorioDadosInstitucionais.get("siglaSipac");
//		
//		try {
//			
//			//lista de hashMap com resultado
//			List<HashMap<String, Object>> resultado =  new ArrayList<HashMap<String,Object>>();
//			
//			con = Database.getInstance().getSipacConnection();
//			if (con == null)
//				throw new ArqException("Erro ao obter conexão com " + SIGLA_SIPAC + "!");
//
//			
//			ResultSet rs = null;
//			StringBuilder sql = new StringBuilder("" +
//				" SELECT aluno.id_discente as id_discente, aluno.id_aluno,  " +
//				"   tipo_bolsa.id AS id_tipo_bolsa, tipo_bolsa.denominacao as denominacao, " +
//				"	bolsa.inicio, bolsa.fim, bolsa.data_finalizacao, " +
//				"	unidade.sigla AS sigla_unidade_pagadora, unidade.nome AS unidade_pagadora, " +
//				"	unidade_responsavel.sigla AS sigla_unidade_pagadora_responsavel" +
//				"	FROM bolsas.bolsista bolsista " +
//				"		INNER JOIN bolsas.bolsa  bolsa ON bolsa.id_bolsista = bolsista.id " +
//				"		INNER JOIN academico.aluno aluno ON bolsista.id_aluno = aluno.id_aluno " +
//				"		INNER JOIN bolsas.tipo_bolsa tipo_bolsa ON tipo_bolsa.id = bolsa.id_tipo_bolsa " +
//				"		INNER JOIN comum.unidade unidade ON unidade.id_unidade = tipo_bolsa.id_unidade_pagadora " +
//				"		INNER JOIN comum.unidade unidade_responsavel ON unidade_responsavel.id_unidade = unidade.unidade_responsavel " +
//				"	WHERE aluno.id_discente IN" + UFRNUtils.gerarStringIn(idDiscente)); 
//			
//			PreparedStatement pst = con.prepareStatement(sql.toString());
//			
//			rs = pst.executeQuery();
//			
//			//construindo o mapa com valores
//			while (rs.next()) {
//				 HashMap<String, Object> mapa = new HashMap<String, Object>();
//				 mapa.put("id_discente", rs.getInt("id_discente"));
//				 mapa.put("id_aluno", rs.getInt("id_aluno"));
//				 mapa.put("id_tipo_bolsa", rs.getInt("id_tipo_bolsa"));
//				 mapa.put("denominacao", rs.getString("denominacao"));
//				 mapa.put("inicio", rs.getDate("inicio"));
//				 mapa.put("fim", rs.getDate("fim"));
//				 mapa.put("data_finalizacao", rs.getDate("data_finalizacao"));
//				 mapa.put("sigla_unidade_pagadora", rs.getString("sigla_unidade_pagadora"));
//				 mapa.put("sigla_unidade_pagadora_responsavel", rs.getString("sigla_unidade_pagadora_responsavel"));
//				 resultado.add(mapa);
//			}
//			
////			BolsaCapes bc = new BolsaCapes();
////			for (HashMap<String, Object> listaBolsa : resultado){
////						
////					DiscenteCapesHelper dcHelper = new DiscenteCapesHelper();
////					
////					bc.setFimBolsa(dcHelper.getDataCalendar((Date) listaBolsa.get("fim") ));
////					bc.setInicioBolsa(dcHelper.getDataCalendar((Date) listaBolsa.get("inicio") ));
////					bc.setOutraAgencia(String.valueOf( listaBolsa.get("sigla_unidade_pagadora_responsavel") ));
////					bc.setAgencia(new AgenciaCapes(AgenciaCapes._OUTRA));
////					
////					Date dataFim = (Date) listaBolsa.get("fim");
////					
////					SituacaoBolsaCapes situacao = new SituacaoBolsaCapes( dataFim.after(new Date()) ? "A" : "S");
////					bc.setSituacaoBolsa(situacao);
////					
////			}
////			 if(bc.getAgencia() != null)
////				 return bc;
////			 else 
////				 return null;
//			
//
//		} catch (Exception e) {
//			e.printStackTrace();
//			throw new ArqException(e);
//		} finally {
//			Database.getInstance().close(con);
//		}
//	}
//		

}
