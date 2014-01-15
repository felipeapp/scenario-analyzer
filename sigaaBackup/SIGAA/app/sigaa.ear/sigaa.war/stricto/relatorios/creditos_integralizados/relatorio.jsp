<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
    <h2 class="tituloTabela"><b>Relatório de Créditos Integralizados</b></h2>
    <a4j:keepAlive beanName="relatorioCreditosIntegralizadosMBean"/>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatorioCreditosIntegralizadosMBean.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Nível: </th>
				<td>
				    ${relatorioCreditosIntegralizadosMBean.descricaoNivel}
				</td>		
			</tr>	
		</table>
	</div>
		
	<br/>
	<h:form>
		<table class="tabelaRelatorioBorda" width="100%">
			<thead>
				<tr>
					<th style="width:100px; text-align:center">Matrícula</th>
					<th style="width:400px;">Nome</th>		
					<th style="width:100px; text-align:right" >Créditos já integralizados</th>
					<th style="width:100px; text-align:right">Créditos Pendentes</th>		
				</tr>
			</thead>		
			<tbody>
				<c:forEach items="#{relatorioCreditosIntegralizadosMBean.listaDiscente}" var="linha">
					<tr>
						<td style="text-align:center">${linha.matricula}</td>
						<td>${linha.nome}</td>
						<td style="text-align:right">${linha.integralizados}</td>							
						<td style="text-align:right">
							<c:choose>
								<c:when test="${linha.obrigatorios > linha.integralizados}">
									${linha.obrigatorios - linha.integralizados}
								</c:when>								
								<c:otherwise>
									0
								</c:otherwise>
							</c:choose>
						</td>																	
					</tr>
				</c:forEach>
			</tbody>					
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
