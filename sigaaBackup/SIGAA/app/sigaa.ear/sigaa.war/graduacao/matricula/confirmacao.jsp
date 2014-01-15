<%@ include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@ include file="/graduacao/matricula/cabecalho_matricula.jsp"%>
	
	<h:form>
		<c:if test="${matriculaGraduacao.matriculaFerias}">
		<div class="descricaoOperacao">
			<h4> Aten��o! </h4>
			<p>
			 	O aluno poder� acompanhar o resultado da matr�cula na turma de f�rias atrav�s de seu <b>Plano de Matr�culas</b>, dispon�vel no
				Portal Discente (op��o Ensino > Matr�cula Online > Meu Plano de Matr�culas).
			</p>
		</div>
		</c:if>	
	
		<c:if test="${not matriculaGraduacao.matriculaFerias}">
		<table  class="subFormulario" align="center">
			<tr>
			<td width="8%" valign="middle" align="center">
				<html:img page="/img/warning.gif"/>
			</td>
			<td valign="middle" style="text-align: justify">
				Por favor imprima o comprovante clicando no �cone ao lado
				ou anote o n�mero da sua solicita��o para maior seguran�a dessa opera��o.
			</td>
			<td>
			<table>
				<tr>
					<td align="center">
						<h:commandLink title="Imprimir Comprovante"  target="_blank" action="#{matriculaGraduacao.exibirAtestadoMatricula}" id="printComprovante" >
				 			<h:graphicImage url="/img/printer_ok.png" />
				 		</h:commandLink>
				 	</td>
				 </tr>
				 <tr>
				 	<td style="font-size: medium;">
				 		<h:commandLink title="Imprimir Comprovante"  target="_blank" value="COMPROVANTE" action="#{matriculaGraduacao.exibirAtestadoMatricula}"  id="imprimirComprovante"/>
				 	</td>
				 </tr>
			</table>
			</td>
			</tr>
		</table>
		</c:if>
			
	<br>
	<table class="visualizacao" style="width: 100%">
	<tr>
		<th width="25%"> Discente: </th>
		<td> ${matriculaGraduacao.discente.matriculaNome} </td>
	</tr>
	<c:if test="${matriculaGraduacao.discente.graduacao}">
	<tr>
		<th> Matriz Curricular: </th>
		<td> ${matriculaGraduacao.discente.matrizCurricular.descricao} </td>
	</tr>
	</c:if>
	<tr>
		<th> Curr�culo: </th>
		<td> ${matriculaGraduacao.discente.curriculo.codigo} </td>
	</tr>
	<c:if test="${matriculaGraduacao.solicitacaoMatricula}">
	<tr>
		<th> N�mero Solicita��o: </th>
		<td> ${matriculaGraduacao.numeroSolicitacao} </td>
	</tr>
	</c:if>
	</table>
	
	<c:if test="${not empty matriculaGraduacao.turmas}">
	<br>
	<table class="listagem" style="width: 100%">
		<caption>Turmas</caption>
		<thead>
			<tr>
			<td>Componente Curricular</td>
			<td width="2%">Turma</td>
			<td width="10%">Local</td>
			</tr>
		</thead>
		<tbody>
			<c:forEach items="#{matriculaGraduacao.turmas}" var="turma" varStatus="status">
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td>${turma.disciplina.descricao}</td>
				<td width="12%">Turma ${turma.codigo}</td>
				<td width="12%">${turma.local}</td>
			</tr>
			<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}" style="font-size: xx-small">
				<td colspan="3">
					Docente(s): ${turma.docentesNomes }
				</td>
			</tr>
			</c:forEach>
		</tbody>
	</table>
	</c:if>
	<c:if test="${not matriculaGraduacao.solicitacaoMatricula}">
	<br><br>
	<center>
	<h:commandButton value="Buscar Outro Discente" action="#{matriculaGraduacao.iniciar}" id="buscarOutroDiscentes"/>
	<h:commandButton value="Voltar ao Menu Principal" action="#{matriculaGraduacao.cancelarMatricula}" id="voltarAoMenuPrincipal"/>
	</center>
	</c:if>
	</h:form>
</f:view>

<%@ include file="/WEB-INF/jsp/include/rodape.jsp"%>