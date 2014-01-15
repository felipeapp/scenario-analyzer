<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2 class="title"><ufrn:subSistema /> > Personalizar Relatórios de Atividades dos Docentes</h2>
 <h:form>
 	<h:outputText value="#{relatorioAtividades.create}" />
		<table class="formulario" width="700px">
			<caption class="formulario">Dados do Relatório</caption>
			<tr>
				<th>Título do Relatório</th>
				<td><h:inputText size="70" maxlength="200" value="#{relatorioAtividades.obj.titulo}" id="titulo" readonly="#{relatorioAtividades.readOnly}" /> <span
					class="required">&nbsp;</span></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<table class="subFormulario" width="90%">
						<caption class="subFormulario">Inserir Novos Grupos</caption>
						<tr>
								<th>Nome: </th>
								<td><h:inputText id="nomeGrupo" size="70" maxlength="200" value="#{relatorioAtividades.nomeGrupo}"  /></td>
								<th nowrap="nowrap">Pontuação Máxima:</th>
								<td><h:inputText size="4" maxlength="6" value="#{relatorioAtividades.pontuacaoMaximaGrupo}" id="maximaPontuacao" onkeyup="return formatarInteiro(this);"/></td>
								<td><h:commandButton id="addgrupo" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Novo Grupo"	actionListener="#{relatorioAtividades.adicionarGrupo}" /></td>

						</tr>
					</table>
				</td>
			</tr>

			<tr>
				<td colspan="2" align="center">
					<table width="98%" cellspacing="0"><caption>Grupos e Itens</caption>
					<c:if test="${empty relatorioAtividades.gruposRelatorio}">
						<tr><td colspan="2">Nenhum Grupo Adicionado</td></tr>
					</c:if>
					<tr><td>
							<t:dataTable align="center" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
							value="#{relatorioAtividades.modelGrupo}" var="grupo" id="regiaoTabelaGrupos">
							<t:column>
								<h:outputText styleClass="subFormulario" value="#{grupo.titulo }" />
								<f:verbatim> &nbsp; </f:verbatim>
								<h:inputText size="6" value="#{grupo.pontuacaoMaxima}" />
								<f:verbatim> &nbsp; </f:verbatim>
								<h:commandButton id="addgrupo" image="/img/adicionar.gif" styleClass="noborder" title="Adicionar Itens" action="#{relatorioAtividades.buscarItens}"  />
								<h:commandButton id="remg" image="/img/garbage.png" title="Remover Grupo" actionListener="#{relatorioAtividades.removerGrupo}"  styleClass="noborder"/>
								<f:verbatim> <br> </f:verbatim>
								
								<t:dataTable align="center" width="100%" styleClass="listagem" rowClasses="linhaPar,linhaImpar"
									value="#{relatorioAtividades.modelItem}" var="item"> 
									<t:column>
										<f:facet name="header">
											<h:outputText value="#" />
										</f:facet>
										<h:outputText value="#{item.indiceTopico}" />
									</t:column>
									<t:column >
										<f:facet name="header">
											<h:outputText value="Descrição" />
										</f:facet>
										<h:outputText value="#{item.itemRelatorioProdutividade.descricao}"/>
									</t:column>
									<t:column >
										<f:facet name="header">
											<h:outputText value="Tipo da Pontuação" />
										</f:facet>
										<h:selectOneMenu style="width: 95px;" value="#{item.tipoPontuacao}">
												<f:selectItem itemValue="1" itemLabel="Quantitativo" />
												<f:selectItem itemValue="2" itemLabel="Qualitativo" />

										</h:selectOneMenu>
									</t:column>
									<t:column >
										<f:facet name="header">
											<h:outputText value="Validade" />
										</f:facet>
										<h:selectOneMenu style="width: 60px;" value="#{item.validade}">
												<f:selectItem itemValue="1" itemLabel="1 ano" />
												<f:selectItem itemValue="2" itemLabel="2 anos" />
												<f:selectItem itemValue="3" itemLabel="3 anos" />
										</h:selectOneMenu>
									</t:column>
									<t:column>
										<f:facet name="header">
											<h:outputText value="Pontuação" />
										</f:facet>
										<h:inputText size="6" value="#{item.pontuacao}" />
									</t:column>
									<t:column>
										<f:facet name="header">
											<h:outputText value="Pontuação Máxima" />
										</f:facet>
										<h:inputText size="6" value="#{item.limitePontuacao}" />
									</t:column>
									<t:column>
										<a4j:commandButton id="cima" image="/img/prodocente/cima.gif" 
											title="Mover para cima" actionListener="#{relatorioAtividades.moveItemCima}" 
											reRender="regiaoTabelaGrupos" styleClass="noborder"/>
										<a4j:commandButton id="baixo" image="/img/prodocente/baixo.gif" title="Mover para baixo" 
											actionListener="#{relatorioAtividades.moveItemBaixo}" 
											reRender="regiaoTabelaGrupos"  styleClass="noborder"/>
										<a4j:commandButton id="remitem" image="/img/garbage.png" title="Remover Item" 
											actionListener="#{relatorioAtividades.removerItem}" 
											reRender="regiaoTabelaGrupos" styleClass="noborder"/>
									</t:column>
								</t:dataTable>
							</t:column>
							</t:dataTable>
						</td></tr>
				</table>
			</td></tr>
			<tfoot>
				<tr>
					<td colspan="2">
						<h:inputHidden value="#{relatorioAtividades.confirmButton}" />
						<h:commandButton value="#{relatorioAtividades.confirmButton}" action="#{relatorioAtividades.submeterRelatorio}" />
					 	<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{relatorioAtividades.cancelar}" />
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
