/*
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
*
* Created on 30/06/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.negocio;

import java.rmi.RemoteException;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Processador que realiza a persist�ncia de objetos MatriculaComponente
 * para implanta��o de hist�rico.
 *
 * @author Arlindo
 *
 */
public class ProcessadorImplantarHistoricoMedio extends AbstractProcessador {

	@Override
	public Object execute(Movimento movimento) throws NegocioException, ArqException,
			RemoteException {
		
		validate(movimento);

		MovimentoImplantarHistoricoMedio mov = (MovimentoImplantarHistoricoMedio) movimento;
		GenericDAO dao = getGenericDAO(mov);
		try {
			Discente d = mov.getDiscente();
	
			for( MatriculaComponente mc : mov.getMatriculas() ){
				mc.setDataCadastro( new Date() );
				mc.setRegistroEntrada( mov.getUsuarioLogado().getRegistroEntrada() );
				mc.setDiscente(d);
	
				mc.setNotas(null);
				mc.setRecuperacao(null);
				mc.setPeriodo((byte)0);
	
				dao.create(mc);
			}
	
			return mov.getMatriculas();
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	@Override
	public void validate(Movimento movimento) throws NegocioException, ArqException {
		MovimentoImplantarHistoricoMedio mov = (MovimentoImplantarHistoricoMedio) movimento;
		ListaMensagens erros = new ListaMensagens();
		validarMatriculaComponenteImplantacao(movimento, mov.getDiscente(), mov.getMatriculas(), null, erros);
		
		checkValidation(erros);		
	}
	
	/**
	 * 
	 * Utilizado para validar a matr�culas de um discente
	 * 
	 * @param discente
	 * @param matriculas
	 * @param situacaoNova
	 * @param erros
	 * @throws ArqException
	 */
	public void validarMatriculaComponenteImplantacao(Movimento mov, DiscenteAdapter discente, Collection<MatriculaComponente> matriculas,
				SituacaoMatricula situacaoNova, ListaMensagens erros) throws ArqException {

		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class, mov);
		try {
			Collection<MatriculaComponente> pagosEMatriculados = dao.findBySituacoes(discente, SituacaoMatricula.getSituacoesImplantadasMedio());
			
			for (MatriculaComponente mat : matriculas) {
				
				/**
				 * Se n�o j� pagou (ou est� matriculado) no mesmo componente e s�rie.
				 */
				for (MatriculaComponente pag : pagosEMatriculados) {
					if (mat.getComponente().getId() == pag.getComponente().getId() && mat.getId() != pag.getId() && mat.getAno().equals(pag.getAno())
							||( mat.getSerie().getNumero() >= pag.getSerie().getNumero() && pag.getSituacaoMatricula().getId() == SituacaoMatricula.MATRICULADO.getId())) {
						erros.addErro("O componente " + mat.getComponenteDescricaoResumida() + " n�o pode ser pago ou matriculado."
								+ "<Br>O discente j� pagou ou est� matriculado no mesmo");
						return;
					}
					if(mat.getSerie().getNumero() > pag.getSerie().getNumero() && mat.getAno() <= pag.getAno()){
						erros.addErro("J� existe uma serie anterior a esta cadastrada para o ano "+ pag.getAno() +", selecione um ano posterior a este.");
						return;
					}
				}
			}
		} finally {
			dao.close();
		}
	}

}
