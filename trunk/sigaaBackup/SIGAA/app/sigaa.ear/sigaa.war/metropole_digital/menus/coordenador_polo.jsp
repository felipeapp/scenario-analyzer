<%@ taglib uri="/tags/ufrn" prefix="ufrn" %>

<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul style="list-style-image: none; list-style: none;">
    <li> Listas
    <ul>

        <li>
	        <h:commandLink
				action="#{relatoriosCoordenadorPoloIMD.gerarListagemTodosAlunos}"
				value="Lista de Todos os Alunos" onclick="setAba('relatorios')">
			</h:commandLink>
        </li>
	    <li>
	        <h:commandLink
				action="#{relatoriosCoordenadorPoloIMD.gerarListagemAlunosCadastrados}"
				value="Lista de Alunos Cadastrados" onclick="setAba('relatorios')">
			</h:commandLink>
		</li>

        <li>
	        <h:commandLink
				action="#{relatoriosCoordenadorPoloIMD.selecionarTurma}"
				value="Lista de Discentes por Turma do IMD" onclick="setAba('coordenador_polo')">
			</h:commandLink>
        </li>
        
    </ul>
    </li>
  </ul>