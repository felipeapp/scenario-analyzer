<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
<h2> <ufrn:subSistema /> > Relatório de Atividades de Docente na Pós-Graduação </h2>

<h:form id="form">
<a4j:keepAlive beanName="relatorioAtividadesDocente"></a4j:keepAlive>

<table class="formulario" style="width: 50%">
<caption> Informe os Critérios para a Emissão do Relatório </caption>
	<tr>
		<th class="required">Docente: </th>
		<td>
			<a4j:outputPanel>
				<h:inputText value="#{relatorioAtividadesDocente.obj.pessoa.nome}" id="nomeDocente" style="width: 400px;"/>
				<rich:suggestionbox width="400" height="100" for="nomeDocente" 
					minChars="3" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
					suggestionAction="#{servidor.autocompleteDocente}" var="_servidor" fetchValue="#{_servidor.nome}">
					<h:column>
						<h:outputText value="#{_servidor.siape}"/>
					</h:column>
					<h:column>
						<h:outputText value="#{_servidor.nome}"/>
					</h:column>
					<a4j:support event="onselect">
						<f:setPropertyActionListener value="#{_servidor.id}" target="#{relatorioAtividadesDocente.obj.id}" />
					</a4j:support>
				</rich:suggestionbox>
				<rich:spacer width="10"/>
	            <a4j:status>
	                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
	            </a4j:status>
			</a4j:outputPanel>
		</td>
	</tr>
	<tr>
		<th class="required"> Ano: </th>
		<td>
			<h:inputText value="#{relatorioAtividadesDocente.ano}" size="4" maxlength="4" onkeyup="return formatarInteiro(this);" converter="#{ intConverter }"/>
		</td>
	</tr>
	<tfoot>
	<tr>
		<td colspan="2">
			<h:commandButton action="#{relatorioAtividadesDocente.gerarRelatorioAtividades}" value="Emitir Relatório"/>
			<h:commandButton action="#{relatorioAtividadesDocente.cancelar}" value="Cancelar"/>
		</td>
	</tr>
	</tfoot>
</table>
<br/>
<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
</h:form>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>