<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Tipo de Público Alvo</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif" style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoPublicoAlvo.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Tipo de Público Alvo</caption>
			<h:inputHidden value="#{tipoPublicoAlvo.confirmButton}" />
			<h:inputHidden value="#{tipoPublicoAlvo.obj.id}" />
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoPublicoAlvo.readOnly}"  value="#{tipoPublicoAlvo.obj.descricao}" /></td>
			</tr>
			<tr>
				<th class="required">Grupo:</th>
				<td><h:selectOneMenu id="grupo"
					value="#{tipoPublicoAlvo.obj.grupo.id}" readonly="#{tipoPublicoAlvo.readOnly}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{grupoPublicoAlvo.allCombo}" />
					</h:selectOneMenu>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{tipoPublicoAlvo.confirmButton}"
						action="#{tipoPublicoAlvo.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{tipoPublicoAlvo.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	
	<br>
	<center><h:graphicImage url="/img/required.gif"/> 
		<span class="fontePequena"> Campos de preenchimento obrigatório.</span>
	</center>
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>