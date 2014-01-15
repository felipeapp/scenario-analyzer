<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js" ></script>
<style>
<!--
.leftCol {
	text-align: left;
	border-spacing: 3px;
}

.centerCol {
	text-align: center;
	border-spacing: 3px;
}

.buttonCol {
	text-align: center;
	border-spacing: 3px;
	width: 10%;
}
-->
</style>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Alterar Associação de Local de
	Aplicação de Prova a um Processo Seletivo</h2>

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption class="listagem">Edite o nome do coordenador e o
			local da reunião</caption>
			<tr>
				<th>Processo Seletivo:</th>
				<td><h:outputText
					value="#{localAplicacaoProcessoSeletivo.obj.processoSeletivoVestibular.nome}" />
				</td>
			</tr>
			<tr>
				<th>Local de Aplicação de Prova:</th>
				<td><h:outputText
					value="#{localAplicacaoProcessoSeletivo.obj.localAplicacaoProva.nome}" />
				</td>
			</tr>
			<tr>
				<th>Coordenador:</th>
				<td>
					<h:inputText value="#{localAplicacaoProcessoSeletivo.obj.nomeCoordenador}" id="nomeCoordenador" size="60" maxlength="160"/>
					<rich:suggestionbox for="nomeCoordenador" height="100"	width="450" minChars="4"
						suggestionAction="#{localAplicacaoProcessoSeletivo.autocompleteCoordenador}"
						 var="_servidor" fetchValue="#{_servidor.nome}">
						<h:column>
							<h:outputText value="#{_servidor.siape}"/>
						</h:column>
						<h:column>
							<h:outputText value="#{_servidor.nome}"/>
						</h:column>
						<a4j:support event="onselect" actionListener="#{localAplicacaoProcessoSeletivo.atualizaServidor}" reRender="isServidor">
							<f:attribute name="idServidor" value="#{_servidor.id}"/>
						</a4j:support>
					</rich:suggestionbox> 
				</td>
			</tr>
			<tr>
				<th><b>Matricula SIAPE:</b></th>
				<td><h:panelGrid id="isServidor">
						<h:outputText value="#{localAplicacaoProcessoSeletivo.obj.coordenador.siape}" rendered="#{not empty localAplicacaoProcessoSeletivo.obj.coordenador}"/>
						<h:outputText value="Não é Servidor da Instituição" rendered="#{empty localAplicacaoProcessoSeletivo.obj.coordenador}"/>
					</h:panelGrid>
				</td>
			</tr>
			<tr>
				<th valign="top">Reunião:</th>
				<td><h:inputTextarea rows="3" cols="60"
					value="#{localAplicacaoProcessoSeletivo.obj.localReuniao}" /></td>
			</tr>
			<tfoot>
				<tr>
					<td colspan="2"><h:commandButton
						value="#{localAplicacaoProcessoSeletivo.confirmButton}"
						action="#{localAplicacaoProcessoSeletivo.alterar}" /> <h:commandButton
						value="Cancelar" onclick="#{confirm}"
						action="#{localAplicacaoProcessoSeletivo.cancelar}"
						immediate="true" /></td>
				</tr>
			</tfoot>
		</table>
		<br />
		<center><html:img page="/img/required.gif"
			style="vertical-align: top;" /> <span class="fontePequena">
		Campos de preenchimento obrigatório. </span> <br>
		</center>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>