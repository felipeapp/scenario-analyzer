<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<f:view>

	<table width="100%" class="tabelaRelatorio">
		  <caption>Inscritos no Processo Seletivo</caption>
			
			<tbody>
					<tr><th width="25%"><b>Projeto de Ensino:</b></th><td>${provaSelecao.obj.projetoEnsino.anoTitulo}</td></tr>
					<tr><th><b>Título da Prova:</b></th><td>${provaSelecao.obj.titulo}</td></tr>
					<tr><th><b>Inscrições até:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataLimiteIncricao}"/></td></tr>
					<tr><th><b>Data da Prova:</b></th><td><fmt:formatDate pattern="dd/MM/yyyy" value="${provaSelecao.obj.dataProva}"/></td></tr>
					<tr><th><b>Vagas p/ Bolsistas:</b></th><td>${provaSelecao.obj.vagasRemuneradas}</td></tr>
					<tr><th><b>Vagas p/ Voluntários:</b></th><td>${provaSelecao.obj.vagasNaoRemuneradas}</td></tr>
					<tr><th><b>Situação da Prova:</b></th><td>${provaSelecao.obj.situacaoProva.descricao}</td></tr>
					<tr><th><b>Outras Informações:</b></th><td>${provaSelecao.obj.informacaoSelecao}</td></tr>
					
					<tr>
						<td class="subFormulario" colspan="2">Lista de Requisitos:</td>
					</tr>
					
					<tr>						
						<td colspan="2">
							<t:dataTable id="dtComponentesProva" value="#{provaSelecao.obj.componentesObrigatorios}" var="compProva" 
									align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar" 
									rendered="#{not empty provaSelecao.obj.componentesObrigatorios}">
				
									<t:column>
											<f:facet name="header"><f:verbatim>Obrigatório</f:verbatim></f:facet>								
											<h:outputText value="#{compProva.obrigatorio ? 'SIM':'NÃO'}" />
									</t:column>
									<t:column>
										<f:facet name="header"><f:verbatim>Componente Curricular</f:verbatim></f:facet>
										<h:outputText value="#{compProva.componenteCurricularMonitoria.disciplina.descricao}" />
									</t:column>			
							</t:dataTable>
						</td>
					</tr>
			</tbody>			
		</table>
		
		<br/>
		<br/>		

		<table width="100%" class="tabelaRelatorio">
		    <caption>Lista de Candidatos Inscritos</caption>
		      <thead>
		      	<tr>
		        	<th>Discente</th>
		        	<th>Data da Inscrição</th>
		        </tr>
		      </thead>
		     <tbody>
    			<c:set var="lista" value="${provaSelecao.obj.discentesInscritos}" />

	       		<c:if test="${empty lista}">
                    <tr> <td colspan="5" align="center"> <font color="red">Não há discentes cadastrados neste projeto.</font> </td></tr>
			     </c:if>

			     <c:if test="${not empty lista}">
			       	<c:forEach items="${lista}" var="inscricao" varStatus="status">
				        <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
    	                    <td> ${inscricao.discente.matriculaNome} </td>
		                    <td> <fmt:formatDate pattern="dd/MM/yyyy HH:mm:ss" value="${inscricao.dataCadastro}" /> </td>
	   	                </tr>
				    </c:forEach>
			     </c:if>	
		      </tbody>						        
	</table>

<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>