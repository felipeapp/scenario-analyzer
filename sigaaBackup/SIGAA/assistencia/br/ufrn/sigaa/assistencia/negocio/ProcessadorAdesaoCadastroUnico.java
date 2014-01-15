package br.ufrn.sigaa.assistencia.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.assistencia.cadunico.dominio.AdesaoCadastroUnicoBolsa;
import br.ufrn.sigaa.questionario.dominio.QuestionarioRespostas;
import br.ufrn.sigaa.questionario.dominio.Resposta;
import br.ufrn.sigaa.questionario.negocio.ProcessadorQuestionarioRespostas;

/**
 * Processador responsável pelas ações de adesão ao cadastro único
 * 
 * @author Henrique André
 *
 */
public class ProcessadorAdesaoCadastroUnico extends AbstractProcessador {

	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {

		MovimentoAdesaoCadastroUnicoBolsa movCu = (MovimentoAdesaoCadastroUnicoBolsa) mov;
		
		if (movCu.getCodMovimento().equals(SigaaListaComando.ADESAO_CADASTRO_UNICO)) {
			cadastrar(movCu);
		} else if (movCu.getCodMovimento().equals(SigaaListaComando.ALTERAR_ADESAO_CADASTRO_UNICO)) {
			alterar(movCu);
		}

		return movCu.getAdesaoCadatro();
	}

	/**
	 * Altera uma adesão já existente
	 * 
	 * @param movCu
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterar(MovimentoAdesaoCadastroUnicoBolsa movCu) throws ArqException, NegocioException {
		AdesaoCadastroUnicoBolsa adesao = movCu.getAdesaoCadatro();
		QuestionarioRespostas respostas = movCu.getRespostas();		
		
		GenericDAO dao = getGenericDAO(movCu);

		/** evita lazy */
		for (Resposta r : respostas.getRespostas()) {
			r.setAlternativa(dao.refresh(r.getAlternativa()));
		}
		
		int pontuacao = PontuacaoCadastroUnicoHelper.geraPontuacao(respostas, adesao.getListaConfortoFamiliar());
		
		adesao.setPontuacao(pontuacao);

		/**
		 * Atualiza a adesão
		 */		
		dao.update(adesao);

		/**
		 * Atualiza as respostas
		 */
		ProcessadorQuestionarioRespostas proc = new ProcessadorQuestionarioRespostas();
		proc.atualizarRespostas(movCu, respostas.getRespostas());
	}

	/**
	 * Insere uma nova adesão
	 * 
	 * @param movCu
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void cadastrar(MovimentoAdesaoCadastroUnicoBolsa movCu) throws ArqException, NegocioException {
		
		AdesaoCadastroUnicoBolsa adesao = movCu.getAdesaoCadatro();
		QuestionarioRespostas respostas = movCu.getRespostas();		
		
		GenericDAO dao = getGenericDAO(movCu);

		/** evita lazy */
		for (Resposta r : respostas.getRespostas()) {
			r.setAlternativa(dao.refresh(r.getAlternativa()));
		}
		
		int pontuacao = PontuacaoCadastroUnicoHelper.geraPontuacao(respostas, adesao.getListaConfortoFamiliar());
		adesao.setPontuacao(pontuacao);

		dao.detach(adesao.getContatoFamilia());
		
		if (adesao.getContatoFamilia().getEndereco().getId() == 0)
			dao.create(adesao.getContatoFamilia().getEndereco());
		else
			dao.refresh(adesao.getContatoFamilia().getEndereco());
		
		/**
		 * Realiza o cadastro
		 */		
		dao.create(adesao);

		/**
		 * Cadastra as respostas
		 */
		dao.detach(respostas);
		ProcessadorQuestionarioRespostas proc = new ProcessadorQuestionarioRespostas();
		proc.cadastrarRespostas(movCu, respostas);
	}

	public void validate(Movimento mov) throws NegocioException, ArqException {

	}

}
