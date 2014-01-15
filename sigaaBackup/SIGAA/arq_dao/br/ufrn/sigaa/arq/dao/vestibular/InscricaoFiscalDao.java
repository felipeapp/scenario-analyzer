/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criada em: 07/10/2008
 *
 */
package br.ufrn.sigaa.arq.dao.vestibular;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.dominio.SituacaoBolsaAuxilio;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.ensino.dominio.IndiceAcademicoDiscente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.stricto.dominio.DiscenteStricto;
import br.ufrn.sigaa.parametros.dominio.ParametrosVestibular;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.vestibular.dominio.Fiscal;
import br.ufrn.sigaa.vestibular.dominio.InscricaoFiscal;
import br.ufrn.sigaa.vestibular.dominio.ProcessoSeletivoVestibular;
import br.ufrn.sigaa.vestibular.dominio.QuantidadeFiscalPorMunicipio;

/**
 * Dao responsável por realizar consultas as inscrições de Fiscais.
 * 
 * @author Édipo Elder
 * 
 */
public class InscricaoFiscalDao extends GenericSigaaDAO {

	/** Limita o tamanho máximo da consulta */
	private static final int TAMANHO_MAXIMO_CONSULTA_IN = 512;
	
	/**
	 * Construtor padrão
	 * 
	 */
	public InscricaoFiscalDao() {
	}

	/**
	 * Recupera uma lista de InscricaoFiscal associadas a Pessoa indicada no
	 * parâmetro
	 * 
	 * @param idPessoa
	 *            Pessoa associada a inscrição
	 * @return Inscrições associada à Pessoa
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findByPessoa(int idPessoa)
			throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.add(Restrictions.eq("pessoa.id", idPessoa));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Recupera um objeto InscricaoFiscal associado à uma Pessoa e um Processo
	 * Seletivo
	 * 
	 * @param idPessoa
	 *            Pessoa associada à inscrição
	 * @param idProcessoSeletivoVestibular
	 *            Processo Seletivo em que a pessoa está inscrita
	 * @return Inscrição associada
	 * @throws DAOException
	 */
	public InscricaoFiscal findByPessoaProcessoSeletivo(int idPessoa,
			int idProcessoSeletivoVestibular) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.addOrder(Order.desc("numeroInscricao")); // pega a última inscrição
		c.add(Restrictions.eq("pessoa.id", idPessoa));
		if (idProcessoSeletivoVestibular > 0)
			c.add(Restrictions.eq("processoSeletivoVestibular.id", idProcessoSeletivoVestibular));
		c.setMaxResults(1);
		return (InscricaoFiscal) c.uniqueResult();
	}

	/**
	 * Criado para atender a busca na seleção manual de fiscais, recupera uma
	 * lista de inscritos que não foram selecionados como fiscal em determinado
	 * Processo Seletivo, buscando por nome da pessoa.
	 * 
	 * @param nome
	 *            Nome para realizar a busca
	 * @param idProcessoSeletivoVestibular
	 *            Processo Seletivo em que a pessoa estaria inscrito
	 * @return Lista de IncricaoFiscal ordenada alfabeticamente por nome
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findInscritosNaoFiscalByPessoaProcessoSeletivo(
			String nome, int idProcessoSeletivoVestibular) throws DAOException {
		nome = nome.replace('%', ' ').trim();
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.createCriteria("discente").createCriteria("pessoa").add(Restrictions.ilike("nomeAscii", nome + "%"));
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivoVestibular));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> discentes = c.list();
		c = getSession().createCriteria(InscricaoFiscal.class);
		c.createCriteria("servidor").createCriteria("pessoa").add(Restrictions.ilike("nomeAscii", nome + "%"));
		c.createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivoVestibular));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> servidores = c.list();
		c = getSession().createCriteria(Fiscal.class);
		c.createCriteria("inscricaoFiscal").createCriteria("processoSeletivoVestibular").add(Restrictions.eq("id", idProcessoSeletivoVestibular));
		@SuppressWarnings("unchecked")
		Collection<Fiscal> fiscais = c.list();
		List<InscricaoFiscal> lista = new LinkedList<InscricaoFiscal>();
		if (!isEmpty(discentes)) lista.addAll(discentes);
		if (!isEmpty(servidores)) lista.addAll(servidores);
		if (!isEmpty(fiscais)){
			for (Fiscal fiscal : fiscais) {
				lista.remove(fiscal.getInscricaoFiscal());
			}
		}
		Collections.sort(lista, InscricaoFiscal.nomeComparator);
		return lista;
	}

	/**
	 * Busca uma InscricaoFiscal associada a um Discente em um Processo Seletivo
	 * 
	 * @param idDiscente
	 *            Discente associado ao Processo Seletivo
	 * @param idProcessoSeletivoVestibular
	 *            Processo Seletivo em que o discente estaria inscrito
	 * @return Inscrição Fiscal associada ao Discente
	 * @throws DAOException
	 */
	public InscricaoFiscal findByDiscenteProcessoSeletivo(int idDiscente,
			int idProcessoSeletivoVestibular) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.addOrder(Order.asc("numeroInscricao"));
		c.add(Restrictions.eq("discente.id", idDiscente));
		c.add(Restrictions.eq("processoSeletivoVestibular.id",
				idProcessoSeletivoVestibular));
		c.setMaxResults(1);
		return (InscricaoFiscal) c.uniqueResult();
	}

	/**
	 * Busca todos inscritos em um Processo Seletivo
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @return Lista de inscritos no Processo Seletivo especificado
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findByProcessoSeletivo(
			int idProcessoSeletivo) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.add(Expression
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Recupera uma lista de InscricaoFiscal somente de servidores inscritos em
	 * um Processo Seletivo e ligados à uma determinada Unidade.
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado.
	 * @param idUnidade
	 *            Unidade associada. Caso o idUnidade seja igual a zero, a
	 *            busca não restringe a unidade
	 * @return Lista de inscritos no Processo Seletivo que estão ligados à
	 *         Unidade especificada.
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findServidorByProcessoSeletivoUnidade(
			int idProcessoSeletivo, int idUnidade) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.add(Expression
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		if (idUnidade != 0) {
			c.createCriteria("servidor").add(
					Restrictions.eq("unidade.id", idUnidade));
		} else {
			c.add(Restrictions.isNotNull("servidor"));
		}
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Recupera uma lista de IncricaoFiscal de Discentes inscritos em um
	 * Processo Seletivo de um determinado Curso.
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado.
	 * @param idCurso
	 *            Curso associado. Caso seja passado um valor igual a zero, a
	 *            busca não restringirá o curso.
	 * @return Lista de InscricaoFiscal associada ao Processo Seletivo e ao
	 *         Curso.
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findDiscenteByProcessoSeletivoCurso(
			int idProcessoSeletivo, int idCurso) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.add(Expression
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		if (idCurso != 0) {
			c.createCriteria("discente")
					.add(Restrictions.eq("curso.id", idCurso));
		} else {
			c.add(Restrictions.isNotNull("discente"));
		}
		c.createCriteria("pessoa").addOrder(Order.asc("nome"));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Recupera uma lista contendo InscricaoFiscal de Servidores que não foram
	 * selecionados como fiscais no Processo Seletivo especificado
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @param idMunicipio
	 *            Município de lotação do servidor
	 * @return Lista de InscricaoFiscal associadas a Servidor que estejam
	 *         lotados no município indicado.
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findServidoresByProcessoSeletivoMunicipio(
			int idProcessoSeletivo, int idMunicipio) throws DAOException {
		List<Integer> ids = findIdDiscenteFiscais(idProcessoSeletivo);
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		if (ids.size() > 0)
			c.add(Restrictions.not(Restrictions.in("id", ids)));
		c.createCriteria("servidor").createCriteria("unidade").createCriteria(
				"municipio").add(Restrictions.eq("id", idMunicipio));
		c.createCriteria("processoSeletivoVestibular").add(
				Restrictions.eq("id", idProcessoSeletivo));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Recupera um Map<Curso, List<<InscricaoFiscal>> contendo todos discentes
	 * concorrendo para a seleção fiscais em um ProcessoSeletivo que tenham
	 * curso associado ao Município especificado
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @param idMunicipio
	 *            Município do Curso do Discente
	 * @return Mapa contendo InscricaoFiscal por Curso do
	 *         ProcessoSeletivo/Município especificado
	 * @throws DAOException
	 */
	public Map<Curso, List<InscricaoFiscal>> findAllConcorrendoByProcessoSeletivoMunicipio(
			int idProcessoSeletivo, int idMunicipio) throws DAOException {
		// fiscais já selecionados
		List<Integer> ids = findIdDiscenteFiscais(idProcessoSeletivo);
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		// exceto os fiscais selecionados
		if (ids.size() > 0)
			c.add(Restrictions.not(Restrictions.in("id", ids)));
		c.add(Expression
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("discente").addOrder(Order.desc("ira"))
				.createCriteria("curso").add(
						Restrictions.eq("municipio.id", idMunicipio));
		c.add(Restrictions.eq("recadastro", false));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> inscricoes = c.list();
		Map<Curso, List<InscricaoFiscal>> mapa = new HashMap<Curso, List<InscricaoFiscal>>();
		for (InscricaoFiscal inscricaoFiscal : inscricoes) {
			Curso curso = inscricaoFiscal.getDiscente().getCurso();
			if (mapa.get(curso) != null) {
				mapa.get(curso).add(inscricaoFiscal);
			} else {
				List<InscricaoFiscal> lista = new ArrayList<InscricaoFiscal>();
				lista.add(inscricaoFiscal);
				mapa.put(curso, lista);
			}
		}
		return mapa;
	}

	/**
	 * Recupera um Map<Curso, List<<InscricaoFiscal>> contendo todos discentes
	 * concorrendo para a seleção fiscais em um ProcessoSeletivo que tenham
	 * curso associado ao Município especificado, excetuando-se as inscrições de
	 * uma lista de Fiscais já selecionados
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @param idMunicipio
	 *            Município do Curso do Discente
	 * @param excetoFiscal
	 *            Lista de Fiscais já selecionados durante o processamento
	 * @return Mapa contendo InscricaoFiscal por Curso do
	 *         ProcessoSeletivo/Município especificado
	 * @throws DAOException
	 */
	public Map<String, List<InscricaoFiscal>> findAllConcorrendoByProcessoSeletivoMunicipio(
			int idProcessoSeletivo, int idMunicipio,
			ArrayList<Fiscal> excetoFiscal) throws DAOException {
		int indiceSelecao = ParametroHelper.getInstance().getParametroInt(ParametrosVestibular.INDICE_ACADEMICO_SELECAO_FISCAL_GRADUACAO);
		// fiscais já selecionados em processamento anterior
		List<Integer> ids = findIdDiscenteFiscais(idProcessoSeletivo);
		// exceto os fiscais selecionados no processamento atual
		if (excetoFiscal != null) {
			for (Fiscal fiscal : excetoFiscal) {
				ids.add(fiscal.getInscricaoFiscal().getId());
			}
		}
		// se tem fiscais para excetuar, adiciona à Criteria
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		if (ids.size() > 0)
			c.add(Restrictions.not(Restrictions.in("id", ids)));
		c.add(Expression
				.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		c.createCriteria("discente").createCriteria("curso").add(Restrictions.eq("municipio.id", idMunicipio));
		c.add(Restrictions.eq("recadastro", false));
		@SuppressWarnings("unchecked")
		List<InscricaoFiscal> inscricoes = c.list();
		Map<String, List<InscricaoFiscal>> mapa = new HashMap<String, List<InscricaoFiscal>>();
		if (inscricoes != null) {
			// recupera os índices acadêmicos
			carregaIndicesAcademicos(inscricoes, indiceSelecao);
			// pré-busca os discentes de graduação / pós, a fim de evitar várias consultas únicas
			preBuscaDiscentesGraduacaoStricto(inscricoes);
			// separa os inscritos por matriz curricular
			for (InscricaoFiscal inscricaoFiscal : inscricoes) {
				Discente discente = inscricaoFiscal.getDiscente().getDiscente();
				// os alunos serão agrupados por curso / turno / habilitação
				StringBuilder chave = new StringBuilder(discente.getCurso().getNome());
				if (discente.getCurso().getModalidadeEducacao() != null)
					chave.append(" - " + discente.getCurso().getModalidadeEducacao().getDescricao());
				if (discente.getCurriculo() != null && discente.getCurriculo().getMatriz() != null) {
					if (discente.getCurriculo().getMatriz().getTurno() != null)
						chave.append(" (" + discente.getCurriculo().getMatriz().getTurno().getSigla()+")");
					if (discente.getCurriculo().getMatriz().getHabilitacao() != null)
						chave.append(" - " + discente.getCurriculo().getMatriz().getHabilitacao().getNome());
				}
				if (mapa.get(chave.toString()) != null) {
					mapa.get(chave.toString()).add(inscricaoFiscal);
				} else {
					List<InscricaoFiscal> lista = new LinkedList<InscricaoFiscal>();
					lista.add(inscricaoFiscal);
					mapa.put(chave.toString(), lista);
				}
			}
		}
		return mapa;
	}

	/**
	 * Responsável pela busca dos discentes de graduação de Stricto Sensu.
	 * 
	 * @param inscricoes
	 * @throws DAOException
	 */
	private void preBuscaDiscentesGraduacaoStricto(Collection<InscricaoFiscal> inscricoes) throws DAOException{
		List<Integer> listaIDDiscente;
		// discentes de graduação
		listaIDDiscente = new ArrayList<Integer>();
		for (InscricaoFiscal inscricao : inscricoes) {
			if (inscricao.getDiscente().isGraduacao()) {
				listaIDDiscente.add(inscricao.getDiscente().getId());
			}
		}
		// consulta por lote
		while (!listaIDDiscente.isEmpty()) {
			int k = 0;
			Collection<Integer> subLista = new ArrayList<Integer>();
			for (java.util.Iterator<Integer> iterator = listaIDDiscente.iterator(); iterator.hasNext() && k < TAMANHO_MAXIMO_CONSULTA_IN; k++ ) {
				subLista.add(iterator.next());
				iterator.remove();
			}
			Criteria c = getSession().createCriteria(DiscenteGraduacao.class).add(Restrictions.in("id", subLista));
			@SuppressWarnings("unchecked")
			List<DiscenteGraduacao> lista = c.list();
			if (lista != null) {
				for (InscricaoFiscal inscricao : inscricoes) {
					for (DiscenteGraduacao discenteGraduacao : lista) {
						if (inscricao.getDiscente().getId() == discenteGraduacao.getId()) {
							inscricao.setDiscente(discenteGraduacao);
						}
					}
				}
			}
		}
		// discentes stricto sensu
		listaIDDiscente = new ArrayList<Integer>();
		for (InscricaoFiscal inscricao : inscricoes) {
			if (inscricao.getDiscente().isStricto()) {
				listaIDDiscente.add(inscricao.getDiscente().getId());
			}
		}
		// consulta por lote
		while (!listaIDDiscente.isEmpty()) {
			int k = 0;
			Collection<Integer> subLista = new ArrayList<Integer>();
			for (java.util.Iterator<Integer> iterator = listaIDDiscente.iterator(); iterator.hasNext() && k < TAMANHO_MAXIMO_CONSULTA_IN; k++ ) {
				subLista.add(iterator.next());
				iterator.remove();
			}
			Criteria c = getSession().createCriteria(DiscenteStricto.class).add(Restrictions.in("id", subLista));
			@SuppressWarnings("unchecked")
			List<DiscenteStricto> lista = c.list();
			if (lista != null) {
				for (InscricaoFiscal inscricao : inscricoes) {
					for (DiscenteStricto discenteStricto : lista) {
						if (inscricao.getDiscente().getId() == discenteStricto.getId()) {
							inscricao.setDiscente(discenteStricto);
						}
					}
				}
			}
		}
	}
	
	/**
	 * Carrega os indices acadêmicos baseado nas inscrições e do índice de seleção
	 * 
	 * @param inscricoes
	 * @param indiceSelecao
	 * @throws HibernateException
	 * @throws DAOException
	 */
	private void carregaIndicesAcademicos(Collection<InscricaoFiscal> inscricoes, int indiceSelecao) throws HibernateException, DAOException{
		List<Integer> listaIDDiscente = new ArrayList<Integer>();
		for (InscricaoFiscal inscricao : inscricoes)
			listaIDDiscente.add(inscricao.getDiscente().getId());
		String hql = "select indiceDiscente from IndiceAcademicoDiscente indiceDiscente where discente.id in (:listaIDDiscente) and indiceDiscente.indice.id = :indiceSelecao";
		Query q = getSession().createQuery(hql).setInteger("indiceSelecao", indiceSelecao);
		while (!listaIDDiscente.isEmpty()) {
			int k = 0;
			Collection<Integer> subLista = new ArrayList<Integer>();
			for (java.util.Iterator<Integer> iterator = listaIDDiscente.iterator(); iterator.hasNext() && k < TAMANHO_MAXIMO_CONSULTA_IN; k++ ) {
				subLista.add(iterator.next());
				iterator.remove();
			}
			if (subLista.size() > 0) {
				q.setParameterList("listaIDDiscente", subLista);
				@SuppressWarnings("unchecked")
				Collection<IndiceAcademicoDiscente> indices = q.list();
				// seta as os índices dos discentes
				if (indices != null) {
					for (IndiceAcademicoDiscente indice : indices)
						for (InscricaoFiscal inscricao : inscricoes)
							if (inscricao.getDiscente().getId() == indice.getDiscente().getId()) {
								inscricao.getDiscente().getDiscente().setIndices(new ArrayList<IndiceAcademicoDiscente>());
								inscricao.getDiscente().getDiscente().getIndices().add(indice);
							}
				}
			}
		}
	}

	/**
	 * Recupera uma lista contendo RECADASTROS de Discentes inscritos para
	 * seleção de fiscais de um determinado Município e que não foram
	 * selecionados como fiscais.
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @param idMunicipio
	 *            Município do curso do Discente
	 * @return Lista de RECADASTRO de InscricaoFiscal de Discentes com o Curso
	 *         no Município especificado
	 * @throws DAOException
	 */
	public Map<String, List<InscricaoFiscal>> findAllInscricoesFiscaisRecadastro(
			int idProcessoSeletivo, int idMunicipio) throws DAOException {
		List<Integer> ids = findIdDiscenteFiscais(idProcessoSeletivo);
		Criteria c = getSession().createCriteria(InscricaoFiscal.class).add(
				Restrictions.eq("processoSeletivoVestibular.id", idProcessoSeletivo));
		if (idMunicipio != 0) {
			c.createCriteria("discente").createCriteria("curso")
					.createCriteria("municipio").add(
							Restrictions.eq("id", idMunicipio));
		}
		c.add(Restrictions.eq("recadastro", true));
		if (ids.size() > 0)
			c.add(Restrictions.not(Restrictions.in("id", ids)));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> inscricoes = c.list();
		Map<String, List<InscricaoFiscal>> mapa = new HashMap<String, List<InscricaoFiscal>>();
		for (InscricaoFiscal inscricaoFiscal : inscricoes) {
			Discente discente = inscricaoFiscal.getDiscente().getDiscente();
			StringBuilder chave = new StringBuilder(discente.getCurso().getNome());
			if (discente.getCurso().getModalidadeEducacao() != null)
				chave.append(" - " + discente.getCurso().getModalidadeEducacao().getDescricao());
			if (discente.getCurriculo() != null && discente.getCurriculo().getMatriz() != null) {
				if (discente.getCurriculo().getMatriz().getTurno() != null)
					chave.append(" (" + discente.getCurriculo().getMatriz().getTurno().getSigla()+")");
				if (discente.getCurriculo().getMatriz().getHabilitacao() != null)
					chave.append(" - " + discente.getCurriculo().getMatriz().getHabilitacao().getNome());
			}
			if (mapa.get(chave.toString()) != null) {
				mapa.get(chave.toString()).add(inscricaoFiscal);
			} else {
				List<InscricaoFiscal> lista = new ArrayList<InscricaoFiscal>();
				lista.add(inscricaoFiscal);
				mapa.put(chave.toString(), lista);
			}
		}
		return mapa;
	}

	/**
	 * Retorna uma lista de IDs de inscrições de fiscais inscritos no processo
	 * seletivo especificado. Esse método é utilizado como subconsulta em outros
	 * métodos deste DAO.
	 * 
	 * @param idProcessoSeletivo restringe a busca ao ID do processo seletivo
	 * @return Lista de IDs de inscrição de fiscais
	 * @throws DAOException
	 */
	private List<Integer> findIdDiscenteFiscais(int idProcessoSeletivo)
			throws DAOException {
		String sql = "select id_inscricao_fiscal" + " from vestibular.fiscal"
				+ " where id_processo_seletivo = :idProcessoSeletivo"
				+ " and id_discente is not null";
		Query query = getSession().createSQLQuery(sql).addScalar(
				"id_inscricao_fiscal", Hibernate.INTEGER);
		query.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		@SuppressWarnings("unchecked")
		List<Integer> ids = query.list();
		return ids;
	}

	/**
	 * Conta quantos discente estão inscritos para fiscal que são cadastro e
	 * estariam "concorrendo" à seleção de fiscais
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo associado
	 * @param idMunicipio
	 *            Município do Curso do Discente
	 * @return
	 */
	public int findTotalDiscenteConcorrendo(int idProcessoSeletivo,
			int idMunicipio) {
		String sql;
		Object[] params = null;
		if (idMunicipio != 0) {
			sql = "select count (*)" + " from vestibular.inscricao_fiscal"
					+ " inner join discente using (id_discente)"
					+ " inner join curso using (id_curso)"
					+ " inner join comum.municipio using (id_municipio)"
					+ " where id_processo_seletivo = ?"
					+ " and id_municipio = ?" + " and recadastro = falseValue()"
					+ " and id_servidor is null";
			params = new Object[] { idProcessoSeletivo, idMunicipio };
		} else {
			sql = "select count (*)" + " from vestibular.inscricao_fiscal"
					+ " where id_processo_seletivo_vestibular = ?"
					+ " and recadastro = falseValue()"
					// não é recadastro
					+ " and id_servidor is null"; // não é servidor
			params = new Object[] { idProcessoSeletivo };
		}
		return getJdbcTemplate().queryForInt(sql, params);
	}

	/**
	 * Recupera uma lista de QuantidadeFiscalPorMunicipio de um determinado
	 * ProcessoSeletivo
	 * 
	 * @param idProcessoSeletivoVestibular
	 *            ProcessoSeletivo que se deseja contar o número de fiscais
	 *            inscritos
	 * @return Número de fiscais inscritos por município
	 * @throws DAOException
	 */
	public Collection<QuantidadeFiscalPorMunicipio> findQuantidadeInscritoPorMunicipio(
			int idProcessoSeletivoVestibular) throws DAOException {
		String sql = "select"
				+ " municipio.id_municipio as id,"
				+ " municipio.nome as nome,"
				+ " count(inscricao_fiscal.id_inscricao_fiscal) as num_inscritos,"
				+ " sum(case when not fiscal.reserva then 1 else 0 end) as num_titular,"
				+ " round(100*sum(case when fiscal.reserva then 1 else 0 end)/(sum(case when not fiscal.reserva then 1 else 0 end)+0.001), 0) as perc_reserva"
				+ " from vestibular.inscricao_fiscal"
				+ " left join vestibular.fiscal using (id_inscricao_fiscal)"
				+ " left join discente on (inscricao_fiscal.id_discente = discente.id_discente)"
				+ " left join rh.servidor on (inscricao_fiscal.id_servidor = servidor.id_servidor)"
				+ " left join curso on (curso.id_curso = discente.id_curso)"
				+ " inner join comum.unidade on (servidor.id_unidade = unidade.id_unidade or curso.id_unidade = unidade.id_unidade)"
				+ " inner join comum.municipio on (municipio.id_municipio = unidade.id_municipio and inscricao_fiscal.id_servidor is not null or municipio.id_municipio = curso.id_municipio and inscricao_fiscal.id_discente is not null)"
				+ " where inscricao_fiscal.id_processo_seletivo = :idProcessoSeletivo"
				+ " group by municipio.id_municipio, municipio.nome"
				+ " order by municipio.nome";
		Query query = getSession().createSQLQuery(sql).addScalar("id",
				Hibernate.INTEGER).addScalar("nome", Hibernate.STRING)
				.addScalar("num_inscritos", Hibernate.INTEGER).addScalar(
						"num_titular", Hibernate.INTEGER).addScalar(
						"perc_reserva", Hibernate.INTEGER);
		query.setInteger("idProcessoSeletivo", idProcessoSeletivoVestibular);
		Collection<QuantidadeFiscalPorMunicipio> result = new ArrayList<QuantidadeFiscalPorMunicipio>();
		@SuppressWarnings("unchecked")
		List<Object[]> list = query.list();
		for (Object obj[] : list) {
			QuantidadeFiscalPorMunicipio quant = new QuantidadeFiscalPorMunicipio();
			quant.setId((Integer) obj[0]);
			quant.setNome((String) obj[1]);
			quant.setNumInscritos((Integer) obj[2]);
			quant.setNumFiscais((Integer) obj[3]);
			quant.setPercentualReserva((Integer) obj[4]);
			result.add(quant);
		}
		return result;
	}

	/**
	 * Retorna uma coleção de Curso onde tem Discente inscrito para a seleção de
	 * fiscal
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo para o que contém as inscrições
	 * @param idMunicipio
	 *            Município ao qual a busca se restringe
	 * @return Lista contendo os Curso distintos de discentes inscritos para
	 *         seleção de fiscal
	 * @throws DAOException
	 */
	public Collection<Curso> findAllCursosInscritos(int idProcessoSeletivo,
			int idMunicipio) throws DAOException {
		Query query = getSession()
				.createSQLQuery(
						"select distinct curso.id_curso as id,"
								+ " curso.nome as nome"
								+ " from curso"
								+ " inner join discente using (id_curso)"
								+ " inner join vestibular.inscricao_fiscal using (id_discente)"
								+ " inner join vestibular.processo_seletivo using (id_processo_seletivo)"
								+ " where vestibular.processo_seletivo.id_processo_seletivo = :idProcessoSeletivo"
								+ " and curso.id_municipio = :idMunicipio"
								+ " order by curso.nome").setResultTransformer(
						Transformers.aliasToBean(Curso.class));
		query.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		query.setInteger("idMunicipio", idMunicipio);
		@SuppressWarnings("unchecked")
		Collection<Curso> lista = query.list();
		return lista;
	}

	/**
	 * Busca por todos discentes inscritos em um determinado Curso e Município
	 * 
	 * @param idProcessoSeletivo
	 *            Processo Seletivo que contém as inscrições
	 * @param idCurso
	 *            Curso ao qual a busca se restringe
	 * @param idMunicipio
	 *            Município ao qual a busca se restringe
	 * @return Lista de InscricaoFiscal dos discentes inscritos no Curso e
	 *         Município especificados
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findAllInscritosPorCursoMunicipio(
			int idProcessoSeletivo, int idCurso, int idMunicipio)
			throws DAOException {
		String sql = "SELECT inscricaoFiscal"
				+ " from InscricaoFiscal inscricaoFiscal"
				+ " inner join inscricaoFiscal.pessoa pessoa"
				+ " inner join inscricaoFiscal.processoSeletivo processoSeletivo"
				+ " inner join inscricaoFiscal.discente discente"
				+ " inner join discente.curso curso"
				+ " inner join curso.municipio municipio"
				+ " where processoSeletivo.id = :idProcessoSeletivo"
				+ " and discente.curso.id = :idCurso"
				+ " and discente.curso.municipio.id = :idMunicipio"
				+ " order by discente.ira desc";
		Query q = getSession().createQuery(sql);
		q.setInteger("idProcessoSeletivo", idProcessoSeletivo);
		q.setInteger("idCurso", idCurso);
		q.setInteger("idMunicipio", idMunicipio);
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = q.list();
		return lista;
	}

	/**
	 * Usando na inscrição para fiscal, retorna uma lista de processos seletivos
	 * em que a pessoa ainda não se inscreveu para fiscal.
	 * 
	 * @param idPessoa
	 *            Pessoa para o qual a busca se restringe
	 * @return Lista de Processos Seletivo que a pessoa especificada ainda não
	 *         se inscreveu.
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<ProcessoSeletivoVestibular> findProcessosSeletivosNaoInscritos(
			int idPessoa) throws HibernateException, DAOException {
		String hql = "select processoSeletivo"
				+ " from ProcessoSeletivoVestibular processoSeletivo"
				+ " where processoSeletivo not in ("
				+ " select inscricaoFiscal.processoSeletivoVestibular"
				+ " from InscricaoFiscal inscricaoFiscal"
				+ " where inscricaoFiscal.pessoa.id = :idPessoa)"
				+ " and processoSeletivo.inicioInscricaoFiscal <= :today"
				+ " and processoSeletivo.fimInscricaoFiscal >= :today"
				+ " order by processoSeletivo.nome";
		Query q = getSession().createQuery(hql);
		q.setInteger("idPessoa", idPessoa);
		q.setDate("today", new Date());
		@SuppressWarnings("unchecked")
		Collection<ProcessoSeletivoVestibular> lista = q.list();
		return lista;
	}

	/**
	 * Retorna uma coleção de discentes inscritos para fiscais que possuem bolsa
	 * deferida, ou deferida e em fila de espera, auxílio do tipo, ano e período
	 * especificados.
	 * 
	 * @param idTipoBolsaAuxilio
	 * @param ano
	 * @param periodo
	 * @param idProcessoSeletivo
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findDiscentesByTipoBolsaAuxilio(
			int idTipoBolsaAuxilio, int idProcessoSeletivo, int idMunicipio)
			throws DAOException {
		// Dividindo a consulta
		// IDs dos discentes que possuem bolsa.
		String hql = "select bolsaAuxilio.discente.id"
				+ " from BolsaAuxilio bolsaAuxilio" 
				+ " where bolsaAuxilio.tipoBolsaAuxilio.id = :idTipoBolsaAuxilio"
				+ " and bolsaAuxilio.situacaoBolsa in ("
				+ SituacaoBolsaAuxilio.BOLSA_DEFERIDA_E_CONTEMPLADA + ")";
		Query q = getSession().createQuery(hql);
		q.setInteger("idTipoBolsaAuxilio", idTipoBolsaAuxilio);
		@SuppressWarnings("unchecked")
		List<Integer> idS = q.list();
		// Se não há residentes, retorne
		if (idS == null || idS.size() == 0) {
			return new ArrayList<InscricaoFiscal>();
		}
		// Discentes que estão inscritos e estão no conjunto de IDs de discentes com bolsa
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		c.add(Restrictions.eq("recadastro", false))
			.createCriteria("processoSeletivoVestibular")
			.add(Restrictions.eq("id", idProcessoSeletivo));
		c.createCriteria("discente").add(Restrictions.in("id", idS))
				.createCriteria("curso").createCriteria("municipio").add(
						Restrictions.eq("id", idMunicipio));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

	/**
	 * Retorna uma coleção de inscrições para seleção de fiscais que possuem
	 * perfil dúbio, isto é, não se consegue determinar se o fiscal é discente
	 * ou servidor.
	 * 
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public Collection<InscricaoFiscal> findInscricoesComPerfilDubio() throws HibernateException, DAOException {
		String hql = "select inscricaoFiscal" +
				" from InscricaoFiscal inscricaoFiscal" +
				" where (inscricaoFiscal.discente is not null and inscricaoFiscal.servidor is not null)" +
				" or (inscricaoFiscal.discente is null and inscricaoFiscal.servidor is null)";
		Query q = getSession().createQuery(hql);
		@SuppressWarnings("unchecked")
		List<InscricaoFiscal> lista = q.list();
		return lista;
	}
	
	/**
	 * Retorna todas as Inscrições que cujos parâmetros passados.
	 * 
	 * @param nome
	 * @param cpf
	 * @param idStatusFoto
	 * @return
	 * @throws DAOException
	 */
	public Collection<InscricaoFiscal> findByNomeCpf(String nome, Long cpf, int idStatusFoto) throws DAOException {
		Criteria c = getSession().createCriteria(InscricaoFiscal.class);
		if (idStatusFoto > 0)
			c.createCriteria("statusFoto").add(Restrictions.eq("id", idStatusFoto));
		Criteria cPessoa = c.createCriteria("pessoa");
		if (!ValidatorUtil.isEmpty(nome))
			cPessoa.add(Restrictions.ilike("nome", nome  +"%"));
		if (!ValidatorUtil.isEmpty(cpf))
			cPessoa.add(Restrictions.eq("cpf_cnpj", cpf));
		cPessoa.addOrder(Order.asc("nomeAscii"));
		@SuppressWarnings("unchecked")
		Collection<InscricaoFiscal> lista = c.list();
		return lista;
	}

}
