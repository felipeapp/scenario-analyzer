<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Relatório Quantitativo de Alunos Matriculados</b></caption>
			<tr>
				<th>Ano-Semestre Matrícula:</th>
				<td ><b><h:outputText value="#{relatoriosTecnico.ano}"/>-<h:outputText
					value="#{relatoriosTecnico.periodo}"/></b>
				</td>
			</tr>
			<tr>
				<th>Unidade:</th>
				<td><b>${usuario.unidade.nome}</b></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		
	<c:set var="curso_"/>
    <c:set var="total" value="0"/>
    <c:set var="totalGrupo" value="0"/>

	<c:forEach items="${relatoriosTecnico.lista}" var="linha" varStatus="row">
			<c:set var="cursoAtual" value="${linha.id_curso}"/>
			

		<c:if test="${curso_ != cursoAtual}">
			<c:if test="${not empty curso}">
				<tr>
	    			<td colspan=3><hr></td>
	    		</tr>
	    		<tr>
	    			<td align="right"><b>Total:</b></td>
	    			<td><b>${totalGrupo}</b></td>
	    		</tr>
			</c:if>
			<c:set var="totalGrupo" value="0"/>
			<c:set var="curso_" value="${cursoAtual}"/>
			

			<tr>
				<td colspan="3">
					<br>
					<b>${linha.nome} </b><br>
					<hr>
				</td>
			</tr>
			
				<tr>
					<td><b>Especialização</b></td>
					<td><b>Quantidade</b></td>
				</tr>
			
		</c:if>
		 <c:set var="totalGrupo" value="${totalGrupo + linha.qtd_matriculados}"/>
		 <c:set var="total" value="${total + linha.qtd_matriculados}"/>
		
			<tr>
				<td>
					${empty linha.descricao? "<i>Curso sem especialização</i>": linha.descricao }					
				</td>
				<td>${linha.qtd_matriculados}</td>
			</tr>
		
	</c:forEach>
		<tr>
   			<td colspan=3><hr></td>
   		</tr>
   		<tr>
   			<td align="right"><b>Total:</b></td>
   			<td><b>${totalGrupo}</b></td>
   		</tr>
   		<tr>
   			<td colspan="2"><br><hr></td>
   		</tr>
   		<tr>
   			<td colspan="2" align="center"><b>Total de Alunos: ${total}</b></td>
   		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
