<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/matricula.js"></script>

<f:view>
<a4j:keepAlive beanName="gerarMatriculaDiscentesIMD"/>
<h2> <ufrn:subSistema /> > Gerar Matrícula dos Discentes sem Matrícula</h2>
	
<h:form id="form">
	
<c:if test="${not empty gerarMatriculaDiscentesIMD.discentesSelecionados}">

	
	
	<rich:dataTable value="#{ gerarMatriculaDiscentesIMD.discentesSelecionados }" styleClass="listagem" rowClasses="linhaPar, linhaImpar" var="discente" width="100%" rowKeyVar="c">
	
		<f:facet name="caption"><f:verbatim>Discentes Selecionados (${fn:length(gerarMatriculaDiscentesIMD.discentesSelecionados)})</f:verbatim></f:facet>
		
	
		<rich:column>
			<f:facet name="header"><f:verbatim>CPF</f:verbatim></f:facet>
			<h:outputText value="#{discente.pessoa.cpf_cnpjString}"/>
		</rich:column>
		<rich:column>
			<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
			<h:outputText value="#{discente.pessoa.nome}"/>
		</rich:column>
		<rich:column>
			<f:facet name="header"><f:verbatim>Opção - Pólo - Grupo</f:verbatim></f:facet>
			<h:outputText value="#{discente.opcaoPoloGrupo.descricao}"/>
		</rich:column>
		
		
		<f:facet name="footer">
		<rich:columnGroup>
			<rich:column style="text-align: center" colspan="6">
				<h:commandButton value="Gerar Matrícula" action="#{ gerarMatriculaDiscentesIMD.gerarMatriculaDiscentesSelecionados }" />
				<h:commandButton value="Cancelar" action="#{gerarMatriculaDiscentesIMD.cancelar}" onclick="#{confirm}" id="cancelar" />
			</rich:column>
		</rich:columnGroup>
	</f:facet>	
		
		
	
	</rich:dataTable>
	
</c:if>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>