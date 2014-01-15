/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 * 
 * Criado em 05/10/2011
 * Autor: Rafael Gomes
 */

package br.ufrn.sigaa.ensino.medio.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.StatusDiscente;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.negocio.validacao.ListaMensagens;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.MatriculaComponenteDao;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.ConsolidacaoIndividual;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.jsf.ConsolidacaoMBean;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoIndividualValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.ConsolidacaoIndividualMov;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para consolidar uma turma de um aluno.
 * 
 * @author Rafael Gomes
 *
 */
@Component("consolidacaoIndividualMedio")
@Scope("session")
public class ConsolidacaoIndividualMedioMBean extends ConsolidacaoMBean implements OperadorDiscente{

	private DiscenteAdapter discente;

	private ParametrosGestoraAcademica param;

	private MatriculaComponente matricula;

	Collection<MatriculaComponente> matriculasAbertas = new ArrayList<MatriculaComponente>();
	
	
	/**
	 * Método que carrega a página inicial do caso de uso.
	 * 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO);
		turma = new Turma();
		matricula = new MatriculaComponente();
		discente = new Discente();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CONSOLIDACAO_INDIVIDUAL_MEDIO);
		String forward = buscaDiscenteMBean.popular();

		if( isUserInRole( SigaaPapeis.GESTOR_MEDIO, SigaaPapeis.COORDENADOR_MEDIO ) ){
			buscaDiscenteMBean.getOperacao().setStatusValidos( new int[] { StatusDiscente.ATIVO, StatusDiscente.ATIVO_DEPENDENCIA, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO } );
		}

		return forward;
	}
	
	/**
	 * Método responsável por carregar os dados dos discente após a sua seleção na busca de discente.
	 */
	public String selecionaDiscente() throws DAOException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);
		matriculasAbertas = dao.findByDiscenteComTurma(discente, null, null, SituacaoMatricula.MATRICULADO);
		for(MatriculaComponente mc : matriculasAbertas){
			System.out.println( mc.getTurma().getNome() );
		}
		if( isEmpty( matriculasAbertas ) ){
			addMensagemWarning("Este discente não está matriculado em nenhuma turma.");
			return null;
		}
		return forward("/medio/consolidacao/disciplinasDiscente.jsp");
	}
	
	/**
	 * Método utilizado na seleção da disciplina do discente.
	 * @return
	 * @throws ArqException
	 */
	public String escolherDisciplina() throws ArqException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);

		matricula = dao.findByPrimaryKey(getParameterInt("id"), MatriculaComponente.class);
		turma = matricula.getTurma();
	
		if( isEmpty( matricula ) ){
			addMensagemErro("O aluno não está matriculado nesta turma.");
			return null;
		}

		if( matricula.getSituacaoMatricula().getId() != SituacaoMatricula.MATRICULADO.getId() ){
			addMensagemErro("Só é possível consolidar alunos com o status matriculado.");
			return null;
		}

		param = getParametrosAcademicos();
		if (param == null) {
			addMensagemErro("Os parâmetros acadêmicos para o nível " + turma.getDisciplina().getNivelDesc() + 
							" não estão configurados em sua unidade. Por favor, entre em contato com a administração do sistema.");
			return null;
		}

		String[] pesosAvaliacoes = param.getArrayPesosAvaliacoes();

		if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && (pesosAvaliacoes == null || pesosAvaliacoes.length == 0)) {
			addMensagemErro("A sua unidade não está com os pesos das avaliações definidos. " +
							"Por favor, entre em contato com a administração do sistema.");
			return null;
		}

		metodoAvaliacao = param.getMetodoAvaliacao();

		prepareMovimento(SigaaListaComando.CONSOLIDAR_INDIVIDUAL_MEDIO);

		return forward("/medio/consolidacao/consolidacao_individual.jsp");

	}

	/**
	 * Método que chama o processador para realizar a consolidação individual.
	 *
	 * @return
	 * @throws ArqException 
	 */
	public String chamaModelo() throws ArqException {

		if( !confirmaSenha() )
			return null;

		if( isNota() )
			matricula.setMetodoAvaliacao( MetodoAvaliacao.NOTA );
		
		ConsolidacaoIndividualMov mov = new ConsolidacaoIndividualMov();
		ConsolidacaoIndividual cons = new ConsolidacaoIndividual();
		cons.setMatricula(matricula);

		mov.setMediaMinima(getParametrosAcademicos().getMediaMinimaAprovacao());
		mov.setFaltasMinima(getParametrosAcademicos().getFrequenciaMinima());
		mov.setMetodoAvaliacao( matricula.getMetodoAvaliacao() );
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_INDIVIDUAL_MEDIO);

		mov.setConsolicacao(cons);

		try {
			ListaMensagens lista = new ListaMensagens();
			ConsolidacaoIndividualValidator.validarConsolidacao(cons, lista);
			addMensagens(lista);
			if (hasErrors())
				return null;

			execute(mov, getCurrentRequest());
		} catch (NegocioException e) {
			addMensagens(e.getListaMensagens());
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			addMensagemErro("Não foi possível realizar operação, contacte a administração do sistema.");
			notifyError(e);
			e.printStackTrace();
			return null;
		}

		String nomeAluno = mov.getConsolicacao().getMatricula().getDiscente().getMatriculaNome();
		String disciplina = mov.getConsolicacao().getMatricula().getComponente().getNome();
		addMessage("Consolidação individual realizada com sucesso para o aluno "
				+ nomeAluno + " na turma " + disciplina,
				TipoMensagemUFRN.INFORMATION);
		return cancelar();

	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public ParametrosGestoraAcademica getParam() {
		return param;
	}

	public void setParam(ParametrosGestoraAcademica param) {
		this.param = param;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public Collection<MatriculaComponente> getMatriculasAbertas() {
		return matriculasAbertas;
	}

	public void setMatriculasAbertas(
			Collection<MatriculaComponente> matriculasAbertas) {
		this.matriculasAbertas = matriculasAbertas;
	}
	
	
}
