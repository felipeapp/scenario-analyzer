/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on 30/08/2007
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;
import static br.ufrn.arq.util.ValidatorUtil.validateMaxLength;
import static br.ufrn.arq.util.ValidatorUtil.validateRange;
import static br.ufrn.arq.util.ValidatorUtil.validateRequired;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.dao.GenericDAO;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.NegocioException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.Formatador;
import br.ufrn.arq.util.UFRNUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.arq.negocio.SigaaListaComando;
import br.ufrn.sigaa.ava.dao.AvaliacaoDao;
import br.ufrn.sigaa.ava.dao.TurmaVirtualDao;
import br.ufrn.sigaa.ava.dominio.ConfiguracoesAva;
import br.ufrn.sigaa.ava.jsf.ConfiguracoesAvaMBean;
import br.ufrn.sigaa.ava.jsf.TurmaVirtualMBean;
import br.ufrn.sigaa.ensino.dominio.Avaliacao;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.dominio.AvaliacaoMov;
import br.ufrn.sigaa.ensino.util.TurmaUtil;
import br.ufrn.sigaa.mensagens.MensagensGraduacao;

/**
 * MBean para cadastro de avaliações na turma virtual.
 *
 * @author David Pereira
 *
 */
@Component("cadastrarAvaliacao")
@Scope("session")
public class CadastrarAvaliacaoMBean extends SigaaAbstractController<Avaliacao> {

	/** Turma em que as avaliações serão cadastrada. */
	private Turma turma;

	/** Unidades em que as avaliações serão cadastradas */
	private int unidade;

	/** Matrículas em que as avaliações serão cadastradas. */
	private Collection<MatriculaComponente> matriculas;

	/** Verifica se ocorreu erro utilizando o botão voltar do browser */
	private boolean erro = false;
	
	public CadastrarAvaliacaoMBean() {
		obj = new Avaliacao();
		obj.setPeso(1);
	}

	/** Recupera a unidade em que a avaliacao será cadastrada. 
	 *  Método não invocado por JSPs.
	 */
	public String getConfiguracaoUnidade() {	
		unidade = getParameterInt("unidade");
		return null;
	}
	
	/**
	 * Retorna a turma que está sendo consolidada.
	 * @return
	 * @throws DAOException 
	 * @throws Exception
	 */
	public Turma getTurma() throws DAOException{
		GenericDAO dao = null;
		try {
			dao = getGenericDAO();
			if (turma == null) {
				ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
				
				// Carrega as matrículas para verificação do números de unidades de uma turma EAD
				Collection<MatriculaComponente> matriculas = bean.getTurma().getMatriculasDisciplina();	
				
				// Recarrega a turma
				turma = dao.findByPrimaryKey(bean.getTurma().getId(), Turma.class);
				turma.setMatriculasDisciplina(matriculas);
				
				// Garante que o dao vai trazer a turma agrupadora.
				if (turma.getTurmaAgrupadora() != null)
					turma.setTurmaAgrupadora(dao.refresh(turma.getTurmaAgrupadora()));
				
				// Garante que o dao vai trazer o programa da disciplina.
				if (turma.getDisciplina().getPrograma() != null)
					turma.getDisciplina().setPrograma(dao.refresh(turma.getDisciplina().getPrograma()));
			}
			return turma;
		} finally {
			if (dao != null)
				dao.close();
		}
	}

	/**
	 * Retorna um conjunto de SelectItem com a quantidade de unidades
	 * do componente curricular da turma que está sendo consolidada.
     * Método chamdo pela seguinte JSP: 
     * <ul>
	 * <li> sigaa.war/ensino/avaliacao/cadastrar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws Exception
	 */
	public List<SelectItem> getUnidades() throws ArqException {
		try {
			Turma turma = getTurma();
			turma.setDisciplina(getGenericDAO().refresh(turma.getDisciplina()));
			Integer qtdUnidades = TurmaUtil.getNumUnidadesDisciplina(turma); 
			MensagemAviso msgAviso = UFRNUtils.getMensagem(MensagensGraduacao.COMPONENTE_SEM_NUMERO_UNIDADES_DEFINIDO);
			if (qtdUnidades == null) throw new NegocioException(msgAviso.getMensagem());
			List<SelectItem> itens = new ArrayList<SelectItem>();
	
			for (int i = 1; i <= qtdUnidades; i++)
				itens.add(new SelectItem(i, i + "a Unidade"));
	
			return itens;
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}

	/**
	 * Retorna as matrículas da turma
	 * Método chamdo pela seguinte JSP: 
     * <ul>
	 * <li> sigaa.war/ensino/avaliacao/turma.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws Exception 
	 */
	public Collection<MatriculaComponente> getMatriculas() throws DAOException {
		if (matriculas == null) {
			matriculas = getDAO(TurmaDao.class).findMatriculasByTurma(getTurma().getId());
		}

		return matriculas;
	}

	public void setMatriculas(Collection<MatriculaComponente> matriculas) {
		this.matriculas = matriculas;
	}

	public int getUnidade() {
		return unidade;
	}

	public void setUnidade(int unidade) {
		this.unidade = unidade;
	}

	/**
	 * Direciona o usuário para a tela de cadastro da avaliação. Caso o método de
	 * cálculo da média não esteja definido, direciona o usuário para a tela de
	 * configuração da turma virtual.
	 *  
	 * Método chamado pela seguinte JSP: sigaa.war/ensino/consolidacao/detalhesTurma.jsp
	 * <ul>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 * @throws Exception
	 */
	public String telaCadastro() throws ArqException{
		prepareMovimento(SigaaListaComando.CADASTRAR_AVALIACAO);
		ConsolidarTurmaMBean ctBean = getMBean("consolidarTurma");
		TurmaVirtualMBean tv = getMBean("turmaVirtual");
		
		try{
			erro = false;
			ctBean.salvarNotas(false);
			turma = getTurma();
			tv.setTurma(turma);
			tv.entrar();
			
			ConfiguracoesAva config = null;
			if (turma.getTurmaAgrupadora() == null)
				config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma);
			else
				config = getDAO(TurmaVirtualDao.class).findConfiguracoes(turma.getTurmaAgrupadora());
			
			if (config != null && config.getTipoMediaAvaliacoes(getParameterInt("unidade")) != null) {
				return redirect("/ensino/avaliacao/cadastrar.jsf?unidade=" + getParameter("unidade"));
			} else {
				addMensagemWarning("Não é possível cadastrar uma avaliação porque o tipo de cálculo da média da avaliação não foi definido. "
						+ "Para defini-lo, realize a configuração da turma no formulário abaixo.");
				ConfiguracoesAvaMBean bean = getMBean("configuracoesAva");
				bean.setCadastroAvaliacao(true);
				return bean.configurar(turma);
			}
		} catch (NegocioException e){
			addMensagens(e.getListaMensagens());
		}
		
		return null;
	}
	
	/**
	 * Cadastra a avaliação e direciona o usuário para a tela da consolidação.
	 * Método chamado pela seguinte JSP: 
	 * Método chamdo pela seguinte JSP: 
     * <ul>
	 * <li> sigaa.war/ensino/avaliacao/cadastrar.jsp</li>
	 * </ul>
	 */
	@Override
	public String cadastrar() {

		AvaliacaoDao dao = null;
		
		try {
			
			if (!validate()) return null;
			
			AvaliacaoMov mov = new AvaliacaoMov();
			mov.setCodMovimento(SigaaListaComando.CADASTRAR_AVALIACAO);
			mov.setAvaliacao(obj);
			
			// Se for uma subturma, usa a agrupadora para garantir a criação em todas as subturmas.
			Turma t = getTurma();
			dao = getDAO(AvaliacaoDao.class);
			if (t.getTurmaAgrupadora() != null){
				t = t.getTurmaAgrupadora ();
				t = dao.refresh(t);
			}	
			mov.setTurma(t);
			
			mov.setUnidade(unidade);

			execute(mov, getCurrentRequest());
			addMessage("Avaliação cadastrada com sucesso!", TipoMensagemUFRN.INFORMATION);
			
			// Se a nota da unidade for calculada por soma das avaliações e a soma das notas máximas destas não for 10, exibe o aviso.
			if (((ConsolidarTurmaMBean) getMBean("consolidarTurma")).getConfig().isAvaliacoesSoma(unidade)){
				
				double nota = dao.getSomaNotaAvaliacao(getTurma(), unidade);
				if (nota < 10.0) {
					addMensagemWarning("As notas das avaliações da unidade " + unidade + " somam " + Formatador.getInstance().formatarDecimal1(nota) + " pontos. Certifique-se de que, ao final da unidade, a soma das notas máximas das avaliações será igual a 10,0.");
				}
			}
			
		} catch (NegocioException e) {
			addMensagemErro(e.getMessage());
			return null;
		} catch (Exception e) {
			notifyError(e);
			e.printStackTrace();
			addMensagemErro(e.getMessage());
		} finally {
			if (dao != null)
				dao.close();
		}

		obj = new Avaliacao();
		turma = null;
		resetBean();
		return cancelar();
	}

	/**
	 * Valida o cadastro da avaliação. Retorna true se não houve erros,
	 * false caso contrário.
	 * @return
	 * @throws DAOException 
	 * @throws Exception 
	 */
	private boolean validate() throws DAOException{
		ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
		
		validateRequired(obj.getAbreviacao(), "Abreviação", erros);
		validateMaxLength(obj.getAbreviacao(), 4, "Abreviação", erros);
		validateRequired(obj.getDenominacao(), "Denominação", erros);
		validateMaxLength(obj.getDenominacao(), 100, "Denominação", erros);
		
		if (bean.getConfig().isAvaliacoesMediaPonderada(unidade)) {
			validateRequired(obj.getPeso(), "Peso", erros);
			validateRange(obj.getPeso(), 1, 100, "Peso", erros);
		}
		
		if (bean.getConfig().isAvaliacoesSoma(unidade)) {
			validateRequired(obj.getNotaMaxima(), "Nota Máxima", erros);
			validateRange(obj.getNotaMaxima(), 1, 10, "Nota Máxima", erros);
			
			double nota = getDAO(AvaliacaoDao.class).getSomaNotaAvaliacao(getTurma(), unidade);
			if (nota >= 10.0) {
				erros.addErro("A unidade " + unidade + " já possui um conjunto de avaliações cuja soma das notas máximas é 10,0. Não é possível cadastrar mais uma avaliação.");
			} else if (nota < 10.0 && obj.getNotaMaxima() != null && nota + obj.getNotaMaxima() > 10.0) {
				erros.addErro("A soma das notas máximas das avaliações da unidade " + unidade + " excede 10,0. O valor máximo para a nota desta avaliação deve ser " + Formatador.getInstance().formatarDecimal1(10.0 - nota) + ".");
			}
			
		}
		
		return !hasErrors();
	}

	/**
	 * Cancela o cadastro de uma avaliação.
	 * Método chamdo pela seguinte JSP: 
     * <ul>
	 * <li> sigaa.war/ensino/avaliacao/cadastrar.jsp</li>
	 * </ul>
	 */
	@Override
	public String cancelar() {
		ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
		try {
			return bean.escolherTurma();
		} catch(ArqException e) {
			notifyError(e);
			return null;
		}
	}


	/**
	 * Remove um desmembramento em avaliações.
	 * Método chamdo pelas seguintes JSPs: 
     * <ul>
	 * <li> sigaa.war/ensino/avaliacao/remover.jsp</li>
	 * <li> sigaa.war/ensino/consolidacao/detalhesTurma.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws ArqException 
	 * @throws RemoteException 
	 * @throws Exception
	 */
	public String removerAvaliacao() throws ArqException{
		
		GenericDAO dao = null;
		
		try {
			prepareMovimento(SigaaListaComando.REMOVER_AVALIACAO);
				
			dao = getGenericDAO();
			Avaliacao aval = dao.findByPrimaryKey(getParameterInt("avaliacao"), Avaliacao.class);
			
			if ( isEmpty(aval)) {
				addMensagem(MensagensArquitetura.OBJETO_SELECIONADO_FOI_REMOVIDO);
				return null;
			}	
			// Se for uma subturma, usa a agrupadora para garantir a criação em todas as subturmas.
			Turma t = aval.getUnidade().getMatricula().getTurma();
			if (t.getTurmaAgrupadora() != null)
				t = dao.refresh(t.getTurmaAgrupadora ());
			
			if (t.getDisciplina().getUnidade() != null)
				t.getDisciplina().setUnidade(dao.refresh(t.getDisciplina().getUnidade()));
			
			aval.getUnidade().getMatricula().setTurma(t);
	
			AvaliacaoMov mov = new AvaliacaoMov();
			mov.setAvaliacao(aval);
			mov.setCodMovimento(SigaaListaComando.REMOVER_AVALIACAO);

		
			executeWithoutClosingSession(mov, getCurrentRequest());
			addMessage("Avaliação removida com sucesso.", TipoMensagemUFRN.INFORMATION);

			ConsolidarTurmaMBean bean = (ConsolidarTurmaMBean) getMBean("consolidarTurma");
			bean.recarregarMatriculas();
			bean.salvarNotas(false);

			return bean.escolherTurma();
		} catch(NegocioException e) {
			addMensagens(e.getListaMensagens());
		} finally {
			if (dao != null)
				dao.close();
		}

		return null;
	}
	
	
	/**
	 * Verifica se a unidade possui nota.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * 	<li> sigaa.war/ava/tarefaTurma/_forma.jsp</li>
	 * </ul>
	 * @return
	 */
	public Boolean getUnidadePossuiNota( ) throws DAOException{
		AvaliacaoDao aDao = null;
		try {			
			
			if (turma == null)
				return null;
			
			aDao = getDAO(AvaliacaoDao.class);	
			boolean possuiNotas = aDao.possuiNotasCadastradasNaUnidade(turma, unidade);
			boolean possuiAvaliacoes = aDao.possuiAvaliacoesNaUnidade(turma, unidade);
			return possuiNotas && !possuiAvaliacoes;
		} finally {
			if (aDao != null)
				aDao.close();
		}
	}
	
	/**
	 * Pega o número de NotaUnidades para essa turma.
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 * <li> sigaa.war/ensino/avaliacao/cadastrar.jsp</li>
	 * </ul>
	 * @return
	 * @throws ArqException 
	 * @throws NegocioException 
	 */
	public Integer getNumeroUnidades() throws ArqException, NegocioException {
		
		if (turma == null){
			if (!erro){
				addMensagemErro("O procedimento que você tentou realizar já foi processado anteriormente." +
				" Para realizá-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
				redirectJSF(getSubSistema().getLink());
				erro = true;
			}
			return null;
		}	
		
		return TurmaUtil.getNumUnidadesDisciplina(turma);
	}
}