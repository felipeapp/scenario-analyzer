<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.espaco td {height: 8px; }
	tr.departamento td {padding: 8px; }
	tr.justificativa td {border: 1px dashed #000;}
	tr.parametro td {border-bottom: 1px solid #555}
	tr.disciplina td {background-color: #DCDCDC; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.header td {background-color: #eee; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {border-bottom: 1px solid #000; border: 1px solid #000;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<tr>
			<td align="center">
				<b>Relatório Quantitativo de Trancamentos por Justificativa</b>
			</td>
		</tr>
	</table>
	<br />
	
	<table width="100%">
		<tr class="parametro">
		<td><b>Ano - Período:</b> 
		<h:outputText value="#{relatorioDiscente.ano}"/>.<h:outputText value="#{relatorioDiscente.periodo}"/>	
		</td>
	</table>
	<br />
    
    <c:set var="_disciplina" />
    <c:set var="totalDepartamento" value="0"/>
    
	<table width="100%" style="font-size: 11px;">
		<c:forEach items="#{relatorioDiscente.relatorioTrancamento}" var="lista" varStatus="indice">
		  <c:if test="${totalDepartamento != 0}">
			<tr class="header">
				  <td colspan="2" align="right">Total Geral do Departamento:</td>
				  <td align="right">${totalDepartamento}</td>
				  <c:set var="totalDepartamento" value="0"/>
			</tr>
		  </c:if>
			<tr class="departamento">
				<td colspan="3">&nbsp;</td>
			</tr>
			<tr class="disciplina">
				<td colspan="3"><b>${lista.key}</b></td>
			</tr>
			<tr class="disciplina">
				<td colspan="3">Componente Curricular</td>
			</tr>
			<tr class="disciplina">
				<td colspan="2">Justificativa</td>
				<td>Total</td>
			</tr>
		  <c:forEach items="#{lista.value}" var="linha" varStatus="indice">
			
			<c:set var="disciplinaAtual" value="${linha.nome}"/>
			  <c:if test="${_disciplina != disciplinaAtual}">
				<c:set var="_total" value="0" />
				
				<tr class="espaco">
					<td colspan="3"></td>
					<c:set var="total" value="0"/>
				</tr>			
				
				<tr class="header">
					<td colspan="3">${linha.codigoCurricular} - ${linha.nome}  T${linha.codigoTurma}</td>
				</tr>
	          <c:set var="_disciplina" value="${disciplinaAtual}"/>
			 </c:if>
			
			 
			<c:forEach items="#{linha.justificativas}" var="j">
			    <tr class="justificativa">
					<td colspan="2">${j.descricao}</td>
					<td align="right">${j.quantidade}</td>
					<c:set var="total" value="${total + j.quantidade}"/>
				</tr>
			</c:forEach>				  

  		    <c:set var="proximo" value="${lista.value[indice.index+1].nome}" />
			  <c:if test="${disciplinaAtual != proximo}">
				<tr class="componentes">
					<td colspan="2" align="right">Total:</td>
					<td align="right">${total}</td>
					<c:set var="totalDepartamento" value="${totalDepartamento + total}"/>
					<c:set var="total" value="0"/>
				</tr>
			  </c:if>
		  </c:forEach>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>