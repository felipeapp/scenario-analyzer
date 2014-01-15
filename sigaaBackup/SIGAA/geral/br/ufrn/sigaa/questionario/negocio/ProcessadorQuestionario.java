/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Nov 22, 2007
 *
 */
package br.ufrn.sigaa.questionario.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateEmptyCollection;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.InscricaoSelecaoDao;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.questionario.dominio.Alternativa;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.Questionario;

/**
 * Processador respons�vel pela manuten��o dos question�rio na base de dados.
 * @author Victor Hugo
 *
 */
public class ProcessadorQuestionario extends AbstractProcessador {

	public Object execute(Movimento movimento) throws DAOException, NegocioException {

		MovimentoQuestionario mov = (MovimentoQuestionario) movimento;

		if (mov.getCodMovimento() == SigaaListaComando.CADASTRAR_QUESTIONARIO) {
			validate(mov);
			cadastrarQuestionario(mov);
		} else if (mov.getCodMovimento() == SigaaListaComando.REMOVER_QUESTIONARIO) {
			validate(mov);
			removerQuestionario(mov);
		}


		return null;
	}


	/**
	 * Altera o status do question�rio para inativo, indisponibilizando sua visualiza��o
	 * na view.
	 * @param mov
	 * @throws DAOException
	 */
	private void removerQuestionario(MovimentoQuestionario mov) throws DAOException {

		Questionario questionario = mov.getObjMovimentado();
		GenericDAO dao = null;
		try{
			dao = getGenericDAO(mov);
			questionario.setAtivo(false);
			dao.update(questionario);
		}finally{
			if(dao != null) dao.close();
		}
	}

	/**
	 * Altera/Inseri um question�rio de um processo seletivo, removendo ou adicionando perguntas 
	 * reordenando-as.
	 * @param mov
	 * @throws DAOException
	 */
	private void cadastrarQuestionario(MovimentoQuestionario mov) throws DAOException {
		Questionario questionario = mov.getObjMovimentado();


		questionario.atualizarOrdemPerguntasEAlternativas();

		GenericDAO dao = null;
		
		try{
			dao = getGenericDAO(mov);
			
			if( questionario.getId() == 0 ){
				/* cadastrando novo question�rio */
				dao.create(questionario);
			} else{
				/* atualizando question�rio existente */
				if( !isEmpty( mov.getPerguntasRemover() ) ){
					for( PerguntaQuestionario p : mov.getPerguntasRemover() ){
						dao.updateField(p.getClass(), p.getId(), "ativo", false);
					}
				}
				if( !isEmpty( mov.getAlternativasRemover() ) ){
					for( Alternativa a : mov.getAlternativasRemover() ){
						dao.updateField(a.getClass(), a.getId(), "ativo", false);
					}
				}
				dao.update(questionario);
			}
		}finally{
			if(dao != null) dao.close();
		}
	}

	
	
	/** (non-Javadoc)
	 * @throws DAOException 
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, DAOException {
		ListaMensagens erros = new ListaMensagens();
		Questionario questionario = ((MovimentoQuestionario) mov) .getObjMovimentado();
		
		validateEmptyCollection( "� necess�rio adicionar pelo menos uma pergunta a este question�rio", questionario.getPerguntas() , erros);
		
		/* Verifica se o question�rio est� associado a um processo seletivo
		 com inscri��es abertas e que j� possui inscritos */
		if(questionario.getId()>0){
			int qtdInscritos = getDAO(InscricaoSelecaoDao.class, mov).findQtdInscritosByQuestionario(questionario.getId());
			if(qtdInscritos>0)
				erros.addErro("N�o � poss�vel alterar/remover um question�rio que " +
					"esteja associado a um processo seletivo aberto com inscri��es cadastradas.");
			
			QuestionarioDao iaDao = null;
			try{
				iaDao = getDAO(QuestionarioDao.class, mov);
				int qtdInscritosAtividades = iaDao.findQtdInscritosByQuestionario(questionario.getId());
				if(qtdInscritosAtividades>0)
					erros.addErro("N�o � poss�vel alterar/remover um question�rio que " +
						"esteja associado a uma inscri��o de atividade aberta e com inscri��es cadastradas.");
			}finally{
				if(iaDao != null) iaDao.close();
			}
		}
		
		if ( (mov.getCodMovimento() == SigaaListaComando.REMOVER_QUESTIONARIO) && 
			getDAO(QuestionarioDao.class, mov).questionarioJaRespondido(questionario.getId()) ) {
			erros.addErro("N�o � poss�vel remover o question�rio, pois j� existe respostas cadastradas.");
		}
		
		checkValidation(erros);
	}

}
