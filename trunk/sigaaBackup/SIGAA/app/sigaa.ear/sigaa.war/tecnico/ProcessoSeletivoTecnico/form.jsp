<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="processoSeletivoTecnico"></a4j:keepAlive>
	<h2><ufrn:subSistema /> > Cadastro de Processo Seletivo</h2>
	<h:form id="form" enctype="multipart/form-data">
		<c:set var="readOnly" value="#{processoSeletivoTecnico.readOnly}" />
		<h:inputHidden value="#{processoSeletivoTecnico.obj.id}" />
		<table class=formulario width="100%">
			<caption class="listagem">Dados do Processo Seletivo</caption>
			<tbody>
				<tr>
					<th class="obrigatorio">Nome:</th>
					<td><h:inputText id="nome"
						value="#{processoSeletivoTecnico.obj.nome}"
						size="60" readonly="#{processoSeletivoTecnico.readOnly}"
						maxlength="160" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Sigla/Nome Abreviado:</th>
					<td><h:inputText id="sigla"
						value="#{processoSeletivoTecnico.obj.sigla}" size="60"
						readonly="#{processoSeletivoTecnico.readOnly}"
						maxlength="60" /></td>
				</tr>
				<tr>
					<th class="obrigatorio">Ano de Entrada:</th>
					<td><h:inputText id="anoEntrada"
					    value="#{processoSeletivoTecnico.obj.anoEntrada}"
						readonly="#{processoSeletivoTecnico.readOnly}"
						size="4" maxlength="4" immediate="true"
						onkeyup="formatarInteiro(this)" converter="#{ intConverter }"/>
					</td>
				</tr>
				<tr>
					<th><h:selectBooleanCheckbox id="ativo"
						value="#{processoSeletivoTecnico.obj.ativo}"  
						disabled="#{processoSeletivoTecnico.readOnly}"
						 /></th>
					<td>Ativo.</td>
				</tr>
				<tr>
					<th class="obrigatorio">Forma de Ingresso:</th>
					<td><h:selectOneMenu id="formaIngresso"
						value="#{processoSeletivoTecnico.obj.formaIngresso.id}"
						disabled="#{processoSeletivoTecnico.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{formaIngresso.allCombo}" />
					</h:selectOneMenu></td>
				</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{processoSeletivoTecnico.confirmButton}"
						action="#{processoSeletivoTecnico.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{processoSeletivoTecnico.cancelar}" immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br>
		<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>