/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 17/12/2010
 *
 */
package br.ufrn.sigaa.arq.dao.graduacao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.FormaIngresso;
import br.ufrn.sigaa.ensino.dominio.GrauAcademico;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.Habilitacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.pessoa.dominio.Municipio;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.PessoaVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoClassificacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.SemestreConvocacao;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.TipoConvocacao;
/**
 * Dao com consultas utilizadas na convocação dos candidatos do vestibular para
 * as vagas remanescentes.
 * 
 * @author Leonardo Campos
 * 
 */
public class ConvocacaoVestibularDao extends GenericSigaaDAO {

	/**
	 * Retorna todas as matrizes curriculares que possuem alunos convocados
	 * ainda com status {@link StatusDiscente.PENDENTE_CADASTRO} para o processo
	 * seletivo vestibular informado como parâmetro.
	 * 
	 * @param psVest
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findMatrizesComAlunosPendentesCadastro(ProcessoSeletivoVestibular psVest) throws DAOException {
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct m.id, m.curso.nome, enfase.nome , habilitacao.nome, m.turno.sigla, m.grauAcademico.descricao"); 
		hql.append(" FROM DiscenteGraduacao dg ");
		hql.append(" JOIN dg.matrizCurricular m");
		hql.append(" LEFT JOIN m.enfase enfase");
		hql.append(" LEFT JOIN m.habilitacao habilitacao");
		hql.append(" WHERE dg.id in (" +
				"  select c.discente.id" +
				"  from ConvocacaoProcessoSeletivoDiscente c" +
				"  where c.convocacaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo)");
		hql.append(" and dg.discente.status = :pendenteCadastro ");
		hql.append(" ORDER BY m.curso.nome ");
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery(hql.toString())
				.setInteger("idProcessoSeletivo", psVest.getId())
				.setInteger("pendenteCadastro", StatusDiscente.PENDENTE_CADASTRO)
				.list();
		
		List<MatrizCurricular> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<MatrizCurricular>();
			}
			
			int col = 0;
			
			Object[] colunas = lista.get(a);
			MatrizCurricular matrizCurricular = new MatrizCurricular();
			matrizCurricular = new MatrizCurricular();
		
			matrizCurricular.setId((Integer) colunas[col++]);
			
			matrizCurricular.setCurso(new Curso());
			matrizCurricular.getCurso().setNome((String) colunas[col++]);
			
			matrizCurricular.setEnfase(new Enfase());
			matrizCurricular.getEnfase().setNome((String) colunas[col++]);
			
			matrizCurricular.setHabilitacao(new Habilitacao());
			matrizCurricular.getHabilitacao().setNome((String) colunas[col++]);
			
			matrizCurricular.setTurno(new Turno());
			matrizCurricular.getTurno().setSigla((String) colunas[col++]);
			
			matrizCurricular.setGrauAcademico(new GrauAcademico());
			matrizCurricular.getGrauAcademico().setDescricao((String) colunas[col++]);
			
			result.add(matrizCurricular);
		}
		
		return result;
	}
	
	/** Retorna uma coleção de matrizes curriculares de cursos que funcionam em turno distinto.
	 * @param psVest
	 * @return
	 * @throws DAOException
	 */
	public Collection<MatrizCurricular> findMatrizesTurnoDistinto(ProcessoSeletivoVestibular psVest) throws DAOException {
		StringBuilder hql = new StringBuilder();
		
		hql.append(" SELECT distinct m.id, m.curso.nome, enfase.nome , habilitacao.nome, m.turno.sigla, m.grauAcademico.descricao"); 
		hql.append(" FROM OfertaVagasCurso o ");
		hql.append(" JOIN o.matrizCurricular m");
		hql.append(" LEFT JOIN m.enfase enfase");
		hql.append(" LEFT JOIN m.habilitacao habilitacao");
		hql.append(" WHERE o.formaIngresso.id = :idFormaIngresso");
		hql.append(" ORDER BY m.curso.nome ");
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = getSession().createQuery(hql.toString())
				.setInteger("idFormaIngresso", psVest.getFormaIngresso().getId())
				.list();
		
		List<MatrizCurricular> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<MatrizCurricular>();
			}
			
			int col = 0;
			
			Object[] colunas = lista.get(a);
			MatrizCurricular matrizCurricular = new MatrizCurricular();
			matrizCurricular = new MatrizCurricular();
		
			matrizCurricular.setId((Integer) colunas[col++]);
			
			matrizCurricular.setCurso(new Curso());
			matrizCurricular.getCurso().setNome((String) colunas[col++]);
			
			matrizCurricular.setEnfase(new Enfase());
			matrizCurricular.getEnfase().setNome((String) colunas[col++]);
			
			matrizCurricular.setHabilitacao(new Habilitacao());
			matrizCurricular.getHabilitacao().setNome((String) colunas[col++]);
			
			matrizCurricular.setTurno(new Turno());
			matrizCurricular.getTurno().setSigla((String) colunas[col++]);
			
			matrizCurricular.setGrauAcademico(new GrauAcademico());
			matrizCurricular.getGrauAcademico().setDescricao((String) colunas[col++]);
			
			result.add(matrizCurricular);
		}
		
		return result;
	}
	
	/**
	 * Retorna os processos seletivos cuja forma de ingresso é {@link FormaIngresso.VESTIBULAR}.
	 * @return
	 * @throws DAOException
	 */
	public List<ProcessoSeletivoVestibular> findProcessosSeletivos() throws DAOException {
		String hql = "SELECT id, nome " +
				" FROM ProcessoSeletivoVestibular" +
				" WHERE anoEntrada > 0" +
//				" AND formaIngresso.id = :formaIngressoVestibular" +
				" ORDER BY anoEntrada DESC";
		
		@SuppressWarnings("unchecked")
//		List<Object[]> lista = getSession().createQuery(hql).setInteger("formaIngressoVestibular", FormaIngresso.VESTIBULAR.getId()).list();
		List<Object[]> lista = getSession().createQuery(hql).list();
		
		List<ProcessoSeletivoVestibular> result = null;
		for (int a = 0; a < lista.size(); a++) {
			
			if (result == null){
				result = new ArrayList<ProcessoSeletivoVestibular>();
			}
			
			int col = 0;
			
			Object[] colunas = lista.get(a);
			ProcessoSeletivoVestibular psv = new ProcessoSeletivoVestibular((Integer) colunas[col++]);
			psv.setNome((String) colunas[col++]);
			
			result.add(psv);
		}
		
		return result;
	}
	
	/**
	 * Retorna um mapa com o id da matriz curricular e seu respectivo número de vagas ociosas.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Integer> findVagasOciosas(ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestre) throws DAOException {
		String colunaVagas = "";
		String restricaoSemestreEntrada = "";
		String restricaoVagas = "";
		
		switch (semestre) {
		case CONVOCA_TODOS_SEMESTRES:
			colunaVagas = "total_vagas";
			restricaoSemestreEntrada = "";
			restricaoVagas = " and total_vagas > 0"+
			" and coalesce(o.total_vagas - preenchidas.total, 0) > 0";
			break;
		case CONVOCA_APENAS_PRIMEIRO_SEMESTRE:
			colunaVagas = "vagas_periodo_1";
			restricaoSemestreEntrada = " and d.periodo_ingresso = 1";
			restricaoVagas = " and vagas_periodo_1 > 0"+
				" and coalesce(o.vagas_periodo_1 - preenchidas.total, 0) > 0";
			break;
		case CONVOCA_APENAS_SEGUNDO_SEMESTRE:
			colunaVagas = "vagas_periodo_2";
			restricaoSemestreEntrada = " and d.periodo_ingresso = 2";
			restricaoVagas = " and vagas_periodo_2 > 0" +
				" and coalesce(o.vagas_periodo_2 - preenchidas.total, 0) > 0";
			break;
		}
		
		Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
		statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
		
		String sql = "select o.id_matriz_curricular, "  +
			" cast(("+ colunaVagas + " - coalesce(preenchidas.total, 0)) as integer) as ociosas" +
			" from ensino.oferta_vagas_curso o " +
			" join vestibular.processo_seletivo ps on (o.id_forma_ingresso = ps.id_forma_ingresso and o.ano = ps.ano_entrada) " +
			" left join (  select dg.id_matriz_curricular, d.id_forma_ingresso, count(*) as total" +
			" 	from graduacao.discente_graduacao dg " +
			" 	join discente d on dg.id_discente_graduacao = d.id_discente " +
			" 	where d.ano_ingresso = " + processoSeletivo.getAnoEntrada() +"" +
			" 	and d.status in " + gerarStringIn(statusQueOcupamVaga) +""+
				restricaoSemestreEntrada  +""+
			" 	group by id_matriz_curricular, d.id_forma_ingresso" +
			" 	) as preenchidas on (preenchidas.id_matriz_curricular = o.id_matriz_curricular and preenchidas.id_forma_ingresso = ps.id_forma_ingresso)" +
			" where ps.id_processo_seletivo = :idProcessoSeletivo" + 
			restricaoVagas;

		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		
		for (Object[] objects : lista) {
			int col = 0;
			Integer idMatriz = (Integer) objects[col++];
			Integer vagas = (Integer) objects[col++];
			result.put(idMatriz, vagas);
		}
		
		return result;
	}
	
	/**
	 * Retorna um mapa com o id da matriz curricular e seu respectivo número de vagas ociosas.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Map<OfertaVagasCurso, Integer> findOfertaComVagasRemanescentes(ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestre, boolean incluirPreCadastrado, boolean incluirPendenteCadastro) throws DAOException {
		String colunaVagas = "";
		String restricaoSemestreEntrada = "";
		String restricaoVagas = "";
		switch (semestre) {
		case CONVOCA_TODOS_SEMESTRES:
			colunaVagas = "total_vagas";
			restricaoSemestreEntrada = "";
			restricaoVagas = " and total_vagas > 0";
			break;
		case CONVOCA_APENAS_PRIMEIRO_SEMESTRE:
			colunaVagas = "vagas_periodo_1";
			restricaoSemestreEntrada = " and d.periodo_ingresso = 1";
			restricaoVagas = " and vagas_periodo_1 > 0";
			break;
		case CONVOCA_APENAS_SEGUNDO_SEMESTRE:
			colunaVagas = "vagas_periodo_2";
			restricaoSemestreEntrada = " and d.periodo_ingresso = 2";
			restricaoVagas = " and vagas_periodo_2 > 0";
			break;
		}
		Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
		if (incluirPreCadastrado) {
			statusQueOcupamVaga.add(StatusDiscente.PRE_CADASTRADO);
		}
		if (incluirPendenteCadastro) {
			statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
		}
		String sql = "select o.id_oferta_vagas_curso, "  +
			" cast(("+ colunaVagas + " - coalesce(preenchidas.total, 0)) as integer) as ociosas" +
			" from ensino.oferta_vagas_curso o " +
			" join vestibular.processo_seletivo ps on (o.id_forma_ingresso = ps.id_forma_ingresso and o.ano = ps.ano_entrada) " +
			" left join (  select dg.id_matriz_curricular, d.id_forma_ingresso, count(*) as total" +
			" 	from graduacao.discente_graduacao dg " +
			" 	join discente d on dg.id_discente_graduacao = d.id_discente " +
			" 	where d.ano_ingresso = " + processoSeletivo.getAnoEntrada() +"" +
			" 	and d.status in " + gerarStringIn(statusQueOcupamVaga) +""+
				restricaoSemestreEntrada  +""+
			" 	group by id_matriz_curricular, d.id_forma_ingresso" +
			" 	) as preenchidas on (preenchidas.id_matriz_curricular = o.id_matriz_curricular and preenchidas.id_forma_ingresso = ps.id_forma_ingresso)" +
			" where ps.id_processo_seletivo = :idProcessoSeletivo" +
			restricaoVagas;
//			+ " and o.id_matriz_curricular = 99411";
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> ofertaVagas = q.list();
		Map<OfertaVagasCurso, Integer> mapa = new LinkedHashMap<OfertaVagasCurso, Integer>();
		if (!isEmpty(ofertaVagas)) {
			List<Integer> ids = new ArrayList<Integer>();
			for (Object[] obj : ofertaVagas)
				ids.add((Integer) obj[0]);
			Criteria c = getSession().createCriteria(OfertaVagasCurso.class).setFetchMode("cotas", FetchMode.SELECT).add(Restrictions.in("id", ids));
			@SuppressWarnings("unchecked")
			Collection<OfertaVagasCurso> ofertas  = new LinkedHashSet<OfertaVagasCurso>(c.list());
			// merge das ofertas com as vagas remanescentes
			for (OfertaVagasCurso oferta : ofertas ) {
				for (Object[] obj : ofertaVagas) {
					int idOferta = (Integer) obj[0];
					int vagas = (Integer) obj[1];
					if (idOferta == oferta.getId()) {
						mapa.put(oferta, vagas);
						break;
					}
				}
			}
		}
		return mapa;
	}
	
	/**
	 * Retorna os candidatos convocados para o segundo semestre para as matrizes informadas ordenados por classificação. 
	 * @param idsMatrizes
	 * @param opcao
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, List<ResultadoOpcaoCurso>> findConvocadosSegundoSemestre(ProcessoSeletivoVestibular psVest, Collection<Integer> idsMatrizes) throws DAOException {
		Map<Integer, List<ResultadoOpcaoCurso>> result = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		if(!isEmpty(idsMatrizes)){
			String sql =  "select roc.id_resultado_opcao_curso, roc.classificacao, roc.id_matriz_curricular, rcc.id_resultado_classificacao_candidato, " +
					" iv.id_inscricao_vestibular, iv.numero_inscricao, pv.cpf_cnpj, pv.nome, c.id_curso, c.nome as nomeCurso, mun.nome as nomeMunicipio, t.id_turno, t.sigla, g.id_grau_academico, g.descricao, h.id_habilitacao, h.nome as nomeHabilitacao, " +
					" aux.id_convocacao_processo_seletivo_discente, aux.id_discente_graduacao, aux.matricula, aux.id_matriz_curricular as id_matriz_ant," +
					" aux.id_curso as id_curso_ant, aux.nomeCurso as nome_curso_ant, aux.id_turno as id_turno_ant, aux.sigla as sigla_ant, aux.id_habilitacao as id_habilitacao_ant, aux.nomeHabilitacao as nome_hab_ant, aux.id_grau_academico as id_grau_academico_ant, aux.descricao as desc_ant, aux.status, aux.periodo_ingresso," +
					" iv.id_processo_seletivo" +
					" from vestibular.resultado_opcao_curso roc" +
					"	join vestibular.resultado_classificacao_candidato rcc using (id_resultado_classificacao_candidato)" +
					"	join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
					"	join vestibular.pessoa_vestibular pv using (id_pessoa)" +
					"	join graduacao.matriz_curricular mc using (id_matriz_curricular)" +
					"	join curso c on (c.id_curso=mc.id_curso)" +
					"	join comum.municipio mun on (mun.id_municipio=c.id_municipio)"+
					"	join ensino.turno t on (t.id_turno=mc.id_turno)" +
					"	join ensino.grau_academico g on (g.id_grau_academico=mc.id_grau_academico)" +
					"	left join graduacao.habilitacao h on (h.id_habilitacao=mc.id_habilitacao)" +
					"	left join (" +
					"		select cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
					"			m.id_matriz_curricular, m.id_curso, c2.nome as nomeCurso, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome as nomeHabilitacao, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso, max(cp.data_convocacao) " +
					"		from vestibular.convocacao_processo_seletivo_discente cd" +
					"			join vestibular.convocacao_processo_seletivo cp using (id_convocacao_processo_seletivo)" +
					"			join graduacao.discente_graduacao dg on (dg.id_discente_graduacao=cd.id_discente)" +
					"			join discente d on (dg.id_discente_graduacao=d.id_discente)" +
					"			join graduacao.matriz_curricular m on (dg.id_matriz_curricular=m.id_matriz_curricular)" +
					"			join curso c2 on (c2.id_curso=m.id_curso)" +
					"			join ensino.turno t2 on (t2.id_turno=m.id_turno)" +
					"			join ensino.grau_academico g2 on (g2.id_grau_academico=m.id_grau_academico)" +
					"			left join graduacao.habilitacao h2 on (h2.id_habilitacao=m.id_habilitacao)" +
					"		where cp.id_processo_seletivo = :idProcessoSeletivo" +
					"		group by cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
					"			m.id_matriz_curricular, m.id_curso, c2.nome, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso" +
					"	) aux on (aux.id_inscricao_vestibular=rcc.id_inscricao_vestibular)"+
					" where iv.id_processo_seletivo = :idProcessoSeletivo" +
					" and roc.id_matriz_curricular in " + UFRNUtils.gerarStringIn(idsMatrizes) +
					" and roc.id_matriz_curricular = rcc.opcao_aprovacao"+
					" and roc.classificacao > 0"+
					" and (rcc.id_inscricao_vestibular, roc.id_matriz_curricular) in" +
					" (" +
					"	select cd.id_inscricao_vestibular, dg.id_matriz_curricular" +
					"	from vestibular.convocacao_processo_seletivo_discente cd" +
					"		join vestibular.convocacao_processo_seletivo c using (id_convocacao_processo_seletivo)" +
					"		join graduacao.discente_graduacao dg on (dg.id_discente_graduacao=cd.id_discente)" +
					"		join discente d on (dg.id_discente_graduacao=d.id_discente)" +
					"	where c.id_processo_seletivo = :idProcessoSeletivo" +
					//" and dg.id_matriz_curricular in " + UFRNUtils.gerarStringIn(idsMatrizes) +
					"	and d.ano_ingresso = :anoEntradaProcessoSeletivo" +
					"	and d.periodo_ingresso = :segundoSemestre" +
					"	and d.status = :cadastrado" +
					" )" +
					" order by roc.id_matriz_curricular, roc.ordem_opcao, roc.classificacao, roc.argumento_final desc";
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = getSession().createSQLQuery(sql)
					.setInteger("idProcessoSeletivo", psVest.getId())
					.setInteger("anoEntradaProcessoSeletivo", psVest.getAnoEntrada())
					.setInteger("segundoSemestre", 2)
					.setInteger("cadastrado", StatusDiscente.CADASTRADO)
					.list();
			
			
			for (Object[] objects : list) {
				int col = 0;
				Integer idResultadoOpcao = (Integer) objects[col++];
				Integer classificacao = (Integer) objects[col++];
				Integer idMatriz = (Integer) objects[col++];
				Integer idResultadoClassificacao = (Integer) objects[col++];
				Integer idInscricao = (Integer) objects[col++];
				Integer numeroInscricao = (Integer) objects[col++];
				BigInteger cpf = (BigInteger) objects[col++];
				String nome = (String) objects[col++];
				Integer idCurso = (Integer) objects[col++];
				String nomeCurso = (String) objects[col++];
				String nomeMunicipio = (String) objects[col++];
				Integer idTurno = (Integer) objects[col++];
				String turnoSigla = (String) objects[col++];
				Integer idGrauAcademico = (Integer) objects[col++];
				String descricaoGrau = (String) objects[col++];
				Integer idHabilitacao = (Integer) objects[col++];
				String nomeHabilitacao = (String) objects[col++];
				Integer idConvocacaoDiscente = (Integer) objects[col++];
				Integer idDiscenteGraduacao = (Integer) objects[col++];
				BigInteger matricula = (BigInteger) objects[col++];
				Integer idProcessoSeletivo = (Integer) objects[31];
				
				PessoaVestibular pv = new PessoaVestibular();
				pv.setCpf_cnpj(cpf.longValue());
				pv.setNome(nome);
				
				ProcessoSeletivoVestibular ps = new ProcessoSeletivoVestibular(idProcessoSeletivo);
				
				InscricaoVestibular iv = new InscricaoVestibular(idInscricao);
				iv.setNumeroInscricao(numeroInscricao);
				iv.setPessoa(pv);
				iv.setProcessoSeletivo(ps);
				
				Curso curso = new Curso(idCurso);
				curso.setNome(nomeCurso);
				Municipio municipio = new Municipio();
				municipio.setNome(nomeMunicipio);
				curso.setMunicipio(municipio);
				
				MatrizCurricular matrizCurricular = new MatrizCurricular(idMatriz);
				matrizCurricular.setCurso(curso);
				Turno turno = new Turno(idTurno);
				turno.setSigla(turnoSigla);
				matrizCurricular.setTurno(turno);
				matrizCurricular.setGrauAcademico(new GrauAcademico(idGrauAcademico, descricaoGrau));
				matrizCurricular.setHabilitacao(idHabilitacao != null ? new Habilitacao(idHabilitacao, nomeHabilitacao) : null);
				
				ResultadoClassificacaoCandidato rcc = new ResultadoClassificacaoCandidato();
				rcc.setId(idResultadoClassificacao);
				rcc.setInscricaoVestibular(iv);
				
				ResultadoOpcaoCurso roc = new ResultadoOpcaoCurso();
				roc.setId(idResultadoOpcao);
				roc.setClassificacao(classificacao);
				roc.setMatrizCurricular(matrizCurricular);
				roc.setResultadoClassificacaoCandidato(rcc);
				
				ConvocacaoProcessoSeletivoDiscente convocacao = new ConvocacaoProcessoSeletivoDiscente(idConvocacaoDiscente);
				convocacao.setDiscente(new DiscenteGraduacao(idDiscenteGraduacao, matricula.longValue(), nome));
				roc.setConvocacaoAnterior(convocacao);
				
				if (result.get(idMatriz) == null) result.put(idMatriz, new ArrayList<ResultadoOpcaoCurso>());
				result.get(idMatriz).add(roc);
			}
		}
		
		return result;
	}
	
	/**
	 * Retorna os próximos candidatos suplentes da 1ª  ou 2ª opção, de acordo com o argumento passado, para as matrizes informadas. 
	 * @param idsMatrizes
	 * @param opcao
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, List<ResultadoOpcaoCurso>> findSuplentesByOpcao(ProcessoSeletivoVestibular psVest, int opcao) throws DAOException {
		Map<Integer, List<ResultadoOpcaoCurso>> result = new LinkedHashMap<Integer, List<ResultadoOpcaoCurso>>();
		
		int[] tipos = new int[]{TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal()};
		if(opcao == ResultadoOpcaoCurso.SEGUNDA_OPCAO)
			tipos = new int[]{TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal(), TipoConvocacao.CONVOCACAO_TURNO_DISTINTO.ordinal(), TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO.ordinal()};
		int[] situacoes = new int[]{SituacaoCandidato.SUPLENTE.getId(), SituacaoCandidato.APROVADO.getId()};
		String sql =  "select roc.id_resultado_opcao_curso, roc.ordem_opcao, roc.classificacao, roc.id_matriz_curricular, rcc.id_resultado_classificacao_candidato, rcc.id_situacao_candidato," +
		" iv.id_inscricao_vestibular, iv.numero_inscricao, pv.id_pessoa, pv.cpf_cnpj, pv.nome, c.id_curso, c.nome as nomeCurso, mun.nome as nomeMunicipio, t.id_turno, t.sigla, g.id_grau_academico, g.descricao, h.id_habilitacao, h.nome as nomeHabilitacao, " +
		" aux.id_convocacao_processo_seletivo_discente, aux.id_discente_graduacao, aux.matricula, aux.id_matriz_curricular as id_matriz_ant," +
		" aux.id_curso as id_curso_ant, aux.nomeCurso as nome_curso_ant, aux.id_turno as id_turno_ant, aux.sigla as sigla_ant, aux.id_habilitacao as id_habilitacao_ant, aux.nomeHabilitacao as nome_hab_ant, aux.id_grau_academico as id_grau_academico_ant, aux.descricao as desc_ant, aux.status, aux.periodo_ingresso," +
		" iv.id_processo_seletivo" +
		" from vestibular.resultado_opcao_curso roc" +
		"	join vestibular.resultado_classificacao_candidato rcc using (id_resultado_classificacao_candidato)" +
		"	join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
		"	join vestibular.pessoa_vestibular pv using (id_pessoa)" +
		"	join graduacao.matriz_curricular mc using (id_matriz_curricular)" +
		"	join curso c on (c.id_curso=mc.id_curso)" +
		"	join comum.municipio mun on (mun.id_municipio=c.id_municipio)"+	
		"	join ensino.turno t on (t.id_turno=mc.id_turno)" +
		"	join ensino.grau_academico g on (g.id_grau_academico=mc.id_grau_academico)" +
		"	left join graduacao.habilitacao h on (h.id_habilitacao=mc.id_habilitacao)" +
		"	left join (" +
		"		select cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
		"			m.id_matriz_curricular, m.id_curso, c2.nome as nomeCurso, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome as nomeHabilitacao, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso, max(cp.data_convocacao) " +
		"		from vestibular.convocacao_processo_seletivo_discente cd" +
		"			join vestibular.convocacao_processo_seletivo cp using (id_convocacao_processo_seletivo)" +
		"			join graduacao.discente_graduacao dg on (dg.id_discente_graduacao=cd.id_discente)" +
		"			join discente d on (dg.id_discente_graduacao=d.id_discente)" +
		"			join graduacao.matriz_curricular m on (dg.id_matriz_curricular=m.id_matriz_curricular)" +
		"			join curso c2 on (c2.id_curso=m.id_curso)" +
		"			join ensino.turno t2 on (t2.id_turno=m.id_turno)" +
		"			join ensino.grau_academico g2 on (g2.id_grau_academico=m.id_grau_academico)" +
		"			left join graduacao.habilitacao h2 on (h2.id_habilitacao=m.id_habilitacao)" +
		"		where cp.id_processo_seletivo = :idProcessoSeletivo" +
		"		group by cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
		"			m.id_matriz_curricular, m.id_curso, c2.nome, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso" +
		"	) aux on (aux.id_inscricao_vestibular=rcc.id_inscricao_vestibular)"+	
		" where roc.ordem_opcao = :opcao " +
		" and rcc.aprovado_ama is false "+
		" and rcc.id_situacao_candidato in " + UFRNUtils.gerarStringIn(situacoes) +
		" and iv.id_processo_seletivo = :idProcessoSeletivo" +
		" and rcc.id_inscricao_vestibular not in" +
		" (	" +
		"	select c.id_inscricao_vestibular" +
		"	from vestibular.convocacao_processo_seletivo_discente c" +
		"	join vestibular.convocacao_processo_seletivo cd using (id_convocacao_processo_seletivo)" +
		"	where c.tipo in " + UFRNUtils.gerarStringIn(tipos) +
		"	and cd.id_processo_seletivo = :idProcessoSeletivo" +
		" ) " +
		" order by roc.id_matriz_curricular, roc.ordem_opcao, roc.classificacao, roc.argumento_final desc";
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql)
				.setInteger("opcao", opcao)
				.setInteger("idProcessoSeletivo", psVest.getId())
				.list();
		
		return popularResultado(list, result);
	}

	/**
	 * Constrói o mapa de resultado a partir da lista de objetos retornada pela consulta.
	 * 
	 * @param list
	 * @return
	 */
	private Map<Integer, List<ResultadoOpcaoCurso>> popularResultado(
			List<Object[]> list, Map<Integer, List<ResultadoOpcaoCurso>> result) {
		
		for (Object[] objects : list) {
			int col = 0;
			Integer idResultadoOpcao = (Integer) objects[col++];
			Integer ordemOpcao = (Integer) objects[col++];
			Integer classificacao = (Integer) objects[col++];
			Integer idMatriz = (Integer) objects[col++];
			Integer idResultadoClassificacao = (Integer) objects[col++];
			Integer idSituacaoCandidato = (Integer) objects[col++];
			Integer idInscricao = (Integer) objects[col++];
			Integer numeroInscricao = (Integer) objects[col++];
			Integer idPessoa = (Integer) objects[col++];
			BigInteger cpf = (BigInteger) objects[col++];
			String nome = (String) objects[col++];
			Integer idCurso = (Integer) objects[col++];
			String nomeCurso = (String) objects[col++];
			String nomeMunicipio = (String) objects[col++];
			Integer idTurno = (Integer) objects[col++];
			String turnoSigla = (String) objects[col++];
			Integer idGrauAcademico = (Integer) objects[col++];
			String descricaoGrau = (String) objects[col++];
			Integer idHabilitacao = (Integer) objects[col++];
			String nomeHabilitacao = (String) objects[col++];
			Integer idConvocacaoDiscente = (Integer) objects[col++];
			Integer idDiscenteGraduacao = (Integer) objects[col++];
			BigInteger matricula = (BigInteger) objects[col++];
			Integer idMatrizAntiga = (Integer) objects[col++];
			Integer idCursoAntigo = (Integer) objects[col++];
			String nomeCursoAntigo = (String) objects[col++];
			Short idTurnoAntigo = (Short) objects[col++];
			String siglaTurnoAntigo = (String) objects[col++];
			Integer idHabilitacaoAntiga = (Integer) objects[col++];
			String nomeHabilitacaoAntiga = (String) objects[col++];
			Integer idGrauAcademicoAntigo = (Integer) objects[col++];
			String descricaoGrauAcademicoAntigo = (String) objects[col++];
			Short statusAntigo = (Short) objects[col++];
			Integer periodoIngresso = (Integer) objects[col++];
			Integer idProcessoSeletivo  = (Integer) objects[col++];
			
			PessoaVestibular pv = new PessoaVestibular(idPessoa);
			pv.setCpf_cnpj(cpf.longValue());
			pv.setNome(nome);
			
			ProcessoSeletivoVestibular ps = new ProcessoSeletivoVestibular(idProcessoSeletivo);
			
			InscricaoVestibular iv = new InscricaoVestibular(idInscricao);
			iv.setNumeroInscricao(numeroInscricao);
			iv.setPessoa(pv);
			iv.setProcessoSeletivo(ps);
			
			ResultadoClassificacaoCandidato rcc = new ResultadoClassificacaoCandidato();
			rcc.setId(idResultadoClassificacao);
			rcc.setSituacaoCandidato(new SituacaoCandidato(idSituacaoCandidato));
			rcc.setInscricaoVestibular(iv);
			
			Curso curso = new Curso(idCurso);
			curso.setNome(nomeCurso);
			Municipio municipio = new Municipio();
			municipio.setNome(nomeMunicipio);
			curso.setMunicipio(municipio);
			
			MatrizCurricular matrizCurricular = new MatrizCurricular(idMatriz);
			matrizCurricular.setCurso(curso);
			Turno turno = new Turno(idTurno);
			turno.setSigla(turnoSigla);
			matrizCurricular.setTurno(turno);
			matrizCurricular.setGrauAcademico(new GrauAcademico(idGrauAcademico, descricaoGrau));
			matrizCurricular.setHabilitacao(idHabilitacao != null ? new Habilitacao(idHabilitacao, nomeHabilitacao) : null);
			
			ResultadoOpcaoCurso roc = new ResultadoOpcaoCurso();
			roc.setId(idResultadoOpcao);
			roc.setOrdemOpcao(ordemOpcao);
			roc.setClassificacao(classificacao);
			roc.setMatrizCurricular(matrizCurricular);
			roc.setResultadoClassificacaoCandidato(rcc);
			
			if(idConvocacaoDiscente != null){
				ConvocacaoProcessoSeletivoDiscente convocacao = new ConvocacaoProcessoSeletivoDiscente(idConvocacaoDiscente);
				
				DiscenteGraduacao discente = new DiscenteGraduacao();
				discente.getDiscente().setId(idDiscenteGraduacao);
				discente.setId(idDiscenteGraduacao);
				discente.setMatricula(matricula != null ? matricula.longValue() : 0l);
				discente.getDiscente().getPessoa().setNome(nome);
				discente.setPeriodoIngresso(periodoIngresso);
				
				convocacao.setDiscente(discente);
				
				MatrizCurricular matrizAntiga = new MatrizCurricular(idMatrizAntiga);
				Curso cursoAntigo = new Curso(idCursoAntigo);
				cursoAntigo.setNome(nomeCursoAntigo);
				matrizAntiga.setCurso(cursoAntigo);
				Turno turnoAntigo = new Turno(idTurnoAntigo);
				turnoAntigo.setSigla(siglaTurnoAntigo);
				matrizAntiga.setTurno(turnoAntigo);
				matrizAntiga.setGrauAcademico(new GrauAcademico(idGrauAcademicoAntigo, descricaoGrauAcademicoAntigo));
				matrizAntiga.setHabilitacao(idHabilitacaoAntiga != null ? new Habilitacao(idHabilitacaoAntiga, nomeHabilitacaoAntiga) : null);
				
				convocacao.getDiscente().setMatrizCurricular(matrizAntiga);
				convocacao.getDiscente().setStatus(statusAntigo);
				roc.setConvocacaoAnterior(convocacao);
			}
			
			
			if (result.get(idMatriz) == null) result.put(idMatriz, new ArrayList<ResultadoOpcaoCurso>());
			
			result.get(idMatriz).add(roc);
		}
		
		
		return result;
	}
	
	/**
	 * Retorna os próximos suplentes das matrizes de turno distinto.
	 * @param psVest
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, List<ResultadoOpcaoCurso>> findSuplentesTurnoDistinto(ProcessoSeletivoVestibular psVest) throws DAOException {
		Map<Integer, List<ResultadoOpcaoCurso>> result = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		String sql =  "select roc.id_resultado_opcao_curso, roc.ordem_opcao, roc.classificacao, td.id_matriz_curricular, rcc.id_resultado_classificacao_candidato, rcc.id_situacao_candidato," +
				" iv.id_inscricao_vestibular, iv.numero_inscricao, pv.id_pessoa, pv.cpf_cnpj, pv.nome, c.id_curso, c.nome as nomeCurso, mun.nome as nomeMunicipio, t.id_turno, t.sigla, g.id_grau_academico, g.descricao, h.id_habilitacao, h.nome as nomeHabilitacao, " +
				" aux.id_convocacao_processo_seletivo_discente, aux.id_discente_graduacao, aux.matricula, aux.id_matriz_curricular as id_matriz_ant," +
				" aux.id_curso as id_curso_ant, aux.nomeCurso as nome_curso_ant, aux.id_turno as id_turno_ant, aux.sigla as sigla_ant, aux.id_habilitacao as id_habilitacao_ant, aux.nomeHabilitacao as nome_hab_ant, aux.id_grau_academico as id_grau_academico_ant, aux.descricao as desc_ant, aux.status, aux.periodo_ingresso," +
				" iv.id_processo_seletivo" +
				" from vestibular.resultado_opcao_curso roc" +
				"	join vestibular.resultado_classificacao_candidato rcc using (id_resultado_classificacao_candidato)" +
				"	join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
				"	join vestibular.pessoa_vestibular pv using (id_pessoa)" +
				"	join vestibular.matriz_turno_distinto td on (roc.id_matriz_curricular=td.id_matriz_turno_distinto)" +
				"	join graduacao.matriz_curricular mc on (mc.id_matriz_curricular=td.id_matriz_curricular)" +
				"	join curso c on (c.id_curso=mc.id_curso)" +
				"	join comum.municipio mun on (mun.id_municipio=c.id_municipio)"+
				"	join ensino.turno t on (t.id_turno=mc.id_turno)" +
				"	join ensino.grau_academico g on (g.id_grau_academico=mc.id_grau_academico)" +
				"	left join graduacao.habilitacao h on (h.id_habilitacao=mc.id_habilitacao)" +
				"	left join (" +
				"		select cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
				"			m.id_matriz_curricular, m.id_curso, c2.nome as nomeCurso, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome as nomeHabilitacao, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso, max(cp.data_convocacao) " +
				"		from vestibular.convocacao_processo_seletivo_discente cd" +
				"			join vestibular.convocacao_processo_seletivo cp using (id_convocacao_processo_seletivo)" +
				"			join graduacao.discente_graduacao dg on (dg.id_discente_graduacao=cd.id_discente)" +
				"			join discente d on (dg.id_discente_graduacao=d.id_discente)" +
				"			join graduacao.matriz_curricular m on (dg.id_matriz_curricular=m.id_matriz_curricular)" +
				"			join curso c2 on (c2.id_curso=m.id_curso)" +
				"			join ensino.turno t2 on (t2.id_turno=m.id_turno)" +
				"			join ensino.grau_academico g2 on (g2.id_grau_academico=m.id_grau_academico)" +
				"			left join graduacao.habilitacao h2 on (h2.id_habilitacao=m.id_habilitacao)" +
				"		where cp.id_processo_seletivo = :idProcessoSeletivo" +
				"		group by cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
				"			m.id_matriz_curricular, m.id_curso, c2.nome, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso" +
				"	) aux on (aux.id_inscricao_vestibular=rcc.id_inscricao_vestibular)"+
				" where roc.ordem_opcao = :primeiraOpcao " +
				" and rcc.aprovado_ama is false "+
				" and rcc.id_situacao_candidato in " + UFRNUtils.gerarStringIn(new int[]{SituacaoCandidato.SUPLENTE.getId(), SituacaoCandidato.APROVADO.getId()}) +
				" and iv.id_processo_seletivo = :idProcessoSeletivo" +
				" and rcc.id_inscricao_vestibular not in " +
				" (" +
				"		select c.id_inscricao_vestibular " +
				"		from vestibular.convocacao_processo_seletivo_discente c" +
				"		join vestibular.convocacao_processo_seletivo cd using (id_convocacao_processo_seletivo)" +
				"		where c.tipo in " + UFRNUtils.gerarStringIn(new int[]{TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal(), TipoConvocacao.CONVOCACAO_TURNO_DISTINTO.ordinal()}) +
				"		and cd.id_processo_seletivo = :idProcessoSeletivo" +
				" )" +
				" order by roc.id_matriz_curricular, roc.ordem_opcao, roc.classificacao, roc.argumento_final desc";
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql)
				.setInteger("primeiraOpcao", ResultadoOpcaoCurso.PRIMEIRA_OPCAO)
				.setInteger("idProcessoSeletivo", psVest.getId())
				.list();
		
		return popularResultado(list, result);
	}
	
	/**
	 * Retorna os próximos suplentes que não atigiram o Argumento Mínimo de Aprovação.
	 * @param psVest
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, List<ResultadoOpcaoCurso>> findSuplentesSemArgumentoMinimoAprovacao(ProcessoSeletivoVestibular psVest) throws DAOException {
		Map<Integer, List<ResultadoOpcaoCurso>> result = new HashMap<Integer, List<ResultadoOpcaoCurso>>();
		String sql =  "select roc.id_resultado_opcao_curso, roc.ordem_opcao, roc.classificacao, roc.id_matriz_curricular, rcc.id_resultado_classificacao_candidato, rcc.id_situacao_candidato, " +
		" iv.id_inscricao_vestibular, iv.numero_inscricao, pv.id_pessoa, pv.cpf_cnpj, pv.nome, c.id_curso, c.nome as nomeCurso, mun.nome as nomeMunicipio, t.id_turno, t.sigla, g.id_grau_academico, g.descricao, h.id_habilitacao, h.nome as nomeHabilitacao, " +
		" aux.id_convocacao_processo_seletivo_discente, aux.id_discente_graduacao, aux.matricula, aux.id_matriz_curricular as id_matriz_ant," +
		" aux.id_curso as id_curso_ant, aux.nomeCurso as nome_curso_ant, aux.id_turno as id_turno_ant, aux.sigla as sigla_ant, aux.id_habilitacao as id_habilitacao_ant, aux.nomeHabilitacao as nome_hab_ant, aux.id_grau_academico as id_grau_academico_ant, aux.descricao as desc_ant, aux.status, aux.periodo_ingresso," +
		" iv.id_processo_seletivo" +
		" from vestibular.resultado_opcao_curso roc" +
		"	join vestibular.resultado_classificacao_candidato rcc using (id_resultado_classificacao_candidato)" +
		"	join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
		"	join vestibular.pessoa_vestibular pv using (id_pessoa)" +
		"	join graduacao.matriz_curricular mc using (id_matriz_curricular)" +
		"	join curso c on (c.id_curso=mc.id_curso)" +
		"	join comum.municipio mun on (mun.id_municipio=c.id_municipio)"+
		"	join ensino.turno t on (t.id_turno=mc.id_turno)" +
		"	join ensino.grau_academico g on (g.id_grau_academico=mc.id_grau_academico)" +
		"	left join graduacao.habilitacao h on (h.id_habilitacao=mc.id_habilitacao)" +
		"	left join (" +
		"		select cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
		"			m.id_matriz_curricular, m.id_curso, c2.nome as nomeCurso, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome as nomeHabilitacao, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso, max(cp.data_convocacao) " +
		"		from vestibular.convocacao_processo_seletivo_discente cd" +
		"			join vestibular.convocacao_processo_seletivo cp using (id_convocacao_processo_seletivo)" +
		"			join graduacao.discente_graduacao dg on (dg.id_discente_graduacao=cd.id_discente)" +
		"			join discente d on (dg.id_discente_graduacao=d.id_discente)" +
		"			join graduacao.matriz_curricular m on (dg.id_matriz_curricular=m.id_matriz_curricular)" +
		"			join curso c2 on (c2.id_curso=m.id_curso)" +
		"			join ensino.turno t2 on (t2.id_turno=m.id_turno)" +
		"			join ensino.grau_academico g2 on (g2.id_grau_academico=m.id_grau_academico)" +
		"			left join graduacao.habilitacao h2 on (h2.id_habilitacao=m.id_habilitacao)" +
		"		where cp.id_processo_seletivo = :idProcessoSeletivo" +
		"		group by cd.id_inscricao_vestibular, cd.id_convocacao_processo_seletivo_discente, dg.id_discente_graduacao, d.matricula," +
		"			m.id_matriz_curricular, m.id_curso, c2.nome, m.id_turno, t2.sigla, m.id_habilitacao, h2.nome, m.id_grau_academico, g2.descricao, d.status, d.periodo_ingresso" +
		"	) aux on (aux.id_inscricao_vestibular=rcc.id_inscricao_vestibular)"+
		" where rcc.id_situacao_candidato in " + UFRNUtils.gerarStringIn(new int[]{SituacaoCandidato.NAO_CLASSIFICADO_AMA.getId(), SituacaoCandidato.APROVADO.getId()}) +
		" and iv.id_processo_seletivo = :idProcessoSeletivo" +
		" and rcc.id_inscricao_vestibular not in" +
		" (	" +
		"	select c.id_inscricao_vestibular" +
		"	from vestibular.convocacao_processo_seletivo_discente c" +
		"	join vestibular.convocacao_processo_seletivo cd using (id_convocacao_processo_seletivo)" +
		"   where c.tipo in " + UFRNUtils.gerarStringIn(new int[]{TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal()}) +
		"	and cd.id_processo_seletivo = :idProcessoSeletivo" +
		" ) " +
		" order by roc.id_matriz_curricular, roc.classificacao, roc.argumento_final desc, roc.ordem_opcao asc";
		
		@SuppressWarnings("unchecked")
		List<Object[]> list = getSession().createSQLQuery(sql)
				.setInteger("idProcessoSeletivo", psVest.getId())
				.list();
		
		return popularResultado(list, result);
	}

	/**
	 * Retorna um mapa com os ids de cada matriz curricular e o seu respectivo
	 * semestre de ingresso para atribuir aos novos alunos convocados.
	 * 
	 * @param psVest
	 * @param idMatrizes 
	 * @param date 
	 * @return
	 * @throws DAOException
	 */
	public Map<Integer, Integer> findMapaSemestreIngresso(ProcessoSeletivoVestibular psVest, Set<Integer> idMatrizes) throws DAOException {
		String sql = "select id_matriz_curricular, " +
		" (case when (vagas_periodo_1 > 0 and vagas_periodo_2 > 0) then 2" +
		"	when (vagas_periodo_1 > 0 and vagas_periodo_2 = 0) then 1" +
		"	when (vagas_periodo_1 = 0 and vagas_periodo_2 > 0) then 2" +
		" end) as semestre_ingresso" +
		" from ensino.oferta_vagas_curso o" +
		" where o.id_forma_ingresso = :idFormaIngresso" +
		" and o.ano = :ano" +
		(!isEmpty(idMatrizes) ? " and id_matriz_curricular in " + UFRNUtils.gerarStringIn(idMatrizes) : "");
		SQLQuery q = getSession().createSQLQuery(sql);
		q.setInteger("idFormaIngresso", psVest.getFormaIngresso().getId());
		q.setInteger("ano", psVest.getAnoEntrada());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		Map<Integer, Integer> result = new HashMap<Integer, Integer>();
		for (Object[] objects : lista) {
			result.put((Integer) objects[0], (Integer) objects[1]);
		}
		return result;
	}

	/**
	 * Retorna um array com duas coleções: uma lista de convocações e uma lista
	 * de cancelamentos, correspondentes a uma determinada chamada realizada. As
	 * duas consultas são realizadas com projeção, retornando somente as
	 * informações utilizadas na view.
	 * 
	 * @param idChamada
	 * @return
	 * @throws DAOException
	 */
	public Object[] findResumoConvocacao(Integer idChamada) throws DAOException {
		List<ConvocacaoProcessoSeletivoDiscente> convocacoes = new ArrayList<ConvocacaoProcessoSeletivoDiscente>();
		List<CancelamentoConvocacao> cancelamentos =  new ArrayList<CancelamentoConvocacao>();
		
		String projecao = "c.id, c.tipo, r.classificacao, iv.id, iv.numeroInscricao, dg.id, d.periodoIngresso, " +
				"p.nome, p.cpf_cnpj, m.id, curso.nome, mun.nome, mod.descricao, t.sigla, g.descricao, h.nome, cant.id, " +
				"c.dentroNumeroVagas, gcc, c.grupoCotaRemanejado";
		
		StringBuilder hql = new StringBuilder();
		hql.append("select " + projecao);
		hql.append(" from ConvocacaoProcessoSeletivoDiscente c " +
				"   left join c.grupoCotaConvocado gcc" +
				"	left join c.resultado r"+	
				"	join c.inscricaoVestibular iv"+	
				"	join c.discente dg"+	
				"	join dg.discente d"+	
				"	join d.pessoa p" +
				"	join dg.matrizCurricular m " +
				"	join m.curso curso " +
				"	join curso.municipio mun " +
				"	join curso.modalidadeEducacao mod " +
				"	join m.turno t " +
				"	join m.grauAcademico g " +
				"	left join m.habilitacao h " +
				"	left join c.convocacaoAnterior cant " +
				" where c.convocacaoProcessoSeletivo.id = :idConvocacao" +
				" order by m.id, r.classificacao, r.argumentoFinal desc, r.ordemOpcao asc");
		
		Query query = getSession().createQuery(hql.toString());
		query.setInteger("idConvocacao", idChamada);
		
		List<Integer> idsConvocacoesAnteriores = new ArrayList<Integer>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> lista = query.list();
		int col = 0;
		for(Object[] colunas: lista){
			ConvocacaoProcessoSeletivoDiscente c = new ConvocacaoProcessoSeletivoDiscente( (Integer)colunas[col++] );
			c.setTipo( (TipoConvocacao)colunas[col++] );
			
			ResultadoOpcaoCurso r = new ResultadoOpcaoCurso();
			r.setClassificacao( (Integer)colunas[col++] );
			c.setResultado(r);
			
			InscricaoVestibular iv = new InscricaoVestibular( (Integer)colunas[col++] );
			iv.setNumeroInscricao( (Integer)colunas[col++] );
			c.setInscricaoVestibular(iv);
			
			DiscenteGraduacao dg = new DiscenteGraduacao( (Integer)colunas[col++] );
			dg.setPeriodoIngresso( (Integer)colunas[col++] );
			
			Pessoa p = new Pessoa();
			p.setNome( (String)colunas[col++] );
			p.setCpf_cnpj( (Long)colunas[col++] );
			dg.setPessoa(p);
			
			MatrizCurricular m = new MatrizCurricular( (Integer)colunas[col++] );
			
			Curso curso = new Curso();
			curso.setNome( (String)colunas[col++] );
			
			Municipio mun = new Municipio();
			mun.setNome( (String)colunas[col++] );
			curso.setMunicipio(mun);
			
			ModalidadeEducacao mod = new ModalidadeEducacao();
			mod.setDescricao( (String)colunas[col++] );
			curso.setModalidadeEducacao(mod);
			
			m.setCurso(curso);
			
			Turno t = new Turno();
			t.setSigla( (String)colunas[col++] );
			m.setTurno(t);
			
			GrauAcademico g = new GrauAcademico();
			g.setDescricao( (String)colunas[col++] );
			m.setGrauAcademico(g);
			
			String nomeHabilitacao = (String)colunas[col++];
			if(nomeHabilitacao != null){
				Habilitacao h = new Habilitacao();
				h.setNome(nomeHabilitacao);
				m.setHabilitacao(h);
			}
			Integer idConvocacaoAnterior = (Integer)colunas[col++];
			if(idConvocacaoAnterior != null){
				idsConvocacoesAnteriores.add(idConvocacaoAnterior);
			}
//			"c.dentroNumeroVagas, c.grupoCotaConvocado, c.grupoCotaRemanejado";
			c.setDentroNumeroVagas((Boolean) colunas[col++]);
			c.setGrupoCotaConvocado((GrupoCotaVagaCurso) colunas[col++]);
			c.setGrupoCotaRemanejado((Boolean) colunas[col++]);
			
			dg.setMatrizCurricular(m);
			c.setDiscente(dg);
			
			convocacoes.add(c);
			col = 0;
		}
		
		if(idsConvocacoesAnteriores != null && !idsConvocacoesAnteriores.isEmpty()){
			String projecao2 = "cc.id, mot.descricao, c.id, dg.id, d.matricula, " +
					"p.nome, m.id, curso.nome, mun.nome, mod.descricao, t.sigla, g.descricao, h.nome";
			
			StringBuilder hql2 = new StringBuilder();
			hql2.append("select " + projecao2);
			hql2.append(" from CancelamentoConvocacao cc" +
					"	join cc.motivo mot " +
					"	join cc.convocacao c " +
					"	join c.discente dg"+	
					"	join dg.discente d"+	
					"	join d.pessoa p" +
					"	join dg.matrizCurricular m " +
					"	join m.curso curso " +
					"	join curso.municipio mun " +
					"	join curso.modalidadeEducacao mod " +
					"	join m.turno t " +
					"	join m.grauAcademico g " +
					"	left join m.habilitacao h " +
					" where c.id in " + UFRNUtils.gerarStringIn(idsConvocacoesAnteriores));
			
			
			query = getSession().createQuery(hql2.toString());
			@SuppressWarnings("unchecked")
			List<Object[]> lista2 = query.list();
			col = 0;
			for(Object[] colunas: lista2){
				CancelamentoConvocacao cc = new CancelamentoConvocacao();
				cc.setId( (Integer)colunas[col++] );
				
				MotivoCancelamentoConvocacao mot = new MotivoCancelamentoConvocacao();
				mot.setDescricao( (String)colunas[col++] );
				cc.setMotivo(mot);
				
				ConvocacaoProcessoSeletivoDiscente c = new ConvocacaoProcessoSeletivoDiscente( (Integer)colunas[col++] );
				
				DiscenteGraduacao dg = new DiscenteGraduacao( (Integer)colunas[col++] );
				dg.setMatricula( (Long)colunas[col++] );
				
				Pessoa p = new Pessoa();
				p.setNome( (String)colunas[col++] );
				dg.setPessoa(p);
				
				MatrizCurricular m = new MatrizCurricular( (Integer)colunas[col++] );
				
				Curso curso = new Curso();
				curso.setNome( (String)colunas[col++] );
				
				Municipio mun = new Municipio();
				mun.setNome( (String)colunas[col++] );
				curso.setMunicipio(mun);
				
				ModalidadeEducacao mod = new ModalidadeEducacao();
				mod.setDescricao( (String)colunas[col++] );
				curso.setModalidadeEducacao(mod);
				m.setCurso(curso);
				
				Turno t = new Turno();
				t.setSigla( (String)colunas[col++] );
				m.setTurno(t);
				
				GrauAcademico g = new GrauAcademico();
				g.setDescricao( (String)colunas[col++] );
				m.setGrauAcademico(g);
				
				String nomeHabilitacao = (String)colunas[col++];
				if(nomeHabilitacao != null){
					Habilitacao h = new Habilitacao();
					h.setNome(nomeHabilitacao);
					m.setHabilitacao(h);
				}
				
				dg.setMatrizCurricular(m);
				c.setDiscente(dg);
				cc.setConvocacao(c);
				
				cancelamentos.add(cc);
				col = 0;
			}
		}
		
		return new Object[]{convocacoes, cancelamentos};
	}
	
	/** Retorna as convocações de discentes que estão cadastrados para a segunda opção.
	 * @param psVest
	 * @param idMatrizes
	 * @return
	 * @throws DAOException
	 */
	public List<ConvocacaoProcessoSeletivoDiscente> findConvocadosSegundaOpcao(ProcessoSeletivoVestibular psVest, Set<Integer> idMatrizes) throws DAOException {
		int status[] = { StatusDiscente.CADASTRADO, StatusDiscente.ATIVO, StatusDiscente.PENDENTE_CADASTRO, StatusDiscente.ATIVO_DEPENDENCIA} ;
		String projecao = "cpsd.id, cpsd.discente.id, cpsd.discente.matrizCurricular.id," +
				" cpsd.inscricaoVestibular.id";
		StringBuilder hql = new StringBuilder("select ").append(projecao).append(
				" from ConvocacaoProcessoSeletivoDiscente cpsd" +
				" where cpsd.discente.discente.status in ").append(UFRNUtils.gerarStringIn(status)).append(
				" and cpsd.convocacaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo" +
				" and cpsd.discente.discente.matricula = 2013060430");
		if (!isEmpty(idMatrizes))
			hql.append(" and cpsd.discente.matrizCurricular.id in ").append(UFRNUtils.gerarStringIn(idMatrizes));
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", psVest.getId());
		@SuppressWarnings("unchecked")
		List<ConvocacaoProcessoSeletivoDiscente> lista = (List<ConvocacaoProcessoSeletivoDiscente>) HibernateUtils.parseTo(q.list(), projecao, ConvocacaoProcessoSeletivoDiscente.class, "cpsd");
		return lista;
	}

	/** Retorna um mapa com cotas de oferta que possuem vagas não preenchidas.
	 * @param processoSeletivo
	 * @param semestreConvocacao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<CotaOfertaVagaCurso, Integer> findCotasComVagasRemanescentes(ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestreConvocacao, boolean incluirPreCadastrado, boolean incluirPendenteCadastro) throws HibernateException, DAOException {
		Map<CotaOfertaVagaCurso, Integer> mapa = new LinkedHashMap<CotaOfertaVagaCurso, Integer>();
		// determina as vagas remancescentes para o grupo de cotas
		Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
		if (incluirPreCadastrado) {
			statusQueOcupamVaga.add(StatusDiscente.PRE_CADASTRADO);
		}
		if (incluirPendenteCadastro) {
			statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
		}
		String campoVagas = null;
		switch (semestreConvocacao) {
			case CONVOCA_APENAS_PRIMEIRO_SEMESTRE : campoVagas = "vagas_periodo_1"; break; 
			case CONVOCA_APENAS_SEGUNDO_SEMESTRE : campoVagas = "vagas_periodo_2"; break;
			case CONVOCA_TODOS_SEMESTRES : campoVagas = "total_vagas"; break;
		}
		String hql = "select covc.id_cota_oferta_vaga_curso," +
				" covc.id_oferta_vagas_curso," +
				" covc.id_grupo_cota_vaga_curso," +
				" o.id_matriz_curricular," +
				" cast(( covc."+campoVagas+" - coalesce(preenchidas.total, 0)) as integer) as ociosas "
				+ " from ensino.grupo_cota_vaga_curso gcvc"
				+ " inner join ensino.cota_oferta_vaga_curso covc using (id_grupo_cota_vaga_curso)"
				+ " inner join ensino.oferta_vagas_curso o  using (id_oferta_vagas_curso)"
				+ " inner join vestibular.processo_seletivo ps on (o.id_forma_ingresso = ps.id_forma_ingresso and o.ano = ps.ano_entrada)  "
				+ " left join (  select dg.id_matriz_curricular, d.id_forma_ingresso, cpsd.id_grupo_cota_vaga_curso, count(*) as total "
				+ " 	from graduacao.discente_graduacao dg"
				+ " 	join discente d on dg.id_discente_graduacao = d.id_discente"
				+ "     join vestibular.convocacao_processo_seletivo_discente cpsd using (id_discente, id_matriz_curricular)"
				+ " 	where d.ano_ingresso = :ano"
				+ " 	and d.status in " + gerarStringIn(statusQueOcupamVaga)
				+ (semestreConvocacao != SemestreConvocacao.CONVOCA_TODOS_SEMESTRES ? "     and d.periodo_ingresso = :semestre" : "")
				+ " 	group by id_matriz_curricular, d.id_forma_ingresso, cpsd.id_grupo_cota_vaga_curso"
				+ " 	) as preenchidas on (preenchidas.id_matriz_curricular = o.id_matriz_curricular"
				+ "                      and preenchidas.id_forma_ingresso = ps.id_forma_ingresso" 
				+ "                      and preenchidas.id_grupo_cota_vaga_curso = covc.id_grupo_cota_vaga_curso)"
				+ " where ps.id_processo_seletivo = :idProcessoSeletivo"
				+ " and cast(( covc."+campoVagas+" - coalesce(preenchidas.total, 0)) as integer) > 0";
		Query q = getSession().createSQLQuery(hql);
		q.setInteger("ano", processoSeletivo.getAnoEntrada());
		if (semestreConvocacao != SemestreConvocacao.CONVOCA_TODOS_SEMESTRES)
			q.setInteger("semestre", semestreConvocacao.ordinal());
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		if (lista != null){
			// grupos de cotas
			@SuppressWarnings("unchecked")
			List<GrupoCotaVagaCurso> grupos = getSession().createCriteria(GrupoCotaVagaCurso.class).list();
			for (Object[] obj : lista) {
				CotaOfertaVagaCurso cota = new CotaOfertaVagaCurso();
				int i = 0;
				cota.setId((Integer) obj[i++]);
				cota.setOfertaVagasCurso(new OfertaVagasCurso((Integer) obj[i++]));
				int idGrupo = (Integer) obj[i++];
				cota.getOfertaVagasCurso().setMatrizCurricular(new MatrizCurricular((Integer) obj[i++]));
				for (GrupoCotaVagaCurso grupo : grupos)
					if (grupo.getId() == idGrupo) {
						cota.setGrupoCota(grupo); 
						break;
					}
				mapa.put(cota, (Integer) obj[i++]);
			}
		}
		return mapa;
	}
	
	/** Retorna uma coleção de resultados de opção de curso de candidatos para o preenchimento de vagas ociosas de cotas em determinar oferta.
	 * @param oferta
	 * @param grupoCota
	 * @param vagasOciosas
	 * @param processoSeletivo
	 * @param semestreConvocacao
	 * @param convocados
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ResultadoOpcaoCurso> resultadosOpcaoCursoClassificadosCota(OfertaVagasCurso oferta, GrupoCotaVagaCurso grupoCota, int vagasOciosas, ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestreConvocacao, List<ConvocacaoProcessoSeletivoDiscente> convocados, int ordemOpcao) throws HibernateException, DAOException {
		// se tem vagas ociosas, cria as convocações
		if (vagasOciosas > 0){
			Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
			statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
			statusQueOcupamVaga.add(StatusDiscente.PRE_CADASTRADO);
			statusQueOcupamVaga.add(StatusDiscente.EXCLUIDO);
			statusQueOcupamVaga.add(StatusDiscente.CANCELADO);
			Collection<Integer> tipoConvocacao = new LinkedList<Integer>();
			tipoConvocacao.add(TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal());
			if (ordemOpcao == 2)
				tipoConvocacao.add(TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO.ordinal());
			String projecao = "roc.id, roc.classificacao, roc.matrizCurricular.id, roc.resultadoClassificacaoCandidato.id," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.id, roc.resultadoClassificacaoCandidato.inscricaoVestibular.numeroInscricao," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.id," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.nome, roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.cpf_cnpj";
			String hqlConvocacao = "select " + projecao
				+ " from ResultadoOpcaoCurso roc"
				+ " inner join roc.resultadoClassificacaoCandidato rcc"
				+ " inner join rcc.inscricaoVestibular iv,"
				+ " GrupoCotaVagaCurso gcvc"
				+ " where iv.processoSeletivo.id = :idProcessoSeletivo"
				+ " and roc.matrizCurricular.id = :idMatrizCurricular"
				+ " and gcvc.id = :idGrupoCota"
				+ " and gcvc.egressoEscolaPublica = iv.egressoEscolaPublica"
				+ " and gcvc.baixaRendaFamiliar = iv.baixaRendaFamiliar"
				+ " and gcvc.pertenceGrupoEtnico = iv.pertenceGrupoEtnico"
				+ " and roc.ordemOpcao = :ordemOpcao"
				+ " and iv.id not in ("
				+ "     select cpsd.inscricaoVestibular.id"
				+ "     from ConvocacaoProcessoSeletivoDiscente cpsd" 
				+ "     where cpsd.discente.discente.status in " + gerarStringIn(statusQueOcupamVaga)
				+ "     and cpsd.tipo in "+gerarStringIn(tipoConvocacao)+")"
				+ " order by roc.classificacao";
			Query qc = getSession().createQuery(hqlConvocacao);
			qc.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			qc.setInteger("idMatrizCurricular", oferta.getMatrizCurricular().getId());
			qc.setInteger("idGrupoCota", grupoCota.getId());
			qc.setInteger("ordemOpcao", ordemOpcao);
			@SuppressWarnings("unchecked")
			Collection<ResultadoOpcaoCurso> resultados = HibernateUtils.parseTo(qc.list(), projecao, ResultadoOpcaoCurso.class, "roc");
			// remove as convocações anteriores e limita ao número de vagas
			removeConvocacoesAnteriores(vagasOciosas, convocados, resultados);
			carregaConvocacoesAnteriores(resultados);
			return resultados;
		}
		return null; 
	}

	/** Remove as convocações anteriores e limita as convocações a um número de vagas 
	 * @param vagasOciosas
	 * @param convocados
	 * @param resultados
	 */
	private void removeConvocacoesAnteriores(int vagasOciosas, List<ConvocacaoProcessoSeletivoDiscente> convocados, Collection<ResultadoOpcaoCurso> resultados) {
		if (convocados == null) return;
		Collection<Integer> idInscricoes = new LinkedList<Integer>();
		for (ConvocacaoProcessoSeletivoDiscente convocado : convocados) {
			idInscricoes.add(convocado.getInscricaoVestibular().getId());
		}
		Iterator<ResultadoOpcaoCurso> iterator = resultados.iterator();
		while (iterator.hasNext()) {
			ResultadoOpcaoCurso resultado = iterator.next();
			if (idInscricoes.contains(resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId())
					|| vagasOciosas == 0)
				iterator.remove();
			else
				vagasOciosas--;
		}
	}
	
	/** Retorna uma coleção de resultados de opção de curso de candidatos para o preenchimento de vagas ociosas em determinar oferta.
	 * @param oferta
	 * @param vagasOciosas
	 * @param processoSeletivo
	 * @param semestreConvocacao
	 * @param convocados
	 * @param opcao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ResultadoOpcaoCurso> resultadosClassificadosOpcao(OfertaVagasCurso oferta, int vagasOciosas, ProcessoSeletivoVestibular processoSeletivo, SemestreConvocacao semestreConvocacao, List<ConvocacaoProcessoSeletivoDiscente> convocados, int opcao) throws HibernateException, DAOException {
		// se tem vagas ociosas, cria as convocações
		if (vagasOciosas > 0){
			Collection<Integer> statusQueOcupamVaga = StatusDiscente.getStatusComVinculo();
			statusQueOcupamVaga.add(StatusDiscente.PENDENTE_CADASTRO);
			statusQueOcupamVaga.add(StatusDiscente.PRE_CADASTRADO);
			statusQueOcupamVaga.add(StatusDiscente.EXCLUIDO);
			statusQueOcupamVaga.add(StatusDiscente.CANCELADO);
			Collection<Integer> tiposConvocacao = new LinkedList<Integer>();
			tiposConvocacao.add(TipoConvocacao.CONVOCACAO_PRIMEIRA_OPCAO.ordinal());
			if (opcao == 2)
				tiposConvocacao.add(TipoConvocacao.CONVOCACAO_SEGUNDA_OPCAO.ordinal());
			String projecao = "roc.id, roc.classificacao, roc.matrizCurricular.id, roc.resultadoClassificacaoCandidato.id," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.id, roc.resultadoClassificacaoCandidato.inscricaoVestibular.numeroInscricao," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.id," +
					" roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.nome, roc.resultadoClassificacaoCandidato.inscricaoVestibular.pessoa.cpf_cnpj";
			String hqlConvocacao = "select " + projecao
				+ " from ResultadoOpcaoCurso roc"
				+ " inner join roc.resultadoClassificacaoCandidato rcc"
				+ " inner join rcc.inscricaoVestibular iv"
				+ " where iv.processoSeletivo.id = :idProcessoSeletivo"
				+ " and roc.matrizCurricular.id = :idMatrizCurricular"
				+ " and roc.ordemOpcao = :opcao"
				// candidatos convocados no processamento
				+ " and iv.id not in ("
				+ "     select cpsd.inscricaoVestibular.id"
				+ "     from ConvocacaoProcessoSeletivoDiscente cpsd" 
				+ "     where cpsd.discente.discente.status in " + gerarStringIn(statusQueOcupamVaga)
				+ "     and cpsd.convocacaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo"
				+ "     and cpsd.tipo in " + gerarStringIn(tiposConvocacao) +" )"
				+ " order by roc.classificacao";
			Query qc = getSession().createQuery(hqlConvocacao);
			qc.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			qc.setInteger("idMatrizCurricular", oferta.getMatrizCurricular().getId());
			qc.setInteger("opcao", opcao);
			@SuppressWarnings("unchecked")
			Collection<ResultadoOpcaoCurso> resultados = HibernateUtils.parseTo(qc.list(), projecao, ResultadoOpcaoCurso.class, "roc");
			carregaConvocacoesAnteriores(resultados);
			return resultados;
		}
		return null; 
	}

	/** Carrega as convocações anteriores de um discente.
	 * @param resultados
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaConvocacoesAnteriores(Collection<ResultadoOpcaoCurso> resultados) throws HibernateException, DAOException {
		if (!isEmpty(resultados)) {
			Collection<Integer> ids = new LinkedList<Integer>();
			for (ResultadoOpcaoCurso resultado : resultados) 
				ids.add(resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId());
			if (isEmpty(ids)) return;
			String projecao = "cpsd.id, cpsd.inscricaoVestibular.id, cpsd.discente.id, cpsd.discente.discente.matricula, " +
					 " cpsd.discente.discente.status, cpsd.discente.discente.pessoa.id, cpsd.discente.discente.pessoa.nome," +
					 " matrizCurricular.id as cpsd.discente.matrizCurricular.id, " +
					 " cpsd.discente.discente.curso.id as cpsd.discente.discente.curso.id, " +
					 " matrizCurricular.curso.id as cpsd.discente.matrizCurricular.curso.id, " +
					 " matrizCurricular.turno.id as cpsd.discente.matrizCurricular.turno.id, " +
					 " grauAcademico.id as cpsd.discente.matrizCurricular.grauAcademico.id," +
					 " habilitacao.id as cpsd.discente.matrizCurricular.habilitacao.id";
			String hql = "select " + HibernateUtils.removeAliasFromProjecao(projecao) + " from ConvocacaoProcessoSeletivoDiscente cpsd" +
					" inner join cpsd.matrizCurricular matrizCurricular" +
					" left join matrizCurricular.grauAcademico grauAcademico" +
					" left join matrizCurricular.habilitacao habilitacao" +
					" where cpsd.inscricaoVestibular.id in " + gerarStringIn(ids);  
			Query q = getSession().createQuery(hql);
			@SuppressWarnings("unchecked")
			Collection<ConvocacaoProcessoSeletivoDiscente> anteriores = HibernateUtils.parseTo(q.list(), projecao, ConvocacaoProcessoSeletivoDiscente.class, "cpsd");
			if (!isEmpty(anteriores)) {
				for (ConvocacaoProcessoSeletivoDiscente convocacao : anteriores) {
					for (ResultadoOpcaoCurso resultado : resultados) {
						if (resultado.getResultadoClassificacaoCandidato().getInscricaoVestibular().getId() == convocacao.getInscricaoVestibular().getId()) {
							resultado.setConvocacaoAnterior(convocacao);
							break;
						}
					}
				}
			}
		}
	}
}
