<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul style="list-style-image: none; list-style: none;">
    <li> Listas
    <ul>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.listagemTodosAlunos}"
				value="Lista de Todos os Alunos" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
	    <li>
	        <h:commandLink
				action="#{relatoriosTecnico.listagemAlunosCadastrados}"
				value="Lista de Alunos Cadastrados" onclick="setAba('relatorios')">
			</h:commandLink>
		</li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosIngressantes}"
				value="Lista de Alunos Ingressantes" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosConcluintes}"
				value="Lista de Alunos Concluintes" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosConcluidos}"
				value="Lista de Alunos que Concluíram o Programa" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAbandono}"
				value="Lista de Alunos que Abandonaram por Semestre" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioListaAlunosMatriculados}"
				value="Lista de Alunos Matriculados" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioListaTrancamentoAluno}"
				value="Lista de Trancamentos por Disciplina" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.gerarRelatorioListaAlunosComMovimentacaoCancelamento}"
				value="Lista de Alunos com Movimentação de Cancelamento" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
		<li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosCertificados}"
				value="Lista de Alunos que podem tirar Certificados" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosPorCidade}"
				value="Relatório de Alunos por Cidade" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioArtigo29}"
				value="Relatório do Artigo 29" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{buscaTurmaBean.popularBuscaTecnico}"
				value="Busca Geral de Turmas" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        
        <li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciar}" value="Consulta Geral de Discentes"/></li>
        
        <li><h:commandLink action="#{ relatorioOrientacoes.iniciar }" value="Relatório de Orientações" onclick="setAba('relatorios')"/></li>
    </ul>
    </li>
    <li> Quantitativos
   	<ul>
   		<li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioQuantitativoAlunosMatriculadosByAnoPeriodoFaixaEtaria}"
				value="Quantitativo de Alunos Matriculados Por Ano, Período, Faixa Etária e Habilitação" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
    	<li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAprovadosReprovadosDisciplina}"
				value="Quantitativo de Alunos Aprovados, Reprovados e Trancados por Disciplina" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.gerarRelatorioQuantitativoAlunosAnoIngresso}"
				value="Quantitativo de Alunos por Ano de Ingresso" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.gerarRelatorioListaTrancamentosSemestre}"
				value="Quantitativo de Matrículas de Alunos Trancadas por Semestre" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioAlunosAtivosMatriculadosOrientados}"
				value="Quantitativo de Alunos Ativos, Matriculados e Orientados" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
    	<li>
	        <h:commandLink
				action="#{relatoriosTecnico.iniciarRelatorioOrientadoresPorTurma}"
				value="Quantitativo de Orientador por Turma" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        <li>
	        <h:commandLink
				action="#{relatorioQuantAlunosCidadeBean.gerarRelatorio}"
				value="Quantitativo de Alunos por Município" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>        
   	</ul>
    </li>
    <li> Outros
    <ul>
    	<li>
	        <h:commandLink
				action="#{declaracaoTecnico.iniciar}"
				value="Declaração de turmas ministradas" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
    	<li>
	        <h:commandLink
				action="#{relatorioEntradaNotasMBean.iniciarTecnico}"
				value="Relatório de Entrada de Notas" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
    </ul>
    </li>
    </ul>
