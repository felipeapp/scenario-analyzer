/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '10/11/2009'
 *
 */
package br.ufrn.sigaa.arq.dao.extensao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.transform.AliasToBeanResultTransformer;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusInscricaoParticipante;
import br.ufrn.sigaa.extensao.dominio.StatusPagamentoInscricao;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * DAO responsável pelas consultas às inscrições para participantes nas ações (cursos e eventos) de extensão.
 * 
 * @author Daniel Augusto 
 */
public class InscricaoAtividadeParticipanteDao extends GenericSigaaDAO {
	
	
	/**
	 * <p>Retorna as inscrições ativias do participante na atividade passada.</p>
	 * 
	 * <p>O usuário não pode ter mais de 1 inscrição ativa para a mesma atividade, essa consulta é usada para verificar isso.</p>
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	public List<InscricaoAtividadeParticipante> findInscricoesAtivasParticipanteAtividade(int idAtividade, int idCadastroParticipante) throws DAOException {
		
		String projecao = " inscricaoParticipante.id, inscricaoParticipante.statusInscricao, participanteExtensao.id ";
		
		String hql = " SELECT " +projecao+
				 " FROM InscricaoAtividadeParticipante inscricaoParticipante "
				+" LEFT JOIN inscricaoParticipante.participanteExtensao participanteExtensao "
				+" WHERE inscricaoParticipante.inscricaoAtividade.atividade.id = :idAtividade "
				+" AND inscricaoParticipante.cadastroParticipante.id = :idCadastroParticipante "
				+" AND inscricaoParticipante.statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos());
		
	
			
		Query q = getSession().createQuery(hql);
		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		
		@SuppressWarnings("unchecked")
		Collection<InscricaoAtividadeParticipante> parseTo = HibernateUtils.parseTo(q.list(), projecao, InscricaoAtividadeParticipante.class, "inscricaoParticipante");
		return new ArrayList<InscricaoAtividadeParticipante>( parseTo);
	
	}
	
	
	/**
	 * <p>Retorna as inscrições ativas do participante para as sub atividades da atividade passada.</p>
	 * 
	 * <p>O usuário não pode ter mais de 1 inscrição ativa para a mesma mini atividade, essa consulta é usada para verificar isso.</p>
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	public List<InscricaoAtividadeParticipante> findInscricoesParticipanteAtivasSubAtividadeDaAtividade(int idAtividade, int idCadastroParticipante) throws DAOException {
		
		String projecao = " inscricaoParticipante.id, inscricaoParticipante.statusInscricao, participanteExtensao.id ";
		
		String hql = " SELECT " +projecao+
				 " FROM InscricaoAtividadeParticipante inscricaoParticipante "
				+" LEFT JOIN inscricaoParticipante.participanteExtensao participanteExtensao "
				+" WHERE inscricaoParticipante.inscricaoAtividade.subAtividade.atividade.id = :idAtividade "
				+" AND inscricaoParticipante.cadastroParticipante.id = :idCadastroParticipante "
				+" AND inscricaoParticipante.statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos());
		
	
			
		Query q = getSession().createQuery(hql);
		q.setInteger("idAtividade", idAtividade);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		
		@SuppressWarnings("unchecked")
		Collection<InscricaoAtividadeParticipante> parseTo = HibernateUtils.parseTo(q.list(), projecao, InscricaoAtividadeParticipante.class, "inscricaoParticipante");
		return new ArrayList<InscricaoAtividadeParticipante>( parseTo);
	
	}
	
	
	/**
	 * <p>Retorna as inscrições ativias do participante na sub atividade passada.</p>
	 * 
	 * <p>O usuário não pode ter mais de 1 inscrição ativa para a mesma mini atividade, essa consulta é usada para verificar isso.</p>
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	public List<InscricaoAtividadeParticipante> findInscricoesAtivasParticipanteSubAtividade(int idSubAtividade, int idCadastroParticipante) throws DAOException {
		
		String projecao = " participante.id, participante.statusInscricao ";
		
		String hql = " SELECT " +projecao+
				 " FROM InscricaoAtividadeParticipante participante "
				+" WHERE participante.inscricaoAtividade.subAtividade.id = :idSubAtividade "
				+" AND participante.cadastroParticipante.id = :idCadastroParticipante "
				+" AND participante.statusInscricao NOT IN "+UFRNUtils.gerarStringIn(StatusInscricaoParticipante.getStatusInativos());
		
	
			
		Query q = getSession().createQuery(hql);
		q.setInteger("idSubAtividade", idSubAtividade);
		q.setInteger("idCadastroParticipante", idCadastroParticipante);
		
		@SuppressWarnings("unchecked")
		Collection<InscricaoAtividadeParticipante> parseTo = HibernateUtils.parseTo(q.list(), projecao, InscricaoAtividadeParticipante.class, "participante");
		return new ArrayList<InscricaoAtividadeParticipante>( parseTo);
	
	}
	
	
	/**
	 * Confirma no módulo de extensão a pagamento da GRU que foi registrado na parte de GRU no banco comum.
	 * 
	 * @param idsGRUsCursosEEventosQuitadas
	 * @throws DAOException 
	 * @throws  
	 */
	public void confirmaPagamentosManuaisGRUs(List<Integer> idsInscricaopartitipante) throws DAOException {
		
		
		if(idsInscricaopartitipante != null && idsInscricaopartitipante.size() > 0){ // só se existem GRUs quitadas
			
			String sql =
				" UPDATE extensao.inscricao_atividade_participante SET status_pagamento = "+StatusPagamentoInscricao.CONFIRMADO_MANUALMENTE
				+" WHERE status_pagamento = "+StatusPagamentoInscricao.EM_ABERTO+" AND id_inscricao_atividade_participante in ( :idsInscricaopartitipante ) ";
			
			Query q = getSession().createSQLQuery(sql);
			
			q.setParameterList("idsInscricaopartitipante", idsInscricaopartitipante);
			
			if (q.executeUpdate() < 1)
				throw new DAOException ("Ocorreu um erro ao confirmar o pagamento da atividades de extensão manualmente.");	
			
		}
	
	}
	
	
//	/**
//	 * Retorna se para a "Inscrição Atividade", da "Inscrição Atividade Participante" exige a cobrança de taxa.
//	 * 
//	 * Em outras palavaras, retorna se a ativida para a qual o participante está inscrito exige a cobrança de taxa.
//	 * 
//	 * @param idInscricao
//	 * @return
//	 * @throws DAOException
//	 */
//	@SuppressWarnings("unchecked")
//	public CursoEventoExtensao findCursoEventoExtensaoByInscricaoAtividadeParticipante(int idInscricaoAtividadeParticipante) throws DAOException {
//		
//		try {
//
//			String projecao = " inscricao.inscricaoAtividade.atividade.cursoEventoExtensao.id " +
//					", inscricao.inscricaoAtividade.atividade.cursoEventoExtensao.cobrancaTaxaMatricula " +
//					", inscricao.inscricaoAtividade.atividade.cursoEventoExtensao.taxaMatricula "+
//					", inscricao.inscricaoAtividade.atividade.cursoEventoExtensao.dataVencimentoGRU ";
//			
//			
//			String hql = " SELECT  "+projecao
//			+" FROM InscricaoAtividadeParticipante inscricao "
//			+" WHERE inscricao.id = :idInscricao";
//						
//			
//			Query query = getSession().createQuery(hql);
//			query.setInteger("idInscricao", idInscricaoAtividadeParticipante);
//			
//			List<Object[]> dados = query.list();
//			if(dados.size() == 1){
//				Object[] dadoCursoEvento = dados.get(0);
//				CursoEventoExtensao cursoEvento =  new CursoEventoExtensao();
//				cursoEvento.setId((Integer)dadoCursoEvento[0]);
//				//cursoEvento.setCobrancaTaxaMatricula((Boolean)dadoCursoEvento[1]);
//				//cursoEvento.setTaxaMatricula((Double)dadoCursoEvento[2]);
//				//cursoEvento.setDataVencimentoGRU((Date)dadoCursoEvento[3]);
//				return cursoEvento;
//			}else
//				return null;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
	
	
	/**
	 * Retorna a unidade orçamentária a partir da inscrição do participante. 
	 * É a unidade que irá receber o dinheiro do curso/evento, e para a qual a GRU deve ser emitida.
	 * 
	 * @param idInscricao
	 * @return
	 * @throws DAOException
	 */
	public Unidade findUnidadeOrcamentariaByInscricaoAtividadeParticipante(int idInscricaoAtividadeParticipante) throws DAOException {
		
		try {

			String projecao = " inscricao.inscricaoAtividade.atividade.projeto.unidadeOrcamentaria.id" +
					", inscricao.inscricaoAtividade.atividade.projeto.unidadeOrcamentaria.nome ";
			
			String hql = " SELECT  "+projecao
			+" FROM InscricaoAtividadeParticipante inscricao "
			+" WHERE inscricao.id = :idInscricao";
			
			Query query = getSession().createQuery(hql);
			query.setInteger("idInscricao", idInscricaoAtividadeParticipante);
			
			Object[] dados = (Object[]) query.uniqueResult();
			
			if( dados != null )
				return new Unidade((Integer)dados[0], 0l, (String)dados[1], "");
			else
				return null; // essa informação não é obrigatória

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	
	
	/**
	 * Retorna a inscrição do participante pelo código de acesso informado.
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findInscricaoParticipanteByCodigoAcesso(String codigo, int idAtividade) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.codigoAcesso = :codigo AND participante.ativo = trueValue()");
			hql.append(" AND participante.inscricaoAtividade.id = :idAtividade");
			
			Query query = getSession().createQuery(hql.toString());
			query.setString("codigo", codigo);
			query.setInteger("idAtividade", idAtividade);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a inscrição do participante pelo login (email e senha) informado.
	 * 
	 * @param email
	 * @param senha
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findInscricaoParticipanteByLogin(String email, String senha) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.email = :email AND participante.senha = :senha");
			hql.append(" AND participante.ativo = trueValue()");
			
			Query query = getSession().createQuery(hql.toString());
			query.setString("email", email);
			query.setString("senha", senha);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
//	/**
//	 * Verifica se a senha informada para confirmação da inscrição está correta, em caso positivo a inscrição é confirmada.
//	 * 
//	 * @param participante
//	 * @return
//	 * @throws DAOException
//	 */
//	public boolean confirmarInscricaoParticipanteBySenha(InscricaoAtividadeParticipante participante) throws DAOException {
//		
//		try {
//			StringBuilder hql = new StringBuilder();
//			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
//			hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
//			hql.append(" AND participante.id = :id AND participante.statusInscricao.id = :status");
//			hql.append(" AND participante.senha = :senha AND participante.ativo = trueValue()");
//			
//			Query query = getSession().createQuery(hql.toString());
//			query.setInteger("id", participante.getId());
//			query.setInteger("status", StatusInscricaoParticipante.INSCRITO);
//			query.setString("senha", participante.getSenha());
//			return ((InscricaoAtividadeParticipante) query.uniqueResult() != null) ? true : false;
//
//		} catch (Exception e) {
//			throw new DAOException(e.getMessage(), e);
//		}
//	}
	
	/**
	 * Realiza o somatório da quantidade de participantes em um período de inscrição.
	 * 
	 * @param idInscricao
	 * @return
	 * @throws DAOException
	 */
	public int countNumeroParticipantes(Integer idInscricao) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT count(participante.id) FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.id = :idInscricao");
			hql.append(" AND participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.statusInscricao.id = :idStatus");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idInscricao", idInscricao != null ? idInscricao : 0);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			return Integer.parseInt(query.uniqueResult().toString());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a inscrição pelos parâmetros definidos.
	 * 
	 * @param idInscricao
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findInscricaoParticipanteByStatus(Integer idInscricao, Long cpf, Integer idStatus) 
			throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.id = :idInscricao");
			hql.append(" AND participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.statusInscricao.id = :idStatus");
			hql.append(" AND participante.cpf = :cpf");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idInscricao", idInscricao);
			query.setInteger("idStatus", idStatus);
			query.setLong("cpf", cpf);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna a inscrição pelos parâmetros definidos.
	 * 
	 * @param idInscricao
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findInscricaoParticipanteByStatusEstrangeiro(Integer idInscricao, String passaporte, Integer idStatus) 
			throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.id = :idInscricao");
			hql.append(" AND participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.statusInscricao.id = :idStatus");
			hql.append(" AND participante.passaporte = :passaporte");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idInscricao", idInscricao);
			query.setInteger("idStatus", idStatus);
			query.setString("passaporte", passaporte);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca dados de participante por CPF ordenados por data de cadastro. 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findDadosRecentesParticipanteByCpf(Long cpf) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.ativo = trueValue() AND participante.cpf = :cpf");
			hql.append(" ORDER BY participante.dataCadastro DESC");
			
			Query query = getSession().createQuery(hql.toString());
			query.setLong("cpf", cpf);
			query.setMaxResults(1);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * busca dados de participante por passaporte ordenados por data mais atual.
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findDadosRecentesParticipanteByPassaporte(String passaporte) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.ativo = trueValue() AND participante.passaporte = :passaporte");
			hql.append(" ORDER BY participante.dataCadastro DESC");
			
			Query query = getSession().createQuery(hql.toString());
			query.setString("passaporte", passaporte);
			query.setMaxResults(1);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca participante de ação de extensão por id.
	 * @param idParticipante
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findByPrimaryKey(Integer idParticipante) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante.id as id, participante.statusInscricao as statusInscricao,");
			hql.append(" participante.nome as nome, participante.cpf as cpf, participante.passaporte as passaporte, ");
			hql.append(" participante.passaporte as passaporte,");
			hql.append(" participante.dataNascimento as dataNascimento, participante.instituicao as instituicao,");
			hql.append(" participante.logradouro as logradouro, participante.numero as numero,");
			hql.append(" participante.bairro as bairro, participante.bairro as bairro,");
			hql.append(" participante.municipio as municipio, participante.unidadeFederativa as unidadeFederativa,");
			hql.append(" participante.cep as cep, participante.email as email,");
			hql.append(" participante.telefone as telefone, participante.celular as celular");
			hql.append(" FROM InscricaoAtividadeParticipante participante");
			hql.append(" WHERE participante.id = :idParticipante AND participante.ativo = trueValue()");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idParticipante", idParticipante);
			return (InscricaoAtividadeParticipante) query.setResultTransformer(
					new AliasToBeanResultTransformer(InscricaoAtividadeParticipante.class)).uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Lista TODOS os inscritos na ação de extensão com o status passado.
	 * Caso o status não seja informado, retorna os inscritos com todos os status.
	 * Estes participantes são o resultado dos cadastrados em todas as inscrições on-line 
	 * abertas para a ação informada. 
	 * 
	 * @param idAtividade
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")	
	public Collection<InscricaoAtividadeParticipante> findInscricoesByAtividadeStatus(
			Integer idAtividade, Integer idStatus) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("SELECT participante.id as id, participante.statusInscricao as statusInscricao,");
			hql.append(" participante.nome as nome, participante.cpf as cpf, participante.passaporte as passaporte, ");
			hql.append(" participante.dataNascimento as dataNascimento, participante.instituicao as instituicao,");
			hql.append(" participante.logradouro as logradouro, participante.numero as numero,");
			hql.append(" participante.bairro as bairro, participante.bairro as bairro,");
			hql.append(" participante.municipio as municipio, participante.unidadeFederativa as unidadeFederativa,");
			hql.append(" participante.cep as cep, participante.email as email");
			hql.append(" FROM InscricaoAtividadeParticipante participante");			
			hql.append(" WHERE participante.inscricaoAtividade.atividade.id = :idAtividade");
			hql.append(" AND participante.inscricaoAtividade.ativo = trueValue()");
			
			if (idStatus != null) {
			    hql.append(" AND participante.statusInscricao.id = :idStatus");
			}
			
			hql.append(" ORDER BY participante.nome asc");			
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idAtividade", idAtividade);
			
			if (idStatus != null) {
			    query.setInteger("idStatus", idStatus);
			}
			
			return query.setResultTransformer(new AliasToBeanResultTransformer(InscricaoAtividadeParticipante.class)).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Lista TODOS os inscritos na sub atividade da ação de extensão com o status passado.
	 * Caso o status não seja informado, retorna os inscritos com todos os status.
	 * Estes participantes são o resultado dos cadastrados em todas as inscrições on-line 
	 * abertas para a ação informada. 
	 * 
	 * @param idAtividade
	 * @param idStatus
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")	
	public Collection<InscricaoAtividadeParticipante> findInscricoesBySubAtividadeStatus(
			Integer idSubAtividade, Integer idStatus) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();

			hql.append("SELECT participante.id as id, participante.statusInscricao as statusInscricao,");
			hql.append(" participante.nome as nome, participante.cpf as cpf, participante.passaporte as passaporte, ");
			hql.append(" participante.dataNascimento as dataNascimento, participante.instituicao as instituicao,");
			hql.append(" participante.logradouro as logradouro, participante.numero as numero,");
			hql.append(" participante.bairro as bairro, participante.bairro as bairro,");
			hql.append(" participante.municipio as municipio, participante.unidadeFederativa as unidadeFederativa,");
			hql.append(" participante.cep as cep, participante.email as email");
			hql.append(" FROM InscricaoAtividadeParticipante participante");			
			hql.append(" WHERE participante.inscricaoAtividade.subAtividade.id = :idSubAtividade");
			hql.append(" AND participante.inscricaoAtividade.ativo = trueValue()");
			
			if (idStatus != null) {
			    hql.append(" AND participante.statusInscricao.id = :idStatus");
			}
			
			hql.append(" ORDER BY participante.nome asc");			
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idSubAtividade", idSubAtividade);
			
			if (idStatus != null) {
			    query.setInteger("idStatus", idStatus);
			}
			
			return query.setResultTransformer(new AliasToBeanResultTransformer(InscricaoAtividadeParticipante.class)).list();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Retorna uma inscrição on-line de um participante
	 * a partir de um participante de ação de extensão.
	 * Utilizado na manutenção de inscrições on-line 
	 * Principalmente para o reenvio de senha e código de acesso. 
	 * 
	 * @param idParticipanteAcaoExtensao
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findByParticipanteAcaoExtensao(Integer idParticipanteAcaoExtensao) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante.id as id, participante.statusInscricao as statusInscricao,");
			hql.append(" participante.nome as nome, participante.cpf as cpf, participante.passaporte as passaporte, ");
			hql.append(" participante.dataNascimento as dataNascimento, participante.instituicao as instituicao,");
			hql.append(" participante.logradouro as logradouro, participante.numero as numero,");
			hql.append(" participante.bairro as bairro, participante.bairro as bairro,");
			hql.append(" participante.municipio as municipio, participante.unidadeFederativa as unidadeFederativa,");
			hql.append(" participante.cep as cep, participante.email as email");
			hql.append(" FROM InscricaoAtividadeParticipante participante");
			hql.append(" JOIN participante.participanteExtensao pae");
			hql.append(" WHERE pae.id = :idParticipanteAcaoExtensao AND participante.ativo = trueValue() AND pae.ativo=trueValue()");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idParticipanteAcaoExtensao", idParticipanteAcaoExtensao);
			return (InscricaoAtividadeParticipante) query.setResultTransformer(
					new AliasToBeanResultTransformer(InscricaoAtividadeParticipante.class)).uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	/**
	 * Retorna a inscrição do participante pelo código de email informado.
	 * 
	 * @param codigo
	 * @return
	 * @throws DAOException
	 */
	public InscricaoAtividadeParticipante findByEmailAcao(String email, Date dataNascimento, int idInscricaoAtividade) throws DAOException {
		
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT participante FROM InscricaoAtividadeParticipante participante");	
			hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
			hql.append(" AND participante.email = :email AND participante.dataNascimento = :dataNascimento AND participante.ativo = trueValue()");
			hql.append(" AND participante.inscricaoAtividade.id = :idAtividade ORDER BY participante.dataCadastro");
			
			Query query = getSession().createQuery(hql.toString());
			query.setString("email", email);
			query.setDate("dataNascimento",dataNascimento);
			query.setInteger("idAtividade", idInscricaoAtividade);
			return (InscricaoAtividadeParticipante) query.uniqueResult();

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}


	/**
	 * Realiza o somatório da quantidade de participantes aprovados (aceitos pelo coordenador) em uma 
	 * Ação de Extensão.
	 * 
	 * @param idInscricao
	 * @return
	 * @throws DAOException
	 */
	public int countParticipantesByAcao(Integer idAcao) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT count(inscricao.id) FROM InscricaoAtividadeParticipante inscricao ");
			hql.append(" JOIN inscricao.inscricaoAtividade as inscAtv JOIN inscAtv.atividade as atv ");
			hql.append(" WHERE atv.id = :idAcao");
			hql.append(" AND inscricao.ativo = trueValue()");
			hql.append(" AND inscricao.statusInscricao.id = :idStatus");
			
			Query query = getSession().createQuery(hql.toString());
			query.setInteger("idAcao", idAcao != null ? idAcao : 0);
			query.setInteger("idStatus", StatusInscricaoParticipante.APROVADO);
			return Integer.parseInt(query.uniqueResult().toString());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	
	



	/**
	 * Busca discente participante por CPF ordenados por data de cadastro. 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Discente findDiscenteParticipanteByCpf(Long cpf) throws DAOException {
	
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT d.id FROM Discente d JOIN d.pessoa ");	
			hql.append(" WHERE d.pessoa.cpf_cnpj = :cpf ");
			hql.append(" ORDER BY d.dataCadastro DESC");
		
			Query query = getSession().createQuery(hql.toString());
			query.setLong("cpf", cpf);
			query.setMaxResults(1);
			if(query.uniqueResult() == null) return null;
			return new Discente((Integer) query.uniqueResult());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * busca discente participante por passaporte ordenados por data mais atual.
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	public Discente findDiscenteParticipanteByPassaporte(String passaporte) throws DAOException {
	
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT d.id FROM Discente d JOIN d.pessoa ");	
			hql.append(" WHERE d.pessoa.passaporte = :passaporte  ");
			hql.append(" ORDER BY d.dataCadastro DESC");
		
			Query query = getSession().createQuery(hql.toString());
			query.setString("passaporte", passaporte);
			query.setMaxResults(1);
			if(query.uniqueResult() == null) return null;
			return new Discente((Integer) query.uniqueResult());
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Busca servidor participante por passaporte ordenados por data mais atual.
	 * @param passaporte
	 * @return
	 * @throws DAOException
	 */
	public Servidor findServidorParticipanteByPassaporte(String passaporte) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT d.id FROM Servidor d JOIN d.pessoa ");	
			hql.append(" WHERE d.pessoa.passaporte = :passaporte ");
			hql.append(" ORDER BY d.dataAdmissao DESC");
		
			Query query = getSession().createQuery(hql.toString());
			query.setString("passaporte", passaporte);
			query.setMaxResults(1);
			if(query.uniqueResult() == null) return null;
			return new Servidor((Integer) query.uniqueResult());
			
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}
	
	/**
	 * Busca servidor participante por CPF ordenados por data de cadastro. 
	 * @param cpf
	 * @return
	 * @throws DAOException
	 */
	public Servidor findServidorParticipanteByCpf(Long cpf) throws DAOException {
		try {
			StringBuilder hql = new StringBuilder();
			hql.append("SELECT d.id FROM Servidor d JOIN d.pessoa ");	
			hql.append(" WHERE d.pessoa.cpf_cnpj = :cpf  ");
			hql.append(" ORDER BY d.dataAdmissao DESC");
		
			Query query = getSession().createQuery(hql.toString());
			query.setLong("cpf", cpf);
			query.setMaxResults(1);
			if(query.uniqueResult() == null) return null;
			return new Servidor((Integer) query.uniqueResult());

		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/** Retorna todas as incrições dos participantes já cadastrados com o cpf informado  */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividadeParticipante> findInformacoesEmailInscricoesParticipanteByCPF( Long cpf ) throws DAOException {
		String projecao = " participante.id, participante.email ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT "+projecao+" FROM InscricaoAtividadeParticipante participante");	
		hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
		hql.append(" AND participante.cpf = :cpf");
		Query query = getSession().createQuery(hql.toString());
		query.setLong("cpf", cpf);
		return new ArrayList<InscricaoAtividadeParticipante>( HibernateUtils.parseTo(query.list(), projecao, InscricaoAtividadeParticipante.class, "participante")) ;
	}

	/** Retorna todas as incrições dos participantes já cadastrados com o cpf informado  */
	@SuppressWarnings("unchecked")
	public List<InscricaoAtividadeParticipante> findInformacoesEmailInscricoesParticipanteByPassaporte( String passaporte ) throws DAOException {
		String projecao = " participante.id, participante.email ";
		
		StringBuilder hql = new StringBuilder();
		hql.append("SELECT "+projecao+" FROM InscricaoAtividadeParticipante participante");	
		hql.append(" WHERE participante.inscricaoAtividade.ativo = trueValue()");
		hql.append(" AND participante.passaporte = :passaporte");
		Query query = getSession().createQuery(hql.toString());
		query.setString("passaporte", passaporte);
		return new ArrayList<InscricaoAtividadeParticipante>( HibernateUtils.parseTo(query.list(), projecao, InscricaoAtividadeParticipante.class, "participante")) ;
	}
	
	
	/** Atualiza o email do Participantes baseado no cpf informado */
	public void atualizacaoEmailParticipante( Long cpf, String email ) {
		update("update extensao.inscricao_atividade_participante set email = ? where cpf = ?", new Object[] { email, cpf });
	}
	
	/** Atualiza o email do Participantes baseado no cpf informado */
	public void atualizacaoEmailParticipante( String passaporte, String email ) {
		update("update extensao.inscricao_atividade_participante set email = ? where passaporte = ?", new Object[] { email, passaporte });
	}
	
}