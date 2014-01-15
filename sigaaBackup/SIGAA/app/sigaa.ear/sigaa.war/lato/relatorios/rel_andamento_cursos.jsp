<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<style>
	tr.curso td {border-bottom: 1px solid #555;  }
	tr.titulo td {border-bottom: 2.5px solid #555}
	tr.header td {padding: 0px ; background-color: #EFEFEF; border-bottom: 1px solid #555; font-weight: bold; border: 1px solid #000;}
	tr.componentes td {padding: 5px 2px 2px; border-bottom: 1px solid #000; border: 1px solid #c0c0c0; }
	tr.total td {padding: 5px 2px 2px; border: 1px solid #000; font-weight: bold;}
	tr.espaco td {padding: 12px;}
</style>
<h2> RELATÓRIO DE ANDAMENTO DOS CURSOS DE LATO SENSU </h2>

<f:view>
<div id="parametrosRelatorio">
	<table>
	<tr>
		  <td style="font-weight: bold;"> Ano Inicial: </td>
		  <td><h:outputText  value="#{relatoriosLato.anoInicial}" /></td>
		</tr>
		<tr>
		  <td style="font-weight: bold;"> Ano Final: </td>
		  <td><h:outputText  value="#{relatoriosLato.ano}" /></td>
		</tr>
	</table>
</div>
<br/>
<table width="100%" style="font-size: 10px;" >
	
	
		<tr class="header">
			<td style="text-align: left;">Curso</td>
			<td style="text-align: center;">Data Início</td>
			<td style="text-align: center;">Data Fim</td>
			<td style="text-align: right;">Andamento</td> 	
		</tr>

	<c:forEach items="#{relatoriosLato.cursosLato}" var="linha" varStatus="loop">
		
		<tr  class="componentes">

			<td style="text-align: left;"> ${ linha.nome } </td>
			<td style="text-align: center;"> <ufrn:format type="data" valor="${ linha.dataInicio }"/> </td>
			<td style="text-align: center;"> <ufrn:format type="data" valor="${ linha.dataFim }"/> </td>
			<td style="text-align: right;"> <ufrn:format type="valorint" valor="${ linha.andamento }"/>% </td>
		</tr>

	</c:forEach>
	
</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>