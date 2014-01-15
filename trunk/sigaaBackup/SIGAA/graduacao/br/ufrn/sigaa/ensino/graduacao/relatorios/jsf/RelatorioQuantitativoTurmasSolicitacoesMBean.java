/* 
 * Superintendência de Informática - Diretoria de Sistemas
 * Projeto: SIGAA - Sistema Integrado de Gestão de Atividades Acadêmicas
 * Criado em: 20/01/2009
 */
package br.ufrn.sigaa.ensino.graduacao.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.ArrayList;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.sigaa.arq.dao.ensino.TurmaDao;
import br.ufrn.sigaa.arq.jsf.SigaaAbstractController;
import br.ufrn.sigaa.ensino.graduacao.relatorios.dominio.RelatorioQuantitativoTurmas;

/** 
 * Este Mbean é responsável pelo relatório que exibe o quantitativo de solicitações, 
 * turmas, matriculas solicitadas, matriculas efetivas e 
 * matriculas indeferidas por componente curricular por semestre
 * @author Victor Hugo
 */
@Component("relatorioQuantitativoTurmasSolicitacoesBean") @Scope("request")
public class RelatorioQuantitativoTurmasSolicitacoesMBean extends SigaaAbstractController {

	private ArrayList<RelatorioQuantitativoTurmas> relatorio;
	private Integer ano;
	private Integer periodo;
	
	/**
	 * construtor
	 */
	public RelatorioQuantitativoTurmasSolicitacoesMBean() {
		ano = getCalendarioVigente().getAno();
		periodo = getCalendarioVigente().getPeriodo();
	}
	
	/**
	 * inicia o caso de uso, vai para a página para seleção de filtros do relatório 
	 * @return
	 * @throws SegurancaException
	 */
	public String iniciar() throws SegurancaException{
		checkRole(SigaaPapeis.ADMINISTRADOR_DAE);
		return forward( getFormPage() );
	}

	/**
	 * gera o relatório
	 * @return
	 * @throws DAOException 
	 */
	public String gerarRelatorio() throws DAOException{
		
		if( isEmpty( ano ) || isEmpty(periodo) ){
			addMensagemErro( "Informe o ano e o período." );
			return null;
		}
		
		TurmaDao dao = getDAO( TurmaDao.class );
		relatorio = dao.gerarRelatorioQuantitativoGeralTurmas( ano , periodo);
		
		return telaRelatorio();
	}
	
	/**
	 * vai para a tela do relatório
	 * @return
	 */
	public String telaRelatorio(){
		System.out.println( getDirBase() + "/relatorio.jsf" );
		return forward( getDirBase() + "/relatorio.jsf");
	}
	
	@Override
	public String getDirBase() {
		return "/graduacao/relatorios/quantitativo_geral_turmas";
	}

	public ArrayList<RelatorioQuantitativoTurmas> getRelatorio() {
		return relatorio;
	}

	public void setRelatorio(ArrayList<RelatorioQuantitativoTurmas> relatorio) {
		this.relatorio = relatorio;
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

}
