/*
 * Sistema Integrado de Gestão de Atividades Acadêmicas
 * Superintendência de Informática - UFRN
 *
 * Created on Oct 13, 2008
 *
 */
package br.ufrn.sigaa.ensino.stricto.relatorios.jsf;

import static br.ufrn.arq.util.ValidatorUtil.isEmpty;

import java.util.Collection;
import java.util.HashMap;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import br.ufrn.arq.erros.DAOException;
import br.ufrn.arq.erros.SegurancaException;
import br.ufrn.arq.seguranca.SigaaPapeis;
import br.ufrn.arq.util.JasperReportsUtil;
import br.ufrn.sigaa.arq.dao.CursoDao;
import br.ufrn.sigaa.arq.dao.graduacao.EstruturaCurricularDao;
import br.ufrn.sigaa.dominio.Curso;
import br.ufrn.sigaa.dominio.Unidade;
import br.ufrn.sigaa.ensino.graduacao.dominio.Curriculo;

/**
 * Relatório que mostra os alunos de pós com suas matriculas e o numero de meses cursados e pendentes
 * @author Victor Hugo
 */
@Component("relatorioAlunosMatriculasPosBean") @Scope("request")
public class RelatorioAlunosMatriculasPosMBean extends RelatoriosStrictoMBean {
	
	private Integer mestrado, doutorado, ano;
	private String nomeRelatorio;
	
	public RelatorioAlunosMatriculasPosMBean() {
	}


	public String iniciar() throws SegurancaException, DAOException{
		checkRole( SigaaPapeis.COORDENADOR_CURSO_STRICTO, SigaaPapeis.SECRETARIA_POS, SigaaPapeis.PPG );
		unidade = new Unidade();
		mestrado = new Integer(24);
		doutorado = new Integer(48);
		ano = getCalendarioVigente().getAno();
		nomeRelatorio = "trf9021_PosMatriculasEPrazo";
		
		if( getProgramaStricto() != null ){
			
			CursoDao dao = getDAO(CursoDao.class);
			Collection<Curso> cursos = dao.findByUnidade(getProgramaStricto().getId(), 'E');
			Curso mest = null;
			if( !isEmpty(cursos)  )
				mest = cursos.iterator().next();
			
			cursos = dao.findByUnidade(getProgramaStricto().getId(), 'D');
			Curso dout = null;
			if( !isEmpty(cursos)  )
				dout = cursos.iterator().next();
			
			EstruturaCurricularDao ecDao = getDAO(EstruturaCurricularDao.class);
			Collection<Curriculo> curriculos = null;
			if( mest != null ){
				curriculos = ecDao.findByCurso(mest.getId(), 'E');
				Curriculo curriculoMest = null;
				if( !isEmpty(curriculos) ){
					curriculoMest = curriculos.iterator().next();
					mestrado = curriculoMest.getMesesConclusaoIdeal();
				}
			}
			if( dout != null ){
				curriculos = ecDao.findByCurso(dout.getId(), 'D');
				Curriculo curriculoDout = null;
				if( !isEmpty(curriculos) ){
					curriculoDout = curriculos.iterator().next();
					doutorado = curriculoDout.getMesesConclusaoIdeal();					
				}
			}
			
		}
		
		return forward("/stricto/relatorios/form_alunos_matriculas_pos.jsf");
	}
	
	@Override
	public String gerarRelatorio() throws DAOException {

		if( getProgramaStricto() !=  null)
			unidade = getProgramaStricto();
		if( isEmpty(unidade) ){
			addMensagemErro("Selecione o programa.");
			return null;
		}
		if(mestrado == null || doutorado == null){
			addMensagemErro("O tempo em meses deve ser maior que zero.");
			return null;
		}
		if(mestrado <= 0 || doutorado <= 0){
			addMensagemErro("O tempo em meses deve ser maior que zero.");
			return null;
		}

    	relatorio = JasperReportsUtil.getReportSIGAA(nomeRelatorio + ".jasper");
		parametros = new HashMap<String, Object>();
        parametros.put("unidade", unidade.getId() );
        parametros.put("mestrado", mestrado );
        parametros.put("doutorado", doutorado );
        parametros.put("ano", ano );
        parametros.put("subSistema", getSubSistema().getNome());
        parametros.put("subSistemaLink", getSubSistema().getLink());

        return super.gerarRelatorio();
	}


	public Integer getMestrado() {
		return mestrado;
	}

	public void setMestrado(Integer mestrado) {
		this.mestrado = mestrado;
	}

	public Integer getDoutorado() {
		return doutorado;
	}

	public void setDoutorado(Integer doutorado) {
		this.doutorado = doutorado;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public String getNomeRelatorio() {
		return nomeRelatorio;
	}

	public void setNomeRelatorio(String nomeRelatorio) {
		this.nomeRelatorio = nomeRelatorio;
	}

}
