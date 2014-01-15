<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>


<%@page import="java.util.Collection"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.ArrayList"%><style>
	table tr td.espaco{
		height: 20px;
	}

	table tr td.espaco_docentes{
		height: 8px;
	}

	#relatorio table tr.centro td{
		font-weight: bold;
		padding: 3px 8px;
		border: 1px solid #444;
		border-width: 1px 0px;
		font-size: 1.2em;
		background: #EEE;
	}
	table tr.docente td{
		padding: 2px 0 2px 15px;
		border-bottom: 1px dashed #444;
		text-align: center;
	}
	
	table tr.total td{
		padding: 3px 0px;
		border-top: 1px solid #222;
		font-weight: bold;
		text-align: center;
	}

</style>

<h2> Relatório de Acompanhamento de Distribuição de Cotas de Bolsas </h2>

<div id="parametrosRelatorio">
	<table>
		<tr>
			<th>Cota:</th>
			<td>${cota}</td>
		</tr>
	</table>
</div>

<br/><br/>

<table width="100%">
	<% String centro = ""; %>
			
	<thead>
		<tr>
			<th rowspan="2"> Docente</th>
			<th rowspan="2"> Solic. </th>
			<th colspan="${tamanho}" style="border-left: 1px dashed"> Distribuição </th>
			<th colspan="${tamanho}" style="border-left: 1px dashed"> Indicações </th>
		</tr>
		<tr>
			<c:forEach var="tipo" items="${tiposBolsa}" varStatus="status">
				<th style="padding: 2px 4px; ${ status.index == 0 ? 'border-left: 1px dashed' : '' }"> ${ tipo }</th>
			</c:forEach>
			<c:forEach var="tipo" items="${tiposBolsa}" varStatus="status">
				<th style="padding: 2px 4px; ${ status.index == 0 ? 'border-left: 1px dashed' : '' }"> ${ tipo }</th>
			</c:forEach>
		</tr>
	</thead>

	<tbody>

<%
	int tam = (Integer) request.getAttribute("tamanho");
	long totaisCentro[] = new long[2+2*tam];
	long totais[] = new long[2+2*tam];
	
	Collection rel = (Collection) request.getAttribute("relatorio");
	int status = 0;
	for(Object o: rel){
		HashMap linha = (HashMap) o;
%>
		<%-- Centro --%>
		<% if(!centro.equals((String)linha.get("centro"))){ %>

			<% if(totaisCentro[0] != 0){ %>
				<tr class="total">
					<td style="text-align: center"> <%= totaisCentro[0] %> docente(s) </td>
					<% for(int i = 1; i < totaisCentro.length; i++){ %>
						<td> <%= totaisCentro[i] %></td>
					<%} %>
				</tr>
				<%
					for(int i = 0; i < totaisCentro.length; i++)
						totaisCentro[i] = 0;
				
				} %>

			<% if(status != 0){ %>
				<tr><td class="espaco"></td></tr>
			<%} %>

			<% centro = (String) linha.get("centro"); %>
			<tr class="centro">
				<td colspan="${tamanho != 0 ? 2+2*tamanho : 4}"> <%= centro %> </td>
			</tr>
		<%} %>

		<%-- Docente --%>

		<tr class="docente">
			<td style="text-align: left"> <%= linha.get("docente") %> </td>
			<td> <%= linha.get("solicitacoes") %></td>
			<% 
			if(tam != 0) {
				for(int i = 0; i < tam; i++){ %>
					<td> <%= linha.get("cota"+i) != null ? linha.get("cota"+i) : 0 %> </td>
				<%} %>
			 <% for(int i = 0; i < tam; i++){ %>
				<td> <%= linha.get("indicacao"+i) %> </td>
			<%} }
			else {%>
				<td> 0 </td>
				<td> 0 </td>
			<%} %>
		</tr>

	<%
		totaisCentro[0]++;
		totaisCentro[1] += (Long) linha.get("solicitacoes");
		for(int i = 0; i < tam; i++)
			totaisCentro[i+2] += linha.get("cota"+i) != null ? (Long) linha.get("cota"+i) : 0;
		for(int i = 0; i < tam; i++)
			totaisCentro[i+2+tam] += (Long) linha.get("indicacao"+i);
		
		totais[0]++;
		totais[1] += (Long) linha.get("solicitacoes");
		for(int i = 0; i < tam; i++)
			totais[i+2] += linha.get("cota"+i) != null ? (Long) linha.get("cota"+i) : 0;
		for(int i = 0; i < tam; i++)
			totais[i+2+tam] += (Long) linha.get("indicacao"+i);
		
		status++;
	} 
	%>

	<tr class="total">
		<td style="text-align: center"> <%= totaisCentro[0] %> docente(s) </td>
		<% for(int i = 1; i < totaisCentro.length; i++){ %>
			<td> <%= totaisCentro[i] %></td>
		<%} %>
	</tr>

	<tbody>
</table>

<style>
	h4 {
		text-align: center;
		font-variant: small-caps;
		font-size: 1.2em;
		background: #DDD;
		width: 60%;
		margin: 18px auto 1px;
	}

	table#resumo {
		border: 1px solid #555;
		border-width: 1px 0 1px;
		width: 60%;
		margin: 0 auto;
	}

	table#resumo td {
		border-bottom: 1px dashed #999;
		padding: 3px;
		font-size: 1.1em;
		font-variant: small-caps;
	}


	table#resumo td.valor{
		text-align: right;
		font-weight: bold;
	}

	table#resumo tr.grupo td{
		border-bottom-style: solid;
	}

	table#resumo td.subGrupo{
		padding-left: 2em;
	}
</style>

<%
ArrayList tBolsas = (ArrayList) request.getAttribute("tiposBolsa");
%>

<h4> Resumo </h4>
<table id="resumo">
	<tr class="grupo">
		<td> Docentes </td>
		<td class="valor"> <%= totais[0] %></td>
	<tr>
	<tr class="grupo">
		<td> Solicitações </td>
		<td class="valor"> <%= totais[1] %> </td>
	</tr>
	<tr class="grupo">
		<td> Cotas </td>
		<td class="valor"> 
			<%
			long soma = 0;
			for(int i = 0; i < tam; i++)
				soma += totais[i+2];
			out.print(soma);
			%> 
		</td>
	</tr>
	<% for(int i = 0; i < tam; i++){	%>
	<tr>
		<td class="subGrupo"> <%= tBolsas.get(i) %> </td>
		<td class="valor"> <%= totais[i+2] %> </td>
	</tr>
	<%} %>
	<tr class="grupo">
		<td> Indicações </td>
		<td class="valor"> 
			<%
			soma = 0;
			for(int i = 0; i < tam; i++)
				soma += totais[i+tam+2];
			out.print(soma);
			%> 
		</td>
	</tr>
	<% for(int i = 0; i < tam; i++){	%>
	<tr>
		<td class="subGrupo"> <%= tBolsas.get(i) %> </td>
		<td class="valor"> <%= totais[i+tam+2] %> </td>
	</tr>
	<%} %>
	
</table>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>