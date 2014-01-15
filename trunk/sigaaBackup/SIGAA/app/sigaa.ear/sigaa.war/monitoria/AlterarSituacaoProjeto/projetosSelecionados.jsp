<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="alterarStatusProjetoMonitoriaMBean" />
			
	<h2><ufrn:subSistema /> > Alterar Situação dos Projetos de Ensino</h2>

	<h:form>

		<table class="formulario" width="55%">
		  <caption>Situação do Projetos</caption>
			<tr>
		    	<th class="obrigatorio"> Situação do Projeto: </th>
		    	<td>
		    		<h:selectOneMenu value="#{alterarStatusProjetoMonitoriaMBean.idSituacao}" onchange="submit()">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{projetoMonitoria.tipoSituacaoProjetoCombo}" />
	 			 	</h:selectOneMenu>
		    	 </td>
		    </tr>
		</table>

		<br />
	
	<c:if test="${not empty alterarStatusProjetoMonitoriaMBean.projetosSelecionados}">
		 <table class="listagem">
		    <caption>Projetos de Monitoria Selecionados (${ fn:length(alterarStatusProjetoMonitoriaMBean.projetosSelecionados) })</caption>
		      <thead>
		      	<tr>
		        	<th>Ano</th>
		        	<th>Título</th>
		        	<th width="10%">Tipo</th>
		        	<th width="15%">Dimensão</th>		        	
		        	<th>Situação</th>
		        </tr>
		 	</thead>
		 			 	
		 	<tbody>
			<c:set var="unidadeProjeto" value=""/>	 		 
	       	<c:forEach items="#{alterarStatusProjetoMonitoriaMBean.projetosSelecionados}" var="projeto" varStatus="status">
	
						<c:if test="${ unidadeProjeto != projeto.unidade.id }">
							<c:set var="unidadeProjeto" value="${ projeto.unidade.id }"/>
							<tr>
								<td colspan="11" style="background: #C8D5EC; font-weight: bold; padding: 2px 0 2px 5px;">
								${ projeto.unidade.nome } / ${ projeto.unidade.sigla }
								</td>
							</tr>
						</c:if>

		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
						
						<td> ${projeto.ano}</td>
	                    <td>
	                    	<h:outputText value="#{projeto.titulo}">
	                    		<f:attribute name="lineWrap" value="90"/>
								<f:converter converterId="convertTexto"/>
	                    	</h:outputText>  
		                     <br/><i><h:outputText value="Coordenador(a): #{projeto.coordenacao.pessoa.nome}" rendered="#{not empty projeto.coordenacao}"/></i>
	                    </td>
	                    <td> ${projeto.tipoProjetoEnsino.sigla} </td> 
	                    <td> ${projeto.projetoAssociado ? 'PROJETO ASSOCIADO' : 'PROJETO ISOLADO'}</td>	                    
						<td> ${projeto.situacaoProjeto.descricao} </td>
	              </tr>
	          </c:forEach>
		 	</tbody>
		 	
			<tfoot>
				<tr>
					<td colspan="6" style="text-align: center;">
						<h:commandButton value="<< Voltar" action="#{alterarStatusProjetoMonitoriaMBean.iniciarSelecao}" immediate="true"/>
						<h:commandButton value="Alterar Status" action="#{alterarStatusProjetoMonitoriaMBean.alterarStatus}" immediate="true" />
						<h:commandButton value="Cancelar" action="#{alterarStatusProjetoMonitoriaMBean.cancelar}" onclick="#{confirm}" immediate="true" />
			    	</td>
			    </tr>
			</tfoot>
		 	
		 </table>
	</c:if>
	
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>