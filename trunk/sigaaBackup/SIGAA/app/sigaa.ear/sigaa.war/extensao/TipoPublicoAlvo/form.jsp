<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<a4j:keepAlive beanName="tipoPublicoAlvo"/>

	<h2><ufrn:subSistema /> > Tipo de Público Alvo</h2>

	<h:form id="form">
	<input type="hidden" name="id" value="${tipoPublicoAlvo.obj.id}"/> 
	
	<center>
			<h:messages showDetail="true" showSummary="true"/>
			<div class="infoAltRem" style="text-align: center; width: 100%">
				<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/>
	  			<h:commandLink value="Listar" action="#{tipoPublicoAlvo.listar}" id="listarPublico"/>
			</div>
	</center>

	<table class=formulario width="100%">
			<caption class="listagem">Cadastro de Tipo de Público Alvo</caption>
			<tr>
				<th class="required">Descrição:</th>
				<td><h:inputText size="60" maxlength="255"
					readonly="#{tipoPublicoAlvo.readOnly}"  value="#{tipoPublicoAlvo.obj.descricao}" id="descricao"/></td>
			</tr>
			
			<tr>
				<th  class="required">Grupo</th>
				<td>
					<c:if test="${! tipoPublicoAlvo.readOnly}">
						<h:selectOneMenu id="grupo"	value="#{tipoPublicoAlvo.obj.grupo.id}"	style="width: 60%;">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE UM GRUPO --"/>
							<f:selectItems value="#{tipoPublicoAlvo.allGrupoCombo}"/>
						</h:selectOneMenu>
					</c:if>
					<c:if test="${tipoPublicoAlvo.readOnly}">
						<h:outputText value="#{tipoPublicoAlvo.obj.grupo.descricao}" />
					</c:if>
				</td>
			</tr>
			
			<tfoot>
				<tr>
					<td colspan=2>
						<h:commandButton value="#{tipoPublicoAlvo.confirmButton}" action="#{tipoPublicoAlvo.cadastrar}" id="btCadastrar" rendered="#{tipoPublicoAlvo.confirmButton != 'Remover'}"/> 
						<h:commandButton value="#{tipoPublicoAlvo.confirmButton}" action="#{tipoPublicoAlvo.inativar}" id="btInativar" rendered="#{tipoPublicoAlvo.confirmButton == 'Remover'}"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{tipoPublicoAlvo.cancelar}" id="btCancelar"/>
					</td>
				</tr>
			</tfoot>
			
	</table>
	</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>