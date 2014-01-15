<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h:outputText value="#{municipio.create }"></h:outputText>

	<h2><ufrn:subSistema /> > Município</h2>
<c:if test="${!municipio.subSistemaGraduacao}">
	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{municipio.listar}"/>
			</div>
			</h:form>
	</center>
</c:if>
	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Município</caption>
			<h:inputHidden value="#{municipio.confirmButton}" />
			<h:inputHidden value="#{municipio.obj.id}" />
			<tr>
				<th class="required">Unidade Federativa:</th>

				<td><h:selectOneMenu value="#{municipio.obj.unidadeFederativa.id}" disabled="#{municipio.readOnly}">
					<f:selectItems value="#{unidadeFederativa.allCombo}" />
				</h:selectOneMenu>
				</td>
			</tr>
			<tr>
				<th class="required">Código:</th>
				<td><h:inputText value="#{municipio.obj.codigo}" size="12"
					maxlength="12" readonly="#{municipio.readOnly}" />
				</td>
			</tr>
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText readonly="#{municipio.readOnly}" value="#{municipio.obj.nome}" size="60" maxlength="80"/>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton value="#{municipio.confirmButton}"
						action="#{municipio.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{municipio.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>

 <br />
 <center>
 <h:graphicImage url="/img/required.gif" style="vertical-align: top;"/>
 <span class="fontePequena"> Campos de preenchimento obrigatório. </span>
 </center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>