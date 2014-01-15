<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Áreas de Concentração</h2>

	<h:form prependId="false">
		<a4j:keepAlive beanName="areaConcentracao"/>
		<h:outputText value="#{areaConcentracao.create}"></h:outputText>
		<table class="formulario">
			<caption class="formulario">Dados da Área de Concentração</caption>
			<tr>
				<th class="required">Programa:</th>
				<td>
					<h:selectOneMenu id="programa" value="#{areaConcentracao.obj.programa.id}" disabled="#{areaConcentracao.readOnly}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allProgramaPosCombo}" id="selectItemsProgramas"/>
					</h:selectOneMenu>
				</td>
			</tr>

			<tr>
				<th class="required">Nível:</th>
				<td><h:selectOneRadio id="nivel"
					value="#{areaConcentracao.obj.nivel}" disabled="#{areaConcentracao.readOnly}">
					<f:selectItems value="#{nivelEnsino.strictoCombo}" id="selectItemsNivelEnsinoStricto"/>
				</h:selectOneRadio></td>
			</tr>

			<tr>
				<th class="required">Área de Conhecimento CNPq:</th>
				<td>
					<h:selectOneMenu value="#{areaConcentracao.obj.areaConhecimentoCnpq.id}" id="area" disabled="#{areaConcentracao.readOnly}">
					<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
					<f:selectItems value="#{area.allCombo}" id="selectItemsAreas"/>
				</h:selectOneMenu>
				</td>
			</tr>


			<tr>
				<th class="required">Denominação:</th>
				<td>
					<h:inputText id="nome" value="#{areaConcentracao.obj.denominacao}" size="70" maxlength="150" onkeyup="return CAPS(this)"  disabled="#{areaConcentracao.readOnly}"/>
				</td>
			</tr>

			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton id="voltar" value="<< Voltar" action="#{areaConcentracao.listPage}" rendered="#{areaConcentracao.confirmButton != 'Cadastrar'}" />
						<h:commandButton value="#{areaConcentracao.confirmButton}" action="#{areaConcentracao.cadastrar}" id="cadastrar" />
						<h:commandButton value="Cancelar" action="#{areaConcentracao.cancelar}" onclick="#{confirm}" id="cancelar" />
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>

</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
