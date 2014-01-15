/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 23/01/2008
 * 
 */
package br.ufrn.sigaa.ava.dao;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.criterion.Restrictions;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.ava.dominio.GrupoDiscentes;
import br.ufrn.sigaa.ava.dominio.RespostaTarefaTurma;
import br.ufrn.sigaa.ava.dominio.TarefaTurma;
import br.ufrn.sigaa.ava.dominio.TopicoAula;
import br.ufrn.sigaa.dominio.Usuario;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteDetalhes;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.pessoa.dominio.Discente;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;

/**
 * DAO para buscas de tarefas em uma turma virtual.
 * 
 * @author David Pereira & Diego Jácome
 *
 */
public class TarefaTurmaDao extends GenericSigaaDAO {

	/**
	 * Retorna as Tarefas de uma turma, só as que não estão com o campo Ativo = false.
	 * @param turma isDocente
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List<TarefaTurma> findByTurma(Turma turma, boolean isDocente) throws HibernateException, DAOException {
		
		String projecao = " select  t.id_tarefa_turma, r.id , a.titulo , t.conteudo, "+ 
		" t.data_entrega , a.data_cadastro , t.data_inicio , t.hora_entrega , t.hora_inicio , t.minuto_entrega , t.minuto_inicio, t.id_arquivo, "+
		" a.possui_nota , a.nota_maxima , a.peso , a.unidade , a.abreviacao, "+ 
		" t.envio_arquivo , t.resposta_online , t.permite_novo_envio , t.emgrupo, "+ 
		" a.id_topico_aula , r.data_envio , r.id_usuario_envio , r.id_grupo_discentes, d.id_discente , d.matricula, r.data_correcao ";
			
		String sql = projecao +" from ava.tarefa as t "+ 
		" inner join ava.atividade_avaliavel as a on t.id_tarefa_turma = a.id_atividade_avaliavel "+ 
		" left join ava.topico_aula as ta on a.id_topico_aula = ta.id_topico_aula "+ 
		" left join ava.resposta_tarefa_turma as r on t.id_tarefa_turma = r.id_tarefa and r.ativo = trueValue() "+
		" left join ava.grupo_discentes_discente as g on r.id_grupo_discentes = g.id_grupo_discentes"+
		" left join discente as d on g.id_discente = d.id_discente "+ 
		" where ta.id_turma = "+turma.getId()+" and a.ativo = trueValue() "; 
		
		if (!isDocente) {
			sql += " and ta.visivel = trueValue() ";
		}
		
		
		sql +=  " order by t.id_tarefa_turma, r.id, r.id_grupo_discentes, g.id_discente;";
		
		Query  q = getSession().createSQLQuery(sql);
		
		List<TarefaTurma> tarefas = new ArrayList<TarefaTurma>();
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		if ( result != null ){
			 
			 Integer oldIdTarefa = null;
			 Integer oldIdResposta = null;
			 Integer newIdTarefa;
			 Integer newIdResposta;
			 Boolean isNovaTarefa;
			 Boolean isNovaResposta;
			 TarefaTurma t = null;
			 RespostaTarefaTurma r = null;
			 
			 for ( Object[] linha : result ) {
				 
				 Integer i = 0;				 
				 newIdTarefa = ( Integer ) linha[i++];
				 newIdResposta = ( Integer ) linha[i++];
				 
				 GrupoDiscentes g = null;
				 
				 if ( newIdTarefa.equals(oldIdTarefa) ) isNovaTarefa = false; else isNovaTarefa = true;
				 if ( newIdResposta != null && newIdResposta.equals(oldIdResposta) ) isNovaResposta = false; else isNovaResposta = true;
				 
				 if ( isNovaTarefa ) {
					 
					 t = new TarefaTurma();
					 
					 t.setId(newIdTarefa);
					 t.setTitulo( (String) linha[i++] );
					 t.setConteudo( (String) linha[i++] );
					 t.setDataEntrega( (Date) linha[i++] );
					 t.setDataCadastro( (Date) linha[i++] );
					 t.setDataInicio( (Date) linha[i++] );
					 t.setHoraEntrega( (Integer) linha[i++] );
					 t.setHoraInicio( (Integer) linha[i++] );
					 t.setMinutoEntrega( (Integer) linha[i++] );
					 t.setMinutoInicio( (Integer) linha[i++] );
					 t.setIdArquivo( (Integer) linha[i++] );
					 t.setPossuiNota( (Boolean) linha[i++] );
					 Number notaMaxima = (Number) linha[i++];
					 if ( notaMaxima != null )
						 t.setNotaMaxima( notaMaxima.doubleValue() );
					 else
						 t.setNotaMaxima(null);
					 t.setPeso( (Integer) linha[i++] );
					 t.setUnidade( (Integer) linha[i++] );
					 t.setAbreviacao( (String) linha[i++] );
					 t.setEnvioArquivo( (Boolean) linha[i++] );
					 t.setRespostaOnline( (Boolean) linha[i++] );
					 t.setPermiteNovoEnvio( (Boolean) linha[i++] ); 
					 t.setEmGrupo( (Boolean) linha[i++] );
					 
					 TopicoAula ta = new TopicoAula();
					 ta.setId( (Integer) linha[i++] );
					 t.setAula(ta);
					 
					 if ( newIdResposta != null ) {
						 r = new RespostaTarefaTurma();
						 r.setId(newIdResposta);
						 r.setDataEnvio( (Date) linha[i++] );
						 Usuario u = new Usuario();
						 u.setId( (Integer) linha[i++] );
						 r.setUsuarioEnvio(u);
						 Integer idGrupo = (Integer) linha[i++];
						 if ( idGrupo != null ) {
							 g = new GrupoDiscentes();
							 g.setId( idGrupo );
							 g.setDiscentes( new ArrayList<Discente>() );
							 Discente d = new Discente();
							 d.setId( (Integer) linha[i++] );
							 d.setMatricula( ((Number) linha[i++]).longValue() );
							 g.getDiscentes().add(d);
							 r.setGrupoDiscentes(g);
						 }
						 // ATENÇÃO
						 r.setDataCorrecao( (Date) linha[27] );
						 t.setRespostas(new ArrayList<RespostaTarefaTurma>());
						 t.getRespostas().add(r);
					 }
					 tarefas.add(t);
				 } else if ( isNovaResposta ) {
					 
					 r = new RespostaTarefaTurma();
					 r.setId(newIdResposta);
					 // Setando o index
					 i = 22;
					 r.setDataEnvio( (Date) linha[i++] );
					 Usuario u = new Usuario();
					 u.setId( (Integer) linha[i++] );
					 r.setUsuarioEnvio(u);
					 Integer idGrupo = (Integer) linha[i++];
					 if ( idGrupo != null ) {
						 g = new GrupoDiscentes();
						 g.setId( idGrupo );
						 g.setDiscentes( new ArrayList<Discente>() );
						 Discente d = new Discente();
						 d.setId( (Integer) linha[i++] );
						 d.setMatricula( ((Number) linha[i++]).longValue() );
						 g.getDiscentes().add(d);
						 r.setGrupoDiscentes(g);
					 }
					 // ATENÇÃO
					 r.setDataCorrecao( (Date) linha[27] );
					 if ( t != null && t.getRespostas() != null ) {
						 t.getRespostas().add(r);
					 }	 
				 }else {
					 // Setando o index
					 i = 25;
					 Discente d = new Discente();
					 d.setId( (Integer) linha[i++] );
					 d.setMatricula( ((Number) linha[i++]).longValue() );
					 if ( r != null && r.getGrupoDiscentes() != null && r.getGrupoDiscentes().getDiscentes() != null)
						 r.getGrupoDiscentes().getDiscentes().add(d);
				 }
				 
				 oldIdTarefa = newIdTarefa;
				 oldIdResposta = newIdResposta;
			 }
			 return tarefas;
		 }

		return null;
	}
	
	/**
	 * Busca uma Tarefa de uma turma pelo seu id.
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public TarefaTurma findTarefaTurma(int id) throws DAOException {
		try {
			Criteria c = getCriteria(TarefaTurma.class);
			c.add(Restrictions.eq("id", id));
			c.add(Restrictions.eq("ativo", true));
			return (TarefaTurma) c.uniqueResult();
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca a última resposta de uma tarefa que foi enviada por um aluno.
	 * @param tarefa
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings({ "unchecked", "cast" })
	public RespostaTarefaTurma findRespostaByTarefaAluno(TarefaTurma tarefa, Usuario usuario) throws DAOException {
		try {
			
		List<RespostaTarefaTurma> lista = new ArrayList<RespostaTarefaTurma>();
			
			String sql = "select r.id, r.comentarios, r.data_envio, r.data_correcao , r.lida, r.id_arquivo, r.texto_resposta , " +
						" r.texto_correcao , r.id_arquivo_correcao , r.nome_arquivo , r.nome_arquivo_correcao , r.id_grupo_discentes , r.id_usuario_envio " +
						" from ava.resposta_tarefa_turma as r " +
						" inner join ava.tarefa as t on t.id_tarefa_turma = r.id_tarefa " +
						" inner join ava.atividade_avaliavel as a on a.id_atividade_avaliavel = r.id_tarefa ";
			
			if ( tarefa.isEmGrupo() )
				sql += " left join ava.grupo_discentes_discente as gdd on gdd.id_grupo_discentes = r.id_grupo_discentes " + 
				" left join ava.grupo_discentes as gd on gd.id_grupo_discentes = gdd.id_grupo_discentes " +
				" left join discente as d on d.id_discente = gdd.id_discente " +
				" left join comum.usuario as u on u.id_pessoa = d.id_pessoa " +
				" where t.id_tarefa_turma = " +tarefa.getId()+ " and (u.id_usuario = " +usuario.getId()+ " or r.id_usuario_envio = " +usuario.getId()+ ") and a.ativo = true and r.ativo = true";
			else
				sql += " where t.id_tarefa_turma = " +tarefa.getId()+ " and r.id_usuario_envio = " +usuario.getId()+ " and a.ativo = true and r.ativo = true";
			
			Query  q = getSession().createSQLQuery(sql);
			List<Object[]> results = (List<Object[]> ) q.list();
						
			if ( results != null ) 
				for (Object[] result : results) {
					if ( result != null )
					{	
						Integer i = 0;
						RespostaTarefaTurma r = new RespostaTarefaTurma();
						r.setId((Integer) result[i++]);
						r.setComentarios((String) result[i++]);
						r.setDataEnvio((Date) result[i++]);
						r.setDataCorrecao((Date) result[i++]);
						r.setLida((Boolean) result[i++]);
						r.setIdArquivo((Integer) result[i++]);
						r.setTextoResposta((String) result[i++]);
						r.setTextoCorrecao((String) result[i++]);
						r.setIdArquivoCorrecao((Integer) result[i++]);
						r.setNomeArquivo((String) result[i++]);
						r.setNomeArquivoCorrecao((String) result[i++]);
						r.setTarefa(tarefa);
						Integer idGrupo = (Integer) result[i++];
						if ( idGrupo != null ){
							r.setGrupoDiscentes(new GrupoDiscentes());
							r.getGrupoDiscentes().setId(idGrupo);
						}
						r.setUsuarioEnvio(new Usuario());
						r.getUsuarioEnvio().setId((Integer) result[i++]);
						lista.add(r);
					}
				}		
				
			// Caso seja permitido novo envio e o aluno envie mais de uma respostas.
			RespostaTarefaTurma result = null;
			if ( lista != null && !lista.isEmpty() ){
				result = lista.get(0);
				for ( RespostaTarefaTurma r : lista )
					if ( r.getDataEnvio().after(result.getDataEnvio()) )
						result = r;
			}
			return result;
			
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}	
	
	/**
	 * Busca a resposta de uma tarefa que foi enviada por um grupo de discentes.
	 * @param tarefa
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public RespostaTarefaTurma findRespostaByTarefaGrupo (TarefaTurma tarefa, GrupoDiscentes grupo) throws DAOException {
	
		try{
			
			Query  q = getSession().createSQLQuery( "select r.id, r.comentarios, r.data_envio, r.data_correcao , r.lida, r.id_arquivo, r.nome_arquivo , r.texto_resposta ," +
													" r.texto_correcao , r.id_arquivo_correcao , r.nome_arquivo_correcao , r.id_grupo_discentes , r.id_usuario_envio from ava.resposta_tarefa_turma as r " +
													"inner join ava.grupo_discentes as g on g.id_grupo_discentes = r.id_grupo_discentes " +
													"inner join ava.tarefa as t on t.id_tarefa_turma = r.id_tarefa " +
													"where t.id_tarefa_turma = "+tarefa.getId()+" and g.id_grupo_discentes = "+grupo.getId()+" and r.ativo = true order by r.data_envio desc");		
			q.setMaxResults(1);
			
			Object[] result = (Object[]) q.uniqueResult();			
			
			if ( result != null )
			{	
				Integer i = 0;
				RespostaTarefaTurma r = new RespostaTarefaTurma();
				r.setId((Integer) result[i++]);
				r.setComentarios((String) result[i++]);
				r.setDataEnvio((Date) result[i++]);
				r.setDataCorrecao((Date) result[i++]);
				r.setLida((Boolean) result[i++]);
				r.setIdArquivo((Integer) result[i++]);
				r.setNomeArquivo((String) result[i++]);
				r.setTextoResposta((String) result[i++]);
				r.setTextoCorrecao((String) result[i++]);
				r.setIdArquivoCorrecao((Integer) result[i++]);
				r.setNomeArquivoCorrecao((String) result[i++]);
				r.setTarefa(tarefa);
				r.setGrupoDiscentes(new GrupoDiscentes());
				r.getGrupoDiscentes().setId((Integer) result[i++]);
				r.setUsuarioEnvio(new Usuario());
				r.getUsuarioEnvio().setId((Integer) result[i++]);
				return r;
			}
		}catch (Exception e) {
			throw new DAOException(e);
		}	
		return null;
	
	}
	
	/**
	 * Busca a resposta de uma tarefa que foi enviada por um grupo de discentes, verificando seus ancestrais.
	 * @param tarefa
	 * @param grupo
	 * @return
	 * @throws DAOException
	 */
	public RespostaTarefaTurma findRespostaByTarefaGrupoComAncestrais (TarefaTurma tarefa, GrupoDiscentes grupo) throws DAOException {
	
		try{
			
			String ids = "( " + grupo.getId();
			GrupoDiscentes pai = grupo.getGrupoPai();
			int j = 0;
			while (pai != null){
				ids+=" , " +pai.getId();
				pai = pai.getGrupoPai();
				j++;
			}	
			ids+= " )";
			
			Query  q = getSession().createSQLQuery( "select r.id, r.comentarios, r.data_envio, r.data_correcao , r.lida, r.id_arquivo, r.nome_arquivo , r.texto_resposta ," +
													" r.texto_correcao , r.id_arquivo_correcao , r.nome_arquivo_correcao , r.id_grupo_discentes , r.id_usuario_envio from ava.resposta_tarefa_turma as r " +
													"inner join ava.tarefa as t on t.id_tarefa_turma = r.id_tarefa " +
													"where t.id_tarefa_turma = "+tarefa.getId()+" and r.id_grupo_discentes in "+ids+" and r.ativo = trueValue() order by r.data_envio desc");		
			q.setMaxResults(1);
			
			Object[] result = (Object[]) q.uniqueResult();			
			
			if ( result != null )
			{	
				Integer i = 0;
				RespostaTarefaTurma r = new RespostaTarefaTurma();
				r.setId((Integer) result[i++]);
				r.setComentarios((String) result[i++]);
				r.setDataEnvio((Date) result[i++]);
				r.setDataCorrecao((Date) result[i++]);
				r.setLida((Boolean) result[i++]);
				r.setIdArquivo((Integer) result[i++]);
				r.setNomeArquivo((String) result[i++]);
				r.setTextoResposta((String) result[i++]);
				r.setTextoCorrecao((String) result[i++]);
				r.setIdArquivoCorrecao((Integer) result[i++]);
				r.setNomeArquivoCorrecao((String) result[i++]);
				r.setTarefa(tarefa);
				r.setGrupoDiscentes(new GrupoDiscentes());
				r.getGrupoDiscentes().setId((Integer) result[i++]);
				r.setUsuarioEnvio(new Usuario());
				r.getUsuarioEnvio().setId((Integer) result[i++]);
				return r;
			}
		}catch (Exception e) {
			throw new DAOException(e);
		}	
		return null;
	
	}
	
	/**
	 * Identifica se um aluno já respondeu ou não a uma tarefa
	 * @param tarefa
	 * @param usuario
	 * @return
	 * @throws DAOException
	 */
	public boolean findRespostaByAluno(Usuario usuario, TarefaTurma tarefa) throws DAOException {
		
		
		if (tarefa == null) return false;

		RespostaTarefaTurma resposta = findRespostaByTarefaAluno(tarefa, usuario);
		TarefaTurma tarefaTurma = findTarefaTurma(tarefa.getId());

		if (resposta != null && !tarefaTurma.isPermiteNovoEnvio())
			return true;
		else 
			return false;
	}

	
	/**
	 * Busca as respostas de todos os alunos para uma tarefa individual cadastrada
	 * para a turma virtual.
	 * @param tarefa
	 * @return
	 * @throws DAOException
	 */
	public List<RespostaTarefaTurma> findRespostasNaoAvaliadasByTarefa(TarefaTurma tarefa, Turma turma) throws DAOException {
		try {
		
			String idsTurmas = "";
			List<Integer> ids = new ArrayList<Integer>();
			
			if (turma.isAgrupadora())
				for (Turma t: turma.getSubturmas())
					ids.add(t.getId());
			else
				ids.add(turma.getId());
			
			idsTurmas = UFRNUtils.gerarStringIn(ids);
			
			Query  q = getSession().createSQLQuery("select r.id, r.comentarios, r.data_envio, r.lida, p.id_pessoa, p.nome, u.login, u.id_usuario , r.id_arquivo, r.texto_resposta, r.texto_correcao from ava.resposta_tarefa_turma as r "+
					" left join comum.usuario as u on r.id_usuario_envio = u.id_usuario " +
					" left join comum.pessoa as p on u.id_pessoa = p.id_pessoa " +
					" left join discente as d on d.id_pessoa = p.id_pessoa " +
					" left join ensino.matricula_componente as m on m.id_discente = d.id_discente and m.id_turma in " +idsTurmas+
					" where r.id_tarefa = " +tarefa.getId()+
					" and m.id_situacao_matricula in " + UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAcessoTurmaVirtual()) +
					" and r.id_grupo_discentes IS NULL and r.ativo = true " +
					" order by p.nome asc");

			List<RespostaTarefaTurma> respostas = new ArrayList<RespostaTarefaTurma>();
			
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
			
			if ( result != null )
			{	
				for (Object[] linha : result) {
					Integer i = 0;
					RespostaTarefaTurma r = new RespostaTarefaTurma();
					r.setId((Integer) linha[i++]);
					r.setComentarios((String) linha[i++]);
					r.setDataEnvio((Date) linha[i++]);
					r.setLida((Boolean) linha[i++]);
					r.setUsuarioEnvio(new Usuario());
					r.getUsuarioEnvio().setPessoa(new Pessoa());
					r.getUsuarioEnvio().getPessoa().setId(((Integer) linha[i++]));
					r.getUsuarioEnvio().getPessoa().setNome((String) linha[i++]);
					r.getUsuarioEnvio().setLogin((String) linha[i++]);
					r.getUsuarioEnvio().setId((Integer) linha[i++]);
					r.setIdArquivo((Integer) linha[i++]);
					r.setTextoResposta((String) linha[i++]);
					r.setTextoCorrecao((String) linha[i++]);
					r.setTarefa(tarefa);

					respostas.add(r);
				}
				
				return respostas;
			}	
		
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}
	
	/**
	 * Busca as respostas de todos os alunos para uma tarefa em grupo cadastrada
	 * para a turma virtual.
	 * @param tarefa
	 * @return
	 * @throws DAOException
	 */
	public List<RespostaTarefaTurma> findRespostasNaoAvaliadasByTarefaGrupoTurma(TarefaTurma tarefa, Turma t) throws DAOException {
		try {
			
			String idTurmas = "";
			if (t.isAgrupadora()){
				@SuppressWarnings("unchecked")
				List <Integer> ids = getSession().createSQLQuery ("select id_turma from ensino.turma where id_turma_agrupadora = " + t.getId()).list();
				for (Integer id : ids)
					idTurmas += (idTurmas.equals("") ? "" : ",") + id;
			}
			
			if (idTurmas.equals(""))
				idTurmas = ""+t.getId();
			
			idTurmas = "(" + idTurmas + ")";
			
			Query  q = getSession().createSQLQuery("select r.id , r.id_grupo_discentes as gd, d.id_discente as d, d.matricula as md, d.status, mc.id_situacao_matricula as mcs, " +
						"p.id_pessoa , p.nome as nome_grupo, p.email , " +
						" gd.ativo, gd.nome, " +
						"r.comentarios, r.data_envio, r.lida, u.id_pessoa as pe, u.login, u.id_usuario , r.id_arquivo, r.texto_resposta, r.texto_correcao, " +
						"gf.id_grupo_discentes as gdf, gdf.id_discente as df, df.matricula as mf, df.status as sf, mcf.id_situacao_matricula as mcsf " +
						"from ava.resposta_tarefa_turma as r " +
						"left join comum.usuario as u on r.id_usuario_envio = u.id_usuario " +
						"left join ava.grupo_discentes as gd on gd.id_grupo_discentes = r.id_grupo_discentes " +
						"left join ava.grupo_discentes_discente as gdd on gdd.id_grupo_discentes = r.id_grupo_discentes " +
						"left join discente as d on d.id_discente = gdd.id_discente " +
						"left join ensino.matricula_componente as mc on d.id_discente = mc.id_discente and mc.id_turma in " + idTurmas + " " +
						"left join comum.pessoa as p on d.id_pessoa = p.id_pessoa " +
						"left join ava.grupo_discentes as gf on gf.id_grupo_pai = gdd.id_grupo_discentes " +
						"left join ava.grupo_discentes_discente as gdf on gdf.id_grupo_discentes = gf.id_grupo_discentes " +
						"left join discente as df on df.id_discente = gdf.id_discente " +
						"left join ensino.matricula_componente as mcf on df.id_discente = mcf.id_discente and mcf.id_turma in " +  idTurmas  + " " +
						"where r.id_tarefa = "+tarefa.getId()+" and r.id_grupo_discentes IS NOT NULL and r.ativo = trueValue() " +
						"order by r.id , gdd.id_grupo_discentes , p.nome , gdf.id_discente asc ");
			
			List<RespostaTarefaTurma> respostas = new ArrayList<RespostaTarefaTurma>();
				
			@SuppressWarnings("unchecked")
			List<Object[]> result = q.list();
				
			GrupoDiscentes gd  = null;
			GrupoDiscentes gdFilho = null;
				
			Integer oldIdResposta = null;
			Integer newIdResposta = null;
			Integer oldIdGrupo = null;
			Integer newIdGrupo = null;
			Integer oldIdDiscente = null;
			Integer newIdDiscente = null;
				
			// Resultado da consulta retorna os dados da resposta da tarefa, o grupo com n-alunos que enviou a resposta e seu grupo-filho com n-alunos.				
			if ( result != null )
			{	
				for (Object[] linha : result) {
					
					Integer i = 0;
					RespostaTarefaTurma r = new RespostaTarefaTurma();
					
					newIdResposta = (Integer) linha[i++];
					newIdGrupo = (Integer) linha[i++];
					newIdDiscente = (Integer) linha[i++];
					// A linha possui uma nova resposta
					if ( !newIdResposta.equals(oldIdResposta) || !newIdGrupo.equals(oldIdGrupo) )
					{	
						Discente d = new Discente();
						d.setId(newIdDiscente);
						d.setMatricula( ((Number) linha[i++]).longValue() );
						d.setStatus((Short) linha[i++]);
						
						SituacaoMatricula sm = new SituacaoMatricula();
						sm.setId((Integer) linha[i++]);				
						if (SituacaoMatricula.TRANCADO.equals(sm) || d.isTrancado())
							d.setRemovidoGrupo(true);
						else
							d.setRemovidoGrupo(false);
						
						Pessoa p = new Pessoa();
						p.setId((Integer) linha[i++]);
						p.setNome((String) linha[i++]);
						p.setEmail((String) linha[i++]);
						d.setPessoa(p);
	
						gd = new GrupoDiscentes();
						gd.setId(newIdGrupo);
						gd.getDiscentes().add(d);
						gd.setAtivo((Boolean) linha[i++] );
						gd.setNome( (String) linha[i++] );
								
						r.setId(newIdResposta);
						r.setComentarios((String) linha[i++]);
						r.setDataEnvio((Date) linha[i++]);
						r.setLida((Boolean) linha[i++]);
						r.setUsuarioEnvio(new Usuario());
						r.getUsuarioEnvio().setPessoa(new Pessoa());
						r.getUsuarioEnvio().getPessoa().setId(((Integer) linha[i++]));
						r.getUsuarioEnvio().setLogin((String) linha[i++]);
						r.getUsuarioEnvio().setId((Integer) linha[i++]);
						r.setIdArquivo((Integer) linha[i++]);
						r.setTextoResposta((String) linha[i++]);
						r.setTextoCorrecao((String) linha[i++]);
						r.setTarefa(tarefa);
						r.setGrupoDiscentes(gd);					
						
						Integer idGrupoFilho = (Integer) linha[i++];
						if ( idGrupoFilho != null ) {
							gdFilho = new GrupoDiscentes();
							gdFilho.setId(idGrupoFilho);
						}
						Integer idDiscenteFilho = (Integer) linha[i++];	
						if ( gdFilho != null && idDiscenteFilho != null) {
							Discente dFilho = new Discente();
							dFilho.setId(idDiscenteFilho);
							dFilho.setMatricula(((Number) linha[i++]).longValue());
							dFilho.setStatus((Short) linha[i++]);
							
							SituacaoMatricula smFilho = new SituacaoMatricula();
							smFilho.setId((Integer) linha[i++]);						
							if (SituacaoMatricula.TRANCADO.equals(smFilho) || dFilho.isTrancado())
								dFilho.setRemovidoGrupo(true);
							else
								dFilho.setRemovidoGrupo(false);
							
							gdFilho.getDiscentes().add(dFilho);
							gd.setGrupoFilho(gdFilho);
						}
						
						respostas.add(r);
					} // A linha possui uma resposta antiga, mas um novo discente do grupo que respondeu a tarefa.
					else if ( newIdGrupo.equals(oldIdGrupo) && !newIdDiscente.equals(oldIdDiscente) ){
						
						Discente d = new Discente();
						d.setId(newIdDiscente);
						d.setMatricula( ((Number) linha[i++]).longValue() );
						d.setStatus((Short) linha[i++]);
						
						SituacaoMatricula sm = new SituacaoMatricula();
						sm.setId((Integer) linha[i++]);						
						if (SituacaoMatricula.TRANCADO.equals(sm) || d.isTrancado())
							d.setRemovidoGrupo(true);
						else
							d.setRemovidoGrupo(false);

						Pessoa p = new Pessoa();
						p.setId((Integer) linha[i++]);
						p.setNome((String) linha[i++]);
						p.setEmail((String) linha[i++]);
						d.setPessoa(p);
						
						if ( gd != null )
							gd.getDiscentes().add(d);
					} // A linha possui uma resposta e um discentes antigos, mas um novo discente do grupo-filho do grupo que respondeu a tarefa.
					else if ( newIdGrupo.equals(oldIdGrupo) && newIdDiscente.equals(oldIdDiscente) ) {
						// Pulando para penúltimo atributo
						i = 21;
						Integer idDiscenteFilho = (Integer) linha[i++];
						if ( gd != null && gd.getGrupoFilho() != null && idDiscenteFilho != null) {
							Discente dFilho = new Discente();
							dFilho.setId(idDiscenteFilho);
							dFilho.setMatricula(((Number) linha[i++]).longValue());
							dFilho.setStatus((Short) linha[i++]);
							
							SituacaoMatricula smFilho = new SituacaoMatricula();
							smFilho.setId((Integer) linha[i++]);
							if (SituacaoMatricula.TRANCADO.equals(smFilho) || dFilho.isTrancado())
								dFilho.setRemovidoGrupo(true);
							else
								dFilho.setRemovidoGrupo(false);
							
							if ( !gd.getGrupoFilho().getDiscentes().contains(dFilho) )
								gd.getGrupoFilho().getDiscentes().add(dFilho);
						}	
					}	
					
					oldIdResposta = newIdResposta;
					oldIdGrupo = newIdGrupo;
					oldIdDiscente = newIdDiscente;
				}
				
				return respostas;
			}	
			
			return null;
		} catch (Exception e) {
			throw new DAOException(e);
		}
	}

	/**
	 * Remove uma resposta de uma tarefa pelo seu id.
	 * @param idTarefa
	 */
	 public void removerRespostaByTarefaAluno(int idTarefa) {
		update("update ava.resposta_tarefa_turma set ativo = falseValue() where id = ?", idTarefa);
	}

	/**
	 * Atualiza uma tarefa, marcando-a como lida.
	 * @param resposta
	 */
	public void marcarTarefaLida(RespostaTarefaTurma resposta) {
		update("update ava.resposta_tarefa_turma set lida = not lida where id = ? and ativo = trueValue()", new Object[] { resposta.getId() });
	}

	
	/**
	 * Retorna uma coleção de datas de tarefas cadastradas dentro de um número de dias especificados.
     *
	 * @param dias
	 * @param discente
	 * @param ano
	 * @param semestre
	 * @return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public List <TarefaTurma> findTarefasData (int diasInicio, int diasFim, Discente discente, Usuario usuario, int ano, int semestre) throws HibernateException, DAOException{
		
		Query  q = getSession().createSQLQuery(
				"select tt.id_tarefa_turma , a.titulo , tt.conteudo , tt.data_entrega , r.id , t.id_turma , cd.nome , tt.hora_entrega , tt.minuto_entrega from ava.tarefa tt " +
				"inner join ava.atividade_avaliavel a on a.id_atividade_avaliavel = tt.id_tarefa_turma " +
				"left join ava.resposta_tarefa_turma r on r.id_tarefa = tt.id_tarefa_turma and r.ativo = trueValue() and r.id_usuario_envio = "+usuario.getId()+ " " +
				"inner join ava.topico_aula ta on ta.id_topico_aula = a.id_topico_aula " +
				"inner join ensino.turma t on ta.id_turma = t.id_turma " +
				"inner join ensino.componente_curricular c on t.id_disciplina = c.id_disciplina " +
				"inner join ensino.componente_curricular_detalhes cd  on c.id_detalhe = cd.id_componente_detalhes " +
				"where t.id_turma in ( " +
				"	select m.id_turma from ensino.matricula_componente m " +
				"	join ensino.turma t2 on t2.id_turma = m.id_turma " +
				"	where id_situacao_matricula = "+SituacaoMatricula.MATRICULADO.getId()+" and t2.ano = "+ano+" and t2.periodo = "+semestre+" and m.id_discente = "+discente.getId()+" " +
				") " +
				"and tt.data_entrega >= :dataInicio and tt.data_entrega <= :dataFim "+
				"and a.ativo = trueValue() order by tt.data_entrega asc "
				);
		
		Calendar calendario = Calendar.getInstance();
		calendario.add(Calendar.DAY_OF_YEAR, diasFim);
		
		q.setDate("dataFim", calendario.getTime());
		q.setDate("dataInicio", CalendarUtils.subtraiDias(new Date(),diasInicio));
		
		@SuppressWarnings("unchecked")
		List<Object[]> result = q.list();
		List<TarefaTurma> tarefas = new ArrayList<TarefaTurma>();
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				
				TarefaTurma t = new TarefaTurma();
				t.setId((Integer) linha[i++]);
				t.setTitulo((String) linha[i++]);
				t.setConteudo((String) linha[i++]);
				t.setDataEntrega((Date)linha[i++]);
				t.setRespostas(new ArrayList<RespostaTarefaTurma>());
				Integer idResposta = (Integer) linha[i++];
				if ( idResposta != null ){
					RespostaTarefaTurma r = new RespostaTarefaTurma();
					r.setId(idResposta);
					t.getRespostas().add(r);
				}
				t.setAula( new TopicoAula() );
				t.getAula().setTurma( new Turma () );
				t.getAula().getTurma().setId((Integer) linha[i++]);
				t.getAula().getTurma().setDisciplina(new ComponenteCurricular());
				t.getAula().getTurma().getDisciplina().setDetalhes(new ComponenteDetalhes());
				t.getAula().getTurma().getDisciplina().getDetalhes().setNome((String) linha[i++]);
				t.setHoraEntrega((Integer) linha[i++]);
				t.setMinutoEntrega((Integer) linha[i++]);
				
				tarefas.add(t);
			}
		}
		
		return tarefas;

	}

	/**
	 * Retorna uma coleção de respostas cujo os componentes não possuem grupo de uma tarefa em grupo. 
	 * O sistema deve tratar este caso já que existia a opção de cadastrar tarefas em grupo a
	 * antes do desenvolvimento do caso de uso de gerenciamento de grupos.
     *
	 * @param tarefa 
	 * 	@return
	 */
	public List<RespostaTarefaTurma> findRepostasEmGrupoSemGrupo(TarefaTurma tarefa) {
		
		@SuppressWarnings("unchecked")
		List<RespostaTarefaTurma> result = getHibernateTemplate().find("select r from RespostaTarefaTurma r left join r.tarefa t where  "
				+ "r.ativo = true and t.ativo = true and t.emGrupo = true and r.grupoDiscentes is null and t.id = ?",tarefa.getId());

		return result;
	}

	/**
	 * Retorna se uma tarefa em grupo possui respostas cujo os componentes não possuem grupo.
	 * Utilizado para tratar os caso onde não existiam tarefas em grupo. 
     *
	 * @param tarefa 
	 * 	@return
	 */
	public boolean existeGrupoAntigo(TarefaTurma tarefa) {
		
		@SuppressWarnings("unchecked")
		List<RespostaTarefaTurma> result = getHibernateTemplate().find("select r from RespostaTarefaTurma r left join r.tarefa t where  "
				+ "r.ativo = true and t.ativo = true and t.emGrupo = true and r.grupoDiscentes is null and t.id = ?",tarefa.getId());

		if (result != null && result.size() > 0)
			return true;
		else
			return false;
	}

	/**
	 * Retorna as tarefas que o discente respondeu.
     *
	 * @param tarefa
	 * @param usuario 
	 * @param discente  
	 * 	@return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public List<Integer> findTarefasRespondidasByUsuario(List<TarefaTurma> tarefas, Discente discente, Usuario usuario) throws HibernateException, DAOException {

		if ( isEmpty(tarefas) )
			return new ArrayList<Integer>();
		
		String ids = UFRNUtils.gerarStringIn(tarefas);
		
		Query  q = getSession().createSQLQuery(
				" select r.id_tarefa from ava.resposta_tarefa_turma r " +
				" left join ava.grupo_discentes gd on gd.id_grupo_discentes = r.id_grupo_discentes " +
				" left join ava.grupo_discentes_discente gdd on gd.id_grupo_discentes = gdd.id_grupo_discentes " +
				" where " +
				" ( r.id_usuario_envio = " + usuario.getId() +" or gdd.id_discente = "+ discente.getId() +" ) " +
				" and r.id_tarefa in " +ids);
		
		List<Integer> res = q.list();
		
		if ( res != null )
			return res;
		
		return new ArrayList<Integer>();
	}
	
	/**
	 * Retorna um mapa com a quantidade de respostas de cada tarefa.
     *
	 * @param tarefa
	 * @param usuario 
	 * @param discente  
	 * 	@return
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	@SuppressWarnings("unchecked")
	public HashMap<Integer,Integer> findQtdRespostaTarefas(List<TarefaTurma> tarefas, Turma turma) throws HibernateException, DAOException {

		if ( isEmpty(tarefas) )
			return null;
		
		String idsTurmas = "";
		List<Integer> idsTurmaArray = new ArrayList<Integer>();		
		if (turma.isAgrupadora())
			for (Turma t: turma.getSubturmas())
				idsTurmaArray.add(t.getId());
		else
			idsTurmaArray.add(turma.getId());		
		idsTurmas = UFRNUtils.gerarStringIn(idsTurmaArray);
		
		String ids = UFRNUtils.gerarStringIn(tarefas);
		
		String sql = " select t.id_tarefa_turma , count(r.id) "+ 
						" from ava.tarefa t "+ 
						" left join ava.resposta_tarefa_turma r on r.id_tarefa = t.id_tarefa_turma "+ 
						" left join comum.usuario u on u.id_usuario = r.id_usuario_envio "+ 
						" left join discente d on d.id_pessoa = u.id_pessoa "+ 
						" left join ensino.matricula_componente m on m.id_discente = d.id_discente and m.id_turma in "+idsTurmas+ 
						" where t.id_tarefa_turma in "+ids+ 
						" 		and m.id_situacao_matricula in "+ UFRNUtils.gerarStringIn(SituacaoMatricula.getSituacoesAcessoTurmaVirtual())+
						" 		and r.ativo = trueValue() "+ 
						" group by t.id_tarefa_turma ";
		
		Query  q = getSession().createSQLQuery(sql);	
		List<Object[]> result = q.list();
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
		
		if ( result != null )
		{	
			for (Object[] linha : result) {
				Integer i = 0;
				Integer id = (Integer)linha[i++];
				Integer qtd = ((Number)linha[i++]).intValue();
				map.put(id,qtd);
			}
		}
		
		if ( map != null )
			return map;
		
		return null;
	}
}
