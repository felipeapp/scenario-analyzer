<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages/>

	<h:outputText value="#{consultarMonitor.create}"/>

		<br/>
		<br/>
 
	<center>
	
		<h3>MINISTÉRIO DA EDUCAÇÃO DE DO DESPORTO<br/>
		UNIVERSIDADE FEDERAL DO RIO GRANDE DO NORTE<br/>
		PRÓ-REITORIA DE GRADUAÇÃO</h3>
		
		<br/>
		<br/>
		
			<h3>CERTIFICADO</h3><br/>
			
			<font color="red"><h3>[MODELO SENDO DEFINIDO PELA PROGRAD.....]</h3><br/></font>
		
		CERTIFICAMOS, nos termos do artigo 34 da Resolução nº 013/2006 - CONSEPE, de 14 de março de 2006, que<br/>
		<b><c:out value="${ consultarMonitor.obj.discente.nome }"/></b> exerceu as funções de monitor(a) no Projeto de Ensino:
		"<b><c:out value="${ consultarMonitor.obj.projetoEnsino.titulo }" />"</b>
		Coordenado pelo(a) Prof.(a) 
		
		<c:forEach var="orientacao" items="${ consultarMonitor.obj.orientacoes }">
		   <c:if test="${(orientacao.equipeDocente.coordenador)}">
		   
				<b><c:out value="${ orientacao.equipeDocente.servidor.pessoa.nome }" /></b> <br/>
				do "<b><c:out value="${ consultarMonitor.obj.projetoEnsino.unidade.nome }" /></b>", no período de 
				<i><fmt:formatDate pattern="dd/MM/yyyy" value="${ orientacao.dataInicio }"/></i> à <i><fmt:formatDate pattern="dd/MM/yyyy" value="${ orientacao.dataFim }"/></i>. 	
				
		   </c:if>
		</c:forEach>
		
		<br/>
		<br/>
		<br/>
				
		${ configSistema['cidadeInstituicao'] }, 	<span class="data-atual">
					<fmt:formatDate value="${dataAtual}" pattern="dd ' de ' MMMM ' de ' yyyy"></fmt:formatDate>
				</span><br/>

	<br/>
	<br/>
	<br/>
	
	
	<table width="100%">
		<tr><td align="center">_________________________________</td> <td width="10%"></td> <td align="center">_________________________________</td>
		<tr><td align="center">Coordenador(a) do Programa de Monitoria</td><td width="20%"></td><td align="center">Pró-Reitor(a) de Graduação</td>
	</table>
	
			
	
	<br/>
	<br/>
	<br/>
	<hr/>			
	
		<input type="button" value="Voltar" onclick="javascript:history.go(-1)"/>		
		<input type="button" value="Imprimir" onclick="javascript:window.print()"/>		
	
	</center>	 
	

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>