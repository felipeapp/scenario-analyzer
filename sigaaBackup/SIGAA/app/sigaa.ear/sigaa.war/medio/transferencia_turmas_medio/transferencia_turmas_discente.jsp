<%@include file="/WEB-INF/jsp/include/cabecalho.jsp" %>

<style>
	tr.componente td{
		background: #C4D2EB;
		font-weight: bold;
		border-bottom: 1px solid #BBB;
		color: #222;
	}
	
	#tableTurmas .colLeft{text-align: left; }
	#tableTurmas .colCenter{text-align: center; }
	#tableTurmas .colRight{text-align: right; }

</style>

<f:view>
	<h:outputText value="#{transferenciaTurma.create}"/>
	
	<h2><ufrn:subSistema /> &gt; 
	Transferência de Aluno entre Turmas  &gt; Transferência por Aluno
	</h2>
	<table class="visualizacao">
		<caption>Dados do Discente</caption>
	</table>	
	<c:set var="discente" value="#{transferenciaTurma.discente}"/>
	<%@include file="/graduacao/info_discente.jsp"%>
	
	<h:form id="table_turma">
		
		<table class="subFormulario" width="100%">
			<td></td>
			<c:set var="idOrigem" value="0"/> 
			<c:if test="${not empty transferenciaTurma.turmasDataModel}">
				<table class=listagem>
					<caption class="listagem">Transferência de Turmas</caption>
				</table>	
				<t:dataTable value="#{transferenciaTurma.turmasDataModel}" var="turma" rowIndexVar="index"
					columnClasses="colLeft, colRight" rowClasses="linhaPar,linhaImpar" width="100%" id="tableTurmas" >
						
						<t:column headerstyle="text-align:left" width="50%">
							<f:facet name="header" >
								<h:outputText value="Turma Origem" />
							</f:facet>
							
							<h:outputText value="#{turma.turma.descricaoNivelTecnico}"/>
						</t:column>
						
						<t:column headerstyle="text-align:center" width="50%">
							<f:facet name="header">
								<h:outputText value="Turma Destino"/>
							</f:facet>
							<div style="text-align:center">
							<h:inputHidden id="idTurmaOrigem" value="#{turma.turma.id}"/>
							<h:selectOneMenu id="idTurmaDestino" style="width: 95%" 
								value="#{turma.turmaDestino.id}"
								disabled="#{empty turma.selectTurmasDestino}">
								<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
								<f:selectItems value="#{turma.selectTurmasDestino}" />
							</h:selectOneMenu>
							</div>
						</t:column>
											
				</t:dataTable>
			</c:if>
		</table>
		<br/>
		<center>
			<h:form>
				<h:commandButton value="Confirmar Transferência" action="#{transferenciaTurma.transferirTurmasDiscente}" />
				<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{transferenciaTurma.cancelar}" />
			</h:form>
		</center>		
	
	</h:form>
			
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp" %>