package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dao.RegraNotaDao;
import br.ufrn.sigaa.ensino.dominio.RegraNota;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dao.NotaSerieDao;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.NotaSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Controller para exibir uma um relatório de médias de uma determinada turma.
 * 
 * @author Suelton Miguel
 */
@Component("graficoBimestralNotas") @Scope(value="request")
public class GraficoBimestralNotasMedioMBean extends SigaaAbstractController<TurmaSerie> {
	
	
	/** Define o link para o formulário de dados gerais. */
	public static final String PAGINA_RELATORIO = "/medio/grafico_bimestral_notas/relatorio.jsp";
	
	/** Collection que irá armazenar a listagem das turmas séries. */
	private Collection<TurmaSerie> listaTurmaSeries= new ArrayList<TurmaSerie>();
	
	/** Indica se a busca também vai incluir as turmas inativas.*/
	private boolean turmasInativas;
	
	/** Modelo de Configurações das notas */
	private List<RegraNota> regraNotas;
	/**Lista que armazena a media de um discente por semestre.*/
	private List<Map<String,Object>> mediaDiscenteBimestre = new ArrayList<Map<String,Object>>();
	
	/** Objeto que guarda as informações sobre os parâmetros da unidade gestora da turma. */
	private ParametrosGestoraAcademica param;

	
	/**
	 * Construtor padrão.
	 */
	public GraficoBimestralNotasMedioMBean() {
		initObj();
	}
	
	/** Inicializando das variáveis utilizadas */
	private void initObj() {
		obj = new TurmaSerie();
		obj.setSerie(new Serie());
		obj.getSerie().setCursoMedio(new CursoMedio());
		obj.setTurno(new Turno());
		turmasInativas = false;
	}
	
	/**
	 * Diretório que se encontra as view's
	 * <br />
     * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
	 * </ul>
	 */
	@Override
	public String getDirBase() {
		return "/medio/grafico_bimestral_notas/";
	}
	
	/**
	 * Diretório base da lista.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>Método não invocado por JSP</li>
	 * </ul>
	 * @return
	 */
	@Override
	public String getListPage() {
		return getDirBase() + "lista.jsp";
	}
	
	/**
	 * Redireciona para o formulário de busca.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/menus/relatorios.jsp</li>
	 * </ul>
	 * @return
	 */
	public String iniciarBusca() {
		return forward(getListPage());
	}
	
	
	/** 
	 * Executa a busca de turmas de acordo com os parâmetros passados. 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/grafico_bimestral_notas/lista.jsp</li>
	 * </ul>
	 */
	public String buscar() throws DAOException {
		
		if (obj.getAno() == null || obj.getAno().equals(0))
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Ano");
		if (obj.getSerie().getCursoMedio().getId() == 0 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Curso");
		if (obj.getSerie().getId() == 0 )
			addMensagem(MensagensArquitetura.CAMPO_OBRIGATORIO_NAO_INFORMADO, "Série");
		
		if (hasErrors())
			return null;
		
		
		TurmaSerieDao dao = null;
		try {
			dao = getDAO(TurmaSerieDao.class);
		listaTurmaSeries = dao.findByCursoSerieAno(obj.getSerie().getCursoMedio(), 
				obj.getSerie(), obj.getAno(), turmasInativas , getNivelEnsino(), false);
		} finally {
			if (dao != null)
				dao.close();
		}
		
		if (listaTurmaSeries.isEmpty())
			addMensagemErro("Não foram encontradas turmas para os dados informados.");
			
		return getListPage();
	}
	
	/**
	 * Método que redirecionar o usuário para o relatório.
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 *  <li>/sigaa.war/medio/grafico_bimestral_notas/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarTurma() throws DAOException, ArqException {
		setId();
		populateObj();
		mediaDiscenteBimestre = new ArrayList<Map<String,Object>>();
		param = getParametrosAcademicos();
		
		NotaSerieDao nDao = null;
		RegraNotaDao rDao = null;

		try {
			nDao = getDAO(NotaSerieDao.class);
			rDao = getDAO(RegraNotaDao.class);
			regraNotas = rDao.findByCurso(obj.getSerie().getCursoMedio());
						
			for (MatriculaDiscenteSerie m: obj.getAlunos()) {
				List<NotaSerie> notas = nDao.findNotasByDiscente(m.getDiscenteMedio(), obj);
				Collections.sort(notas, new  Comparator<NotaSerie>() {
					@Override
					public int compare(NotaSerie arg0, NotaSerie arg1) {
						return arg0.getRegraNota().getOrdem()-arg1.getRegraNota().getOrdem();
					}
					
				});
				
				if (!notas.isEmpty()) {
					double media = 0;
					RegraNota r = notas.iterator().next().getRegraNota();
					int count = 0;
					for (NotaSerie nota:notas) {
						if (nota.getNotaUnidade().getNota() != null) {
							if (r.getId() == nota.getRegraNota().getId()) {
								media += nota.getNotaUnidade().getNota();
								count++;
							}
							else {
								Map<String,Object> map = new HashMap<String, Object>();
								map.put("aluno",m);
								map.put("media",media/count);
								map.put("regra",r);
								mediaDiscenteBimestre.add(map);
								r = nota.getRegraNota();
								media = nota.getNotaUnidade().getNota();
								count = 1;
							}
						}
					}
					if (count > 0) {
						Map<String,Object> map = new HashMap<String, Object>();
						map.put("aluno",m);
						map.put("media",media/count);
						map.put("regra",r);
						mediaDiscenteBimestre.add(map);
					}
				}
			}
			
			if (mediaDiscenteBimestre.isEmpty()) {
				addMensagemErro("Nenhuma nota foi lançada em nenhuma das disciplinas da turma selecionada.");
				return null;
			}
			
			Collections.sort(mediaDiscenteBimestre, new  Comparator<Map<String,Object>>() {
				@Override
				public int compare(Map<String,Object> arg0, Map<String,Object> arg1) {
					RegraNota r1 = (RegraNota)arg0.get("regra");
					RegraNota r2 = (RegraNota)arg1.get("regra");
					return r1.getOrdem()-r2.getOrdem();
				}
				
			});
			
		} finally {
			if (nDao != null)
				nDao.close();
			if (rDao != null)
				rDao.close();
			
		}
					
		return forward(PAGINA_RELATORIO);
	}
	
	public Collection<TurmaSerie> getListaTurmaSeries() {
		return listaTurmaSeries;
	}

	public void setListaTurmaSeries(Collection<TurmaSerie> listaTurmaSeries) {
		this.listaTurmaSeries = listaTurmaSeries;
	}

	public boolean isTurmasInativas() {
		return turmasInativas;
	}

	public void setTurmasInativas(boolean turmasInativas) {
		this.turmasInativas = turmasInativas;
	}

	public List<RegraNota> getRegraNotas() {
		return regraNotas;
	}

	public void setRegraNotas(List<RegraNota> regraNotas) {
		this.regraNotas = regraNotas;
	}

	public List<Map<String, Object>> getMediaDiscenteBimestre() {
		return mediaDiscenteBimestre;
	}

	public void setMediaDiscenteBimestre(
			List<Map<String, Object>> mediaDiscenteBimestre) {
		this.mediaDiscenteBimestre = mediaDiscenteBimestre;
	}

	public ParametrosGestoraAcademica getParam() {
		return param;
	}

	public void setParam(ParametrosGestoraAcademica param) {
		this.param = param;
	}

}
