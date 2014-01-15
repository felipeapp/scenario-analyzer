package br.ufrn.sigaa.extensao.dao;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.extensao.dominio.GrupoQuestionarioExtensao;
import br.ufrn.sigaa.extensao.dominio.LinhaQuestionarioProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.QuestionarioProjetoExtensao;
import br.ufrn.sigaa.extensao.dominio.TipoAtividadeExtensao;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.projetos.dominio.MembroProjeto;
import br.ufrn.sigaa.projetos.dominio.Projeto;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.vestibular.dominio.LinhaQuestionarioRespostas;

public class QuestionarioProjetoExtensaoDao extends GenericSigaaDAO {

	public List<MembroProjeto> findProjetos( GrupoQuestionarioExtensao grupo, int idEdital, int ano, int idTipoAcao ) throws DAOException {
		String in = "";
		if ( idTipoAcao > 0 )
			in = UFRNUtils.gerarStringIn(new int[] {idTipoAcao});
		else
			in = UFRNUtils.gerarStringIn(TipoAtividadeExtensao.getAllTiposAtividadesExtensao());
		Query q = getSession().createSQLQuery(String.format(grupo.getSql(), idEdital, ano, in));
		List<Object[]> lista = q.list();
		List<MembroProjeto> result = new ArrayList<MembroProjeto>();
		for (Object[] objects : lista) {
			MembroProjeto mp = new MembroProjeto();
			int count = 0;
			mp.setProjeto(new Projeto( (Integer) objects[count++] ));
			mp.setPessoa(new Pessoa( (Integer) objects[count++] ));
			result.add(mp);
		}

		return result;
	}
	
	public boolean haQuestionarioEdital( int idQuestionario, int tipoGrupo, int tipoAcao ) throws DAOException {
		String sql = " select count(*)" +
				" from extensao.questionario_projeto_extensao qpe" +
				" join projetos.projeto p using ( id_projeto )" +
				" where qpe.id_questionario = " + idQuestionario +
				" and qpe.tipo_grupo = " + tipoGrupo +
				" and qpe.id_tipo_atividade_extensao = " + tipoAcao +
				" and qpe.ativo = trueValue() ";
					   
		Query q = getSession().createSQLQuery(sql);
		
		return ((BigInteger) q.uniqueResult()).intValue() > 0;
	}

	public boolean haQuestionarioNaoRespondido( Pessoa pessoa) throws DAOException {
		Date hoje = CalendarUtils.descartarHoras(new Date());
		String sql = " select count(*) " +
				" from extensao.questionario_projeto_extensao qpe " +
				" join questionario.questionario q using ( id_questionario ) " +
				" where '" + hoje + "' >= q.inicio " + 
				" and '" + hoje + "' <= q.fim " +
				" and qpe.ativo = trueValue() and qpe.data_resposta is null " +
				" and qpe.id_pessoa = " + pessoa.getId();
					   
		Query q = getSession().createSQLQuery(sql);
		return ((BigInteger) q.uniqueResult()).intValue() > 0;
	}

	public Collection<QuestionarioProjetoExtensao> findQuestionarioByPessoa( Pessoa pessoa) throws DAOException {
		Date hoje = CalendarUtils.descartarHoras(new Date());
		String sql = " select id_questionario_projeto, p.titulo, q.titulo as quest, tae.descricao, qpe.obrigatoriedade" +
				" from extensao.questionario_projeto_extensao qpe " +
				" join projetos.projeto p using ( id_projeto )" +
				" join extensao.atividade a using ( id_projeto )" +
				" join extensao.tipo_atividade_extensao tae on ( a.id_tipo_atividade_extensao = tae.id_tipo_atividade_extensao )" +
				" join questionario.questionario q using ( id_questionario )" +
				" where qpe.ativo = trueValue() " +
				" and p.ativo = trueValue() " +
				" and '" + hoje + "' >= q.inicio " + 
				" and '" + hoje + "' <= q.fim " +
				" and q.ativo = trueValue() " +
				" and qpe.data_resposta is null " +
				" and id_pessoa = " + pessoa.getId() +
		        " order by tae.descricao";

		Query q = getSession().createSQLQuery(sql);
		List<Object[]> lista = q.list();
		List<QuestionarioProjetoExtensao> result = new ArrayList<QuestionarioProjetoExtensao>();
		for (Object[] objects : lista) {
			QuestionarioProjetoExtensao qpe = new QuestionarioProjetoExtensao();
			int count = 0;
			qpe.setId( (Integer) objects[count++] );  
			qpe.setProjeto(new Projeto());
			qpe.getProjeto().setTitulo( (String) objects[count++] );
			qpe.setQuestionario(new Questionario());
			qpe.getQuestionario().setTitulo( (String) objects[count++] );
			qpe.getProjeto().setTipoAcaoExtensao( (String) objects[count++] );
			qpe.setObrigatoriedade( (Boolean) objects[count++] );
			result.add(qpe);
		}

		return result;
	}

	public Collection<LinhaQuestionarioRespostas> findByEstatisticaQuestionarioSocioEconomico(Questionario questionario, 
			int tipoQuestao, int idTipoAtividade) throws HibernateException, DAOException{
		
		Collection<LinhaQuestionarioRespostas> linhas = new ArrayList<LinhaQuestionarioRespostas>();
			StringBuilder sql = null;
			switch (tipoQuestao) {

			case PerguntaQuestionario.UNICA_ESCOLHA:
				linhas.addAll(carregarPerguntaSelecao(questionario, idTipoAtividade));
				break;
			
			case PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO:
				linhas.addAll(carregarPerguntaSelecao(questionario, idTipoAtividade));
				break;
				
			case PerguntaQuestionario.MULTIPLA_ESCOLHA:
				sql = new StringBuilder();
				sql.append("select p.ordem, p.pergunta, ap.alternativa, cast(COUNT(*) as float) as totalparcial"+
						" from questionario.pergunta p"+
						" join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+
						" join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
						" join questionario.resposta_pergunta_multipla rpm on (rpm.id_resposta = r.id_resposta)"+
						" join questionario.alternativa_pergunta ap on (ap.id_alternativa_pergunta =  rpm.id_alternativa_pergunta)"+
						" join extensao.questionario_projeto_extensao qpe on ( qpe.id_questionario = p.id_questionario " +
							" and qpe.id_questionario_resposta = qr.id_questionario_respostas ) " +
						" where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() "+
						" and p.tipo = " + PerguntaQuestionario.MULTIPLA_ESCOLHA + " and qpe.ativo " +
						" and qpe.id_tipo_atividade_extensao =  " + idTipoAtividade +
						" GROUP BY ap.alternativa, p.pergunta, ap.ordem, p.ordem"+
						" order by ap.ordem");

				List<Object[]> mult= getSession().createSQLQuery(sql.toString()).list(); 
				for (Object[] item : mult) {
					LinhaQuestionarioRespostas linha = new LinhaQuestionarioRespostas();
					linha.setOrdem((Integer) item[0]);
					linha.setPergunta((String) item[1]);
					linha.setAlternativa((String) item[2]);
					linha.setTotalParcial((Double) item[3]);
					linhas.add(linha);
				}
				break;
				
			case PerguntaQuestionario.VF:
				sql = new StringBuilder();
				sql.append("select p.ordem, p.pergunta, cast(r.resposta_vf as text), cast(count(r.resposta_vf) as float) as totalparcial"+ 
						" from questionario.pergunta p "+
						" inner join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+ 
						" inner join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
						" left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa) "+
						" join extensao.questionario_projeto_extensao qpe on ( qpe.id_questionario = p.id_questionario " +
							" and qpe.id_questionario_resposta = qr.id_questionario_respostas ) " +
						" where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() "+
						" and p.tipo = "+ PerguntaQuestionario.VF + " and qpe.ativo " +
						" and qpe.id_tipo_atividade_extensao =  " + idTipoAtividade +
						" group by p.ordem, p.pergunta, resposta_vf, ap.ordem "+
						" order by resposta_vf ");

				List<Object[]> trueFalse= getSession().createSQLQuery(sql.toString()).list(); 
				for (Object[] item : trueFalse) {
					LinhaQuestionarioRespostas linha = new LinhaQuestionarioRespostas();
					linha.setOrdem((Integer) item[0]);
					linha.setPergunta((String) item[1]);
					linha.setAlternativa((String) item[2]);
					linha.setTotalParcial((Double) item[3]);
					linhas.add(linha);
				}
				break;

			case PerguntaQuestionario.NUMERICA:
				sql = new StringBuilder();
				sql.append("select p.ordem, p.pergunta, cast(r.resposta_numerica as text), cast(count(r.resposta_numerica) as float) as totalparcial"+
						   " from questionario.pergunta p"+
						   " inner join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+
						   " inner join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
						   " left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa)"+
   						   " join extensao.questionario_projeto_extensao qpe on ( qpe.id_questionario = p.id_questionario " +
   						   		" and qpe.id_questionario_resposta = qr.id_questionario_respostas ) " +
						   " where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() " +
					   	   " and p.tipo = " + PerguntaQuestionario.NUMERICA + " and qpe.ativo " +
					   	   " and qpe.id_tipo_atividade_extensao =  " + idTipoAtividade +
						   " group by p.ordem, p.pergunta, resposta_numerica, ap.ordem"+
						   " order by resposta_numerica");

				List<Object[]> numeric = getSession().createSQLQuery(sql.toString()).list(); 
				for (Object[] item : numeric) {
					LinhaQuestionarioRespostas linha = new LinhaQuestionarioRespostas();
					linha.setOrdem((Integer) item[0]);
					linha.setPergunta((String) item[1]);
					linha.setAlternativa((String) item[2]);
					linha.setTotalParcial((Double) item[3]);
					linhas.add(linha);
				}
				break;
				
		}
		return linhas;
	}

	/**
	 * Serve para carregar as quantidades de perguntas e respostas das alternativas. 
	 * 
	 * @param questionario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<LinhaQuestionarioRespostas> carregarPerguntaSelecao(Questionario questionario, int idTipoAtividade) throws HibernateException, DAOException{
		Collection<LinhaQuestionarioRespostas> linhas = new ArrayList<LinhaQuestionarioRespostas>();
		
		String sql = "select p.ordem, p.pergunta, ap.alternativa, cast(COUNT(*) as float) as total"+ 
					 " from questionario.pergunta p "+
					 " inner join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+ 
					 " inner join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
					 " left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa) "+
					 " inner join extensao.questionario_projeto_extensao qpe on ( qpe.id_questionario = p.id_questionario " +
					 " 		and qpe.id_questionario_resposta = qr.id_questionario_respostas ) "+
					 " where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() "+
					 " and p.tipo in "+ UFRNUtils.gerarStringIn(new int[] { PerguntaQuestionario.UNICA_ESCOLHA, 
							 PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO }) + " and qpe.ativo " +
					 " and qpe.id_tipo_atividade_extensao =  " + idTipoAtividade +
					 " GROUP BY ap.ordem, p.ordem, p.pergunta, ap.alternativa order by p.ordem";

				List<Object[]> resp = getSession().createSQLQuery(sql.toString()).list(); 
				for (Object[] item : resp) {
					LinhaQuestionarioRespostas linha = new LinhaQuestionarioRespostas();
					linha.setOrdem((Integer) item[0]);
					linha.setPergunta((String) item[1]);
					linha.setAlternativa((String) item[2]);
					linha.setTotalParcial((Double) item[3]);
					linhas.add(linha);
				}
		return linhas;
	} 

	/**
	 * Serve para carregar as quantidades de perguntas e respostas das alternativas. 
	 * 
	 * @param questionario
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<QuestionarioProjetoExtensao> carregarPerguntaSelecao() throws HibernateException, DAOException{
		Collection<QuestionarioProjetoExtensao> questionariosProjetoExt = new ArrayList<QuestionarioProjetoExtensao>();
		
		String sql = "select distinct qpe.id_questionario, qpe.tipo_grupo, gqe.descricao as grupo, q.titulo as questionario, " +
				" q.inicio, q.fim, qpe.id_tipo_atividade_extensao, tae.descricao, qpe.obrigatoriedade" +
				" from extensao.questionario_projeto_extensao  qpe" +
				" join extensao.grupo_questionario_extensao gqe on ( qpe.tipo_grupo = gqe.id_grupo_questionario_extensao )" +
				" join questionario.questionario q on ( qpe.id_questionario = q.id_questionario )" +
				" left join extensao.tipo_atividade_extensao tae on ( tae.id_tipo_atividade_extensao = qpe.id_tipo_atividade_extensao ) " +
				" where qpe.ativo = trueValue() and q.ativo = trueValue()" +
				" order by q.inicio desc, q.fim, q.titulo";

				List<Object[]> resp = getSession().createSQLQuery(sql.toString()).list(); 
				for (Object[] item : resp) {
					QuestionarioProjetoExtensao linha = new QuestionarioProjetoExtensao();
					linha.setQuestionario(new Questionario((Integer) item[0]));
					linha.setTipoGrupo((Integer) item[1]);
					linha.setNomeGrupo( (String) item[2] );
					linha.getQuestionario().setTitulo( (String) item[3] );
					linha.getQuestionario().setInicio( (Date) item[4] );
					linha.getQuestionario().setFim( (Date) item[5] );
					linha.setTipoAtividade(new TipoAtividadeExtensao((Integer) item[6]));
					if ( (String) item[7] != null && !(item[7].equals("")) )
						linha.getTipoAtividade().setDescricao( (String) item[7] );
					else
						linha.getTipoAtividade().setDescricao("TODOS");
					
					linha.setObrigatoriedade( (Boolean) item[8] );
					questionariosProjetoExt.add(linha);
				}
		return questionariosProjetoExt;
	} 
	
	public void inativarQuestionarios(QuestionarioProjetoExtensao questionario) {
		update("UPDATE extensao.questionario_projeto_extensao SET ativo = falseValue() WHERE id_questionario = ? and tipo_grupo = ? and id_tipo_atividade_extensao = ?",
				new Object[] { questionario.getQuestionario().getId(), questionario.getTipoGrupo(), questionario.getTipoAtividade().getId() });
	}
	
	public void mudarObrigatoriedadeQuestionario(QuestionarioProjetoExtensao questionario) {
		update("UPDATE extensao.questionario_projeto_extensao SET obrigatoriedade = ? " +
					" WHERE id_questionario = ? and tipo_grupo = ? and id_tipo_atividade_extensao = ? and ativo = ? ",
				new Object[] { !questionario.isObrigatoriedade(), questionario.getQuestionario().getId(), questionario.getTipoGrupo(), 
					questionario.getTipoAtividade().getId(), Boolean.TRUE});
	}

	public boolean haRespostaQuestionario( Questionario questionario ) throws DAOException {
		String sql = " select count(*) " +
				" from extensao.questionario_projeto_extensao qpe " +
				" where qpe.ativo = trueValue() " +
				" and qpe.id_registro_resposta is not null" +
				" and qpe.data_resposta is not null" +
				" and qpe.id_questionario = " + questionario.getId();
					   
		Query q = getSession().createSQLQuery(sql);
		return ((BigInteger) q.uniqueResult()).intValue() > 0;
	}
	
	public List<LinhaQuestionarioProjetoExtensao> exportarRespostaQuestionario(int idQuestionario, int idTipoGrupo, int idTipoAtividade ) throws HibernateException, DAOException{
		List<LinhaQuestionarioProjetoExtensao> linhas = new ArrayList<LinhaQuestionarioProjetoExtensao>();
		
		String sql = "select proj.id_projeto, proj.titulo, p.pergunta, p.ordem, ap.alternativa as unica_alternatica, apmult.alternativa as multi, r.resposta_dissertativa, r.resposta_vf" +
				" from questionario.pergunta p" +
				" join questionario.resposta r on (p.id_pergunta = r.id_pergunta)" +
				" join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)" +
				" join extensao.questionario_projeto_extensao qpe on ( qpe.id_questionario = p.id_questionario and qpe.id_questionario_resposta = qr.id_questionario_respostas )" +
				" join projetos.projeto proj using ( id_projeto )" +
				
				// Unica Alternativa ou Peso
				" left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa) " + 
				
				//Multipla Escolha
				" left join questionario.resposta_pergunta_multipla rpm on (rpm.id_resposta = r.id_resposta)" +
				" left join questionario.alternativa_pergunta apmult on (apmult.id_alternativa_pergunta =  rpm.id_alternativa_pergunta)" +
				
				" where qpe.id_questionario = " + idQuestionario + " and qpe.tipo_grupo = " + idTipoGrupo +
				" and qpe.id_tipo_atividade_extensao = " + idTipoAtividade + " and p.ativo = trueValue() and qpe.ativo" +
				" order by proj.titulo, p.ordem";

				List<Object[]> resp = getSession().createSQLQuery(sql.toString()).list(); 
				int idProjeto = 0, ordem = 0, coluna;
				boolean multiplaEscolha = false;
				LinhaQuestionarioProjetoExtensao linha = null;
				for (Object[] item : resp) {
					coluna = 0;
					if ( idProjeto != (Integer) item[coluna] ) {
						//Adicionar Novo Registro
						if ( idProjeto != 0 )
							linhas.add(linha);
						
						linha = new LinhaQuestionarioProjetoExtensao();	
						linha.setIdProjeto( (Integer) item[coluna] );
						linha.setTituloProjeto( (String) item[++coluna] );
						
						linha.setPerguntas(new ArrayList<String>());
						linha.getPerguntas().add("Título Projeto");
						linha.getPerguntas().add( (String) item[++coluna] );
						
						linha.setRespostas(new ArrayList<String>());
						multiplaEscolha = false;
						ordem = (Integer) item[++coluna];
					}
				
					coluna = 2;
					if ( ordem != (Integer) item[++coluna] ) {
						linha.getPerguntas().add( (String) item[2] );
					}
					
					//Unica Alternatica ou Peso
					if ( (String) item[++coluna] != null ) {
						linha.getRespostas().add( (String) item[coluna] );
					// Multipla Alternatica
					} else if ( (String) item[++coluna] != null ) {
						if ( !multiplaEscolha ) {
							linha.getRespostas().add( (String) item[coluna] );
						} else {
							String resposta = linha.getRespostas().get(linha.getRespostas().size() - 1);
							resposta += " / " + (String) item[coluna];
							linha.getRespostas().set(linha.getRespostas().size() - 1, resposta);
						}
						multiplaEscolha = true;
					//Dissertatica
					} else if ( (String) item[++coluna] != null ) {
						linha.getRespostas().add( (String) item[coluna] );
					// V ou F
					} else {
						linha.getRespostas().add( Formatador.getInstance().formatarBoolean(((Boolean) item[++coluna]), "S/N") );
					}
					
					idProjeto = (Integer) item[0];
					ordem = (Integer) item[3];
				}
				
				linhas.add(linha);
				
			return linhas;
	}
	
}