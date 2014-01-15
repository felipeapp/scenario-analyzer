<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<h2><b>Relatório de Alunos com trancamento em um determinado componente curricular</b></h2>
	
	<table>
		<tr>
			<th>Ano-Período:</th>
			<td style="font-weight: bold;">
				<h:outputText value="#{relatorioDiscente.ano}" />.<h:outputText value="#{relatorioDiscente.periodo}" />
			</td>
		</tr>
	</table>
	
	<hr/>
	<br/>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;" class="tabelaRelatorio" >
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	    <c:set var="componenteVar"/>
		<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha">
			<c:set var="componenteAtual" value="${linha.disciplina_codigo}"/>

			<c:if test="${componenteVar != componenteAtual}">
			    <c:set var="componenteVar" value="${componenteAtual}"/>
				<tr>
					<td colspan="5" style="background-color: #DEDFE3; font-weight: bold;">
						${linha.disciplina_codigo} - ${linha.disciplina_nome}
					</td>
				</tr>
				<tr style="font-weight: bold;">
					<td>Matrícula</td>
					<td>Nome</td>
					<td>Programa</td>
					<td>Nível</td>
					<td>Tipo</td>
				</tr>
			</c:if>

			<%-- Obs.: o Internet Explorer não aceita bordas num <tr>, só num <td> --%>
			<tr>
				<td style="border-top: solid #cccccc 1px;">
					${linha.matricula}
				</td>
				<td style="border-top: solid #cccccc 1px;">
					${linha.nome_aluno}
					<c:if test="${not empty linha.municipio_polo}"> - PÓLO ${linha.municipio_polo}</c:if>
				</td>
				<td style="border-top: solid #cccccc 1px;">
					${linha.nome_curso}
				</td>
				<td style="border-top: solid #cccccc 1px;">
					${linha.nivel}
				</td>
				<td style="border-top: solid #cccccc 1px;">
					${linha.tipo}
				</td>
			</tr>
		</c:forEach>
	</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>