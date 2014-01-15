package br.ufrn.sigaa.prodocente.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.ProcessadorCadastro;
import br.ufrn.sigaa.arq.dao.prodocente.CHDedicadaResidenciaMedicaDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.prodocente.atividades.dominio.ProgramaResidenciaMedica;


/**
 * Processador responsável pelo registro dos programa de Residência Médica 
 *
 * @author Jean Guerethes
 */
public class ProcessadorProgramaResidenciaMedica extends ProcessadorCadastro {
	
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException, RemoteException {
		validate(movimento);

		if (movimento.getCodMovimento().equals(SigaaListaComando.CADASTRAR_RESIDENCIA_MEDICA))
			return criar((MovimentoCadastro) movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.ALTERAR_RESIDENCIA_MEDICA))
			return alterar((MovimentoCadastro) movimento);
		else if (movimento.getCodMovimento().equals(SigaaListaComando.REMOVER_RESIDENCIA_MEDICA))
			return remover((MovimentoCadastro) movimento);
		return null;
	}

	@Override
	protected Object remover(MovimentoCadastro mov) throws DAOException,
			NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		CHDedicadaResidenciaMedicaDao chDao = getDAO(CHDedicadaResidenciaMedicaDao.class, mov); 
		MovimentoCadastro programaMovimento = mov;
		ProgramaResidenciaMedica programa = (ProgramaResidenciaMedica) programaMovimento.getObjMovimentado();
		try {
			List<HashMap<String, Object>> lista = chDao.findResidenciaMedicaPorPrograma(programa.getId());
			
			if (!lista.isEmpty()) {
				List<Integer> idProgramaResidenciaMedica = new ArrayList<Integer>();
				 
				for (HashMap<String, Object> linha : lista)
					idProgramaResidenciaMedica.add((Integer) linha.get("id"));
				
				chDao.inativarCargaHorariaResidenciaMedica(idProgramaResidenciaMedica);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			dao.updateField(ProgramaResidenciaMedica.class, programa.getId(), "ativo", false);
			dao.close();
			chDao.close();
		}
		return null;
	}
	
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		super.validate(mov);
	}
	
}