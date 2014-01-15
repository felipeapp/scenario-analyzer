<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Ênfase</h2>

	<h:form id="form">
		<a4j:keepAlive beanName="enfase"></a4j:keepAlive>
		<table class="formulario">
			<caption>${enfase.confirmButton} Ênfases</caption>
			
			<tr>
				<th width="23%" class="required">Curso:</th>
				<td colspan="3">
					<h:selectOneMenu id="curso"
						value="#{enfase.obj.curso.id}" style="width: 85%">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoGrad.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			
			<tr>
				<th class="required">Nome da Ênfase:</th>
				<td><h:inputText value="#{ enfase.obj.nome }" id="nome"
					size="60" maxlength="120" readonly="#{enfase.readOnly}"
					disabled="#{enfase.readOnly}" onkeyup="CAPS(this)"/></td>
			</tr>
			<tr>
				<th>Ativa:</th>
				<td><h:selectBooleanCheckbox id="ativo" value="#{enfase.obj.ativo}"
					readonly="#{enfase.readOnly}" disabled="#{enfase.obj.id == 0}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{enfase.obj.id}" /> <h:commandButton id="btnCadastrar" value="#{enfase.confirmButton}" action="#{enfase.cadastrar}" /> 
						<c:if test="${enfase.obj.id > 0}">
							<h:commandButton value="<< Voltar" action="#{enfase.listar}" id="btnVoltar"	immediate="true" />
						</c:if> 
						<h:commandButton value="Cancelar" action="#{enfase.cancelarEnfase}" onclick="#{confirm}" immediate="true" id="cancelarOp"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
