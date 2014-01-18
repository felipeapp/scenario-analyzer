<%@ taglib uri="/tags/ufrn" prefix="ufrn"  %>
<%@page import="br.ufrn.arq.seguranca.SigaaPapeis"%>
    <ul>

		<li>Aproveitamento de Disciplina
		<ul>
			<li><h:commandLink
				action="#{aproveitamento.iniciarAproveitamento}" id="aproveitarDisciplinaTecnico"
				value="Aproveitar Disciplina" onclick="setAba('aluno')"/></li>
			<li><h:commandLink
				action="#{aproveitamento.iniciarCancelamento}" id="cancelarAproveitamentoDisciplinaTecnico"
				value="Cancelar Aproveitamento" onclick="setAba('aluno')"/></li>
		</ul>
		</li>
		<li> Transferência de Aluno entre Turmas
			<ul>
				<li> <h:commandLink id="transferenciaTurmaIMD" action="#{transferenciaTurmaIMD.iniciarTransfTurmaEntrada }" value="Transferir Alunos de Turmas do IMD" onclick="setAba('aluno')"/> </li>
			</ul>
		</li>
		<li> Carteira de Estudante
			<ul>
				<li><a href="${ ctx }/arquivoSttu?nivel=T&unidade=${usuario.unidade.id}" id="listaAlunosSTTU">Lista de alunos para STTU</a></li>
				<li><a href="${ ctx }/arquivoSttu?nivel=T&log=true&unidade=${usuario.unidade.id}" id="listaAlunosProblema">Lista de alunos com problema</a></li>
			</ul>
		</li>
    </ul>
