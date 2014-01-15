/* 
* Universidade Federal do Rio Grande do Norte
* Superintendência de Informática
* Diretoria de Sistemas
 *
 * Created on 22/01/2009
 *
 */
 
package br.ufrn.sigaa.ensino.stricto.docenciaassistida.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.List;
import java.util.Map;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;
import br.ufrn.sigaa.ensino.stricto.docenciaassistida.dao.TurmaDocenciaAssistidaDao;

/** 
 * Este mbean é responsável pelo relatório das Turmas de Docência Assistida
 * 
 * @author Mario Rizzi
 */
@Component("relatorioTurmasDocenciaAssistida") @Scope("request")
public class RelatorioTurmasDocenciaAssistidaMBean extends SigaaAbstractController<DocenteTurma> {
	
	/** Ano e período informado */
	private Integer ano, periodo;
	/** Departamento selecionado */
	private Integer idDepartamento;
	/** Lista das Turmas que são atendidas por docência assistida */
	private List<Map<String, Object>> turmasDocenciaAssistida;
	
	/** Construtor da classe */
	public RelatorioTurmasDocenciaAssistidaMBean() {
		ano = (ano == null) ? getCalendarioVigente().getAno() : ano;
		periodo = (periodo == null) ? getCalendarioVigente().getPeriodo() : periodo;
	}
	
	/**
	 * Inicia o relatório, exibe a pagina para a seleção dos filtros do relatório.
	 * <br/><br/>
	 * Método chamado pelas seguintes JSPs:
	 * <ul>
	 *    <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 *    <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException{
		checkRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.MEMBRO_APOIO_DOCENCIA_ASSISTIDA );
		return forward( getFormPage() );
	}
	
	/**
	 * Exibe o relatório
	 * <br/><br/>
	 * Método chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/relatorios/docentes_turma/form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		if( isEmpty(ano) || isEmpty(periodo) ){
			addMensagemErro("Informe o ano e o período.");
			return null;
		}
//		if( isEmpty(idDepartamento) ){
//			addMensagemErro("Selecione o departamento.");
//			return null;
//		}
		
		TurmaDocenciaAssistidaDao dao = getDAO( TurmaDocenciaAssistidaDao.class );
		turmasDocenciaAssistida = dao.findRelatorioByAnoPeriodo( ano, periodo );

		if( isEmpty( turmasDocenciaAssistida ) ){
			addMensagemErro("Não existe nenhuma turma atendida para docência assistida para o ano e período selecionados.");
			return null;
		}
		
		return forward( getDirBase() + "/relatorio.jsp" );
	}
	
	/**
	 * Cancela a geração do relatório e redireciona par o subsistema
	 * @see br.ufrn.arq.web.jsf.AbstractController#cancelar()
	 */
	@Override
	public String cancelar() {
		// TODO Auto-generated method stub
		return super.cancelar();
	}
	
	@Override
	public String getDirBase() {
		return "/stricto/relatorios/turma_docencia_assistida";
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

	public Integer getIdDepartamento() {
		return idDepartamento;
	}

	public void setIdDepartamento(Integer idDepartamento) {
		this.idDepartamento = idDepartamento;
	}

	public List<Map<String, Object>> getTurmasDocenciaAssistida() {
		return turmasDocenciaAssistida;
	}

	public void setTurmasDocenciaAssistida(
			List<Map<String, Object>> turmasDocenciaAssistida) {
		this.turmasDocenciaAssistida = turmasDocenciaAssistida;
	}

}
