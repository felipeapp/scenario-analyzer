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
				action="#{relatoriosTecnico.gerarRelatorioListaAlunosComMovimentacaoCancelamento}"
				value="Lista de Alunos com Movimentação de Cancelamento" onclick="setAba('relatorios')">
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
				action="#{buscaTurmaBean.popularBuscaTecnico}"
				value="Busca Geral de Disciplinas Ofertadas" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        
        <li> <h:commandLink action="#{ buscaAvancadaDiscenteMBean.iniciar}" value="Consulta Geral de Alunos"/></li>
        
    </ul>
    </li>
  </ul>
