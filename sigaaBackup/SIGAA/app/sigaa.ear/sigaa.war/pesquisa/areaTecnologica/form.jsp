<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
 	<a4j:keepAlive beanName="areaConhecimentoTecnMBean" />
	<h2><ufrn:subSistema /> &gt; Áreas Tecnológicas</h2>

 <h:form id="form">
 
	<center>
		<div class="infoAltRem" style="text-align: center; width: 100%">
			<h:graphicImage value="/img/listar.gif"style="overflow: visible;"/>
  			<h:commandLink value="Listar as Áreas Cadastradas" action="#{areaConhecimentoTecnMBean.listar}"/>
		</div>
	</center>

	<table class=formulario width="64%">
			<caption class="listagem">Área Tecnológica</caption>
			<h:inputHidden value="#{areaConhecimentoTecnMBean.confirmButton}" />
			<h:inputHidden value="#{areaConhecimentoTecnMBean.obj.id}" />

			<tr>
				<th class="obrigatorio">Área de Conhecimento:</th>
				<td>
					<h:inputHidden id="idCurso" value="#{ areaConhecimentoTecnMBean.obj.area.id }" />
					<h:inputText id="nomeCurso" value="#{ areaConhecimentoTecnMBean.obj.area.nome }" size="80" maxlength="120" style="width: 500px;" />
					<rich:suggestionbox id="suggestionNomeCurso" for="form:nomeCurso" var="_area" nothingLabel="Nenhuma área encontrada" 
							suggestionAction="#{ area.autocompleteNomeArea }" width="500" height="250" minChars="3" >
						<h:column>
							<h:outputText value="#{ _area.nome }"/> 
						</h:column>
						
						<a4j:support event="onselect" reRender="form" >
							<f:setPropertyActionListener value="#{ _area.id }" target="#{ areaConhecimentoTecnMBean.obj.area.id }"/>
						</a4j:support>
						
					</rich:suggestionbox>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="btnCadastrar" value="#{ areaConhecimentoTecnMBean.confirmButton }" action="#{ areaConhecimentoTecnMBean.cadastrar }" /> 
						<h:commandButton id="btnCancelar" immediate="true" value="Cancelar" onclick="#{confirm}" action="#{ areaConhecimentoTecnMBean.cancelar }" />
					</td>
				</tr>
			</tfoot>
	</table>

	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
 </h:form>
 
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>