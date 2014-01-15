/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 13/12/2010
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.math.BigInteger;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.CotaOfertaVagaCurso;
import br.ufrn.sigaa.ensino.dominio.OfertaVagasCurso;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.MotivoCancelamentoConvocacao;
import br.ufrn.sigaa.vestibular.dominio.ConvocacaoProcessoSeletivoDiscente;
import br.ufrn.sigaa.vestibular.dominio.GrupoCotaVagaCurso;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.ResultadoClassificacaoCandidato;
import br.ufrn.sigaa.vestibular.dominio.ResultadoOpcaoCurso;
import br.ufrn.sigaa.vestibular.dominio.SituacaoCandidato;

/**
 * Dao responsável pelas consultas que são inerentes as convocações 
 * de Processo Seletivo aos discentes. 
 * 
 * @author Rafael Gomes
 */
public class ConvocacaoProcessoSeletivoDiscenteDao extends GenericSigaaDAO {

	/**
	 * Retorna todas os discentes  com status {@link StatusDiscente.PENDENTE_CADASTRO} para o processo
	 * seletivo vestibular informado como parâmetro.
	 * 
	 * @param psVest
	 * @return
	 * @throws DAOException
	 */
	public Collection<DiscenteGraduacao> findDiscentesPendentesCadastro(ProcessoSeletivoVestibular psVest) throws DAOException {
		String projecao = "dg.id, dg.discente.id, dg.discente.status";
//				", m.id as dg.matrizCurricular.id," +
//				" m.curso.nome as dg.matrizCurricular.curso.nome," +
//				" m.curso.municipio.nome as dg.matrizCurricular.curso.municipio.nome," +
//				" enfase.nome as dg.matrizCurricular.enfase.nome," +
//				" habilitacao.nome as dg.matrizCurricular.habilitacao.nome," +
//				" m.turno.sigla as dg.matrizCurricular.turno.sigla," +
//				" m.grauAcademico.descricao as dg.matrizCurricular.grauAcademico.descricao," +
//				" dg.discente.status";
		StringBuilder hql = new StringBuilder(" SELECT ")
				.append(HibernateUtils.removeAliasFromProjecao(projecao))
				.append(" FROM DiscenteGraduacao dg "
//						+ " JOIN dg.matrizCurricular m"
//						+ " LEFT JOIN m.enfase enfase"
//						+ " LEFT JOIN m.habilitacao habilitacao"
						+ " WHERE dg.id in ("
						+ "  select c.discente.id"
						+ "  from ConvocacaoProcessoSeletivoDiscente c"
						+ "  where c.convocacaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo)"
						+ " and dg.discente.status = :pendenteCadastro "
//						+ " ORDER BY m.curso.municipio.nome, m.curso.nome, enfase.nome, habilitacao.nome, m.turno.sigla"
						);
		Query q = getSession().createQuery(hql.toString())
				.setInteger("idProcessoSeletivo", psVest.getId())
				.setInteger("pendenteCadastro", StatusDiscente.PENDENTE_CADASTRO);
		@SuppressWarnings("unchecked")
		Collection<DiscenteGraduacao> lista = HibernateUtils.parseTo(q.list(), projecao, DiscenteGraduacao.class, "dg");
		return lista;
	}
	
	/**
	 * Retorna a InscricaoVestibular pelo número da Inscrição do candidato.
	 * @param numeroInscricao
	 * @return
	 * @throws DAOException
	 */
	public InscricaoVestibular findByNumeroInscricao(Integer numeroInscricao) throws DAOException {
		
		Criteria c = getSession().createCriteria(InscricaoVestibular.class);
		c.add(Restrictions.eq("numeroInscricao", numeroInscricao));
		c.setMaxResults(1);		
		return (InscricaoVestibular) c.uniqueResult();
		
	}
	
	/**
	 * Retornar a matriz curricular referente a opção de aprovação do candidato para a 1ª chamada.
	 * @param resultCandidato
	 * @return
	 * @throws DAOException
	 */
	public MatrizCurricular findMatrizCurricularByOpcaoAprovacao( int idMatrizCurricular ) throws DAOException{
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT id, curso.id FROM MatrizCurricular ");
			hql.append("WHERE id = :idMatrizCurricular");
			
			Query q = getSession().createQuery(hql.toString());
			q.setLong("idMatrizCurricular", idMatrizCurricular);
			
			Object[] result = (Object[]) q.uniqueResult();
			
			MatrizCurricular matriz = null;
			if (!isEmpty(result) ) {
				matriz = new MatrizCurricular((Integer) result[0]);
				matriz.setCurso( new Curso((Integer) result[1]) );
			}
			
			return matriz;

		} catch (Exception e) {
			e.printStackTrace();
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna os resultados de classificação do vestibular para os candidatos aprovados para 1ª chamada.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResultadoClassificacaoCandidato> findResultadoVestibularAprovados(ProcessoSeletivoVestibular processoSeletivo, MatrizCurricular matriz) throws DAOException{
		String projecao = "rcc.id,rcc.inscricaoVestibular.id, rcc.inscricaoVestibular.pessoa.id, rcc.inscricaoVestibular.pessoa.cpf_cnpj, " +
				"rcc.numeroInscricao, rcc.opcaoAprovacao, rcc.semestreAprovacao, " +
				"grupoCotaConvocado.id as rcc.grupoCotaConvocado.id, " +
				"grupoCotaConvocado.descricao as rcc.grupoCotaConvocado.descricao, " +
				"rcc.grupoCotaRemanejado ";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT ").append(HibernateUtils.removeAliasFromProjecao(projecao));
			hql.append(" FROM ResultadoClassificacaoCandidato rcc " +
					" left join rcc.grupoCotaConvocado grupoCotaConvocado");
			hql.append(" WHERE rcc.situacaoCandidato.id = :situacaoCandidato ");
			if (!isEmpty(matriz))
				hql.append(" AND rcc.opcaoAprovacao = :idMatrizCurricular");
			hql.append(" AND rcc.inscricaoVestibular.processoSeletivo.id = :idProcessoSeletivo ");
			hql.append(" AND NOT EXISTS ( select new ConvocacaoProcessoSeletivoDiscente(id) from ConvocacaoProcessoSeletivoDiscente where inscricaoVestibular.id = rcc.inscricaoVestibular.id ) ");
			hql.append(" ORDER BY rcc.opcaoAprovacao, rcc.inscricaoVestibular.pessoa.nome");
			
			Query q = getSession().createQuery(hql.toString());
			q.setInteger("situacaoCandidato", SituacaoCandidato.APROVADO.getId());
			q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			if (!isEmpty(matriz))
				q.setInteger("idMatrizCurricular", matriz.getId());
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<ResultadoClassificacaoCandidato> result = HibernateUtils.parseTo(lista, projecao, ResultadoClassificacaoCandidato.class, "rcc");
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}
	
	/**
	 * Retorna a convocação de processo seletivo mediante a inscrição de vestibular.
	 * @param inscricaoVestibular
	 * @return
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoDiscente findConvocacaoByInscricaoVestibular(InscricaoVestibular inscricaoVestibular) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivoDiscente.class);
			c.add(Restrictions.eq("inscricaoVestibular.id",inscricaoVestibular.getId()));
			
			return (ConvocacaoProcessoSeletivoDiscente) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}

	/** Indica se o candidato já foi convocado para o preenchimento de vaga.
	 * @param inscricaoVestibular
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isConvocado(InscricaoVestibular inscricaoVestibular) throws HibernateException, DAOException {
		Query q = getSession().createQuery("select count(id) from ConvocacaoProcessoSeletivoDiscente where inscricaoVestibular.id = :id");
		q.setParameter("id", inscricaoVestibular.getId());
		return (Long) (q.uniqueResult()) > 0;
	}
	
	/**
	 * Indica se um conjunto de candidatos já foram convocados para o
	 * preenchimento de vaga. Será retornado um mapa de pares <ID da Inscrição
	 * do Vestibular, Booleano (convocado = true)>.
	 * 
	 * @param inscricaoVestibular
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Map<Integer, Boolean> mapaConvocados(Collection<Integer> idsInscricaoVestibular) throws HibernateException, DAOException {
		Map<Integer, Boolean> mapa = new TreeMap<Integer, Boolean>();
		if (!isEmpty(idsInscricaoVestibular)) {
			Query q = getSession().createQuery("select inscricaoVestibular.id" +
					" from ConvocacaoProcessoSeletivoDiscente" +
					" where inscricaoVestibular.id in " + UFRNUtils.gerarStringIn(idsInscricaoVestibular));
			@SuppressWarnings("unchecked")
			List<Integer> idsConvocados = q.list();
			for (Integer idInscricao :idsInscricaoVestibular) {
				boolean convocado = idsConvocados.contains(idInscricao);
				mapa.put(idInscricao, convocado);
			}
		}
		return mapa;
	}

	/** Retorna uma lista de convocações de discentes de um dado processo seletivo e convocação.
	 * @param idProcessoSeletivo
	 * @param idConvocacaoProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscente> dadosContatoConvocados(int idProcessoSeletivo, int idConvocacaoProcessoSeletivo) throws HibernateException, DAOException {
		StringBuilder projecao = new StringBuilder("cpsd.id," +
				"cpsd.tipo," +
				"cpsd.discente.discente.id," +
				"cpsd.discente.discente.matricula," +
				"cpsd.discente.discente.anoIngresso," +
				"cpsd.discente.discente.periodoIngresso," +
				"cpsd.discente.discente.pessoa.id," +
				"cpsd.discente.discente.pessoa.nome," +
				"cpsd.discente.discente.pessoa.codigoAreaNacionalTelefoneFixo," +
				"cpsd.discente.discente.pessoa.telefone," +
				"cpsd.discente.discente.pessoa.celular," +
				"cpsd.discente.discente.pessoa.email," +
				"cpsd.discente.discente.pessoa.enderecoContato," +
				"cpsd.discente.discente.curso.id," +
				"cpsd.discente.discente.curso.nome," +
				"cpsd.discente.discente.curso.municipio.id," +
				"cpsd.discente.discente.curso.municipio.nome," +
				"cpsd.convocacaoProcessoSeletivo.id," +
				"cpsd.convocacaoProcessoSeletivo.descricao," +
				"cpsd.discente.matrizCurricular.turno.sigla");
		StringBuilder hql = new StringBuilder("SELECT ").append(projecao)
		.append(" FROM ConvocacaoProcessoSeletivoDiscente cpsd" +
				" INNER JOIN cpsd.convocacaoProcessoSeletivo cps" +
				" WHERE cps.processoSeletivo.id = :idProcessoSeletivo" +
				" AND cps.id = :idConvocacaoProcessoSeletivo" +
				" ORDER BY cpsd.discente.discente.curso.municipio.nome, cpsd.discente.discente.curso.nome, cpsd.discente.discente.pessoa.nome");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		q.setInteger("idConvocacaoProcessoSeletivo", idConvocacaoProcessoSeletivo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao.toString(), ConvocacaoProcessoSeletivoDiscente.class, "cpsd");
	}

	/** Retorna uma coleção de {@link ResultadoOpcaoCurso} de um processo seletivo para uma determinada matriz curricular.
	 * @param processoSeletivo
	 * @param matriz
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ResultadoOpcaoCurso> findResultadoOpcaoCurso(ProcessoSeletivoVestibular processoSeletivo, MatrizCurricular matriz) throws HibernateException, DAOException {
		String projecao = "roc.id, roc.argumentoFinal, roc.argumentoFinalSemBeneficio, roc.classificacao, roc.matrizCurricular.id," +
				" roc.ordemOpcao, roc.resultadoClassificacaoCandidato.id";
		StringBuilder hql = new StringBuilder("SELECT ").append(projecao)
				.append(" FROM ResultadoOpcaoCurso roc" +
						" WHERE roc.resultadoClassificacaoCandidato.inscricaoVestibular.processoSeletivo.id = :idProcessoSeletivo" +
						" AND roc.resultadoClassificacaoCandidato.opcaoAprovacao = :idMatriz");
		hql.append(" ORDER BY roc.resultadoClassificacaoCandidato.id, roc.ordemOpcao");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		q.setInteger("idMatriz", matriz.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao.toString(), ResultadoOpcaoCurso.class, "roc");
	}

	/** Retorna o cancelamento associado à convocação do discente.
	 * @param idConvocacaoDiscente
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public CancelamentoConvocacao findCancelamentoByConvocacao(int idConvocacaoDiscente) throws HibernateException, DAOException {
		Criteria c = getSession().createCriteria(CancelamentoConvocacao.class)
				.createCriteria("convocacao")
				.add(Restrictions.eq("id", idConvocacaoDiscente));
		return (CancelamentoConvocacao) c.uniqueResult();				
	}

	/** Retorna uma lista de convocações para um processo seletivo, ordenadas por classificação de aprovação dentro de uma matriz curricular
	 * @param idProcessoSeletivo
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes 
	 * @param naoExcluir 
	 * @param preencherVagasComCotistas 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscente> classificaPreCadastramento(int idProcessoSeletivo, Map<OfertaVagasCurso, Integer> ofertaComVagasRemanescentes, Map<CotaOfertaVagaCurso, Integer> cotasComVagasRemanescentes, boolean naoExcluir, boolean preencherVagasComCotistas) throws HibernateException, DAOException {
		String projecao =
				"cpsd.id_convocacao_processo_seletivo_discente as id," +
				"cpsd.dentro_numero_vagas                      as dentroNumeroVagas," +
				"cpsd.ano                                      as ano," +
				"cpsd.periodo                                  as periodo," +
				"gcvc.id_grupo_cota_vaga_curso                 as grupoCotaConvocado.id," +
				"gcvc.descricao                                as grupoCotaConvocado.descricao," +
				"matriz_curricular.id_matriz_curricular        as matrizCurricular.id," +
				"curso.id_curso                                as matrizCurricular.curso.id," +
				"curso.nome                                    as matrizCurricular.curso.nome," +
				"grau_academico.descricao                      as matrizCurricular.grauAcademico.descricao," +
				"turno.sigla                                   as matrizCurricular.turno.sigla," +
				"habilitacao.nome                              as matrizCurricular.habilitacao.nome," +
				"municipio.nome                                as matrizCurricular.curso.municipio.nome," +
				"enfase.nome                                   as matrizCurricular.enfase.nome," +
				"pessoa.id_pessoa                              as discente.discente.pessoa.id," +
				"pessoa.nome                                   as discente.discente.pessoa.nome," +
				"pessoa.email                                  as discente.discente.pessoa.email," +
				"pessoa.cpf_cnpj                               as discente.discente.pessoa.cpf_cnpj," +
				"discente.id_discente                          as discente.discente.id," +
				"discente.id_discente                          as discente.id," +
				"discente.nivel                                as discente.nivel," +
				"discente.matricula                            as discente.discente.matricula," +
				"discente.status                               as discente.discente.status," +
				"discente.ano_ingresso                         as discente.discente.anoIngresso," +
				"discente.periodo_ingresso                     as discente.discente.periodoIngresso," +
				"iv.id_inscricao_vestibular                    as inscricaoVestibular.id," +
				"iv.numero_inscricao                           as inscricaoVestibular.numeroInscricao," +
				"classificacao                                 as resultado.classificacao";
		// POG: o hibernate traz o mesmo valor para as colunas curso.nome, habilitacao.nome, pessoa.nome, etc...
		// dai a necessidade de renomear as colunas
		StringBuilder projecaoPog = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(projecao, ",");
		int k = 1;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] split = token.split(" as ");
			projecaoPog.append(split[0]).append(" as ").append("coluna_").append(k++).append(", ");
		}
		projecaoPog.delete(projecaoPog.lastIndexOf(","), projecaoPog.length());
		String sql = "select " + projecaoPog +
				" from graduacao.matriz_curricular " +
				" inner join ensino.turno using (id_turno)" +
				" left join graduacao.habilitacao using (id_habilitacao)" +
				" left join graduacao.enfase using (id_enfase)" +
				" inner join ensino.grau_academico using (id_grau_academico)" +
				" inner join curso on (matriz_curricular.id_curso = curso.id_curso)" +
				" inner join comum.municipio using (id_municipio)" +
				" inner join graduacao.discente_graduacao using (id_matriz_curricular)" +
				" inner join discente on (id_discente = id_discente_graduacao)" +
				" inner join comum.pessoa using (id_pessoa)" +
				" inner join vestibular.convocacao_processo_seletivo_discente cpsd using (id_discente)" +
				" left join ensino.grupo_cota_vaga_curso gcvc using (id_grupo_cota_vaga_curso)" +
				" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
				" inner join vestibular.resultado_classificacao_candidato using (id_inscricao_vestibular)" +
				" inner join vestibular.resultado_opcao_curso roc using (id_resultado_classificacao_candidato)" +
				" where discente.status = " + StatusDiscente.PRE_CADASTRADO +
				" and roc.id_resultado_opcao_curso = cpsd.id_resultado" +
				" and iv.id_processo_seletivo = :idProcessoSeletivo"+
				" order by matriz_curricular.id_matriz_curricular, classificacao";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		// POG para converter BigInt em long (matrícula/CPF)
		for (Object[] obj : lista) {
			for (int i = 0; i < obj.length; i++)
				if (obj[i] instanceof BigInteger) obj[i] = ((BigInteger) obj[i]).longValue();
		}
		Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes = HibernateUtils.parseTo(lista, projecao, ConvocacaoProcessoSeletivoDiscente.class);
		// seta em discente a matriz curricular da convocação (que não vem setado na consulta)
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes)
			convocacao.getDiscente().setMatrizCurricular(convocacao.getMatrizCurricular());
		// define os status de cada convocação de acordo com o numero de vagas
		// cotistas
		for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
			int vagasCota = cotasComVagasRemanescentes.get(cota);
			int vagasAmpla = ofertaComVagasRemanescentes.get(cota.getOfertaVagasCurso());
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
				if (vagasCota > 0 && vagasAmpla > 0) {
					if (convocacao.getMatrizCurricular().getId() == cota.getOfertaVagasCurso().getMatrizCurricular().getId()
						&& convocacao.getGrupoCotaConvocado() != null && convocacao.getGrupoCotaConvocado().getId() == cota.getGrupoCota().getId()
						&& convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
						convocacao.getDiscente().setStatus(StatusDiscente.CADASTRADO);
						cotasComVagasRemanescentes.put(cota, --vagasCota);
						ofertaComVagasRemanescentes.put(cota.getOfertaVagasCurso(), --vagasAmpla);
					} 
				} else {
					break;
				}
			}
		}
		// diminui as vagas reservadas aos cotistas, para poder fazer o preenchimento de ampla concorrência
		for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
			for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
				if (oferta.getId() == cota.getOfertaVagasCurso().getId()) {
					int vagas = ofertaComVagasRemanescentes.get(oferta);
					int reserva = cotasComVagasRemanescentes.get(cota);
					ofertaComVagasRemanescentes.put(oferta, vagas - reserva);
				}
			}
		}
		// ampla concorrência
		for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
			int vagasAmpla = ofertaComVagasRemanescentes.get(oferta);
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
				if (vagasAmpla > 0) {
					if (convocacao.getMatrizCurricular().getId() == oferta.getMatrizCurricular().getId()
//							&& convocacao.getGrupoCotaConvocado() == null // não cotista
							&& convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
						convocacao.getDiscente().setStatus(StatusDiscente.CADASTRADO);
						ofertaComVagasRemanescentes.put(oferta, --vagasAmpla);
					}
				} else {
					break;
				}
			}
		}
		// soma as vagas reservadas aos cotistas, para poder fazer o preenchimento de ampla concorrência
		for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
			for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
				if (oferta.getId() == cota.getOfertaVagasCurso().getId()) {
					int vagas = ofertaComVagasRemanescentes.get(oferta);
					int reserva = cotasComVagasRemanescentes.get(cota);
					ofertaComVagasRemanescentes.put(oferta, vagas + reserva);
				}
			}
		}
		// caso tenha vaga de cotistas remanescentes
		if (preencherVagasComCotistas) {
			// preencher com vagas de ampla concorrência se definido assim pelo usuário
			for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
				int vagasCota = cotasComVagasRemanescentes.get(cota);
				int vagasAmpla = ofertaComVagasRemanescentes.get(cota.getOfertaVagasCurso());
				for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
					if (vagasCota > 0 && vagasAmpla > 0) {
						if (convocacao.getMatrizCurricular().getId() == cota.getOfertaVagasCurso().getMatrizCurricular().getId()
							&& convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
							convocacao.getDiscente().setStatus(StatusDiscente.CADASTRADO);
							cotasComVagasRemanescentes.put(cota, --vagasCota);
							ofertaComVagasRemanescentes.put(cota.getOfertaVagasCurso(), --vagasAmpla);
						} 
					} else {
						break;
					}
				}
			}
		} else {
			// caso contrário, buscar por cotistas de outros grupos
			for (CotaOfertaVagaCurso cota : cotasComVagasRemanescentes.keySet()) {
				int vagasCota = cotasComVagasRemanescentes.get(cota);
				int vagasAmpla = ofertaComVagasRemanescentes.get(cota.getOfertaVagasCurso());
				for (GrupoCotaVagaCurso grupo : cota.getGrupoCota().getOrdemChamadaVagasRemanescentes()) {
					for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
						if (vagasCota > 0 && vagasAmpla > 0) {
							if (convocacao.getMatrizCurricular().getId() == cota.getOfertaVagasCurso().getMatrizCurricular().getId() 
								&& convocacao.getGrupoCotaConvocado() != null && convocacao.getGrupoCotaConvocado().getId() == grupo.getId()
								&& convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
								convocacao.getDiscente().setStatus(StatusDiscente.CADASTRADO);
								convocacao.setGrupoCotaConvocado(cota.getGrupoCota());
								convocacao.setGrupoCotaRemanejado(true);
								cotasComVagasRemanescentes.put(cota, --vagasCota);
								ofertaComVagasRemanescentes.put(cota.getOfertaVagasCurso(), --vagasAmpla);
							}
						} else {
							break;
						}
					}
				}
			}
		}
		// convocação geral para as vagas remanescentes (ampla cnocorrência, cotista ou não)
		for (OfertaVagasCurso oferta : ofertaComVagasRemanescentes.keySet()) {
			int vagasAmpla = ofertaComVagasRemanescentes.get(oferta);
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
				if (vagasAmpla > 0) {
					if (convocacao.getMatrizCurricular().getId() == oferta.getMatrizCurricular().getId()
							&& convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
						convocacao.getDiscente().setStatus(StatusDiscente.CADASTRADO);
						ofertaComVagasRemanescentes.put(oferta, --vagasAmpla);
					}
				} else {
					break;
				}
			}
		}
		// caso não tenha definido dentro/fora do número de vagas, será excluído, se o usuário assim tiver marcado
		if (!naoExcluir) {
			for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes){
				if (convocacao.getDiscente().getStatus() == StatusDiscente.PRE_CADASTRADO){
					convocacao.getDiscente().setStatus(StatusDiscente.EXCLUIDO);
					CancelamentoConvocacao cancelamento = new CancelamentoConvocacao();
					cancelamento.setMotivo(MotivoCancelamentoConvocacao.EXCEDENTE_NUMERO_VAGAS);
					cancelamento.setConvocacao(convocacao);
					convocacao.setCancelamento(cancelamento);
				}
			}
		}
		return convocacoes;
	}
	
	/** Retorna uma lista de convocações para um processo seletivo com discentes que estão com status PRÉ-CADASTRADO para exclusão.
	 * @param idProcessoSeletivo
	 * @param ofertaComVagasRemanescentes
	 * @param cotasComVagasRemanescentes 
	 * @param naoExcluir 
	 * @param preencherVagasComCotistas 
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ConvocacaoProcessoSeletivoDiscente> buscaEncerramentoCadastramento(int idProcessoSeletivo) throws HibernateException, DAOException {
		String projecao =
				"cpsd.id_convocacao_processo_seletivo_discente as id," +
				"cpsd.dentro_numero_vagas                      as dentroNumeroVagas," +
				"cpsd.ano                                      as ano," +
				"cpsd.periodo                                  as periodo," +
				"gcvc.id_grupo_cota_vaga_curso                 as grupoCotaConvocado.id," +
				"gcvc.descricao                                as grupoCotaConvocado.descricao," +
				"matriz_curricular.id_matriz_curricular        as matrizCurricular.id," +
				"curso.id_curso                                as matrizCurricular.curso.id," +
				"curso.nome                                    as matrizCurricular.curso.nome," +
				"grau_academico.descricao                      as matrizCurricular.grauAcademico.descricao," +
				"turno.sigla                                   as matrizCurricular.turno.sigla," +
				"habilitacao.nome                              as matrizCurricular.habilitacao.nome," +
				"municipio.nome                                as matrizCurricular.curso.municipio.nome," +
				"enfase.nome                                   as matrizCurricular.enfase.nome," +
				"pessoa.id_pessoa                              as discente.discente.pessoa.id," +
				"pessoa.nome                                   as discente.discente.pessoa.nome," +
				"pessoa.email                                  as discente.discente.pessoa.email," +
				"pessoa.cpf_cnpj                               as discente.discente.pessoa.cpf_cnpj," +
				"discente.id_discente                          as discente.discente.id," +
				"discente.id_discente                          as discente.id," +
				"discente.nivel                                as discente.nivel," +
				"discente.matricula                            as discente.discente.matricula," +
				"discente.status                               as discente.discente.status," +
				"discente.ano_ingresso                         as discente.discente.anoIngresso," +
				"discente.periodo_ingresso                     as discente.discente.periodoIngresso," +
				"iv.id_inscricao_vestibular                    as inscricaoVestibular.id," +
				"iv.numero_inscricao                           as inscricaoVestibular.numeroInscricao," +
				"classificacao                                 as resultado.classificacao";
		// POG: o hibernate traz o mesmo valor para as colunas curso.nome, habilitacao.nome, pessoa.nome, etc...
		// dai a necessidade de renomear as colunas
		StringBuilder projecaoPog = new StringBuilder();
		StringTokenizer tokenizer = new StringTokenizer(projecao, ",");
		int k = 1;
		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken();
			String[] split = token.split(" as ");
			projecaoPog.append(split[0]).append(" as ").append("coluna_").append(k++).append(", ");
		}
		projecaoPog.delete(projecaoPog.lastIndexOf(","), projecaoPog.length());
		String sql = "select " + projecaoPog +
				" from graduacao.matriz_curricular " +
				" inner join ensino.turno using (id_turno)" +
				" left join graduacao.habilitacao using (id_habilitacao)" +
				" left join graduacao.enfase using (id_enfase)" +
				" inner join ensino.grau_academico using (id_grau_academico)" +
				" inner join curso on (matriz_curricular.id_curso = curso.id_curso)" +
				" inner join comum.municipio using (id_municipio)" +
				" inner join graduacao.discente_graduacao using (id_matriz_curricular)" +
				" inner join discente on (id_discente = id_discente_graduacao)" +
				" inner join comum.pessoa using (id_pessoa)" +
				" inner join vestibular.convocacao_processo_seletivo_discente cpsd using (id_discente)" +
				" left join ensino.grupo_cota_vaga_curso gcvc using (id_grupo_cota_vaga_curso)" +
				" inner join vestibular.inscricao_vestibular iv using (id_inscricao_vestibular)" +
				" inner join vestibular.resultado_classificacao_candidato using (id_inscricao_vestibular)" +
				" inner join vestibular.resultado_opcao_curso roc using (id_resultado_classificacao_candidato)" +
				" where discente.status = " + StatusDiscente.PRE_CADASTRADO +
				" and roc.id_resultado_opcao_curso = cpsd.id_resultado" +
				" and iv.id_processo_seletivo = :idProcessoSeletivo"+
				" order by matriz_curricular.id_matriz_curricular, classificacao";
		Query q = getSession().createSQLQuery(sql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		// POG para converter BigInt em long (matrícula/CPF)
		for (Object[] obj : lista) {
			for (int i = 0; i < obj.length; i++)
				if (obj[i] instanceof BigInteger) obj[i] = ((BigInteger) obj[i]).longValue();
		}
		Collection<ConvocacaoProcessoSeletivoDiscente> convocacoes = HibernateUtils.parseTo(lista, projecao, ConvocacaoProcessoSeletivoDiscente.class);
		for (ConvocacaoProcessoSeletivoDiscente convocacao : convocacoes) {
			// seta em discente a matriz curricular da convocação (que não vem setado na consulta)
			convocacao.getDiscente().setMatrizCurricular(convocacao.getMatrizCurricular());
			// exclui o discente
			convocacao.getDiscente().setStatus(StatusDiscente.EXCLUIDO);
			// cancela as convocões
			CancelamentoConvocacao cancelamento = new CancelamentoConvocacao();
			cancelamento.setMotivo(MotivoCancelamentoConvocacao.EXCEDENTE_NUMERO_VAGAS);
			cancelamento.setConvocacao(convocacao);
			convocacao.setCancelamento(cancelamento);
		}
		return convocacoes;
	}
	
}
