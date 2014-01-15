<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> > Lista de Membros da Ação de Extensão</h2>
	
	
	<div class="descricaoOperacao">
		<b>Atenção:</b><br/>
		Os documentos só poderão ser emitidos para Membros da Equipe ativos.<br/>
		Os Certificados só serão liberados quando a participação do membro da equipe na ação for finalizada.<br>
		As Declarações poderão ser emitidas a qualquer tempo para os membros ativos da ação de extensão.
	</div>
	
	
	
	<div class="infoAltRem">
	    	<h:graphicImage value="/img/view.gif" 		 style="overflow: visible;"/>: Visualizar membro da equipe  
	    	<h:graphicImage value="/img/comprovante.png" height="20" width="20" style="overflow: visible;"/>: Emitir declaração
	    	<h:graphicImage value="/img/certificate.png" height="20" width="20" style="overflow: visible;"/>: Emitir certificado
	</div>
	
	
	<h:form id="membrosAtividade">

		<table class="formulario" width="100%">
			<caption class="listagem">Membros</caption>

			<c:if test="${empty declaracaoExtensao.membros}">
				<tr>
					<td colspan="2">
						<center><font color='red'>Esta ação de extensão não possui membros de equipe cadastrados.</font></center>
					</td>
				</tr>
			</c:if>

			<c:if test="${not empty declaracaoExtensao.membros}">
			<tr>
				<td colspan="2">

					<t:dataTable id="datatableServidores" value="#{declaracaoExtensao.membros}" var="membro" align="center" width="100%" styleClass="listagem" rowClasses="linhaPar, linhaImpar">

						<t:column>
							<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>	
							<h:outputText value="#{membro.pessoa.nome}" rendered="#{not empty membro.pessoa}"/>						
						</t:column>


						<t:column>
							<f:facet name="header"><f:verbatim>Categoria</f:verbatim></f:facet>	
							<h:outputText value="#{membro.categoriaMembro.descricao}" />	
						</t:column>										

						
						<t:column>
							<f:facet name="header"><f:verbatim>Função</f:verbatim></f:facet>	
							<h:outputText value="#{membro.funcaoMembro.descricao}" />
						</t:column>										

						<t:column>
							<f:facet name="header"><f:verbatim>Ch Semanal</f:verbatim></f:facet>	
							<h:outputText value="#{membro.chDedicada}h" />
						</t:column>										

						<t:column>
							<f:facet name="header"><f:verbatim>Início</f:verbatim></f:facet>	
							<h:outputText value="#{membro.dataInicio}" />
						</t:column>										

						<t:column>
							<f:facet name="header"><f:verbatim>Fim</f:verbatim></f:facet>	
							<h:outputText value="#{membro.dataFim}" />
						</t:column>										

						<t:column width="2%" styleClass="centerAlign">							
							<h:commandLink title="Visualizar Membro da Equipe" action="#{membroProjeto.view}" immediate="true">
								<f:param name="idMembro" value="#{membro.id}"/>
	                   			<h:graphicImage url="/img/view.gif"/>
							</h:commandLink>	
						</t:column>

						<t:column width="2%" styleClass="centerAlign">							
							<h:commandLink title="Emitir declaração" action="#{declaracaoExtensao.emitirDeclaracao}" immediate="true" rendered="#{membro.passivelEmissaoDeclaracao}">
							        <f:setPropertyActionListener target="#{declaracaoExtensao.membro.id}" value="#{membro.id}"/>
		                   			<h:graphicImage url="/img/comprovante.png" height="20" width="20"/>
							</h:commandLink>	
						</t:column>

						<t:column width="2%" styleClass="centerAlign">							
							<h:commandLink title="Emitir certificado" action="#{certificadoExtensao.emitirCertificado}" immediate="true" rendered="#{membro.passivelEmissaoCertificado}">
									<f:setPropertyActionListener target="#{certificadoExtensao.membro.id}" value="#{membro.id}"/>
		                   			<h:graphicImage url="/img/certificate.png" height="20" width="20"/>
							</h:commandLink>	
						</t:column>
						
						
					</t:dataTable>
					
				</td>
			</tr>
			</c:if>


			<tfoot>
				<tr>
					<td colspan="2">
					<input type="button" value="<< Voltar"  onclick="javascript: history.back();" id="voltar" /> 
					<h:commandButton id="btn_cancelar" value="Cancelar" action="#{declaracaoExtensao.cancelar}" />					
					</td>
				</tr>
			</tfoot>
			</table>

		</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
