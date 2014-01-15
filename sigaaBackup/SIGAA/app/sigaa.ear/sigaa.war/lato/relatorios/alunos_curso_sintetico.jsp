<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #DEDFE3;}
</style>

<h2> RELATÓRIO SINTÉTICO DE ALUNOS POR CURSO </h2>
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
		<tr>	
			<th>
				Status Discente:
			</th>
			<td>
				Todos
			</td>
		</tr>				
	</table>
</div>
<br />
</c:if>

<f:view>
<h:form>
<table class="tabelaRelatorioBorda" width="100%" style="font-size: 10px;">
		<thead>
			<tr>
				<th align="left">Curso</th> 	
				<th style="text-align: right;">Total de Alunos</th> 		
			</tr>
		</thead>
		<c:set var="totalAlunos" value="0"/>
		<c:set var="centroAtual_" value=""/>
		<c:forEach items="#{relatoriosLato.relatorioCurso}" var="linha" varStatus="indice">
		
		<c:if test="${centroAtual_ ne linha.value.curso.unidade.gestora.nome}">
		<c:set var="centroAtual_" value="${linha.value.curso.unidade.gestora.nome }"/>
		<tr>
			<td colspan="2"><b>${linha.value.curso.unidade.gestora.nome}</b></td>
		</tr>
		</c:if>
		
		<tr class="ano">
			<td> &nbsp;&nbsp;
				<h:commandLink value="#{ linha.key.descricao }" action="#{relatoriosLato.detalharCursoSintetico }">
					<f:param name="id" value="#{ linha.key.id }" />
				</h:commandLink>
			</td>
			<td style="text-align: right;"> ${ linha.value.numeroAlunos }  </td>
		</tr>
		<c:set var="totalAlunos" value="${ totalAlunos + linha.value.numeroAlunos }"/>
		</c:forEach>
		<tr class="total">
			<td align="right"><b> Total Geral de Alunos: </b></td>
			<td style="text-align: right;"> <b>${ totalAlunos }</b></td>
		</tr>
	<tbody>
</table>
</h:form>
</f:view>
<br />

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>