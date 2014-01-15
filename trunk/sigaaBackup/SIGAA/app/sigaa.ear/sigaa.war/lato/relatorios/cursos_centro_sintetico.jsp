<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<h2> RELATÓRIO SINTÉTICO DE CURSOS POR CENTRO </h2>
<c:if test="${not empty relatoriosLato.anoInicial or not empty relatoriosLato.ano }">
<div id="parametrosRelatorio">
	<table >
		<tr>
			<th>
				<c:if test="${not empty relatoriosLato.anoInicial }">Ano Inicial:</c:if>
			</th>
			<td>
				${relatoriosLato.anoInicial != null ? relatoriosLato.anoInicial : null }
			</td>
		</tr>
		<tr>	
			<th>
				<c:if test="${not empty relatoriosLato.ano }">Ano Final:</c:if>
			</th>
			<td>
				${relatoriosLato.ano != null ? relatoriosLato.ano : null}
			</td>
		</tr>
		<tr>	
			<th>
				Situação do Curso:
			</th>
			<td>
				Submetido e Aceito
			</td>
		</tr>			
	</table>
</div>
<br />
</c:if>
<f:view>
<h:form>
<table class="tabelaRelatorioBorda" align="center">
		<thead>
			<tr>
				<th align="left">Centro</th> 	
				<th style="text-align: right;">Total de Cursos</th> 		
			</tr>
		</thead>
	<c:set var="totalCursos" value="0"/>
	<c:forEach items="#{relatoriosLato.relatorioCursoCentro}" var="linha" varStatus="indice">
		<tr class="ano">
		
			<td>
				<h:commandLink value="#{ linha.key.nome }" action="#{relatoriosLato.detalharCentroCursoSintetico }">
					<f:param name="id" value="#{ linha.key.id }" />
				</h:commandLink>
			</td>
			<td style="text-align: right;"> ${ linha.value.numeroCursos }</td>
		</tr>
		<c:set var="totalCursos" value="${ totalCursos + linha.value.numeroCursos }"/>
	</c:forEach>
		<tr class="total">
			<td align="right"><b>Total Geral de Cursos:</b></td>
			<td style="text-align: right;"> <b>${ totalCursos }</b></td>
		</tr>
	<tbody>
</table>
</h:form>
</f:view>
<br />

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>