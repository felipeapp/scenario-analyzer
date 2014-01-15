<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<%@include file="/portais/docente/menu_docente.jsp" %>
	
	<h2><ufrn:subSistema /> > Visualização de Monitor</h2>
	<br>


	<h:outputText value="#{consultarMonitor.create}"/>
	
<h:form onclick="javascript: confirm('Tem certeza que deseja realizar exclusão?')">

	<h:inputHidden value="#{consultarMonitor.obj.id}"/>
		<h:inputHidden value="#{consultarMonitor.obj.discente.matricula}"/>

	<table class="formulario" width="100%">

		<caption class="listagem"> DADOS DO MONITOR </caption>


<tbody>
	<tr>
		<td>
		<b> Projeto de Monitoria: </b>
		<h:outputText value="#{consultarMonitor.obj.projetoEnsino.titulo}"/> 
		<br/> 
		</td>
	</tr>	
	
	<tr>
		<td>
		<b> Discente: </b>
		<h:outputText value="#{consultarMonitor.obj.discente.matriculaNome}"/> 
		<br/> 
		</td>
	</tr>
	
	<tr>
		<b>Curso:</b>
		<h:outputText value="#{consultarMonitor.obj.discente.curso.nomeCompleto}"/>
		<br/> 
		</td>
	</tr>
	
	<tr>
		<td>
		<b> Tipo de Vínculo: </b>
		<h:outputText value="#{consultarMonitor.obj.tipoVinculo.descricao}"/> 
		<br/> 
		</td>
	</tr>	
	
	<tr>
		<td>
		<b> Situação: </b>
		<h:outputText value="#{consultarMonitor.obj.situacaoDiscenteMonitoria.descricao}"/> 
		<br/> 
		</td>
	</tr>	
	
	<tr>
		<td>
		<b> Classificação: </b>
		<h:outputText value="#{consultarMonitor.obj.classificacao}"/> 
		<br/> 
		</td>
	</tr>		

	<tr>
		<td>
		<b> Nota da prova escrita: </b>
		<h:outputText value="#{consultarMonitor.obj.notaProva}"/> 
		<br/> 
		</td>
	</tr>
	<tr>
		<td>
		<b> Nota Final: </b>
		<h:outputText value="#{consultarMonitor.obj.nota}"/> 
		<br/> 
		</td>
	</tr>		

	<tr>
		<td>
		<b> Data de Entrada: </b>
		<h:outputText value="#{consultarMonitor.obj.dataInicio}">
			<f:convertDateTime pattern="dd/MM/yyyy"/>
		</h:outputText>
		<br/> 
		</td>
	</tr>						


	<tr>
		<td>
		<b> Data de Saída: </b>
		<h:outputText value="#{consultarMonitor.obj.dataFim}">
			<f:convertDateTime pattern="dd/MM/yyyy"/>
		</h:outputText>
		<br/> 
		</td>
	</tr>						
	


	
	<tr>
		<td>
		<b> Observações: </b><br/>
		<h:outputText value="#{consultarMonitor.obj.observacao}"/> 
		<br/> 
		</td>
	</tr>
	



	<tr>
		<td colspan="2">

		<t:dataTable value="#{consultarMonitor.obj.orientacoes}" var="orientacao" align="center" width="100%">
		
					<t:column>
						<f:facet name="header">
							<f:verbatim>Orientadores</f:verbatim>
						</f:facet>
						<h:outputText value="#{orientacao.equipeDocente.servidor.siapeNome}" />
						
					</t:column>
		</t:dataTable>
		</td>
	</tr>


	</tbody>

	<tfoot>
	<tr><td colspan="2">
		<h:commandButton alt="Voltar" value="Voltar" action="#{consultarMonitor.listPage}"/>	
		<h:commandButton alt="Finalizar Monitoria" value="Finalizar Monitoria" action="#{ consultarMonitor.remover }"/>	
	</td></tr>
	</tfoot>
	
</table>


</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>