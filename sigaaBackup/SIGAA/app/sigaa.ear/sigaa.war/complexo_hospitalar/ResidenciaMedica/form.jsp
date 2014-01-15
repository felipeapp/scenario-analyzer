<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Carga Horária em Residência</h2>

<h:form id="form">
	<a4j:keepAlive beanName="residenciaMedica"></a4j:keepAlive>
	<table class=formulario width="100%">
		<caption class="listagem">Carga Horária em Residência</caption>
			<tr>
				<th class="required">Programa:</th>
				<td>
				
					<h:selectOneMenu value="#{residenciaMedica.obj.programaResidenciaMedica.id}" style="width: 80%"
						disabled="#{residenciaMedica.readOnly}" disabledClass="#{residenciaMedica.disableClass}" id="programaResidencia">
						<f:selectItem itemValue="0" itemLabel=" --- SELECIONE UM PROGRAMA DE RESIDÊNCIA --- " />
						<f:selectItems value="#{programaResidencia.allUnidadeCombo}" />
					</h:selectOneMenu>
				 </td>
			</tr>
			<tr>
				<th class="required">Servidor:</th>
				<td> 
					<h:inputText id="nomeServidor" value="#{residenciaMedica.obj.servidor.pessoa.nome}" style="width: 80%" />
					<rich:suggestionbox for="nomeServidor" width="450" height="100" minChars="3" id="suggestionNomeServidor" 
						suggestionAction="#{servidorAutoCompleteMBean.autocompleteNomeServidor}" var="_servidor" 
						fetchValue="#{_servidor.pessoa.nome}">
						<h:column>
							<h:outputText value="#{_servidor.pessoa.nome}" />
						</h:column>							        
						<h:column>
							<h:outputText value="#{_servidor.ativo.descricao}" />
						</h:column>
					        <a4j:support event="onselect">
							<f:setPropertyActionListener value="#{_servidor.id}" target="#{residenciaMedica.obj.servidor.id}" />
						</a4j:support>
					</rich:suggestionbox>
			   </td>
			</tr>
			<tr>
				<th class="required">Ano/Período:</th>
				<td>
					<h:inputText value="#{residenciaMedica.obj.ano}" size="5" maxlength="4" readonly="#{residenciaMedica.readOnly}" id="ano" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"  />
					.<h:inputText value="#{residenciaMedica.obj.semestre}" size="2" maxlength="1" readonly="#{residenciaMedica.readOnly}" id="semestre" converter="#{ intConverter }" onkeyup="return formatarInteiro(this);" />
				</td>
			</tr>
			<tr>
				<th class="required">Carga Horária Semanal Dispendida:</th>
				<td>
					<h:inputText value="#{residenciaMedica.obj.chSemanal}" size="3" maxlength="2" readonly="#{residenciaMedica.readOnly}" id="chSemanal" style="text-align: right;" onkeypress="return ApenasNumeros(event);" /> horas
				</td>
			</tr>
			<tr>
				<th>Observações:</th>
				<td>
					<h:inputTextarea value="#{residenciaMedica.obj.observacoes}" cols="80" rows="3"
						readonly="#{residenciaMedica.readOnly}" id="observacoes" />
				</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2">
  				 <h:commandButton id="btConfirmarAcao"
				   value="#{residenciaMedica.confirmButton}" action="#{residenciaMedica.cadastrar}" />
				 <h:commandButton value="Cancelar" id="btCancelar" action="#{residenciaMedica.cancelamento}" onclick="#{confirm}" immediate="true"/>
			</tr>
		</tfoot>
	</table>
	<br/>
	<div class="obrigatorio"> Campos de preenchimento obrigatório. </div>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>