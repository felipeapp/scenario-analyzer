<%-- Botão que exibe o painel dos exemplares. --%>
<input type="submit" id="bVisualizarInformacoesLivro" style="display:none;" onclick="dialogInfoLivro.show();return false;"/>

<%-- Painel das informações sobre os exemplares. --%>
<p:dialog id="panelLivro" styleClass="panelPerfil" header="INFORMAÇÕES SOBRE EXEMPLARES" widgetVar="dialogInfoLivro" modal="true" width="900" height="340">
	<t:dataTable id="listaExemplares"  value="#{planoCurso.indicacaoReferencia.exemplares}" var="exemplar" rowClasses="linhaPar,linhaImpar" style="width:100%;">
		<t:column>
			<f:facet name="header"><h:outputText value="Biblioteca" /></f:facet>
			<h:outputText id="biblioteca" value="#{exemplar.biblioteca.descricao}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Código de Barras" /></f:facet>
			<h:outputText id="codBarras" value="#{exemplar.codigoBarras}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Tipo do Material" /></f:facet>
			<h:outputText id="tipoMaterial" value="#{exemplar.tipoMaterial.descricao}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Coleção" /></f:facet>
			<h:outputText id="colecao" value="#{exemplar.colecao.descricao}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Status" /></f:facet>
			<h:outputText id="status" value="#{exemplar.status.descricao}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Situação" /></f:facet>
			<h:outputText style="color:#CC0000;" id="situacaoEmprestado" value="#{exemplar.situacao.descricao}" rendered="#{exemplar.emprestado}" />
			<h:outputText value=" (#{exemplar.prazoEmprestimo})" rendered="#{exemplar.emprestado}" style="color:#CC0000;"/>
			<h:outputText style="color:#00CC00;" id="situacaoDisponivel" value="#{exemplar.situacao.descricao}" rendered="#{exemplar.disponivel}" />
			<h:outputText id="situacao" value="#{exemplar.situacao.descricao}" rendered="#{!exemplar.disponivel && !exemplar.emprestado}" />
		</t:column>
	</t:dataTable>
</p:dialog>