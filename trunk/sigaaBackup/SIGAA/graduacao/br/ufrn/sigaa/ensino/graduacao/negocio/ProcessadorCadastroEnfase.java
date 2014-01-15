/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 08/12/2009
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.dominio.MovimentoCadastro;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.sigaa.arq.dao.graduacao.MatrizCurricularDao;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;
import br.ufrn.sigaa.ensino.graduacao.dominio.CurriculoComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.Enfase;
import br.ufrn.sigaa.ensino.graduacao.dominio.GrupoOptativas;
import br.ufrn.sigaa.ensino.graduacao.dominio.MatrizCurricular;
import br.ufrn.sigaa.ensino.graduacao.dominio.OptativaCurriculoSemestre;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * Processador responsável pelo cadastro de Ênfase.
 * @author Édipo Elder F. Melo
 *
 */
public class ProcessadorCadastroEnfase extends AbstractProcessador {

	/** Cadastra a ênfase.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#execute(br.ufrn.arq.dominio.Movimento)
	 */
	public Object execute(Movimento mov) throws NegocioException, ArqException,
			RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			switch (((MovimentoCadastro) mov).getAcao()) {
				case MovimentoCadastro.ACAO_ALTERAR: return setaEnfaseCurriculo(mov);
				case MovimentoCadastro.ACAO_CRIAR: return duplicaCurriculo(mov);
				default:
					throw new NegocioException("Ação não definida corretamente");
			}
			
		} finally {
			dao.close();
		}
	}
	
	/** Cria a ênfase duplicando o currículo.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Object duplicaCurriculo(Movimento mov) throws NegocioException, ArqException,
	RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			Curriculo curriculo = ((MovimentoCadastro) mov).getObjMovimentado();
			
			Enfase enfase = dao.refresh(curriculo.getMatriz().getEnfase());
			Curriculo novoCurriculo = dao.refresh(curriculo);
			MatrizCurricular novaMatriz = dao.refresh(curriculo.getMatriz());
			
			novoCurriculo.setEnfases(new ArrayList<GrupoOptativas>(novoCurriculo.getEnfases()));
			novoCurriculo.setCurriculoComponentes(new ArrayList<CurriculoComponente>(novoCurriculo.getCurriculoComponentes()));
			novoCurriculo.setCodigo(curriculo.getCodigo());
			novoCurriculo.setDataAlteracao(null);
			CalendarioAcademico cal = CalendarioAcademicoHelper.getCalendarioUnidadeGlobalGrad();
			novoCurriculo.setAnoEntradaVigor(cal.getAno());
			novoCurriculo.setPeriodoEntradaVigor(cal.getPeriodo());
			novaMatriz.setPossuiHabilitacao(false);
			novaMatriz.setHabilitacao(null);
			
			dao.detach(novaMatriz);
			dao.detach(novoCurriculo);
			
			novaMatriz.setEnfase(enfase);
			
			// Validando registros iguais para matrizes
			validarMatrizDuplicada(novaMatriz, mov);
			
			// duplicando os componentes curriculares do currículo
			if (novoCurriculo.getCurriculoComponentes() != null) { 
				for (CurriculoComponente cc : novoCurriculo.getCurriculoComponentes()) {
					dao.detach(cc);
					cc.setId(0);
				}
			}
			if (novoCurriculo.getOptativasCurriculoSemestre() != null) {
				for (OptativaCurriculoSemestre opt : novoCurriculo.getOptativasCurriculoSemestre()) {
					dao.detach(opt);
					opt.setId(0);
				}
			}
			if (novoCurriculo.getEnfases() != null) {
				for (GrupoOptativas opt : novoCurriculo.getEnfases()){
					dao.detach(opt);
					opt.setId(0);
				}
			}
			
			novaMatriz.setId(0);
			novoCurriculo.setId(0);
			novoCurriculo.setMatriz(novaMatriz);

			dao.create(novaMatriz);
			dao.create(novoCurriculo);
		} finally {
			dao.close();
		}
		return mov;
	}
	
	/** Cria a ênfase associando-a ao currículo.
	 * @param mov
	 * @return
	 * @throws NegocioException
	 * @throws ArqException
	 * @throws RemoteException
	 */
	private Object setaEnfaseCurriculo(Movimento mov) throws NegocioException, ArqException,
	RemoteException {
		GenericDAO dao = getGenericDAO(mov);
		try {
			Curriculo curriculo = ((MovimentoCadastro) mov).getObjMovimentado();
			Enfase enfase = curriculo.getMatriz().getEnfase();
			dao.create(enfase);
			
			curriculo = dao.refresh(curriculo);
			MatrizCurricular novaMatriz = dao.refresh(curriculo.getMatriz());
			
			dao.detach(novaMatriz);

			novaMatriz.setPossuiHabilitacao(false);
			novaMatriz.setHabilitacao(null);
			
			novaMatriz.setEnfase(enfase);
			novaMatriz.setPossuiEnfase(true);
			curriculo.setMatriz(novaMatriz);

			novaMatriz.setId(0);
			
			dao.create(novaMatriz);
			dao.update(curriculo);
		} finally {
			dao.close();
		}
		return mov;
	}

	/**
	 * Validando registros iguais para matrizes com ênfase.
	 * @param novaMatriz
	 * @param mov
	 * @throws DAOException
	 * @throws NegocioException
	 */
	public void validarMatrizDuplicada(MatrizCurricular novaMatriz, Movimento mov) throws DAOException, NegocioException{
		MatrizCurricularDao dao = getDAO(MatrizCurricularDao.class, mov);
		
		// Validando registros iguais para matrizes
		if (dao.jaExiste(novaMatriz)) {
			throw new NegocioException("Não foi possível criar a matriz curricular reaproveitando os dados da matriz de referência," +
					" porque já existe uma matriz curricular ativa com mesmo curso, turno e grau acadêmico, para a ênfase selecionada e as demais informações");
		}
	}
	
	/** Valida os dados da ênfase.
	 * @see br.ufrn.arq.negocio.ProcessadorComando#validate(br.ufrn.arq.dominio.Movimento)
	 */
	public void validate(Movimento mov) throws NegocioException, ArqException {
		Curriculo curriculo = ((MovimentoCadastro) mov).getObjMovimentado();
		if (curriculo.getMatriz().getEnfase().validate().isErrorPresent())
			throw new NegocioException();
	}

}
