<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #DEDFE3; border: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<f:view>

	<table width="100%" style="font-size: 11px;">
		<caption><td align="center"><h2>Relatório de Bancas por Orientador</h2></td></b>
	</table>
	<br />
	<br />
	<div id="parametrosRelatorio">
		<table >
			
			<tr>
				<th>Orientador:</th>
				<td> <h:outputText value="#{relatorioBancasOrientador.membroBanca.pessoa.nome}" /></td>
			</tr>
			<tr>
				<th>Data Inicial:</th>
				<td> <h:outputText value="#{relatorioBancasOrientador.dataInicio}" /></td>
			</tr>
			<c:if test="${not empty relatorioBancasOrientador.dataFim}">
				<tr>
					<th>Data Final:</th>
					<td> <h:outputText value="#{relatorioBancasOrientador.dataFim}" /></td>
				</tr>
			</c:if>
			
				 
		</table>
	</div>
	
	<br />
	
    <c:set var="_ano" />
    <c:set var="_total" value="0"/>
	<table cellspacing="1" width="100%" style="font-size: 11px;">
		<tr class="titulo">
			<td colspan="8"></td>
		</tr>
		<tr class="header">
			<td align="left">Título</td>
			<td align="left">Função</td>
			<td align="center">Data da Banca</td>
		</tr>

		<c:forEach items="#{relatorioBancasOrientador.lista}" var="linha"
			varStatus="indice">
			<tr class="componentes">
				<td align="left">${linha.titulo}</td>
				<td align="left">${linha.funcao}</td>
				<td align="center"><ufrn:format type="data" valor="${linha.data}"></ufrn:format> </td>
			</tr>
		</c:forEach>
	</table>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>