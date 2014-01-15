<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<f:view>
	<h:outputText value="#{projetoMonitoria.create}"/>	
    
	<h2><ufrn:subSistema /> > Relatorio de projetos que estão ativos e com monitores ativos ou inativos</h2>


 	<h:messages />

 	<h:form id="formBusca">

	<table class="formulario" width="70%">
	<caption>Projetos que estão ativos e com monitores ativos ou inativos</caption>
	<tbody>	     

		<tr align="center">			
	    	<td align="right" width="50%" class="required"> Ano do Projeto:&nbsp;&nbsp;  </td>
	    	<td align="left" width="50%"> <h:inputText value="#{projetoMonitoria.buscaAnoProjeto}" maxlength="4" size="4" onkeyup="return formatarInteiro(this)"/></td>
	    </tr>	 

	</tbody>
	<tfoot>
		<tr>
			<td colspan="3">
				<h:commandButton value="Buscar" action="#{ projetoMonitoria.relatorioProjetosAtivosComMonitoresAtivosInativos }"/>
				<h:commandButton value="Cancelar" action="#{ projetoMonitoria.cancelar }" onclick="#{confirm}"/>
	    	</td>
	    </tr>
	</tfoot>
	</table>
	
	<br/><center>	<h:graphicImage  url="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> </center><br/>
	
	
	<br/>
	<br/>
	
	<c:if test="${ not empty projetoMonitoria.projetosLocalizados}">
	<table class="listagem">
		    <caption>Projetos de Monitoria Encontrados (${ fn:length(projetoMonitoria.projetosLocalizados) })</caption>
		      <thead>
		      	<tr>
		        	<th>Ano</th>
		        	<th>Título</th>		        	
		        	<th>Situação do Projeto</th>
		        	<th></th>
		        </tr>
		 	</thead>
		 			 	
		 	<tbody>
				 		 
	       	<c:set var="BOLSISTA" value="${ 2 }"/>
			<c:set var="ASSUMIU_MONITORIA" value="${ 5 }"/>
	       	<c:forEach items="${projetoMonitoria.projetosLocalizados}" var="projeto" varStatus="status">
	
				<c:set var="TOTAL_DISCENTES_ATIVOS" value="0"/>
				<c:set var="TOTAL_DISCENTES_INATIVOS" value="0"/>
				
				<tr class="linhaImpar">
						
					<td> ${projeto.ano}</td>
	                <td> ${projeto.titulo} </td>
					<td> ${projeto.situacaoProjeto.descricao} </td>						
						
					<tr>	
					<td colspan="4">	
						<table class="listagem">
							<thead>
								<tr>
									<th>Discente</th>
									<th>Vínculo</th>
		        					<th>Data Início</th>		        	
		        					<th>Data Fim</th>
		        					<th>Situação do Discente</th>
		        					<th>Situação da Monitoria</th>
		        				</tr>
		        				
		        				
		        			</thead>	
							
							<c:set var="TOTAL_BOLSISTA_ASSUMIU_MONITORIA" value="${ 0 }"/>						
							<c:forEach items="${projeto.discentesMonitoria}" var="discMonitoria" varStatus="status">
								<tr>
									<c:if test="${discMonitoria.ativo}"> <c:set var="TOTAL_DISCENTES_ATIVOS" value="${ TOTAL_DISCENTES_ATIVOS + 1 }"/>  </c:if>
									<c:if test="${!discMonitoria.ativo}"> <c:set var="TOTAL_DISCENTES_INATIVOS" value="${ TOTAL_DISCENTES_INATIVOS + 1 }"/>  </c:if>
									<c:if test="${discMonitoria.tipoVinculo.id == BOLSISTA && discMonitoria.situacaoDiscenteMonitoria.id == ASSUMIU_MONITORIA}"> <c:set var="TOTAL_BOLSISTA_ASSUMIU_MONITORIA" value="${ TOTAL_BOLSISTA_ASSUMIU_MONITORIA + 1 }"/>  </c:if>
								
									<td> ${discMonitoria.discente.pessoa} </td>
									
									<td> ${discMonitoria.tipoVinculo.descricao} </td>
									
									<td> <fmt:formatDate value="${discMonitoria.dataInicio}" pattern="dd/MM/yyyy"/> </td>
									<td> <fmt:formatDate value="${discMonitoria.dataFim}" pattern="dd/MM/yyyy"/> </td>
									<td> 
										<c:if test="${discMonitoria.ativo}">ATIVO</c:if>
										<c:if test="${!discMonitoria.ativo}">INATIVO</c:if>
									</td>
									<td> ${discMonitoria.situacaoDiscenteMonitoria.descricao} </td>									
								</tr>
							</c:forEach>
								
								<tr style="background-color: white;">
									<td colspan="6" align="center"><b>Total de Discentes Ativos: ${ TOTAL_DISCENTES_ATIVOS }</b></td>
								</tr>
								
								<tr style="background-color: white;">
									<td colspan="6" align="center"><b>Total de Discentes Inativos: ${ TOTAL_DISCENTES_INATIVOS }</b></td>
								</tr>
								
								<tr style="background-color: white;">
									<td colspan="6" align="center"><b>Total de Bolsas Concedidas ao Projeto: ${ projeto.bolsasConcedidas }</b></td>
								</tr>
								
								<tr style="background-color: white;">
									<td colspan="6" align="center">
										<b>Total de Vagas que ainda não foram Preenchidas: ${ projeto.bolsasConcedidas - TOTAL_BOLSISTA_ASSUMIU_MONITORIA }</b>										
									</td>
								</tr>
						</table>
					</td>
					</tr>
						
						
								
						
	              
	          </c:forEach>
		 	</tbody>
		 </table>
		 </c:if>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>	