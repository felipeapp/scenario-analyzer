/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica - UFRN
 * Diretoria de Sistemas
 *
 * Created on 26/09/2006
 *
 */
package br.ufrn.sigaa.ensino.tecnico.negocio;

import java.rmi.RemoteException;
import java.util.Collection;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.StringUtils;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.ensino.tecnico.dominio.ModuloDisciplina;

/**
 * Processador para cadastrar m�dulo do ensino t�cnico.
 *
 * @author Andre M Dantas
 *
 */
public class ProcessadorModuloTecnico extends ProcessadorCadastro {

	/**
	 * M�todo respons�vel pela execu��o do processador do M�dulo T�cnico.
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException, RemoteException {
		validate(mov);

		if (mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MODULO_TECNICO)) {
			return criar((MovimentoCadastro) mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_MODULO_TECNICO)) {
			return atualizar(mov);
		}

		return null;
	}

	@Override
	protected Object criar(MovimentoCadastro mov) throws NegocioException, ArqException {
		gerarCodigo(mov);
		return super.criar(mov);
	}

	/**
	 * Respons�vel por gera o c�digo do M�dulo.
	 * @param mov
	 * @throws DAOException
	 */
	private void gerarCodigo(MovimentoCadastro mov) throws DAOException {
		Modulo modulo = (Modulo) mov.getObjMovimentado();
		ModuloDao dao = getDAO(ModuloDao.class, mov);
		Modulo ultimo = dao.findUltimoCodigo(modulo.getUnidade().getId(), modulo.getNivel());
		Integer maxCodigo = 0;
		if (ultimo != null)
			maxCodigo = StringUtils.extractInteger(ultimo.getCodigo());
		maxCodigo++;
		modulo.setCodigo("MOD" + UFRNUtils.completaZeros(maxCodigo, 4));
		dao.close();
	}

	/**
	 * Atualizando objeto do m�dulo.
	 *
	 * @param mov
	 * @throws DAOException
	 */
	private Modulo atualizar(Movimento mov) throws DAOException {
		ModuloMov modMov = (ModuloMov) mov;
		Modulo modulo = (Modulo) modMov.getObjMovimentado();
		Collection<ModuloDisciplina> modDiscRemovidas = modMov.getModulosDisciplinasRemovidos();

		GenericDAO dao = getGenericDAO(mov);
		try {
			/*
			 * removendo individualmente e manualmente os elem. da cole��o de
			 * moduloDisciplinas
			 */
			if (modDiscRemovidas != null) {
				for (ModuloDisciplina modDisc : modDiscRemovidas) {
					dao.remove(modDisc);
				}
			}

			dao.update(modulo);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.close();
		}
		return modulo;
	}

	/**
	 * Respons�vel pela valida��o do M�dulo.
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {

		ModuloMov modMov = (ModuloMov) mov;
		Modulo modulo = (Modulo) modMov.getObjMovimentado();
		ListaMensagens erros = new ListaMensagens();

		/*
		 * valida CH com as disciplinas adicionadas. n�o pode ser maior que a CH
		 * do modulo
		 */
		int chTotal = 0;
		for (ComponenteCurricular disc : modulo.getDisciplinas()) {
			chTotal += disc.getChAula() + disc.getChEstagio() + disc.getChLaboratorio();
		}
		if (chTotal > modulo.getCargaHoraria()) {
			erros.addMensagem(new MensagemAviso("A soma das cargas hor�rias das disciplinas adicionadas "
					+ "� maior que a carga hor�ria m�xima do m�dulo.", TipoMensagemUFRN.ERROR));
		}


		checkValidation(erros);

	}

}
