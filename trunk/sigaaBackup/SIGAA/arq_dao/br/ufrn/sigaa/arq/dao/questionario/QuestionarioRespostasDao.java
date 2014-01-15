/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on '28/10/2008'
 *
 */
package br.ufrn.sigaa.arq.dao.questionario;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.GenericSigaaDAO;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.ensino.dominio.ProcessoSeletivo;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;
import br.ufrn.sigaa.vestibular.dominio.LinhaQuestionarioRespostas;

/**
 * DAO responsável por buscar as respostas de questionários 
 * 
 * @author wendell
 *
 */
public class QuestionarioRespostasDao extends GenericSigaaDAO {

	/**
	 * Busca as respostas fornecidas por um candidato de processo seletivo
	 * ao questionário aplicado
	 * 
	 * @param inscricaoSelecao
	 * @return
	 * @throws DAOException
	 */
	public QuestionarioRespostas findByInscricaoSelecao(InscricaoSelecao inscricaoSelecao) throws DAOException {
		String hql = "SELECT q FROM QuestionarioRespostas q WHERE q.inscricaoSelecao.id = :idInscricao ";
		Query q = getSession().createQuery(hql);
		q.setInteger("idInscricao", inscricaoSelecao.getId());
		return (QuestionarioRespostas) q.uniqueResult();
	}

	/**
	 * Busca as respostas fornecidas por um aluno na adesão ao cadastro único
	 * 
	 * @param adesao
	 * @return
	 * @throws DAOException
	 */
	public QuestionarioRespostas findByAdesao(AdesaoCadastroUnicoBolsa adesao) throws DAOException {
		String hql = "select qr from QuestionarioRespostas qr where qr.adesao.id = :idAdesao";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idAdesao", adesao.getId());
		q.setMaxResults(1);
		return (QuestionarioRespostas) q.uniqueResult();
	}
	
	/**
	 * Busca as respostas conforme o id da resposta informado
	 * @param id
	 * @return
	 * @throws DAOException
	 */
	public QuestionarioRespostas findById(int id) throws DAOException {
		String hql = "select qr from QuestionarioRespostas qr where qr.id = :id";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("id", id);
		q.setMaxResults(1);
		return (QuestionarioRespostas) q.uniqueResult();
	}
	
	
	/**
	 *Busca todas as respostas de questionário dos inscritos em um processo seletivo 
	 * @param processoSeletivo
	 * @return
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public Collection<QuestionarioRespostas> findByProcessoSeletivo(ProcessoSeletivo processoSeletivo) throws DAOException{
	
		String hql = "SELECT q FROM QuestionarioRespostas q WHERE " +
				"q.inscricaoSelecao.processoSeletivo.id = :idProcessoSeletivo ";
		Query q = getSession().createQuery(hql);
		q.setInteger("idProcessoSeletivo", processoSeletivo.getId());
		return q.list();
	
	}
	
	/**
	 * Retorna a resposta e o quantitativo das respostas das pergunta do Questionário. 
	 * 
	 * @param questionario
	 * @param tipoQuestao
	 * @param idProcessoSeletivo
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public Collection<LinhaQuestionarioRespostas> findByEstatisticaQuestionarioSocioEconomico(Questionario questionario, 
			int tipoQuestao, int idProcessoSeletivo) throws HibernateException, DAOException{
		
		Collection<LinhaQuestionarioRespostas> linhas = new ArrayList<LinhaQuestionarioRespostas>();
			StringBuilder sql = null;
			switch (tipoQuestao) {

			case PerguntaQuestionario.UNICA_ESCOLHA:
				linhas.addAll(carregarPerguntaSelecao(questionario, idProcessoSeletivo));
				break;
			
			case PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO:
				linhas.addAll(carregarPerguntaSelecao(questionario, idProcessoSeletivo));
				break;
				
			case PerguntaQuestionario.MULTIPLA_ESCOLHA:
				sql = new StringBuilder();
				sql.append("select p.ordem, p.pergunta, ap.alternativa, cast(COUNT(*) as float) as totalparcial"+
						" from questionario.pergunta p"+
						" join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+
						" join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
						" join vestibular.inscricao_vestibular iv on (qr.id_inscricao_vestibular = iv.id_inscricao_vestibular) "+
						" join questionario.resposta_pergunta_multipla rpm on (rpm.id_resposta = r.id_resposta)"+
						" join questionario.alternativa_pergunta ap on (ap.id_alternativa_pergunta =  rpm.id_alternativa_pergunta)"+
						" where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() and iv.validada is true "+
						" and iv.id_processo_seletivo = "+idProcessoSeletivo+" and p.tipo = " + PerguntaQuestionario.MULTIPLA_ESCOLHA +
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
						" inner join vestibular.inscricao_vestibular iv on (qr.id_inscricao_vestibular = iv.id_inscricao_vestibular)  "+
						" left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa) "+
						" where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() and iv.validada is true "+
						" and iv.id_processo_seletivo = "+idProcessoSeletivo+" and p.tipo = "+ PerguntaQuestionario.VF +
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
						   " inner join vestibular.inscricao_vestibular iv on (qr.id_inscricao_vestibular = iv.id_inscricao_vestibular) "+
						   " left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa)"+
						   " where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue()"+
						   " and iv.validada is true and iv.id_processo_seletivo = "+idProcessoSeletivo+" and p.tipo = 5 "+
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
	public Collection<LinhaQuestionarioRespostas> carregarPerguntaSelecao(Questionario questionario, int idProcessoSeletivo) throws HibernateException, DAOException{
		Collection<LinhaQuestionarioRespostas> linhas = new ArrayList<LinhaQuestionarioRespostas>();
		
		String sql = "select p.ordem, p.pergunta, ap.alternativa, cast(COUNT(*) as float) as total"+ 
					 " from questionario.pergunta p "+
					 " inner join questionario.resposta r on (p.id_pergunta = r.id_pergunta)"+ 
					 " inner join questionario.questionario_respostas qr on (qr.id_questionario_respostas = r.id_questionario_respostas)"+ 
					 " inner join vestibular.inscricao_vestibular iv on (qr.id_inscricao_vestibular = iv.id_inscricao_vestibular) "+
					 " left join questionario.alternativa_pergunta ap on (id_alternativa_pergunta = r.id_alternativa) "+
					 " where p.id_questionario = "+ questionario.getId()+" and p.ativo = trueValue() "+
					 " and iv.validada is true and iv.id_processo_seletivo = "+idProcessoSeletivo+
					 " and p.tipo in "+ UFRNUtils.gerarStringIn(new int[] { PerguntaQuestionario.UNICA_ESCOLHA, 
							 PerguntaQuestionario.UNICA_ESCOLHA_ALTERNATIVA_PESO }) +
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
	 * <p>Retorna as informações da perguntas e respostas dados por um usuário ao questionário.</p>
	 * 
	 * <p> Usado para visualização dessas informações em qualquer parte do sistema.</p>
	 * 
	 * @param unidade
	 * @return
	 * @throws HibernateException
	 * @throws DAOException
	 */
	@SuppressWarnings("unchecked")
	public QuestionarioRespostas findInformacaoRespostasQuestionario( int idQuestionarioResposta, int idTipoQuestionario) throws DAOException {
		
		String projecao = " pergunta.tipo, pergunta.pergunta, pergunta.ativo, "+
						  " respostasUsuario.questionario.id, respostasUsuario.questionario.titulo,  respostasUsuario.questionario.ativo, "+
						  " respostas.id, respostas.respostaDissertativa, respostas.respostaNumerica, respostas.respostaVf, respostas.respostaArquivo, alternativa.alternativa, alternativa.peso, alternativas.id, alternativas.alternativa "
						  ;
		
		String hql = " SELECT  "+projecao 
					+" FROM QuestionarioRespostas respostasUsuario "
					+" INNER JOIN respostasUsuario.respostas respostas "
					+" INNER JOIN respostas.pergunta pergunta "
					+" LEFT JOIN respostas.alternativa alternativa"
					+" LEFT JOIN respostas.alternativas alternativas"
			        +" WHERE respostasUsuario.questionario.tipo.id = :idTipoQuestionario "
			        +" AND respostasUsuario.id = :idQuestionarioResposta "
			        +" AND respostasUsuario.questionario.ativo = :true "
			        +" AND pergunta.ativo = :true ";
		
		Query q = getSession().createQuery(hql);
		q.setInteger("idQuestionarioResposta", idQuestionarioResposta);
		q.setInteger("idTipoQuestionario", idTipoQuestionario);
		q.setBoolean("true", true);
		
		List<Object[]> objects = q.list(); 
		
		QuestionarioRespostas retorno = new QuestionarioRespostas();
		
		List<Resposta> respostas = new ArrayList<Resposta>();
		
		for (Object[] item : objects) {
			
			
			PerguntaQuestionario pergunta = new PerguntaQuestionario( (Integer)item[0], (String)item[1]) ;
			pergunta.setAtivo( (Boolean)item[2] );
			
			if(retorno.getQuestionario() == null ){
				Questionario questionario = new Questionario();
				questionario.setId((Integer)item[3]);
				questionario.setTitulo((String)item[4]);
				questionario.setAtivo((Boolean)item[5]);
				retorno.setQuestionario(questionario);
			}
			
			
			Resposta resposta = new Resposta(pergunta);
			resposta.setId((Integer)item[6]);
			
			// Em caso de multipla escolha, as respostas virão repetidas
			if(respostas.contains(resposta)){
				resposta = respostas.get(respostas.indexOf(resposta));
			}else{
				respostas.add(resposta);
			}
			
			resposta.setRespostaDissertativa( (String)item[7] );
			resposta.setRespostaNumerica( (Float)item[8] );
			resposta.setRespostaVf( (Boolean)item[9] );
			resposta.setRespostaArquivo( (Integer) item[10] );
			
			if(pergunta.isUnicaEscolha() || pergunta.isUnicaEscolhaAlternativaPeso()){
				Alternativa alternativa = new Alternativa();
				alternativa.setAlternativa((String) item[11]);
				alternativa.setPeso((Integer) item[12]);
				resposta.setAlternativa(alternativa);
			}
			
			if(pergunta.isMultiplaEscolha()){
				
				if( item[13] != null){ // se o usuário respondeu, caso a partunta não seja obritatória
					Alternativa alternativa = new Alternativa();
					alternativa.setId((Integer) item[13]);
					
					if(resposta.getAlternativas() == null || ! resposta.getAlternativas().contains(alternativa)){
						alternativa.setAlternativa((String) item[14]);
						resposta.adicionaAlternativa(alternativa);
					}else{
						
					}
				}
			}
			
			
		}
		
		retorno.setRespostas(respostas);
		
		return retorno;
		
	}
	
}