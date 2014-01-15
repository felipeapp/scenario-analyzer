/*
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
*
* Created on 01/12/2006
*/

package br.ufrn.sigaa.ensino.form;

import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.negocio.validacao.MensagemAviso;
import br.ufrn.arq.negocio.validacao.TipoMensagemUFRN;
import br.ufrn.arq.util.RequestUtils;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.struts.SigaaForm;
import br.ufrn.sigaa.ensino.dominio.ComponenteCurricular;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.dominio.HorarioTurma;
import br.ufrn.sigaa.ensino.dominio.SituacaoTurma;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.latosensu.dominio.CursoLato;
import br.ufrn.sigaa.ensino.latosensu.dominio.RegistroAlteracaoLato;
import br.ufrn.sigaa.ensino.tecnico.dominio.EspecializacaoTurmaEntrada;
import br.ufrn.sigaa.pessoa.dominio.DocenteExterno;
import br.ufrn.sigaa.pessoa.dominio.Pessoa;
import br.ufrn.sigaa.pessoa.dominio.Servidor;

/**
 * Classe que trabalha com os atributos necessários para os casos de uso que utilizam a 
 * classe Turma.
 * 
 * @author Gleydson
 */
public class TurmaForm extends SigaaForm<Turma> {

	/** Data inicial da turma */
	String dataInicio;

	/** Data final da turma */
	String dataFim;

	/** Hora inicial da turma */
	String horaInicial;

	/** Hora final da turma */
	String horaFinal;

	/** Docente da Turma */
	private DocenteTurma docenteTurma;

	/** Horário da turma */
	private HorarioTurma horario;

	/** Se para a turma existe um pós horário */
	private int posHorario;

	/** Chave primária do docente */
	private int docenteId;

	/** Atributos usados para busca */
	private ComponenteCurricular disciplina = new ComponenteCurricular();

	private int[] filtros = {};
	
	public static final int FILTRO_DISCIPLINA = 1;
	public static final int FILTRO_DOCENTE = 2;
	public static final int FILTRO_ANO_PERIODO = 3;
	
	/** Curso de Lato que a turma está inserida */
	private CursoLato curso = new CursoLato();

	/** Registra a Alteração realizada pelo nível de Lato */
	private RegistroAlteracaoLato registroAlteracao;
	
	/** Variável para remover ítens de uma lista pela posição */ 
	private int posLista;

	public int getDocenteId() {
		return docenteId;
	}

	public void setDocenteId(int docenteId) {
		this.docenteId = docenteId;
	}

	public int getPosHorario() {
		return posHorario;
	}

	public void setPosHorario(int posHorario) {
		this.posHorario = posHorario;
	}

	public TurmaForm() {
		try {
			clear();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** Inicializa as informações da turma */
	@Override
	public void clear() throws Exception {
		obj = new Turma();
		obj.setDisciplina(new ComponenteCurricular());
		obj.setSituacaoTurma(new SituacaoTurma(SituacaoTurma.ABERTA));
		obj.setEspecializacao(new EspecializacaoTurmaEntrada());
		dataFim = "";
		dataInicio = "";

		initDocenteTurma();

		horario = new HorarioTurma();

	}

	/** Inicializa as informações do Docente da turma */
	public void initDocenteTurma() {
		docenteTurma = new DocenteTurma();
		docenteTurma.setDocente(new Servidor());
		docenteTurma.setDocenteExterno(new DocenteExterno());
		docenteTurma.getDocente().setPessoa(new Pessoa());
		docenteTurma.setTurma(obj);
	}

	public String getDataFim() {
		return dataFim;
	}

	public void setDataFim(String dataFim) {
		this.dataFim = dataFim;
	}

	public String getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(String dataInicio) {
		this.dataInicio = dataInicio;
	}

	public DocenteTurma getDocenteTurma() {
		return docenteTurma;
	}

	public void setDocenteTurma(DocenteTurma docenteTurma) {
		this.docenteTurma = docenteTurma;
	}

	public HorarioTurma getHorarioTurma() {
		return horario;
	}

	public void setHorarioTurma(HorarioTurma horario) {
		this.horario = horario;
	}

	public String getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(String horaFinal) {
		this.horaFinal = horaFinal;
	}

	public String getHoraInicial() {
		return horaInicial;
	}

	public void setHoraInicial(String horaInicial) {
		this.horaInicial = horaInicial;
	}

	@Override
	public Collection<Turma> customSearch(HttpServletRequest req) throws ArqException {

		TurmaDao dao = getDAO(TurmaDao.class, req);
		try {
			int idDisciplina =0 , idSituacao, ano = 0, periodo = 0, idDocente = 0;
			idSituacao = RequestUtils.getIntParameter(req, "obj.situacaoTurma.id");
			for(int filtro: getFiltros()){
				switch (filtro) {
				case FILTRO_DISCIPLINA:
					idDisciplina = RequestUtils.getIntParameter(req, "disciplina.id");
					break;
				case FILTRO_DOCENTE:
					idDocente = RequestUtils.getIntParameter(req, "docenteTurma.docente.id");
					break;
				case FILTRO_ANO_PERIODO:
					ano = RequestUtils.getIntParameter(req, "obj.ano");
					periodo = RequestUtils.getIntParameter(req, "obj.periodo");
					break;
				}
			}
			if (idDisciplina == 0 && ano == 0 && periodo == 0 && idDocente == 0){
				addMensagem(new MensagemAviso("Selecione uma forma de busca e informe o parâmetro de busca.", TipoMensagemUFRN.ERROR), req);
				return new ArrayList<Turma>(0);
			}
			
			if(getNivelEnsino(req) != 'L')
				return dao.findCompleto(idDisciplina, idSituacao, ano, periodo, idDocente, getUnidadeGestora(req), getNivelEnsino(req), getPaging(req));
			else
				return dao.findCompleto(idDisciplina, idSituacao, ano, periodo, idDocente, -1, getNivelEnsino(req), getPaging(req));
		} finally {
			dao.close();
		}
	}

	public ComponenteCurricular getDisciplina() {
		return disciplina;
	}

	public void setDisciplina(ComponenteCurricular disciplina) {
		this.disciplina = disciplina;
	}

	public CursoLato getCurso() {
		return curso;
	}

	public void setCurso(CursoLato curso) {
		this.curso = curso;
	}

	public RegistroAlteracaoLato getRegistroAlteracao() {
		return registroAlteracao;
	}

	public void setRegistroAlteracao(RegistroAlteracaoLato registroAlteracao) {
		this.registroAlteracao = registroAlteracao;
	}

	public boolean isMatriculada() {
		return obj.getQtdMatriculados() > 0;
	}

	public int[] getFiltros() {
		return filtros;
	}

	public void setFiltros(int[] filtros) {
		this.filtros = filtros;
	}

	public int getPosLista() {
		return posLista;
	}

	public void setPosLista(int posLista) {
		this.posLista = posLista;
	}

}
