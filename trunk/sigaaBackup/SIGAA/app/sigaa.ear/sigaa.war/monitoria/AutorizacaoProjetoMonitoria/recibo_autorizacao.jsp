<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>



<f:view>
	<h:messages/>
	<h2>RECIBO DE AUTORIZAÇÃO</h2>

	<h:outputText value="#{autorizacaoProjetoMonitoria.create}"/>

	
<h:form>
	<table class="tabelaRelatorio" width="100%">

		<caption class="listagem"> Recibo de Autorização do Projeto de Ensino </caption>

<tbody>


	<tr>
		<td>
		<b> Número Recibo:</b>
		<h:outputText value="#{autorizacaoProjetoMonitoria.obj.id}"/> 
		<br/> 
		</td>
	</tr>

	<tr>
		<td>
		<b> Título do Projeto: </b>
		<h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.titulo}"/> 
		<br/> 
		</td>
	</tr>

	<c:if test="${ autorizacaoProjetoMonitoria.obj.projetoEnsino.editalMonitoria != null }">
	<tr>		
		
		<td>
		<b> Edital:</b>
		<h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.editalMonitoria.numeroEdital}"/> 
		(<h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.editalMonitoria.descricao}"/>) </td>
	</tr>
	</c:if>

	<tr>
		<td><b> Situação:</b>
		<h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.situacaoProjeto.descricao}"/> </td>
	</tr>

	<tr>
		<td>
		<b> Resumo do Projeto: </b><br/>
		<h:outputText value="#{autorizacaoProjetoMonitoria.obj.projetoEnsino.resumo}" escape="false"/>
		<br/> 
		<hr/>
		</td>
	</tr>


	<tr>
		<td colspan="2">
		
			<table class="listagem">
			    <caption>Lista de Departamentos Envolvidos na Autorização do projeto</caption>
			      <thead>
			      	<tr>
						<th>Departamento</th>
			        	<th>Data Autorização</th>
			       		<th>Situação</th>
			        </tr>
			      </thead>
			        
				
				<c:if test="${not empty autorizacaoProjetoMonitoria.obj.projetoEnsino.autorizacoesProjeto}">		
					<tbody>
				        <c:forEach items="${autorizacaoProjetoMonitoria.obj.projetoEnsino.autorizacoesProjeto}" var="auto" varStatus="status">
    		    	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

					            <td> ${auto.unidade.nome} </td>
					            <td> <fmt:formatDate value="${auto.dataAutorizacao}"  pattern="dd/MM/yyyy HH:mm:ss" var="data"/>${data} <c:if test="${empty auto.dataAutorizacao}">-</c:if></td>

	
					            <c:if test="${not empty auto.dataAutorizacao}">
	   								<td> ${(auto.autorizado == true ? 'Autorizado': 'NÃO Autorizado')} </td>
	   							</c:if>

					            <c:if test="${empty auto.dataAutorizacao}">
	   								<td> Pendente </td>
	   							</c:if>
    		    	       		
        			       </tr>
						</c:forEach>
					</tbody>
				</c:if>
				
				<c:if test="${empty autorizacaoProjetoMonitoria.obj.projetoEnsino.autorizacoesProjeto}">
			        <tbody>
	                    <tr> <td colspan="3" align="center"> <font color="red">Projeto ainda não foi enviado aos departamentos envolvidos!</font> </td></tr>
					</tbody>		
				</c:if>
				
			</table>
			
			<br>
				
		</td>
	</tr>

	</tbody>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>