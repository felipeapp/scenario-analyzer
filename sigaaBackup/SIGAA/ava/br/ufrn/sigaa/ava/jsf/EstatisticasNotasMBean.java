/*
 * Universidade Federal do Rio Grande do Norte
 * Superintend�ncia de Inform�tica 
 * Diretoria de Sistemas
 * 
 * Criado em: 08/06/2011
 */
package br.ufrn.sigaa.ava.jsf;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.erros.DAOException;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.ava.dao.EstatisticasNotasDao;
import br.ufrn.sigaa.dominio.ParametrosGestoraAcademica;
import br.ufrn.sigaa.ensino.dominio.MetodoAvaliacao;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.negocio.ParametrosGestoraAcademicaHelper;

/**
 * Managed Bean para buscar informa��es de m�dias e desvios padr�es
 * das notas dos alunos para serem usados pelo relat�rio de estat�sticas
 * de notas da turma virtual.
 * 
 * @author David Pereira
 *
 */
@Component @Scope("request")
public class EstatisticasNotasMBean extends ControllerTurmaVirtual {
	
	private Integer unidade;

	private Turma turma;
	
	/**
	 * Retorna a m�dia de notas da unidade 1.
	 * @return
	 */
	public double getMediaUnidade1() {
		return getMediaUnidade(1);
	}
	
	/**
	 * Retorna a m�dia de notas da unidade 2.
	 * @return
	 */
	public double getMediaUnidade2() {
		return getMediaUnidade(2);		
	}
	
	/**
	 * Retorna a m�dia de notas da unidade 3.
	 * @return
	 */
	public double getMediaUnidade3() {
		return getMediaUnidade(3);
	}

	/**
	 * Retorna a m�dia de notas de acordo com a unidade passada.
	 * @return
	 */
	private double getMediaUnidade(Integer unidade) {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		double media = 0;
		
		try {
			media = dao.findMediaUnidade(turma.getId(), unidade);
		} finally {
			dao.close();
		}
		
		return media;
	}
	
	/**
	 * Retorna a m�dia de notas da recupera��o.
	 * @return
	 */
	public double getMediaRecup() {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		return dao.findMediaRecup(turma.getId());
	}
	
	/**
	 * Retorna a m�dia das m�dias finais dos alunos.
	 * @return
	 */
	public double getMediaFinal() {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		return dao.findMediaFinal(turma.getId());
	}
	
	/**
	 * Retorna o desvio padr�o das notas da unidade 1.
	 * @return
	 */
	public double getDesvioUnidade1() {
		return getDesvioUnidade(1);
	}
	
	/**
	 * Retorna o desvio padr�o das notas da unidade 2.
	 * @return
	 */
	public double getDesvioUnidade2() {
		return getDesvioUnidade(2);
	}
	
	/**
	 * Retorna o desvio padr�o das notas da unidade 3.
	 * @return
	 */
	public double getDesvioUnidade3() {
		return getDesvioUnidade(3);
	}
	
	/**
	 * Retorna o desvio padr�o das notas da unidade 1.
	 * @return
	 */
	public double getDesvioUnidade(Integer unidade) {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		double desvio = 0;
		
		try {
			desvio = dao.findDesvioUnidade(turma.getId(), unidade);
		} finally {
			dao.close();
		}
		
		return desvio;
	}
	
	/**
	 * Retorna o desvio padr�o das notas da recupera��o.
	 * @return
	 */
	public double getDesvioRecup() {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		return dao.findDesvioRecup(turma.getId());
	}
	
	/**
	 * Retorna o desvio padr�o das m�dias finais dos alunos.
	 * @return
	 */
	public double getDesvioFinal() {
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		return dao.findDesvioFinal(turma.getId());
	}
	
	/**
	 * Retorna todas as unidades que o professor j� lan�ou nota.
	 * M�todo utilizado para renderizar os relat�rios de notas por unidade.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public List<Integer> getUnidadesComNota() throws DAOException {
		List<Integer> unidades = new ArrayList<Integer>();
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		
		try {
			if ( turma != null )
				unidades = dao.findUnidadesComNota(turma.getId());
		} finally {
			dao.close();
		}
		
		return unidades;
	}
	
	/**
	 * Define se informa��es como m�dia e desvio padr�o devem ser mostradas.
	 * Essas informa��es s� s�o mostradas quando a turma for avaliada por nota.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public boolean isMostrarMedias() throws DAOException {
		if ( turma != null ){
			ParametrosGestoraAcademica p = ParametrosGestoraAcademicaHelper.getParametros(turma.getDisciplina());
			return p.getMetodoAvaliacao() == MetodoAvaliacao.NOTA;
		} else
			return false;
	}
	
	/**
	 * Valida os dados do mbean.
	 * 
	 * @return
	 * @throws DAOException
	 */
	public String getValidarDados() throws DAOException {
		if (turma == null){
			addMensagemErro("O procedimento que voc� tentou realizar j� foi processado anteriormente." +
			" Para realiz�-lo novamente, reinicie o processo utilizando os links oferecidos pelo sistema.");
			return redirectJSF(getSubSistema().getLink());
		}
		return null;
	}
	
	public boolean isMostrarGraficoRecuperacao() {
		boolean mostrar = false;
		EstatisticasNotasDao dao = getDAO(EstatisticasNotasDao.class);
		
		try {
			if ( turma != null )
				mostrar = dao.hasRecuperacao(turma.getId());
		} finally {
			dao.close();
		}
		
		return mostrar;
	}
	
	/**
	 * Seleciona a sub-turma caso a turma da turma virtual seja agrupadora.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/subturmas_estatistica.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String escolherSubTurma() throws ArqException {
		turma = getDAO(TurmaDao.class).findByPrimaryKey(getParameterInt("id"), Turma.class);
		turma.getDisciplina().setUnidade(getDAO(TurmaDao.class).refresh(turma.getDisciplina().getUnidade()));

		return forward("/ava/estatisticas_notas.jsf");

	}

	/**
	 * Caso a estatisticas seja iniciada em uma turma agrupadora, 
	 * ela dever� acontecer sub-turma por sub-turma. Este m�todo � chamado para 
	 * listar as sub-turmas da turma agrupadora.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/menu.jsp
	 * @return
	 */
	public String listaSubTurmas() {
		return forward("/ava/subturmas_estatisticas.jsp");
	}
	
	
	/**
	 * Seleciona a sub-turma caso a turma da turma virtual seja agrupadora.<br/><br/>
	 * 
	 * M�todo chamado pela seguinte JSP: /ava/subturmas_estatistica.jsp
	 * @return
	 * @throws ArqException 
	 */
	public String iniciar() throws ArqException {
		turma = turma();
		
		return forward("/ava/estatisticas_notas.jsf");

	}
	
	public Integer getUnidade() {
		return unidade;
	}

	public void setUnidade(Integer unidade) {
		this.unidade = unidade;
	}

	public void setTurma(Turma turma) {
		this.turma = turma;
	}

	public Turma getTurma() {
		return turma;
	}
	
}
