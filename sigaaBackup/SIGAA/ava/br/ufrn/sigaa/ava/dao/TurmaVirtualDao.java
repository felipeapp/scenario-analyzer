/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 18/01/2008
 * 
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.UFRNUtils.gerarStringIn;
import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Expression;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.jdbc.core.RowMapper;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.DAOFactory;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.HibernateUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.integracao.dto.DiscenteDTO;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.arq.dao.UsuarioDao;
import br.ufrn.sigaa.arq.expressao.ArvoreExpressao;
import br.ufrn.sigaa.ava.dominio.AbstractMaterialTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoTurma;
import br.ufrn.sigaa.ava.dominio.ArquivoUsuario;
import br.ufrn.sigaa.ava.dominio.AulaExtra;
import br.ufrn.sigaa.ava.dominio.ChatTurma;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesVideoTurma;
import br.ufrn.sigaa.ava.dominio.ConteudoTurma;
import br.ufrn.sigaa.ava.dominio.DataAvaliacao;
import br.ufrn.sigaa.ava.dominio.Enquete;
import br.ufrn.sigaa.ava.dominio.EstacaoChamadaBiometricaTurma;
import br.ufrn.sigaa.ava.dominio.IndicacaoReferencia;
import br.ufrn.sigaa.ava.dominio.MaterialTurma;
import br.ufrn.sigaa.ava.dominio.NoticiaTurma;
import br.ufrn.sigaa.ava.dominio.PermissaoAva;
import br.ufrn.sigaa.ava.dominio.PlanoEnsino;
import br.ufrn.sigaa.ava.dominio.RegistroAtividadeTurma;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.RotuloTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.ava.dominio.VideoTurma;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeral;
import br.ufrn.sigaa.ava.forum.dominio.ForumGeralMensagem;
import br.ufrn.sigaa.ava.forum.dominio.TipoForum;
import br.ufrn.sigaa.ava.forum.relacionamentos.dominio.ForumTurma;
import br.ufrn.sigaa.ava.negocio.ConverterVideoThread;
import br.ufrn.sigaa.ava.questionarios.dominio.QuestionarioTurma;
import br.ufrn.sigaa.ava.util.MimeTypeVideoUtil;
import br.ufrn.sigaa.ava.validacao.TurmaVirtualException;
import br.ufrn.sigaa.biblioteca.processos_tecnicos.dominio.TituloCatalografico;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.monitoria.dominio.SituacaoDiscenteMonitoria;
import br.ufrn.sigaa.parametros.dominio.ParametrosTurmaVirtual;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Implementação de TurmaVirtualDAO
 *
 * @author David Pereira
 *
 */
public class TurmaVirtualDao extends GenericSigaaDAO {


	/**
	 * Lista as datas de avaliação de determinada turma
	 * @param turma
	 * @return
	 */
	public List<DataAvaliacao> buscarDatasAvaliacao(Turma turma) {
		DetachedCriteria criteria = DetachedCriteria.forClass(DataAvaliacao.class);
		criteria.add(eq("turma", turma)).add(eq("ativo", true)).addOrder(asc("data"));
		
		@SuppressWarnings("unchecked")
		List<DataAvaliacao> lista = getHibernateTemplate().findByCriteria(criteria);
		return lista;
	}

	/**
	 * Lista as notícias de determinada turma
	 * @param turma
	 * @return
	 */
	public List<NoticiaTurma> findNoticiasByTurma(Turma turma) {
		DetachedCriteria c = DetachedCriteria.forClass(NoticiaTurma.class);
		c.add(eq("turma", turma)).addOrder(desc("data"));

		@SuppressWarnings("unchecked")
		List<NoticiaTurma> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}

	/**
	 * Lista as últimas 5 notícias de determinada turma
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public List<NoticiaTurma> findUltimasNoticiasByTurma(Turma turma) throws DAOException {
		Criteria c = getSession().createCriteria(NoticiaTurma.class);
		c.add(eq("turma", turma)).addOrder(desc("data"));

		c.setMaxResults(5);
		
		@SuppressWarnings("unchecked")
		List<NoticiaTurma> lista = c.list();
		return lista;
	}

	/**
	 * Lista os tópicos de aula de determinada turma
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<TopicoAula> findAulasByTurma(Turma turma) throws DAOException {
		Query q = getSession().createQuery("select distinct t from TopicoAula t LEFT JOIN FETCH t.docentesTurma where t.turma.id = ? and t.ativo = trueValue() order by t.data asc, t.fim asc, t.topicoPai desc");
		
		q.setInteger(0, turma.getId());
		
		Collection<TopicoAula> lista = q.list();
		// Lista de todas as pessoas associadas a um DocenteTurma dos TopicoAulas listados.
		List<Integer> pessoas = new ArrayList<Integer>();
		
		for (TopicoAula ta : lista) {
			if(!isEmpty(ta.getDocentesTurma())) {
				for (DocenteTurma dt : ta.getDocentesTurma()) {
					int idPessoa = dt.getDocente() != null ? dt.getDocente().getPessoa().getId() : dt.getDocenteExterno().getPessoa().getId();
					
					if(!pessoas.contains(idPessoa))
						pessoas.add(idPessoa);
				}
			}
		}
		
		// Todos os usuários das pessoas.
		Collection<Usuario> usuarios = null;
		if(!isEmpty(pessoas)) {
			UsuarioDao uDao = DAOFactory.getInstance().getDAO(UsuarioDao.class);
			try {
				usuarios = uDao.findUsuariosComFotoByPessoas(pessoas);
			} finally {
				uDao.close();
			}
		}
		
		if(!isEmpty(usuarios)) {
			for (TopicoAula ta : lista) {
				if(!isEmpty(ta.getDocentesTurma())) {
					for (DocenteTurma dt : ta.getDocentesTurma()) {
						int idPessoa = dt.getDocente() != null ? dt.getDocente().getPessoa().getId() : dt.getDocenteExterno().getPessoa().getId();
						
						for (Usuario u : usuarios) {
							if(idPessoa == u.getPessoa().getId()) {
								ta.adicionarDocente(u);
								break;
							}
						}
					}
				}
			}
		}
		
		return lista;
	}

	/**
	 * Lista as indicações de referência de determinada turma
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<IndicacaoReferencia> findLinksByTurma(Turma turma)
	throws DAOException {
		try {
			Criteria c = getSession().createCriteria(IndicacaoReferencia.class);
			c.add(Expression.eq("turma", turma));
			c.add(Expression.eq("ativo", true));

			ProjectionList pl = Projections.projectionList();
			pl.add(Projections.property("id"));
			pl.add(Projections.property("url"));
			pl.add(Projections.property("descricao"));
			pl.add(Projections.property("detalhes"));
			pl.add(Projections.property("tipo"));
			pl.add(Projections.property("turma"));
			pl.add(Projections.property("aula"));

			c.setProjection(pl);

			@SuppressWarnings("unchecked")
			List<IndicacaoReferencia> result = c.list();
			List<IndicacaoReferencia> links = new ArrayList<IndicacaoReferencia>();
			for (Iterator<?> it = result.iterator(); it.hasNext();) {
				Object[] linha = (Object[]) it.next();
				IndicacaoReferencia ir = new IndicacaoReferencia();
				ir.setId((Integer) linha[0]);
				ir.setUrl((String) linha[1]);
				ir.setDescricao((String) linha[2]);
				ir.setDetalhes((String) linha[3]);
				ir.setTipo((Character) linha[4]);
				ir.setTurma((Turma) linha[5]);
				ir.setAula((TopicoAula) linha[6]);
				links.add(ir);
			}

			return links;
		} catch (Exception e) {
			throw new DAOException(e.getMessage(), e);
		}
	}

	/**
	 * Lista as atividades de determinada turma
	 * @param discente
	 * @param ano
	 * @param semestre
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividadeTurma> findAtividadesTurmas(
			Discente discente, int ano, int semestre) throws DAOException {

		Query q = getSession().createQuery(
				"from RegistroAtividadeTurma r where r.turma in "
				+ "( select m.turma from MatriculaComponente m where m.turma.ano = "
				+ ano + " and m.turma.periodo = " + semestre
				+ " and m.discente = " + discente.getId()
				+ ") order by r.data desc");

		q.setMaxResults(15);

		@SuppressWarnings("unchecked")
		Collection<RegistroAtividadeTurma> lista = q.list();
		return lista;

	}

	/**
	 * Lista os conteúdos de determinada turma
	 * @param turma
	 * @return
	 */
	public List<ConteudoTurma> findConteudoTurma(Turma turma) {
		try {
			String projecao = "id, titulo, aula.id, material.id, material.ordem, material.nivel ";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM ConteudoTurma c WHERE c.ativo = trueValue() AND c.aula.turma.id = :idTurma ORDER BY c.material.ordem");
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<ConteudoTurma> lista = (List<ConteudoTurma>) HibernateUtils.parseTo(q.list(), projecao, ConteudoTurma.class);
			
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Lista os conteúdos de determinada turma de acordo com o usuario que acessar (discente ou docente)
	 * @param turma
	 * @return
	 */
	public List<ConteudoTurma> findConteudoTurma(Turma turma,boolean isDocente) {
		try {
			String projecao = "id, titulo, aula.id, material.id, material.ordem, material.nivel ";
			String hql = "SELECT " + projecao + " FROM ConteudoTurma c WHERE c.ativo = trueValue() AND c.aula.turma.id = :idTurma ";
			
			if (!isDocente) {
				hql += "AND c.aula.visivel = trueValue() ";
			}
			
			hql += " ORDER BY c.material.ordem";
			
			Query q = getSession().createQuery(hql);
					
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<ConteudoTurma> lista = (List<ConteudoTurma>) HibernateUtils.parseTo(q.list(), projecao, ConteudoTurma.class);
			
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}


	/**
	 * Procura a Agenda de Avaliações da Turma Usado em AvaliacaoDataMBean
	 * Autor: Edson Anibal (ambar@info.ufrn.br)
	 */
	public List<DataAvaliacao> findAvaliacaoDataByTurma(int idTurma)
	throws DAOException {
		Query q = getSession()
		.createQuery(
				"select new DataAvaliacao(a.id, a.descricao, a.data, a.hora, a.turma.id) from DataAvaliacao a where a.turma.id = ? and a.ativo = true order by a.data asc");
		q.setInteger(0, idTurma);
		
		@SuppressWarnings("unchecked")
		List<DataAvaliacao> lista = q.list();
		return lista;
	}

	/**
	 * Lista os arquivos de determinada turma
	 * @param idTurma
	 * @return
	 */
	public List<ArquivoTurma> findArquivosByTurma(int idTurma) {
		@SuppressWarnings("unchecked")
		List<ArquivoTurma> result = getHibernateTemplate().find(
				"select at.id, at.nome, at.data, at.aula.id, at.aula.descricao, au.id, au.nome, au.idArquivo, at.descricao, " +
				"at.material.id, at.material.ordem, at.material.nivel, at.data "
				+ "from ArquivoTurma at, ArquivoUsuario au "
				+ "where at.arquivo.id = au.id and at.aula.turma.id = ?",
				idTurma);
		List<ArquivoTurma> arquivos = new ArrayList<ArquivoTurma>();
		for (Iterator<?> it = result.iterator(); it.hasNext();) {
			Object[] linha = (Object[]) it.next();
			ArquivoTurma at = new ArquivoTurma();
			at.setId((Integer) linha[0]);
			at.setNome((String) linha[1]);
			at.setData((Date) linha[2]);
			at.setAula(new TopicoAula());
			at.getAula().setId((Integer) linha[3]);
			at.getAula().setDescricao((String)linha[4]);
			at.setArquivo(new ArquivoUsuario());
			at.getArquivo().setId((Integer) linha[5]);
			at.getArquivo().setNome((String) linha[6]);
			at.getArquivo().setIdArquivo((Integer) linha[7]);
			at.setDescricao((String) linha[8]);
			at.setMaterial(new MaterialTurma());
			at.getMaterial().setId((Integer) linha[9]);
			at.getMaterial().setOrdem((Integer) linha[10]);
			at.getMaterial().setNivel((Integer) linha[11]);
			at.setData((Date) linha[12]);
			at.setContentType(EnvioArquivoHelper.recuperaContentTypeArquivo((Integer) linha[7]));

			arquivos.add(at);
		}

		return arquivos;
	}
	
	/**
	 * Lista os arquivos de determinada turma
	 * @param idTurma
	 * @return
	 */
	public List<ArquivoTurma> findArquivosByTurma(int idTurma,boolean isDocente) {
		
		String hql = "select at.id, at.nome, at.data, at.aula.id, at.aula.descricao, au.id, au.nome, au.idArquivo, at.descricao, " +
					"at.material.id, at.material.ordem, at.material.nivel, at.data "
					+ "from ArquivoTurma at, ArquivoUsuario au "
					+ "where at.arquivo.id = au.id and at.aula.turma.id = ?";
		
		if (!isDocente) {
			hql += " and at.aula.visivel = trueValue()";		
		}
		
		@SuppressWarnings("unchecked")
		List<ArquivoTurma> result = getHibernateTemplate().find(hql, idTurma);
		List<ArquivoTurma> arquivos = new ArrayList<ArquivoTurma>();
		for (Iterator<?> it = result.iterator(); it.hasNext();) {
			Object[] linha = (Object[]) it.next();
			ArquivoTurma at = new ArquivoTurma();
			at.setId((Integer) linha[0]);
			at.setNome((String) linha[1]);
			at.setData((Date) linha[2]);
			at.setAula(new TopicoAula());
			at.getAula().setId((Integer) linha[3]);
			at.getAula().setDescricao((String)linha[4]);
			at.setArquivo(new ArquivoUsuario());
			at.getArquivo().setId((Integer) linha[5]);
			at.getArquivo().setNome((String) linha[6]);
			at.getArquivo().setIdArquivo((Integer) linha[7]);
			at.setDescricao((String) linha[8]);
			at.setMaterial(new MaterialTurma());
			at.getMaterial().setId((Integer) linha[9]);
			at.getMaterial().setOrdem((Integer) linha[10]);
			at.getMaterial().setNivel((Integer) linha[11]);
			at.setData((Date) linha[12]);
			at.setContentType(EnvioArquivoHelper.recuperaContentTypeArquivo((Integer) linha[7]));

			arquivos.add(at);
		}

		return arquivos;
	}

	

	/**
	 * Lista as respostas de uma tarefa de determinada turma
	 * @param idTarefa
	 * @return
	 * @throws DAOException
	 */
	public List<RespostaTarefaTurma> findRespostasByTarefa(int idTarefa)
	throws DAOException {
		try {
			Criteria c = getSession().createCriteria(RespostaTarefaTurma.class);
			c.add(Expression.eq("tarefa.id", idTarefa));
			c.add(Expression.eq("ativo", true));
			
			@SuppressWarnings("unchecked")
			List<RespostaTarefaTurma> lista = c.list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna a permissão que determinada pessoa tem em determinada turma
	 * @param pessoa
	 * @param turma
	 * @return
	 */
	public PermissaoAva findPermissaoByPessoaTurma(Pessoa pessoa, Turma turma) {
		return (PermissaoAva) getHibernateTemplate().uniqueResult(
				"from PermissaoAva p where p.pessoa.id = ? and p.turma.id = ?",
				new Object[] { pessoa.getId(), turma.getId() });
	}
	
	/**
	 * Retorna a permissão que determinada pessoa tem em determinada turma
	 * @param pessoa
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<PermissaoAva> findPermissaoByPessoaTurmas(Pessoa pessoa, List<Integer> idsTurma) throws HibernateException, DAOException {
		return (ArrayList<PermissaoAva>) getSession().createQuery("select p from PermissaoAva p left join p.turma t where p.pessoa.id = " + pessoa.getId() + " and t.id in "+UFRNUtils.gerarStringIn(idsTurma)+" and t.situacaoTurma.id in " + gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA})).list();
	}
	
	/**
	 * Retorna todas as turmas que esta pessoa tem a permissão passada
	 * @param pessoa
	 * @param turma
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <Turma> findTurmasPermitidasByPessoa(Pessoa pessoa, int tipoPermissao) throws HibernateException, DAOException{
		
		String auxPermissao = "";
		
		switch (tipoPermissao) {
			case PermissaoAva.DOCENTE: auxPermissao += "a.docente = true"; break;
			case PermissaoAva.FORUM: auxPermissao += "a.forum = true"; break;
			case PermissaoAva.ENQUETE: auxPermissao += "a.enquete = true"; break;
			case PermissaoAva.TAREFA: auxPermissao += "a.tarefa = true"; break;
			case PermissaoAva.CORRIGIR_TAREFA: auxPermissao += "a.corrigir_tarefa = true"; break;
			case PermissaoAva.INSERIR_ARQUIVO: auxPermissao += "a.inserir_arquivo = true"; break;
		}
		
		@SuppressWarnings("unchecked")
		List <Turma> rs = getSession().createQuery("select t from PermissaoAva a join a.turma t where a.pessoa.id = " + pessoa.getId() + " and " + auxPermissao + " and t.situacaoTurma.id in " + gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA})).list();
		
		return rs;
	}

	/**
	 * Retorna todas as permissões existentes para determinada turma 
	 * @param turma
	 * @return
	 */
	public List<PermissaoAva> findPermissoesByTurma(Turma turma) {
		@SuppressWarnings("unchecked")
		List<PermissaoAva> lista = getHibernateTemplate().find(
				"from PermissaoAva p where p.turma.id = ? order by p.pessoa.nome asc",
				turma.getId());
		return lista;
	}


	/**
	 * Retorna as turmas que estão habilitadas para determinada pessoa   
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasHabilitadasByPessoa(Pessoa pessoa)
	throws DAOException {
		try {
			Query q = getSession().createQuery(
					"select t from PermissaoAva p join p.turma t left join t.polo pl where p.pessoa.id = :idPessoa and t.situacaoTurma.id in " + gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA} ));
			q.setInteger("idPessoa", pessoa.getId());
			
			@SuppressWarnings("unchecked")
			List<Turma> lista = q.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Retorna as turmas que estão habilitadas para determinada pessoa   
	 * @param pessoa
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasEADHabilitadasByPessoa(Pessoa pessoa)
	throws DAOException {
		try {
			Query q = getSession().createQuery(
					"select t from PermissaoAva p join p.turma t " +
					"left join fetch t.polo pl " +
					"left join fetch pl.cidade m " +
					"left join fetch m.unidadeFederativa uf " +
					"where p.pessoa.id = :idPessoa and t.situacaoTurma.id in " + gerarStringIn( new int[] {SituacaoTurma.A_DEFINIR_DOCENTE, SituacaoTurma.ABERTA} ));
			q.setInteger("idPessoa", pessoa.getId());
			
			@SuppressWarnings("unchecked")
			List<Turma> lista = q.setResultTransformer( Criteria.DISTINCT_ROOT_ENTITY).list();
			return lista;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todas as últimas atividades de determinada turma
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public List<RegistroAtividadeTurma> findUltimasAtividades(Turma turma)
	throws DAOException {
		try {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("select new RegistroAtividadeTurma(id, descricao, data) from RegistroAtividadeTurma ");
			stringBuilder.append("where turma.id = ? order by id desc");
			@SuppressWarnings("unchecked")
			List<RegistroAtividadeTurma> result = getSession().createQuery(
					stringBuilder.toString())
					.setInteger(0, turma.getId()).setMaxResults(5).list();
			return result;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna as últimas mensagens de fóruns enviadas para uma turmas virtual.
	 * Usado na barra direita da turma virtual, caso um tópico não possua pai, seta o pai dele com seu próprio id
	 * para que o link da barra direita possa redirecioná-lo para a listagem de tópicos.
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public List<ForumGeralMensagem> findUltimasMensagens(Turma turma)
	throws DAOException {
		try {
			String sql = "select fm.id_forum_mensagem, fm.titulo, fm.conteudo, fm.data, fm.id_mensagem_pai , p.nome " +
						" from ava.forum_mensagem fm "+
						" join ava.forum f on fm.id_forum = f.id_forum " +
						" join ava.forum_turma ft on ft.id_forum = f.id_forum " +
						" join comum.usuario u on u.id_usuario = fm.id_usuario " +
						" join comum.pessoa p on p.id_pessoa = u.id_pessoa " +
						" where ft.id_turma = "+turma.getId()+" and fm.ativo = trueValue() and fm.ativo = trueValue() " +
						" order by fm.data desc " +
						" limit 5 ";
			
			List<Object[]> result = getSession().createSQLQuery(sql).list();
			List<ForumGeralMensagem> foruns = new ArrayList<ForumGeralMensagem>();
			
			if ( result != null )
				for ( Object[] linha : result ) {
					int i = 0;
					ForumGeralMensagem fm = new ForumGeralMensagem();
					fm.setId( (Integer) linha[i++] );
					fm.setTitulo( (String) linha[i++] );
					fm.setConteudo( (String) linha[i++] );
					fm.setData( (Date) linha[i++] );
					
					ForumGeralMensagem mpai = new ForumGeralMensagem();
					Integer idPai = (Integer) linha[i++]; 
					// Trata caso o tópico não possua pai 
					mpai.setId( idPai == null ? fm.getId() : idPai );
					fm.setMensagemPai(mpai);
					
					Usuario u = new Usuario();
					Pessoa p = new Pessoa();
					p.setNome( (String) linha[i++] );
					u.setPessoa(p);
					fm.setUsuario(u);
					
					foruns.add(fm);
				}
			
			return foruns;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Retorna todas as últimas atividades de determinada turma
	 * @param turma
	 * @return
	 * @throws DAOException
	 */
	public Collection<RegistroAtividadeTurma> findUltimasAtividadesTurmas(Collection<Turma> turmas) throws DAOException {
		try {
			String projecao = " at.id, at.descricao, at.data, turma.id ";
			
			String hql = "select" + projecao + 
							"from RegistroAtividadeTurma at " +
								"inner join at.turma as turma " +
								"inner join turma.disciplina " +
							"where turma.id in " + UFRNUtils.gerarStringIn(turmas) +
							" order by at.id desc";
			
			@SuppressWarnings("unchecked")
			List<Object[]> list = getSession().createQuery(hql).setMaxResults(10).list();
			
			Collection<RegistroAtividadeTurma> registros = HibernateUtils.parseTo(list, projecao, RegistroAtividadeTurma.class, "at");
			
			for (RegistroAtividadeTurma reg : registros) {
				for (Turma turma : turmas) {
					if(reg.getTurma().getId() == turma.getId()) {
						reg.setTurma(turma);
						break;
					}
				}
			}
			
			return registros;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Este método retorna as outras turmas da disciplina da turma para por parâmetros
	 * que o professor participou, seja com vínculo através de DocenteTurma ou com vínculo através de PermissaoAva.
	 * @param turma
	 * @param docente
	 * @param docenteExterno
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasAnteriores(Turma turma, Servidor docente,
			DocenteExterno docenteExterno) throws DAOException {

		if (docente == null && docenteExterno == null) {
			return null;
		}

		/** Disciplinas equivalentes. */
		String componentes = "( " +turma.getDisciplina().getId();
		if ( turma.getDisciplina().getEquivalencia() != null ){
			
			ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(turma.getDisciplina().getEquivalencia());
			
			if ( arvore != null ){
				Integer[] equivalencias = arvore.componentesIsolados();
				for ( Integer equivalencia : equivalencias )
					componentes += " , " + equivalencia.toString();
			}	
		}
		componentes += " )";	
			
		/** turmas vinculadas a DocenteTurma */
		StringBuilder hql = new StringBuilder( "SELECT t FROM Turma t LEFT JOIN fetch t.docentesTurmas dt LEFT JOIN fetch dt.docente" );
		hql.append( docenteExterno != null ? "Externo" : "");
		hql.append( " d WHERE t.disciplina.id in "+componentes+" AND d.id = :idDocente  and t.id <> :idTurma and t.turmaAgrupadora is NULL" );
		hql.append( " ORDER BY t.ano desc, t.periodo desc, t.codigo asc");


		Query query1 = getSession().createQuery(hql.toString())
		.setInteger("idDocente", (docenteExterno != null ? docenteExterno.getId() : docente.getId()))
		.setInteger("idTurma", turma.getId());

		@SuppressWarnings("unchecked")
		List<Turma> turmas = query1.list();

		/** turmas Vinculadas a PermissaoAva */
		StringBuilder permissoes = new StringBuilder("SELECT t FROM Turma t, PermissaoAva p");
		permissoes.append( " WHERE t.id = p.turma.id " );
		permissoes.append( " AND p.docente = trueValue() " );
		permissoes.append( " AND t.disciplina.id in "+componentes + " ");
		permissoes.append( " AND t.turmaAgrupadora is NULL" );
		permissoes.append( " AND t.id <> :idTurma " );
		permissoes.append( " AND p.pessoa.id = :idPessoa " );
		permissoes.append( " ORDER BY t.ano desc, t.periodo desc, t.codigo asc" );

		Query query2 = getSession().createQuery(permissoes.toString())
		.setInteger("idPessoa", (docenteExterno != null ? docenteExterno.getPessoa().getId() : docente.getPessoa().getId()))
		.setInteger("idTurma", turma.getId());

		@SuppressWarnings("unchecked")
		List<Turma> result = query2.list();
		turmas.addAll(  result );
		Collections.sort(turmas, new Comparator<Turma>(){
			public int compare(Turma t1, Turma t2) {
				int retorno = 0;
				retorno = t2.getAno() - t1.getAno();
				if( retorno == 0 )
					retorno = t2.getPeriodo() - t1.getPeriodo();
				return retorno;
			}
		});

		return turmas;

	}

	/**
	 * Este método retorna as turmas agrupadoras da disciplina da turma para por parâmetros
	 * que o professor participou, seja com vínculo através de DocenteTurma ou com vínculo através de PermissaoAva.
	 * @param turma
	 * @param docente
	 * @param docenteExterno
	 * @return
	 * @throws DAOException
	 */
	public List<Turma> findTurmasAgrupadorasImportacao ( Turma turma, Servidor docente,
			DocenteExterno docenteExterno ) throws HibernateException, DAOException {
		
		if (docente == null && docenteExterno == null) {
			return null;
		}

		/** Disciplinas equivalentes. */
		String componentes = "( " +turma.getDisciplina().getId();
		if ( turma.getDisciplina().getEquivalencia() != null ){
			
			ArvoreExpressao arvore = ArvoreExpressao.fromExpressao(turma.getDisciplina().getEquivalencia());
			
			if ( arvore != null ){
				Integer[] equivalencias = arvore.componentesIsolados();
				for ( Integer equivalencia : equivalencias )
					componentes += " , " + equivalencia.toString();
			}	
		}
		componentes += " )";
		
		StringBuilder hql = new StringBuilder("SELECT ta FROM Turma ta WHERE  ta.id <> :idTurma  AND ta.id in ( ");   
		hql.append("SELECT t.turmaAgrupadora.id FROM Turma t LEFT JOIN t.docentesTurmas dt LEFT JOIN dt.docente" );
		hql.append( docenteExterno != null ? "Externo" : "");
		hql.append( " d WHERE t.disciplina.id in "+componentes+" AND d.id = :idDocente " );
		hql.append( " ORDER BY t.ano desc, t.periodo desc, t.codigo asc)");
		
		Query query1 = getSession().createQuery(hql.toString())
		.setInteger("idDocente", (docenteExterno != null ? docenteExterno.getId() : docente.getId()))
		.setInteger("idTurma", turma.getId());

		@SuppressWarnings("unchecked")
		List<Turma> turmas = query1.list();
		
		/** turmas Vinculadas a PermissaoAva */
		StringBuilder permissoes = new StringBuilder("SELECT ta FROM Turma ta WHERE ta.id in ( ");
		permissoes.append("SELECT t.turmaAgrupadora.id FROM Turma t, PermissaoAva p");
		permissoes.append( " WHERE t.id = p.turma.id " );
		permissoes.append( " AND p.docente = trueValue() " );
		permissoes.append( " AND t.disciplina.id in "+componentes+ " " );
		permissoes.append( " AND t.id <> :idTurma " );
		permissoes.append( " AND p.pessoa.id = :idPessoa " );
		permissoes.append( " ORDER BY t.ano desc, t.periodo desc, t.codigo asc)" );

		Query query2 = getSession().createQuery(permissoes.toString())
		.setInteger("idPessoa", (docenteExterno != null ? docenteExterno.getPessoa().getId() : docente.getPessoa().getId()))
		.setInteger("idTurma", turma.getId());
		
		@SuppressWarnings("unchecked")
		List<Turma> result = query2.list();
		turmas.addAll(  result );
				
		return turmas;
	}
	
	/**
	 * Retorna as referências de determinada turma
	 * @param turma
	 * @return
	 */
	public List<IndicacaoReferencia> findReferenciasTurma(Turma turma) {
		try {
			String projecao = " id, descricao, url, tipoIndicacao, tipo, aula.id, material.id, material.ordem, material.nivel, tituloCatalografico.id ";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM IndicacaoReferencia r WHERE r.ativo = trueValue() AND r.turma.id = :idTurma ORDER BY r.material.ordem");
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<Object[]> results = q.list();
			
			ArrayList<IndicacaoReferencia> referencias = null;
			if ( results != null ) {
				referencias = new ArrayList<IndicacaoReferencia>();
				for(Object[] linha:results) {
					if(linha != null) {
						Integer i = 0;
						IndicacaoReferencia r = new IndicacaoReferencia();
						r.setId((Integer) linha[i++]);
						r.setDescricao((String) linha[i++]);
						r.setUrl((String) linha[i++]);
						r.setTipoIndicacao((Integer)linha[i++]);
						r.setTipo((Character)linha[i++]);
						
						if (linha[i] != null) {
							TopicoAula a = new TopicoAula();
							a.setId((Integer)linha[i++]);
							r.setAula(a);
						}
						else {
							i++;
						}
						
						MaterialTurma m = new MaterialTurma();
						m.setId((Integer)linha[i++]);
						m.setOrdem((Integer) linha[i++]);
						m.setNivel((Integer) linha[i++]);
						
						r.setMaterial(m);
						
						// para mostrar que foi gerado a partir de um Título do acervo da biblioteca e pode visualizar os materiais existentes
						Integer idTitulo = (Integer) linha[i++];
						if ( idTitulo != null )
							r.setTituloCatalografico( new TituloCatalografico(idTitulo)); 
						
						referencias.add(r);
					}
										
				}
			}
			return referencias;
			
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
		
	}

	/**
	 * Retorna as aulas extras de determinada turma
	 * @param turma
	 * @return
	 */
	public List<AulaExtra> buscarAulasExtra(Turma turma) {
		DetachedCriteria c = DetachedCriteria.forClass(AulaExtra.class);
		c.add(eq("turma.id", turma.getId()));
		c.add(Restrictions.ne("tipo", AulaExtra.ENSINO_INDIVIDUAL)).addOrder(asc("dataAula"));
		
		@SuppressWarnings("unchecked")
		List<AulaExtra> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}

	/**
	 * Retorna as aulas extras de determinada turma e por data especificada
	 * @param turma
	 * @param dataSelecionada
	 * @return
	 */
	public List<AulaExtra> buscarAulasExtra(Turma turma, Date dataSelecionada) {
		DetachedCriteria c = DetachedCriteria.forClass(AulaExtra.class);
		c.add(eq("turma.id", turma.getId()));
		c.add(eq("dataAula", dataSelecionada));
		c.add(Restrictions.ne("tipo", AulaExtra.ENSINO_INDIVIDUAL));
		c.addOrder(asc("dataAula"));
		
		@SuppressWarnings("unchecked")
		List<AulaExtra> lista = getHibernateTemplate().findByCriteria(c);
		return lista;
	}

	/**
	 * Retorna as configurações que determinada turma possui 
	 * @param turma
	 * @return
	 */
	public ConfiguracoesAva findConfiguracoes(Turma turma) {
		try {
			Criteria c = getSession().createCriteria(ConfiguracoesAva.class);
			// Se a turma passada for uma subturma, busca pelas configurações da turma principal.
			c.add(eq("turma.id", turma.getTurmaAgrupadora() != null ? turma.getTurmaAgrupadora().getId() : turma.getId()));
			
			@SuppressWarnings("unchecked")
			List<ConfiguracoesAva> configuracoes = c.list();
			if (!isEmpty(configuracoes))
				return configuracoes.get(0);
			else
				return null;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Retorna o plano de ensino de determinada turma 
	 * @param turma
	 * @return
	 */
	public PlanoEnsino buscarPlanoEnsino(Turma turma) {
		DetachedCriteria c = DetachedCriteria.forClass(PlanoEnsino.class);
		c.add(eq("turma.id", turma.getId()));
		return (PlanoEnsino) getHibernateTemplate().uniqueResult(c);
	}

	/**
	 * Monta um mapa dos tópicos de aula e cada um dos materiais.
	 * 
	 * Substituído por: 
	 * MaterialTurmaDao.findMateriaisByTurma(turma)
	 * 
	 * @param turma
	 * @return
	 * @throws DAOException 
	 */
	public Map<TopicoAula, List<AbstractMaterialTurma>> findMateriaisByTurma(Turma turma) throws DAOException {
		Map<TopicoAula, List<AbstractMaterialTurma>> result = new HashMap<TopicoAula, List<AbstractMaterialTurma>>();

		List<IndicacaoReferencia> referencias = findReferenciasTurma(turma);
		List<ArquivoTurma> arquivos = findArquivosByTurma(turma.getId());
		List<ConteudoTurma> conteudos = findConteudoTurma(turma);
		List<TarefaTurma> tarefas = findTarefasTurma(turma);
		List<QuestionarioTurma> questionarios = findQuestionariosTurma(turma);
		List<VideoTurma> videos = findVideosTurma(turma);
		List<RotuloTurma> rotulos = findRotulosByTurma(turma);
		List<ForumTurma> foruns = findForunsByTurma(turma);
		List<Enquete> enquetes = findEnquetesByTurma(turma);
		List<ChatTurma> chats = findChatsByTurma(turma);
		

		for (IndicacaoReferencia ref : referencias) {
			if (ref.getAula() != null) {
				if (result.get(ref.getAula()) == null)
					result.put(ref.getAula(), new ArrayList<AbstractMaterialTurma>());
				result.get(ref.getAula()).add(ref);
			}
		}

		for (ArquivoTurma arq : arquivos) {
			if (arq.getAula() != null) {
				if (result.get(arq.getAula()) == null)
					result.put(arq.getAula(), new ArrayList<AbstractMaterialTurma>());
				result.get(arq.getAula()).add(arq);
			}
		}

		for (ConteudoTurma con : conteudos) {
			if (con.getAula() != null) {
				if (result.get(con.getAula()) == null)
					result.put(con.getAula(), new ArrayList<AbstractMaterialTurma>());
				result.get(con.getAula()).add(con);
			}
		}

		for (TarefaTurma tar : tarefas) {
			if (tar.getAula() != null) {
				TopicoAula t = new TopicoAula(tar.getAula().getId());
				if (result.get(t) == null)
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				result.get(t).add(tar);
			}
		}
		
		for (QuestionarioTurma q : questionarios) {
			if (q.getAula() != null) {
				TopicoAula t = new TopicoAula(q.getAula().getId());
				if (result.get(t) == null)
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				result.get(t).add(q);
			}
		}
		
		for (VideoTurma v : videos) {
			if (v.getTopicoAula() != null) {
				TopicoAula t = new TopicoAula(v.getTopicoAula().getId());
				if (result.get(t) == null)
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				result.get(t).add(v);
				
				// Verifica se o vídeo deve ser convertido e está a muito tempo sem converter
				if (v.isConverter() && v.getIdArquivoConvertido() == null && CalendarUtils.calculaMinutos(v.getDataUltimaTentativaConversao(), new Date()) > 60){
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					EnvioArquivoHelper.recuperaArquivo(baos, v.getIdArquivo());
					byte [] bytes = baos.toByteArray();
				
					if (bytes != null && bytes.length > 0){
						ConfiguracoesVideoTurma videoConfig = MimeTypeVideoUtil.getConfiguracoesVideoByMimeType(bytes);
						ConverterVideoThread cThread = new ConverterVideoThread(v, videoConfig, bytes, ParametroHelper.getInstance().getParametro(ParametrosTurmaVirtual.ENDERECO_VIDEO_CONVERTER));

						cThread.start();
						v.setDataUltimaTentativaConversao(new Date());
						updateField(VideoTurma.class, v.getId(), "dataUltimaTentativaConversao", v.getDataUltimaTentativaConversao());
					}
				}
			}
		}

		for (RotuloTurma r : rotulos) {
			if (r.getAula() != null) {
				TopicoAula t = new TopicoAula(r.getAula().getId());
				if (result.get(t) == null)
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				result.get(t).add(r);
			}
		}
		
		for (ForumTurma f : foruns){
			if (f.getTopicoAula() != null){
				TopicoAula t = new TopicoAula(f.getTopicoAula().getId());
				if (result.get(t) == null) {
					result.put(t, new ArrayList <AbstractMaterialTurma> ());
				}						
				result.get(t).add(f);
			}
		}

		for (Enquete e : enquetes) {
			if (e.getAula() != null) {
				TopicoAula t = new TopicoAula(e.getAula().getId());
				if (result.get(t) == null){
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				}
				result.get(e.getAula()).add(e);
			}
		}

		for (ChatTurma ct : chats) {
			if (ct.getAula() != null) {
				TopicoAula t = new TopicoAula(ct.getAula().getId());
				if (result.get(t) == null){
					result.put(t, new ArrayList<AbstractMaterialTurma>());
				}
				result.get(ct.getAula()).add(ct);
			}
		}

		
		return result;
	}

	/**
	 * Retorna as tarefas de determinada turma
	 * @param turma
	 * @return
	 */
	private List<TarefaTurma> findTarefasTurma(Turma turma) {
		try {
			
			String projecao = " id, titulo, possuiNota, dataInicio, horaInicio, minutoInicio, dataEntrega, horaEntrega, minutoEntrega, aula.id, material.id, material.ordem, material.nivel ";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM TarefaTurma t WHERE t.ativo = trueValue() AND t.aula.turma.id = :idTurma ORDER BY t.material.ordem");
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<TarefaTurma> lista = (List<TarefaTurma>) HibernateUtils.parseTo(q.list(), projecao, TarefaTurma.class);
			
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Retorna os questionários de determinada turma
	 * @param turma
	 * @return
	 */
	private List<QuestionarioTurma> findQuestionariosTurma(Turma turma) {
		try {
			String projecao = "id, titulo, inicio, fim, minutoInicio, horaInicio, minutoFim, horaFim, aula.id, finalizado, " +
					"dataCadastro, material.id, material.ordem, material.nivel";
			String hql = "select " + projecao + " from QuestionarioTurma where ativo = trueValue() and turma.id = " + turma.getId() + " order by material.ordem";
			
			@SuppressWarnings("unchecked")
			List<QuestionarioTurma> rs = (List<QuestionarioTurma>) HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, QuestionarioTurma.class);
			
			return rs;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Retorna os vídeos de determinada turma
	 * @param turma
	 * @return
	 */
	public List<VideoTurma> findVideosTurma(Turma turma) {
		try {
			String projecao = "id, titulo, descricao, idArquivo, idArquivoConvertido, converter, dataUltimaTentativaConversao, contentType, topicoAula.id, link, altura, telaCheia, data, " +
					"material.id, material.ordem, material.nivel, erro, mensagemConversao ";
			String hql = "select " + projecao + " from VideoTurma where ativo = trueValue() and turma.id = " + turma.getId() + " order by material.ordem";
			
			@SuppressWarnings("unchecked")
			List<VideoTurma> rs = (List<VideoTurma>) HibernateUtils.parseTo(getSession().createQuery(hql.toString()).list(), projecao, VideoTurma.class);
			
			return rs;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	/**
	 * Verifica se o Discente informado faz parte da turma que é passada como parâmetro 
	 * @param discenteAtivo
	 * @param turma
	 * @return
	 */
	public boolean isDiscenteTurma(Discente discenteAtivo, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.matricula_componente where id_discente=? and id_turma=? and id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAcessoTurmaVirtual()),
				new Object[] { discenteAtivo.getId(), turma.getId() }) > 0;
	}

	/**
	 * Verifica se o Docente informado faz parte da turma que é passada como parâmetro
	 * @param servidor
	 * @param turma
	 * @return
	 */
	public boolean isDocenteTurma(Servidor servidor, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.docente_turma  where id_docente=? and id_turma=?",
				new Object[] { servidor.getId(), turma.getId() }) > 0;
	}

	/**
	 * Verifica se o DocenteExterno informado faz parte da turma que é passada como parâmetro  
	 * @param docente
	 * @param turma
	 * @return
	 */
	public boolean isDocenteExternoTurma(DocenteExterno docente, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.docente_turma  where id_docente_externo=? and id_turma=?",
				new Object[] { docente.getId(), turma.getId() }) > 0;
	}

	/**
	 * Indica se um servidor passado como parâmetro é docente de alguma sub-turma da turma
	 * passada como parâmetro.
	 * @param servidor
	 * @param turma
	 * @return
	 */
	public boolean isDocenteSubTurma(Servidor servidor, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.docente_turma dt where dt.id_docente=? and dt.id_turma in (select id_turma from ensino.turma where id_turma_agrupadora = ?)",
				new Object[] { servidor.getId(), turma.getId() }) > 0;
	}

	/**
	 * Indica se um docente externo passado como parâmetro é docente de alguma sub-turma da turma
	 * passada como parâmetro.
	 * @param docenteExterno
	 * @param turma
	 * @return
	 */
	public boolean isDocenteExternoSubTurma(DocenteExterno docenteExterno, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.docente_turma dt where dt.id_docente_externo=? and dt.id_turma in (select id_turma from ensino.turma where id_turma_agrupadora = ?)",
				new Object[] { docenteExterno.getId(), turma.getId() }) > 0;

	}

	/**
	 * Indica se o discente passado como parâmetro está matriculado em alguma sub-turma da turma
	 * passada como parâmetro.
	 * @param discenteAtivo
	 * @param turma
	 * @return
	 */
	public boolean isDiscenteSubTurma(Discente discenteAtivo, Turma turma) {
		return getJdbcTemplate().queryForInt(
				"select count(*) from ensino.matricula_componente where id_discente=? and id_turma in (select id_turma from ensino.turma where id_turma_agrupadora = ?) and id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAcessoTurmaVirtual()),
				new Object[] { discenteAtivo.getId(), turma.getId() }) > 0;
	}

	/**
	 * Retorna a lista de aulas extra das sub-turmas de uma turma.
	 * @param turma
	 * @return
	 */
	public List<AulaExtra> buscarAulasExtraSubTurmas(Turma turma) {
		@SuppressWarnings("unchecked")
		List<AulaExtra> lista = getHibernateTemplate().find("from AulaExtra a where a.turma.id in " + UFRNUtils.gerarStringIn(turma.getSubturmas()) + " and a.tipo <> "+AulaExtra.ENSINO_INDIVIDUAL+" order by dataAula asc");
		return lista;
	}

	/**
	 * Retorna a data em que um discente trancou a matrícula em uma turma.
	 * @param discente
	 * @param turma
	 * @return
	 */
	public Date findDataTrancamento(int discente, int turma) {
		String sql = 
			" select alt.data_alteracao from ensino.solicitacao_trancamento_matricula sol " +
			" inner join ensino.alteracao_matricula alt on alt.id_alteracao_matricula = sol.id_alteracao_matricula " +
			" inner join ensino.matricula_componente matc on matc.id_matricula_componente = sol.id_matricula_componente " +
			" inner join discente d on d.id_discente = matc.id_discente " + 
			" where d.id_discente = ? and id_situacao_nova = " + SituacaoMatricula.TRANCADO.getId() + " and matc.id_turma = ?"; 

		try {
			return (Date) getJdbcTemplate().queryForObject(sql, new Object[] { discente, turma }, new RowMapper() {
				public Object mapRow(ResultSet rs, int arg1) throws SQLException {
					Date d = rs.getDate("data_alteracao");
					return d;
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
			return new Date();
		}
	}

	/**
	 * Retorna os presentes do dia até o momento 
	 * 
	 */
	public List<br.ufrn.integracao.dto.DiscenteDTO> findUltimasChamadas(int idTurma) {
		String sql = 
			" select d.matricula, p.nome, f.hora_freq_digital, frequencia from ensino.frequencia_aluno f " +
			" inner join discente d on d.id_discente = f.id_discente " +
			" inner join comum.pessoa p on p.id_pessoa = d.id_pessoa " +
			" where id_turma = ? and tipo_captacao_frequencia = 'D' and f.data = ? and f.ativo = trueValue() ";

		@SuppressWarnings("unchecked")
		List<DiscenteDTO> result = getJdbcTemplate().query(sql, new Object[] { idTurma, new Date() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int arg1) throws SQLException {
				DiscenteDTO discenteDTO = new DiscenteDTO();
				discenteDTO.setMatricula( rs.getLong("matricula") );
				discenteDTO.setNome( rs.getString("nome") );
				discenteDTO.setHoraPresencaDigital(rs.getTime("hora_freq_digital"));
				discenteDTO.setFrequencia(rs.getShort("frequencia"));
				return discenteDTO;
			}
		});
		
		return result;

	}

	/**
	 * Localiza o ID da Turma de acordo com o Código da Turma e o Código do Componente 
	 * 
	 * @param codigoTurma
	 * @param codigoComponente
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Turma findTurmaAnoPeriodoAtual(String codigoTurma, String codigoComponente, int ano, int periodo) throws HibernateException, DAOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select turma from Turma turma " +
				  " inner join turma.disciplina cc " +
				  " where turma.codigo = :codigoTurma and cc.codigo = :codigoComponente " +
				  " and turma.ano = :ano and turma.periodo = :periodo");
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("codigoTurma", codigoTurma);
		query.setString("codigoComponente", codigoComponente);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);  
		
		Turma turma = (Turma) query.uniqueResult();
		if (turma != null)
			return turma;
		else
			return new Turma();
	}

	/**
	 * Verifica se existe uma estação biométrica cadastrada de acordo com o ID da Turma
	 * @param turma
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean findEstacaoChamadaBiometricaByTurma(Turma turma) throws HibernateException, DAOException {
		String hql = "select estacaoTurma from EstacaoChamadaBiometricaTurma estacaoTurma where estacaoTurma.turma.id = ?";

		Query query = getSession().createQuery(hql);
		query.setInteger(0, turma.getId());
		
		EstacaoChamadaBiometricaTurma estacaoTurma = (EstacaoChamadaBiometricaTurma) query.uniqueResult();
		
		if (estacaoTurma != null)
			return true;
		else
			return false;
	}

	/**
	 * Retorna a turma que está vinculada a determinada estação biométrica
	 * 
	 * @param codigoTurma
	 * @param codigoComponente
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Turma findTurmaEstacaoBiometrica(String codigoTurma, String codigoComponente, int ano, int periodo) throws HibernateException, DAOException {
		
		StringBuilder sb = new StringBuilder();
		sb.append(" select estacaoTurma " +
				  " from EstacaoChamadaBiometricaTurma estacaoTurma " +
				  " inner join estacaoTurma.turma t " +
				  " inner join t.disciplina cc " +
				  " where t.codigo = :codigoTurma and cc.codigo = :codigoComponente " +
				  " and t.ano = :ano and t.periodo = :periodo");
		
		Query query = getSession().createQuery(sb.toString());
		query.setString("codigoTurma", codigoTurma);
		query.setString("codigoComponente", codigoComponente);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		
		EstacaoChamadaBiometricaTurma estacaoTurma = (EstacaoChamadaBiometricaTurma) query.uniqueResult();
		if (estacaoTurma != null)
			return estacaoTurma.getTurma();
		else
			return new Turma();
	} 

	/**
	 * Autentica se a senha enviada é a mesma cadastrada pelo Docente na Turma Virtual  
	 * 
	 * @param idTurma
	 * @param senha
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Turma autenticaSenhaChamadaBiometrica(String codigoTurma, String codigoComponente, int ano, int periodo, String senhaChamadaBio) throws HibernateException, DAOException {
		
		Query query = getSession().createSQLQuery("select cc.codigo, ano, periodo, t.codigo, conf.senha_chamada_bio, t.id_turma " +
									" from ensino.turma t " +
									" inner join ensino.componente_curricular cc on cc.id_disciplina = t.id_disciplina " +
									" inner join ava.configuracoes_ava conf on conf.id_turma = t.id_turma " +
									" where t.codigo = :codigoTurma and cc.codigo = :codigoComponente " +
									" and t.ano = :ano and t.periodo = :periodo " +
									" and conf.senha_chamada_bio = :senhaChamadaBio");
		
		query.setString("codigoTurma", codigoTurma);
		query.setString("codigoComponente", codigoComponente);
		query.setInteger("ano", ano);
		query.setInteger("periodo", periodo);
		query.setString("senhaChamadaBio", senhaChamadaBio);
		
		Object[] bulk = (Object[]) query.uniqueResult();
		if (bulk != null) {
			ComponenteCurricular disciplina = new ComponenteCurricular();
			disciplina.setCodigo((String)bulk[0]);
			
			Turma turma = new Turma();
			turma.setId((Integer)bulk[5]);
			turma.setAno((Integer)bulk[1]);
			turma.setPeriodo((Integer)bulk[2]);
			turma.setCodigo((String)bulk[3]);
			
			turma.setDisciplina(disciplina);
			return turma;
		}
		else
			return new Turma();
	}
	
	/**
	 * Autentica se a senha da estação biométrica é válida, autorizando ou não o usuário do app desktop cadastrar uma nova turma
	 * 
	 * @param idTurma
	 * @param senha
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public boolean verificarSenhaEstacaoBiometrica(int idTurma, String senha) throws HibernateException, DAOException {
		String hql = " select estacaoTurma from EstacaoChamadaBiometricaTurma estacaoTurma " +
				 	 " inner join estacaoTurma.turma t " +
				 	 " where t.id = ? and estacaoTurma.estacaoChamadaBiometrica.senhaEstacaoBio = ?";
		
		EstacaoChamadaBiometricaTurma e = (EstacaoChamadaBiometricaTurma) getSession().createQuery(hql).setInteger(0, idTurma).setString(1, senha).uniqueResult();
		if (e != null)
			return true;
		else
			return false;
	}

	/**
	 * Método que realiza a consulta SQL para um relatório, e retorna uma Lista
	 * das linhas da consulta
	 *
	 * @param consultaSql
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 * @throws SQLException
	 */
	public List<Map<String, Object>> executeSql(String consultaSql)
	throws SQLException, HibernateException, DAOException {

		@SuppressWarnings("unchecked")
		List<Map<String, Object>>lista = getJdbcTemplate().queryForList(consultaSql);
		return lista;
	}

	/**
	 * Gera um relatório qualitativo, dos docentes que não usam a turma virtual.  
	 * 
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioDocenteNaoUsaTurmaVirtual(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select s.id_servidor, p.nome, u.nome as departamento from rh.servidor s join comum.pessoa p on s.id_pessoa = p.id_pessoa " +
		" join comum.unidade u on s.id_unidade = u.id_unidade join ensino.docente_turma dt on dt.id_docente = s.id_servidor"+
		" join ensino.turma t on dt.id_turma = t.id_turma"+
		" where t.ano = "+ano+" and periodo = "+periodo+" and s.id_categoria = 1"+
		" and s.id_servidor not in ("+
		" select s.id_servidor"+
		" from ava.topico_aula ta"+
		" join comum.usuario u on ta.id_usuario = u.id_usuario"+
		" join rh.servidor s on u.id_servidor = s.id_servidor"+
		" join comum.unidade un on s.id_unidade = un.id_unidade"+
		" join ensino.turma t on ta.id_turma = t.id_turma"+
		" where t.ano = "+ano+" and periodo = "+periodo+""+ " and ta.ativo = trueValue() " +
		" group by s.id_servidor"+
		" having count(ta.id_topico_aula) > 3)"+
		" group by p.nome, u.nome, s.id_servidor"+
		" order by u.nome, p.nome ";

		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = executeSql(sqlconsulta);
		} catch (HibernateException e) {

			e.printStackTrace();
		} catch (DAOException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}

	/**
	 * Gera um relatório quantitativo do uso da turma virtual de acordo com  
	 * ano e período passado como parâmetro.
	 * 
	 * @param ano
	 * @param periodo
	 * @return
	 * @throws DAOException
	 */
	public List<Map<String,Object>> relatorioUsoTurmaVirtual(Integer ano, Integer periodo) throws DAOException {
		String sqlconsulta = "select p.nome, un.nome as departamento, un2.nome as centro, count(ta.id_topico_aula) as topico_aulas, count( distinct ta.id_turma ) as turmas,"+
		" (select count(id_arquivo_turma) from ava.arquivo_turma arq join ava.topico_aula ta2 on arq.id_aula= ta2.id_topico_aula"+
		" where arq.id_usuario = u.id_usuario and ta2.ativo = trueValue() and ta2.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as arquivos,"+
		" (select count(distinct data) from ensino.frequencia_aluno freq join ensino.turma t on freq.id_turma = t.id_turma"+
		" join ensino.docente_turma dt on t.id_turma = dt.id_turma and dt.id_docente = s.id_servidor"+
		" where ano = "+ano+ " and periodo = "+periodo+" and freq.ativo = trueValue()) as frequencia,"+
		" (select count(id_conteudo_turma) from ava.conteudo arq join ava.topico_aula ta3 on arq.id_topico_aula= ta3.id_topico_aula"+
		" where arq.id_usuario_cadastro = u.id_usuario and ta3.ativo = trueValue() and ta3.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as conteudos,"+
		" (select count(id_tarefa_turma) from ava.tarefa t join ava.atividade_avaliavel a on a.id_atividade_avaliavel = t.id_tarefa_turma join ava.topico_aula ta4 on a.id_topico_aula = ta4.id_topico_aula left join comum.registro_entrada r on r.id_entrada = a.id_registro_cadastro"+
		" where (r.id_usuario = u.id_usuario or t.id_usuario_cadastro = u.id_usuario) and ta4.ativo = trueValue() and a.ativo = trueValue() and ta4.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as tarefas,"+
		" (select count(id_enquete) from ava.enquete e where id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+")"+
		" and e.id_usuario = u.id_usuario) as enquetes,"+ 
		" (select count(id_noticia_turma) from ava.noticia_turma nt where id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+")"+
		" and nt.id_usuario_cadastro = u.id_usuario ) as noticias,"+
		" ( select count(id_forum_mensagem) from ava.forum_mensagem fm join ava.forum f on f.id_forum = fm.id_forum left join ava.forum_turma ft on ft.id_forum_turma = f.id_forum join ensino.turma t on (f.id_turma = t.id_turma or ft.id_turma = t.id_turma)"+
		" join ensino.docente_turma dt on t.id_turma = dt.id_turma and dt.id_docente = s.id_servidor and fm.id_usuario = u.id_usuario and periodo = "+periodo+" and ano = "+ano+") as mensagens_forum,"+
		" (select count(id_questionario_turma) from ava.questionario_turma q join ava.atividade_avaliavel a on a.id_atividade_avaliavel = q.id_questionario_turma join ava.topico_aula ta5 on a.id_topico_aula = ta5.id_topico_aula join comum.registro_entrada r on r.id_entrada = a.id_registro_cadastro"+
		" where r.id_usuario = u.id_usuario and ta5.ativo = trueValue() and a.ativo = trueValue() and ta5.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as questionarios,"+
		" (select count(id_chat_turma) from ava.chat_turma c join ava.topico_aula ta6 on c.id_topico_aula = ta6.id_topico_aula"+
		" where c.id_usuario = u.id_usuario and ta6.ativo = trueValue() and c.ativo = trueValue() and ta6.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as chat,"+
		" (select count(id_video_turma) from ava.video_turma v join ava.topico_aula ta7 on v.id_topico_aula = ta7.id_topico_aula"+
		" where v.id_usuario = u.id_usuario and v.ativo = trueValue() and ta7.ativo = trueValue() and ta7.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as videos,"+
		" (select count(id_twitter_turma) from ava.turma_twitter tt join ensino.docente_turma dt on tt.id_turma = dt.id_turma and dt.id_docente = s.id_servidor where dt.id_turma in ( select id_turma from ensino.turma where ano = "+ano+" and periodo = "+periodo+") ) as twitter"+
		" from ava.topico_aula ta"+
		" join comum.usuario u on ta.id_usuario = u.id_usuario"+
		" join rh.servidor s on u.id_servidor = s.id_servidor"+
		" join comum.unidade un on s.id_unidade = un.id_unidade"+
		" join comum.unidade un2 on un2.id_unidade = un.unidade_responsavel"+
		" join comum.pessoa p on s.id_pessoa = p.id_pessoa"+
		" join ensino.turma t on ta.id_turma = t.id_turma"+
		" where t.ano = "+ano+" and periodo = "+periodo+""+ " and ta.ativo = trueValue() " +
		" group by p.nome, un.nome, s.id_servidor, u.id_usuario, un2.nome"+
		" order by un.nome, p.nome ";

		List<Map<String,Object>> result = new ArrayList<Map<String,Object>>();

		try {
			result = executeSql(sqlconsulta);
		} catch (HibernateException e) {

			e.printStackTrace();
		} catch (DAOException e) {

			e.printStackTrace();
		} catch (SQLException e) {

			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * Lista todos os rótulos da turma. 
	 * 
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private List<RotuloTurma> findRotulosByTurma(Turma turma) {
		try {
			String projecao = " id, descricao, visivel, dataCadastro, usuarioCadastro.id, aula.id, material.id, material.ordem, material.nivel ";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM RotuloTurma rt WHERE rt.ativo = trueValue() AND rt.aula.turma.id = :idTurma ORDER BY rt.material.ordem ");
			q.setInteger("idTurma", turma.getId());
			return (List<RotuloTurma>) HibernateUtils.parseTo(q.list(), projecao, RotuloTurma.class);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}

	
	/**
	 * Busca os fóruns da turma informada.
	 * 
	 * @param turma
	 * @return
	 */
	private List<ForumTurma> findForunsByTurma(final Turma turma) {
		String sql = "SELECT ft.id_forum_turma, f.id_forum, f.descricao, f.titulo, tf.id_tipo_forum, " +
				"tf.descricao as descricao_tipo, f.data_criacao, u.id_foto, "
			+ "p.nome, f.total_topicos, fm.id_ultima_mensagem, fm.data, u.id_usuario, "
			+ "t.id_turma, t.codigo, "
			+ "ta.id_topico_aula, ta.descricao as descricao_topico_aula, mt.id_material_turma, mt.ordem, mt.nivel  "
			+ "FROM ava.forum f " +
					"JOIN ava.forum_turma ft ON ft.id_forum = f.id_forum " +
					"JOIN ava.material_turma mt ON mt.id_material_turma = ft.id_material_turma " +
					"LEFT JOIN ava.topico_aula ta ON ta.id_topico_aula = ft.id_topico_aula " +
					"JOIN ensino.turma t ON t.id_turma = ft.id_turma " +
					"JOIN ava.tipo_forum tf ON tf.id_tipo_forum = f.id_tipo_forum " +
					"JOIN comum.usuario u ON u.id_usuario = f.id_usuario " +
					"JOIN comum.pessoa p ON p.id_pessoa = u.id_pessoa " +
					"LEFT JOIN ava.forum_mensagem fm ON fm.id_ultima_mensagem = fm.id_forum_mensagem "  
			+ "WHERE f.ativo = trueValue() AND (ft.ativo = trueValue()) AND (ft.id_turma = ?) "
			+ "ORDER BY mt.ordem ";
		
		@SuppressWarnings("unchecked")
		List<ForumTurma> lista =  getJdbcTemplate().query(sql, new Object[] { turma.getId() }, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				ForumTurma ft = new ForumTurma(rs.getInt("id_forum_turma"));
				ft.setTurma(turma);
				ft.getTopicoAula().setId(rs.getInt("id_topico_aula"));
				ft.getTopicoAula().setDescricao(rs.getString("descricao_topico_aula"));
				
				ForumGeral forum = new ForumGeral();
				forum.setId(rs.getInt("id_forum"));
				forum.setDescricao(rs.getString("descricao"));
				forum.setTitulo(rs.getString("titulo"));
				forum.setTipo(new TipoForum(rs.getInt("id_tipo_forum")));
				forum.getTipo().setDescricao(rs.getString("descricao_tipo"));
				forum.setDataCadastro(rs.getTimestamp("data_criacao"));
				forum.setUsuario(new Usuario(rs.getInt("id_usuario")));
				forum.getUsuario().setIdFoto(rs.getInt("id_foto"));
				forum.getUsuario().setPessoa(new Pessoa());
				forum.getUsuario().getPessoa().setNome(rs.getString("nome"));
				forum.setTotalTopicos(rs.getInt("total_topicos"));
				forum.setUltimaMensagem(new ForumGeralMensagem(rs.getInt("id_ultima_mensagem")));
				forum.getUltimaMensagem().setData(rs.getTimestamp("data"));				
				ft.setForum(forum);
				ft.setMaterial(new MaterialTurma());
				ft.getMaterial().setId(rs.getInt("id_material_turma"));
				ft.getMaterial().setOrdem(rs.getInt("ordem"));
				ft.getMaterial().setNivel(rs.getInt("nivel"));
				return ft;
			}
		});
		return lista;
	}

	
	/**
	 * Busca lista de enquetes por turma informada.
	 *
	 * @throws  
	 * @throws DAOException 
	 */
	private List<Enquete> findEnquetesByTurma(Turma turma)  {
		try {
			String projecao = "id, pergunta, aula.id, material.id, material.ordem, material.nivel ";
			Query q = getSession().createQuery(
						"SELECT " + projecao + " FROM Enquete e WHERE e.aula.turma.id = :idTurma ORDER BY e.material.ordem");
			q.setInteger("idTurma", turma.getId());
			
			@SuppressWarnings("unchecked")
			List<Enquete> lista = (List<Enquete>) HibernateUtils.parseTo(q.list(), projecao, Enquete.class);
			
			return lista;
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	

	/**
	 * Busca lista de chats por turma informada.
	 *
	 * @throws  
	 * @throws DAOException 
	 */
	@SuppressWarnings("unchecked")
	private List<ChatTurma> findChatsByTurma(Turma turma) throws DAOException  {
		try {
			String projecao = " id, descricao, titulo, dataCadastro, dataInicio, dataFim, horaInicio, horaFim, " +
					"usuario.id, aula.id, videoChat, material.id, material.ordem, material.nivel, usuario.pessoa.nome ";
			Query q = getSession().createQuery(
					"SELECT " + projecao + " FROM ChatTurma ct WHERE ct.ativo = trueValue() AND ct.turma.id = :idTurma ORDER BY ct.material.ordem ");
			q.setInteger("idTurma", turma.getId());
			return (List<ChatTurma>) HibernateUtils.parseTo(q.list(), projecao, ChatTurma.class);
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}
	}
	
	/**
	 * Retorna os discentes que são monitores de uma turma da disciplina passada.
	 * 
	 * @param idDisciplina
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public List <Discente> findMonitores (int idDisciplina) throws HibernateException, DAOException {
		Query q = getSession().createSQLQuery ("select d.id_discente , d.matricula , d.id_foto , c.id_curso , c.nome as cnome, u.login , p.id_pessoa , p.nome , p.email" +
				" from comum.pessoa p "+
				"join comum.usuario u using (id_pessoa) "+
				"join discente d using (id_pessoa) "+
				"join curso c using (id_curso) " +
				"join monitoria.discente_monitoria dm using (id_discente) "+
				"join monitoria.prova_selecao_componente_curricular pscc using (id_prova_selecao) "+
				"join monitoria.componente_curricular_monitoria ccm using (id_componente_curricular_monitoria) "+
				"where dm.id_situacao_discente_monitoria = :situacaoDiscenteMonitoria and dm.ativo = trueValue() and ccm.id_disciplina = :idDisciplina");

		q.setInteger("situacaoDiscenteMonitoria", SituacaoDiscenteMonitoria.ASSUMIU_MONITORIA);
		q.setInteger("idDisciplina", idDisciplina);
		
		@SuppressWarnings("unchecked")
		List<Object[]> results = q.list();
		
		ArrayList<Discente> discentes = null;
		if ( results != null ) {
			discentes = new ArrayList<Discente>();
			
			for ( Object[] r : results ){
				if ( r != null )
				{	
					Integer i = 0;
					Discente d = new Discente();
					d.setId((Integer) r[i++]);
					d.setMatricula(((BigInteger) r[i++]).longValue());
					d.setIdFoto((Integer) r[i++]);
					
					Curso c = new Curso();
					c.setId((Integer) r[i++]);
					c.setNome((String) r[i++]);
					d.setCurso(c);

					Usuario u = new Usuario();
					u.setLogin((String) r[i++]);
					d.setUsuario(u);
					
					Pessoa p = new Pessoa();
					p.setId((Integer) r[i++]);
					p.setNome((String) r[i++]);
					p.setEmail((String) r[i++]);
					d.setPessoa(p);
					
					discentes.add(d);
				}
			}
		}
		
		return discentes;
	}
	
	/**
	 * Retorna os dados necessários para enviar notificações aos discentes ativos das turmas passadas.
	 * 
	 * @param idsTurmas
	 * @param situacoes
	 * @return
	 */
	public Collection<MatriculaComponente> findEmailsParticipantesTurmasCurso(Turma turma, Curso curso , SituacaoMatricula ... situacoes) {

		int[] statusPossiveis = new int[] { StatusDiscente.ATIVO, StatusDiscente.CADASTRADO, StatusDiscente.CONCLUIDO,
				StatusDiscente.CANCELADO, StatusDiscente.FORMANDO, StatusDiscente.GRADUANDO, StatusDiscente.TRANCADO, StatusDiscente.DEFENDIDO};
		
		String idCurso = "";
		if (curso != null)
			idCurso += "and d.id_curso = " + curso.getId() + " "; 
		
		String sql = "(select mc.id_matricula_componente, mc.id_situacao_matricula, u.email " +
				"from ensino.matricula_componente mc " +
				"join discente d using (id_discente) " +
				"join comum.usuario u using (id_pessoa) " +
				"join ensino.turma t using (id_turma) " +
				"left join ensino.turma ta on t.id_turma_agrupadora = ta.id_turma " +
				"where d.status in " + gerarStringIn(statusPossiveis) + " " +
				idCurso +
				"and (mc.id_turma = " + turma.getId() + ") " +
				"and mc.id_situacao_matricula in " + gerarStringIn(situacoes)+") " +
				"union " +
				"(select mc.id_matricula_componente, mc.id_situacao_matricula, u.email " +
				"from ensino.matricula_componente mc " +
				"join discente d using (id_discente) " +
				"join comum.usuario u using (id_pessoa) " +
				"join ensino.turma t using (id_turma) " +
				"left join ensino.turma ta on t.id_turma_agrupadora = ta.id_turma " +
				"where d.status in " + gerarStringIn(statusPossiveis) + " " +
				idCurso +
				"and (ta.id_turma = " + turma.getId() + ") " +
				"and mc.id_situacao_matricula in " + gerarStringIn(situacoes)+")";
		
		try {
			Query q = getSession().createSQLQuery(sql);
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();

			if (result != null && !result.isEmpty()){
				
				List <MatriculaComponente> matriculas = new ArrayList <MatriculaComponente> ();
				
				for (Object [] l : result){
					MatriculaComponente m = new MatriculaComponente ();
					m.setId((Integer) l[0]);
					
					m.setSituacaoMatricula(new SituacaoMatricula((Integer) l[1]));
					
					Usuario u = new Usuario ();
					u.setEmail((String) l[2]);
					m.setDiscente(new Discente());
					m.getDiscente().setUsuario(u);
					
					matriculas.add(m);
				}
				
				return matriculas;
			}
		} catch (DAOException e) {
			throw new TurmaVirtualException(e);
		}

		return null;
	}
	
	/**
	 * Retorna a sub-turma passado a turma agrupadora e um usuário discente matriculado na turma.
	 * Utilizado na correção de tarefas, para saber qual a subturma do usuário que enviou a resposta.
	 * 
	 * @param idsTurmas
	 * @param situacoes
	 * @return
	 */
	public Turma findSubturmaByUsuarioTurmaAgrupadora (Turma t, Usuario u) throws HibernateException, DAOException{
		
		Query q = getSession().createQuery(
				" SELECT t FROM MatriculaComponente m , Usuario u " +
				" LEFT JOIN m.turma t " +
				" LEFT JOIN m.discente d " +
				" WHERE d.pessoa.id = u.pessoa.id AND u.id = "+u.getId()+" AND t.turmaAgrupadora.id = " +t.getId());
		q.setMaxResults(1);
		return (Turma) q.uniqueResult();
		
	}
}