/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Criado em 27/05/2011
 *
 */
package br.ufrn.sigaa.ensino.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.dao.Database;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.LimiteResultadosException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.dominio.UnidadeFederativa;
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.jsf.BuscaTurmaMBean;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Municipio;

/**
 * Consultas específicas para turmas de EAD. <br/>
 * Baseada na classe {@link TurmaDao}
 * 
 * @author Édipo Elder F. de Melo
 * 
 */
public class TurmaEADDao extends GenericSigaaDAO {

	/** Define o limite de resultados da busca por turmas. */
	private static final int LIMITE_RESULTADOS_BUSCA_TURMA = 500;

	/**
	 * Construtor padrão 
	 */
	public TurmaEADDao() {
	}
	
	/**
	 * Seta os parâmetros informados no PreparedStatement passado
	 *
	 * @param nome
	 * @param codigo
	 * @param tipo
	 * @param depto
	 * @param curso
	 * @param polo
	 * @param st
	 * @param ano
	 * @param periodo
	 * @throws SQLException
	 */
	private void setParametrosTurma(String codigo, Curso curso, Polo polo, PreparedStatement st) throws SQLException {
		int i = 1;
		if (curso != null && curso.getId() > 0){
			st.setInt(i++, curso.getId());
			st.setInt(i++, curso.getId());
		}	
		if (polo != null && polo.getId() > 0)
			st.setInt(i++, polo.getId());
		if (codigo != null && codigo.length() > 0)
			st.setString(i++, codigo);
	}
	
	/**
	 * Busca por turmas abertas. Filtra a partir de diversos atributos dos
	 * componentes dessas turmas.
	 * Só procurar por turmas de componentes matriculáveis
	 * BUSCA USADA PARA MATRÍCULA DE GRADUAÇÃO.
	 *
	 * @param nomeComponente
	 * @param codigo
	 * @param depto
	 * @param tipo
	 * @return
	 * @throws DAOException
	 */
	public Collection<Turma> findAbertasByComponenteCurricular( 
			String nomeComponente, 
			String codigo, 
			Curso curso, 
			Polo polo,
			Integer ano, 
			Integer periodo,
			Boolean matriculaveis, 
			char nivel, 
			Integer... tiposTurma) throws DAOException, LimiteResultadosException {
		Connection con = null;
		PreparedStatement st = null;
		ResultSet rs = null;
		try {
			con = Database.getInstance().getSigaaConnection();

			StringBuffer restoSQL = new StringBuffer();

			restoSQL.append(" FROM ensino.componente_curricular cc " +
				" join ensino.componente_curricular_detalhes ccd on ( cc.id_detalhe = ccd.id_componente_detalhes ) " +
				" join ensino.turma t on ( cc.id_disciplina = t.id_disciplina ) " +
				" left join curso on (t.id_curso = curso.id_curso) " +
				" left join graduacao.reserva_curso rc on (t.id_turma = rc.id_turma) " +
				" left join graduacao.matriz_curricular mc on (mc.id_matriz_curricular = rc.id_matriz_curricular) " +
				" left join ensino.docente_turma dt on ( t.id_turma = dt.id_turma ) " +
				" left join rh.servidor s on (dt.id_docente = s.id_servidor) " +
				" left join comum.pessoa p on ( p.id_pessoa = s.id_pessoa ) " +
				" left join ead.polo polo on ( polo.id_polo = t.id_polo ) " +
				" left join comum.municipio municipio on (municipio.id_municipio = polo.id_cidade)" +
				" left join comum.unidade_federativa uf on (municipio.id_unidade_federativa = uf.id_unidade_federativa)");
			
			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				restoSQL.append( " join comum.unidade unidade on ( cc.id_unidade = unidade.id_unidade ) " );
			}

			restoSQL.append(" WHERE t.id_turma_bloco is null AND t.agrupadora = falseValue()" + 
					" AND t.id_situacao_turma in " + gerarStringIn(new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA}));
			restoSQL.append(" AND t.distancia = trueValue()");
			
			if (curso != null && curso.getId() > 0) {
				restoSQL.append(" and (" +
						" t.id_curso = ? " +
						" or (rc.id_reserva_curso is not null and mc.id_curso = ?)" +
						" or (t.id_curso is null and rc.id_reserva_curso is null) " +
						" ) ");
			}
			
			if (NivelEnsino.isValido(nivel)){
				if( NivelEnsino.isAlgumNivelStricto(nivel) ){
					restoSQL.append(" and cc.nivel in " + gerarStringIn( NivelEnsino.getNiveisStricto() ));
				}else
					restoSQL.append(" and cc.nivel= '" + nivel+"'");
			}
			if (matriculaveis != null)
				restoSQL.append(" and cc.matriculavel= " + matriculaveis);

			if (polo != null && polo.getId() > 0)
				restoSQL.append(" and t.id_polo = ?");
			if (codigo != null && codigo.length() > 0)
				restoSQL.append(" and ccd.codigo = ?");
			if (nomeComponente != null && nomeComponente.length() > 0)
				restoSQL.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("ccd.nome") +
						" like "+UFRNUtils.toAsciiUpperUTF8("'"+UFRNUtils.trataAspasSimples(nomeComponente)+"%'"));
			if( ano != null )
				restoSQL.append(" AND t.ano = " + ano);
			if( periodo != null )
				restoSQL.append(" AND t.periodo = " + periodo);
			if( tiposTurma != null )
				restoSQL.append(" AND t.tipo in " + UFRNUtils.gerarStringIn(tiposTurma) );

			st = con.prepareStatement("SELECT count(t.id_turma) as total " + restoSQL);
			setParametrosTurma(codigo, curso, polo, st);
			rs = st.executeQuery();
			long total;
			if (rs.next()) {
				total = rs.getLong("TOTAL");
				if (total > LIMITE_RESULTADOS_BUSCA_TURMA)
					throw new LimiteResultadosException();
			}

			String countMatriculas = " , (select count(mc.id_matricula_componente) from ensino.matricula_componente as mc where mc.id_turma = t.id_turma " +
			"and mc.id_situacao_matricula in " + gerarStringIn( SituacaoMatricula.getSituacoesAtivas() ) + " ) as TOTAL_MATRICULADOS ";

			String projecao = "SELECT t.ano as ANO, t.periodo as PERIODO, t.id_turma, t.codigo as TURMA_CODIGO, t.local, t.id_situacao_turma, t.descricao_horario, cc.id_disciplina,  " +
			"cc.codigo as CC_CODIGO, cc.id_unidade as CC_ID_UNIDADE, ccd.nome as CC_NOME, dt.id_docente, p.nome as DOCENTE_NOME, t.capacidade_aluno AS CAPACIDADE, t.tipo AS TIPO, " +
			"polo.id_polo, polo.id_cidade, municipio.nome as nomeCidade, uf.id_unidade_federativa, uf.sigla, t.id_curso as id_curso, curso.nome as nome_curso";

			String orderBy = " ORDER BY ccd.nome, t.id_disciplina, t.codigo asc";

			if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
				projecao += " , unidade.nome as UNIDADE_NOME ";
				orderBy = " ORDER BY cc.id_unidade, ccd.nome, t.id_disciplina, t.codigo asc";
			}

			System.out.println( projecao +
					countMatriculas +
					restoSQL +
					orderBy );

			st = con.prepareStatement( projecao +
					countMatriculas +
					restoSQL +
					orderBy);

			setParametrosTurma(codigo, curso, polo, st);
			rs = st.executeQuery();

			ArrayList<Turma> turmas = new ArrayList<Turma>(0);
			int idAtual = 0;
			while (rs.next()) {
				Turma t = new Turma();
				t.setId(rs.getInt("ID_TURMA"));
				if (idAtual == t.getId() && idAtual > 0)
					t = turmas.get(turmas.size()-1);
				DocenteTurma dt = new DocenteTurma();
				dt.getDocente().setId(rs.getInt("ID_DOCENTE"));
				dt.getDocente().getPessoa().setNome(rs.getString("DOCENTE_NOME"));
				t.getDocentesTurmas().add(dt);
				if (idAtual == t.getId() && idAtual > 0)
					continue;
				t.setCodigo(rs.getString("TURMA_CODIGO"));
				t.setLocal(rs.getString("LOCAL"));
				t.setSituacaoTurma(new SituacaoTurma(rs.getInt("ID_SITUACAO_TURMA")));
				t.setDescricaoHorario(rs.getString("DESCRICAO_HORARIO"));
				t.setDisciplina(new ComponenteCurricular(rs.getInt("ID_DISCIPLINA")));
				t.getDisciplina().setCodigo(rs.getString("CC_CODIGO"));
				t.getDisciplina().setUnidade(new Unidade());
				t.getDisciplina().getUnidade().setId( rs.getInt("CC_ID_UNIDADE") );

				if( NivelEnsino.isValido(nivel) && NivelEnsino.isAlgumNivelStricto(nivel) ){
					t.getDisciplina().getUnidade().setNome( rs.getString("UNIDADE_NOME") );
				}

				t.getDisciplina().setNome(rs.getString("CC_NOME"));

				t.setCapacidadeAluno( rs.getInt("CAPACIDADE") );
				t.setTipo( rs.getInt("TIPO") );
				t.setQtdMatriculados( rs.getInt("TOTAL_MATRICULADOS") );
				t.setAno( Integer.parseInt( rs.getString("ANO") ) );
				t.setPeriodo( Integer.parseInt( rs.getString("PERIODO") ) );

				Integer idPolo = (Integer) rs.getObject("id_polo");

				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId( idPolo );
					t.getPolo().setCidade(new Municipio());
					t.getPolo().getCidade().setId( Integer.parseInt( rs.getString("id_cidade") ));
					t.getPolo().getCidade().setNome( rs.getString("nomeCidade") );
					t.getPolo().getCidade().setUnidadeFederativa(new UnidadeFederativa());
					t.getPolo().getCidade().getUnidadeFederativa().setId(Integer.parseInt( rs.getString("id_unidade_federativa")));
					t.getPolo().getCidade().getUnidadeFederativa().setSigla(rs.getString("sigla"));
				}
				
				Integer idCurso = rs.getInt("ID_CURSO");
				if (idCurso != null ){
					curso = new Curso(idCurso);
					curso.setNome(rs.getString("NOME_CURSO"));
					t.setCurso(curso);
				}
				
				turmas.add(t);
				idAtual = t.getId();
			}
			return turmas;
		} catch (LimiteResultadosException e) {
			throw new LimiteResultadosException (e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		} finally {
			closeResultSet(rs);
			closeStatement(st);
			Database.getInstance().close(con);
		}
	}
	
	/**
	 * Consulta geral de turmas filtrando por diversos parâmetros.
	 * 
	 * @param nivel
	 * @param unidade
	 * @param codigoComp
	 * @param codigoTurma
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @param cursos
	 * @param modalidade
	 * @param curriculo
	 * @param idComponente
	 * @param local
	 * @param tipoTurma
	 * @param horario
	 * @param ordenarPor
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public List<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos, 
			ModalidadeEducacao modalidade, Curriculo curriculo, Integer idComponente, String local, Integer tipoTurma, String horario, Integer ordenarPor) throws DAOException, LimiteResultadosException {


		ArrayList<Turma> result = new ArrayList<Turma>();
		result.addAll(findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curriculo, idComponente, false, local, tipoTurma, false, null,horario,ordenarPor));

		// exclui as repetidas pois as turmas com docente externo vem na primeira consulta também com a coleção de docentes vazia
		Collection<Turma> comDocentesExternos = findGeral(nivel, unidade, codigoComp, codigoTurma, nome, nomeDocente, situacao, ano, periodo, polo, cursos, modalidade, curriculo, idComponente, true, local, tipoTurma, false, null, horario,ordenarPor);
		for ( Turma t : comDocentesExternos ) {
			int existe = result.indexOf(t);
			if ( existe != -1 ) {
				Turma outra = result.get( result.indexOf(t) );
				outra.getDocentesTurmas().addAll(t.getDocentesTurmas());
			} else {
				result.add(t);
			}
		}
		return result;
	}
	
	/**
	 * Consulta geral de turmas filtrando por diversos parâmetros.
	 * 
	 * @param nivel
	 * @param unidade
	 * @param codigoComp
	 * @param codigoTurma
	 * @param nome
	 * @param nomeDocente
	 * @param situacao
	 * @param ano
	 * @param periodo
	 * @param polo
	 * @param cursos
	 * @param modalidade
	 * @param curriculo
	 * @param idComponente
	 * @param externos
	 * @param local
	 * @param tipoTurma
	 * @param publico
	 * @param palavraChave
	 * @param horario
	 * @param ordenarPor
	 * @return
	 * @throws DAOException
	 * @throws LimiteResultadosException
	 */
	public List<Turma> findGeral(Character nivel, Unidade unidade, String codigoComp, String codigoTurma,
			String nome, String nomeDocente, Integer[] situacao, Integer ano, Integer periodo, Polo polo, Collection<Curso> cursos,
			ModalidadeEducacao modalidade, Curriculo curriculo, Integer idComponente, boolean externos, String local, Integer tipoTurma,
			boolean publico,String palavraChave, String horario, Integer ordenarPor) throws DAOException, LimiteResultadosException {
		try {
			Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
			StringBuilder hql = new StringBuilder();
			StringBuilder projecao = new StringBuilder(" t.id, d.id, p.nome, dt.chDedicadaPeriodo, t.ano, t.periodo, t.distancia, " +
					" t.codigo, t.local, t.descricaoHorario, t.capacidadeAluno, " +
					" t.disciplina.id, t.disciplina.codigo, t.disciplina.nivel, t.disciplina.matriculavel," +
					" t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal," +
					" t.disciplina.unidade.id, t.disciplina.unidade.nome, t.disciplina.unidade.sigla, t.situacaoTurma.id, t.situacaoTurma.descricao, t.idPolo, e.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula ");

			// Contabilização de matrículas ativas
			String countMatriculas = "select count(mc.id) from MatriculaComponente as mc where mc.turma.id = t.id " +
				"and mc.situacaoMatricula.id in " + gerarStringIn(situacoes);
			projecao.append(", ("+countMatriculas+") as total_matriculados ");

			// Contabilização de solicitações pendentes
			String countEspera = "select count(sol.id) from SolicitacaoMatricula as sol where sol.turma.id = t.id " +
				" and sol.anulado = falseValue() " +
				" and sol.idMatriculaGerada is null " +
				" and sol.status in " + gerarStringIn(SolicitacaoMatricula.getStatusSolicitacoesPendentes());
			projecao.append(", ("+countEspera+") as total_espera ");

			String count = " count(distinct t.id) ";

			if ( !externos )
				hql.append(" from Turma t left join t.docentesTurmas as dt left join dt.docente as d left join d.pessoa as p left join t.especializacao as e inner join t.disciplina disciplina inner join disciplina.detalhes detalhes ");
			else
			    hql.append(" from Turma t join t.docentesTurmas as dt join dt.docenteExterno as d left join d.pessoa as p left join t.especializacao as e inner join t.disciplina disciplina inner join disciplina.detalhes detalhes ");

			if(curriculo != null)
				hql.append(", CurriculoComponente cc ");

			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if( publico )
				hql.append(", ConfiguracoesAva ca ");


			hql.append(" WHERE ");
			hql.append( " t.agrupadora = falseValue() " );
			hql.append(" AND t.situacaoTurma.id not in "+ gerarStringIn(SituacaoTurma.getSituacoesInvalidas()));
			if (cursos != null && !cursos.isEmpty()) hql.append(" and t.curso in " + gerarStringIn(cursos));
			if (situacao != null) hql.append(" AND t.situacaoTurma.id in " + UFRNUtils.gerarStringIn(situacao));
			if (!isEmpty(unidade) && unidade.getId()>0)	hql.append(" AND t.disciplina.unidade.id = '" + unidade.getId() + "' ");
			if (codigoTurma != null) hql.append(" AND t.codigo = :codigoTurma ");
			if (codigoComp != null) hql.append(" AND t.disciplina.codigo = :codigoComp ");
			if (idComponente != null) hql.append(" AND t.disciplina.id = :idComponente ");

			// Somente as turmas que tem a permissão para visualização para aluno (pública)
			if(publico)
				hql.append(" AND ca.turma.id = t.id AND ca.permiteVisualizacaoExterna = trueValue() ");

			// Somente quando o valor da palavra chave é passada
			if(palavraChave != null){
				hql.append(" AND (t.disciplina.detalhes.nome_ascii LIKE :palavraChave");
				hql.append(" OR t.id = ANY(SELECT DISTINCT(ta.turma.id) FROM TopicoAula ta WHERE ");
				hql.append(" ta.visivel = trueValue() AND ta.ativo = trueValue() AND ");
				hql.append(" (upper(ta.descricao) LIKE :palavraChave ");
				hql.append(" OR upper(ta.conteudo) LIKE :palavraChave)))");
			}else if (nome != null) hql.append(" AND t.disciplina.detalhes.nome_ascii LIKE :nomeComponente" );

			if (ano != null) hql.append(" AND t.ano = :ano ");
			if (periodo != null) hql.append(" AND t.periodo = :periodo ");
			// turmas à distância
			if (modalidade != null && modalidade.isADistancia())
				hql.append(" AND t.distancia = true");
			else if (modalidade != null && modalidade.isPresencial()) 
				hql.append(" AND t.distancia = false");
			if (nomeDocente != null){
				hql.append(" AND (" + UFRNUtils.toAsciiUpperUTF8("p.nomeAscii") + " like :nomeDocente)");
			}
			if (nivel != null && NivelEnsino.isValido(nivel)) hql.append(" AND t.disciplina.nivel = :nivel ");
			if (horario != null) hql.append(" AND t.descricaoHorario like :horario ");
			if (polo != null) hql.append(" AND t.polo.id = " + polo.getId()+" ");
			if (curriculo != null){
				hql.append(" AND cc.componente.id = t.disciplina.id ");
				hql.append(" AND cc.curriculo.id = " + curriculo.getId());
			}


			if (local != null) hql.append(" AND "+ UFRNUtils.toAsciiUpperUTF8("t.local")+ " like :local");
			if (tipoTurma != null) hql.append(" AND t.tipo = :tipoTurma ");
			String groupBy = " group by t.distancia, t.id, t.codigo, t.local, t.descricaoHorario, t.situacaoTurma.id, t.situacaoTurma.descricao, " +
			" t.disciplina.id, t.disciplina.codigo, t.disciplina.nivel, t.disciplina.matriculavel," +
			" t.disciplina.detalhes.nome,t.disciplina.detalhes.chTotal, " +
			" t.disciplina.unidade.id, t.disciplina.unidade.nome, t.disciplina.unidade.sigla, " +
			" d.id, p.nome, dt.chDedicadaPeriodo, t.ano, t.periodo, t.capacidadeAluno, t.idPolo, t.especializacao.descricao, t.tipo, t.curso.id, t.processada, t.processadaRematricula ";
			
			String orderBy = new String();			
			
			if(ordenarPor == null || ordenarPor == BuscaTurmaMBean.ORDENAR_POR_COMPONENTE_CURRICULAR) {
				orderBy = " order by t.ano desc, t.periodo desc, t.disciplina.detalhes.nome asc, t.codigo asc";
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_DIAS_SEMANA_RESPECTIVOS_HORARIOS) {
				orderBy = " order by t.descricaoHorario";				
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_DOCENTE_DISCIPLINA_HORARIOS) {
				orderBy = " order by p.nome, t.disciplina.detalhes.nome, t.descricaoHorario ";	
			}
			else if(ordenarPor == BuscaTurmaMBean.ORDENAR_POR_LOCAL) {
				orderBy = " order by t.local";	
			}
			
			
			HashMap<Integer, Integer> idTurmaPosicaoLista = new HashMap<Integer, Integer>();

			Query q = getSession().createQuery("select " + count + hql);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);

			if (codigoTurma != null) q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (periodo != null) q.setInteger("periodo",periodo);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");
			
			Long qtd = (Long) q.uniqueResult();
			if (qtd > 300)
				throw new LimiteResultadosException();

			q = getSession().createQuery("select " + projecao.toString() + hql + groupBy + orderBy);
			if( nome != null )
				q.setString("nomeComponente", "%" + StringUtils.toAscii(nome.toUpperCase()) + "%");
			if (codigoComp != null)
				q.setString("codigoComp", codigoComp);
			if (codigoTurma != null) q.setString("codigoTurma", StringUtils.toAscii(codigoTurma.toUpperCase())); 
			if (idComponente != null) q.setInteger("idComponente", idComponente);
			if (palavraChave != null) q.setString("palavraChave", "%" + StringUtils.toAscii(palavraChave.toUpperCase()) + "%");
			if (ano != null) q.setInteger("ano", ano);
			if (periodo != null) q.setInteger("periodo",periodo);
			if (nomeDocente != null) q.setString("nomeDocente", "%" + StringUtils.toAscii(nomeDocente.toUpperCase()) + "%");
			if (local != null) q.setString("local", "%" + StringUtils.toAscii(local.toUpperCase()) + "%");
			if (tipoTurma != null) q.setInteger("tipoTurma", tipoTurma);
			if (nivel != null && NivelEnsino.isValido(nivel)) q.setCharacter("nivel", nivel);
			if (horario != null) q.setString("horario", "%" + StringUtils.toAscii(horario.toUpperCase()) + "%");

			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			ArrayList<Turma> result = new ArrayList<Turma>();
			List<Integer> idTurmas = new ArrayList<Integer>();
			//  Instancia as turmas do resultado da busca.
			for (int a = 0; a < lista.size(); a++) {
				int col = 0;
				Object[] colunas = lista.get(a);

				Turma t = new Turma();
				t.setId((Integer) colunas[col++]);	
				idTurmas.add(t.getId());
				if( !idTurmaPosicaoLista.isEmpty() && idTurmaPosicaoLista.get(t.getId()) != null)				
					t = result.get(idTurmaPosicaoLista.get(t.getId()));
				DocenteTurma dt = new DocenteTurma();
				if ( !externos  ) {
					Integer idDocente = (Integer) colunas[col++];
					String nDocente = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocente != null){
						dt.getDocente().setId(idDocente);
						dt.setChDedicadaPeriodo(ch);
						if (dt.getDocente().getPessoa() != null)
							dt.getDocente().getPessoa().setNome(nDocente);
						t.getDocentesTurmas().add(dt);
					}
				} else {
					Integer idDocenteExt = (Integer) colunas[col++];
					String nDocenteExt = (String) colunas[col++];
					Integer ch = (Integer) colunas[col++];
					if(idDocenteExt != null){
						dt.setDocenteExterno(new DocenteExterno(idDocenteExt));
						dt.setChDedicadaPeriodo(ch);
						if(dt.getDocenteExterno().getPessoa() != null)
							dt.getDocenteExterno().getPessoa().setNome(nDocenteExt);
						t.getDocentesTurmas().add(dt);
					}
				}
				if (idTurmaPosicaoLista.containsKey(t.getId()))
					continue;
				t.setAno((Integer) colunas[col++]);
				t.setPeriodo((Integer) colunas[col++]);
				t.setDistancia((Boolean) colunas[col++]);
				t.setCodigo((String) colunas[col++]);
				t.setLocal((String) colunas[col++]);
				t.setDescricaoHorario((String) colunas[col++]);
				t.setCapacidadeAluno((Integer) colunas[col++]);
				t.setDisciplina(new ComponenteCurricular((Integer) colunas[col++]));
				t.getDisciplina().setCodigo((String) colunas[col++]);
				t.getDisciplina().setNivel((Character) colunas[col++]);
				t.getDisciplina().setMatriculavel((Boolean) colunas[col++]);
				t.getDisciplina().setNome((String) colunas[col++]);
				t.getDisciplina().setChTotal((Integer) colunas[col++]);
				Integer idUnidade = (Integer) colunas[col++];
				if (idUnidade != null){
					t.getDisciplina().getUnidade().setId(idUnidade);
					t.getDisciplina().getUnidade().setNome((String)colunas[col++]);
					t.getDisciplina().getUnidade().setSigla((String)colunas[col++]);
				}
				t.setSituacaoTurma(new SituacaoTurma());
				t.getSituacaoTurma().setId((Integer) colunas[col++]);
				t.getSituacaoTurma().setDescricao((String) colunas[col++]);


				Integer idPolo = (Integer) colunas[col++];
				if (idPolo != null) {
					t.setPolo(new Polo());
					t.getPolo().setId(idPolo);
				}
				String descricaoEspecializacao = (String) colunas[col++];
				if(descricaoEspecializacao != null){
					t.setEspecializacao(new EspecializacaoTurmaEntrada());
					t.getEspecializacao().setDescricao(descricaoEspecializacao);
				}

				t.setTipo( (Integer) colunas[ col++ ] );

				Integer idCurso = (Integer) colunas[ col++ ];
				if( idCurso != null ){
					t.setCurso( new Curso() );
					t.getCurso().setId(idCurso);
				}

				t.setProcessada((Boolean) colunas[col++]);
				t.setProcessadaRematricula((Boolean) colunas[col++]);
				
				Long totalMatriculados = (Long) colunas[col++];
				t.setQtdMatriculados(totalMatriculados);

				t.setQtdEspera( (Long) colunas[col++] );
				result.add(t);
				idTurmaPosicaoLista.put(t.getId(), result.size()-1);				
			}
			return result;
		} catch (LimiteResultadosException lre) {
			throw new LimiteResultadosException(lre);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca as matrículas dos discentes por turmas e situação de matrícula.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List <MatriculaComponente> findMatriculasByTurmas (List <Integer> idTurmas, SituacaoMatricula... situacoes) throws DAOException{
		
		Criteria c = getSession().createCriteria(MatriculaComponente.class);
		c.add( Restrictions.in("turma.id", idTurmas) );
        if (situacoes != null && situacoes.length > 0)
        	c.add(Restrictions.in("situacaoMatricula", situacoes));
        c.addOrder(Order.asc("turma.id"));
        
        @SuppressWarnings("unchecked")
        List <MatriculaComponente> rs = c.list();
    	return rs;
	}
	
}
