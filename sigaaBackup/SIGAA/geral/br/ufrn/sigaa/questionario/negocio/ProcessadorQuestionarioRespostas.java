/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas - SIGAA
 * Superintendência de Informática - UFRN
 *
 * Created on '28/10/2008'
 *
 */
package br.ufrn.sigaa.questionario.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.arquivos.EnvioArquivoHelper;
import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.questionario.QuestionarioRespostasDao;
import br.ufrn.sigaa.questionario.dominio.PerguntaQuestionario;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;

/**
 * Processador responsável pela persistência das respostas
 * fornecidas por usuário a questionários cadastrados no sistema
 * 
 * @author wendell
 *
 */
public class ProcessadorQuestionarioRespostas extends AbstractProcessador{

	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		return null;
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {
		
	}

	/**
	 * Persistir as respostas informadas pelo usuário
	 * 
	 * @param mov
	 * @param questionarioRespostas
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void cadastrarRespostas(Movimento mov, QuestionarioRespostas questionarioRespostas) throws NegocioException, ArqException {
		cadastrarRespostas(mov, questionarioRespostas, true);
	}
	/**
	 * Persistir as respostas informadas pelo usuário
	 * 
	 * @param mov
	 * @param questionarioRespostas
	 * @throws NegocioException 
	 * @throws ArqException 
	 */
	public void cadastrarRespostas(Movimento mov, QuestionarioRespostas questionarioRespostas, boolean validaAntesCadastrar) throws NegocioException, ArqException {
		if (validaAntesCadastrar)
			validate(mov, questionarioRespostas);
		QuestionarioRespostasDao questionarioRespostasDao =  getDAO(QuestionarioRespostasDao.class, mov);
		try {
			// Persistir respostas
			questionarioRespostasDao.createOrUpdate( questionarioRespostas ); 
			//Persisti arquivos se necessário
			atualizaArquivosResposta(questionarioRespostas, questionarioRespostasDao);
		} catch (Exception e) {
			e.printStackTrace();
			// Não silencie o erro !!!!!!!!!!
			throw new NegocioException("Aconteceu um erro ao tentar cadastrar as respostas do questionário. Por favor, entre em contado com o suporte.");
		} finally {
			questionarioRespostasDao.close();
		}
	}
	
	/**
	 * Persisti os arquivos para as pergunta do tipo {@link PerguntaQuestionario#ARQUIVO}
	 * @param questionarioRespostas
	 * @param dao
	 * @throws IOException
	 * @throws DAOException 
	 */
	private void atualizaArquivosResposta(QuestionarioRespostas questionarioRespostas, 
			QuestionarioRespostasDao dao) throws IOException, DAOException{
		for (Resposta r : questionarioRespostas.getRespostas()) {
			if( r.getPergunta().isArquivo() && !isEmpty( r.getArquivo() ) ){
				int idArquivo = !isEmpty(r.getRespostaArquivo()) ? r.getRespostaArquivo() : EnvioArquivoHelper.getNextIdArquivo();
				EnvioArquivoHelper.inserirArquivo(idArquivo,
						r.getArquivo().getBytes(),
						r.getArquivo().getContentType(),
						r.getArquivo().getName());
				r.setRespostaArquivo( idArquivo );
				dao.updateNoFlush(r);
			}
		}
	}
	
	/**
	 * Atualiza somente as respostas
	 * 
	 * @param mov
	 * @param respostas
	 * @throws NegocioException
	 * @throws ArqException
	 */
	public void atualizarRespostas(Movimento mov, Collection<Resposta> respostas) throws NegocioException, ArqException {
		
		GenericDAO dao = getGenericDAO(mov);
		try {
			for (Resposta resposta : respostas) {
				dao.update(resposta);
			}
		} catch (Exception e) {
			e.printStackTrace();
			// Não silencie o erro !!!!!!!!!!
			throw new NegocioException("Aconteceu um erro ao tentar atualizar as respostas do questionário. Por favor, entre em contado com o suporte.");
		} finally {
			dao.close();
		}
	}	
	
	/**
	 * Validar as respostas fornecidas pelo usuário
	 * 
	 * @param mov
	 * @param questionarioRespostas
	 * @throws NegocioException
	 * @throws ArqException 
	 */
	private  void validate( Movimento mov, QuestionarioRespostas questionarioRespostas) throws NegocioException, ArqException {
		ListaMensagens erros =  questionarioRespostas.validate();
		
		// Validar identificação do candidato do processo seletivo, quando for o caso
		if ( questionarioRespostas.getQuestionario() != null 
				&& questionarioRespostas.getQuestionario().isProcessoSeletivo() 
				&& questionarioRespostas.getInscricaoSelecao() == null) {
			throw new ArqException("A identificação do candidato não foi realizada pelo sistema!");
		}
		
		checkValidation(erros);
	}
	
}
