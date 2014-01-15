/*
 * Sistema Integrado de Gest�o de Atividades Acad�micas
 * Superintend�ncia de Inform�tica - UFRN
 *
 * Created on Sep 12, 2007
 *
 */
package br.ufrn.sigaa.ensino.negocio;

import java.rmi.RemoteException;

import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.ComponenteCurricularProgramaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricularPrograma;

/**
 * Processador que realiza o cadastro e atualiza��o de programas de componentes curriculares.
 *
 * @author Victor Hugo
 * @author Leonardo Campos
 *
 */
public class ProcessadorProgramaComponente extends ProcessadorCadastro {

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#execute(Movimento)
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		validate(movimento);
		MovimentoCadastro mov = (MovimentoCadastro) movimento;

		if( mov.getCodMovimento() == SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE ){
			cadastrarPrograma(mov);
		} else if( mov.getCodMovimento() == SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE ){
			alterarPrograma(mov);
		}

		return null;
	}

	/**
	 * Atualiza as informa��es do programa e atualiza o respectivo componente curricular
	 *
	 * @param mov
	 * @throws ArqException
	 * @throws NegocioException
	 */
	private void alterarPrograma(MovimentoCadastro mov) throws NegocioException, ArqException {
		alterar(mov);
		atualizarReferenciaPrograma(mov);
	}

	/**
	 * Atualiza a refer�ncia do componente curricular para o programa presente no movimento
	 * @param mov
	 * @throws DAOException
	 */
	private void atualizarReferenciaPrograma(MovimentoCadastro mov)
			throws DAOException {
		ComponenteCurricularPrograma programa = (ComponenteCurricularPrograma) mov.getObjMovimentado();

		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class, mov);
		try {
			dao.updateField(ComponenteCurricular.class,
				programa.getComponenteCurricular().getId(),
				"programa", programa.getId());
		} finally {
			dao.close();
		}
	}

	/**
	 * Cadastra o novo programa e atualiza o respectivo componente curricular
	 *
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void cadastrarPrograma(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		criar(mov);
		atualizarReferenciaPrograma(mov);
	}

	/**
	 * @see br.ufrn.arq.negocio.ProcessadorCadastro#validate(Movimento)
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		checkRole(new int[]{SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO, SigaaPapeis.CHEFE_DEPARTAMENTO, SigaaPapeis.SECRETARIA_DEPARTAMENTO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.SECRETARIA_LATO}, mov);
		MovimentoCadastro movc = (MovimentoCadastro) mov;
		ListaMensagens lista = new ListaMensagens();

		ComponenteCurricularProgramaDao dao = getDAO(ComponenteCurricularProgramaDao.class, mov);
		try {
			ComponenteCurricularPrograma programa = (ComponenteCurricularPrograma) movc.getObjMovimentado();
			
			// Buscar se existe programa cadastrado no ano-per�odo informado
			ComponenteCurricularPrograma programaAnoPeriodo = dao.findAtualByComponente(programa.getComponenteCurricular().getId(), programa.getAno(), programa.getPeriodo());
			
			if(programaAnoPeriodo != null && movc.getCodMovimento() == SigaaListaComando.CADASTRAR_PROGRAMA_COMPONENTE ){
				lista.addErro("N�o � poss�vel cadastrar o programa pois j� existe um programa cadastrado para o componente curricular no ano-per�odo informado.");
			} else if(programaAnoPeriodo == null && movc.getCodMovimento() == SigaaListaComando.ALTERAR_PROGRAMA_COMPONENTE ){
				lista.addErro("N�o � poss�vel atualizar o programa pois n�o existe um programa cadastrado para o componente curricular no ano-per�odo informado.");
			}
			checkValidation(lista);
		} finally {
			dao.close();
		}
	}

}
