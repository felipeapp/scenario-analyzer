package br.ufrn.sigaa.ensino_rede.relatorios.jsf;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component @Scope("session")
public class RelatoriosEnsinoRedeMBean extends RelatoriosEnsinoRedeDiscenteAbstractController<Object> {

	public String iniciarRelatorioDiscentePorUnidade() throws Exception {
		super.limparDados();
		setFiltrarStatus(true);
		setFiltrarUnidade(true);
		setProjecao("da.pessoa.nome, da.status.descricao, da.pessoa.cpf_cnpj, instituicao.sigla as " +
				" da.dadosCurso.campus.instituicao.sigla, campus.sigla as da.dadosCurso.campus.sigla, " +
				" da.anoIngresso, da.periodoIngresso");
		setUrlDestino("/ensino_rede/relatorios/rel_discente_status_unidade.jsp");
		setOrdenacao("instituicao.sigla, campus.sigla, da.anoIngresso, da.periodoIngresso, da.pessoa.nome");
		return super.iniciarRelatorioDiscente(); 	
	}

	public String iniciarRelatorioContatoDiscente() throws Exception {
		super.limparDados();
		setFiltrarStatus(true);
		setFiltrarUnidade(true);
		setProjecao("da.pessoa.nome, da.pessoa.cpf_cnpj, instituicao.sigla as " +
				" da.dadosCurso.campus.instituicao.sigla, campus.sigla as da.dadosCurso.campus.sigla," +
				" da.pessoa.email, da.pessoa.telefone, da.pessoa.celular");
		setUrlDestino("/ensino_rede/relatorios/rel_contato_discente.jsp");
		setOrdenacao("instituicao.sigla, campus.sigla, da.pessoa.nome");
		return super.iniciarRelatorioDiscente(); 	
	}
	
	public String iniciarRelatorioDocentes() throws Exception {
		super.limparDados();
		setFiltrarUnidade(true);
		setProjecao("docente.pessoa.nome, docente.pessoa.cpf_cnpj, docente.pessoa.email, " +
				" tipo.descricao as docente.tipo.descricao, sit.descricao as docente.situacao.descricao, " +
				" inst.sigla as docente.dadosCurso.campus.instituicao.sigla, campus.sigla as docente.dadosCurso.campus.sigla");
		setUrlDestino("/ensino_rede/relatorios/rel_docente_unidade.jsp");
		setOrdenacao("inst.sigla, campus.sigla, docente.pessoa.nome");
		return super.iniciarRelatorioDocentes();
	}

	public String iniciarRelatorioTurmas() throws Exception {
		super.limparDados();
		setFiltrarUnidade(true);
		setFiltrarAnoPeriodo(true);
		setFiltrarDisciplina(true);
		setProjecao("comp.nome as tur.componente.nome, comp.codigo as tur.componente.codigo, " +
				"docente.pessoa.nome as tur.docenteTurma.docente.pessoa.nome, docente.pessoa.email as tur.docenteTurma.docente.pessoa.email," +
				"docente.pessoa.cpf_cnpj as tur.docenteTurma.docente.pessoa.cpf_cnpj, campus.sigla as tur.docenteTurma.docente.dadosCurso.campus.sigla," +
				"inst.sigla as tur.docenteTurma.docente.dadosCurso.campus.instituicao.sigla");
		setUrlDestino("/ensino_rede/relatorios/rel_turma.jsp");
		setOrdenacao("comp.nome, docente.pessoa.nome");
		return super.iniciarRelatorioTurmas();
	}

	public String iniciarRelatorioDesempenho() throws Exception {
		super.limparDados();
		setFiltrarDisciplina(true);
		setFiltrarTaxa(true);
		setFiltrarAnoPeriodo(true);
		setProjecao("sitMat.id as mat.situacao.id, comp.nome as mat.turma.componente.nome, mat.turma.componente.codigo as mat.turma.componente.codigo, campus.sigla as " +
				" mat.turma.dadosCurso.campus.sigla, inst.sigla as mat.turma.dadosCurso.campus.instituicao.sigla");
		setUrlDestino("/ensino_rede/relatorios/rel_disciplina.jsp");
		setOrdenacao("inst.sigla, campus.sigla, comp.nome");
		setValidacao("validate");
		return super.iniciarRelatorioMatricula();
	}

}