/*
 * Universidade Federal do Rio Grande do Norte
 * Superintendência de Informática
 * Diretoria de Sistemas
 *
 * Created on 29/09/2010
 *
 */
package br.ufrn.sigaa.ensino.jsf;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;

import javax.faces.model.SelectItem;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.academico.dominio.NivelEnsino;
import br.ufrn.arq.erros.ArqException;
import br.ufrn.arq.util.ValidatorUtil;
import br.ufrn.sigaa.arq.dao.ensino.RelatorioEntradaNotasDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.CalendarioAcademico;
import br.ufrn.sigaa.ensino.dominio.Turma;
import br.ufrn.sigaa.ensino.relatorios.LinhaRelatorioEntradaNotas;
import br.ufrn.sigaa.ensino.tecnico.dao.ModuloDao;
import br.ufrn.sigaa.ensino.tecnico.dominio.Modulo;
import br.ufrn.sigaa.mensagens.MensagensGerais;

/**
 * Controlador responsável pela emissão do Relatório de Entrada de Notas, um
 * relatório que detalha, para um conjunto de turmas, se as notas de cada
 * unidade foram lançadas ou não.
 * 
 * @author Leonardo Campos
 * 
 */
@Component
@Scope("request")
public class RelatorioEntradaNotasMBean extends SigaaAbstractController<Object> {

	// Atributos utilizados na busca
	/** Ano e período das turmas para restringir o resultado da busca */
	private Integer ano, periodo;
	/** Módulo dos componentes das turmas para restringir o resultado da busca */
	private Modulo modulo;

	/** Coleção de módulos disponíveis para seleção na busca */
	private Collection<SelectItem> modulosCombo = new ArrayList<SelectItem>();

	/** Coleção de turmas retornadas pela busca para serem exibidas no relatório */
	private Collection<Turma> turmas = new ArrayList<Turma>();
	/** Coleção de linhas com as informações que compõem o relatório */
	private Collection<LinhaRelatorioEntradaNotas> linhas = new TreeSet<LinhaRelatorioEntradaNotas>();

	
	/**
	 * Encaminha para a tela do formulário de busca das turmas do ensino técnico
	 * 
	 * Método não é invocado por jsp
	 * 
	 * @return
	 */
	public String telaFormularioBuscaTurmasTecnico(){
		return "/ensino/tecnico/relatorios/form_relatorio_entrada_notas.jsp";
	}
	
	/**
	 * Encaminha para a tela do relatório de entrada de notas
	 * 
	 * Método não é invocado por jsp
	 * 
	 * @return
	 */
	public String telaRelatorioEntradaNotas(){
		return "/ensino/relatorios/relatorio_entrada_notas.jsp";
	}
	
	/**
	 * Popula as informações necessárias para a busca das turmas do ensino técnico
	 * que irão compor o relatório.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war/WEB-INF/jsp/ensino/tecnico/menu/relatorios.jsp</li>
	 * </ul>
	 * 
	 * @return
	 */
	public String iniciarTecnico() throws ArqException {
		modulo = new Modulo();
		modulosCombo = toSelectItems(getDAO(ModuloDao.class).findAll(
				getParametrosAcademicos().getUnidade().getId(),
				NivelEnsino.TECNICO, null), "id", "descricao");
		CalendarioAcademico cal = getCalendarioVigente();
		ano = cal.getAno();
		periodo = cal.getPeriodo();
		return forward(telaFormularioBuscaTurmasTecnico());
	}
	
	/**
	 * Busca as turmas do ensino técnico conforme os filtros selecionados.
	 * 
	 * Método chamado pela(s) seguinte(s) JSP(s):
	 * <ul>
	 * <li>sigaa.war//ensino/tecnico/relatorios/form_relatorio_entrada_notas.jsp</li>
	 * </ul>
	 * 
	 * @return
	 * @throws ArqException
	 */
	public String buscarTurmasTecnico() throws ArqException {
		ValidatorUtil.validateRequired(ano, "Ano", erros);
		ValidatorUtil.validateRequired(periodo, "Período", erros);
		ValidatorUtil.validateRequiredId(modulo.getId(), "Módulo", erros);
		if(hasErrors())
			return null;
		
		RelatorioEntradaNotasDao dao = getDAO(RelatorioEntradaNotasDao.class);
		turmas = dao.findTurmasTecnico(ano, periodo, modulo.getId());
		modulo = dao.refresh(modulo);
		return gerarRelatorio();
	}

	/**
	 * Monta o relatório de entrada de notas a partir das turmas pré-selecionadas.
	 * 
	 * Método não é invocado por jsp
	 * 
	 * @return
	 * @throws ArqException
	 */
	private String gerarRelatorio() throws ArqException {
		
		if(ValidatorUtil.isEmpty(turmas)) {
			addMensagem(MensagensGerais.RELATORIO_VAZIO, "Relatório de Entrada de Notas");
			return null;
		}
		
		Map<Integer, Turma> mapaTurmas = new HashMap<Integer, Turma>();
		for(Turma t: turmas)
			mapaTurmas.put(t.getId(), t);
		
		linhas = getDAO(RelatorioEntradaNotasDao.class).findRelatorioEntradaNotas(mapaTurmas);
		
		return forward(telaRelatorioEntradaNotas());
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Integer getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Integer periodo) {
		this.periodo = periodo;
	}

	public Modulo getModulo() {
		return modulo;
	}

	public void setModulo(Modulo modulo) {
		this.modulo = modulo;
	}

	public Collection<SelectItem> getModulosCombo() {
		return modulosCombo;
	}

	public void setModulosCombo(Collection<SelectItem> modulosCombo) {
		this.modulosCombo = modulosCombo;
	}

	public Collection<Turma> getTurmas() {
		return turmas;
	}

	public void setTurmas(Collection<Turma> turmas) {
		this.turmas = turmas;
	}

	public Collection<LinhaRelatorioEntradaNotas> getLinhas() {
		return linhas;
	}

	public void setLinhas(Collection<LinhaRelatorioEntradaNotas> linhas) {
		this.linhas = linhas;
	}
	
}
