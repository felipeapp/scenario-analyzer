<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@taglib uri="/tags/a4j" prefix="a4j"%>
<f:view>
	<a4j:keepAlive beanName="relatorioAlunosCadastradosResidenciaMBean"/>
	<h:form>
	    <h2 class="tituloTabela"><b>Total de Alunos Cadastrados por Programa (Detalhe)</b></h2>

		<c:if test="${relatorioAlunosCadastradosResidenciaMBean.obj.unidade.id > 0}">
		    <div id="parametrosRelatorio">
				<table>
					<tr>
						<th>Programa:</th>
						<td>
							${relatorioAlunosCadastradosResidenciaMBean.obj.unidade.nome}			
						</td>
					</tr>	   
				</table>
			</div>	   
		</c:if>

	   	<br/>
		<table class="tabelaRelatorioBorda" align="center" style="width: 100%">	
			<tr class="linhaCinza">
				<th style="width: 400px;">Discente</th>
				<th style="width: 100px; text-align: center;">Entrada</th>
				<th style="width: 150px;">Situação</th>										
			</tr>				
			<tbody>
				<c:set var="idUnidade" value="0"/>
				<c:set var="quant" value="0"/>
				<c:set var="programa" value=""/>
				<c:forEach items="#{relatorioAlunosCadastradosResidenciaMBean.discentes}" varStatus="loop" var="linha" >
					
					<c:if test="${relatorioAlunosCadastradosResidenciaMBean.obj.unidade.id == 0}">			
								
						<c:if test="${idUnidade != linha.gestoraAcademica.id}">				
							
							<c:if test="${!loop.first}">		
								<script>document.getElementById("programa${idUnidade}").innerHTML = '${programa} ('+${quant}+')';</script>
							</c:if>
							
							<c:set var="idUnidade" value="${linha.gestoraAcademica.id}"/>
							<c:set var="programa" value="${linha.gestoraAcademica.nome}"/>								
										    						
							<tr class="linhaCinza">
								<td style="text-align: center;" id="programa${idUnidade}" colspan="3"></td>
							</tr>
							
							<c:set var="quant" value="0"/>	
						</c:if>
						
						<c:set var="quant" value="${quant + 1}"/>
						
					</c:if>
					   
					<tr>
						<td>
							${linha.matriculaNome}
						</td>
						<td style="width: 100px; text-align: center;">
							${linha.anoPeriodoIngresso}
						</td>					
						<td>
							${linha.statusString}
						</td>	
					</tr>
				</c:forEach>	  
				<script>document.getElementById("programa${idUnidade}").innerHTML = '${programa} ('+${quant}+')';</script>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3" style="text-align: center;">
						${fn:length(relatorioAlunosCadastradosResidenciaMBean.discentes)}
						Registro(s) Encontrado(s)					
					</td>
				</tr>
			</tfoot>		
		</table>	
		<br/>
		
	</h:form>		
			
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>