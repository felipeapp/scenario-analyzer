/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 25/10/2012
 *
 */

package br.ufrn.sigaa.ensino.metropoledigital.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ensino.graduacao.dominio.CancelamentoConvocacao;
import br.ufrn.sigaa.ensino.metropoledigital.dominio.ReservaVagaGrupo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ConvocacaoProcessoSeletivoDiscenteTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.InscricaoProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ProcessoSeletivoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.ResultadoClassificacaoCandidatoTecnico;
import br.ufrn.sigaa.ensino.tecnico.dominio.SituacaoCandidatoTecnico;

/**
 * Dao responsável pelas consultas que são inerentes as convocações 
 * de Processo Seletivo aos discentes. 
 * 
 * @author Rafael Gomes
 * @author Fred_Castro
 */
public class ConvocacaoProcessoSeletivoDiscenteTecnicoIMDDao extends GenericSigaaDAO {

	/**
	 * Retorna a inscricao pelo número da Inscrição do candidato.
	 * @param numeroInscricao
	 * @return
	 * @throws DAOException
	 */
	public InscricaoProcessoSeletivoTecnico findByNumeroInscricao(Integer numeroInscricao) throws DAOException {
		
		Criteria c = getSession().createCriteria(InscricaoProcessoSeletivoTecnico.class);
		c.add(Restrictions.eq("numeroInscricao", numeroInscricao));
		c.setMaxResults(1);		
		return (InscricaoProcessoSeletivoTecnico) c.uniqueResult();
		
	}
	
	/**
	 * Retorna os resultados de classificação do vestibular para os candidatos aprovados para 1ª chamada.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResultadoClassificacaoCandidatoTecnico> findResultadoPSAprovados(ProcessoSeletivoTecnico processoSeletivo, int idOpcao, int quantidadeComReserva, int quantidadeSemReserva, boolean todosAprovados) throws DAOException{
		String projecao = "rcc.id, rcc.inscricaoProcessoSeletivo.id, rcc.inscricaoProcessoSeletivo.pessoa.id, rcc.inscricaoProcessoSeletivo.pessoa.cpf_cnpj, " +
				"rcc.classificacaoAprovado, rcc.inscricaoProcessoSeletivo.numeroInscricao, rcc.inscricaoProcessoSeletivo.pessoa.nome, rcc.inscricaoProcessoSeletivo.pessoa.email, rcc.inscricaoProcessoSeletivo.reservaVagas, rcc.inscricaoProcessoSeletivo.opcao.id, rcc.inscricaoProcessoSeletivo.opcao.polo.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.nome, rcc.inscricaoProcessoSeletivo.opcao.grupo, rcc.inscricaoProcessoSeletivo.opcao.descricao ";
		try {
			StringBuilder hql = new StringBuilder();
 			hql.append ("SELECT " + projecao);
			hql.append (" FROM ResultadoClassificacaoCandidatoTecnico rcc ");
			hql.append (" WHERE rcc.inscricaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo ");
			if (todosAprovados){
				hql.append (" AND rcc.situacaoCandidato.id = :situacaoCandidato ");
			}
			
			hql.append (" AND NOT EXISTS ( select new ConvocacaoProcessoSeletivoDiscenteTecnico(id) from ConvocacaoProcessoSeletivoDiscenteTecnico where inscricaoProcessoSeletivo.id = rcc.inscricaoProcessoSeletivo.id ) ");
			
			if (idOpcao > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.opcao.id = :idOpcao ");
			}
			if (quantidadeComReserva > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.reservaVagas = true");
			}
			if (quantidadeSemReserva > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.reservaVagas = false");
			}
			
			hql.append (" ORDER BY rcc.classificacaoAprovado");
			
			Query q = getSession().createQuery(hql.toString());
			if (todosAprovados){
				q.setInteger("situacaoCandidato", SituacaoCandidatoTecnico.APROVADO.getId());
			}
			
			q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			if (idOpcao > 0){
				q.setInteger("idOpcao", idOpcao);
			}
			
			// Se o gestor de convocação optar por serem todos os aprovados mas definir uma quantidade máxima, traz somente a quantidade informada.
			if (!todosAprovados || quantidadeSemReserva + quantidadeComReserva > 0){
				q.setMaxResults(quantidadeComReserva == 0 ? quantidadeSemReserva : quantidadeComReserva);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<ResultadoClassificacaoCandidatoTecnico> result = HibernateUtils.parseTo(lista, projecao, ResultadoClassificacaoCandidatoTecnico.class, "rcc");
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}
	
	
	/**
	 * Retorna os resultados de classificação do vestibular para os candidatos aprovados para 1ª chamada.
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<ResultadoClassificacaoCandidatoTecnico> findResultadoPSAprovadosTeste(ProcessoSeletivoTecnico processoSeletivo, int idOpcao, int quantidadeASerConvocada, ReservaVagaGrupo grupo, boolean todosAprovados) throws DAOException{
		String projecao = "rcc.id, rcc.inscricaoProcessoSeletivo.id, rcc.inscricaoProcessoSeletivo.pessoa.id, rcc.inscricaoProcessoSeletivo.pessoa.cpf_cnpj, " +
				"rcc.classificacaoAprovado, rcc.inscricaoProcessoSeletivo.numeroInscricao, rcc.inscricaoProcessoSeletivo.pessoa.nome, rcc.inscricaoProcessoSeletivo.pessoa.email, rcc.inscricaoProcessoSeletivo.reservaVagas, rcc.inscricaoProcessoSeletivo.opcao.id, rcc.inscricaoProcessoSeletivo.opcao.polo.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.id, rcc.inscricaoProcessoSeletivo.opcao.polo.cidade.nome, rcc.inscricaoProcessoSeletivo.opcao.grupo, rcc.inscricaoProcessoSeletivo.opcao.descricao ";
		try {
			StringBuilder hql = new StringBuilder();
			hql.append ("SELECT " + projecao);
			hql.append (" FROM ResultadoClassificacaoCandidatoTecnico rcc ");
			hql.append (" WHERE rcc.inscricaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo ");
			if (todosAprovados){
				hql.append (" AND rcc.situacaoCandidato.id = :situacaoCandidato ");
			}
			
			hql.append (" AND NOT EXISTS ( select new ConvocacaoProcessoSeletivoDiscenteTecnico(id) from ConvocacaoProcessoSeletivoDiscenteTecnico where inscricaoProcessoSeletivo.id = rcc.inscricaoProcessoSeletivo.id ) ");
			
			if (idOpcao > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.opcao.id = :idOpcao ");
			}
			/*
			if (quantidadeComReserva > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.reservaVagas = true");
			}
			if (quantidadeSemReserva > 0){
				hql.append (" AND rcc.inscricaoProcessoSeletivo.reservaVagas = false");
			}*/
			
			hql.append (" ORDER BY rcc.classificacaoAprovado");
			
			Query q = getSession().createQuery(hql.toString());
			if (todosAprovados){
				q.setInteger("situacaoCandidato", SituacaoCandidatoTecnico.APROVADO.getId());
			}
			
			q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
			if (idOpcao > 0){
				q.setInteger("idOpcao", idOpcao);
			}
			
			// Se o gestor de convocação optar por serem todos os aprovados mas definir uma quantidade máxima, traz somente a quantidade informada.
			if (!todosAprovados || quantidadeASerConvocada > 0){
				q.setMaxResults(quantidadeASerConvocada);
			}
			
			@SuppressWarnings("unchecked")
			List<Object[]> lista = q.list();
			Collection<ResultadoClassificacaoCandidatoTecnico > result = HibernateUtils.parseTo(lista, projecao, ResultadoClassificacaoCandidatoTecnico.class, "rcc");
			return result;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}
	
	
	/**
	 * Retorna a convocação de processo seletivo mediante a inscrição de vestibular.
	 * @param inscricao
	 * @return
	 * @throws DAOException
	 */
	public ConvocacaoProcessoSeletivoDiscenteTecnico findConvocacaoByInscricaoVestibular(InscricaoProcessoSeletivoTecnico inscricao) throws DAOException{
		
		try {
			Criteria c = getSession().createCriteria(ConvocacaoProcessoSeletivoDiscenteTecnico.class);
			c.add(Restrictions.eq("inscricaoProcessoTecnico.id", inscricao.getId()));
			
			return (ConvocacaoProcessoSeletivoDiscenteTecnico) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}	
	}

	/** Indica se o candidato já foi convocado para o preenchimento de vaga.
	 * @param inscricao
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean isConvocado(InscricaoProcessoSeletivoTecnico inscricao) throws HibernateException, DAOException {
		Query q = getSession().createQuery("select count(id) from ConvocacaoProcessoSeletivoDiscenteTecnico where inscricaoProcessoSeletivo.id = :id");
		q.setParameter("id", inscricao.getId());
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
	public Map<Integer, Boolean> mapaConvocados(Collection<Integer> idsInscricao) throws HibernateException, DAOException {
		Map<Integer, Boolean> mapa = new TreeMap<Integer, Boolean>();
		if (!isEmpty(idsInscricao)) {
			Query q = getSession().createQuery("select inscricaoProcessoSeletivo.id" +
					" from ConvocacaoProcessoSeletivoDiscenteTecnico" +
					" where inscricaoProcessoSeletivo.id in " + UFRNUtils.gerarStringIn(idsInscricao));
			@SuppressWarnings("unchecked")
			List<Integer> idsConvocados = q.list();
			for (Integer idInscricao :idsInscricao) {
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
	public Collection<ConvocacaoProcessoSeletivoDiscenteTecnico> dadosContatoConvocados(int idProcessoSeletivo, int idConvocacaoProcessoSeletivo) throws HibernateException, DAOException {
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
				"cpsd.convocacaoProcessoSeletivo.descricao");
		StringBuilder hql = new StringBuilder("SELECT ").append(projecao)
		.append(" FROM ConvocacaoProcessoSeletivoDiscenteTecnico cpsd" +
				" INNER JOIN cpsd.convocacaoProcessoSeletivo cps" +
				" WHERE cps.processoSeletivo.id = :idProcessoSeletivo" +
				" AND cps.id = :idConvocacaoProcessoSeletivo" +
				" ORDER BY cpsd.discente.discente.curso.municipio.nome, cpsd.discente.discente.curso.nome, cpsd.discente.discente.pessoa.nome");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		q.setInteger("idConvocacaoProcessoSeletivo", idConvocacaoProcessoSeletivo);
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao.toString(), ConvocacaoProcessoSeletivoDiscenteTecnico.class, "cpsd");
	}

	/** Retorna uma coleção de {@link ResultadoOpcaoCurso} de um processo seletivo para uma determinada matriz curricular.
	 * @param processoSeletivo
	 * @param matriz
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ResultadoClassificacaoCandidatoTecnico> findResultadoOpcaoCurso(ProcessoSeletivoTecnico processoSeletivo) throws HibernateException, DAOException {
		String projecao = "roc.id, roc.argumentoFinal, roc.argumentoFinalSemBeneficio, roc.classificacao, roc.matrizCurricular.id," +
				" roc.ordemOpcao, roc.resultadoClassificacaoCandidato.id";
		StringBuilder hql = new StringBuilder("SELECT ").append(projecao)
				.append(" FROM ResultadoClassificacaoCandidatoTecnico roc" +
						" WHERE roc.inscricaoProcessoSeletivo.processoSeletivo.id = :idProcessoSeletivo");
						//" AND roc.opcaoAprovacao = :idMatriz");
		hql.append(" ORDER BY roc.id");
		Query q = getSession().createQuery(hql.toString());
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		@SuppressWarnings("unchecked")
		List<Object[]> lista = q.list();
		return HibernateUtils.parseTo(lista, projecao.toString(), ResultadoClassificacaoCandidatoTecnico.class, "roc");
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
	
}
