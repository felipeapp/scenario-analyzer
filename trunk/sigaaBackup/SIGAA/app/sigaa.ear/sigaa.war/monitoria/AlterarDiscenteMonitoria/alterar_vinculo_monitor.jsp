<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<c:if test="${!acesso.monitoria}">
		<%@include file="/portais/docente/menu_docente.jsp" %>
	</c:if>
	<a4j:keepAlive beanName="consultarMonitor" />
	
<h:form>

	<h:outputText value="#{alterarDiscenteMonitoria.create}" />

  <h2><ufrn:subSistema /> > Alterar Monitor</h2>

  <table class="formulario" width="100%">

		<caption class="listagem"> DADOS DA MONITORIA </caption>

		<tbody>


			<tr>
				<th>
					Projeto de Monitoria:
				</th>
				<td>				
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.projetoEnsino.anoTitulo}"/></b> 
				</td>
			</tr>	
			
			<tr>
				<th width="20%">
					Discente:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.discente.matriculaNome}"/></b> 
				</td>
			</tr>
			
			
			<tr>
				<th>
					Tipo de Vínculo:
				</th>
				<td>				
					<h:selectOneMenu value="#{alterarDiscenteMonitoria.novoTipoMonitoria}" id="tipoVinculo">
						<f:selectItems value="#{discenteMonitoria.tiposAtivosMonitoriaCombo}"/>
					</h:selectOneMenu>
					<ufrn:help img="/img/ajuda.gif">Selecione o novo Tipo de Vínculo que o(a) monitor(a) irá assumir.</ufrn:help>				
				</td>
			</tr>	
			
			<tr>
				<th>
					Situação:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.situacaoDiscenteMonitoria.descricao}"/></b>
				</td>
			</tr>	
			
			<tr>
				<th>
					Classificação:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.classificacao}"/>º</b>
				</td>
			</tr>		
		
			<tr>
				<th>
					Nota da prova escrita:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.notaProva}"/></b>
				</td>
			</tr>
			<tr>
				<th>
					Nota Final:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.nota}"/></b>
				</td>
			</tr>		
		
			<tr>
				<th>
					Data de Entrada:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
				</td>
			</tr>						
		
		
			<tr>
				<th>
					Data de Saída:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText></b>
				</td>
			</tr>						
		
			<tr>
				<th valign="top">
					Observações:
				</th>
				<td>
					<b><h:outputText value="#{alterarDiscenteMonitoria.obj.observacao}"/></b> 
				</td>
			</tr>
			
			<tr>
				<td colspan="2">
					<table class="subFormulario" width="100%">		
						<caption class="listagem"> Orientações do Discente </caption>
							<tr>
								<td>
									<t:dataTable value="#{alterarDiscenteMonitoria.obj.orientacoes}" var="orient" align="center" width="100%">

										<t:column>
											<f:facet name="header">
												<f:verbatim>Docente</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.equipeDocente.servidor.siapeNome}" />						
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>Início</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataInicio}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
										</t:column>
					
										<t:column>
											<f:facet name="header">
												<f:verbatim>Fim</f:verbatim>
											</f:facet>
											<h:outputText value="#{orient.dataFim}"><f:convertDateTime pattern="dd/MM/yyyy"/></h:outputText>
										</t:column>
									
										<t:column>
											<f:facet name="header">
												<f:verbatim>Status</f:verbatim>
											</f:facet>		
											<h:outputText value="#{orient.status}" />																		
										</t:column>
									
									</t:dataTable>
						
							<c:if test="${empty alterarDiscenteMonitoria.obj.orientacoes}">
								<center><font color='red'>DISCENTE SEM ORIENTADORES RELACIONADOS</font></center>
							</c:if>
							
						  </td>
						</tr>
						
						</table>
				</td>
			</tr>
			<tr>
				<th>
					<font color="red"><h:outputText value="Verificar envio de Relatórios: " rendered="#{acesso.monitoria}" escape="false"/></font>
				</th>					
				<td>					
					<h:selectOneRadio value="#{alterarDiscenteMonitoria.validarRelatorios}" id="validarRelatorios" rendered="#{acesso.monitoria}">
							<f:selectItem itemValue="true" itemLabel="Sim" id="sim" />
							<f:selectItem itemValue="false" itemLabel="Não" id="nao" />
					</h:selectOneRadio>				
				</td>
			</tr>
			</tbody>

			<tfoot>
				<tr>
					<td colspan="2">					
					<h:commandButton value="#{alterarDiscenteMonitoria.confirmButton }" action="#{ alterarDiscenteMonitoria.alterarVinculoMonitor }"/>
					<input type="button" value="<< Voltar" onclick="javascript:history.go(-1)"/>
					<h:commandButton value="Cancelar" action="#{ alterarDiscenteMonitoria.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>

	</table>


</h:form>
<br/>
<br/>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>