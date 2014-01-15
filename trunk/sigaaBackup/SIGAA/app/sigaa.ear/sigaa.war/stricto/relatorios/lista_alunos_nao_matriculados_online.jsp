<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<f:view>
    <h2 class="tituloTabela"><b>Lista dos Alunos não Matriculados On-Line</b></h2>
    <div id="parametrosRelatorio">
		<table>
			<tr>
				<th>Programa:</th>
				<td>
					${relatoriosStricto.unidade.nome}			
				</td>
			</tr>	   
			<tr>
				<th>Ano-Período: </th>
				<td>
				    ${relatoriosStricto.ano}.${relatoriosStricto.periodo}
				</td>		
			</tr>	
		</table>
	</div>
		
	<br/>
	<h:form>
	
		<c:set var="varNivel" value=""/>
		
		<c:forEach items="#{relatoriosStricto.listaDiscente}" var="linha">
			<c:if test="${varNivel != linha.nivel}">
				<c:if test='${varNivel != ""}'>			
					</table>   		
				</c:if>			 
				<c:set var="varNivel" value="${linha.nivel}"/>
				<table class="tabelaRelatorioBorda" width="100%">
				    <caption id="nivel">${varNivel}</caption>
					<thead>
						<tr>
							<th style="width:100px;" align="center">
								Matrícula 
							</th>
							<th style="width:400px;">
								Nome 
							</th>				
							<th>
								<div class="naoImprimir" align="center">
									Ação
								</div>
							</th>
						</tr>
					</thead>		
			</c:if> 
					<tbody>
						<tr>
							<td>
								${linha.matricula}
							</td>
							<td>
								${linha.nome}
							</td>				
							<td  align="center">
								<div class="naoImprimir">
									<h:commandLink action="#{movimentacaoAluno.iniciarCancelamentoDiscentePrograma}" value="Cancelar" id="btIniciarcancelamentoDocente">
										<f:param name="idDiscente" value="#{linha.id}"/>
									</h:commandLink>
								</div>				    
							</td>					
						</tr>
					</tbody>					
		</c:forEach>
				</table>
			
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>
