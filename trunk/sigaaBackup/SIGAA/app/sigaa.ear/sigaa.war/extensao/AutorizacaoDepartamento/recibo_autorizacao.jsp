<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>
	<h:messages/>
	<h2>Recibo de Autoriza��o de A��o Acad�mica</h2>
	<br>


	<h:outputText value="#{autorizacaoDepartamento.create}"/>

	
<h:form>
	<table class="formulario" width="100%">

		<caption class="listagem"> RECIBO DE AUTORIZA��O DA A��O ACAD�MICA</caption>

<tbody>


	<tr>
		<th width="35%"><b>N�mero do Recibo:</b></th>
		<td><h:outputText value="#{autorizacaoDepartamento.obj.id}"/></td>
	</tr>


	<tr>
		<th><b>T�tulo da A��o:</b></th>
		<td><h:outputText value="#{autorizacaoDepartamento.obj.atividade.anoTitulo}"/> </td>
	</tr>

	<tr>
		<th><b>Situa��o:</b></th>
		<td><h:outputText value="#{autorizacaoDepartamento.obj.atividade.situacaoProjeto.descricao}"/> </td>
	</tr>

	<tr>
		<th><b>�rea Tem�tica Principal:</b></th>
		<td><h:outputText value="#{autorizacaoDepartamento.obj.atividade.areaTematicaPrincipal.descricao}"/></td>
	</tr>

	<tr>
		<th><b>�rea de Conhecimento CNPq:</b></th>
		<td><h:outputText value="#{autorizacaoDepartamento.obj.atividade.areaConhecimentoCnpq.nome}"/></td>
	</tr>


	<tr>
		<td colspan="2">
		
			<table class="listagem">
			    <caption>Lista de Departamentos envolvidos na Autoriza��o da A��o</caption>
			      <thead>
			      	<tr>
						<th>Departamento</th>
			        	<th>Autorizado em</th>
			        	<th>Data da Reuni�o</th>
			       		<th>Situa��o</th>
			        </tr>
			      </thead>
			        
				
				<c:if test="${not empty autorizacaoDepartamento.obj.atividade.autorizacoesDepartamentos}">		
					<tbody>
				        <c:forEach items="${autorizacaoDepartamento.obj.atividade.autorizacoesDepartamentos}" var="auto" varStatus="status">

							<c:if test="${auto.ativo}">
	    		    	       <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
	
						            <td> ${auto.unidade.nome} </td>
						            <td> <fmt:formatDate value="${auto.dataAutorizacao}"  pattern="dd/MM/yyyy HH:mm:ss" var="data"/>${data} <c:if test="${empty auto.dataAutorizacao}">-</c:if></td>
									
									<td> <fmt:formatDate value="${auto.dataReuniao}" pattern="dd/MM/yyyy" var="dataReuniao"/>${dataReuniao} <c:if test="${empty auto.dataReuniao}">-</c:if> </td>
		
						            <c:if test="${not empty auto.dataAutorizacao}">
		   								<td> ${(auto.autorizado == true ? 'Autorizado': 'N�O Autorizado')} </td>
		   							</c:if>
	
						            <c:if test="${empty auto.dataAutorizacao}">
		   								<td> Pendente </td>
		   							</c:if>
	    		    	       		
	        			       </tr>
							</c:if>	
										        
						</c:forEach>
					</tbody>
				</c:if>
				
				<c:if test="${empty autorizacaoDepartamento.obj.atividade.autorizacoesDepartamentos}">
			        <tbody>
	                    <tr> <td colspan="3" align="center"> <font color="red">A��o ainda n�o foi enviada aos departamentos envolvidos!</font> </td></tr>
					</tbody>		
				</c:if>
				
			</table>
			
			<br>
				
		</td>
	</tr>

	</tbody>
	<tfoot>
	<tr><td colspan="2">
		<input type="button" value="Imprimir" onclick="javascript:window.print()"/>
		<h:commandButton action="#{autorizacaoDepartamento.autorizacaoChefe}" value="Avaliar outra A��o" rendered="#{!acesso.extensao}"/>		
		<h:commandButton action="#{autorizacaoDepartamento.listarAutorizacoesAcoesDepartamentosProex}" value="Avaliar outra A��o" rendered="#{acesso.extensao}"/>
	</td></tr>
	</tfoot>
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>