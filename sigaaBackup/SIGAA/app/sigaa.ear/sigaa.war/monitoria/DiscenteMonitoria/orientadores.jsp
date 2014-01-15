<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>

	<%@include file="/portais/docente/menu_docente.jsp" %>

	<h2><ufrn:subSistema /> > Cadastro de Resultados da Seleção de Monitores</h2>
	
	<div class="descricaoOperacao">
		<b>Atenção: </b> 
		Utilize a lista abaixo para selecionar os orientadores dos discentes classificados. Cada discente 
		deve ter pelo menos um(a) orientador(a).<br/>
		Após a confirmação desta operação uma mensagem será enviada para todos os discentes classificados
		com bolsas remuneradas e não remuneradas. Estes discentes terão que confirmar a participação no
		projeto e informar os dados bancários, caso seja necessário.
	</div>
	
	<h:form id="frmSelecao">
		<h:outputText value="#{discenteMonitoria.create}"/>

		<table class="formulario" width="100%" cellpadding="3">
		<tbody>
		<caption class="listagem"> Selecione os orientadores para os monitores</caption>


		<input type="hidden" name="idDiscente" value="0" id="idDiscente"/>

        <center>
			<h:outputText value="<font color='red'>Não há discentes classificados para esta prova.</font>"  rendered="#{empty discenteMonitoria.provaSelecao.resultadoSelecao}"/>
		</center>
		
		<c:if test="${ not empty discenteMonitoria.provaSelecao.resultadoSelecao }">

		<tr>
			<td></td>
		</tr>


		<tr>
			<td colspan="2">

				
				<t:dataTable value="#{discenteMonitoria.provaSelecao.resultadoSelecao}" var="selecao" rowClasses="linhaPar,linhaImpar" width="100%" id="selecao" >

					<t:column rendered="#{ selecao.vinculoValido }">
						<f:facet name="header"><f:verbatim>Class.</f:verbatim></f:facet>
						<h:outputText value="#{selecao.classificacaoView}"/>
					</t:column>


					<t:column rendered="#{ selecao.vinculoValido }">
						<f:facet name="header"><f:verbatim>Discente</f:verbatim></f:facet>
						<h:outputText value="#{selecao.discente.nome}"/>
					</t:column>

					<t:column rendered="#{ selecao.vinculoValido }">
						<f:facet name="header"><f:verbatim>Vínculo</f:verbatim></f:facet>
						<h:outputText value="#{selecao.tipoVinculo.descricao}"/>
					</t:column>


					<t:column styleClass="centerAlign" rendered="#{ selecao.vinculoValido }" width="60%">

						<f:facet name="header"><f:verbatim>Lista de orientadores possíveis</f:verbatim></f:facet>
	
						<h:dataTable value="#{selecao.docentes}" var="orientador" id="orientadores" width="100%">
	
							<t:column styleClass="centerAlign">	
								<h:selectBooleanCheckbox id="selecionado" value="#{orientador.selecionado}"/>
								<h:outputText value="#{orientador.servidor.nome}"/>
							</t:column>
							
							<t:column>
								<f:facet name="header">
									<f:verbatim>Início Orientação</f:verbatim>
								</f:facet>
								<t:inputCalendar
									size="10"
									maxlength="10"
									value="#{orientador.dataInicioOrientacao}"
									popupDateFormat="dd/MM/yyyy"
									renderAsPopup="true"
									onkeypress="return(formataData(this,event))"
									renderPopupButtonAsImage="true" id="dataInicioOrientacao">
									<f:converter converterId="convertData"  />
								</t:inputCalendar>																				
							</t:column>
		
							<t:column>
								<f:facet name="header">
									<f:verbatim>Fim Orientação</f:verbatim>
								</f:facet>
								<t:inputCalendar 
									size="10"
									maxlength="10"
									value="#{orientador.dataFimOrientacao}"
									popupDateFormat="dd/MM/yyyy"
									onkeypress="return(formataData(this,event))"
									renderAsPopup="true"
									renderPopupButtonAsImage="true" id="dataFimOrientacao">
									<f:converter converterId="convertData"/>
								</t:inputCalendar>										
							</t:column>
	
						</h:dataTable>
						
					</t:column>

				</t:dataTable>

			</td>
		</tr>

		</c:if>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="2">
				
					<h:commandButton id="btConfirm" value="#{discenteMonitoria.confirmButton}" action="#{discenteMonitoria.cadastrar}"/>
					<input type="button" id="btVoltar" value="<< Voltar" onclick="javascript: history.go(-1)"> 
					<h:commandButton id="btCancelar" value="Cancelar" action="#{discenteMonitoria.cancelar}" immediate="true" onclick="#{confirm}"/>

				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>