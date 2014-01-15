package br.ufrn.sigaa.ensino.infantil.jsf;

import java.util.ArrayList;
import java.util.Collection;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.ArqListaComando;
import br.ufrn.arq.util.CalendarUtils;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.dao.ensino.infantil.TurmaInfantilDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ModalidadeEducacao;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.infantil.dominio.DiscenteInfantil;

/**
 * Atua na realização da consolidação das turmas de Ensino Infantil
 * 
 * @author guerethes
 */
@Component @Scope("request")
public class ConsolidacaoTurmaInfantilMBean extends SigaaAbstractController<ArrayList<DiscenteInfantil>> {

	
	/** Turmas encontradas */
	private Collection<Turma> turmas;
	/** Lista de Matriculados */
	private Collection<MatriculaComponente> matriculados;
	/** Turma para a realização da consolidação */
	private Turma turma;
	/** Escolha do ano da turma para a consolidação */
	private Integer ano;
	/** Id da turma encontrada */
	private int idTurma = 0;
	
	/** Construtor padrão */
	public ConsolidacaoTurmaInfantilMBean() {
		clear();
	}
	
	/** Limpa os atributos deste controller. */
	private void clear() {
		obj = new ArrayList<DiscenteInfantil>();
		turmas = new ArrayList<Turma>();
		ano = CalendarUtils.getAnoAtual();
		matriculados = new ArrayList<MatriculaComponente>();
		removeOperacaoAtiva();
	}

	/**
	 * Método responsável pela busca das turmas
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/infantil/ConsolidarTurma/form.jsp</li>
	 * </ul>
	 */
	@Override
	public String buscar() throws Exception {
		ValidatorUtil.validateRequiredId(idTurma, "Turma", erros);
		if (hasOnlyErrors()) return null;
		carregarTurmas();
		return null;
	}
	
	/**
	 * Carrega as sub-turma das turmas selecionadas.
	 *
	 * @param event
	 * @throws DAOException
	 */
	private void carregarTurmas() throws DAOException{
		try {
			turmas = getDAO(TurmaDao.class).findGeral(getNivelEnsino(), new Unidade(getUnidadeGestora()), null, null, null, 
					null, null, ano, null, null, null, new ModalidadeEducacao(ModalidadeEducacao.PRESENCIAL), null, idTurma, null, null, null,null,null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	 }

	/**
	 * Listagem dos alunos da turma selecionada.
	 * 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/infantil/ConsolidarTurma/form.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String listarAlunos() throws DAOException {
		TurmaDao dao = getDAO(TurmaDao.class);
		try {
			turma = dao.findByPrimaryKey(getParameterInt("id"), Turma.class);
			Collection<SituacaoMatricula> situacoes = SituacaoMatricula.getSituacoesAtivas();
			situacoes.add(SituacaoMatricula.CANCELADO);
			situacoes.add(SituacaoMatricula.NAO_CONCLUIDO);
			matriculados = dao.findMatriculasDadosPessoaisByTurma(turma.getId(), situacoes);
			
			if (matriculados == null) 
				matriculados = new ArrayList<MatriculaComponente>();
			
			if (!matriculados.isEmpty()){
				setOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
				return forward(getListPage());
			}else {
				addMensagemErro("Não foi encontrado nenhum discente.");
				return null;
			}
		} finally {
			dao.close();
		}
	}

	/**
	 * Método responsável pela consolidação das turmas de ensino Infantil. 
	 * 
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/infantil/ConsolidarTurma/lista.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String consolidar() throws DAOException {
		checkOperacaoAtiva(ArqListaComando.CADASTRAR.getId());
		if (hasOnlyErrors()) 
			return redirectJSF(getSubSistema().getLink());
		TurmaInfantilDao dao = getDAO(TurmaInfantilDao.class);
		try {
			dao.consolidarTurmaInfantil(matriculados, turma);
		} finally {
			dao.close();
		}
		addMensagem(MensagensArquitetura.OPERACAO_SUCESSO, "Consolidação");
		removeOperacaoAtiva();
		carregarTurmas();
		return forward(getFormPage());
	} 
	
	/**
	 * Método responsável pela informação do caminho das view's
	 *
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>Não invocado por JSP.</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/infantil/ConsolidarTurma";
	}
	
	/**
	 * Direciona para a tela inicial do caso de uso.
	 *
 	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	 <li>/SIGAA/app/sigaa.ear/sigaa.war/infantil/menu.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String preConsolidar() {
		return forward(getFormPage());
	}
	
	public int getIdTurma() {
		return idTurma;
	}

	public void setIdTurma(int idTurma) {
		this.idTurma = idTurma;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public Collection<MatriculaComponente> getMatriculados() {
		return matriculados;
	}

	public void setMatriculados(Collection<MatriculaComponente> matriculados) {
		this.matriculados = matriculados;
	}

	public Turma getTurma() {
		return turma;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

}