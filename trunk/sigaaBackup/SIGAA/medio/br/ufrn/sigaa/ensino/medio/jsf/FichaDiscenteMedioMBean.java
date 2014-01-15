package br.ufrn.sigaa.ensino.medio.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.mensagens.MensagensArquitetura;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.Turno;
import br.ufrn.sigaa.ensino.medio.dao.TurmaSerieDao;
import br.ufrn.sigaa.ensino.medio.dominio.CursoMedio;
import br.ufrn.sigaa.ensino.medio.dominio.MatriculaDiscenteSerie;
import br.ufrn.sigaa.ensino.medio.dominio.Serie;
import br.ufrn.sigaa.ensino.medio.dominio.TurmaSerie;

/**
 * Controller para exibir uma listagem contendo os dados pessoais dos alunos de uma determinada turma de seu quadro.
 * 
 * @author Suelton Miguel
 */
@Component("fichaDiscenteMedio") @Scope(value="request")
public class FichaDiscenteMedioMBean extends SigaaAbstractController<TurmaSerie> {
	
	/** Define o link para o formulário de dados gerais. */
	public static final String PAGINA_RELATORIO = "/medio/ficha_discente_medio/relatorio.jsp";
	
	/** Collection que irá armazenar a listagem das turmas séries. */
	private Collection<TurmaSerie> listaTurmaSeries= new ArrayList<TurmaSerie>();
	
	/** Indica se a busca também vai incluir as turmas inativas.*/
	private boolean turmasInativas;
	
	/**
	 * Construtor padrão
	 */
	public FichaDiscenteMedioMBean() {
		initObj();
	}
	
	/** Inicializando das variáveis utilizadas */
	private void initObj(){
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
		return "/medio/ficha_discente_medio/";
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
	 *  <li>/sigaa.war/medio/ficha_discente_medio/lista.jsp</li>
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
	 *  <li>/sigaa.war/medio/ficha_discente_medio/lista.jsp</li>
	 * </ul>
	 * @return
	 */
	public String selecionarTurma()  throws DAOException{
		setId();
		populateObj();
		
		TurmaSerieDao dao = null;
		Collection<Integer> repetentes;
		try {
			dao = getDAO(TurmaSerieDao.class);
			repetentes = dao.findIdsRepetentesBySerie(obj);
			
		} finally {
			if (dao != null)
			dao.close();
		}
		
		for(MatriculaDiscenteSerie a :obj.getAlunos()) {
			for(Integer idRepetente :repetentes) {
				if (idRepetente == a.getId())
					a.setRepetente(true);
			}
		}
		 
		
		List<MatriculaDiscenteSerie> discentes = new ArrayList<MatriculaDiscenteSerie>(obj.getAlunos()); 
		Collections.sort(discentes,new Comparator<MatriculaDiscenteSerie>() {
			public int compare(MatriculaDiscenteSerie o1, MatriculaDiscenteSerie o2) {
				return o1.getDiscenteMedio().getNome().compareToIgnoreCase(o2.getDiscenteMedio().getNome());
			}
		});
		obj.setAlunos(discentes);
			
			
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

}
