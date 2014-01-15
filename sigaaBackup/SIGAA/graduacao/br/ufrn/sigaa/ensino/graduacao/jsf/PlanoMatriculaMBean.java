/* 
 * Superintendência de Informática - Diretoria de Sistemas
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

	// Coleções utilizadas para exibição das informações
	Collection<SolicitacaoMatricula> solicitacoesMatricula;
	Collection<SolicitacaoMatricula> solicitacoesMatriculaRematricula;
	Collection<SolicitacaoEnsinoIndividual> solicitacoesEnsinoIndividual;
	Collection<MatriculaComponente> matriculasFerias;

	private Collection<MatriculaComponente> matriculasSemSolicitacao;

	/**
	 * Gerar o plano de matrícula do semestre atual para o discente logado
	 * @throws DAOException
	 */
	public String gerar() throws DAOException {
		// Verificar se o usuário é um discente de graduação
		DiscenteAdapter discente = getUsuarioLogado().getDiscenteAtivo();
		if (discente == null || !discente.isGraduacao()) {
			addMensagemErro("Somente discentes de graduação podem visualizar planos de matrícula");
			return null;
 		}

		this.discente = (DiscenteGraduacao) discente;
		return gerarPlanoMatricula();
	}

	/**
	 * Buscar um discente para emitir seu plano de matrícula
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
	 * Separa as solicitações feitas na matrícula das solicitações feitas na rematrícula.
	 * As solicitações feitas na matrícla continuam em 'solicitacoesMatricula', já as solicitações
	 * feitas na rematrícula são adicionadas em 'solicitacoesMatriculaRematricula'.
	 * Isto é feito para mostrar na view uma separação entre matrícula e rematrícula.
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
		// Buscar calendário atual
		calendario = CalendarioAcademicoHelper.getCalendario(discente);

		// Buscar solicitações de matrícula
		SolicitacaoMatriculaDao solicitacaoMatriculaDao = getDAO(SolicitacaoMatriculaDao.class);
		int ano = calendario.getAno();
		int periodo = calendario.getPeriodo();
//		if( calendario.isPeriodoFerias() ){
//			ano = calendario.getAnoFeriasVigente();
//			periodo = calendario.getPeriodoFeriasVigente();
//		}
		solicitacoesMatricula = solicitacaoMatriculaDao.findValidasByDiscenteAnoPeriodo(discente.getDiscente(), ano, periodo, null, false);
		
		//Separa as solicitações feitas na matrícula das solicitações feitas na rematrícula.
		// As solicitações feitas na matrícla continuam em 'solicitacoesMatricula', já as solicitações
		//feitas na rematrícula são adicionadas em solicitacoesMatriculaRematricula. 
		separarMatriculaDaRematricula();		
		
		// Buscar solicitações de ensino individual
		SolicitacaoEnsinoIndividualDao ensinoIndividualDao = getDAO(SolicitacaoEnsinoIndividualDao.class);
		solicitacoesEnsinoIndividual = ensinoIndividualDao.findByDiscenteAnoPeriodo(discente.getId(), null, ano, periodo, null);

		MatriculaComponenteDao matriculaDao = getDAO(MatriculaComponenteDao.class);

		// Buscar matrículas cadastradas sem solicitação
		matriculasSemSolicitacao =  matriculaDao.findMatriculasSemSolicitacao(discente.getDiscente(), ano, periodo);

		// Buscar matrículas em turma de férias do semestre atual
		if( !isEmpty( calendario.getAnoFeriasVigente() ) && !isEmpty( calendario.getPeriodoFeriasVigente() ) )
			matriculasFerias = matriculaDao.findByDiscente(discente.getDiscente(), calendario.getAnoFeriasVigente(), calendario.getPeriodoFeriasVigente());
		else
			addMensagemWarning("As matrículas em turmas de férias não foram visualizadas, pois o ano.período de férias vigente não está definido no calendário acadêmico vigente.");
		
		// Validar existência de alguma informação
		if (isEmpty(solicitacoesMatricula) && isEmpty(solicitacoesEnsinoIndividual)
				&& isEmpty(matriculasSemSolicitacao) && isEmpty(matriculasFerias)) {
			addMensagemWarning("Não foram encontradas solicitações de matrícula ou de ensino individual para o período " +
					calendario.getAnoPeriodo() +
					" ou matrículas em turmas de férias para o período " +
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
