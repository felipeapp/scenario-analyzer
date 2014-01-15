/* 
 * Superintend�ncia de Inform�tica - Diretoria de Sistemas
 * Projeto: SIGAA
 *
 */
package br.ufrn.sigaa.ensino.graduacao.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoEnsinoIndividualDao;
import br.ufrn.sigaa.arq.dao.graduacao.SolicitacaoMatriculaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.graduacao.dominio.DiscenteGraduacao;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoEnsinoIndividual;
import br.ufrn.sigaa.ensino.graduacao.dominio.SolicitacaoMatricula;
import br.ufrn.sigaa.ensino.negocio.CalendarioAcademicoHelper;

/**
 * @author Ricardo Wendell
 *
 */
@Component("planoMatriculaBean") @Scope("request")
public class PlanoMatriculaMBean extends SigaaAbstractController<Object> implements OperadorDiscente {

	DiscenteGraduacao discente;

	CalendarioAcademico calendario;

	// Cole��es utilizadas para exibi��o das informa��es
	Collection<SolicitacaoMatricula> solicitacoesMatricula;
	Collection<SolicitacaoMatricula> solicitacoesMatriculaRematricula;
	Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual;
	Collection<MatriculaComponente> matriculasFerias;

	private Collection<MatriculaComponente> matriculasSemSolicitacao;

	/**
	 * Gerar o plano de matr�cula do semestre atual para o discente logado
	 * @throws DAOException
	 */
	public String gerar() throws DAOException {
		// Verificar se o usu�rio � um discente de gradua��o
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		if (discente == null || !discente.isGraduacao()) {
			addMensagemErro("Somente discentes de gradua��o podem visualizar planos de matr�cula");
			return null;
 		}

		this.discente = (DiscenteGraduacao) discente;
		return gerarPlanoMatricula();
	}

	/**
	 * Buscar um discente para emitir seu plano de matr�cula
	 *
	 * @return
	 * @throws SegurancaException
	 */
	public String buscarDiscente() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.COORDENADOR_CURSO, SigaaPapeis.SECRETARIA_COORDENACAO);
		BuscaDiscenteMBean buscaDiscenteMBean = getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.GERAR_PLANO_MATRICULA);

		return buscaDiscenteMBean.popular();
	}
	
	
	/**
	 * Separa as solicita��es feitas na matr�cula das solicita��es feitas na rematr�cula.
	 * As solicita��es feitas na matr�cla continuam em 'solicitacoesMatricula', j� as solicita��es
	 * feitas na rematr�cula s�o adicionadas em 'solicitacoesMatriculaRematricula'.
	 * Isto � feito para mostrar na view uma separa��o entre matr�cula e rematr�cula.
	 */
	private void separarMatriculaDaRematricula() {
		
		solicitacoesMatriculaRematricula = new ArrayList<SolicitacaoMatricula>();
		Collection<SolicitacaoMatricula> solicitacoesMatriculaPeriodoMatricula = new ArrayList<SolicitacaoMatricula>();
		
		if(solicitacoesMatricula != null) {
			for(SolicitacaoMatricula s : solicitacoesMatricula ) {				
				if(s.getRematricula()) {				
					solicitacoesMatriculaRematricula.add(s);
				} else {
					solicitacoesMatriculaPeriodoMatricula.add(s);
				}			
			}			
			solicitacoesMatricula = solicitacoesMatriculaPeriodoMatricula;
		}		
	}

	/**
	 * Gerar o plano de matricula do semestre corrente para o discente selecionado
	 *
	 * @return
	 * @throws DAOException
	 */
	private String gerarPlanoMatricula() throws DAOException {
		// Buscar calend�rio atual
		calendario = CalendarioAcademicoHelper.getCalendario(discente);

		// Buscar solicita��es de matr�cula
		SolicitacaoMatriculaDao solicitacaoMatriculaDao = getDAO(SolicitacaoMatriculaDao.class);
		int ano = calendario.getAno();
		int periodo = calendario.getPeriodo();
//		if( calendario.isPeriodoFerias() ){
//			ano = calendario.getAnoFeriasVigente();
//			periodo = calendario.getPeriodoFeriasVigente();
//		}
		solicitacoesMatricula = solicitacaoMatriculaDao.findValidasByDiscenteAnoPeriodo(discente.getDiscente(), ano, periodo, null, false);
		
		//Separa as solicita��es feitas na matr�cula das solicita��es feitas na rematr�cula.
		// As solicita��es feitas na matr�cla continuam em 'solicitacoesMatricula', j� as solicita��es
		//feitas na rematr�cula s�o adicionadas em solicitacoesMatriculaRematricula. 
		separarMatriculaDaRematricula();		
		
		// Buscar solicita��es de ensino individual
		SolicitacaoEnsinoIndividualDao ensinoIndividualDao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		solicitacoesEnsinoIndividual = ensinoIndividualDao.findByDiscenteAnoPeriodo(discente.getId(), null, ano, periodo, null);

		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);

		// Buscar matr�culas cadastradas sem solicita��o
		matriculasSemSolicitacao =  matriculaDao.findMatriculasSemSolicitacao(discente.getDiscente(), ano, periodo);

		// Buscar matr�culas em turma de f�rias do semestre atual
		if( !isEmpty( calendario.getAnoFeriasVigente() ) && !isEmpty( calendario.getPeriodoFeriasVigente() ) )
			matriculasFerias = matriculaDao.findByDiscente(discente.getDiscente(), calendario.getAnoFeriasVigente(), calendario.getPeriodoFeriasVigente());
		else
			addMensagemWarning("As matr�culas em turmas de f�rias n�o foram visualizadas, pois o ano.per�odo de f�rias vigente n�o est� definido no calend�rio acad�mico vigente.");
		
		// Validar exist�ncia de alguma informa��o
		if (isEmpty(solicitacoesMatricula) && isEmpty(solicitacoesEnsinoIndividual)
				&& isEmpty(matriculasSemSolicitacao) && isEmpty(matriculasFerias)) {
			addMensagemWarning("N�o foram encontradas solicita��es de matr�cula ou de ensino individual para o per�odo " +
					calendario.getAnoPeriodo() +
					" ou matr�culas em turmas de f�rias para o per�odo " +
					calendario.getAnoFeriasVigente() + "." + calendario.getPeriodoFeriasVigente());
		}


		return forward("/graduacao/matricula/plano_matricula/plano.jsp");
	}

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#selecionaDiscente()
	 */
	public String selecionaDiscente() throws ArqException {
		return gerarPlanoMatricula();
	}

	/* (non-Javadoc)
	 * @see br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente#setDiscente(br.ufrn.sigaa.pessoa.dominio.Discente)
	 */
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		this.discente = (DiscenteGraduacao) discente;
	}

	public DiscenteGraduacao getDiscente() {
		return this.discente;
	}

	public void setDiscente(DiscenteGraduacao discente) {
		this.discente = discente;
	}

	public CalendarioAcademico getCalendario() {
		return this.calendario;
	}

	public void setCalendario(CalendarioAcademico calendario) {
		this.calendario = calendario;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoesMatricula() {
		return this.solicitacoesMatricula;
	}

	public void setSolicitacoesMatricula(
			Collection<SolicitacaoMatricula> solicitacoesMatricula) {
		this.solicitacoesMatricula = solicitacoesMatricula;
	}

	public Collection<SolicitacaoEnsinoIndividual> getSolicitacoesEnsinoIndividual() {
		return this.solicitacoesEnsinoIndividual;
	}

	public void setSolicitacoesEnsinoIndividual(
			Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual) {
		this.solicitacoesEnsinoIndividual = solicitacoesEnsinoIndividual;
	}

	public Collection<MatriculaComponente> getMatriculasFerias() {
		return this.matriculasFerias;
	}

	public void setMatriculasFerias(Collection<MatriculaComponente> matriculasFerias) {
		this.matriculasFerias = matriculasFerias;
	}

	public Collection<MatriculaComponente> getMatriculasSemSolicitacao() {
		return matriculasSemSolicitacao;
	}

	public void setMatriculasSemSolicitacao(
			Collection<MatriculaComponente> matriculasSemSolicitacao) {
		this.matriculasSemSolicitacao = matriculasSemSolicitacao;
	}

	public Collection<SolicitacaoMatricula> getSolicitacoesMatriculaRematricula() {
		return solicitacoesMatriculaRematricula;
	}

	public void setSolicitacoesMatriculaRematricula(
			Collection<SolicitacaoMatricula> solicitacoesMatriculaRematricula) {
		this.solicitacoesMatriculaRematricula = solicitacoesMatriculaRematricula;
	}	

}
