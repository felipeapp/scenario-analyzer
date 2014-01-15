/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática - UFRN
 * Diretoria de Sistemas
 *
 * Created on 06/01/2007
 *
 */
package br.ufrn.sigaa.ensino.graduacao.negocio;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.dominio.Movimento;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.negocio.AbstractProcessador;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.sigaa.arq.dao.DiscenteDao;
import br.ufrn.sigaa.arq.dao.ParametrosGestoraAcademicaDao;
import br.ufrn.sigaa.arq.dao.graduacao.DiscenteGraduacaoDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.IntegralizacoesHelper;
import br.ufrn.sigaa.ensino.graduacao.negocio.calculos.PerfilInicialFactory;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Processador responsável pelo cadastro de aproveitamentos automático
 * de componentes curriculares de um dado discente.
 * 
 * @author Victor Hugo
 *
 */
public class ProcessadorAproveitamentoAutomatico extends AbstractProcessador {

	public Object execute(Movimento movi) throws NegocioException, ArqException, RemoteException {
		
		MovimentoAproveitamentoAutomatico mov = (MovimentoAproveitamentoAutomatico) movi;
		
		if (mov.getCodMovimento().equals(SigaaListaComando.ANALISAR_MATRICULAS_APROVEITAMENTO_AUTOMATICO))
			return analisarMatriculas(mov);
		else {
			Collection<MatriculaComponente> matriculasZeradas = new ArrayList<MatriculaComponente>(0);
			DiscenteGraduacao discente = mov.getDiscenteDestino();
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametros(discente);
			
			for (MatriculaComponente mat : mov.getMatriculas()) {
				MatriculaComponente novaMatricula = new MatriculaComponente();
				novaMatricula.setDiscente( discente.getDiscente() );
				novaMatricula.setComponente(mat.getComponente());
				novaMatricula.setDetalhesComponente(mat.getDetalhesComponente());
				novaMatricula.setConceito(mat.getConceito());
				novaMatricula.setMediaFinal(param.isExigeNotaAproveitamento() ? mat.getMediaFinal() : null);
				novaMatricula.setNumeroFaltas(mat.getNumeroFaltas());
				if (mat.getSituacaoMatricula().getId() == SituacaoMatricula.APROVADO.getId())
					novaMatricula.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_CUMPRIU);
				if (mat.getSituacaoMatricula().getId()== SituacaoMatricula.APROVEITADO_TRANSFERIDO.getId())
					novaMatricula.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_TRANSFERIDO);
				if (mat.getSituacaoMatricula().getId()== SituacaoMatricula.APROVEITADO_CUMPRIU.getId())
					novaMatricula.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_CUMPRIU);
				if (mat.getSituacaoMatricula().getId() == SituacaoMatricula.APROVEITADO_DISPENSADO.getId())
					novaMatricula.setSituacaoMatricula(SituacaoMatricula.APROVEITADO_DISPENSADO);
				novaMatricula.setAno( discente.getAnoIngresso().shortValue() );
				novaMatricula.setPeriodo( discente.getPeriodoIngresso().byteValue() );
				novaMatricula.setDataCadastro(new Date());
				novaMatricula.setNotas(null);
				novaMatricula.setRecuperacao(null);

				matriculasZeradas.add(novaMatricula);
			}

			mov.setMatriculas(matriculasZeradas);
			validate(movi);


			aproveitamentoAutomatico(mov);
		}

		return null;
	}

	private Collection<MatriculaComponente> analisarMatriculas(MovimentoAproveitamentoAutomatico mov) throws ArqException {
		IntegralizacoesHelper.analisarTipoIntegralizacao(mov.getDiscenteDestino(), mov.getMatriculas(), mov);
		return mov.getMatriculas();
	}

	/**
	 * Método auxiliar responsável por cálculos secundários 
	 * necessários ao cadastro de aproveitamento automático. 
	 * 
	 * @param mov
	 * @throws NegocioException
	 * @throws ArqException
	 */
	private void aproveitamentoAutomatico(MovimentoAproveitamentoAutomatico mov) throws ArqException, NegocioException {

		GenericDAO dao = getGenericDAO(mov);
		DiscenteDao discenteDao = getDAO(DiscenteDao.class, mov);
		DiscenteGraduacaoDao dgDao = getDAO(DiscenteGraduacaoDao.class, mov);
		ParametrosGestoraAcademicaDao pDao = getDAO(ParametrosGestoraAcademicaDao.class, mov);
		
		DiscenteGraduacao destino = mov.getDiscenteDestino();
		
		try {
			for( MatriculaComponente mat : mov.getMatriculas() ){
				dao.create(mat);
			}
	
			/**
			 * cálculos do aluno
			 */
			DiscenteCalculosHelper.atualizarTodosCalculosDiscente(destino, mov);
	
			/**
			 * calculando perfil inicial do discente
			 */
			PerfilInicialFactory.getPerfilInicial(destino).calcular(destino, mov);
		} finally {
			dao.close();
			discenteDao.close();
			dgDao.close();
			pDao.close();
		}
	}

	public void validate(Movimento movi) throws NegocioException, ArqException {

		MovimentoAproveitamentoAutomatico mov = (MovimentoAproveitamentoAutomatico) movi;

		ListaMensagens erros = new ListaMensagens();

		if( mov.getCodMovimento() == SigaaListaComando.APROVEITAMENTO_AUTOMATICO ){
			ParametrosGestoraAcademica param = ParametrosGestoraAcademicaHelper.getParametrosUnidadeGlobalGraduacao();
			AproveitamentoValidator.validaAproveitamentoAutomatico(mov.getDiscenteOrigem(), mov.getDiscenteDestino(), erros);

			for (MatriculaComponente mc : mov.getMatriculas()) {
				AproveitamentoValidator.validaAproveitamento(mov.getDiscenteDestino(), mc, param, erros, mov.getUsuarioLogado());
				checkValidation(erros);
			}
		}



	}

}
