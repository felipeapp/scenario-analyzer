<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>


<f:view>
	<h2><ufrn:subSistema /> > Validar Resultados da Sele��o</h2>
	
	<h:form id="form">

   		<table class="formulario" width="90%">
		<caption>Prova Seletiva Selecionada</caption>
		<tbody>
			<tr>
				<th width="25%" class="rotulo">Projeto de Ensino:</th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.projetoEnsino.titulo}"/></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo">Prova: </th> 
				<td align="left">${validaSelecaoMonitor.prova.titulo}</td>
			</tr>
			<tr>
                <th width="25%" class="rotulo">Data da Prova: </th> 
                <td align="left"><fmt:formatDate value="${validaSelecaoMonitor.prova.dataProva}" pattern="dd/MM/yyyy" /></td>
            </tr>			
			<tr>
				<th width="25%" class="rotulo">Vaga(s) Remunerada(s): </th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.vagasRemuneradas}"/></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo">Vaga(s) N�O Remunerada(s): </th> 
				<td align="left"><h:outputText value="#{validaSelecaoMonitor.prova.vagasNaoRemuneradas}"/></td>
			</tr>
			<tr>
				<th width="25%" class="rotulo">Discente: </th>
				<td> <h:outputText value="#{validaSelecaoMonitor.discenteMonitor.discente.matriculaNome}"/> </td>
			</tr>
			
			<tr>
				<th width="25%" class="rotulo">Nota: </th>
				<td> <h:outputText value="#{validaSelecaoMonitor.discenteMonitor.nota}"/> </td>
			</tr>
			
			<tr>
				<th width="25%" class="required">Situa��o Validada: </th>
				<td>
					<t:selectOneMenu value="#{validaSelecaoMonitor.idSituacao}" immediate="true" id="situacao">
						<f:selectItems value="#{validaSelecaoMonitor.allStatusSelecaoMonitoriaCombo }" />
					</t:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th width="25%" class="required">V�nculo Validado: </th>
				<td>
					<t:selectOneMenu value="#{validaSelecaoMonitor.idVinculo}" immediate="true" id="vinculo">
						<f:selectItems value="#{discenteMonitoria.tipoMonitoriaCombo}"/>
					</t:selectOneMenu>							
				</td>
			</tr>
			
			<tr>
				<th width="25%">Observa��es: </th>
				<td>
					<t:inputText value="#{validaSelecaoMonitor.observacoes}" rendered="#{dm.passivelValidacao}" id="observacao" title="Observa��es"/>
				</td>
			</tr>
			
			</tbody>			

	
		<tfoot>
			<tr>
				<td colspan="2">		
					<h:commandButton value="Confirmar Valida��o" action="#{validaSelecaoMonitor.alterarResultadoMonitor}" id="cmdConfirmarValidacao"/>
					<h:commandButton value="<< Voltar" action="#{validaSelecaoMonitor.voltaProva}" id="btVoltar"/>
					<h:commandButton value="Cancelar" action="#{validaSelecaoMonitor.cancelar}" id="cmdCancelar" onclick="#{confirm}"/>					
				</td>
			</tr>
		</tfoot>
	</table>

	<br/>
	
 </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>