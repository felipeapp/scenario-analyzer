<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<%@page import="br.ufrn.sigaa.projetos.dominio.TipoSituacaoProjeto"%>

<f:view>

<h2 class="tituloPagina"> Lista de Projetos por Colocação</h2>

<strong>Projetos do Edital:</strong> <h:outputText value="#{ classificarProjeto.edital.descricaoCompleta }"/><br/>
<br/>

	<table class="tabelaRelatorio" width="100%">
			<caption class="listagem">Classificação dos Projetos de Ensino</caption>

	      <thead>
	      	<tr>
	        	<th>Class.</th>
	        	<th>Projeto de Ensino</th>
	        	<th>Centro</th>
	        	<th>MF</th>
	        	<th>Situação</th>
	        </tr>
	 	</thead>
	 	<tbody>
	 	
			<c:set var="NAO_RECOMENDADO" value="<%= String.valueOf(TipoSituacaoProjeto.MON_NAO_RECOMENDADO) %>" scope="application"/>
			
			<c:forEach items="${classificarProjeto.projetos}" var="projeto" varStatus="status">			
		      	<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }"
		      		  ${ projeto.situacaoProjeto.id == NAO_RECOMENDADO ? "style='color:red'" : "" }>		      
		      		  
				    <td> ${ status.count }º </td>
					<td> ${ projeto.titulo } </td>
					<td> ${ projeto.unidade.sigla } </td>
					<td> <fmt:formatNumber  pattern="#0.00" value="${ projeto.mediaFinal }" />	</td>
					<td> ${projeto.situacaoProjeto.descricao} </td>
				</tr>
			</c:forEach>
		</tbody>
	</table>

	<div class="infoAltRem">[ MF = Média de Final ]</div>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>