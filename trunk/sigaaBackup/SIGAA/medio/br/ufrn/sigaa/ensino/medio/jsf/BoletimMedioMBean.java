/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 07/07/2011
* 
*/
package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.negocio.DefaultExtensionPointFactory;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.DiscenteAdapter;
import br.ufrn.sigaa.ensino.dominio.MatriculaComponente;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.SituacaoMatricula;
import br.ufrn.sigaa.ensino.graduacao.jsf.BuscaDiscenteMBean;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperacaoDiscente;
import br.ufrn.sigaa.ensino.graduacao.jsf.OperadorDiscente;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaComponenteMedioDao;
import br.ufrn.sigaa.ensino.medio.dao.MatriculaDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.NotaSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.ObservacaoDiscenteSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.Boletim;
import br.ufrn.sigaa.ensino.medio.dominio.DiscenteMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.NotaDisciplina;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.ObservacaoDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.SituacaoMatriculaSerie;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacao;
import br.ufrn.sigaa.ensino.negocio.consolidacao.EstrategiaConsolidacaoFactory;
import br.ufrn.sigaa.parametros.dominio.ParametrosGerais;

/**
 * MBeam usado para emissão de boletim de discentes do ensino médio
 * 
 * @author Arlindo
 */
@Component("boletimMedioMBean") @Scope("request")
public class BoletimMedioMBean extends SigaaAbstractController<Boletim> implements OperadorDiscente {
	
	/** Matrículas do discente selecionado */
	private List<MatriculaDiscenteSerie> matriculasSerie = new ArrayList<MatriculaDiscenteSerie>();
	
	/** Construtor padrão */
	public BoletimMedioMBean() {
		obj = new Boletim();
	}
	
	/**
	 * Inicia a seleção do discente para exibição do boletim
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/menus/discente.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 */
	public String iniciar() throws DAOException{
		BuscaDiscenteMBean buscaDiscenteMBean = (BuscaDiscenteMBean) getMBean("buscaDiscenteGraduacao");
		buscaDiscenteMBean.setCodigoOperacao(OperacaoDiscente.EMISSAO_BOLETIM_MEDIO);
		return buscaDiscenteMBean.popular();
	}
	
	/**
	 * Inicia a exibição do boletim escolar a partir do portal do discente.
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/portais/discente/medio/menu_discente_medio.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException 
	 * @throws DAOException 
	 */
	public String iniciarDiscente() throws DAOException{
		RegraNotaDao dao = getDAO(RegraNotaDao.class); 
		try {
			obj.setDiscente((DiscenteMedio) getUsuarioLogado().getDiscenteAtivo());
			List<RegraNota> regras = dao.findByCurso(obj.getDiscente().getCurso());
			if (ValidatorUtil.isEmpty(regras)){
				addMensagemErro("Não foram definidas as regras de consolidação.");
				return null;
			}	
			
			obj.setRegraNotas(regras);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return exibirMatriculas();
	}
	
	
	/** Gera os dados do boletim do discente.
	 *  <br>Método não invocado por JSP´s.
	 * @param boletim
	 * @return
	 */
	public String exibirBoletim() throws DAOException {
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		ObservacaoDiscenteSerieDao obsDao = getDAO(ObservacaoDiscenteSerieDao.class);
		NotaSerieDao notasDao = getDAO(NotaSerieDao.class); 
		MatriculaComponenteMedioDao mcmDao = getDAO(MatriculaComponenteMedioDao.class);
		try {
			//atribui o ano do calendário vigente
			obj.setAno(getCalendarioVigente().getAno());
			
			//Atribui os parâmetros de média mínima
			obj.setMediaMinimaPassarPorMedia( getParametrosAcademicos().getMediaMinimaPassarPorMedia() );
			obj.setMediaMinimaAprovacao( getParametrosAcademicos().getMediaMinimaAprovacao() );
			
			//Carrega as disciplinas do discente
			List<Integer> id = new ArrayList<Integer>();
			id.add(obj.getDiscente().getId());
			Collection<SituacaoMatricula> situacoesBoletim = SituacaoMatricula.getSituacoesAtivas();
			situacoesBoletim.addAll(SituacaoMatricula.getSituacoesAproveitadas());
			List<MatriculaComponente> disciplinas = (List<MatriculaComponente>) mcmDao.findMatriculasByMatriculaDiscenteSerie(obj.getMatriculaSerie(), true, situacoesBoletim);
			
			if (ValidatorUtil.isEmpty(disciplinas)){
				addMensagemErro("O discente selecionado não possui disciplina com situação ativa cadastrada.");
				return null;
			}
			
			//Carrega as observações do discente na série
			List<ObservacaoDiscenteSerie> obs = obsDao.findByDiscenteAndSerie(obj.getDiscente(), obj.getMatriculaSerie());
			obj.setObservacoes(obs);	
			
			EstrategiaConsolidacaoFactory factory = (EstrategiaConsolidacaoFactory) DefaultExtensionPointFactory.getImplementation(ParametrosGerais.IMPLEMENTACAO_ESTRATEGIA_CONSOLIDACAO_FACTORY);
			EstrategiaConsolidacao estrategia = factory.getEstrategia(obj.getDiscente(), getParametrosAcademicos());	
			
			//Agrupa as notas para cada disciplina 
			List<NotaSerie> notas = notasDao.findNotasByDiscente(obj.getDiscente(), obj.getMatriculaSerie().getTurmaSerie());
			List<NotaDisciplina> lista = new ArrayList<NotaDisciplina>();
			for (MatriculaComponente mat : disciplinas){
				mat.setDiscente(obj.getDiscente());
				mat.setSerie(obj.getMatriculaSerie().getTurmaSerie().getSerie());
				mat.setEstrategia(estrategia);
				NotaDisciplina notaDisc = new NotaDisciplina();
				notaDisc.setMatricula(mat);
				notaDisc.setNotas(new ArrayList<NotaSerie>());
				
				for (NotaSerie notaSerie : notas){
					if (mat.equals(notaSerie.getNotaUnidade().getMatricula())){
//						if (!mat.isConsolidada()){
//							notaSerie.getNotaUnidade().setNota(null);
//							notaSerie.getNotaUnidade().setFaltas((short)0);
//							mat.setMediaFinal(null);
//							mat.setNumeroFaltas(null);
//						}
						notaDisc.getNotas().add(notaSerie);
					}
				}
				
				lista.add(notaDisc);
			}
			
			obj.setNotas(lista);
			
		} finally {
			if (dao != null)
				dao.close();
			if (obsDao != null)
				obsDao.close();
			if (notasDao != null)
				notasDao.close();
		}
		
		return forward(getViewPage());
	}

	/**
	 * Exibe as matrículas das séries cursadas do discente selecionado
	 * <br>Método não invocado por JSP´s.
	 * @return
	 * @throws DAOException
	 */
	public String exibirMatriculas() throws DAOException {
		
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		try {
			matriculasSerie = dao.findAllMatriculasByDiscente(obj.getDiscente(), null, SituacaoMatriculaSerie.APROVADO,
					SituacaoMatriculaSerie.CANCELADO, SituacaoMatriculaSerie.MATRICULADO, SituacaoMatriculaSerie.REPROVADO,
					SituacaoMatriculaSerie.TRANCADO, SituacaoMatriculaSerie.APROVADO_DEPENDENCIA);
			
			if (ValidatorUtil.isEmpty(matriculasSerie)){
				addMensagemErro("O discente selecionado não possui nenhuma matrícula em série cadastrado.");
				return null;
			}
			
			if (matriculasSerie.size() == 1){
				MatriculaDiscenteSerie matricula = matriculasSerie.get(0);
				obj.setMatriculaSerie(matricula);
				return exibirBoletim();
			}
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return forward(getListPage());
	}
	
	/**
	 * Seleciona a matrícula do discente para exibição do boletim
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *   <li>/sigaa.war/medio/boletim/matriculas.jsp</li>
	 * </ul>
	 * @return
	 * @throws DAOException
	 */
	public String selecionarMatricula() throws DAOException{
		
		int id = getParameterInt("id",0);
		if (id == 0){
			addMensagemErro("Nenhuma matrícula selecionada.");
			return null;
		}
			
		MatriculaDiscenteSerieDao dao = getDAO(MatriculaDiscenteSerieDao.class);
		MatriculaDiscenteSerie matriculaSerie = dao.findByPrimaryKey(id, MatriculaDiscenteSerie.class);	
		obj.setMatriculaSerie(matriculaSerie);
		
		return exibirBoletim();
		
	}

	/**
	 * Seleciona o discente para exibição do boletim
	 *  <br>Método não invocado por JSP´s.
	 */
	@Override
	public String selecionaDiscente() throws ArqException {
		
		RegraNotaDao dao = getDAO(RegraNotaDao.class); 
		try {
			
			List<RegraNota> regras = dao.findByCurso(obj.getDiscente().getCurso());
			if (ValidatorUtil.isEmpty(regras)){
				addMensagemErro("Não foram definidas as regras de consolidação.");
				return null;
			}	
			
			obj.setRegraNotas(regras);
			
		} finally {
			if (dao != null)
				dao.close();
		}
		
		return exibirMatriculas();
	}
	
	/** Caminho do boletim */
	@Override
	public String getViewPage() {
		return "/medio/boletim/boletim.jsp";
	}

	/** Caminho da listagem de matrículas do discente */
	@Override
	public String getListPage() {
		return "/medio/boletim/matriculas.jsp";
	}

	/**
	 * Atribui o discente encontrado.
	 */
	@Override
	public void setDiscente(DiscenteAdapter discente) throws ArqException {
		obj.setDiscente( (DiscenteMedio) discente );
	}

	public List<MatriculaDiscenteSerie> getMatriculasSerie() {
		return matriculasSerie;
	}

	public void setMatriculasSerie(List<MatriculaDiscenteSerie> matriculasSerie) {
		this.matriculasSerie = matriculasSerie;
	}
}
