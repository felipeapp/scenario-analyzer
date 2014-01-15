<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<h:outputText value="#{provaSelecao.create}" />

	<h2><ufrn:subSistema /> > Inscritos no Processo Seletivo</h2>
	<br/>
	
		<table width="100%" class="subFormulario">
			<tr>
			<td valign="top" style="text-align: justify">
				<table>
					<tr><td width="20%">Projeto:</td><td><b>${provaSelecao.obj.projetoEnsino.titulo}</td></tr>
					<tr><td>Inscrições até:</td><td><b><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataLimiteIncricao}"/></td></tr>
					<tr><td>Data da Prova:</td><td><b><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataProva}"/></td></tr>
					<tr><td>Outras Informações: </td><td><b>${provaSelecao.obj.informacaoSelecao}</b>
						</td>
					</tr>
				</table>			
			</td>
			</tr>
		</table>
		
		
		<br/>
		<br/>		

		<table class="listagem">
		    <caption>Lista de Candidatos Inscritos</caption>
		            <tbody>
		            <tr><td>

		      <thead>
		      	<tr>
		        	<th>Discente</th>
		        	<th>Data Inscrição</th>
		        </tr>
		      </thead>


			<c:set var="lista" value="${provaSelecao.obj.discentesInscritos}" />


			<c:if test="${empty lista}">
		        <tbody>
                    <tr> <td colspan="5" align="center"> <font color="red">Não há discentes cadastrados neste projeto.</font> </td></tr>
				</tbody>
			</c:if>

			<c:if test="${not empty lista}">

			          	<c:forEach items="${lista}" var="inscricao" varStatus="status">
				               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">

				                    <td> ${inscricao.discente.matriculaNome} </td>
				                    <td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${inscricao.dataCadastro}" /> </td>
				              </tr>
				        </c:forEach>

			</c:if>
	
		</td></tr>		
		</tbody>						        
	</table>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>