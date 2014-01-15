<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema></ufrn:subSistema> > Institutos de Ciência e Tecnologia</h2>
	<h:outputText value="#{institutoCienciaTecnologia.create}" />
	<h:form>
		<table class="formulario" width="100%">
			<caption> Dados do Instituto de Ciência e Tecnologia </caption>
	
			<tr>
				<th><b>Instituto de Ciência e Tecnologia:</b></th>
				<td><h:outputText value="#{institutoCienciaTecnologia.obj.id}"/> - <h:outputText value="#{institutoCienciaTecnologia.obj.nome}"/></td>
			</tr>
			<tr>
				<th> <b>Coordenador:</b></th>
				<td> <h:outputText value="#{institutoCienciaTecnologia.obj.coordenador.pessoa.nome}"/> </td>
			</tr>
			<tr>
				<th> <b>Unidade Federativa:</b></th>
				<td> <h:outputText value="#{institutoCienciaTecnologia.obj.unidadeFederativa.descricao}"/> </td>
			</tr>
			<tr>
				<th> <b>Volume de Recursos:</b>  </th>
				<td> <h:outputText value="#{institutoCienciaTecnologia.obj.volumeRecursos}"/> </td>
			</tr>
			<tr>
				<th> <b>Data da Última Atualização:</b> </th>
				<td> <ufrn:format type="data" name="institutoCienciaTecnologia.obj" property="dataUltimaAtualizacao" /> </td>
			</tr>
			<tr>
				<td colspan="2" class="subFormulario" style="padding-top: 10px;">Lista de Membros do Instituto</td>
			</tr>
			<c:if test="${not empty institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}">
				<tr>
					<td colspan="2">
						<t:dataTable id="dt_membro_equipe" value="#{institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}" 
						var="membro" align="left" width="100%" 
						rowClasses="linhaPar, linhaImpar">
							<t:column>
								<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
								<h:outputText value="#{membro.servidor.pessoa.nome}" rendered="#{empty membro.pessoa && membro.categoriaMembro.id == 1}" id="nome_docente" />
								<h:outputText value="#{membro.pessoa.nome}" rendered="#{not empty membro.pessoa}" id="nome_pessoa" />
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>
								<h:outputText value="#{membro.categoriaMembro.descricao}" rendered="#{not empty membro.categoriaMembro}" id="categoria" />
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>Data Início</f:verbatim></f:facet>
								<h:outputText value="#{membro.dataInicio}" rendered="#{not empty membro.dataInicio}" id="dataInicio" />
							</t:column>
							<t:column>
								<f:facet name="header"><f:verbatim>Data Fim</f:verbatim></f:facet>
								<h:outputText value="#{membro.dataFim}" rendered="#{not empty membro.dataFim}" id="dataFim" />
							</t:column>
						</t:dataTable>
					</td>
				</tr>
			</c:if>
			<c:if test="${ empty institutoCienciaTecnologia.obj.equipesInstitutoCienciaTecnologia}">
				<tr>
					<td colspan="2">
						<p style="text-align: center; font-style: italic; padding: 5px;">
							<h:outputText value="   Não existem membros nesse Instituto." />
						</p>
					</td>
				</tr>
			</c:if>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="<< Voltar" action="#{institutoCienciaTecnologia.listar}" immediate="true" />
					</td>
				</tr>
			</tfoot>
		</table>

	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>