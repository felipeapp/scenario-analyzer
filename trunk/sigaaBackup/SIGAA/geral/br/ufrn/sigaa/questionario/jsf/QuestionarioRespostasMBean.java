/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas - SIGAA
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Criado em 13/12/2007
 *
 */
package br.ufrn.sigaa.questionario.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.HibernateException;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.parametrizacao.ParametroHelper;
import br.ufrn.arq.util.CollectionUtils;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.ensino.dominio.InscricaoSelecao;
import br.ufrn.sigaa.extensao.dominio.InscricaoAtividadeParticipante;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;
import br.ufrn.sigaa.vestibular.dominio.InscricaoVestibular;

/**
 * MBean respons�vel pelo fluxo de opera��es para 
 * cadastrar respostas para um question�rio
 *
 * @author Victor Hugo
 *
 */
@Component("questionarioRespostasBean") @Scope("session")
public class QuestionarioRespostasMBean extends SigaaAbstractController<QuestionarioRespostas> {

	/** DataModel que lista as pergunta e as alternativas **/
	private DataModel respostasModel;
	
	/**
	 * Construtor Padr�o
	 */
	public QuestionarioRespostasMBean() {
		clear();
	}

	/**
	 * Limpa o objeto gerenciado pelo MBean
	 */
	private void clear() {
		obj = new QuestionarioRespostas();
	}
	
	/**
	 * Inicializar MBean para cadastrar novas respostas para um question�rio de processo seletivo
	 * M�todo n�o invocado por JSP's
	 * @param questionario
	 * @param inscricaoSelecao
	 */
	public void inicializar(InscricaoSelecao inscricaoSelecao) {
		inicializar(inscricaoSelecao.getQuestionario());
		obj.setInscricaoSelecao(inscricaoSelecao);
	}
	
	
	/**
	 * Inicializar MBean para cadastrar novas respostas para um question�rio de atividade
	 * M�todo n�o invocado por JSP's
	 * @param questionario
	 * @param inscricaoSelecao
	 */
	public void inicializarQuestionarioAtividade(InscricaoAtividadeParticipante inscricaoAtividadeParticipante) {
		inicializar(inscricaoAtividadeParticipante.getInscricaoAtividade().getQuestionario());
		obj.setInscricaoAtividadeParticipante(inscricaoAtividadeParticipante);
	}

	/**
	 * Inicializar MBean para cadastrar novas respostas para um question�rio de ades�o ao cadastro �nico
	 * M�todo n�o invocado por JSP's
	 * @param adesao
	 */
	public void inicializar(AdesaoCadastroUnicoBolsa adesao) {
		inicializar(adesao.getQuestionario());
		obj.setAdesao(adesao);
	}
	
	/**
	 * Inicializar MBean para cadastrar novas respostas para um question�rio de ades�o ao cadastro �nico
	 * M�todo n�o invocado por JSP's
	 * @param adesao
	 */
	public void inicializar(InscricaoVestibular inscricaoVestibular) {
		inicializar(inscricaoVestibular.getProcessoSeletivo().getQuestionario());
		obj.setInscricaoVestibular(inscricaoVestibular);
	}
	
	/**
	 * Inicializar MBean com as respostas vazias para o question�rio em quest�o
	 * M�todo n�o invocado por JSP's
	 * @param questionario
	 */
	public void inicializar(Questionario questionario) {
		clear();
		if (questionario != null) {
			obj.setQuestionario(questionario);
			popularRespostas();
		}
	}

	/**
	 * Inicializa o MBean com as respostas pr�-populadas da ades�o anterior
	 * M�todo n�o invocado por JSP's 
	 * @param adesaoAterior
	 * @throws DAOException
	 */
	public void inicializarReaproveitarAdesao(AdesaoCadastroUnicoBolsa novaAdesao, AdesaoCadastroUnicoBolsa adesaoAterior) throws DAOException {
		inicializar(novaAdesao);
		
		QuestionarioRespostasDao questionarioRespostasDao = getDAO(QuestionarioRespostasDao.class);
		QuestionarioRespostas questionarioRespostas = questionarioRespostasDao.findByAdesao(adesaoAterior);

		// Preparar respostas que envolvam alternativas
		if (questionarioRespostas != null) {
			obj.setRespostas(questionarioRespostas.getRespostas());
			obj.resetarIdRespostas();
			
			questionarioRespostas.popularVisualizacaoRespostas();
			respostasModel = new ListDataModel( CollectionUtils.toList( questionarioRespostas.getRespostas() ));
		}
		
	}
	
	/**
	 * Popular respostas em branco de acordo com as perguntas do question�rio, para que
	 * possa ser respondidas
	 */
	private void popularRespostas() {
		List<Resposta> respostas = new ArrayList<Resposta>();
		
		for (PerguntaQuestionario pergunta : obj.getPerguntas() ) {
			Resposta resposta = new Resposta(obj, pergunta);
			respostas.add(resposta);
		}		
		
		respostasModel = new ListDataModel(respostas);
		obj.setRespostas(respostas);
	}

	/**
	 * Valida resposta do question�rio
	 * M�todo n�o invocado por JSP's
	 */
	public void validarRepostas() {
		if (obj == null) {
			return;
		}
		
		ListaMensagens listaMensagens = obj.validate();
		
		if (!listaMensagens.isEmpty()) {
			addMensagens(listaMensagens);
		}
	}

	public DataModel getRespostasModel() {
		return respostasModel;
	}

	public void setRespostasModel(DataModel respostasModel) {
		this.respostasModel = respostasModel;
	}

	/**
	 * Popular respostas cadastradas durante uma inscri��o de processo seletivo
	 * M�todo n�o invocado por JSP's
	 * 
	 * @param inscricao
	 * @throws DAOException 
	 * @throws HibernateException 
	 */
	public void popularVizualicacaoRespostas(InscricaoSelecao inscricao) throws HibernateException, DAOException {
		QuestionarioRespostasDao questionarioRespostasDao = getDAO(QuestionarioRespostasDao.class);
		if (inscricao!=null)
			obj = questionarioRespostasDao.findByInscricaoSelecao(inscricao);

		// Preparar respostas que envolvam alternativas
		if (obj != null) {
			obj.popularVisualizacaoRespostas();
			respostasModel = new ListDataModel( CollectionUtils.toList( obj.getRespostas() ));
		}else{
		//Caso n�o tenha ainda respondido o question�rio  	
			List<PerguntaQuestionario> lstPQ = new ArrayList<PerguntaQuestionario>();
			if(inscricao.getProcessoSeletivo().getQuestionario() != null){
			
				Collection<PerguntaQuestionario> colecaoPQ = 
				getGenericDAO().findByExactField(PerguntaQuestionario.class, "questionario.id",
				inscricao.getProcessoSeletivo().getQuestionario().getId());
			
				for (PerguntaQuestionario pq : colecaoPQ)
					lstPQ.add(pq);
				
				inscricao.getProcessoSeletivo().getQuestionario().setPerguntas(lstPQ);
				
			}
			inicializar(inscricao);
		}	
	}

	/**
	 * Popular respostas cadastradas durante uma ades�o ao cadastro �nico
	 * JSP: N�o Invocado por JSP
	 * 
	 * @param adesao
	 * @throws HibernateException
	 * @throws DAOException
	 */
	public void popularVizualicacaoRespostas(AdesaoCadastroUnicoBolsa adesao) throws HibernateException, DAOException {
		QuestionarioRespostasDao questionarioRespostasDao = getDAO(QuestionarioRespostasDao.class);
		if (adesao!=null)
			obj = questionarioRespostasDao.findByAdesao(adesao);

		// Preparar respostas que envolvam alternativas
		if (obj != null) {
			obj.popularVisualizacaoRespostas();
			respostasModel = new ListDataModel( CollectionUtils.toList( obj.getRespostas() ));
		}
	}
	
	/**
	 * Popular respostas cadastradas
	 * JSP: N�o Invocado por JSP
	 * @param questionario
	 * @throws DAOException 
	 */
	public void popularVizualicacaoRespostas(QuestionarioRespostas questionarioRespostas) throws DAOException {
		QuestionarioRespostasDao questionarioRespostasDao = getDAO(QuestionarioRespostasDao.class);
		obj = questionarioRespostasDao.findById(questionarioRespostas.getId());	
		
		if (obj != null) {
			obj.popularVisualizacaoRespostas();
			respostasModel = new ListDataModel( CollectionUtils.toList( obj.getRespostas() ));
		}
	}	
	
	/**
	 * Retorna o endere�o (URL) para baixar e exportar o arquivos texto para PDF.
	 * M�todo chamado pela(s) seguinte(s) JSP(s):
	 * <br/> 
	 * <ul>
	 * 	<li>/sigaa.war/public/processo_seletivo/form_inscricao.jsp</li>
	 * </ul> 
	 * @return
	 */
	public String getEnderecoExportarPDF(){
		return ParametroHelper.getInstance().getParametro( ParametrosGerais.ENDERECO_EXPORTAR_FORMATO_PDF );
	}
	
}
