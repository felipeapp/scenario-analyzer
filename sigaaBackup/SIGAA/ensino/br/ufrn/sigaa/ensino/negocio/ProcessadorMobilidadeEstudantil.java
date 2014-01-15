/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica
 * Diretoria de Sistemas
 * 
 * Created on '30/06/2010'
 *
 */

package br.ufrn.sigaa.ensino.negocio;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.dominio.PersistDB;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.MobilidadeEstudantil;
import br.ufrn.sigaa.pessoa.dominio.Pais;

/**
 * Processador para opera��es relacionadas a Mobilidade Estudantil
 * 
 * @author Bernardo
 *
 */
public class ProcessadorMobilidadeEstudantil extends AbstractProcessador {

	/**
	 * Invoca a l�gica de neg�cio
	 */
	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		MovimentoCadastro mov = (MovimentoCadastro) movimento;
		Object obj = null;
		
		if(mov.getCodMovimento().equals(SigaaListaComando.CADASTRAR_MOBILIDADE_ESTUDANTIL)){
			validate(mov);
			obj = cadastrar(mov);
		} else if(mov.getCodMovimento().equals(SigaaListaComando.ALTERAR_MOBILIDADE_ESTUDANTIL)){
			validate(mov);
			obj = alterar(mov);
		} else if (mov.getCodMovimento().equals(SigaaListaComando.CANCELAR_MOBILIDADE_ESTUDANTIL)){
			obj = alterar(mov);
		}
		
		return obj;
	}

	/**
	 * M�todo auxiliar para o cadastro de uma nova Mobilidade Estudantil.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private Object cadastrar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			dao.create(mov.getObjMovimentado());
		} finally {
			dao.close();
		}
		return mov.getObjMovimentado();
	}
	
	
	/**
	 * M�todo auxiliar para a altera��o de uma Mobilidade Estudantil.
	 * 
	 * @param mov
	 * @return
	 * @throws DAOException
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private Object alterar(MovimentoCadastro mov) throws DAOException, NegocioException, ArqException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			dao.update(mov.getObjMovimentado());
		} finally {
			dao.close();
		}
		return mov.getObjMovimentado();
	}

	/**
	 * M�todo para valida��o dos dados passados e lan�amento das respectivas mensagens de erro.
	 * 
	 */
	@Override
	public void validate(Movimento mov) throws NegocioException, ArqException {
		@SuppressWarnings("unchecked")
		ArrayList<PersistDB> objMovimentados = (ArrayList<PersistDB>) ((MovimentoCadastro)mov).getColObjMovimentado();
		MobilidadeEstudantil obj = (MobilidadeEstudantil) objMovimentados.get(0);
		@SuppressWarnings("unchecked")
		List<MobilidadeEstudantil> historicoMovimentacoes = (List<MobilidadeEstudantil>) ((MovimentoCadastro)mov).getObjAuxiliar();
		CalendarioAcademico calendarioVigente = (CalendarioAcademico) objMovimentados.get(1);
		
		if (obj.getId() == 0) {
			if (obj.getAno().intValue() < calendarioVigente.getAno() || (obj.getAno().intValue() == calendarioVigente.getAno() && obj.getPeriodo().intValue() < calendarioVigente.getPeriodo()))
				throw new NegocioException("O Ano e Per�odo devem ser maior ou igual ao vigente.");		
		}
		
			
		if (historicoMovimentacoes != null){
			for (MobilidadeEstudantil mob : historicoMovimentacoes){
				/* Faz-se a verifica��o baseado nos per�odos de mobilidade estudantil e n�o apenas no per�odo de in�cio.*/
				Integer periodoMobIni = DiscenteHelper.somaSemestres(mob.getAno(), mob.getPeriodo(), 0);
				Integer periodoMobFim = DiscenteHelper.somaSemestres(mob.getAno(), mob.getPeriodo(), mob.getNumeroPeriodos()-1);
				Integer periodoObjIni = DiscenteHelper.somaSemestres(obj.getAno(), obj.getPeriodo(), 0);
				Integer periodoObjFim = DiscenteHelper.somaSemestres(obj.getAno(), obj.getPeriodo(), obj.getNumeroPeriodos()-1);
				if ( mob.isAtivo() && obj.getId() != mob.getId() 
						&& ( (periodoMobIni <= periodoObjIni && periodoObjIni <= periodoMobFim)
							|| (periodoMobIni <= periodoObjFim && periodoObjFim <= periodoMobFim) 
							|| (periodoObjIni <= periodoMobIni && periodoObjFim >= periodoMobFim) )	
						){		
					throw new NegocioException("J� existe uma Mobilidade Estudantil cadatrada para o Per�odo de Mobilidade Informados.");
				}
			}					
		}
		
		if (obj.getTipo() == MobilidadeEstudantil.INTERNA){
			if (isEmpty(obj.getDiscente().getMatrizCurricular().getCampus())){
				throw new NegocioException("A Matriz Curricular n�o possui v�nculo com nenhum Campus.");
			}
			//verifica se o campus de origem � igual a de destino
			if (obj.getDiscente().getMatrizCurricular().getCampus().equals(obj.getCampusDestino())){
				throw new NegocioException("Campus de Destino n�o pode ser igual ao de Origem.");
			}
			//seta o campus de origem do discente.
			obj.setCampusOrigem(obj.getDiscente().getMatrizCurricular().getCampus());			
			obj.setPaisExterna(null);
		} else {
			obj.setCampusDestino(null);
			obj.setCampusOrigem(null);
			if (obj.getSubtipo() == MobilidadeEstudantil.NACIONAL){
				obj.setPaisExterna(new Pais(Pais.BRASIL));
			}			
		}
	}

}