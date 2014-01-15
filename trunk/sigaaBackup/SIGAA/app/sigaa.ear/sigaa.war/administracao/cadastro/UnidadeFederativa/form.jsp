<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h2><ufrn:subSistema /> > Unidade Federativa</h2>

	<center>
			<h:messages/>
			<h:form>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{unidadeFederativa.listar}"/>
			</div>
			</h:form>
	</center>

	<table class=formulario width="70%">
		<h:form>
			<caption class="listagem">Cadastro de Unidade Federativa</caption>
			<h:inputHidden value="#{unidadeFederativa.confirmButton}" />
			<h:inputHidden value="#{unidadeFederativa.obj.id}" />
			<tr>
				<th class="required">País:</th>

				<td><h:selectOneMenu value="#{unidadeFederativa.obj.pais.id}" disabled="#{unidadeFederativa.readOnly}">
					<f:selectItems value="#{pais.allCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{unidadeFederativa.readOnly}" value="#{unidadeFederativa.obj.descricao}"/></td>
			</tr>
			<tr>
				<th class="required">Sigla:</th>
				<td><h:inputText readonly="#{unidadeFederativa.readOnly}" value="#{unidadeFederativa.obj.sigla}" 
							maxlength="2" size="2"/></td>
			</tr>
			<tr>
				<th class="required">Capital:</th>
				 <td><h:selectOneMenu value="#{unidadeFederativa.obj.capital.id}" disabled="#{unidadeFederativa.readOnly}">
					<f:selectItems value="#{municipio.allAtivosCombo}" />
				</h:selectOneMenu></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan=2><h:commandButton
						value="#{unidadeFederativa.confirmButton}"
						action="#{unidadeFederativa.cadastrar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}" action="#{unidadeFederativa.cancelar}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>