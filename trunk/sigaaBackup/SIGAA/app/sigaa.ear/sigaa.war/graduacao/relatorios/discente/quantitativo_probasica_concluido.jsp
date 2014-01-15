<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
	<hr>
	<table width="100%">
		<caption><b>Quantitativos de Alunos de Probásica concluídos</b></caption>
			<tr>
				<td></td>
			</tr>
	</table>
	<hr>
	<table cellspacing="1" width="100%" style="font-size: 10px;">
		<caption><b>Total de Registros: <h:outputText value="#{relatorioDiscente.numeroRegistosEncontrados}"/></b></caption>
	<c:set var="curso"/>
    <c:set var="municipio"/>
    <c:set var="totalHomens " value="0"/>
    <c:set var="totalHomensGrupo" value="0"/>
	<c:forEach items="${relatorioDiscente.listaDiscente}" var="linha" varStatus="row">
			<c:set var="cursoAtual" value="${linha.curso_nome}"/>
			<c:set var="municipioAtual" value="${linha.id_municipio}"/>
		<c:if test="${ (curso != cursoAtual) && row.index!=0}">
		    <tr>
    			<td colspan=3><hr></td>
    		</tr>
    		<tr>
    			<td align="right"><b>Total:</b></td>
    			<td><b>${totalHomensGrupo}</b></td>
    		</tr>
		</c:if>
		<c:if test="${curso != cursoAtual }">
			<c:set var="totalHomensGrupo" value="0"/>
			<c:set var="curso" value="${cursoAtual}"/>
			<c:set var="municipio" value="${municipioAtual}"/>
			<tr>
				<td colspan="3">
					<br>
					<b>${linha.centro}	-  ${linha.curso_nome} </b><br>
					<hr>
				</td>
			</tr>
			<tr>
				<td><b>Cidade</b></td>
				<td><b>Quantidade</b></td>
			</tr>
		</c:if>
		 <c:set var="totalHomensGrupo" value="${totalHomensGrupo + linha.qtd_discente}"/>
		 <c:set var="totalHomens" value="${totalHomens + linha.qtd_discente}"/>
		<tr>
			<td>
				${linha.municipio_nome}
			</td>
			<td>
				${linha.qtd_discente}
			</td>
		</tr>
	</c:forEach>
		 <tr>
    			<td colspan=3><hr></td>
    		</tr>
    		<tr>
    			<td align="right"><b>Total:</b></td>
    			<td><b>${totalHomensGrupo}</b></td>
    		</tr>
	</table>
	<table width="100%">
		<tr>
			<td colspan="2"> <hr></td>
		</tr>
		<tr align="center">
			<td><b>Total de Concluídos: ${totalHomens}</b></td>
		</tr>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
