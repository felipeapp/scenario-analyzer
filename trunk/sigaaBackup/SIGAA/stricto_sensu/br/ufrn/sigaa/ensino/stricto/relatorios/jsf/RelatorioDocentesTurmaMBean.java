/* 
* Universidade Federal do Rio Grande do Norte
* Superintend�ncia de Inform�tica
* Diretoria de Sistemas
 *
 * Created on 22/01/2009
 *
 */
 
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collections;
import java.util.List;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.DocenteTurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.dominio.DocenteTurma;

/** 
 * Este mbean � respons�vel pelo relat�rio de docentes por turma de p�s STRICTO
 * 
 * @author Victor Hugo
 */
@Component("relatorioDocentesTurmaBean") @Scope("request")
public class RelatorioDocentesTurmaMBean extends SigaaAbstractController<DocenteTurma> {
	/** Ano e per�odo informado */
	private Integer ano, periodo;
	/** Programa selecionado */
	private Unidade programa;
	/** Lista de Docentes */
	private List<DocenteTurma> docentes;
	
	/** Construtor da classe */
	public RelatorioDocentesTurmaMBean() {
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
		if( getProgramaStricto() != null )
			programa = getProgramaStricto();
		else
			programa = new Unidade();
				
	}
	
	/**
	 * Inicia o relat�rio, exibe a pagina para a sele��o dos filtros do relat�rio.
	 * <br/><br/>
	 * M�todo chamado pelas seguintes JSPs:
	 * <ul>
	 *    <li>/sigaa.war/stricto/menus/relatorios.jsp</li>
	 *    <li>/sigaa.war/stricto/menu_coordenador.jsp</li>
	 * </ul>
	 * @return
	 * @throws SegurancaException 
	 */
	public String iniciar() throws SegurancaException{
		checkRole( SigaaPapeis.PPG, SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS );
		return forward( getFormPage() );
	}
	
	/**
	 * Exibe o relat�rio
	 * <br/><br/>
	 * M�todo chamado pela seguinte JSP:
	 * <ul>
	 *    <li>/sigaa.war/stricto/relatorios/docentes_turma/form.jsp</li>
	 * </ul> 
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		if( isEmpty(ano) || isEmpty(periodo) ){
			addMensagemErro("Informe o ano e o per�odo.");
			return null;
		}
		if( isEmpty(programa) && getProgramaStricto() != null )
			programa = getProgramaStricto();
		else if( isEmpty(programa) ){
			addMensagemErro("Selecione o programa.");
			return null;
		}
		
		DocenteTurmaDao dao = getDAO( DocenteTurmaDao.class );
		docentes = dao.findByAnoPeriodoUnidade(programa.getId(), ano, periodo);
		Collections.sort(docentes);
		if( isEmpty( docentes ) ){
			addMensagemErro("N�o existe nenhum docentes nas turmas do ano/per�odo selecionados.");
			return null;
		}
		
		return forward( getDirBase() + "/relatorio.jsp" );
	}
	
	@Override
	public String getDirBase() {
		return "/stricto/relatorios/docentes_turma";
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

	public Unidade getPrograma() {
		return programa;
	}

	public void setPrograma(Unidade programa) {
		this.programa = programa;
	}

	public List<DocenteTurma> getDocentes() {
		return docentes;
	}

	public void setDocentes(List<DocenteTurma> docentes) {
		this.docentes = docentes;
	}

}
