<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Metodologia de Avaliação</h2>

	<c:set var="nomeOperacaoVisualizacao" value="Dados da metodologia"/>
	
	<h:form>
		<table class="formulario" width="100%">
			<caption>Resumo da Metodologia de Avaliação</caption>
			<tr>
				<td>
					<%@include file="/ead/MetodologiaAvaliacao/detalhesMetodologia.jsp"%>
				</td>
			</tr>
			<tfoot>
				<tr>
					<td>
						<center>
							<h:commandButton value="Cadastrar" action="#{metodologiaAvaliacaoEad.cadastrar}"/>
							<h:commandButton id="voltarTutor" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}" rendered="#{metodologiaAvaliacaoEad.obj.permiteTutor}">
								<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="2" /> 
							</h:commandButton>
							
							
							<h:commandButton id="voltar" value="<< Voltar" action="#{metodologiaAvaliacaoEad.voltar}" rendered="#{!metodologiaAvaliacaoEad.obj.permiteTutor}">
								<f:setPropertyActionListener target="#{metodologiaAvaliacaoEad.operacaoVoltar}" value="1" /> 
							</h:commandButton>
														
							
							<h:commandButton value="Cancelar" action="#{metodologiaAvaliacaoEad.cancelar}" onclick="#{confirm}" immediate="true"/>
						</center>				
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>