<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<label style="font-weight:bold;">Curso:</label>
	<h:outputText value="#{relatorioDiscente.curso.nomeCursoStricto}" />
		
	<table class="listagem" width="100%">
		<caption>Lista de Alunos e Seus Respectivos Orientadores</caption>
		<thead>
			<tr style="border: 1px solid;"> 
				<td style="text-align: right;">Matrícula</td>
				<td>Nome</td>
				<td>Orientador</td>
			</tr>		
		</thead>
		<tbody>
			<c:set var="ano" value=""/>
			<c:forEach var="item" varStatus="status" items="#{relatorioDiscente.lista}">
			<c:if test="${ano != item.anoingresso}">
				<tr style="background-color:#C8D5EC;font-weight:bold;border: 1px solid;">
					<td align="left" colspan="3"><h:outputText value="#{item.anoingresso}"/> </td>
				</tr>
			</c:if>
			<c:set var="ano" value="${item.anoingresso}"/>
			<c:if test="${ano == item.anoingresso}">			
				<tr style="border: 1px solid;">
					<td align="right"><h:outputText value="#{item.matricula }"/> </td>
					<td><h:outputText value="#{item.aluno }"/></td>
					<td><h:outputText value="#{item.orientador }"/></td>
				</tr>
			</c:if>	
			</c:forEach>
		</tbody>
		
	</table>
</f:view>


<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>