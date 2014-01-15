<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> &gt; Cadastro de Índice Acadêmico</h2>

	<h:form id="form">
		<table class="formulario">
			<caption>Cadastro de Índice Acadêmico</caption>
			<tr>
				<th>Nome: <span class="required">&nbsp;</span></th>
				<td><h:inputText id="nomeIndice" value="#{ indiceAcademicoMBean.obj.nome }"
					readonly="#{indiceAcademicoMBean.readOnly}" size="70"
					disabled="#{indiceAcademicoMBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Sigla: <span class="required">&nbsp;</span></th>
				<td><h:inputText id="siglaIndice" value="#{ indiceAcademicoMBean.obj.sigla }"
					readonly="#{indiceAcademicoMBean.readOnly}" size="5"
					disabled="#{indiceAcademicoMBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Classe: <span class="required">&nbsp;</span></th>
				<td><h:inputText id="classeIndice" value="#{ indiceAcademicoMBean.obj.classe }"
					readonly="#{indiceAcademicoMBean.readOnly}" size="70"
					disabled="#{indiceAcademicoMBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Descrição:</th>
				<td><h:inputTextarea id="descricaoIndice"
					value="#{ indiceAcademicoMBean.obj.descricao }"
					readonly="#{indiceAcademicoMBean.readOnly}" rows="3" cols="67"
					disabled="#{indiceAcademicoMBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Ordem <span class="required">&nbsp;</span></th>
				<td><h:inputText id="ordemIndice" value="#{ indiceAcademicoMBean.obj.ordem }"
					readonly="#{indiceAcademicoMBean.readOnly}" size="3"
					disabled="#{indiceAcademicoMBean.readOnly}" /></td>
			</tr>
			<tr>
				<th>Exibido no Historico: </th>
				<td><h:selectOneRadio id="historicoIndice" value="#{ indiceAcademicoMBean.obj.exibidoHistorico }"
					readonly="#{indiceAcademicoMBean.readOnly}"
					disabled="#{indiceAcademicoMBean.readOnly}">
						<f:selectItems value="#{indiceAcademicoMBean.simNao}"/>
					</h:selectOneRadio></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden id="nivelIndice" value="#{ indiceAcademicoMBean.obj.nivel }"/> 
						<h:inputHidden id="idIndice" value="#{indiceAcademicoMBean.obj.id}" /> 
						<h:inputHidden id="ativo" value="#{indiceAcademicoMBean.obj.ativo}" />
						<h:commandButton value="#{indiceAcademicoMBean.confirmButton}" action="#{indiceAcademicoMBean.cadastrar}" id="btnCadastrar"/> 
						<c:if test="${indiceAcademicoMBean.obj.id > 0}">
							<h:commandButton value="<< Voltar" action="#{indiceAcademicoMBean.listar}" immediate="true" id="btnListar" />
						</c:if> 
						<h:commandButton value="Cancelar" action="#{indiceAcademicoMBean.cancelar}" onclick="#{confirm}" immediate="true" id="btnCancelar" /></td>
				</tr>
			</tfoot>
		</table>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
