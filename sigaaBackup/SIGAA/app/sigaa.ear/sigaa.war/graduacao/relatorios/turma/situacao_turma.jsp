<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 20px 0 0; border-bottom: 1px solid #555; }
	tr.header td {padding: 0px ; border-bottom: #555; background-color: #eee; font-weight: bold;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px dashed #888;}
</style>

<f:view>
	<h2><b>RELATÓRIO SINTÉTICO DE TURMAS POR SITUAÇÃO</b></h2>
	<table width="100%">
			<tr class="curso">
				<td>Ano-Período:
				<b><h:outputText value="#{relatorioTurma.ano}"/>.<h:outputText value="#{relatorioTurma.periodo}"/></b></td>
			</tr>
	</table>

	<br />
	<c:set var="_graduacao" value="G"/>
	<c:set var="_lato" value="L"/>
	<c:set var="_stricto" value="S"/>
	<c:set var="_tecnico" value="T"/>
	<c:set var="total" value="0"/>

	<c:set var="_nivel" />
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	  <c:forEach items="#{relatorioTurma.listaTurma}" var="turma" varStatus="indice">
			<c:set var="nivelAtual" value="${turma.nivel}"/>

			  <c:if test="${_nivel != nivelAtual}">
				<c:if test="${not empty _nivel}">
					<tr>
						<td></td>
					</tr>
				</c:if>
				
				<c:set var="total" value="0"/>
				
			  	<tr class="curso">
					<c:if test="${ turma.nivel == _graduacao }">
						<td align="left" colspan="2"><b>GRADUAÇÃO</b></td>
					</c:if>
					<c:if test="${ turma.nivel == _lato }">
						<td align="left" colspan="2"><b>LATO SENSU</b></td>
					</c:if>
					<c:if test="${ turma.nivel == _stricto }">
						<td align="left" colspan="2"><b>STRICTO SENSU</b></td>
					</c:if>
					<c:if test="${ turma.nivel == _tecnico }">
						<td align="left" colspan="2"><b>TÉCNICO</b></td>
					</c:if>
				</tr>
				
				<tr class="header">
					<td align="left" colspan="2"><b>Situação</b></td>
				</tr>
					<c:set var="_nivel" value="${nivelAtual}"/>
			  </c:if>
			  
				<tr class="componentes">
					<td align="left">${ turma.descricao}</td>
					<td align="right">${ turma.count}</td>
					<c:set var="total" value="${total + turma.count}"/>
				</tr>
			
			  <c:set var="proximo" value="${relatorioTurma.listaTurma[indice.index+1].nivel}" />
			  
			  <c:if test="${nivelAtual != proximo}">
				<tr class="componentes">
					<td><b>TOTAL</b></td>
					<td align="right"><b>${total}</b></td>
				</tr>
			  </c:if>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>