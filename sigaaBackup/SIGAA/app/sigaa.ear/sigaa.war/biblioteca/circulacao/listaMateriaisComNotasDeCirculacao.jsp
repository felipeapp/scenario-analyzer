<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<style type="text/css">

	table.listagem tr.biblioteca td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<h2><ufrn:subSistema /> > Materiais com Notas de Circulação </h2>
	<h:form>
	
		<a4j:keepAlive  beanName="notasCirculacaoMBean"/>
	
		<div class="descricaoOperacao" style="width:80%;">
				<p>Esta listagem exibe os materiais que possuem alguma nota de circulação. Uma nota de circulação pode ser bloqueante ou não.</p> 
				<p>
				<ul>
				<li> <strong>Notas bloqueantes</strong> são mostradas para o operador no momento do empréstimo ou renovação 
				e impedem que o material seja emprestado. Uma nota bloqueante permanecerá ativa até que o material seja desbloqueado. 
				</li> 
				<li><strong>Notas não bloqueantes</strong> são mostradas uma única vez ao operador no momento do empréstimo, renovação ou devolução do material, 
				dependendo da configuração feita pelo usuário. <strong>Estas não impedem que o empréstimo seja realizado.</strong> 
				</li>
				</ul>
				</p>
			</div>
	
		<div class="infoAltRem" style="width:90%;margin-top:15px;">
			<h:graphicImage value="/img/buscar.gif" />
			
			<h:commandLink value="Buscar Materiais para Incluir uma Nota" action="#{notasCirculacaoMBean.iniciarBuscaMaterialIncluirNota}" />
			
			<h:graphicImage value="/img/check.png" style="overflow: visible;" />: Desbloquear Material
			
			<h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Editar Nota
			
			<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Nota
			
		</div> 

		<table class="listagem" style="width:90%;">
		
			<caption>Lista de Materiais Com Notas de Circulação (${fn:length(notasCirculacaoMBean.all)})</caption>
			
			<c:if test="${fn:length(notasCirculacaoMBean.all) <= 0}">
				<tr><td style="text-align:center;color:red;">Não há materiais com notas de circulação para a sua biblioteca.</td></tr>
			</c:if>
			
			<c:if test="${fn:length(notasCirculacaoMBean.all) > 0}">
				<thead>
					<tr>
						<th width="65%">Material</th>
						<th style="width: 10%;">Próximo Empréstimo</th>
						<th style="width: 10%;">Próxima Renovação</th>
						<th style="width: 10%;">Próxima Devolução</th>
						<th style="width: 5%;"></th>
					</tr>
				</thead>
				
				<c:set var="idFiltroBiblioteca" value="-1" scope="request" />
				
				<c:forEach items="#{notasCirculacaoMBean.all}" var="nota" varStatus="status">
					
					<c:if test="${ idFiltroBiblioteca != nota.material.biblioteca.id}">
						<c:set var="idFiltroBiblioteca" value="${nota.material.biblioteca.id}" scope="request"  />
						<tr class="biblioteca">
							<td colspan="8">${nota.material.biblioteca.descricaoCompleta}</td>
						</tr>
					</c:if>
					
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td> ${nota.material.informacao} </td>
						<c:if test="${nota.bloquearMaterial}">
							<td colspan="3" style="font-weight: bold; color: red; text-align: center;">  BLOQUEADO </td>
							
						</c:if>
						<c:if test="${! nota.bloquearMaterial}">
							<c:if test="${nota.mostrarEmprestimo}"> 
								<td style="font-weight: bold; color: green;"> SIM </td>
							</c:if>  
							<c:if test="${!nota.mostrarEmprestimo}"> 
								<td style="color:gray;">NÃO</td> 
							</c:if> 
							
							
							<c:if test="${nota.mostrarRenovacao}"> 
								<td style="font-weight: bold; color: green;"> SIM </td>
							</c:if>  
							<c:if test="${!nota.mostrarRenovacao}"> 
								<td style="color:gray;">NÃO</td> 
							</c:if> 
							
							
							<c:if test="${nota.mostrarDevolucao}"> 
								<td style="font-weight: bold; color: green;"> SIM </td>
							</c:if>  
							<c:if test="${!nota.mostrarDevolucao}"> 
								<td style="color:gray">NÃO</td> 
							</c:if> 
							
						</c:if>
						
						<td>
							
							<h:commandLink action="#{notasCirculacaoMBean.prepararEditarNota}" title="Editar Nota">
								<h:graphicImage value="/img/alterar.gif" style="margin-right: 5px" />
								<f:param name="idMaterial" value="#{nota.material.id}"/>
							</h:commandLink>
							
							<c:if test="${nota.bloquearMaterial}"> 
								<h:commandLink action="#{notasCirculacaoMBean.desbloquearRemoverNota}" title="Desbloquear Material" onclick="if (!confirm('Tem certeza que deseja desbloquear este material?')) return false;">
									<h:graphicImage value="/img/check.png" />
									<f:param name="idMaterial" value="#{nota.material.id}"/>
								</h:commandLink>
							</c:if>
								
							<c:if test="${! nota.bloquearMaterial}"> 
								<h:commandLink action="#{notasCirculacaoMBean.desbloquearRemoverNota}" title="Remover Nota" onclick="if (!confirm('Tem certeza que deseja remover essa nota ?')) return false;">
									<h:graphicImage value="/img/delete.gif" />
									<f:param name="idMaterial" value="#{nota.material.id}"/>
								</h:commandLink>
							</c:if>
						</td>
					</tr>
					
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td colspan="6" style="font-style: italic; padding-left: 5%; padding-right: 1%; width:100%; overflow:auto; ">
             				${nota.nota}
						 </td>
					</tr>
					
				</c:forEach>
			</c:if>
		</table>

	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>