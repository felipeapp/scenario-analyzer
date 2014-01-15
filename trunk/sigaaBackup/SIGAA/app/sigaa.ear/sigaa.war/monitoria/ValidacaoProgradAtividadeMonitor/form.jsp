<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>
<f:view>
	<h:outputText value="#{ atividadeMonitor.create }"/>
	<h2><ufrn:subSistema /> > Avaliando Relatório de Atividades do Monitor</h2>
	<h:form>

	<h:inputHidden value="#{atividadeMonitor.confirmButton}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.id}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.discenteMonitoria.projetoEnsino.id}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.mes}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.ano}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.atividades}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.validadoOrientador}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.dataValidacaoOrientador}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.frequencia}"/>
	<h:inputHidden value="#{atividadeMonitor.obj.observacaoOrientador}"/>
	

	<table class="formulario" width="100%">
	<caption class="listagem">Avaliação do Relatório de Atividades do Monitor pela PROGRAD</caption>

	<tr>
		<td class="subFormulario" colspan="2">Relatório de Atividades do(a) Monitor(a)</td>
	</tr>

	<tr>
		<td>
			<b>Discente:</b>
		</td>
		<td>
			<h:outputText value="#{ atividadeMonitor.obj.discenteMonitoria.discente.matriculaNome }" />
		</td>
	</tr>


	<tr>
		<td>
			<b>Projeto de Ensino:</b>
		</td>
		<td>
			<h:outputText value="#{ atividadeMonitor.obj.discenteMonitoria.projetoEnsino.titulo }" />
		</td>
	</tr>

	<tr>

		<td> 
			<b>Mês Referência do Relatório:</b>
		</td>
		<td>
			<h:outputText value="#{ atividadeMonitor.obj.mes }"/>/<h:outputText value="#{ atividadeMonitor.obj.ano }"/>
		</td>
	</tr>
	
	<tr>
		<td>
			 <b>Atividades do Monitor:</b>
		 </td>
		 <td>
			 <h:outputText value="#{ atividadeMonitor.obj.atividades }"/>
			 <br/>
		 	<br/>
		</td>		
	</tr>

	<tr>
		<td class="subFormulario" colspan="2">Avaliação da Orientação - Freqüência</td>
	</tr>
		
	<tr>
		<td><b>Orientador(es):</b></td>
		<td>
			
			<table width="100%">
				<c:forEach items="${atividadeMonitor.obj.discenteMonitoria.orientacoes}" var="o" varStatus="status">
					<tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }" id="linhaLista">	
						<td><c:out value="${o.equipeDocente.servidor.siapeNome}" /></td>
					</tr>
				</c:forEach>
			</table>
			
		</td>
	</tr>


	<tr>
		<td> 
		<b>Validado:</b>
		</td> 
		<td> 		
		  ${ atividadeMonitor.obj.validadoOrientador == true ? 'SIM': 'NÃO' } em <h:outputText value="#{ atividadeMonitor.obj.dataValidacaoOrientador }"/>
		  
		</td>
	</tr>
	
	<tr>
		<td> 
			<b>Freqüência:</b>
		</td> 
		<td> 		
			<h:outputText value="#{ atividadeMonitor.obj.frequencia }"/>%
		</td>
	</tr>

	<tr>
		<td> 
			<b>Observações:</b>
		</td> 
		<td>
			<h:outputText value="#{ atividadeMonitor.obj.observacaoOrientador }"/>
			<br/>
			<br/>		
		</td>
	</tr>
	
	<tfoot>
		<tr>
			<td colspan="2">
					<h:commandButton value="Desvalidar" action="#{atividadeMonitor.desvalidarAtividadeMonitor}"/>
					<h:commandButton value="Cancelar" action="#{atividadeMonitor.cancelar}" onclick="#{confirm}"/>
			</td>
		</tr>
	</tfoot>
	</table>

</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>