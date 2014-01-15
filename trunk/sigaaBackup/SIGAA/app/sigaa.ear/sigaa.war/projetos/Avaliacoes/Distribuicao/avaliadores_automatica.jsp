<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Distribuição Automática de Projetos para <h:outputText value="#{distribuicaoProjetoMbean.obj.tipoAvaliador.descricao}"/></h2>
	<br>

	<h:form id="form">
	  <table class="formulario" width="100%">
		<caption class="listagem">Distribuir Projetos Selecionados ( ${ fn:length(distribuicaoProjetoMbean.projetosSelecionados) } ) </caption>
			<tr>			
				<td>
					<rich:dataTable id="dtProjetosSelecionados" value="#{distribuicaoProjetoMbean.projetosSelecionados}" 
						var="projeto_" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">
						
						<f:facet name="header">
							<rich:columnGroup>
								<rich:column colspan="4">
									<h:outputText value="Título" />
								</rich:column>
								<rich:column breakBefore="true">
									<rich:spacer />
								</rich:column>
								<rich:column>
									<h:outputText value="Avaliador(a)" />
								</rich:column>
								<rich:column>
									<h:outputText value="Nota" />
								</rich:column>
								<rich:column>
									<h:outputText value="Situação" />
								</rich:column>
							</rich:columnGroup>
						</f:facet>
						
						<rich:column colspan="4">
							<h:outputText value="#{projeto_.titulo}" />
						</rich:column>
						
						<rich:column breakBefore="true" rendered="#{empty projeto_.avaliacoesAtivas}">
							<center><h:outputText  value="Não há avaliadores avaliando este projeto." /></center>
						</rich:column>
						
						<rich:subTable value="#{projeto_.avaliacoesAtivas}" var="avaliacao_">
							<rich:column>
								<rich:spacer />
							</rich:column>
							<rich:column>
								<h:outputText value="#{avaliacao_.avaliador.pessoa.nome}" />
							</rich:column>
							<rich:column>
								<h:outputText value="#{avaliacao_.nota}">
									<f:convertNumber pattern="#0.0"/>
								</h:outputText>
							</rich:column>
							<rich:column>
								<h:outputText value="#{avaliacao_.situacao.descricao}" />
								<h:outputText value=" (incluído)" rendered="#{avaliacao_.id <= 0}"/>
							</rich:column>
						</rich:subTable>
					</rich:dataTable>
				</td>
			</tr>


			<tfoot>
				<tr>
					<td align="center">	
						<h:commandButton action="#{distribuicaoProjetoMbean.distribuir}" value="Confirmar Distribuição" id="confirmarDistribuicao"/>
						<h:commandButton  action="#{distribuicaoProjetoMbean.distribuirNovoProjeto}" value="<< Selecionar Outros Projetos" id="distribuirOutroProjeto"/>						
						<h:commandButton  action="#{distribuicaoProjetoMbean.cancelar}" value="Cancelar"  id="cancelar" onclick="#{confirm}" immediate="true"/>						
					</td>		
				</tr>
			</tfoot>

    </table>
  </h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>