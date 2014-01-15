/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.util.ArrayList;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.Curso;

/**
 * Processador quer realiza a valida��o de objetos Curso
 * 
 * @author Andre M Dantas
 *
 */
public class ProcessadorCurso extends ProcessadorCadastro {

	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);

		ArrayList<MensagemAviso> erros = new ArrayList<MensagemAviso>();
		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_CURSO)) {
			// checar se j� existe o c�digo da UFRN
			CursoDao dao = new CursoDao();
			try {
				MovimentoCadastro cadMov = (MovimentoCadastro) mov;
				Curso curso = (Curso) cadMov.getObjMovimentado();
				dao.findByCodigo(curso.getCodigo(), curso.getUnidade().getId(),
						Curso.class, null);
			} finally {
				dao.close();
			}
		}
		checkValidation(erros);

	}
}
