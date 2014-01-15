/*
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA
 * Criado em: 2007/06/27 - 13:59:25
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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
import br.ufrn.sigaa.ead.dominio.Polo;
import br.ufrn.sigaa.ensino.dominio.ConsolidacaoIndividual;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.negocio.ConsolidacaoIndividualValidator;
import br.ufrn.sigaa.ensino.negocio.dominio.ConsolidacaoIndividualMov;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.pessoa.dominio.Discente;

/**
 * Managed bean para consolidar uma turma de um aluno.
 *
 * @author David Pereira
 *
 */
@Component("consolidacaoIndividual")
@Scope("session")
public class ConsolidacaoIndividualMBean extends ConsolidacaoMBean implements OperadorDiscente {

	/** Discente que terá a matrícula consolidada. */
	private DiscenteAdapter discente;

	/** Parâmentros da gestora da unidade responsável. */
	private ParametrosGestoraAcademica param;

	/** Matrícula do discente. */
	private MatriculaComponente matricula;

	/** Matrículas abertas do discente. */
	Collection<MatriculaComponente> matriculasAbertas = new ArrayList<MatriculaComponente>();

	/**
	 * Método que popula a página inicial do caso de uso.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ead/menu.jsp</li>
	 *	 	<li>sigaa.war/graduacao/menus/aluno.jsp</li>
	 *	 	<li>sigaa.war/lato/menu_coordenador.jsp</li>
	 *		<li>sigaa.war/stricto/menus/discente.jsp</li>
	 *		<li>sigaa.war/stricto/menu_coordenador.jsp</li>
	 *	 	<li>sigaa.war/latosensu/menu/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException {
		checkRole(SigaaPapeis.DAE, SigaaPapeis.SEDIS,
				SigaaPapeis.SECRETARIA_POS, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.PPG,
				SigaaPapeis.SECRETARIA_LATO, SigaaPapeis.COORDENADOR_LATO, SigaaPapeis.GESTOR_LATO);
		turma = new Turma();
		matricula = new MatriculaComponente();
		discente = new Discente();
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.CONSOLIDACAO_INDIVIDUAL);
		String forward = buscaDiscenteMBean.popular();

		if( isUserInRole( SigaaPapeis.PPG, SigaaPapeis.DAE ) ){
			buscaDiscenteMBean.getOperacao().setStatusValidos( new int[] { StatusDiscente.ATIVO, StatusDiscente.FORMANDO, StatusDiscente.CONCLUIDO } );

		}

		return forward;
	}

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
		return forward("/ensino/consolidacao/turmasDiscente.jsp");
	}

	/**
	 * Popula a turma da matrícula.
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ensino/consolidacao/turmasDiscente.jsp</li>
	 * </ul>
	 * @return
	 * @throws NegocioException 
	 * @throws SegurancaException
	 */
	public String escolherTurma() throws ArqException, NegocioException {
		MatriculaComponenteDao dao = getDAO(MatriculaComponenteDao.class);

		matricula = dao.findByPrimaryKey(getParameterInt("id"), MatriculaComponente.class);
		turma = matricula.getTurma();
		if(matricula.getTurma().getPolo()!=null) {
			matricula.getTurma().setPolo( (Polo) getGenericDAO().findByPrimaryKey(matricula.getTurma().getPolo().getId(), Polo.class));
		}
		
		

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
			addMensagemErro("Os parâmetros acadêmicos para o nível " + turma.getDisciplina().getNivelDesc() + " não estão configurados em sua unidade. Por favor, entre em contato com a administração do sistema.");
			return null;
		}

		String[] pesosAvaliacoes = TurmaUtil.getArrayPesosUnidades(turma);

		if (param.getMetodoAvaliacao() == MetodoAvaliacao.NOTA && (pesosAvaliacoes == null || pesosAvaliacoes.length == 0)) {
			addMensagemErro("A sua unidade não está com os pesos das avaliações definidos. Por favor, entre em contato com a administração do sistema.");
			return null;
		}

		metodoAvaliacao = param.getMetodoAvaliacao();

		prepareMovimento(SigaaListaComando.CONSOLIDAR_INDIVIDUAL);

		return forward("/ensino/consolidacao/consolidacao_individual.jsp");

	}

	/**
	 * Método que chama o processador para realizar a consolidacao
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *		<li>sigaa.war/ensino/consolidacao/consolidacaoIndividual.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 */
	public String chamaModelo() throws ArqException {

		if( !confirmaSenha() )
			return null;

		if( isConceito() ){
			matricula.setMetodoAvaliacao( MetodoAvaliacao.CONCEITO );
			String conceito = getParameter("conceito_" + matricula.getId());
			if (conceito != null) {
				Double conceitoD = Double.parseDouble(conceito);
				if (conceitoD < 0) {
					matricula.setConceito(null);
				} else {
					matricula.setConceito(conceitoD);
				}
			}
		} else if( isNota() ){
			matricula.setMetodoAvaliacao( MetodoAvaliacao.NOTA );
		} else if (isCompetencia()){
			matricula.setMetodoAvaliacao( MetodoAvaliacao.COMPETENCIA );
		}	
		ConsolidacaoIndividualMov mov = new ConsolidacaoIndividualMov();
		ConsolidacaoIndividual cons = new ConsolidacaoIndividual();
		cons.setMatricula(matricula);

		mov.setMediaMinima(getParametrosAcademicos().getMediaMinimaAprovacao());
		mov.setFaltasMinima(getParametrosAcademicos().getFrequenciaMinima());
		mov.setMetodoAvaliacao( matricula.getMetodoAvaliacao() );
		mov.setCodMovimento(SigaaListaComando.CONSOLIDAR_INDIVIDUAL);

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

	public void setDiscente(DiscenteAdapter discente) {
		this.discente = discente;
	}

	public Collection<MatriculaComponente> getMatriculasAbertas() {
		return matriculasAbertas;
	}

	public void setMatriculasAbertas(
			Collection<MatriculaComponente> matriculasAbertas) {
		this.matriculasAbertas = matriculasAbertas;
	}

	public List<MatriculaComponente> getMatriculas() {
		List<MatriculaComponente> lista = new ArrayList<MatriculaComponente>();
		lista.add(matricula);
		return lista;
	}

	public MatriculaComponente getMatricula() {
		return matricula;
	}

	public void setMatricula(MatriculaComponente matricula) {
		this.matricula = matricula;
	}

	public DiscenteAdapter getDiscente() {
		return discente;
	}

	public ParametrosGestoraAcademica getParam() {
		return param;
	}

	public void setParam(ParametrosGestoraAcademica param) {
		this.param = param;
	}

}
