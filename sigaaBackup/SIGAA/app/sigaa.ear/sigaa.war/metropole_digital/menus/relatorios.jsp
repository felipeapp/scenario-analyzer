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
				action="#{relatoriosTecnico.iniciarRelatorioAbandono}"
				value="Lista de Alunos que Abandonaram por Semestre" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        
        <li>
	        <h:commandLink
				action="#{tutoriaIMD.selecionarTurma}"
				value="Lista de Discentes por Turma do IMD" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
        
    </ul>
    </li>
  </ul>
