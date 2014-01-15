package br.ufrn.sigaa.arq.dao.pesquisa;

import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.rh.dominio.Ativo;
import br.ufrn.rh.dominio.Categoria;
import br.ufrn.rh.dominio.Formacao;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.pesquisa.dominio.EditalPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.StatusGrupoPesquisa;
import br.ufrn.sigaa.pesquisa.dominio.TipoBolsaPesquisa;
import br.ufrn.sigaa.pesquisa.relatorios.LinhaRelatorioCNPQ;
import br.ufrn.sigaa.projetos.dominio.CategoriaMembro;
import br.ufrn.sigaa.projetos.dominio.TipoProjeto;

/**
 * Responsável pela consultas para a geração dos relatórios CNPq
 * 
 * @author Jean Guerethes
 */
public class RelatorioCNPQDao extends GenericSigaaDAO {
	
	/**
	 * Busca todos os discente da instituição
	 * 
	 * @param agruparPorDepartamento
	 * @param unidade
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findCorpoDiscente(TipoBolsaPesquisa tipoBolsaPesquisa) throws DAOException {

		String consulta = "SELECT ";
	   		if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
	   			consulta += "sum(CASE WHEN d.nivel = '" + NivelEnsino.GRADUACAO + "' and d.id_curso in ( select id_curso from pesquisa.cursos_tecnologicos ) THEN 1 ELSE 0 end) as graduacao_tecn,";
				consulta +=	" sum(CASE WHEN d.nivel = '" + NivelEnsino.GRADUACAO + "' THEN 1 ELSE 0 end) as graduacao, ";
				
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
				consulta += "sum(CASE WHEN d.nivel = 'E' and c.id_tipo_curso_stricto = 2 and d.id_curso in ( select id_curso from pesquisa.cursos_tecnologicos ) THEN 1 ELSE 0 end) as mestrado_tecn,";
				consulta +=	" sum(CASE WHEN d.nivel = 'E' and c.id_tipo_curso_stricto = 2 THEN 1 ELSE 0 end) as mestrado,";
				
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
				consulta += "sum(CASE WHEN d.nivel = 'E' and c.id_tipo_curso_stricto = 1 and d.id_curso in ( select id_curso from pesquisa.cursos_tecnologicos ) THEN 1 ELSE 0 end) as mestrado_profissionalizante_tecn,";
				consulta +=	" sum(CASE WHEN d.nivel = 'E' and c.id_tipo_curso_stricto = 1 THEN 1 ELSE 0 end) as mestrado_profissionalizante,";
				
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
				consulta += "sum(CASE WHEN d.nivel = 'D' and c.id_tipo_curso_stricto = 3 and d.id_curso in ( select id_curso from pesquisa.cursos_tecnologicos ) THEN 1 ELSE 0 end) as doutorado_tecn,";
				consulta +=	" sum(CASE WHEN d.nivel = 'D' and c.id_tipo_curso_stricto = 3 THEN 1 ELSE 0 end) as doutorado";
				
			consulta +=	" from discente d" +
						" join curso c using ( id_curso )" +
						" where d.status in " + UFRNUtils.gerarStringIn(new int[]{ StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO }) + 
						" and d.nivel in " + UFRNUtils.gerarStringIn(new char[]{ NivelEnsino.GRADUACAO, NivelEnsino.MESTRADO, NivelEnsino.DOUTORADO });
			
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll(LinhaRelatorioCNPQ.getCabecalhoCorpoDiscente( tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT ));
				linha.setDescricaoLinha("Total de Alunos");
				
				if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT) {
					linha.getValoresRelatorio().add( ((BigInteger) item[0]).toString() + " / " + ((BigInteger) item[1]).toString() );
					linha.getValoresRelatorio().add( ((BigInteger) item[2]).toString() + " / " + ((BigInteger) item[3]).toString() );
					linha.getValoresRelatorio().add( ((BigInteger) item[4]).toString() + " / " + ((BigInteger) item[5]).toString() );
					linha.getValoresRelatorio().add( ((BigInteger) item[6]).toString() + " / " + ((BigInteger) item[7]).toString() );
				} else {
					linha.getValoresRelatorio().add(((BigInteger) item[0]).toString());
					linha.getValoresRelatorio().add(((BigInteger) item[1]).toString());
					linha.getValoresRelatorio().add(((BigInteger) item[2]).toString());
				}
				
				result.add(linha);
			}
			
			return result;
	}
	
	/**
	 * Busca todos os docente da instituição
	 * 
	 * @param agruparPorDepartamento
	 * @param unidade
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findCorpoDocente() throws DAOException {
		
			String consulta = "select "+
						" sum(CASE WHEN s.id_formacao = " + Formacao.MESTRE + " and ( regime_trabalho >= 40 or dedicacao_exclusiva ) THEN 1 ELSE 0 end) as horas_40,"+ 
						" sum(CASE WHEN s.id_formacao = " + Formacao.MESTRE + " and ( regime_trabalho >= 20 and regime_trabalho < 40 ) THEN 1 ELSE 0 end) as horas_20,"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.MESTRE + " and ( regime_trabalho < 20 ) THEN 1 ELSE 0 end) as menos_20_horas,"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.MESTRE + " THEN 1 ELSE 0 end) as total_mestrado"+
						" from rh.servidor s where id_ativo = " + Ativo.SERVIDOR_ATIVO +
						" union"+
						" select"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.DOUTOR + " and ( regime_trabalho >= 40 or dedicacao_exclusiva ) THEN 1 ELSE 0 end) as horas_40,"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.DOUTOR + " and ( regime_trabalho >= 20 and regime_trabalho < 40 ) THEN 1 ELSE 0 end) as horas_20,"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.DOUTOR + " and ( regime_trabalho < 20 ) THEN 1 ELSE 0 end) as menos_20_horas,"+
						" sum(CASE WHEN s.id_formacao = " + Formacao.DOUTOR + " THEN 1 ELSE 0 end) as total_doutorado"+
						" from rh.servidor s"+
						" where id_ativo = " + Ativo.SERVIDOR_ATIVO;
				
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoCorpoDocente() );
				linha.setDescricaoLinha(result.isEmpty() ? "Mestre (número)" : "Doutores (número)");
				linha.getValoresRelatorio().add(((BigInteger) item[0]).toString());
				linha.getValoresRelatorio().add(((BigInteger) item[1]).toString());
				linha.getValoresRelatorio().add(((BigInteger) item[2]).toString());
				linha.getValoresRelatorio().add(((BigInteger) item[3]).toString());
				result.add(linha);
			}
			
			return result;
	}
	
	/**
	 * 4.4 Sobre a pesquisa Científica desenvolvida na Instituição
	 * 
	 * @param agruparPorDepartamento
	 * @param unidade
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findGruposELinhasPesquisa( TipoBolsaPesquisa tipoBolsaPesquisa ) throws DAOException {
			
			String status = UFRNUtils.gerarStringIn(new int[]{ StatusGrupoPesquisa.CONSOLIDADO, StatusGrupoPesquisa.EM_CONSOLIDACAO, StatusGrupoPesquisa.JUNIOR });
		
			String consulta;
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT) {
				consulta = "select count(*) as total " +
						   " from pesquisa.grupo_pesquisa " +
						   " where ativo and status in " + status +
						   " and id_area_conhecimento_cnpq in ( select id_area from pesquisa.areas_tecnologicas )" +
						 " union" +
						   " select count(*)" +
						   " from pesquisa.linha_pesquisa lp" +
						   " join pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa )" +
						   " where gp.ativo and gp.status in " + status + " and lp.inativa is false" +
						   " and gp.id_area_conhecimento_cnpq in ( select id_area from pesquisa.areas_tecnologicas ) " +
						" union" +
						   " select count(*)" +
						   " from projetos.membro_projeto mp" +
						   " join projetos.projeto p using ( id_projeto )" +
						   " join rh.servidor s on ( mp.id_servidor = s.id_servidor )" +
						   " where p.id_tipo_projeto = " + TipoProjeto.PESQUISA + " and s.id_formacao = " + Formacao.DOUTOR +
						   " and p.id_area_conhecimento_cnpq in ( select id_area from pesquisa.areas_tecnologicas )";
			} else {
				consulta = "select count(*) as total " +
						   " from pesquisa.grupo_pesquisa where ativo and status in " + status +
						" union" +
						   " select count(*)" +
						   " from pesquisa.linha_pesquisa lp" +
						   " join pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa )" +
						   " where gp.ativo and gp.status in " + status + " and lp.inativa is false" +
						" union" +
						  " select count(*)" +
						  " from projetos.membro_projeto mp" +
						  " join projetos.projeto p using ( id_projeto )" +
						  " join rh.servidor s on ( mp.id_servidor = s.id_servidor )" +
						  " where p.id_tipo_projeto = " + TipoProjeto.PESQUISA + " and s.id_formacao = " + Formacao.DOUTOR ;
			}
			
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<BigInteger> list = getSession().createSQLQuery(consulta).list();
			for (BigInteger item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoPesquisaDesenvolvidaNaInstituicao() );
				linha.setDescricaoLinha( LinhaRelatorioCNPQ.descricaoLinhaDesenvolvimentoInstituicao(result.size(), tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT ));
				linha.getValoresRelatorio().add((item).toString());
				result.add(linha);
			}
			return result;
	}

	/**
	 * Número de bolsas de outros programas de iniciação Científica na Instituição
	 * 
	 * @param agruparPorDepartamento
	 * @param unidade
	 * @param idEdital
	 * @return
	 * @throws DAOException
	 * @throws SQLException
	 */
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findNumeroBolsasIniciacaoCientificaInstituicao( TipoBolsaPesquisa tipoBolsaPesquisa ) throws DAOException {
			String bolsasInstituicao = ""; 	
			String bolsasVoluntario = "";
			int idVoluntario;
			
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT) {
				bolsasInstituicao = UFRNUtils.gerarStringIn(new int[]{ TipoBolsaPesquisa.REUNI_IT, TipoBolsaPesquisa.PROPESQ_IT });
				bolsasVoluntario = UFRNUtils.gerarStringIn(new int[]{ TipoBolsaPesquisa.REUNI_IT, TipoBolsaPesquisa.PROPESQ_IT, 
					TipoBolsaPesquisa.PIBIT, TipoBolsaPesquisa.VOLUNTARIO_IT });
				idVoluntario = TipoBolsaPesquisa.VOLUNTARIO_IT;
			} else{
				bolsasInstituicao = UFRNUtils.gerarStringIn(new int[]{ TipoBolsaPesquisa.PROPESQ, TipoBolsaPesquisa.REUNI_IC });
				bolsasVoluntario = UFRNUtils.gerarStringIn(new int[]{ TipoBolsaPesquisa.REUNI_IC, TipoBolsaPesquisa.PROPESQ, 
						TipoBolsaPesquisa.PIBIC, TipoBolsaPesquisa.VOLUNTARIO });
				idVoluntario = TipoBolsaPesquisa.VOLUNTARIO;
			}
			
			String consulta = "select cb.descricao," +
					" sum(CASE WHEN pt.tipo_bolsa in " +  bolsasInstituicao + " THEN 1 ELSE 0 end) as it_instituicao," +
					" sum(CASE WHEN pt.tipo_bolsa = " + idVoluntario + " THEN 1 ELSE 0 end) as it_voluntario," +
					" sum(CASE WHEN pt.tipo_bolsa not in " + bolsasVoluntario + " THEN 1 ELSE 0 end) as it_outras_inst";
					
			if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
					consulta += ", sum(CASE WHEN pt.tipo_bolsa = " + TipoBolsaPesquisa.PIBIC + " THEN 1 ELSE 0 end) as bolsas_pibic";
					
				consulta +=	" from pesquisa.plano_trabalho pt" +
						" join pesquisa.cota_bolsas cb using ( id_cota_bolsas )" +
						" group by cb.descricao" +
						" order by cb.descricao";
				
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( 
						LinhaRelatorioCNPQ.getCabecalhoIniciacaoAoDesenvolvimento(tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT) );
				linha.setDescricaoLinha( (String) item[0] );
				
				linha.getValoresRelatorio().add(((BigInteger) item[1]).toString());
				//Bolsas do tipo da Fundação à Pesquisa do Estado/Secretaria de C&T, não consta no sistema.
				linha.getValoresRelatorio().add(((BigInteger) item[2]).toString());
				linha.getValoresRelatorio().add("-");
				linha.getValoresRelatorio().add(((BigInteger) item[3]).toString());
				
				if (tipoBolsaPesquisa.getId() == TipoBolsaPesquisa.PIBIT)
					linha.getValoresRelatorio().add(((BigInteger) item[4]).toString());
				
				result.add(linha);
			}
			return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findPesquisadoresBolsasAprovadas( EditalPesquisa edital ) throws DAOException {
			
			String consulta = "select distinct p.nome, c.quantidade" +
					" from pesquisa.cota_docente cd" +
					" join prodocente.bolsa_obtida bo on ( cd.id_docente = bo.id_servidor )" +
					" join rh.servidor s using ( id_servidor )" +
					" join comum.pessoa p using ( id_pessoa )" +
					" join prodocente.tipo_bolsa tb on ( bo.id_tipo_bolsa = tb.id_tipo_bolsa )" +
					" join pesquisa.cotas c on ( c.id_cota_docente = cd.id )" +
					" where tb.produtividade and c.quantidade > 0" +
					" and cd.id_edital_pesquisa = " + edital.getId() ;
					
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoPesquisadoresCNPQ() );
				linha.setDescricaoLinha( (String) item[0] );
				linha.getValoresRelatorio().add(((Integer) item[1]).toString());
				result.add(linha);
			}
			return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findBolsistaAreaConhecimento( TipoBolsaPesquisa tipoBolsaPesquisa ) throws DAOException {
			
			String consulta = "select acc2.nome, count(*) as total" +
					" from pesquisa.plano_trabalho pt" +
					" join pesquisa.membro_projeto_discente mpd using ( id_membro_projeto_discente )" +
					" join pesquisa.projeto_pesquisa pp on ( pt.id_projeto_pesquisa = pp.id_projeto_pesquisa )" +
					" join comum.area_conhecimento_cnpq acc on ( pp.id_area_conhecimento_cnpq = acc.id_area_conhecimento_cnpq )" +
					" join comum.area_conhecimento_cnpq acc2 on ( acc.id_grande_area = acc2.id_area_conhecimento_cnpq )" +
					" where mpd.tipo_bolsa = " + tipoBolsaPesquisa.getId() +
					" and mpd.data_fim is null and data_finalizacao is null"+
					" group by acc2.nome, acc2.id_area_conhecimento_cnpq";
					
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoGrandeAreaConhecimento() );
				linha.setDescricaoLinha( (String) item[0] );
				linha.getValoresRelatorio().add(((BigInteger) item[1]).toString());
				result.add(linha);
			}
			return result;
	}
	
	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findComiteExterno() throws DAOException {
			
			String consulta = "select c.nome as membro, acc2.nome as area" +
					" from pesquisa.consultor c" +
					" join comum.area_conhecimento_cnpq acc on ( c.id_area_conhecimento_cnpq = acc.id_area_conhecimento_cnpq )" +
					" join comum.area_conhecimento_cnpq acc2 on ( acc.id_grande_area = acc2.id_area_conhecimento_cnpq )" +
					" join pesquisa.consultoria_especial ce on ( c.id_consultor = ce.id_consultor )" +
					" where interno is false and ce.data_fim >= now()" +
					" order by acc2.nome, c.nome";
					
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoComiteExterno() );
				linha.getValoresRelatorio().add((String) item[0]);
				linha.getValoresRelatorio().add("-");
				linha.getValoresRelatorio().add((String) item[1]);
				linha.getValoresRelatorio().add("-");
				result.add(linha);
			}
			return result;
	}

	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> findComiteInstituicional() throws DAOException {
			
			String consulta = "select c.nome, car.denominacao, acc2.nome" +
					" from pesquisa.consultor c" +
					" join comum.area_conhecimento_cnpq acc on ( c.id_area_conhecimento_cnpq = acc.id_area_conhecimento_cnpq )" +
					" join comum.area_conhecimento_cnpq acc2 on ( acc.id_grande_area = acc2.id_area_conhecimento_cnpq )" +
					" join rh.servidor s on ( s.id_servidor = c.id_servidor )" +
					" join rh.cargo car on ( s.id_cargo = car.id )" +
					" join pesquisa.consultoria_especial ce on ( c.id_consultor = ce.id_consultor )" +
					" where interno is true and ce.data_fim >= now()" +
					" order by acc2.nome, car.denominacao, c.nome";
					
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getCabecalhoComiteExterno() );
				linha.getValoresRelatorio().add((String) item[0]);
				linha.getValoresRelatorio().add((String) item[1]);
				linha.getValoresRelatorio().add((String) item[2]);
				linha.getValoresRelatorio().add("-");
				result.add(linha);
			}
			return result;
	}

	@SuppressWarnings("unchecked")
	public Collection<LinhaRelatorioCNPQ> indicadoresGerais() throws DAOException {
			
			String consulta = "SELECT CAST(sum(x.grupos) as integer) as grupos, CAST(sum(x.pesquisadores) as integer) as pesquisadores," +
					" CAST(sum(x.doutores) as integer) as doutores, CAST(sum(x.estudantes) as integer) as estudantes, " +
					" CAST(sum(x.tecnicos) as integer) as tecnicos, CAST(sum(x.linhas) as integer) as linhas" +
					" FROM (" +
						" SELECT COUNT(*) as grupos, CAST(NULL AS integer) AS pesquisadores, CAST(NULL AS integer) AS doutores, " +
						" CAST(NULL AS integer) AS estudantes, CAST(NULL AS integer) AS tecnicos, CAST(NULL AS integer) AS linhas" +
						" FROM pesquisa.grupo_pesquisa WHERE ativo" +
					" UNION ALL" +
						" SELECT CAST(NULL AS integer) AS grupos, count(*) as pesquisadores, CAST(NULL AS integer) AS doutores, " +
						" CAST(NULL AS integer) AS estudantes, CAST(NULL AS integer) AS tecnicos, CAST(NULL AS integer) AS linhas " +
						" FROM pesquisa.membro_grupo_pesquisa WHERE id_categoria_equipe = " + CategoriaMembro.DOCENTE +
					" UNION ALL " +
						" SELECT CAST(NULL AS integer) AS grupos, CAST(NULL AS integer) AS pesquisadores, count(*) as doutores, " +
						" CAST(NULL AS integer) AS estudantes, CAST(NULL AS integer) AS tecnicos, CAST(NULL AS integer) AS linhas" +
						" FROM pesquisa.membro_grupo_pesquisa mgp" +
						" JOIN rh.servidor s using ( id_servidor ) WHERE id_categoria_equipe = " + CategoriaMembro.DOCENTE + 
						" and s.id_formacao = " + Formacao.DOUTOR +
					" UNION ALL " +
						" SELECT CAST(NULL AS integer) AS grupos, CAST(NULL AS integer) AS pesquisadores, CAST(NULL AS integer) AS doutores, " +
						" count(*) as estudantes, CAST(NULL AS integer) AS tecnicos, CAST(NULL AS integer) AS linhas " +
						" FROM pesquisa.membro_grupo_pesquisa WHERE id_categoria_equipe = " + CategoriaMembro.DISCENTE +
					" UNION ALL " +
						" SELECT CAST(NULL AS integer) AS grupos, CAST(NULL AS integer) AS pesquisadores, CAST(NULL AS integer) AS doutores, " +
						" CAST(NULL AS integer) AS estudantes, count(*) as tecnicos, CAST(NULL AS integer) AS linhas  " +
						" FROM pesquisa.membro_grupo_pesquisa" +
						" JOIN rh.servidor s using ( id_servidor ) WHERE id_categoria_equipe = " + CategoriaMembro.SERVIDOR + 
						" and id_categoria = " + Categoria.TECNICO_ADMINISTRATIVO +
					"  UNION ALL " +
						" SELECT CAST(NULL AS integer) AS grupos, CAST(NULL AS integer) AS pesquisadores, CAST(NULL AS integer) AS doutores, " +
						" CAST(NULL AS integer) AS estudantes, CAST(NULL AS integer) AS tecnicos, count(*) as linhas" +
						" FROM pesquisa.linha_pesquisa lp" +
						" JOIN pesquisa.grupo_pesquisa gp using ( id_grupo_pesquisa )" +
						" WHERE inativa is false and gp.ativo is true" +
					" ) as x; ";
					
			Collection<LinhaRelatorioCNPQ> result = new ArrayList<LinhaRelatorioCNPQ>();
			List<Object[]> list = getSession().createSQLQuery(consulta).list();
			for (Object[] item : list) {
				LinhaRelatorioCNPQ linha = new LinhaRelatorioCNPQ();
				linha.getCabecalho().putAll( LinhaRelatorioCNPQ.getIndicadoresGerais() );
				linha.setDescricaoLinha("Último Censo");
				linha.getValoresRelatorio().add(((Integer) item[0]).toString());
				linha.getValoresRelatorio().add(((Integer) item[1]).toString());
				linha.getValoresRelatorio().add(((Integer) item[2]).toString());
				linha.getValoresRelatorio().add(((Integer) item[3]).toString());
				linha.getValoresRelatorio().add(((Integer) item[4]).toString());
				linha.getValoresRelatorio().add(((Integer) item[5]).toString());
			
				//P/G
				linha.getValoresRelatorio().add( ( String.valueOf(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(linha.getValoresRelatorio().get(1)) / Double.parseDouble(linha.getValoresRelatorio().get(0))))));
				//D/G
				linha.getValoresRelatorio().add( ( String.valueOf(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(linha.getValoresRelatorio().get(2)) / Double.parseDouble(linha.getValoresRelatorio().get(0))))));
				//E/G
				linha.getValoresRelatorio().add( ( String.valueOf(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(linha.getValoresRelatorio().get(3)) / Double.parseDouble(linha.getValoresRelatorio().get(0))))));

				//T/G
				linha.getValoresRelatorio().add( ( String.valueOf(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(linha.getValoresRelatorio().get(4)) / Double.parseDouble(linha.getValoresRelatorio().get(0))))));
				//L/G
				linha.getValoresRelatorio().add( ( String.valueOf(
						Formatador.getInstance().formatarDecimal1(
								Double.parseDouble(linha.getValoresRelatorio().get(5)) / Double.parseDouble(linha.getValoresRelatorio().get(0))))));

				result.add(linha);
			}
			
			return result;
	}

}