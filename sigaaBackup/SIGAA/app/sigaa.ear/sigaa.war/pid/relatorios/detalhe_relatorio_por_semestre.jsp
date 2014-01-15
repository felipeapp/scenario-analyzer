<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<a4j:keepAlive beanName="relatorioPIDPorSemestre"/>
<f:view>
	<h:form>
	    <h2 class="tituloTabela"><b>PID - Relatório por Semestre (Detalhamento)</b></h2>
	    <div id="parametrosRelatorio">
			<table>
				<tr>
					<th>Ano-Período:</th>
					<td>
						${relatorioPIDPorSemestre.ano}.${relatorioPIDPorSemestre.periodo} 			
					</td>
				</tr>
				<c:if test="${relatorioPIDPorSemestre.unidade != null && !empty relatorioPIDPorSemestre.unidade.nome}">
					<tr>
						<th>Unidade:</th>
						<td>
							${relatorioPIDPorSemestre.unidade.nome} 			
						</td>
					</tr>	 					      
				</c:if>
				<c:if test="${relatorioPIDPorSemestre.gestora != null && !empty relatorioPIDPorSemestre.gestora.nome}">
					<tr>
						<th>Centro:</th>
						<td>
							${relatorioPIDPorSemestre.gestora.nome} 			
						</td>
					</tr>	 					      
				</c:if>				
				<c:if test="${!empty relatorioPIDPorSemestre.statusDescricao}">
					<tr>
						<th>Situação:</th>
						<td>${relatorioPIDPorSemestre.statusDescricao}</td>
					</tr>
				</c:if>
			</table>
		</div>
		<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<thead>
				<tr>				
					<th>Docente</th>
					<c:if test="${empty relatorioPIDPorSemestre.statusDescricao}">
						<th>Situação do PID</th>
					</c:if>
				</tr>		
				<tr>
					<td colspan="3"  style="text-align: center;" class="linhaCinza">Quantidade de Docentes: ${ fn:length(relatorioPIDPorSemestre.listagemPIDs) }</td>
				</tr>  						
			</thead>
				
			<tbody>
				<c:set var="idUnidade" value="0"/>
				<c:set var="total" value="0"/>
				<c:forEach items="#{relatorioPIDPorSemestre.listagemPIDs}" varStatus="loop" var="linha" >   
					<c:if test="${relatorioPIDPorSemestre.unidade == null}"> 		
						<c:if test="${idUnidade != linha.servidor.unidade.id}">
							<c:if test="${!loop.first}">
								<tr class="linhaCinza">
									<td colspan="3" style="text-align: center;">Total: ${total}</td>
								</tr>
								<tr><td colspan="3">&nbsp;</td></tr>														
							</c:if>
							<tr class="linhaCinza">
								<td colspan="3" style="text-align: center;">${linha.servidor.unidade.nome}</td>
							</tr>
							<c:set var="total" value="0"/>
						</c:if>
					</c:if>
					<c:set var="idUnidade" value="#{linha.servidor.unidade.id}"/>
					<c:set var="total" value="${total + 1}"/>
					<tr class="${loop.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						<td style="text-align: left;">
							${linha.servidor.nome}
						</td>	
						<c:if test="${empty relatorioPIDPorSemestre.statusDescricao}">
							<c:if test="${!empty linha.descricaoStatus}">
								<td>${linha.descricaoStatus}</td>
							</c:if>
							<c:if test="${empty linha.descricaoStatus}">
								<td>NÃO CADASTRADO</td>
							</c:if>
						</c:if>			
					</tr>
				</c:forEach>	
				<c:if test="${relatorioPIDPorSemestre.unidade == null}"> 		
					<tr class="linhaCinza">
						<td colspan="3" style="text-align: center;">Total: ${total}</td>
					</tr>														
				</c:if>				
			</tbody>		
		</table>	
	</h:form>			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>