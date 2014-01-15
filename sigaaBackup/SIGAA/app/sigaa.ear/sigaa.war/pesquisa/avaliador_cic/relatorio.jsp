<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {padding: 15px 0 0; border-bottom: 1px solid #555; font-weight: bold; font-size: 20px; text-align: center; }
	tr.header td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee;}
	tr.foot td {padding: 3px ; border-bottom: 1px solid #555; background-color: #eee; font-weight: bold; font-size: 13px; }
	tr.discente td {border-bottom: 1px solid #888; font-weight: bold; padding-top: 7px;}
	tr.componentes td {padding: 4px 2px 2px ; border-bottom: 1px dashed #888;}
	tr.componentes td.assinatura { padding:2px; border-bottom: 1px solid #888;  width: 40%;}
</style>
<f:view>
	<h:outputText value="#{ avaliadorCIC.create }" />
	
	<h2>Relatório de Avaliadores de Resumo do CIC</h2>
	<div id="parametrosRelatorio">
		<table >
			<tr>
				<th>Congresso:</th>
				<td>${ avaliadorCIC.obj.congresso.descricao }</td>
			</tr>
			<c:if test="${avaliadorCIC.filtroArea}">
				<tr>
					<th>Área de Conhecimento:</th>
					<td>${ avaliadorCIC.obj.area.nome }</td>
				</tr>
			</c:if>
			<c:if test="${avaliadorCIC.filtroTipo}">
				<tr>
					<th>Avaliador de Resumo:</th>
					<td>${avaliadorCIC.obj.avaliadorResumo?'Sim':'Não'}</td>
				</tr>
				<tr>
					<th>Avaliador de Apresentação:</th>
					<td>${avaliadorCIC.obj.avaliadorApresentacao?'Sim':'Não'}</td>
				</tr>
			</c:if>
		</table>
	</div>
	
	<table cellspacing="1" width="100%" style="font-size: 10px;">
	<caption><b>Total de Registros: <h:outputText value="#{fn:length(avaliadorCIC.resultadosBusca)}"/></b></caption>
	<thead>
		<tr>
			<th style="text-align: left;">Docente</th>
			<th>Avaliador de Resumo</th>
			<th>Avaliador de Apresentação</th>
		</tr>
	</thead>
	<c:set var="areaLoop" />
		<c:forEach var="avaliador" items="#{ avaliadorCIC.resultadosBusca }" varStatus="status">
			<c:set var="areaAtual" value="${avaliador.area.nome}"/>
			<c:if test="${areaAtual != areaLoop}">
				<tr class="curso">
					<td colspan="3"> ${ avaliador.area.nome } </td>
				</tr>
				<c:set var="areaLoop" value="${areaAtual}"/>
			</c:if>
			
			<tr class="componentes">
				<td>${avaliador.docente.pessoa.nome}</td>
				<td style="text-align: center;"> ${ avaliador.avaliadorResumo?'Sim':'Não' } </td>
				<td style="text-align: center;"> ${ avaliador.avaliadorApresentacao?'Sim':'Não' } </td>
			</tr>
		</c:forEach>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
