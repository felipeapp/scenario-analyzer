<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp"%>

<script>
  <!--

	function transferir(idOrigem, idDestino) {
		$(idOrigem).value = $F(idDestino);
	}
  
	function excluir(id) {
		if (confirm('Tem certeza que gostaria de remover esse resultado?')) {
			$('idDiscente').value=id;
			$('frmSelecao').submit();
		}
	}
	
  
  
  
//-->
</script>

<f:view>

	<h:messages showDetail="true" showSummary="true"/>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2><ufrn:subSistema /> > Cadastro de Resultados da Seleção de Extensão</h2>
	<h:form id="frmSelecao">

		<h:outputText value="#{discenteExtensao.create}"/>
		<input type="hidden" value="${discenteExtensao.atividade.id}" name="id"/>		

		<table width="100%" class="subFormulario">
			<tr>
				<td width="40"><html:img page="/img/help.png"/> </td>
				<td valign="top" style="text-align: justify">
					Os critérios de avaliação dos discentes inscritos na seleção são determinados pelo(a) Coordenador(a) da ação de extensão.
				</td>
			</tr>
		</table>
	
		<br/>

		<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Lista de Discentes inscritos </caption>

					<tr>
						<td >
							<t:dataTable value="#{discenteExtensao.atividade.inscricoesSelecao}" 
								var="inscrito" rowClasses="linhaPar,linhaImpar" width="100%" rowIndexVar="linha">

								<t:column>
									<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
									<h:selectBooleanCheckbox value="#{inscrito.selecionado}" styleClass="noborder" />
								</t:column>
			
								<t:column width="20%">
									<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
									<h:outputText value="#{inscrito.discente.matriculaNome}"/>
								</t:column>
			
								<t:column>
									<f:facet name="header"><f:verbatim>Resultado Seleção</f:verbatim></f:facet>
									<h:selectOneMenu value="#{inscrito.situacaoDiscenteExtensao.id}" style="width:170px">
									
										<f:selectItem itemValue="1" itemLabel="EM PROCESSO SELETIVO"/>
										<f:selectItem itemValue="2" itemLabel="NÃO SELECIONADO"/>										
										<f:selectItem itemValue="3" itemLabel="VOLUNTÁRIO"/>
										<f:selectItem itemValue="4" itemLabel="BOLSISTA"/>
									
									<%--	<f:selectItems value="#{discenteExtensao.allSituacaoDiscenteExtensaoCombo}"/> --%>
																			
									</h:selectOneMenu>
								</t:column>
								<%--	
								
								campo de justificativa agora em DiscenteExtensao
								
								<t:column styleClass="centerAlign">
										<f:facet name="header"><f:verbatim>Justificativa</f:verbatim></f:facet>
										<h:inputText value="#{inscrito.justificativa}" size="30"  />
								</t:column>
								--%>								
							</t:dataTable>
						</td>
				</tr>
				<c:if test="${empty discenteExtensao.atividade.inscricoesSelecao}">
					<tr>
						<td>
									<center><font color='red'>Não há discentes inscritos para esta ação de extensão ou todos os discentes já foram selecionados!</font></center>	
						</td>
					</tr>
				</c:if>					
		</tbody>
		
		<c:if test="${not empty discenteExtensao.atividade.inscricoesSelecao}">
			<tfoot>
					<tr>
						<td>
							<h:commandButton id="btCadastrarDiscente" value="Selecionar Discentes" action="#{discenteExtensao.adicionarResultadoSelecao}"/>
						</td>
					</tr>
			</tfoot>
		</c:if>
		
	</table>
		
</h:form>

<br/>
<br/>

<h:form>
	<table class="formulario" width="100%" cellpadding="3">
		<tbody>
			<caption class="listagem"> Cadastro de Resultados da Seleção de Discentes </caption>


				<tr>
					<td colspan="2">
						<h3 style="text-align: center; background: #EFF3FA; font-size: 12px">Lista de Discentes da Ação</h3>
		
							<table width="100%" cellpadding="3">
		
								<tr>
									<td width="15%">Titulo Ação Extensão:</td>
									<td> <b><h:outputText  id="acao" value="#{discenteExtensao.atividade.anoTitulo}"/> </b> </td>
								</tr>

								<tr>
									<td width="15%">Tipo:</td>
									<td> <b><h:outputText  id="tipo" value="#{discenteExtensao.atividade.tipoAtividadeExtensao.descricao}"/> </b> </td>
								</tr>

								<tr>
									<td>Bolsas Concedidas:</td><td> <b><h:outputText value="#{discenteExtensao.atividade.bolsasConcedidas}"/></b></td>
								</tr>
								<tr>							
									<td>Valuntários:</td><td> <b><h:outputText value="#{discenteExtensao.atividade.bolsasSolicitadas - discenteExtensao.atividade.bolsasConcedidas}"/> </b></td>
								</tr>
		
							</table>
	
					</td>
				</tr>
				


				<c:if test="${ not empty discenteExtensao.atividade.discentesSelecionados }">
					<tr>
						<td colspan="7">
							<div class="infoAltRem">
						    	<h:graphicImage value="/img/delete.gif"style="overflow: visible;"/>: Excluir Discente da Lista
							</div>			
						</td>
					</tr>
					<tr>
						<td colspan="7"></td>
					</tr>
		
					<tr>
						<td colspan="2">
							<input type="hidden" name="idDiscente" id="idDiscente"/>

							<t:dataTable value="#{discenteExtensao.atividade.discentesSelecionados}" 
								var="selecao" rowClasses="linhaPar,linhaImpar" width="100%" rowIndexVar="linha">
			
								<t:column>
									<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
									<h:outputText value="#{selecao.discente.matriculaNome}"/>
								</t:column>

								<t:column>
									<f:facet name="header"><f:verbatim>Situação</f:verbatim></f:facet>
									<h:outputText value="#{selecao.situacaoDiscenteExtensao.descricao}"/>
								</t:column>
			
								<t:column width="5%" styleClass="centerAlign">
									<f:facet name="header"></f:facet>
									<h:commandButton image="/img/delete.gif" action="#{discenteExtensao.removeSelecao}" alt="Remover Discente"	
									onclick="excluir(#{selecao.discente.id})"	immediate="false" styleClass="noborder" rendered="#{selecao.id == 0}"/>
								</t:column>
							</t:dataTable>
						</td>
					</tr>
				
				</c:if>
		</tbody>
		<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btCadastrar" value="Confirmar Cadastro" action="#{discenteExtensao.cadastrar}"/>
						<h:commandButton id="btCancelar" value="Cancelar" action="#{discenteExtensao.cancelar}"/>
					</td>
				</tr>
		</tfoot>
	</h:form>

	</table>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>